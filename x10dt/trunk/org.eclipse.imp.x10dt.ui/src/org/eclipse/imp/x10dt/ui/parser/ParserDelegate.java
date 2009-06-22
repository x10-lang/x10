package x10.uide.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import lpg.lpgjavaruntime.LexStream;
import lpg.lpgjavaruntime.Monitor;
import lpg.lpgjavaruntime.PrsStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uide.parser.IParser;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.StdErrorQueue;
import x10.parser.X10Parser;
import x10.uide.X10UIPlugin;

public class ParserDelegate implements IParser {
    X10Parser myParser;
    private final IJavaProject javaProject;

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

    ParserDelegate(LexStream lexStream, IProject project) {
	this.javaProject= JavaCore.create(project);

        ExtensionInfo extInfo= new ExtensionInfo();
	StdErrorQueue eq= new StdErrorQueue(System.err, 100000, "stderr");// TODO replace me
	Compiler compiler= new Compiler(extInfo, eq);

	buildOptions(extInfo);
	extInfo.initCompiler(compiler);
	try {
//	    String fileName= project.getLocation().append(lexStream.getFileName()).toString();
	    FileSource fileSource= new FileSource(new File(lexStream.getFileName()));

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
	} catch (JavaModelException e) {
            X10UIPlugin.getInstance().writeErrorMsg("Unable to obtain resolved class path: " + e.getMessage());
	}
	X10UIPlugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
	X10UIPlugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
    }

    private String buildClassPathSpec() throws JavaModelException {
	StringBuffer buff= new StringBuffer();

	IClasspathEntry[] classpath= javaProject.getResolvedClasspath(true);
	IPath projectPath= javaProject.getProject().getLocation();
        IPath wsPath= javaProject.getProject().getWorkspace().getRoot().getLocation();

        for(int i= 0; i < classpath.length; i++) {
            IClasspathEntry entry= classpath[i];
            String entryPath;

            if (buff.length() > 0) buff.append(";");
            switch(entry.getEntryKind()) {
                case IClasspathEntry.CPE_CONTAINER: {
                    entryPath= projectPath.append(entry.getPath()).toString();
                    break;
                }
                case IClasspathEntry.CPE_LIBRARY:
                    if (entry.getPath().isAbsolute())
                        entryPath= entry.getPath().toString();
                    else
                        entryPath= projectPath.append(entry.getPath()).toString();
                    break;
                case IClasspathEntry.CPE_PROJECT:
                    entryPath= wsPath.append(entry.getPath()).toString();
                    break;
                case IClasspathEntry.CPE_SOURCE:
                    entryPath= wsPath.append(entry.getPath()).toString();
                    break;
                default: // IClasspathEntry.CPE_VARIABLE:
                    entryPath= "";
                    break;
            }
            buff.append(entryPath);
        }
	return buff.toString();
    }
}