package x10dt.search.ui.globalSearch;

import org.eclipse.swt.graphics.Image;

import org.eclipse.core.resources.IResource;

import org.eclipse.jface.viewers.StyledString;

import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.ui.JavaElementLabels;


public class SortingLabelProvider extends SearchLabelProvider {

	public static final int SHOW_ELEMENT_CONTAINER= 1; // default
	public static final int SHOW_CONTAINER_ELEMENT= 2;
	public static final int SHOW_PATH= 3;

	private static final long FLAGS_QUALIFIED= DEFAULT_SEARCH_TEXTFLAGS | JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.M_FULLY_QUALIFIED | JavaElementLabels.I_FULLY_QUALIFIED
		| JavaElementLabels.T_FULLY_QUALIFIED | JavaElementLabels.D_QUALIFIED | JavaElementLabels.CF_QUALIFIED  | JavaElementLabels.CU_QUALIFIED | JavaElementLabels.COLORIZE;


	private int fCurrentOrder;

	public SortingLabelProvider(X10SearchResultPage page) {
		super(page);
		fCurrentOrder= SHOW_ELEMENT_CONTAINER;
	}

	public Image getImage(Object element) {
		Image image= null;
		if (element instanceof IJavaElement || element instanceof IResource)
			image= super.getImage(element);
		if (image != null)
			return image;
		return getParticipantImage(element);
	}

	public final String getText(Object element) {
		if (element instanceof IImportDeclaration)
			element= ((IImportDeclaration)element).getParent().getParent();

		String text= super.getText(element);
		if (text.length() > 0) {
			String labelWithCount= getLabelWithCounts(element, text);
			if (fCurrentOrder == SHOW_ELEMENT_CONTAINER) {
				labelWithCount += getPostQualification(element);
			}
			return labelWithCount;
		}
		return getParticipantText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider#getStyledText(java.lang.Object)
	 */
	public StyledString getStyledText(Object element) {
		if (element instanceof IImportDeclaration)
			element= ((IImportDeclaration)element).getParent().getParent();

		StyledString text= super.getStyledText(element);
		if (text.length() > 0) {
			StyledString countLabel= getColoredLabelWithCounts(element, text);
			if (fCurrentOrder == SHOW_ELEMENT_CONTAINER) {
				countLabel.append(getPostQualification(element), StyledString.QUALIFIER_STYLER);
			}
			return countLabel;
		}
		return getStyledParticipantText(element);
	}

	private String getPostQualification(Object element) {
		String textLabel= JavaElementLabels.getTextLabel(element, JavaElementLabels.ALL_POST_QUALIFIED);
		int indexOf= textLabel.indexOf(JavaElementLabels.CONCAT_STRING);
		if (indexOf != -1) {
			return textLabel.substring(indexOf);
		}
		return new String();
	}

	public void setOrder(int orderFlag) {
		fCurrentOrder= orderFlag;
		long flags= 0;
		if (orderFlag == SHOW_ELEMENT_CONTAINER)
			flags= DEFAULT_SEARCH_TEXTFLAGS;
		else if (orderFlag == SHOW_CONTAINER_ELEMENT)
			flags= FLAGS_QUALIFIED;
		else if (orderFlag == SHOW_PATH) {
			flags= FLAGS_QUALIFIED | JavaElementLabels.PREPEND_ROOT_PATH;
		}
		setTextFlags(flags);
	}
}

