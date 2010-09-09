package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import lpg.runtime.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.core.builder.PolyglotFrontEnd;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.main.UsageError;
import polyglot.util.ErrorQueue;
import polyglot.util.SilentErrorQueue;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

public class CompilerDelegate {
    private ExtensionInfo extInfo;
    private PolyglotFrontEnd fe;

    private final IJavaProject x10Project;

    public X10Lexer getLexer() { return extInfo.getLexer(); }
    public X10Parser getParser() { return extInfo.getParser(); }
    public Job getJob(Source source) { return extInfo.getJob(source); }
    public PolyglotFrontEnd getFrontEnd() { return fe; }

    CompilerDelegate(Monitor monitor, IProject project) {
	this.x10Project= (project != null) ? JavaCore.create(project) : null;

        extInfo= new org.eclipse.imp.x10dt.ui.parser.ExtensionInfo(monitor); // new ExtensionInfo(monitor);
        buildOptions(extInfo);
        ErrorQueue eq= new SilentErrorQueue(100000, "stderr");
        fe = new PolyglotFrontEnd(extInfo, eq);
        Report.setQueue(eq);
    }

    /**
     * @return a list of all project-relative CPE_SOURCE-type classpath entries.
     * @throws JavaModelException
     */
    private List/*<IPath>*/ getProjectSrcPath() throws JavaModelException {
        List/* <IPath> */srcPath= new ArrayList();

        if (this.x10Project == null)
            return srcPath;

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
	    opts.parseCommandLine(new String[] { "-assert", "-noserial", "-cp", buildClassPathSpec(), "-sourcepath", projectSrcPath }, new HashSet());
	} catch (UsageError e) {
	    if (!e.getMessage().equals("must specify at least one source file"))
		System.err.println(e.getMessage());
	} catch (JavaModelException e) {
            X10UIPlugin.getInstance().writeErrorMsg("Unable to obtain resolved class path: " + e.getMessage());
	}
	X10UIPlugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
	X10UIPlugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
    }

    private String buildClassPathSpec() {
        StringBuffer buff= new StringBuffer();
        // RMF 8/2/2006 - Determine whether an X10 runtime is on the project classpath,
        // and if not, silently add the default X10 runtime, so that various IDE services
        // can still run (e.g. syntax highlighting, outlining, etc.).
        boolean hasRuntime= false;
        boolean runtimeValid= false;

        try {
            IClasspathEntry[] classPath= (x10Project != null) ? x10Project.getResolvedClasspath(true) : new IClasspathEntry[0];

            for(int i= 0; i < classPath.length; i++) {
                IClasspathEntry entry= classPath[i];
                final String entryPath= entry.getPath().toOSString();

                if (i > 0)
                    buff.append(";");
                buff.append(entryPath);

                if (entryPath.contains("x10.runtime")) {
                    hasRuntime= true;
                    if (new File(entryPath).exists())
                        runtimeValid= true;
                }
            }
            if (!hasRuntime || !runtimeValid) {
                if (buff.length() > 0)
                    buff.append(";");
                buff.append(getRuntimePath());
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

    private String getRuntimePath() {
        Bundle x10RuntimeBundle= Platform.getBundle("x10.runtime");
        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
        String x10RuntimePath= Platform.getInstallLocation().getURL().getPath() + "plugins/x10.runtime_" + bundleVersion + ".jar";

        return x10RuntimePath;
    }
}
