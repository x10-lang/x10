package x10dt.ui.typeHierarchy;
/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


import x10dt.search.core.elements.ITypeInfo;



/**
 * A match collected while {@link SearchEngine searching} for
 * all type names methods using a {@link TypeNameRequestor requestor}.
 * <p>
 * The type of this match is available from {@link #getType()}.
 * </p>
 * <p>
 * This class is not intended to be overridden by clients.
 * </p>
 *
 * @see TypeNameMatchRequestor
 * @see SearchEngine#searchAllTypeNames(char[], int, char[], int, int, IJavaSearchScope, TypeNameMatchRequestor, int, org.eclipse.core.runtime.IProgressMonitor)
 * @see SearchEngine#searchAllTypeNames(char[][], char[][], IJavaSearchScope, TypeNameMatchRequestor, int, org.eclipse.core.runtime.IProgressMonitor)
 * @since 3.3
 */
public abstract class TypeNameMatch {

/**
 * Returns the matched type's fully qualified name using '.' character
 * as separator (e.g. package name + '.' enclosing type names + '.' simple name).
 *
 * @see #getType()
 * @see IType#getFullyQualifiedName(char)
 *
 * @throws NullPointerException if matched type is <code> null</code>
 * @return Fully qualified type name of the type
 */
public String getFullyQualifiedName() {
	return SearchUtils.getFullyQualifiedName(getType());
}

/**
 * Returns the modifiers of the matched type.
 * <p>
 * This is a handle-only method as neither Java Model nor classpath
 * initialization is done while calling this method.
 *
 * @return the type modifiers
 */
public abstract int getModifiers();

/**
 * Returns the package name of the stored type.
 *
 * @see #getType()
 * @see IType#getPackageFragment()
 *
 * @throws NullPointerException if matched type is <code> null</code>
 * @return the package name
 */
public String getPackageName() {
	return SearchUtils.getPackageName(getType());
}

/**
 * Returns the name of the stored type.
 *
 * @see #getType()
 * @see IJavaElement#getElementName()
 *
 * @throws NullPointerException if matched type is <code> null</code>
 * @return the type name
 */
public String getSimpleTypeName() {
	return SearchUtils.getElementName(getType());
}

/**
 * Returns a java model type handle.
 * This handle may exist or not, but is not supposed to be <code>null</code>.
 * <p>
 * This is a handle-only method as neither Java Model nor classpath
 * initializations are done while calling this method.
 *
 * @see IType
 * @return the non-null handle on matched java model type.
 */
public abstract ITypeInfo getType();

/**
 * Name of the type container using '.' character
 * as separator (e.g. package name + '.' + enclosing type names).
 *
 * @see #getType()
 * @see IMember#getDeclaringType()
 *
 * @throws NullPointerException if matched type is <code> null</code>
 * @return name of the type container
 */
public String getTypeContainerName() {
	return SearchUtils.getTypeContainerName(getType());
}

/**
 * Returns the matched type's type qualified name using '.' character
 * as separator (e.g. enclosing type names + '.' + simple name).
 *
 * @see #getType()
 * @see IType#getTypeQualifiedName(char)
 *
 * @throws NullPointerException if matched type is <code> null</code>
 * @return fully qualified type name of the type
 */
public String getTypeQualifiedName() {
	return SearchUtils.getTypeQualifiedName(getType());
}
}
