/**
 * Created by elie on 03/03/15.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Statement;

public class Cosync {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, Exception {
        /*Couser elie=new Couser("elie","password");
        elie.retrieveCosystems();
        System.out.println(elie.getCosystems().get(0).getIp());*/
        CoDB db=new CoDB();
//        String sql = "CREATE TABLE FILES " +
//                "(ID INT PRIMARY KEY     NOT NULL," +
//                " NAME           CHAR(255)    NOT NULL, " +
//                " PATH            CHAR(255)     NOT NULL, " +
//                " DATE        DATETIME, " +
//                " SUPPRESSED         BOOLEAN)";
//        db.query(sql);
        Path dir = (new File(Config.root).toPath());
        CoWatcher watcher = new CoWatcher(dir, true,db);
        watcher.start();
    }

}
