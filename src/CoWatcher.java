import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CoWatcher extends Thread{

    private final WatchService watcher;
    private final Map keys;
    private final boolean recursive;
    private boolean trace = true;
    private CoDB db;

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
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -&gt; %s\n", prev, dir);
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
        // register directory and sub-directories
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
     * Constructeur
     */
    CoWatcher(Path dir, boolean recursive,CoDB db) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap();
        this.recursive = recursive;

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
                    try {
                        System.out.format("%s: %s\n", event.kind().name(),child.getFileName());
                        String filename=child.getFileName().toString();
                        String path=Paths.get(Config.root).relativize(child).toString().substring(0, Paths.get(Config.root).relativize(child).toString().length() - child.getFileName().toString().length());
                        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String datesql=sdf.format(new File(child.toUri()).lastModified());
                        System.out.println("Modifié le "+datesql);
                        db.update("UPDATE FILES SET SUPPRESSED=1 WHERE NAME='"+filename+"' AND PATH='"+path+"'");
                    }catch (Exception se){
                        System.out.println(se);
                    }
                }

                if(kind==ENTRY_MODIFY){
                    try {
                        System.out.format("%s: %s\n", event.kind().name(),child.getFileName());
                        String filename=child.getFileName().toString();
                        String path=Paths.get(Config.root).relativize(child).toString().substring(0, Paths.get(Config.root).relativize(child).toString().length() - child.getFileName().toString().length());
                        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String datesql=sdf.format(new File(child.toUri()).lastModified());
                        System.out.println("Modifié le "+datesql);
                        db.update("UPDATE FILES SET DATE='"+datesql+"' WHERE NAME='"+filename+"' AND PATH='"+path+"'");
                    }catch (Exception se){
                        System.out.println(se);
                    }
                }

                // Si creation
                if (kind == ENTRY_CREATE) {
                    try {
                        //On surveille les nouveaux dossiers
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                        else{
                            //On enregistre en base les nouveaux fichiers
                            try {
                                System.out.format("%s: %s\n", event.kind().name(),child.getFileName());
                                String filename=child.getFileName().toString();
                                String path=Paths.get(Config.root).relativize(child).toString().substring(0, Paths.get(Config.root).relativize(child).toString().length() - child.getFileName().toString().length());
                                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String datesql=sdf.format(new File(child.toUri()).lastModified());
                                System.out.println("Créé le "+datesql);
                                db.update("INSERT INTO FILES(NAME,PATH,DATE,SUPPRESSED) VALUES ('"+filename+"','"+path+"','"+datesql+"',0)");
                            }catch (Exception se){
                                System.out.println(se);
                            }
                        }
                    } catch (IOException x) {
                        
                    }
                }
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
}