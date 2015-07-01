package CoSync;

import CoSync.Interface.*;
import CoSync.Models.CoEvent;
import CoSync.Models.Cosystem;
import CoSync.Models.Couser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Controlleur général de l'application
 */
public class CoController extends Thread {
    private CoDB coDB;
    private CoWatcher coWatcher;
    private Coserver coserver;

    private final HashMap<String, CoInterface> views;
    private Stack<CoEvent> events;
    private CoLoader loader;
    private Couser user;

    private String actualView;

    public CoDB getCoDB() {
        return coDB;
    }
    public void setCoDB(CoDB coDB) {
        this.coDB = coDB;
    }
    public Couser getUser() {
        return user;
    }
    public void setUser(Couser user) {
        this.user = user;
    }

    public CoController() throws SQLException, IOException {
        Path dir = new File(Config.root).toPath();

        this.coDB = new CoDB();
        this.coWatcher = new CoWatcher(dir, true, this);

        events = new Stack<>();
        views  = new HashMap<>();
    }

    public void run() {
        try {
            proceed();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void proceed() throws InterruptedException, IOException {
        views.put("login", new CoLoginMenu());

        loader = new CoLoader();

        try {
            if(!initApp()) {
                throw new Exception("Erreur Initialisation");
            }

            while (true) {

                sleep(0);
                if (views.get("login") != null) {
                    if(((CoLoginMenu)views.get("login")).isLogged()) {
                        // Récupération des informations de l'utilisateur
                        String error;
                        user = new Couser(((CoLoginMenu) views.get("login")).getLogin().getText(), String.valueOf(((CoLoginMenu) views.get("login")).getPassword().getPassword()));

                        ArrayList<Cosystem> systems = new ArrayList<>();
                        systems.add(new Cosystem("192.168.1.1", "000", "My PC"));
                        systems.add(new Cosystem("192.168.1.10", "001", "My Other PC"));
                        user.setCosystems(systems);

                        switchView("appli");
                    }
                }
            }
        } catch (Exception e) {
            loader.updateLoaderText(e.getMessage());
            loader.setTitle("Erreur");
            loader.setVisible(true);
            e.printStackTrace();
        }
    }

    public void addEvent(String type, String message) {
        events.push(new CoEvent(type, message));
        if(views.get("appli") != null) {
            ((CoMainMenu)views.get("appli")).updateListEvents(events);
        }
    }

    public void switchView(String view) throws InterruptedException {
        try {
            if(!views.containsKey(view) && view.equals("appli"))       { views.put("appli",  new CoMainMenu(this)); }
            if(!views.containsKey(view) && view.equals("managefiles")) { views.put("managefiles",  new CoFileMenu(this)); }

            if(!view.equals(actualView) ) {
                System.out.println(actualView +" => "+view);
                loader.setLoading(true);

                if(actualView != null)  views.get(actualView).setVisible(false);
                views.get(view).setVisible(true);
                views.get(view).update();
                actualView = view;

                sleep(1000);
                loader.setLoading(false);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private Boolean initApp() {

        try {
            Runnable server = new Coserver();
            Thread threadServer= new Thread(server);
            threadServer.start();

            long startTime = System.currentTimeMillis();
            String sql = "CREATE TABLE FILES " +
                    "(" +
                    " ID INTEGER PRIMARY KEY   AUTOINCREMENT,"+
                    " PATH       CHAR(255), " +
                    " DATE        INTEGER, " +
                    " SUPPRESSED         BOOLEAN,"+
                    " MODIFIEDAT  INTEGER);CREATE INDEX `index_path` ON `FILES` (`PATH`);CREATE INDEX `index_id` ON `FILES` (`ID`);";
            coDB.update(sql);
            coDB.prepareInsertBatch(coDB.insertFileSQL);
            coDB.prepareUpdateBatch(coDB.updateFileSQL);

            /*InetAddress address = InetAddress.getByName("10.33.1.247");
            Socket connection = new Socket(address, 8080);
            Runnable client=new Cosocket(connection,0);
            Thread threadClient= new Thread(client);
            threadClient.start();*/

            coDB.executeBatchInsert();
            coDB.executeBatchUpdate();
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("totalTime => "+totalTime);

            coWatcher.start();
            coWatcher.checkFolder(Paths.get(Config.root),coDB);
            coWatcher.saveSuppressed(coDB);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
