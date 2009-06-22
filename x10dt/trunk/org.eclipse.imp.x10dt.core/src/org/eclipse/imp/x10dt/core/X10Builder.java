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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;

import com.ibm.domo.ast.java.translator.polyglot.PolyglotFrontEnd;
import com.ibm.domo.ast.java.translator.polyglot.StreamSource;

public class X10Builder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID= X10Plugin.kPluginID + ".X10Builder";

    public static final String PROBLEMMARKER_ID= X10Plugin.kPluginID + ".problemMarker";

    private final class X10DeltaVisitor implements IResourceDeltaVisitor {
	public boolean visit(IResourceDelta delta) throws CoreException {
	    // System.out.println("Visiting delta " + delta.toString() + " of kind " + k_kindNames[delta.getKind()] + ".");
	    if (isX10SourceFile(delta.getResource()))
		fSourcesToCompile.add(delta.getResource());
	    else if (delta.getResource().getFullPath().lastSegment().equals("bin"))
		return false;
	    return true;
	}
    }

    private class X10ResourceVisitor implements IResourceVisitor {
	public X10ResourceVisitor() { }

	public boolean visit(IResource res) throws CoreException {
	    if (isX10SourceFile(res)) {
		IFile file= (IFile) res;

		fSourcesToCompile.add(file);
		return false; // skip sub-resources
	    } else
		return true; // need to descend into sub-resources
	}
    }

    private IProject fProject;

    private IJavaProject fX10Project;

    private IProgressMonitor fMonitor;

    private X10ResourceVisitor fResourceVisitor= new X10ResourceVisitor();

    private final X10DeltaVisitor fDeltaVisitor= new X10DeltaVisitor();

    private Collection/*<IFile>*/ fSourcesToCompile= new ArrayList();

    private final ExtensionInfo fExtInfo= new polyglot.ext.x10.ExtensionInfo();

    private static Plugin sPlugin= null;

    public X10Builder() { }

    private boolean isX10SourceFile(IResource res) {
	String exten= res.getFileExtension();

	return res.exists() && res instanceof IFile && exten != null && exten.compareTo("x10") == 0;
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

    protected void addMarkerTo(IFile sourceFile, String msg, int severity, String loc, int priority, int lineNum) {
	try {
	    IMarker grammarMarker= sourceFile.createMarker(PROBLEMMARKER_ID);

	    grammarMarker.setAttribute(IMarker.MESSAGE, msg);
	    grammarMarker.setAttribute(IMarker.SEVERITY, severity);
	    grammarMarker.setAttribute(IMarker.LOCATION, loc);
	    grammarMarker.setAttribute(IMarker.PRIORITY, priority);
	    grammarMarker.setAttribute(IMarker.LINE_NUMBER, lineNum);
	} catch (CoreException e) {
	    X10Plugin.writeErrorMsg("Couldn't add marker to file " + sourceFile, BUILDER_ID);
	}
    }

    /**
     * Run the X10 compiler on the given source file.
     * @param file
     */
    private void invokeX10C(Collection/*<IFile>*/ sources) {
	X10Plugin.maybeWriteInfoMsg("Running X10C on source file set '" + fileSetToString(sources) + "'...", BUILDER_ID);
	clearMarkersOn(sources);
	compileAllSources(sources);
	X10Plugin.maybeWriteInfoMsg("X10C completed on source file set.", BUILDER_ID);
    }

    private void compileAllSources(Collection/*<IFile>*/ sourceFiles) {
	List/*<SourceStream>*/ streams= collectStreamSources(sourceFiles);
	final Collection/*<ErrorInfo>*/ errors= new ArrayList();

	buildOptions();

	Compiler compiler= new PolyglotFrontEnd(fExtInfo, new AbstractErrorQueue(1000000, fExtInfo.compilerName()) {
	    protected void displayError(ErrorInfo error) {
		errors.add(error);
	    }
	});

	Report.addTopic(Report.verbose, 5);
	compiler.compile(streams);
	createMarkers(errors);
    }

    private void buildOptions() {
	Options opts= fExtInfo.getOptions();

	try {
	    opts.parseCommandLine(new String[] { "-cp", buildClassPathSpec() }, new HashSet());
	} catch (UsageError e) {
	    // Assertions.UNREACHABLE("Error parsing classpath spec???");
	}
    }

    private void createMarkers(final Collection errors) {
	final IWorkspaceRoot wsRoot= fProject.getWorkspace().getRoot();
	for(Iterator iter= errors.iterator(); iter.hasNext(); ) {
	    ErrorInfo errorInfo= (ErrorInfo) iter.next();
	    Position errorPos= errorInfo.getPosition();
	    IFile errorFile= wsRoot.getFileForLocation(new Path(errorPos.file()));

	    addMarkerTo(errorFile, errorInfo.getErrorString(), errorInfo.getErrorKind(), errorPos.nameAndLineString(), IMarker.PRIORITY_NORMAL, errorPos.line());
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
		X10Plugin.writeErrorMsg("Unable to open source file '" + sourceFile.getLocation() + ": " + e.getMessage(), BUILDER_ID);
	    } catch (CoreException e) {
		X10Plugin.writeErrorMsg("Unable to open source file '" + sourceFile.getLocation() + ": " + e.getMessage(), BUILDER_ID);
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
	    X10Plugin.writeErrorMsg("Error resolving class path: " + e.getMessage(), BUILDER_ID);
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

    protected void scanForX10Sources(IProject proj) throws CoreException {
	X10Plugin.maybeWriteInfoMsg("==> Scanning for X10 source files in project '" + proj.getName() + "'... <==", BUILDER_ID);
	proj.accept(fResourceVisitor);
	X10Plugin.maybeWriteInfoMsg("X10 source file scan completed for project '" + proj.getName() + "'...", BUILDER_ID);
    }

    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	fProject= getProject();
	fX10Project= JavaCore.create(fProject);
	fMonitor= monitor;

	if (sPlugin == null)
	    sPlugin= X10Plugin.getInstance();

	// Refresh prefs every time so that changes take effect on the next build.
	X10Plugin.refreshPrefs();

	fSourcesToCompile.clear();
	collectSourcesToCompile();

	Collection/*<IProject>*/ dependents= doCompile();

	fMonitor.beginTask("Scanning and compiling X10 source files...", 0);
	scanForX10Sources(fProject);
	fMonitor.done();

	return (IProject[]) dependents.toArray(new IProject[dependents.size()]);
    }

    private Collection doCompile() throws CoreException {
	if (!fSourcesToCompile.isEmpty()) {
	    invokeX10C(fSourcesToCompile);
	}
	// TODO Compute set of dependent projects
	return Collections.EMPTY_LIST;
    }

    private void collectSourcesToCompile() throws CoreException {
	IResourceDelta delta= getDelta(fProject);

	if (delta != null) {
	    X10Plugin.maybeWriteInfoMsg("==> Scanning resource delta for project '" + fProject.getName() + "'... <==", BUILDER_ID);
	    delta.accept(fDeltaVisitor);
	    X10Plugin.maybeWriteInfoMsg("X10 delta scan completed for project '" + fProject.getName() + "'...", BUILDER_ID);
	} else {
	    X10Plugin.maybeWriteInfoMsg("==> Scanning for X10 source files in project '" + fProject.getName() + "'... <==", BUILDER_ID);
	    fProject.accept(fResourceVisitor);
	    X10Plugin.maybeWriteInfoMsg("X10 source file scan completed for project '" + fProject.getName() + "'...", BUILDER_ID);
	}
    }
}
