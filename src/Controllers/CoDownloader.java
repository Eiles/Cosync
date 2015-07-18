package Controllers;

import Interface.CoFileMenu;
import Interface.CoMainMenu;
import Models.Cofile;
import Models.Cosystem;
import Models.Couser;
import com.sun.org.apache.xpath.internal.SourceTree;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.*;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by elie on 19/06/15.
 */
public class CoDownloader implements Runnable{

    ArrayList<CoSignal> socketArray=new ArrayList<>();
    ArrayList<CoSignal> socketsToUse=new ArrayList<>();
    private Map<String, CoDB> dbSockets;
    private CoVersionized versionized;

    long downloadStart;
    int blockState[]=null;
    CoDownSignal downSignal;
    private CoController controller;

    public CoDownloader(CoDownSignal signal, CoController controller, CoVersionized versionized) {
        this.controller = controller;
        this.downSignal = signal;
        this.versionized = versionized;
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
                        downSignal.addRequest(request);
                        refreshSockets();
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
            signal=new CoSignal(controller.getCoDB());
            system =controller.getUser().getCosystems().get(i);
            address=InetAddress.getByName(system.getIp());
            signal.setSystemKey(system.getKey());

            try {
                Socket s=new Socket();
                s.connect(new InetSocketAddress(address,7777), 4000);
                Runnable client = new Cosocket(s, 0, signal);
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
            }catch(SocketTimeoutException te){
                controller.getUser().getCosystems().get(i).setOnline(false);
                ((CoMainMenu)controller.getViews().get("main")).updateListSystem(controller.getUser());
            }
        }

        Thread.sleep(500);
        getNewFiles();

        System.out.println("Found "+ socketArray.size()+"sockets");
    }

    public void refreshSockets() throws Exception {
        System.out.println("Refresh sockets");

        InetAddress address;
        Cosystem system;
        boolean ok=true;
        try {
            controller.getUser().retrieveCosystems();

            for(int i=0;i<controller.getUser().getCosystems().size();i++){
                system =controller.getUser().getCosystems().get(i);

                try {


                    for(int j=0;j<socketArray.size();j++){
                        if(system.getKey().equals(socketArray.get(j).getSystemKey())){
                            if(!socketArray.get(j).getBusy()){
                                socketArray.get(j).addRequest("close");
                                socketArray.remove(j);

                            }else{
                                ok=false;
                            }
                        }
                        if(ok){
                            CoSignal signalAdd=new CoSignal(controller.getCoDB());
                            address=InetAddress.getByName(system.getIp());
                            signalAdd.setSystemKey(system.getKey());
                            Socket s=new Socket();
                            s.connect(new InetSocketAddress(address, 7777), 4000);
                            Runnable client = new Cosocket(s, 0, signalAdd);
                            Thread threadClient = new Thread(client);
                            threadClient.start();
                            socketArray.add(signalAdd);

                        }
                    }

                    Thread.sleep(500);
                }
                catch (SocketException e) {
                    controller.getUser().getCosystems().get(i).setOnline(false);
                    ((CoMainMenu)controller.getViews().get("main")).updateListSystem(controller.getUser());
                }catch(SocketTimeoutException te){
                    controller.getUser().getCosystems().get(i).setOnline(false);
                    ((CoMainMenu)controller.getViews().get("main")).updateListSystem(controller.getUser());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        else {
            long startTime = System.currentTimeMillis();
            System.out.println("Get Modifs");
            signal.addRequest("getModif:"+controller.getCoDB().getLastUpdate(system.getKey()));

            while (!hasDB(signal)) {
                if (System.currentTimeMillis() - startTime > 5000) {
                    break;
                }
            }
            if (hasDB(signal)) {
                System.out.println("DB downloaded");
                String line;

                FileReader fr = new FileReader(signal.getSystemKey()+"_modif");
                BufferedReader br = new BufferedReader(fr);
                while((line = br.readLine()) != null) {
                    String[] args=line.split(":");
                    if(args.length>0){
                        args = Arrays.copyOfRange(args, 0, args.length);
                    }
                    System.out.println("length args =>"+args.length);

                    for(int i = 0; i< args.length; i++) {
                        System.out.println("args =>"+args[i]);
                    }

                    // Vérif: si exist: mettre à jour si plus récent
                    // Sinon: insert, copie données et mettre needdownload

                    //Si le fichier est nouveau
                    if(controller.getCoDB().getDateForFile(args[0]) == 0){
                        controller.getCoDB().update("INSERT INTO FILES(PATH,DATE,NEEDDOWNLOAD,SUPPRESSED,MODIFIEDAT) VALUES ('" + args[0] + "'," + System.currentTimeMillis() + "1,0," +"'"+ args[2]+"'");
                    }

                    // S'il le fichier est plus récent
                    else if(controller.getCoDB().getDateForFile(args[0]) < Long.parseLong(args[2])) {
                        controller.getCoDB().update("UPDATE FILES SET NEEDDOWNLOAD = 1, MODIFIEDAT = "+Long.parseLong(args[2])+" WHERE PATH ='"+args[0]+"'");
                    }

                    // Si le fichier est à supprimer
                    else if(Integer.parseInt(args[1]) == 1) {
                        controller.getCoDB().update("UPDATE FILES SET SUPPRESSED = 1, MODIFIEDAT = "+args[2]+" WHERE PATH ="+args[0]+"'");
                    }
                }

                br.close();
                fr.close();
            }
        }

    }

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
                        controller.getCoDB().update("INSERT INTO FILES(PATH,DATE,SUPPRESSED, NEEDDOWNLOAD, MODIFIEDAT) VALUES ('" + files.getString("PATH") + "'," + files.getString("DATE") + ","+(files.getInt("SUPPRESSED")>0?"1":"0")+","+(files.getInt("SUPPRESSED")>0?"0":"1")+"," + files.getLong("MODIFIEDAT") + ")");
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
            if(currentTime-startTime>30000){
                break;
            }
            Thread.sleep(500);
        }
        for(int i=0;i<socketsToUse.size();i++){
            if(socketsToUse.get(i).getFileInfo()==null){
                System.out.println("fileInfo is null");
                socketsToUse.remove(socketsToUse.get(i));
            }
        }
        if(socketsToUse.size()==0){
            downSignal.addRequest(path);
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
            downSignal.addRequest(path);
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
        controller.getCoDB().update("UPDATE FILES SET NEEDDOWNLOAD = 0 WHERE PATH ='" + socketsToUse.get(0).getFileInfo().getPath() + "'");

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
