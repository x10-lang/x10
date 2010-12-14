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

import x10.types.X10TypeMixin;
import x10.parser.AutoGenSentences;

public class RunTestSuite {
    // todo: C:\cygwin\home\Yoav\intellij\sourceforge\x10.dist\samples\tutorial\HeatTransfer_v1.x10:47,46-57 causes: (Warning) Reached threshold when checking constraints. If type-checking fails
    // I have 3 kind of markers:
    // "// ... ERR"  - marks an error or warning
    // "// ... ShouldNotBeERR" - the compiler reports an error, but it shouldn't
    // ShouldBeErr - the compiler doesn't report an error, but it should
    // We always run with -VERBOSE_CALLS.

    // some _MustFailCompile in the test suite cause compiler crashes
    // files with ERR markers must end with _MustFailCompile, and these are compiled by themselves
    // the rest of the files shouldn't have any errors, therefore we should proceed for these to code generation phase
    // (and in the future even run the resulting code)

    //_MustFailCompile means the compilation should fail.
    // Inside those files we should have "//.*ERR" markers that we use to test the position of the errors is correct.
    //_MustFailTimeout means that when running the file it will have an infinite loop
    private static final String[] EXCLUDE_FILES_WITH_SUFFIX = {
            "NonX10Constructs_MustFailCompile.x10",
            //"_MustFailCompile.x10",
    };
    private static final String[] EXCLUDE_DIRS = {
            "WorkStealing", // Have duplicated class from the Samples directory such as ArraySumTest.x10
            "AutoGen", // it takes too long to compile these files
            "Manual",
    };
    private static final String[] EXCLUDE_FILES = {
            "NOT_WORKING","SSCA2","FT-alltoall","FT-global",
            "FieldNamedValTest_MustFailCompile.x10", "VariableNamedValTest_MustFailCompile.x10", // they have a lot of parsing errors
            "GenericLocal4_MustFailCompile.x10", // it causes the compiler to crash
            "TypedefNew11_MustFailCompile.x10", // it causes the compiler to crash
            "TypedefBasic2.x10", //C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Constructs\Typedefs\TypedefBasic2.x10:37,13-31
            "CUDA3DFD.x10", //C:\cygwin\home\Yoav\intellij\sourceforge\x10.dist\samples\CUDA\CUDA3DFD.x10:194,17-289,17
            "CUDAMatMul.x10",
    };
    private static final String[] EXCLUDE_FILES_WITH = {
            "HeatTransfer_v0.x10",
            "TypedefOverloading",
            "PlaceCheckArray.x10",
    };
    private static final String[] INCLUDE_ONLY_FILES_WITH = {
            //"_MustFailCompile.x10",
    };
    public static final int MAX_ERR_QUEUE = 10000;

    static {
        Arrays.sort(EXCLUDE_FILES);
        Arrays.sort(EXCLUDE_DIRS);
    }
    private static boolean shouldIgnoreDir(String name) {
        if (Arrays.binarySearch(EXCLUDE_DIRS,name)>=0) return true;
        return false;
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

        for (String s : args) {
            if (s.contains("STATIC_CALLS") || s.contains("VERBOSE_CALLS"))
                throw new RuntimeException("You should run the test suite without -VERBOSE_CALLS or -STATIC_CALLS");
        }


        final String dirName = args[0];
        ArrayList<File> files = new ArrayList<File>(10);
        if (dirName.endsWith(".x10")) {
            final File dir = new File(dirName);
            assert dir.isFile() : "File doesn't not exists: "+dirName;
            files.add(getCanonicalFile(dir));
        } else {
            for (String dirStr : dirName.split(",")) {
                File dir = new File(dirStr);
                assert dir.isDirectory() : "The first command line argument must be the directory of x10.tests, and you passed: "+dir;
                int before = files.size();
                recurse(dir,files);
                if (before==files.size()) System.out.println("Warning: Didn't find any .x10 files to compile in any subdirectory of "+dir);
            }
        }
        ArrayList<FileSummary> summaries = new ArrayList<FileSummary>();
        for (File f : files) {
            FileSummary fileSummary = analyzeFile(f);
            if (!fileSummary.shouldIgnoreFile) {
                summaries.add(fileSummary);
            }
        }

        if (ONE_FILE_AT_A_TIME) {
            for (FileSummary f : summaries) {
                compileFiles(Arrays.asList(f),remainingArgs);
            }
        } else {
            // We need to compile _MustFailCompile and files with ERR separately (because they behave differently when compiled with other files)
            ArrayList<FileSummary> shouldCompile = new ArrayList<FileSummary>();
            for (FileSummary f : summaries) {
                if (f.lines.size()>0 || f.file.getName().endsWith("_MustFailCompile.x10"))
                    compileFiles(Arrays.asList(f),remainingArgs);
                else
                    shouldCompile.add(f);
            }
            if (shouldCompile.size()>0)
                compileFiles(shouldCompile,remainingArgs);
        }
    }
    private static int count(String s, String sub) {
        final int len = sub.length();
        int index=-len, res=0;
        while ((index=s.indexOf(sub,index+ len))>=0) res++;
        return res;
    }
    public static ArrayList<ErrorInfo> runCompiler(String[] newArgs) {
        SilentErrorQueue errQueue = new SilentErrorQueue(MAX_ERR_QUEUE,"TestSuiteErrQueue");
        try {
            new polyglot.main.Main().start(newArgs,errQueue);
        } catch (Main.TerminationException e) {
            // If we had errors (and we should because we compile _MustFailCompile) then we will get a non-zero exitCode
        }
        final ArrayList<ErrorInfo> res = (ArrayList<ErrorInfo>) errQueue.getErrors();
        assert res.size()<MAX_ERR_QUEUE : "We passed the maximum number of errors!";
        return res;
    }
    static class LineSummary {
        int lineNo;
        int errCount;
        // todo: add todo, ShouldNotBeERR, ShouldBeErr statistics.
    }
    static class FileSummary {
        File file;
        boolean shouldIgnoreFile;
        boolean STATIC_CALLS = false;
        ArrayList<String> options = new ArrayList<String>();
        ArrayList<LineSummary> lines = new ArrayList<LineSummary>();
    }
    private static FileSummary analyzeFile(File file) throws IOException {
        FileSummary res = new FileSummary();
        res.file = file;
        final ArrayList<String> lines = AutoGenSentences.readFile(file);
        int lineNum = 0;
        for (String line : lines) {
            lineNum++;
            int errIndex = line.indexOf("ERR");
            boolean isERR = errIndex!=-1;
            if (line.contains("IGNORE_FILE")) res.shouldIgnoreFile = true;
            int optionsIndex = line.indexOf("OPTIONS:");
            if (optionsIndex>=0) {
                final String option = line.substring(optionsIndex + "OPTIONS:".length()).trim();
                res.options.add(option);
                if (option.equals("-STATIC_CALLS")) res.STATIC_CALLS = true;
            }
            int commentIndex = line.indexOf("//");
            if (isERR && commentIndex!=-1 && commentIndex<errIndex) { 
                LineSummary lineSummary = new LineSummary();
                lineSummary.lineNo = lineNum;
                lineSummary.errCount = count(line,"ERR");
                res.lines.add(lineSummary);
            }
        }
        return res;
    }
    private static void compileFiles(List<FileSummary> summaries, List<String> args) throws IOException {
        // replace \ with /
        ArrayList<String> fileNames = new ArrayList<String>(summaries.size());
        for (FileSummary f : summaries) {
            fileNames.add(f.file.getAbsolutePath().replace('\\','/'));
        }
        boolean STATIC_CALLS = summaries.size()>1 ? true : summaries.get(0).STATIC_CALLS; // all the files without ERR markers are done in my batch, using STATIC_CALLS (cause they shouldn't have any errors)
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
        String[] newArgs = allArgs.toArray(new String[allArgs.size()+1]);
        newArgs[newArgs.length-1] = STATIC_CALLS ? "-STATIC_CALLS" : "-VERBOSE_CALLS";
        System.out.println("Running: "+ fileNames);
        ArrayList<ErrorInfo> errors = runCompiler(newArgs);
        // remove GOOD_ERR_MARKERS  and EXPECTED_ERR_MARKERS
        for (Iterator<ErrorInfo> it = errors.iterator(); it.hasNext(); ) {
            ErrorInfo info = it.next();
            final int kind = info.getErrorKind();
            if (kind==ErrorInfo.GOOD_ERR_MARKERS || kind==ErrorInfo.EXPECTED_ERR_MARKERS)
                it.remove();
        }

        // Now checking the errors reported are correct and match ERR markers
        // 1. find all ERR markers that don't have a corresponding error
        for (FileSummary fileSummary : summaries) {
            File file = fileSummary.file;
            for (LineSummary lineSummary : fileSummary.lines) {
                // try to find the matching error
                int expectedErrCount = lineSummary.errCount;
                int lineNum = lineSummary.lineNo;
                int foundErrCount = 0;
                ArrayList<ErrorInfo> errorsFound = new ArrayList<ErrorInfo>(expectedErrCount);
                for (Iterator<ErrorInfo> it=errors.iterator(); it.hasNext(); ) {
                    ErrorInfo err = it.next();
                    final Position position = err.getPosition();
                    if (position!=null && new File(position.file()).equals(file) && position.line()==lineNum) {
                        // found it!
                        errorsFound.add(err);
                        if (Report.should_report("TestSuite", 2))
                            Report.report(2, "Found error: "+ err);
                        it.remove();
                        foundErrCount++;
                    }
                }
                if (expectedErrCount!=foundErrCount)
                    System.err.println("File "+file+" has "+expectedErrCount+" ERR markers on line "+lineNum+", but the compiler reported "+ foundErrCount+" errors on that line! errorsFound=\n"+errorsFound);
            }
        }

        // 2. report all the remaining errors that didn't have a matching ERR marker
        // first report warnings
        int warningCount = 0;
        for (ErrorInfo err : errors)
            if (err.getErrorKind()==ErrorInfo.WARNING) {
                if (!err.getMessage().startsWith(X10TypeMixin.MORE_SEPCIFIC_WARNING)) // ignore those warning messages
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
            final boolean isDir = f.isDirectory();
            if (!isDir && shouldIgnoreFile(name)) continue;
            if (isDir && shouldIgnoreDir(name)) continue;
            if (files.size()>=MAX_FILES_NUM) return;
            if (isDir)
                recurse(f, files);
            else {
                if (name.endsWith(".x10")) {
                    files.add(getCanonicalFile(f));
                }
            }
        }
    }
    private static File getCanonicalFile(File f) {
        try {
            return f.getCanonicalFile();
        } catch (java.io.IOException e) {
            return f;
        }
    }
}
