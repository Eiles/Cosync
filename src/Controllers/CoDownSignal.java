package Controllers;

import java.util.LinkedList;

/**
 * Created by elie on 02/07/15.
 */
public class CoDownSignal {
    public static final int FREE=1;
    public static final int BUSY=1;
    public int state;

    protected LinkedList<String> requestList=new LinkedList<>();

    public synchronized String getRequest(){
        return this.requestList.pop();
    }

    public synchronized void addRequest(String request) {
        this.requestList.add(request);
    }


}
