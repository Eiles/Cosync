package Controllers;

import Models.Cofile;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by elie on 12/06/15.
 */
public class CoSignal {
    protected LinkedList<String> requestList=new LinkedList<>();
    protected Cofile fileInfo=null;
    protected int blockState[]=null;
    protected boolean busy=false;
    protected HashMap <String,Boolean> hasfile=new HashMap<>();
    protected boolean dbDownload=false;

    public synchronized boolean getdbDownload() {
        return dbDownload;
    }

    public synchronized void setdbDownload(boolean dbDownload) {
        this.dbDownload= dbDownload;
    }
    public synchronized Cofile getFileInfo() {
        return fileInfo;
    }

    public synchronized void resetBlockState(int size){
        this.blockState=new int[size];
    }

    public synchronized void setFileInfo(Cofile fileInfo) {
        System.out.println("Fileinfo setted");
        if(fileInfo!=null)
            resetBlockState(fileInfo.blockHash.length);
        this.fileInfo = fileInfo;
    }
    public synchronized int[] getBlockState() {
        return this.blockState;
    }
    public synchronized void setBlockState(int blocknum, int value){
        this.getBlockState()[blocknum]=value;
    }

    public synchronized String getRequest(){
        return this.requestList.pop();
    }

    public synchronized void addRequest(String request) {
        this.requestList.add(request);
    }

    public synchronized void setBusy(boolean busy){
        this.busy=busy;
    }

    public synchronized void setHasfile(HashMap<String,Boolean> hasfile){
        this.hasfile=hasfile;
    }

    public synchronized void addHasFile(String path, Boolean hasFile) {
        hasfile.put(path,hasFile);
    }

    public synchronized HashMap getHasfile(){
        return this.hasfile;
    }

    public synchronized boolean getBusy(){
        return this.busy;
    }

}

