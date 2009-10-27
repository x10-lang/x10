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

package org.eclipse.imp.x10dt.ui.editor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;
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
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.ObjectType;
import polyglot.types.ProcedureInstance;
import polyglot.types.ReferenceType;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.VarInstance;
import polyglot.util.Position;

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
        if (traceOn) System.out.println("----\n" + doc+"\n----\n");
        return doc;
    }

	/**
	 * Provides javadoc-like info (if available) and more for a variety of entities
	 */
	public String getHelpForEntity(Object target, IParseController parseController) {
		Node root = (Node) parseController.getCurrentAst();

		if (target instanceof Id) {
			if(traceOn)System.out.println("==>Id, get parent instead.");
			Id id = (Id) target;
			PolyglotNodeLocator locator = (PolyglotNodeLocator) parseController.getSourcePositionLocator();
			Node parent = (Node) locator.getParentNodeOf(id, root);
			target = parent;
		}

		if (target instanceof Field) { // field reference
			if(traceOn)System.out.println("==>Field");
			Field field = (Field) target;
			FieldInstance fi = field.fieldInstance();
			target = fi;
		}
		else if (target instanceof FieldDecl) {
			FieldDecl fieldDecl = (FieldDecl) target;
			FieldInstance fi = fieldDecl.fieldDef().asInstance(); // PORT 1.7 was fieldDecl.fieldInstance();
			target = fi;
		}
		if (target instanceof Local) { // field reference	
			Local local = (Local) target;
			LocalInstance li = local.localInstance();
			target = li;
		}
		else if (target instanceof LocalDecl) {
			LocalDecl localDecl = (LocalDecl) target;
			LocalInstance li = localDecl.localDef().asInstance(); // PORT1.7   was localDecl.localInstance();
			target = li;		
		}
		if (target instanceof FieldInstance) {
			if(traceOn)System.out.println("==>FieldInstance");
			FieldInstance fi = (FieldInstance) target;
			ReferenceType ownerType = fi.container().toReference(); // PORT1.7 cast must succeed?  was fi.container();

			if (ownerType.isClass()) {
				ClassType ownerClass = (ClassType) ownerType;
				String ownerName = ownerClass.fullName().toString(); // PORT1.7 was fullname();
				if (isJavaType(ownerName)) {
					IType javaType = findJavaType(ownerName, parseController);
					IField javaField = javaType.getField(fi.name().toString()); // PORT1.7  was fi.name();

					return getJavaDocFor(javaField);
				} else {
					//String sig = fi.toString();  // field int Class.varName
					String type = fi.type().toString(); // int   or pkg.TypeName; want TypeName only
					type=unqualify(type);//FIXME must be a better way to get simple type, not fully qualified type
					//sig= fi.declaration().toString();
					String varName=fi.name().toString();  // PORT 1.7 was just name();
					String sig = type+" "+ownerName+"."+varName;

					return getX10DocFor(sig,fi);  // 2nd arg needs to be Node
				}
			}
			return "Field '" + fi.name() + "' of type " + fi.type().toString();
		} else if (target instanceof NamedVariable) {
			if(traceOn)System.out.println("==>NamedVariable");
			NamedVariable var = (NamedVariable) target;
			
			Type type = var.type();

			return "Variable '" + var + "' of type " + type.toString(); //PORT1.7 var.name() changed to var (implicit toString())
		} else if (target instanceof Call) {
			if(traceOn)System.out.println("==>Call");
			Call call = (Call) target;
			MethodInstance mi = call.methodInstance();    
			ObjectType ownerType = (ObjectType)mi.container();//PORT1.7 ReferenceType->ObjectType.  We assume the cast will succeed.

			if (ownerType.isClass()) {
				ClassType ownerClass = (ClassType) ownerType;
				String ownerName = ownerClass.fullName().toString();//PORT1.7 fullName() no longer returns a string
				//String fullName =ownerName+"."+mi.name();

				if (isJavaType(ownerName)) {
					IType javaType = findJavaType(ownerName, parseController);
					String[] paramTypes = convertParamTypes(mi);
					IMethod method = javaType.getMethod(mi.name().toString(), paramTypes);//PORT1.7 name() no longer returns String

					return getJavaDocFor(method);
				} else {
					String sig = getSignature(mi);               
					return getX10DocFor(sig, mi);
				}
			}
			return "Method " + mi.signature() + " of type " + mi.container().toString();
		} else if (target instanceof VarInstance) {
			// local var, parm, (java or x10) or field
			// won't fall thru to Declaration
			if(traceOn)System.out.println("==>VarInstance, no action");
			VarInstance var = (VarInstance) target;

			// this is a method reference (the call)
			
		} else if (target instanceof MethodDef) {
			if(traceOn)System.out.println("==>MethodDef");
		    MethodDef methodDef = (MethodDef) target;
		    String classInfo=methodDef.container().get().toString();
		    String sig=methodDef.signature();
		   
		    String str= BOLD+ classInfo + "." + sig + UNBOLD + PARA;
		    String s2=getX10DocFor(methodDef);
		    str=str+"\n"+s2;  // newline simply makes stdout output more readable
		    return str;
		} else if (target instanceof MethodInstance || target instanceof ConstructorInstance) {
			if(traceOn)System.out.println("==>MethodInstance or ConstructorInstance");
			//we get different info from different interfaces, so make them both for use here:
			MemberInstance memi = (MemberInstance) target;
			ProcedureInstance pi = (ProcedureInstance)target;
			 
			
			if (isJavaMember(memi)) {   
				StructType rt=memi.container();//PORT1.7  ReferenceType -> StructType
				// if java then we can omit the 'java.lang' prefix ... TBD
				if (rt instanceof ClassType) {
					ClassType ct = (ClassType) rt;
					String fullname=ct.fullName().toString(); //PORT1.7  fullName no longer returns a String
					IType it = findJavaType(fullname, parseController);
					String[] paramTypes = convertParamTypes(pi);
					String mname = null;
					if (pi instanceof ConstructorInstance) {
						mname = ct.name().toString();  //PORT1.7 name() no longer returns a String
					} else {
						mname = ((MethodInstance) pi).name().toString();  //PORT1.7 name() no longer returns a String
					}
					IMethod method = it.getMethod(mname, paramTypes);
					fullname=fullname+"."+mname;  // add args?
					String doc = getJavaDocFor(fullname, method);
					return doc;
				}
			} else { // x10
				 /*
				Declaration mti = (Declaration) target;
				String sig ="?";
				ReferenceType rt = memi.container();
				if(rt instanceof ClassType) {
					//name=((ClassType)rt).name(); //w/o pkg name
					name=((ClassType)rt).fullName();
				} else {
					MethodInstance mi = (MethodInstance) target;
					name = mi.name();  
					List types = mi.formalTypes();  // args???
					int numArgs=types.size();
					sig=getSignature(mi);
				}
				*/
				if(target instanceof MethodInstance) {
					if(traceOn)System.out.println("==>MethodInstance");
					MethodInstance mi = (MethodInstance) target;
					String sig=getSignature(mi);
					String doc = getX10DocFor(sig,mi);
					return doc;
				}else { // constructorInstance
					if(traceOn)System.out.println("==>ConstructorInstance?");
					ConstructorInstance ci = (ConstructorInstance)target;
					String name="(name)";
					String t=ci.toString();
					StructType rt = ci.container();//PORT1.7  ReferenceType -> StructType
					if(rt instanceof Named) {
						Named named = (Named) rt;
						//name=named.name().toString();//PORT1.7 name() no longer returns a String
						name=named.fullName().toString();//PORT1.7 fullName() no longer returns a String
					}
					String args=formatArgs(ci.formalTypes());
					String sig=name+args;
					String doc = getX10DocFor(sig, ci);
					return doc;
				}

			}
			
		} 
		else if (target instanceof ClassType) {  
			if(traceOn)System.out.println("==>ClassType");
			ClassType ct = (ClassType) target;
			String qualifiedName =ct.fullName().toString();//PORT1.7 fullName() no longer returns a String

			if (isJavaType(qualifiedName)) { 
				IType javaType = findJavaType(qualifiedName, parseController);
				String doc = getJavaDocFor(javaType); 
				return doc;
			} else {				
				// It's an X10 classtype (not java)
				ClassType type = (ClassType)target;
				String qualifiedName2 = type.fullName().toString();//PORT1.7 fullName()->fullName().toString()
				qualifiedName2 = stripArraySuffixes(qualifiedName);			
				String ret2= getJavaOrX10DocFor(qualifiedName2, type, parseController);//BRT
				//
				return getX10DocFor(qualifiedName, ct);
			}
			
		} else if (target instanceof ClassDecl) {
			if(traceOn)System.out.println("==>ClassDecl");
			ClassDecl cd = (ClassDecl)target;
			String name=cd.name().id().toString();//PORT1.7 want fullname, how to get from ClassDecl?
			String fullName = cd.classDef().fullName().toString(); ////PORT1.7 is this right?? ask Nate
			String doc = getX10DocFor(fullName,cd);
			return doc;
		}
		else if (target instanceof MethodDecl) {
			MethodDecl md = (MethodDecl) target;
			if(traceOn)System.out.println("==>MethodDecl");
			
			String doct=getX10DocFor(md);
			String tempNameMd=md.toString();// does not include pkg info: public int foo(...);
			if(traceOn)System.out.println("====> MethodDecl.toString(): "+tempNameMd);
			//MethodInstance mi = md.methodInstance();
			MethodInstance mi=md.methodDef().asInstance();//PORT1.7 was md.methodInstance();
			String tempName=mi.toString(); // lots of info: method public int my.pkg.foo(type,type);
			if(traceOn)System.out.println("====> methodInstance()(): "+tempName);
			
			String name="";
			//MethodInstance test;
			String sig = md.methodDef().signature();//PORT1.7 see what this returns.  arg names too???
			//String sig = mi.signature();// doesn't include arg names, just types
			if(traceOn)System.out.println("====> sig: "+sig);
			
				 
			StructType rt = mi.container();//PORT1.7  ReferenceType -> StructType
			if(rt instanceof ClassType) {
				ClassType ct = (ClassType) rt;
				name =ct.fullName().toString();// includes package info		//PORT1.7 fullName() no longer returns a String
				if(traceOn)System.out.println("====> ClassType.fullName : "+name);
				
			}
			name = getSignature(mi,md);	
			if(traceOn)System.out.println("====> getSignature(): "+name);
			
			String doc = getX10DocFor(name, md);//PORT1.7 md is a Node; was md.methodInstance()
			if(traceOn)System.out.println("====> RETURNED: getX10DocFor(sig,md): "+tempNameMd);
			
			return doc;
		} else if (target instanceof FieldDecl) {
			if(traceOn)System.out.println("==>FieldDecl");
			FieldDecl fd = (FieldDecl) target;
			FieldInstance fi = fd.fieldDef().asInstance();//PORT1.7 was fd.fieldInstance();
			return getX10DocFor(fi);
		}

		else if (target instanceof TypeNode) {
			if(traceOn)System.out.println("==>TypeNode");
			TypeNode typeNode = (TypeNode) target;
			PolyglotNodeLocator locator = (PolyglotNodeLocator) parseController.getSourcePositionLocator();
			Node parent = (Node) locator.getParentNodeOf(target, root);

			if (parent instanceof ConstructorDecl) {
				ConstructorDecl cd = (ConstructorDecl) parent;
				//String id = cd.id().toString();//shortname
				//String name = typeNode.name();//shortname
				String fullName = typeNode.toString();// FIXME better way of getting fully qualified name, incl. pkg info??
				
				// get Constructor args, if any
				String sig=fullName+formatArgs(cd.formals()); 
				ConstructorInstance ci = cd.constructorDef().asInstance(); //PORT1.7 was cd.constructorInstance();
				return getX10DocFor(sig, ci);  //PORT1.7 use local variable
			} else if (parent instanceof New) {
				New n = (New) parent;		
				return getX10DocFor(n.constructorInstance());
			} else {
				Type type = typeNode.type();			
				String qualifiedName = typeNode.qualifierRef().get().toString();//PORT1.7 was qualifier()->qualifierRef().get()
				qualifiedName = stripArraySuffixes(qualifiedName);
				return getJavaOrX10DocFor(qualifiedName, type, parseController); 
			}
		}
		else if (target instanceof ClassType) {
			if(traceOn)System.out.println("==>ClassType 2 - unreachable??");
			ClassType type = (ClassType)target;
			String qualifiedName = type.fullName().toString();//PORT1.7 fullName()->fullName().toString()
			qualifiedName = stripArraySuffixes(qualifiedName);			
			return getJavaOrX10DocFor(qualifiedName, type, parseController);//BRT
			
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
		MethodInstance mi=md.methodDef().asInstance();//PORT1.7 md.methodInstance()->md.methodDef().asInstance()
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
		StructType type1 = mi.container(); //PORT1.7 ReferenceType changed to StructType

		// We assume this is an ObjectType since we are dealing with Methods
		assert(type1 instanceof ObjectType);
		ObjectType type = (ObjectType) type1;

		String containerName="(unspecified)";
		if(type instanceof Named) {
			Named ct = (Named) type;
			containerName =ct.fullName().toString();	//PORT1.7 ct.fullName()->ct.fullName().toString()
		}

		// find return value type
		Type rType = mi.returnType();
		// do we always add a dot? what if containerName is empty? default
		// package? Should *always* have at least a container/class name.
		// Methinks.
		String sig=rType+" "+containerName+"."+mi.name();
		
		// get the args. use MethodDecl if we have it (more complete info)
		List argList=null;
		if(md==null) {
			argList=mi.formalTypes();
		}else {
			argList=md.formals(); // this includes arg names, mi.formalTypes() does not
		}
		String argString=formatArgs(argList);
		sig = sig+argString;
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
		String doc=null;
		if (isJavaType(qualifiedName)) {
			IType javaType = findJavaType(qualifiedName, parseController);
			doc= (javaType != null) ? getJavaDocFor(javaType) : "";
		} else {
			doc= type.isClass() ? getX10DocFor(qualifiedName, (ClassType) type) : "";
		}
		return doc;
	}

	/**
	 * Get the javadoc-like comment string for an X10 declaration
	 * <p>Does not return signature(qualifiedName) info if the javadoc is empty.
	 * <p>That is, if the javadoc is empty, don't return anything
	 */
	@SuppressWarnings("restriction")
	private String getX10DocFor(String qualifiedName, TypeObject decl) {//PORT1.7 Declaration -> TypeObject?  try this.
		String doc = getX10DocFor(decl);
		if(doc!=null) doc = addNameToDoc(qualifiedName, doc);
		// if we return Null, HoverHelper will display something unless it's the decl. Uncomment this if you don't want it to.
		//  that is, if you're hovering right over the declaration for the thing, you probably don't need a hover with only type info.
		//if(doc==null) doc="";
		return doc;
	}
	private String getX10DocFor(TypeObject decl) {//PORT1.7 Declaration -> TypeObject?  try this.
		
		String doc = getNewRawX10DocFor(decl.position());
		return doc;
	}
	/**
	 * Get the javadoc-like comment string for an X10 entity represented by a Node
	 * (Note that this includes ClassDecl, formerly handled separately)
	 */
	@SuppressWarnings("restriction")
	private String getX10DocFor(Node node) {
		String doc = getNewRawX10DocFor(node.position());
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
		String path = pos.file();
		String result="";
		try {
			Reader reader = new FileReader(new File(path));
			String fileSrc = readReader(reader);
			int idx = pos.offset();
			int endOffset=pos.endOffset();
		
			String s1=fileSrc.substring(idx,endOffset);

			idx = skipBackwardWhite(fileSrc, idx);
			boolean test=lookingPastEndOf(fileSrc, idx, "*/");
			
			//test=isImmediatelyBefore(fileSrc, idx, fileSrc);
			//test=true;
			if (test) {
				result = collectBackwardTo(fileSrc, idx, "/**");
				if(result!=null && result.length()>0) {
					String doc = getCommentText(result);// strip comment chars,stars, etc
					if (traceOn) System.out.println("X10DocProvider.getX10DocCharsFor: " + doc);
					StringReader rdr = new StringReader(doc);
					X10Doc2HTMLTextReader xrdr = new X10Doc2HTMLTextReader(rdr);
					result = readReader(xrdr); // seems to give same result as getCommentText() above
					// String result = xrdr.getString();
				}
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Exception ignored: "+e.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			se.printStackTrace();
			System.out.println("Exception ignored: "+se.getMessage());
		}
		return result;
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
		int len=fileSrc.length();
		int last=fileSrc.lastIndexOf(string,idx);
		if(last==-1) {
			if(traceOn)System.out.println("  no x10doc info found.");
			return "";  // no x10doc info
		}
		return fileSrc.substring(fileSrc.lastIndexOf(string, idx), idx);
	}

	/**
	 * Is the given string located at the end of fileSrc?  <br>
	 * That is, is the given string located JUST prior to index position in filesrc, or anywhere after it? <br>
	 * (BRT: Don't understand the 'looking past end of ' method title. "should we look at or past this position?" maybe? )
	 * @param fileSrc
	 * @param endIdx  end index in fileSrc
	 * @param string
	 * @return
	 */
	private boolean lookingPastEndOf(String fileSrc, int endIdx, String string) {
		int idx = endIdx - string.length();
		int fnd=fileSrc.indexOf(string, idx);
		return fnd == idx;
	}
	/**
	 * Is the given string located immediately before the given index position, not counting whitespace?
	 * @param fileSrc
	 * @param endIdx
	 * @param string
	 * @return
	 */
	/* untested
	private boolean isImmediatelyBefore(String fileSrc, int endIdx, String string) {
		String beforeStr=fileSrc.substring(0,endIdx).trim();
		int loc=beforeStr.lastIndexOf(string);
		return loc>=0;
	}
	*/
	/**
	 * Is the given string located before the given index in the fileSrc?
	 * @param fileSrc
	 * @param endIdx
	 * @param string
	 * @return
	 */
	private boolean lookingBefore(String fileSrc, int endIdx, String string) {
		int idx = endIdx - string.length();
		int fnd=fileSrc.indexOf(string, idx);
		return fnd == idx;
	}

	/**
	 * Find position of previous non-whitespace character in the given string, starting at index position.
	 * @param fileSrc
	 * @param idx
	 * @return
	 */
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

	private boolean isJavaType(String qualifiedName) {
		return qualifiedName.startsWith("java.");
	}
	@SuppressWarnings("restriction")
	private boolean isJavaMember(MemberInstance mem) {
		StructType rt = mem.container();//PORT1.7 ReferenceType->StructType
		if(rt instanceof ClassType) {
			ClassType ct = (ClassType) rt;
			String fullname = ct.fullName().toString(); //PORT1.7 fullName() no longer returns a string
			return isJavaType(fullname);
			
		}
		return false;
		
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

	/**
	 * Get javadoc info - will get from jar file if necessary
	 * 
	 * @param member
	 * @return
	 */
	private String getJavaDocFor(IMember member) {
		try {
			Reader reader = JavadocContentAccess.getHTMLContentReader(member, true, true);

			if (reader == null)
				return "";
			String doc = readReader(reader);
			if (traceOn)
				System.out.println("X10ContextHelper.getJavaDocFor(): " + doc);
			// BRT note: e.g. for System.out.println, includes double <code><code>
			// which loses the contents in context help view.
			// JDT hover does correctly display this, however
			
			/*
			// how to get full name of member?
			String name=member.toString();
			String ename=member.getElementName();
			String cname = member.getClass().getName();
			IType type=member.getDeclaringType();

			doc=addNameToDoc(name, doc);
			*/
			return doc;
		} catch (JavaModelException e) {
			String msg=e.getMessage();
			return "";
		}
	}
	/**
	 * Get javadoc info for a member, and since we can't determine a reasonably formatted name,
	 * we get the name passed in from those who have more information than us.
	 * @param name
	 * @param member
	 * @return
	 */
	private String getJavaDocFor(String name, IMember member) {
		String doc = getJavaDocFor(member);
		doc = addNameToDoc(name, doc);
		return doc;
	}

	private IType findJavaType(String qualName, IParseController parseController) {
		try {
			IJavaProject javaProject = JavaCore.create(parseController.getProject().getRawProject());
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