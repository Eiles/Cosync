package Controllers;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alban on 18/07/2015.
 */
public class CoVersionized {
    //protected List<String> versionizedFiles;
    protected HashMap<String,Boolean> versionizedFiles;

    public CoVersionized(){
        this.versionizedFiles=new HashMap<>();
    }



    public synchronized HashMap getVersionizedFiles() {
        return versionizedFiles;
    }


    public synchronized void  addVersionizedFile(String file, Boolean state){
        versionizedFiles.put(file,state);
    }

    public synchronized boolean isFileInVersionized(String file) {
        if(versionizedFiles.get(file)!=null) {
            if (versionizedFiles.get(file) == true)
                versionizedFiles.remove(file);
            return true;
        }
        return false;
    }
}
