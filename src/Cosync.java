/**
 * Created by elie on 03/03/15.
 */

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Cosync {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, Exception {
        Couser elie=new Couser("elie","password");
        elie.retrieveCosystems();
        System.out.println(elie.getCosystems().get(0).getIp());
    }

}
