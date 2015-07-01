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


        final CoController coController = new CoController();
        coController.start();
    }
}