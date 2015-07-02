import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by elie on 19/06/15.
 */
public class CoDownloader implements Runnable{

    ArrayList<Cosocket> socketArray=new ArrayList<>();
    CoDownSignal downSignal;

    public void initSockets() throws Exception {
        Couser elie=new Couser("elie","password");
        elie.retrieveCosystems();
        InetAddress address;
        Cosystem system;
        CoSignal signal;
        for(int i=0;i<elie.getCosystems().size();i++){
            signal=new CoSignal();
            system=elie.getCosystems().get(i);
            address=InetAddress.getByName(system.getIp());
            socketArray.add(new Cosocket(new Socket(address, 7777), i, signal));
        }
    }

    public void getLastDB() {

    }


}
