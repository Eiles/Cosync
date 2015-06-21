package CoSync;

import java.io.*;
import java.net.Socket;

public class Cosocket implements Runnable {

    private Socket connection;
    private int ID;
    private boolean auth;
    Cosocket(Socket s, int i) {
        this.connection = s;
        this.ID = i;
        this.auth=false;
    }

    public void run() {
        try {
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            DataInputStream dis=new DataInputStream(bis);
            int toRead=0;
            int i=0;
            byte[] request=null;
            String process;

            while(true){
                toRead=dis.readInt();
                if(toRead>0) {
                    System.out.println(toRead);
                    request=new byte[toRead];
                }
                while(i<toRead){
                    request[i]=dis.readByte();
                    i++;
                }
                process=new String(request,"UTF-8");
                System.out.println(process);
                toRead=0;
                treatRequest(process.toString());
                i=0;
                process=null;
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void treatRequest(String request) throws IOException {
        switch (request){
            case "auth" :{
                    compareToken();
                    break;
            }
            case "getToken" :{
                sendToken("abcdefgh");
                break;
            }
            case "getDB" : {
                    sendDataBase();
                    break;
            }
            case "close" : {
                    connection.close();
                    break;
            }
        }
    }
    public void sendDataBase() throws IOException {
        System.out.println("getDB recu");
        File fic=new File("cosync.db");
        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        FileInputStream fis=new FileInputStream(fic);
        BufferedInputStream bis=new BufferedInputStream(fis);
        DataOutputStream dos=new DataOutputStream(bos);
        int byte_;
        int cpt=0;
        dos.writeInt((int) fic.length());
        dos.flush();
        System.out.println((int)fic.length());
        while ((byte_ = bis.read()) != -1){
            bos.write(byte_);
            cpt++;
        }
        System.out.println(cpt);
        bos.flush();
    }

    public void compareToken() throws IOException {
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        DataInputStream dis=new DataInputStream(bis);
        sendRequest("getToken");
        int toRead=0;
        int i=0;
        byte[] response=null;
        String token;

        while(true){
            toRead=dis.readInt();
            if(toRead>0) {
                System.out.println(toRead);
                response=new byte[toRead];
            }
            while(i<toRead){
                response[i]=dis.readByte();
                i++;
            }
            token=new String(response,"UTF-8");
            System.out.println(token);
        }
    }

    public void sendRequest(String request) throws IOException {
        int i=0;
        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        DataOutputStream dos=new DataOutputStream(bos);
        String process = request;
        byte[] b = process.getBytes("UTF-8");
        dos.writeInt(b.length);
        System.out.println("Size sent : "+b.length);
        dos.write(b);
        dos.flush();
        System.out.println("Request sent : "+process);
    }

    public void sendToken(String token) throws IOException {
        int i=0;
        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        DataOutputStream dos=new DataOutputStream(bos);
        byte[] b = token.getBytes("UTF-8");
        dos.writeInt(b.length);
        System.out.println("Size sent : "+b.length);
        dos.write(b);
        dos.flush();
        System.out.println("Request sent : "+token);
    }

}