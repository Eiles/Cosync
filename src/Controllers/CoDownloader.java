package Controllers;

import Models.Cosystem;
import Models.Couser;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by elie on 19/06/15.
 */
public class CoDownloader implements Runnable{

    ArrayList<CoSignal> socketArray=new ArrayList<>();
    ArrayList<CoSignal> socketsToUse=new ArrayList<>();
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

            initSockets();
            while (true){
                if(downSignal.requestList.size()!=0){
                    System.out.println(downSignal.requestList.size()!=0);
                    String request=downSignal.getRequest();
                    getSocketsForFile(request);
                    if(socketsToUse.size()<1){
                        System.out.println("No sockets for given File");
                       break;
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
        controller.getUser().retrieveCosystems();
        InetAddress address;
        Cosystem system;
        CoSignal signal;

        System.out.println("cosystems => "+controller.getUser().getCosystems());
        for(int i=0;i<controller.getUser().getCosystems().size();i++){
            signal=new CoSignal();
            system =controller.getUser().getCosystems().get(i);
            address=InetAddress.getByName(system.getIp());
            Runnable client=new Cosocket(new Socket(address, 7777),0,signal);
            Thread threadClient= new Thread(client);
            threadClient.start();
            socketArray.add(signal);
        }
        System.out.println("Found "+ socketArray.size()+"sockets");
    }

    public void getLastDB() {

    }

    public void getFile(String path) throws InterruptedException {
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
        System.out.println(blockState.length);
        for(int i=0;i<blockState.length;i++){
            blockState[i]=0;
        }
        downloadStart=System.currentTimeMillis();
        while(!allBlocksAreDownloaded()){
            for(int i=0;i<socketsToUse.size();i++){
                if(!socketsToUse.get(i).getBusy()){
                    for(int j=0;j<blockState.length;j++){
                        if(socketsToUse.get(i).blockState[j] == 2){
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
        System.out.println("File downloaded in : "+(System.currentTimeMillis()-downloadStart)/1000+" sec 2");
        socketsToUse=new ArrayList<CoSignal>();
    }

    public void getSocketsForFile(String path){
        for(int i=0;i<socketArray.size();i++){
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

}
