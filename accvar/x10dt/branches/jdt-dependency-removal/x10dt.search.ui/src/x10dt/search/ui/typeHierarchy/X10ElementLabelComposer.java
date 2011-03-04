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
package x10dt.search.ui.typeHierarchy;

import java.text.MessageFormat;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.ui.ElementLabelComposer;
import org.eclipse.imp.ui.UIMessages;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.jface.viewers.StyledString;

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
public class X10ElementLabelComposer extends ElementLabelComposer{

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
	
	
	
	
	public X10ElementLabelComposer(FlexibleBuffer buffer) {
		super(buffer);
	}



	public X10ElementLabelComposer(StringBuffer buffer) {
		super(buffer);
	}



	public X10ElementLabelComposer(StyledString buffer) {
		super(buffer);
	}



	/**
	 * Appends the label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags.
	 */
	public void appendElementLabel(Object element, long flags) {
		ISourceRoot root= null;

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
//			case IX10Element.IMPORT_CONTAINER:
//			case IX10Element.IMPORT_DECLARATION:
//			case IX10Element.PACKAGE_DECLARATION:
//				appendDeclarationLabel(element, flags);
//				break;
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

			fBuffer.append(getName(method));

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
//								// UISearchPlugin.logErrorMessage("X10ElementLabels: Number of param types(" + nParams + ") != number of names(" + names.length + "): " + method.getName());   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
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
//			UISearchPlugin.log(e); // NotExistsException will not reach this point
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
				fBuffer.append(getName(typeParameters[i]));
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
			fBuffer.append(getName(field));

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
//			UISearchPlugin.log(e); // NotExistsException will not reach this point
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
//		fBuffer.append(getName(localVariable));
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
//			fBuffer.append(getName(typeParameter));
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
//			UISearchPlugin.log(e); // NotExistsException will not reach this point
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
			ISourceFolder pack= SearchUtils.getSourceFolder(type);
			if (pack != null && !BuildPathUtils.isDefaultPackage(pack.getName())) {
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

		String typeName= getName(type);
		if (typeName.length() == 0) { // anonymous
			try {
//				if (type.getParent() instanceof IFieldInfo && type.isEnum()) {
//					typeName= '{' + X10ElementLabels.ELLIPSIS_STRING + '}';
//				} else {
					String supertypeName;
				ITypeHierarchy hierarchy = X10SearchEngine.createTypeHierarchy(
						SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL), typeName, new NullProgressMonitor());

						
					ITypeInfo[] superInterfaceSignatures= hierarchy.getAllSuperInterfaces(typeName);
					if (superInterfaceSignatures.length > 0) {
						supertypeName= getSimpleTypeName(type, superInterfaceSignatures[0].getName());
					} else {
						supertypeName= getSimpleTypeName(type, hierarchy.getSuperClass(typeName).getName());
					}
					typeName= MessageFormat.format(UIMessages.ElementLabels_anonym_type , supertypeName);
//				}
			} catch (Exception e) {
				//ignore
				typeName= UIMessages.ElementLabels_anonym;
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
				appendPackageFragmentLabel(SearchUtils.getSourceFolder(type), flags & QUALIFIER_FLAGS);
			}
			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
			}
		}
	}

	/**
	 * Returns the string for rendering the {@link IX10Element#getName() element name} of the given element.
	 *
	 * @param element the element to render
	 * @return the string for rendering the element name
	 */
	protected String getName(IMemberInfo element) {
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
//			fBuffer.append(declaration.getName());
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
//			ISourceFolder pack= (ISourceFolder) classFile.getParent();
//			if (!pack.isDefaultPackage()) {
//				appendPackageFragmentLabel(pack, (flags & QUALIFIER_FLAGS));
//				fBuffer.append('.');
//			}
//		}
//		fBuffer.append(classFile.getName());
//
//		if (getFlag(flags, X10ElementLabels.CF_POST_QUALIFIED)) {
//			int offset= fBuffer.length();
//			fBuffer.append(X10ElementLabels.CONCAT_STRING);
//			appendPackageFragmentLabel((ISourceFolder) classFile.getParent(), flags & QUALIFIER_FLAGS);
//			if (getFlag(flags, X10ElementLabels.COLORIZE)) {
//				fBuffer.setStyle(offset, fBuffer.length() - offset, QUALIFIER_STYLE);
//			}
//		}
//	}
}
