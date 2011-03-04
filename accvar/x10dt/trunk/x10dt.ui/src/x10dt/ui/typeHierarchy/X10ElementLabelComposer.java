/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Guven Demir <guven.internet+eclipse@gmail.com> - [package explorer] Alternative package name shortening: abbreviation - https://bugs.eclipse.org/bugs/show_bug.cgi?id=299514
 *******************************************************************************/
package x10dt.ui.typeHierarchy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.elements.IX10Element;
import x10dt.search.core.engine.ITypeHierarchy;
import x10dt.search.core.engine.X10SearchEngine;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;


/**
 * Implementation of {@link X10ElementLabels}.
 *
 * @since 3.5
 */
public class X10ElementLabelComposer {

	private static class Signature
	{
		static String[] getTypeParameters(String signature)
		{
			return new String[0];
		}
		
		static String toString(String signature)
		{
			return signature;
		}
	}
	
	/**
	 * Use of this constant is <b>FORBIDDEN</b> for external clients.
	 * <p>
	 * TODO: Make API in PreferenceConstants in 3.7, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=306069
	 * 
	 * @see PreferenceConstants#APPEARANCE_PKG_NAME_ABBREVIATION_PATTERN_FOR_PKG_VIEW
	 */
	public static final String APPEARANCE_PKG_NAME_ABBREVIATION_PATTERN_FOR_PKG_VIEW= "org.eclipse.jdt.ui.pkgNameAbbreviationPatternForPackagesView";//$NON-NLS-1$

	/**
	 * Use of this constant is <b>FORBIDDEN</b> for external clients.
	 * <p>
	 * TODO: Make API in PreferenceConstants in 3.7, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=306069
	 * 
	 * @see PreferenceConstants#APPEARANCE_ABBREVIATE_PACKAGE_NAMES
	 */
	public static final String APPEARANCE_ABBREVIATE_PACKAGE_NAMES= "org.eclipse.jdt.ui.abbreviatepackagenames";//$NON-NLS-1$

	/**
	 * An adapter for buffer supported by the label composer.
	 */
	public static abstract class FlexibleBuffer {

		/**
		 * Appends the string representation of the given character to the buffer.
		 *
		 * @param ch the character to append
		 * @return a reference to this object
		 */
		public abstract FlexibleBuffer append(char ch);

		/**
		 * Appends the given string to the buffer.
		 *
		 * @param string the string to append
		 * @return a reference to this object
		 */
		public abstract FlexibleBuffer append(String string);

		/**
		 * Returns the length of the the buffer.
		 *
		 * @return the length of the current string
		 */
		public abstract int length();

		/**
		 * Sets a styler to use for the given source range. The range must be subrange of actual
		 * string of this buffer. Stylers previously set for that range will be overwritten.
		 *
		 * @param offset the start offset of the range
		 * @param length the length of the range
		 * @param styler the styler to set
		 *
		 * @throws StringIndexOutOfBoundsException if <code>start</code> is less than zero, or if
		 *             offset plus length is greater than the length of this object.
		 */
		public abstract void setStyle(int offset, int length, Styler styler);
	}

	public static class FlexibleStringBuffer extends FlexibleBuffer {
		private final StringBuffer fStringBuffer;

		public FlexibleStringBuffer(StringBuffer stringBuffer) {
			fStringBuffer= stringBuffer;
		}

		public FlexibleBuffer append(char ch) {
			fStringBuffer.append(ch);
			return this;
		}

		public FlexibleBuffer append(String string) {
			fStringBuffer.append(string);
			return this;
		}

		public int length() {
			return fStringBuffer.length();
		}

		public void setStyle(int offset, int length, Styler styler) {
			// no style
		}

		public String toString() {
			return fStringBuffer.toString();
		}
	}

	public static class FlexibleStyledString extends FlexibleBuffer {
		private final StyledString fStyledString;

		public FlexibleStyledString(StyledString stringBuffer) {
			fStyledString= stringBuffer;
		}

		public FlexibleBuffer append(char ch) {
			fStyledString.append(ch);
			return this;
		}

		public FlexibleBuffer append(String string) {
			fStyledString.append(string);
			return this;
		}

		public int length() {
			return fStyledString.length();
		}

		public void setStyle(int offset, int length, Styler styler) {
			fStyledString.setStyle(offset, length, styler);
		}

		public String toString() {
			return fStyledString.toString();
		}
	}

	private static class PackageNameAbbreviation {
		private String fPackagePrefix;

		private String fAbbreviation;

		public PackageNameAbbreviation(String packagePrefix, String abbreviation) {
			fPackagePrefix= packagePrefix;
			fAbbreviation= abbreviation;
		}

		public String getPackagePrefix() {
			return fPackagePrefix;
		}

		public String getAbbreviation() {
			return fAbbreviation;
		}
	}


	private final static long QUALIFIER_FLAGS= X10ElementLabels.P_COMPRESSED | X10ElementLabels.USE_RESOLVED;

	private static final Styler QUALIFIER_STYLE= StyledString.QUALIFIER_STYLER;
	private static final Styler COUNTER_STYLE= StyledString.COUNTER_STYLER;
	private static final Styler DECORATIONS_STYLE= StyledString.DECORATIONS_STYLER;
	
	/*
	 * Package name compression
	 */
	private static String fgPkgNamePattern= ""; //$NON-NLS-1$
	private static String fgPkgNamePrefix;
	private static String fgPkgNamePostfix;
	private static int fgPkgNameChars;
	private static int fgPkgNameLength= -1;
	
	/*
	 * Package name abbreviation
	 */
	private static String fgPkgNameAbbreviationPattern= ""; //$NON-NLS-1$
	private static PackageNameAbbreviation[] fgPkgNameAbbreviation;

	private final FlexibleBuffer fBuffer;

	private static final boolean getFlag(long flags, long flag) {
		return (flags & flag) != 0;
	}

	/**
	 * Creates a new java element composer based on the given buffer.
	 *
	 * @param buffer the buffer
	 */
	public X10ElementLabelComposer(FlexibleBuffer buffer) {
		fBuffer= buffer;
	}

	/**
	 * Creates a new java element composer based on the given buffer.
	 *
	 * @param buffer the buffer
	 */
	public X10ElementLabelComposer(StyledString buffer) {
		this(new FlexibleStyledString(buffer));
	}

	/**
	 * Creates a new java element composer based on the given buffer.
	 *
	 * @param buffer the buffer
	 */
	public X10ElementLabelComposer(StringBuffer buffer) {
		this(new FlexibleStringBuffer(buffer));
	}

	/**
	 * Appends the label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags.
	 */
	public void appendElementLabel(IX10Element element, long flags) {
//		ISourceRoot root= null;

//		if (type != IX10Element.JAVA_MODEL && type != IX10Element.JAVA_PROJECT && type != IX10Element.PACKAGE_FRAGMENT_ROOT)
//			root= ModelUtil.getPackageFragmentRoot(element);
//		if (root != null && getFlag(flags, X10ElementLabels.PREPEND_ROOT_PATH)) {
//			appendPackageFragmentRootLabel(root, X10ElementLabels.ROOT_QUALIFIED);
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//		}

		if(element instanceof IMethodInfo)
		{
			appendMethodLabel((IMethodInfo) element, flags);
		}
		
		else if(element instanceof IFieldInfo)
		{
			appendFieldLabel((IFieldInfo) element, flags);
		}
		
		else if(element instanceof ITypeInfo)
		{
			appendTypeLabel((ITypeInfo) element, flags);
		}
		
		else if(element instanceof IMemberInfo)
		{
			fBuffer.append(((IMemberInfo)element).getName());
		}
		
//		switch (type) {
//			case IX10Element.LOCAL_VARIABLE:
//				appendLocalVariableLabel((ILocalVariable) element, flags);
//				break;
//			case IX10Element.TYPE_PARAMETER:
//				appendTypeParameterLabel((ITypeInfo) element, flags);
//				break;
//			case IX10Element.INITIALIZER:
//				appendInitializerLabel((IInitializer) element, flags);
//				break;
//			case IX10Element.CLASS_FILE:
//				appendClassFileLabel((IClassFile) element, flags);
//				break;
//			case IX10Element.COMPILATION_UNIT:
//				appendCompilationUnitLabel((ICompilationUnit) element, flags);
//				break;
//			case IX10Element.PACKAGE_FRAGMENT:
//				appendPackageFragmentLabel((IPackageFragment) element, flags);
//				break;
//			case IX10Element.PACKAGE_FRAGMENT_ROOT:
//				appendPackageFragmentRootLabel((IPackageFragmentRoot) element, flags);
//				break;
//			case IX10Element.IMPORT_CONTAINER:
//			case IX10Element.IMPORT_DECLARATION:
//			case IX10Element.PACKAGE_DECLARATION:
//				appendDeclarationLabel(element, flags);
//				break;
//			case IX10Element.JAVA_PROJECT:
//			case IX10Element.JAVA_MODEL:
//				fBuffer.append(element.getElementName());
//				break;
//			default:
//				fBuffer.append(element.getElementName());
//		}
//
//		if (root != null && getFlag(flags, X10ElementLabels.APPEND_ROOT_PATH)) {
//			int offset= fBuffer.length();
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendPackageFragmentRootLabel(root, X10ElementLabels.ROOT_QUALIFIED);
//
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//
//		}
	}



	/**
	 * Appends the label for a method. Considers the M_* flags.
	 *
	 * @param method the element to render
	 * @param flags the rendering flags. Flags with names starting with 'M_' are considered.
	 */
	public void appendMethodLabel(IMethodInfo method, long flags) {
//		try {
//			BindingKey resolvedKey= getFlag(flags, X10ElementLabels.USE_RESOLVED) && method.isResolved() ? new BindingKey(method.getKey()) : null;
//			String resolvedSig= (resolvedKey != null) ? resolvedKey.toSignature() : null;
//
//			// type parameters
//			if (getFlag(flags, X10ElementLabels.M_PRE_TYPE_PARAMETERS)) {
//				if (resolvedKey != null) {
//					if (resolvedKey.isParameterizedMethod()) {
//						String[] typeArgRefs= resolvedKey.getTypeArguments();
//						if (typeArgRefs.length > 0) {
//							appendTypeArgumentSignaturesLabel(method, typeArgRefs, flags);
//							fBuffer.append(' ');
//						}
//					} else {
//						String[] typeParameterSigs= Signature.getTypeParameters(resolvedSig);
//						if (typeParameterSigs.length > 0) {
//							appendTypeParameterSignaturesLabel(typeParameterSigs, flags);
//							fBuffer.append(' ');
//						}
//					}
//				} else if (method.exists(new NullProgressMonitor())) {
//					ITypeInfo[] typeParameters= method.getTypeParameters();
//					if (typeParameters.length > 0) {
//						appendTypeParametersLabels(typeParameters, flags);
//						fBuffer.append(' ');
//					}
//				}
//			}

			// return type
			if (getFlag(flags, X10ElementLabels.M_PRE_RETURNTYPE) && method.exists(new NullProgressMonitor()) && !method.isConstructor()) {
//				String returnTypeSig= resolvedSig != null ? Signature.getReturnType(resolvedSig) : method.getReturnType();
				appendTypeSignatureLabel(method, method.getReturnType(), flags);
				fBuffer.append(' ');
			}

			// qualification
			if (getFlag(flags, X10ElementLabels.M_FULLY_QUALIFIED)) {
				appendTypeLabel(method.getDeclaringType(), X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
				fBuffer.append('.');
			}

			fBuffer.append(getElementName(method));

			// parameters
			fBuffer.append('(');
			if (getFlag(flags, X10ElementLabels.M_PARAMETER_TYPES | X10ElementLabels.M_PARAMETER_NAMES)) {
				ITypeInfo[] types= null;
				int nParams= 0;
				boolean renderVarargs= false;
				if (getFlag(flags, X10ElementLabels.M_PARAMETER_TYPES)) {
//					if (resolvedSig != null) {
//						types= Signature.getParameterTypes(resolvedSig);
//					} 
//					else {
						types= method.getParameters();
//					}
					nParams= types.length;
//					renderVarargs= method.exists(new NullProgressMonitor()) /*&& Flags.isVarargs(method.getFlags())*/;
				}
//				ITypeInfo[] names= null;
//				if (getFlag(flags, X10ElementLabels.M_PARAMETER_NAMES) && method.exists(new NullProgressMonitor())) {
//					types= method.getParameters();
//					if (types == null) {
//						nParams= names.length;
//					} else { // types != null
//						if (nParams != names.length) {
//							if (resolvedSig != null && types.length > names.length) {
//								// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=99137
//								nParams= names.length;
//								String[] typesWithoutSyntheticParams= new String[nParams];
//								System.arraycopy(types, types.length - nParams, typesWithoutSyntheticParams, 0, nParams);
//								types= typesWithoutSyntheticParams;
//							} else {
//								// https://bugs.eclipse.org/bugs/show_bug.cgi?id=101029
//								// X10DTUIPlugin.getInstance().writeErrorMsg("X10ElementLabels: Number of param types(" + nParams + ") != number of names(" + names.length + "): " + method.getElementName());   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
//								names= null; // no names rendered
//							}
//						}
//					}
//				}

				for (int i= 0; i < nParams; i++) {
					if (i > 0) {
						fBuffer.append(X10ElementLabels.COMMA_STRING);
					}
					if (types != null) {
						String paramSig= types[i].getName();
						if (renderVarargs && (i == nParams - 1)) {
//							int newDim= Signature.getArrayCount(paramSig) - 1;
							appendTypeSignatureLabel(method, types[i], flags);
//							for (int k= 0; k < newDim; k++) {
//								fBuffer.append('[').append(']');
//							}
							fBuffer.append(X10ElementLabels.ELLIPSIS_STRING);
						} else {
							appendTypeSignatureLabel(method, types[i], flags);
						}
					}
//					if (names != null) {
//						if (types != null) {
//							fBuffer.append(' ');
//						}
//						fBuffer.append(names[i]);
//					}
				}
			} else {
				if (method.getParameters().length > 0) {
					fBuffer.append(X10ElementLabels.ELLIPSIS_STRING);
				}
			}
			fBuffer.append(')');

			if (getFlag(flags, X10ElementLabels.M_EXCEPTIONS)) {
				ITypeInfo[] types;
//				if (resolvedKey != null) {
//					types= resolvedKey.getThrownExceptions();
//				} else {
					types= /*method.exists(new NullProgressMonitor()) ? method.getExceptionTypes() : */ new ITypeInfo[0];
//				}
				if (types.length > 0) {
					fBuffer.append(" throws "); //$NON-NLS-1$
					for (int i= 0; i < types.length; i++) {
						if (i > 0) {
							fBuffer.append(X10ElementLabels.COMMA_STRING);
						}
						appendTypeSignatureLabel(method, types[i], flags);
					}
				}
			}

//			if (getFlag(flags, X10ElementLabels.M_APP_TYPE_PARAMETERS)) {
//				int offset= fBuffer.length();
//				if (resolvedKey != null) {
//					if (resolvedKey.isParameterizedMethod()) {
//						String[] typeArgRefs= resolvedKey.getTypeArguments();
//						if (typeArgRefs.length > 0) {
//							fBuffer.append(' ');
//							appendTypeArgumentSignaturesLabel(method, typeArgRefs, flags);
//						}
//					} else {
//						String[] typeParameterSigs= Signature.getTypeParameters(resolvedSig);
//						if (typeParameterSigs.length > 0) {
//							fBuffer.append(' ');
//							appendTypeParameterSignaturesLabel(typeParameterSigs, flags);
//						}
//					}
//				} 
//				else 
//				if (method.exists(new NullProgressMonitor())) {
//					ITypeInfo[] typeParameters= method.getParameters();
//					if (typeParameters.length > 0) {
//						fBuffer.append(' ');
//						appendTypeParametersLabels(typeParameters, flags);
//					}
//				}
//				if (getFlag(flags, X10ElementLabels.COLORIZE) && offset != fBuffer.length()) {
//					fBuffer.setStyle(offset, fBuffer.length() - offset, DECORATIONS_STYLE);
//				}
//			}

			if (getFlag(flags, X10ElementLabels.M_APP_RETURNTYPE) && method.exists(new NullProgressMonitor()) && !method.isConstructor()) {
				int offset= fBuffer.length();
				fBuffer.append(X10ElementLabels.DECL_STRING);
//				String returnTypeSig= resolvedSig != null ? Signature.getReturnType(resolvedSig) : method.getReturnType();
				appendTypeSignatureLabel(method, method.getReturnType(), flags);
				if (getFlag(flags, X10ElementLabels.COLORIZE)) {
					fBuffer.setStyle(offset, fBuffer.length() - offset, DECORATIONS_STYLE);
				}
			}

			// category
//			if (getFlag(flags, X10ElementLabels.M_CATEGORY) && method.exists(new NullProgressMonitor()))
//				appendCategoryLabel(method, flags);

			// post qualification
			if (getFlag(flags, X10ElementLabels.M_POST_QUALIFIED)) {
				int offset= fBuffer.length();
				fBuffer.append(X10ElementLabels.CONCAT_STRING);
				appendTypeLabel(method.getDeclaringType(), X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
				if (getFlag(flags, X10ElementLabels.COLORIZE)) {
					fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
				}
			}

//		} catch (ModelException e) {
//			X10DTUIPlugin.log(e); // NotExistsException will not reach this point
//		}
	}

//	private void appendCategoryLabel(IMemberInfo member, long flags) throws ModelException {
//		String[] categories= member.getCategories();
//		if (categories.length > 0) {
//			int offset= fBuffer.length();
//			StringBuffer categoriesBuf= new StringBuffer();
//			for (int i= 0; i < categories.length; i++) {
//				if (i > 0)
//					categoriesBuf.append(X10ElementLabels.CATEGORY_SEPARATOR_STRING);
//				categoriesBuf.append(categories[i]);
//			}
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			fBuffer.append(Messages.format(JavaUIMessages.JavaElementLabels_category, categoriesBuf.toString()));
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, COUNTER_STYLE);
//			}
//		}
//	}

	/**
	 * Appends labels for type parameters from type binding array.
	 *
	 * @param typeParameters the type parameters
	 * @param flags flags with render options
	 */
	private void appendTypeParametersLabels(ITypeInfo[] typeParameters, long flags) {
		if (typeParameters.length > 0) {
			fBuffer.append(getLT());
			for (int i = 0; i < typeParameters.length; i++) {
				if (i > 0) {
					fBuffer.append(X10ElementLabels.COMMA_STRING);
				}
				fBuffer.append(getElementName(typeParameters[i]));
			}
			fBuffer.append(getGT());
		}
	}

	/**
	 * Appends the style label for a field. Considers the F_* flags.
	 *
	 * @param field the element to render
	 * @param flags the rendering flags. Flags with names starting with 'F_' are considered.
	 */
	public void appendFieldLabel(IFieldInfo field, long flags) {
//		try {

			if (getFlag(flags, X10ElementLabels.F_PRE_TYPE_SIGNATURE) && field.exists(new NullProgressMonitor()) /*&& !Flags.isEnum(field.getX10FlagsCode()) */) {
//				if (getFlag(flags, X10ElementLabels.USE_RESOLVED) && field.isResolved()) {
//					appendTypeSignatureLabel(field, new BindingKey(field.getKey()).toSignature(), flags);
//				} else {
					appendTypeSignatureLabel(field, field.getFieldTypeInfo(), flags);
//				}
				fBuffer.append(' ');
			}

			// qualification
			if (getFlag(flags, X10ElementLabels.F_FULLY_QUALIFIED)) {
				appendTypeLabel(field.getDeclaringType(), X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
				fBuffer.append('.');
			}
			fBuffer.append(getElementName(field));

			if (getFlag(flags, X10ElementLabels.F_APP_TYPE_SIGNATURE) && field.exists(new NullProgressMonitor()) /*&& !Flags.isEnum(field.getX10FlagsCode()) */) {
				int offset= fBuffer.length();
				fBuffer.append(X10ElementLabels.DECL_STRING);
//				if (getFlag(flags, X10ElementLabels.USE_RESOLVED) && field.isResolved()) {
//					appendTypeSignatureLabel(field, new BindingKey(field.getKey()).toSignature(), flags);
//				} else {
					appendTypeSignatureLabel(field, field.getFieldTypeInfo(), flags);
//				}
				if (getFlag(flags, X10ElementLabels.COLORIZE)) {
					fBuffer.setStyle(offset, fBuffer.length() - offset, DECORATIONS_STYLE);
				}
			}

			// category
//			if (getFlag(flags, X10ElementLabels.F_CATEGORY) && field.exists(new NullProgressMonitor()))
//				appendCategoryLabel(field, flags);

			// post qualification
			if (getFlag(flags, X10ElementLabels.F_POST_QUALIFIED)) {
				int offset= fBuffer.length();
				fBuffer.append(X10ElementLabels.CONCAT_STRING);
				appendTypeLabel(field.getDeclaringType(), X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
				if (getFlag(flags, X10ElementLabels.COLORIZE)) {
					fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
				}
			}

//		} catch (ModelException e) {
//			X10DTUIPlugin.log(e); // NotExistsException will not reach this point
//		}
	}

//	/**
//	 * Appends the styled label for a local variable.
//	 *
//	 * @param localVariable the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'F_' are considered.
//	 */
//	public void appendLocalVariableLabel(ILocalVariable localVariable, long flags) {
//		if (getFlag(flags, X10ElementLabels.F_PRE_TYPE_SIGNATURE)) {
//			appendTypeSignatureLabel(localVariable, localVariable.getTypeSignature(), flags);
//			fBuffer.append(' ');
//		}
//
//		if (getFlag(flags, X10ElementLabels.F_FULLY_QUALIFIED)) {
//			appendElementLabel(localVariable.getParent(), X10ElementLabels.M_PARAMETER_TYPES | X10ElementLabels.M_FULLY_QUALIFIED | X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
//			fBuffer.append('.');
//		}
//
//		fBuffer.append(getElementName(localVariable));
//
//		if (getFlag(flags, X10ElementLabels.F_APP_TYPE_SIGNATURE)) {
//			int offset= fBuffer.length();
//			fBuffer.append(X10ElementLabels.DECL_STRING);
//			appendTypeSignatureLabel(localVariable, localVariable.getTypeSignature(), flags);
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, DECORATIONS_STYLE);
//			}
//		}
//
//		// post qualification
//		if (getFlag(flags, X10ElementLabels.F_POST_QUALIFIED)) {
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendElementLabel(localVariable.getParent(), X10ElementLabels.M_PARAMETER_TYPES | X10ElementLabels.M_FULLY_QUALIFIED | X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
//		}
//	}

//	/**
//	 * Appends the styled label for a type parameter.
//	 *
//	 * @param typeParameter the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'T_' are considered.
//	 */
//	public void appendTypeParameterLabel(ITypeInfo typeParameter, long flags) {
//		try {
//			fBuffer.append(getElementName(typeParameter));
//
//			if (typeParameter.exists()) {
//				String[] bounds= typeParameter.getBoundsSignatures();
//				if (bounds.length > 0 &&
//						! (bounds.length == 1 && "Ljava.lang.Object;".equals(bounds[0]))) { //$NON-NLS-1$
//					fBuffer.append(" extends "); //$NON-NLS-1$
//					for (int j= 0; j < bounds.length; j++) {
//						if (j > 0) {
//							fBuffer.append(X10ElementLabels.COMMA_STRING);
//						}
//						appendTypeSignatureLabel(typeParameter, bounds[j], flags);
//					}
//				}
//			}
//
//			// post qualification
//			if (getFlag(flags, X10ElementLabels.TP_POST_QUALIFIED)) {
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				IMemberInfo declaringMember= typeParameter.getDeclaringMember();
//				appendElementLabel(declaringMember, X10ElementLabels.M_PARAMETER_TYPES | X10ElementLabels.M_FULLY_QUALIFIED | X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
//			}
//
//		} catch (ModelException e) {
//			X10DTUIPlugin.log(e); // NotExistsException will not reach this point
//		}
//	}

//	/**
//	 * Appends the label for a initializer. Considers the I_* flags.
//	 *
//	 * @param initializer the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'I_' are considered.
//	 */
//	public void appendInitializerLabel(IInitializer initializer, long flags) {
//		// qualification
//		if (getFlag(flags, X10ElementLabels.I_FULLY_QUALIFIED)) {
//			appendTypeLabel(initializer.getDeclaringType(), X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
//			fBuffer.append('.');
//		}
//		fBuffer.append(JavaUIMessages.JavaElementLabels_initializer);
//
//		// post qualification
//		if (getFlag(flags, X10ElementLabels.I_POST_QUALIFIED)) {
//			int offset= fBuffer.length();
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendTypeLabel(initializer.getDeclaringType(), X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
//	}

	protected void appendTypeSignatureLabel(IX10Element enclosingElement, ITypeInfo typeSig, long flags) {
//		int sigKind= Signature.getTypeSignatureKind(typeSig);
//		switch (sigKind) {
//			case Signature.BASE_TYPE_SIGNATURE:
//				fBuffer.append(Signature.toString(typeSig));
//				break;
//			case Signature.ARRAY_TYPE_SIGNATURE:
//				appendTypeSignatureLabel(enclosingElement, Signature.getElementType(typeSig), flags);
//				for (int dim= Signature.getArrayCount(typeSig); dim > 0; dim--) {
//					fBuffer.append('[').append(']');
//				}
//				break;
//			case Signature.CLASS_TYPE_SIGNATURE:
//				String baseType= getSimpleTypeName(enclosingElement, Signature.getTypeErasure(typeSig));
//				fBuffer.append(baseType);
//
//				String[] typeArguments= Signature.getTypeArguments(typeSig);
//				appendTypeArgumentSignaturesLabel(enclosingElement, typeArguments, flags);
//				break;
//			case Signature.TYPE_VARIABLE_SIGNATURE:
//				fBuffer.append(getSimpleTypeName(enclosingElement, typeSig));
//				break;
//			case Signature.WILDCARD_TYPE_SIGNATURE:
//				char ch= typeSig.charAt(0);
//				if (ch == Signature.C_STAR) { //workaround for bug 85713
//					fBuffer.append('?');
//				} else {
//					if (ch == Signature.C_EXTENDS) {
//						fBuffer.append("? extends "); //$NON-NLS-1$
//						appendTypeSignatureLabel(enclosingElement, typeSig.substring(1), flags);
//					} else if (ch == Signature.C_SUPER) {
//						fBuffer.append("? super "); //$NON-NLS-1$
//						appendTypeSignatureLabel(enclosingElement, typeSig.substring(1), flags);
//					}
//				}
//				break;
//			case Signature.CAPTURE_TYPE_SIGNATURE:
//				appendTypeSignatureLabel(enclosingElement, typeSig.substring(1), flags);
//				break;
//			default:
//				// unknown
//		}
		fBuffer.append(SearchUtils.getElementName(typeSig));
	}

	/**
	 * Returns the simple name of the given type signature.
	 *
	 * @param enclosingElement the enclosing element in which to resolve the signature
	 * @param typeSig a {@link Signature#CLASS_TYPE_SIGNATURE} or {@link Signature#TYPE_VARIABLE_SIGNATURE}
	 * @return the simple name of the given type signature
	 */
	protected String getSimpleTypeName(IX10Element enclosingElement, String typeSig) {
		return Signature.toString(typeSig);
	}

//	private void appendTypeArgumentSignaturesLabel(IX10Element enclosingElement, String[] typeArgsSig, long flags) {
//		if (typeArgsSig.length > 0) {
//			fBuffer.append(getLT());
//			for (int i = 0; i < typeArgsSig.length; i++) {
//				if (i > 0) {
//					fBuffer.append(X10ElementLabels.COMMA_STRING);
//				}
//				appendTypeSignatureLabel(enclosingElement, typeArgsSig[i], flags);
//			}
//			fBuffer.append(getGT());
//		}
//	}

	/**
	 * Appends labels for type parameters from a signature.
	 *
	 * @param typeParamSigs the type parameter signature
	 * @param flags flags with render options
	 */
	private void appendTypeParameterSignaturesLabel(String[] typeParamSigs, long flags) {
		if (typeParamSigs.length > 0) {
			fBuffer.append(getLT());
			for (int i = 0; i < typeParamSigs.length; i++) {
				if (i > 0) {
					fBuffer.append(X10ElementLabels.COMMA_STRING);
				}
//				fBuffer.append(Signature.getTypeVariable(typeParamSigs[i]));
			}
			fBuffer.append(getGT());
		}
	}

	/**
	 * Returns the string for rendering the '<code>&lt;</code>' character.
	 *
	 * @return the string for rendering '<code>&lt;</code>'
	 */
	protected String getLT() {
		return "<"; //$NON-NLS-1$
	}

	/**
	 * Returns the string for rendering the '<code>&gt;</code>' character.
	 *
	 * @return the string for rendering '<code>&gt;</code>'
	 */
	protected String getGT() {
		return ">"; //$NON-NLS-1$
	}

	/**
	 * Appends the label for a type. Considers the T_* flags.
	 *
	 * @param type the element to render
	 * @param flags the rendering flags. Flags with names starting with 'T_' are considered.
	 */
	public void appendTypeLabel(ITypeInfo type, long flags) {

		if (getFlag(flags, X10ElementLabels.T_FULLY_QUALIFIED)) {
//			IPackageFragment pack= type.getPackageFragment();
			String pack = SearchUtils.getPackageName(type);
			if (!SearchUtils.isDefaultPackage(pack)) {
				appendPackageFragmentLabel(pack, (flags & QUALIFIER_FLAGS));
				fBuffer.append('.');
			}
		}
		if (getFlag(flags, X10ElementLabels.T_FULLY_QUALIFIED | X10ElementLabels.T_CONTAINER_QUALIFIED)) {
			ITypeInfo declaringType= type.getDeclaringType();
			if (declaringType != null) {
				appendTypeLabel(declaringType, X10ElementLabels.T_CONTAINER_QUALIFIED | (flags & QUALIFIER_FLAGS));
				fBuffer.append('.');
			}
//			int parentType= type.getParent().getElementType();
//			if (parentType == IX10Element.METHOD || parentType == IX10Element.FIELD || parentType == IX10Element.INITIALIZER) { // anonymous or local
//				appendElementLabel(type.getParent(), 0);
//				fBuffer.append('.');
//			}
		}

		String typeName= getElementName(type);
		if (typeName.length() == 0) { // anonymous
			try {
//				if (type.getParent() instanceof IFieldInfo && type.isEnum()) {
//					typeName= '{' + X10ElementLabels.ELLIPSIS_STRING + '}';
//				} else {
					String supertypeName;
				ITypeHierarchy hierarchy = X10SearchEngine.createTypeHierarchy(
						SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL), type, new NullProgressMonitor());

						
					ITypeInfo[] superInterfaceSignatures= hierarchy.getAllSuperInterfaces(type);
					if (superInterfaceSignatures.length > 0) {
						supertypeName= getSimpleTypeName(type, superInterfaceSignatures[0].getName());
					} else {
						supertypeName= getSimpleTypeName(type, hierarchy.getSuperClass(type).getName());
					}
					typeName= MessageFormat.format(Messages.X10ElementLabels_anonym_type , supertypeName);
//				}
			} catch (Exception e) {
				//ignore
				typeName= Messages.X10ElementLabels_anonym;
			}
		}
		fBuffer.append(typeName);
		if (getFlag(flags, X10ElementLabels.T_TYPE_PARAMETERS)) {
//			if (getFlag(flags, X10ElementLabels.USE_RESOLVED) && type.isResolved()) {
//				BindingKey key= new BindingKey(type.getKey());
//				if (key.isParameterizedType()) {
//					String[] typeArguments= key.getTypeArguments();
//					appendTypeArgumentSignaturesLabel(type, typeArguments, flags);
//				} else {
//					String[] typeParameters= Signature.getTypeParameters(key.toSignature());
//					appendTypeParameterSignaturesLabel(typeParameters, flags);
//				}
//			} 
//			else if (type.exists()) {
//				try {
//					appendTypeParametersLabels(type.getTypeParameters(), flags);
//				} catch (ModelException e) {
//					// ignore
//				}
//			}
		}

//		// category
//		if (getFlag(flags, X10ElementLabels.T_CATEGORY) && type.exists()) {
//			try {
//				appendCategoryLabel(type, flags);
//			} catch (ModelException e) {
//				// ignore
//			}
//		}

		// post qualification
		if (getFlag(flags, X10ElementLabels.T_POST_QUALIFIED)) {
			int offset= fBuffer.length();
			fBuffer.append(X10ElementLabels.CONCAT_STRING);
			ITypeInfo declaringType= type.getDeclaringType();
			if (declaringType != null) {
				appendTypeLabel(declaringType, X10ElementLabels.T_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS));
//				int parentType= type.getParent().getElementType();
//				if (parentType == IX10Element.METHOD || parentType == IX10Element.FIELD || parentType == IX10Element.INITIALIZER) { // anonymous or local
//					fBuffer.append('.');
//					appendElementLabel(type.getParent(), 0);
//				}
			} 
			else {
				appendPackageFragmentLabel(SearchUtils.getPackageName(type), flags & QUALIFIER_FLAGS);
			}
			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
			}
		}
	}

	/**
	 * Returns the string for rendering the {@link IX10Element#getElementName() element name} of the given element.
	 *
	 * @param element the element to render
	 * @return the string for rendering the element name
	 */
	protected String getElementName(IMemberInfo element) {
		return SearchUtils.getElementName(element);
	}


//	/**
//	 * Appends the label for a import container, import or package declaration. Considers the D_* flags.
//	 *
//	 * @param declaration the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'D_' are considered.
//	 */
//	public void appendDeclarationLabel(IX10Element declaration, long flags) {
//		if (getFlag(flags, X10ElementLabels.D_QUALIFIED)) {
//			IX10Element openable= (IX10Element) declaration.getOpenable();
//			if (openable != null) {
//				appendElementLabel(openable, X10ElementLabels.CF_QUALIFIED | X10ElementLabels.CU_QUALIFIED | (flags & QUALIFIER_FLAGS));
//				fBuffer.append('/');
//			}
//		}
//		if (declaration.getElementType() == IX10Element.IMPORT_CONTAINER) {
//			fBuffer.append(JavaUIMessages.JavaElementLabels_import_container);
//		} else {
//			fBuffer.append(declaration.getElementName());
//		}
//		// post qualification
//		if (getFlag(flags, X10ElementLabels.D_POST_QUALIFIED)) {
//			int offset= fBuffer.length();
//			IX10Element openable= (IX10Element) declaration.getOpenable();
//			if (openable != null) {
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				appendElementLabel(openable, X10ElementLabels.CF_QUALIFIED | X10ElementLabels.CU_QUALIFIED | (flags & QUALIFIER_FLAGS));
//			}
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
//	}

//	/**
//	 * Appends the label for a class file. Considers the CF_* flags.
//	 *
//	 * @param classFile the element to render
//	 * @param flags the rendering flags. Flags with names starting with 'CF_' are considered.
//	 */
//	public void appendClassFileLabel(IClassFile classFile, long flags) {
//		if (getFlag(flags, X10ElementLabels.CF_QUALIFIED)) {
//			IPackageFragment pack= (IPackageFragment) classFile.getParent();
//			if (!pack.isDefaultPackage()) {
//				appendPackageFragmentLabel(pack, (flags & QUALIFIER_FLAGS));
//				fBuffer.append('.');
//			}
//		}
//		fBuffer.append(classFile.getElementName());
//
//		if (getFlag(flags, X10ElementLabels.CF_POST_QUALIFIED)) {
//			int offset= fBuffer.length();
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendPackageFragmentLabel((IPackageFragment) classFile.getParent(), flags & QUALIFIER_FLAGS);
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
//	}

	/**
	 * Appends the label for a compilation unit. Considers the CU_* flags.
	 *
	 * @param cu the element to render
	 * @param flags the rendering flags. Flags with names starting with 'CU_' are considered.
	 */
	public void appendCompilationUnitLabel(ICompilationUnit cu, long flags) {
//		if (getFlag(flags, X10ElementLabels.CU_QUALIFIED)) {
//			IPackageFragment pack= (IPackageFragment) cu.getParent();
//			if (!pack.isDefaultPackage()) {
//				appendPackageFragmentLabel(pack, (flags & QUALIFIER_FLAGS));
//				fBuffer.append('.');
//			}
//		}
		fBuffer.append(cu.getName());

		if (getFlag(flags, X10ElementLabels.CU_POST_QUALIFIED)) {
			int offset= fBuffer.length();
			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendPackageFragmentLabel((IPackageFragment) cu.getParent(), flags & QUALIFIER_FLAGS);
			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
			}
		}
	}

	/**
	 * Appends the label for a package fragment. Considers the P_* flags.
	 *
	 * @param pack the element to render
	 * @param flags the rendering flags. Flags with names starting with P_' are considered.
	 */
	public void appendPackageFragmentLabel(String pack, long flags) {
//		if (getFlag(flags, X10ElementLabels.P_QUALIFIED)) {
//			appendPackageFragmentRootLabel((IPackageFragmentRoot) pack.getParent(), X10ElementLabels.ROOT_QUALIFIED);
//			fBuffer.append('/');
//		}
		if (SearchUtils.isDefaultPackage(pack)) {
			fBuffer.append(X10ElementLabels.DEFAULT_PACKAGE);
		} 
//		else if (getFlag(flags, X10ElementLabels.P_COMPRESSED)) {
//			if (isPackageNameAbbreviationEnabled())
//				appendAbbreviatedPackageFragment(pack);
//			else
//				appendCompressedPackageFragment(pack);
//		} 
		else {
			fBuffer.append(pack);
		}
//		if (getFlag(flags, X10ElementLabels.P_POST_QUALIFIED)) {
//			int offset= fBuffer.length();
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendPackageFragmentRootLabel((IPackageFragmentRoot) pack.getParent(), X10ElementLabels.ROOT_QUALIFIED);
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
	}
//
//	private void appendCompressedPackageFragment(IPackageFragment pack) {
//		appendCompressedPackageFragment(pack.getElementName());
//	}
//	
//	private void appendCompressedPackageFragment(String elementName) {
//		refreshPackageNamePattern();
//		if (fgPkgNameLength < 0) {
//			fBuffer.append(elementName);
//			return;
//		}
//		String name= elementName;
//		int start= 0;
//		int dot= name.indexOf('.', start);
//		while (dot > 0) {
//			if (dot - start > fgPkgNameLength-1) {
//				fBuffer.append(fgPkgNamePrefix);
//				if (fgPkgNameChars > 0)
//					fBuffer.append(name.substring(start, Math.min(start+ fgPkgNameChars, dot)));
//				fBuffer.append(fgPkgNamePostfix);
//			} else
//				fBuffer.append(name.substring(start, dot + 1));
//			start= dot + 1;
//			dot= name.indexOf('.', start);
//		}
//		fBuffer.append(name.substring(start));
//	}
//
//	private void appendAbbreviatedPackageFragment(IPackageFragment pack) {
//		refreshPackageNameAbbreviation();
//
//		String pkgName= pack.getElementName();
//
//		if (fgPkgNameAbbreviation != null && fgPkgNameAbbreviation.length != 0) {
//
//			for (int i= 0; i < fgPkgNameAbbreviation.length; i++) {
//				PackageNameAbbreviation abbr= fgPkgNameAbbreviation[i];
//
//				String abbrPrefix= abbr.getPackagePrefix();
//				if (pkgName.startsWith(abbrPrefix)) {
//					int abbrPrefixLength= abbrPrefix.length();
//					int pkgLength= pkgName.length();
//					if (!(pkgLength == abbrPrefixLength || pkgName.charAt(abbrPrefixLength) == '.'))
//						continue;
//
//					fBuffer.append(abbr.getAbbreviation());
//
//					if (pkgLength > abbrPrefixLength) {
//						fBuffer.append('.');
//
//						String remaining= pkgName.substring(abbrPrefixLength + 1);
//
//						if (isPackageNameCompressionEnabled())
//							appendCompressedPackageFragment(remaining);
//						else
//							fBuffer.append(remaining);
//					}
//
//					return;
//				}
//			}
//		}
//
//		if (isPackageNameCompressionEnabled()) {
//			appendCompressedPackageFragment(pkgName);
//		} else {
//			fBuffer.append(pkgName);
//		}
//	}
//
//	/**
//	 * Appends the label for a package fragment root. Considers the ROOT_* flags.
//	 *
//	 * @param root the element to render
//	 * @param flags the rendering flags. Flags with names starting with ROOT_' are considered.
//	 */
//	public void appendPackageFragmentRootLabel(IPackageFragmentRoot root, long flags) {
//		// Handle variables different
//		if (getFlag(flags, X10ElementLabels.ROOT_VARIABLE) && appendVariableLabel(root, flags))
//			return;
//		if (root.isArchive())
//			appendArchiveLabel(root, flags);
//		else
//			appendFolderLabel(root, flags);
//	}
//
//	private void appendArchiveLabel(IPackageFragmentRoot root, long flags) {
//		boolean external= root.isExternal();
//		if (external)
//			appendExternalArchiveLabel(root, flags);
//		else
//			appendInternalArchiveLabel(root, flags);
//	}
//
//	private boolean appendVariableLabel(IPackageFragmentRoot root, long flags) {
//		try {
//			IClasspathEntry rawEntry= root.getRawClasspathEntry();
//			if (rawEntry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
//				IClasspathEntry entry= ModelUtil.getClasspathEntry(root);
//				if (entry.getReferencingEntry() != null) {
//					return false; // not the variable entry itself, but a referenced entry
//				}
//				IPath path= rawEntry.getPath().makeRelative();
//
//				if (getFlag(flags, X10ElementLabels.REFERENCED_ROOT_POST_QUALIFIED)) {
//					int segements= path.segmentCount();
//					if (segements > 0) {
//						fBuffer.append(path.segment(segements - 1));
//						if (segements > 1) {
//							int offset= fBuffer.length();
//							fBuffer.append(X10ElementLabels.CONCAT_STRING);
//							fBuffer.append(path.removeLastSegments(1).toOSString());
//							if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//								fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//							}
//						}
//					} else {
//						fBuffer.append(path.toString());
//					}
//				} else {
//					fBuffer.append(path.toString());
//				}
//				int offset= fBuffer.length();
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				if (root.isExternal())
//					fBuffer.append(root.getPath().toOSString());
//				else
//					fBuffer.append(root.getPath().makeRelative().toString());
//
//				if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//					fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//				}
//				return true;
//			}
//		} catch (ModelException e) {
//			// problems with class path, ignore (bug 202792)
//			return false;
//		}
//		return false;
//	}
//
//	private void appendExternalArchiveLabel(IPackageFragmentRoot root, long flags) {
//		IPath path;
//		IClasspathEntry classpathEntry= null;
//		try {
//			classpathEntry= ModelUtil.getClasspathEntry(root);
//			IPath rawPath= classpathEntry.getPath();
//			if (classpathEntry.getEntryKind() != IClasspathEntry.CPE_CONTAINER && !rawPath.isAbsolute())
//				path= rawPath;
//			else
//				path= root.getPath();
//		} catch (ModelException e) {
//			path= root.getPath();
//		}
//		if (getFlag(flags, X10ElementLabels.REFERENCED_ROOT_POST_QUALIFIED)) {
//			int segements= path.segmentCount();
//			if (segements > 0) {
//				fBuffer.append(path.segment(segements - 1));
//				int offset= fBuffer.length();
//				if (segements > 1 || path.getDevice() != null) {
//					fBuffer.append(X10ElementLabels.CONCAT_STRING);
//					fBuffer.append(path.removeLastSegments(1).toOSString());
//				}
//				if (classpathEntry != null) {
//					IClasspathEntry referencingEntry= classpathEntry.getReferencingEntry();
//					if (referencingEntry != null) {
//						fBuffer.append(Messages.format(JavaUIMessages.JavaElementLabels_onClassPathOf, new Object[] { Name.CLASS_PATH.toString(), referencingEntry.getPath().lastSegment() }));
//					}
//				}
//				if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//					fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//				}
//			} else {
//				fBuffer.append(path.toOSString());
//			}
//		} else {
//			fBuffer.append(path.toOSString());
//		}
//	}
//
//	private void appendInternalArchiveLabel(IPackageFragmentRoot root, long flags) {
//		IResource resource= root.getResource();
//		boolean rootQualified= getFlag(flags, X10ElementLabels.ROOT_QUALIFIED);
//		if (rootQualified) {
//			fBuffer.append(root.getPath().makeRelative().toString());
//		} else {
//			fBuffer.append(root.getElementName());
//			int offset= fBuffer.length();
//			boolean referencedPostQualified= getFlag(flags, X10ElementLabels.REFERENCED_ROOT_POST_QUALIFIED);
//			if (referencedPostQualified && isReferenced(root)) {
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				fBuffer.append(resource.getParent().getFullPath().makeRelative().toString());
//			} else if (getFlag(flags, X10ElementLabels.ROOT_POST_QUALIFIED)) {
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				fBuffer.append(root.getParent().getPath().makeRelative().toString());
//			}
//			if (referencedPostQualified) {
//				try {
//					IClasspathEntry referencingEntry= ModelUtil.getClasspathEntry(root).getReferencingEntry();
//					if (referencingEntry != null) {
//						fBuffer.append(Messages.format(JavaUIMessages.JavaElementLabels_onClassPathOf, new Object[] { Name.CLASS_PATH.toString(), referencingEntry.getPath().lastSegment() }));
//					}
//				} catch (ModelException e) {
//					// ignore
//				}
//			}
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
//	}
//
//	private void appendFolderLabel(IPackageFragmentRoot root, long flags) {
//		IResource resource= root.getResource();
//		if (resource == null) {
//			appendExternalArchiveLabel(root, flags);
//			return;
//		}
//
//		boolean rootQualified= getFlag(flags, X10ElementLabels.ROOT_QUALIFIED);
//		boolean referencedQualified= getFlag(flags, X10ElementLabels.REFERENCED_ROOT_POST_QUALIFIED) && isReferenced(root);
//		if (rootQualified) {
//			fBuffer.append(root.getPath().makeRelative().toString());
//		} else {
//			IPath projectRelativePath= resource.getProjectRelativePath();
//			if (projectRelativePath.segmentCount() == 0) {
//				fBuffer.append(resource.getName());
//				referencedQualified= false;
//			} else {
//				fBuffer.append(projectRelativePath.toString());
//			}
//
//			int offset= fBuffer.length();
//			if (referencedQualified) {
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				fBuffer.append(resource.getProject().getName());
//			} else if (getFlag(flags, X10ElementLabels.ROOT_POST_QUALIFIED)) {
//				fBuffer.append(X10ElementLabels.CONCAT_STRING);
//				fBuffer.append(root.getParent().getElementName());
//			} else {
//				return;
//			}
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
//	}
//
//	/**
//	 * Returns <code>true</code> if the given package fragment root is
//	 * referenced. This means it is a descendant of a different project but is referenced
//	 * by the root's parent. Returns <code>false</code> if the given root
//	 * doesn't have an underlying resource.
//	 *
//	 * @param root the package fragment root
//	 * @return returns <code>true</code> if the given package fragment root is referenced
//	 */
//	private boolean isReferenced(IPackageFragmentRoot root) {
//		IResource resource= root.getResource();
//		if (resource != null) {
//			IProject jarProject= resource.getProject();
//			IProject container= root.getJavaProject().getProject();
//			return !container.equals(jarProject);
//		}
//		return false;
//	}

//	private void refreshPackageNamePattern() {
//		String pattern= getPkgNamePatternForPackagesView();
//		final String EMPTY_STRING= ""; //$NON-NLS-1$
//		if (pattern.equals(fgPkgNamePattern))
//			return;
//		else if (pattern.length() == 0) {
//			fgPkgNamePattern= EMPTY_STRING;
//			fgPkgNameLength= -1;
//			return;
//		}
//		fgPkgNamePattern= pattern;
//		int i= 0;
//		fgPkgNameChars= 0;
//		fgPkgNamePrefix= EMPTY_STRING;
//		fgPkgNamePostfix= EMPTY_STRING;
//		while (i < pattern.length()) {
//			char ch= pattern.charAt(i);
//			if (Character.isDigit(ch)) {
//				fgPkgNameChars= ch-48;
//				if (i > 0)
//					fgPkgNamePrefix= pattern.substring(0, i);
//				if (i >= 0)
//					fgPkgNamePostfix= pattern.substring(i+1);
//				fgPkgNameLength= fgPkgNamePrefix.length() + fgPkgNameChars + fgPkgNamePostfix.length();
//				return;
//			}
//			i++;
//		}
//		fgPkgNamePrefix= pattern;
//		fgPkgNameLength= pattern.length();
//	}
	
//	private void refreshPackageNameAbbreviation() {
//		String pattern= getPkgNameAbbreviationPatternForPackagesView();
//
//		if (fgPkgNameAbbreviationPattern.equals(pattern))
//			return;
//
//		fgPkgNameAbbreviationPattern= pattern;
//
//		if (pattern == null || pattern.length() == 0) {
//			fgPkgNameAbbreviationPattern= ""; //$NON-NLS-1$
//			fgPkgNameAbbreviation= null;
//			return;
//		}
//
//		PackageNameAbbreviation[] abbrs= parseAbbreviationPattern(pattern);
//
//		if (abbrs == null)
//			abbrs= new PackageNameAbbreviation[0];
//
//		fgPkgNameAbbreviation= abbrs;
//	}

	public static PackageNameAbbreviation[] parseAbbreviationPattern(String pattern) {
		String[] parts= pattern.split("\\s*(?:\r\n?|\n)\\s*"); //$NON-NLS-1$

		ArrayList result= new ArrayList();

		for (int i= 0; i < parts.length; i++) {
			String part= parts[i].trim();

			if (part.length() == 0)
				continue;

			String[] parts2= part.split("\\s*=\\s*", 2); //$NON-NLS-1$

			if (parts2.length != 2)
				return null;

			String prefix= parts2[0].trim();
			String abbr= parts2[1].trim();

			if (prefix.startsWith("#")) //$NON-NLS-1$
				continue;

			PackageNameAbbreviation pkgAbbr= new PackageNameAbbreviation(prefix, abbr);

			result.add(pkgAbbr);
		}

		Collections.sort(result, new Comparator() {
			public int compare(Object o1, Object o2) {
				PackageNameAbbreviation a1= (PackageNameAbbreviation)o1;
				PackageNameAbbreviation a2= (PackageNameAbbreviation)o2;

				return a2.getPackagePrefix().length() - a1.getPackagePrefix().length();
			}
		});

		return (PackageNameAbbreviation[])result.toArray(new PackageNameAbbreviation[0]);
	}
	
//	private boolean isPackageNameCompressionEnabled() {
//		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
//		return store.getBoolean(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES);
//	}
//
//	private String getPkgNamePatternForPackagesView() {
//		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
//		if (!store.getBoolean(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES))
//			return ""; //$NON-NLS-1$
//		return store.getString(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW);
//	}
//
//	private boolean isPackageNameAbbreviationEnabled() {
//		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
//		return store.getBoolean(JavaElementLabelComposer.APPEARANCE_ABBREVIATE_PACKAGE_NAMES);
//	}
//
//	private String getPkgNameAbbreviationPatternForPackagesView() {
//		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
//		if (!store.getBoolean(JavaElementLabelComposer.APPEARANCE_ABBREVIATE_PACKAGE_NAMES))
//			return ""; //$NON-NLS-1$
//		return store.getString(JavaElementLabelComposer.APPEARANCE_PKG_NAME_ABBREVIATION_PATTERN_FOR_PKG_VIEW);
//	}

}
