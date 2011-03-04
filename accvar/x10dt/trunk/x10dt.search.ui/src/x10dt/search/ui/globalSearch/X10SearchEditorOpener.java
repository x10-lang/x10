package x10dt.search.ui.globalSearch;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;


public class X10SearchEditorOpener {

	private IEditorReference fReusedEditor;

	public IEditorPart openElement(Object element) throws PartInitException {
		IWorkbenchPage wbPage= JavaPlugin.getActivePage();
		IEditorPart editor;
		if (NewSearchUI.reuseEditor())
			editor= showWithReuse(element, wbPage);
		else
			editor= showWithoutReuse(element);

		if (element instanceof IJavaElement)
			EditorUtility.revealInEditor(editor, (IJavaElement) element);

		return editor;
	}

	public IEditorPart openMatch(Match match) throws PartInitException {
		Object element= getElementToOpen(match);
		return openElement(element);
	}

	protected Object getElementToOpen(Match match) {
		return match.getElement();
	}

	private IEditorPart showWithoutReuse(Object element) throws PartInitException {
		try {
			return EditorUtility.openInEditor(element, false);
		} catch (PartInitException e) {
			if (e.getStatus().getCode() != IJavaStatusConstants.EDITOR_NO_EDITOR_INPUT) {
				throw e;
			}
		}
		return null;
	}

	private IEditorPart showWithReuse(Object element, IWorkbenchPage wbPage) throws PartInitException {
		IEditorInput input= EditorUtility.getEditorInput(element);
		if (input == null)
			return null;
		String editorID= EditorUtility.getEditorID(input);
		return showInEditor(wbPage, input, editorID);
	}

	private IEditorPart showInEditor(IWorkbenchPage page, IEditorInput input, String editorId) {
		IEditorPart editor= page.findEditor(input);
		if (editor != null) {
			page.bringToTop(editor);
			return editor;
		}
		IEditorReference reusedEditorRef= fReusedEditor;
		if (reusedEditorRef !=  null) {
			boolean isOpen= reusedEditorRef.getEditor(false) != null;
			boolean canBeReused= isOpen && !reusedEditorRef.isDirty() && !reusedEditorRef.isPinned();
			if (canBeReused) {
				boolean showsSameInputType= reusedEditorRef.getId().equals(editorId);
				if (!showsSameInputType) {
					page.closeEditors(new IEditorReference[] { reusedEditorRef }, false);
					fReusedEditor= null;
				} else {
					editor= reusedEditorRef.getEditor(true);
					if (editor instanceof IReusableEditor) {
						((IReusableEditor) editor).setInput(input);
						page.bringToTop(editor);
						return editor;
					}
				}
			}
		}
		// could not reuse
		try {
			editor= page.openEditor(input, editorId, false);
			if (editor instanceof IReusableEditor) {
				IEditorReference reference= (IEditorReference) page.getReference(editor);
				fReusedEditor= reference;
			} else {
				fReusedEditor= null;
			}
			return editor;
		} catch (PartInitException ex) {
			MessageDialog.openError(JavaPlugin.getActiveWorkbenchShell(), SearchMessages.Search_Error_openEditor_title, SearchMessages.Search_Error_openEditor_message);
			return null;
		}
	}

}
