package CoSync.Models;

import CoSync.Config;
import difflib.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class Cofile implements Config {
    byte[] hash;
    String path;
    long modDate;
    boolean suppressed;





    public Cofile(String path,long date,boolean suppressed) {
        super();
        this.path = path;
        this.modDate=date;
        this.suppressed=suppressed;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getAbsolutePath(){
        return Config.root+path;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public byte[] generateHash() throws NoSuchAlgorithmException, IOException,Exception{
        if(this.isSuppressed()){
            throw new Exception("File no longer exists");
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis=null;
        try{

            fis = new FileInputStream(this.getAbsolutePath());
            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };
            return md.digest();
        }finally{
            if (fis!=null) {
                fis.close();
            }
        }
    }
    private  List<String> cofileToLines(String filename) throws Exception {
        if(this.isSuppressed()){
            throw new Exception("File no longer exists");
        }
        List<String> lines = new LinkedList<String>();
        String line = "";
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

    public long getModDate() {
        return modDate;
    }

    public void setModDate(long modDate) {
        this.modDate = modDate;
    }


    public String getHexHash() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (byte b : this.generateHash()) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        return sb.toString();
    }
}

