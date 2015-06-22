/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10doc.visit;

import java.io.IOException;
import java.util.Stack;

import lpg.runtime.IToken;
import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.ast.SourceFile_c;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.types.ClassDef.Kind;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.SilentErrorQueue;
import x10.ast.PropertyDecl;
import x10.ast.PropertyDecl_c;
import x10.ast.TypeDecl_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10SourceFile_c;
import x10.extension.X10Ext;
import x10.parser.X10SemanticRules;
import x10.types.TypeDef;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;
import x10.visit.X10DelegatingVisitor;
import x10doc.ExtensionInfo;
import x10doc.doc.X10ClassDoc;
import x10doc.doc.X10PackageDoc;
import x10doc.doc.X10RootDoc;
import x10doc.X10DocOptions;

public class X10DocGenerator extends X10DelegatingVisitor {

    private final Job job;
    private X10SemanticRules parser;
    private X10RootDoc rootDoc;
    private Stack<X10ClassDoc> stack;
    private X10SourceFile_c source;
    // stack of X10ClassDoc objects created as X10ClassDecl_c objects are
    // visited; the
    // X10ClassDecl_c objects visited included top level class declarations and
    // inner classes;
    // when a Doc object is created for a class member, it is added as a member
    // of the top
    // X10ClassDoc object of stack;
    private static final String PACKAGE_DUMMY_CLASS_NAME = "___TopLevelTypeDefs";

    public X10DocGenerator(Job job) {
        this.job = job;
    }

    @Override
    public void visit(Node n) {
        assert false : "visit: Unexpected node type " + n.getClass();
    }

    @Override
    public void visit(SourceFile_c n) {
        assert (job.source() instanceof FileSource);
        FileSource source = (FileSource) job.source();
        this.source = (X10SourceFile_c) n;
        if (!((X10DocOptions)job.extensionInfo().getOptions()).x10_config.ANTLR_PARSER){
        	try {
        		this.parser = (X10SemanticRules) job.extensionInfo().parser(source.open(), source,
                                                                        new SilentErrorQueue(0, "Ignored"));
        	} catch (IOException e) {
        		assert false : "Cannot reparse file " + source;
        	}
        }

        // List<TopLevelDecl> decls = n.decls();
        // X10ClassDoc[] classes = new X10ClassDoc[decls.size()];
        ExtensionInfo extInfo = (ExtensionInfo) job.extensionInfo();
        this.rootDoc = X10RootDoc.getRootDoc(extInfo.getOptions().output_directory.getPath(),
                                             extInfo.getOptions().doc_access_modifier);
        this.stack = new Stack<X10ClassDoc>();
        for (TopLevelDecl td : n.decls()) {
            // System.out.println("in visit(SourceFile_c): topleveldecl.getClass() = "
            // + td.getClass());
            visitAppropriate(td);
        }
        ((ExtensionInfo) job.extensionInfo()).setRoot(this.rootDoc);

        // rootDoc.printStats();
        if (!((X10DocOptions)job.extensionInfo().getOptions()).x10_config.ANTLR_PARSER){
        	this.parser = null;
        }
    }

    private String getDocComments(Node n) {
        String s = ((X10Ext) n.ext()).comment();
        if (s != null) return s;
        if (!((X10DocOptions)job.extensionInfo().getOptions()).x10_config.ANTLR_PARSER){
        	return printDocComments(n.position().offset());
        }
        return null;
    }

    @Override
    public void visit(X10ClassDecl_c n) {
        if (X10RootDoc.isSynthetic(n.classDef())) return;

        String comments = getDocComments(n);

        /*
         * System.out.println(
         * "visit(X10ClassDecl_c): Extracted comment text follows.");
         * System.out.println(X10Doc.rawCommentToText(comments));
         */

        // the following obtains an x10doc-specific context
        // X10DocData data = ((X10DocContext) tr.context()).getData();
        // if (n.flags().flags().isPrivate())
        // return;

        /*
         * System.out.print("Class: " + n.name() + "["); for (ParameterType p:
         * ((X10ClassDef) n.classDef()).typeParameters()) { // param name,
         * bounds System.out.print("<" + p.name().toString() + "," +
         * p.def().get() + ">, "); p.toType(); } // TypeParamNode x;
         * System.out.println("]");
         * 
         * System.out.print("  Properties: "); for (PropertyDecl p:
         * n.properties()) { System.out.print(p.name() + ", "); }
         * System.out.println(); System.out.print("  Interfaces: "); for
         * (TypeNode i: n.interfaces()) { System.out.print(i.nameString() +
         * ", "); X10ClassDef iClassDef = (X10ClassDef)
         * i.type().toClass().def(); } System.out.println();
         */

        X10ClassDoc containingClass = (stack.isEmpty() ? null : stack.peek());
        X10ClassDef classDef = (X10ClassDef) n.classDef();
        // System.out.println("ClassDef.fullname() = " + classDef.fullName());

        // boolean isIncluded =
        // ((job.extensionInfo().scheduler().commandLineJobs().contains(job) ||
        // (containingClass != null && containingClass.isIncluded())));
        // the class is included in the "specified set" if it is specified on
        // the command line
        // or it is an inner class of an included class
        X10ClassDoc cd = rootDoc.getSpecClass(classDef, containingClass, comments);
        cd.setSource(source);
        // all classes, including inner classes are added to rootDoc

        // for (TypeNode i: n.interfaces()) {
        // X10ClassDef intClassDef = (X10ClassDef) i.type().toClass().def();
        // X10ClassDoc intClassDoc = rootDoc.createRecClassDoc(intClassDef);
        // cd.addInterface(intClassDoc);
        // }

        // X10PackageDoc containingPackage =
        // rootDoc.getPackage(classDef.package_());
        // X10ClassDoc cd = new X10ClassDoc(classDef, containingClass, rootDoc,
        // comments);
        // cd.setPackage(containingPackage);
        // containingPackage.addClass(cd);
        // cd.setIncluded((job.extensionInfo().scheduler().commandLineJobs().contains(job)
        // ||
        // (containingClass != null && containingClass.isIncluded())));

        stack.push(cd);
        for (PropertyDecl p : n.properties()) {
            // properties are not included in n.body().member()
            visitAppropriate(p);
        }
        for (ClassMember cm : n.body().members()) {
            visitAppropriate(cm);
        }
        cd = stack.pop();
        // rootDoc.addClass(cd); // inner classes are also added to rootDoc
        if (containingClass != null) {
            containingClass.addInnerClass(cd);
        }
    }

    @Override
    public void visit(PropertyDecl_c n) {
        if (X10RootDoc.isSynthetic(n.fieldDef())) return;

        String comments = "/** Property. */";

        FieldDef fd = n.fieldDef();
        X10ClassDoc cd = stack.peek();
        cd.updateField((X10FieldDef) fd, comments);
    }

    @Override
    public void visit(X10FieldDecl_c n) {
        if (X10RootDoc.isSynthetic(n.fieldDef())) return;

        String comments = getDocComments(n);

        FieldDef fd = n.fieldDef();

        /*
         * if (!n.flags().flags().isPrivate()) { System.out.println("  Field: "
         * + n.flags().flags() + " " + n.type().toString() + " " + n.name()); }
         * 
         * System.out.println("    fd.toString() = " + fd);
         * System.out.println("    fd = n.fieldDef(); fd.name().toString() = " +
         * fd.name()); System.out.println("    fd.type().toString() = " +
         * fd.type().toString());
         * System.out.println("    fd.type().get().isArray() = " +
         * fd.type().get().isArray()); if (fd.type().get().isArray()) {
         * System.out.println("    fd.type().get().toArray().dims() = " +
         * fd.type().get().toArray().dims()); }
         */

        X10ClassDoc cd = stack.peek();
        // cd.addField(new X10FieldDoc((X10FieldDef) fd, cd, comments));
        cd.updateField((X10FieldDef) fd, comments);
    }

    @Override
    public void visit(X10ConstructorDecl_c n) {
        if (X10RootDoc.isSynthetic(n.constructorDef())) return;

        String comments = getDocComments(n);

        /*
         * System.out.print("  Constructor: " + n.returnType().nameString() +
         * " " + n.name() + "("); boolean first = true; for (Formal f:
         * n.formals()) { if (first) { System.out.print(f.type().nameString() +
         * " " + f.name()); first = false; } else { System.out.print(", " +
         * f.type().nameString() + " " + f.name()); } } System.out.println(")");
         */

        X10ClassDoc cd = stack.peek();
        // cd.addConstructor(new X10ConstructorDoc(((X10ConstructorDef)
        // n.constructorDef()), cd, comments));
        // X10ConstructorDoc constrDoc = new
        // X10ConstructorDoc(((X10ConstructorDef) n.constructorDef()), cd,
        // comments);
        cd.updateConstructor(((X10ConstructorDef) n.constructorDef()), comments);
    }

    @Override
    public void visit(X10MethodDecl_c n) {
        if (X10RootDoc.isSynthetic(n.methodDef())) return;

        String comments = getDocComments(n);

        /*
         * System.out.print("  Method: " + n.returnType().nameString() + " " +
         * n.name() + "("); boolean first = true; for (Formal f: n.formals()) {
         * if (first) { System.out.print(f.type().nameString() + " " +
         * f.name()); first = false; } else { System.out.print(", " +
         * f.type().nameString() + " " + f.name()); } } System.out.println(")");
         */

        X10ClassDoc cd = stack.peek();
        // cd.addMethod(new X10MethodDoc(((X10MethodDef) n.methodDef()), cd,
        // comments));
        // X10MethodDoc md = new X10MethodDoc(((X10MethodDef) n.methodDef()),
        // cd, comments);
        cd.updateMethod(((X10MethodDef) n.methodDef()), comments);
    }

    @Override
    public void visit(TypeDecl_c n) {
        if (X10RootDoc.isSynthetic(n.typeDef())) return;

        // System.out.println("visit(TypeDecl_c{" + n + "}: node not handled");
        String comments = getDocComments(n);
        TypeDef def = n.typeDef();
        X10ClassDoc xcd = null;

        if (stack.isEmpty()) {
            // Find/create the dummy X10ClassDoc for the top-level
            // typedefs/typedecls in this package.
            // First need to figure out what "this package" is...
            try {
                String typeDeclFullName = n.type().qualifierRef().get().toType().fullName().toString();
                String packageName = typeDeclFullName.substring(0, typeDeclFullName.lastIndexOf('.'));
                X10PackageDoc pkgDoc = rootDoc.getPackage(n.typeDef().package_(), packageName);
                // com.sun.javadoc.ClassDoc[] pkgClasses = pkgDoc.allClasses();

                xcd = pkgDoc.classDocForName(PACKAGE_DUMMY_CLASS_NAME);

                if (xcd == null) {
                    TypeSystem typeSystem = job.extensionInfo().typeSystem();
                    X10ClassDef x10CDef = (X10ClassDef) typeSystem.createClassDef();
                    x10CDef.name(Name.make(PACKAGE_DUMMY_CLASS_NAME));
                    x10CDef.flags(Flags.PUBLIC);
                    x10CDef.kind(Kind.TOP_LEVEL);
                    polyglot.types.Package pkg = typeSystem.packageForName(QName.make(packageName));
                    x10CDef.setPackage(new Ref_c<polyglot.types.Package>(pkg));
                    X10ClassDoc packageDummyClassDoc = new X10ClassDoc(x10CDef, null,
                                                                       "/** Top-level typedecls/defs for the package "
                                                                               + packageName + "**/");

                    xcd = packageDummyClassDoc;
                    xcd.setIncluded();
                    xcd.setPackage(pkgDoc);
                    pkgDoc.addClass(xcd);
                    rootDoc.addDummyClass(xcd);
                }
            } catch (SemanticException e) {
                e.printStackTrace(System.err);
                return;
            }
        } else {
            xcd = stack.peek();
        }
        xcd.updateTypeDef(def, comments);
    }

    // go through comments preceding a given offset corresponding to a
    // class/method/...
    // declaration, and print documentation comments
    String printDocComments(int offset) {
        String retVal = null;
        for (IToken t : parser.getIPrsStream().getTokenAtCharacter(offset).getPrecedingAdjuncts()) {
            String str = t.toString().trim();
            if (str.startsWith("/**")) {
                // System.out.println("adjunct: " + t);
                retVal = str;
            }
        }
        return retVal;
    }
}
