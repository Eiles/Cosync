package Models;

import Controllers.Config;
import difflib.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Cofile implements Config,Serializable{
    byte[] hash;
    String path;
    long modDate;
    public byte[][]blockHash;
    long length;


    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Cofile(String path,long date,boolean suppressed) {
        super();
        this.path = path;
        this.modDate=date;

    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getAbsolutePath(){
        return System.getProperties().getProperty("user.home")+"/Cosync"+"/"+path;
    }

    public void generateHash() throws NoSuchAlgorithmException, IOException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis=null;
        try{
            fis = new FileInputStream(this.getAbsolutePath());
            byte[] dataBytes = new byte[1024];

            int nread=0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            this.hash=md.digest();
        }finally{
            if (fis!=null) {
                fis.close();
            }
        }
    }
    public void generateBlockHash() throws NoSuchAlgorithmException, IOException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis=null;
        int blockNumber=0;
        int nread;
        try{
            fis = new FileInputStream(this.getAbsolutePath());
            int hashsize=(int)Math.ceil((double)(new File(this.getAbsolutePath())).length()/(double)(1024*1024));
            System.out.println((new File(this.getAbsolutePath())).length());
            this.blockHash=new byte[hashsize][];

            while(blockNumber<this.blockHash.length) {
                byte[] dataBytes = new byte[1024*1024];
                nread=fis.read(dataBytes);
                md.update(dataBytes, 0, nread);
                this.blockHash[blockNumber] = md.digest();
                blockNumber++;
                md.reset();
            }
        }finally{
            if (fis!=null) {
                fis.close();
            }
        }
    }
    private  List<String> cofileToLines(String filename) {
        List<String> lines = new LinkedList<String>();
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public Patch generatePatch(String oldfile, String newfile) throws Exception {
        List<String> original = cofileToLines(oldfile);
        List<String> revised  = cofileToLines(newfile);
        return DiffUtils.diff(original, revised);
    }

    public void savePatch(String oldfile, String newfile,String path) {
        try {
            List<String> str=DiffUtils.generateUnifiedDiff(oldfile, newfile, cofileToLines(oldfile), generatePatch(oldfile, newfile), 80);
            FileWriter fw=new FileWriter(path);
            for(int i=0;i<str.size();i++){
                fw.write(str.get(i)+"\n");
            }
            fw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreFromDiff( String file,String diffpath,String destination) {
        try {
            List<String> diff=cofileToLines(diffpath);
            Patch p=DiffUtils.parseUnifiedDiff(diff);
            List<String> toRestore= cofileToLines(file);
            List restored = DiffUtils.unpatch(toRestore, p);

            FileWriter fw=new FileWriter(destination);
            for(int i=0;i<restored.size();i++){
                fw.write(restored.get(i)+"\n");
            }
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getModDate() {
        return modDate;
    }

    public void setModDate(long modDate) {
        this.modDate = modDate;
    }


    public String getHexHash() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getHash().length; i++)
            sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
        return sb.toString();
    }

    public String hashToHex(byte[] hash) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; i++)
            sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
        return sb.toString();
    }

    public byte[] getHash() {
        if(this.hash==null){
            try {
                this.generateHash();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hash;
    }

    public byte[][] getBlockHash() {
        if(this.blockHash==null){
            try {
                this.generateBlockHash();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return blockHash;
    }

    @Override public boolean equals(Object other) {
        //check for self-comparison
        if ( this == other ) return true;

        if ( !(this instanceof Cofile) ) return false;

        //cast to native object is now safe
        Cofile that = (Cofile)other;

        //now a proper field-by-field evaluation can be made
        return Arrays.equals(this.getBlockHash(),((Cofile) other).getBlockHash());
    }
}