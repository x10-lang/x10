package x10dt.ui.editor;

import org.eclipse.imp.editor.StructuredSourceViewerConfiguration;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import x10dt.search.ui.typeHierarchy.HierarchyInformationControl;
import x10dt.search.ui.typeHierarchy.TypeElementProvider;
import x10dt.ui.editor.formatting.IX10Partitions;

public class X10StructuredSourceViewerConfiguration extends StructuredSourceViewerConfiguration{
	public X10StructuredSourceViewerConfiguration(IPreferenceStore prefStore, UniversalEditor editor) {
		super(prefStore, editor);
	}
	
	private IInformationControlCreator getHierarchyPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE;
				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
				return new HierarchyInformationControl(fEditor, parent, shellStyle, treeStyle);
			}
		};
	}
	
	/**
	 * Returns the hierarchy presenter which will determine and shown type hierarchy
	 * information requested for the current cursor position.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param doCodeResolve a boolean which specifies whether code resolve should be used to compute the Java element
	 * @return an information presenter
	 * @since 3.0
	 */
	public IInformationPresenter getHierarchyPresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {

		// Do not create hierarchy presenter if there's no CU.
		//if (fEditor != null && fEditor.getEditorInput() != null)// && EditorUtility.getEditorInputModelElement(fEditor.getEditorInput()) == null)
		//	return null;

		InformationPresenter presenter= new InformationPresenter(getHierarchyPresenterControlCreator());
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		IInformationProvider provider= new TypeElementProvider(fEditor, doCodeResolve);
		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
		presenter.setInformationProvider(provider, IX10Partitions.X10_DOC);
		presenter.setInformationProvider(provider, IX10Partitions.X10_MULTI_LINE_COMMENT);
		presenter.setInformationProvider(provider, IX10Partitions.X10_SINGLE_LINE_COMMENT);
		presenter.setInformationProvider(provider, IX10Partitions.X10_STRING);
		presenter.setInformationProvider(provider, IX10Partitions.X10_CHARACTER);
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}

}
