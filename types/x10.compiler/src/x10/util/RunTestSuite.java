package x10.util;

import polyglot.frontend.Globals;
import polyglot.types.Types;
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

import x10.parser.AutoGenSentences;

/**
 * This program reads a bunch of x10 files and runs the front end on them,
 * then it extracts information (line numbers) from error messages, and we compare these errors with error markers in the file
 * and verify that all errors are expected.
 *
 * You run RunTestSuite exactly like you would run the compiler (with the same flags)
 * except that instead a list of *.x10 files,
 * the first argument for RunTestSuite is a directory (or comma-separated list of directories) from which we collect all *.x10 files.
 *
 * We have 5 kinds of error markers:
 * ERR  - marks an error or warning
 * ShouldNotBeERR - the compiler reports an error, but it shouldn't
 * ShouldBeErr - the compiler doesn't report an error, but it should
 * COMPILER_CRASHES - the compiler currently crashes on this file
 * SHOULD_NOT_PARSE - the compiler should report parsing and lexing errors on this file
 *
 * The first 3 markers (ERR, ShouldNotBeERR, ShouldBeErr) can come in the form of annotations (@ERR)
 * or in the form of comments (// ERR)
 * The last two (COMPILER_CRASHES,SHOULD_NOT_PARSE) must be a comment.
 *
 * Annotations are checked by a compiler phase called ErrChecker.
 * The problem with annotations currently are:
 * 1) You can't put annotations on statement expressions, i.e.,
 * @ERR i=3;
 * doesn't parse
 * However, you can write:
 * @ERR {i=3;}
 * 2) ErrChecker goal is not reached sometimes (if a previous goal failed).
 *
 * In the future I plan to use only annotations and run ErrChecker here instead of inside the compiler.
 *
 * There is also an OPTIONS marker:
 * OPTIONS: -STATIC_CALLS
 * If no OPTIONS is specified, we run with VERBOSE_CALLS.
 */
public class RunTestSuite {
    public static boolean QUIET = System.getenv("QUIET")!=null;
    private static void println(String s) {
        if (!QUIET) System.out.println(s);
    }
    private static int EXIT_CODE = 0;                      
    private static void err(String s) {
        EXIT_CODE = 1;
        System.err.println(s);
    }

    //_MustFailCompile means the compilation should fail.
    //_MustFailTimeout means that when running the file it will have an infinite loop
    private static final String[] EXCLUDE_FILES_WITH_SUFFIX = {
            //"_MustFailCompile.x10",
    };
    private static final String[] EXCLUDE_DIRS = {
            "AutoGen", // it takes too long to compile these files
    };
    private static final String[] EXCLUDE_FILES = {
            "NOT_WORKING", // to exclude some benchmarks: https://x10.svn.sourceforge.net/svnroot/x10/benchmarks/trunk
    };
    private static final String[] EXCLUDE_FILES_WITH = {
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
    public static boolean ONE_FILE_AT_A_TIME = true; // RecursiveConstraint.x10 gives an error when compiled with other files, but not when compiled alone. So I have to compile all fils one-by-one...
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
                if (before==files.size()) println("Warning: Didn't find any .x10 files to compile in any subdirectory of "+dir);
            }
        }
        ArrayList<FileSummary> summaries = new ArrayList<FileSummary>();
        for (File f : files) {
            FileSummary fileSummary = analyzeFile(f);
            summaries.add(fileSummary);
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
        System.exit(EXIT_CODE);
    }
    private static int count(String s, String sub) {
        final int len = sub.length();
        int index=-len, res=0;
        while ((index=s.indexOf(sub,index+ len))>=0) res++;
        return res;
    }
    public static ArrayList<ErrorInfo> runCompiler(String[] newArgs) {
        return runCompiler(newArgs,false);
    }
    public static ArrayList<ErrorInfo> runCompiler(String[] newArgs, boolean COMPILER_CRASHES) {
        SilentErrorQueue errQueue = new SilentErrorQueue(MAX_ERR_QUEUE,"TestSuiteErrQueue");
        long start = System.currentTimeMillis();
        Throwable err = null;
        try {
            new polyglot.main.Main().start(newArgs,errQueue);
        } catch (Main.TerminationException e) {
            // If we had errors (and we should because we compile _MustFailCompile) then we will get a non-zero exitCode
        } catch (Throwable e) {
            err = e;
        }
        if (COMPILER_CRASHES) {
            if (err==null) err("We expected the compiler to crash, but it didn't :) Remove the 'COMPILER_CRASHES' marker from file "+newArgs[0]);
        } else {
            if (err!=null) {
                err("Compiler crashed for args="+Arrays.toString(newArgs)+" with exception:");
                err.printStackTrace();
            }
        }

        println("Compiler running time="+(System.currentTimeMillis()-start));
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
        boolean STATIC_CALLS = false;
        boolean COMPILER_CRASHES;
        boolean SHOULD_NOT_PARSE;
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
            if (line.contains("COMPILER_CRASHES")) res.COMPILER_CRASHES = true;
            if (line.contains("SHOULD_NOT_PARSE")) res.SHOULD_NOT_PARSE = true;
            int optionsIndex = line.indexOf("OPTIONS:");
            if (optionsIndex>=0) {
                final String option = line.substring(optionsIndex + "OPTIONS:".length()).trim();
                res.options.add(option);
                if (option.equals("-STATIC_CALLS")) res.STATIC_CALLS = true;
            }
            line = line.trim();
            int commentIndex = line.indexOf("//");
            if (commentIndex>0) { // if the line contains just a comment, then we ignore it.
                int errIndex = line.indexOf("ERR");
                boolean isERR = errIndex!=-1;
                if (isERR && commentIndex<errIndex) {
                    LineSummary lineSummary = new LineSummary();
                    lineSummary.lineNo = lineNum;
                    lineSummary.errCount = count(line,"ERR");
                    res.lines.add(lineSummary);
                }
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
        // I'm also adding parent folders to support packages (see T3.x10)
        HashSet<String> directories = new HashSet<String>();
        for (String f : fileNames) {
            int index = -1;
            while ((index = f.indexOf('/',index+1))!=-1) {
                directories.add(f.substring(0, index));
            }

        }
        String dirs = "";
        for (String dir : directories)
            dirs += ";"+dir;
        int argsNum = args.size();
        boolean foundSourcePath = false;
        for (int i=1; i<argsNum; i++) {
            final String arg = args.get(i);
            if (arg.contains("/x10.runtime/src-x10")) {
                args.set(i,arg+dirs);
                foundSourcePath = true;
                break;
            }
        }
        assert foundSourcePath : "You must use an argument -sourcepath that includes '/x10.runtime/src-x10'";
        // Now running polyglot
        List<String> allArgs = new ArrayList<String>(fileNames);
        allArgs.addAll(args);
        String[] newArgs = allArgs.toArray(new String[allArgs.size()+2]);
        newArgs[newArgs.length-2] = STATIC_CALLS ? "-STATIC_CALLS" : "-VERBOSE_CALLS";
        newArgs[newArgs.length-1] = !STATIC_CALLS ? "-STATIC_CALLS=false" : "-VERBOSE_CALLS=false";
        println("Running: "+ fileNames);
        final FileSummary firstSummary = summaries.get(0);
        ArrayList<ErrorInfo> errors = runCompiler(newArgs, firstSummary.COMPILER_CRASHES);
        // remove GOOD_ERR_MARKERS  and EXPECTED_ERR_MARKERS
        for (Iterator<ErrorInfo> it = errors.iterator(); it.hasNext(); ) {
            ErrorInfo info = it.next();
            final int kind = info.getErrorKind();
            if ((kind==ErrorInfo.GOOD_ERR_MARKERS || kind==ErrorInfo.EXPECTED_ERR_MARKERS) ||
                (firstSummary.SHOULD_NOT_PARSE && (kind==ErrorInfo.LEXICAL_ERROR || kind==ErrorInfo.SYNTAX_ERROR)))
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
                if (expectedErrCount!=foundErrCount) {
                    err("File "+file+" has "+expectedErrCount+" ERR markers on line "+lineNum+", but the compiler reported "+ foundErrCount+" errors on that line! errorsFound=\n"+errorsFound);
                }
            }
        }

        // 2. report all the remaining errors that didn't have a matching ERR marker
        // first report warnings
        int warningCount = 0;
        for (ErrorInfo err : errors)
            if (err.getErrorKind()==ErrorInfo.WARNING) {
                if (!err.getMessage().startsWith(Types.MORE_SPECIFIC_WARNING)) { // ignore those warning messages
                    err("Got a warning in position: "+err.getPosition()+"\nMessage: "+err+"\n");
                }
                warningCount++;
            }
        if (errors.size()>warningCount) {
            err("\nThe following errors did not have a matching ERR marker:\n\n");
            for (ErrorInfo err : errors)
                if (err.getErrorKind()!=ErrorInfo.WARNING)
                    err("Position:\n"+err.getPosition()+"\nMessage: "+err+"\n");
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
