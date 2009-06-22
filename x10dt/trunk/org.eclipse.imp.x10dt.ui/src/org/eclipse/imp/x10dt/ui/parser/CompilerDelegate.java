package x10.uide.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import lpg.lpgjavaruntime.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.StdErrorQueue;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;
import x10.uide.X10UIPlugin;

import com.ibm.watson.safari.x10.X10Plugin;
import com.ibm.watson.safari.x10.builder.PolyglotFrontEnd;
import com.ibm.watson.safari.x10.preferences.X10Preferences;

public class CompilerDelegate {
    private ExtensionInfo extInfo;
    private PolyglotFrontEnd fe;

    private final IJavaProject x10Project;

    public X10Lexer getLexer() { return extInfo.getLexer(); }
    public X10Parser getParser() { return extInfo.getParser(); }
    public Job getJob(Source source) { return extInfo.getJob(source); }
    public PolyglotFrontEnd getFrontEnd() { return fe; }

    CompilerDelegate(Monitor monitor, IProject project) {
        this.x10Project= JavaCore.create(project);

        extInfo= new x10.uide.parser.ExtensionInfo(monitor); // new ExtensionInfo(monitor);
        buildOptions(extInfo);
        StdErrorQueue eq= new StdErrorQueue(System.err, 100000, "stderr");// TODO replace me
        fe = new PolyglotFrontEnd(extInfo, eq);
    }

    /**
     * @return a list of all project-relative CPE_SOURCE-type classpath entries.
     * @throws JavaModelException
     */
    private List/*<IPath>*/ getProjectSrcPath() throws JavaModelException {
        List/* <IPath> */srcPath= new ArrayList();
        IClasspathEntry[] classPath= x10Project.getResolvedClasspath(true);

        for(int i= 0; i < classPath.length; i++) {
            IClasspathEntry e= classPath[i];

            if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE)
                srcPath.add(e.getPath());
        }
        if (srcPath.size() == 0)
            srcPath.add(x10Project.getProject().getLocation());
        return srcPath;
    }

    private String pathListToPathString(List/*<IPath>*/ pathList) {
        StringBuffer buff= new StringBuffer();

        for(Iterator iter= pathList.iterator(); iter.hasNext(); ) {
            IPath path= (IPath) iter.next();

            buff.append(x10Project.getProject().getWorkspace().getRoot().getLocation().append(path).toOSString());
            if (iter.hasNext())
                buff.append(';');
        }
        return buff.toString();
    }

    private void buildOptions(ExtensionInfo extInfo) {
	Options opts= extInfo.getOptions();

	Options.global= opts;
	try {
            List/*<IPath>*/ projectSrcLoc= getProjectSrcPath();
            String projectSrcPath= pathListToPathString(projectSrcLoc);
	    opts.parseCommandLine(new String[] { "-cp", buildClassPathSpec(), "-sourcepath", projectSrcPath }, new HashSet());
	} catch (UsageError e) {
	    if (!e.getMessage().equals("must specify at least one source file"))
		System.err.println(e.getMessage());
	} catch (JavaModelException e) {
            X10UIPlugin.getInstance().writeErrorMsg("Unable to obtain resolved class path: " + e.getMessage());
	}
	X10UIPlugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
	X10UIPlugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
    }
/*
    private String buildClassPathSpec() throws JavaModelException {
	StringBuffer buff= new StringBuffer();

	IClasspathEntry[] classpath= x10Project.getResolvedClasspath(true);
	IPath projectPath= x10Project.getProject().getLocation();
        IPath wsPath= x10Project.getProject().getWorkspace().getRoot().getLocation();

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
    */
    
    private String buildClassPathSpec() {
        StringBuffer buff= new StringBuffer();

        try {
            IClasspathEntry[] classPath= x10Project.getResolvedClasspath(true);

            for(int i= 0; i < classPath.length; i++) {
                IClasspathEntry entry= classPath[i];

                if (i > 0)
                    buff.append(";");
                buff.append(entry.getPath().toOSString());
            }
            // if (X10Preferences.autoAddRuntime) {
            //    String commonPath= X10Plugin.x10CommonPath;
            //    String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separator) + 1) + "x10.runtime" + File.separator + "classes";
            //
            //    if (classPath.length > 0)
            //        buff.append(';');
            //    buff.append(runtimePath);
            // }
        } catch (JavaModelException e) {
            X10Plugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
        }
        return buff.toString();
    }
}