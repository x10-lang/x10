package org.eclipse.imp.x10dt.ui.launch.cpp.launching;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.ptp.core.resources.FileStorage;
import org.eclipse.ptp.debug.core.PTPDebugCorePlugin;
import org.eclipse.ptp.debug.core.model.IPStackFrame;

/**
 * Does source location for the C++ backend-generated executables.
 * 
 * @author igor
 */
public class CppSourceLocator implements IPersistableSourceLocator {

  /**
   * TODO
   */
  public CppSourceLocator() {
  }

  public String getMemento() throws CoreException {
    // TODO Auto-generated method stub
    return null;
  }

  public void initializeDefaults(ILaunchConfiguration configuration)
      throws CoreException
  {
    // TODO Auto-generated method stub
  }

  public void initializeFromMemento(String memento) throws CoreException {
    // TODO Auto-generated method stub
  }

  public Object getSourceElement(IStackFrame stackFrame) {
    IPStackFrame frame = (IPStackFrame) stackFrame;
    String file = frame.getFile();
    IWorkspaceRoot wsRoot = PTPDebugCorePlugin.getWorkspace().getRoot();
	String wsRootPath = wsRoot.getLocation().toString();
    if (!file.startsWith(wsRootPath)) {
      // TODO: try to find it in the classpath jars and return a ZipEntryStorage
      IPath path = new Path(file);
      if (!path.isAbsolute())
        path = path.makeAbsolute();
	  return new FileStorage(path);
    }
    IFile[] wsFiles = null;
	try {
	  wsFiles = wsRoot.findFilesForLocationURI(new URI("file://"+file)); //$NON-NLS-1$
	} catch (URISyntaxException e) {
	  // TODO Auto-generated catch block
	  e.printStackTrace();
	  return null;
	}
    assert (wsFiles != null);
    if (wsFiles.length != 1)
      return null;
    IFile f = wsFiles[0];
//    IEditorPart editor = null;
//    try {
//      editor = EditorUtility.openInEditor(f);
//    } catch (PartInitException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      return null;
//    }
//    IEditorInput input = editor.getEditorInput();
////    IDocument doc = ((UniversalEditor)editor).getDocumentProvider().getDocument(input); // doesn't work
////    EditorUtility.revealInEditor(editor, doc.getLineOffset(line), 0);

    // This is cool - we only need to return the file, and Eclipse does the rest
    return f;
  }

}