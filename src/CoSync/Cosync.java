package CoSync; /**
 * Created by elie on 03/03/15.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

public class Cosync {
    static int cpt;
    static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args)
            throws NoSuchAlgorithmException, IOException, Exception {

        //CoSync.Models.Couser elie=new CoSync.Models.Couser("elie","password");
        //elie.retrieveCosystems();
        //System.out.println(elie.getCosystems().get(0).getIp());

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
        Path dir = new File(Config.root).toPath();
        CoWatcher watcher = new CoWatcher(dir, true,db);
        watcher.start();
        watcher.checkFolder(Paths.get(Config.root),db);
        watcher.saveSuppressed(db);

        System.out.println(cpt);
        Runnable server = new Coserver();
        Thread threadServer= new Thread(server);
        threadServer.start();

        /*InetAddress address = InetAddress.getByName("10.33.1.247");
        Socket connection = new Socket(address, 8080);
        Runnable client=new Cosocket(connection,0);
        Thread threadClient= new Thread(client);
        threadClient.start();*/

        db.executeBatchInsert();
        db.executeBatchUpdate();
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("totalTime => "+totalTime);

        CoController coController = new CoController();
        coController.start();
    }
}