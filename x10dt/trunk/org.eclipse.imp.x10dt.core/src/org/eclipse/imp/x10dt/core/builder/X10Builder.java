/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lpg.runtime.IMessageHandler;
import lpg.runtime.IToken;

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
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.core.X10PreferenceConstants;
import org.eclipse.imp.x10dt.core.runtime.X10RuntimeUtils;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.preferences.BuildPathsPropertyPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ext.x10.Configuration;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.frontend.Goal;
import polyglot.frontend.SourceGoal_c;
import polyglot.frontend.VisitorGoal;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

import x10.parser.X10Lexer;
import x10.parser.X10Parser;

public class X10Builder extends IncrementalProjectBuilder {
	/**
     * Builder ID for the X10 compiler. Must match the ID of the builder extension defined in plugin.xml.
     */
    public static final String BUILDER_ID= X10Plugin.kPluginID + ".X10Builder";
    /**
     * Problem marker ID for X10 compiler errors/warnings/infos. Must match the ID of the marker extension defined in plugin.xml.
     */
    public static final String PROBLEMMARKER_ID= X10Plugin.kPluginID + ".problemMarker";

    private static final String ClasspathError= "Classpath error in project: ";

    private final class X10DeltaVisitor implements IResourceDeltaVisitor {
        public boolean visit(IResourceDelta delta) throws CoreException {
            return processResource(delta.getResource());
        }
    }

    private class X10ResourceVisitor implements IResourceVisitor {
        public boolean visit(IResource res) throws CoreException {
            return processResource(res);
        }
    }

    private IProject fProject;
    private IJavaProject fX10Project;
    private IProgressMonitor fMonitor;
    private X10ResourceVisitor fResourceVisitor= new X10ResourceVisitor();
    private final X10DeltaVisitor fDeltaVisitor= new X10DeltaVisitor();
    private Collection<IFile> fSourcesToCompile= new ArrayList<IFile>();
    private ExtensionInfo fExtInfo;
    private static PluginBase sPlugin= null;
    protected PolyglotDependencyInfo fDependencyInfo;

    private Collection<IPath> fSrcFolderPaths; // project-relative paths

    public X10Builder() {}

    /**
     * "Compile" the given resource
     * @param resource
     * @return true if the resource's children should be visited, false otherwise
     */
    protected boolean processResource(IResource resource) {
        if (resource instanceof IFile) {
            IFile file= (IFile) resource;
            if (isSourceFile(file))
                fSourcesToCompile.add(file);
        } else if (isBinaryFolder(resource))
            return false;
        return true;
    }

    protected boolean isSourceFile(IFile file) {
        String exten= file.getFileExtension();

        if (!(file.exists() && exten != null && exten.compareTo("x10") == 0))
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
        return resource.getFullPath().lastSegment().equals("bin");
    }

    protected void clearMarkersOn(Collection<IFile> sources) {
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

    protected void addMarkerTo(IFile sourceFile, String type, String msg, int severity, String loc, int priority, int lineNum, int startOffset, int endOffset) {
        try {
            IMarker marker= sourceFile.createMarker(type);

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
            X10Plugin.getInstance().writeErrorMsg("Couldn't add marker to file " + sourceFile);
        }
        
    }

    protected void addProblemMarkerTo(IFile sourceFile, String msg, int severity, String loc, int priority, int lineNum, int startOffset, int endOffset) {
        addMarkerTo(sourceFile, PROBLEMMARKER_ID, msg, severity, loc, priority, lineNum, startOffset, endOffset);
    }

    protected void addTaskTo(IFile sourceFile, String msg, int severity, int priority, int lineNum, int startOffset, int endOffset) {
        addMarkerTo(sourceFile, IMarker.TASK, msg, severity, "", priority, lineNum, startOffset, endOffset);
    }

    protected void addProblemMarkerTo(IProject project, String msg, int severity, int priority) {
        try {
            IMarker marker= project.createMarker(PROBLEMMARKER_ID);

            marker.setAttribute(IMarker.MESSAGE, msg);
            marker.setAttribute(IMarker.SEVERITY, severity);
            marker.setAttribute(IMarker.PRIORITY, priority);
        } catch (CoreException e) {
            X10Plugin.getInstance().writeErrorMsg("Couldn't add marker to project " + project.getName());
        }
    }

    /**
     * Run the X10 compiler on the given Collection of source IFile's.
     * @param sources
     */
    private void invokeX10C(Collection<IFile> sources) {
        X10Plugin.getInstance().maybeWriteInfoMsg("Running X10C on source file set '" + fileSetToString(sources) + "'...");
        clearMarkersOn(sources);
        compileAllSources(sources);
        X10Plugin.getInstance().maybeWriteInfoMsg("X10C completed on source file set.");
    }

    // TODO This goal should probably prereq Disambiguated instead of CodeGenerated,
    // and should be made a prereq of TypeChecked, so that dependency information
    // gets collected even if code can't be generated for some reason.
    private class ComputeDependenciesGoal extends VisitorGoal {
        public ComputeDependenciesGoal(Job job) {
            super(job, new ComputeDependenciesVisitor(job, job.extensionInfo().typeSystem(), fDependencyInfo));
            addPrereq(job.extensionInfo().scheduler().TypeChecked(job));
        }
    }

    private class CheckPackageDeclVisitor extends NodeVisitor {
        private final Job fJob;
        private boolean fSeenPkg= false;

        public CheckPackageDeclVisitor(Job job) {
            fJob= job;
        }
        @Override
        public NodeVisitor begin() {
            String path= fJob.source().path();
            // BRT don't bother looking for dependencies if we're in jar/zip
            //PORT1.7
            if(path.endsWith(".jar")|| path.endsWith(".zip")) {
            	System.out.println("looking for resource in zip/jar???");
            	return null;
            }
            return super.begin();
        }
        private void checkPackage(String declaredPkg, String actualPkg, Position pos) {
            if (!actualPkg.equals(declaredPkg)) {
                fJob.extensionInfo().compiler().errorQueue().enqueue(new ErrorInfo(ErrorInfo.SEMANTIC_ERROR, "Declared package doesn't match source file location.", pos));
            }
        }

        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof PackageNode) {
                PackageNode pkg= (PackageNode) n;
                Source src= fJob.source();
                String declaredPkg= pkg.package_().get().fullName().name().toString();
                String actualPkg= determineActualPackage(src);

                checkPackage(declaredPkg, actualPkg, pkg.position());
                fSeenPkg= true;
            }
            return super.enter(n);
        }

        private String determineActualPackage(Source src) {
            String srcPath= src.path();
            String projPath= X10Builder.this.fProject.getLocation().toOSString();
            String pkgPath;

            if (srcPath.startsWith(projPath)) {
                pkgPath= srcPath.substring(projPath.length()+1);
            } else {
                pkgPath= srcPath;
            }
            if (pkgPath.startsWith("src" + File.separator)) {
                pkgPath= pkgPath.substring(4);
            }
            if (pkgPath.length() == src.name().length()) {
                return ""; // It's in the default pkg
            } else {
                return pkgPath.substring(0, pkgPath.length() - src.name().length() - 1).replace(File.separatorChar, '.');
            }
        }

        @Override
        public Node override(Node n) {
            if (fSeenPkg) {
                return n;
            }
            return null;
        }
        @Override
        public void finish() {
            if (!fSeenPkg) { // No package decl -> implicitly in the default package
                Source src= fJob.source();
                checkPackage("", determineActualPackage(src), new Position(src.path(), src.name(), 1, 1));
            }
        }
    };

    private class CheckPackageDeclGoal extends VisitorGoal {
        public CheckPackageDeclGoal(Job job) {
            super(job, new CheckPackageDeclVisitor(job));
            addPrereq(job.extensionInfo().scheduler().intern(new ComputeDependenciesGoal(job)));
        }
    }

    private final static String[] sTaskPrefixes= new String[] { "// TODO ", "// BUG ", "// FIXME "  };

    private class CollectBookmarksGoal extends SourceGoal_c {
        public CollectBookmarksGoal(Job job) {
            super(job);
            addPrereq(job.extensionInfo().scheduler().intern(new CheckPackageDeclGoal(job)));
        }
		@Override
		public boolean runTask() {
            Job job= this.job();
            Node ast= job.ast();
            String path= job.source().path();
            // PORT1.7 if in a zip or jar then we don't need to...
            if(path.contains("*.zip")||path.contains("*.jar")) {
            	return true;
            }
            X10Parser.JPGPosition pos= (X10Parser.JPGPosition) ast.position();
            
            List<IToken> adjuncts=null;
            try {
            	//PORT1.7 -- uses getLeftToken()... can we get to parse stream in another way??
            	//adjuncts= pos.getLeftIToken().getPrsStream().getAdjuncts();
            	throw new UnsupportedOperationException("X10Builder collecting bookmarks, need adjuncts");
            }
            catch(Exception e) {
            	//PORT1.7 -- Hack to postpone JPGPosition.getLeftIToken() problem (always null now, need general method to recompute)
            	X10Plugin.getInstance().logException("Error while collecting bookmarks during build: JPGPosition / adjuncts", e);
            	adjuncts=new ArrayList<IToken>();
            }
            IFile file= fProject.getFile(path.substring(fProject.getLocation().toOSString().length()));

            try {
                file.deleteMarkers(IMarker.TASK, true, 1);
            } catch (CoreException e) {
                X10Plugin.getInstance().logException("Error while creating task", e);
            }

            for(IToken adjunct: adjuncts) {
                String adjunctStr= adjunct.toString();
                for(int i=0; i < sTaskPrefixes.length; i++) {
                    if (adjunctStr.startsWith(sTaskPrefixes[i])) {
                        String msg= adjunctStr.substring(3);
                        int lineNum= adjunct.getLine();
                        int startOffset= adjunct.getStartOffset();
                        int endOffset= adjunct.getEndOffset();

                        addTaskTo(file, msg, IMarker.SEVERITY_INFO, IMarker.PRIORITY_NORMAL, lineNum, startOffset, endOffset);
                    }
                }
            }
            return true;
		}
    }

    private final class BuilderExtensionInfo extends polyglot.ext.x10.ExtensionInfo {
    	@Override
    	protected Scheduler createScheduler() {
    		return new X10Scheduler(this) {
    			@Override
    			public List<Goal> goals(Job job) {
    				List<Goal> goals= super.goals(job);
    				Goal endGoal = goals.get(goals.size()-1);
    				if(!(endGoal.name().equals("End"))) {
    					throw new IllegalStateException("Not an End Goal?");
    				}
					endGoal.addPrereq(new CollectBookmarksGoal(job));
					return goals;
    			}
    		};
    	}
    	/**
    	 * Exactly like the base-class implementation, but sets the lexer up with an IMessageHandler.
    	 */
    	public Parser parser(Reader reader, FileSource source, final ErrorQueue eq) {
//    	    if (source.path().endsWith(XML_FILE_DOT_EXTENSION)) {
//    	        return new DomParser(reader, (X10TypeSystem) ts, (X10NodeFactory) nf, source, eq);
//    	    }

    	    try {
                //
                // X10Lexer may be invoked using one of two constructors.
                // One constructor takes as argument a string representing
                // a (fully-qualified) filename; the other constructor takes
                // as arguments a (file) Reader and a string representing the
                // name of the file in question. Invoking the first
                // constructor is more efficient because a buffered File is created
                // from that string and read with one (read) operation. However,
                // we depend on Polyglot to provide us with a fully qualified
                // name for the file. In Version 1.3.0, source.name() yielded a
                // fully-qualified name. In 1.3.2, source.path() yields a fully-
                // qualified name. If this assumption still holds then the
                // first constructor will work.
                // The advantage of using the Reader constructor is that it
                // will always work, though not as efficiently.
                //
    	    	final X10Lexer x10_lexer = new X10Lexer(reader, source.name());
                //
//                final X10Lexer x10_lexer= (source instanceof SourceLoader.ZipSource) ? new X10Lexer() : new X10Lexer(source.path());//PORT1.7 zip file requires different Lexer
//                if (source instanceof SourceLoader.ZipSource) {
//                	//PORT 1.7 special case for loading from jar file
//                	SourceLoader.ZipSource zipSource = (SourceLoader.ZipSource) source;
//                	String name= zipSource.name();
//                	String contents= BuilderUtils.getFileContents(zipSource.open());
//
//                	x10_lexer.initialize(contents.toCharArray(), name);
//                }
                x10_lexer.setMessageHandler(new IMessageHandler() {
                    public void handleMessage(int errorCode, int[] msgLocation, int[] errorLocation, String filename, String[] errorInfo) {
                       //PORT1.7 -- need to get info (e.g. is this zipfile?) 
                    	// need file and entry name from sourceLoader$ZipSource
                    	// to be able to issue accurate error messages
                    	// store enough info in the Position so that createMarkers can put marker in right spot (or ???)
                    	//(but.. runtime jar file will usually be outside the workspace)
                    	// UBT
                    	// check whether this message corresponds to something outside workspace; if so, discard or put "someplace obvious"
                    	//            ( visible)  -- project root folder???  so that marker will show up and info isn't lost
                    	Position p= new Position(null, filename, msgLocation[IMessageHandler.START_LINE_INDEX],
                                msgLocation[IMessageHandler.START_COLUMN_INDEX], msgLocation[IMessageHandler.END_LINE_INDEX],
                                msgLocation[IMessageHandler.END_COLUMN_INDEX], msgLocation[IMessageHandler.OFFSET_INDEX],
                                msgLocation[IMessageHandler.OFFSET_INDEX] + msgLocation[IMessageHandler.LENGTH_INDEX]);
                        eq.enqueue(ErrorInfo.SYNTAX_ERROR, errorInfo[0] + " " + x10_lexer.errorMsgText[errorCode], p);
                    }
                });
                X10Parser x10_parser= new X10Parser(x10_lexer, ts, nf, source, eq); // Create the parser
                x10_lexer.lexer(x10_parser);
                return x10_parser; // Parse the token stream to produce an AST
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new IllegalStateException("Could not parse " + source.path());
        }
    }

    private void compileAllSources(Collection<IFile> sourceFiles) {
        fExtInfo= new BuilderExtensionInfo();

        List<Source> streams= collectStreamSources(sourceFiles);
        final Collection<ErrorInfo> errors= new ArrayList<ErrorInfo>();

        buildOptions();

        Compiler compiler= new Compiler(fExtInfo, new AbstractErrorQueue(1000000, fExtInfo.compilerName()) {
            protected void displayError(ErrorInfo error) {
                errors.add(error);
            }
        });
        Globals.initialize(compiler);//PORT1.7 Must initialize before actually calling compiler
//      Report.addTopic(Report.verbose, 1);
        try {
            compiler.compile(streams);
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
            }
        } catch (final Error error) {
            String msg= error.getMessage();
            if (msg.startsWith("No translation for ")) {
                final String type= msg.substring(19).substring(0, msg.lastIndexOf(' ')-19);

                postMsgDialog("X10 Compiler Configuration error", "Unable to locate compiler template for " + type + "; check X10 Preferences.");
            }
        } catch (Exception e) {
            String msg= e.getMessage();
            X10Plugin.getInstance().writeErrorMsg("Internal X10 compiler error: " + (msg != null ? msg : e.getClass().getName()));
            addProblemMarkerTo(fProject, "An internal X10 compiler error occurred; see the Error Log for more details.", IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
//            postMsgDialog("Internal Compiler Error", "An internal X10 compiler error occurred; see the Error Log for more details.");
        }
//      fDependencyInfo.dump();
        createMarkers(errors);
        fExtInfo=null;
    }

    private void buildOptions() {
        Options opts= fExtInfo.getOptions();
      
//      Options.global= opts; // PORT1.7 RMF 9/23/2008 - Global Options object no longer exists
        try {
            List<IPath> projectSrcLoc= getProjectSrcPath();
            String projectSrcPath= pathListToPathString(projectSrcLoc);// note this is user's src dir, plus runtime jar.
            String outputDir= fProject.getWorkspace().getRoot().getLocation().append((IPath) projectSrcLoc.get(0)).toOSString(); // HACK: just take 1st directory as output
            //BRT note: probably won't work if > 1 src folder
            // TODO RMF 11/9/2006 - Remove the "-noserial" option; it's really for the demo
//          opts.parseCommandLine(new String[] { "-assert", "-noserial", "-cp", buildClassPathSpec(), "-d", outputDir, "-sourcepath", projectSrcPath }, new HashSet());
            List<String> optsList = new ArrayList();
            String[] stdOptsArray = new String[] {
//	            "-assert", // default preference (see below under P_ASSERT)
                "-noserial",
                "-cp",
                buildClassPathSpec(),
                "-d", outputDir,
                "-sourcepath", 
                projectSrcPath,
                //"-commandlineonly"  FIXME temp only  BRT
            };
            for (String s: stdOptsArray) {
                optsList.add(s);
            }
            // get the same PreferencesService (from RuntimePlugin) that the pref. page uses.  
            // getting it from X10Plugin returns a different one.
            IPreferencesService prefService = RuntimePlugin.getInstance().getPreferencesService();
            
            optsList.add(0, "-BAD_PLACE_RUNTIME_CHECK="+(prefService.getBooleanPreference(X10PreferenceConstants.P_BAD_PLACE_CHECK)));
            optsList.add(0, "-LOOP_OPTIMIZATIONS="+(prefService.getBooleanPreference(X10PreferenceConstants.P_LOOP_OPTIMIZATIONS)));
            optsList.add(0, "-ARRAY_OPTIMIZATIONS="+(prefService.getBooleanPreference(X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS)));
            if (prefService.getBooleanPreference(X10PreferenceConstants.P_ASSERT)) {
                optsList.add(0, "-assert");
            }

            if (prefService.isDefined(X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS)) {
                String optionString = prefService.getStringPreference(X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS);
                String[] options = optionString.split("\\s");
                int extraOptionsPos=0;
                for (String s: options) {
                    if (s!=null) {
                        optsList.add(extraOptionsPos++, s);  // FIXME refactor this and do stuff in the order it belongs in the first place
                    }
                }
            }

            String[] optsArray = optsList.toArray(new String[optsList.size()]);
            opts.parseCommandLine(optsArray, new HashSet());
        } catch (UsageError e) {
            if (!e.getMessage().equals("must specify at least one source file"))
                System.err.println(e.getMessage());
        } catch (JavaModelException e) {
            X10Plugin.getInstance().writeErrorMsg("Unable to determine project source folder location for " + fProject.getName());
        }
        X10Plugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
        X10Plugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
        X10Plugin.getInstance().maybeWriteInfoMsg("Compiler templates = " + Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY);
        X10Plugin.getInstance().maybeWriteInfoMsg("Output directory = " + opts.output_directory);
    }

    private String pathListToPathString(List<IPath> pathList) {
        StringBuffer buff= new StringBuffer();
        IPath wsLoc= fProject.getWorkspace().getRoot().getLocation();

        for(Iterator<IPath> iter= pathList.iterator(); iter.hasNext();) {
            IPath path= iter.next();

            if (wsLoc.isPrefixOf(path)) {
            	buff.append(wsLoc.append(path).toOSString());
            } else {
            	buff.append(path.toOSString());
            }
            if (iter.hasNext())
                buff.append(File.pathSeparatorChar);
        }
        return buff.toString();
    }

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
                srcPath.add(e.getPath());
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
            	if (path.toPortableString().contains(X10Plugin.X10_RUNTIME_BUNDLE_ID)) {
            		srcPath.add(path);
            	}
            }
        }
        if (srcPath.size() == 0)
            srcPath.add(fProject.getLocation());
        return srcPath;
    }

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
            	System.out.println(msg+"\n   "+errorPos);
            	addProblemMarkerTo(fProject, msg, IMarker.SEVERITY_ERROR, IMarker.PRIORITY_NORMAL);
            } else {
            	int severity= (errorInfo.getErrorKind() == ErrorInfo.WARNING ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR);

            	if (errorPos == Position.COMPILER_GENERATED)
            		X10Plugin.getInstance().writeErrorMsg(errorInfo.getMessage());
            	else
            		addProblemMarkerTo(errorFile, errorInfo.getMessage(), severity, errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line(), errorPos
                        .offset(), errorPos.endOffset());
            }
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

    private List<Source> collectStreamSources(Collection<IFile> sourceFiles) {
        List<Source> streams= new ArrayList<Source>();

        for(Iterator<IFile> it= sourceFiles.iterator(); it.hasNext(); ) {
            IFile sourceFile= it.next();

            try {
                final String filePath= sourceFile.getLocation().toOSString();
                StreamSource srcStream= new StreamSource(sourceFile.getContents(), filePath);

                streams.add(srcStream);
            } catch (IOException e) {
                X10Plugin.getInstance().writeErrorMsg("Unable to open source file '" + sourceFile.getLocation() + ": " + e.getMessage());
            } catch (CoreException e) {
                X10Plugin.getInstance().writeErrorMsg("Unable to open source file '" + sourceFile.getLocation() + ": " + e.getMessage());
            }
        }
        return streams;
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
//          if (X10Preferences.autoAddRuntime) {
//              String commonPath= X10Plugin.x10CommonPath;
//              String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separator) + 1) + "x10.runtime" + File.separator + "classes";
//
//              if (classPath.length > 0)
//                buff.append(';');
//              buff.append(runtimePath);
//          }
        } catch (JavaModelException e) {
            X10Plugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
        }
        return buff.toString();
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

    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        final String[] buildKind= { "0", "1", "2", "3", "4", "5", "Full", "7", "8", "Auto", "Incremental", "11", "12", "13", "14", "Clean" };

        fProject= getProject();
        fX10Project= JavaCore.create(fProject);

        X10Plugin.getInstance().maybeWriteInfoMsg("build kind = " + buildKind[kind]);

        if (fDependencyInfo == null)
            fDependencyInfo= new PolyglotDependencyInfo(fProject);

        fMonitor= monitor;
        if (sPlugin == null)
            sPlugin= X10Plugin.getInstance();

        // Refresh prefs every time so that changes take effect on the next build.
        sPlugin.refreshPrefs();

        if (X10Plugin.x10CompilerPath.equals("???")) {
            postMsgDialog("X10 Error", "X10 common directory location not yet set.");
            return null;
        }
        readCompilerConfig();
        checkClasspathForRuntime();
        if (kind == CLEAN_BUILD || kind == FULL_BUILD)
            fDependencyInfo.clearAllDependencies();
        fSourcesToCompile.clear();

        fMonitor.beginTask("Scanning and compiling X10 source files...", 0);
        collectSourcesToCompile();
        cleanGeneratedFiles();

        Collection<IProject> dependents= doCompile();

//      fDependencyInfo.dump();
        fMonitor.done();
        return (IProject[]) dependents.toArray(new IProject[dependents.size()]);
    }

    private void cleanGeneratedFiles() {
        IWorkspace ws= ResourcesPlugin.getWorkspace();
        IWorkspaceRoot wsRoot= ws.getRoot();
        final List<IFile> genFiles= new ArrayList<IFile>();
		boolean traceOn=true;
        for(IFile srcFile: fSourcesToCompile) {
        	if(traceOn)System.out.println("srcFile: "+srcFile);
            IPath genJavaFile= srcFile.getFullPath().removeFileExtension().addFileExtension("java");
            if(traceOn)System.out.println("genJavaFile: "+genJavaFile);
            IPath genFileFolder= srcFile.getFullPath().removeLastSegments(1);
            if(traceOn)System.out.println("genFileFolder: "+genFileFolder);

            genFiles.add(wsRoot.getFile(genJavaFile));
        }

        IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) {
                IStatus status= null;
                monitor.beginTask("Clearing generated files", genFiles.size());
                for(IFile file: genFiles) {
                    try {
                        file.delete(true, monitor);
                    } catch (CoreException e) {
                        if (status == null) {
                            status= new Status(IStatus.ERROR, X10Plugin.kPluginID, e.getLocalizedMessage());
                        } else if (status instanceof MultiStatus) {
                            MultiStatus ms= (MultiStatus) status;
                            ms.add(new Status(IStatus.ERROR, X10Plugin.kPluginID, e.getLocalizedMessage()));
                        } else {
                            IStatus newStat= new Status(IStatus.ERROR, X10Plugin.kPluginID, e.getLocalizedMessage());
                            status= new MultiStatus(X10Plugin.kPluginID, IStatus.ERROR, new IStatus[] { status, newStat }, "Multiple errors occurred", null);
                        }
                    }
                    monitor.worked(1);
                }
                monitor.done();
            }
        };
        try {
            ws.run(runnable, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
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

    private final class OpenProjectPropertiesHelper implements Runnable {
        public void run() {
            // Open the project properties dialog and go to the "Java Build Path" page
            PreferenceDialog d= PreferencesUtil.createPropertyDialogOn(null, getProject(), BuildPathsPropertyPage.PROP_ID, null, null);

            d.open();
        }
    }

    private boolean fSuppressClasspathWarnings= false;

    private class MaybeSuppressFutureClasspathWarnings implements Runnable {
        public void run() {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    postQuestionDialog("Classpath error", "Suppress future classpath warnings",
                            new Runnable() {
                                public void run() {
                                    fSuppressClasspathWarnings= true;
                                }
                            }, null);
                }
            });
        }
    }

    private class UpdateProjectClasspathHelper implements Runnable {
        public void run() {
            updateProjectClasspath();
        }
    }





                                                                                                                                                                                                  

   



    private void updateProjectClasspath() {
        try {
            IClasspathEntry[] entries= fX10Project.getRawClasspath();
            List<Integer> runtimeIndexes= X10RuntimeUtils.findAllX10RuntimeClasspathEntries(entries);//PORT1.7 moved to X10runtimeUtils
            IPath languageRuntimePath= X10RuntimeUtils.getLanguageRuntimePath();//PORT1.7 moved to X10runtimeUtils
            IClasspathEntry newEntry;
            if (languageRuntimePath == null) {
                return;
            }
            if (languageRuntimePath.isAbsolute()) {
                newEntry= JavaCore.newLibraryEntry(languageRuntimePath, null, null);
            } else {
                newEntry= JavaCore.newVariableEntry(languageRuntimePath, null, null);
            }
            IClasspathEntry[] newEntries;

            if (runtimeIndexes.size() == 0) { // no entry, broken or otherwise
                newEntries= new IClasspathEntry[entries.length + 1];
                System.arraycopy(entries, 0, newEntries, 0, entries.length);
                newEntries[entries.length]= newEntry;
            } else {
                newEntries= new IClasspathEntry[entries.length - runtimeIndexes.size() + 1];
                int idx= 0;
                for(int i=0; i < entries.length; i++) {
                    if (!runtimeIndexes.contains(i)) {
                        newEntries[idx++]= entries[i];
                    }
                }
                newEntries[idx]= newEntry;
            }
            fX10Project.setRawClasspath(newEntries, new NullProgressMonitor());
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the project's classpath to make sure that an X10 runtime is available,
     * and warns the user if not.
     */
    private void checkClasspathForRuntime() {// BRT put in utils too later
        if (fSuppressClasspathWarnings)
            return;
        try {
            IClasspathEntry[] entries= fX10Project.getResolvedClasspath(true); // PORT1.7 --  use getRawClasspath() here? Note for Bob?
            int runtimeIdx= X10RuntimeUtils.findValidX10RuntimeClasspathEntry(entries);//PORT1.7 moved to RuntimeUtils

            if (runtimeIdx >= 0) {
                IPath entryPath= entries[runtimeIdx].getPath();
                File entryFile= new File(entryPath.makeAbsolute().toOSString());

                if (!entryFile.exists()) {
                    postQuestionDialog(ClasspathError + fProject.getName(),
                            "The X10 runtime entry in the project's classpath does not exist: " + entryPath.toOSString() + "; shall I update the project's classpath with the runtime installed as part of the X10DT?",
                            new UpdateProjectClasspathHelper(),
                            new MaybeSuppressFutureClasspathWarnings());
                    return; // found a runtime entry but it is/was broken
                }
                String currentVersion= X10RuntimeUtils.getCurrentRuntimeVersion();

                // TODO Only insist that a jar file whose name embeds the version has the right version.
                // Jar files whose names don't embed a version number won't be checked.
                if (entryFile.getPath().endsWith(".jar") && entryFile.getAbsolutePath().indexOf(currentVersion) < 0) {
                    postQuestionDialog(ClasspathError + fProject.getName(),
                            "The X10 runtime entry " + entryPath.toOSString() + " in the classpath of project '" + fProject.getName() + "' is an old runtime version; shall I update the project's classpath with the runtime installed as part of the X10DT (version " + currentVersion + ")?",
                            new UpdateProjectClasspathHelper(),
                            new MaybeSuppressFutureClasspathWarnings());
                }
                return; // found the runtime
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        postQuestionDialog(ClasspathError + fProject.getName(),
                "No X10 runtime entry exists in the classpath of project '" + fProject.getName() + "'; shall I update the project's classpath with the runtime installed as part of the X10DT?",
                new UpdateProjectClasspathHelper(),
                new MaybeSuppressFutureClasspathWarnings());
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
                Shell shell= X10Plugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();

                MessageDialog.openInformation(shell, title, msg);
            }
        });
    }

    /**
     * Posts a dialog displaying the given message as soon as "conveniently possible".
     * This is not a synchronous call, since this method will get called from a
     * different thread than the UI thread, which is the only thread that can
     * post the dialog box.
     */
    private void postQuestionDialog(final String title, final String query, final Runnable runIfYes, final Runnable runIfNo) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell shell= X10Plugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
                boolean response= MessageDialog.openQuestion(shell, title, query);

                if (response)
                    runIfYes.run();
                else if (runIfNo != null)
                    runIfNo.run();
            }
        });
    }

    private Collection<IProject> doCompile() throws CoreException {
        if (!fSourcesToCompile.isEmpty()) {
        	System.out.println("X10Builder.doCompile() fSourcesToCompile: "+fSourcesToCompile);
            // RMF 8/5/2008 - Don't clear the dependency info right away - if Polyglot fails to
            // get to some source file on the list, it'll end up with no dependency info. Better
            // to just leave the dependency info untouched. So, instead: clear a file's dependency
            // info just before recomputing it (i.e. in the ComputeDependenciesVisitor).
//    	    clearDependencyInfoForChangedFiles();
    	    invokeX10C(fSourcesToCompile);
    	    // Now do a refresh to make sure the Java compiler sees the Java
    	    // source files that Polyglot just created.

            // Bug #516: For some reason, when someone goes to export the generated class files,
            // the class files are out of sync with the workspace, even though we're using the
            // JDT compiler to create them. So instead of sync'ing individual source folders,
            // just sync the whole project.

//          List<IPath> projectSrcPath= getProjectSrcPath();
//    	    for(Iterator<IPath> iter= projectSrcPath.iterator(); iter.hasNext(); ) {
//    	        IPath pathEntry= iter.next();
//
//    	        if (pathEntry.segmentCount() == 1)
//        		    // Work around Eclipse 3.1.0 bug 101733: gives spurious exception
//        		    // if folder refers to project itself (happens when a Java project
//        		    // is configured not to use separate src/bin folders).
//        		    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=101733
//        		    fProject.refreshLocal(IResource.DEPTH_INFINITE, fMonitor);
//        		else {
////        		    fProject.getWorkspace().getRoot().getFolder(pathEntry).refreshLocal(IResource.DEPTH_INFINITE, fMonitor);
//        		}
//    	    }
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
            X10Plugin.getInstance().logException("Unable to resolve X10 project classpath", e);
        }
        return dependentProjects;
    }

//    private void clearDependencyInfoForChangedFiles() {
//        for(Iterator<IFile> iter= fSourcesToCompile.iterator(); iter.hasNext(); ) {
//            IFile srcFile= iter.next();
//
//            fDependencyInfo.clearDependenciesOf(srcFile.getFullPath().toString());
//        }
//    }

    private void dumpSourceList(Collection<IFile> sourcesToCompile) {
        for(Iterator<IFile> iter= sourcesToCompile.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();

            System.out.println("  " + srcFile.getFullPath());
        }
    }

    private void collectChangeDependents() {
        Collection<IFile> changeDependents= new ArrayList<IFile>();
        IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

//      System.out.println("Changed files:");
//      dumpSourceList(fSourcesToCompile);
        for(Iterator<IFile> iter= fSourcesToCompile.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();
            Set<String> fileDependents= fDependencyInfo.getDependentsOf(srcFile.getFullPath().toString());

            if (fileDependents != null) {
                for(Iterator<String> iterator= fileDependents.iterator(); iterator.hasNext(); ) {
                    String depPath= iterator.next();
                    IFile depFile= wsRoot.getFile(new Path(depPath));

                    changeDependents.add(depFile);
                }
            }
        }
        fSourcesToCompile.addAll(changeDependents);
//      System.out.println("Changed files + dependents:");
//      dumpSourceList(fSourcesToCompile);
    }

    private void collectSourcesToCompile() throws CoreException {
        collectSourceFolders();

        IResourceDelta delta= getDelta(fProject);
        System.out.println("fSourcesToCompile="+fSourcesToCompile);

        if (delta != null) {
            X10Plugin.getInstance().maybeWriteInfoMsg("==> Scanning resource delta for project '" + fProject.getName() + "'... <==");
            delta.accept(fDeltaVisitor);
            X10Plugin.getInstance().maybeWriteInfoMsg("X10 delta scan completed for project '" + fProject.getName() + "'...");
        } else {
            X10Plugin.getInstance().maybeWriteInfoMsg("==> Scanning for X10 source files in project '" + fProject.getName() + "'... <==");
            fProject.accept(fResourceVisitor);
            X10Plugin.getInstance().maybeWriteInfoMsg("X10 source file scan completed for project '" + fProject.getName() + "'...");
        }
        System.out.println("fSourcesToCompile="+fSourcesToCompile);// OK here
        collectChangeDependents();
        collectFilesWithErrors();
    }

    private class X10ErrorVisitor implements IResourceVisitor {
        private boolean fCompileAll= false;
        public boolean visit(IResource res) throws CoreException {
            return processResource(res);
        }
        protected boolean processResource(IResource resource) throws CoreException {
            if (resource instanceof IFile) {
                IFile file= (IFile) resource;
                if (isSourceFile(file) && (fCompileAll || file.findMaxProblemSeverity(PROBLEMMARKER_ID, true, IResource.DEPTH_INFINITE) == IMarker.SEVERITY_ERROR)) {
                    if (!fSourcesToCompile.contains(file)) {
                        fSourcesToCompile.add(file);
                    }
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
            X10Plugin.getInstance().logException("Error while looking for source files with errors", e);
        }
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
                    X10Plugin.getInstance().maybeWriteInfoMsg("Source classpath entry refers to another project: " + entryPath);
                    continue;
                }
                fSrcFolderPaths.add(entryPath.removeFirstSegments(1));
            }
        }
    }

    public String toString() {
        return "X10 Builder for project " + fProject.getName();
    }
}
