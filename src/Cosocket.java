import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Cosocket implements Runnable {

    private Socket connection;
    private int id;
    private boolean auth;
    private CoSignal sharedSignal;
    private Cofile cofile;
    public Cosocket(Socket s, int i, CoSignal signal) {
        this.connection = s;
        this.id = i;
        this.auth=false;
        this.sharedSignal=signal;
    }

    public void run() {
        try {
            //Input stream from the socket
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            DataInputStream dis=new DataInputStream(bis);
            int toRead=0;
            int i=0;
            byte[] request=null;
            String process;
            String[] args=null;
            int index;
            //Keep trying

            while(true){
                //If has request to send
                if(sharedSignal.requestList.size()!=0){
                    //Send request
                    sendRequest(sharedSignal.getRequest());
                }
                //Keep waiting for a request
                toRead=dis.readInt();
                if(toRead>0) {
                    System.out.println(toRead);
                    request=new byte[toRead];
                }
                while(i<toRead){
                    request[i]=dis.readByte();
                    i++;
                }
                //Convert byte array to String
                process=new String(request,"UTF-8");
                //Search for a separator
                if((index=process.indexOf((char)58))!=-1){
                    //Get args
                    args=process.split(":");
                    process=args[0];
                    if(args.length>1){
                        args = Arrays.copyOfRange(args, 1, args.length);
                    }
                }

                System.out.println(process);
                for(i=0;i<args.length;i++){
                    System.out.print(args[i]);
                }
                toRead=0;
                //Handle request
                treatRequest(process,args);
                i=0;
                process=null;
                args=null;
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void treatRequest(String request, String args[]) throws IOException, NoSuchAlgorithmException, InterruptedException {
        switch (request){
            case "auth" :{
                    compareToken();
                    break;
            }
            case "getToken" :{
                sendToken("abcdefgh");
                break;
            }
            case "hasFile" :{
                hasFile(args[0]);
                break;
            }
            case "getDB" : {
                sendDataBase();
                break;
            }
            case "getFile" :{
                sendFileInfo(args);
                break;
            }
            case "getBlock" :{
                sendBlock(args);
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

    public void sendFileInfo(String[] path) throws IOException, NoSuchAlgorithmException {
        System.out.println("getFileInfo for "+path+" received ");
        Cofile file=new Cofile(path[0],0,false);
        file.generateHash();
        file.generateBlockHash();

        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        int byte_;
        int cpt=0;
        oos.writeObject(file);
        oos.flush();
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
            if(!token.equals("")){
                break;
            }
        }
        if(token.equals("abcdefgh")){
            System.out.println("TOKEN OK");
            this.auth=true;

        }


    }

    public void sendRequest(String request) throws IOException{
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
        if (request.contains("getFile"))
            getFileInfo();
        if (request.contains("getBlock")){
            String[] args=request.split(":");
            if(args.length>1){
                args = Arrays.copyOfRange(args, 1, args.length);
            }
            getBlock(args);
        }


    }

    public void getFileInfo() throws IOException {
        ObjectInputStream ois=new ObjectInputStream(connection.getInputStream());
        Cofile file= null;
        try {
            while((file = (Cofile)ois.readObject())==null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(file.getHexHash());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBlock(String[] args) throws IOException {
        //System.out.println("GetBlock Function");
        BufferedInputStream bis=new BufferedInputStream(connection.getInputStream());
        DataInputStream dis=new DataInputStream(bis);
        int i=0;
        int toRead=dis.readInt();
        while(toRead==0) {
            System.out.println(toRead);
            toRead=dis.readInt();
        }
        byte[] writeBlock=new byte[toRead];
        System.out.println(toRead);
        if(toRead==-1)
            return;
        while(i<toRead){
            writeBlock[i]=dis.readByte();
            i++;
        }

        RandomAccessFile raf=new RandomAccessFile("/Users/elie/truc.avi","rw");
        raf.write(writeBlock,1024*1024*Integer.parseInt(args[1]),writeBlock.length);
        raf.close();
    }

    public void sendBlock(String[] args) throws IOException, InterruptedException {
        RandomAccessFile raf=new RandomAccessFile(Config.root+"/"+args[0],"r");
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        byte[] block=new byte[1024*1024];
        int read=raf.read(block,1024*1024*Integer.parseInt(args[1]),block.length);
        BufferedOutputStream bos=new BufferedOutputStream(connection.getOutputStream());
        DataOutputStream dos=new DataOutputStream(bos);
        dos.writeInt(read);
        System.out.println("Sent int for block : "+read);
        dos.write(block);
        dos.flush();
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

    public void hasFile(String path) throws IOException {
        if(new File(Config.root+"/"+path).exists()){
            sendRequest("hasFileResponse: "+path);
        }
        else
            sendRequest("hasFileResponse: "+path);
    }

    public Socket getConnection(){
        return connection;
    }
}