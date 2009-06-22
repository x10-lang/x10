/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

 *******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Mar 8, 2007
 */
package org.eclipse.imp.x10dt.ui.editor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import lpg.runtime.IToken;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.imp.utils.HTMLPrinter;
import org.eclipse.imp.x10dt.ui.parser.PolyglotNodeLocator;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavadocContentAccess;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.types.X10ParsedClassType_c;
import polyglot.types.ClassType;
import polyglot.types.Declaration;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.parser.X10Parser.JPGPosition;

/**
 * Originally provided info for Hover Help; now also is called by context help
 * (Dynamic Help / F1) as well.
 * 
 * This aims to provide some sort of information for a variety of objects. Need
 * to document whether it provides info with HTML or not.
 */
public class X10DocProvider implements IDocumentationProvider, ILanguageService {
	private static final boolean traceOn = false;

	public String getDocumentation(Object target, IParseController parseController) {
		// 1. try to find help info the way context help did
		String doc = getHelpForEntity(target, parseController);
		// if (doc == null || doc.length() == 0) {
		// // 2. try to find help info the way Hover help did (original code
		// // from this class, previously)
		// doc = getDocumentationOld(target, parseController);
		// }
		if (traceOn) {
			System.out.println("\nX10DocProvider.getDocumentation(), target is :"
							+ target.toString());
			System.out.println("   " + doc);
		}
		return doc;
	}

	public String getDocumentationOld(Object target, IParseController parseController) {

		Language lang = parseController.getLanguage();
		StringBuffer buff = new StringBuffer();
		Position pos = null;

		String temp = target.toString();
		HTMLPrinter.addSmallHeader(buff, temp);

		if (target instanceof Node) {
			Node targetNode = (Node) target;
			pos = targetNode.position();
		} else if (target instanceof Declaration) {
			Declaration decl = (Declaration) target;
			pos = decl.position();
		} else if (target instanceof Id) {
			Id id = (Id) target;
			pos = id.position();
		}

		if (pos instanceof JPGPosition) {
			IToken leftToken = ((JPGPosition) pos).getLeftIToken();
			// this is empty, it seems there are no comments in the list of
			// tokens. we want the comments.
			IToken[] adjuncts = leftToken.getPrecedingAdjuncts();

			for (int i = 0; i < adjuncts.length; i++) {
				String s = adjuncts[i].toString();
				if (s.startsWith("/*")) {
					int openingFenceEnd = s.startsWith("/**") ? 3 : 2;
					int endingFenceStart = s.length()
							- (s.startsWith("//") ? 0 : (s.endsWith("**/") ? 3
									: 2));

					HTMLPrinter.addParagraph(buff, s.substring(openingFenceEnd,
							endingFenceStart));
				}
			}
		}
		// How to get JavaDoc corresponding to classes residing in jar files?
		return buff.toString();
	}

	/**
	 * From X10ContextHelper originally, moved here. Provides javadoc-like info
	 * and more
	 */
	public String getHelpForEntity(Object target, IParseController parseController) {
		Node root = (Node) parseController.getCurrentAst();

		if (target instanceof Id) {
			Id id = (Id) target;
			PolyglotNodeLocator locator = (PolyglotNodeLocator) parseController.getNodeLocator();
			Node parent = (Node) locator.getParentNodeOf(id, root);
			target = parent;
		}

		if (target instanceof Field) {
			Field field = (Field) target;
			FieldInstance fi = field.fieldInstance();
			ReferenceType ownerType = fi.container();

			if (ownerType.isClass()) {
				ClassType ownerClass = (ClassType) ownerType;
				String ownerName = ownerClass.fullName();

				if (isJavaType(ownerName)) {
					IType javaType = findJavaType(ownerName, parseController);
					IField javaField = javaType.getField(fi.name());

					return getJavaDocFor(javaField);
				} else {
					return getX10DocFor(fi);
				}
			}
			return "Field '" + fi.name() + "' of type " + fi.type().toString();
		} else if (target instanceof NamedVariable) {
			NamedVariable var = (NamedVariable) target;
			Type type = var.type();

			return "Variable '" + var.name() + "' of type " + type.toString();
		} else if (target instanceof Call) {
			Call call = (Call) target;
			MethodInstance mi = call.methodInstance();
			ReferenceType ownerType = mi.container();

			if (ownerType.isClass()) {
				ClassType ownerClass = (ClassType) ownerType;
				String ownerName = ownerClass.fullName();

				if (isJavaType(ownerName)) {
					IType javaType = findJavaType(ownerName, parseController);
					String[] paramTypes = convertParamTypes(mi);
					IMethod method = javaType.getMethod(mi.name(), paramTypes);

					return getJavaDocFor(method);
				} else {
					return getX10DocFor(mi);
				}
			}
			return "Method " + mi.signature() + " of type "
					+ mi.container().toString();
		} else if (target instanceof Declaration) {
			Declaration decl = (Declaration) target;
			//FIXME if(isJavaType...) like above ....
			
			return getX10DocFor(decl);
		} else if (target instanceof MethodDecl) {
			MethodDecl md = (MethodDecl) target;

			return getX10DocFor(md.methodInstance());
		} else if (target instanceof FieldDecl) {
			FieldDecl fd = (FieldDecl) target;
			FieldInstance fi = fd.fieldInstance();
			return getX10DocFor(fi);
		}

		else if (target instanceof TypeNode) {
			TypeNode typeNode = (TypeNode) target;
			PolyglotNodeLocator locator = (PolyglotNodeLocator) parseController
					.getNodeLocator();
			Node parent = (Node) locator.getParentNodeOf(target, root);

			if (parent instanceof ConstructorDecl) {
				ConstructorDecl cd = (ConstructorDecl) parent;
				return getX10DocFor(cd.constructorInstance());
			} else if (parent instanceof New) {
				New n = (New) parent;
				return getX10DocFor(n.constructorInstance());
			} else {
				Type type = typeNode.type();
				String qualifiedName = typeNode.qualifier().toString();
				qualifiedName = stripArraySuffixes(qualifiedName);

				if (isJavaType(qualifiedName)) {
					IType javaType = findJavaType(qualifiedName, parseController);
					return (javaType != null) ? getJavaDocFor(javaType) : "";
				} else {
					return type.isClass() ? getX10DocFor((ClassType) type) : "";
				}

			}
		}
		else if (target instanceof ClassType) {
			ClassType type = (ClassType)target;
			
			// copied from above  FIXME
			String qualifiedName = type.fullName();
			qualifiedName = stripArraySuffixes(qualifiedName);

			if (isJavaType(qualifiedName)) {
				IType javaType = findJavaType(qualifiedName,
						parseController);

				return (javaType != null) ? getJavaDocFor(javaType) : "";
			} else {
				return type.isClass() ? getX10DocFor((ClassType) type) : "";
			}
			
		}
		// JavadocContentAccess seems to provide no way to get at that package
		// docs...
		// else if (target instanceof PackageNode) {
		// PackageNode pkgNode= (PackageNode) target;
		// String pkgName= pkgNode.qualifier().toString();
		// IJavaProject javaProject=
		// JavaCore.create(parseController.getProject().getRawProject());
		// IPackageFragmentRoot[] pkgFragRoots=
		// javaProject.getAllPackageFragmentRoots();
		// for(int i= 0; i < pkgFragRoots.length; i++) {
		// IPackageFragmentRoot pkgRoot= pkgFragRoots[i];
		// IPackageFragment pkgFrag= pkgRoot.getPackageFragment(pkgName);
		// if (pkgFrag.exists()) {
		// JavadocContentAccess.???()
		// }
		// }
		//
		// }
		
		// return "This is a " + target.getClass().getCanonicalName();
		return "";
	}

	/**
	 * Get the javadoc-like comment string for an X10 declaration
	 */
	private String getX10DocFor(Declaration decl) {
		Position pos = decl.position();
		String path = pos.file();

		try {
			Reader reader = new FileReader(new File(path));
			String fileSrc = readReader(reader);

			int idx = pos.offset();
			idx = skipBackwardWhite(fileSrc, idx);
			if (lookingPastEndOf(fileSrc, idx, "*/")) {
				String doc = collectBackwardTo(fileSrc, idx, "/**");
				doc = getCommentText(doc);
				if (traceOn)
					System.out.println("X10ContextHelper.getX10DocFor(): "
							+ doc);
				return doc;
			}
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 
	 * Reads text from javadoc (x10doc) comment, ignoring leading/trailing
	 * slash-star-star and star-slash and * leading lines. <br>
	 * Assumes leading/trailing comment chars should also be removed
	 * 
	 * @param text
	 *            the text of the comment
	 * @returns text without the javadoc comment characters
	 * 
	 */
	private String getCommentText(String text) {
		return getCommentText(text, true);
	}

	/**
	 * 
	 * Reads text from javadoc (x10doc) comment, ignoring leading/trailing
	 * slash-star-star and star-slash and * leading lines. <br>
	 * adapted from JavadocCommentReader.read() <br>
	 * Also strips some html and other tags as well, for now
	 * 
	 * @param text
	 *            the text of the comment
	 * @param stripLeadingTrailingParts
	 *            whether or not to strip the leading/trailing comment chars
	 * @returns text with the intervening (if any) leading star characters
	 * 
	 */
	private String getCommentText(String text, boolean stripLeadingTrailingParts) {
		StringBuilder result = new StringBuilder();
		String showResult = "|";

		// If it starts with the 3 chars /** and ends with the two chars */,
		// then start by ditching these
		if (stripLeadingTrailingParts) {
			text = text.substring(3, text.length() - 2);
		}
		int fCurrPos = 0;
		int fEndPos = text.length() - 1;

		boolean fWasNewLine = false;
		while (fCurrPos < fEndPos) {// was if
			char ch;
			if (fWasNewLine) {
				do {
					ch = text.charAt(fCurrPos++);
				} while (fCurrPos < fEndPos && Character.isWhitespace(ch));
				if (ch == '*') {
					if (fCurrPos < fEndPos) {
						do {
							ch = text.charAt(fCurrPos++);
						} while (ch == '*');
					}
				}
			} else {
				ch = text.charAt(fCurrPos++);
			}
			if (ch == '@') {
				// TBD handle
			}
			// determine if prev char was line delimiter; if so will affect how
			// we start next line
			fWasNewLine = ch == '\n' || ch == '\r';
			result.append(ch);
			showResult = "|" + result.toString() + "|";
		}
		// strip html, leave some as bold tags
		String resStr = result.toString();
		String boldStr = decodeContextBoldTags(resStr);
		System.out.println("x10ContextHelper boldStr: " + boldStr);
		return boldStr;
	}

	/**
	 * Adapted from jdt's ContextHelpPart.decodeContextBoldTags()
	 */
	private String decodeContextBoldTags(String styledText) {

		if (styledText == null) {
			return "No description available ";
		}
		String decodedString = styledText.replaceAll("<@#\\$b>", "<b>"); //$NON-NLS-1$ //$NON-NLS-2$
		decodedString = decodedString.replaceAll("</@#\\$b>", "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		// decodedString = escapeSpecialChars(decodedString, true);
		// decodedString = decodedString.replaceAll("\r\n|\n|\r", "<br/>");
		// //$NON-NLS-1$ //$NON-NLS-2$
		return decodedString;
	}

	/**
	 * Adapted from jdt's ReusableHelpPart.escapeSpecialChars()
	 */
	String escapeSpecialChars(String value) {
		return escapeSpecialChars(value, false);
	}

	/**
	 * Also adapted from jdt's ReusableHelpPart.escapeSpecialChars()
	 */
	String escapeSpecialChars(String value, boolean leaveBold) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			switch (c) {
			case '&':
				buf.append("&amp;"); //$NON-NLS-1$
				break;
			case '<':
				if (leaveBold) {
					int length = value.length();
					if (i + 6 < length) {
						String tag = value.substring(i, i + 7);
						if (tag.equalsIgnoreCase("</code>")) { //$NON-NLS-1$
							buf.append("</span>"); //$NON-NLS-1$
							i += 6;
							continue;
						}
					}
					if (i + 5 < length) {
						String tag = value.substring(i, i + 6);
						if (tag.equalsIgnoreCase("<code>")) { //$NON-NLS-1$
							buf.append("<span font=\"code\">"); //$NON-NLS-1$
							i += 5;
							continue;
						}
					}
					if (i + 3 < length) {
						String tag = value.substring(i, i + 4);
						if (tag.equalsIgnoreCase("</b>")) { //$NON-NLS-1$
							buf.append(tag);
							i += 3;
							continue;
						}
						if (tag.equalsIgnoreCase("<br>")) { //$NON-NLS-1$
							buf.append("<br/>"); //$NON-NLS-1$
							i += 3;
							continue;
						}
					}
					if (i + 2 < length) {
						String tag = value.substring(i, i + 3);
						if (tag.equalsIgnoreCase("<b>")) { //$NON-NLS-1$
							buf.append(tag);
							i += 2;
							continue;
						}
					}
				}
				buf.append("&lt;"); //$NON-NLS-1$
				break;
			case '>':
				buf.append("&gt;"); //$NON-NLS-1$
				break;
			case '\'':
				buf.append("&apos;"); //$NON-NLS-1$
				break;
			case '\"':
				buf.append("&quot;"); //$NON-NLS-1$
				break;
			case 160:
				buf.append(" "); //$NON-NLS-1$
				break;
			default:
				buf.append(c);
				break;
			}
		}
		return buf.toString();
	}

	private String collectBackwardTo(String fileSrc, int idx, String string) {
		return fileSrc.substring(fileSrc.lastIndexOf(string, idx), idx);
	}

	private boolean lookingPastEndOf(String fileSrc, int endIdx, String string) {
		int idx = endIdx - string.length();
		return fileSrc.indexOf(string, idx) == idx;
	}

	private int skipBackwardWhite(String fileSrc, int idx) {
		while (idx > 0 && Character.isWhitespace(fileSrc.charAt(idx - 1)))
			idx--;
		return idx;
	}

	/**
	 * Get the javadoc-like comment string for a X10 class declaration
	 */
	private String getX10DocFor(ClassDecl decl) {
		Position pos = decl.position();
		String path = pos.file();

		try {
			Reader reader = new FileReader(new File(path));
			String fileSrc = readReader(reader);
			int idx = pos.offset();

			idx = skipBackwardWhite(fileSrc, idx);
			if (lookingPastEndOf(fileSrc, idx, "*/")) {
				String doc = collectBackwardTo(fileSrc, idx, "/**");
				if (traceOn)
					System.out
							.println("X10ContextHelper.getX10DocFor(classDecl): "
									+ doc);
				return doc;
			}
		} catch (IOException e) {
		}
		return null;
	}
	/**
	 * Get the javadoc-like comment string for an X10 entity that is represented by a Node
	 */
	private String getX10DocFor(Node node) {
		Position pos = node.position();
		String path = pos.file();

		try {
			Reader reader = new FileReader(new File(path));
			String fileSrc = readReader(reader);
			int idx = pos.offset();

			idx = skipBackwardWhite(fileSrc, idx);
			if (lookingPastEndOf(fileSrc, idx, "*/")) {
				String doc = collectBackwardTo(fileSrc, idx, "/**");
				if (traceOn)
					System.out.println("X10ContextHelper.getX10DocFor(Node): "
							+ doc);
				return doc;
			}
		} catch (IOException e) {
		}
		return null;
	}

	private String stripArraySuffixes(String qualifiedName) {
		while (qualifiedName.endsWith("[]")) {
			qualifiedName = qualifiedName.substring(0,
					qualifiedName.length() - 2);
		}
		return qualifiedName;
	}

	private boolean isJavaType(String qualifiedName) {
		return qualifiedName.startsWith("java.");
	}

	private String[] convertParamTypes(MethodInstance mi) {
		String[] paramTypes = new String[mi.formalTypes().size()];

		int i = 0;
		for (Iterator iterator = mi.formalTypes().iterator(); iterator
				.hasNext();) {
			Type formalType = (Type) iterator.next();
			String typeName = formalType.toString();
			String typeSig = (typeName.indexOf('.') > 0) ? "L"
					+ formalType.toString() + ";" : typeName;
			paramTypes[i++] = typeSig;
		}
		return paramTypes;
	}

	/**
	 * Get javadoc info - will get from jar file if necessary
	 * 
	 * @param member
	 * @return
	 */
	private String getJavaDocFor(IMember member) {
		try {
			Reader reader = JavadocContentAccess.getHTMLContentReader(member,
					true, true);

			if (reader == null)
				return "";
			String doc = readReader(reader);
			if (traceOn)
				System.out.println("X10ContextHelper.getJavaDocFor(): " + doc);
			// BRT note: e.g. for System.out.println, includes double <code><code>
			// which loses the contents .
			// JDT hover does correctly display this, however
			return doc;
		} catch (JavaModelException e) {
			return "";
		}
	}

	private IType findJavaType(String qualName, IParseController parseController) {
		try {
			IJavaProject javaProject = JavaCore.create(parseController
					.getProject().getRawProject());
			IType javaType = javaProject.findType(qualName);

			return javaType;
		} catch (JavaModelException e) {
			return null;
		}
	}

	private String readReader(Reader reader) {
		try {
			StringBuffer buffer = new StringBuffer();
			char[] part = new char[2048];
			int read = 0;

			while ((read = reader.read(part)) != -1)
				buffer.append(part, 0, read);

			return buffer.toString();
		} catch (IOException ex) {
			System.err.println("I/O Exception: " + ex.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// silently ignored
				}
			}
		}
		return "";
	}
}
