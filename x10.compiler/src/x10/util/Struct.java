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

package x10.util;


import java.util.*;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.TypeBuilder;
import x10.ast.*;
import x10.constraint.XVar;
import x10.types.constraints.ConstraintManager;
import x10.types.*;

import x10.extension.X10Ext;
import x10cpp.visit.SharedVarsMethods;

public class Struct {
    private final static java.util.Set<String> ignoreTypes = CollectionFactory.newHashSet();

    // these are the structs that are @NativeRep to java
    static {
        ignoreTypes.add("Boolean");
        ignoreTypes.add("Byte");
        ignoreTypes.add("UByte");
        ignoreTypes.add("Char");
        ignoreTypes.add("Short");
        ignoreTypes.add("UShort");
        ignoreTypes.add("Int"); 
        ignoreTypes.add("UInt");
        ignoreTypes.add("Long");
        ignoreTypes.add("ULong");
        ignoreTypes.add("Float");
        ignoreTypes.add("Double");
    }

    public static X10ClassDecl_c addStructMethods(TypeBuilder tb, X10ClassDecl_c n) {
        final TypeSystem xts =  tb.typeSystem();
        final X10ClassDef cd = (X10ClassDef) n.classDef();
        X10ParsedClassType ct = (X10ParsedClassType) cd.asType();

        QName fullName = cd.fullName();

        String strName = fullName.name().toString();
        final QName qualifier = fullName.qualifier();

        List<Ref<? extends Type>> interfaces = cd.interfaces();
        boolean hasAny = false;
        for (Ref<? extends Type> intf : interfaces) {
            if (intf.get().isAny()) {
                hasAny = true;
                break;
            }
        }
        if (!hasAny) {
            final ArrayList<Ref<? extends Type>> interfacesList = new ArrayList<Ref<? extends Type>>(interfaces);
            interfacesList.add(xts.lazyAny());
            cd.setInterfaces(interfacesList);
        }

        final Position pos = Position.compilerGenerated(n.body().position());


        final LazyRef<X10ParsedClassType> PLACE = Types.lazyRef(null);
        PLACE.setResolver(new Runnable() {
        	public void run() {
        		PLACE.update((X10ParsedClassType) xts.Place());
        	}
        });
        final LazyRef<X10ParsedClassType> STRING = Types.lazyRef(null);
        STRING.setResolver(new Runnable() {
        	public void run() {
        		STRING.update((X10ParsedClassType) xts.String());
        	}
        });
        final LazyRef<X10ParsedClassType> BOOLEAN = Types.lazyRef(null);
        BOOLEAN.setResolver(new Runnable() {
        	public void run() {
        		BOOLEAN.update((X10ParsedClassType) xts.Boolean());
        	}
        });




        // Now I add the auto-generated methods (equals(Any), equals(SomeStruct), hashCode(), toString())
        // if the programmer didn't already defined them
        // primitive classes do not define hashCode, and we should not auto-create hashCode for them,
        // or the java backend will translate:
        //      var x:Int; x.hashCode
        // to
        //      x.hashCode()
        // and it should translate it to:
        //      ((Object)x).hashCode()

        boolean isPrim = (qualifier!=null && qualifier.toString().equals("x10.lang") && ignoreTypes.contains(strName));
        boolean seenToString = isPrim;
        boolean seenTypeName = false;   // some unsigned types are still boxed, so do a honest check
        boolean seenHashCode = isPrim;
        boolean seenEqualsAny = isPrim;
        boolean seenEqualsSelf = isPrim;

       for (ClassMember member : n.body().members())
           if (member instanceof MethodDecl_c) {
                MethodDecl_c mdecl = (MethodDecl_c) member;
               final Flags methodFlags = mdecl.flags().flags();
               if (methodFlags.isStatic() || methodFlags.isAbstract()) continue;

                // The compiler provides implementations of equals, hashCode, and toString if
                // there are no user-defined implementations.  So, we need to search the struct's members
                // and determine which methods to auto-generate and which ones are user-provided.
                if (mdecl.name().id().toString().equals("toString") &&
                    mdecl.formals().isEmpty()) {
                    seenToString = true;
                }
                if (mdecl.name().id().toString().equals("typeName") &&
                        mdecl.formals().isEmpty()) {
                    seenTypeName = true;
                }
                if (mdecl.name().id().toString().equals("hashCode") &&
                    mdecl.formals().isEmpty()) {
                    seenHashCode = true;
                }
                // [DC] this code heavily broken... unsure how to fix it at this point
                if (mdecl.name().id().toString().equals("equals") && mdecl.formals().size() == 1) {
                    seenEqualsAny = true; // XTENLANG-2441: Needs to be a test to see if the type of formal is Any.
                    seenEqualsSelf = true; // XTENLANG-2441: Needs to be a test to see if the type of the formal is the Struct ct 
                }      
            }


       // I use the AST instead of the type, because the type hasn't been built yet (so ct.fields() is empty!)
       // I modify the AST before type-checking, because I want to connect calls to "equals"
       // with the correct  "equals(SomeStruct)"  or  "equals(Any)"
       // This is important for C++ efficiency (to prevent auto-boxing of structs in equality checking)
       ArrayList<FieldDecl> fields = new ArrayList<FieldDecl>();
       fields.addAll(n.properties());
       for (ClassMember member : n.body().members())
           if (member instanceof FieldDecl_c) {
                FieldDecl_c field = (FieldDecl_c) member;
                if (field.flags().flags().isStatic()) continue;
                fields.add(field);
           }

        final Flags flags = Flags.PUBLIC.Final();
        final NodeFactory nf = tb.nodeFactory();
        final TypeSystem ts = tb.typeSystem();
        final TypeNode intTypeNode = nf.CanonicalTypeNode(pos, ts.Int());
        final TypeNode boolTypeNode = nf.CanonicalTypeNode(pos, ts.Boolean());
        final TypeNode placeTypeNode = nf.CanonicalTypeNode(pos, ts.Place());
        final TypeNode stringTypeNode = nf.CanonicalTypeNode(pos, ts.String());
        final TypeNode anyTypeNode = nf.CanonicalTypeNode(pos, ts.Any());
        final List<TypeParamNode> typeParamNodeList = n.typeParameters();
        List<TypeNode> params = new ArrayList<TypeNode>();
        for (TypeParamNode p : typeParamNodeList)
            params.add(nf.CanonicalTypeNode(pos, p.type()));
        final TypeNode structTypeNode = typeParamNodeList.isEmpty() ? nf.CanonicalTypeNode(pos, cd.asType()) :
                nf.AmbDepTypeNode(pos, null,
                        nf.Id(pos, fullName.name()), params, Collections.<Expr>emptyList(), null);
        ArrayList<Stmt> bodyStmts;
        Expr expr;
        Block block;
        String methodName;
        X10MethodDecl md;


        /*
        // final public global safe def typeName():String { return "FULL_NAME"; }
        bodyStmts = new ArrayList<Stmt>();
        expr = nf.StringLit(pos, fullName.toString());
        bodyStmts.add(nf.Return(pos, expr));
        block = nf.Block(pos).statements(bodyStmts);
        methodName = "typeName";
        md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),stringTypeNode,nf.Id(pos,Name.make(methodName)),Collections.EMPTY_LIST,Collections.EMPTY_LIST,block);
        n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        */

        if (!seenTypeName) {
            Flags nativeFlags = Flags.PUBLIC.Native().Final();
            ArrayList<AnnotationNode> natives;
            Formal formal;
            // In the Java backend, some structs (like Int) are mapped to primitives (like int)
            // So I must add a native annotation on this method.

            //@Native("java", "x10.rtt.Types.typeName(#this)")
            //@Native("c++", "::x10aux::type_name(#0)")
            //global safe def typeName():String;
            natives = createNative(nf, pos, "x10.rtt.Types.typeName(#this)", "::x10aux::type_name(#0)");
            AnnotationNode nonEscaping = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "NonEscaping"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
            natives.add(nonEscaping);
            AnnotationNode synthetic = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Synthetic"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
            natives.add(synthetic);
            methodName = "typeName";
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,nativeFlags),stringTypeNode,nf.Id(pos,Name.make(methodName)),Collections.<Formal>emptyList(),null);
            md = (X10MethodDecl) ((X10Ext) md.ext()).annotations(natives);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        }

        if (!seenToString) {
            // final public global safe def toString():String {
            //  return "struct NAME:"+" FIELD1="+FIELD1+...;
            //          or
            //  return "struct NAME"; // if there are no fields
            // }
            bodyStmts = new ArrayList<Stmt>();
            expr = nf.StringLit(pos, "struct " + fullName + (fields.size()==0?"":":"));
            for (FieldDecl fi : fields) {
                 String name = fi.name().toString();
                 expr = nf.Binary(pos,expr, Binary.ADD,nf.StringLit(pos," "+name+"="));
                 expr = nf.Binary(pos,expr, Binary.ADD,nf.Field(pos,nf.This(pos),nf.Id(pos,name)));
            }
            bodyStmts.add(nf.Return(pos, expr));
            block = nf.Block(pos).statements(bodyStmts);
            methodName = "toString";
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),stringTypeNode,nf.Id(pos,Name.make(methodName)),Collections.<Formal>emptyList(),block);
            ArrayList<AnnotationNode> ans = new ArrayList<AnnotationNode>(1);
            AnnotationNode synthetic = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Synthetic"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
            ans.add(synthetic);
            md = (X10MethodDecl) ((X10Ext) md.ext()).annotations(ans);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        }
        if (!seenHashCode) {
            // final public global safe def hashCode():Int {
            //  var result:Int = 1;
            //  result = 8191*result + FIELD1.hashCode();
            //  ...
            //  return result;
            // }
            bodyStmts = new ArrayList<Stmt>();
            bodyStmts.add(nf.LocalDecl(pos, nf.FlagsNode(pos,Flags.NONE), intTypeNode,nf.Id(pos,"result"),nf.IntLit(pos, IntLit.INT,1)));
            final Local target = nf.Local(pos, nf.Id(pos, "result"));
            for (FieldDecl fi : fields) {
                String name = fi.name().toString();
                bodyStmts.add(nf.Eval(pos,nf.Assign(pos, target, Assign.ASSIGN,
                    nf.Binary(pos,
                        nf.Binary(pos,nf.IntLit(pos,IntLit.INT,8191),Binary.MUL,target),
                        Binary.ADD,
                        nf.Call(pos,nf.Field(pos,nf.This(pos),nf.Id(pos,name)),nf.Id(pos,"hashCode"))))));
            }
            bodyStmts.add(nf.Return(pos, target));
            block = nf.Block(pos).statements(bodyStmts);
            methodName = "hashCode";
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),intTypeNode,nf.Id(pos,Name.make(methodName)),Collections.<Formal>emptyList(),block);
            ArrayList<AnnotationNode> ans = new ArrayList<AnnotationNode>(1);
            AnnotationNode synthetic = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Synthetic"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
            ans.add(synthetic);
            md = (X10MethodDecl) ((X10Ext) md.ext()).annotations(ans);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        }
        // _struct_equals is used for == even when the user defined equals
        // (both backends need to convert == to _struct_equals)
        for (boolean isStructEquals : new boolean[]{false,true}) {
            methodName = isStructEquals ? SharedVarsMethods.STRUCT_EQUALS_METHOD : "equals";

            if (isStructEquals || !seenEqualsAny) {
                // final public global safe def equals(other:Any):Boolean {
                //  if (!(other instanceof NAME)) return false;
                //  return equals(other as NAME);
                // }
                bodyStmts = new ArrayList<Stmt>();
                Expr other =nf.Local(pos,nf.Id(pos,"other"));
                bodyStmts.add(nf.If(pos, nf.Unary(pos, Unary.NOT,
                                                  nf.Instanceof(pos,other,structTypeNode)),
                                                  nf.Return(pos,nf.BooleanLit(pos,false))));
                bodyStmts.add(nf.Return(pos,nf.Call(pos,nf.Id(pos,methodName),nf.Cast(pos,structTypeNode,other))));
                block = nf.Block(pos).statements(bodyStmts);
                Formal formal = nf.Formal(pos,nf.FlagsNode(pos,Flags.NONE),anyTypeNode,nf.Id(pos,"other"));
                md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),boolTypeNode,nf.Id(pos,Name.make(methodName)), Collections.singletonList(formal),block);
                ArrayList<AnnotationNode> ans = new ArrayList<AnnotationNode>(1);
                AnnotationNode synthetic = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Synthetic"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
                ans.add(synthetic);
                md = (X10MethodDecl) ((X10Ext) md.ext()).annotations(ans);
                n = (X10ClassDecl_c) n.body(n.body().addMember(md));
            }

            if (isStructEquals || !seenEqualsSelf) {
                // final public global safe def equals(other:NAME):Boolean {
                //  return true && FIELD1==other.FIELD1 && ...;
                // }
                bodyStmts = new ArrayList<Stmt>();
                Expr other =nf.Local(pos,nf.Id(pos,"other"));
                Expr res = fields.isEmpty() ? nf.BooleanLit(pos, true) : null;
                for (FieldDecl fi : fields) {
                    String name = fi.name().toString();
                    final Id id = nf.Id(pos, name);
                    Expr right = nf.Binary(pos,nf.Field(pos,nf.This(pos),id),Binary.EQ,nf.Field(pos,other,id));
                    if (res==null)
                        res = right;
                    else
                        res = nf.Binary(pos,res,Binary.COND_AND,right);
                }
                bodyStmts.add(nf.Return(pos, res));
                block = nf.Block(pos).statements(bodyStmts);
                Formal formal = nf.Formal(pos,nf.FlagsNode(pos,Flags.NONE),structTypeNode,nf.Id(pos,"other"));
                md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),boolTypeNode,nf.Id(pos,Name.make(methodName)),Collections.singletonList(formal),block);
                ArrayList<AnnotationNode> ans = new ArrayList<AnnotationNode>(1);
                AnnotationNode synthetic = nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Synthetic"), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()));
                ans.add(synthetic);
                md = (X10MethodDecl) ((X10Ext) md.ext()).annotations(ans);
                n = (X10ClassDecl_c) n.body(n.body().addMember(md));
            }
        }

       return n;
    }
    private static ArrayList<AnnotationNode> createNative(NodeFactory nf,Position pos, String java, String cpp) {
        ArrayList<AnnotationNode> res = new ArrayList<AnnotationNode>(2);
        for (int i=0; i<2; i++) {
            List<Expr> list = new ArrayList<Expr>(2);
            list.add(nf.StringLit(pos, i==0 ? "java" : "c++"));
            list.add(nf.StringLit(pos, i==0 ? java : cpp));
            res.add( nf.AnnotationNode(pos, nf.AmbMacroTypeNode(pos, nf.PrefixFromQualifiedName(pos,QName.make("x10.compiler")), nf.Id(pos, "Native"), Collections.<TypeNode>emptyList(), list)) );
        }
        return res;
    }
}
