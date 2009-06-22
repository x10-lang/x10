package x10.uide.parser;

import java.io.File;
import java.io.IOException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.uide.parser.IParser;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.util.StdErrorQueue;
import x10.parser.X10Parser;
import lpg.lpgjavaruntime.LexStream;
import lpg.lpgjavaruntime.Monitor;
import lpg.lpgjavaruntime.PrsStream;

public class ParserDelegate implements IParser {
    X10Parser myParser;

    public PrsStream getParseStream() {
	return myParser.getParseStream();
    }

    public int getEOFTokenKind() {
	return myParser.getEOFTokenKind();
    }

    public Object parser(Monitor monitor, int error_repair_count) {
	return myParser.parser(monitor);
    }

    String[] orderedTerminalSymbols() {
	return myParser.orderedTerminalSymbols();
    }

    ParserDelegate(LexStream lexStream) {
	ExtensionInfo extInfo= new ExtensionInfo();
	StdErrorQueue eq= new StdErrorQueue(System.err, 100000, "stderr");// TODO replace me
	Compiler compiler= new Compiler(extInfo, eq);
	extInfo.initCompiler(compiler);
	try {
	    // TODO Need to know a/the real file name (must exist or File() ctor will barf)
	    // The lexStream should know but doesn't, because noone's telling it...
//	    String fileName= lexStream.getFileName();
	    String fileName= Platform.getOS().equals("win32") ? "c:\\" : "/tmp";
	    FileSource fileSource= new FileSource(new File(fileName));

	    myParser= new X10Parser(lexStream, extInfo.typeSystem(), extInfo.nodeFactory(), fileSource, extInfo.compiler().errorQueue());
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new IllegalArgumentException(e.getMessage());
	}
    }
}