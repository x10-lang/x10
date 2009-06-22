package com.ibm.watson.safari.x10;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uide.runtime.UIDEPluginBase;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import x10.parser.X10Parser.JPGPosition;

public class X10Builder extends IncrementalProjectBuilder {
    /**
     * Builder ID for the X10 compiler. Must match the ID of the builder extension
     * defined in plugin.xml.
     */
    public static final String BUILDER_ID= X10Plugin.kPluginID + ".X10Builder";

    /**
     * Problem marker ID for X10 compiler errors/warnings/infos. Must match the
     * ID of the marker extension defined in plugin.xml.
     */
    public static final String PROBLEMMARKER_ID= X10Plugin.kPluginID + ".problemMarker";

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

    private Collection/*<IFile>*/ fSourcesToCompile= new ArrayList();

    private ExtensionInfo fExtInfo;

    private static UIDEPluginBase sPlugin= null;

    public X10Builder() { }

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

	return file.exists() && exten != null && exten.compareTo("x10") == 0;
    }

    private boolean isBinaryFolder(IResource resource) {
	return resource.getFullPath().lastSegment().equals("bin");
    }

    protected void clearMarkersOn(Collection/*<IFile>*/ sources) {
	for(Iterator iter= sources.iterator(); iter.hasNext();) {
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
	    marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
	    if (startOffset >= 0) {
		marker.setAttribute(IMarker.CHAR_START, startOffset);
		marker.setAttribute(IMarker.CHAR_END, endOffset+1);
	    }
	} catch (CoreException e) {
	    X10Plugin.getInstance().writeErrorMsg("Couldn't add marker to file " + sourceFile);
	}
    }

    /**
     * Run the X10 compiler on the given source file.
     * @param file
     */
    private void invokeX10C(Collection/*<IFile>*/ sources) {
	X10Plugin.getInstance().maybeWriteInfoMsg("Running X10C on source file set '" + fileSetToString(sources) + "'...");
	clearMarkersOn(sources);
	compileAllSources(sources);
	X10Plugin.getInstance().maybeWriteInfoMsg("X10C completed on source file set.");
    }

    private void compileAllSources(Collection/*<IFile>*/ sourceFiles) {
	fExtInfo= new polyglot.ext.x10.ExtensionInfo();

	List/*<SourceStream>*/ streams= collectStreamSources(sourceFiles);
	final Collection/*<ErrorInfo>*/ errors= new ArrayList();

	buildOptions();

	Compiler compiler= new PolyglotFrontEnd(fExtInfo, new AbstractErrorQueue(1000000, fExtInfo.compilerName()) {
	    protected void displayError(ErrorInfo error) {
		errors.add(error);
	    }
	});

//	Report.addTopic(Report.verbose, 1);
	try {
	    compiler.compile(streams);
	} catch (Exception e) {
	    X10Plugin.getInstance().writeErrorMsg("Internal X10 compiler error: " + e.getMessage());
	}
	createMarkers(errors);
    }

    private void buildOptions() {
	Options opts= fExtInfo.getOptions();

	Options.global= opts;
	try {
	    IPath projectSrcLoc= getProjectSrcPath();
	    String projectSrcPath= fProject.getWorkspace().getRoot().getLocation().append(projectSrcLoc).toOSString();

	    opts.parseCommandLine(new String[] {
		    "-cp", buildClassPathSpec(),
		    "-d", projectSrcPath
	    }, new HashSet());
	} catch (UsageError e) {
	    if (!e.getMessage().equals("must specify at least one source file"))
		System.err.println(e.getMessage());
	} catch (JavaModelException e) {
	    X10Plugin.getInstance().writeErrorMsg("Unable to determine project source folder location for " + fProject.getName());
	}
    }

    /**
     * @return the project-relative path of the first CPE_SOURCE-type classpath entry.
     * @throws JavaModelException
     */
    private IPath getProjectSrcPath() throws JavaModelException {
	IJavaProject javaProject= JavaCore.create(fProject);
	IClasspathEntry[] classPath= javaProject.getResolvedClasspath(true);

	for(int i= 0; i < classPath.length; i++) {
	    IClasspathEntry e= classPath[i];

	    if (e.getEntryKind() == IClasspathEntry.CPE_SOURCE)
		return e.getPath();
	}
	return fProject.getLocation();
    }

    private void createMarkers(final Collection/*<ErrorInfo>*/ errors) {
	final IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();

	for(Iterator iter= errors.iterator(); iter.hasNext(); ) {
	    ErrorInfo errorInfo= (ErrorInfo) iter.next();
	    Position errorPos= errorInfo.getPosition();

	    if (errorPos == null) continue;

	    IFile errorFile= wsRoot.getFileForLocation(new Path(errorPos.file()));

	    if (errorPos == Position.COMPILER_GENERATED)
		X10Plugin.getInstance().writeErrorMsg(errorInfo.getMessage());
	    else if (errorPos instanceof JPGPosition)
		addMarkerTo(errorFile, errorInfo.getMessage(), errorInfo.getErrorKind(),
			errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line(),
			((JPGPosition) errorPos).getLeftIToken().getStartOffset(), ((JPGPosition) errorPos).getRightIToken().getEndOffset());
	    else
		addMarkerTo(errorFile, errorInfo.getMessage(), errorInfo.getErrorKind(),
			errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line(), -1, -1);
	}
    }

    private List/*<SourceStream>*/ collectStreamSources(Collection/*<IFile>*/ sourceFiles) {
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

		if (i > 0) buff.append(";");
		buff.append(entry.getPath().toOSString());
	    }
	} catch (JavaModelException e) {
	    X10Plugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
	}

	return buff.toString();
    }

    private String fileSetToString(Collection/*<IFile>*/ sources) {
	StringBuffer buff= new StringBuffer();

	for(Iterator iter= sources.iterator(); iter.hasNext();) {
	    IFile file= (IFile) iter.next();
	    buff.append(file.getFullPath());
	    if (iter.hasNext())
		buff.append(',');
	}
	return buff.toString();
    }

    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	fProject= getProject();
	fX10Project= JavaCore.create(fProject);
	fMonitor= monitor;

	if (sPlugin == null)
	    sPlugin= X10Plugin.getInstance();

	// Refresh prefs every time so that changes take effect on the next build.
	sPlugin.refreshPrefs();

	fSourcesToCompile.clear();
	fMonitor.beginTask("Scanning and compiling X10 source files...", 0);
	collectSourcesToCompile();

	Collection/*<IProject>*/ dependents= doCompile();

	fMonitor.done();

	return (IProject[]) dependents.toArray(new IProject[dependents.size()]);
    }

    private Collection doCompile() throws CoreException {
	if (!fSourcesToCompile.isEmpty()) {
	    invokeX10C(fSourcesToCompile);
	    // Now do a refresh to make sure the Java compiler sees the Java source files that Polyglot just created.
	    fProject.getWorkspace().getRoot().getFolder(getProjectSrcPath()).refreshLocal(IResource.DEPTH_INFINITE, fMonitor);
	}
	// TODO Compute set of dependent projects
	return Collections.EMPTY_LIST;
    }

    private void collectSourcesToCompile() throws CoreException {
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
    }
}
