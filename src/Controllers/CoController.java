package Controllers;

import Interface.*;
import Models.CoEvent;
import Models.Couser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Controlleur général de l'application
 */
public class CoController extends Thread {
    private CoDB coDB;
    private CoWatcher watcher;
    private Coserver coserver;

    private final HashMap<String, CoInterface> views;
    private Stack<CoEvent> events;
    private CoLoader loader;
    private Couser user;

    private String actualView;
    private CoDownSignal downSignal;
    private CoDownloader downloader;
    private Thread downloadThread;
    private CoDownloadMenu download;

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
    public Stack<CoEvent> getEvents() {
        return events;
    }

    public void setEvents(Stack<CoEvent> events) {
        this.events = events;
    }

    public HashMap<String, CoInterface> getViews() {
        return views;
    }

    public CoController() throws SQLException, IOException {
        Path dir = new File(Config.root).toPath();

        coDB = new CoDB();
        coserver = new Coserver();
        watcher = new CoWatcher(dir, true, this);
        downSignal = new CoDownSignal();
        downloader = new CoDownloader(downSignal, this);

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
        views.put("login", new CoLoginMenu(this));
        loader = new CoLoader();

        try {
            if(!initApp()) {
                throw new Exception("Erreur Initialisation");
            }

            while (true) {

                sleep(500);
                if (null == user) {
                    // Récupération des informations de l'utilisateur
                    String error;

                    switchView("login");
                }
            }
    } catch (Exception e) {
            loader.updateLoaderText(e.getMessage());
            loader.setTitle("Erreur");
            loader.setVisible(true);
            e.printStackTrace();
        }
    }

    public void addEvent(WatchEvent.Kind kind, Path child) {

        String type = null;

        if(kind == ENTRY_DELETE) {
            type = "Suppression";
        }

        if(kind == ENTRY_CREATE) {
            type = "Ajout";
        }

        if(kind == ENTRY_MODIFY) {
            type = "Modification";
        }

        if(views.get("main") != null) {
            events.push(new CoEvent(type, child.getFileName().toString()));
            ((CoMainMenu)views.get("main")).updateListEvents(events);
        }
    }

    public void switchView(String view) throws InterruptedException {
        try {
            if(!views.containsKey(view) && view.equals("main"))       { views.put("main",  new CoMainMenu(this)); }
            else if(!views.containsKey(view) && view.equals("managefiles")) { views.put("managefiles",  new CoFileMenu(this)); }

            if(!view.equals(actualView) ) {
                System.out.println(actualView +" => "+view);
                loader.setLoading(true);

                if(actualView != null)  views.get(actualView).setVisible(false);
                views.get(view).setVisible(true);
                views.get(view).update();
                actualView = view;

                loader.setLoading(false);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public void showView(String view) throws InterruptedException {
        try {
            if(!views.containsKey(view) && view.equals("main"))       { views.put("main",  new CoMainMenu(this)); }
            else if(!views.containsKey(view) && view.equals("managefiles")) { views.put("managefiles",  new CoFileMenu(this)); }
            else if(!views.containsKey(view) && view.equals("downloads")) { views.put("downloads",  new CoDownloadMenu(this)); }

            views.get(view).setVisible(true);
            views.get(view).update();

        } catch (Exception e) {
            throw e;
        }
    }

    public void logIn(String name, String password) throws Exception {

        try {
            //TODO: Vérification et récupération des données du User
            Couser user = new Couser(name, password);
            this.user = user;

            if (user.exist()) {
                user.retrieveCosystems();

                /*if(downloadThread.isAlive ()) {
                    downloadThread.
                }*/
                downloadThread = new Thread(downloader);
                downloadThread.start();

                switchView("main");
            } else {
                JOptionPane.showMessageDialog(null, "L'utilisateur renseigné n'existe pas");
            }
        }
        catch (Exception e) {
                loader.updateLoaderText(e.getMessage());
                loader.setTitle("Erreur");
                loader.setVisible(true);
                e.printStackTrace();
        }
    }

    public void logOut() throws InterruptedException {

        this.user = null;
    }

    private Boolean initApp() {

        try {
            System.out.println("Init Application");

            coserver = new Coserver();
            Thread threadServer = new Thread(coserver);
            threadServer.start();

            long startTime = System.currentTimeMillis();
            String sql = "CREATE TABLE FILES " +
                    "(" +
                    " ID INTEGER PRIMARY KEY   AUTOINCREMENT,"+
                    " PATH       CHAR(255), " +
                    " DATE        INTEGER, " +
                    " SUPPRESSED         BOOLEAN," +
                    " NEEDDOWNLOAD      BOOLEAN  DEFAULT 0,"+
                    " MODIFIEDAT  INTEGER);CREATE INDEX `index_path` ON `FILES` (`PATH`);CREATE INDEX `index_id` ON `FILES` (`ID`);";
            coDB.update(sql);

            sql = "CREATE TABLE LASTDB "+
                    "(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "SYSTEM     CHAR(255)," +
                    "UPDATEDATE      INTEGER" +
                    ");";
            coDB.update(sql);

            coDB.prepareInsertBatch(coDB.insertFileSQL);
            coDB.prepareUpdateBatch(coDB.updateFileSQL);
            coDB.prepareUpdateLastDBBatch(coDB.updateLastDBSQL);
            coDB.prepareInsertLastDBBatch(coDB.insertLastDBSQL);

            coDB.executeBatchInsert();
            coDB.executeBatchUpdate();
            coDB.executeBatchLastDBInsert();
            coDB.executeBatchLastDBUpdate();

            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("totalTime => "+totalTime);

            watcher.start();
            watcher.checkFolder(Paths.get(Config.root), coDB);
            watcher.saveSuppressed(coDB);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
