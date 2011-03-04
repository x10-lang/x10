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


import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.editor.EditorUtility;
import org.eclipse.jdt.internal.core.search.StringOperation;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.SearchPattern;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.X10SearchEngine;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;
import x10dt.ui.X10DTUIPlugin;



public class SearchUtils {
	
	public static class Flags
	{
		static boolean isPublic(int flags)
		{
			return SearchUtils.hasFlag(X10.PUBLIC, flags);
		}
		
		static boolean isProtected(int flags)
		{
			return SearchUtils.hasFlag(X10.PROTECTED, flags);
		}
		
		static boolean isPrivate(int flags)
		{
			return SearchUtils.hasFlag(X10.PRIVATE, flags);
		}
		
		static boolean isAbstract(int flags)
		{
			return SearchUtils.hasFlag(X10.ABSTRACT, flags);
		}
		
		static boolean isFinal(int flags)
		{
			return SearchUtils.hasFlag(X10.FINAL, flags);
		}
		
		static boolean isStatic(int flags)
		{
			return SearchUtils.hasFlag(X10.STATIC, flags);
		}
		
		static boolean isInterface(int flags)
		{
			return SearchUtils.hasFlag(X10.INTERFACE, flags);
		}
	}

//	/**
//	 * @param match the search match
//	 * @return the enclosing {@link IJavaElement}, or null iff none
//	 */
//	public static IJavaElement getEnclosingJavaElement(SearchMatch match) {
//		Object element = match.getElement();
//		if (element instanceof IJavaElement)
//			return (IJavaElement) element;
//		else
//			return null;
//	}
//
//	/**
//	 * @param match the search match
//	 * @return the enclosing {@link ICompilationUnit} of the given match, or null iff none
//	 */
//	public static ICompilationUnit getCompilationUnit(SearchMatch match) {
//		IJavaElement enclosingElement = getEnclosingJavaElement(match);
//		if (enclosingElement != null){
//			if (enclosingElement instanceof ICompilationUnit)
//				return (ICompilationUnit) enclosingElement;
//			ICompilationUnit cu= (ICompilationUnit) enclosingElement.getAncestor(IJavaElement.COMPILATION_UNIT);
//			if (cu != null)
//				return cu;
//		}
//
//		IJavaElement jElement= JavaCore.create(match.getResource());
//		if (jElement != null && jElement.exists() && jElement.getElementType() == IJavaElement.COMPILATION_UNIT)
//			return (ICompilationUnit) jElement;
//		return null;
//	}
//
//	public static SearchParticipant[] getDefaultSearchParticipants() {
//		return new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
//	}
//
//    /**
//     * Constant for use as matchRule in {@link SearchPattern#createPattern(IJavaElement, int, int)}
//     * to get search behavior as of 3.1M3 (all generic instantiations are found).
//     */
    public final static int GENERICS_AGNOSTIC_MATCH_RULE= SearchPattern.RULE_EXACT_MATCH | SearchPattern.RULE_CASE_SENSITIVE;

    /**
     * Returns whether the given pattern is a camel case pattern or not.
     * <em>Note: this method does not consider the
     * {@link SearchPattern#RULE_CAMELCASE_SAME_PART_COUNT_MATCH} variant.<em>
     *
     * @param pattern the pattern to inspect
     * @return whether it is a camel case pattern or not
     */
	public static boolean isCamelCasePattern(String pattern) {
		SearchPattern sp = new SearchPattern();
		if(sp.matches(pattern))
		{
			return (sp.getMatchRule() & SearchPattern.RULE_CAMELCASE_MATCH) == SearchPattern.RULE_CAMELCASE_MATCH;
		}
		return false;
	}
	
	public static final int[] getMatchingRegions(String pattern, String name, int matchRule) {
		if (name == null) return null;
		final int nameLength = name.length();
		if (pattern == null) {
			return new int[] { 0, nameLength };
		}
		final int patternLength = pattern.length();
		boolean countMatch = false;
		switch (matchRule) {
			case SearchPattern.RULE_EXACT_MATCH:
				if (patternLength == nameLength && pattern.equalsIgnoreCase(name)) {
					return new int[] { 0, patternLength };
				}
				break;
			case SearchPattern.RULE_EXACT_MATCH | SearchPattern.RULE_CASE_SENSITIVE:
				if (patternLength == nameLength && pattern.equals(name)) {
					return new int[] { 0, patternLength };
				}
				break;
			case SearchPattern.RULE_PREFIX_MATCH:
				if (patternLength <= nameLength && name.substring(0, patternLength).equalsIgnoreCase(pattern)) {
					return new int[] { 0, patternLength };
				}
				break;
			case SearchPattern.RULE_PREFIX_MATCH | SearchPattern.RULE_CASE_SENSITIVE:
				if (name.startsWith(pattern)) {
					return new int[] { 0, patternLength };
				}
				break;
//			case SearchPattern.RULE_CAMELCASE_SAME_PART_COUNT_MATCH:
//				countMatch = true;
				//$FALL-THROUGH$
			case SearchPattern.RULE_CAMELCASE_MATCH:
				if (patternLength <= nameLength) {
					int[] regions = StringOperation.getCamelCaseMatchingRegions(pattern, 0, patternLength, name, 0, nameLength, countMatch);
					if (regions != null) return regions;
					if (name.substring(0, patternLength).equalsIgnoreCase(pattern)) {
						return new int[] { 0, patternLength };
					}
				}
				break;
//			case SearchPattern.RULE_CAMELCASE_SAME_PART_COUNT_MATCH | SearchPattern.RULE_CASE_SENSITIVE:
//				countMatch = true;
				//$FALL-THROUGH$
			case SearchPattern.RULE_CAMELCASE_MATCH | SearchPattern.RULE_CASE_SENSITIVE:
				if (patternLength <= nameLength) {
					return StringOperation.getCamelCaseMatchingRegions(pattern, 0, patternLength, name, 0, nameLength, countMatch);
				}
				break;
			case SearchPattern.RULE_PATTERN_MATCH:
				return StringOperation.getPatternMatchingRegions(pattern, 0, patternLength, name, 0, nameLength, false);
			case SearchPattern.RULE_PATTERN_MATCH | SearchPattern.RULE_CASE_SENSITIVE:
				return StringOperation.getPatternMatchingRegions(pattern, 0, patternLength, name, 0, nameLength, true);
		}
		return null;
	}
	
	public static String getElementName(IMemberInfo info) {
		if(info instanceof ITypeInfo)
		{
			return getElementName((ITypeInfo)info);
		}
		return info.getName();
	}
	
	public static String getElementName(ITypeInfo info) {
		return info.getName().substring(info.getName().lastIndexOf(".") + 1);
	}

	public static String getFullyQualifiedName(ITypeInfo info) {
		return info.getName();
	}

	public static String getFullyQualifiedName(ITypeInfo info, String qualifier) {
		return info.getName().replaceAll(".", qualifier);
	}

	public static String getFullyQualifiedName(ITypeInfo info, char qualifier) {
		return info.getName().replaceAll("\\.", new String(new char[]{qualifier}));
	}
	
	public static IPath getPath(IMemberInfo info)
	{
		return new Path(info.getLocation().getURI().getSchemeSpecificPart());
	}

	public static String getHandleIdentifier(IMemberInfo info) {
		if(info instanceof ITypeInfo)
		{
			getHandleIdentifier((ITypeInfo)info);
		}
		
		else if(info instanceof IMethodInfo)
		{
			getHandleIdentifier((ITypeInfo)info);
		}
		
		else if(info instanceof IFieldInfo)
		{
			getHandleIdentifier((ITypeInfo)info);
		}
		return null;
	}
	
	public static String getHandleIdentifier(ITypeInfo info) {
		return "TypeInfo:" + info.getName() + "," +  getPath(info).toPortableString();
	}
	
	public static String getHandleIdentifier(IMethodInfo info) {
		return "MethodInfo:" + info.getName();// + "," +  getPath(info).toPortableString();
	}
	
	public static String getHandleIdentifier(IFieldInfo info) {
		return "FieldInfo:" + info.getName();// + "," +  getPath(info).toPortableString();
	}

	public static String getPackageName(ITypeInfo info) {
		return getTypeContainerName(info);
	}
	
	public static IResource getResource(IMemberInfo info) {
		try {
			IPath path = getPath(info);
			return ResourcesPlugin.getWorkspace().getRoot().findMember(path.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
		} catch (Exception e) {
			//ignore
		}
		
		return null;
	}
	
	public static IResource getResource(URI uri) {
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
		if(files.length > 0)
		{
			return files[0];
		}
		
		return null;
	}

	public static boolean isDefaultPackage(String name)
	{
		return name.equals(Messages.X10ElementLabels_default_package);
	}
	
	public static String getTypeContainerName(ITypeInfo info) {
		try {
			return info.getName().substring(0, info.getName().lastIndexOf("."));
		} catch (Exception e) {
			return Messages.X10ElementLabels_default_package;
		}
	}

	public static String getTypeQualifiedName(ITypeInfo info) {
		return info.getName();
	}

	public static String getTypeQualifiedName(ITypeInfo info, char qualifier) {
		return getFullyQualifiedName(info, qualifier);
	}
	
	public static ITypeInfo createType(String handle)
	{
		try {
			String[] tokens = handle.split(",");
			IResource res = getResource(new URI(tokens[1]));
			final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, res);
			final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, tokens[0], new NullProgressMonitor());
			return (typeInfo.length != 1) ? null : typeInfo[0];
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getTypeRegex(String typeName)
	{
		return typeName + ".*";
	}
	
	public static boolean hasFlag(X10 flag, int modifiers) {
		return (modifiers & flag.getCode()) != 0;
	}
	
	public static ITypeInfo getOuterTypeInfo(IMemberInfo member)
	{
		ITypeInfo dt = member.getDeclaringType();
		if(dt == null)
		{
			if(member instanceof ITypeInfo)
			{
				dt = (ITypeInfo)member;
			}
		}
		
		return dt;
	}
	
	/**
     * Tests if a given input element is currently shown in an editor
     * 
     * @return the IEditorPart if shown, null if element is not open in an editor
     */
	public static IEditorPart isOpenInEditor(Object inputElement) {
		if (inputElement instanceof IMemberInfo) {
			IMemberInfo mi = (IMemberInfo)inputElement;
			ITypeInfo dt = getOuterTypeInfo((IMemberInfo)inputElement);
			IResource res = SearchUtils.getResource(dt);
			if(res != null)
			{
				EditorUtility.isOpenInEditor(inputElement);
			}
			
			else
			{
				URI uri = mi.getLocation().getURI();
				String scheme = uri.getSchemeSpecificPart();
				if(uri.getScheme().equals("jar"))
				{
//					scheme = scheme.substring(0, scheme.lastIndexOf(":"));
					scheme = scheme.replace("file:", "");
				}
				
				IPath path = new Path(scheme);
				return EditorUtility.isOpenInEditor(path);
			}
		}

		return EditorUtility.isOpenInEditor(inputElement);
	}
	
	public static void openEditor(IEditorPart part, IMemberInfo ti)
	{
		EditorUtility.revealInEditor(part, new Region(ti.getLocation().getOffset(), 0));
	}
	
	public static void openEditor(IMemberInfo ti) throws CoreException
	{
		ITypeInfo dt = getOuterTypeInfo(ti);
		IResource res = SearchUtils.getResource(dt);
		if(res != null)
		{
			IEditorPart part= EditorUtility.openInEditor(res);
			EditorUtility.revealInEditor(part, new Region(ti.getLocation().getOffset(), 0));
		}
		
		else
		{
			URI uri = ti.getLocation().getURI();
			String scheme = uri.getSchemeSpecificPart();
			if(uri.getScheme().equals("jar"))
			{
//				scheme = scheme.substring(0, scheme.lastIndexOf(":"));
				scheme = scheme.replace("file:", "");
			}
			
			IPath path = new Path(scheme);
			IEditorPart part= EditorUtility.openInEditor(path);
			EditorUtility.revealInEditor(part, new Region(ti.getLocation().getOffset(), 0));
		}
	}
	
	public static ITypeInfo getType(IProject project, String type)
	{
		try {
		  final IX10SearchScope scope;
			if(project == null)
			{
				scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL);
			} else
			{
			  scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
			}
			final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, type, new NullProgressMonitor());
			return (typeInfo.length != 1) ? null : typeInfo[0];
		} catch (Exception e) {
			X10DTUIPlugin.log(e);
		}
		
		return null;
	}
	
	public static IMethodInfo[] getMethods(ITypeInfo type, String methodName)
	{
		try {
			IResource res = getResource(type);
			IX10SearchScope scope = null;
			if (res == null) {
				scope = SearchScopeFactory
						.createWorkspaceScope(X10SearchScope.ALL);
			}

			else {
				scope = SearchScopeFactory.createSelectiveScope(
						X10SearchScope.ALL, res);
			}

			return X10SearchEngine.getMethodInfos(scope,
					type, methodName,
					new NullProgressMonitor());
		} catch (Exception e) {
			return new IMethodInfo[0];
		}
	}
	
	public static IMethodInfo[] getMethods(ITypeInfo type)
	{
		try {
			IResource res = getResource(type);
			IX10SearchScope scope = null;
			if (res == null) {
				scope = SearchScopeFactory
						.createWorkspaceScope(X10SearchScope.ALL);
			}

			else {
				scope = SearchScopeFactory.createSelectiveScope(
						X10SearchScope.ALL, res);
			}

			return X10SearchEngine.getAllMatchingMethodInfo(scope,
					type, getTypeRegex(""), false,
					new NullProgressMonitor());
		} catch (Exception e) {
			return new IMethodInfo[0];
		}
	}
	
	public static IFieldInfo[] getFields(ITypeInfo type)
	{
		try {
			IResource res = getResource(type);
			IX10SearchScope scope = null;
			if (res == null) {
				scope = SearchScopeFactory
						.createWorkspaceScope(X10SearchScope.ALL);
			}

			else {
				scope = SearchScopeFactory.createSelectiveScope(
						X10SearchScope.ALL, res);
			}

			return X10SearchEngine.getAllMatchingFieldInfo(scope,
					type, getTypeRegex(""), false,
					new NullProgressMonitor());
		} catch (Exception e) {
			return new IFieldInfo[0];
		}
	}
}