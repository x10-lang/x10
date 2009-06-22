package x10.uide.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uide.parser.IParser;
import com.ibm.watson.safari.x10.X10Plugin;
import com.ibm.watson.safari.x10.preferences.X10Preferences;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.main.Options;
import polyglot.main.UsageError;
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

	buildOptions(extInfo);
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

    private void buildOptions(ExtensionInfo extInfo) {
	Options opts= extInfo.getOptions();

	Options.global= opts;
	try {
	    opts.parseCommandLine(new String[] { "-cp", buildClassPathSpec(), }, new HashSet());
	} catch (UsageError e) {
	    if (!e.getMessage().equals("must specify at least one source file"))
		System.err.println(e.getMessage());
	}
	X10Plugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
	X10Plugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
    }

    private String buildClassPathSpec() {
	StringBuffer buff= new StringBuffer();

	// HACK RMF 2/3/2005 - Really, should copy the classpath of the source file's
	// containing project, but we don't even know what bloody file we're parsing.
	// This will become critical when we start providing services that require
	// name-resolved AST's.
	String commonPath= X10Preferences.x10CommonPath;
	String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separator) + 1) + "x10.runtime" + File.separator + "classes";

	buff.append(runtimePath);
	return buff.toString();
    }
}