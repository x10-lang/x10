package x10.util;

import polyglot.frontend.Globals;
import polyglot.util.ErrorQueue;
import polyglot.util.SilentErrorQueue;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.main.Report;
import polyglot.main.Main;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

public class RunTestSuite {
    // I have 3 kind of markers:
    // "// ... ERR"  - marks an error
    // "// ... ShouldNotBeERR" - the compiler reports an error, but it shouldn't
    // ShouldBeErr - the compiler doesn't report an error, but it should

    // todo: some _MustFailCompile in the test suite cause compiler crashes
    // todo: add support for various options, like testing with STATIC_CALLS/DYNAMIC_CALLS

    //_MustFailCompile means the compilation should fail.
    // Inside those files we should have "//.*ERR" markers that we use to test the position of the errors is correct.
    //_MustFailTimeout means that when running the file it will have an infinite loop
    private static final String[] EXCLUDE_FILES_WITH_SUFFIX = {
            "_DYNAMIC_CALLS.x10",
            "NonX10Constructs_MustFailCompile.x10",
            "_MustFailCompile.x10",
    };
    private static final String[] EXCLUDE_FILES = {
            "NOT_WORKING","SSCA2","FT-alltoall","FT-global"
    };
    private static final String[] EXCLUDE_FILES_WITH = {
            "HeatTransfer_v0.x10",
            "TypedefOverloading",
            "PlaceCheckArray.x10",
    };
    private static final String[] INCLUDE_ONLY_FILES_WITH = {
            //"_MustFailCompile.x10",
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
        if (INCLUDE_ONLY_FILES_WITH.length>0) {
            for (String mid : INCLUDE_ONLY_FILES_WITH)
                if (name.contains(mid))
                    return false;
            return true; 
        }
        return false;
    }
    public static boolean ONE_FILE_AT_A_TIME = false;
    private static final int MAX_FILES_NUM = Integer.MAX_VALUE; // Change it if you want to process only a small number of files    

    /**
     * Finds all *.x10 files in all sub-directories, and compiles them.
     * @param args
     *  The first argument is the directory to search all the *.x10 files.
     *  E.g.,
     *  C:\cygwin\home\Yoav\intellij\sourceforge\x10.runtime\src-x10
     *  C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests
     * @throws Throwable Can be a failed assertion or missing file.
     */
    public static void main(String[] args) throws Throwable {
        assert args.length>0 : "The first command line argument must be an x10 filename or a comma separated list of the directories.\n"+
                    "E.g.,\n"+
                    "C:\\cygwin\\home\\Yoav\\intellij\\sourceforge\\x10.tests,C:\\cygwin\\home\\Yoav\\intellij\\sourceforge\\x10.dist\\samples,C:\\cygwin\\home\\Yoav\\intellij\\sourceforge\\x10.runtime\\src-x10";
        List<String> remainingArgs = new ArrayList<String>(Arrays.asList(args));
        remainingArgs.remove(0);
        //remainingArgs.add("-STATIC_CALLS");

        final String dirName = args[0];
        ArrayList<File> files = new ArrayList<File>(10);
        if (dirName.endsWith(".x10")) {
            final File dir = new File(dirName);
            assert dir.isFile() : "File doesn't not exists: "+dirName;
            files.add(dir);
        } else {
            for (String dirStr : dirName.split(",")) {
                File dir = new File(dirStr);
                assert dir.isDirectory() : "The first command line argument must be the directory of x10.tests, and you passed: "+dir;
                int before = files.size();
                recurse(dir,files);
                if (before==files.size()) System.out.println("Warning: Didn't find any .x10 files to compile in any subdirectory of "+dir);
            }
        }
        if (ONE_FILE_AT_A_TIME) {
            for (File f : files) {
                compileFiles(Arrays.asList(f),remainingArgs);
            }
        } else {
            compileFiles(files,remainingArgs);
        }
    }
    private static int count(String s, String sub) {
        int index=-1, res=0;
        while ((index=s.indexOf(sub,index+sub.length()))>=0) res++;
        return res;
    }
    private static void compileFiles(List<File> files, List<String> args) throws IOException {
        // replace \ with /
        ArrayList<String> fileNames = new ArrayList<String>(files.size());
        for (File f : files) {
            final BufferedReader in = new BufferedReader(new FileReader(f));
            String firstLine = in.readLine();
            assert firstLine!=null : f;
            in.close();
            if (firstLine.contains("IGNORE_FILE"))
                continue;
            fileNames.add(f.getAbsolutePath().replace('\\','/'));
        }
        // adding the directories of the files to -sourcepath (in case they refer to other files that are not compiled, e.g., if we decide to compile the files one by one)
        HashSet<String> directories = new HashSet<String>();
        for (String f : fileNames) {
            final int index = f.lastIndexOf('/');
            assert index>0 : f;
            directories.add(f.substring(0, index));
        }
        String dirs = "";
        for (String dir : directories)
            dirs += ";"+dir;
        int argsNum = args.size();
        for (int i=1; i<argsNum; i++) {
            final String arg = args.get(i);
            if (arg.contains("/x10.runtime/src-x10;")) {
                args.set(i,arg+dirs);
                break;
            }
        }
        // Now running polyglot
        List<String> allArgs = new ArrayList<String>(fileNames);
        allArgs.addAll(args);
        String[] newArgs = allArgs.toArray(new String[allArgs.size()]);
        System.out.println("Running: "+ Arrays.toString(newArgs));
        SilentErrorQueue errQueue = new SilentErrorQueue(10000,"TestSuiteErrQueue");
        try {
            new polyglot.main.Main().start(newArgs,errQueue);
        } catch (Main.TerminationException e) {
            // If we had errors (and we should because we compile _MustFailCompile) then we will get a non-zero exitCode
        }
        ArrayList<ErrorInfo> errors = (ArrayList<ErrorInfo>)errQueue.getErrors();

        // Now checking the errors reported are correct and match ERR markers
        // 1. find all ERR markers that don't have a corresponding error
        for (File file : files) {
            if (Report.should_report("TestSuite", 3))
                Report.report(3, "Looking for ERR markers in file "+ file);
            BufferedReader in = new BufferedReader(new FileReader(file));
            int lineNum = 0;
            boolean foundErr = false;
            String line;
            while ((line=in.readLine())!=null) {
                lineNum++;
                if (line.contains("ERR") && line.contains("//") &&
                    !file.getName().contains("Console.x10")) { // Console defines "static ERR:Printer"
                    foundErr = true;
                    // try to find the matching error
                    int expectedMatchCount = count(line,"ERR");
                    int foundMatchCount = 0;
                    ArrayList<ErrorInfo> errorsFound = new ArrayList<ErrorInfo>(expectedMatchCount);
                    for (Iterator<ErrorInfo> it=errors.iterator(); it.hasNext(); ) {
                        ErrorInfo err = it.next();
                        final Position position = err.getPosition();
                        if (new File(position.file()).equals(file) && position.line()==lineNum) {
                            // found it!
                            errorsFound.add(err);
                            if (Report.should_report("TestSuite", 2))
                                Report.report(2, "Found error: "+ err);
                            it.remove();
                            foundMatchCount++;
                        }
                    }
                    if (expectedMatchCount!=foundMatchCount)
                        System.err.println("File "+file+" has "+expectedMatchCount+" ERR markers on line "+lineNum+", but the compiler reported "+foundMatchCount+" errors on that line! errorsFound=\n"+errorsFound);
                }
            }
            in.close();
            if (!foundErr && file.getName().endsWith("_MustFailCompile.x10")) {
                System.err.println("File "+file+" ends in _MustFailCompile.x10 but it doesn't contain any 'ERR' markers!");
            }
        }

        // 2. report all the remaining errors that didn't have a matching ERR marker
        // first report warnings
        int warningCount = 0;
        for (ErrorInfo err : errors)
            if (err.getErrorKind()==ErrorInfo.WARNING) {
                System.err.println("Got a warning in position: "+err.getPosition()+"\nMessage: "+err+"\n");
                warningCount++;
            }
        if (errors.size()>warningCount) {
            System.err.println("\nThe following errors did not have a matching ERR marker:\n\n");
            for (ErrorInfo err : errors)
                if (err.getErrorKind()!=ErrorInfo.WARNING)
                    System.err.println("Position:\n"+err.getPosition()+"\nMessage: "+err+"\n");
        }
        // todo: check that for each file (without errors) we generated a *.class file, and load them and run their main method (except for the ones with _MustFailTimeout) 
    }
    private static void recurse(File dir, ArrayList<File> files) {
        if (files.size()>=MAX_FILES_NUM) return;
        for (File f : dir.listFiles()) {
            String name = f.getName();
            if (!f.isDirectory() && shouldIgnoreFile(name)) continue;
            if (files.size()>=MAX_FILES_NUM) return;
            if (f.isDirectory())
                recurse(f, files);
            else {
                if (name.endsWith(".x10")) {
                    files.add(f);
                }
            }
        }
    }
}