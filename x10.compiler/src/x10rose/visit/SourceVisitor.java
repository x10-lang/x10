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
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Allocation_c;
import polyglot.ast.AmbReceiver_c;
import polyglot.ast.ArrayAccess_c;
import polyglot.ast.ArrayTypeNode_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Case_c;
import polyglot.ast.Catch_c;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassLit_c;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall.Kind;
import polyglot.ast.Branch;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.Import;
import polyglot.ast.Import_c;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.JL;
import polyglot.ast.Labeled_c;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.NullLit_c;
import polyglot.ast.PackageNode;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.SourceFile_c;
import polyglot.ast.Special;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ast.Unary;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorQueue;
import polyglot.util.StringUtil;
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
import x10.ast.HasZeroTest_c;
import x10.ast.Here_c;
import x10.ast.IsRefTest;
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
import x10.types.ClosureDef;
import x10.types.FunctionType_c;
import x10.types.ParameterType.Variance;
import x10.visit.X10DelegatingVisitor;
import x10rose.ExtensionInfo;

public class SourceVisitor extends X10DelegatingVisitor {
    Node parent;
    CodeWriter w;
    private List<Import> imports;
    private List<String> package_ref;

    public static boolean isGatheringFile = true;
    private static String currentPackageName = "";
    
    public SourceVisitor(CodeWriter w, Node parent) {
        this.parent = parent;
        this.w = w;
        package_ref = new ArrayList<String>();
        imports = new ArrayList<Import>();
    }

    void toRose(Node n, String name, Object... extra) {
        if (RoseTranslator.DEBUG) System.out.print("PRINT ");
        if (name != null)
            if (RoseTranslator.DEBUG) System.out.print(name);
        if (extra != null)
            for (Object s : extra) {
                if (RoseTranslator.DEBUG) System.out.print(s + " ");
            }
        if (RoseTranslator.DEBUG) System.out.println();
    }

    void visitChild(Node p, Node n) {
        if (n == null)
            return;
        
        new SourceVisitor(w, p).visitAppropriate(n);
    }

    void visitChildren(Node p, List<? extends Node> l) {
        if (l == null)
            return;
        for (Node n : l)
            visitChild(p, n);
    }

    /**
     * Checks the node type and invoke an appropriate visitor method
     * for <tt>n</tt>. 
     * 
     * Note that this method will be invoked when <tt>n</tt> does not 
     * match any X10 type handled in the <tt>visitAppropriate(JL)</tt>.
     * 
     * @see X10DelegatingVisit.visitAppropriate(JL)
     * 
     */
    public void visit(Node_c n) {
        toRose(n, null);
        if (n instanceof FinishExpr_c) { visit((FinishExpr_c)n); }
        else
            System.err.println("UNRECOGNISED NODE in ToRoseVisitor: " + n.getClass());
    }

//    private static boolean isDecl = true;

    private static int numSourceFile;

    private static HashMap<String, Node> astMap = new HashMap<String, Node>();

    public void searchFileList(String packageName, String typeName) throws IOException {
        if (RoseTranslator.DEBUG) System.out.println("SourceVisitor.searchFileList() for package=" + packageName + ", type=" + typeName);
        for (Job job : RoseTranslator.jobList) {
            FileSource source = (FileSource) job.source();
            String sourceName = source.toString();
            boolean isFoundSourceFile = false;
            for (int i = 0; i <= RoseTranslator.fileIndex; ++i) { // including currently processing file
                String sourceFileGiven = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(i).source().path();
                if (sourceName.equals(sourceFileGiven))
                    isFoundSourceFile = true;
            }
            if (isFoundSourceFile)
                continue;

            Reader reader = source.open();
            ErrorQueue eq = job.extensionInfo().compiler().errorQueue();
            Parser p = job.extensionInfo().parser(reader, source, eq);
            source.close();
            Node ast;
            if (astMap.containsKey(sourceName))
                ast = astMap.get(sourceName);
            else {
                ast = p.parse();
                astMap.put(sourceName, ast);
            }

            LibraryVisitor tVisitor = new LibraryVisitor(packageName, typeName, (SourceFile_c) job.ast(), job);

            ast.visit(tVisitor);

            if (tVisitor.isFound()) {
                tVisitor.createTypeReferenceEnd();
                return;
            }
        }
    }

    public boolean addFileIndex() {
        if (++RoseTranslator.fileIndex == numSourceFile) {
            --RoseTranslator.fileIndex;
            return false;
        }
        return true;
    }

    public void subFileIndex() {
        if (--RoseTranslator.fileIndex < 0)
            ++RoseTranslator.fileIndex;
    }

    public void visitDeclarations() {
//        isDecl = true;
        SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);
        FileStatus fileStatus = ExtensionInfo.fileHandledMap.get(file);
        /* MH-20141129 : Skips the second traversal if this file has been already visited.
         *               This check is necessary when this method is invoked by ROSE.
         *               @see src/frontend/X10_ROSE_Connection/x10ActionROSE.C:Java_x10rose_visit_JNI_cactionTypeReference()
         */
        if (fileStatus.isDeclHandled()) 
            return;      
        ExtensionInfo.fileHandledMap.put(file, fileStatus);
        visit(file);
        fileStatus.handleDecl();  
    }

    public void visitDefinitions() {
//        isDecl = false;
        SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);
        FileStatus fileStatus = ExtensionInfo.fileHandledMap.get(file);
        ExtensionInfo.fileHandledMap.put(file, fileStatus);
        JNI.cactionSetCurrentFilePath(file.source().path());
        if (!fileStatus.isDefHandled())
            visit(file);
        fileStatus.handleDef();
    }

    public int handleClassMembers(List<ClassMember> members, String package_name, String type_name) {
        int final_member_size = members.size();
        
        List<ClassMember> sorted_members = new ArrayList<ClassMember>();        
        for (int i = 0; i < members.size(); ++i) {
            ClassMember cm = members.get(i);
            if (cm instanceof X10ClassDecl_c)
                sorted_members.add(0, cm);
            else
                sorted_members.add(cm);
        }
        
        for (int i = 0; i < sorted_members.size(); ++i) {
            JL m = sorted_members.get(i);
            if (m instanceof X10MethodDecl_c) {
                X10MethodDecl_c methodDecl = (X10MethodDecl_c) m;
                StringBuffer param = new StringBuffer();
                for (Formal f : methodDecl.formals()) {
                    param.append(f.type().toString().toLowerCase());
                }
                if (methodDecl.position().isCompilerGenerated()) {
                    System.out.println("CompilerGen : " + methodDecl);
                    --final_member_size;
                    continue;
                }
                RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + methodDecl.name().toString() + "(" + param + ")", RoseTranslator.uniqMemberIndex++);
                previsit(methodDecl, package_name, type_name);
            } else if (m instanceof X10ConstructorDecl_c) {
                X10ConstructorDecl_c constructorDecl = (X10ConstructorDecl_c) m;
                StringBuffer param = new StringBuffer();
                for (Formal f : constructorDecl.formals()) {
                    param.append(f.type().toString().toLowerCase());
                }
                if (constructorDecl.position().isCompilerGenerated()) {
                    --final_member_size;
                    continue;
                }
                RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + ((X10ConstructorDecl_c) m).name().toString() + "(" + param + ")", RoseTranslator.uniqMemberIndex++);
                previsit(constructorDecl, package_name, type_name);
            } else if (m instanceof X10FieldDecl_c) {
                X10FieldDecl_c fieldDecl = (X10FieldDecl_c) m;
                if (fieldDecl.position().isCompilerGenerated()) {
                    --final_member_size;
                    continue;
                }                
                RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + ((X10FieldDecl_c) m).name().toString(), RoseTranslator.uniqMemberIndex++);
                previsit(fieldDecl, type_name);
            } else if (m instanceof TypeNode_c) {
                if (RoseTranslator.DEBUG) System.out.println("TypeNode_c : " + m);
            } else if (m instanceof TypeDecl_c) {
                if (RoseTranslator.DEBUG) System.out.println("TypeDecl_c : " + m);
                // nested class
            } else if (m instanceof X10ClassDecl_c) {
                if (RoseTranslator.DEBUG) System.out.println("Previsit ClassDecl_c : " + m + ", package=" + package_name + ", type=" + type_name);
                X10ClassDecl_c nested_class = (X10ClassDecl_c) m;
                RoseTranslator.recordNestedClass(package_name, type_name, nested_class.name().toString());
                previsit(nested_class, package_name, type_name/*+"."+nested_class.name().toString()*/);
                JNI.cactionSetCurrentClassName((package_name.length() == 0 ? "" : package_name + ".") + type_name);
            } else if (m instanceof ClassDecl_c) {
                if (RoseTranslator.DEBUG) System.out.println("ClassDecl_c : " + m);
            } else {
                if (RoseTranslator.DEBUG) System.out.println("Unhandled node : " + m);
            }
        }
        return final_member_size;
    }

    public void visit(SourceFile_c n) {

        if (isGatheringFile) {
            ++numSourceFile;
            x10rose.ExtensionInfo.X10Scheduler.sourceList.add(n);
            FileStatus fileStatus = new FileStatus(n);
            ExtensionInfo.fileHandledMap.put(n, fileStatus);
            return;
        }
        
        FileStatus fileStatus = ExtensionInfo.fileHandledMap.get(n);
        
        if (!fileStatus.isDeclHandled()) {
            toRose(n, "First visit SourceFile_c:" + n.source().path());

            JNI.cactionCompilationUnitList(1, new String[] { n.source().path() });
            JNI.cactionSetupSourceFilename(n.source().path());

            package_ref.add("x10.lang"); // auto-import
            package_ref.add("x10.io");
            imports = n.imports();

            PackageNode pnode = n.package_();
            String package_name = (pnode == null) ? "" : pnode.toString();
            if (package_name.length() != 0) {
                JNI.cactionInsertImportedPackageOnDemand(package_name, RoseTranslator.createJavaToken(n, n.source().path()));
                package_ref.add(package_name);
                currentPackageName = package_name;
            }
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, n.source().path()));
            JNI.cactionPopPackage();
            
            JNI.cactionCompilationUnitDeclaration(n.source().path(), package_name, n.source().path(), RoseTranslator.createJavaToken(n, n.source().path()));
        }

        visitChildren(n, n.decls());
    }

    /*
     * This is not invoked. Instead, import clauses are handled
     * inside <tt>visitDeclarations(X10ClassDecl_c)</tt>.
     */
    public void visit(Import_c n) {
        toRose(n, "Import:", n.kind() + " " + n.name().toString());
    }
    
    public void visit(AnnotationNode_c n) {
        toRose(n, "Annotation:", n);
    }


    /*
     * This is not invoked. Instead, package clauses are handled
     * inside <tt>visitDeclarations(X10ClassDecl_c)</tt>. 
     */
    public void visit(PackageNode_c n) {
        toRose(n, "Package:", n.package_().get().toString());
    }

    public void visitDeclarations(X10ClassDecl_c n) {
        toRose(n, "X10ClassDecl in visitDeclarations:", n.name().id());
        Flags flags = n.flags().flags();
        SourceFile_c srcfile = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);  
        List<Import> imports = srcfile.imports();
        for (Import import_ : imports) {
            if (RoseTranslator.DEBUG) System.out.println("Import=" + import_.name() + ", kind=" + import_.kind());
            if (import_.kind() == Import.CLASS) {
                JNI.cactionImportReference(false, import_.name().toString(), false, RoseTranslator.createJavaToken());
            }
            else if (import_.kind() == Import.PACKAGE) {
                JNI.cactionImportReference(false, import_.name().toString(), true, RoseTranslator.createJavaToken());
            }
        }
        Ref ref = n.classDef().package_();
        String package_name = (ref == null) ? "" : ref.toString();
        if (package_name.length() != 0)
            RoseTranslator.package_traversed.add(package_name);
        
        String class_name = n.name().id().toString();
        if (n.classDef().isInnerClass()) {
            /* 
             * Since outer().toString() includes a package name, we need
             * to separate it to a package name and an outer class name to 
             * reuse the existing JNI interfaces.
             */
            String outer = n.classDef().outer().toString(); 
            class_name = outer + "." + class_name;
        }
        
        RoseTranslator.classes.put(class_name, package_name);
        JNI.cactionSetCurrentClassName((package_name.length() == 0 ? "" : package_name + ".") + class_name);
        
        JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, class_name));
        JNI.cactionInsertClassStart(class_name, false, false, false, flags.isStruct(), RoseTranslator.createJavaToken(n, class_name));
        
        JNI.cactionInsertClassEnd(class_name, RoseTranslator.createJavaToken(n, class_name));

        List<ClassMember> members = ((X10ClassBody_c) n.body()).members();
        List<TypeParamNode> typeParamList = n.typeParameters();

        String[] typeParamNames = new String[typeParamList.size()];
        for (int i = 0; i < typeParamList.size(); ++i) {
            String typeParam = typeParamList.get(i).name().toString();
            typeParamNames[i] = typeParam;
            JNI.cactionSetCurrentClassName(typeParam);
            JNI.cactionInsertClassStart(typeParam, false, false, false, false, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertClassEnd(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionPushTypeParameterScope(package_name, class_name, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertTypeParameter(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionBuildTypeParameterSupport(package_name, class_name, -1, typeParam, 0, RoseTranslator.createJavaToken(n, typeParam));
        }
        if (typeParamList.size() > 0)
            JNI.cactionSetCurrentClassName((package_name.length() > 0 ? package_name + "." : "") + class_name);

        JNI.cactionBuildClassSupportStart(class_name, "", true, 
                false, false, false, false, RoseTranslator.createJavaToken(n, class_name));

        // handling of a super class and interfaces
        TypeNode superClass = n.superClass();
        String superClassName = "";
        if (superClass != null) {
            superClassName = superClass.nameString();
            visit((X10CanonicalTypeNode_c) superClass);
        }
        List<TypeNode> interfaces = n.interfaces();
        String[] interfaceNames = new String[interfaces.size()];
        for (int i = 0; i < interfaces.size(); ++i) {
            TypeNode intface = interfaces.get(i);
            String interfaceName = RoseTranslator.trimTypeParameterClause(intface.toString());
            interfaceNames[i] = interfaceName;
            visit((X10CanonicalTypeNode_c) intface);
        }

        JNI.cactionBuildClassExtendsAndImplementsSupport(typeParamList.size(), typeParamNames, superClass != null, superClassName, interfaces == null ? 0 : interfaces.size(), interfaceNames, RoseTranslator.createJavaToken(n, class_name));
        int member_size = handleClassMembers(members, package_name, class_name);
        
        JNI.cactionBuildClassSupportEnd(class_name, member_size, RoseTranslator.createJavaToken(n, class_name));
        
        // MH-20141128
        JNI.cactionSetupSourceFilename(srcfile.source().path());
        
        JNI.cactionTypeDeclaration(package_name, class_name, /* num_annotations */0, n.superClass() != null, /* is_annotation_interface */false, flags.isInterface(),
        /* is_enum */false, flags.isAbstract(), flags.isFinal(), flags.isPrivate(), flags.isPublic(), flags.isProtected(), flags.isStatic(), /* is_strictfp */false, RoseTranslator.createJavaToken(n, class_name));
        
        List<PropertyDecl> propList = n.properties();
        for (PropertyDecl prop : propList) {
            visitChild(prop, prop.type());
            JNI.cactionAppendProperty(prop.name().id().toString(), prop.type().type().isRail(), 
                                        prop.flags().flags().isFinal(), RoseTranslator.createJavaToken());
        }
        if (propList.size() > 0)
            JNI.cactionSetProperties(propList.size(), RoseTranslator.createJavaToken());
        //MH-20141128
        JNI.cactionSetCurrentClassName(class_name);
    }

    public void visitDefinitions(X10ClassDecl_c n) {
        toRose(n, "X10ClassDecl in visitDefinitions:", n.name().id().toString());
        String class_name = n.name().id().toString();

        Ref ref = n.classDef().package_();
        String package_name = (ref == null) ? "" : ref.toString();
        
//        String path = ((package_name.length() == 0)? "" : package_name + ".") + n.name().id().toString();
        String path = n.classDef().toString();
        JNI.cactionSetCurrentClassName(path);

//        if (package_name.length() != 0) {
//            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, n.toString()));
//            JNI.cactionInsertClassStart(class_name, false, false, false, RoseTranslator.createJavaToken(n, class_name));
//        }
        // Are the following five lines necessary again?
        visitChildren(n, n.typeParameters());
        visitChildren(n, n.properties());
        visitChild(n, n.classInvariant());
        visitChild(n, n.superClass());
        visitChildren(n, n.interfaces());
        
        visitChild(n, n.body());
        SourceFile_c srcfile = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);  
        JNI.cactionSetupSourceFilename(srcfile.source().path());
        JNI.cactionTypeDeclarationEnd(true, RoseTranslator.createJavaToken(n, class_name));
        JNI.cactionSetCurrentClassName(path);
        if (!n.classDef().isInnerClass()
            && !n.classDef().isNested()) {
            JNI.cactionPopPackage();
        }
        toRose(n, "X10ClassDecl in visitDefinitions end:", n.name().id().toString());
    }

    public void visit(X10ClassDecl_c n) {
        toRose(n, "X10ClassDecl:", n.name().id().toString(), n.flags().flags().isStruct()); 
        SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(RoseTranslator.fileIndex);
        FileStatus fileStatus = ExtensionInfo.fileHandledMap.get(file);

        if (!fileStatus.isDeclHandled())
            visitDeclarations(n);
        else 
            visitDefinitions(n);
    }

    public void visit(LocalClassDecl_c n) {
        toRose(n, "LocalClassDecl:");
        visitChild(n, n.decl());
    }

    /**
     * (non-Javadoc)
     * @see x10.visit.X10DelegatingVisitor#visit(x10.ast.X10ClassBody_c)
     */
    public void visit(X10ClassBody_c n) {
        toRose(n, "classBody: ", n.toString());
        /*
         * Extract the information about the declaring class by using the
         * return type of a constructor.
         *
         * This manipulation is based on the fact that at least one 
         * implicit constructor exists in an x10 class.
         */
        String declaringClass = null;
        for (ClassMember mem : n.members()) {
            if (mem instanceof X10ConstructorDecl_c) {
                X10ConstructorDecl_c cd = (X10ConstructorDecl_c) mem;
                declaringClass = cd.returnType().type().fullName().toString();
            }
        }
        if (declaringClass==null)
            throw new RuntimeException("Failed to obtain a class name");
//        visitChildren(n, n.members());
        for (Node mem : n.members()) {
            if (!(mem instanceof X10ClassDecl_c))
                JNI.cactionSetCurrentClassName(declaringClass);
            
            visitChild(n, mem);
            
            if (!(mem instanceof X10ClassDecl_c)) {
                JNI.cactionSetCurrentClassName(declaringClass);
            }
        }
        toRose(n, "classBody end: ", n.toString());
    }

    public void visit(X10MethodDecl_c n) {
        toRose(n, "method decl: ", n.name().id().toString(), n.body(), n.flags().position().isCompilerGenerated());
        if (n.body() == null || n.position().isCompilerGenerated())
            return;

        List<Formal> formals = n.formals();
        String method_name = n.name().id().toString();
        StringBuffer param = new StringBuffer();
        for (Formal f : n.formals()) {
            param.append(f.type().toString().toLowerCase());
        }
        processAnnotation(n);
        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + method_name + "(" + param + ")");
        JNI.cactionMethodDeclaration(method_name, method_index, RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));
        
        Flags flags = n.flags().flags();
        JNI.cactionMethodDeclarationHeader(method_name, flags.isAbstract(), flags.isNative(), flags.isStatic(), flags.isFinal(), /* java_is_synchronized */false, flags.isPublic(), flags.isProtected(), flags.isPrivate(), /* java_is_strictfp */false, n.typeParameters().size(), formals.size(), n.throwsTypes().size(), RoseTranslator.createJavaToken(n, method_name));
        visitChild(n, n.body());
        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        JNI.cactionMethodDeclarationEnd(0, n.body().statements().size(), RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));
        toRose(n, "method decl end: ", n.name().id().toString());
    }
    
    void processAnnotation(Node_c n) {    
//        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
//        System.out.println("1216:annotation size=" + annotations.size() + " for " + n);
//        for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext(); ) {
//             AnnotationNode a = i.next();
//             System.out.println("1216:anno=" + a + ", " + a.annotationType() + ", " + a.annotationType().type().toClass());
//             visitChild(n, a.annotationType());
//             JNI.cactionNormalAnnotationEnd(0, RoseTranslator.createJavaToken(n, n.toString()));
//        }
    }

    public void previsit(X10MethodDecl_c n, String package_name, String class_name) {
        toRose(n, "Previsit method decl: ", n.name().id().toString());
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
        // writing like"public def toString() = "Place(" + this.id + ")";"
        if (n.returnType() instanceof UnknownTypeNode_c)
            JNI.cactionTypeReference("", "void", this, RoseTranslator.createJavaToken());
        else 
            visitChild(n, n.returnType());

        visitChildren(n, n.formals());

        /*
         * JNI.cactionMethodDeclaration(n.name().id().toString(),
         * method_index++, formals.size(), new
         * JavaToken(n.name().id().toString(), new
         * JavaSourcePositionInformation(n.position().line())), new
         * JavaToken(n.name().id().toString()+"_args", new
         * JavaSourcePositionInformation(n.position().line())));
         */
        Flags flags = n.flags().flags();
        JNI.cactionBuildMethodSupportEnd(method_name, method_index, false, flags.isAbstract(), flags.isNative(), 0, formals.size(), true, RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));
        
        if (n.guard() != null) {
            String guard = "";
            List<Expr> cond = n.guard().condition();
            for (int i = 0; i < cond.size(); ++i) {
                if (i > 0)
                    guard += ", ";
                Expr exp = cond.get(i);
                if (exp instanceof IsRefTest) {
                    guard += ((IsRefTest)exp).firstChild() + " isref";
                } else if (exp instanceof HasZeroTest_c) {
                    guard += ((HasZeroTest_c)exp).firstChild() + " haszero";
                } else {
                    throw new RuntimeException("Unsupported guard: " + exp);
                }
            }
            JNI.cactionAttachGuard(guard, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        }
        for (int i = 0; i < typeParamList.size(); ++i) { 
            TypeParamNode node = typeParamList.get(i);
            Variance variance = node.type().getVariance();
            String typeParam = typeParamList.get(i).name().toString(); 
//            System.out.println("TypeParam=" + typeParam);
            JNI.cactionAttachTypeParameterToMethodDecl(typeParam, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        }
        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        for (AnnotationNode a : annotations)  {
            visitChild(n, a.annotationType());
        }
        JNI.cactionAttachAnnotationsToMethodDecl(annotations.size(), RoseTranslator.createJavaToken(n, n.name().id().toString()));

        if (RoseTranslator.DEBUG) System.out.println("Previsit method decl end");
    }
    
    /**
     * This method is invoked to parse nested classes
     * 
     * @param n
     * @param package_name
     * @param parentClass_name
     */
    public void previsit(X10ClassDecl_c n, String package_name, String parentClass_name) {
        toRose(n, "Previsit class decl: ", n.name().id().toString());
        String class_name = n.name().id().toString();
        String path = ((package_name.length() == 0)? "" : package_name + ".") 
                      + parentClass_name + "." +n.name().id().toString();      
        Flags flags = n.flags().flags();
        JNI.cactionSetCurrentClassNameWithCopyingStacks(path);
        
        JNI.cactionInsertClassStart(class_name, false, false, false, flags.isStruct(), RoseTranslator.createJavaToken(n, class_name));
        JNI.cactionInsertClassEnd(class_name, RoseTranslator.createJavaToken(n, class_name));

        List<ClassMember> members = ((X10ClassBody_c) n.body()).members();
        List<TypeParamNode> typeParamList = n.typeParameters();

        String[] typeParamNames = new String[typeParamList.size()];
        for (int i = 0; i < typeParamList.size(); ++i) {
            String typeParam = typeParamList.get(i).name().toString();
            typeParamNames[i] = typeParam;
            JNI.cactionSetCurrentClassName(typeParam);
            JNI.cactionInsertClassStart(typeParam, false, false, false, false, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertClassEnd(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionPushTypeParameterScope(package_name, parentClass_name + "." + class_name, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionInsertTypeParameter(typeParam, RoseTranslator.createJavaToken(n, typeParam));
            JNI.cactionBuildTypeParameterSupport(package_name, parentClass_name + "." + class_name, -1, typeParam, 0, RoseTranslator.createJavaToken(n, typeParam));
        }
        if (typeParamList.size() > 0)
            JNI.cactionSetCurrentClassName(path);

        JNI.cactionBuildClassSupportStart(class_name, "", true, 
            false, false, false, false, RoseTranslator.createJavaToken(n, class_name));

        // handling of a super class and interfaces
        TypeNode superClass = n.superClass();
        String superClassName = "";
        if (superClass != null) {
            superClassName = superClass.nameString();
            visit((X10CanonicalTypeNode_c) superClass);
        }
        List<TypeNode> interfaces = n.interfaces();
        String[] interfaceNames = new String[interfaces.size()];
        for (int i = 0; i < interfaces.size(); ++i) {
            TypeNode intface = interfaces.get(i);
            String interfaceName = RoseTranslator.trimTypeParameterClause(intface.toString());
            interfaceNames[i] = interfaceName;
            visit((X10CanonicalTypeNode_c) intface);
        }

        JNI.cactionBuildClassExtendsAndImplementsSupport(typeParamList.size(), typeParamNames, superClass != null, superClassName, interfaces == null ? 0 : interfaces.size(), interfaceNames, RoseTranslator.createJavaToken(n, class_name));
        // MH-20141024
        int member_size = handleClassMembers(members, package_name, parentClass_name+"."+class_name);

        JNI.cactionBuildClassSupportEnd(class_name, member_size, RoseTranslator.createJavaToken(n, class_name));

        System.out.println("0218-2: class_name=" + class_name + ": " + flags.isPublic());
        JNI.cactionTypeDeclaration(package_name, parentClass_name + "." + class_name, 0, n.superClass() != null, false, flags.isInterface(),
                false, flags.isAbstract(), flags.isFinal(), flags.isPrivate(), flags.isPublic(), flags.isProtected(), 
                flags.isStatic(), false, RoseTranslator.createJavaToken(n, class_name));
        
        List<PropertyDecl> propList = n.properties();
        for (PropertyDecl prop : propList) {
            visitChild(prop, prop.type());
            JNI.cactionAppendProperty(prop.name().id().toString(), prop.type().type().isRail(), 
                                        prop.flags().flags().isFinal(), RoseTranslator.createJavaToken());
        }
        if (propList.size() > 0)
            JNI.cactionSetProperties(propList.size(), RoseTranslator.createJavaToken());
        
        JNI.cactionPushNestedClass(((package_name.length() == 0)? "" : package_name + ".") + parentClass_name + "." + class_name, 
                                    ((package_name.length() == 0)? "" : package_name + ".") + parentClass_name);
        
        if (RoseTranslator.DEBUG) System.out.println("Previsit class decl end");
    }

    public void previsit(X10ConstructorDecl_c n, String package_name, String class_name) {
        toRose(n, "Previsit constructor decl: ", n.name().id().toString());
        List<Formal> formals = n.formals();
        String method_name = n.name().id().toString();
        StringBuffer param = new StringBuffer();
        for (Formal f : n.formals()) {
            param.append(f.type().toString().toLowerCase());
        }
        
//        RoseTranslator.memberMap = RoseTranslator.classMemberMap.get(JNI.cactionGetCurrentClassName());
        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + method_name + "(" + param + ")");

        List<TypeParamNode> typeParamList = n.typeParameters();

        JNI.cactionBuildMethodSupportStart(method_name, method_index, RoseTranslator.createJavaToken(n, method_name + "(" + param + ")")
        // new JavaToken(method_name+"("+param+")", new
        // JavaSourcePositionInformation(n.position().startOf().line(),
        // n.position().endLine()))
        );

        JNI.cactionTypeReference("", "void", this, RoseTranslator.createJavaToken());
        visitChildren(n, n.formals());

        JNI.cactionBuildMethodSupportEnd(method_name, method_index, false, false, false, 0, formals.size(), true, RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));
        
        if (n.guard() != null) {
            String guard = "";
            List<Expr> cond = n.guard().condition();
            for (int i = 0; i < cond.size(); ++i) {
                if (i > 0)
                    guard += ", ";
                Expr exp = cond.get(i);
                if (exp instanceof IsRefTest) {
                    guard += ((IsRefTest)exp).firstChild() + " isref";
                } else if (exp instanceof HasZeroTest_c) {
                    guard += ((HasZeroTest_c)exp).firstChild() + " haszero";
                } else {
                    throw new RuntimeException("Unsupported guard: " + exp);
                }
            }
            JNI.cactionAttachGuard(guard, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        }
        for (int i = 0; i < typeParamList.size(); ++i) { 
            TypeParamNode node = typeParamList.get(i);
            Variance variance = node.type().getVariance();
            String typeParam = typeParamList.get(i).name().toString(); 
//            System.out.println("TypeParam=" + typeParam);
            JNI.cactionAttachTypeParameterToMethodDecl(typeParam, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        }
        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        for (AnnotationNode a : annotations) 
            visitChild(n, a.annotationType());
        JNI.cactionAttachAnnotationsToMethodDecl(annotations.size(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
        
        toRose(n, "Previsit constructor decl end: ", method_name);
    }

    public void previsit(X10FieldDecl_c fieldDecl, String class_name) {
        String fieldName = fieldDecl.name().id().toString();
        toRose(fieldDecl, "Previsit field decl: ", fieldName);
        String package_name = "";
        QName qname;
        if (    (qname = fieldDecl.type().type().fullName()) != null
             && qname.qualifier() != null)
            package_name = qname.qualifier().toString();     
        String type_name = fieldDecl.type().toString();
        
        visit((X10CanonicalTypeNode_c)fieldDecl.type());
        JNI.cactionBuildFieldSupport(fieldName, RoseTranslator.createJavaToken());

        Flags flags = fieldDecl.flags().flags();

        // boolean hasInitializer = (fieldDecl.init() != null);
        // if (hasInitializer) {
        // // JNI.cactionAssignment(RoseTranslator.createJavaToken(fieldDecl,
        // fieldDecl.toString()));
        // visitChild(fieldDecl, fieldDecl.init());
        // // JNI.cactionSingleNameReference("", "", fieldName,
        // createJavaToken(fieldDecl, fieldDecl.name().id().toString()));
        //
        // // JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(fieldDecl,
        // fieldDecl.toString()));
        //
        // // int field_index = memberMap.get(fieldName);
        // //
        // // JNI.cactionBuildInitializerSupport(flags.isStatic(),
        // fieldDecl.init().toString()/*fieldName*/,
        // // field_index,
        // // createJavaToken());
        // // JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(fieldDecl,
        // fieldDecl.toString()));
        // }

        // MH-20140401
        // needs to invoke cactionTypeReference again, since
        // cactionFieldDeclarationEnd
        // first pop SgType for "!is_enum_field"
        // JNI.cactionTypeReference("", fieldDecl.type().nameString());

        JNI.cactionFieldDeclarationEnd(fieldName, false, false,// hasInitializer,
                flags.isFinal(), flags.isPrivate(), flags.isProtected(), flags.isPublic(), false, // java_is_volatile
                false, // java_is_synthetic
                flags.isStatic(), flags.isTransient(), RoseTranslator.createJavaToken());

        toRose(fieldDecl, "Previsit field decl end: ", fieldName);
    }

    public void visit(X10Formal_c n) {
        toRose(n, "formal: ", n, n.type().nameString(), n.type().toString());
        
        String[] names = getPackageAndTypeName(n.type().toString());
        String typeName = names[0] + "." + names[1];
        String formalName = n.name().toString();
        
        List<Formal> vars = n.vars();
        if (typeName.indexOf("x10.lang.Point") == 0) {
            typeName = "x10.lang.Point";
            for (int i = 0; i < vars.size(); ++i) {
                JNI.cactionTypeReference("", "long", this, RoseTranslator.createJavaToken());     
                JNI.cactionBuildArgumentSupport(vars.get(i).name().toString(), "", 0, true, RoseTranslator.createJavaToken());
            }
            JNI.cactionPushPackage("x10.lang", RoseTranslator.createJavaToken());
            JNI.cactionPopPackage();
            JNI.cactionTypeReference("x10.lang", "Point", this, RoseTranslator.createJavaToken());
        }
        else 
            visitChild(n, n.type());
        
        JNI.cactionBuildArgumentSupport(formalName, typeName, vars.size(),
                                        n.flags().flags().isFinal(),
                                        RoseTranslator.createJavaToken(n, n.name().id().toString()));
    }

    private void handleArgumentTypes(List<Expr> args) {
        for (int i = 0; i < args.size(); ++i) {
            Type t = args.get(i).type();
//            if (t instanceof FunctionType_c) {
//                FunctionType_c ft = (FunctionType_c) t;
//                System.out.println("PRINT type=" + ft.typeToString());
//            }
//            String arg_type_name = t.name().toString();
            String arg_type_name = t.toString();
            String full = t.fullName().toString();

            polyglot.types.Package arg_package_ = t.toPackage();
            String arg_package_name = "";
            if (arg_package_ != null)
                arg_package_name = arg_package_.toString();
            else {
                int lastDot = full.lastIndexOf('.');
                if (lastDot > 0)
                    arg_package_name = full.substring(0, lastDot);
            }
            if (RoseTranslator.DEBUG)
                System.out.println(i + ":" + t.fullName() + ", " + args.get(i) + ", pacakge=" + arg_package_name + ", type=" + arg_type_name);

            if (RoseTranslator.isX10Primitive(arg_package_name, arg_type_name))
                JNI.cactionTypeReference("", arg_type_name, this, RoseTranslator.createJavaToken());
            else if (full.equals("x10.lang.Rail") || full.equals("x10.util.GrowableRail")) {
                String railString = t.toString();
                String type = railString.substring(railString.indexOf('[') + 1, railString.indexOf(']'));
                String[] railTypeSplit = getPackageAndTypeName(type);
                arg_package_name = railTypeSplit[0];
                arg_type_name = railTypeSplit[1];

                if (RoseTranslator.isX10Primitive(arg_package_name, arg_type_name))
                    arg_package_name = "";

                if (arg_package_name.length() != 0) {
                    JNI.cactionPushPackage(arg_package_name, RoseTranslator.createJavaToken());
                    JNI.cactionPopPackage();
                }
                JNI.cactionTypeReference(arg_package_name, arg_type_name, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
            } else if (t instanceof FunctionType_c) {
                if (arg_package_name.length() != 0) {
                    JNI.cactionPushPackage(arg_package_name, RoseTranslator.createJavaToken());
                    JNI.cactionPopPackage();
                }
                JNI.cactionTypeReference(arg_package_name, arg_type_name, this, RoseTranslator.createJavaToken());
            } else {
                String[] names = getPackageAndTypeName(full);
                arg_package_name = names[0];
                arg_type_name = names[1];
                if (arg_package_name.length() != 0) {
                    JNI.cactionPushPackage(arg_package_name, RoseTranslator.createJavaToken());
                    JNI.cactionPopPackage();
                }
                JNI.cactionTypeReference(arg_package_name, arg_type_name, this, RoseTranslator.createJavaToken());
            }
        }
    }

    // MH-20140917
    public void visit(X10Loop_c n) {
        toRose(n, "X10Loop: ", n.toString());

    }

    public void visit(X10Call_c n) {
        toRose(n, "x10call: ", n.name().id().toString(), n.typeArguments());
        if (RoseTranslator.DEBUG)
            System.out.println("n=" + n.name().id() + ", type=" + n.target().type() + ", package=" + n.target().type().toPackage());
        String func_name = n.name().id().toString();
        String class_name = n.target().type().toString();
        polyglot.types.Package package_ = n.target().type().toPackage();
        String full = n.target().type().fullName().toString();
        String target_type_name = n.target().type().name().toString();

        if (func_name.equals("operator()")) {
            if (n.target().type().isRail()) {
                visitChild(n, n.target());
                List<Expr> args = n.arguments();
                visitChildren(n, args);
                JNI.cactionArrayReferenceEnd(RoseTranslator.createJavaToken(n, n.toString()));
                return;
            }           
            else {
                if (RoseTranslator.DEBUG)
                    System.out.println("Currently eliminate \"operator()\"");
                // visitChild(n, n.target());
//                func_name = n.target().toString();
                full = full.replaceAll("operator()", "");
            }
        } else if (func_name.equals("implicit_operator_as") || func_name.equals("operator_as")) {
            visitChild(n, n.target());
            visitChildren(n, n.arguments());
            JNI.cactionCastExpressionEnd(RoseTranslator.createJavaToken(n, n.toString()));

            return;
        }
        if (class_name.equals("x10.lang.Long") 
                && (   func_name.equals("parse") 
                    || func_name.equals("parseLong"))
         || class_name.equals("x10.lang.Int")
                && (   func_name.equals("parse")
                    || func_name.equals("parseInt"))
         || class_name.equals("x10.lang.Double") 
                && (   func_name.equals("parse"))
                    || func_name.equals("parseDouble")
                ) {

            String callerClass = JNI.cactionGetCurrentClassName();

            String helperName = "X10RoseUtility_" + func_name;
            String returnType = "void";
            String methodName = class_name.replaceAll("\\.", "_") + "_" + func_name;
            String argType = "x10.lang.String";
            String argName = n.arguments().get(0).toString();
            if (RoseTranslator.DEBUG)
                System.out.println("ARG_Class=" + n.arguments().get(0).getClass());

            // MH-20140812 TODO: remove when operator is available
            argName = argName.replaceAll(".operator\\(\\)", "");

            int methodIndex = RoseTranslator.uniqMemberIndex++;

            JNI.cactionSetCurrentClassName(helperName);
            
            JNI.cactionPushPackage("", RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionInsertClassStart(helperName, false, false, false, false, RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionInsertClassEnd(helperName, RoseTranslator.createJavaToken(n, helperName));

            JNI.cactionBuildClassSupportStart(helperName, "", true, // a
                                                                    // user-defined
                                                                    // class?
                    false, false, false, false, RoseTranslator.createJavaToken(n, helperName));

            String[] typeParamNames = new String[0];
            String[] interfaceNames = new String[0];

            JNI.cactionBuildClassExtendsAndImplementsSupport(0, typeParamNames, false, "", 0, interfaceNames, RoseTranslator.createJavaToken(n, n.toString()));

            JNI.cactionBuildMethodSupportStart(methodName, methodIndex, RoseTranslator.createJavaToken(n, methodName));

            // build return type
            JNI.cactionTypeReference("", returnType, this, RoseTranslator.createJavaToken());

            // build an argument
            JNI.cactionPushPackage("x10.lang", RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionPopPackage();
            JNI.cactionTypeReference("x10.lang", "String", this, RoseTranslator.createJavaToken());
            JNI.cactionBuildArgumentSupport(argName, "", 0, false, RoseTranslator.createJavaToken(n, argName));

            JNI.cactionBuildMethodSupportEnd(methodName, methodIndex, 
                    false, false, false, 0, 1, true, /* user-defined-method */
                    RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));

            JNI.cactionBuildClassSupportEnd(helperName, 1, RoseTranslator.createJavaToken(n, helperName));

            JNI.cactionTypeDeclaration("", helperName, /* num_annotations */0, false, /* is_annotation_interface */false, false,
            /* is_enum */false, false, false, false, true, false, false, /* is_strictfp */false, RoseTranslator.createJavaToken(n, helperName));

            JNI.cactionSetCurrentClassName(callerClass);

            JNI.cactionMessageSend("", helperName, methodName, RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionTypeReference("", helperName, this, RoseTranslator.createJavaToken(n, helperName));

            visitChildren(n, n.arguments());
            handleArgumentTypes(n.arguments());

            JNI.cactionTypeReference("", helperName, this, RoseTranslator.createJavaToken());
            
            JNI.cactionMessageSendEnd(true, true, methodName, 1, 0, 1, RoseTranslator.createJavaToken(n, helperName));

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

        JNI.cactionMessageSend(package_name, target_type_name, func_name, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        visitChild(n, n.target());

        List<Expr> args = n.arguments();
        List<Expr> argTypes = new ArrayList<Expr>();

        for (int i = 0; i < args.size(); ++i) {
            Node n2 = args.get(i);
            visitChild(n2, n2.node());
        }

        System.out.println("PRINT args size=" + args.size());
        handleArgumentTypes(args);
        // for (int i = 0; i < args.size(); ++i) {
        // Type t = args.get(i).type();
        // String arg_type_name = t.name().toString();
        // String full = t.fullName().toString();
        //
        // polyglot.types.Package arg_package_ = t.toPackage();
        // String arg_package_name = "";
        // if (arg_package_ != null)
        // arg_package_name = arg_package_.toString();
        // else {
        // int lastDot = full.lastIndexOf('.');
        // if (lastDot > 0)
        // arg_package_name = full.substring(0, lastDot);
        // }
        // if (RoseTranslator.DEBUG) System.out.println(i + ":" + t.fullName() + ", " +
        // args.get(i) + ", pacakge=" + arg_package_name + ", type=" +
        // arg_type_name);
        //
        // if (arg_package_name.equals("x10.lang")
        // && ( arg_type_name.equals("Boolean")
        // || arg_type_name.equals("Byte")
        // || arg_type_name.equals("Char")
        // || arg_type_name.equals("Int")
        // || arg_type_name.equals("Short")
        // || arg_type_name.equals("Float")
        // || arg_type_name.equals("Long")
        // || arg_type_name.equals("Double"))) {
        // JNI.cactionTypeReference("", arg_type_name, this,
        // createJavaToken());
        // }
        // else if ( full.equals("x10.lang.Rail")
        // || full.equals("x10.util.GrowableRail")) {
        // String railString = t.toString();
        // String type = railString.substring(railString.indexOf('[')+1,
        // railString.indexOf(']'));
        // int lastDot = type.lastIndexOf(".");
        // arg_package_name = type.substring(0, lastDot);
        // arg_type_name = type.substring(lastDot+1);
        //
        // if (arg_package_name.equals("x10.lang")
        // && ( arg_type_name.equals("Boolean")
        // || arg_type_name.equals("Byte")
        // || arg_type_name.equals("Char")
        // || arg_type_name.equals("Int")
        // || arg_type_name.equals("Short")
        // || arg_type_name.equals("Float")
        // || arg_type_name.equals("Long")
        // || arg_type_name.equals("Double"))) {
        // arg_package_name = "";
        // }
        //
        // if (arg_package_name.length() != 0) {
        // JNI.cactionPushPackage(arg_package_name, RoseTranslator.createJavaToken(n,
        // arg_package_name));
        // JNI.cactionPopPackage();
        // }
        // JNI.cactionTypeReference(arg_package_name, arg_type_name, this,
        // createJavaToken());
        // JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
        // }
        // else
        // JNI.cactionTypeReference(arg_package_name, arg_type_name, this,
        // createJavaToken());
        // }

        if (RoseTranslator.isX10Primitive(package_name, target_type_name))
            JNI.cactionTypeReference("", target_type_name, this, RoseTranslator.createJavaToken());
        else
            JNI.cactionTypeReference(package_name, target_type_name, this, RoseTranslator.createJavaToken());
        // JNI.cactionTypeReference(package_name, target_type_name, this,
        // createJavaToken());

        int num_parameters = n.arguments().size();
        int numTypeArguments = n.typeArguments().size();
        int numArguments = n.arguments().size();
        JNI.cactionMessageSendEnd(n.methodInstance().flags().isStatic(), n.target() != null, func_name, num_parameters, numTypeArguments, numArguments, RoseTranslator.createJavaToken(n, n.name().id().toString()));
        
        List<TypeNode> typeParamList = n.typeArguments();
        for (TypeNode type_node : typeParamList) {
            JNI.cactionAttachTypeParameterToMethodCall(type_node.nameString(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
        }
        toRose(n, "x10call end: ", n.name().id().toString());
    }

    public void visit(X10ConstructorDecl_c n) {
        toRose(n, "X10ConstructorDecl:", n.name().id().toString());
        if (n.body() == null || n.position().isCompilerGenerated())
            return;

        List<Formal> formals = n.formals();
//        processAnnotation(n);
        String method_name = n.name().id().toString();
        StringBuffer param = new StringBuffer();
        for (Formal f : n.formals()) {
            param.append(f.type().toString().toLowerCase());
        }


        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + method_name + "(" + param + ")");
        JNI.cactionMethodDeclaration(n.name().id().toString(), method_index, RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));
        Flags flags = n.flags().flags();
        JNI.cactionMethodDeclarationHeader(method_name, flags.isAbstract(), flags.isNative(), flags.isStatic(), flags.isFinal(), /* java_is_synchronized */false, flags.isPublic(), flags.isProtected(), flags.isPrivate(), /* java_is_strictfp */false, n.typeParameters().size(), formals.size(), n.throwsTypes().size(), RoseTranslator.createJavaToken(n, method_name));
        visitChild(n, n.body());
//        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
//        JNI.cactionMethodDeclarationEnd(annotations.size(), n.body().statements().size(), RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));
        JNI.cactionMethodDeclarationEnd(0, n.body().statements().size(), RoseTranslator.createJavaToken(n, method_name + "(" + param + ")"));
        toRose(n, "X10ConstructorDecl end:", n.name().id().toString());
    }

    public void visit(X10ConstructorCall_c n) {
        /* 
         * n.target() is null at this traversal moment
         */
        toRose(n, "X10ConstructorCall:", n, n.target());
       
        JNI.cactionExplicitConstructorCall(RoseTranslator.createJavaToken(n, n.toString()));
        visitChildren(n, n.typeArguments());
        visitChildren(n, n.arguments());
        /*
         * Information about package, type, and constructor index 
         * is not given to the following JNI interface. 
         */
        JNI.cactionExplicitConstructorCallEnd(false, n.kind()==Kind.SUPER,
                                              n.qualifier()!=null, "", "",
                                              0, n.typeArguments().size(),
                                              n.arguments().size(),
                                              RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Block_c n) {
        toRose(n, "Block: ", n.toString(), n.statements().size());
//        processAnnotation(n);
//        System.out.println("1215: firstChild=" + n.firstChild());
        JNI.cactionBlock(RoseTranslator.createJavaToken(n, n.toString()));
        visitChildren(n, n.statements());
        JNI.cactionBlockEnd(n.statements().size(), RoseTranslator.createJavaToken(n, n.toString()));
        toRose(n, "Block end: ", n.toString());
    }

    public void visit(StmtSeq_c n) {
        toRose(n, "StmtSeq: ");
        visitChildren(n, n.statements());
    }

    /**
     * Currently, property calls are created at unparser phase in ROSE.
     * Arguments of a property call are constructed by referring to
     * the parameters of the property declaration.
     * 
     * For details, see edg4x-rose:
     * Unparse_X10::unparseMFuncDeclStmt(SgStatement* stmt, SgUnparse_Info& info) in 
     * src/backend/unparser/X10CodeGeneration/unparseX10_statement.C
     */
    public void visit(AssignPropertyCall_c n) {
        toRose(n, "AssignPropertyCall:", n, n.arguments().size());
        JNI.cactionEmptyStatement(RoseTranslator.createJavaToken(n, n.toString()));
        JNI.cactionEmptyStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
        toRose(n, "AssignPropertyCall end:", n, n.arguments().size());
    }

    public void visit(Empty_c n) {
        toRose(n, "Empty:");
        JNI.cactionEmptyStatement(RoseTranslator.createJavaToken(n, n.toString()));
        JNI.cactionEmptyStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
        toRose(n, "Empty end:");
    }

    public void visit(ArrayTypeNode_c n) {
        toRose(n, "ArrayTypeNode:", n.nameString());
    }

    public void visit(ArrayAccess_c n) {
        toRose(n, "ArrayAccess:", n.toString());
    }
    
    private String[] getPackageAndTypeName(String class_name) {
        String package_name = "";
        String type_name = "";
        String[] package_name_ifAny = new String[1];
                
        if (RoseTranslator.isNestedClass(class_name, package_name_ifAny)) {
            package_name = package_name_ifAny[0];
            if (package_name.length() == 0)
                type_name = class_name;
            else
                type_name = class_name.substring(package_name.length()+1);
            return new String[]{package_name, type_name};
        }
        
        for (String package_ : RoseTranslator.package_traversed) {
            if (package_.length() == 0)
                break;
            
            if (class_name.indexOf(package_) == 0 && class_name.length() != package_.length()) {
                package_name = package_;
                type_name = class_name.substring(package_name.length()+1);            
                // reset package name if type name is ""
                if (type_name.length() == 0) 
                    package_name = "";
                break;
            }
        }
        
        if (package_name.length() == 0 && type_name.length() == 0) {
            int index = class_name.indexOf("[");
            if (index == 0)
                throw new RuntimeException("Unexpected token: brace was found in the first character");
            
            if (index < 0) {
                index = class_name.indexOf("(\""); // whether annotation or not
                if (index > 0)
                    return new String[]{"", class_name};
                else if (index < 0) 
                    index = class_name.length();
                else 
                    throw new RuntimeException("Unexpected token: `(` was found in the first character");
            }
            int lastDot = class_name.lastIndexOf('.', index);
            
            if (lastDot == 0)
                throw new RuntimeException("Unexpected token: dot was found in the first character");
            
            if (lastDot > 0) {
                package_name = class_name.substring(0, lastDot);
                type_name = class_name.substring(lastDot+1);
            } else {
                type_name = class_name;
            }
        }
        return new String[]{package_name, type_name};
    }
    
    private void visitType(String class_name, String class_name_full, String class_name_short, Node_c n) {
        if (RoseTranslator.isX10Primitive(class_name)) {
            String canonicalTypeName = class_name_short;
            JNI.cactionTypeReference("", canonicalTypeName, this, RoseTranslator.createJavaToken());
        } else if (class_name_full.indexOf("=>") > 0) {
            JNI.cactionTypeReference("", class_name_full, this, RoseTranslator.createJavaToken(n, n.toString()));
        } else if (    class_name_full.indexOf("x10.lang.Rail[") == 0 
                    || class_name_full.indexOf("x10.util.GrowableRail[") == 0) {
            String railString = class_name_full;
            class_name = railString.substring(railString.indexOf('[') + 1, railString.lastIndexOf(']'));
            String[] names;
            String package_name = "";
            String type_name = "";
            int dim = 1;
            while (true) {
                names = getPackageAndTypeName(class_name);
                package_name = names[0];
                type_name = names[1];
                if ( ! (package_name.equals("x10.lang") && type_name.indexOf("Rail[") == 0
                        ||  package_name.equals("x10.util") && type_name.indexOf("GrowableRail[") == 0)) {
                    class_name = type_name;
                    break;
                }
                class_name = type_name.substring(type_name.indexOf('[') + 1, type_name.lastIndexOf(']'));
                ++dim;
            }
            if (RoseTranslator.isX10Primitive(class_name))
                package_name = "";
            if (package_name.length() != 0) {
                JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, package_name));
                JNI.cactionPopPackage();
            }
            JNI.cactionTypeReference(package_name, type_name, this, RoseTranslator.createJavaToken());
            JNI.cactionArrayTypeReference(dim, RoseTranslator.createJavaToken());
        } else if (class_name_full.indexOf("{self") >= 0) {
            class_name = class_name_full;
            int index = class_name.indexOf("[");
            if (index >= 0)
                class_name = class_name.substring(0, index);

            index = class_name.indexOf("{");
            if (index >= 0)
                class_name = class_name.substring(0, index);
            
            String[] names = getPackageAndTypeName(class_name);
            String package_name = names[0];
            String type_name = names[1];
            JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, package_name));
            JNI.cactionPopPackage();
            JNI.cactionTypeReference(package_name, type_name, this, RoseTranslator.createJavaToken());
        }         
        else {
            class_name = class_name_full;
            String[] names = getPackageAndTypeName(class_name);
            String package_name = names[0];
            String type_name = names[1];
            // MH-20141205
            int index = type_name.indexOf("[");
            String typeParam = "";
            if (index > 0) {
                typeParam = type_name.substring(index);
            }
            
            if (RoseTranslator.DEBUG) System.out.println("className=" + class_name + ", pkg=" + package_name + ", type=" + type_name);
            if (package_name.length() != 0) {
                JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, package_name));
                JNI.cactionPopPackage();
            } else {
                try {
                    TypeSystem ts = RoseTranslator.jobList.get(RoseTranslator.jobList.size() - 1).extensionInfo().typeSystem();
                    boolean isFoundInPackageRef = false;
                    for (String package_ : package_ref) {
                        List<Type> list = new ArrayList<Type>();
                        try {
                            list = ts.systemResolver().find(QName.make(package_ + "." + type_name));
                        } catch (polyglot.types.NoClassException e) {
                            if (RoseTranslator.DEBUG)
                                System.out.println(package_ + "." + type_name + " not found: " + e);
                        }
                        if (list.size() != 0) {
                            Type t = list.get(0);
                            JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(n, n.toString()));
                            JNI.cactionPopPackage();
                            JNI.cactionTypeReference(package_, type_name, this, RoseTranslator.createJavaToken(n, n.toString()));
                            // MH-20141205
                            if (index > 0) 
                              JNI.cactionAttachTypeParameterToType(typeParam, RoseTranslator.createJavaToken(n, n.toString()));
                            isFoundInPackageRef = true;
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
                                JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(n, n.toString()));
                                JNI.cactionPopPackage();
                                JNI.cactionTypeReference(package_, type2_, this, RoseTranslator.createJavaToken(n, n.toString()));
                                // MH-20141205
                                if (index > 0) 
                                  JNI.cactionAttachTypeParameterToType(typeParam, RoseTranslator.createJavaToken(n, n.toString()));
                                isFoundInPackageRef = true;
                                break;
                            }
                        } else if (import_.kind() == Import.PACKAGE) {
                            String package_ = import_.name().toString();
                            List<Type> list = new ArrayList<Type>();
                            try {
                                list = ts.systemResolver().find(QName.make(package_ + "." + type_name));
                            } catch (polyglot.types.NoClassException e) {
                                if (RoseTranslator.DEBUG)
                                    System.out.println(package_ + "." + type_name + " not found: " + e);
                            }
                            // List<Type> list =
                            // ts.systemResolver().find(QName.make(package_
                            // + "." + type));
                            if (list.size() != 0) {
                                Type t = list.get(0);
                                // String type_ = amb.name().toString();
                                JNI.cactionPushPackage(package_, RoseTranslator.createJavaToken(n, n.toString()));
                                JNI.cactionPopPackage();
                                JNI.cactionTypeReference(package_, type_name, this, RoseTranslator.createJavaToken(n, n.toString()));
                                // MH-20141205
                                if (index > 0) 
                                  JNI.cactionAttachTypeParameterToType(typeParam, RoseTranslator.createJavaToken(n, n.toString()));
                                isFoundInPackageRef = true;
                                break;
                            }
                        }
                    }

                    if (!isFoundInPackageRef) {
                        // treat as a generic type
                        JNI.cactionTypeReference("", type_name, this, RoseTranslator.createJavaToken(n, n.toString()));
                        // MH-20141205
                        if (index > 0) 
                          JNI.cactionAttachTypeParameterToType(typeParam, RoseTranslator.createJavaToken(n, n.toString()));
                    }
                } catch (SemanticException e) {
                    // treat as a generic type
                    JNI.cactionTypeReference("", type_name, this, RoseTranslator.createJavaToken(n, n.toString()));
                    // MH-20141205
                    if (index > 0) 
                      JNI.cactionAttachTypeParameterToType(typeParam, RoseTranslator.createJavaToken(n, n.toString()));
                }
                return;
            }

            JNI.cactionTypeReference(package_name, type_name, this, RoseTranslator.createJavaToken(n, n.toString()));

            // MH-20141205
            if (index > 0) 
              JNI.cactionAttachTypeParameterToType(typeParam, RoseTranslator.createJavaToken(n, n.toString()));

            // JNI.cactionPushPackage(pkg, RoseTranslator.createJavaToken(n, pkg));
            // JNI.cactionPushTypeScope(pkg, type, RoseTranslator.createJavaToken(n,
            // type));
            // JNI.cactionTypeReference(pkg, type, this, RoseTranslator.createJavaToken());
            // JNI.cactionPopTypeScope();
            // JNI.cactionPopPackage();
            // JNI.cactionTypeReference("", type, this, RoseTranslator.createJavaToken());
        }
        // JNI.cactionTypeDeclaration("", n.nameString(), false, false,
        // false, false, false, false, false, false, false, false);           
    }

    public void visit(X10CanonicalTypeNode_c n) {
        toRose(n, "X10CanonicalTypeNode:", n.type().fullName(), n.type(), n.nameString());
        String class_name = n.type().fullName().toString();
        String class_name_full = n.type().toString();
        String class_name_short = n.nameString();
        visitType(class_name, class_name_full, class_name_short, n);
    }

    public void visit(Return_c n) {
        toRose(n, "Return:", n.toString());
        JNI.cactionReturnStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        JNI.cactionReturnStatementEnd((n.expr() != null), RoseTranslator.createJavaToken(n, n.toString()));

        toRose(n, "Return end:", n.toString());
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
        if (n.operator() == Unary.Operator.PRE_DEC || n.operator() == Unary.Operator.PRE_INC) {
            JNI.cactionPrefixExpressionEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
        } else if (n.operator() == Unary.Operator.POST_DEC || n.operator() == Unary.Operator.POST_INC) {
            JNI.cactionPostfixExpressionEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
        }
        else {
            JNI.cactionUnaryExpressionEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
        }
    }

    public void visit(ParExpr_c n) { // parentheses
        toRose(n, "ParExpr:");
        visitChild(n, n.expr());
        JNI.cactionParenthesizedExpression(1);
    }

    public void visit(X10Special_c n) {
        toRose(n, "X10Special:", n.kind().toString(), n.type().name(), n.type().fullName());
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
                throw new RuntimeException("X10Special : Unhandled token " + kind);
        }
        toRose(n, "X10Special end:", n.kind().toString());
    }

    public void visit(Here_c n) {
        toRose(n, "Here:");
        JNI.cactionHere(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10Local_c n) {
        toRose(n, "X10Local :", n.name().id().toString());
        // JavaParser.cactionSingleNameReference(package_name, type_name, varRefName, javaParserSupport.createJavaToken(node));
        JNI.cactionSingleNameReference("", "", n.name().id().toString(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
        if (RoseTranslator.DEBUG)
            System.out.println("Leaving X10Local=====");
    }

    public void visit(Eval_c n) {
        toRose(n, "Eval:", n.toString());
        visitChild(n, n.expr());
        toRose(n, "Eval end:", n.toString());
    }

    public void visit(For_c n) {
        toRose(n, "For:");
        JNI.cactionForStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChildren(n, n.inits());
        visitChild(n, n.cond());
        visitChildren(n, n.iters());
        visitChild(n, n.body());
        JNI.cactionForStatementEnd(n.inits().size(), n.cond() != null, n.iters().size(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(ForLoop_c n) {
        toRose(n, "ForLoop:", n.toString(), n.domain().toString(), n.domain());
        JNI.cactionForeachStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.formal().type());
        JNI.cactionLocalDeclaration(0, n.formal().name().toString(), false, RoseTranslator.createJavaToken(n, n.formal().name().id().toString()));
        JNI.cactionLocalDeclarationEnd(n.formal().name().toString(), false, RoseTranslator.createJavaToken(n, n.formal().name().id().toString()));
        Expr domain = n.domain();

        if (domain instanceof X10Call_c) {
            X10Call_c call = (X10Call_c) n.domain();
            visit(call);
//            String class_name = call.target().toString();
//            int lastDot = class_name.lastIndexOf('.');
//            String pkg = "";
//            String type = "";
//            if (lastDot > 0) {
//                pkg = class_name.substring(0, lastDot);
//                type = class_name.substring(lastDot + 1);
//            } else
//                type = class_name;
//            
//            String func_name = call.name().toString();
//            JNI.cactionMessageSend(pkg, type, func_name, RoseTranslator.createJavaToken(n, class_name));
//            JNI.cactionTypeReference(pkg, type, this, RoseTranslator.createJavaToken(n, class_name));
//            visitChild(call, call.target());
//            visitChildren(n, call.arguments());
//            handleArgumentTypes(call.arguments());
//            JNI.cactionMessageSendEnd(false, true, func_name, call.arguments().size(), 0, call.arguments().size(), RoseTranslator.createJavaToken(n, class_name));
        } else if (domain instanceof X10Binary_c) {
            X10Binary_c bin = (X10Binary_c) domain;
            JNI.cactionBinaryExpression(RoseTranslator.createJavaToken(n, n.toString()));
            visitChild(n, bin.left());
            visitChild(n, bin.right());
            JNI.cactionBinaryExpressionEnd(RoseTranslator.getOperatorKind((Binary.Operator) bin.operator()), RoseTranslator.createJavaToken(bin, bin.toString()));
            if (RoseTranslator.DEBUG)
                System.out.println("BinaryExpressionEnd end");
        } else if (domain instanceof X10Local_c) {
            X10Local_c local = (X10Local_c) n.domain();
            JNI.cactionSingleNameReference("", "", local.name().id().toString(), RoseTranslator.createJavaToken(local, local.name().id().toString()));
        } else if (domain instanceof X10Field_c) {
            X10Field_c field = (X10Field_c) n.domain();
            visit(field);
        } else {
            throw new RuntimeException("No support for " + n.domain().getClass() + " in ForLoop");
        }

        visitChild(n, n.body());
        JNI.cactionForeachStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
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
        toRose(n, "X10Do:", n.cond().toString(), n.body().toString());
        JNI.cactionDoStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.body());
        visitChild(n, n.cond());
        JNI.cactionDoStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10While_c n) {
        toRose(n, "X10While:", n.cond().toString(), n.body().toString());
        JNI.cactionWhileStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.cond());
        visitChild(n, n.body());
        JNI.cactionWhileStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Tuple_c n) {
        toRose(n, "Tuple:", n);
        JNI.cactionTuple(RoseTranslator.createJavaToken(n, n.toString()));
        visitChildren(n, n.arguments());
        JNI.cactionTupleEnd(n.arguments().size(), RoseTranslator.createJavaToken(n, n.toString()));
        toRose(n, "Tuple end:", n);
    }

    public void visit(SettableAssign_c n) {
        toRose(n, "SettableAssign:", n, n.operator(), n.left(), n.index(), n.right().toString(), n.index().size());
        
        if (n.operator()==Assign.Operator.ASSIGN) {
            JNI.cactionAssignment(RoseTranslator.createJavaToken(n, n.toString()));
            visitChild(n, n.left());
            visitChild(n, n.right());
            JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString()));
        }
        else {
            JNI.cactionCompoundAssignment(RoseTranslator.createJavaToken(n, n.toString()));
            visitChild(n, n.left());
            visitChild(n, n.right());
            JNI.cactionCompoundAssignmentEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
        }
        
//        JNI.cactionAssignment(RoseTranslator.createJavaToken(n, n.toString()));
//        visitChild(n, n.left());
//        visitChild(n, n.right());
//        JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(FieldAssign_c n) {
        toRose(n, "FieldAssign:", n.name().id().toString());
        // if (RoseTranslator.DEBUG) System.out.println("target=" + n.target() + ", right="
        // + n.right());
        String fieldName = n.name().id().toString();
        String className = n.target().type().fullName().toString();
        int lastDot = className.lastIndexOf('.');
        String pkg = "";
        String type = "";
        if (lastDot >= 0) {
            pkg = className.substring(0, lastDot);
            type = className.substring(lastDot + 1);
        } else {
            type = className;
        }

        visitChild(n, n.target());
        JNI.cactionTypeReference(pkg, type, this, RoseTranslator.createJavaToken());
        JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, RoseTranslator.createJavaToken(n, fieldName));
        visitChild(n, n.right());
//        JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.right().toString()));
        
        if (n.operator()==Assign.Operator.ASSIGN) {
//            JNI.cactionAssignment(RoseTranslator.createJavaToken(n, n.toString()));
//            visitChild(n, n.left());
//            visitChild(n, n.right());
            JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString()));
        }
        else {
//            JNI.cactionCompoundAssignment(RoseTranslator.createJavaToken(n, n.toString()));
//            visitChild(n, n.left());
//            visitChild(n, n.right());
            JNI.cactionCompoundAssignmentEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
        }
        
        
        
        toRose(n, "FieldAssign end:", n.name().id().toString());
    }

    public void visit(X10Field_c n) {
        toRose(n, "X10Field:", n.name().id().toString());
        if (RoseTranslator.DEBUG)
            System.out.println("n=" + n + ", target=" + n.target() + ", target type=" + n.target().type().name() + ", target type's package=" + n.target().type().fullName() + ", name=" + n.name().id() + ", instance=" + n.fieldInstance());
        String fieldName = n.name().id().toString();
        String className = n.target().type().fullName().toString();
        int lastDot = className.lastIndexOf('.');
        String[] names = getPackageAndTypeName(className);
        String pkg = names[0];
        String type = names[1];

        if (   className.equals("x10.lang.Rail") && fieldName.equals("size")) {
                String callerClass = JNI.cactionGetCurrentClassName();

                String helperName = "X10RoseUtility_" + fieldName;
                String typeName = n.type().toString();
                int index = typeName.indexOf("{");
                if (index >= 0)
                    typeName = typeName.substring(0, index);
                String returnType = /*"Long";*/getPackageAndTypeName(typeName)[1];
                String methodName = /*"Rail_size";*/className.replaceAll("\\.", "_") + "_" + fieldName;            
                String argClass = n.target().type().toClass().typeArguments().get(0).toString();
                String argClass_package = argClass.substring(0, argClass.lastIndexOf('.'));
                String argClass_type = argClass.substring(argClass.lastIndexOf('.') + 1);
                String argName = n.target().toString();
                int methodIndex = RoseTranslator.uniqMemberIndex++;

                JNI.cactionSetCurrentClassName(helperName);

                JNI.cactionPushPackage("", RoseTranslator.createJavaToken(n, helperName));
                JNI.cactionInsertClassStart(helperName, false, false, false, false, RoseTranslator.createJavaToken(n, helperName));
                JNI.cactionInsertClassEnd(helperName, RoseTranslator.createJavaToken(n, helperName));

                JNI.cactionBuildClassSupportStart(helperName, "", true,
                        false, false, false, false, RoseTranslator.createJavaToken(n, helperName));

                String[] typeParamNames = new String[0];
                String[] interfaceNames = new String[0];

                JNI.cactionBuildClassExtendsAndImplementsSupport(0, typeParamNames, false, "", 0, interfaceNames, RoseTranslator.createJavaToken(n, n.toString()));

                JNI.cactionBuildMethodSupportStart(methodName, methodIndex, RoseTranslator.createJavaToken(n, methodName));

                // build a return type
                JNI.cactionTypeReference("", returnType, this, RoseTranslator.createJavaToken());

                JNI.cactionPushPackage(argClass_package, RoseTranslator.createJavaToken(n, argClass_package));
                JNI.cactionPopPackage();
                    
                JNI.cactionTypeReference(argClass_package, argClass_type, this, RoseTranslator.createJavaToken());
                JNI.cactionArrayTypeReference(1, RoseTranslator.createJavaToken());
                JNI.cactionBuildArgumentSupport(argName, "", 0, false, RoseTranslator.createJavaToken(n, argName));

                JNI.cactionBuildMethodSupportEnd(methodName, methodIndex,
                        false, false, false, 0, 1, true,
                        RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));

                JNI.cactionBuildClassSupportEnd(helperName, 1, RoseTranslator.createJavaToken(n, helperName));

                JNI.cactionTypeDeclaration("", helperName, /* num_annotations */0, false, /* is_annotation_interface */false, false,
                /* is_enum */false, false, false, false, true, false, false, /* is_strictfp */false, RoseTranslator.createJavaToken(n, helperName));

                JNI.cactionSetCurrentClassName(callerClass);

                JNI.cactionMessageSend("", helperName, methodName, RoseTranslator.createJavaToken(n, helperName));
                JNI.cactionTypeReference("", helperName, this, RoseTranslator.createJavaToken(n, helperName));

                visitChild(n, n.target());
                List<Expr> arglist = new ArrayList<Expr>();
                arglist.add((Expr) n.target());
                handleArgumentTypes(arglist);

                JNI.cactionTypeReference("", helperName, this, RoseTranslator.createJavaToken());

                JNI.cactionMessageSendEnd(true, true, methodName, 1, 0, 1, RoseTranslator.createJavaToken(n, helperName));
                
                return;
        }
        else if (className.equals("x10.lang.Int")  && fieldName.equals("MIN_VALUE")) {
            String callerClass = JNI.cactionGetCurrentClassName();

            String helperName = "X10RoseUtility_" + fieldName;
            String typeName = n.type().toString();
            int index = typeName.indexOf("{");
            if (index >= 0)
                typeName = typeName.substring(0, index);
            String[] packageAndType = getPackageAndTypeName(typeName);
            String package_ = packageAndType[0];
            String returnType = /*"Long";*/packageAndType[1];
            String methodName = /*"Rail_size";*/className.replaceAll("\\.", "_") + "_" + fieldName;            
            int methodIndex = RoseTranslator.uniqMemberIndex++;
            
            JNI.cactionSetCurrentClassName(helperName);

            JNI.cactionPushPackage("", RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionInsertClassStart(helperName, false, false, false, false, RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionInsertClassEnd(helperName, RoseTranslator.createJavaToken(n, helperName));

            JNI.cactionBuildClassSupportStart(helperName, "", true,
                    false, false, false, false, RoseTranslator.createJavaToken(n, helperName));

            String[] typeParamNames = new String[0];
            String[] interfaceNames = new String[0];

            JNI.cactionBuildClassExtendsAndImplementsSupport(0, typeParamNames, false, "", 0, interfaceNames, RoseTranslator.createJavaToken(n, n.toString()));

            JNI.cactionBuildMethodSupportStart(methodName, methodIndex, RoseTranslator.createJavaToken(n, methodName));

            // build a return type
            JNI.cactionTypeReference("", returnType, this, RoseTranslator.createJavaToken());

            // No need to build an argument
            
            JNI.cactionBuildMethodSupportEnd(methodName, methodIndex,
                    false, false, false, 0, 0, true,
                    RoseTranslator.createJavaToken(n, n.name().id().toString()), RoseTranslator.createJavaToken(n, n.name().id().toString() + "_args"));

            JNI.cactionBuildClassSupportEnd(helperName, 1, RoseTranslator.createJavaToken(n, helperName));
            
            JNI.cactionTypeDeclaration("", helperName, /* num_annotations */0, false, /* is_annotation_interface */false, false,
            /* is_enum */false, false, false, false, true, false, false, /* is_strictfp */false, RoseTranslator.createJavaToken(n, helperName));
            
            JNI.cactionSetCurrentClassName(callerClass);

            JNI.cactionMessageSend("", helperName, methodName, RoseTranslator.createJavaToken(n, helperName));
            JNI.cactionTypeReference("", helperName, this, RoseTranslator.createJavaToken(n, helperName));

            JNI.cactionTypeReference("", helperName, this, RoseTranslator.createJavaToken());
            
            JNI.cactionMessageSendEnd(true, true, methodName, 0, 0, 0, RoseTranslator.createJavaToken(n, helperName));
            
            return;
        }
        visitChild(n, n.target());

//        if (RoseTranslator.isX10Primitive(pkg, type)) {
//            JNI.cactionTypeReference("", type, this, RoseTranslator.createJavaToken());
//        } 
//        else {
            JNI.cactionPushPackage(pkg, RoseTranslator.createJavaToken(n, type));
            JNI.cactionPopPackage();
            JNI.cactionTypeReference(pkg, type, this, RoseTranslator.createJavaToken());
//        }

        // JNI.cactionTypeReference(pkg, type, this, RoseTranslator.createJavaToken());
        // JNI.cactionQualifiedTypeReference(pkg, type, RoseTranslator.createJavaToken(n,
        // type));
        JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, RoseTranslator.createJavaToken(n, fieldName));
        if (RoseTranslator.DEBUG)
            System.out.println("Leaving X10Field=====");
    }

    public void visit(X10FieldDecl_c n) {
        toRose(n, "X10FieldDecl:", n.name().id().toString());
        
        if (n.position().isCompilerGenerated())
            return;
        
        // visitChild(n, n.type());
        // visitChild(n, n.init());

        String fieldName = n.name().id().toString();
        // String package_name =
        // n.type().type().fullName().qualifier().toString();
        // String type_name = n.type().nameString();
        //
        // // if (package_name.equals("x10.lang") &&
        // // ( type_name.equals("boolean") || type_name.equals("Boolean")
        // // || type_name.equals("byte") || type_name.equals("Byte")
        // // || type_name.equals("char") || type_name.equals("Char")
        // // || type_name.equals("int") || type_name.equals("Int")
        // // || type_name.equals("short") || type_name.equals("Short")
        // // || type_name.equals("float") || type_name.equals("Float")
        // // || type_name.equals("long") || type_name.equals("Long")
        // // || type_name.equals("double") || type_name.equals("Double")))
        // {
        // // JNI.cactionTypeReference("", type_name, this,
        // createJavaToken());
        // // }
        // // else if (package_name.length() != 0) {
        // // JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n,
        // type_name));
        // // JNI.cactionPopPackage();
        // // JNI.cactionTypeReference(package_name, type_name, this,
        // createJavaToken());
        // // }
        // // else
        // // JNI.cactionTypeReference(package_name, type_name, this,
        // createJavaToken());
        //
        // JNI.cactionBuildFieldSupport(fieldName, RoseTranslator.createJavaToken());

        Flags flags = n.flags().flags();

        boolean hasInitializer = (n.init() != null);
        if (hasInitializer) {
            // JNI.cactionAssignment(RoseTranslator.createJavaToken(fieldDecl,
            // fieldDecl.toString()));
            // visitChild(n, n.type());
            visitChild(n, n.init());
            // JNI.cactionSingleNameReference("", "", fieldName,
            // createJavaToken(fieldDecl,
            // fieldDecl.name().id().toString()));

            // JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(fieldDecl,
            // fieldDecl.toString()));

            // int field_index = memberMap.get(fieldName);
            //
            // JNI.cactionBuildInitializerSupport(flags.isStatic(),
            // fieldDecl.init().toString()/*fieldName*/,
            // field_index,
            // createJavaToken());
            // JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(fieldDecl,
            // fieldDecl.toString()));
        }

        JNI.cactionFieldDeclarationEnd(fieldName, false, // is_enum_field
                hasInitializer, flags.isFinal(), flags.isPrivate(), flags.isProtected(), flags.isPublic(), false, // java_is_volatile
                false, // java_is_synthetic
                flags.isStatic(), flags.isTransient(), RoseTranslator.createJavaToken());
    }

    public void visit(X10LocalDecl_c n) {
        toRose(n, "X10LocalDecl:", n.name().id().toString());
        Expr init = n.init();
//        visitChild(n, n.type());
        // MH-20141007
        String package_name = "";
        QName qname;
        if (    (qname = n.type().type().fullName()) != null
             && qname.qualifier() != null)
            package_name = qname.qualifier().toString();
        visitChild(n, n.type());
        /*
         * 
         * if (init != null) JNI.cactionAssignment(RoseTranslator.createJavaToken(n,
         * n.toString()));
         * 
         * JNI.cactionLocalDeclaration(0, n.name().id().toString(),
         * n.flags().flags().isFinal(), RoseTranslator.createJavaToken(n,
         * n.name().id().toString())); if (init == null)
         * JNI.cactionLocalDeclarationEnd(n.name().id().toString(), false,
         * createJavaToken(n, n.name().id().toString()));
         * 
         * if (init != null) { JNI.cactionSingleNameReference("", "",
         * n.name().id().toString(), RoseTranslator.createJavaToken(n,
         * n.name().id().toString())); visitChild(n, init);
         * 
         * JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString())); }
         */
        if (init != null)
            JNI.cactionAssignment(RoseTranslator.createJavaToken(n, n.toString()));

        JNI.cactionLocalDeclaration(0, n.name().id().toString(), n.flags().flags().isFinal(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
        if (init != null) {
            // JNI.cactionSingleNameReference("", "",
            // n.name().id().toString(), RoseTranslator.createJavaToken(n,
            // n.name().id().toString()));

            visitChild(n, init);
            // JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString()));
        }
        JNI.cactionLocalDeclarationEnd(n.name().id().toString(), init != null, RoseTranslator.createJavaToken(n, n.name().id().toString()));
   
        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        for (AnnotationNode a : annotations)
            visitChild(n, a.annotationType());
        JNI.cactionAttachAnnotationsToLocalDecl(annotations.size(), RoseTranslator.createJavaToken(n, n.name().id().toString()));
    }

    public void visit(PropertyDecl_c n) {
        toRose(n, "PropertyDecl:", n.name().id());
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
        JNI.cactionConditionalExpression(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.cond());
        visitChild(n, n.consequent());
        visitChild(n, n.alternative());
        JNI.cactionConditionalExpressionEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Assert_c n) {
        toRose(n, "Assert:", n, n.cond(), n.errorMessage());
        JNI.cactionAssertStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.cond());
        visitChild(n, n.errorMessage());
        JNI.cactionAssertStatementEnd(n.errorMessage() != null, RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Throw_c n) {
        toRose(n, "Throw:");
        JNI.cactionThrowStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        JNI.cactionThrowStatementEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Try_c n) {
        toRose(n, "Try:");
        JNI.cactionTryStatement(n.catchBlocks().size(), n.finallyBlock() != null, RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.tryBlock());
        visitChildren(n, n.catchBlocks());
        visitChild(n, n.finallyBlock());
        JNI.cactionTryStatementEnd(0, n.catchBlocks().size(), n.finallyBlock() != null, RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Catch_c n) {
        toRose(n, "Catch: ", n.formal().name());
        JNI.cactionCatchArgument(n.formal().name().toString(), RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.formal().type());
        JNI.cactionCatchArgumentEnd(0, n.formal().name().toString(), 1, true, RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.body());
        JNI.cactionCatchBlockEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Labeled_c n) {
        toRose(n, "Labeled: ***caution: NOT insert a JNI function yet***", n.labelNode().id().toString());
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
        toRose(n, "ClassLit: ***caution: NOT insert a JNI function yet***");
        visitChild(n, n.typeNode());
    }

    public void visit(X10FloatLit_c n) {
        toRose(n, "X10FloatLit:", Double.toString(n.value()));
        JNI.cactionFloatLiteral(new Float(n.value()), "" + n.value(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(NullLit_c n) {
        toRose(n, "NullLit:");
        JNI.cactionNullLiteral(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10CharLit_c n) {
        toRose(n, "X10CharLit:", "" + n.value());
        JNI.cactionCharLiteral(n.value(), RoseTranslator.createJavaToken(n, n.toString()));
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
        if (RoseTranslator.DEBUG)
            System.out.println("End of IntLit");
    }

    public void visit(X10StringLit_c n) {
        toRose(n, "X10StringLit:", StringUtil.escape(n.value()));
        JNI.cactionStringLiteral(StringUtil.escape(n.value()), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Finish_c n) {
        toRose(n, "Finish:");        
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
        toRose(n, "AtStmt:", n.place().toString(), n.body().toString());        
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
        toRose(n, "AtExpr:", n.place().toString(), n.body().toString());
        JNI.cactionAtExpr(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.place());
        visitChild(n, n.body());
        JNI.cactionAtExprEnd(RoseTranslator.createJavaToken(n, n.toString()));
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
        toRose(n, "Async:");
        JNI.cactionAsync(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.body());
        JNI.cactionAsyncEnd(n.clocked(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Atomic_c n) {
        toRose(n, "Atomic:");
        JNI.cactionAtomic(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.body());
        JNI.cactionAtomicEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(When_c n) {
        toRose(n, "When:");
        JNI.cactionWhen(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        visitChild(n, n.stmt());
        JNI.cactionWhenEnd(RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(X10New_c n) {
        toRose(n, "X10New: ", n.objectType().toString(), "n=" + n.toString(), "objectType=" + n.objectType(), "body=" + n.body(), n.objectType().type().isRail());
        boolean isRail = n.objectType().type().isRail();

        if (isRail) {
            JNI.cactionArrayAllocationExpression(RoseTranslator.createJavaToken(n, n.toString()));
            visitChildren(n, n.typeArguments());
            List<Expr> args = n.arguments();
            // for (int i = 0; i < args.size(); ++i)
            // if (RoseTranslator.DEBUG) System.out.println(i + " : " + args.get(i));
            // if (RoseTranslator.DEBUG) System.out.println("ArrayOf=" +
            // n.objectType().type().arrayOf());
            // visitChild(n, n.objectType());
            visitChildren(n, n.arguments());
            
            /**
             * Second parameter of cactionArrayAllocationExpressionEnd is whether an initializer exists or not. 
             * Here, an initializer means the way of representation by using {' and '}' such as "int[] a = {1, 2, 3}".
             * For x10, Michihiro modified the code for the initializer so that it can parse instantiation of Rail with 
             * initial values such as "new Rail[Long](5, 1)".
             */
            JNI.cactionArrayAllocationExpressionEnd(1, n.arguments().size() == 2, RoseTranslator.createJavaToken(n, n.toString()));
        } else {
            JNI.cactionAllocationExpression(RoseTranslator.createJavaToken(n, n.toString()));
            List<TypeNode> typeArg = n.typeArguments();
            for (int i = 0; typeArg != null && i < typeArg.size(); ++i) {
                // visitChildren(n, n.typeArguments());
                String typeParam = typeArg.get(i).nameString();
            }
            visitChild(n, n.objectType());
            visitChildren(n, n.arguments());
            JNI.cactionAllocationExpressionEnd(n.objectType() != null, n.arguments().size(), RoseTranslator.createJavaToken(n, n.toString()));
        }
        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        for (AnnotationNode a : annotations) 
            visitChild(n, a.annotationType());
        JNI.cactionAttachAnnotationsToNewExp(annotations.size(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(Allocation_c n) {
        toRose(n, "Allocation:");
        visitChild(n, n.objectType());
    }

    public void visit(LocalAssign_c n) {
        toRose(n, "LocalAssign:", n, n.operator());
        if (n.operator()==Assign.Operator.ASSIGN) {
            JNI.cactionAssignment(RoseTranslator.createJavaToken(n, n.toString()));
            visitChild(n, n.local());
            visitChild(n, n.right());
            JNI.cactionAssignmentEnd(RoseTranslator.createJavaToken(n, n.toString()));
        }
        else {
            JNI.cactionCompoundAssignment(RoseTranslator.createJavaToken(n, n.toString()));
            visitChild(n, n.local());
            visitChild(n, n.right());
            JNI.cactionCompoundAssignmentEnd(RoseTranslator.getOperatorKind(n.operator()), RoseTranslator.createJavaToken(n, n.toString()));
        }
        
        if (RoseTranslator.DEBUG) toRose(n, "LocalAssign end:", n, n.operator());
    }

    public void visit(X10LocalAssign_c n) {
        toRose(n, "X10LocalAssign:");
        visitChild(n, n.local());
        visitChild(n, n.right());
    }

    public void visit(X10Cast_c n) {
        toRose(n, "X10Cast:", n, n.castType(), n.expr());
        JNI.cactionCastExpression(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.castType());
        visitChild(n, n.expr());
        JNI.cactionCastExpressionEnd(RoseTranslator.createJavaToken(n, n.toString()));
        toRose(n, "X10Cast end:", n);
    }

    public void visit(X10Instanceof_c n) {
        toRose(n, "X10Instanceof:");
        JNI.cactionInstanceOfExpression(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        visitChild(n, n.compareType());
        JNI.cactionInstanceOfExpressionEnd(RoseTranslator.createJavaToken(n, n.toString()));
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
    
//    public void visit(Closure_c n) {
//        toRose(n, "Closure:", n, n.formals(), n.body());
//        Closure_c closure = (Closure_c) n;
//
//        JNI.cactionClosure(RoseTranslator.createJavaToken(n, n.toString()));
//
//        String callerClass = JNI.cactionGetCurrentClassName();
//        int closureIndex = RoseTranslator.uniqMemberIndex++;
//
//        /* Defines class declaration */
//        String closureName = "Closure_" + closureIndex;
//        JNI.cactionSetCurrentClassName(closureName);
//        JNI.cactionPushPackage("", RoseTranslator.createJavaToken(n, closureName));
//        JNI.cactionInsertClassStart(closureName, false, false, false, false, RoseTranslator.createJavaToken(n, closureName));
//        JNI.cactionInsertClassEnd(closureName, RoseTranslator.createJavaToken(n, closureName));
//        JNI.cactionBuildClassSupportStart(closureName, "", true, false, false, false, false, RoseTranslator.createJavaToken(n, closureName));
//        JNI.cactionBuildClassExtendsAndImplementsSupport(0, new String[0], false, "", 0, new String[0],
//                                                         RoseTranslator.createJavaToken(n, n.toString()));
//
//        /* Defines <tt>apply</tt> method declaration in the defining class above */
//        String closureMethodName = "apply";
//        String returnType = "void";
//        JNI.cactionBuildMethodSupportStart(closureMethodName, closureIndex, RoseTranslator.createJavaToken(n, closureMethodName));
//        JNI.cactionTypeReference("", returnType, this, RoseTranslator.createJavaToken());      // build return type
//        List<Formal> formals = n.formals();
//        visitChildren(n, formals);  // build parameters
//        JNI.cactionBuildMethodSupportEnd(closureMethodName, closureIndex, false, false, false, 0, n.formals().size(), true,
//                                         RoseTranslator.createJavaToken(n, n.toString()),
//                                         RoseTranslator.createJavaToken(n, n.toString() + "_args"));
//
//        /* Defines method body */
//        StringBuffer param = new StringBuffer();
//        for (Formal f : formals)
//            param.append(f.type().toString().toLowerCase());
//        RoseTranslator.memberMap.put(JNI.cactionGetCurrentClassName() + ":" + closureMethodName + "(" + param + ")", RoseTranslator.uniqMemberIndex++);
//        int method_index = RoseTranslator.memberMap.get(JNI.cactionGetCurrentClassName() + ":" + closureMethodName + "(" + param + ")");
//        JNI.cactionMethodDeclaration(closureMethodName, closureIndex, formals.size(), RoseTranslator.createJavaToken(n, closureMethodName),
//                                   RoseTranslator.createJavaToken(n, closureMethodName + "(" + param + ")"));
//        JNI.cactionMethodDeclarationHeader(closureMethodName, false, false, false, false, false, false, false, false, false, 0, 0, 0,
//                                           RoseTranslator.createJavaToken(n, closureMethodName));
//        visitChild(n, n.body());
//        JNI.cactionMethodDeclarationEnd(0, n.body().statements().size(), RoseTranslator.createJavaToken(n, closureMethodName + "(" + param + ")"));
//
//        /* Finishes to define class declaration */
//        JNI.cactionBuildClassSupportEnd(closureName, 1, RoseTranslator.createJavaToken(n, closureName));
//        JNI.cactionTypeDeclaration("", closureName, 0, false, false, false, false, false, false, false, true, false, false, false,
//                                   RoseTranslator.createJavaToken(n, closureName));
//
//        JNI.cactionClosureEnd(callerClass, RoseTranslator.createJavaToken(n, n.toString()));
//
//        /* cactionClosureEnd internally changes the current class name, thus comment out */
////        /* change current class */
////        JNI.cactionSetCurrentClassName(callerClass);
//
//        toRose(n, "Closure end:", n);
//    }


    public void visit(Closure_c n) {
        toRose(n, "Closure:", n, n.formals(), n.body());
        Closure_c closure = (Closure_c) n;
        
        String currentClass = JNI.cactionGetCurrentClassName();
        JNI.cactionClosure(RoseTranslator.createJavaToken(n, n.toString()));
        int closureIndex = RoseTranslator.uniqMemberIndex++;

        /* Prepare necessary information */
        String[] names = getPackageAndTypeName(currentClass);
        String package_name = names[0];
        String caller_name = names[1];     
        String closure_name = "Closure_" + closureIndex;
        String class_name = caller_name + "." + closure_name;  
        String path = ((package_name.length() == 0)? "" : package_name + ".") + class_name;  
        System.out.println("0416 current class=" + currentClass + ", package=" + package_name + ", caller=" + caller_name
                           + ", closure=" + closure_name + ", class_name=" + class_name + ", path=" + path);
        
        /* record as nested class */
//        RoseTranslator.recordNestedClass(package_name, caller_name, closure_name);
        
        /* create a new stack for closure class and copy caller's stack onto it */
        JNI.cactionSetCurrentClassNameWithCopyingStacks(path);
        
        /* Defines class declaration */         
        JNI.cactionSetCurrentClassName(path);
        JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, closure_name));
//        JNI.cactionPopPackage();
//        JNI.cactionTypeReference(package_name, closure_name, this, RoseTranslator.createJavaToken());
        
        JNI.cactionInsertClassStart(closure_name, false, false, false, false, RoseTranslator.createJavaToken(n, closure_name));
        JNI.cactionInsertClassEnd(closure_name, RoseTranslator.createJavaToken(n, closure_name));
        JNI.cactionBuildClassSupportStart(closure_name, "", true, false, false, false, false, RoseTranslator.createJavaToken(n, closure_name));
        JNI.cactionBuildClassExtendsAndImplementsSupport(0, new String[0], false, "", 0, new String[0], 
                                                         RoseTranslator.createJavaToken(n, n.toString()));

        /* Defines <tt>apply</tt> method declaration */
        String method_name = "apply";
        String returnType = "void";
        JNI.cactionBuildMethodSupportStart(method_name, closureIndex, RoseTranslator.createJavaToken(n, method_name));
        JNI.cactionTypeReference("", returnType, this, RoseTranslator.createJavaToken());      // build return type
        List<Formal> formals = n.formals();
        visitChildren(n, formals);
        ClosureDef cdef = closure.closureDef();
        List<VarInstance<? extends VarDef>> vardefList = cdef.capturedEnvironment();
        for (VarInstance<? extends VarDef> vins : vardefList) {
            visitType(vins.type().toString(), vins.type().toString(), vins.type().name().toString(),n);
            JNI.cactionBuildArgumentSupport(vins.name().toString(), "", 0, true, RoseTranslator.createJavaToken(n, vins.toString()));
        }
        
        JNI.cactionBuildClosureMethodSupportEnd(method_name, closureIndex, false, false, false, 0, n.formals().size() + vardefList.size(),
                                         n.formals().size(), true, RoseTranslator.createJavaToken(n, n.toString()), 
                                         RoseTranslator.createJavaToken(n, n.toString() + "_args"));               
        
        /* Finishes to define class declaration */
        JNI.cactionBuildClassSupportEnd(closure_name, 1, RoseTranslator.createJavaToken(n, closure_name));
        JNI.cactionTypeDeclaration(package_name, class_name, 0, false, false, false, false, false, false, false, true, false, false, false, 
                                 RoseTranslator.createJavaToken(n, closure_name));
        
        JNI.cactionPushNestedClass(((package_name.length() == 0)? "" : package_name + ".") + class_name, 
                                    ((package_name.length() == 0)? "" : package_name + ".") + caller_name);



        /* Defines method body */        
        JNI.cactionMethodDeclaration(method_name, closureIndex, RoseTranslator.createJavaToken(n, method_name + "(...)"));
        JNI.cactionMethodDeclarationHeader(method_name, false, false, false, false, false, false, false, false, false, 0, 0, 0, 
                                           RoseTranslator.createJavaToken(n, method_name));
        
        JNI.cactionPushPackage(package_name, RoseTranslator.createJavaToken(n, closure_name));
        JNI.cactionPopPackage();
        visitChild(n, n.body());        
        JNI.cactionMethodDeclarationEnd(0, n.body().statements().size(), RoseTranslator.createJavaToken(n, method_name + "(...)"));
        
//        /* Finishes to define class declaration */
        JNI.cactionBuildClassSupportEnd(class_name, 1, RoseTranslator.createJavaToken(n, closure_name));
        JNI.cactionTypeDeclaration(package_name, class_name, 0, false, false, false, false, false, false, false, true, false, false, false, 
                                   RoseTranslator.createJavaToken(n, closure_name));

//        JNI.cactionTypeDeclarationEnd(true, RoseTranslator.createJavaToken(n, class_name));

//        JNI.cactionSetCurrentClassName(currentClass);
      
        JNI.cactionClosureEnd(currentClass, RoseTranslator.createJavaToken(n, n.toString()));

        /* cactionClosureEnd internally changes the current class name, thus comment out */
//        /* change current class */
//        JNI.cactionSetCurrentClassName(callerClass);

//        JNI.cactionSetCurrentClassName(currentClass);
        
        toRose(n, "Closure end:", n);
    }

    public void visit(ClosureCall_c n) {
        toRose(n, "ClosureCall:", n.target(), n.arguments());
        JNI.cactionClosureCall(RoseTranslator.createJavaToken(n, n.toString()));
        List<Expr> args = n.arguments();
        visitChildren(n, args);
        visitChild(n, n.target());
        JNI.cactionClosureCallEnd(args.size(), RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(StmtExpr_c n) {
        toRose(n, "StmtExpr:");
        visitChildren(n, n.statements());
    }

    public void visit(AmbReceiver_c n) {
        toRose(n, "AmbReceiver:", n.nameNode().id().toString());
    }

    public void visit(Switch_c n) {
        toRose(n, "Switch:", n.elements().size());
        int numCases = 0;
        boolean hasDefault = false;
        for (polyglot.ast.SwitchElement el : n.elements()) {
            /**
             * This count does not include the default clause.
             * Rose manages the additional count for the default.
             * @see src/frontend/X10_ROSE_Connection/ParserActionROSE.C:cactionSwitchStatementEnd()
             */
            if (el instanceof Case_c) {
                if (((Case_c) el).isDefault()) {
                    hasDefault = true;
                    continue;
                }
                ++numCases;
            }
        }
        JNI.cactionSwitchStatement(RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        visitChildren(n, n.elements());
        JNI.cactionSwitchStatementEnd(numCases, hasDefault, RoseTranslator.createJavaToken(n, n.toString()));
    }

    public void visit(SwitchBlock_c n) {
        toRose(n, "SwitchBlock:", n);
        visitChildren(n, n.statements());
    }

    public void visit(Case_c n) {
        toRose(n, "Case:", n.expr(), n.isDefault());
        boolean hasExpression = n.expr() != null;
        JNI.cactionCaseStatement(hasExpression, RoseTranslator.createJavaToken(n, n.toString()));
        visitChild(n, n.expr());
        JNI.cactionCaseStatementEnd(hasExpression, RoseTranslator.createJavaToken(n, n.toString()));
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
