import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class Cosocket implements Runnable {

    private Socket connection;
    private String TimeStamp;
    private int ID;
    Cosocket(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    public void run() {
        try {
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(is);
            int character;
            StringBuffer process = new StringBuffer();
            while((character = isr.read()) != 13) {
                process.append((char)character);
            }
            treatRequest(process.toString());
            //need to wait 10 seconds to pretend that we're processing something
            try {
                Thread.sleep(1);
            }
            catch (Exception e){}
            TimeStamp = new java.util.Date().toString();
            String returnCode = "MultipleSocketServer responded at "+ TimeStamp + (char) 13;
            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");

        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                connection.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void treatRequest(String request) throws IOException {
        switch (request){
            case "getDB" : {
                System.out.println("getDB recu");
                File fic=new File("cosync.db");
                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
                FileInputStream fis=new FileInputStream(fic);
                BufferedInputStream bis=new BufferedInputStream(fis);
                DataOutputStream dos=new DataOutputStream(bos);
                int byte_;
                dos.writeInt((int) fic.length());
                dos.flush();
                System.out.println((int)fic.length());
                while ((byte_ = bis.read()) != -1)
                    bos.write(byte_);
                bos.flush();
                connection.close();
                System.out.println("done");

                }

        }

    }
}