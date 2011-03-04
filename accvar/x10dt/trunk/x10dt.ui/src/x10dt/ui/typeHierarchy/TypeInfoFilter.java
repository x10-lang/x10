package x10dt.ui.typeHierarchy;
/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


import org.eclipse.ui.dialogs.SearchPattern;

import x10dt.search.core.engine.scope.IX10SearchScope;


public class TypeInfoFilter {

	private final String fText;
	private final IX10SearchScope fSearchScope;
	//private final boolean fIsWorkspaceScope;
	private final int fElementKind;
	private final ITypeInfoFilterExtension fFilterExtension;
	private final TypeInfoRequestorAdapter fAdapter= new TypeInfoRequestorAdapter();

	private final SearchPattern fPackageMatcher;
	private final SearchPattern fNameMatcher;

	//private static final int TYPE_MODIFIERS= Flags.AccEnum | Flags.AccAnnotation | Flags.AccInterface;

	public TypeInfoFilter(String text, IX10SearchScope scope, int elementKind, ITypeInfoFilterExtension extension) {
		fText= text;
		fSearchScope= scope;
		//fIsWorkspaceScope= fSearchScope.equals(APPLICATION);
		fElementKind= elementKind;
		fFilterExtension= extension;

		int index= text.lastIndexOf("."); //$NON-NLS-1$
		if (index == -1) {
			fNameMatcher= new SearchPattern();
			fNameMatcher.setPattern(text);
			fPackageMatcher= null;
		} else {
			fPackageMatcher= new SearchPattern();
			fPackageMatcher.setPattern(evaluatePackagePattern(text.substring(0, index)));
			String name= text.substring(index + 1);
			if (name.length() == 0)
				name= "*"; //$NON-NLS-1$
			fNameMatcher= new SearchPattern();
			fNameMatcher.setPattern(name);
		}
	}

	/*
	 * Transforms o.e.j  to o*.e*.j*
	 */
	private String evaluatePackagePattern(String s) {
		StringBuffer buf= new StringBuffer();
		boolean hasWildCard= false;
		int len= s.length();
		for (int i= 0; i < len; i++) {
			char ch= s.charAt(i);
			if (ch == '.') {
				if (!hasWildCard) {
					buf.append('*');
				}
				hasWildCard= false;
			} else if (ch == '*' || ch =='?') {
				hasWildCard= true;
			}
			buf.append(ch);
		}
		if (!hasWildCard) {
			if (len == 0) {
				buf.append('?');
			}
			buf.append('*');
		}
		return buf.toString();
	}

	public String getText() {
		return fText;
	}

	/**
	 * Checks whether <code>this</code> filter is a subFilter of the given <code>text</code>.
	 * <p>
	 * <i>WARNING: This is the <b>reverse</b> interpretation compared to
	 * {@link org.eclipse.ui.dialogs.SearchPattern#isSubPattern(org.eclipse.ui.dialogs.SearchPattern)} and
	 * {@link org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter#isSubFilter}.
	 * </i>
	 * </p>
	 *
	 * @param text another filter text
	 * @return <code>true</code> if <code>this</code> filter is a subFilter of <code>text</code>
	 * e.g. "List" is a subFilter of "L". In this case, the filters matches a proper subset of
	 * the items matched by <code>text</code>.
	 */
	public boolean isSubFilter(String text) {
		if (! fText.startsWith(text))
			return false;

		return fText.indexOf('.', text.length()) == -1;
	}

	public boolean isCamelCasePattern() {
		int ccMask= SearchPattern.RULE_CAMELCASE_MATCH;
		return (fNameMatcher.getMatchRule() & ccMask) != 0;
	}

	public String getPackagePattern() {
		if (fPackageMatcher == null)
			return null;
		return fPackageMatcher.getPattern();
	}

	public String getNamePattern() {
		return fNameMatcher.getPattern();
	}

	public int getSearchFlags() {
		return fNameMatcher.getMatchRule();
	}

	public int getElementKind() {
		return fElementKind;
	}

	public IX10SearchScope getSearchScope() {
		return fSearchScope;
	}

	public int getPackageFlags() {
		if (fPackageMatcher == null)
			return SearchPattern.RULE_EXACT_MATCH;

		return fPackageMatcher.getMatchRule();
	}

	public boolean matchesRawNamePattern(TypeNameMatch type) {
		return Strings.startsWithIgnoreCase(type.getSimpleTypeName(), fNameMatcher.getPattern());
	}

	public boolean matchesCachedResult(TypeNameMatch type) {
		if (!(matchesPackage(type) && matchesFilterExtension(type)))
			return false;
		return matchesName(type);
	}

	public boolean matchesHistoryElement(TypeNameMatch type) {
		if (!(matchesPackage(type) && matchesModifiers(type) && matchesScope(type) && matchesFilterExtension(type)))
			return false;
		return matchesName(type);
	}

	public boolean matchesFilterExtension(TypeNameMatch type) {
		if (fFilterExtension == null)
			return true;
		fAdapter.setMatch(type);
		return fFilterExtension.select(fAdapter);
	}

	private boolean matchesName(TypeNameMatch type) {
		if (fText.length() == 0) {
			return true; //empty pattern matches all names
		}
		return fNameMatcher.matches(type.getSimpleTypeName());
	}

	private boolean matchesPackage(TypeNameMatch type) {
		if (fPackageMatcher == null)
			return true;
		return fPackageMatcher.matches(type.getTypeContainerName());
	}

	private boolean matchesScope(TypeNameMatch type) {
//		if (fIsWorkspaceScope)
//			return true;
//		return fSearchScope.encloses(type.getType());
		return true;
	}

	private boolean matchesModifiers(TypeNameMatch type) {
//		if (fElementKind == IJavaSearchConstants.TYPE)
//			return true;
//		int modifiers= type.getModifiers() & TYPE_MODIFIERS;
//		switch (fElementKind) {
//			case IJavaSearchConstants.CLASS:
//				return modifiers == 0;
//			case IJavaSearchConstants.ANNOTATION_TYPE:
//				return Flags.isAnnotation(modifiers);
//			case IJavaSearchConstants.INTERFACE:
//				return Flags.isInterface(modifiers);
//			case IJavaSearchConstants.ENUM:
//				return Flags.isEnum(modifiers);
//			case IJavaSearchConstants.CLASS_AND_INTERFACE:
//				return modifiers == 0 || Flags.isInterface(modifiers);
//			case IJavaSearchConstants.CLASS_AND_ENUM:
//				return modifiers == 0 || Flags.isEnum(modifiers);
//		}
//		return false;
		return true;
	}
}
