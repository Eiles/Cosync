package CoSync;

import CoSync.Interface.CoInterface;
import CoSync.Interface.LoginMenu;
import CoSync.Models.Cosystem;
import CoSync.Models.Couser;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alban on 16/06/2015.
 */
public class CoController extends Thread {
    private HashMap<String, JFrame> views;
    private Couser user;

    public void run() {
        try {
            proceed();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void proceed() throws InterruptedException {
        views = new HashMap<>();
        views.put("login", new LoginMenu());

        while (true) {
            sleep(0);
            if (views.get("login") != null) {
                if(((LoginMenu)views.get("login")).isLogged()) {
                    user = new Couser(((LoginMenu) views.get("login")).getLogin().getText(), String.valueOf(((LoginMenu) views.get("login")).getPassword().getPassword()));

                    ArrayList<Cosystem> systems = new ArrayList<>();
                    systems.add(new Cosystem("192.168.1.1", "000", "My PC"));
                    systems.add(new Cosystem("192.168.1.10", "001", "My Other PC"));
                    user.setCosystems(systems);

                    if(!views.containsKey("appli")) {
                        views.put("appli",  new CoInterface());

                        views.get("login").setVisible(false);
                        views.remove("login");
                    }

                    ((CoInterface)views.get("appli")).updateHeader(user);
                    ((CoInterface)views.get("appli")).updateListSystem(user);
                }
            }
        }
    }

    private void switchView(String view) {

    }
}
