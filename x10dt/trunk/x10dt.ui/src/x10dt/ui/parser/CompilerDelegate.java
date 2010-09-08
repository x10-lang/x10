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
package x10dt.ui.parser;

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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

import polyglot.ast.SourceFile;
import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.errors.X10ErrorInfo;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.X10DTUIPlugin;

public class CompilerDelegate {
	private x10dt.ui.parser.ExtensionInfo fExtInfo;

    private final IJavaProject fX10Project;

    private final ParseController.InvariantViolationHandler fViolationHandler;

    CompilerDelegate(Monitor monitor, final IMessageHandler handler, final IProject project, final IPath filePath, ParseController.InvariantViolationHandler violationHandler) {
        this.fX10Project= (project != null) ? JavaCore.create(project) : null;
        fViolationHandler= violationHandler;
        fExtInfo= new x10dt.ui.parser.ExtensionInfo(monitor, new MessageHandlerAdapterFilter(handler, filePath));
        buildOptions(fExtInfo);

        ErrorQueue eq = new AbstractErrorQueue(1000000, fExtInfo.compilerName()) {
            protected void displayError(ErrorInfo error) {
            	if (isValidationMsg(error)) {
            		if (fViolationHandler != null) {
            			fViolationHandler.handleViolation(error);
            		}
            	} else {
            	 	Position pos = error.getPosition();
                	if (pos != null) {
                		IPath errorPath = new Path(pos.file());
                		if (filePath.equals(errorPath)) {
                			handler.handleSimpleMessage(error.getMessage(), pos.offset(), pos.endOffset(), pos.column(), pos.endColumn(), pos.line(), pos.endLine());
                		}
                	} else {
                		if (error.getErrorKind()!=ErrorInfo.WARNING)
                			handler.handleSimpleMessage(error.getMessage(), 0, 0, 1, 1, 1, 1);
                	}
            	}
            }
        };
        final Compiler compiler = new Compiler(fExtInfo, eq);
    	Globals.initialize(compiler);
    }

    protected boolean isValidationMsg(ErrorInfo error) {
    	return (error.getErrorKind() == X10ErrorInfo.INVARIANT_VIOLATION_KIND);
    }

    public Compiler getCompiler() { return Globals.Compiler(); }
    public ExtensionInfo getExtInfo() { return fExtInfo; }

    public X10Lexer getLexerFor(Source src) { return fExtInfo.getLexerFor(src); }
    public X10Parser getParserFor(Source src) { return fExtInfo.getParserFor(src); }
    public SourceFile getASTFor(Source src) { return (SourceFile) fExtInfo.getASTFor(src); }
    public Job getJobFor(Source src) { return fExtInfo.getJobFor(src); }

    public boolean compile(Collection<Source> sources) {
        if (fViolationHandler != null) {
        	fViolationHandler.clear();
        }

        fExtInfo.setInterestingSources(sources);
    	return Globals.Compiler().compile(sources);
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

            if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                srcPath.add(e.getPath());
            } else if (e.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
                //PORT1.7 Compiler needs to see X10 source for all referenced compilation units,
                // so add source path entries of referenced projects to this project's sourcepath.
                // Assume that goal dependencies are such that Polyglot will not be compelled to
                // compile referenced X10 source down to Java source (causing duplication; see below).
                //
                // RMF 6/4/2008 - Don't add referenced projects to the source path:
                // 1) doing so should be unnecessary, since the classpath will include
                //    the project, and the class files should satisfy all references,
                // 2) doing so will cause Polyglot to compile the source files found in
                //    the other project to Java source files located in the *referencing*
                //    project, causing duplication, which is not what we want.
                //
                IProject refProject= ResourcesPlugin.getWorkspace().getRoot().getProject(e.getPath().toPortableString());
                IJavaProject refJavaProject= JavaCore.create(refProject);
                IClasspathEntry[] refJavaCPEntries= refJavaProject.getResolvedClasspath(true);
                for(int j= 0; j < refJavaCPEntries.length; j++) {
                    if (refJavaCPEntries[j].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                        srcPath.add(refJavaCPEntries[j].getPath());
                    }
                }
            } else if (e.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                // PORT1.7 Add the X10 runtime jar to the source path, since the compiler
                // needs to see the X10 source for the user-visible runtime classes (like
                // x10.lang.Region) to get the extra type information (for deptypes) that
                // can't be stored in Java class files, and for now, these source files
                // actually live in the X10 runtime jar.
                IPath path= e.getPath();
                if (path.toPortableString().contains(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID)) {
                    srcPath.add(path);
                }
            }
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
            opts.parseCommandLine(new String[] { "-assert", "-noserial", "-c", "-commandlineonly",
                    "-cp", buildClassPathSpec(), "-sourcepath", projectSrcPath
            }, new HashSet<String>());
            x10.Configuration.CHECK_INVARIANTS= (fViolationHandler != null);
        } catch (UsageError e) {
            if (!e.getMessage().equals("must specify at least one source file")) {
                X10DTUIPlugin.getInstance().writeErrorMsg(e.getMessage());
            }
        } catch (JavaModelException e) {
            X10DTUIPlugin.getInstance().writeErrorMsg("Unable to obtain resolved class path: " + e.getMessage());
        }
        // X10UIPlugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
        // X10UIPlugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
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

                if (entryPath.contains(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID)) {//PORT1.7 use constant
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
            X10DTCorePlugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
        }
        return buff.toString();
    }

    private String getRuntimePath() {
        Bundle x10RuntimeBundle= Platform.getBundle(X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID);//PORT1.7 use constant for runtime bundle
        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
        String x10RuntimePath= Platform.getInstallLocation().getURL().getPath() + "plugins/"+X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID+"_" + bundleVersion + ".jar";//PORT1.7 use constant

        return x10RuntimePath;
    }
}
