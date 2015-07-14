package Controllers;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;


public class CoWatcher extends Thread{
    private static int cpt=0;
    private final WatchService watcher;
    private final Map keys;
    private final boolean recursive;
    private boolean trace = true;
    private CoDB db;
    private boolean active=false;
    private CoController controller;

    /**
     * Constructeur
     */
    CoWatcher(Path dir, boolean recursive,CoController controller) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap();
        this.recursive = recursive;
        this.controller = controller;
        this.db = controller.getCoDB();

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }


        this.trace = true;
        this.db=db;
    }

    public void run(){
        processEvents();
    }

    /**
     * Enregistre un dossier avec le service
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = (Path) keys.get(key);
            if (prev == null) {
                //System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    //System.out.format("update: %s -&gt; %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     *
     *Enregistrement recursif des sous-dossiers
     */
    private void registerAll(final Path start) throws IOException {
        // Enregistre un dossier et ses sous-dossier
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Boucle de traitement des evenements
     */
    void processEvents() {
        while (true)  {
            
            WatchKey key;

            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = (Path) keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent event: key.pollEvents()) {

                WatchEvent.Kind kind = event.kind();

                
                if (kind == OVERFLOW) {
                    continue;
                }

                // On recupère les infos d'evenements
                WatchEvent ev = event;
                Path name = (Path) ev.context();
                Path child = dir.resolve(name);

                // Si suppression
                if(kind==ENTRY_DELETE){
                    if(active) {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            try {
                                String path = Paths.get(Config.root).relativize(child).toString();
                                this.registerAll(child.getParent());
                                System.out.println(path);
                                System.out.println("UPDATE FILES SET SUPPRESSED=1" + " ,MODIFIEDAT=" + System.currentTimeMillis() + "  WHERE PATH LIKE '" + path + "%'");
                                db.update("UPDATE FILES SET SUPPRESSED=1" + " ,MODIFIEDAT=" + System.currentTimeMillis() + "  WHERE PATH LIKE '" + path + "%'");
                            } catch (Exception se) {
                                System.out.println(se);
                            }
                        } else {
                            try {
                                System.out.format("%s: %s\n", event.kind().name(), child.getFileName());
                                String path = Paths.get(Config.root).relativize(child).toString();
                                long datesql = new File(child.toUri()).lastModified();
                                System.out.println("Supprimé le " + datesql);
                                db.update("UPDATE FILES SET SUPPRESSED=1" + " ,MODIFIEDAT=" + System.currentTimeMillis() + "  WHERE PATH LIKE '" + path + "/%'");
                                db.update("UPDATE FILES SET SUPPRESSED=1" + " , MODIFIEDAT=" + System.currentTimeMillis() + "  WHERE PATH='" + path + "'");
                            } catch (Exception se) {
                                System.out.println(se);
                            }
                        }
                    }
                }

                if(kind==ENTRY_MODIFY){
                    if(active) {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            try {
                                checkFolder(child, db);
                                db.update("UPDATE FILES SET SUPPRESSED=1" + " , MODIFIEDAT=" + System.currentTimeMillis() + "  WHERE PATH LIKE '" + child + "%'");
                            } catch (Exception se) {
                                System.out.println(se);
                            }
                        } else {
                            try {
                                System.out.format("%s: %s\n", event.kind().name(), child.getFileName());
                                String path = Paths.get(Config.root).relativize(child).toString();
                                long datesql = new File(child.toUri()).lastModified();
                                System.out.println("Modifié le " + datesql);
                                db.update("UPDATE FILES SET DATE=" + datesql + ", MODIFIEDAT=" + System.currentTimeMillis() + " WHERE PATH='" + path + "'");
                            } catch (Exception se) {
                                System.out.println(se);
                            }
                        }
                    }
                }

                // Si creation
                if (kind == ENTRY_CREATE) {
                    if(active) {
                        try {
                            //On surveille les nouveaux dossiers
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                checkFolder(child, db);
                                registerAll(child);
                            } else {
                                //On enregistre en base les nouveaux fichiers
                                try {
                                    System.out.format("%s: %s\n", event.kind().name(), child.getFileName());
                                    String path = Paths.get(Config.root).relativize(child).toString();
                                    long datesql = new File(child.toUri()).lastModified();
                                    System.out.println("Créé le " + datesql);
                                    db.update("INSERT INTO FILES(PATH,DATE,SUPPRESSED,MODIFIEDAT) VALUES ('" + path + "'," + datesql + ",0," + System.currentTimeMillis() + ")");
                                } catch (Exception se) {
                                    System.out.println(se);
                                }
                            }
                        } catch (IOException x) {

                        }
                    }
                }

                controller.addEvent(kind, child);
            }

            // On retire la clef si le dossier n'est plus accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // Si tout est innaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void check(Path file,CoDB db) throws ParseException, SQLException {
        cpt++;
        File fic=new File(file.toUri());
        String path= Paths.get(Config.root).relativize(file).toString();
        String filename=file.getFileName().toString();
        long datesql=fic.lastModified();
        long olddate=db.getDateForFile(path.toString(), filename);
        boolean suppressed=db.getSuppressedForFile(path.toString(), filename);
        //System.out.println(cpt);
        if(olddate==0){
            db.addForBatchInsert(path,filename,datesql,0);
            if(db.getInsertSize()>1000){
                db.executeBatchInsert();
            }
        }else{
            if(datesql!=olddate || suppressed){
                db.addForBatchUpdate(path.toString(),datesql);
                if(db.getUpdateSize()>1000){
                    db.executeBatchUpdate();
                }
            }
        }
    }

    public void checkFolder(Path folder, CoDB db) throws IOException {
        Files.walk(folder)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        check(file,db);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } /*catch (IOException e) {
                        e.printStackTrace();
                    } */catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
        try {
            db.executeBatchInsert();
            db.executeBatchUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cpt=0;
    }

    public void saveSuppressed(CoDB db){
        int idIncrementer=1;
        int suppressed=0;
        String path;
        try{
            while(true){
                System.out.println(idIncrementer);
                path=db.getFilePathById(idIncrementer);
                if(path!=null && !Paths.get(Config.root+"/"+path).toFile().exists()){
                    db.update("UPDATE FILES SET SUPPRESSED=1 WHERE ID="+idIncrementer);
                    suppressed++;
                }
                idIncrementer++;
            }
        }catch (Exception e){
            System.out.println("Files checked : "+idIncrementer);
            System.out.println("Files marked suppressed : "+suppressed);
            return;
        }
    }

}