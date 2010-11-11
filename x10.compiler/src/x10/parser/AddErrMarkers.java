package x10.parser;

import polyglot.util.SilentErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.main.Main;

import java.util.ArrayList;
import java.io.File;

/**
 * Runs the compiler on a single source file (args[0])
 * and add "// ERR" markers on the lines with errors.
 * The errors can only appear in args[0].
 */
public class AddErrMarkers {
    public static void main(String[] args) {
        if (args.length==0) {
            System.err.println("You need to run AddErrMarkers like the x10 compiler, with at least one argument: INPUT_OUTPUT_FILE\nFor example: java AddErrMarkers Bla.x10\n");
            System.exit(-1);
        }
        File source = new File(args[0]);
        assert source.exists();

        SilentErrorQueue errQueue = new SilentErrorQueue(10000,"TestSuiteErrQueue");
        try {
            new polyglot.main.Main().start(args,errQueue);
        } catch (Main.TerminationException e) {
            if (e.exitCode==0) {
                System.err.println("No errors found.");
                return; // no errors
            }
        }
        ArrayList<ErrorInfo> errors = (ArrayList<ErrorInfo>)errQueue.getErrors();
        System.err.println("Adding "+errors.size()+" ERR markers to file "+source);

        ArrayList<String> lines = AutoGenSentences.readFile(source);
        // remove old ERR markers
        for (int i=0; i<lines.size(); i++) {
            final String line = lines.get(i);
            final int index = line.indexOf("// ERR");
            if (index!=-1) lines.set(i, line.substring(0,index));
        }

        for (ErrorInfo err : errors) {
            final Position position = err.getPosition();
            if (position==null || !new File(position.file()).equals(source)) {
                System.err.println("Found an error without a position or in a different file:\n"+position+"\nError: "+err);
            } else {
                int lineNo = position.line()-1; // line no starts with 1
                lines.set(lineNo,lines.get(lineNo)+" // ERR ("+err.toString().replace('\n','\t')+")");
            }
        }
        AutoGenSentences.writeFile(source,lines);
    }
}