package x10dt.search.ui.globalSearch;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import org.eclipse.jdt.ui.JavaElementLabels;

public class PatternStrings {

	public static String getSignature(IJavaElement element) {
		if (element == null)
			return null;
		else
			switch (element.getElementType()) {
				case IJavaElement.METHOD:
					return getMethodSignature((IMethod)element);
				case IJavaElement.TYPE:
					return getTypeSignature((IType) element);
				case IJavaElement.FIELD:
					return getFieldSignature((IField) element);
				default:
					return element.getElementName();
			}
	}

	public static String getMethodSignature(IMethod method) {
		StringBuffer buffer= new StringBuffer();
		buffer.append(JavaElementLabels.getElementLabel(
			method.getDeclaringType(),
			JavaElementLabels.T_FULLY_QUALIFIED | JavaElementLabels.USE_RESOLVED));
		boolean isConstructor= method.getElementName().equals(method.getDeclaringType().getElementName());
		if (!isConstructor) {
			buffer.append('.');
		}
		buffer.append(getUnqualifiedMethodSignature(method, !isConstructor));

		return buffer.toString();
	}

	private static String getUnqualifiedMethodSignature(IMethod method, boolean includeName) {
		StringBuffer buffer= new StringBuffer();
		if (includeName) {
			buffer.append(method.getElementName());
		}
		buffer.append('(');

		String[] types= method.getParameterTypes();
		for (int i= 0; i < types.length; i++) {
			if (i > 0)
				buffer.append(", "); //$NON-NLS-1$
			String typeSig= Signature.toString(types[i]);
			buffer.append(typeSig);
		}
		buffer.append(')');

		return buffer.toString();
	}

	public static String getUnqualifiedMethodSignature(IMethod method) {
		return getUnqualifiedMethodSignature(method, true);
	}

	public static String getTypeSignature(IType field) {
		return JavaElementLabels.getElementLabel(field,
			JavaElementLabels.T_FULLY_QUALIFIED | JavaElementLabels.T_TYPE_PARAMETERS | JavaElementLabels.USE_RESOLVED);
	}

	public static String getFieldSignature(IField field) {
		return JavaElementLabels.getElementLabel(field, JavaElementLabels.F_FULLY_QUALIFIED);
	}
}

