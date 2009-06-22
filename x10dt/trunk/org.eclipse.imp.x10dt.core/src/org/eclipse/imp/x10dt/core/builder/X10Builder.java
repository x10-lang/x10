package org.eclipse.imp.x10dt.core.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.x10dt.core.X10Plugin;
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
import org.osgi.framework.Bundle;

import polyglot.ext.x10.Configuration;
import polyglot.frontend.Compiler;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.parser.X10Parser.JPGPosition;

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
    private Collection/* <IFile> */fSourcesToCompile= new ArrayList();
    private ExtensionInfo fExtInfo;
    private static PluginBase sPlugin= null;
    protected PolyglotDependencyInfo fDependencyInfo;

    private Collection/*<IPath>*/ fSrcFolderPaths; // project-relative paths

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

	for(Iterator iter= fSrcFolderPaths.iterator(); iter.hasNext(); ) {
	    IPath srcFolderPath= (IPath) iter.next();

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

    protected void clearMarkersOn(Collection/* <IFile> */sources) {
	for(Iterator iter= sources.iterator(); iter.hasNext(); ) {
	    IFile file= (IFile) iter.next();

	    try {
		file.deleteMarkers(PROBLEMMARKER_ID, true, IResource.DEPTH_INFINITE);
	    } catch (CoreException e) {
	    }
	}
    }

    protected void addMarkerTo(IFile sourceFile, String msg, int severity, String loc, int priority, int lineNum, int startOffset, int endOffset) {
	try {
	    IMarker marker= sourceFile.createMarker(PROBLEMMARKER_ID);

	    marker.setAttribute(IMarker.MESSAGE, msg);
	    marker.setAttribute(IMarker.SEVERITY, severity);
	    marker.setAttribute(IMarker.LOCATION, loc);
	    marker.setAttribute(IMarker.PRIORITY, priority);
	    marker.setAttribute(IMarker.LINE_NUMBER, (lineNum >= 0) ? lineNum : 0);
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

    /**
     * Run the X10 compiler on the given Collection of source IFile's.
     * @param sources
     */
    private void invokeX10C(Collection/* <IFile> */sources) {
	X10Plugin.getInstance().maybeWriteInfoMsg("Running X10C on source file set '" + fileSetToString(sources) + "'...");
	clearMarkersOn(sources);
	compileAllSources(sources);
	X10Plugin.getInstance().maybeWriteInfoMsg("X10C completed on source file set.");
    }

    // TODO This goal should probably prereq Disambiguated instead of CodeGenerated,
    // and should be made a prereq of TypeChecked, so that dependency information
    // gets collected even if code can't be generated for some reason.
    private class ComputeDependenciesGoal extends VisitorGoal {
	public ComputeDependenciesGoal(Job job) throws CyclicDependencyException {
	    super(job, new ComputeDependenciesVisitor(job, job.extensionInfo().typeSystem(), fDependencyInfo));
	    addPrerequisiteGoal(job.extensionInfo().scheduler().CodeGenerated(job), job.extensionInfo().scheduler());
	}
    }

    private final class BuilderExtensionInfo extends polyglot.ext.x10.ExtensionInfo {
	public Goal getCompileGoal(Job job) {
	    try {
		return scheduler().internGoal(new ComputeDependenciesGoal(job));
	    } catch (CyclicDependencyException e) {
		job.compiler().errorQueue().enqueue(
			new ErrorInfo(ErrorInfo.INTERNAL_ERROR, "Cyclic dependency exception: " + e.getMessage(), Position.COMPILER_GENERATED));
		return null;
	    }
	}
    }

    private void compileAllSources(Collection/* <IFile> */sourceFiles) {
	fExtInfo= new BuilderExtensionInfo();

	List/* <SourceStream> */streams= collectStreamSources(sourceFiles);
	final Collection/* <ErrorInfo> */errors= new ArrayList();

	buildOptions();

	Compiler compiler= new PolyglotFrontEnd(fExtInfo, new AbstractErrorQueue(1000000, fExtInfo.compilerName()) {
	    protected void displayError(ErrorInfo error) {
		errors.add(error);
	    }
	});
//	Report.addTopic(Report.verbose, 1);
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
		    addMarkerTo(errorFile, "Probable missing package declaration", IMarker.SEVERITY_ERROR, "", IMarker.PRIORITY_NORMAL, 0, 0, 0);
	    }
	} catch (final Error error) {
	    String msg= error.getMessage();
	    if (msg.startsWith("No translation for ")) {
		final String type= msg.substring(19).substring(0, msg.lastIndexOf(' ')-19);

		postMsgDialog("X10 Compiler Configuration error", "Unable to locate compiler template for " + type + "; check X10 Preferences.");
	    }
	} catch (Exception e) {
	    X10Plugin.getInstance().writeErrorMsg("Internal X10 compiler error: " + e.getMessage());
	}
	fDependencyInfo.dump();
	createMarkers(errors);
    }

    private void buildOptions() {
	Options opts= fExtInfo.getOptions();

	Options.global= opts;
	try {
	    List/*<IPath>*/ projectSrcLoc= getProjectSrcPath();
	    String projectSrcPath= pathListToPathString(projectSrcLoc);
	    String outputDir= fProject.getWorkspace().getRoot().getLocation().append((IPath) projectSrcLoc.get(0)).toOSString(); // HACK: just take 1st directory as output

	    // TODO RMF 11/9/2006 - Remove the "-noserial" option; it's really for the demo
	    opts.parseCommandLine(new String[] { "-assert", "-noserial", "-cp", buildClassPathSpec(), "-d", outputDir, "-sourcepath", projectSrcPath }, new HashSet());
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

    private String pathListToPathString(List/*<IPath>*/ pathList) {
	StringBuffer buff= new StringBuffer();

	for(Iterator iter= pathList.iterator(); iter.hasNext(); ) {
	    IPath path= (IPath) iter.next();

	    buff.append(fProject.getWorkspace().getRoot().getLocation().append(path).toOSString());
	    if (iter.hasNext())
		buff.append(';');
	}
	return buff.toString();
    }

    /**
     * @return a list of all project-relative CPE_SOURCE-type classpath entries.
     * @throws JavaModelException
     */
    private List/*<IPath>*/ getProjectSrcPath() throws JavaModelException {
	List/* <IPath> */srcPath= new ArrayList();
	IJavaProject javaProject= JavaCore.create(fProject);
	IClasspathEntry[] classPath= javaProject.getResolvedClasspath(true);

	for(int i= 0; i < classPath.length; i++) {
	    IClasspathEntry e= classPath[i];

	    if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE)
		srcPath.add(e.getPath());
	}
	if (srcPath.size() == 0)
	    srcPath.add(fProject.getLocation());
	return srcPath;
    }

    private void createMarkers(final Collection/* <ErrorInfo> */errors) {
	final IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

	for(Iterator iter= errors.iterator(); iter.hasNext(); ) {
	    ErrorInfo errorInfo= (ErrorInfo) iter.next();
	    Position errorPos= errorInfo.getPosition();

	    if (errorPos == null)
		continue;

	    IFile errorFile= wsRoot.getFileForLocation(new Path(errorPos.file()));

	    if (errorFile == null)
		errorFile= findFileInSrcPath(errorPos.file(), wsRoot);

	    int severity= (errorInfo.getErrorKind() == ErrorInfo.WARNING ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR);

	    if (errorPos == Position.COMPILER_GENERATED)
		X10Plugin.getInstance().writeErrorMsg(errorInfo.getMessage());
	    else if (errorPos instanceof JPGPosition)
		addMarkerTo(errorFile, errorInfo.getMessage(), severity, errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line(),
			((JPGPosition) errorPos).getLeftIToken().getStartOffset(), ((JPGPosition) errorPos).getRightIToken().getEndOffset());
	    else
		addMarkerTo(errorFile, errorInfo.getMessage(), severity, errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line(), -1, -1);
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

    private List/*<SourceStream>*/ collectStreamSources(Collection/* <IFile> */sourceFiles) {
	List/*<SourceStream>*/ streams= new ArrayList();

	for(Iterator/*<IFile>*/ it= sourceFiles.iterator(); it.hasNext(); ) {
	    IFile sourceFile= (IFile) it.next();

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
	    IClasspathEntry[] classPath= fX10Project.getResolvedClasspath(true);

	    for(int i= 0; i < classPath.length; i++) {
		IClasspathEntry entry= classPath[i];

		if (i > 0)
		    buff.append(";");
		buff.append(entry.getPath().toOSString());
	    }
//	    if (X10Preferences.autoAddRuntime) {
//		String commonPath= X10Plugin.x10CommonPath;
//		String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separator) + 1) + "x10.runtime" + File.separator + "classes";
//
//		if (classPath.length > 0)
//		    buff.append(';');
//		buff.append(runtimePath);
//	    }
	} catch (JavaModelException e) {
	    X10Plugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
	}
	return buff.toString();
    }

    private String fileSetToString(Collection/*<IFile>*/ sources) {
	StringBuffer buff= new StringBuffer();

	for(Iterator iter= sources.iterator(); iter.hasNext(); ) {
	    IFile file= (IFile) iter.next();

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
	// Read configuration at every compiler invocation, in case it changes (e.g. via
        // the X10 Preferences page). Theoretically, if the user really does change the
        // compiler configuration, we should trigger a full rebuild, but we don't yet.
        try {
            // The X10 configuration file's location is given by the value of the System
            // property "x10.configuration", which is initialized by X10Plugin.refreshPrefs()
            // and by a preference store listener in X10PreferencePage.
            Configuration.readConfiguration(Configuration.class, System.getProperty("x10.configuration"));
        } catch (x10.runtime.util.ConfigurationError e) {
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
        checkClasspathForRuntime();
	if (kind == CLEAN_BUILD || kind == FULL_BUILD)
	    fDependencyInfo.clearAllDependencies();
	fSourcesToCompile.clear();

	fMonitor.beginTask("Scanning and compiling X10 source files...", 0);
	collectSourcesToCompile();

	Collection/* <IProject> */dependents= doCompile();

	fMonitor.done();
	return (IProject[]) dependents.toArray(new IProject[dependents.size()]);
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

    protected IPath getLanguageRuntimePath() {
        Bundle x10RuntimeBundle= Platform.getBundle("x10.runtime");
        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
        IPath x10RuntimePath= new Path("ECLIPSE_HOME/plugins/x10.runtime_" + bundleVersion + ".jar");

        return x10RuntimePath;
    }

    protected String getCurrentRuntimeVersion() {
        Bundle x10RuntimeBundle= Platform.getBundle("x10.runtime");
        String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");

        return bundleVersion;
    }

    private int findX10RuntimeClasspathEntry(IClasspathEntry[] entries) throws JavaModelException {
        for(int i= 0; i < entries.length; i++) {
            IClasspathEntry entry= entries[i];

            if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY || entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
                IPath entryPath= entry.getPath();

                if (entryPath.lastSegment().indexOf("x10.runtime") >= 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void updateProjectClasspath() {
        try {
            IClasspathEntry[] entries= fX10Project.getRawClasspath();
            int runtimeIdx= findX10RuntimeClasspathEntry(entries);
            IPath languageRuntimePath= getLanguageRuntimePath();
            IClasspathEntry newEntry= JavaCore.newVariableEntry(languageRuntimePath, null, null);
            IClasspathEntry[] newEntries;

            if (runtimeIdx < 0) { // no entry, broken or otherwise
                newEntries= new IClasspathEntry[entries.length + 1];
                System.arraycopy(entries, 0, newEntries, 0, entries.length);
                newEntries[entries.length]= newEntry;
            } else {
                newEntries= entries;
                newEntries[runtimeIdx]= newEntry;
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
    private void checkClasspathForRuntime() {
        if (fSuppressClasspathWarnings)
            return;
        try {
            IClasspathEntry[] entries= fX10Project.getResolvedClasspath(true);
            int runtimeIdx= findX10RuntimeClasspathEntry(entries);

            if (runtimeIdx >= 0) {
                IPath entryPath= entries[runtimeIdx].getPath();
                File jarFile= new File(entryPath.makeAbsolute().toOSString());

                if (!jarFile.exists()) {
                    postQuestionDialog(ClasspathError + fProject.getName(),
                            "X10 runtime entry in classpath does not exist: " + entryPath.toOSString() + "; update project classpath with default runtime?",
                            new UpdateProjectClasspathHelper(),
                            new MaybeSuppressFutureClasspathWarnings());
                    return; // found a runtime entry but it is/was broken
                }
                String currentVersion= getCurrentRuntimeVersion();

                if (!jarFile.getAbsolutePath().endsWith(currentVersion + ".jar")) {
                    postQuestionDialog(ClasspathError + fProject.getName(),
                            "X10 runtime entry in classpath is out of date: " + entryPath.toOSString() + "; update project classpath with latest runtime?",
                            new UpdateProjectClasspathHelper(),
                            new MaybeSuppressFutureClasspathWarnings());
                }
                return; // found the runtime
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        postQuestionDialog(ClasspathError + fProject.getName(),
                "No X10 runtime entry in classpath of project '" + fProject.getName() + "'; update project classpath with default runtime?",
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

    private Collection doCompile() throws CoreException {
	if (!fSourcesToCompile.isEmpty()) {
	    clearDependencyInfoForChangedFiles();
	    invokeX10C(fSourcesToCompile);
	    // Now do a refresh to make sure the Java compiler sees the Java
	    // source files that Polyglot just created.
	    List/*<IPath>*/ projectSrcPath= getProjectSrcPath();

	    for(Iterator iter= projectSrcPath.iterator(); iter.hasNext(); ) {
		IPath pathEntry= (IPath) iter.next();
		
		if (pathEntry.segmentCount() == 1)
		    // Work around Eclipse 3.1.0 bug 101733: gives spurious exception
		    // if folder refers to project itself (happens when a Java project
		    // is configured not to use separate src/bin folders).
		    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=101733
		    fProject.refreshLocal(IResource.DEPTH_INFINITE, fMonitor);
		else
		    fProject.getWorkspace().getRoot().getFolder(pathEntry).refreshLocal(IResource.DEPTH_INFINITE, fMonitor);
	    }
	}
	// TODO Compute set of dependent projects
	return Collections.EMPTY_LIST;
    }

    private void clearDependencyInfoForChangedFiles() {
	for(Iterator iter= fSourcesToCompile.iterator(); iter.hasNext(); ) {
	    IFile srcFile= (IFile) iter.next();

	    fDependencyInfo.clearDependenciesOf(srcFile.getFullPath().toString());
	}
    }

    private void dumpSourceList(Collection/* <IFile> */sourcesToCompile) {
	for(Iterator iter= sourcesToCompile.iterator(); iter.hasNext(); ) {
	    IFile srcFile= (IFile) iter.next();

	    System.out.println("  " + srcFile.getFullPath());
	}
    }

    private void collectChangeDependents() {
	Collection changeDependents= new ArrayList();
	IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

	System.out.println("Changed files:");
	dumpSourceList(fSourcesToCompile);
	for(Iterator iter= fSourcesToCompile.iterator(); iter.hasNext(); ) {
	    IFile srcFile= (IFile) iter.next();
	    Set/* <String path> */fileDependents= fDependencyInfo.getDependentsOf(srcFile.getFullPath().toString());

	    if (fileDependents != null) {
		for(Iterator iterator= fileDependents.iterator(); iterator.hasNext(); ) {
		    String depPath= (String) iterator.next();
		    IFile depFile= wsRoot.getFile(new Path(depPath));

		    changeDependents.add(depFile);
		}
	    }
	}
	fSourcesToCompile.addAll(changeDependents);
	System.out.println("Changed files + dependents:");
	dumpSourceList(fSourcesToCompile);
    }

    private void collectSourcesToCompile() throws CoreException {
	collectSourceFolders();

	IResourceDelta delta= getDelta(fProject);

	if (delta != null) {
	    X10Plugin.getInstance().maybeWriteInfoMsg("==> Scanning resource delta for project '" + fProject.getName() + "'... <==");
	    delta.accept(fDeltaVisitor);
	    X10Plugin.getInstance().maybeWriteInfoMsg("X10 delta scan completed for project '" + fProject.getName() + "'...");
	} else {
	    X10Plugin.getInstance().maybeWriteInfoMsg("==> Scanning for X10 source files in project '" + fProject.getName() + "'... <==");
	    fProject.accept(fResourceVisitor);
	    X10Plugin.getInstance().maybeWriteInfoMsg("X10 source file scan completed for project '" + fProject.getName() + "'...");
	}
	collectChangeDependents();
    }

    /**
     * Collects the set of source folders in this project, to be used in filtering out
     * files in non-src folders that the builder should not attempt to compile.
     * @see isSourceFile()
     */
    private void collectSourceFolders() throws JavaModelException {
	fSrcFolderPaths= new HashSet();
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
}
