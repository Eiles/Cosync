package Controllers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alban on 18/07/2015.
 */
public class CoVersionized {
    protected List<String> versionizedFiles;

    public CoVersionized() {
        versionizedFiles = new ArrayList<>();
    }

    public synchronized List<String> getVersionizedFiles() {
        return versionizedFiles;
    }

    public synchronized void setVersionizedFiles(List<String> versionizedFiles) {
        this.versionizedFiles = versionizedFiles;
    }

    public synchronized boolean isFileInVersionized(String file) {

        return versionizedFiles.contains(file);
    }
}
