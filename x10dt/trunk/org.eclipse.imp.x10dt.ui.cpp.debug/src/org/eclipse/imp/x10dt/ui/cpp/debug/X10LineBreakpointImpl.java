package org.eclipse.imp.x10dt.ui.cpp.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;

public class X10LineBreakpointImpl implements IPLineBreakpoint {

	private final IFile fFile;
	private final int fLineNumber;
	private IMarker fMarker;
	private boolean fEnabled;
	private boolean fPersisted;
	private boolean fRegistered;

	public X10LineBreakpointImpl(IFile file, int lineNumber, IMarker marker) {
		fFile = file;
		fLineNumber = lineNumber;
		fMarker = marker;
		fEnabled = true;
		fPersisted = true;
		fRegistered = false;
	}

	public String getAddress() throws CoreException {
		throw new IllegalArgumentException();
	}

	public String getFileName() throws CoreException {
		return fFile.getFullPath().toString();
	}

	public String getFunction() throws CoreException {
		return null;
	}

	public void setAddress(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setFunction(String arg0) throws CoreException {
		throw new IllegalArgumentException();

	}

	public int decrementInstallCount() throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCondition() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurSetId() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIgnoreCount() throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJobId() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobName() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSetId() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSourceHandle() throws CoreException {
		throw new IllegalArgumentException();
	}

	public int incrementInstallCount() throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isConditional() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isGlobal() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInstalled() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetInstallCount() throws CoreException {
		// TODO Auto-generated method stub

	}

	public void setCondition(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setCurSetId(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setIgnoreCount(int arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setJobId(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setJobName(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setSetId(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void setSourceHandle(String arg0) throws CoreException {
		throw new IllegalArgumentException();
	}

	public void updateMarkerMessage() throws CoreException {
		throw new IllegalArgumentException();
	}

	public void delete() throws CoreException {
		throw new IllegalArgumentException();
	}

	public IMarker getMarker() {
		return fMarker;
	}

	public String getModelIdentifier() {
		return "org.eclipse.imp.x10dt.ui.debug";
	}

	public boolean isEnabled() throws CoreException {
		return fEnabled;
	}

	public boolean isPersisted() throws CoreException {
		return fPersisted;
	}

	public boolean isRegistered() throws CoreException {
		return fRegistered;
	}

	public void setEnabled(boolean enabled) throws CoreException {
		fEnabled = enabled;
	}

	public void setMarker(IMarker marker) throws CoreException {
		fMarker = marker;
	}

	public void setPersisted(boolean persisted) throws CoreException {
		fPersisted = persisted;
	}

	public void setRegistered(boolean registered) throws CoreException {
		fRegistered = registered;
	}

	public Object getAdapter(Class adapter) {
		throw new IllegalArgumentException();
	}

	public int getCharEnd() throws CoreException {
		throw new IllegalArgumentException();
	}

	public int getCharStart() throws CoreException {
		throw new IllegalArgumentException();
	}

	public int getLineNumber() throws CoreException {
		return fLineNumber;
	}
}
