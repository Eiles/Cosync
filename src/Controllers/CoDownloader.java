package Controllers;

import Interface.CoFileMenu;
import Interface.CoMainMenu;
import Models.Cofile;
import Models.Cosystem;
import Models.Couser;
import com.sun.org.apache.xpath.internal.SourceTree;

import javax.swing.*;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by elie on 19/06/15.
 */
public class CoDownloader implements Runnable{

    ArrayList<CoSignal> socketArray=new ArrayList<>();
    ArrayList<CoSignal> socketsToUse=new ArrayList<>();
    private Map<String, CoDB> dbSockets;

    long downloadStart;
    int blockState[]=null;
    CoDownSignal downSignal;
    private CoController controller;

    public CoDownloader(CoDownSignal signal, CoController controller) {
        this.controller = controller;
        this.downSignal=signal;
    }

    public void run() {
        try {
            System.out.println("Start CoDownloader");
            dbSockets = new HashMap<>();
            init:
            initSockets();
            while (true){
                if(downSignal.requestList.size()!=0){
                    System.out.println("Request list => "+downSignal.requestList.size());
                    String request=downSignal.getRequest();
                    getSocketsForFile(request);
                    if(socketsToUse.size()<1){
                        initSockets();
                    }
                    getFile(request);
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSockets() throws Exception {
        System.out.println("Init Sockets");

        InetAddress address;
        Cosystem system;
        CoSignal signal;

        for(int i=0;i<controller.getUser().getCosystems().size();i++){
            signal=new CoSignal();
            system =controller.getUser().getCosystems().get(i);
            address=InetAddress.getByName(system.getIp());

            try {
                Runnable client = new Cosocket(new Socket(address, 7777), 0, signal);
                Thread threadClient = new Thread(client);
                threadClient.start();
                socketArray.add(signal);

                //TODO: Deplacer getLastDB
                getLastDB(signal, system);
                Thread.sleep(500);
            }
            catch (SocketException e) {
                controller.getUser().getCosystems().get(i).setOnline(false);
                ((CoMainMenu)controller.getViews().get("main")).updateListSystem(controller.getUser());
            }
        }

        Thread.sleep(500);
        getNewFiles();

        System.out.println("Found "+ socketArray.size()+"sockets");
    }

    public void getLastDB(CoSignal signal, Cosystem system) throws Exception {
        if(controller.getCoDB().getLastUpdate(system.getKey())==0) {
            System.out.println("Get Last DB");

            signal.addRequest("getDB:" + system.getKey());
            long startTime = System.currentTimeMillis();
            while (!hasDB(signal)) {
                if (System.currentTimeMillis() - startTime > 5000) {
                    break;
                }
            }
            if (hasDB(signal)) {
                System.out.println("DB downloaded");
                dbSockets.put(system.getKey(), new CoDB(system.getKey()));
            }
        }

    }

    //TODO: Finir fonction
    public void getNewFiles() throws SQLException {
        System.out.println("Get New Files");
        CoDB compareDB=null;
        ResultSet files;

        for(String system: dbSockets.keySet()) {
            compareDB=dbSockets.get(system);
            System.out.println("New Files for "+system);

            int i = 1;
            try {
                files = compareDB.getFiles();
                while(files.next()) {
                    System.out.println("Check file "+ files.getString("PATH"));
                    if(controller.getCoDB().getDateForFile(files.getString("PATH")) == 0) {
                        System.out.println("Add new file");
                        controller.getCoDB().update("INSERT INTO FILES(PATH,DATE,SUPPRESSED, NEEDDOWNLOAD, MODIFIEDAT) VALUES ('" + files.getString("PATH") + "'," + files.getString("DATE") + ","+(files.getInt("SUPPRESSED")>0?"0":"1")+","+(files.getInt("SUPPRESSED")>0?"0":"1")+"," + files.getLong("MODIFIEDAT") + ")");
                }
                    else{
                        if(controller.getCoDB().getDateForFile(files.getString("PATH")) < files.getLong("DATE")){
                                System.out.println("Update file");
                                controller.getCoDB().update("UPDATE FILES SET NEEDDOWNLOAD="+(files.getInt("SUPPRESSED")>0?"0":"1")+", SUPPRESSED = "+files.getInt("SUPPRESSED")+", MODIFIEDAT =+" + files.getLong("MODIFIEDAT")+" WHERE PATH='"+files.getString("PATH")+"'");
                        }
                    }
                }
                files = controller.getCoDB().getFiles();
                while(files.next()) {
                    if(files.getInt("NEEDDOWNLOAD") == 1) {
                        this.downSignal.addRequest(files.getString("PATH"));
                    }
                }
                controller.getCoDB().update("INSERT INTO LASTDB (SYSTEM, UPDATEDATE) VALUES ('"+system+"',"+System.currentTimeMillis()+")");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void getFile(String path) throws InterruptedException, SQLException {
        for(int i=0;i<socketsToUse.size();i++){
            socketsToUse.get(i).addRequest("getFile:" + path);
        }
        long startTime = System.currentTimeMillis();
        while(!allSocketsHaveFileInfo(path)){
            long currentTime = System.currentTimeMillis();
            if(currentTime-startTime>20000){
                break;
            }
        }
        for(int i=0;i<socketsToUse.size();i++){
            if(socketsToUse.get(i).getFileInfo()==null){
                System.out.println("fileInfo is null");
                socketsToUse.remove(socketsToUse.get(i));
            }
        }
        if(socketsToUse.size()==0){
            return;
        }
        long mostRecentDate=0;
        byte[] mostRecentHash=null;
        for(int i=0;i<socketsToUse.size();i++){
            if(socketsToUse.get(i).getFileInfo().getModDate()>mostRecentDate){
                mostRecentDate=socketsToUse.get(i).getFileInfo().getModDate();
                mostRecentHash=socketsToUse.get(i).getFileInfo().getHash();
            }
        }
        System.out.println(mostRecentDate);
        for(int i=0;i<socketsToUse.size();i++){
            if(socketsToUse.get(i).getFileInfo().getModDate()!=mostRecentDate){
                socketsToUse.remove(socketsToUse.get(i));
            }
        }
        if(socketsToUse.size()==0){
            System.out.println("No more sockets");
            return;
        }
        blockState=new int[socketsToUse.get(0).getBlockState().length];
        System.out.println("Nombre de block : "+blockState.length);
        for(int i=0;i<blockState.length;i++){
            blockState[i]=0;
        }
        downloadStart=System.currentTimeMillis();
        while(!allBlocksAreDownloaded()){
            for(int i=0;i<socketsToUse.size();i++){
                if(!socketsToUse.get(i).getBusy()){
                    for(int j=0;j<blockState.length;j++){
                         if(socketsToUse.get(i).getBlockState()[j] == 2){
                            blockState[j]=2;
                        }
                    }
                    int blockNumber=getFirstBlockToDownload();
                    if(blockNumber==-1 && allBlocksAreDownloaded()){
                        break;
                    }
                    if(blockNumber>=0){
                        socketsToUse.get(i).addRequest("getBlock:" + path + ":" + blockNumber);
                        blockState[blockNumber]=1;
                    }
                }
            }
            Thread.sleep(200);
        }
        new File(Config.root+"/"+socketsToUse.get(0).getFileInfo().getPath()).setLastModified(socketsToUse.get(0).getFileInfo().getModDate());
        controller.getCoDB().update("UPDATE FILES SET NEEDDOWNLOAD = 0 WHERE PATH ='"+socketsToUse.get(0).getFileInfo().getPath()+"'");

        System.out.println("File downloaded in : "+(System.currentTimeMillis()-downloadStart)/1000+" sec 2");
        for(int i=0;i<socketArray.size();i++){
            socketArray.get(i).setFileInfo(null);
        }
        socketsToUse=new ArrayList<CoSignal>();
    }

    public void getSocketsForFile(String path){
        for(int i=0;i<socketArray.size();i++){
            System.out.println("Array Size : "+socketArray.size());
            socketArray.get(i).addRequest("hasFile:" + path);
        }
        long startTime = System.currentTimeMillis();

        while(!allSocketsHaveCheckedFile(path)){
            long currentTime = System.currentTimeMillis();
            if(currentTime-startTime>5000){
                break;
            }
        }
        for(int i=0;i<socketArray.size();i++){
            if(socketArray.get(i).hasfile.get(path)!=null){
                socketsToUse.add(socketArray.get(i));
            }
        }
    }

    public boolean allBlocksAreDownloaded(){
        for(int i=0;i<blockState.length;i++){
            if(blockState[i]!=2){
                return false;
            }
        }
        return true;
    }

    public int getFirstBlockToDownload(){
        for(int i=0;i<blockState.length;i++){
            if(blockState[i]==0){
                return i;
            }
        }
        return -1;
    }


    public boolean allSocketsHaveFileInfo(String path){
        for(int i=0;i<socketArray.size();i++){
            if(socketArray.get(i).getFileInfo()==null){
                return false;
            }
        }
        return true;
    }

    public boolean allSocketsHaveCheckedFile(String path){
        for(int i=0;i<socketArray.size();i++){
            if(socketArray.get(i).hasfile.get(path)==null){
                return false;
            }
        }
        return true;
    }

    public boolean hasDB(CoSignal signal){
        return signal.getdbDownload();
    }

}
