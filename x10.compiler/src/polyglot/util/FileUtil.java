package polyglot.util;

import java.io.File;

public class FileUtil {
    /** Check that file in dir has the same name one would get by listing the directory contents.
     *  This prevents list.java from being found when looking for a class named List.
     */
    public static boolean checkNameFromRoot(File dir, File file) {
    	if (! file.exists()) {
    		return false;
    	}
        final File parentFile = file.getParentFile();
        if (parentFile==null) return false;
        if (parentFile.equals(dir)) {
            File[] ls = dir.listFiles();
            for (File f : ls) {
                if (f.getName().equals(file.getName())) {
                    return true;
                }
            }
            return false;
        }
        else {
            return checkNameFromRoot(dir, parentFile) &&
                checkNameFromRoot(parentFile, file);
        }
    }

}
