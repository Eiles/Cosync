package Controllers;

import Interface.*;
import Models.CoEvent;
import Models.Cofile;
import Models.Couser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.sql.SQLException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private CoVersionized versionized;
    private CoDownloader downloader;
    private Thread downloadThread;
    private CoDownloadMenu download;

    public synchronized CoDB getCoDB() {
        return coDB;
    }
    public synchronized void setCoDB(CoDB coDB) {
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
        coserver = new Coserver(this);
        versionized = new CoVersionized();
        watcher = new CoWatcher(dir, true, this, versionized);
        downSignal = new CoDownSignal();
        downloader = new CoDownloader(downSignal, this, versionized);


        events = new Stack<>();
        views = new HashMap<>();

    }

    public void run() {
        try {
            proceed();
        } catch (Exception e) {
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
            if(events.size() > 50)
                events.remove(events.lastElement());
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
            Couser user = new Couser(name, password);
            this.user = user;

            if (user.exist(InetAddress.getLocalHost().getHostName())) {
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

            coserver = new Coserver(this);
            Thread threadServer = new Thread(coserver);
            threadServer.start();

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


            watcher.start();
            watcher.checkFolder(Paths.get(Config.root), coDB);
            watcher.saveSuppressed(coDB);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getOldVersionsOfFile(String path) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File diffDir = new File("diff");
        File files[] = diffDir.listFiles();
        List<String> diffList = new LinkedList<String>();
        for (int i = 0; i < files.length; i++) {
            if (((files[i].getName().substring(0, files[i].getName().lastIndexOf('_')))).equals(path)) {
                diffList.add(files[i].getName().substring(files[i].getName().lastIndexOf('_') + 1));
            }
        }
        Collections.sort(diffList, Collator.getInstance().reversed());
        try {
            System.out.println(getCoDB().getModifiedAtForFile(path));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (diffList.size() > 0) {
            for (int i = 0; i < diffList.size(); i++) {
                long datelong = Long.parseLong(diffList.get(i).substring(0, diffList.get(i).lastIndexOf('-')));
                Date date = new Date();
                date.setTime(datelong);
                System.out.println("Can go back to : " + sdf.format(date));
            }
        }

        return diffList;
    }

    public static void getRevision(String path,String destination,List<String> list,int index){
        Cofile fic= new Cofile(path,0,false);
        String thePath=path;
        for(int i=0;i<=index;i++){
            fic.restoreFromDiff(Config.root+"/"+thePath,"diff/"+list.get(i),"tmp/revised");
            thePath="tmp/revised";
        }
        File output = new File("tmp/revised");
        output.renameTo(new File(destination));
    }
}
