package x10.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class RunTestSuite {
    private static final String[] EXCLUDE_FILES_WITH_SUFFIX = {
            "_DYNAMIC_CALLS.x10",
            "_MustFailCompile.x10",
    };
    private static final String[] EXCLUDE_FILES = {
    };
    private static final String[] EXCLUDE_FILES_WITH = {
            "TypedefOverloading","NQueens"
    };

    static {
        Arrays.sort(EXCLUDE_FILES);
    }
    private static boolean shouldIgnoreFile(String name) {
        if (Arrays.binarySearch(EXCLUDE_FILES,name)>=0) return true;
        for (String suffix : EXCLUDE_FILES_WITH_SUFFIX)
            if (name.endsWith(suffix))
                return true;
        for (String mid : EXCLUDE_FILES_WITH)
            if (name.contains(mid))
                return true;
        return false;
    }
    private static final int MAX_FILES_NUM = Integer.MAX_VALUE; // Change it if you want to process only a small number of files

    /**
     * Finds all *.x10 files in all sub-directories, and compiles them.
     * @param args
     *  The first argument is the directory to search all the *.x10 files.
     *  E.g.,
     *  C:\cygwin\home\Yoav\intellij\sourceforge\x10.runtime\src-x10
     *  C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests
     * @throws Throwable
     */
    public static boolean ONE_FILE_AT_A_TIME = false;
    public static void main(String[] args) throws Throwable {
        assert args.length>0 : "The first command line argument must be an x10 filename or the directory of x10.tests";
        File dir = new File(args[0]);
        String[] newArgs = args;
        if (!dir.getName().endsWith(".x10")) {
            assert dir.isDirectory() : "The first command line argument must be the directory of x10.tests, and you passed: "+dir;
            ArrayList<String> files = new ArrayList<String>(10);
            recurse(dir,files);
            assert files.size()>0 : "Didn't find any .x10 files to compile in any subdirectory of "+dir;
            if (ONE_FILE_AT_A_TIME) {
                for (String f : files) {
                    newArgs[0] = f;
                    // adding to -sourcepath
                    for (int i=1; i<newArgs.length; i++)
                        if (newArgs[i].contains("/x10.runtime/src-x10;"))
                            newArgs[i] += ";"+f.substring(0,f.lastIndexOf('/'));
                    System.out.println("Running: "+ Arrays.toString(newArgs));
                    polyglot.main.Main.main(newArgs);
                }
                return;
            }
            newArgs = new String[args.length-1+files.size()];
            files.toArray(newArgs);
            System.arraycopy(args,1,newArgs,files.size(),args.length-1);
        }                               
        System.out.println("Running: "+ Arrays.toString(newArgs));
        polyglot.main.Main.main(newArgs);
    }
    private static void recurse(File dir, ArrayList<String> files) {
        if (files.size()>=MAX_FILES_NUM) return;
        for (File f : dir.listFiles()) {
            if (files.size()>=MAX_FILES_NUM) return;
            if (f.isDirectory())
                recurse(f, files);
            else {
                String name = f.getName();
                if (name.endsWith(".x10") && !shouldIgnoreFile(name)) {                    
                    files.add(f.getAbsolutePath().replace('\\','/'));
                }
            }
        }
    }
}