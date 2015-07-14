package Services;

import java.io.File;

/**
 * Created by Alban on 11/07/2015.
 */
public class FilesServices {
    public static void addFile(String addFile, String newFile) {

    }

    public static void deleteFile(File file) {
        if(file.isFile()) {
            file.delete();
        }
    }
}
