/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10dt.search.ui.typeHierarchy;

import org.eclipse.imp.ui.ElementLabels;
import org.eclipse.imp.ui.wizards.buildpaths.Strings;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.elements.IX10Element;



/**
 * <code>JavaElementLabels</code> provides helper methods to render names of Java elements.
 *
 * @since 3.1
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class X10ElementLabels extends ElementLabels {

	private X10ElementLabels() {
		super();
	}

	/**
	 * Returns the label of the given object. The object must be of type {@link IX10Element} or adapt to {@link IWorkbenchAdapter}.
	 * If the element type is not known, the empty string is returned.
	 * The returned label is BiDi-processed with {@link TextProcessor#process(String, String)}.
	 *
	 * @param obj object to get the label for
	 * @param flags the rendering flags
	 * @return the label or the empty string if the object type is not supported
	 */
	public static String getTextLabel(Object obj, long flags) {
		if (obj instanceof IX10Element) {
			return getElementLabel((IX10Element) obj, flags);

		} 
		return ElementLabels.getTextLabel(obj, flags); //$NON-NLS-1$
	}

	/**
	 * Returns the styled label of the given object. The object must be of type {@link IX10Element} or adapt to {@link IWorkbenchAdapter}.
	 * If the element type is not known, the empty string is returned.
	 * The returned label is BiDi-processed with {@link TextProcessor#process(String, String)}.
	 *
	 * @param obj object to get the label for
	 * @param flags the rendering flags
	 * @return the label or the empty string if the object type is not supported
	 *
	 * @since 3.4
	 */
	public static StyledString getStyledTextLabel(Object obj, long flags) {
		if (obj instanceof IX10Element) {
			return getStyledElementLabel((IX10Element)obj, flags);
		}
		return ElementLabels.getStyledTextLabel(obj, flags);
	}

	/**
	 * Returns the styled label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags
	 * @return the label of the Java element
	 *
	 * @since 3.4
	 */
	public static StyledString getStyledElementLabel(IX10Element element, long flags) {
		StyledString result= new StyledString();
		getElementLabel(element, flags, result);
		return Strings.markJavaElementLabelLTR(result);
	}
	
	/**
	 * Returns the label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags
	 * @return the label of the Java element
	 */
	public static String getElementLabel(IX10Element element, long flags) {
		StringBuffer result= new StringBuffer();
		getElementLabel(element, flags, result);
		return Strings.markJavaElementLabelLTR(result.toString());
	}

	/**
	 * Returns the label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags
	 * @param buf the buffer to append the resulting label to
	 */
	public static void getElementLabel(IX10Element element, long flags, StringBuffer buf) {
		new X10ElementLabelComposer(buf).appendElementLabel(element, flags);
	}
	
	/**
	 * Returns the styled label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags
	 * @param result the buffer to append the resulting label to
	 *
	 * @since 3.4
	 */
	public static void getElementLabel(IX10Element element, long flags, StyledString result) {
		new X10ElementLabelComposer(result).appendElementLabel(element, flags);
	}

	/**
	 * Appends the label for a method to a {@link StringBuffer}. Considers the M_* flags.
	 *
	 * @param method the element to render
	 * @param flags the rendering flags. Flags with names starting with 'M_' are considered.
	 * @param buf the buffer to append the resulting label to
	 */
	public static void getMethodLabel(IMethodInfo method, long flags, StringBuffer buf) {
		new X10ElementLabelComposer(buf).appendMethodLabel(method, flags);
	}

	/**
	 * Appends the label for a method to a {@link StyledString}. Considers the M_* flags.
	 *
	 * @param method the element to render
	 * @param flags the rendering flags. Flags with names starting with 'M_' are considered.
	 * @param result the buffer to append the resulting label to
	 *
	 * @since 3.4
	 */
	public static void getMethodLabel(IMethodInfo method, long flags, StyledString result) {
		new X10ElementLabelComposer(result).appendMethodLabel(method, flags);
	}

	/**
	 * Appends the label for a field to a {@link StringBuffer}. Considers the F_* flags.
	 *
	 * @param field the element to render
	 * @param flags the rendering flags. Flags with names starting with 'F_' are considered.
	 * @param buf the buffer to append the resulting label to
	 */
	public static void getFieldLabel(IFieldInfo field, long flags, StringBuffer buf) {
		new X10ElementLabelComposer(buf).appendFieldLabel(field, flags);
	}

	/**
	 * Appends the style label for a field to a {@link StyledString}. Considers the F_* flags.
	 *
	 * @param field the element to render
	 * @param flags the rendering flags. Flags with names starting with 'F_' are considered.
	 * @param result the buffer to append the resulting label to
	 *
	 * @since 3.4
	 */
	public static void getFieldLabel(IFieldInfo field, long flags, StyledString result) {
		new X10ElementLabelComposer(result).appendFieldLabel(field, flags);
	}

//	/**
//	 * Appends the label for a local variable to a {@link StringBuffer}.
//	 *
//	 * @param localVariable the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'F_' are considered.
//	 * @param buf the buffer to append the resulting label to
//	 */
//	public static void getLocalVariableLabel(ILocalVariable localVariable, long flags, StringBuffer buf) {
//		new X10ElementLabelComposer(buf).appendLocalVariableLabel(localVariable, flags);
//	}
//
//	/**
//	 * Appends the styled label for a local variable to a {@link StyledString}.
//	 *
//	 * @param localVariable the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'F_' are considered.
//	 * @param result the buffer to append the resulting label to
//	 *
//	 * @since 3.4
//	 */
//	public static void getLocalVariableLabel(ILocalVariable localVariable, long flags, StyledString result) {
//		new X10ElementLabelComposer(result).appendLocalVariableLabel(localVariable, flags);
//	}
//
//
//	/**
//	 * Appends the label for a initializer to a {@link StringBuffer}. Considers the I_* flags.
//	 *
//	 * @param initializer the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'I_' are considered.
//	 * @param buf the buffer to append the resulting label to
//	 */
//	public static void getInitializerLabel(IInitializer initializer, long flags, StringBuffer buf) {
//		new X10ElementLabelComposer(buf).appendInitializerLabel(initializer, flags);
//	}
//
//	/**
//	 * Appends the label for a initializer to a {@link StyledString}. Considers the I_* flags.
//	 *
//	 * @param initializer the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'I_' are considered.
//	 * @param result the buffer to append the resulting label to
//	 *
//	 * @since 3.4
//	 */
//	public static void getInitializerLabel(IInitializer initializer, long flags, StyledString result) {
//		new X10ElementLabelComposer(result).appendInitializerLabel(initializer, flags);
//	}

	/**
	 * Appends the label for a type to a {@link StringBuffer}. Considers the T_* flags.
	 *
	 * @param type the element to render
	 * @param flags the rendering flags. Flags with names starting with 'T_' are considered.
	 * @param buf the buffer to append the resulting label to
	 */
	public static void getTypeLabel(ITypeInfo type, long flags, StringBuffer buf) {
		new X10ElementLabelComposer(buf).appendTypeLabel(type, flags);
	}

	/**
	 * Appends the label for a type to a {@link StyledString}. Considers the T_* flags.
	 *
	 * @param type the element to render
	 * @param flags the rendering flags. Flags with names starting with 'T_' are considered.
	 * @param result the buffer to append the resulting label to
	 *
	 * @since 3.4
	 */
	public static void getTypeLabel(ITypeInfo type, long flags, StyledString result) {
		new X10ElementLabelComposer(result).appendTypeLabel(type, flags);
	}


//	/**
//	 * Appends the label for a type parameter to a {@link StringBuffer}. Considers the TP_* flags.
//	 *
//	 * @param typeParameter the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'TP_' are considered.
//	 * @param buf the buffer to append the resulting label to
//	 *
//	 * @since 3.5
//	 */
//	public static void getTypeParameterLabel(ITypeParameter typeParameter, long flags, StringBuffer buf) {
//		new X10ElementLabelComposer(buf).appendTypeParameterLabel(typeParameter, flags);
//	}
//
//	/**
//	 * Appends the label for a type parameter to a {@link StyledString}. Considers the TP_* flags.
//	 *
//	 * @param typeParameter the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'TP_' are considered.
//	 * @param result the buffer to append the resulting label to
//	 *
//	 * @since 3.5
//	 */
//	public static void getTypeParameterLabel(ITypeParameter typeParameter, long flags, StyledString result) {
//		new X10ElementLabelComposer(result).appendTypeParameterLabel(typeParameter, flags);
//	}


//	/**
//	 * Appends the label for a import container, import or package declaration to a {@link StringBuffer}. Considers the D_* flags.
//	 *
//	 * @param declaration the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'D_' are considered.
//	 * @param buf the buffer to append the resulting label to
//	 */
//	public static void getDeclarationLabel(IX10Element declaration, long flags, StringBuffer buf) {
//		new X10ElementLabelComposer(buf).appendDeclarationLabel(declaration, flags);
//	}
//
//	/**
//	 * Appends the label for a import container, import or package declaration to a {@link StyledString}. Considers the D_* flags.
//	 *
//	 * @param declaration the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'D_' are considered.
//	 * @param result the buffer to append the resulting label to
//	 *
//	 * @since 3.4
//	 */
//	public static void getDeclarationLabel(IX10Element declaration, long flags, StyledString result) {
//		new X10ElementLabelComposer(result).appendDeclarationLabel(declaration, flags);
//	}
}
