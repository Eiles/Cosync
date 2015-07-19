package Controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by elie on 05/06/15.
 */
public class Coserver
        implements Runnable{

    private int port = 7777;
    private int count = 0;



    public void run(){

        ServerSocket socket1 = null;
        try {
            socket1 = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("MultipleSocketServer Initialized");
            while (true) {
                try{
                Socket connection = socket1.accept();
                Runnable runnable = new Cosocket(connection, ++count, new CoSignal());
                Thread thread = new Thread(runnable);
                thread.start();
            }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
}
