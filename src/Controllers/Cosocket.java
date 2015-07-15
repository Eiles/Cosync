package Controllers;

import Controllers.Config;
import Models.Cofile;

import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Cosocket implements Runnable {

    private Socket connection;
    private int id;
    private boolean auth;
    public CoSignal sharedSignal;
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
                    String myreq=sharedSignal.getRequest();
                    System.out.println(myreq);
                    System.out.println("Requesting "+myreq);
                    sendRequest(myreq);

                }
                if(dis.available()>0){
                    toRead=dis.readInt();
                    System.out.println("Reading");
                    if(toRead>0){
                        request=new byte[toRead];
                    }else{
                        break;
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

                    if(args != null)
                    for(i=0;i<args.length;i++){
                        System.out.println("arg " + i + " =>" + args[i]);
                    }
                    toRead=0;
                    //Handle request
                    treatRequest(process,args);
                    i=0;

                    process=null;
                    args=null;
                }
                Thread.sleep(100);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
            case "hasFileResponse" : {
                hasFileResponse(args);
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
        System.out.println("length: "+(int)fic.length());
        while ((byte_ = bis.read()) != -1){
            bos.write(byte_);
            cpt++;
        }
        System.out.println("cpt:"+cpt);
        bos.flush();
        this.sharedSignal.setBusy(false);
    }

    public void sendFileInfo(String[] path) throws IOException, NoSuchAlgorithmException {
        System.out.println("getFileInfo for "+path[0]+" received ");
        Cofile file=new Cofile(path[0],0,false);
        file.generateHash();
        file.generateBlockHash();
        file.setModDate(new File(file.getAbsolutePath()).lastModified());
        file.setLength(new File(file.getAbsolutePath()).length());
        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(file);
        oos.flush();
        this.sharedSignal.setBusy(false);
    }

    public void hasFileResponse(String[] args) throws IOException, NoSuchAlgorithmException {
        if(args[1].equals("true")){
            this.sharedSignal.addHasFile(args[0],true);
        }else{
            this.sharedSignal.addHasFile(args[0],false);
        }
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
        this.sharedSignal.setBusy(false);


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

        if(request.contains("getDB")) {
            String[] args=request.split(":");
            getDB(args[1]);
        }

        this.sharedSignal.setBusy(false);

    }

    public void getDB(String db) throws IOException {
        System.out.println("Waiting for DB of "+db);
        DataInputStream dis = new DataInputStream(connection.getInputStream());
        FileOutputStream fis = new FileOutputStream(db+".db");
        Cofile cofile = null;

        int bufferSize = dis.readInt();
        byte[] buffer = new byte[bufferSize];
        try {
            while((dis.read(buffer) > 0)) {
                fis.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            fis.close();
            dis.close();
        }
        try {
            this.sharedSignal.setFileInfo(cofile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFileInfo() throws IOException {
        System.out.println("Waiting for fileInfo");
        ObjectInputStream ois=new ObjectInputStream(connection.getInputStream());
        Cofile cofile = null;
        try {
            while((cofile = (Cofile)ois.readObject())==null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.sharedSignal.setFileInfo(cofile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBlock(String[] args) throws IOException {
        this.sharedSignal.setBusy(true);
        this.sharedSignal.blockState[Integer.parseInt(args[1])] = 1;
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

        RandomAccessFile raf=new RandomAccessFile(Config.root+"/"+sharedSignal.getFileInfo().getPath(),"rw");
        raf.seek(1024*1024*Integer.parseInt(args[1]));
        raf.write(writeBlock,0,writeBlock.length);
        raf.close();
        this.sharedSignal.setBlockState(Integer.parseInt(args[1]),2);
        this.sharedSignal.setBusy(false);
    }

    public void sendBlock(String[] args) throws IOException, InterruptedException {

        RandomAccessFile raf=new RandomAccessFile(Config.root+"/"+args[0],"r");
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());

        byte[] block=new byte[1024*1024];
        raf.seek(1024*1024*Integer.parseInt(args[1]));
        int read=raf.read(block,0,block.length);
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
        this.sharedSignal.setBusy(false);
    }


    public void hasFile(String path) throws IOException {
        if(new File(Config.root+"/"+path).exists())
            sendRequest("hasFileResponse:"+path+":true");
        else
            sendRequest("hasFileResponse:"+path+":false");
    }

}