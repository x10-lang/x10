/*******************************************************************************
* Copyright (c) 2008,2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.core.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.Configuration;

public class X10Builder extends IncrementalProjectBuilder {
	/**
     * Builder ID for the X10 compiler. Must match the ID of the builder extension defined in plugin.xml.
     */
    public static final String BUILDER_ID= X10DTCorePlugin.kPluginID + ".X10Builder";

    /**
     * Problem marker ID for X10 compiler errors/warnings/infos. Must match the ID of the marker extension defined in plugin.xml.
     */
    public static final String PROBLEMMARKER_ID= X10DTCorePlugin.kPluginID + ".problemMarker";
    private static final String ClasspathError= "Classpath error in project: ";
    protected IProject fProject;
    private IJavaProject fX10Project;
    private IProgressMonitor fMonitor;
    private X10ResourceVisitor fResourceVisitor= new X10ResourceVisitor();
    private final X10DeltaVisitor fDeltaVisitor= new X10DeltaVisitor();
    private Collection<IFile> fSourcesToCompile= new HashSet<IFile>();
    private Collection<IFile> fSourcesToDelete = new HashSet<IFile>(); //contains .x10 files that have been just deleted
    private static PluginBase sPlugin= null;
    protected PolyglotDependencyInfo fDependencyInfo;
    private Collection<IPath> fSrcFolderPaths; // project-relative paths
    private static final boolean traceOn=false;
    
    /**
     * The very first build of a project needs to build all sources, to start with a fresh state for dependencies.
     * fBuildAll is reset in collectSourcesToCompile.
     */
    private boolean fBuildAll = true;
    
    private Collection<ErrorInfo> fErrors;

    public X10Builder() {}

    
    /******************
     * Visitors
     ******************/
    
    private final class X10DeltaVisitor implements IResourceDeltaVisitor {
        public boolean visit(IResourceDelta delta) throws CoreException {
        	if (delta.getKind() == IResourceDelta.REMOVED){
        		return processResource(delta.getResource(), true);
        	} else 
        		return processResource(delta.getResource(), false);
        }
    }

    private class X10ResourceVisitor implements IResourceVisitor {
        public boolean visit(IResource res) throws CoreException {
            return processResource(res, false);
        }
    }

//    public Collection<ErrorInfo> getErrors(){
//    	return fErrors;
//    }
    
    protected boolean processResource(final IResource resource) {
            	if (resource instanceof IFile) {
            		IFile file= (IFile) resource;
            		if (isSourceFile(file))
            			fSourcesToCompile.add(file);
            	} else if (isBinaryFolder(resource))
            		return false;
            	return true;
     }


    /**
     * "Compile" the given resource
     * @param resource
     * @param removed true if resource has just been deleted
     * @return true if the resource's children should be visited, false otherwise
     */
    protected boolean processResource(final IResource resource, boolean removed){
    	if (resource instanceof IFile){
    		IFile file = (IFile) resource;
    		if (isSourceFile(file)){
    			if (!removed) {
    				fSourcesToCompile.add(file);
    			} else {
    				fSourcesToCompile.addAll(getChangeDependents(file));
    				fSourcesToDelete.add(file);
    			}
    		} 
    	} else if (isBinaryFolder(resource)) 
			return false;
    	return true;
    }
    
    protected boolean isSourceFile(IFile file) {
        String exten= file.getFileExtension();

        if (!(exten != null && exten.compareTo("x10") == 0))
            return false;

        IContainer parent= (IContainer) file.getParent();
        boolean isInSrc= false;

        for(Iterator<IPath> iter= fSrcFolderPaths.iterator(); iter.hasNext(); ) {
            IPath srcFolderPath= iter.next();

            if (srcFolderPath.isPrefixOf(parent.getProjectRelativePath()))
                isInSrc= true;
        }
        return isInSrc;
    }

    private boolean isBinaryFolder(IResource resource) {
        // BUG This should check whether the given resource is in an "output folder"
        // of a source classpath entry, analogous to above check in isSourceFile().
        return resource.getFullPath().lastSegment().equals("bin"); //TODO: Fix 
    }
    
    
    /*
     * Visitor to find files that have a compilation error
     */
    private class HasErrorVisitor implements IResourceVisitor {
        boolean hasErrors = false;
        public boolean visit(IResource res) throws CoreException {
            return processResource(res);
        }
        protected boolean processResource(IResource resource) throws CoreException {
            if (resource instanceof IFile) {
                IFile file= (IFile) resource;
                if (isSourceFile(file) && hasErrors(file)){
                	hasErrors = true;
                	return false;
                }
            } else if (isBinaryFolder(resource)) {
                return false;
            }
            return true;
        }
    }
    
    private boolean hasErrors(IFile file) throws CoreException{
    	return file.findMaxProblemSeverity(PROBLEMMARKER_ID, true, IResource.DEPTH_INFINITE) == IMarker.SEVERITY_ERROR;
    }

    private HasErrorVisitor fHasErrorVisitor= new HasErrorVisitor();

    public boolean hasErrors() {
        try { 
            fProject.accept(fHasErrorVisitor);
            return fHasErrorVisitor.hasErrors;
        } catch (CoreException e) {
            X10DTCorePlugin.getInstance().logException("Error while looking to see if project has errors", e);
        }
        return false;
    }
    

    /******************
     * END - Visitors
     ******************/
    
    public String toString() {
        return "X10 Builder for project " + fProject.getName();
    }
    
    /***********************
     * Collecting Sources
     ***********************/
    /**
     * @return a list of all workspace-relative CPE_SOURCE-type classpath entries.
     * @throws JavaModelException
     */
    private List<IPath> getProjectSrcPath() throws JavaModelException {
        List<IPath> srcPath= new ArrayList<IPath>();
        IJavaProject javaProject= JavaCore.create(fProject);
        IClasspathEntry[] classPath= javaProject.getResolvedClasspath(true);

        for(int i= 0; i < classPath.length; i++) {
            IClasspathEntry e= classPath[i];

            if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE)
            	//The path is relative to the workspace but actually considered absolute because it starts with '/'
            	//We call makeRelative() to remove the initial '/'
            	//This will allow method pathListToPathString(...) to correctly append the absolute path of the workspace.
                srcPath.add(e.getPath().makeRelative());
            else if (e.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
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
            srcPath.add(fProject.getLocation());
        return srcPath;
    }

   
    private List<Source> collectStreamSources(Collection<IFile> sourceFiles) {
        List<Source> streams= new ArrayList<Source>();

        for(Iterator<IFile> it= sourceFiles.iterator(); it.hasNext(); ) {
            IFile sourceFile= it.next();

            try {
                final String filePath= sourceFile.getLocation().toOSString();
                StreamSource srcStream= new StreamSource(sourceFile.getContents(), filePath);
                streams.add(srcStream);
            } catch (IOException e) {
                X10DTCorePlugin.getInstance().writeErrorMsg("Unable to open source file '" + sourceFile.getLocation() + ": " + e.getMessage());
            } catch (CoreException e) {
                X10DTCorePlugin.getInstance().writeErrorMsg("Unable to open source file '" + sourceFile.getLocation() + ": " + e.getMessage());
            }
        }
        return streams;
    }

    /**
     * Collects the set of source folders in this project, to be used in filtering out
     * files in non-src folders that the builder should not attempt to compile.
     * @see isSourceFile()
     */
    private void collectSourceFolders() throws JavaModelException {
        fSrcFolderPaths= new HashSet<IPath>();
        IClasspathEntry[] cpEntries= fX10Project.getResolvedClasspath(true);

        for(int i= 0; i < cpEntries.length; i++) {
            IClasspathEntry cpEntry= cpEntries[i];
            if (cpEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                final IPath entryPath= cpEntry.getPath();
                if (!entryPath.segment(0).equals(fX10Project.getElementName())) {
                    X10DTCorePlugin.getInstance().maybeWriteInfoMsg("Source classpath entry refers to another project: " + entryPath);
                    continue;
                }
                fSrcFolderPaths.add(entryPath.removeFirstSegments(1));
            }
        }
    }

    private void collectChangeDependents() {
        Collection<IFile> changeDependents= new ArrayList<IFile>();
        for(Iterator<IFile> iter= fSourcesToCompile.iterator(); iter.hasNext(); ) {
        	IFile srcFile= iter.next();
        	changeDependents.addAll(getChangeDependents(srcFile));
        }
        for (IFile file: changeDependents){
        	fSourcesToCompile.add(file);
        }
    }

    private Collection<IFile> getChangeDependents(IFile srcFile){
    	Collection<IFile> result = new ArrayList<IFile>();
        Set<String> fileDependents= fDependencyInfo.getDependentsOf(srcFile.getFullPath().toString());
        IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();
        if (fileDependents != null) {
            for(Iterator<String> iterator= fileDependents.iterator(); iterator.hasNext(); ) {
                String depPath= iterator.next();
                IFile depFile= wsRoot.getFile(new Path(depPath));
                	result.add(depFile);
            }
        }
        return result;
    }
    
    private void collectSourcesToCompile() throws CoreException {
        collectSourceFolders();

        IResourceDelta delta= getDelta(fProject);
        if(traceOn)System.out.println("fSourcesToCompile="+fSourcesToCompile);

        if (delta != null) {
        	X10DTCorePlugin.getInstance().maybeWriteInfoMsg("==> Scanning resource delta for project '" + fProject.getName() + "'... <==");
            delta.accept(fDeltaVisitor);
            X10DTCorePlugin.getInstance().maybeWriteInfoMsg("X10 delta scan completed for project '" + fProject.getName() + "'...");
        } 
        if (fBuildAll || delta == null){
            X10DTCorePlugin.getInstance().maybeWriteInfoMsg("==> Scanning for X10 source files in project '" + fProject.getName() + "'... <==");
            fProject.accept(fResourceVisitor);
            X10DTCorePlugin.getInstance().maybeWriteInfoMsg("X10 source file scan completed for project '" + fProject.getName() + "'...");
        }
        if(traceOn)System.out.println("fSourcesToCompile="+fSourcesToCompile);
        collectChangeDependents();
        //collectFilesWithErrors();
        collectFilesWithNoJavaFile();
        if (fBuildAll) fBuildAll = false;
    }

   

	/**
	 * Assumes that each path is either workspace-relative or filesystem-absolute.
	 * Note that a path starting with '/' is considered absolute.
	 * @param pathList
	 * @return semicolon-separated list of filesystem-absolute paths. For workspace-relative paths, adds the absolute path of the workspace.
	 */
    private String pathListToPathString(List<IPath> pathList) {
        StringBuffer buff= new StringBuffer();
        IPath wsLoc= fProject.getWorkspace().getRoot().getLocation();

        for(Iterator<IPath> iter= pathList.iterator(); iter.hasNext();) {
            IPath path= iter.next();
            if (path.isAbsolute()){
            	buff.append(path.toOSString());
            } else { 
            	buff.append(wsLoc.append(path).toOSString());
            } 
            if (iter.hasNext())
                buff.append(File.pathSeparatorChar);
        }
        return buff.toString();
    }
    
    
    /***********************
     * END - Collecting Sources
     ***********************/
    
    /***********************
     * Compilation
     ***********************/
    
    private Collection<IProject> doCompile() throws CoreException {
        if (!fSourcesToCompile.isEmpty()) {
        	if(traceOn)System.out.println("X10Builder.doCompile() fSourcesToCompile: "+fSourcesToCompile);
    	    invokeX10C(fSourcesToCompile);
    	    // Now do a refresh to make sure the Java compiler sees the Java
    	    // source files that Polyglot just created.
            fProject.refreshLocal(IResource.DEPTH_INFINITE, fMonitor);
        }
        return computeDependentProjects();
    }

    private Collection<IProject> computeDependentProjects() {
        Collection<IProject> dependentProjects= new ArrayList<IProject>();
        try {

            IClasspathEntry[] cpEntries= fX10Project.getResolvedClasspath(false);
            IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

            for(int i= 0; i < cpEntries.length; i++) {
                IClasspathEntry cpEntry= cpEntries[i];
    
                if (cpEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
                    IProject proj= wsRoot.getProject(cpEntry.getPath().segment(0));
    
                    dependentProjects.add(proj);
                }
            }
        } catch (JavaModelException e) {
            X10DTCorePlugin.getInstance().logException("Unable to resolve X10 project classpath", e);
        }
        return dependentProjects;
    }

    private String fileSetToString(Collection<IFile> sources) {
        StringBuffer buff= new StringBuffer();

        for(Iterator<IFile> iter= sources.iterator(); iter.hasNext(); ) {
            IFile file= iter.next();

            buff.append(file.getProjectRelativePath());
            if (iter.hasNext())
                buff.append(',');
        }
        return buff.toString();
    }
   
    /**
     * Run the X10 compiler on the given Collection of source IFile's.
     * @param sources
     */
    private void invokeX10C(final Collection<IFile> sources) {
        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("Running X10C on source file set '" + fileSetToString(sources) + "'...");
        clearMarkersOn(sources);
        fErrors= new ArrayList<ErrorInfo>();
        
        IWorkspace ws= ResourcesPlugin.getWorkspace();
    	IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
        	public void run(IProgressMonitor monitor) {
        		//Due to bug XTENLANG-1368, we can't hand all the source files at once to the compiler
        		//Once this is fixed, remove the for loop below.
        		compileAllSources(sources, fErrors);
        	
//        		for(IFile f: sources){
//        			Collection<IFile> c = new ArrayList<IFile>();
//        			c.add(f);
//        			compileAllSources(c, fErrors);
//        		}
        	}
        };
        try {
        	ws.run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
        createMarkers(fErrors);
        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("X10C completed on source file set.");
    }

 
    private void compileAllSources(Collection<IFile> sources, final Collection<ErrorInfo> errors) {  
    	final BuilderExtensionInfo extInfo = new BuilderExtensionInfo(this,sources);
    	buildOptions(extInfo);
    	final Compiler compiler= new Compiler(extInfo, new AbstractErrorQueue(1000000, extInfo.compilerName()) {
            protected void displayError(ErrorInfo error) {
                errors.add(error);
            }
        });
        Globals.initialize(compiler);
        
        List<Source> streams= collectStreamSources(sources);
        try {
        		compiler.compile(streams);
        		computeDependencies(/*extInfo.getJobs());*/  extInfo.scheduler().commandLineJobs());
        } catch (InternalCompilerError ice) {
            // HACK - RMF 2/1/2005: Polyglot may throw an InternalCompilerError when a
            // source file (say A) references a type residing in a package directory when
            // the corresponding compilation unit lacks the proper package declaration.
            // This happens b/c the type that gets recorded in the TableResolver when
            // processing the erroneous compilation unit has an unqualified name, and the
            // reference causes Polyglot to attempt to re-process the same compilation unit.
            final String iceRegexp= "Attempted to load source (.*), but it's already loaded.";
            final Pattern icePat= Pattern.compile(iceRegexp);
            final Matcher iceMatcher= icePat.matcher(ice.getMessage());
            final IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

            if (iceMatcher.matches()) {
                String filePath= iceMatcher.group(1);
                IFile errorFile= wsRoot.getFileForLocation(new Path(filePath));

                if (errorFile != null)
                    addProblemMarkerTo(errorFile, "Probable missing package declaration", IMarker.SEVERITY_ERROR, "", IMarker.PRIORITY_NORMAL, 0, 0, 0);
            } else {
                String msg= ice.getMessage();
                X10DTCorePlugin.getInstance().writeErrorMsg("Internal X10 compiler error: " + (msg != null ? msg : ice.getClass().getName()));
                addProblemMarkerTo(fProject, "An internal X10 compiler error occurred; see the Error Log for more details.", IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
            }
        } catch (final Error error) {
            String msg= error.getMessage();
            if (msg.startsWith("No translation for ")) {
                final String type= msg.substring(19).substring(0, msg.lastIndexOf(' ')-19);

                postMsgDialog("X10 Compiler Configuration error", "Unable to locate compiler template for " + type + "; check X10 Preferences.");
            }
        } catch (Exception e) {
            String msg= e.getMessage();
            X10DTCorePlugin.getInstance().writeErrorMsg("Internal X10 compiler error: " + (msg != null ? msg : e.getClass().getName()));
            addProblemMarkerTo(fProject, "An internal X10 compiler error occurred; see the Error Log for more details.", IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
        } 
        finally {
        	//close all streams
        	for(Source s: streams){
        		try {
					((StreamSource)s).close();
				} catch (IOException e) {
					
				}
        	}
        }
    }

    private void buildOptions(BuilderExtensionInfo extinfo) {
        Options opts= extinfo.getOptions();
        try {
            List<IPath> projectSrcLoc= getProjectSrcPath();
            String projectSrcPath= pathListToPathString(projectSrcLoc);// note this is user's src dir, plus runtime jar.
            String outputDir= fProject.getWorkspace().getRoot().getLocation().append((IPath) projectSrcLoc.get(0)).toOSString(); // HACK: just take 1st directory as output
            //BRT note: probably won't work if > 1 src folder
            List<String> optsList = new ArrayList();
            String[] stdOptsArray = new String[] {
//	            "-assert", // default preference (see below under P_PERMITASSERT)
                "-noserial",
                "-cp",
                buildClassPathSpec(),
                "-d", outputDir,
                "-sourcepath", 
                projectSrcPath,
                "-commandlineonly",
                "-c"
            };
            for (String s: stdOptsArray) {
                optsList.add(s);
            }
            IPreferencesService prefService = X10DTCorePlugin.getInstance().getPreferencesService();
            
            optsList.add(0, "-BAD_PLACE_RUNTIME_CHECK="+(prefService.getBooleanPreference(X10Constants.P_BADPLACERUNTIMECHECK)));
            optsList.add(0, "-LOOP_OPTIMIZATIONS="+(prefService.getBooleanPreference(X10Constants.P_LOOPOPTIMIZATIONS)));
            optsList.add(0, "-ARRAY_OPTIMIZATIONS="+(prefService.getBooleanPreference(X10Constants.P_ARRAYOPTIMIZATIONS)));
            optsList.add(0, "-STATIC_CALLS="+(prefService.getBooleanPreference(X10Constants.P_STATICCALLS)));
            optsList.add(0, "-VERBOSE_CALLS="+(prefService.getBooleanPreference(X10Constants.P_VERBOSECALLS)));
            optsList.add(0, "-OPTIMIZE="+(prefService.getBooleanPreference(X10Constants.P_VERBOSECALLS)));
            optsList.add(0, "-CLOSURE_INLINING="+(prefService.getBooleanPreference(X10Constants.P_CLOSUREINLINING)));
            optsList.add(0, "-WORK_STEALING="+(prefService.getBooleanPreference(X10Constants.P_WORKSTEALING)));
            if (prefService.getBooleanPreference(X10Constants.P_PERMITASSERT)) {
                optsList.add(0, "-assert");
            }

            if (prefService.isDefined(X10Constants.P_ADDITIONALCOMPILEROPTIONS)) {
                String optionString = prefService.getStringPreference(X10Constants.P_ADDITIONALCOMPILEROPTIONS);
                String[] options = optionString.split("\\s");
                int extraOptionsPos=0;
                for (String s: options) {
                    if (s!=null) {
                        optsList.add(extraOptionsPos++, s);  // FIXME refactor this and do stuff in the order it belongs in the first place
                    }
                }
            }
            if(prefService.getBooleanPreference(X10Constants.P_ECHOCOMPILEARGUMENTSTOCONSOLE)) {
            	echoBuildOptions(optsList);
			}
            String[] optsArray = optsList.toArray(new String[optsList.size()]);
            opts.parseCommandLine(optsArray, new HashSet());
        } catch (UsageError e) {
            if (!e.getMessage().equals("must specify at least one source file")) {
                X10DTCorePlugin.getInstance().logException("Error parsing compiler options", e);
            }
        } catch (JavaModelException e) {
            X10DTCorePlugin.getInstance().writeErrorMsg("Unable to determine project source folder location for " + fProject.getName());
        }
        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("Compiler templates = " + Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY);
        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("Output directory = " + opts.output_directory);
    }

	protected void echoBuildOptions(List<String> optsList) {
		X10DTCorePlugin plugin = X10DTCorePlugin.getInstance();
		MessageConsole console = plugin.getConsole();
		MessageConsoleStream consoleOut = console.newMessageStream();
		consoleOut.println(plugin.getTimeAndDate());
		consoleOut.print("Build options for compiling "+fSourcesToCompile+": ");  
		final String dash="-";
		for (String s : optsList) {
			// try to put "-option" on same line with the option arg
			if(s.startsWith(dash))
				consoleOut.print("\n   "+s);
			else
				consoleOut.print("   "+s);
		}
		consoleOut.println("");
		try {
			consoleOut.flush();
			consoleOut.close();
		} catch (IOException e1) {
			//e1.printStackTrace();
			plugin.logException("Exception writing build options to console", e1);
		}
		plugin.showConsole();
	}
	
    protected IProject[] build(final int kind, final Map args, IProgressMonitor monitor) throws CoreException {
    	
        final String[] buildKind= { "0", "1", "2", "3", "4", "5", "Full", "7", "8", "Auto", "Incremental", "11", "12", "13", "14", "Clean" };

  		fProject= getProject();
        fX10Project= JavaCore.create(fProject);

        X10DTCorePlugin.getInstance().maybeWriteInfoMsg("build kind = " + buildKind[kind]);

        if (fDependencyInfo == null)
            fDependencyInfo= new PolyglotDependencyInfo(fProject);

        fMonitor= monitor;
        if (sPlugin == null)
            sPlugin= X10DTCorePlugin.getInstance();

        // Refresh prefs every time so that changes take effect on the next build.
        sPlugin.refreshPrefs();

        if (X10DTCorePlugin.x10CompilerPath.equals("???")) {
            postMsgDialog("X10 Error", "X10 common directory location not yet set.");
            return null;
        }
        readCompilerConfig();
        //checkClasspathForRuntime(); //TODO: silencing for now until we have container.
        if (kind == CLEAN_BUILD || kind == FULL_BUILD)
            fDependencyInfo.clearAllDependencies();
        fSourcesToCompile.clear();
        fSourcesToDelete.clear();
        fMonitor.beginTask("Scanning and compiling X10 source files...", 0);

        collectSourcesToCompile();
        Collection<IFile> c = new ArrayList<IFile>();
        c.addAll(fSourcesToCompile);
        c.addAll(fSourcesToDelete);
        cleanGeneratedFiles(c);
        Collection<IProject> dependents= doCompile();
        fMonitor.done();
        return (IProject[]) dependents.toArray(new IProject[dependents.size()]);
    }
    
    private void computeDependencies(Collection<Job> jobs){
    	for(Job job: jobs){
    		ComputeDependenciesVisitor visitor = new ComputeDependenciesVisitor(job, job.extensionInfo().typeSystem(), fDependencyInfo);
    		if (job.ast() != null)
    			job.ast().visit(visitor.begin());
    	}
    }

    private boolean cleanGeneratedFiles(Collection<IFile> sources) {
    	IWorkspace ws= ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot wsRoot= ws.getRoot();
        final List<IFile> genFiles= new ArrayList<IFile>();
       
//		final boolean traceOn=false;
//        for(IFile srcFile: fSourcesToCompile) {
//        	if(traceOn)System.out.println("srcFile: "+srcFile);
//            IPath genJavaFile= srcFile.getFullPath().removeFileExtension().addFileExtension("java");
//            if(traceOn)System.out.println("genJavaFile: "+genJavaFile);
//            IPath genFileFolder= srcFile.getFullPath().removeLastSegments(1);
//            if(traceOn)System.out.println("genFileFolder: "+genFileFolder);
//            genFiles.add(wsRoot.getFile(genJavaFile));
//        }
//        
//        for(IFile srcFile: fSourcesToDelete){
//        	IPath genJavaFile= srcFile.getFullPath().removeFileExtension().addFileExtension("java");
//        	genFiles.add(wsRoot.getFile(genJavaFile));
//        }
        
        for(IFile srcFile: sources){
        	IPath genJavaFile= srcFile.getFullPath().removeFileExtension().addFileExtension("java");
        	genFiles.add(wsRoot.getFile(genJavaFile));
        }
        
       
        IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
        	public void run(IProgressMonitor monitor) {   
        		IStatus status= null;
        		for (IFile file : genFiles) {
        			try {
        				file.delete(true, new NullProgressMonitor());
        			} catch (CoreException e) {
        				if (status == null) {
        					status = new Status(IStatus.ERROR,
        							X10DTCorePlugin.kPluginID, e.getLocalizedMessage());
        				} else if (status instanceof MultiStatus) {
        					MultiStatus ms = (MultiStatus) status;
        					ms.add(new Status(IStatus.ERROR, X10DTCorePlugin.kPluginID,
        							e.getLocalizedMessage()));
        				} else {
        					IStatus newStat = new Status(IStatus.ERROR,
        							X10DTCorePlugin.kPluginID, e.getLocalizedMessage());
        					status = new MultiStatus(X10DTCorePlugin.kPluginID,
        							IStatus.ERROR, new IStatus[] { status, newStat },
        							"Multiple errors occurred", null);
        				}
        			}
        		}
        	}
        };
        try {
            ws.run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());		 
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }

		return true;
	}
    
  
    private String buildClassPathSpec() {
        StringBuffer buff= new StringBuffer();

        try {
            IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();
            IClasspathEntry[] classPath= fX10Project.getResolvedClasspath(true);

            for(int i= 0; i < classPath.length; i++) {
                IClasspathEntry entry= classPath[i];

                if (i > 0)
                    buff.append(File.pathSeparatorChar);
                if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
                    buff.append(entry.getPath().toOSString());
                else if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
                    // Add the output location of each CPE_SOURCE classpath entry in the referenced project
                    IProject refProject= wsRoot.getProject(entry.getPath().toPortableString());
                    IJavaProject refJavaProject= JavaCore.create(refProject);
                    IClasspathEntry[] refJavaCPEntries= refJavaProject.getResolvedClasspath(true);
                    for(int j= 0; j < refJavaCPEntries.length; j++) {
                        if (refJavaCPEntries[j].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                            IPath outputLoc= refJavaCPEntries[j].getOutputLocation();
                            if (outputLoc == null) {
                                outputLoc= refJavaProject.getOutputLocation();
                            }
                            buff.append(wsRoot.getLocation().append(outputLoc).toOSString());
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            X10DTCorePlugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
        }
        return buff.toString();
    }

    
    private void readCompilerConfig() {
    	// PORT1.7 RMF 9/28/2008 - Not sure we even need to do this any more - check w/ compiler team
        // Read configuration at every compiler invocation, in case it changes (e.g. via
        // the X10 Preferences page). Theoretically, if the user really does change the
        // compiler configuration, we should trigger a full rebuild, but we don't yet.
        try {
            // The X10 configuration file's location is given by the value of the System
            // property "x10.configuration", which is initialized by X10Plugin.refreshPrefs()
            // and by a preference store listener in X10PreferencePage.
        	String x10Config=System.getProperty("x10.configuration");
            Configuration.readConfiguration(Configuration.class, x10Config);
        } catch (x10.config.ConfigurationError e) {
            if (e.getCause() instanceof FileNotFoundException) {
                FileNotFoundException fnf= (FileNotFoundException) e.getCause();
                if (fnf.getMessage().startsWith("???\\standard.cfg")) {
                    postMsgDialog("X10 Error", "X10 configuration file location not yet set.");
                }
            } else if (e.getCause() instanceof IOException) {
                IOException io= (IOException) e.getCause();
                if (io.getMessage().endsWith("non-existent"))
                    postMsgDialog("X10 Error", "X10 compiler configuration invalid; please re-set in the X10 Preferences page.");
            } else
                postMsgDialog("X10 Error", e.getMessage());
        }
    }
    
    /**
     * Posts a dialog displaying the given message as soon as "conveniently possible".
     * This is not a synchronous call, since this method will get called from a
     * different thread than the UI thread, which is the only thread that can
     * post the dialog box.
     */
    private void postMsgDialog(final String title, final String msg) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell shell= X10DTCorePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();

                MessageDialog.openInformation(shell, title, msg);
            }
        });
    }
   
    /***********************
     * END - Compilation
     ***********************/
    
    
    /***********************
     * Handling Markers
     ***********************/
    private void createMarkers(final Collection<ErrorInfo> errors) {
        final IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

        for(Iterator<ErrorInfo> iter= errors.iterator(); iter.hasNext();) {
            ErrorInfo errorInfo= (ErrorInfo) iter.next();
            Position errorPos= errorInfo.getPosition();

            if (errorPos == null)
                continue;

            IFile errorFile= wsRoot.getFileForLocation(new Path(errorPos.file()));

            if (errorFile == null)
                errorFile= findFileInSrcPath(errorPos.file(), wsRoot);

            // if file is still null (e.g. file isn't within eclipse project), then attach it to this project, so we don't lose it
            if (errorFile == null) {      	
            	String msg = "Error on runtime library file " + errorPos.nameAndLineString()+" - "+errorInfo.getMessage();
            	addProblemMarkerTo(fProject, msg, IMarker.SEVERITY_ERROR, IMarker.PRIORITY_NORMAL);
            } else {
            	int severity= (errorInfo.getErrorKind() == ErrorInfo.WARNING ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR);

            	if (errorPos == Position.COMPILER_GENERATED)
            		X10DTCorePlugin.getInstance().writeErrorMsg(errorInfo.getMessage());
            	else
            		addProblemMarkerTo(errorFile, errorInfo.getMessage(), severity, errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line(), errorPos
                        .offset(), errorPos.endOffset());
            }
        }
    }

    protected void clearMarkersOn(final Collection<IFile> sources) {
    	IWorkspace ws= ResourcesPlugin.getWorkspace();
    	IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) {
            	try {
                    // Remove the project-level "compiler crash" marker, if any
                    fProject.deleteMarkers(PROBLEMMARKER_ID, true, 0);
                } catch (CoreException e1) {
                	
                }
                for(Iterator<IFile> iter= sources.iterator(); iter.hasNext(); ) {
                    IFile file= iter.next();

                    try {
                        file.deleteMarkers(PROBLEMMARKER_ID, true, IResource.DEPTH_INFINITE);
                    } catch (CoreException e) {
                    	
                    }
                }
            }
    	};
        try {
        	ws.run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    protected void addMarkerTo(final IFile sourceFile, final String type, final String msg, final int severity, final String loc, final int priority, final int lineNum, final int startOffset, final int endOffset) {
    	IWorkspace ws= ResourcesPlugin.getWorkspace();
    	IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) {
            	try {
                    IMarker marker= sourceFile.createMarker(type);
                    //TODO: change to setAttributes
                    marker.setAttribute(IMarker.MESSAGE, msg);
                    marker.setAttribute(IMarker.SEVERITY, severity);
                    marker.setAttribute(IMarker.LOCATION, loc);
                    marker.setAttribute(IMarker.PRIORITY, priority);
                    marker.setAttribute(IMarker.LINE_NUMBER, (lineNum >= 1) ? lineNum : 1);
                    if (startOffset >= 0) {
                        marker.setAttribute(IMarker.CHAR_START, startOffset);
                        marker.setAttribute(IMarker.CHAR_END, endOffset + 1);
                    } else {
                        marker.setAttribute(IMarker.CHAR_START, 0);
                        marker.setAttribute(IMarker.CHAR_END, 0);
                    }
                } catch (CoreException e) {
                    X10DTCorePlugin.getInstance().writeErrorMsg("Couldn't add marker to file " + sourceFile);
                }
            }
        };
        try {
        	ws.run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }  
    }

    protected void addProblemMarkerTo(IFile sourceFile, String msg, int severity, String loc, int priority, int lineNum, int startOffset, int endOffset) {
        addMarkerTo(sourceFile, PROBLEMMARKER_ID, msg, severity, loc, priority, lineNum, startOffset, endOffset);
    }

    protected void addTaskTo(IFile sourceFile, String msg, int severity, int priority, int lineNum, int startOffset, int endOffset) {
        addMarkerTo(sourceFile, IMarker.TASK, msg, severity, "", priority, lineNum, startOffset, endOffset);
    }

    protected void addProblemMarkerTo(final IProject project, final String msg, final int severity, final int priority) {
    	IWorkspace ws= ResourcesPlugin.getWorkspace();
    	IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) {
            	try {
                    IMarker marker= project.createMarker(PROBLEMMARKER_ID);

                    marker.setAttribute(IMarker.MESSAGE, msg);
                    marker.setAttribute(IMarker.SEVERITY, severity);
                    marker.setAttribute(IMarker.PRIORITY, priority);
                } catch (CoreException e) {
                    X10DTCorePlugin.getInstance().writeErrorMsg("Couldn't add marker to project " + project.getName());
                }
            }
        };
        try {
        	ws.run(runnable, ResourcesPlugin.getWorkspace().getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        } 
    }

    private IFile findFileInSrcPath(String fileName, IWorkspaceRoot wsRoot) {
        IFile entryFile= null;

        try {
            IClasspathEntry[] cp= fX10Project.getResolvedClasspath(true);

            for(int i= 0; i < cp.length && entryFile == null; i++) {
                IClasspathEntry entry= cp[i];

                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    IPath entryPath= entry.getPath();
                    IFolder cpDir= wsRoot.getFolder(entryPath);

                    if (cpDir != null) {
                        entryFile= recursiveSearchDirectories(wsRoot, cpDir, fileName);
                    }
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return entryFile;
    }

    /**
     * Find first file with name, fileName existing in cpDir or subdirectory of cpDir.
     * @return resource file, null if not found
     */
    private IFile recursiveSearchDirectories(IWorkspaceRoot wsRoot, IFolder cpDir, String fileName) throws CoreException {
        IPath entryPath= cpDir.getFullPath();
        IFile entryFile= wsRoot.getFile(entryPath.append(fileName));
        if (entryFile == null || !entryFile.exists()) {
            entryFile= null;
            IResource[] chillen= cpDir.members();
            for(int i= 0; entryFile == null && i < chillen.length; ++i) {
                if (chillen[i].getType() == IResource.FOLDER) {
                    entryFile= recursiveSearchDirectories(wsRoot, (IFolder) chillen[i], fileName);
                }
            }
        }
        return entryFile;
    }

    
    /***********************
     * END - Handling Markers
     ***********************/
    
    /***********************
     * Unused Methods
     ***********************/
    
    /*
     * Visitor to find sources that have no Java visitor
     */
    private class NoJavaVisitor implements IResourceVisitor {
    	public boolean visit(IResource res) throws CoreException {
    		return processResource(res);
    	}
    	
    	protected boolean processResource(IResource resource) throws CoreException {
    		 if (resource instanceof IFile) {
                 IFile file= (IFile) resource;
                 if (isSourceFile(file) && hasNoJava(file)) {
                     fSourcesToCompile.add(file);
                 }
             } else if (isBinaryFolder(resource)) {
                 return false;
             }
             return true;
    	}
    }

    private boolean hasNoJava(IFile file){
    	 IPath genJavaFile= file.getFullPath().removeFileExtension().addFileExtension("java");
    	 IFile javaFile= fProject.getWorkspace().getRoot().getFile(genJavaFile);
         if (!javaFile.exists()) 
        	 return true;
         return false;
    }
    
    private NoJavaVisitor fNoJavaVisitor= new NoJavaVisitor();

    private void collectFilesWithNoJavaFile() {
        try {
            fProject.accept(fNoJavaVisitor);
        } catch (CoreException e) {
            X10DTCorePlugin.getInstance().logException("Error while looking for source files with no Java file", e);
        }
    }
    
    /*
     * Visitor to find files that have a compilation error
     */
    private class X10ErrorVisitor implements IResourceVisitor {
        private boolean fCompileAll= false;
       
        public boolean visit(IResource res) throws CoreException {
            return processResource(res);
        }
        protected boolean processResource(IResource resource) throws CoreException {
            if (resource instanceof IFile) {
                IFile file= (IFile) resource;
                if (isSourceFile(file) && (fCompileAll || hasErrors(file))) {
                	fSourcesToCompile.add(file);
                }
            } else if (isBinaryFolder(resource)) {
                return false;
            }
            return true;
        }
        public void setCompileAll(boolean yesNo) {
            fCompileAll= yesNo;
        }
    }
    
  

    private X10ErrorVisitor fErrorVisitor= new X10ErrorVisitor();

    private void collectFilesWithErrors() {
        try {
            // The only project-level error we issue is for a compiler crash, for which we get no
            // info as to what file caused the problem. So we play it safe and recompile
            // everything in that case.
        	fErrorVisitor.setCompileAll(fProject.findMaxProblemSeverity(PROBLEMMARKER_ID, false, 0) == IMarker.SEVERITY_ERROR);
            fProject.accept(fErrorVisitor);
        } catch (CoreException e) {
            X10DTCorePlugin.getInstance().logException("Error while looking for source files with errors", e);
        }
    }
    
    /***********************
     * END - Unused Methods
     ***********************/
    
}
