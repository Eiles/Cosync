import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by elie on 05/06/15.
 */
public class Coserver
        implements Runnable{

    private int port = 5152;
    private int  count = 0;



    public void run(){
        try{
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("MultipleSocketServer Initialized");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new Cosocket(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
