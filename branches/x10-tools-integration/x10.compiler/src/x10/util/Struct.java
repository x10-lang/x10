/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import polyglot.ast.Expr;
import polyglot.ast.Block;
import polyglot.ast.Stmt;
import polyglot.ast.Binary;
import polyglot.ast.FieldDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.IntLit;
import polyglot.ast.Assign;
import polyglot.ast.Local;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Unary;
import polyglot.ast.TypeNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;
import x10.ast.X10StringLit_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10MethodDecl;
import x10.ast.TypeParamNode;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XVar;
import x10.constraint.XTerms;
import x10.types.X10ClassDef;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeSystem_c;
import x10cpp.visit.SharedVarsMethods;

public class Struct {
    private final static java.util.Set<String> ignoreTypes = new HashSet<String>();

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
        ignoreTypes.add("Place");
    }

    public static X10ClassDecl_c addStructMethods(TypeBuilder tb, X10ClassDecl_c n) {
        final X10TypeSystem_c xts = (X10TypeSystem_c) tb.typeSystem();
        final X10ClassDef cd = (X10ClassDef) n.classDef();
        X10ParsedClassType ct = (X10ParsedClassType) cd.asType();

        QName fullName = cd.fullName();

        String strName = fullName.name().toString();
        final QName qualifier = fullName.qualifier();

        final ArrayList<Ref<? extends Type>> interfacesList = new ArrayList<Ref<? extends Type>>(cd.interfaces());
        interfacesList.add(xts.lazyAny());
        cd.setInterfaces(interfacesList);

       final Position pos = Position.COMPILER_GENERATED;

       String fullNameWithThis = fullName + "#this";
       //String fullNameWithThis = "this";
       XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
       XVar thisVar = XTerms.makeLocal(thisName);




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
       final LazyRef<X10ParsedClassType> OBJECT = Types.lazyRef(null);
       OBJECT.setResolver(new Runnable() {
           public void run() {
               OBJECT.update((X10ParsedClassType) xts.Object());
           }
       });

       X10MethodDef mi;
	
       // @Native("java", "x10.lang.Place.place(x10.core.Ref.home(#0))")
       // property def home():Place
       mi = xts.methodDef(pos, Types.ref(ct), 
               X10Flags.toX10Flags(Flags.PUBLIC.Native()).Property().Global().Safe(), 
               PLACE,
               xts.homeName(), 
               Collections.EMPTY_LIST, 
               Collections.EMPTY_LIST, 
               thisVar,
               Collections.EMPTY_LIST, 
               null, 
               null, 
               Collections.EMPTY_LIST, 
               null, //offerType
               null);
       final LazyRef<X10ParsedClassType> NATIVE_LOC = Types.lazyRef(null);
       NATIVE_LOC.setResolver(new Runnable() {
           public void run() {
               List<Expr> list = new ArrayList<Expr>(2);
               list.add(new X10StringLit_c(pos, "java"));
               list.add(new X10StringLit_c(pos,  "x10.lang.Place.place(x10.core.Ref.home(#0))"));
               X10ParsedClassType ann=  (X10ParsedClassType) ((X10ParsedClassType) xts.NativeType()).propertyInitializers(list);
               NATIVE_LOC.update(ann);
           }
       });
       mi.setDefAnnotations(Collections.<Ref<? extends Type>> singletonList(NATIVE_LOC));
       cd.addMethod(mi);
        
       // @Native("java", "x10.core.Ref.at(#0, #1)")
       // property def at(p:Object):boolean;
       List<LocalDef> parameters = xts.dummyLocalDefs(Collections.<Ref<? extends Type>> singletonList(OBJECT));
       mi = xts.methodDef(pos, Types.ref(ct), 
               X10Flags.toX10Flags(Flags.PUBLIC.Native()).Property().Safe(), 
               BOOLEAN,
               Name.make("at"), 
               Collections.EMPTY_LIST, 
               Collections.<Ref<? extends Type>> singletonList(OBJECT),
               thisVar,
               parameters,
               null, 
               null, 
               Collections.EMPTY_LIST, 
               null, //offerType
               null);
       final LazyRef<X10ParsedClassType> NATIVE_AT_1 = Types.lazyRef(null);
       NATIVE_AT_1.setResolver(new Runnable() {
           public void run() {
               List<Expr> list = new ArrayList<Expr>(2);
               list.add(new X10StringLit_c(pos, "java"));
               list.add(new X10StringLit_c(pos, "x10.core.Ref.at(#0, #1)"));
               X10ParsedClassType ann=  (X10ParsedClassType) ((X10ParsedClassType) xts.NativeType()).propertyInitializers(list);
               NATIVE_AT_1.update(ann);
           }
       });
       mi.setDefAnnotations(Collections.<Ref<? extends Type>> singletonList(NATIVE_AT_1));
       cd.addMethod(mi);

        /*
       // @Native("java", "x10.core.Ref.typeName(#0)")
       // @Native("c++", "x10aux::type_name(#0)")
       // native final global safe def typeName():String;        
       mi = xts.methodDef(pos,
               Types.ref(ct),
               X10Flags.toX10Flags(Flags.PUBLIC.Native().Final()).Global().Safe(), 
               STRING,
               Name.make("typeName"),
               Collections.EMPTY_LIST, 
               Collections.EMPTY_LIST,
               thisVar,
               Collections.EMPTY_LIST, 
               null,
               null,
               Collections.EMPTY_LIST,
               null, // offerType
               null
       );
       final LazyRef<X10ParsedClassType> NATIVE_TYPE_NAME = Types.lazyRef(null);
       NATIVE_TYPE_NAME.setResolver(new Runnable() {
           public void run() {
               List<Expr> list = new ArrayList<Expr>(2);
               list.add(new X10StringLit_c(pos, "java"));
               list.add(new X10StringLit_c(pos,  "x10.core.Ref.typeName(#0)"));
               X10ParsedClassType ann=  (X10ParsedClassType) ((X10ParsedClassType) xts.NativeType()).propertyInitializers(list);
               NATIVE_TYPE_NAME.update(ann);
           }
       });
       final LazyRef<X10ParsedClassType> NATIVE_CPP_TYPE_NAME = Types.lazyRef(null);
       NATIVE_CPP_TYPE_NAME.setResolver(new Runnable() {
           public void run() {
               List<Expr> list = new ArrayList<Expr>(2);
               list.add(new X10StringLit_c(pos, "c++"));
               list.add(new X10StringLit_c(pos,  "x10aux::type_name(#0)"));
               X10ParsedClassType ann=  (X10ParsedClassType) ((X10ParsedClassType) xts.NativeType()).propertyInitializers(list);
               NATIVE_CPP_TYPE_NAME.update(ann);
           }
       });

       List<Ref<? extends Type>> tn_ann = new ArrayList<Ref<? extends Type>>();
       tn_ann.add(NATIVE_TYPE_NAME);
       tn_ann.add(NATIVE_CPP_TYPE_NAME);
       mi.setDefAnnotations(tn_ann);
       //mi.setDefAnnotations(Collections.<Ref<? extends Type>> singletonList(NATIVE_TYPE_NAME));
       cd.addMethod(mi);
       */

       // @Native("java", "x10.core.Ref.at(#0, #1.id)")
       // property def at(p:Place):boolean;
       parameters = xts.dummyLocalDefs(Collections.<Ref<? extends Type>> singletonList(PLACE));
       mi = xts.methodDef(pos, Types.ref(ct), 
               X10Flags.toX10Flags(Flags.PUBLIC.Native()).Property().Safe(), 
               BOOLEAN,
               Name.make("at"), 
               Collections.EMPTY_LIST, 
               Collections.<Ref<? extends Type>> singletonList(PLACE),
               thisVar,
               parameters,
               null, 
               null, 
               Collections.EMPTY_LIST, 
               null, // offerType
               null);
       final LazyRef<X10ParsedClassType> NATIVE_AT_2 = Types.lazyRef(null);
       NATIVE_AT_2.setResolver(new Runnable() {
           public void run() {
               List<Expr> list = new ArrayList<Expr>(2);
               list.add(new X10StringLit_c(pos, "java"));
               list.add(new X10StringLit_c(pos, "x10.core.Ref.at(#0, #1.id)"));
               X10ParsedClassType ann=  (X10ParsedClassType) ((X10ParsedClassType) xts.NativeType()).propertyInitializers(list);
               NATIVE_AT_2.update(ann);
           }
       });
       mi.setDefAnnotations(Collections.<Ref<? extends Type>> singletonList(NATIVE_AT_2));
       cd.addMethod(mi);



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
        boolean seenHashCode = isPrim;
        boolean seenEquals = isPrim;




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
                if (mdecl.name().id().toString().equals("hashCode") &&
                    mdecl.formals().isEmpty()) {
                    seenHashCode = true;
                }
                if (mdecl.name().id().toString().equals("equals") &&
                    mdecl.formals().size() == 1) {
                    seenEquals = true;
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

        final Flags flags = X10Flags.GLOBAL.Safe().Public().Final();
        final X10NodeFactory nf = (X10NodeFactory)tb.nodeFactory();
        final TypeNode intTypeNode = nf.TypeNodeFromQualifiedName(pos,QName.make("x10.lang","Int"));
        final TypeNode boolTypeNode = nf.TypeNodeFromQualifiedName(pos,QName.make("x10.lang","Boolean"));
        final TypeNode stringTypeNode = nf.TypeNodeFromQualifiedName(pos,QName.make("x10.lang","String"));
        final TypeNode anyTypeNode = nf.TypeNodeFromQualifiedName(pos,QName.make("x10.lang","Any"));
        final List<TypeParamNode> typeParamNodeList = n.typeParameters();
        List<TypeNode> params = new ArrayList<TypeNode>();
        for (TypeParamNode p : typeParamNodeList)
            params.add(nf.TypeNodeFromQualifiedName(pos,QName.make(null,p.name().id())));
        final TypeNode structTypeNode = typeParamNodeList.isEmpty() ? nf.TypeNodeFromQualifiedName(pos,fullName) :
                nf.AmbDepTypeNode(pos, null,
                        nf.Id(pos,fullName.name()), params, Collections.EMPTY_LIST, null);
        ArrayList<Stmt> bodyStmts;
        Expr expr;
        Block block;
        String methodName;
        X10MethodDecl md;


        // final public global safe def typeName():String { return "FULL_NAME"; }
        bodyStmts = new ArrayList<Stmt>();
        expr = nf.StringLit(pos, fullName.toString());
        bodyStmts.add(nf.Return(pos, expr));
        block = nf.Block(pos).statements(bodyStmts);
        methodName = "typeName";
        md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),stringTypeNode,nf.Id(pos,Name.make(methodName)),Collections.EMPTY_LIST,Collections.EMPTY_LIST,block);
        n = (X10ClassDecl_c) n.body(n.body().addMember(md));

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
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),stringTypeNode,nf.Id(pos,Name.make(methodName)),Collections.EMPTY_LIST,Collections.EMPTY_LIST,block);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        }
        if (!seenHashCode) {
            // final public global safe def hashCode():Int {
            //  var result:Int = 0;
            //  result = 31*result + FIELD1.hashCode();
            //  ...
            //  return result;
            // }
            bodyStmts = new ArrayList<Stmt>();
            bodyStmts.add(nf.LocalDecl(pos, nf.FlagsNode(pos,Flags.NONE), intTypeNode,nf.Id(pos,"result"),nf.IntLit(pos, IntLit.INT,0)));
            final Local target = nf.Local(pos, nf.Id(pos, "result"));
            for (FieldDecl fi : fields) {
                String name = fi.name().toString();
                bodyStmts.add(nf.Eval(pos,nf.Assign(pos, target, Assign.ASSIGN,
                    nf.Binary(pos,
                        nf.Binary(pos,nf.IntLit(pos,IntLit.INT,31),Binary.MUL,target),
                        Binary.ADD,
                        nf.Call(pos,nf.Field(pos,nf.This(pos),nf.Id(pos,name)),nf.Id(pos,"hashCode"))))));
            }
            bodyStmts.add(nf.Return(pos, target));
            block = nf.Block(pos).statements(bodyStmts);
            methodName = "hashCode";
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),intTypeNode,nf.Id(pos,Name.make(methodName)),Collections.EMPTY_LIST,Collections.EMPTY_LIST,block);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        }
        // _struct_equals is used for == even when the user defined equals
        // (both backends need to convert == to _struct_equals)
        for (boolean isStructEquals : new boolean[]{false,true}) {
            methodName = isStructEquals ? SharedVarsMethods.STRUCT_EQUALS_METHOD : "equals";
            if (!isStructEquals && seenEquals) continue;

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
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),boolTypeNode,nf.Id(pos,Name.make(methodName)), Collections.singletonList(formal),Collections.EMPTY_LIST,block);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));

            // final public global safe def equals(other:NAME):Boolean {
            //  return true && FIELD1==other.FIELD1 && ...;
            // }
            bodyStmts = new ArrayList<Stmt>();
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
            formal = nf.Formal(pos,nf.FlagsNode(pos,Flags.NONE),structTypeNode,nf.Id(pos,"other"));
            md = nf.MethodDecl(pos,nf.FlagsNode(pos,flags),boolTypeNode,nf.Id(pos,Name.make(methodName)),Collections.singletonList(formal),Collections.EMPTY_LIST,block);
            n = (X10ClassDecl_c) n.body(n.body().addMember(md));
        }

       return n;
    }

}
