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

    public synchronized Cofile getFileInfo() {
        return fileInfo;
    }

    public synchronized void setFileInfo(Cofile fileInfo) {
        System.out.println("Fileinfo setted");
        this.blockState=new int[fileInfo.blockHash.length];
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

