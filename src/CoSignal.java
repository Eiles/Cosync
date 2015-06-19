import java.util.LinkedList;

/**
 * Created by elie on 12/06/15.
 */
public class CoSignal {
    protected LinkedList<String> requestList=new LinkedList<>();
    protected Cofile fileInfo;

    public Cofile getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(Cofile fileInfo) {
        this.fileInfo = fileInfo;
    }

    public synchronized String getRequest(){
        return this.requestList.pop();
    }

    public synchronized void addRequest(String request) {
        this.requestList.add(request);
    }
}
