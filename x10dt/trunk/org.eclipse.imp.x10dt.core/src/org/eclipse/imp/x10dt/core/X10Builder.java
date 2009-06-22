package com.ibm.watson.safari.x10;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Plugin;

public class X10Builder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID= X10Plugin.kPluginID + ".X10Builder";

    public static final String PROBLEMMARKER_ID= X10Plugin.kPluginID + ".problemMarker";

    private class X10ResourceVisitor implements IResourceVisitor {
	public X10ResourceVisitor() { }

	public boolean visit(IResource res) throws CoreException {
	    if (isX10SourceFile(res)) {
		IFile file= (IFile) res;

		X10Plugin.maybeWriteInfoMsg("Running X10C on source file '" + file.getFullPath() + "'...", BUILDER_ID);
		invokeX10C(file);
		X10Plugin.maybeWriteInfoMsg("X10C completed on source file '" + file.getFullPath() + "'.", BUILDER_ID);
		return false; // skip sub-resources
	    } else
		return true; // need to descend into sub-resources
	}
    }

    private IProject fProject;
    private IProgressMonitor fMonitor;
    private X10ResourceVisitor fVisitor= new X10ResourceVisitor();

    private static Plugin sPlugin= null;

    public X10Builder() { }

    private boolean isX10SourceFile(IResource res) {
	String exten= res.getFileExtension();

	return res.exists() && res instanceof IFile && exten != null && exten.compareTo("x10") == 0;
    }

    protected void clearMarkersOn(IFile file) {
	try {
	    file.deleteMarkers(PROBLEMMARKER_ID, true, IResource.DEPTH_INFINITE);
	} catch (CoreException e) {
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
    private void invokeX10C(IFile file) {
	clearMarkersOn(file);
    }

    protected void scanForX10Sources(IProject proj) throws CoreException {
	X10Plugin.maybeWriteInfoMsg("==> Scanning for X10 source files in project '" + proj.getName() + "'... <==", BUILDER_ID);
	proj.accept(fVisitor);
	X10Plugin.maybeWriteInfoMsg("X10 source file scan completed for project '" + proj.getName() + "'...", BUILDER_ID);
    }

    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	fProject= getProject();
	fMonitor= monitor;

	if (sPlugin == null)
	    sPlugin= X10Plugin.getInstance();

	// Refresh prefs every time so that changes take effect on the next build.
	X10Plugin.refreshPrefs();

	IResourceDelta delta= getDelta(fProject);

	if (delta != null) {
	    final Collection interestingFiles= new HashSet();

	    delta.accept(new IResourceDeltaVisitor() {
		public boolean visit(IResourceDelta delta) throws CoreException {
//		    System.out.println("Visiting delta " + delta.toString() + " of kind " + k_kindNames[delta.getKind()] + ".");
		    if (isX10SourceFile(delta.getResource()))
			interestingFiles.add(delta.getResource());
		    return true;
		}
	    });
	    if (!interestingFiles.isEmpty()) {
		for(Iterator iter= interestingFiles.iterator(); iter.hasNext(); ) {
		    IFile file= (IFile) iter.next();

		    fVisitor.visit(file);
		}
		return new IProject[] { fProject };
	    } else
		return new IProject[0];
	}

	fMonitor.beginTask("Scanning and compiling JavaCC source files...", 0);
	scanForX10Sources(fProject);
	fMonitor.done();

	return new IProject[] { fProject };
    }
}
