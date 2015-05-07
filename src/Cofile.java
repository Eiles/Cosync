

import difflib.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class Cofile implements Config{
    byte[] hash;
    String filename;
    String path;

    public Cofile(String path,String filename) {
        super();
        this.path = path;
        this.filename = filename;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public byte[] getHash() {
        return hash;
    }
    public void setHash(byte[] hash) {
        this.hash = hash;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getAbsolutePath(){
        return root+path+filename;
    }
    public void generateHash() throws NoSuchAlgorithmException, IOException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis=null;
        try{
            fis = new FileInputStream(this.getAbsolutePath());
            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };
            byte[] mdbytes = md.digest();
            this.setHash(mdbytes);
        }finally{
            if (fis!=null) {
                fis.close();
            }
        }
    }
    private static List<String> cofileToLines(String filename) {
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

    public Patch generatePatch(String oldfile, String newfile){
        List<String> original = cofileToLines(oldfile);
        List<String> revised  = cofileToLines(newfile);
        return DiffUtils.diff(original, revised);
    }



    public String getHexHash(){
        StringBuilder sb = new StringBuilder();
        for (byte b : this.getHash()) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        return sb.toString();
    }
}

