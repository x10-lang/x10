package x10dt.search.ui.globalSearch;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledString;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IType;

import org.eclipse.jdt.ui.JavaElementLabels;


public class PostfixLabelProvider extends SearchLabelProvider {
	private ITreeContentProvider fContentProvider;

	public PostfixLabelProvider(X10SearchResultPage page) {
		super(page);
		fContentProvider= new LevelTreeContentProvider.FastJavaElementProvider();
	}

	public Image getImage(Object element) {
		Image image= super.getImage(element);
		if (image != null)
			return image;
		return getParticipantImage(element);
	}

	public String getText(Object element) {
		String labelWithCounts= getLabelWithCounts(element, internalGetText(element));
		return labelWithCounts + getQualification(element);
	}

	private String getQualification(Object element) {
		StringBuffer res= new StringBuffer();

		ITreeContentProvider provider= (ITreeContentProvider) fPage.getViewer().getContentProvider();
		Object visibleParent= provider.getParent(element);
		Object realParent= fContentProvider.getParent(element);
		Object lastElement= element;
		while (realParent != null && !(realParent instanceof IJavaModel) && !realParent.equals(visibleParent)) {
			if (!isSameInformation(realParent, lastElement))  {
				res.append(JavaElementLabels.CONCAT_STRING).append(internalGetText(realParent));
			}
			lastElement= realParent;
			realParent= fContentProvider.getParent(realParent);
		}
		return res.toString();
	}

	protected boolean hasChildren(Object element) {
		ITreeContentProvider contentProvider= (ITreeContentProvider) fPage.getViewer().getContentProvider();
		return contentProvider.hasChildren(element);
	}

	private String internalGetText(Object element) {
		String text= super.getText(element);
		if (text != null && text.length() > 0)
			return text;
		return getParticipantText(element);
	}

	private StyledString internalGetStyledText(Object element) {
		StyledString text= super.getStyledText(element);
		if (text != null && text.length() > 0)
			return text;
		return getStyledParticipantText(element);
	}

	private boolean isSameInformation(Object realParent, Object lastElement) {
		if (lastElement instanceof IType) {
			IType type= (IType) lastElement;
			if (realParent instanceof IClassFile) {
				if (type.getClassFile().equals(realParent))
					return true;
			} else if (realParent instanceof ICompilationUnit) {
				if (type.getCompilationUnit().equals(realParent))
					return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider#getStyledText(java.lang.Object)
	 */
	public StyledString getStyledText(Object element) {
		StyledString styledString= getColoredLabelWithCounts(element, internalGetStyledText(element));
		styledString.append(getQualification(element), StyledString.QUALIFIER_STYLER);
		return styledString;
	}

}
