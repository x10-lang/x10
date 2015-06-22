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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

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
import polyglot.ast.Binary;
import polyglot.ast.Binary.Operator;
import polyglot.ast.Binary_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Cast_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall.Kind;
import polyglot.ast.Assign;
import polyglot.ast.Block_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Case_c;
import polyglot.ast.Catch_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassLit_c;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Disamb;
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
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.NewArray_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NodeList_c;
import polyglot.ast.Node_c;
import polyglot.ast.NullLit_c;
import polyglot.ast.NumLit_c;
import polyglot.ast.PackageNode;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.SourceCollection_c;
import polyglot.ast.SourceFile;
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
import polyglot.ast.Typed;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Source;
import polyglot.frontend.TargetFactory;
import polyglot.main.Options;
import polyglot.types.Flags;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.StringUtil;
import polyglot.visit.ConformanceChecker;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.Translator;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.ExtensionInfo.X10Scheduler.X10Job;
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
import x10.ast.Finish_c;
import x10.ast.ForLoop_c;
import x10.ast.FunctionTypeNode_c;
import x10.ast.HasZeroTest_c;
import x10.ast.Here_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.Next_c;
import x10.ast.ParExpr_c;
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
import x10.ast.X10AmbTypeNode_c;
import x10.ast.X10Binary_c;
import x10.ast.X10BooleanLit_c;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast_c;
import x10.ast.X10CharLit_c;
import x10.ast.X10ClassBody_c;
import x10.ast.X10ClassDecl;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ClockedLoop_c;
import x10.ast.X10Conditional_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10Do_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
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
import x10.ast.X10SourceFile_c;
import x10.ast.X10Special_c;
import x10.ast.X10StringLit_c;
import x10.ast.X10Unary_c;
import x10.ast.X10While_c;
import x10.extension.X10Del;
import x10.extension.X10Ext;
import x10.parser.X10Lexer;
import x10.parser.X10SemanticRules;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.util.StringResource;
import x10.visit.X10DelegatingVisitor;
import x10.visit.X10TypeChecker;
import x10rose.ExtensionInfo;

public class RoseTranslator extends Translator {

    public static Set<String> package_traversed = new HashSet<String>();

    public static int uniqMemberIndex = 0;
    
    /** 
     * Key represents full path for a class, and the value for a key represents
     * the package name for the class
     */
    public static HashMap<String, String> nestedClasses = new HashMap<String, String>();
    
    public static HashMap<String, String> classes = new HashMap<String, String>();
    
    public RoseTranslator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
        super(job, ts, nf, tf);
        jobList.add(job);
        package_traversed.add("x10.lang");
        package_traversed.add("x10.io");
    }
    
    public static void recordNestedClass(String package_name, String parentClass_name, String nestedClass_name) {
        if (DEBUG) System.out.println("nested class=" + nestedClass_name + ", package=" + package_name + ", parent class=" + parentClass_name);
        nestedClasses.put((package_name.length() == 0 ? "" : package_name + ".") + parentClass_name + "." + nestedClass_name,
                            package_name);
    }

    public static boolean isNestedClass(String class_name, String[] package_name) {
        for (String path : nestedClasses.keySet()) {
            if (path.equals(class_name)) {
                package_name[0] = nestedClasses.get(path);
                return true;
            }
        }
        String outer = "";
        int index = class_name.lastIndexOf('.');
        if (index > 0)
            outer = class_name.substring(0, index);
        for (String path : classes.keySet()) {
            if (path.equals(outer)) {
                package_name[0] = classes.get(path);
                return true;
            }
        }

        return false;
    }

    protected boolean translateSource(SourceFile sfn) {
        TypeSystem ts = typeSystem();
        NodeFactory nf = nodeFactory();
        TargetFactory tf = this.tf;
        ErrorQueue eq = job.compiler().errorQueue();
        Compiler compiler = job.compiler();
        Options options = job.extensionInfo().getOptions();

        Source src = sfn.source();
        String in_file_name = sfn.source().path();
        String out_file_name = in_file_name + ".txt";

        // MH-20140123
        // Just confirm that AST node is created
        try {
            SourceFile sfile = (SourceFile) job.ast();
            CodeWriter w = tf.outputCodeWriter(new File(out_file_name), 100);
            sfile.prettyPrint(w, new PrettyPrinter());
            // if (DEBUG) System.out.println(sfile);

            new SourceVisitor(w, null).visitAppropriate(sfn);

            w.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find the public declarations in the file. We'll use these to
        // derive the names of the target files. There will be one
        // target file per public declaration. If there are no public
        // declarations, we'll use the source file name to derive the
        // target file name.
        List<TopLevelDecl> exports = exports(sfn);

        CodeWriter w = null;

        out_file_name = in_file_name + ".Rose.dot";
        try {

            w = tf.outputCodeWriter(new File(out_file_name), 100);
            job.compiler().addOutputFile(in_file_name, out_file_name);

            w.writeln("digraph \"" + in_file_name + "\" {");

            new DotGenerator(w, null).visitAppropriate(sfn);

            w.writeln("}");

            w.newline(0);

            return true;

        } catch (IOException e) {

            eq.enqueue(ErrorInfo.IO_ERROR, "I/O error while translating: " + e.getMessage());

            return false;

        } finally {
            /*
             * if (w != null) { try { w.close();
             * 
             * String[] cmdline = {"dot", "-Tpng", "-O", out_file_name}; try {
             * Runtime runtime = Runtime.getRuntime(); Process proc =
             * runtime.exec(cmdline, null, options.output_directory);
             * 
             * InputStreamReader err = new
             * InputStreamReader(proc.getErrorStream());
             * 
             * String output = null; try { char[] c = new char[72]; int len;
             * StringBuffer sb = new StringBuffer(); while((len = err.read(c)) >
             * 0) { sb.append(String.valueOf(c, 0, len)); }
             * 
             * if (sb.length() != 0) { output = sb.toString(); } } finally {
             * err.close(); }
             * 
             * proc.waitFor();
             * 
             * if (!options.keep_output_files) { String[] rmCmd = new String[]
             * {"rm", out_file_name}; runtime.exec(rmCmd, null,
             * options.output_directory); }
             * 
             * if (output != null) eq.enqueue(proc.exitValue()>0 ?
             * ErrorInfo.POST_COMPILER_ERROR : ErrorInfo.WARNING, output); if
             * (proc.exitValue() > 0) {
             * eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,
             * "Non-zero return code: " + proc.exitValue()); return false; } }
             * catch(Exception e) { eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,
             * e.getMessage() != null ? e.getMessage() : e.toString()); return
             * false; } } catch (IOException e) {
             * job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
             * "I/O error while closing output file: " + e.getMessage()); } }
             */
        }
    }

    int counter = 0;

    static final boolean DEBUG = true;


    public static boolean hasFunctionType(X10MethodDecl_c methodDecl) {
        if (methodDecl.returnType() instanceof FunctionTypeNode_c)
            return true;

        List<Formal> args = methodDecl.formals();
        for (Formal f : methodDecl.formals()) {
            if (f.type() instanceof FunctionTypeNode_c)
                return true;
        }
        return false;
    }

    public static boolean hasFunctionType(X10ConstructorDecl_c constDecl) {
        TypeNode ret = constDecl.returnType();
        if (ret != null && ret instanceof FunctionTypeNode_c)
            return true;

        List<Formal> args = constDecl.formals();
        for (Formal f : constDecl.formals()) {
            if (f.type() instanceof FunctionTypeNode_c)
                return true;
        }
        return false;
    }

    public static boolean hasFunctionType(X10FieldDecl_c fieldDecl) {
        return fieldDecl.type() instanceof FunctionTypeNode_c;
    }

    public static JavaToken createJavaToken(/* ASTNode node */) {
        JavaSourcePositionInformation pos = null;// this.posFactory.createPosInfo(node);
        // For now we return dummy text
        return new JavaToken(x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex).source().path()/* "Dummy JavaToken (see createJavaToken)" */, new JavaSourcePositionInformation(0, 0));
    }

    public static JavaToken createJavaToken(Node_c node, String name) {
        try {
            return new JavaToken(x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex).source().path()/* name */, new JavaSourcePositionInformation(node.position().startOf().line(), node.position().endLine()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static List<Job> jobList = new ArrayList<Job>();

    static HashMap<String, Integer> memberMap = new HashMap<String, Integer>();

    static int fileIndex;

    /**
     * This method is invoked from TypeVisitor.<init>() and
     * visitDeclarations(Term_c). Note that package name is not supposed to be
     * included in the parameter <tt>clazz</tt>.
     * 
     * @param clazz
     *            does not have package name
     * @return
     */
    static String trimTypeParameterClause(String clazz) {
        if (   clazz.indexOf("Rail[") >= 0 
            || clazz.indexOf("GrowableRail[") >= 0)
            return clazz;

        int token_typeParam;
        if ((token_typeParam = clazz.indexOf('[')) >= 0) {
            clazz = clazz.substring(0, token_typeParam);
        }
        int token_constraint;
        if ((token_constraint = clazz.indexOf('{')) >= 0) {
            clazz = clazz.substring(0, token_constraint);
        }
        // TODO: remove this when operator is available
        int token_operator;
        if ((token_operator = clazz.indexOf('(')) >= 0) {
            clazz = clazz.substring(0, token_operator);
        }
        return clazz;
    }

    static boolean isX10Primitive(String package_name, String type_name) {
        return (package_name.equals("x10.lang") 
                && (type_name.equals("Boolean") 
                        || type_name.equals("Byte") 
                        || type_name.equals("Char") 
                        || type_name.equals("Int") 
                        || type_name.equals("Short") 
                        || type_name.equals("Float") 
                        || type_name.equals("Long") 
                        || type_name.equals("Double") 
                        || type_name.equals("UByte") 
                        || type_name.equals("UInt") 
                        || type_name.equals("UShort") 
                        || type_name.equals("ULong")
        // TODO: confirm when the primitive type starting with lower
        // case can be used
        // for ambtype?
                        || type_name.equals("byte") 
                        || type_name.equals("char") 
                        || type_name.equals("int") 
                        || type_name.equals("short") 
                        || type_name.equals("float") 
                        || type_name.equals("long") 
                        || type_name.equals("double") 
                        || type_name.equals("ubyte") 
                        || type_name.equals("uint")  
                        || type_name.equals("ushort") 
                        || type_name.equals("ulong")));
    }

    static boolean isX10Primitive(String class_name) {
        return // TODO: confirm when void type is used
                class_name.equals("void") 
                || class_name.equals("x10.lang.Boolean") 
                || class_name.equals("x10.lang.Byte") 
                || class_name.equals("x10.lang.Char") 
                || class_name.equals("x10.lang.Int") 
                || class_name.equals("x10.lang.Short") 
                || class_name.equals("x10.lang.Float") 
                || class_name.equals("x10.lang.Long") 
                || class_name.equals("x10.lang.Double") 
                || class_name.equals("x10.lang.UByte") 
                || class_name.equals("x10.lang.UInt") 
                || class_name.equals("x10.lang.UShort") 
                || class_name.equals("x10.lang.ULong")
        // TODO: confirm when the primitive type starting with lower
        // case can be used
        // for ambtype?
                || class_name.equals("Boolean") 
                || class_name.equals("Byte") 
                || class_name.equals("Char") 
                || class_name.equals("Int") 
                || class_name.equals("Short") 
                || class_name.equals("Float") 
                || class_name.equals("Long") 
                || class_name.equals("Double") 
                || class_name.equals("UByte") 
                || class_name.equals("UInt") 
                || class_name.equals("UShort") 
                || class_name.equals("ULong") 
                || class_name.equals("boolean") 
                || class_name.equals("byte") 
                || class_name.equals("char") 
                || class_name.equals("int") 
                || class_name.equals("short") 
                || class_name.equals("float") 
                || class_name.equals("long") 
                || class_name.equals("double")
                || class_name.equals("ubyte")
                || class_name.equals("uint") 
                || class_name.equals("ushort")
                || class_name.equals("ulong");
    }

    private static HashMap<Binary.Operator, Integer> binaryOpTable = new HashMap<Binary.Operator, Integer>();
    private static HashMap<Unary.Operator, Integer> unaryOpTable = new HashMap<Unary.Operator, Integer>();
    private static HashMap<Assign.Operator, Integer> assignOpTable = new HashMap<Assign.Operator, Integer>();
    
    /**
     * 
     * operator code values are directly derived from ECJ.
     * 
     * @see ParserActionROSE.C:cactionUaryExpressionEnd()
     * @param op
     * @return
     */
    static int getOperatorKind(Unary.Operator op) {
        if (unaryOpTable.isEmpty()) {
            unaryOpTable.put(Unary.Operator.NOT, 11);
            unaryOpTable.put(Unary.Operator.BIT_NOT, 12);
            unaryOpTable.put(Unary.Operator.PRE_DEC, 13);
            unaryOpTable.put(Unary.Operator.POST_DEC, 13);
            unaryOpTable.put(Unary.Operator.PRE_INC, 14);
            unaryOpTable.put(Unary.Operator.POST_INC, 14);
            unaryOpTable.put(Unary.Operator.NEG, 13);
        }
        return unaryOpTable.get(op);
    }

    /**
     * 
     * operator code values are directly derived from ECJ.
     * 
     * 
     * @see JavaParserActionROSE.C:
     *      Java_JavaParser_cactionBinaryExpressionEnd(JNIEnv *env, jclass, jint
     *      java_operator_kind, jobject jToken)
     * @param op
     * @return
     */
    static int getOperatorKind(Binary.Operator op) {
        if (binaryOpTable.isEmpty()) {
            binaryOpTable.put(Binary.Operator.BIT_AND, 2); // or COND_AND?
            binaryOpTable.put(Binary.Operator.BIT_OR, 3); // or COND_OR?
            binaryOpTable.put(Binary.Operator.LT, 4);
            binaryOpTable.put(Binary.Operator.LE, 5);
            binaryOpTable.put(Binary.Operator.GT, 6);
            binaryOpTable.put(Binary.Operator.GE, 7);
            binaryOpTable.put(Binary.Operator.BIT_XOR, 8);
            binaryOpTable.put(Binary.Operator.DIV, 9);
            binaryOpTable.put(Binary.Operator.SHL, 10);
            binaryOpTable.put(Binary.Operator.SUB, 13);
            binaryOpTable.put(Binary.Operator.ADD, 14);
            binaryOpTable.put(Binary.Operator.MUL, 15);
            binaryOpTable.put(Binary.Operator.MOD, 16);
            binaryOpTable.put(Binary.Operator.SHR, 17);
            binaryOpTable.put(Binary.Operator.USHR, 19);
            binaryOpTable.put(Binary.Operator.DOT_DOT, 20);
            binaryOpTable.put(Binary.Operator.EQ, 21);
            binaryOpTable.put(Binary.Operator.NE, 22);
            binaryOpTable.put(Binary.Operator.COND_OR, 100);
            binaryOpTable.put(Binary.Operator.COND_AND, 101);
        }
        return binaryOpTable.get(op);
    }
    
    static int getOperatorKind(Assign.Operator op) {
        if (assignOpTable.isEmpty()) {
            assignOpTable.put(Assign.Operator.BIT_AND_ASSIGN, 2);
            assignOpTable.put(Assign.Operator.BIT_OR_ASSIGN, 3);
            assignOpTable.put(Assign.Operator.BIT_XOR_ASSIGN, 8);
            assignOpTable.put(Assign.Operator.DIV_ASSIGN, 9);
            assignOpTable.put(Assign.Operator.SHL_ASSIGN, 10);
            assignOpTable.put(Assign.Operator.SUB_ASSIGN, 13);
            assignOpTable.put(Assign.Operator.ADD_ASSIGN, 14);
            assignOpTable.put(Assign.Operator.MUL_ASSIGN, 15);
            assignOpTable.put(Assign.Operator.MOD_ASSIGN, 16);
            assignOpTable.put(Assign.Operator.SHR_ASSIGN, 17);
        }
        return assignOpTable.get(op);
    }
}
