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

package x10rose.visit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import polyglot.ast.AbstractBlock_c;
import polyglot.ast.Allocation_c;
import polyglot.ast.AmbAssign_c;
import polyglot.ast.AmbExpr_c;
import polyglot.ast.AmbPrefix_c;
import polyglot.ast.AmbReceiver_c;
import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.ArrayAccess_c;
import polyglot.ast.ArrayInit_c;
import polyglot.ast.ArrayTypeNode_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary_c;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch;
import polyglot.ast.Branch_c;
import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Case_c;
import polyglot.ast.Cast_c;
import polyglot.ast.Catch_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassLit_c;
import polyglot.ast.ClassMember;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.Instanceof_c;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.JL;
import polyglot.ast.Labeled_c;
import polyglot.ast.Lit_c;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.Loop_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.NewArray_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeList_c;
import polyglot.ast.Node_c;
import polyglot.ast.NullLit_c;
import polyglot.ast.NumLit_c;
import polyglot.ast.PackageNode;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.SourceCollection_c;
import polyglot.ast.SourceFile_c;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt_c;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Term_c;
import polyglot.ast.Throw_c;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.StringUtil;
import polyglot.visit.NodeVisitor;
import x10.ast.AmbDepTypeNode_c;
import x10.ast.AnnotationNode;
import x10.ast.AnnotationNode_c;
import x10.ast.AssignPropertyCall_c;
import x10.ast.Async_c;
import x10.ast.AtEach_c;
import x10.ast.AtExpr_c;
import x10.ast.AtHomeExpr_c;
import x10.ast.AtHomeStmt_c;
import x10.ast.AtStmt_c;
import x10.ast.Atomic_c;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.DepParameterExpr_c;
import x10.ast.FinishExpr_c;
import x10.ast.Finish_c;
import x10.ast.ForLoop_c;
import x10.ast.FunctionTypeNode_c;
import x10.ast.HasZeroTest_c;
import x10.ast.Here_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.Next_c;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl;
import x10.ast.PropertyDecl_c;
import x10.ast.SettableAssign_c;
import x10.ast.StmtExpr_c;
import x10.ast.StmtSeq_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.TypeParamNode;
import x10.ast.UnknownTypeNode_c;
import x10.ast.When_c;
import x10.ast.X10Binary_c;
import x10.ast.X10BooleanLit_c;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast_c;
import x10.ast.X10CharLit_c;
import x10.ast.X10ClassBody_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ClockedLoop_c;
import x10.ast.X10Conditional_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10Do_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10FloatLit_c;
import x10.ast.X10Formal_c;
import x10.ast.X10If_c;
import x10.ast.X10Instanceof_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10Loop_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10Special_c;
import x10.ast.X10StringLit_c;
import x10.ast.X10Unary_c;
import x10.ast.X10While_c;
import x10.extension.X10Ext;

public class LibraryVisitor extends NodeVisitor {
    private List<Import> imports;
    private List<String> package_ref;
    private String package_name;
    private String class_name;
    // private FileSource source;
    private boolean isClassMatched;
    private boolean isPackageMatched = true; // so far, set true;
    private int memberIndex;
    private Node_c node;
    private Job currentJob;
    
    private static HashMap<Operator, Integer> opTable = new HashMap<Operator, Integer>();

    public LibraryVisitor(String pack, String clazz, SourceFile_c src, Job job) {
        if (RoseTranslator.DEBUG) System.out.println("package=" + pack + ", class=" + clazz + ", source=" + src);
        package_name = pack;
        RoseTranslator.package_traversed.add(package_name);
        class_name = RoseTranslator.trimTypeParameterClause(clazz); // Rail is not supposed to be here
        // source = src;
        currentJob = job;
        package_ref = new ArrayList<String>();
        package_ref.add("x10.lang"); // auto-import
        package_ref.add("x10.io");
        if (!package_ref.contains(pack))
            package_ref.add(pack);
        imports = src.imports();
    }

    public void visitDeclarations() {
    }

    public void visitDefinitions() {
    }

    public boolean addFileIndex() {
        return false;
    }

    public void subFileIndex() {
    }

    private void handleAmbType(AmbTypeNode_c amb) {
        handleAmbType(amb, null, -1);
    }

    private void handleAmbType(AmbTypeNode_c amb, String[] output, int index) {
        String type_ = amb.name().toString();
        String ambTypeName = amb.toString().replaceAll("\\{amb\\}", "");
        ambTypeName = RoseTranslator.trimTypeParameterClause(ambTypeName);
        if (RoseTranslator.DEBUG) System.out.println("handleAmbType: " + amb + ", ambTypeName: " + ambTypeName);

        if (RoseTranslator.isX10Primitive(ambTypeName)) {
            JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken());
            return;
        }

        boolean isRailType = false;
        if (   ambTypeName.indexOf("Rail[") >= 0 
            || ambTypeName.indexOf("GrowableRail[") >= 0) {
            String class_name = ambTypeName.substring(ambTypeName.indexOf('[') + 1, ambTypeName.indexOf(']'));
            int lastDot = class_name.lastIndexOf(".");
            String package_ = lastDot > 0 ? class_name.substring(0, lastDot) : "";
            String type = lastDot > 0 ? class_name.substring(lastDot + 1) : class_name;
            if (RoseTranslator.isX10Primitive(type)) {
                JNI.cactionTypeReference("", type, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                return;
            }

            if (package_.length() != 0) {
                JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                JNI.cactionPopPackage();
                JNI.cactionTypeReference(package_, type, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
            } else {
                // so far, do nothing
                isRailType = true;
                type_ = type;
            }
        } else if (amb.prefix() != null) { 
            // package is NOT null
            // Need to check whether amd.prefix() returns a package name
            // before invoking cactionTypeReference() because
            // it is possible that amb.prefix() returns a class name when
            // amb is a nested class.
            //
            // JNI.cactionTypeReference(amb.prefix().toString(),
            // amb.name().toString(), this, RoseTranslator.createJavaToken(amb,
            // amb.toString()));
            // return;            
            type_ = ambTypeName;
        } 

        try {
            TypeSystem ts = currentJob.extensionInfo().typeSystem();
            boolean isFoundInPackageRef = false;
            if (RoseTranslator.DEBUG) System.out.println("package_ref_size=" + package_ref.size());
            for (String package_ : package_ref) {
                if (RoseTranslator.DEBUG) System.out.println("package_ref=" + package_);
                List<Type> list = new ArrayList<Type>();
                try {
                    list = ts.systemResolver().find(QName.make(package_ + "." + type_));
                } catch (polyglot.types.NoClassException e) {
                    if (RoseTranslator.DEBUG) System.out.println(package_ + "." + type_ + " not found: " + e);
                }
                if (list.size() != 0) {
                    Type t = list.get(0);
                    // String type_ = amb.name().toString();
                    JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                    JNI.cactionPopPackage();
                    JNI.cactionTypeReference(package_, type_, this, RoseTranslator.createJavaToken(amb, amb.toString()));
                    isFoundInPackageRef = true;
                    if (output != null)
                        output[index] = package_ + "." + type_;
                    if (isRailType)
                        JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                    break;
                }
            }
            if (isFoundInPackageRef)
                return;

            if (RoseTranslator.DEBUG) System.out.println("import_size=" + imports.size());
            for (Import import_ : imports) {
                if (import_.kind() == Import.CLASS) {
                    String importedClass = import_.name().toString();
                    if (RoseTranslator.DEBUG) System.out.println("Imported=" + importedClass);
                    int lastDot = importedClass.lastIndexOf('.');
                    String package_ = importedClass.substring(0, lastDot);
                    String type2_ = importedClass.substring(lastDot + 1);
                    if (RoseTranslator.DEBUG) System.out.println("importName=" + import_.name() + ", type2=" + type2_);
                    if (type_.equals(type2_)) {
                        JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                        JNI.cactionPopPackage();
                        JNI.cactionTypeReference(package_, type2_, this, RoseTranslator.createJavaToken(amb, amb.toString()));
                        isFoundInPackageRef = true;
                        if (output != null)
                            output[index] = package_ + "." + type_;
                        if (isRailType)
                            JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                        break;
                    }
                } else if (import_.kind() == Import.PACKAGE) {
                    String package_ = import_.name().toString();
                    List<Type> list = new ArrayList<Type>();
                    try {
                        list = ts.systemResolver().find(QName.make(package_ + "." + type_));
                    } catch (polyglot.types.NoClassException e) {
                        if (RoseTranslator.DEBUG) System.out.println(package_ + "." + type_ + " not found: " + e);
                    }
                    if (list.size() != 0) {
                        Type t = list.get(0);
                        // String type_ = amb.name().toString();
                        JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                        JNI.cactionPopPackage();
                        JNI.cactionTypeReference(package_, type_, this, RoseTranslator.createJavaToken(amb, amb.toString()));
                        isFoundInPackageRef = true;
                        if (output != null)
                            output[index] = package_ + "." + type_;
                        if (isRailType)
                            JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                        break;
                    }
                }
            }

            if (!isFoundInPackageRef)
                // treat as a generic type
                if (isRailType) {
                    JNI.cactionTypeReference("", type_, this, RoseTranslator.createJavaToken());
                    JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                } else {
                    if (output != null)
                        output[index] = amb.name().toString();
                    JNI.cactionTypeReference("", amb.name().toString(), this, RoseTranslator.createJavaToken(amb, amb.toString()));
                }
        } catch (SemanticException e) {
            // treat as a generic type
            if (isRailType) {
                JNI.cactionTypeReference("", type_, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
            } else
                JNI.cactionTypeReference("", amb.name().toString(), this, RoseTranslator.createJavaToken(amb, amb.toString()));
        }
        if (RoseTranslator.DEBUG) System.out.println("handleAmbType end");
    }

    private void handleAmbType(AmbDepTypeNode_c amb) {
        handleAmbType(amb, null, -1);
    }

    private void handleAmbType(AmbDepTypeNode_c amb, String[] output, int index) {
        String ambTypeName = amb.toString().replaceAll("\\{amb\\}", "");
        ambTypeName = RoseTranslator.trimTypeParameterClause(ambTypeName);
        String type_ = ambTypeName;
        if (RoseTranslator.DEBUG)
            System.out.println("handleAmbDepType: " + amb + ", ambTypeName: " + ambTypeName);

        if (RoseTranslator.isX10Primitive(ambTypeName)) {
            JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken());
            return;
        }

        boolean isRailType = false;
        if (RoseTranslator.isX10Primitive(ambTypeName)) {
            JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken());
            return;
        } else if (ambTypeName.indexOf("Rail[") >= 0 || ambTypeName.indexOf("GrowableRail[") >= 0) {
            String class_name = ambTypeName.substring(ambTypeName.indexOf('[') + 1, ambTypeName.indexOf(']'));
            int lastDot = class_name.lastIndexOf(".");
            String package_ = lastDot > 0 ? class_name.substring(0, lastDot) : "";
            String type = lastDot > 0 ? class_name.substring(lastDot + 1) : class_name;
            if (RoseTranslator.isX10Primitive(type)) {
                JNI.cactionTypeReference("", type, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                return;
            }

            if (package_.length() != 0) {
                JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                JNI.cactionPopPackage();
                JNI.cactionTypeReference(package_, type, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
            } else {
                // so far, do nothing
                isRailType = true;
                type_ = type;
            }
        }
        // 08/19/2014 simply commented out because AmbDepTypeNode_c has no
        // method named prefix()
        // else if (amb.prefix() != null) { // package is NOT null
        // // Need to check whether amd.prefix() returns a package name
        // before invoking cactionTypeReference() because
        // // it is possible that amb.prefix() returns a class name when amb
        // is a nested class.
        // // JNI.cactionTypeReference(amb.prefix().toString(),
        // amb.name().toString(), this, RoseTranslator.createJavaToken(amb,
        // amb.toString()));
        // // return;
        // }

        try {
            TypeSystem ts = currentJob.extensionInfo().typeSystem();
            boolean isFoundInPackageRef = false;
            if (RoseTranslator.DEBUG)
                System.out.println("AMBDEPTYPE package size=" + package_ref.size() + " for type=" + ambTypeName);
            for (String package_ : package_ref) {
                if (RoseTranslator.DEBUG)
                    System.out.println("AMBDEPTYPE class=" + package_ + "." + type_);
                List<Type> list = new ArrayList<Type>();
                try {
                    list = ts.systemResolver().find(QName.make(package_ + "." + type_));
                } catch (polyglot.types.NoClassException e) {
                    if (RoseTranslator.DEBUG)
                        System.out.println(package_ + "." + type_ + " not found: " + e);
                    continue;
                }
                if (RoseTranslator.DEBUG)
                    System.out.println("AMBDEPTYPE list size=" + list.size());
                if (list.size() != 0) {
                    Type t = list.get(0);
                    if (RoseTranslator.DEBUG)
                        System.out.println("found type=" + t);
                    // String type_ = amb.name().toString();
                    JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                    JNI.cactionPopPackage();
                    JNI.cactionTypeReference(package_, type_, this, RoseTranslator.createJavaToken(amb, amb.toString()));
                    isFoundInPackageRef = true;
                    if (output != null)
                        output[index] = package_ + "." + type_;
                    if (isRailType)
                        JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                    break;
                }
            }
            if (isFoundInPackageRef)
                return;

            for (Import import_ : imports) {
                if (import_.kind() == Import.CLASS) {
                    String importedClass = import_.name().toString();
                    int lastDot = importedClass.lastIndexOf('.');
                    String package_ = importedClass.substring(0, lastDot);
                    String type2_ = importedClass.substring(lastDot + 1);
                    if (import_.name().toString().equals(type2_)) {
                        JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                        JNI.cactionPopPackage();
                        JNI.cactionTypeReference(package_, type2_, this, RoseTranslator.createJavaToken(amb, amb.toString()));
                        isFoundInPackageRef = true;
                        if (output != null)
                            output[index] = package_ + "." + type_;
                        if (isRailType)
                            JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                        break;
                    }
                } else if (import_.kind() == Import.PACKAGE) {
                    String package_ = import_.name().toString();
                    List<Type> list = new ArrayList<Type>();
                    try {
                        list = ts.systemResolver().find(QName.make(package_ + "." + type_));
                    } catch (polyglot.types.NoClassException e) {
                        if (RoseTranslator.DEBUG)
                            System.out.println(package_ + "." + type_ + " not found: " + e);
                        continue;
                    }
                    if (list.size() != 0) {
                        Type t = list.get(0);
                        // String type_ = amb.name().toString();
                        JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(amb, amb.toString()));
                        JNI.cactionPopPackage();
                        JNI.cactionTypeReference(package_, type_, this, RoseTranslator.createJavaToken(amb, amb.toString()));
                        isFoundInPackageRef = true;
                        if (output != null)
                            output[index] = package_ + "." + type_;
                        if (isRailType)
                            JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                        break;
                    }
                }
            }

            if (!isFoundInPackageRef) {
                // treat as a generic type
                if (isRailType) {
                    JNI.cactionTypeReference("", type_, this, RoseTranslator.createJavaToken());
                    JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                }
                JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken(amb, amb.toString()));
            }
        } catch (SemanticException e) {
            // treat as a generic type
            if (isRailType) {
                JNI.cactionTypeReference("", type_, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
            }
            JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken(amb, amb.toString()));
        }
    }

    public int handleClassMembers(List<ClassMember> members, String package_name, String type_name) throws SemanticException {
        int final_member_size = members.size();
        for (int i = 0; i < members.size(); ++i) {
            JL m = members.get(i);
            if (m instanceof X10MethodDecl_c) {
                X10MethodDecl_c methodDecl = (X10MethodDecl_c) m;
                // TODO: remove this condition when parsing closure is supported 
                if (RoseTranslator.hasFunctionType(methodDecl)) {
                    --final_member_size;
                    continue;
                }
                StringBuffer param = new StringBuffer();
                for (Formal f : methodDecl.formals()) {
                    param.append(f.type().toString().toLowerCase());
                }
                RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + methodDecl.name().toString() + "(" + param + ")", RoseTranslator.uniqMemberIndex++);
                previsit(methodDecl, type_name);
            } else if (m instanceof X10ConstructorDecl_c) {
                X10ConstructorDecl_c constructorDecl = (X10ConstructorDecl_c) m;
                // TODO: remove this condition when parsing closure is supported 
                if (RoseTranslator.hasFunctionType(constructorDecl)) {
                    --final_member_size;
                    continue;
                }
                StringBuffer param = new StringBuffer();
                for (Formal f : constructorDecl.formals()) {
                    param.append(f.type().toString().toLowerCase());
                }      
                RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + ((X10ConstructorDecl_c) m).name().toString() + "(" + param + ")", RoseTranslator.uniqMemberIndex++);
                previsit(constructorDecl, package_name, type_name);
            } else if (m instanceof X10FieldDecl_c) {
                X10FieldDecl_c fieldDecl = (X10FieldDecl_c) m;
                // TODO: remove this condition when parsing closure is supported 
                if (RoseTranslator.hasFunctionType(fieldDecl)) {                    
                    --final_member_size;
                    continue;  
                }
                RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + ((X10FieldDecl_c) m).name().toString(), RoseTranslator.uniqMemberIndex++);
                previsit(fieldDecl, type_name);
            } else if (m instanceof TypeNode_c) {
                if (RoseTranslator.DEBUG)
                    System.out.println("TypeNode_c : " + m);
            } else if (m instanceof TypeDecl_c) {
                if (RoseTranslator.DEBUG)
                    System.out.println("TypeDecl_c : " + m);
            } else if (m instanceof X10ClassDecl_c) {
                if (RoseTranslator.DEBUG)
                    System.out.println("X10ClassDecl_c : " + m);
                --final_member_size;
//                visitDeclarationsOnly((X10ClassDecl_c) m, (package_name == "") ? type_name : package_name + "." + type_name);
            } else if (m instanceof ClassDecl_c) {
                if (RoseTranslator.DEBUG)
                    System.out.println("ClassDecl_c : " + m);
            } else {
                if (RoseTranslator.DEBUG)
                    System.out.println("Unhandled node : " + m);
            }
        }
        return final_member_size;
    }

    public void visit(FunctionTypeNode_c n) {
        toRose(n, "FunctionTypeNode_c in TypeVisitor:", n.toString());

    }

    public void visitDeclarations(X10ClassDecl_c n) throws SemanticException {
        visitDeclarations(n, null);
    }
    
    public void visitDeclarationsOnly(X10ClassDecl_c n, String parent) throws SemanticException {
        // public void visitDeclarations(Term_c n) {
        toRose(n, "X10ClassDecl_c visitDeclarationsOnly in TypeVisitor:", n.toString());
        X10ClassDecl_c decl = (X10ClassDecl_c) n;
        Flags flags = decl.flags().flags();
        List<TypeParamNode> typeParamList = decl.typeParameters();
        TypeNode superClass = decl.superClass();
        List<TypeNode> interfaces = decl.interfaces();
        if (RoseTranslator.DEBUG) System.out.println("PACKAGE_NAME=" + package_name);
        SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);

        JNI.cactionSetCurrentClassName(((package_name.length() == 0)? "" : package_name + ".") + class_name);
//        RoseTranslator.classMemberMap.put(((package_name.length() == 0)? "" : package_name + ".") + class_name, RoseTranslator.memberMap);

        if (package_name.length() != 0)
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, class_name));
        JNI.cactionInsertClassStart(class_name, false, false, false, flags.isStruct(), RoseTranslator.createJavaToken(n, class_name));
        // does not consider nested class so far
        JNI.cactionInsertClassEnd(class_name, RoseTranslator.createJavaToken(n, class_name));

        List<ClassMember> members = ((X10ClassBody_c) ((X10ClassDecl_c) n).body()).members();

        String[] typeParamNames = new String[typeParamList.size()];
        for (int i = 0; i < typeParamList.size(); ++i) {
            String typeParam = typeParamList.get(i).name().toString();
            typeParamNames[i] = typeParam;
            // typeParamNames[i] = package_name + "." + class_name + "." +
            // typeParam;
            JNI.cactionSetCurrentClassName(typeParam);
            // JNI.cactionSetCurrentClassName(package_name + "." +
            // class_name + "." + typeParam);
            JNI.cactionInsertClassStart(typeParam, false, false, false, false, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertClassEnd(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            // JNI.cactionPushTypeParameterScope("", typeParam,
            // createJavaToken(n, typeParam));
            JNI.cactionPushTypeParameterScope(package_name, class_name, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertTypeParameter(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionBuildTypeParameterSupport(package_name, class_name, -1, typeParam, 0, RoseTranslator.createJavaToken(n, typeParam));
        }
        if (typeParamList.size() > 0)
            JNI.cactionSetCurrentClassName(((package_name.length() == 0)? "" : package_name + ".") + class_name);

        JNI.cactionBuildClassSupportStart(class_name, "", true, 
                false, false, false, false, RoseTranslator.createJavaToken(n, class_name));

        // handling of a super class and interfaces
        String superClassName = "";
        if (superClass != null) {
            if (superClass instanceof AmbTypeNode_c) {
                handleAmbType((AmbTypeNode_c) superClass);
                superClassName = ((AmbTypeNode_c) superClass).name().toString();
                // superClassName =
                // currently, not handle the function type
                // TODO: handle function type
            } else if (superClass instanceof AmbDepTypeNode_c) {
                handleAmbType((AmbDepTypeNode_c) superClass);
                superClassName = ((AmbDepTypeNode_c) superClass).toString();
                if (RoseTranslator.DEBUG) System.out.println("SUEPRCLASSNAME=" + superClassName);
            } else if (superClass instanceof FunctionTypeNode_c) {
                superClass = null;
            } else
                visit((X10CanonicalTypeNode_c) superClass);
        }

        String[] interfaceNames = new String[interfaces.size()];
        for (int i = 0; i < interfaces.size(); ++i) {
            TypeNode intface = interfaces.get(i);
            if (RoseTranslator.DEBUG) System.out.println("Interface=" + intface);
            if (intface instanceof AmbTypeNode_c) {
                System.out.println("111");
                handleAmbType((AmbTypeNode_c) intface, interfaceNames, i);
            }
            // currently, not handle the function type
            // for simplicity, just ignore interfaces
            // TODO: handle function type
            else if (intface instanceof FunctionTypeNode_c) {
                FunctionTypeNode_c funcType = (FunctionTypeNode_c) intface;
                // Currently, use only return type
                TypeNode node = funcType.returnType();
                if (node instanceof AmbTypeNode_c) {
                    handleAmbType((AmbTypeNode_c) node, interfaceNames, i);
                }
                else {
                    interfaceNames[i] = RoseTranslator.trimTypeParameterClause(node.toString());
                }
            } else {
                interfaceNames[i] = RoseTranslator.trimTypeParameterClause(intface.toString());
                if (RoseTranslator.DEBUG) System.out.println("name["+i+"]=" + interfaceNames[i] + ", " + intface.getClass());
                // visit(intface);
                visit(intface);
            }
            if (RoseTranslator.DEBUG) System.out.println("name["+i+"]=" + interfaceNames[i]);
        }
        JNI.cactionBuildClassExtendsAndImplementsSupport(typeParamList.size(), typeParamNames, superClass != null, superClassName, interfaces == null ? 0 : interfaces.size(), interfaceNames, RoseTranslator.createJavaToken(n, n.toString()));
        
        JNI.cactionBuildClassSupportEnd(class_name, members.size(), RoseTranslator.createJavaToken(n, class_name));
        if (package_name.length() != 0) {
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, class_name));
            JNI.cactionPopPackage();
        }
        // JNI.cactionPopTypeScope();

        // TODO: eliminate if-satement after removing the appearance of
        // ambiguous typesPlaceLocalHandle

        // TODO: enum and interface type
        JNI.cactionTypeDeclaration(package_name, class_name, 0, decl.superClass() != null, /* is_annotation_interface */false, flags.isInterface(),
        /* is_enum */false, flags.isAbstract(), flags.isFinal(), flags.isPrivate(), flags.isPublic(), flags.isProtected(), flags.isStatic(), /* is_strictfp */false, RoseTranslator.createJavaToken(n, class_name));

        List<PropertyDecl> propList = n.properties();
        for (PropertyDecl prop : propList) {
            visitChild(prop, prop.type());
            boolean isRail = false;
            TypeNode type = prop.type();
            if (type instanceof AmbTypeNode_c) {
                String ambTypeName = prop.toString().replaceAll("\\{amb\\}", "");
                /**
                 * Currently, eliminate GlobalRail
                 */
                isRail = (ambTypeName.indexOf("Rail[") >= 0 || ambTypeName.indexOf("GrowableRail[") >= 0)
                        && ambTypeName.indexOf("GlobalRef") < 0;
            }
            else 
                isRail = type.type().isRail();
            
            JNI.cactionAppendProperty(prop.name().id().toString(), isRail, 
                                        prop.flags().flags().isFinal(), RoseTranslator.createJavaToken());
        }
        if (propList.size() > 0)
            JNI.cactionSetProperties(propList.size(), RoseTranslator.createJavaToken());
    }


    public void visitDeclarations(X10ClassDecl_c n, String parent) throws SemanticException {
        // public void visitDeclarations(Term_c n) {
        toRose(n, "X10ClassDecl_c visitDeclarations in TypeVisitor:", n.toString());
        X10ClassDecl_c decl = (X10ClassDecl_c) n;
        Flags flags = decl.flags().flags();
        List<TypeParamNode> typeParamList = decl.typeParameters();
        TypeNode superClass = decl.superClass();
        List<TypeNode> interfaces = decl.interfaces();
        if (RoseTranslator.DEBUG) System.out.println("PACKAGE_NAME=" + package_name);
        SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);

        JNI.cactionSetCurrentClassName(((package_name.length() == 0)? "" : package_name + ".") + class_name);
//        RoseTranslator.classMemberMap.put(((package_name.length() == 0)? "" : package_name + ".") + class_name, RoseTranslator.memberMap);

        if (package_name.length() != 0)
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, class_name));
        JNI.cactionInsertClassStart(class_name, false, false, false, flags.isStruct(), RoseTranslator.createJavaToken(n, class_name));
        // does not consider nested class so far
        JNI.cactionInsertClassEnd(class_name, RoseTranslator.createJavaToken(n, class_name));

        List<ClassMember> members = ((X10ClassBody_c) ((X10ClassDecl_c) n).body()).members();

        String[] typeParamNames = new String[typeParamList.size()];
        for (int i = 0; i < typeParamList.size(); ++i) {
            String typeParam = typeParamList.get(i).name().toString();
            typeParamNames[i] = typeParam;
            // typeParamNames[i] = package_name + "." + class_name + "." +
            // typeParam;
            JNI.cactionSetCurrentClassName(typeParam);
            // JNI.cactionSetCurrentClassName(package_name + "." +
            // class_name + "." + typeParam);
            JNI.cactionInsertClassStart(typeParam, false, false, false, false, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertClassEnd(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            // JNI.cactionPushTypeParameterScope("", typeParam,
            // createJavaToken(n, typeParam));
            JNI.cactionPushTypeParameterScope(package_name, class_name, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertTypeParameter(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionBuildTypeParameterSupport(package_name, class_name, -1, typeParam, 0, RoseTranslator.createJavaToken(n, typeParam));
        }
        if (typeParamList.size() > 0)
            JNI.cactionSetCurrentClassName(((package_name.length() == 0)? "" : package_name + ".") + class_name);

        JNI.cactionBuildClassSupportStart(class_name, "", true, 
                false, false, false, false, RoseTranslator.createJavaToken(n, class_name));

        // handling of a super class and interfaces
        String superClassName = "";
        if (superClass != null) {
            if (superClass instanceof AmbTypeNode_c) {
                handleAmbType((AmbTypeNode_c) superClass);
                superClassName = ((AmbTypeNode_c) superClass).name().toString();
                // superClassName =
                // currently, not handle the function type
                // TODO: handle function type
            } else if (superClass instanceof AmbDepTypeNode_c) {
                handleAmbType((AmbDepTypeNode_c) superClass);
                superClassName = ((AmbDepTypeNode_c) superClass).toString();
                if (RoseTranslator.DEBUG) System.out.println("SUEPRCLASSNAME=" + superClassName);
            } else if (superClass instanceof FunctionTypeNode_c) {
                superClass = null;
            } else
                visit((X10CanonicalTypeNode_c) superClass);
        }

        String[] interfaceNames = new String[interfaces.size()];
        for (int i = 0; i < interfaces.size(); ++i) {
            TypeNode intface = interfaces.get(i);
            if (RoseTranslator.DEBUG) System.out.println("Interface=" + intface);
            if (intface instanceof AmbTypeNode_c) {
                handleAmbType((AmbTypeNode_c) intface, interfaceNames, i);
            }
            // currently, not handle the function type
            // for simplicity, just ignore interfaces
            // TODO: handle function type
            else if (intface instanceof FunctionTypeNode_c) {
                FunctionTypeNode_c funcType = (FunctionTypeNode_c) intface;
                // Currently, use only return type
                TypeNode node = funcType.returnType();
                if (node instanceof AmbTypeNode_c) {
                    handleAmbType((AmbTypeNode_c) node, interfaceNames, i);
                }
                else {
                    interfaceNames[i] = RoseTranslator.trimTypeParameterClause(node.toString());
                }
            } else {
                interfaceNames[i] = RoseTranslator.trimTypeParameterClause(intface.toString());
                if (RoseTranslator.DEBUG) System.out.println("name["+i+"]=" + interfaceNames[i] + ", " + intface.getClass());
                // visit(intface);
                visit(intface);
            }
            if (RoseTranslator.DEBUG) System.out.println("name["+i+"]=" + interfaceNames[i]);
        }
        JNI.cactionBuildClassExtendsAndImplementsSupport(typeParamList.size(), typeParamNames, superClass != null, superClassName, interfaces == null ? 0 : interfaces.size(), interfaceNames, RoseTranslator.createJavaToken(n, n.toString()));
        
        int member_size = handleClassMembers(members, package_name, class_name);

      JNI.cactionBuildClassSupportEnd(class_name, member_size, RoseTranslator.createJavaToken(n, class_name));

//        JNI.cactionBuildClassSupportEnd(class_name, members.size(), RoseTranslator.createJavaToken(n, class_name));
        if (package_name.length() != 0) {
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, class_name));
            JNI.cactionPopPackage();
        }
        // JNI.cactionPopTypeScope();

        // TODO: eliminate if-satement after removing the appearance of
        // ambiguous typesPlaceLocalHandle

        // TODO: enum and interface type
        JNI.cactionTypeDeclaration(package_name, class_name, 0, decl.superClass() != null, /* is_annotation_interface */false, flags.isInterface(),
        /* is_enum */false, flags.isAbstract(), flags.isFinal(), flags.isPrivate(), flags.isPublic(), flags.isProtected(), flags.isStatic(), /* is_strictfp */false, RoseTranslator.createJavaToken(n, class_name));

        List<PropertyDecl> propList = n.properties();
        for (PropertyDecl prop : propList) {
            visitChild(prop, prop.type());
            boolean isRail = false;
            TypeNode type = prop.type();
            if (type instanceof AmbTypeNode_c) {
                String ambTypeName = prop.toString().replaceAll("\\{amb\\}", "");
                /**
                 * Currently, eliminate GlobalRail
                 */
                isRail = (ambTypeName.indexOf("Rail[") >= 0 || ambTypeName.indexOf("GrowableRail[") >= 0)
                        && ambTypeName.indexOf("GlobalRef") < 0;
            }
            else 
                isRail = type.type().isRail();
            
            JNI.cactionAppendProperty(prop.name().id().toString(), isRail, 
                                        prop.flags().flags().isFinal(), RoseTranslator.createJavaToken());
        }
        if (propList.size() > 0)
            JNI.cactionSetProperties(propList.size(), RoseTranslator.createJavaToken());
    }

    public void visitDefinitions(X10ClassDecl_c n) {
        toRose(n, "X10ClassDecl in visitDefinitions:", n.name().id().toString());
        String class_name = n.name().id().toString();

        visitChildren(n, n.typeParameters());
        visitChildren(n, n.properties());
        visitChild(n, n.classInvariant());
        visitChild(n, n.superClass());
        visitChildren(n, n.interfaces());
        visitChild(n, n.body());
        // JNI.cactionTypeDeclaration("", n.name().id().toString(), false,
        // false, false, false, false, false, false, false, false, false);

        JNI.cactionTypeDeclarationEnd(false, RoseTranslator.createJavaToken(n, class_name));

        // JNI.cactionBuildClassSupportEnd(class_name, RoseTranslator.createJavaToken());
    }

    // public void createTypeReference(Node_c n) {
    // JNI.cactionInsertClassStart(class_name, false, false, false,
    // createJavaToken(n, class_name));
    // JNI.cactionInsertClassEnd(class_name, RoseTranslator.createJavaToken(n,
    // class_name));
    // node = n;
    // JNI.cactionBuildClassSupportStart(class_name, "", true, // a
    // user-defined class?
    // false, false, false, false, RoseTranslator.createJavaToken(n, class_name));
    //
    // X10ClassDecl_c decl = (X10ClassDecl_c)n;
    //
    // List<ClassMember> members = ((X10ClassBody_c)decl.body()).members();
    // for (int i = 0; i < members.size(); ++i) {
    // JL m = members.get(i);
    // if (m instanceof X10MethodDecl_c) {
    // X10MethodDecl_c methodDecl = (X10MethodDecl_c) m;
    // StringBuffer param = new StringBuffer();
    // for (Formal f : methodDecl.formals()) {
    // param.append(f.type().toString());
    // }
    // memberMap.put(methodDecl.name().toString()+"("+param+")", i);
    // classMemberMap.put(class_name, memberMap);
    // previsit(methodDecl);
    // }
    // else if (m instanceof X10ConstructorDecl_c) {
    // X10ConstructorDecl_c constructorDecl = (X10ConstructorDecl_c) m;
    // StringBuffer param = new StringBuffer();
    // for (Formal f : constructorDecl.formals()) {
    // param.append(f.type().toString());
    // }
    // memberMap.put(((X10ConstructorDecl_c) m).name().toString() + "(" +
    // param + ")", i);
    // classMemberMap.put(class_name, memberMap);
    // previsit(constructorDecl);
    // }
    // else if (m instanceof X10FieldDecl_c) {
    // X10FieldDecl_c fieldDecl = (X10FieldDecl_c)m;
    // memberMap.put(((X10FieldDecl_c) m).name().toString(), i);
    // classMemberMap.put(class_name, memberMap);
    // previsit(fieldDecl);
    // }
    // else if (m instanceof TypeNode_c) { if (RoseTranslator.DEBUG)
    // System.out.println("TypeNode_c : " + m); }
    // else if (m instanceof TypeDecl_c) { if (RoseTranslator.DEBUG)
    // System.out.println("TypeDecl_c : " + m); }
    // else if (m instanceof X10ClassDecl_c) { if (RoseTranslator.DEBUG)
    // System.out.println("X10ClassDecl_c : " + m); }
    // else if (m instanceof ClassDecl_c) { if (RoseTranslator.DEBUG)
    // System.out.println("ClassDecl_c : " + m); }
    // else {
    // if (RoseTranslator.DEBUG) System.out.println("Unhandled node : " + m);
    // }
    // }
    // if (RoseTranslator.DEBUG)
    // System.out.println("TypeVisitor : createTypeReference for " + n);
    // }

    public void createTypeReferenceEnd() {
        JNI.cactionTypeDeclarationEnd(false, RoseTranslator.createJavaToken());
    }

    void toRose(Node n, String name, String... extra) {
        if (RoseTranslator.DEBUG) System.out.print("PRINT ");
        if (name != null)
            if (RoseTranslator.DEBUG) System.out.print(name);
        if (extra != null)
            for (String s : extra) {
                if (RoseTranslator.DEBUG) System.out.print(s + " ");
            }
        if (RoseTranslator.DEBUG) System.out.println();
    }

    void visitChild(Node p, Node n) {
        if (n == null)
            return;
        /* w ToRoseVisitor(w, p). */visitAppropriate(n);
        // new TypeVisitor(package_name, class_name,
        // null).visitAppropriate(n);
    }

    void visitChildren(Node p, List<? extends Node> l) {
        if (l == null)
            return;
        for (Node n : l)
            visitChild(p, n);
    }

    public void previsit(X10MethodDecl_c n, String class_name) {
        toRose(n, "TypeVisitor.Previsit method decl: ", n.name().id().toString());
        List<Formal> formals = n.formals();

        String method_name = n.name().id().toString();
        StringBuffer param = new StringBuffer();
        for (Formal f : n.formals()) {
            param.append(f.type().toString().toLowerCase());
        }
        
        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + method_name + "(" + param + ")");
      
        JNI.cactionBuildMethodSupportStart(method_name, method_index, RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));

        List<TypeParamNode> typeParamList = n.typeParameters();
        
        // in case the return type is unknown. Such a case will occur by
        // writing
        // like"public def toString() = "Place(" + this.id + ")";"
        // comment out parsing return type
//        if (n.returnType() instanceof UnknownTypeNode_c)
            JNI.cactionTypeReference("", "void", this, RoseTranslator.createJavaToken());
//        else
//            visitChild(n, n.returnType());
        
        visitChildren(n, n.formals());

        JNI.cactionBuildMethodSupportEnd(method_name, method_index, 
                false, false, false, 0, formals.size(), true, 
                RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));
        if (RoseTranslator.DEBUG) toRose(n, "TypeVisitor.Previsit method decl end: ", n.name().id().toString());
    }

    public void previsit(X10ConstructorDecl_c n, String package_name, String type_name) {
        toRose(n, "TypeVisitor.Previsit constructor decl: ", n.name().id().toString());
        List<Formal> formals = n.formals();
        String method_name = n.name().id().toString();
        StringBuffer param = new StringBuffer();
        for (Formal f : n.formals()) {
            param.append(f.type().toString().toLowerCase());
        }
        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + method_name + "(" + param + ")");

        List<TypeParamNode> typeParamList = n.typeParameters();
        for (int i = 0; i < typeParamList.size(); ++i) {
            String typeParam = typeParamList.get(i).name().toString();
            typeParam = typeParam + "_" + method_index + "_" + package_name + "_" + class_name;
            JNI.cactionPushTypeParameterScope(package_name, type_name, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertTypeParameter(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionBuildTypeParameterSupport(package_name, type_name, method_index, typeParam, 0, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionPopTypeParameterScope(RoseTranslator.createJavaToken(n, typeParam));
        }

        JNI.cactionBuildMethodSupportStart(method_name, method_index, RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));
        if (n.returnType() == null)
            JNI.cactionTypeReference(package_name, type_name, this, RoseTranslator.createJavaToken(n, n.toString()));
        else
            visitChild(n, n.returnType());
        // String raw = n.returnType().node().toString();
        // String ret = raw.substring(0, raw.indexOf("{amb}"));
        // if (RoseTranslator.DEBUG) System.out.println("returnType=" + n.returnType() +
        // " returnType2 = " + ret + ", formals.size=" +
        // n.formals().size());
        // JNI.cactionTypeReference("", ret, this, RoseTranslator.createJavaToken(n, ret));
        visitChildren(n, n.formals());

        // for (int i = 0; i < formals.size(); ++i) {
        // Formal f = formals.get(i);
        // String type = f.type().toString();
        // if (RoseTranslator.DEBUG) System.out.println("f.type().getClass()=>" +
        // f.type().getClass().toString());
        // if (f.type() instanceof AmbTypeNode_c) {
        // if (RoseTranslator.DEBUG) System.out.println(f.type().toString() +
        // " => AmbTypeNode_c");
        // }
        // type = type.substring(0, type.indexOf("{"));
        // if (RoseTranslator.DEBUG) System.out.println(i + " : " + type);
        // JNI.cactionTypeReference("", type, this, RoseTranslator.createJavaToken(n,
        // type));
        // // visit(f);
        // JNI.cactionBuildArgumentSupport(f.name().toString(), false,
        // false, RoseTranslator.createJavaToken(n, f.name().toString()));
        // }

        JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method
                                                                    // index
                false, false, false, 0, formals.size(), true, /*
                                                               * user-defined
                                                               * -method
                                                               */
                RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));
        // visitChild(n, n.guard());
        // visitChild(n, n.offerType());
        // visitChildren(n, n.throwsTypes());
    }

    public void previsit(X10FieldDecl_c fieldDecl, String class_name) {
        String fieldName = fieldDecl.name().id().toString();
        toRose(fieldDecl, "TypeVisitor.Previsit field decl: ", fieldName);

        TypeNode type = fieldDecl.type();
        if (type instanceof AmbTypeNode_c)
            handleAmbType((AmbTypeNode_c) type);
        else if (type instanceof AmbDepTypeNode_c)
            visit((AmbDepTypeNode_c) type);
        else if (type instanceof UnknownTypeNode_c) {
            // TODO: should look for the type
            //
            // An example of unknown type is Printer.lock:
            // public class Printer extends FilterWriter {
            // private lock = new Lock();
            // :
            // }
            JNI.cactionTypeReference("", "Unknown", this, RoseTranslator.createJavaToken());
        } else {
            String package_name = fieldDecl.type().type().fullName().qualifier().toString();
            if (package_name.length() != 0) {
                JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(fieldDecl, package_name));
                JNI.cactionPopPackage();
            }
            JNI.cactionTypeReference(package_name, fieldDecl.type().nameString(), this, RoseTranslator.createJavaToken());
        }
        JNI.cactionBuildFieldSupport(fieldName, RoseTranslator.createJavaToken());

        // boolean hasInitializer = (fieldDecl.init() != null);
        //
        // if (hasInitializer) {
        // visitChild(fieldDecl, fieldDecl.init());
        // // int field_index = memberMap.get(fieldName);
        // //
        // // JNI.cactionBuildInitializerSupport(flags.isStatic(),
        // fieldName,
        // // field_index,
        // // createJavaToken());
        // }

        // MH-20140401
        // needs to invoke cactionTypeReference again, since
        // cactionFieldDeclarationEnd
        // first pop SgType for "!is_enum_field"
        // JNI.cactionTypeReference("", fieldDecl.type().nameString());

        Flags flags = fieldDecl.flags().flags();

        JNI.cactionFieldDeclarationEnd(fieldName, false, // is_enum_field
                false, flags.isFinal(), flags.isPrivate(), flags.isProtected(), flags.isPublic(), false, // java_is_volatile
                false, // java_is_synthetic
                flags.isStatic(), flags.isTransient(), RoseTranslator.createJavaToken());

        toRose(fieldDecl, "TypeVisitor.Previsit field decl end: ", fieldName);
    }

    boolean isClassFound = false;

    public NodeVisitor enter(Node n) {
        // if (RoseTranslator.DEBUG) System.out.println("ENTER:" + n);
        if (isClassFound)
            return this;

        try {
            if (n instanceof SourceFile_c) {
                String file_name = ((SourceFile_c) n).node().toString();
                List<TopLevelDecl> decls = ((SourceFile_c) n).decls();
                // if (RoseTranslator.DEBUG) System.out.println("SRC2=" + file_name +
                // ", decls2=" + decls.size() + ", node=" + n.hashCode());
                // if (file_name.indexOf(package_name.replaceAll("\\.",
                // "/")) < 0) {
                // }
            }

            if (n instanceof X10ClassDecl_c) {
                String type = ((X10ClassDecl_c) n).name().toString();
                if (type.equals(class_name) || type.toLowerCase().equals(class_name)) {
                    if (RoseTranslator.DEBUG)
                        System.out.println("TYPE=" + type + ", class_name=" + class_name + ", package_name=" + package_name);
                    isClassMatched = true;
                    if (isPackageMatched) {
                        visitDeclarations((X10ClassDecl_c) n);
                        // visitDefinitions((X10ClassDecl_c)n); // so far,
                        // only declarations should be resolved
                        isClassFound = true;
                    }
                }
            } else if (n instanceof PackageNode) {
                String pkg = n.toString();
                if (pkg.equals(package_name)) {
                    isPackageMatched = true;
                    if (isClassMatched) {
                        visitDeclarations((X10ClassDecl_c) n);
                        // visitDefinitions((X10ClassDecl_c)n); // so far,
                        // only declarations should be resolved
                        isClassFound = true;
                    }
                }
            }
        } catch (SemanticException e) {
            throw new RuntimeException("SemanticException thrown in traversing " + n);
        }

        return this;
    }

    public boolean isFound() {
        return isPackageMatched & isClassMatched;
    }

    /**
     * Invoke the appropriate visit method for a given dynamic type. Note
     * that the order of invocation of the various visit() methods is
     * significant!
     */
    public void visitAppropriate(JL n) {
        if (n instanceof Id_c) {
            visit((Id_c) n);
            return;
        }
        if (n instanceof NodeList_c) {
            visit((NodeList_c) n);
            return;
        }
        if (n instanceof AnnotationNode_c) {
            visit((AnnotationNode_c) n);
            return;
        }
        if (n instanceof X10CanonicalTypeNode_c) {
            visit((X10CanonicalTypeNode_c) n);
            return;
        }
        if (n instanceof CanonicalTypeNode_c) {
            visit((CanonicalTypeNode_c) n);
            return;
        }
        if (n instanceof ArrayTypeNode_c) {
            visit((ArrayTypeNode_c) n);
            return;
        }
        if (n instanceof AmbDepTypeNode_c) {
            visit((AmbDepTypeNode_c) n);
            return;
        }
        if (n instanceof AmbTypeNode_c) {
            visit((AmbTypeNode_c) n);
            return;
        }
        if (n instanceof TypeNode_c) {
            visit((TypeNode_c) n);
            return;
        }
        if (n instanceof TypeDecl_c) {
            visit((TypeDecl_c) n);
            return;
        }
        if (n instanceof AtEach_c) {
            visit((AtEach_c) n);
            return;
        }
        if (n instanceof X10ClockedLoop_c) {
            visit((X10ClockedLoop_c) n);
            return;
        }
        if (n instanceof ForLoop_c) {
            visit((ForLoop_c) n);
            return;
        }
        if (n instanceof X10Loop_c) {
            visit((X10Loop_c) n);
            return;
        }
        if (n instanceof When_c) {
            visit((When_c) n);
            return;
        }
        if (n instanceof Try_c) {
            visit((Try_c) n);
            return;
        }
        if (n instanceof Throw_c) {
            visit((Throw_c) n);
            return;
        }
        if (n instanceof Switch_c) {
            visit((Switch_c) n);
            return;
        }
        if (n instanceof Return_c) {
            visit((Return_c) n);
            return;
        }
        if (n instanceof Next_c) {
            visit((Next_c) n);
            return;
        }
        if (n instanceof X10While_c) {
            visit((X10While_c) n);
            return;
        }
        if (n instanceof While_c) {
            visit((While_c) n);
            return;
        }
        if (n instanceof For_c) {
            visit((For_c) n);
            return;
        }
        if (n instanceof X10Do_c) {
            visit((X10Do_c) n);
            return;
        }
        if (n instanceof Do_c) {
            visit((Do_c) n);
            return;
        }
        if (n instanceof Loop_c) {
            visit((Loop_c) n);
            return;
        }
        if (n instanceof LocalTypeDef_c) {
            visit((LocalTypeDef_c) n);
            return;
        }
        if (n instanceof X10LocalDecl_c) {
            visit((X10LocalDecl_c) n);
            return;
        }
        if (n instanceof LocalDecl_c) {
            visit((LocalDecl_c) n);
            return;
        }
        if (n instanceof LocalClassDecl_c) {
            visit((LocalClassDecl_c) n);
            return;
        }
        if (n instanceof Labeled_c) {
            visit((Labeled_c) n);
            return;
        }
        if (n instanceof X10If_c) {
            visit((X10If_c) n);
            return;
        }
        if (n instanceof If_c) {
            visit((If_c) n);
            return;
        }
        if (n instanceof Finish_c) {
            visit((Finish_c) n);
            return;
        }          
        if (n instanceof FinishExpr_c) {
            visit((FinishExpr_c) n);
            return;
        }          
        if (n instanceof Eval_c) {
            visit((Eval_c) n);
            return;
        }
        if (n instanceof Empty_c) {
            visit((Empty_c) n);
            return;
        }
        if (n instanceof X10ConstructorCall_c) {
            visit((X10ConstructorCall_c) n);
            return;
        }
        if (n instanceof ConstructorCall_c) {
            visit((ConstructorCall_c) n);
            return;
        }
        if (n instanceof Catch_c) {
            visit((Catch_c) n);
            return;
        }
        if (n instanceof Case_c) {
            visit((Case_c) n);
            return;
        }
        if (n instanceof Branch_c) {
            visit((Branch_c) n);
            return;
        }
        if (n instanceof Atomic_c) {
            visit((Atomic_c) n);
            return;
        }
        if (n instanceof AtHomeStmt_c) {
            visit((AtHomeStmt_c) n);
            return;
        }
        if (n instanceof AtStmt_c) {
            visit((AtStmt_c) n);
            return;
        }
        if (n instanceof Async_c) {
            visit((Async_c) n);
            return;
        }
        if (n instanceof AssignPropertyCall_c) {
            visit((AssignPropertyCall_c) n);
            return;
        }
        if (n instanceof Assert_c) {
            visit((Assert_c) n);
            return;
        }
        if (n instanceof SwitchBlock_c) {
            visit((SwitchBlock_c) n);
            return;
        }
        if (n instanceof StmtSeq_c) {
            visit((StmtSeq_c) n);
            return;
        }
        if (n instanceof Block_c) {
            visit((Block_c) n);
            return;
        }
        if (n instanceof AbstractBlock_c) {
            visit((AbstractBlock_c) n);
            return;
        }
        if (n instanceof Stmt_c) {
            visit((Stmt_c) n);
            return;
        }
        if (n instanceof X10MethodDecl_c) {
            visit((X10MethodDecl_c) n);
            return;
        }
        if (n instanceof MethodDecl_c) {
            visit((MethodDecl_c) n);
            return;
        }
        if (n instanceof Initializer_c) {
            visit((Initializer_c) n);
            return;
        }
        if (n instanceof X10Formal_c) {
            visit((X10Formal_c) n);
            return;
        }
        if (n instanceof Formal_c) {
            visit((Formal_c) n);
            return;
        }
        if (n instanceof PropertyDecl_c) {
            visit((PropertyDecl_c) n);
            return;
        }
        if (n instanceof X10FieldDecl_c) {
            visit((X10FieldDecl_c) n);
            return;
        }
        if (n instanceof FieldDecl_c) {
            visit((FieldDecl_c) n);
            return;
        }
        if (n instanceof X10Unary_c) {
            visit((X10Unary_c) n);
            return;
        }
        if (n instanceof Unary_c) {
            visit((Unary_c) n);
            return;
        }
        if (n instanceof Tuple_c) {
            visit((Tuple_c) n);
            return;
        }
        if (n instanceof SubtypeTest_c) {
            visit((SubtypeTest_c) n);
            return;
        }
        if (n instanceof HasZeroTest_c) {
            visit((HasZeroTest_c) n);
            return;
        }
        if (n instanceof X10Special_c) {
            visit((X10Special_c) n);
            return;
        }
        if (n instanceof Special_c) {
            visit((Special_c) n);
            return;
        }
        if (n instanceof ParExpr_c) {
            visit((ParExpr_c) n);
            return;
        }
        if (n instanceof NewArray_c) {
            visit((NewArray_c) n);
            return;
        }
        if (n instanceof X10New_c) {
            visit((X10New_c) n);
            return;
        }
        if (n instanceof New_c) {
            visit((New_c) n);
            return;
        }
        if (n instanceof X10Local_c) {
            visit((X10Local_c) n);
            return;
        }
        if (n instanceof Local_c) {
            visit((Local_c) n);
            return;
        }
        if (n instanceof X10StringLit_c) {
            visit((X10StringLit_c) n);
            return;
        }
        if (n instanceof StringLit_c) {
            visit((StringLit_c) n);
            return;
        }
        if (n instanceof IntLit_c) {
            visit((IntLit_c) n);
            return;
        }
        if (n instanceof X10CharLit_c) {
            visit((X10CharLit_c) n);
            return;
        }
        if (n instanceof CharLit_c) {
            visit((CharLit_c) n);
            return;
        }
        if (n instanceof NumLit_c) {
            visit((NumLit_c) n);
            return;
        }
        if (n instanceof NullLit_c) {
            visit((NullLit_c) n);
            return;
        }
        if (n instanceof X10FloatLit_c) {
            visit((X10FloatLit_c) n);
            return;
        }
        if (n instanceof FloatLit_c) {
            visit((FloatLit_c) n);
            return;
        }
        if (n instanceof ClassLit_c) {
            visit((ClassLit_c) n);
            return;
        }
        if (n instanceof X10BooleanLit_c) {
            visit((X10BooleanLit_c) n);
            return;
        }
        if (n instanceof BooleanLit_c) {
            visit((BooleanLit_c) n);
            return;
        }
        if (n instanceof Lit_c) {
            visit((Lit_c) n);
            return;
        }
        if (n instanceof X10Instanceof_c) {
            visit((X10Instanceof_c) n);
            return;
        }
        if (n instanceof Instanceof_c) {
            visit((Instanceof_c) n);
            return;
        }
        if (n instanceof Here_c) {
            visit((Here_c) n);
            return;
        }
        if (n instanceof X10Field_c) {
            visit((X10Field_c) n);
            return;
        }
        if (n instanceof Field_c) {
            visit((Field_c) n);
            return;
        }
        if (n instanceof DepParameterExpr_c) {
            visit((DepParameterExpr_c) n);
            return;
        }
        if (n instanceof X10Conditional_c) {
            visit((X10Conditional_c) n);
            return;
        }
        if (n instanceof Conditional_c) {
            visit((Conditional_c) n);
            return;
        }
        if (n instanceof X10Cast_c) {
            visit((X10Cast_c) n);
            return;
        }
        if (n instanceof Cast_c) {
            visit((Cast_c) n);
            return;
        }
        if (n instanceof X10Call_c) {
            visit((X10Call_c) n);
            return;
        }
        if (n instanceof Call_c) {
            visit((Call_c) n);
            return;
        }
        if (n instanceof X10Binary_c) {
            visit((X10Binary_c) n);
            return;
        }
        if (n instanceof Binary_c) {
            visit((Binary_c) n);
            return;
        }
        if (n instanceof SettableAssign_c) {
            visit((SettableAssign_c) n);
            return;
        }
        if (n instanceof LocalAssign_c) {
            visit((LocalAssign_c) n);
            return;
        }
        if (n instanceof FieldAssign_c) {
            visit((FieldAssign_c) n);
            return;
        }
        if (n instanceof ArrayAccessAssign_c) {
            visit((ArrayAccessAssign_c) n);
            return;
        }
        if (n instanceof AmbAssign_c) {
            visit((AmbAssign_c) n);
            return;
        }
        if (n instanceof Assign_c) {
            visit((Assign_c) n);
            return;
        }
        if (n instanceof ArrayInit_c) {
            visit((ArrayInit_c) n);
            return;
        }
        if (n instanceof ArrayAccess_c) {
            visit((ArrayAccess_c) n);
            return;
        }
        if (n instanceof AmbExpr_c) {
            visit((AmbExpr_c) n);
            return;
        }
        if (n instanceof AtHomeExpr_c) {
            visit((AtHomeExpr_c) n);
            return;
        }
        if (n instanceof AtExpr_c) {
            visit((AtExpr_c) n);
            return;
        }
        if (n instanceof Closure_c) {
            visit((Closure_c) n);
            return;
        }
        if (n instanceof ClosureCall_c) {
            visit((ClosureCall_c) n);
            return;
        }
        if (n instanceof StmtExpr_c) {
            visit((StmtExpr_c) n);
            return;
        }
        if (n instanceof Allocation_c) {
            visit((Allocation_c) n);
            return;
        }
        if (n instanceof Expr_c) {
            visit((Expr_c) n);
            return;
        }
        if (n instanceof X10ConstructorDecl_c) {
            visit((X10ConstructorDecl_c) n);
            return;
        }
        if (n instanceof ConstructorDecl_c) {
            visit((ConstructorDecl_c) n);
            return;
        }
        if (n instanceof X10ClassDecl_c) {
            visit((X10ClassDecl_c) n);
            return;
        }
        if (n instanceof ClassDecl_c) {
            visit((ClassDecl_c) n);
            return;
        }
        if (n instanceof X10ClassBody_c) {
            visit((X10ClassBody_c) n);
            return;
        }
        if (n instanceof ClassBody_c) {
            visit((ClassBody_c) n);
            return;
        }
        if (n instanceof Term_c) {
            visit((Term_c) n);
            return;
        }
        if (n instanceof SourceFile_c) {
            visit((SourceFile_c) n);
            return;
        }
        if (n instanceof SourceCollection_c) {
            visit((SourceCollection_c) n);
            return;
        }
        if (n instanceof PackageNode_c) {
            visit((PackageNode_c) n);
            return;
        }
        if (n instanceof Import_c) {
            visit((Import_c) n);
            return;
        }
        if (n instanceof AmbReceiver_c) {
            visit((AmbReceiver_c) n);
            return;
        }
        if (n instanceof AmbPrefix_c) {
            visit((AmbPrefix_c) n);
            return;
        }
        if (n instanceof Node_c) {
            visit((Node_c) n);
            return;
        }
        if (n instanceof FunctionTypeNode_c) {
            visit((FunctionTypeNode_c) n);
            return;
        }
        if (n instanceof Node) {
            visit((Node) n);
            return;
        }
        throw new RuntimeException("No visit method defined in " + this.getClass() + " for " + n.getClass());
    }

    // ///////////////////////////////////////////////////////////////////////
    // Note that the indentation of the visit() methods below, while not
    // significant, is intended to signify the class hierarchy.
    // ///////////////////////////////////////////////////////////////////////

    public void visit(Node n) {
    }

    
    public void visit(Node_c n) {
        visit((Node) n);
    }

    public void visit(AmbPrefix_c n) {
        visit((Node_c) n);
    }

    public void visit(Import_c n) {
        visit((Node_c) n);
    }

    public void visit(PackageNode_c n) {
        visit((Node_c) n);
    }

    public void visit(SourceCollection_c n) {
        visit((Node_c) n);
    }

    // public void visit(SourceFile_c n) { visit((Node_c)n); }
    public void visit(SourceFile_c n) {
        if (RoseTranslator.DEBUG)
            System.out.println("source visited");
        visit((Node_c) n);
    }

    public void visit(Term_c n) {
        visit((Node_c) n);
    }

    public void visit(ClassBody_c n) {
        visit((Term_c) n);
    }

    public void visit(X10ClassBody_c n) {
        visit((ClassBody_c) n);
    }

    public void visit(ClassDecl_c n) {
        visit((Term_c) n);
    }

    public void visit(X10ClassDecl_c n) {
        visit((ClassDecl_c) n);
    }

    public void visit(ConstructorDecl_c n) {
        visit((Term_c) n);
    }

    public void visit(Expr_c n) {
        visit((Term_c) n);
    }

    public void visit(AmbExpr_c n) {
        visit((Expr_c) n);
    }

    public void visit(ArrayInit_c n) {
        visit((Expr_c) n);
    }

    public void visit(Assign_c n) {
        visit((Expr_c) n);
    }

    public void visit(AmbAssign_c n) {
        visit((Assign_c) n);
    }

    public void visit(ArrayAccessAssign_c n) {
        visit((Assign_c) n);
    }

    public void visit(Binary_c n) {
        visit((Expr_c) n);
    }

    public void visit(Call_c n) {
        visit((Expr_c) n);
    }

    public void visit(Cast_c n) {
        visit((Expr_c) n);
    }

    public void visit(Conditional_c n) {
        visit((Expr_c) n);
    }

    public void visit(Field_c n) {
        visit((Expr_c) n);
    }

    public void visit(Instanceof_c n) {
        visit((Expr_c) n);
    }

    public void visit(Lit_c n) {
        visit((Expr_c) n);
    }

    public void visit(BooleanLit_c n) {
        visit((Lit_c) n);
    }

    public void visit(FloatLit_c n) {
        visit((Lit_c) n);
    }

    public void visit(NumLit_c n) {
        visit((Lit_c) n);
    }

    public void visit(CharLit_c n) {
        visit((NumLit_c) n);
    }

    public void visit(StringLit_c n) {
        visit((Lit_c) n);
    }

    public void visit(Local_c n) {
        visit((Expr_c) n);
    }

    public void visit(New_c n) {
        visit((Expr_c) n);
    }

    public void visit(NewArray_c n) {
        visit((Expr_c) n);
    }

    public void visit(Special_c n) {
        visit((Expr_c) n);
    }

    public void visit(Unary_c n) {
        visit((Expr_c) n);
    }

    public void visit(FieldDecl_c n) {
        visit((Term_c) n);
    }

    public void visit(Formal_c n) {
        visit((Term_c) n);
    }

    public void visit(Initializer_c n) {
        visit((Term_c) n);
    }

    public void visit(MethodDecl_c n) {
        visit((Term_c) n);
    }

    public void visit(X10MethodDecl_c n) {
        visit((MethodDecl_c) n);
    }

    public void visit(Stmt_c n) {
        visit((Term_c) n);
    }

    public void visit(AbstractBlock_c n) {
        visit((Stmt_c) n);
    }

    public void visit(ConstructorCall_c n) {
        visit((Stmt_c) n);
    }

    public void visit(If_c n) {
        visit((Stmt_c) n);
    }

    public void visit(LocalClassDecl_c n) {
        visit((Stmt_c) n);
    }

    public void visit(LocalDecl_c n) {
        visit((Stmt_c) n);
    }

    public void visit(Loop_c n) {
        visit((Stmt_c) n);
    }

    public void visit(Do_c n) {
        visit((Loop_c) n);
    }

    public void visit(While_c n) {
        visit((Loop_c) n);
    }

    public void visit(X10Loop_c n) {
        visit((Stmt_c) n);
    }

    public void visit(X10ClockedLoop_c n) {
        visit((X10Loop_c) n);
    }

    public void visit(TypeNode_c n) {
        visit((Term_c) n);
    }

    // public void visit(AmbTypeNode_c n) { visit((TypeNode_c)n); }
    public void visit(CanonicalTypeNode_c n) {
        visit((TypeNode_c) n);
    }

    // public void visit(AmbDepTypeNode_c n) { visit((TypeNode_c)n); }
    public void visit(AnnotationNode_c n) {
        visit((Node_c) n);
    }

    public void visit(NodeList_c n) {
        visit((Node_c) n);
    }

    public void visit(Id_c n) {
        visit((Node_c) n);
    }

    // public void visit(FunctionTypeNode_c n) {
    // visit((FunctionTypeNode_c)n); }

    public void searchFileList(String packageName, String typeName) throws IOException {
        if (RoseTranslator.DEBUG)
            System.out.println("LibraryVisitor.searchFileList()for package=" + packageName + ", type=" + typeName);
        return;
        // MH-20140901 comment out for skipping to lookup library classes
        // from library classess
        // for (Job job : jobList) {
        // FileSource source = (FileSource) job.source();
        // String sourceName = source.toString();
        // boolean isFoundSourceFile = false;
        // for (int i = 0; i <= fileIndex; ++i) { // including currently
        // processing file
        // String sourceFileGiven =
        // x10rose.ExtensionInfo.X10Scheduler.sourceList.get(i).source().path();
        // if (sourceName.equals(sourceFileGiven))
        // isFoundSourceFile = true;
        // }
        // if (isFoundSourceFile)
        // continue;
        //
        // Reader reader = source.open();
        // ErrorQueue eq = job.extensionInfo().compiler().errorQueue();
        // Parser p = job.extensionInfo().parser(reader, source, eq);
        // Node ast = p.parse();
        // TypeVisitor tVisitor = new TypeVisitor(packageName, typeName,
        // (SourceFile_c) job.ast(), job);
        //
        // ast.visit(tVisitor);
        // source.close();
        // if (tVisitor.isFound()) {
        // //////
        // // for (int i = 0; i < 5; i++) {
        // // if (RoseTranslator.DEBUG) System.out.println("loop test for parser " + i);
        // // reader = source.open();
        // // eq = job.extensionInfo().compiler().errorQueue();
        // // p = job.extensionInfo().parser(reader, source, eq);
        // // tVisitor = new TypeVisitor(packageName, typeName, source);
        // // ast.visit(tVisitor);
        // // }
        // //////
        // tVisitor.createTypeReferenceEnd();
        // return;
        // }
        // }
    }

    public void visit(AmbDepTypeNode_c n) {
        toRose(n, "TypeVisitor.AmbDepTypeNode_c : ", n.toString());
        String raw = n.toString();
        String ambTypeName = raw.replaceAll("\\{amb\\}", "");
        ambTypeName = RoseTranslator.trimTypeParameterClause(ambTypeName);

        if (RoseTranslator.isX10Primitive(ambTypeName))
            JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken());

        // TODO: convert Rail and GrowableRail as object types, not a normal
        // array in ROSE
        // This will cause collision of the same overloaded method. To
        // disable the check for collision,
        // commented out a line in x10_support.C in
        // X10_ROSE_Connection/x10_support.C in ROSE side:
        // ROSE_ASSERT(name.getString().compare(inputName.getString()) ==
        // 0);
        //
        else if (ambTypeName.indexOf("Rail[") >= 0 || ambTypeName.indexOf("GrowableRail[") >= 0) {
            handleAmbType(n);
            // n.type() and n.nameString() return null, so manipulate string
            // String type =
            // ambTypeName.substring(ambTypeName.indexOf('[')+1,
            // ambTypeName.indexOf(']'));
            // int lastDot = type.lastIndexOf(".");
            // // so far, eliminate package name
            // type = type.substring(lastDot+1);
            //
            // JNI.cactionTypeReference(package_name, type, this,
            // createJavaToken());
            // JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
        } else if (ambTypeName.indexOf("self==this") >= 0) {
            JNI.cactionTypeReference("", n.nameString(), this, RoseTranslator.createJavaToken());
        } else {
            handleAmbType(n);
        }
    }

    public void visit(AmbTypeNode_c n) {
        String raw = n.toString(); // n.nameString() throws
                                   // NullPointerException, so use
                                   // toString() instead
        String ambTypeName = raw.replaceAll("\\{amb\\}", "");
        ambTypeName = RoseTranslator.trimTypeParameterClause(ambTypeName);
        if (RoseTranslator.DEBUG) System.out.println("AmbTypeNode_c : " + ambTypeName + ", type=" + n.type());

        if (RoseTranslator.isX10Primitive(ambTypeName))
            JNI.cactionTypeReference("", ambTypeName, this, RoseTranslator.createJavaToken());

        // TODO: convert Rail and GrowableRail as object types, not a normal
        // array in ROSE
        // This will cause collision of the same overloaded method. To
        // disable the check for collision,
        // commented out a line in x10_support.C in
        // X10_ROSE_Connection/x10_support.C in ROSE side:
        // ROSE_ASSERT(name.getString().compare(inputName.getString()) ==
        // 0);
        //
        else if (ambTypeName.indexOf("Rail[") >= 0 || ambTypeName.indexOf("GrowableRail[") >= 0) {
            handleAmbType(n);
            // n.type() and n.nameString() return null, so manipulate string
            // String type =
            // ambTypeName.substring(ambTypeName.indexOf('[')+1,
            // ambTypeName.indexOf(']'));
            // int lastDot = type.lastIndexOf(".");
            // // so far, eliminate package name
            // type = type.substring(lastDot+1);
            //
            // JNI.cactionTypeReference(package_name, type, this,
            // createJavaToken());
            // JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
        } else if (ambTypeName.indexOf("self==this") >= 0) {
            JNI.cactionTypeReference("", n.nameString(), this, RoseTranslator.createJavaToken());
        } else {
            handleAmbType(n);
        }
    }

    public void visit(X10Formal_c n) {
        toRose(n, "TypeVisitor.formal: ", n.name().id().toString(), n.type().toString());

        // args_location = createJavaToken(args[0], args[args.length - 1]);
        //
        // for (int j = 0; j < args.length; j++) {
        // Argument arg = args[j];
        // JavaToken arg_location = createJavaToken(arg);
        // generateAndPushType(arg.type.resolvedType, arg_location);
        // String argument_name = new String(arg.name);
        // JavaParser.cactionBuildArgumentSupport(argument_name,
        // arg.isVarArgs(),
        // arg.binding.isFinal(),
        // arg_location);
        // }
        visitChild(n, n.type());
        JNI.cactionBuildArgumentSupport(n.name().toString(), n.vars().size() > 0, 
                                        n.flags().flags().isFinal(),
                                        RoseTranslator.createJavaToken(n, n.name().id().toString()));
    }

    public void visit(X10Call_c n) {
        toRose(n, "x10call: ", n.name().id().toString());
        String func_name = n.name().id().toString();
        String full = n.target().type().fullName().toString();
        polyglot.types.Package package_ = n.target().type().toPackage();
        String target_type_name = n.target().type().name().toString();
        if (func_name.equals("operator()")) {
//            if (RoseTranslator.DEBUG)
//                System.out.println("So far, return in case of \"operator()\"");
//            visitChild(n, n.target());
//            return;

            if (n.target().type().isRail()) {
                visitChild(n, n.target());
                List<Expr> args = n.arguments();
                visitChildren(n, args);
                JNI.cactionArrayReferenceEnd(RoseTranslator.createJavaToken(n, n.toString()));
                return;
            } else {
                if (RoseTranslator.DEBUG)
                    System.out.println("Currently eliminate \"operator()\"");
                // visitChild(n, n.target());
                func_name = n.target().toString();
                full = full.replaceAll("operator()", "");
            }
        }  else if (func_name.equals("implicit_operator_as") || func_name.equals("operator_as")) {
            visitChild(n, n.target());
            visitChildren(n, n.arguments());
            JNI.cactionCastExpressionEnd(RoseTranslator.createJavaToken(n, n.toString()));

            return;
        }  
        
        String package_name = "";
        if (package_ != null)
            package_name = package_.toString();
        else {
            int lastDot = full.lastIndexOf('.');
            if (lastDot > 0)
                package_name = full.substring(0, lastDot);
        }
        if (package_name.length() != 0) {
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, package_name));
            JNI.cactionPopPackage();
        }
        
        if (RoseTranslator.DEBUG)
            System.out.println("package=" + package_name + ", " + n.target());
//        JNI.cactionMessageSend(package_name, n.target().type().name().toString(), n.name().id().toString(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
        JNI.cactionMessageSend(package_name, target_type_name, func_name, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        visitChild(n, n.target());

        List<Expr> args = n.arguments();
        List<Expr> argTypes = new ArrayList<Expr>();

        for (int i = 0; i < args.size(); ++i) {
            Node n2 = args.get(i);
            visitChild(n2, n2.node());
        }

        for (int i = 0; i < args.size(); ++i) {
            Type t = args.get(i).type();
            // if (RoseTranslator.DEBUG) System.out.println(i + ":" + t + ", " +
            // args.get(i));
            Node n2 = args.get(i);
            JNI.cactionTypeReference(package_name, t.name().toString(), this, RoseTranslator.createJavaToken());
        }
        // visitChildren(n, argTypes);
        // visitChildren(n, n.typeArguments());
        // visitChildren(n, n.arguments());
//        JNI.cactionTypeReference(package_name, n.target().type().name().toString(), this, RoseTranslator.createJavaToken());
        if (RoseTranslator.isX10Primitive(package_name, target_type_name))
            JNI.cactionTypeReference("", target_type_name, this, RoseTranslator.createJavaToken());
        else
            JNI.cactionTypeReference(package_name, target_type_name, this, RoseTranslator.createJavaToken());
        
        int num_parameters = n.arguments().size();
        int numTypeArguments = n.typeArguments().size();
        int numArguments = n.arguments().size();
//        JNI.cactionMessageSendEnd(n.methodInstance().flags().isStatic(), n.target() != null, n.name().toString(), num_parameters, numTypeArguments, numArguments, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        JNI.cactionMessageSendEnd(n.methodInstance().flags().isStatic(), n.target() != null, func_name, num_parameters, numTypeArguments, numArguments, RoseTranslator.createJavaToken(n, n.name().id().toString()));
    }

    public void visit(X10ConstructorDecl_c n) {
        toRose(n, "X10ConstructorDecl:", n.name().id().toString());

        List<Formal> formals = n.formals();

        String method_name = n.name().id().toString();
        StringBuffer param = new StringBuffer();
        for (Formal f : n.formals()) {
            param.append(f.type().toString().toLowerCase());
        }
        // if (n.returnType().toString().indexOf("{") >= 0) {
        //
        // }

        // JNI.cactionBuildMethodSupportStart(method_name, method_index,
        // createJavaToken());
        // visitChild(n, n.returnType());
        // visitChildren(n, n.formals());
        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + method_name + "(" + param + ")");

        JNI.cactionMethodDeclaration(n.name().id().toString(), method_index, formals.size(), RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));

        // JNI.cactionBuildMethodSupportEnd(method_name, method_index, //
        // method index
        // false, false, false, 0, formals.size(),
        // true, /* user-defined-method */
        // new JavaToken(n.name().id().toString(), new
        // JavaSourcePositionInformation(n.position().line())),
        // new JavaToken(n.name().id().toString()+"_args", new
        // JavaSourcePositionInformation(n.position().line())));

        // visitChild(n, n.guard());
        // visitChild(n, n.offerType());
        // visitChildren(n, n.throwsTypes());
        visitChild(n, n.body());
        
        JNI.cactionMethodDeclarationEnd(0, n.body().statements().size(), RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));
        // String constructor_name = n.name().toString();
        //
        // JNI.cactionConstructorDeclarationHeader(constructor_name, false,
        // false, n.typeParameters().size(), n.formals().size(), n
        // .throwsTypes().size(), RoseTranslator.createJavaToken());
        // JNI.cactionConstructorDeclarationEnd(n.body().statements().size(),
        // createJavaToken());
        //
        // visitChildren(n, n.formals());
        // // visitChild(n, n.guard());
        // // visitChild(n, n.offerType());
        // // visitChildren(n, n.throwsTypes());
        // visitChild(n, n.body());
    }

    public void visit(X10ConstructorCall_c n) {
        toRose(n, "X10ConstructorCall:");
        visitChild(n, n.target());
        visitChildren(n, n.typeArguments());
        visitChildren(n, n.arguments());
    }

    public void visit(Block_c n) {
        toRose(n, "Block:");
        JNI.cactionBlock(RoseTranslator.createJavaToken(n, n.toString()));
        visitChildren(n, n.statements());
        JNI.cactionBlockEnd(n.statements().size(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(StmtSeq_c n) {
        toRose(n, "StmtSeq: ");
        visitChildren(n, n.statements());
    }

    public void visit(AssignPropertyCall_c n) {
        toRose(n, "AssignPropertyCall:");
        // MH-20140313 go through at this moment
        // Tentatively process empty statement instead of propertycall
        JNI.cactionEmptyStatement(RoseTranslator.createJavaToken(n, n.toString()));
        JNI.cactionEmptyStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
        // visitChildren(n, n.arguments());
    }

    public void visit(Empty_c n) {
        toRose(n, "Empty:");
        JNI.cactionEmptyStatement(RoseTranslator.createJavaToken(n, n.toString()));
        JNI.cactionEmptyStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(ArrayTypeNode_c n) {
        toRose(n, "ArrayTypeNode:", n.nameString());
    }

    public void visit(ArrayAccess_c n) {
        toRose(n, "ArrayAccess in TypeVisitor:", n.toString());
    }

    public void visit(X10CanonicalTypeNode_c n) {
        toRose(n, "X10CanonicalTypeNode in TypeVisitor:", n.nameString());
        String class_name = n.type().fullName().toString();
        if (RoseTranslator.isX10Primitive(class_name)) {
            String canonicalTypeName = n.nameString();
            JNI.cactionTypeReference("", canonicalTypeName, this, RoseTranslator.createJavaToken());
        } else if (n.type().toString().indexOf("x10.lang.Rail[") == 0 || n.type().toString().indexOf("x10.util.GrowableRail[") == 0) {
            String railString = n.type().toString();
            String type = railString.substring(railString.indexOf('[') + 1, railString.indexOf(']'));
            int lastDot = type.lastIndexOf(".");

            String package_name = type.substring(0, lastDot);
            type = type.substring(lastDot + 1);
            if (RoseTranslator.isX10Primitive(package_name, type))
                package_name = "";

            if (package_name.length() != 0) {
                JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, package_name));
                JNI.cactionPopPackage();
            }
            JNI.cactionTypeReference(package_name, type, this, RoseTranslator.createJavaToken());

            JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
        } else if (n.node().toString().indexOf("self==this") >= 0) {
            JNI.cactionTypeReference("", n.nameString(), this, RoseTranslator.createJavaToken());
        }
        // else if (n.node().toString().indexOf("{amb}") >= 0) {
        // if (RoseTranslator.DEBUG) System.out.println("n.node()=" + n.node());
        // JNI.cactionTypeReference("", n.node().toString(), this,
        // createJavaToken());
        // }
        else {
            // if (RoseTranslator.DEBUG) System.out.println(">>>> " + n.node() + ", " +
            // n.nameString() + ", " + n.type().arrayOf());
            int index = n.node().toString().indexOf("[");
            String className = n.node().toString();
            if (index >= 0)
                className = className.substring(0, index);
            int lastDot = className.lastIndexOf('.');

            String pkg = "";
            String type = "";
            if (lastDot >= 0) {
                pkg = className.substring(0, lastDot);
                type = className.substring(lastDot + 1);
            } else {
                type = className;
            }

            // if (RoseTranslator.DEBUG) System.out.println("className=" + className +
            // ", pkg=" + pkg + ", type=" + type);
            // So far, I use a representation without package name such as
            // "Rail".
            // If I use a representation such as "x10.lang.Rail",
            // lookupTypeByName() function tries
            // to look for a type name whether the type name is already
            // registered or not. (and, there is
            // no registration for Rail, so ROSE compiler fails.)
            JNI.cactionTypeReference("", n.nameString(), this, RoseTranslator.createJavaToken());
            // JNI.cactionTypeReference(pkg, n.nameString(), this,
            // createJavaToken());
        }
        // JNI.cactionTypeDeclaration("", n.nameString(), false, false,
        // false, false, false, false, false, false, false, false);
    }

    public void visit(Return_c n) {
        toRose(n, "Return:", (String) null);
        JNI.cactionReturnStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        JNI.cactionReturnStatementEnd((n.expr() != null), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10Binary_c n) {
        toRose(n, "X10Binary:", n.operator().toString());
        JNI.cactionBinaryExpression(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.left());
        visitChild(n, n.right());
        JNI.cactionBinaryExpressionEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10Unary_c n) {
        toRose(n, "X10Unary:", n.operator().toString());
        visitChild(n, n.expr());
    }

    public void visit(ParExpr_c n) { // parentheses
        toRose(n, "( )");
        visitChild(n, n.expr());
    }

    public void visit(X10Special_c n) {
        toRose(n, "X10Special:", n.kind().toString());
        String kind = n.kind().toString();

        String className = n.type().fullName().toString();
        int lastDot = className.lastIndexOf('.');
        String pkg = "";
        String type = "";
        if (lastDot >= 0) {
            pkg = className.substring(0, lastDot);
            type = className.substring(lastDot + 1);
        } else {
            type = className;
        }
        
        if (kind.equals(Special.Kind.THIS.toString())) {
            JNI.cactionThisReference(pkg, type, RoseTranslator.createJavaToken(n, n.kind().toString()));
        } else if (kind.equals(Special.Kind.SUPER.toString())) {
            JNI.cactionSuperReference(RoseTranslator.createJavaToken(n, n.kind().toString()));
        } else if (kind.equals(Special.Kind.SELF.toString())) {
            if (RoseTranslator.DEBUG)
                System.out.println("X10Special : Unhandled token " + kind);
        }
    }

    public void visit(Here_c n) {
        toRose(n, "here:");
        JNI.cactionHere(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10Local_c n) {
        toRose(n, "X10Local :", n.name().id().toString());
        // JavaParser.cactionSingleNameReference(package_name, type_name,
        // varRefName, javaParserSupport.createJavaToken(node));
        JNI.cactionSingleNameReference("", "", n.name().id().toString(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
    }

    public void visit(Eval_c n) {
        toRose(n, "Eval:", n.toString());
        visitChild(n, n.expr());
    }

    public void visit(For_c n) {
        toRose(n, "For:");
        visitChildren(n, n.inits());
        visitChild(n, n.cond());
        visitChildren(n, n.iters());
        visitChild(n, n.body());
    }

    public void visit(ForLoop_c n) {
        toRose(n, "ForLoop:");
        visitChild(n, n.formal());
        visitChild(n, n.cond());
        visitChild(n, n.domain());
        visitChild(n, n.body());
    }

    public void visit(Branch_c n) {
        toRose(n, "Branch:", n.kind() + (n.labelNode() != null ? "\\n" + n.labelNode().id().toString() : ""));
        Branch.Kind kind = n.kind();
        String label = n.labelNode() != null ? n.labelNode().id().toString() : "";
        if (kind == Branch.Kind.CONTINUE) {
            JNI.cactionContinueStatement(label, RoseTranslator.createJavaToken(n, kind.toString()));
        }
        else if (kind ==Branch.Kind.BREAK) {
            JNI.cactionBreakStatement(label, RoseTranslator.createJavaToken(n, kind.toString()));
        }
        else {
            throw new RuntimeException("Unsupported branch");
        }
    }

    public void visit(X10Do_c n) {
        toRose(n, "X10Do:");
        visitChild(n, n.cond());
        visitChild(n, n.body());
    }

    public void visit(X10While_c n) {
        toRose(n, "X10While:");
        visitChild(n, n.cond());
        visitChild(n, n.body());
    }

    public void visit(Tuple_c n) {
        toRose(n, "Tuple:");
        visitChildren(n, n.arguments());
    }

    public void visit(SettableAssign_c n) {
        toRose(n, "SettableAssign:", n.toString() + ", " + n.left().toString() + ", " + n.index() + ", " + n.right().toString());
        visitChild(n, n.left());
        visitChildren(n, n.index());
        visitChild(n, n.right());
    }

    public void visit(FieldAssign_c n) {
        toRose(n, "FieldAssign:", n.name().id().toString());
        // if (RoseTranslator.DEBUG) System.out.println("target=" + n.target() + ", right="
        // + n.right());
        String fieldName = n.name().id().toString();
        visitChild(n, n.target());
        JNI.cactionTypeReference("", n.target().type().name().toString(), this, RoseTranslator.createJavaToken());
        JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, RoseTranslator.createJavaToken(n, fieldName));
        visitChild(n, n.right());
        JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.right().toString()));
    }

    public void visit(X10Field_c n) {
        toRose(n, "X10Field:", n.name().id().toString());
        // if (RoseTranslator.DEBUG) System.out.println("target=" + n.target()
        // + ", target type=" + n.target().type().name()
        // + ", name=" + n.name().id()
        // + ", instnace=" + n.fieldInstance());
        String fieldName = n.name().id().toString();
        visit(n.target());
        JNI.cactionTypeReference("", n.target().type().name().toString(), this, RoseTranslator.createJavaToken());
        JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, RoseTranslator.createJavaToken(n, fieldName));
    }

    public void visit(X10FieldDecl_c n) {
        toRose(n, "X10FieldDecl:", n.name().id().toString());
        // visitChild(n, n.type());
        // visitChild(n, n.init());
    }

    public void visit(X10LocalDecl_c n) {
        toRose(n, "X10LocalDecl:", n.name().id().toString());
        // if (RoseTranslator.DEBUG) System.out.println("init=" + n.init());
        visitChild(n, n.type());
        JNI.cactionLocalDeclaration(0, n.name().id().toString(), false, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        JNI.cactionLocalDeclarationEnd(n.name().id().toString(), false, RoseTranslator.createJavaToken(n, n.name().id().toString()));
    }

    public void visit(PropertyDecl_c n) {
        toRose(n, "PropertyDecl:", n.name().id().toString());
        visitChild(n, n.type());
        visitChild(n, n.init());
    }

    public void visit(X10If_c n) {
        toRose(n, "X10If:", "if");

        visitChild(n, n.cond());
        visitChild(n, n.consequent());
        visitChild(n, n.alternative());

        JNI.cactionIfStatement(RoseTranslator.createJavaToken(n, n.toString()));
        JNI.cactionIfStatementEnd(n.alternative() != null, RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10Conditional_c n) {
        toRose(n, "X10Conditional:");
        visitChild(n, n.cond());
        visitChild(n, n.consequent());
        visitChild(n, n.alternative());
    }

    public void visit(Assert_c n) {
        toRose(n, "Assert:");
        visitChild(n, n.cond());
        visitChild(n, n.errorMessage());
    }

    public void visit(Throw_c n) {
        toRose(n, "Throw:");
        visitChild(n, n.expr());
    }

    public void visit(Try_c n) {
        toRose(n, "Try:");
        visitChild(n, n.tryBlock());
        visitChildren(n, n.catchBlocks());
        visitChild(n, n.finallyBlock());
    }

    public void visit(Catch_c n) {
        toRose(n, "Catch:");
        visitChild(n, n.formal());
        visitChild(n, n.body());
    }

    public void visit(Labeled_c n) {
        toRose(n, "Labeled:", n.labelNode().id().toString());
        visitChild(n, n.statement());
    }

    public void visit(X10BooleanLit_c n) {
        toRose(n, "X10BooleanLit:", Boolean.toString(n.value()));
        if (n.value())
            JNI.cactionTrueLiteral(RoseTranslator.createJavaToken(n, n.value() + ""));
        else
            JNI.cactionFalseLiteral(RoseTranslator.createJavaToken(n, n.value() + ""));

    }

    public void visit(ClassLit_c n) {
        toRose(n, "ClassLit: ");
        visitChild(n, n.typeNode());
    }

    public void visit(X10FloatLit_c n) {
        toRose(n, "X10FloatLit:", Double.toString(n.value()));
    }

    public void visit(NullLit_c n) {
        toRose(n, "NullLit:");
        JNI.cactionNullLiteral(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10CharLit_c n) {
        toRose(n, "X10CharLit:", "" + n.value());
    }

    public void visit(IntLit_c n) {
        toRose(n, "IntLit:", Long.toString(n.value()));

        if (n.kind() == IntLit.INT) {
            JNI.cactionIntLiteral((int) n.value(), n.toString(), RoseTranslator.createJavaToken(n, n.value() + ""));
        } else if (n.kind() == IntLit.LONG) {
            JNI.cactionLongLiteral(n.value(), n.toString(), RoseTranslator.createJavaToken(n, n.value() + ""));

        } else {
            if (RoseTranslator.DEBUG)
                System.out.println("Unhandled literal : " + n.toString());
        }
    }

    public void visit(X10StringLit_c n) {
        toRose(n, "TypeVisitor.X10StringLit:", StringUtil.escape(n.value()));
    }

    public void visit(Finish_c n) {
        toRose(n, "Finish in TypeVisitor:");
        JNI.cactionFinish(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.body());
        JNI.cactionFinishEnd(n.clocked(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(FinishExpr_c n) {
        toRose(n, "FinishExpr:", n.toString());
        JNI.cactionFinishExpr(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.reducer());
        visitChild(n, n.body());
        JNI.cactionFinishExprEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(AtStmt_c n) {
        toRose(n, "AtStmt in TypeVisitor:");
        JNI.cactionAt(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.place());
        visitChild(n, n.body());
        JNI.cactionAtEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(AtHomeStmt_c n) {
        toRose(n, "AtHomeStmt:");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtExpr_c n) {
        toRose(n, "AtExpr:");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtHomeExpr_c n) {
        toRose(n, "AtHomeExpr:");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtEach_c n) {
        toRose(n, "AtEach:");
        visitChild(n, n.formal());
        visitChild(n, n.domain());
        visitChild(n, n.body());
    }

    public void visit(Async_c n) {
        toRose(n, "Async in TypeVisitor:");
        JNI.cactionAsync(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.body());
        JNI.cactionAsyncEnd(n.clocked(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Atomic_c n) {
        toRose(n, "Atomic:");
        visitChild(n, n.body());
    }

    public void visit(When_c n) {
        toRose(n, "When:");
        visitChild(n, n.expr());
        visitChild(n, n.stmt());
    }

    public void visit(X10New_c n) {
        toRose(n, "TypeVisitor.X10New:");
        visitChildren(n, n.typeArguments());
        visitChildren(n, n.arguments());
        visitChild(n, n.objectType());
        visitChild(n, n.body());
    }

    public void visit(Allocation_c n) {
        toRose(n, "Allocation:");
        visitChild(n, n.objectType());
    }

    public void visit(LocalAssign_c n) {
        toRose(n, "LocalAssign:");
        JNI.cactionAssignment(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.local());
        visitChild(n, n.right());
        JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10LocalAssign_c n) {
        toRose(n, "X10LocalAssign:");
        visitChild(n, n.local());
        visitChild(n, n.right());
    }

    public void visit(X10Cast_c n) {
        toRose(n, "X10Cast:");
        visitChild(n, n.castType());
        visitChild(n, n.expr());
    }

    public void visit(X10Instanceof_c n) {
        toRose(n, "X10Instanceof:");
        visitChild(n, n.compareType());
        visitChild(n, n.expr());
    }

    public void visit(SubtypeTest_c n) {
        toRose(n, "SubtypeTest:");
        visitChild(n, n.subtype());
        visitChild(n, n.supertype());
    }

    public void visit(DepParameterExpr_c n) {
        toRose(n, "DepParameterExpr:");
        visitChildren(n, n.formals());
        visitChildren(n, n.condition());
    }

    public void visit(HasZeroTest_c n) {
        toRose(n, "HasZeroTest:");
        visitChild(n, n.parameter());
    }

    public void visit(Closure_c n) {
        toRose(n, "Closure:");
        visitChildren(n, n.formals());
        visitChild(n, n.body());
    }

    public void visit(ClosureCall_c n) {
        toRose(n, "ClosureCall:");
        visitChild(n, n.target());
        visitChildren(n, n.arguments());
    }

    public void visit(StmtExpr_c n) {
        toRose(n, "StmtExpr:");
        visitChildren(n, n.statements());
    }

    public void visit(AmbReceiver_c n) {
        toRose(n, "AmbReceiver:", n.nameNode().id().toString());
    }

    public void visit(Switch_c n) {
        toRose(n, "Switch:");
        visitChild(n, n.expr());
        visitChildren(n, n.elements());
    }

    public void visit(SwitchBlock_c n) {
        toRose(n, "SwitchBlock:");
        visitChildren(n, n.statements());
    }

    public void visit(Case_c n) {
        toRose(n, "Case:");
        visitChild(n, n.expr());
    }

    public void visit(LocalTypeDef_c n) {
        toRose(n, "LocalTypeDef:");
        visitChild(n, n.typeDef());
    }

    public void visit(Next_c n) {
        toRose(n, "Next:", "here");
    }

    public void visit(TypeDecl_c n) {
        toRose(n, "TypeDecl: ", n.name().id().toString());
        visitChild(n, n.type());
    }

}
