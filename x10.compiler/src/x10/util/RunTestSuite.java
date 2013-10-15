package x10.util;

import polyglot.frontend.Compiler;
import polyglot.types.Types;
import polyglot.util.SilentErrorQueue;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import x10.util.CollectionFactory;
import polyglot.main.Main;
import polyglot.visit.InitChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.lang.*;
import java.lang.StringBuilder;

import x10.parser.AutoGenSentences;
import x10.X10CompilerOptions;
import x10.visit.CheckEscapingThis;

/**
 * This program is intended to be used to determine if on a given test
 * suite there is a difference between errors produced by one version
 * of the X10 compiler and another. This
 * program is intended to help in situations in which the first
 * version of the compiler does not completely pass the tests,
 * producing some errors or failing instead. The compiler writer can
 * add various <em>markers</em> into the source code for the tests
 * specifying whether an error is expected on this line or not,
 * whether the compiler should not parse a given line, whether the
 * compiler crashes on a given file etc. This program then checks the
 * errors it gets against these markers and emits an error only if the
 * error it receives is not accounted for by the markers.

 * <p> Here is how this program is typically used. A version of the
 * compiler is considered stable if <code>RunTestSuite</code> runs on
 * a given test suite without producing errors. Note: This does not
 * mean that the compiler successfully handles the given tests -- just
 * that the errors/crashes it produces have been accounted for by appropriate
 * markers in the source code for the tests.
 * Now make some changes to the compiler and run
 * <code>RunTestSuite</code> again. If it produces errors, then you
 * know that your code changes have caused new errors to arise. Fix
 * your code so that <code>RunTestSuite</code> reports no errors. Now
 * you know that your code is producing exactly the same errors as the
 * stable version of the compiler.

 *<p> Again, this does not mean that your new compiler has no errors,
 *just that it has no <em>new</em> errors. At some point you need to
 *go and fix the existing errors, and remove the corresponding markers
 *from the <code>*.x10</code> files.

 * <p> In more detail, this program reads a bunch of x10 files and
 * runs the front end on them, taps the <code>ErrorQueue</code>, 
 * extracts line number information from error messages, and
 * compares these errors with error markers in the file and verify
 * that all errors are expected.
 *
 * <p> <code>RunTestSuite</code> accepts all the flags that the X10
 * compiler accepts (and uses them in the same way) except that
 * instead of accepting a list of <code>*.x10</code> files, it
 * requires a directory (or comma-separated list of directories) to be
 * provided as the first argument.  From these directories it collects
 * all <code>*.x10</code> files.
 *
 * Property flags are passed with:
 * java -DFLAG=SOMETHING
 *
 * <p>The property flag <code>SEPARATE_COMPILER</code> can be set
 * to <code>false<code> to force <code>RunTestSuite</code> to use the same
 * compiler object for multiple tests. In general this runs the tests
 * much faster, but is still a bit brittle (may yield errors when
 * compiling each test with a separate compiler object would not).
 *
 * <p> The property flag <code>QUIET</code> can be set to
 * <code>true</code> to run in quiet mode. In this mode various
 * warnings and helpful messages are not printed out.
 *
 * <p> The property flag <code>SHOW_EXPECTED_ERRORS</code> prints all
 * the errors (even the expected errors).
 * This is useful if we want to diff the output of the compiler to make
 * sure the error messages are exactly the same.
 *
 * <p> The property flag <code>SHOW_RUNTIMES</code> prints runtime for the entire suite and for each file.
 *
 * 
 * <p> Five kinds of error markers can be inserted in <code>*.x10</code> files:
 * <ul>
 *  <li><code>ERR</code>  - marks an error or warning
 * <li><code>ShouldNotBeERR<code> - the compiler reports an error, but
 * it shouldn't
 * <li><code>ShouldBeErr</code> - the compiler doesn't report an
 * error, but it should
 * <li><code>COMPILER_CRASHES</code> - the compiler currently crashes
 * on this file
 * <li><code>SHOULD_NOT_PARSE</code> - the compiler should report
 * parsing and lexing errors on this file
 * </ul> 
 * <p> Multiple markers (even of the same kind) may appear on the
 * same line. Thus if a line is marked with <code>ShouldNotBeErr
 * ShouldNotBeErr</code>, the test runner complains if the compiler
 * does not produce two errors on this line.

 * <p>The first 3 markers (<code>ERR, ShouldNotBeERR, ShouldBeErr</code>)
 * can come in the form of annotations (<code>@ERR</code>) or in the
 * form of comments (<code>// ERR</code>) The last two
 * (<code>COMPILER_CRASHES,SHOULD_NOT_PARSE</code>) must be a comment.
 *
 * <p> Annotations are replaced with errors by a compiler phase called
 * <code>ErrChecker</code> that happens immediately after parsing.

 * <p> The problem with annotations currently
 * are: 
 * <ol>
   <li>You can't put annotations on statement expressions, i.e.,
 * <code> @ERR i=3;</code>
 * doesn't parse
 * However, you can write:
 * <code>@ERR {i=3;}</code>
 *
 *
 * By default we run the compiler with VERBOSE_CHECKS.
 * If the file contains the line:
//OPTIONS: -STATIC_CHECKS
 * then we run it with STATIC_CHECKS.

 * Some directories are permanently excluded from the test suite
 * (see EXCLUDE_DIRS and EXCLUDE_FILES fields)
 * For example,  "AutoGen" directory contains really big files that takes a long time to compile,
 * or LangSpec contains some problematic files I can't fix because they are auto-generated.
</ul>
 */
public class RunTestSuite {
    public static String getProp(String name, String defVal) {
        final String val = System.getProperty(name); // I prefer System.getProperty over getenv because you see the properties on the command line for the java (it's not something you set on the outside)
        System.out.println("getProperty("+name+")="+val);
        return val==null ? defVal : val;
    }
    public static boolean getBoolProp(String name) {
        final String val = getProp(name,null);
        return val!=null && (val.equalsIgnoreCase("t") || val.equalsIgnoreCase("true"));
    }
    public static boolean SEPARATE_COMPILER = getBoolProp("SEPARATE_COMPILER");
    public static boolean SHOW_EXPECTED_ERRORS = getBoolProp("SHOW_EXPECTED_ERRORS");
    public static boolean SHOW_RUNTIMES = getBoolProp("SHOW_RUNTIMES");
    public static boolean SKIP_CRASHES = getBoolProp("SKIP_CRASHES");
    public static boolean QUIET = !SHOW_EXPECTED_ERRORS && getBoolProp("QUIET");

    public static String SOURCE_PATH_SEP = File.pathSeparator; // on MAC the separator is ":" and on windows it is ";"
    public static String LANGSPEC = "LangSpec"; // in LangSpec directory we ignore multiple errors on the same line (so one ERR marker matches any number of errors)

    private static void println(String s) {
        if (!QUIET) {
            System.out.println(s);
            System.out.flush();
        }
    }
    private static int EXIT_CODE = 0;
    private static java.lang.StringBuilder ALL_ERRORS = new StringBuilder();
    private static void err(String s) {
        EXIT_CODE = 1;
        System.err.println(s);
        System.err.flush();
        ALL_ERRORS.append(s).append("\n");
    }

    //_MustFailCompile means the compilation should fail.
    //_MustFailTimeout means that when running the file it will have an infinite loop
    private static final String[] EXCLUDE_FILES_WITH_SUFFIX = {
            //"_MustFailCompile.x10",
    };
    private static final String[] EXCLUDE_DIRS = {
            //"LangSpec", // Bard has too many errors...
            "WorkStealing", // it has copies of existing tests
            "AutoGen", // it takes too long to compile these files
            "NOT_WORKING", // to exclude some benchmarks: https://svn.code.sourceforge.net/p/x10/code/benchmarks/trunk
    };
    private static final String[] EXCLUDE_FILES = {
            // difference on MAC and PC (on PC the compiler crashes on AssertionError, on MAC it outputs this error: Semantic Error: Type definition type static TypedefOverloading06_MustFailCompile.A = x10.lang.String has the same name as member class TypedefOverloading06_MustFailCompile.A.
            //"TypedefOverloading04_MustFailCompile.x10",
            //"TypedefOverloading06_MustFailCompile.x10",
            //"TypedefOverloading08_MustFailCompile.x10",
            //"TypedefOverloading10_MustFailCompile.x10",
            //"TypedefNew11_MustFailCompile.x10",
            "TypedefBasic2.x10",

            // LangSpec is auto-generated, so I can't fix those files to make a clean test suite
            //"Classes250.x10","Classes160.x10","Classes170.x10",
            //"Interfaces3l4a.x10", "Interfaces_static_val.x10", "Types6a9m.x10",
            //"Stimulus.x10",
            "ClassCtor30_MustFailCompile.x10",
            "ThisEscapingViaAt_MustFailCompile.x10",
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
    private static final int MAX_FILES_NUM = Integer.MAX_VALUE; // Change it if you want to process only a small number of files

    public static void Assert(boolean val, String msg) {
        if (!val) throw new RuntimeException(msg);
    }
    public static void checkAssertionsEnabled() {
        boolean isEA = true;
        try {
            assert false : "Test if assertion work";
            isEA = false;
        } catch (Throwable e) {}
        if (!isEA) throw new RuntimeException("You must run RunTestSuite with assertions enabled, i.e.,  java -ea RunTestSuite ...");                                                                                                                
    }
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
        checkAssertionsEnabled();
        Assert(args.length>0, "The first command line argument must be an x10 filename or a comma separated list of the directories.\n"+
                    "E.g.,\n"+
                    "C:\\cygwin\\home\\Yoav\\intellij\\sourceforge\\x10.tests,C:\\cygwin\\home\\Yoav\\intellij\\sourceforge\\x10.dist\\samples,C:\\cygwin\\home\\Yoav\\intellij\\sourceforge\\x10.runtime\\src-x10");

        List<String> remainingArgs = new ArrayList<String>(Arrays.asList(args));
        remainingArgs.remove(0);

        for (String s : args) {
            if (s.contains("STATIC_CHECKS") || s.contains("VERBOSE_CHECKS"))
                throw new RuntimeException("You should run the test suite without -VERBOSE_CHECKS or -STATIC_CHECKS");
        }
        if (SEPARATE_COMPILER)
            println("Running each file with a separate (new) compiler object, so it's less efficient but more stable.");


        ArrayList<File> files = new ArrayList<File>(10);
        for (String dirStr : args[0].split(",")) {
            if (dirStr.endsWith(".x10")) {
                final File dir = new File(dirStr);
                Assert(dir.isFile(), "File does not exist: "+dirStr);
                files.add(getCanonicalFile(dir));
            } else {
                File dir = new File(dirStr);
                Assert(dir.isDirectory(), "The first command line argument must be a directory or x10 file, and you passed: "+dir);
                int before = files.size();
                recurse(dir,files);
                if (before==files.size()) println("Warning: Didn't find any .x10 files to compile in any subdirectory of "+dir);
            }
        }
        ArrayList<FileSummary> summaries = new ArrayList<FileSummary>();
        java.util.Map<String,File> fileName2File = CollectionFactory.newHashMap();
        for (File f : files) {
            FileSummary fileSummary = analyzeFile(f);
            summaries.add(fileSummary);

            String name = f.getName();
            if (fileName2File.containsKey(name))
                println("Warning: Found two files with the same name in different directories. This maybe confusing and might cause problems in the classpath.\n\tfile1="+fileName2File.get(name)+"\n\tfile2="+f);
            else
                fileName2File.put(name,f);
        }


        // adding the directories of the files to -sourcepath (in case they refer to other files that are not compiled, e.g., if we decide to compile the files one by one)
        // I'm also adding parent folders to support packages (see T3.x10)
        LinkedHashSet<String> directories = new LinkedHashSet<String>();
        for (FileSummary f : summaries) {
            int index = -1;
            while ((index = f.fileName.indexOf('/',index+1))!=-1) {
                directories.add(f.fileName.substring(0, index));
            }
        }
        String dirs = "";
        for (String dir : directories)
            dirs += SOURCE_PATH_SEP+dir;
        remainingArgs.add("-sourcepath");
        remainingArgs.add(dirs);
        remainingArgs.add("-CHECK_ERR_MARKERS"); // make sure we check @ERR annotations
        remainingArgs.add("-CHECK_INVARIANTS");
        println("sourcepath is: "+dirs);
        println("Arguments are:"+remainingArgs);

        long start = System.currentTimeMillis();
        for (FileSummary f : summaries) {
            compileFile(f,remainingArgs);
        }
        // report remaining errors that were not matched in any file
        printRemaining(null);
        if (SHOW_RUNTIMES) println("Total running time to compile all files="+(System.currentTimeMillis()-start)+" CheckEscapingThis="+ CheckEscapingThis.TIME+" InitChecker="+ InitChecker.TIME);
        
        if (EXIT_CODE!=0) System.out.println("Summary of all errors:\n\n"+ALL_ERRORS);
        System.out.println("\n\n\n\n\n"+ (EXIT_CODE==0 ? "SUCCESS" : "FAILED") + "\n\n\n");
        System.exit(EXIT_CODE);
    }
    private static void printRemaining(File file) {
        for (Iterator<ErrorInfo> it = remainingErrors.iterator(); it.hasNext();) {
            ErrorInfo err = it.next();
            final Position pos = err.getPosition();
            if (file==null || pos==null || isInFile(pos,file)) {
                it.remove();
                err((err.getErrorKind()==ErrorInfo.WARNING ? "Warning":"ERROR")+" in position:\n"+ pos +"\nMessage: "+err+"\n");
            }
        }

    }
    private static int count(String s, String sub) {
        final int len = sub.length();
        int index=-len, res=0;
        while ((index=s.indexOf(sub,index+len))>=0) res++;
        return res;
    }
    public static ArrayList<ErrorInfo> runCompiler(String[] newArgs) {
        return runCompiler(newArgs,false,false);
    }

    private static SilentErrorQueue errQueue = new SilentErrorQueue(MAX_ERR_QUEUE,"TestSuiteErrQueue");
    private static Main MAIN = new Main();
    private static Compiler COMPILER;
    public static ArrayList<ErrorInfo> runCompiler(String[] newArgs, boolean COMPILER_CRASHES, boolean STATIC_CHECKS) {
        errQueue.getErrors().clear();
        LinkedHashSet<String> sources = new LinkedHashSet<String>();
        final Compiler comp = MAIN.getCompiler(newArgs, null, errQueue, sources);
        if (SEPARATE_COMPILER || COMPILER==null)
            COMPILER = comp;
        X10CompilerOptions opts = (X10CompilerOptions) COMPILER.sourceExtension().getOptions();
        opts.x10_config.STATIC_CHECKS = STATIC_CHECKS;
        opts.x10_config.VERBOSE_CHECKS = !STATIC_CHECKS;
        opts.x10_config.VERBOSE = true;
        long start = System.currentTimeMillis();
        Throwable err = null;
        try {
            if (COMPILER_CRASHES && SKIP_CRASHES) {
                err = new RuntimeException("We do not want to compile crashes when SKIP_CRASHES=true");
            } else
                COMPILER.compileFiles(sources);
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

        if (SHOW_RUNTIMES) println("Compiler running time="+(System.currentTimeMillis()-start));
        final ArrayList<ErrorInfo> res = (ArrayList<ErrorInfo>) errQueue.getErrors();
        Assert(res.size()<MAX_ERR_QUEUE, "We passed the maximum number of errors!");
        return res;
    }

    static class LineSummary {
        int lineNo;
        int errCount;
        // todo: add todo, ShouldNotBeERR, ShouldBeErr statistics.
    }
    static class FileSummary {
        final File file;
        final String fileName;
        FileSummary(File f) {
            file = f;
            fileName = f.getAbsolutePath().replace('\\','/');
        }
        boolean STATIC_CHECKS = false;
        boolean COMPILER_CRASHES;
        boolean SHOULD_NOT_PARSE;
        ArrayList<String> options = new ArrayList<String>();
        ArrayList<LineSummary> lines = new ArrayList<LineSummary>();
    }
    private static FileSummary analyzeFile(File file) throws IOException {
        FileSummary res = new FileSummary(file);
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
                if (option.equals("-STATIC_CHECKS"))
                    res.STATIC_CHECKS = true;
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
    private static void compileFile(FileSummary summary, List<String> args) throws IOException {
        File file = summary.file;

        boolean STATIC_CHECKS = summary.STATIC_CHECKS; // all the files without ERR markers are done in my batch, using STATIC_CHECKS (cause they shouldn't have any errors)

        // Now running polyglot
        List<String> allArgs = new ArrayList<String>();
        allArgs.add(summary.fileName);
        allArgs.addAll(args);
        String[] newArgs = allArgs.toArray(new String[allArgs.size()+2]);
        newArgs[newArgs.length-2] = STATIC_CHECKS ? "-STATIC_CHECKS" : "-VERBOSE_CHECKS";
        newArgs[newArgs.length-1] = STATIC_CHECKS ? "-VERBOSE_CHECKS=false" : "-STATIC_CHECKS=false";
        println("Running: "+ summary.fileName);
        ArrayList<ErrorInfo> errors = runCompiler(newArgs, summary.COMPILER_CRASHES, STATIC_CHECKS);
        // remove SHOULD_BE_ERR_MARKER and
        // parsing errors (if SHOULD_NOT_PARSE)
        // treating @ERR and @ShouldNotBeERR as if it were a comment (adding a LineSummary)
        boolean didFailCompile = false;
        for (Iterator<ErrorInfo> it = errors.iterator(); it.hasNext(); ) {
            ErrorInfo info = it.next();
            final int kind = info.getErrorKind();
            if (ErrorInfo.isErrorKind(kind))
                didFailCompile = true;

            if ((kind==ErrorInfo.SHOULD_BE_ERR_MARKER) ||
                (summary.SHOULD_NOT_PARSE && (kind==ErrorInfo.LEXICAL_ERROR || kind==ErrorInfo.SYNTAX_ERROR)))
                it.remove();

            final Position position = info.getPosition();
            if (kind==ErrorInfo.ERR_MARKER || kind==ErrorInfo.SHOULD_NOT_BE_ERR_MARKER) {
                it.remove();
                int lineNo = position.line();
                LineSummary foundLine = null;
                for (LineSummary lineSummary : summary.lines)
                    if (lineNo==lineSummary.lineNo) {
                        foundLine = lineSummary;
                        break;
                    }
                if (foundLine==null) {
                    foundLine = new LineSummary();
                    foundLine.lineNo = lineNo;
                    summary.lines.add(foundLine);
                }
                foundLine.errCount++;
            }
        }

        if (didFailCompile!=summary.fileName.endsWith("_MustFailCompile.x10") && !summary.fileName.contains("TorontoSuite")) {
            println("WARNING: "+ summary.fileName+" "+(didFailCompile ? "FAILED":"SUCCESSFULLY")+" compiled, therefore it should "+(didFailCompile?"":"NOT ")+"end with _MustFailCompile.x10. "+(summary.lines.isEmpty()?"":"It did have @ERR markers but they might match warnings."));
        }
        errors.addAll(remainingErrors);
        remainingErrors.clear();

        // Now checking the errors reported are correct and match ERR markers
        // 1. find all ERR markers that don't have a corresponding error
            for (LineSummary lineSummary : summary.lines) {
                // try to find the matching error
                int expectedErrCount = lineSummary.errCount;
                int lineNum = lineSummary.lineNo;
                int foundErrCount = 0;
                ArrayList<ErrorInfo> errorsFound = new ArrayList<ErrorInfo>(expectedErrCount);
                for (Iterator<ErrorInfo> it=errors.iterator(); it.hasNext(); ) {
                    ErrorInfo err = it.next();
                    final Position position = err.getPosition();
                    if (position!=null && isInFile(position,file) && position.line()==lineNum) {
                        // found it!
                        errorsFound.add(err);
                        it.remove();
                        if (SHOW_EXPECTED_ERRORS)
                            println("Found error in position="+position+" err: "+ err);
                        foundErrCount++;
                    }
                }
                if (expectedErrCount!=foundErrCount &&
                        // we try to have at most 1 error in a line when writing the tests, but sometimes an error cascades into multiple ones
                        (expectedErrCount<2 || foundErrCount<2)) { // if the compiler reports more than 2 errors, and we marked more than 2, then it's too many errors on one line and it marks the fact the compiler went crazy and issues too many wrong errors.
                    if (foundErrCount>1 && expectedErrCount==1 && summary.fileName.contains(LANGSPEC)) {
                        // nothing to do - a single ERR marker in LangSpec match multiple errors
                    } else
                    err("File "+file+" has "+expectedErrCount+" ERR markers on line "+lineNum+", but the compiler reported "+ foundErrCount+" errors on that line! errorsFound=\n"+errorsFound);
                }
            }

        // 2. report all the remaining errors that didn't have a matching ERR marker
        remainingErrors.addAll(errors);
        printRemaining(file);
        // todo: check that for each file (without errors) we generated a *.class file, and load them and run their main method (except for the ones with _MustFailTimeout)
    }
    static boolean isInFile(Position position, File file) {
        return new File(position.file()).equals(file);
    }
    static ArrayList<ErrorInfo> remainingErrors = new ArrayList<ErrorInfo>(); // sometimes when compiling one file it uses another that has an ERR marker, so we keep those errors so we will match them to ERR markers in the other file (e.g., Activity.x10 uses HashMap.x10 that has an ERR marker for a warning)
    private static void recurse(File dir, ArrayList<File> files) {
        if (files.size()>=MAX_FILES_NUM) return;
        // sort the result, so the output is identical for diff purposes (see SHOW_EXPECTED_ERRORS)
        final File[] filesInDir = dir.listFiles();
        Arrays.sort(filesInDir, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());  // comparing o1.compareTo(o2) directly is different on MAC and PC (on MAC: Array1DCodeGen.x10 < Array1b.x10, and on PC it's reverse)
            }
        });
        for (File f : filesInDir) {
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
