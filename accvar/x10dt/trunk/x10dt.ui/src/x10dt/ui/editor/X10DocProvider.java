/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) -initial API and implementation
 *******************************************************************************/

package x10dt.ui.editor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;

import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.ObjectType;
import polyglot.types.ProcedureInstance;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.util.Position;
import x10.types.ConstrainedType;
import x10.types.MethodInstance;
import x10dt.ui.parser.PolyglotNodeLocator;

/**
 * Provide  info for Hover Help and context help
 * (Dynamic Help / F1) as well.
 * 
 * This aims to provide some sort of information for a variety of objects. Need
 * to document whether it provides info with HTML or not.
 * Currently, queries by ContextHelper and HoverHelper process any html provided differently.
 */
public class X10DocProvider implements IDocumentationProvider, ILanguageService {
    private static final boolean traceOn = false;

    /**
     * Get text documentation for an entity in the x10 source code
     * @param target the object for which we want text info
     * @param parseController
     * @returns a text string of javadoc-style info, or other, if available
     */
    public String getDocumentation(Object target, IParseController parseController) {
        if (traceOn) System.out.println("\nX10DocProvider.getDocumentation(), target is :"+ target.toString());
        String doc = getHelpForEntity(target, parseController);
        if (traceOn) System.out.println("   " + doc);
        return doc;
    }

    private Object getTarget(Object target, IParseController parseController, Node root)
    {
		if (target instanceof Id) {
			Id id = (Id) target;
			PolyglotNodeLocator locator = (PolyglotNodeLocator) parseController
					.getSourcePositionLocator();
			Node parent = (Node) locator.getParentNodeOf(id, root);
			target = parent;
		}

		// TODO Convert the following to use Defs in lieu of Instances
		if (target instanceof Field) { // field reference
			Field field = (Field) target;
			FieldInstance fi = field.fieldInstance();
			target = fi;
		} else if (target instanceof FieldDef) {
			FieldInstance fi = ((FieldDef) target).asInstance();
			target = fi;
		} else if (target instanceof Local) { // local var reference
			Local local = (Local) target;
			LocalInstance li = local.localInstance();
			target = li;
		} else if (target instanceof LocalDecl) {
			LocalDecl localDecl = (LocalDecl) target;
			LocalDef ld = localDecl.localDef();
			if (ld == null)
				return null;
			LocalInstance li = ld.asInstance();

			target = li;
		} else if (target instanceof LocalDef) { // field reference
			target = ((LocalDef) target).asInstance();
		} else if (target instanceof MethodDef) {
			target = ((MethodDef) target).asInstance();
		} else if (target instanceof ConstructorDef) {
			target = ((ConstructorDef) target).asInstance();
		}

		return target;
    }
    
    
	/**
	 * Provides javadoc-like info (if available) and more for a variety of entities
	 */
	public String getHelpForEntity(Object target, IParseController parseController) {
		try {
			Node root = (Node) parseController.getCurrentAst();
			target = getTarget(target, parseController, root);
			
			if (target instanceof FieldDecl) {
				return getHelpForEntity((FieldDecl)target, parseController, root);
			} else if (target instanceof FieldInstance) {
				return getHelpForEntity((FieldInstance)target, parseController, root);
			} else if (target instanceof NamedVariable) {
				return getHelpForEntity((NamedVariable)target, parseController, root);
			} else if (target instanceof LocalInstance) {
				return getHelpForEntity((LocalInstance)target, parseController, root);
			} else if(target instanceof MethodInstance) {
				return getHelpForEntity((MethodInstance)target, parseController, root);
			} else if(target instanceof ConstructorInstance) {
				return getHelpForEntity((ConstructorInstance)target, parseController, root);
			} else if (target instanceof ClassType) {  
				return getHelpForEntity((ClassType)target, parseController, root);	
			} else if (target instanceof ClassDecl) {
				return getHelpForEntity((ClassDecl)target, parseController, root);
			} else if (target instanceof MethodDecl) {
				return getHelpForEntity((MethodDecl)target, parseController, root);
			} else if (target instanceof TypeNode) {
				return getHelpForEntity((TypeNode)target, parseController, root);
			} else if(target instanceof ConstrainedType) {
				return getHelpForEntity((ConstrainedType)target, parseController, root);
			}
		} catch (NullPointerException e){
			//If this exception is thrown, it means that there was a compilation error in the file, silently ignore
			return "";
		}
		return "";
	}
	
	private String getSignature(FieldInstance fi) {
		ObjectType ownerType = fi.container().toReference();
		if (ownerType.isClass()) {
			ClassType ownerClass = (ClassType) ownerType;
			String ownerName = ownerClass.fullName().toString();
			String type = fi.type().toString(); // int or pkg.TypeName; want TypeName only

			// RMF 10 Nov 2010 - Disable this for now - unqualify() is horribly broken, since types can also include '.' as part of property refs in constraints
//			type= unqualify(type);//FIXME must be a better way to get simple type, not fully qualified type
			
			String varName= fi.name().toString();

			return type + " " + ownerName + "." + varName;
		}
		return null;
	}
	
	private String getHelpForEntity(FieldDecl target, IParseController parseController, Node root) {
    	FieldInstance fi = target.fieldDef().asInstance();
    	String sig = getSignature(fi);

    	if (sig != null) {
			return getX10DocFor(sig,target);
		}
		return "Field '" + fi.name() + "' of type " + fi.type().toString();
    }
	
	private String getHelpForEntity(FieldInstance target, IParseController parseController, Node root) {
		String sig = getSignature(target);
		if (sig != null) {
			return getX10DocFor(sig,target);
		}
		return "Field '" + target.name() + "' of type " + target.type().toString();
    }
	
	private String getHelpForEntity(NamedVariable target, IParseController parseController, Node root) {
    	NamedVariable var = (NamedVariable) target;
		Type type = var.type();
		return "Variable '" + var + "' of type " + type.toString();
    }
	
	private String getHelpForEntity(LocalInstance li,
			IParseController parseController, Node root) {

		String s = li.toString();
//		int start = s.indexOf("{self.home");
//
//		if (start != -1) {
//			s = s.substring(0, start);
//		}

		return addNameToDoc(s, null);
	}
	
	private String getHelpForEntity(MethodInstance mi,
			IParseController parseController, Node root) {
		String sig=getSignature(mi);
		String doc = getX10DocFor(sig,mi);
		return doc;
	}
	
	private String getHelpForEntity(ConstructorInstance ci, IParseController parseController, Node root) {
		String name="(name)";
		ContainerType rt = ci.container();

		if (rt instanceof Named) {
			Named named = (Named) rt;

			name=named.fullName().toString();
		}
		String args= formatArgs(ci.formalTypes());
		String sig= name+args;
		String doc= getX10DocFor(sig, ci);
		return doc;
	}

	private String getHelpForEntity(ClassType ct, IParseController parseController, Node root) {
		String qualifiedName = ct.fullName().toString();
		qualifiedName = stripArraySuffixes(qualifiedName);			
		return getJavaOrX10DocFor(qualifiedName, ct, parseController);//BRT
	}
	
	private String getHelpForEntity(ClassDecl cd, IParseController parseController, Node root) {
		ClassDef cdef= cd.classDef();
		if (cdef == null)
		    return null;
		String fullName = cdef.fullName().toString();
		String doc = getX10DocFor(fullName,cd);
		return doc;
	}
	
	private String getHelpForEntity(MethodDecl md, IParseController parseController, Node root) {
		MethodDef mdef= md.methodDef();
		if (mdef == null)
		    return null;
		MethodInstance mi= mdef.asInstance();
		String name="";
			 
		ContainerType rt = mi.container();
		if(rt instanceof ClassType) {
			ClassType ct = (ClassType) rt;
			name= ct.fullName().toString();// includes package info
		}
		name = getSignature(mi,md);	
		String doc = getX10DocFor(name, md);
		return doc;
    }
	
	private String getHelpForEntity(TypeNode tn,
			IParseController parseController, Node root) {
		PolyglotNodeLocator locator = (PolyglotNodeLocator) parseController.getSourcePositionLocator();
		Node parent = (Node) locator.getParentNodeOf(tn, root);

		if (parent instanceof ConstructorDecl) {
			ConstructorDecl cd = (ConstructorDecl) parent;
			//String id = cd.id().toString();//shortname
			//String name = typeNode.name();//shortname
			String fullName = tn.toString();// FIXME better way of getting fully qualified name??
			
			// get Constructor args, if any
			String sig= fullName + formatArgs(cd.formals()); 
			ConstructorInstance ci= cd.constructorDef().asInstance();

			return getX10DocFor(sig, ci);
		} else if (parent instanceof New) {
			New n = (New) parent;		
			return getX10DocFor(n.constructorInstance());
		} else {
			Type type = tn.type();
			if (type == null)
			    return null;
			String qualifiedName = tn.qualifierRef().get().toString();
			qualifiedName = stripArraySuffixes(qualifiedName);
			return getJavaOrX10DocFor(qualifiedName, type, parseController); 
		}
	}

	private String getHelpForEntity(ConstrainedType target, IParseController parseController, Node root) {
		return addNameToDoc(target.toString(), null);
	}

	

	
    
    
    
    
    
    
	

	

	/**
	 * return a simple type name, not a fully qualified type
	 * e.g. "my.pkg.Foo"  returns "Foo"
	 * <p>Surely i have missed something to have to implement this
	 * @param type
	 * @return
	 */
	private String unqualify(String type) {
		if(!type.contains(".")) {
			return type;
		}
		int pos=type.lastIndexOf('.');
		String result = type.substring(pos+1);
		return result;
	}

	/**
	 * unused?
	 * @param md
	 * @return
	 */
	private String getSignature(MethodDecl md) {
		MethodInstance mi=md.methodDef().asInstance();
		String sig = getSignature(mi,md);

		return sig;
	}


	private String getSignature(MethodInstance mi) {
		String sig = getSignature(mi,null);

		return sig;
	}

	/**
	 * Get the method signature, including argument types and argument names
	 * @param mi the method instance
	 * @param md the method decl - can be null, but if available, can get better info
	 * @return
	 */
	private String getSignature(MethodInstance mi, MethodDecl md) {	
		ContainerType type1 = mi.container();

		// We assume this is an ObjectType since we are dealing with Methods
		assert(type1 instanceof ObjectType);
		ObjectType type = (ObjectType) type1;

		String containerName="(unspecified)";
		if(type instanceof Named) {
			Named ct = (Named) type;
			containerName =ct.fullName().toString();
		}

		// find return value type
		Type rType = mi.returnType();
		// do we always add a dot? what if containerName is empty? default
		// package? Should *always* have at least a container/class name.
		// Methinks.
		String sig=rType+" "+containerName+"."+mi.name();
		
		// get the args. use MethodDecl if we have it (more complete info)
		List argList= null;
		if(md==null) {
			argList= mi.formalTypes();
		}else {
			argList= md.formals(); // this includes arg names, mi.formalTypes() does not
		}
		String argString= formatArgs(argList);
		sig = sig + argString;
		return sig;
	}

	/**
	 * For a fully qualified name, return either the javadoc or x10Doc for
	 * the entity, if available.
	 * 
	 * @param qualifiedName
	 * @param type the ClassType of the entity
	 * @param parseController
	 * @return the appropriate comment for the entity, or the empty string if none is found
	 */
	private String getJavaOrX10DocFor(String qualifiedName, Type type,
			IParseController parseController) {
		return type.isClass() ? getX10DocFor(qualifiedName, (ClassType) type) : "";
	}

	/**
	 * Get the javadoc-like comment string for an X10 declaration
	 * <p>Does not return signature(qualifiedName) info if the javadoc is empty.
	 * <p>That is, if the javadoc is empty, don't return anything
	 */
	private String getX10DocFor(String qualifiedName, TypeObject decl) {
		String doc = getX10DocFor(decl);
		if (doc != null) doc = addNameToDoc(qualifiedName, doc);
		// if we return Null, HoverHelper will display something unless it's the decl. Uncomment this if you don't want it to.
		//  that is, if you're hovering right over the declaration for the thing, you probably don't need a hover with only type info.
		//if(doc==null) doc="";
		return doc;
	}

	private String getX10DocFor(TypeObject decl) {
		if (decl == null) return "";
		String doc = getNewRawX10DocFor(decl.position());
		return doc;
	}

	/**
	 * Get the javadoc-like comment string for an X10 entity represented by a Node
	 * (Note that this includes ClassDecl, formerly handled separately)
	 */
	private String getX10DocFor(Node node) {
		Position pos = node.position();
		if (node instanceof FieldDecl) {
			pos = ((FieldDecl) node).flags().position();
		}

		String doc = getNewRawX10DocFor(pos);
		return doc;
	}

	private String getX10DocFor(String name, Node node) {
		String doc = getX10DocFor(node);
		doc = addNameToDoc(name, doc);
		return doc;
	}

	/**
	 * Get the javadoc-like comment string for an X10 entity that occurs at a certain position.
	 * Does not add name, this is *just* the javadoc comments, without the stars or comment chars
	 * <p>
	 * Unused? replaced by getNewRawX10DocFor    BRT 8/20/08 unused?
	 */
	private String getRawX10DocFor(Position pos) {
		String path = pos.file();
		try {
			Reader reader = new FileReader(new File(path));
			String fileSrc = readReader(reader);
			int idx = pos.offset();

			idx = skipBackwardWhite(fileSrc, idx);
			if (lookingPastEndOf(fileSrc, idx, "*/")) {
				String doc = collectBackwardTo(fileSrc, idx, "/**");
				doc = getCommentText(doc);// strip stars, etc
				if (traceOn)
					System.out.println("X10DocProvider.getX10DocFor: "
							+ doc);
				// somebody takes out \n later if they are added here:   ???
				//doc = "\n"+doc+"\n"; // be like javadoc and surround with blank lines 
				final String P="<p>";
				doc=P+doc+P;
				return doc;
			}
		} catch (IOException e) {
		}
		return null;
	}
	/**
	 * Get the actual text within the x10doc(javadoc) text. Substitutes HTML for tags, etc
	 * 
	 */
	private String getNewRawX10DocFor(Position pos) {
		if (pos.isCompilerGenerated()) {
			return "";
		}

		String path = pos.file();
		Reader reader = getReader(path);
		
		try {
			String fileSrc = readReader(reader);
			int idx = pos.offset();

			idx = skipBackwardWhite(fileSrc, idx);
			if (lookingPastEndOf(fileSrc, idx, "*/")) {
				String doc = collectBackwardTo(fileSrc, idx, "/**");
				doc = getCommentText(doc);// strip comment chars,stars, etc
				if (traceOn)System.out.println("X10DocProvider.getX10DocCharsFor: "+ doc);
				StringReader rdr = new StringReader(doc);
				X10Doc2HTMLTextReader xrdr=new X10Doc2HTMLTextReader(rdr);
				String result = readReader(xrdr);
				//String result = xrdr.getString();
				return result;
			}
		} catch (Exception e) {
			return "";
		} 
		return null;
	}
	
	private Reader getReader(String path) {
		Reader reader = null;
		try {
			if (path.contains(".jar")) {
				int index = path.lastIndexOf(":");
				JarFile jar = new JarFile(path.substring(0, index));
				return new InputStreamReader(jar.getInputStream(jar.getJarEntry(path.substring(index + 1))));
			}

			else {
				reader = new FileReader(new File(path));
			}
		} catch (IOException e) {
			// fall through
		}

		return reader;
	}
	
	
	private static final String BOLD="<b>";
	private static final String UNBOLD="</b>";
	private static final String NEWLINE="\n";
	private static final String PARA="<p>";
	
	/**
	 * Add name (probably a full signature) to the javadoc string, nicely formatted
	 * <p>Note: if we just use newlines, somebody takes them out later.
	 * This will be uglier in Context help till we better-format it, but prettier in hover help
	 * @param name the signature or properly qualified name of the thing
	 * @param doc the javadoc string
	 * @return
	 */
	private String addNameToDoc(String name, String doc) {
		if(doc == null) {
			doc = "";
		}
		doc=BOLD+name+UNBOLD+PARA+doc+PARA;
		return doc;
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
	 * @return text with the intervening (if any) leading star characters
	 * 
	 */
	private String getCommentText(String text, boolean stripLeadingTrailingParts) {
		StringBuilder result = new StringBuilder();
		String showResult = "|";
		boolean inBlockInfo=false; // true once we are into parsing the @block tags

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
			// determine if prev char was line delimiter; if so will affect how
			// we start next line
			fWasNewLine = ch == '\n' || ch == '\r';
			result.append(ch);
			showResult = "|" + result.toString() + "|";
		}
		String resStr = result.toString();

		return resStr;
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
	 * handle the at-param, at-returns etc tags by translating into html defn lists
	 * @param styledText
	 * @return
	 */
	private String decodeAts(String styledText) {
		return "";
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
		int fnd=fileSrc.indexOf(string, idx);
		return fnd == idx;
	}

	private int skipBackwardWhite(String fileSrc, int idx) {
		while (idx > 0 && Character.isWhitespace(fileSrc.charAt(idx - 1)))
			idx--;
		return idx;
	}
	/**
	 * Get the next 'token' in given string, starting at given index, up to next whitespace or end of string.
	 * <p>e.g. looking for \@param foo the description of something  -- looking for 'foo'
	 * @param str
	 * @param idx position in the string to start looking
	 * @return the token found.  The length of this token will specify how far parsing ate into the original string
	 */
	private String getNextToken(String str, int idx) {
		String result="";
		int begin=idx;
		int len=str.length();
		char tmp='!';
		try {

		String temp = str.substring(idx,idx+10);
		tmp=str.charAt(idx);
		// skip leading whitespace
		while(idx<len && Character.isWhitespace(str.charAt(idx))) {
			tmp=str.charAt(idx);
			idx++;
		}	
		while(idx<len && !Character.isWhitespace(str.charAt(idx))) {
			tmp=str.charAt(idx);
			idx++;
		}
		 result= str.substring(begin,idx);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return result;
	}

	private String stripArraySuffixes(String qualifiedName) {
		while (qualifiedName.endsWith("[]")) {
			qualifiedName = qualifiedName.substring(0,
					qualifiedName.length() - 2);
		}
		return qualifiedName;
	}

	private String[] convertParamTypes(ProcedureInstance pi) {
		String[] paramTypes = new String[pi.formalTypes().size()];

		int i = 0;
		for (Iterator iterator = pi.formalTypes().iterator(); iterator
				.hasNext();) {
			Type formalType = (Type) iterator.next();
			String typeName = formalType.toString();
			String typeSig = (typeName.indexOf('.') > 0) ? "L"
					+ formalType.toString() + ";" : typeName;
			paramTypes[i++] = typeSig;
		}
		return paramTypes;
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
	/**
	 * Given a list of string args, format them with commas between
	 * e.g. "int a, int b, String c"  etc.
	 * @param args
	 * @return the formatted string list of args
	 */
	@SuppressWarnings("unchecked")
	private String formatArgs(List a) {
		StringBuilder result=new StringBuilder();
		
		result.append("(");
		for (Iterator iterator = a.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			if (obj instanceof Formal) {
				Formal f = (Formal) obj;
				//String name = f.name();
				//String type = f.type().name();
				//String typeAndName=type+" "+name;
				// Just use toString(), it's already being done for us
				String typeAndName = f.toString();
				typeAndName=removeJavaLang(typeAndName);
				result.append(typeAndName);
				result.append(", ");
			}
			else {
				String simpleName=removeJavaLang(obj.toString());			
				result.append(simpleName+", "); // placeholder for the unexpected, probably type only instead of type+name
			}
		}
		String res=result.toString();
		// remove trailing comma (if any args were there at all) and add a closing paren
		if(res.endsWith(", ")) {
			res=res.substring(0,res.length()-2);
		}
		res=res+")";
		return res;
		
	}

	/**
	 * Remove the "java.lang." prefix on a type, if it exists
	 * @param name
	 * @return the truncated name
	 */
	private String removeJavaLang(String name) {
		final String JAVALANG="java.lang.";
		final int len=JAVALANG.length();
		String result=name;
		if(name.startsWith(JAVALANG)) {
			result=result.substring(len);
		}
		return result;
	}

}
