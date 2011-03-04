/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    @author Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    @author pcharles@us.ibm.com
*******************************************************************************/
package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import lpg.runtime.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.MessageHandlerAdapter;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

import polyglot.frontend.Globals;
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
    private ExtensionInfo fExtInfo;
    private polyglot.frontend.Compiler fCompiler;

    private final IJavaProject fX10Project;

    CompilerDelegate(Monitor monitor, IMessageHandler handler, IProject project) {
        this.fX10Project= (project != null) ? JavaCore.create(project) : null;

        fExtInfo= new org.eclipse.imp.x10dt.ui.parser.ExtensionInfo(monitor, new MessageHandlerAdapter(handler));
        buildOptions(fExtInfo);
        ErrorQueue eq= new SilentErrorQueue(100, "stderr");
        fCompiler = new polyglot.frontend.Compiler(fExtInfo, eq);
    	Globals.initialize(fCompiler); //PORT1.7 must initialize before jobs/goals are added to queue (change for Polyglot v3)
        Report.setQueue(eq);
    }

    public X10Lexer getLexer() { return fExtInfo.getLexer(); }
    public X10Parser getParser() { return fExtInfo.getParser(); }
    public Job getJob(Source source) { return fExtInfo.getJob(source); }

    public boolean compile(Collection<Source> sources) {
    	return fCompiler.compile(sources);
    }

    /**
     * @return a list of all project-relative CPE_SOURCE-type classpath entries.
     * @throws JavaModelException
     */
    private List<IPath> getProjectSrcPath() throws JavaModelException {
        List<IPath> srcPath= new ArrayList<IPath>();

        if (this.fX10Project == null)
            return srcPath;

        IClasspathEntry[] classPath= fX10Project.getResolvedClasspath(true);

        for(int i= 0; i < classPath.length; i++) {
            IClasspathEntry e= classPath[i];

            if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE)
                srcPath.add(e.getPath());
        }
        if (srcPath.size() == 0)
            srcPath.add(fX10Project.getProject().getLocation());
        return srcPath;
    }

    private String pathListToPathString(List<IPath> pathList) {
        StringBuffer buff= new StringBuffer();
        IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();

        for(Iterator<IPath> iter= pathList.iterator(); iter.hasNext(); ) {
            IPath path= iter.next();
            IProject projectRef= wsRoot.getProject(path.segment(0));

            if (projectRef != null && projectRef.exists()) {
                // This is a workspace-relative path, but the project may not actually
                // live inside the workspace, so use its actual location as the prefix
                // for the rest of the specified path.
                buff.append(projectRef.getLocation().append(path.removeFirstSegments(1)).toOSString());
            } else if (fX10Project.getProject().exists(path)) {
                buff.append(fX10Project.getProject().getLocation().append(path).toOSString());
            } else {
                buff.append(path.toOSString());
            }
            if (iter.hasNext())
                buff.append(File.pathSeparatorChar);
        }
        return buff.toString();
    }

    private void buildOptions(ExtensionInfo extInfo) {
		Options opts = extInfo.getOptions();

		// Options.global= opts;//PORT1.7 Global options object no longer exists. 
		//   instead, need to call Globals.initialize(compiler) prior to calling compiler
		//    Note this is done in constructor

		try {
			List<IPath> projectSrcLoc = getProjectSrcPath();
			String projectSrcPath = pathListToPathString(projectSrcLoc);
			opts.parseCommandLine(new String[] { "-assert", "-noserial", "-cp", buildClassPathSpec(), "-sourcepath",
					projectSrcPath }, new HashSet<String>());
		} catch (UsageError e) {
			if (!e.getMessage().equals("must specify at least one source file"))
				System.err.println(e.getMessage());
		} catch (JavaModelException e) {
			X10UIPlugin.getInstance().writeErrorMsg("Unable to obtain resolved class path: " + e.getMessage());
		}
		// X10UIPlugin.getInstance().maybeWriteInfoMsg("Source path = " +
		// opts.source_path);
		// X10UIPlugin.getInstance().maybeWriteInfoMsg("Class path = " +
		// opts.classpath);
	}

    private String buildClassPathSpec() {
        StringBuffer buff= new StringBuffer();
        // RMF 8/2/2006 - Determine whether an X10 runtime is on the project classpath,
        // and if not, silently add the default X10 runtime, so that various IDE services
        // can still run (e.g. syntax highlighting, outlining, etc.).
        boolean hasRuntime= false;
        boolean runtimeValid= false;

        try {
            IClasspathEntry[] classPath= (fX10Project != null) ? fX10Project.getResolvedClasspath(true) : new IClasspathEntry[0];

            for(int i= 0; i < classPath.length; i++) {
                IClasspathEntry entry= classPath[i];
                final String entryPath= entry.getPath().toOSString();

                if (i > 0)
                    buff.append(File.pathSeparatorChar);
                buff.append(entryPath);

                if (entryPath.contains(X10Plugin.X10_RUNTIME_BUNDLE_ID)) {//PORT1.7 use constant
                    hasRuntime= true;
                    if (new File(entryPath).exists())
                        runtimeValid= true;
                }
            }
            if (!hasRuntime || !runtimeValid) {
                if (buff.length() > 0)
                    buff.append(File.pathSeparatorChar);
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
        Bundle x10RuntimeBundle= Platform.getBundle(X10Plugin.X10_RUNTIME_BUNDLE_ID);//PORT1.7 use constant for runtime bundle
        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
        String x10RuntimePath= Platform.getInstallLocation().getURL().getPath() + "plugins/"+X10Plugin.X10_RUNTIME_BUNDLE_ID+"_" + bundleVersion + ".jar";//PORT1.7 use constant

        return x10RuntimePath;
    }
}
