/**
 * Created by elie on 03/03/15.
 */

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;

public class Cosync {
    static int cpt;
    static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void main(String[] args)
            throws NoSuchAlgorithmException, IOException, Exception {
        /*Couser elie=new Couser("elie","password");
        elie.retrieveCosystems();
        System.out.println(elie.getCosystems().get(0).getIp());*/
        long startTime = System.currentTimeMillis();
        CoDB db=new CoDB();
        String sql = "CREATE TABLE FILES " +
                "(" +
                " ID INTEGER PRIMARY KEY   AUTOINCREMENT,"+
                " PATH       CHAR(255), " +
                " DATE        INTEGER, " +
                " SUPPRESSED         BOOLEAN,"+
                " MODIFIEDAT  INTEGER);CREATE INDEX `index_path` ON `FILES` (`PATH`);CREATE INDEX `index_id` ON `FILES` (`ID`);";
        db.update(sql);
        db.prepareInsertBatch(db.insertFileSQL);
        db.prepareUpdateBatch(db.updateFileSQL);
        db.executeBatchInsert();
        db.executeBatchUpdate();
        Path dir = new File(Config.root).toPath();
        CoWatcher watcher = new CoWatcher(dir, true,db);
        watcher.start();
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        watcher.checkFolder(Paths.get(Config.root),db);
        watcher.saveSuppressed(db);

        Runnable server = new Coserver();
        Thread threadServer= new Thread(server);
        threadServer.start();
        /*CoSignal signal=new CoSignal();
        InetAddress address = InetAddress.getByName("localhost");
        Socket connection = new Socket(address, 7777);
        Runnable client=new Cosocket(connection,0,signal);
        Thread threadClient= new Thread(client);
        threadClient.start();
        //signal.addRequest("auth");
        signal.addRequest("getBlock:Parks.and.Recreation.S02E01.HDTV.XviD-2HD.avi:0");
        /*File testFile=new File(Config.root+"/"+"Project Zomboid.zip");
        Cofile testCofile = new Cofile("Project Zomboid.zip",0,false);
        System.out.println("Size : "+testFile.length());
        System.out.println(testCofile.getHexHash());
        testCofile.generateBlockHash();
        for(int i=0;i<testCofile.blockHash.length;i++){
            System.out.println("Block "+(i+1)+" : "+testCofile.hashToHex(testCofile.blockHash[i]));
        }*/
        /*Cofile file=new Cofile("2",0,false);
        file.generateBlockHash();
        System.out.println("Blocks : "+ file.blockHash.length);*/
        CoDownSignal signal=new CoDownSignal();
        CoDownloader dler=new CoDownloader(signal);
        Thread threadDler= new Thread(dler);
        threadDler.start();
        signal.addRequest("film.avi");
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(totalTime);
    }
}