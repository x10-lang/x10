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
import x10.parser.X10Lexer;
import x10.parser.X10SemanticRules;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.util.StringResource;
import x10.visit.X10DelegatingVisitor;
import x10.visit.X10TypeChecker;
import x10rose.ExtensionInfo;
import x10rose.ExtensionInfo.FileStatus;

public class RoseTranslator extends Translator {

/*
    public JavaToken createJavaToken(ASTNode lnode, ASTNode rnode) {
        JavaSourcePositionInformation pos = this.posFactory.createPosInfo(lnode.sourceStart(), rnode.sourceEnd());
        // For now we return dummy text
        return new JavaToken("Dummy JavaToken (see createJavaToken)", pos);
    }
*/
	
	public RoseTranslator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
		super(job, ts, nf, tf);
		jobList.add(job);
	}
	
	protected boolean translateSource(SourceFile sfn) {
//System.out.println("RoseTranslator.translateSource()");
		TypeSystem ts = typeSystem();
		NodeFactory nf = nodeFactory();
		TargetFactory tf = this.tf;
		ErrorQueue eq = job.compiler().errorQueue();
		Compiler compiler = job.compiler();
		Options options = job.extensionInfo().getOptions();
		
//	System.out.println("job.ast="+job.ast());
		
/*
		try {
//        JNI.cactionTest();
//        JavaParser.cactionTest();l
		} catch (Error er) {
//			System.out.println("cactionTest1");
			er.printStackTrace();
//			System.out.println("cactionTest2");
		}
*/

		Source src = sfn.source();
		String in_file_name = sfn.source().path();
		String out_file_name = in_file_name + ".txt";
		
		//MH-20140123
		//	Just confirm that AST node is created
		try {
			SourceFile sfile = (SourceFile)job.ast();
			CodeWriter w = tf.outputCodeWriter(new File(out_file_name), 100);
			sfile.prettyPrint(w, new PrettyPrinter());	
//			System.out.println(sfile);
			
			new ToRoseVisitor(w, null).visitAppropriate(sfn);
			
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

			new DV(w, null).visitAppropriate(sfn);

			w.writeln("}");

			w.newline(0);

			return true;

		} catch (IOException e) {

			eq.enqueue(ErrorInfo.IO_ERROR, "I/O error while translating: " + e.getMessage());

			return false;

		} finally {
/*
			if (w != null) {
				try {
					w.close();
					
					String[] cmdline = {"dot", "-Tpng", "-O", out_file_name};
			        try {
			            Runtime runtime = Runtime.getRuntime();
			        	Process proc = runtime.exec(cmdline, null, options.output_directory);

			        	InputStreamReader err = new InputStreamReader(proc.getErrorStream());

			        	String output = null;
			        	try {
			        		char[] c = new char[72];
			        		int len;
			        		StringBuffer sb = new StringBuffer();
			        		while((len = err.read(c)) > 0) {
			        			sb.append(String.valueOf(c, 0, len));
			        		}

			        		if (sb.length() != 0) {
			        			output = sb.toString();
			        		}
			        	}
			        	finally {
			        		err.close();
			        	}

			        	proc.waitFor();

			        	if (!options.keep_output_files) {
			        		String[] rmCmd = new String[] {"rm", out_file_name};
			        		runtime.exec(rmCmd, null, options.output_directory);
			        	}

			        	if (output != null)
			        		eq.enqueue(proc.exitValue()>0 ? ErrorInfo.POST_COMPILER_ERROR : ErrorInfo.WARNING, output);
			        	if (proc.exitValue() > 0) {
			        		eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, "Non-zero return code: " + proc.exitValue());
			        		return false;
			        	}
			        } catch(Exception e) {
			        	eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, e.getMessage() != null ? e.getMessage() : e.toString());
			        	return false;
			        }						
				} catch (IOException e) {
					job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR, "I/O error while closing output file: " + e.getMessage());
				}
			}
			
*/
		}
	}

	int counter = 0;

	String getDotNode(Node n) {
		String id;
		if (dotNode.containsKey(n)) {
			id = dotNode.get(n);
		} else {
			id = Integer.toString(counter++);
			dotNode.put(n, id);
		}
		return id;
	}
	
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
		if (ret != null 
				&& ret instanceof FunctionTypeNode_c)
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
	
	 public static JavaToken createJavaToken(/*ASTNode node*/) {
	        JavaSourcePositionInformation pos = null;//this.posFactory.createPosInfo(node);
	        // For now we return dummy text
	        return new JavaToken(x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex).source().path()/*"Dummy JavaToken (see createJavaToken)"*/, 
	        		new JavaSourcePositionInformation(0, 0));
	    }
		
	public static JavaToken createJavaToken(Node_c node, String name) {
		try {
		return new JavaToken(x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex).source().path()/*name*/, new JavaSourcePositionInformation(node
				.position().startOf().line(), node.position().endLine()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static List<Job> jobList = new ArrayList<Job>();

	Map<Node, String> dotNode = new HashMap<Node, String>();
	
	private static HashMap<String, HashMap<String, Integer>> classMemberMap = new HashMap<String, HashMap<String, Integer>>();
	
	private static HashMap<String, Integer> memberMap = new HashMap<String, Integer>();

	private static int fileIndex;
	
	/**
	 * This method is invoked from TypeVisitor.<init>() and visitDeclarations(Term_c).
	 * Note that package name is not supposed to be included in the parameter <tt>clazz</tt>.
	 * 
	 * @param clazz does not have package name
	 * @return
	 */
	private static String trimTypeParameterClause(String clazz) {
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

	public static class ToRoseVisitor extends X10DelegatingVisitor {
		Node parent;
		CodeWriter w;
		private List<Import> imports;
		private List<String> package_ref;

		public static boolean isGatheringFile = true;
		private static String currentPackageName = "";	
		private static HashMap<Binary.Operator, Integer> binaryOpTable = new HashMap<Binary.Operator, Integer>();
		private static HashMap<Unary.Operator, Integer> unaryOpTable = new HashMap<Unary.Operator, Integer>();
		
		
		/**     
		 * 
		 * operator code values are directly derived from ECJ.
		 *
	     *  enum ops // NO_STRINGIFY
    	 *	{
         *		ERROR_OPERATOR = 0, // This is not a ECJ value 
         *		NOT            = 11,
         *		TWIDDLE        = 12,
         *		MINUS          = 13,
         * 		PLUS           = 14,
         *		LAST_OPERATOR
    	 * 	};
		  *
		  *@see ParserActionROSE.C:cactionUaryExpressionEnd()
		 * @param op
		 * @return
		 */
		private int getOperatorKind(Unary.Operator op) {
			if (unaryOpTable.isEmpty()) {
				unaryOpTable.put(Unary.Operator.NOT, 11);
				unaryOpTable.put(Unary.Operator.BIT_NOT, 12);
				unaryOpTable.put(Unary.Operator.PRE_DEC, 13);
				unaryOpTable.put(Unary.Operator.POST_DEC, 13);
				unaryOpTable.put(Unary.Operator.PRE_INC, 14);
				unaryOpTable.put(Unary.Operator.POST_INC, 14);
			}
			return unaryOpTable.get(op);
		}
		
		/**     
		 * 
		 * operator code values are directly derived from ECJ.
		 * 
	     * enum ops {
	     *    ERROR_OPERATOR       = 0, // This is not a ECJ value 
	     *   AND                  = 2,
	     *   DIVIDE               = 9,
	     *   GREATER              = 6,
	     *   GREATER_EQUAL        = 7,
	     *   LEFT_SHIFT           = 10,
	     *   LESS                 = 4,
	     *   LESS_EQUAL           = 5,
	     *   MINUS                = 13,
	     *   MULTIPLY             = 15,
	     *   OR                   = 3,
	     *   PLUS                 = 14,
	     *   REMAINDER            = 16,
	     *   RIGHT_SHIFT          = 17,
	     *   UNSIGNED_RIGHT_SHIFT = 19,
	     *   XOR                  = 8,
	     *   OR_OR                = 100, // Handled by separate function 
	     *   AND_AND              = 101, // Handled by separate function 
	     *   LAST_OPERATOR
	      *};
		  *
		  *@see JavaParserActionROSE.C:Java_JavaParser_cactionBinaryExpressionEnd(JNIEnv *env, jclass, jint java_operator_kind, jobject jToken)
		 * @param op
		 * @return
		 */
		private int getOperatorKind(Binary.Operator op) {			
			if (binaryOpTable.isEmpty()) {
				binaryOpTable.put(Binary.Operator.BIT_AND, 2); // or COND_AND?
				binaryOpTable.put(Binary.Operator.BIT_OR, 3);  // or COND_OR?
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
				binaryOpTable.put(Binary.Operator.COND_OR, 100);
				binaryOpTable.put(Binary.Operator.COND_AND, 101);
			}
			return binaryOpTable.get(op);
		}
		
		public ToRoseVisitor(CodeWriter w, Node parent) {
			this.parent = parent;
			this.w = w;
			package_ref = new ArrayList<String>();
			imports = new ArrayList<Import>();
		}
		
		void toRose(Node n, String name, String... extra) {
			if (name != null)
				System.out.print(name);
			if (extra != null)
				for (String s : extra) {
					System.out.print(s + " ");
				}
			System.out.println();
			/*
*/
		}		
		
		
		void visitChild(Node p, Node n) {
			if (n == null)
				return;
			
			new ToRoseVisitor(w, p).visitAppropriate(n);
		}

		void visitChildren(Node p, List<? extends Node> l) {
			if (l == null)
				return;
			for (Node n : l)
				visitChild(p, n);
		}
		
		public void visit(Node_c n) {
			toRose(n, null);
			System.err.println("UNRECOGNISED NODE in ToRoseVisitor: " + n.getClass());
		}
		
		private static boolean isDecl = true;
		
		private static int numSourceFile;
		
		private static HashMap<String, Node> astMap = new HashMap<String, Node>();
				
		public  void searchFileList(String packageName, String typeName) throws IOException {
			for (Job job : jobList) {
				FileSource source = (FileSource) job.source();
				String sourceName = source.toString();
				boolean isFoundSourceFile = false;
				for (int i = 0; i <= fileIndex; ++i) { // including currently processing file
					String sourceFileGiven = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(i).source().path();
					if (sourceName.equals(sourceFileGiven)) 
						isFoundSourceFile = true;
				}
				if (isFoundSourceFile) 
					continue;
				
				Reader reader = source.open();
				ErrorQueue eq = job.extensionInfo().compiler().errorQueue();
				Parser p = job.extensionInfo().parser(reader, source, eq);
				Node ast;
				if (astMap.containsKey(sourceName))
					ast = astMap.get(sourceName);
				else {
					ast = p.parse();
					astMap.put(sourceName, ast);
				}
				
				TypeVisitor tVisitor = new TypeVisitor(packageName, typeName, (SourceFile_c) job.ast(), job);

				ast.visit(tVisitor);

				if (tVisitor.isFound()) {
					tVisitor.createTypeReferenceEnd();
					return;
				}
			}
		}
		
		public boolean addFileIndex() {
			if (++fileIndex == numSourceFile) {
				--fileIndex;
				return false;
			}
			return true;
		}
		
		public void subFileIndex() {
			if (--fileIndex < 0)
				++fileIndex;
		}
		
		public void visitDeclarations() {
			isDecl = true;
			SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex);
			FileStatus fileStatus = ExtensionInfo.fileHandledMap.get(file);
			fileStatus.handleDecl();
			ExtensionInfo.fileHandledMap.put(file, fileStatus);
			visit(file);
		}
		
		public void visitDefinitions() {
			isDecl = false;
			SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex);
			FileStatus fileStatus = ExtensionInfo.fileHandledMap.get(file);
			fileStatus.handleDef();
			ExtensionInfo.fileHandledMap.put(file, fileStatus);
			JNI.cactionSetCurrentFilePath(file.source().path());
			visit(file);
		}
		
		public void handleClassMembers(List<ClassMember> members, String package_name, String type_name) {
			for (int i = 0; i < members.size(); ++i) {
				JL m = members.get(i);
				if (m instanceof X10MethodDecl_c) {
					X10MethodDecl_c methodDecl = (X10MethodDecl_c) m;
					StringBuffer param = new StringBuffer();
					for (Formal f : methodDecl.formals()) {
						param.append(f.type().toString().toLowerCase());
					}
					memberMap.put(methodDecl.name().toString() + "(" + param + ")", i);
					classMemberMap.put(type_name, memberMap);
					previsit(methodDecl, package_name, type_name);
				} else if (m instanceof X10ConstructorDecl_c) {
					X10ConstructorDecl_c constructorDecl = (X10ConstructorDecl_c) m;
					StringBuffer param = new StringBuffer();
					for (Formal f : constructorDecl.formals()) {
						param.append(f.type().toString().toLowerCase());
					}
					memberMap.put(((X10ConstructorDecl_c) m).name().toString() + "(" + param + ")", i);
					classMemberMap.put(type_name, memberMap);
					previsit(constructorDecl, package_name, type_name);
				} else if (m instanceof X10FieldDecl_c) {
					X10FieldDecl_c fieldDecl = (X10FieldDecl_c) m;
					memberMap.put(((X10FieldDecl_c) m).name().toString(), i);
					classMemberMap.put(type_name, memberMap);
					previsit(fieldDecl, type_name);
				} else if (m instanceof TypeNode_c) {
					System.out.println("TypeNode_c : " + m);
				} else if (m instanceof TypeDecl_c) {
					System.out.println("TypeDecl_c : " + m);
				// nested class 
				} else if (m instanceof X10ClassDecl_c) {
					System.out.println("Previsit ClassDecl_c : " + m + ", package=" + package_name + ", type=" + type_name);
					visitDeclarations((X10ClassDecl_c) m);
				} else if (m instanceof ClassDecl_c) {
					System.out.println("ClassDecl_c : " + m);
				} else {
					System.out.println("Unhandled node : " + m);
				}
			}
		}
		
		public void visit(SourceFile_c n) {
			toRose(n, "First visit SourceFile_c:" + n.source().path());
			
			if (isGatheringFile) {
				++numSourceFile;
				x10rose.ExtensionInfo.X10Scheduler.sourceList.add(n);
				FileStatus fileStatus = new FileStatus(n);
				ExtensionInfo.fileHandledMap.put(n, fileStatus);
				return;
			}
			
			if (isDecl) {
				JNI.cactionCompilationUnitList(1, new String[]{n.source().path()});
				JNI.cactionSetupSourceFilename(n.source().path());
				
				package_ref.add("x10.lang"); // auto-import
				package_ref.add("x10.io");	
				imports = n.imports();
				
				PackageNode pnode = n.package_();
				String package_name = (pnode == null)? "" : pnode.toString();
				if (package_name.length() != 0) {
					JNI.cactionInsertImportedPackageOnDemand(package_name, createJavaToken(n, n.source().path()));
					package_ref.add(package_name);
					currentPackageName = package_name;
				}
				JNI.cactionPushPackage(package_name, createJavaToken(n, n.source().path()));
				JNI.cactionPopPackage();
				
				JNI.cactionCompilationUnitDeclaration(n.source().path(), package_name, n.source().path(), createJavaToken(n, n.source().path()));
			}
			
			visitChildren(n, n.decls());
		}
		
		public void visit(Import_c n) {
			toRose(n, "Import:", n.kind() + " " + n.name().toString());
		}
		
		public void visit(PackageNode_c n) {
			toRose(n, "Package:", n.package_().get().toString());
		}
		
//		public void visitDeclarations(X10ClassDecl_c n) {
//			visitDeclarations(n, null);
//		}
		
		public void visitDeclarations(X10ClassDecl_c n) {
			toRose(n, "X10ClassDecl in visitDeclarations:", n.name().id().toString());
//			SourceFile_c srcfile = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex);
//			List<Import> imports = srcfile.imports();
//			for (Import import_ : imports) {
//				System.out.println("Import=" + import_.name() + ", kind=" + import_.kind());
//			if (import_.kind() == Import.CLASS) {
//				String importedClass = import_.name().toString();
//				int lastDot = importedClass.lastIndexOf('.');
//				String package_= importedClass.substring(0, lastDot);
//				String type_ = importedClass.substring(lastDot+1);
//				System.out.println("class=" + type_ + ", pacakge=" + package_);
//				if (import_.name().toString().equals(type_)) {
//				}
//			}
//			}
			Ref ref = n.classDef().package_();
			String package_name = (ref==null)? "" : ref.toString();
			
			String class_name = n.name().id().toString();
//			if (parent != null)
//				class_name = parent + "." + class_name;

			classMemberMap.put(class_name, memberMap);
			JNI.cactionSetCurrentClassName(package_name + "." + class_name);

			if (package_name.length() != 0)
				JNI.cactionPushPackage(package_name, createJavaToken(n, class_name));
			JNI.cactionInsertClassStart(class_name, false, false, false, createJavaToken(n, class_name));

			// does not consider nested class so far
			JNI.cactionInsertClassEnd(class_name, createJavaToken(n, class_name));
			
	        List<ClassMember> members = ((X10ClassBody_c)n.body()).members();
			List<TypeParamNode> typeParamList = n.typeParameters();
			
			String[] typeParamNames = new String[typeParamList.size()];
			for (int i = 0; i < typeParamList.size(); ++i) {
				String typeParam = typeParamList.get(i).name().toString();
				typeParamNames[i] = typeParam;
//				typeParamNames[i] = package_name + "." + class_name + "." + typeParam;
				JNI.cactionSetCurrentClassName(typeParam);
//				JNI.cactionSetCurrentClassName(package_name + "." + class_name + "." + typeParam);
				JNI.cactionInsertClassStart(typeParam, false, false, false, createJavaToken(n, typeParam));
				JNI.cactionInsertClassEnd(typeParam, createJavaToken(n, typeParam));
//				JNI.cactionPushTypeParameterScope("", typeParam, createJavaToken(n, typeParam));
				JNI.cactionPushTypeParameterScope(package_name, class_name, createJavaToken(n, typeParam));
				JNI.cactionInsertTypeParameter(typeParam, createJavaToken(n, typeParam));
				JNI.cactionBuildTypeParameterSupport(package_name, class_name, -1, typeParam, 0, createJavaToken(n, typeParam));
			}
			if (typeParamList.size() > 0) 
				JNI.cactionSetCurrentClassName(package_name + "." + class_name);
		
	        JNI.cactionBuildClassSupportStart(/*"::" + package_name+"::"+ */class_name, "", true, // a user-defined class?
	                   false, false, false,	false,	createJavaToken(n, class_name));

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
        		String interfaceName = trimTypeParameterClause(intface.toString());		
	        	interfaceNames[i] = interfaceName;
	        	visit((X10CanonicalTypeNode_c) intface);
	        }
	        
	        JNI.cactionBuildClassExtendsAndImplementsSupport(typeParamList.size(), typeParamNames,
															superClass != null, superClassName,
															interfaces == null ? 0 : interfaces.size(), 
															interfaceNames,
															createJavaToken(n, class_name));
			
	        handleClassMembers(members, package_name, class_name);
	        
	        JNI.cactionBuildClassSupportEnd(class_name, createJavaToken(n, class_name));

	        if (package_name.length() != 0)
	        	JNI.cactionPopPackage();
	        
			Flags flags = n.flags().flags();
			JNI.cactionTypeDeclaration(package_name, class_name, /*num_annotations*/0, 
									n.superClass() != null, /*is_annotation_interface*/false, flags.isInterface(), 
									/*is_enum*/false, flags.isAbstract(), flags.isFinal(), flags.isPrivate(), 
									flags.isPublic(), flags.isProtected(), flags.isStatic(), /*is_strictfp*/false, 
									createJavaToken(n, class_name));
		}
		
		public void visitDefinitions(X10ClassDecl_c n) {
			toRose(n, "X10ClassDecl in visitDefinitions:", n.name().id().toString());
			String class_name = n.name().id().toString();
			
			Ref ref = n.classDef().package_();
			String package_name = (ref==null)? "" : ref.toString();
			
			if (package_name.length() != 0) {
				JNI.cactionPushPackage(package_name, createJavaToken(n, n.toString()));
				JNI.cactionInsertClassStart(class_name, false, false, false, createJavaToken(n, class_name));
			}
			// Are the following five lines necessary again?
			visitChildren(n, n.typeParameters());
			visitChildren(n, n.properties());
			visitChild(n, n.classInvariant());
			visitChild(n, n.superClass());
			visitChildren(n, n.interfaces());		
			
			visitChild(n, n.body());			
			JNI.cactionTypeDeclarationEnd(true, createJavaToken(n, class_name));
		}
		
		public void visit(X10ClassDecl_c n) {
			toRose(n, "X10ClassDecl:", n.name().id().toString());
			if (isDecl) {
				visitDeclarations(n);
			}
			else {
				visitDefinitions(n);
			}
		}

		public void visit(LocalClassDecl_c n) {
			toRose(n, "LocalClassDecl:");
			visitChild(n, n.decl());
		}

		public void visit(X10ClassBody_c n) {
			toRose(n, "classBody: ");
			// don't know what kind of caction* should be invoked here! (2014/01/30 horie)
			visitChildren(n, n.members());
		}
		
		public void visit(X10MethodDecl_c n) {
			toRose(n, "method decl: ", n.name().id().toString());
			if (n.body() == null)
				return;
			
			List<Formal> formals = n.formals();
			
			String method_name = n.name().id().toString();
			StringBuffer param = new StringBuffer();
			for (Formal f : n.formals()) {
				param.append(f.type().toString().toLowerCase());
			}

//          JNI.cactionBuildMethodSupportStart(method_name, method_index, createJavaToken());
//			visitChild(n, n.returnType());
//			visitChildren(n, n.formals());
			int method_index = memberMap.get(method_name+"("+param+")");
			
			JNI.cactionMethodDeclaration(method_name, method_index, formals.size(), 
					createJavaToken(n, method_name),
					createJavaToken(n, method_name+"("+param+")"));
			
//           JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
//												 false, false, false, 0, formals.size(),
//                                           true, /* user-defined-method */
//					new JavaToken(n.name().id().toString(), new JavaSourcePositionInformation(n.position().line())), 
//					new JavaToken(n.name().id().toString()+"_args", new JavaSourcePositionInformation(n.position().line())));

//			visitChild(n, n.guard());
//			visitChild(n, n.offerType());
//			visitChildren(n, n.throwsTypes());

			Flags flags = n.flags().flags();
			JNI.cactionMethodDeclarationHeader(method_name, flags.isAbstract(), flags.isNative(), flags.isStatic(), 
															flags.isFinal(), /*java_is_synchronized*/false, flags.isPublic(), 
															flags.isProtected(), flags.isPrivate(), /*java_is_strictfp*/false, 
															n.typeParameters().size(), formals.size(), n.throwsTypes().size(), 
															createJavaToken(n, method_name));
			visitChild(n, n.body());
		    JNI.cactionMethodDeclarationEnd(n.body().statements().size(), createJavaToken(n, method_name+"("+param+")"));
//		    JNI.cactionMethodDeclarationEnd(0, createJavaToken());
		}
		
		public void previsit(X10MethodDecl_c n, String package_name, String class_name) {
			toRose(n, "Previsit method decl: ", n.name().id().toString());
			// TODO: currently, skip operator
			if (n.name().id().toString().indexOf("operator") >= 0)
				return;
			List<Formal> formals = n.formals();
			
			String method_name = n.name().id().toString();
			StringBuffer param = new StringBuffer();
			for (Formal f : n.formals()) {
				param.append(f.type().toString().toLowerCase());
			}

			int method_index = memberMap.get(method_name+"("+param+")");
			
			JNI.cactionBuildMethodSupportStart(method_name, method_index, 
					createJavaToken(n, method_name+"("+param+")"));
			
			List<TypeParamNode> typeParamList = n.typeParameters();
			for (int i = 0; i < typeParamList.size(); ++i) {
				String typeParam = typeParamList.get(i).name().toString();
				JNI.cactionPushTypeParameterScope(package_name, class_name, createJavaToken(n, typeParam));
				JNI.cactionInsertTypeParameter(typeParam, createJavaToken(n, typeParam));
				JNI.cactionBuildTypeParameterSupport(package_name, class_name, method_index, typeParam, 0, createJavaToken(n, typeParam));
				JNI.cactionPopTypeParameterScope(createJavaToken(n, typeParam));
			}
			// in case the return type is unknown. Such a case will occur by writing 
			// like"public def toString() = "Place(" + this.id + ")";"
			if (n.returnType() instanceof UnknownTypeNode_c)
				JNI.cactionTypeReference("", "void", this, createJavaToken());
			else 
				visitChild(n, n.returnType());

			visitChildren(n, n.formals());
			
/*
			JNI.cactionMethodDeclaration(n.name().id().toString(), method_index++, formals.size(), 
					new JavaToken(n.name().id().toString(), new JavaSourcePositionInformation(n.position().line())), 
					new JavaToken(n.name().id().toString()+"_args", new JavaSourcePositionInformation(n.position().line())));
*/
			
           JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
											false, false, false, 0, formals.size(),
											true, /* user-defined-method */
											createJavaToken(n, n.name().id().toString()),
                                           	createJavaToken(n, n.name().id().toString()+"_args"));
		}
		
		public void previsit(X10ConstructorDecl_c n, String package_name, String class_name) {
			toRose(n, "Previsit constructor decl: ", n.name().id().toString());
			List<Formal> formals = n.formals();			
			String method_name = n.name().id().toString();
			StringBuffer param = new StringBuffer();
			for (Formal f : n.formals()) {
				param.append(f.type().toString().toLowerCase());
			}
			
			int method_index = memberMap.get(method_name+"("+param+")");
			
			List<TypeParamNode> typeParamList = n.typeParameters();
			for (int i = 0; i < typeParamList.size(); ++i) {
				String typeParam = typeParamList.get(i).name().toString();
				JNI.cactionPushTypeParameterScope(package_name, class_name, createJavaToken(n, typeParam));
				JNI.cactionInsertTypeParameter(typeParam, createJavaToken(n, typeParam));
				JNI.cactionBuildTypeParameterSupport(package_name, class_name, method_index, typeParam, 0, createJavaToken(n, typeParam));
				JNI.cactionPopTypeParameterScope(createJavaToken(n, typeParam));
			}
			
			JNI.cactionBuildMethodSupportStart(method_name, method_index, 
					createJavaToken(n, method_name+"("+param+")")
//					new JavaToken(method_name+"("+param+")", new JavaSourcePositionInformation(n.position().startOf().line(), n.position().endLine()))
			);
        
			JNI.cactionTypeReference("", "void", this, createJavaToken());
			visitChildren(n, n.formals());

			JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
												 false, false, false, 0, formals.size(),
                                           true, /* user-defined-method */
                                           createJavaToken(n, n.name().id().toString())
					/*new JavaToken(n.name().id().toString(), new JavaSourcePositionInformation(n.position().line()))*/, 
					createJavaToken(n, n.name().id().toString()+"_args")
					/*new JavaToken(n.name().id().toString()+"_args", new JavaSourcePositionInformation(n.position().line()))*/);
		}
		
	     public void previsit(X10FieldDecl_c fieldDecl, String class_name) {
	            String fieldName = fieldDecl.name().id().toString();
	            toRose(fieldDecl, "Previsit field decl: ", fieldName);
	            String package_name = fieldDecl.type().type().fullName().qualifier().toString();
	            String type_name = fieldDecl.type().nameString();
	            
	    		int token_constraint;
	    		// TODO: remove this when type constraint is supported
	    		if ((token_constraint = type_name.indexOf('{')) > 0) { 
	    			type_name = type_name.substring(0, token_constraint);
	    		}
				if (package_name.equals("x10.lang") &&
					(  type_name.equals("Boolean")
					|| type_name.equals("Byte")
					|| type_name.equals("Char")
					|| type_name.equals("Int")
					|| type_name.equals("Short")
					|| type_name.equals("Float")
					|| type_name.equals("Long")
					|| type_name.equals("Double"))) {
					JNI.cactionTypeReference("", type_name, this, createJavaToken());
				}
				else if (package_name.length() != 0) {
					JNI.cactionPushPackage(package_name, createJavaToken(fieldDecl, type_name));
					JNI.cactionPopPackage();
					JNI.cactionTypeReference(package_name, type_name, this, createJavaToken());
				}
	            JNI.cactionBuildFieldSupport(fieldName, createJavaToken());
	            
	            Flags flags = fieldDecl.flags().flags();
	            
//	            boolean hasInitializer = (fieldDecl.init() != null);
//	            if (hasInitializer) {
////					JNI.cactionAssignment(createJavaToken(fieldDecl, fieldDecl.toString()));		
//					visitChild(fieldDecl, fieldDecl.init());
////	                JNI.cactionSingleNameReference("", "", fieldName, createJavaToken(fieldDecl, fieldDecl.name().id().toString()));
//	             
////	                JNI.cactionAssignmentEnd(createJavaToken(fieldDecl, fieldDecl.toString()));
//
////					int field_index = memberMap.get(fieldName);
////	              	
////					JNI.cactionBuildInitializerSupport(flags.isStatic(), fieldDecl.init().toString()/*fieldName*/,
////	                                                  field_index,
////	                                                  createJavaToken());
////	    			JNI.cactionAssignmentEnd(createJavaToken(fieldDecl, fieldDecl.toString()));
//	            }

	            // MH-20140401 
	            // needs to invoke cactionTypeReference again, since cactionFieldDeclarationEnd
	            // first pop SgType for "!is_enum_field"
//	            JNI.cactionTypeReference("", fieldDecl.type().nameString());


	            JNI.cactionFieldDeclarationEnd(fieldName,
	                                            false, // is_enum_field
	                                            false,//hasInitializer,
	                                            flags.isFinal(),
	                                            flags.isPrivate(),
	                                            flags.isProtected(),
	                                            flags.isPublic(),
	                                            false, // java_is_volatile
	                                            false, // java_is_synthetic
	                                            flags.isStatic(),
	                                            flags.isTransient(),
	                                            createJavaToken());


	            toRose(fieldDecl, "Previsit field decl end: ", fieldName);
	        }


		public void visit(X10Formal_c n) {
			toRose(n, "formal: ", n + "");

//                args_location = createJavaToken(args[0], args[args.length - 1]); 
//
//                for (int j = 0; j < args.length; j++) {
//                    Argument arg = args[j];
//                    JavaToken arg_location = createJavaToken(arg);
//                    generateAndPushType(arg.type.resolvedType, arg_location);
//                    String argument_name = new String(arg.name);
//                    JavaParser.cactionBuildArgumentSupport(argument_name,
//                                                           arg.isVarArgs(),
//                                                           arg.binding.isFinal(),
//                                                           arg_location);
//                }
			visitChild(n, n.type());
			// so far, all parameters's modifier are set as final.
			JNI.cactionBuildArgumentSupport(n.name().toString(), n.vars().size()>0, false, createJavaToken(n, n.name().id().toString()));
		}
		
		private void handleArgumentTypes(List<Expr> args) {
			for (int i = 0; i < args.size(); ++i) {
				Type t = args.get(i).type();
				String arg_type_name = t.name().toString();
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
				System.out.println(i + ":" + t.fullName() + ", " + args.get(i) + ", pacakge=" + arg_package_name + ", type=" + arg_type_name);
				
				if (arg_package_name.equals("x10.lang")
						&& (	arg_type_name.equals("Boolean")
							||	arg_type_name.equals("Byte")
							||	arg_type_name.equals("Char")
							||	arg_type_name.equals("Int")
							||	arg_type_name.equals("Short")
							||	arg_type_name.equals("Float")
							||	arg_type_name.equals("Long")
							||	arg_type_name.equals("Double"))) {
					JNI.cactionTypeReference("", arg_type_name, this, createJavaToken());
				}
				else if (	full.equals("x10.lang.Rail")
						 || full.equals("x10.util.GrowableRail")) {
					String railString = t.toString();
					String type = railString.substring(railString.indexOf('[')+1, railString.indexOf(']'));
					int lastDot = type.lastIndexOf(".");
					arg_package_name = type.substring(0, lastDot);
					arg_type_name = type.substring(lastDot+1);

					if (arg_package_name.equals("x10.lang")
							&& (	arg_type_name.equals("Boolean")
								||	arg_type_name.equals("Byte")
								||	arg_type_name.equals("Char")
								||	arg_type_name.equals("Int")
								||	arg_type_name.equals("Short")
								||	arg_type_name.equals("Float")
								||	arg_type_name.equals("Long")
								||	arg_type_name.equals("Double")))	{
						arg_package_name = "";
					}
					
					if (arg_package_name.length() != 0) {
						JNI.cactionPushPackage(arg_package_name, createJavaToken());
						JNI.cactionPopPackage();
					}
					JNI.cactionTypeReference(arg_package_name, arg_type_name, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
				}
				else {				
					if (arg_package_name.length() != 0) {
						JNI.cactionPushPackage(arg_package_name, createJavaToken());
						JNI.cactionPopPackage();
					}
					JNI.cactionTypeReference(arg_package_name, arg_type_name, this, createJavaToken());
				}
			}
		}

		public void visit(X10Call_c n) {
			toRose(n, "x10call: ", n.name().id().toString());
			System.out.println("n=" + n.name().id() + ", type=" + n.target().type() + ", package=" + n.target().type().toPackage());
			String func_name = n.name().id().toString();
			String class_name = n.target().type().toString();
			if (func_name.equals("operator()")) {
				if (n.target().type().isRail()) {
					visitChild(n, n.target());
					List<Expr> args = n.arguments();
					visitChildren(n, args);
					JNI.cactionArrayReferenceEnd(createJavaToken(n, n.toString()));
				} 
				else {
					System.out.println("So far, return in case of \"operator()\"");
//					visitChild(n, n.target());
				}
				return;
			}
			else if (func_name.equals("implicit_operator_as")
					|| func_name.equals("operator_as")) {
				visitChild(n, n.target());
				visitChildren(n, n.arguments());
				JNI.cactionCastExpressionEnd(createJavaToken(n, n.toString()));
				
				return;
			}
			if (class_name.equals("x10.lang.Long")
					&& (	func_name.equals("parse")
						|| 	func_name.equals("parseLong"))
					) {                                   
				
				String callerClass = JNI.cactionGetCurrentClassName();
				
				String helperName = "X10RoseUtility_" + func_name;
				String returnType = "void";
				String methodName = "Long_" + func_name;
				String argType = "x10.lang.String";
				String argName = n.arguments().get(0).toString();
				System.out.println("ARG_Class=" + n.arguments().get(0).getClass());
				
				// MH-20140812 TODO: remove when operator is available
				argName = argName.replaceAll(".operator\\(\\)", "");
				
				int methodIndex = 1;
				
				JNI.cactionSetCurrentClassName(helperName);
				JNI.cactionInsertClassStart(helperName, false, false, false, createJavaToken(n, helperName));
				JNI.cactionInsertClassEnd(helperName, createJavaToken(n, helperName));
				
		        JNI.cactionBuildClassSupportStart(helperName, "", true, // a user-defined class?
		                   false, false, false,	false,	createJavaToken(n, helperName));

		        String[] typeParamNames = new String[0];
		        String[] interfaceNames = new String[0];

				JNI.cactionBuildClassExtendsAndImplementsSupport(0,
						typeParamNames, false, "", 0, interfaceNames,
						createJavaToken(n, n.toString()));

				JNI.cactionBuildMethodSupportStart(methodName, methodIndex,
						createJavaToken(n, methodName));
				
				// build return type
				JNI.cactionTypeReference("", returnType, this, createJavaToken());
		
				// build an argument
				JNI.cactionPushPackage("x10.lang", createJavaToken(n, helperName));
				JNI.cactionPopPackage();						
				JNI.cactionTypeReference("x10.lang", "String", this, createJavaToken());
				JNI.cactionBuildArgumentSupport(argName, false, false, createJavaToken(n, argName));

		        JNI.cactionBuildMethodSupportEnd(methodName, methodIndex, // method index l
													false, false, false, 0, 1,
													true, /* user-defined-method */
													createJavaToken(n, n.name().id().toString()),
		                                           	createJavaToken(n, n.name().id().toString()+"_args"));
				

		        JNI.cactionBuildClassSupportEnd(helperName, createJavaToken(n, helperName));
		        
				JNI.cactionTypeDeclaration("", helperName, /*num_annotations*/0, 
										false, /*is_annotation_interface*/false, false, 
										/*is_enum*/false, false, false, false, 
										true, false, false, /*is_strictfp*/false, 
										createJavaToken(n, helperName));

				JNI.cactionSetCurrentClassName(callerClass);
								
				JNI.cactionMessageSend("", helperName, methodName, createJavaToken(n, helperName));
				JNI.cactionTypeReference("", helperName, this, createJavaToken(n, helperName));

				visitChildren(n, n.arguments());
				handleArgumentTypes(n.arguments());

				JNI.cactionTypeReference("", helperName, this, createJavaToken());

				JNI.cactionMessageSendEnd(true, true, methodName, 1, 0, 1, createJavaToken(n, helperName));
				
				return;
			}
			
			polyglot.types.Package package_ = n.target().type().toPackage();
			String package_name = "";
			if (package_ != null)
				package_name = package_.toString();
			else {
				String full = n.target().type().fullName().toString();
				int lastDot = full.lastIndexOf('.');
				if (lastDot > 0)
					package_name = full.substring(0, lastDot);
			}
			if (package_name.length() != 0) {
				JNI.cactionPushPackage(package_name, createJavaToken(n, package_name));
				JNI.cactionPopPackage();
			}
			
			JNI.cactionMessageSend(package_name, n.target().type().name().toString(), n.name().id().toString(), createJavaToken(n, n.name().id().toString()));
			visitChild(n, n.target());
			
			List<Expr> args = n.arguments();
			List<Expr> argTypes = new ArrayList<Expr>();
			
			for (int i = 0; i < args.size(); ++i) {
				Node n2 = args.get(i);		
				visitChild(n2, n2.node());
			}
			
			handleArgumentTypes(args);
//			for (int i = 0; i < args.size(); ++i) {
//				Type t = args.get(i).type();
//				String arg_type_name = t.name().toString();
//				String full = t.fullName().toString();
//				
//				polyglot.types.Package arg_package_ = t.toPackage();
//				String arg_package_name = "";
//				if (arg_package_ != null)
//					arg_package_name = arg_package_.toString();
//				else {
//					int lastDot = full.lastIndexOf('.');
//					if (lastDot > 0)
//						arg_package_name = full.substring(0, lastDot);
//				}				
//				System.out.println(i + ":" + t.fullName() + ", " + args.get(i) + ", pacakge=" + arg_package_name + ", type=" + arg_type_name);
//				
//				if (arg_package_name.equals("x10.lang")
//						&& (	arg_type_name.equals("Boolean")
//							||	arg_type_name.equals("Byte")
//							||	arg_type_name.equals("Char")
//							||	arg_type_name.equals("Int")
//							||	arg_type_name.equals("Short")
//							||	arg_type_name.equals("Float")
//							||	arg_type_name.equals("Long")
//							||	arg_type_name.equals("Double"))) {
//					JNI.cactionTypeReference("", arg_type_name, this, createJavaToken());
//				}
//				else if (	full.equals("x10.lang.Rail")
//						 || full.equals("x10.util.GrowableRail")) {
//					String railString = t.toString();
//					String type = railString.substring(railString.indexOf('[')+1, railString.indexOf(']'));
//					int lastDot = type.lastIndexOf(".");
//					arg_package_name = type.substring(0, lastDot);
//					arg_type_name = type.substring(lastDot+1);
//
//					if (arg_package_name.equals("x10.lang")
//							&& (	arg_type_name.equals("Boolean")
//								||	arg_type_name.equals("Byte")
//								||	arg_type_name.equals("Char")
//								||	arg_type_name.equals("Int")
//								||	arg_type_name.equals("Short")
//								||	arg_type_name.equals("Float")
//								||	arg_type_name.equals("Long")
//								||	arg_type_name.equals("Double")))	{
//						arg_package_name = "";
//					}
//					
//					if (arg_package_name.length() != 0) {
//						JNI.cactionPushPackage(arg_package_name, createJavaToken(n, arg_package_name));
//						JNI.cactionPopPackage();
//					}
//					JNI.cactionTypeReference(arg_package_name, arg_type_name, this, createJavaToken());
//					JNI.cactionArrayTypeReference(1, createJavaToken());
//				}
//				else
//					JNI.cactionTypeReference(arg_package_name, arg_type_name, this, createJavaToken());
//			}
			
			String target_type_name = n.target().type().name().toString();						
			if (package_name.equals("x10.lang")
				&& (	target_type_name.equals("Boolean")
					||	target_type_name.equals("Byte")
					||	target_type_name.equals("Char")
					||	target_type_name.equals("Int")
					||	target_type_name.equals("Short")
					||	target_type_name.equals("Float")
					||	target_type_name.equals("Long")
					||	target_type_name.equals("Double"))) {
				JNI.cactionTypeReference("", target_type_name, this, createJavaToken());
			}
			else
				JNI.cactionTypeReference(package_name, target_type_name, this, createJavaToken());
//			JNI.cactionTypeReference(package_name, n.target().type().name().toString(), this, createJavaToken());
			
			int num_parameters = n.arguments().size();
			int numTypeArguments = n.typeArguments().size();
			int numArguments = n.arguments().size();
			JNI.cactionMessageSendEnd(n.methodInstance().flags().isStatic(), n.target() != null, n.name().toString(), num_parameters, numTypeArguments, numArguments, createJavaToken(n, n.name().id().toString()));
		}

		public void visit(X10ConstructorDecl_c n) {
			toRose(n, "X10ConstructorDecl:", n.name().id().toString());
			if (n.body() == null)
				return;
			
			List<Formal> formals = n.formals();
			
			String method_name = n.name().id().toString();
			StringBuffer param = new StringBuffer();
			for (Formal f : n.formals()) {
				param.append(f.type().toString().toLowerCase());
			}

//          JNI.cactionBuildMethodSupportStart(method_name, method_index, createJavaToken());
//			visitChild(n, n.returnType());
//			visitChildren(n, n.formals());
			int method_index = memberMap.get(method_name+"("+param+")");

			JNI.cactionMethodDeclaration(n.name().id().toString(), method_index, formals.size(), 
					createJavaToken(n, n.name().id().toString())
					/*new JavaToken(n.name().id().toString(), new JavaSourcePositionIcactionMessageSendEndnformation(n.position().line()))*/, 
					createJavaToken(n, n.name().id().toString()+"_args")
					/*new JavaToken(n.name().id().toString()+"_args", new JavaSourcePositionInformation(n.position().line()))*/);

//           JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
//												 false, false, false, 0, formals.size(),
//                                           true, /* user-defined-method */
//					new JavaToken(n.name().id().toString(), new JavaSourcePositionInformation(n.position().line())), 
//					new JavaToken(n.name().id().toString()+"_args", new JavaSourcePositionInformation(n.position().line())));

//			visitChild(n, n.guard());
//			visitChild(n, n.offerType());
//			visitChildren(n, n.throwsTypes());
			Flags flags = n.flags().flags();
			JNI.cactionMethodDeclarationHeader(method_name, flags.isAbstract(), flags.isNative(), flags.isStatic(), 
															flags.isFinal(), /*java_is_synchronized*/false, flags.isPublic(), 
															flags.isProtected(), flags.isPrivate(), /*java_is_strictfp*/false, 
															n.typeParameters().size(), formals.size(), n.throwsTypes().size(), 
															createJavaToken(n, method_name));
			visitChild(n, n.body());

			JNI.cactionMethodDeclarationEnd(n.body().statements().size(), createJavaToken(n, method_name+"("+param+")"));
//			String constructor_name = n.name().toString();
//
//			JNI.cactionConstructorDeclarationHeader(constructor_name, false,
//					false, n.typeParameters().size(), n.formals().size(), n
//							.throwsTypes().size(), createJavaToken());
//			JNI.cactionConstructorDeclarationEnd(n.body().statements().size(),
//					createJavaToken());
//
//			visitChildren(n, n.formals());
////			visitChild(n, n.guard());
////			visitChild(n, n.offerType());
////			visitChildren(n, n.throwsTypes());
//			visitChild(n, n.body());			
		}

		public void visit(X10ConstructorCall_c n) {
			toRose(n, "X10ConstructorCall:");
			visitChild(n, n.target());
			visitChildren(n, n.typeArguments());
			visitChildren(n, n.arguments());
		}

		public void visit(Block_c n) {
			toRose(n, "Block: ", n.toString());
//			System.out.println("Block start : " + n.statements().size() + ", " + n.statements());
		   JNI.cactionBlock(createJavaToken(n, n.toString()));
			visitChildren(n, n.statements());
			JNI.cactionBlockEnd(n.statements().size(), createJavaToken(n, n.toString()));
		}

		public void visit(StmtSeq_c n) {
			toRose(n, "StmtSeq: ");
			visitChildren(n, n.statements());
		}

		public void visit(AssignPropertyCall_c n) {
			toRose(n, "AssignPropertyCall:");
			// MH-20140313 go through at this moment
			// Tentatively process empty statement instead of propertycall
			JNI.cactionEmptyStatement(createJavaToken(n, n.toString()));
			JNI.cactionEmptyStatementEnd(createJavaToken(n, n.toString()));
//			visitChildren(n, n.arguments());
		}

		public void visit(Empty_c n) {
			toRose(n, "Empty:");
			JNI.cactionEmptyStatement(createJavaToken(n, n.toString()));
			JNI.cactionEmptyStatementEnd(createJavaToken(n, n.toString()));
		}
		
		public void visit(ArrayTypeNode_c n) {
			toRose(n, "ArrayTypeNode:", n.nameString());
		}
		
		public void visit(ArrayAccess_c n) {
			toRose(n, "ArrayAccess:", n.toString());
		}

		public void visit(X10CanonicalTypeNode_c n) {
			toRose(n, "X10CanonicalTypeNode:", n.nameString(), n.type()+"", n.type() + "", n.type().fullName() + "");
			String class_name = n.type().fullName().toString();
			if (	class_name.equals("void")
				||	class_name.equals("x10.lang.Boolean")
				||	class_name.equals("x10.lang.Byte")
				||	class_name.equals("x10.lang.Char")
				||	class_name.equals("x10.lang.Int")
				||	class_name.equals("x10.lang.Short")
				||	class_name.equals("x10.lang.Float")
				||	class_name.equals("x10.lang.Long")
				||	class_name.equals("x10.lang.Double")) {
				String canonicalTypeName = n.nameString();
				JNI.cactionTypeReference("", canonicalTypeName, this, createJavaToken());
			}
			else if (	  n.type().toString().indexOf("x10.lang.Rail[") == 0
					  || n.type().toString().indexOf("x10.util.GrowableRail[") == 0) {
				String railString = n.type().toString();
				class_name = railString.substring(railString.indexOf('[')+1, railString.indexOf(']'));
				int lastDot = class_name.lastIndexOf(".");
				String package_name = "";
				if (lastDot > 0) 
					package_name = class_name.substring(0, lastDot);
				if (	class_name.equals("x10.lang.Boolean")
					||	class_name.equals("x10.lang.Byte")
					||	class_name.equals("x10.lang.Char")
					||	class_name.equals("x10.lang.Int")
					||	class_name.equals("x10.lang.Short")
					||	class_name.equals("x10.lang.Float")
					||	class_name.equals("x10.lang.Long")
					||	class_name.equals("x10.lang.Double"))	{
					package_name = "";
				}
				if (package_name.length() != 0) {
					JNI.cactionPushPackage(package_name, createJavaToken(n, package_name));
					JNI.cactionPopPackage();
				}				
				String type = class_name.substring(lastDot+1);
				JNI.cactionTypeReference(package_name, type, this, createJavaToken());
				JNI.cactionArrayTypeReference(1, createJavaToken());
			}
			else if (n.node().toString().indexOf("{self") >= 0) { 
				String className = n.node().toString();
				int index = className.indexOf("[");
				if (index >= 0)
					className = className.substring(0, index);
				
				index = className.indexOf("{");
				if (index >= 0)
					className = className.substring(0, index);
				
				int lastDot = className.lastIndexOf('.');
				String pkg = "";
				String type = "";
				if (lastDot >= 0) {
					pkg = className.substring(0, lastDot);
					type = className.substring(lastDot+1);
				}
				else {
					type = className;
				}
				System.out.println("PACKAGE=" + pkg + ", TYPE=" + className);
				if (pkg.length() != 0) {
					JNI.cactionPushPackage(pkg, createJavaToken(n, currentPackageName));
					JNI.cactionPopPackage();
				}
				JNI.cactionTypeReference(pkg, type, this, createJavaToken());
			}
			else {
				String className = n.node().toString();
				int index = className.indexOf("[");
				if (index < 0)
					index = className.length();
//				if (index >= 0)
//					className = className.substring(0, index);
				int lastDot = className.lastIndexOf('.', index);
				
				String pkg = "";
				String type = "";
				if (lastDot > 0) {
					pkg = className.substring(0, lastDot);
					type = className.substring(lastDot+1);
				}
				else {
					type = className;
				}
				
				System.out.println("className=" + className + ", pkg=" + pkg + ", type=" + type);
				if (pkg.length() != 0) {
					JNI.cactionPushPackage(pkg, createJavaToken(n, pkg));
					JNI.cactionPopPackage();
				}
				else {
					try {
						TypeSystem ts = jobList.get(jobList.size() - 1).extensionInfo().typeSystem();
						boolean isFoundInPackageRef = false;
						for (String package_ : package_ref) {
							
							List<Type> list = new ArrayList<Type>();
							try {
								list = ts.systemResolver().find(QName.make(package_ + "." + type));
							} catch (polyglot.types.NoClassException e) {
								System.out.println(package_ + "." + type + " not found: " + e);
							}
							if (list.size() != 0) {
								Type t = list.get(0);
								JNI.cactionPushPackage(package_, createJavaToken(n, n.toString()));
					    		JNI.cactionPopPackage();
								JNI.cactionTypeReference(package_, type, this, createJavaToken(n, n.toString()));
								isFoundInPackageRef = true;
								break;
							}
						}
						if (isFoundInPackageRef)
							return;

						for (Import import_ : imports) {
							if (import_.kind() == Import.CLASS) {
								String importedClass = import_.name().toString();
								lastDot = importedClass.lastIndexOf('.');
								String package_ = importedClass.substring(0, lastDot);
								String type2_ = importedClass.substring(lastDot + 1);
								if (import_.name().toString().equals(type2_)) {
						    		JNI.cactionPushPackage(package_, createJavaToken(n, n.toString()));
						    		JNI.cactionPopPackage();
									JNI.cactionTypeReference(package_, type2_, this, createJavaToken(n, n.toString()));
									isFoundInPackageRef = true;
									break;
								}
							} else if (import_.kind() == Import.PACKAGE) {
								String package_ = import_.name().toString();
								List<Type> list = new ArrayList<Type>();
								try {
									list = ts.systemResolver().find(QName.make(package_ + "." + type));
								} catch (polyglot.types.NoClassException e) {
									System.out.println(package_ + "." + type + " not found: " + e);
								}
//								List<Type> list = ts.systemResolver().find(QName.make(package_ + "." + type));
								if (list.size() != 0) {
									Type t = list.get(0);
//									String type_ = amb.name().toString();
						    		JNI.cactionPushPackage(package_, createJavaToken(n, n.toString()));
						    		JNI.cactionPopPackage();
									JNI.cactionTypeReference(package_, type, this, createJavaToken(n, n.toString()));
									isFoundInPackageRef = true;
									break;
								}
							}
						}
						
						if (!isFoundInPackageRef)
							// treat as a generic type
							JNI.cactionTypeReference("", type, this, createJavaToken(n, n.toString()));
					
					} catch (SemanticException e) {
						// treat as a generic type
						JNI.cactionTypeReference("", type, this, createJavaToken(n, n.toString()));
					}
					return;
				}
				
				JNI.cactionTypeReference(pkg, type, this, createJavaToken(n, n.toString()));

//				JNI.cactionPushPackage(pkg, createJavaToken(n, pkg));
//				JNI.cactionPushTypeScope(pkg, type, createJavaToken(n, type));
//				JNI.cactionTypeReference(pkg, type, this, createJavaToken());
//				JNI.cactionPopTypeScope();
//				JNI.cactionPopPackage();
//				JNI.cactionTypeReference("", type, this, createJavaToken());
			}
//			JNI.cactionTypeDeclaration("", n.nameString(), false, false, false, false, false, false, false, false, false, false);
		}

		public void visit(Return_c n) {
			toRose(n, "Return:", (String)null);
			JNI.cactionReturnStatement(createJavaToken(n, n.toString()));
			visitChild(n, n.expr());
			JNI.cactionReturnStatementEnd((n.expr() != null), createJavaToken(n, n.toString()));
		}

		public void visit(X10Binary_c n) {
			toRose(n, "X10Binary:", n.operator().toString());					
			JNI.cactionBinaryExpression(createJavaToken(n, n.toString()));	
			visitChild(n, n.left());
			visitChild(n, n.right());
			JNI.cactionBinaryExpressionEnd(getOperatorKind(n.operator()), createJavaToken(n, n.toString()));
		}

		public void visit(X10Unary_c n) {
			toRose(n, "X10Unary:", n.operator().toString());
			visitChild(n, n.expr());
			if (	n.operator() == Unary.Operator.PRE_DEC
				||	n.operator() == Unary.Operator.PRE_INC) {
				JNI.cactionPrefixExpressionEnd(getOperatorKind(n.operator()), createJavaToken(n, n.toString()));
			}
			else if (	n.operator() == Unary.Operator.POST_DEC
				||	n.operator() == Unary.Operator.POST_INC) {
				JNI.cactionPostfixExpressionEnd(getOperatorKind(n.operator()), createJavaToken(n, n.toString()));
			}
		}

		public void visit(ParExpr_c n) { // parentheses
			toRose(n, "ParExpr:");
			visitChild(n, n.expr());
			JNI.cactionParenthesizedExpression(1);
		}		

		public void visit(X10Special_c n) {
			toRose(n, "X10Special:", n.kind().toString());
			String kind = n.kind().toString();

			if (kind.equals(Special.Kind.THIS.toString())) {
				JNI.cactionThisReference(createJavaToken(n, n.kind().toString()));
			} else if (kind.equals(Special.Kind.SUPER.toString())) {
				JNI.cactionSuperReference(createJavaToken(n, n.kind().toString()));
			} else if (kind.equals(Special.Kind.SELF.toString())) {
				System.out.println("X10Special : Unhandled token " + kind);				
			}
		}

		public void visit(Here_c n) {
			toRose(n, "Here:");
			JNI.cactionHere(createJavaToken(n, n.toString()));
		}

		public void visit(X10Local_c n) {
			toRose(n, "X10Local :", n.name().id().toString());
//            JavaParser.cactionSingleNameReference(package_name, type_name, varRefName, javaParserSupport.createJavaToken(node));
			JNI.cactionSingleNameReference("", "", n.name().id().toString(), createJavaToken(n, n.name().id().toString()));
			System.out.println("Leaving X10Local=====");
		}

		public void visit(Eval_c n) {
			toRose(n, "Eval:", n.toString());
			visitChild(n, n.expr());
		}

		public void visit(For_c n) {
			toRose(n, "For:");
			JNI.cactionForStatement(createJavaToken(n, n.toString()));
			visitChildren(n, n.inits());
			visitChild(n, n.cond());
			visitChildren(n, n.iters());
			visitChild(n, n.body());
			JNI.cactionForStatementEnd(n.inits().size(), n.cond() != null, n.iters().size(), createJavaToken(n, n.toString()));
		}

		public void visit(ForLoop_c n) {
			toRose(n, "ForLoop:");
			visitChild(n, n.formal());
			visitChild(n, n.cond());
			visitChild(n, n.domain());
			visitChild(n, n.body());
		}

		public void visit(Branch_c n) {
			toRose(n, "Branch:", n.kind()+(n.labelNode()!=null ? "\\n"+n.labelNode().id().toString() : ""));
		}
		
		public void visit(X10Do_c n) {
			toRose(n, "X10Do:");
			visitChild(n, n.cond());
			visitChild(n, n.body());
		}

		public void visit(X10While_c n) {
			toRose(n, "X10While:", n.cond().toString(), n.body().toString());
			JNI.cactionWhileStatement(createJavaToken(n, n.toString()));
			visitChild(n, n.cond());
			visitChild(n, n.body());		
			JNI.cactionWhileStatementEnd(createJavaToken(n, n.toString()));
		}


		public void visit(Tuple_c n) {
			toRose(n, "Tuple:");
			visitChildren(n, n.arguments());
		}

		public void visit(SettableAssign_c n) {
			toRose(n, "SettableAssign:", n.toString() + ", " + n.left().toString() + ", " + n.index() + ", " + n.right().toString() + ", " + n.index().size());
			JNI.cactionAssignment(createJavaToken(n, n.toString()));
			visitChild(n, n.left());
			visitChild(n, n.right());	
			JNI.cactionAssignmentEnd(createJavaToken(n, n.toString()));
		}

		
		public void visit(FieldAssign_c n) {
			toRose(n, "FieldAssign:", n.name().id().toString());
//			System.out.println("target=" + n.target() + ", right=" + n.right());
			String fieldName = n.name().id().toString();
			String className = n.target().type().fullName().toString();
			int lastDot = className.lastIndexOf('.');
			String pkg = "";
			String type = "";
			if (lastDot >= 0) {
				pkg = className.substring(0, lastDot);
				type = className.substring(lastDot+1);
			}
			else {
				type = className;
			}
			
			visitChild(n, n.target());
			JNI.cactionTypeReference(pkg, type, this, createJavaToken());
			JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, createJavaToken(n, fieldName));
			visitChild(n, n.right());			
			JNI.cactionAssignmentEnd(createJavaToken(n, n.right().toString()));
		}

		public void visit(X10Field_c n) {
			toRose(n, "X10Field:", n.name().id().toString());
			System.out.println("n=" + n
									+ ", target=" + n.target() 
									+ ", target type=" + n.target().type().name() 
									+ ", target type's package=" + n.target().type().fullName()
									+ ", name=" + n.name().id() 
									+ ", instance=" + n.fieldInstance());
			String fieldName = n.name().id().toString();
			String className = n.target().type().fullName().toString();
			int lastDot = className.lastIndexOf('.');
			String pkg = "";
			String type = "";
			if (lastDot >= 0) {
				pkg = className.substring(0, lastDot);
				type = className.substring(lastDot+1);
			}
			else {
				type = className;
			}

			if (className.equals("x10.lang.Rail")) {
				int methodIndex;
				if (fieldName.equals("size")) { 
					String callerClass = JNI.cactionGetCurrentClassName();
					
					String helperName = "X10RoseUtility1";
					String returnType = "Long";
					String methodName = "Rail_size";
					String argClass = n.target().type().toClass().typeArguments().get(0).toString();
					String argClass_package = argClass.substring(0, argClass.lastIndexOf('.'));
					String argClass_type = argClass.substring(argClass.lastIndexOf('.') + 1);
					String argName = n.target().toString();
					methodIndex = 0;
	                
					JNI.cactionSetCurrentClassName(helperName);
					JNI.cactionInsertClassStart(helperName, false, false, false, createJavaToken(n, helperName));
					JNI.cactionInsertClassEnd(helperName, createJavaToken(n, helperName));
					
			        JNI.cactionBuildClassSupportStart(/*"::" + package_name+"::"+ */helperName, "", true, // a user-defined class?
			                   false, false, false,	false,	createJavaToken(n, helperName));

			        String[] typeParamNames = new String[0];
			        String[] interfaceNames = new String[0];

					JNI.cactionBuildClassExtendsAndImplementsSupport(0,
							typeParamNames, false, "", 0, interfaceNames,
							createJavaToken(n, n.toString()));

					JNI.cactionBuildMethodSupportStart(methodName, methodIndex,
							createJavaToken(n, methodName));
					
					// build a return type
					JNI.cactionTypeReference("", returnType, this, createJavaToken());
					
					// build an argument
//					JNI.cactionPushPackage("x10.lang", createJavaToken(n, helperName));
//					JNI.cactionPopPackage();						
//					JNI.cactionTypeReference("x10.lang", "String", this, createJavaToken());
//					JNI.cactionBuildArgumentSupport(argName, false, false, createJavaToken(n, argName));

					if (argClass_package.length() != 0) {
						JNI.cactionPushPackage(argClass_package, createJavaToken(n, argClass_package));
						JNI.cactionPopPackage();
					}
					JNI.cactionTypeReference(argClass_package, argClass_type, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
					JNI.cactionBuildArgumentSupport(argName, false, false, createJavaToken(n, argName));
					
			        JNI.cactionBuildMethodSupportEnd(methodName, methodIndex, // method index 
														false, false, false, 0, 1,
														true, /* user-defined-method */
														createJavaToken(n, n.name().id().toString()),
			                                           	createJavaToken(n, n.name().id().toString()+"_args"));
			        
			        JNI.cactionBuildClassSupportEnd(helperName, createJavaToken(n, helperName));
			        
					JNI.cactionTypeDeclaration("", helperName, /*num_annotations*/0, 
											false, /*is_annotation_interface*/false, false, 
											/*is_enum*/false, false, false, false, 
											true, false, false, /*is_strictfp*/false, 
											createJavaToken(n, helperName));

					JNI.cactionSetCurrentClassName(callerClass);
					
					JNI.cactionMessageSend("", helperName, methodName, createJavaToken(n, helperName));
					JNI.cactionTypeReference("", helperName, this, createJavaToken(n, helperName));
System.out.println("abc1");
					visitChild(n, n.target());
					List<Expr> arglist = new ArrayList<Expr>();
					arglist.add((Expr)n.target());
					handleArgumentTypes(arglist);
//					JNI.cactionStringLiteral(StringUtil.escape(/*methodName*/argName), createJavaToken(n, helperName));
//					JNI.cactionTypeReference("x10.lang", "String", this, createJavaToken());
//					JNI.cactionArrayTypeReference(1, createJavaToken());

					JNI.cactionTypeReference("", helperName, this, createJavaToken());

					JNI.cactionMessageSendEnd(true, true, methodName, 1, 0, 1, createJavaToken(n, helperName));
				}
				return;
			}
			visitChild(n, n.target());
			
			if (pkg.equals("x10.lang") &&
					( type.equals("Boolean")
					|| type.equals("Byte")
					|| type.equals("Char")
					|| type.equals("Int")
					|| type.equals("Short")
					|| type.equals("Float")
					|| type.equals("Long")
					|| type.equals("Double"))
				|| pkg.length() == 0) {
				JNI.cactionTypeReference("", type, this, createJavaToken());
			}
			else if (pkg.length() != 0) {
				JNI.cactionPushPackage(pkg, createJavaToken(n, type));
				JNI.cactionPopPackage();
				JNI.cactionTypeReference(pkg, type, this, createJavaToken());
			}

//			JNI.cactionTypeReference(pkg, type, this, createJavaToken());
//			JNI.cactionQualifiedTypeReference(pkg, type, createJavaToken(n, type));
			JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, createJavaToken(n, fieldName));
		}

		public void visit(X10FieldDecl_c n) {
			toRose(n, "X10FieldDecl:", n.name().id().toString());
//			visitChild(n, n.type());
//			visitChild(n, n.init());
			
            String fieldName = n.name().id().toString();
//            String package_name = n.type().type().fullName().qualifier().toString();
//            String type_name = n.type().nameString();
//            
////			if (package_name.equals("x10.lang") &&
////				(  type_name.equals("boolean")	|| type_name.equals("Boolean")
////				|| type_name.equals("byte") 	|| type_name.equals("Byte")
////				|| type_name.equals("char")		|| type_name.equals("Char")
////				|| type_name.equals("int")		|| type_name.equals("Int")
////				|| type_name.equals("short")	|| type_name.equals("Short")
////				|| type_name.equals("float")	|| type_name.equals("Float")
////				|| type_name.equals("long")		|| type_name.equals("Long")
////				|| type_name.equals("double")	|| type_name.equals("Double"))) {
////				JNI.cactionTypeReference("", type_name, this, createJavaToken());
////			}
////			else if (package_name.length() != 0) {
////				JNI.cactionPushPackage(package_name, createJavaToken(n, type_name));
////				JNI.cactionPopPackage();
////				JNI.cactionTypeReference(package_name, type_name, this, createJavaToken());
////			}
////			else
////				JNI.cactionTypeReference(package_name, type_name, this, createJavaToken());
//
//            JNI.cactionBuildFieldSupport(fieldName, createJavaToken());
            
            Flags flags = n.flags().flags();
            
            boolean hasInitializer = (n.init() != null);
            if (hasInitializer) {
//				JNI.cactionAssignment(createJavaToken(fieldDecl, fieldDecl.toString()));		
//            	visitChild(n, n.type());
				visitChild(n, n.init());
//                JNI.cactionSingleNameReference("", "", fieldName, createJavaToken(fieldDecl, fieldDecl.name().id().toString()));
             
//                JNI.cactionAssignmentEnd(createJavaToken(fieldDecl, fieldDecl.toString()));

//				int field_index = memberMap.get(fieldName);
//              	
//				JNI.cactionBuildInitializerSupport(flags.isStatic(), fieldDecl.init().toString()/*fieldName*/,
//                                                  field_index,
//                                                  createJavaToken());
//    			JNI.cactionAssignmentEnd(createJavaToken(fieldDecl, fieldDecl.toString()));
            }

            JNI.cactionFieldDeclarationEnd(fieldName,
                                            false, // is_enum_field
                                            hasInitializer,
                                            flags.isFinal(),
                                            flags.isPrivate(),
                                            flags.isProtected(),
                                            flags.isPublic(),
                                            false, // java_is_volatile
                                            false, // java_is_synthetic
                                            flags.isStatic(),
                                            flags.isTransient(),
                                            createJavaToken());
		}

		public void visit(X10LocalDecl_c n) {
			toRose(n, "X10LocalDecl:", n.name().id().toString());	
			Expr init = n.init();
			visitChild(n, n.type());	
			/*
			 
            if (init != null)
                JNI.cactionAssignment(createJavaToken(n, n.toString()));

            JNI.cactionLocalDeclaration(0, n.name().id().toString(), n.flags().flags().isFinal(), createJavaToken(n, n.name().id().toString()));
            if (init == null)
                JNI.cactionLocalDeclarationEnd(n.name().id().toString(), false, createJavaToken(n, n.name().id().toString()));

            if (init != null) {
                JNI.cactionSingleNameReference("", "", n.name().id().toString(), createJavaToken(n, n.name().id().toString()));
                visitChild(n, init);

                JNI.cactionAssignmentEnd(createJavaToken(n, n.toString()));
            }
           */
			if (init != null)
				JNI.cactionAssignment(createJavaToken(n, n.toString()));				
			
			JNI.cactionLocalDeclaration(0, n.name().id().toString(), n.flags().flags().isFinal(), createJavaToken(n, n.name().id().toString()));
			if (init != null) {
//                JNI.cactionSingleNameReference("", "", n.name().id().toString(), createJavaToken(n, n.name().id().toString()));

				visitChild(n, init);
//                JNI.cactionAssignmentEnd(createJavaToken(n, n.toString()));
			}
			JNI.cactionLocalDeclarationEnd(n.name().id().toString(), init != null, createJavaToken(n, n.name().id().toString()));
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
			
			JNI.cactionIfStatement(createJavaToken(n, n.toString()));
			JNI.cactionIfStatementEnd(n.alternative() != null, createJavaToken(n, n.toString()));
		}

		public void visit(X10Conditional_c n) {
			toRose(n, "X10Conditional:");
			JNI.cactionConditionalExpression(createJavaToken(n, n.toString()));
			visitChild(n, n.cond());
			visitChild(n, n.consequent());
			visitChild(n, n.alternative());
			JNI.cactionConditionalExpressionEnd(createJavaToken(n, n.toString()));
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
			toRose(n, "Catch: ");
			visitChild(n, n.formal());
			visitChild(n, n.body());
		}

		
		public void visit(Labeled_c n) {
			toRose(n, "Labeled: ***caution: NOT insert a JNI function yet***",  n.labelNode().id().toString());
			visitChild(n, n.statement());
		}
		

		
		public void visit(X10BooleanLit_c n) {
			toRose(n, "X10BooleanLit:", Boolean.toString(n.value()));
			if (n.value()) 
				JNI.cactionTrueLiteral(createJavaToken(n, n.value() + ""));
			else
				JNI.cactionFalseLiteral(createJavaToken(n, n.value() + ""));
			
		}

		public void visit(ClassLit_c n) {
			toRose(n, "ClassLit: ***caution: NOT insert a JNI function yet***");
			visitChild(n, n.typeNode());
		}

		public void visit(X10FloatLit_c n) {
			toRose(n, "X10FloatLit:", Double.toString(n.value()));
			JNI.cactionFloatLiteral(new Float(n.value()), "" + n.value(), createJavaToken(n, n.toString()));
		}

		public void visit(NullLit_c n) {
			toRose(n, "NullLit:");
			JNI.cactionNullLiteral(createJavaToken(n, n.toString()));
		}

		public void visit(X10CharLit_c n) {
			toRose(n, "X10CharLit:", "" + n.value());
			JNI.cactionCharLiteral(n.value(), createJavaToken(n, n.toString()));
		}

		public void visit(IntLit_c n) {
			toRose(n, "IntLit:", Long.toString(n.value()));
			
			if (n.kind() == IntLit.INT) {
				JNI.cactionIntLiteral((int)n.value(), n.toString(), createJavaToken(n, n.value() + ""));
			} else if (n.kind() == IntLit.LONG) {
				JNI.cactionLongLiteral(n.value(), n.toString(), createJavaToken(n, n.value() + ""));
				
			} else {
				System.out.println("Unhandled literal : " + n.toString());
			}
			System.out.println("End of IntLit");
		}

		public void visit(X10StringLit_c n) {
			toRose(n, "X10StringLit:", StringUtil.escape(n.value()));
			JNI.cactionStringLiteral(StringUtil.escape(n.value()), createJavaToken(n, n.toString()));
		}

		
		public void visit(Finish_c n) {
			toRose(n, "Finish:");
			JNI.cactionFinish(createJavaToken(n, n.toString()));
			visitChild(n, n.body());
			JNI.cactionFinishEnd(createJavaToken(n, n.toString()));
		}

		public void visit(AtStmt_c n) {
			toRose(n, "AtStmt:", n.place().toString(), n.body().toString());
			System.out.println("place=" + n.place());
			JNI.cactionAt(createJavaToken(n, n.toString()));
			visitChild(n, n.place());
			visitChild(n, n.body());
			JNI.cactionAtEnd(createJavaToken(n, n.toString()));
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
			toRose(n, "Async:");
			JNI.cactionAsync(createJavaToken(n, n.toString()));
			visitChild(n, n.body());
			JNI.cactionAsyncEnd(createJavaToken(n, n.toString()));
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
			toRose(n, "X10New: ", n.objectType().toString(), "n=" + n.toString(), "objectType=" + n.objectType(), "body=" + n.body());
			boolean isRail = n.objectType().type().isRail();
			
			if (isRail) {
				JNI.cactionArrayAllocationExpression(createJavaToken(n, n.toString()));
				visitChildren(n, n.typeArguments());
				List<Expr> args = n.arguments();
//				for (int i = 0; i < args.size(); ++i)
//					System.out.println(i + " : " + args.get(i));	
//				System.out.println("ArrayOf=" + n.objectType().type().arrayOf());
//				visitChild(n, n.objectType());
				visitChildren(n, n.arguments());
				JNI.cactionArrayAllocationExpressionEnd(1, false, createJavaToken(n, n.toString()));
			}
			else {
				JNI.cactionAllocationExpression(createJavaToken(n, n.toString()));
				List<TypeNode> typeArg = n.typeArguments();
				for (int i = 0; typeArg != null && i < typeArg.size(); ++i) {
//					visitChildren(n, n.typeArguments());
					String typeParam = typeArg.get(i).nameString();
					

				}
				visitChild(n, n.objectType());
				visitChildren(n, n.arguments());
				JNI.cactionAllocationExpressionEnd(n.objectType() != null, n.arguments().size(), createJavaToken(n, n.toString()));
			}
		}

		public void visit(Allocation_c n) {
			toRose(n, "Allocation:");
			visitChild(n, n.objectType());
		}
				
		public void visit(LocalAssign_c n) {
			toRose(n, "LocalAssign:");
			JNI.cactionAssignment(createJavaToken(n, n.toString()));
			visitChild(n, n.local());
			visitChild(n, n.right());
			JNI.cactionAssignmentEnd(createJavaToken(n, n.toString()));
			System.out.println("LocalAssign end");
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
	
	public static class TypeVisitor extends NodeVisitor {
		private List<Import> imports;
		private List<String> package_ref;
		private String package_name;
		private String class_name;
//		private FileSource source;
		private boolean isClassMatched;
		private boolean isPackageMatched = true; // so far, set true;
		private int memberIndex;
		private Node_c node;
		private Job currentJob;
		
		private static HashMap<Operator, Integer> opTable = new HashMap<Operator, Integer>();

		
		public TypeVisitor(String pack, String clazz, SourceFile_c src, Job job) {
			System.out.println("package=" + pack + ", class=" + clazz + ", source=" + src);
			package_name = pack;
			class_name = trimTypeParameterClause(clazz); // Rail is not supposed to be here 
//			source = src;
			currentJob = job;
			package_ref = new ArrayList<String>();
			package_ref.add("x10.lang"); // auto-import
			package_ref.add("x10.io");
			package_ref.add(pack);
			imports = src.imports();
		}				
		
		public void visitDeclarations() {}
		public void visitDefinitions() {}
		public boolean  addFileIndex() { return false; }		
		public void subFileIndex() {}
		
		/**     
		 * 
		 * operator code values are directly derived from ECJ.
		 * 
	     * enum ops {
	     *    ERROR_OPERATOR       = 0, // This is not a ECJ value 
	     *   AND                  = 2,
	     *   DIVIDE               = 9,
	     *   GREATER              = 6,
	     *   GREATER_EQUAL        = 7,
	     *   LEFT_SHIFT           = 10,
	     *   LESS                 = 4,
	     *   LESS_EQUAL           = 5,
	     *   MINUS                = 13,
	     *   MULTIPLY             = 15,
	     *   OR                   = 3,
	     *   PLUS                 = 14,
	     *   REMAINDER            = 16,
	     *   RIGHT_SHIFT          = 17,
	     *   UNSIGNED_RIGHT_SHIFT = 19,
	     *   XOR                  = 8,
	     *   OR_OR                = 100, // Handled by separate function 
	     *   AND_AND              = 101, // Handled by separate function 
	     *   LAST_OPERATOR
	      *};
		  *
		  *@see JavaParserActionROSE.C:Java_JavaParser_cactionBinaryExpressionEnd(JNIEnv *env, jclass, jint java_operator_kind, jobject jToken)
		 * @param op
		 * @return
		 */
		private int getOperatorKind(Operator op) {
			if (opTable.isEmpty()) {
				opTable.put(Operator.ADD, 14);
				opTable.put(Operator.SUB, 13);
				opTable.put(Operator.MUL, 15);
				opTable.put(Operator.DIV, 9);
				opTable.put(Operator.MOD, 16);
				opTable.put(Operator.GT, 6);
				opTable.put(Operator.GE, 7);
				opTable.put(Operator.LT, 4);
				opTable.put(Operator.LE, 5);
				opTable.put(Operator.SHL, 10);
				opTable.put(Operator.SHR, 17);
				opTable.put(Operator.USHR, 19);
				opTable.put(Operator.BIT_AND, 2); // or COND_AND?
				opTable.put(Operator.BIT_OR, 3);  // or COND_OR?
				opTable.put(Operator.BIT_XOR, 8);
				opTable.put(Operator.COND_OR, 100);
				opTable.put(Operator.COND_AND, 101);
			}
			return opTable.get(op);
		}
		
//		public NodeVisitor begin() {
//			System.out.println("Beginning of traversal of " + class_name);
//			return this;
//		}		
		
		private void handleAmbType(AmbTypeNode_c amb) {
			handleAmbType(amb, null, -1);
		}
		
		private void handleAmbType(AmbTypeNode_c amb, String[] output, int index) {
			String type_ = amb.name().toString();
		    String ambTypeName = amb.toString().replaceAll("\\{amb\\}", "");
			ambTypeName = trimTypeParameterClause(ambTypeName);
			System.out.println("handleAmbType: " + amb + ", ambTypeName: " + ambTypeName);
			
			if (	ambTypeName.equals("Boolean")
				||	ambTypeName.equals("Byte")
				|| 	ambTypeName.equals("Char")
				|| 	ambTypeName.equals("Int")
				|| 	ambTypeName.equals("Short")
				|| 	ambTypeName.equals("Float")
				|| 	ambTypeName.equals("Long")
				|| 	ambTypeName.equals("Double")) {
				JNI.cactionTypeReference("", ambTypeName, this, createJavaToken());
				return;
			}
			
			boolean isRailType = false;				
			if (	ambTypeName.indexOf("Rail[") >= 0
				|| 	ambTypeName.indexOf("GrowableRail[") >= 0) {
				String class_name = ambTypeName.substring(ambTypeName.indexOf('[')+1, ambTypeName.indexOf(']'));
				int lastDot = class_name.lastIndexOf(".");
				String package_ = lastDot > 0 ? class_name.substring(0, lastDot) : "";
				String type = lastDot > 0 ? class_name.substring(lastDot+1) : class_name;
				if (	type.equals("Boolean")
					||	type.equals("Byte")
					|| 	type.equals("Char")
					|| 	type.equals("Int")
					|| 	type.equals("Short")
					|| 	type.equals("Float")
					|| 	type.equals("Long")
					|| 	type.equals("Double")) {
					JNI.cactionTypeReference("", type, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
					return;
				}
				
				if (package_.length() != 0) {						
					JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
					JNI.cactionPopPackage();
					JNI.cactionTypeReference(package_, type, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
				}
				else {
					// so far, do nothing
					isRailType = true;
					type_ = type;
				}
			}
			else if (amb.prefix() != null) { // package is NOT null
				// Need to check whether amd.prefix() returns a package name before invoking cactionTypeReference() because
				// it is possible that amb.prefix() returns a class name when amb is a nested class.
//				JNI.cactionTypeReference(amb.prefix().toString(), amb.name().toString(), this, createJavaToken(amb, amb.toString()));
//				return;
			}
			
			try {
				TypeSystem ts = currentJob.extensionInfo().typeSystem();
				boolean isFoundInPackageRef = false;
				System.out.println("package_ref_size=" + package_ref.size());
				for (String package_ : package_ref) {
					System.out.println("package_ref=" + package_);
					List<Type> list = new ArrayList<Type>();
					try {
						list = ts.systemResolver().find(QName.make(package_ + "." + type_));
					} catch (polyglot.types.NoClassException e) {
						System.out.println(package_ + "." + type_ + " not found: " + e);
					}
					if (list.size() != 0) {
						Type t = list.get(0);
//						String type_ = amb.name().toString(); 		
						JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
			    		JNI.cactionPopPackage();
						JNI.cactionTypeReference(package_, type_, this, createJavaToken(amb, amb.toString()));
						isFoundInPackageRef = true;
						if (output != null)
							output[index] = package_ + "." + type_;
						if (isRailType)
							JNI.cactionArrayTypeReference(1, createJavaToken());
						break;
					}
				}
				if (isFoundInPackageRef)
					return;

				System.out.println("import_size="+ imports.size());
				for (Import import_ : imports) {
					if (import_.kind() == Import.CLASS) {
						String importedClass = import_.name().toString();
						System.out.println("Imported=" + importedClass);
						int lastDot = importedClass.lastIndexOf('.');
						String package_ = importedClass.substring(0, lastDot);
						String type2_ = importedClass.substring(lastDot + 1);
						System.out.println("importName=" + import_.name() + ", type2=" + type2_);
						if (type_.equals(type2_)) {
				    		JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
				    		JNI.cactionPopPackage();
							JNI.cactionTypeReference(package_, type2_, this, createJavaToken(amb, amb.toString()));
							isFoundInPackageRef = true;
							if (output != null)
								output[index] = package_ + "." + type_;
							if (isRailType)
								JNI.cactionArrayTypeReference(1, createJavaToken());
							break;
						}
					} else if (import_.kind() == Import.PACKAGE) {
						String package_ = import_.name().toString();
						List<Type> list = new ArrayList<Type>();
						try {
							list = ts.systemResolver().find(QName.make(package_ + "." + type_));
						} catch (polyglot.types.NoClassException e) {
							System.out.println(package_ + "." + type_ + " not found: " + e);
						}
						if (list.size() != 0) {
							Type t = list.get(0);
//							String type_ = amb.name().toString();
				    		JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
				    		JNI.cactionPopPackage();
							JNI.cactionTypeReference(package_, type_, this, createJavaToken(amb, amb.toString()));
							isFoundInPackageRef = true;
							if (output != null)
								output[index] = package_ + "." + type_;
							if (isRailType)
								JNI.cactionArrayTypeReference(1, createJavaToken());
							break;
						}
					}
				}
				
				if (!isFoundInPackageRef)
					// treat as a generic type					
					if (isRailType) {					
						JNI.cactionTypeReference("", type_, this, createJavaToken());
						JNI.cactionArrayTypeReference(1, createJavaToken());
					}
					else
						JNI.cactionTypeReference("", amb.name().toString(), this, createJavaToken(amb, amb.toString()));
			
			} catch (SemanticException e) {
				// treat as a generic type
				if (isRailType) {					
					JNI.cactionTypeReference("", type_, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
				}
				else
					JNI.cactionTypeReference("", amb.name().toString(), this, createJavaToken(amb, amb.toString()));
			}
			System.out.println("handleAmbType end");
		}
		
		private void handleAmbType(AmbDepTypeNode_c amb) {
			handleAmbType(amb, null, -1);	
		}
		
		private void handleAmbType(AmbDepTypeNode_c amb, String[] output, int index) {
		    String ambTypeName = amb.toString().replaceAll("\\{amb\\}", "");
			ambTypeName = trimTypeParameterClause(ambTypeName);
			String type_ =ambTypeName;
			System.out.println("handleAmbDepType: " + amb + ", ambTypeName: " + ambTypeName);
			
			if (	ambTypeName.equals("Boolean")
				||	ambTypeName.equals("Byte")
				|| 	ambTypeName.equals("Char")
				|| 	ambTypeName.equals("Int")
				|| 	ambTypeName.equals("Short")
				|| 	ambTypeName.equals("Float")
				|| 	ambTypeName.equals("Long")
				|| 	ambTypeName.equals("Double")) {
				JNI.cactionTypeReference("", ambTypeName, this, createJavaToken());
				return;
			}
			
			boolean isRailType = false;
			if (	ambTypeName.equals("Boolean")
					||	ambTypeName.equals("Byte")
					|| 	ambTypeName.equals("Char")
					|| 	ambTypeName.equals("Int")
					|| 	ambTypeName.equals("Short")
					|| 	ambTypeName.equals("Float")
					|| 	ambTypeName.equals("Long")
					|| 	ambTypeName.equals("Double")) {
					JNI.cactionTypeReference("", ambTypeName, this, createJavaToken());
					return;
				}
			else if (	ambTypeName.indexOf("Rail[") >= 0
				|| 	ambTypeName.indexOf("GrowableRail[") >= 0) {
				String class_name = ambTypeName.substring(ambTypeName.indexOf('[')+1, ambTypeName.indexOf(']'));
				int lastDot = class_name.lastIndexOf(".");
				String package_ = lastDot > 0 ? class_name.substring(0, lastDot) : "";
				String type = lastDot > 0 ? class_name.substring(lastDot+1) : class_name;
				if (	type.equals("Boolean")
					||	type.equals("Byte")
					|| 	type.equals("Char")
					|| 	type.equals("Int")
					|| 	type.equals("Short")
					|| 	type.equals("Float")
					|| 	type.equals("Long")
					|| 	type.equals("Double")) {
					JNI.cactionTypeReference("", type, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
					return;
				}
				
				if (package_.length() != 0) {						
					JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
					JNI.cactionPopPackage();
					JNI.cactionTypeReference(package_, type, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
				}
				else {
					// so far, do nothing
					isRailType = true;
					type_ = type;
				}
			}
			// 08/19/2014 simply commented out because AmbDepTypeNode_c has no method named prefix()
//			else if (amb.prefix() != null) { // package is NOT null
//				// Need to check whether amd.prefix() returns a package name before invoking cactionTypeReference() because
//				// it is possible that amb.prefix() returns a class name when amb is a nested class.
////				JNI.cactionTypeReference(amb.prefix().toString(), amb.name().toString(), this, createJavaToken(amb, amb.toString()));
////				return;
//			}
			
			try {
				TypeSystem ts = currentJob.extensionInfo().typeSystem();
				boolean isFoundInPackageRef = false;
				System.out.println("AMBDEPTYPE package size=" + package_ref.size() + " for type=" + ambTypeName);
				for (String package_ : package_ref) {
					System.out.println("AMBDEPTYPE class=" + package_ + "." + type_);
					List<Type> list = new ArrayList<Type>();
					try {
						list = ts.systemResolver().find(QName.make(package_ + "." + type_));
					} catch (polyglot.types.NoClassException e) {
						System.out.println(package_ + "." + type_ + " not found: " + e);
						continue;
					}
					System.out.println("AMBDEPTYPE list size=" + list.size());
					if (list.size() != 0) {
						Type t = list.get(0);
						System.out.println("found type=" + t);
//						String type_ = amb.name().toString(); 		
						JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
			    		JNI.cactionPopPackage();
						JNI.cactionTypeReference(package_, type_, this, createJavaToken(amb, amb.toString()));
						isFoundInPackageRef = true;
						if (output != null)
							output[index] = package_ + "." + type_;
						if (isRailType)
							JNI.cactionArrayTypeReference(1, createJavaToken());
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
				    		JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
				    		JNI.cactionPopPackage();
							JNI.cactionTypeReference(package_, type2_, this, createJavaToken(amb, amb.toString()));
							isFoundInPackageRef = true;
							if (output != null)
								output[index] = package_ + "." + type_;
							if (isRailType)
								JNI.cactionArrayTypeReference(1, createJavaToken());
							break;
						}
					} else if (import_.kind() == Import.PACKAGE) {
						String package_ = import_.name().toString();
						List<Type> list = new ArrayList<Type>();
						try {
							list = ts.systemResolver().find(QName.make(package_ + "." + type_));
						} catch (polyglot.types.NoClassException e) {
							System.out.println(package_ + "." + type_ + " not found: " + e);
							continue;
						}
						if (list.size() != 0) {
							Type t = list.get(0);
//							String type_ = amb.name().toString();
				    		JNI.cactionPushPackage(package_, createJavaToken(amb, amb.toString()));
				    		JNI.cactionPopPackage();
							JNI.cactionTypeReference(package_, type_, this, createJavaToken(amb, amb.toString()));
							isFoundInPackageRef = true;
							if (output != null)
								output[index] = package_ + "." + type_;
							if (isRailType)
								JNI.cactionArrayTypeReference(1, createJavaToken());
							break;
						}
					}
				}
				
				if (!isFoundInPackageRef) {
					// treat as a generic type
					if (isRailType) {					
						JNI.cactionTypeReference("", type_, this, createJavaToken());
						JNI.cactionArrayTypeReference(1, createJavaToken());
					}
					JNI.cactionTypeReference("", ambTypeName, this, createJavaToken(amb, amb.toString()));
				}			
			} catch (SemanticException e) {
				// treat as a generic type					
				if (isRailType) {					
					JNI.cactionTypeReference("", type_, this, createJavaToken());
					JNI.cactionArrayTypeReference(1, createJavaToken());
				}
				JNI.cactionTypeReference("", ambTypeName, this, createJavaToken(amb, amb.toString()));
			}
		}
		
		public void handleClassMembers(List<ClassMember> members, String package_name, String type_name) throws SemanticException {
			for (int i = 0; i < members.size(); ++i) {
				JL m = members.get(i);
				if (m instanceof X10MethodDecl_c) {
					X10MethodDecl_c methodDecl = (X10MethodDecl_c) m;
					if (hasFunctionType(methodDecl))
						continue;
					StringBuffer param = new StringBuffer();
					for (Formal f : methodDecl.formals()) {
						param.append(f.type().toString().toLowerCase());
					}
					memberMap.put(methodDecl.name().toString() + "(" + param + ")", i);
					classMemberMap.put(type_name, memberMap);
					previsit(methodDecl, type_name);
				} else if (m instanceof X10ConstructorDecl_c) {
					X10ConstructorDecl_c constructorDecl = (X10ConstructorDecl_c) m;
					if (hasFunctionType(constructorDecl))
						continue;
					StringBuffer param = new StringBuffer();
					for (Formal f : constructorDecl.formals()) {
						param.append(f.type().toString().toLowerCase());
					}
					memberMap.put(((X10ConstructorDecl_c) m).name().toString() + "(" + param + ")", i);
					classMemberMap.put(type_name, memberMap);
					previsit(constructorDecl, package_name, type_name);
				} else if (m instanceof X10FieldDecl_c) {
					X10FieldDecl_c fieldDecl = (X10FieldDecl_c) m;
					if (hasFunctionType(fieldDecl))
						continue;
					memberMap.put(((X10FieldDecl_c) m).name().toString(), i);
					classMemberMap.put(type_name, memberMap);
					previsit(fieldDecl, type_name);
				} else if (m instanceof TypeNode_c) {
					System.out.println("TypeNode_c : " + m);
				} else if (m instanceof TypeDecl_c) {
					System.out.println("TypeDecl_c : " + m);
				} else if (m instanceof X10ClassDecl_c) {
					System.out.println("X10ClassDecl_c : " + m);
					visitDeclarations((X10ClassDecl_c) m, (package_name == "")? type_name : package_name+"."+type_name);
				} else if (m instanceof ClassDecl_c) {
					System.out.println("ClassDecl_c : " + m);
				} else {
					System.out.println("Unhandled node : " + m);
				}
			}
		}
		
		public void visit(FunctionTypeNode_c n) {
			toRose(n, "FunctionTypeNode_c in TypeVisitor:", n.toString());
			
		}
		
		
		public void visitDeclarations(X10ClassDecl_c n) throws SemanticException {
			visitDeclarations(n, null);
		}
		
		public void visitDeclarations(X10ClassDecl_c n, String parent) throws SemanticException {
//		public void visitDeclarations(Term_c n) {
			toRose(n, "X10ClassDecl_c visitDeclarations in TypeVisitor:", n.toString());
			X10ClassDecl_c decl = (X10ClassDecl_c) n;
			List<TypeParamNode> typeParamList = decl.typeParameters();
        	TypeNode superClass = decl.superClass();
	        List<TypeNode> interfaces = decl.interfaces();
	        System.out.println("PACKAGE_NAME=" + package_name);
			SourceFile_c file = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(fileIndex);
				     
	        classMemberMap.put(class_name, memberMap);
			JNI.cactionSetCurrentClassName(package_name + "." +class_name);
			
			if (package_name.length() != 0)
				JNI.cactionPushPackage(package_name, createJavaToken(n, class_name));
			JNI.cactionInsertClassStart(class_name, false, false, false, createJavaToken(n, class_name));
			// does not consider nested class so far
			JNI.cactionInsertClassEnd(class_name, createJavaToken(n, class_name));
			
			List<ClassMember> members = ((X10ClassBody_c)((X10ClassDecl_c) n).body()).members();

        	String[] typeParamNames = new String[typeParamList.size()];
			for (int i = 0; i < typeParamList.size(); ++i) {
				String typeParam = typeParamList.get(i).name().toString();
				typeParamNames[i] = typeParam;				
//				typeParamNames[i] = package_name + "." + class_name + "." + typeParam;
				JNI.cactionSetCurrentClassName(typeParam);
//				JNI.cactionSetCurrentClassName(package_name + "." + class_name + "." + typeParam);
				JNI.cactionInsertClassStart(typeParam, false, false, false, createJavaToken(n, typeParam));
				JNI.cactionInsertClassEnd(typeParam, createJavaToken(n, typeParam));
//				JNI.cactionPushTypeParameterScope("", typeParam, createJavaToken(n, typeParam));
				JNI.cactionPushTypeParameterScope(package_name, class_name, createJavaToken(n, typeParam));
				JNI.cactionInsertTypeParameter(typeParam, createJavaToken(n, typeParam));
				JNI.cactionBuildTypeParameterSupport(package_name, class_name, -1, typeParam, 0, createJavaToken(n, typeParam));
			}
			if (typeParamList.size() > 0) 
				JNI.cactionSetCurrentClassName(package_name + "." + class_name);
	 
        	JNI.cactionBuildClassSupportStart(class_name, "", true, // a user-defined class?
                    false, false, false,	false,	createJavaToken(n, class_name));
        	
	        // handling of a super class and interfaces    
        	String superClassName = "";
	        if (superClass != null) {
	        	if (superClass instanceof AmbTypeNode_c) {
	        		handleAmbType((AmbTypeNode_c) superClass);
	        		superClassName = ((AmbTypeNode_c) superClass).name().toString();
//	        		superClassName = 
       			// currently, not handle the function type
       			// TODO: handle function type
	        	}
	        	else if (superClass instanceof AmbDepTypeNode_c) {
	        		handleAmbType((AmbDepTypeNode_c) superClass);
	        		superClassName = ((AmbDepTypeNode_c) superClass).toString();
	        		System.out.println("SUEPRCLASSNAME=" + superClassName);
	        	}
	        	else if (superClass instanceof FunctionTypeNode_c) {
	        		superClass = null;
	        	}
	        	else
	        		visit((X10CanonicalTypeNode_c) superClass);
	        }
	        
	        String[] interfaceNames = new String[interfaces.size()];
       		for (int i = 0; i < interfaces.size(); ++i) {
       			TypeNode intface = interfaces.get(i);
	        	System.out.println("Interface=" + intface);
       			if (intface instanceof AmbTypeNode_c) 
       				handleAmbType((AmbTypeNode_c) intface, interfaceNames, i);
       			// currently, not handle the function type
       			// for simplicity, just ignore interfaces
       			// TODO: handle function type
       			else if (intface instanceof FunctionTypeNode_c) { 
       				interfaces = null; 
       				break;
       			}
       			else {
       				interfaceNames[i] = trimTypeParameterClause(intface.toString());
       				System.out.println("name=" + interfaceNames[i] + ", " + intface.getClass());
//       				visit(intface);
       				visit(intface);
       			}
        	}
        	
	        JNI.cactionBuildClassExtendsAndImplementsSupport(typeParamList.size(), typeParamNames,
	        												superClass != null, superClassName,
	        												interfaces == null ? 0 : interfaces.size(), 
	        												interfaceNames,
	        												createJavaToken(n, class_name));
        	
	        handleClassMembers(members, package_name, class_name);

	        JNI.cactionBuildClassSupportEnd(class_name, createJavaToken(n, class_name));
	    	if (package_name.length() != 0) {
	    		JNI.cactionPushPackage(package_name, createJavaToken(n, class_name));
	    		JNI.cactionPopPackage();
	    	}
//	        JNI.cactionPopTypeScope();
	    	
	    	// TODO: eliminate if-satement after removing the appearance of ambiguous typesPlaceLocalHandle

				Flags flags = decl.flags().flags();
		    	//TODO: enum and interface type
				JNI.cactionTypeDeclaration(package_name, class_name, /*((X10Del) decl.del()).annotations().size()*/0, 
									decl.superClass() != null, /*is_annotation_interface*/false, flags.isInterface(), 
									/*is_enum*/false, flags.isAbstract(), flags.isFinal(), flags.isPrivate(), 
									flags.isPublic(), flags.isProtected(), flags.isStatic(), /*is_strictfp*/false, 
									createJavaToken(n, class_name));

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
//			JNI.cactionTypeDeclaration("", n.name().id().toString(), false, false, false, false, false, false, false, false, false, false);
			
			JNI.cactionTypeDeclarationEnd(false, createJavaToken(n, class_name));
			
//	        JNI.cactionBuildClassSupportEnd(class_name, createJavaToken());
		}
		
//		public void createTypeReference(Node_c n) {
//			JNI.cactionInsertClassStart(class_name, false, false, false, createJavaToken(n, class_name));
//			JNI.cactionInsertClassEnd(class_name, createJavaToken(n, class_name));
//			node = n;
//        	JNI.cactionBuildClassSupportStart(class_name, "", true, // a user-defined class?
//                    false, false, false,	false,	createJavaToken(n, class_name));
//			
//			X10ClassDecl_c decl = (X10ClassDecl_c)n;
//
//	        List<ClassMember> members = ((X10ClassBody_c)decl.body()).members();
//	        for (int i = 0; i < members.size(); ++i) {
//	        	JL m = members.get(i);
//	 			if (m instanceof X10MethodDecl_c) {
//	 				X10MethodDecl_c methodDecl = (X10MethodDecl_c) m;
//	 				StringBuffer param = new StringBuffer();
//	 				for (Formal f : methodDecl.formals()) {
//	 					param.append(f.type().toString());
//	 				}
//	 				memberMap.put(methodDecl.name().toString()+"("+param+")", i);
//	 				classMemberMap.put(class_name, memberMap);
//	 				previsit(methodDecl);
//	 			}	 			
//	 			else if (m instanceof X10ConstructorDecl_c) {
//	 				X10ConstructorDecl_c constructorDecl = (X10ConstructorDecl_c) m;
//	 				StringBuffer param = new StringBuffer();
// 					for (Formal f : constructorDecl.formals()) {
// 						param.append(f.type().toString());
// 					}
//	 				memberMap.put(((X10ConstructorDecl_c) m).name().toString() + "(" + param + ")", i);
//	 				classMemberMap.put(class_name, memberMap);
//	 				previsit(constructorDecl); 
//	 			}                
//	 			else if (m instanceof X10FieldDecl_c) {
//                    X10FieldDecl_c fieldDecl = (X10FieldDecl_c)m;
//                    memberMap.put(((X10FieldDecl_c) m).name().toString(), i);
//                    classMemberMap.put(class_name, memberMap);
//                    previsit(fieldDecl);
//                }
//	 			else if (m instanceof TypeNode_c) { System.out.println("TypeNode_c : " + m);  }
//	 			else if (m instanceof TypeDecl_c) { System.out.println("TypeDecl_c : " + m);  }		
//	 			else if (m instanceof X10ClassDecl_c) {  System.out.println("X10ClassDecl_c : " + m);  }
//	 			else if (m instanceof ClassDecl_c) { System.out.println("ClassDecl_c : " + m);  }
//				else {
//					System.out.println("Unhandled node : " + m);
//				}
//	         }
//			System.out.println("TypeVisitor : createTypeReference for " + n);
//		}
		
		public void createTypeReferenceEnd() {
			JNI.cactionTypeDeclarationEnd(false, createJavaToken());
		}		
		
		void toRose(Node n, String name, String... extra) {
			if (name != null)
				System.out.print(name);
			if (extra != null)
				for (String s : extra) {
					System.out.print(s + " ");
				}
			System.out.println();
			/*
			 */
		}
		
		void visitChild(Node p, Node n) {
			if (n == null)
				return;
			/*w ToRoseVisitor(w, p).*/visitAppropriate(n);
//			new TypeVisitor(package_name, class_name, null).visitAppropriate(n);
		}

		void visitChildren(Node p, List<? extends Node> l) {
			if (l == null)
				return;
			for (Node n : l)
				visitChild(p, n);
		}
		
		public void previsit(X10MethodDecl_c n, String class_name) {
			toRose(n, "TypeVisitor.Previsit method decl: ", n.name().id().toString());
			// TODO: currently, skip operator
			if (n.name().id().toString().indexOf("operator") >= 0)
				return;
			List<Formal> formals = n.formals();
			
			String method_name = n.name().id().toString();
			StringBuffer param = new StringBuffer();
			for (Formal f : n.formals()) {
				param.append(f.type().toString().toLowerCase());
			}

			int method_index = memberMap.get(method_name+"("+param+")");

			List<TypeParamNode> typeParamList = n.typeParameters();			
			for (int i = 0; i < typeParamList.size(); ++i) {
				String typeParam = typeParamList.get(i).name().toString();
				System.out.println("TypeParam=" + typeParam + ", package=" + package_name +", type=" + class_name);				
				JNI.cactionPushTypeParameterScope(package_name, class_name, createJavaToken(n, typeParam));
				JNI.cactionInsertTypeParameter(typeParam, createJavaToken(n, typeParam));
				JNI.cactionBuildTypeParameterSupport(package_name, class_name, method_index, typeParam, 0, createJavaToken(n, typeParam));		
				JNI.cactionPopTypeParameterScope(createJavaToken(n, typeParam));
			}

			JNI.cactionBuildMethodSupportStart(method_name, method_index, 
					createJavaToken(n, method_name+"("+param+")"));

			// in case the return type is unknown. Such a case will occur by writing 
			// like"public def toString() = "Place(" + this.id + ")";"
			if (n.returnType() instanceof UnknownTypeNode_c) 
				JNI.cactionTypeReference("", "void", this, createJavaToken());
			else 
				visitChild(n, n.returnType());	
			
			visitChildren(n, n.formals());

           JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
												 false, false, false, 0, formals.size(),
                                           true, /* user-defined-method */
                                           createJavaToken(n, n.name().id().toString()),
                                           createJavaToken(n, n.name().id().toString()+"_args"));
		}
		
		public void previsit(X10ConstructorDecl_c n, String package_name, String type_name) {
			toRose(n, "TypeVisitor.Previsit constructor decl: ", n.name().id().toString());
			List<Formal> formals = n.formals();			
			String method_name = n.name().id().toString();
			StringBuffer param = new StringBuffer();
			for (Formal f : n.formals()) {
				param.append(f.type().toString().toLowerCase());
			}

			int method_index = memberMap.get(method_name+"("+param+")");
					
			List<TypeParamNode> typeParamList = n.typeParameters();
			for (int i = 0; i < typeParamList.size(); ++i) {
				String typeParam = typeParamList.get(i).name().toString();	
				JNI.cactionPushTypeParameterScope(package_name, type_name, createJavaToken(n, typeParam));		
				JNI.cactionInsertTypeParameter(typeParam, createJavaToken(n, typeParam));
				JNI.cactionBuildTypeParameterSupport(package_name, type_name, method_index, typeParam, 0, createJavaToken(n, typeParam));	
				JNI.cactionPopTypeParameterScope(createJavaToken(n, typeParam));
			}
		
          JNI.cactionBuildMethodSupportStart(method_name, method_index, 
        		  createJavaToken(n, method_name+"("+param+")"));
          if (n.returnType() == null) 
        	  JNI.cactionTypeReference(package_name, type_name, this, createJavaToken(n, n.toString()));
          else
        	  visitChild(n, n.returnType());
//          String raw = n.returnType().node().toString();
//          String ret = raw.substring(0, raw.indexOf("{amb}"));
//          System.out.println("returnType=" + n.returnType() + " returnType2 = " + ret + ", formals.size=" + n.formals().size());
//			JNI.cactionTypeReference("", ret, this, createJavaToken(n, ret));
			visitChildren(n, n.formals());
			
//			for (int i = 0; i < formals.size(); ++i) {
//				Formal f = formals.get(i);
//				String type = f.type().toString();
//				System.out.println("f.type().getClass()=>" + f.type().getClass().toString());
//				if (f.type() instanceof AmbTypeNode_c) {
//					System.out.println(f.type().toString() + " => AmbTypeNode_c");
//				}
//				type = type.substring(0, type.indexOf("{"));
//				System.out.println(i + " : " + type);
//				JNI.cactionTypeReference("", type, this, createJavaToken(n, type));
////				visit(f);
//				JNI.cactionBuildArgumentSupport(f.name().toString(), false, false, createJavaToken(n, f.name().toString()));
//			}
		
           JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
												 false, false, false, 0, formals.size(),
                                           true, /* user-defined-method */
                                           createJavaToken(n, n.name().id().toString()),
                                           createJavaToken(n, n.name().id().toString()+"_args"));
//			visitChild(n, n.guard());
//			visitChild(n, n.offerType());
//			visitChildren(n, n.throwsTypes());
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
	        		// 		private lock = new Lock();
	        		// 			:
	        		// }
	        		JNI.cactionTypeReference("", "Unknown", this, createJavaToken());
	        	}
	        	else {
	        		String package_name = fieldDecl.type().type().fullName().qualifier().toString();
	        		if (package_name.length() != 0) {
	        			JNI.cactionPushPackage(package_name, createJavaToken(fieldDecl, package_name));
	        			JNI.cactionPopPackage();
	        		}
	        		JNI.cactionTypeReference(package_name, fieldDecl.type().nameString(), this, createJavaToken());
	        	}
	            JNI.cactionBuildFieldSupport(fieldName, createJavaToken());
	            
//	            boolean hasInitializer = (fieldDecl.init() != null);
//	            
//	            if (hasInitializer) {
//	                visitChild(fieldDecl, fieldDecl.init());
////						int field_index = memberMap.get(fieldName);
////	              	
////	              JNI.cactionBuildInitializerSupport(flags.isStatic(), fieldName,
////	                                                  field_index,
////	                                                  createJavaToken());
//	            }

	            // MH-20140401 
	            // needs to invoke cactionTypeReference again, since cactionFieldDeclarationEnd
	            // first pop SgType for "!is_enum_field"
//	            JNI.cactionTypeReference("", fieldDecl.type().nameString());

	            Flags flags = fieldDecl.flags().flags();

	            JNI.cactionFieldDeclarationEnd(fieldName,
	                                            false, // is_enum_field
	                                            false,
	                                            flags.isFinal(),
	                                            flags.isPrivate(),
	                                            flags.isProtected(),
	                                            flags.isPublic(),
	                                            false, // java_is_volatile
	                                            false, // java_is_synthetic
	                                            flags.isStatic(),
	                                            flags.isTransient(),
	                                            createJavaToken());


	            toRose(fieldDecl, "TypeVisitor.Previsit field decl end: ", fieldName);
	        }
		
	     boolean isClassFound = false;
	     
	     public NodeVisitor enter(Node n) {
//	    	System.out.println("ENTER:" + n);
			if (isClassFound)
				return this;
			
			try {
				if (n instanceof SourceFile_c) {
					String file_name = ((SourceFile_c) n).node().toString();
					List<TopLevelDecl> decls = ((SourceFile_c) n).decls();
//					System.out.println("SRC2=" + file_name + ", decls2=" + decls.size() + ", node=" + n.hashCode());
//					if (file_name.indexOf(package_name.replaceAll("\\.", "/")) < 0) {
//					}
				}
				
				if (n instanceof X10ClassDecl_c) {
					String type = ((X10ClassDecl_c) n).name().toString();
					if (type.equals(class_name) || type.toLowerCase().equals(class_name)) {
						System.out.println("TYPE=" + type + ", class_name=" + class_name + ", package_name=" + package_name);
						isClassMatched = true;
						if (isPackageMatched) {
							visitDeclarations((X10ClassDecl_c)n);
//							visitDefinitions((X10ClassDecl_c)n); // so far, only declarations should be resolved
							isClassFound = true;
						}
					}
				}
				else if (n instanceof PackageNode) {
					String pkg = n.toString();
					if (pkg.equals(package_name)) { 
						isPackageMatched = true;
						if (isClassMatched) {
							visitDeclarations((X10ClassDecl_c)n);
//							visitDefinitions((X10ClassDecl_c)n);	// so far, only declarations should be resolved
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
		 * Invoke the appropriate visit method for a given dynamic type.
		 * Note that the order of invocation of the various visit() methods is significant!
		 */
		public void visitAppropriate(JL n) {
			if (n instanceof Id_c) { visit((Id_c)n); return; }
			if (n instanceof NodeList_c) { visit((NodeList_c)n); return; }
			if (n instanceof AnnotationNode_c) { visit((AnnotationNode_c)n); return; }
			if (n instanceof X10CanonicalTypeNode_c) { visit((X10CanonicalTypeNode_c)n); return; }
			if (n instanceof CanonicalTypeNode_c) { visit((CanonicalTypeNode_c)n); return; }
			if (n instanceof ArrayTypeNode_c) { visit((ArrayTypeNode_c)n); return; }
			if (n instanceof AmbDepTypeNode_c) { visit((AmbDepTypeNode_c)n); return; }
			if (n instanceof AmbTypeNode_c) { visit((AmbTypeNode_c)n); return; }
			if (n instanceof TypeNode_c) { visit((TypeNode_c)n); return; }
			if (n instanceof TypeDecl_c) { visit((TypeDecl_c)n); return; }
			if (n instanceof AtEach_c) { visit((AtEach_c)n); return; }
			if (n instanceof X10ClockedLoop_c) { visit((X10ClockedLoop_c)n); return; }
			if (n instanceof ForLoop_c) { visit((ForLoop_c)n); return; }
			if (n instanceof X10Loop_c) { visit((X10Loop_c)n); return; }
			if (n instanceof When_c) { visit((When_c)n); return; }
			if (n instanceof Try_c) { visit((Try_c)n); return; }
			if (n instanceof Throw_c) { visit((Throw_c)n); return; }
			if (n instanceof Switch_c) { visit((Switch_c)n); return; }
			if (n instanceof Return_c) { visit((Return_c)n); return; }
			if (n instanceof Next_c) { visit((Next_c)n); return; }
			if (n instanceof X10While_c) { visit((X10While_c)n); return; }
			if (n instanceof While_c) { visit((While_c)n); return; }
			if (n instanceof For_c) { visit((For_c)n); return; }
			if (n instanceof X10Do_c) { visit((X10Do_c)n); return; }
			if (n instanceof Do_c) { visit((Do_c)n); return; }
			if (n instanceof Loop_c) { visit((Loop_c)n); return; }
			if (n instanceof LocalTypeDef_c) { visit((LocalTypeDef_c)n); return; }
			if (n instanceof X10LocalDecl_c) { visit((X10LocalDecl_c)n); return; }
			if (n instanceof LocalDecl_c) { visit((LocalDecl_c)n); return; }
			if (n instanceof LocalClassDecl_c) { visit((LocalClassDecl_c)n); return; }
			if (n instanceof Labeled_c) { visit((Labeled_c)n); return; }
			if (n instanceof X10If_c) { visit((X10If_c)n); return; }
			if (n instanceof If_c) { visit((If_c)n); return; }
			if (n instanceof Finish_c) { visit((Finish_c)n); return; }
			if (n instanceof Eval_c) { visit((Eval_c)n); return; }
			if (n instanceof Empty_c) { visit((Empty_c)n); return; }
			if (n instanceof X10ConstructorCall_c) { visit((X10ConstructorCall_c)n); return; }
			if (n instanceof ConstructorCall_c) { visit((ConstructorCall_c)n); return; }
			if (n instanceof Catch_c) { visit((Catch_c)n); return; }
			if (n instanceof Case_c) { visit((Case_c)n); return; }
			if (n instanceof Branch_c) { visit((Branch_c)n); return; }
			if (n instanceof Atomic_c) { visit((Atomic_c)n); return; }
			if (n instanceof AtHomeStmt_c) { visit((AtHomeStmt_c)n); return; }
			if (n instanceof AtStmt_c) { visit((AtStmt_c)n); return; }
			if (n instanceof Async_c) { visit((Async_c)n); return; }
			if (n instanceof AssignPropertyCall_c) { visit((AssignPropertyCall_c)n); return; }
			if (n instanceof Assert_c) { visit((Assert_c)n); return; }
			if (n instanceof SwitchBlock_c) { visit((SwitchBlock_c)n); return; }
			if (n instanceof StmtSeq_c) { visit((StmtSeq_c)n); return; }
			if (n instanceof Block_c) { visit((Block_c)n); return; }
			if (n instanceof AbstractBlock_c) { visit((AbstractBlock_c)n); return; }
			if (n instanceof Stmt_c) { visit((Stmt_c)n); return; }
			if (n instanceof X10MethodDecl_c) { visit((X10MethodDecl_c)n); return; }
			if (n instanceof MethodDecl_c) { visit((MethodDecl_c)n); return; }
			if (n instanceof Initializer_c) { visit((Initializer_c)n); return; }
			if (n instanceof X10Formal_c) { visit((X10Formal_c)n); return; }
			if (n instanceof Formal_c) { visit((Formal_c)n); return; }
			if (n instanceof PropertyDecl_c) { visit((PropertyDecl_c)n); return; }
			if (n instanceof X10FieldDecl_c) { visit((X10FieldDecl_c)n); return; }
			if (n instanceof FieldDecl_c) { visit((FieldDecl_c)n); return; }
			if (n instanceof X10Unary_c) { visit((X10Unary_c)n); return; }
			if (n instanceof Unary_c) { visit((Unary_c)n); return; }
			if (n instanceof Tuple_c) { visit((Tuple_c)n); return; }
			if (n instanceof SubtypeTest_c) { visit((SubtypeTest_c)n); return; }
			if (n instanceof HasZeroTest_c) { visit((HasZeroTest_c)n); return; }
			if (n instanceof X10Special_c) { visit((X10Special_c)n); return; }
			if (n instanceof Special_c) { visit((Special_c)n); return; }
			if (n instanceof ParExpr_c) { visit((ParExpr_c)n); return; }
			if (n instanceof NewArray_c) { visit((NewArray_c)n); return; }
			if (n instanceof X10New_c) { visit((X10New_c)n); return; }
			if (n instanceof New_c) { visit((New_c)n); return; }
			if (n instanceof X10Local_c) { visit((X10Local_c)n); return; }
			if (n instanceof Local_c) { visit((Local_c)n); return; }
			if (n instanceof X10StringLit_c) { visit((X10StringLit_c)n); return; }
			if (n instanceof StringLit_c) { visit((StringLit_c)n); return; }
			if (n instanceof IntLit_c) { visit((IntLit_c)n); return; }
			if (n instanceof X10CharLit_c) { visit((X10CharLit_c)n); return; }
			if (n instanceof CharLit_c) { visit((CharLit_c)n); return; }
			if (n instanceof NumLit_c) { visit((NumLit_c)n); return; }
			if (n instanceof NullLit_c) { visit((NullLit_c)n); return; }
			if (n instanceof X10FloatLit_c) { visit((X10FloatLit_c)n); return; }
			if (n instanceof FloatLit_c) { visit((FloatLit_c)n); return; }
			if (n instanceof ClassLit_c) { visit((ClassLit_c)n); return; }
			if (n instanceof X10BooleanLit_c) { visit((X10BooleanLit_c)n); return; }
			if (n instanceof BooleanLit_c) { visit((BooleanLit_c)n); return; }
			if (n instanceof Lit_c) { visit((Lit_c)n); return; }
			if (n instanceof X10Instanceof_c) { visit((X10Instanceof_c)n); return; }
			if (n instanceof Instanceof_c) { visit((Instanceof_c)n); return; }
			if (n instanceof Here_c) { visit((Here_c)n); return; }
			if (n instanceof X10Field_c) { visit((X10Field_c)n); return; }
			if (n instanceof Field_c) { visit((Field_c)n); return; }
			if (n instanceof DepParameterExpr_c) { visit((DepParameterExpr_c)n); return; }
			if (n instanceof X10Conditional_c) { visit((X10Conditional_c)n); return; }
			if (n instanceof Conditional_c) { visit((Conditional_c)n); return; }
			if (n instanceof X10Cast_c) { visit((X10Cast_c)n); return; }
			if (n instanceof Cast_c) { visit((Cast_c)n); return; }
			if (n instanceof X10Call_c) { visit((X10Call_c)n); return; }
			if (n instanceof Call_c) { visit((Call_c)n); return; }
			if (n instanceof X10Binary_c) { visit((X10Binary_c)n); return; }
			if (n instanceof Binary_c) { visit((Binary_c)n); return; }
			if (n instanceof SettableAssign_c) { visit((SettableAssign_c)n); return; }
			if (n instanceof LocalAssign_c) { visit((LocalAssign_c)n); return; }
			if (n instanceof FieldAssign_c) { visit((FieldAssign_c)n); return; }
			if (n instanceof ArrayAccessAssign_c) { visit((ArrayAccessAssign_c)n); return; }
			if (n instanceof AmbAssign_c) { visit((AmbAssign_c)n); return; }
			if (n instanceof Assign_c) { visit((Assign_c)n); return; }
			if (n instanceof ArrayInit_c) { visit((ArrayInit_c)n); return; }
			if (n instanceof ArrayAccess_c) { visit((ArrayAccess_c)n); return; }
			if (n instanceof AmbExpr_c) { visit((AmbExpr_c)n); return; }
			if (n instanceof AtHomeExpr_c) { visit((AtHomeExpr_c)n); return; }
			if (n instanceof AtExpr_c) { visit((AtExpr_c)n); return; }
			if (n instanceof Closure_c) { visit((Closure_c)n); return; }
			if (n instanceof ClosureCall_c) { visit((ClosureCall_c)n); return; }
			if (n instanceof StmtExpr_c) { visit((StmtExpr_c)n); return; }
			if (n instanceof Allocation_c) { visit((Allocation_c)n); return; }
			if (n instanceof Expr_c) { visit((Expr_c)n); return; }
			if (n instanceof X10ConstructorDecl_c) { visit((X10ConstructorDecl_c)n); return; }
			if (n instanceof ConstructorDecl_c) { visit((ConstructorDecl_c)n); return; }
			if (n instanceof X10ClassDecl_c) { visit((X10ClassDecl_c)n); return; }
			if (n instanceof ClassDecl_c) { visit((ClassDecl_c)n); return; }
			if (n instanceof X10ClassBody_c) { visit((X10ClassBody_c)n); return; }
			if (n instanceof ClassBody_c) { visit((ClassBody_c)n); return; }
			if (n instanceof Term_c) { visit((Term_c)n); return; }
			if (n instanceof SourceFile_c) { visit((SourceFile_c)n); return; }
			if (n instanceof SourceCollection_c) { visit((SourceCollection_c)n); return; }
			if (n instanceof PackageNode_c) { visit((PackageNode_c)n); return; }
			if (n instanceof Import_c) { visit((Import_c)n); return; }
			if (n instanceof AmbReceiver_c) { visit((AmbReceiver_c)n); return; }
			if (n instanceof AmbPrefix_c) { visit((AmbPrefix_c)n); return; }
			if (n instanceof Node_c) { visit((Node_c)n); return; }
			if (n instanceof FunctionTypeNode_c) { visit((FunctionTypeNode_c)n); return;}
			if (n instanceof Node) { visit((Node)n); return; }
			throw new RuntimeException("No visit method defined in " + this.getClass() + 
					" for " + n.getClass()); 
		}

		/////////////////////////////////////////////////////////////////////////
		// Note that the indentation of the visit() methods below, while not
		// significant, is intended to signify the class hierarchy.
		/////////////////////////////////////////////////////////////////////////

		public void visit(Node n) { }
		public void visit(Node_c n) { visit((Node)n); }
			public void visit(AmbPrefix_c n) { visit((Node_c)n); }
			public void visit(Import_c n) { visit((Node_c)n); }
			public void visit(PackageNode_c n) { visit((Node_c)n); }
			public void visit(SourceCollection_c n) { visit((Node_c)n); }
//			public void visit(SourceFile_c n) { visit((Node_c)n); }
			public void visit(SourceFile_c n) { 
				System.out.println("source visited");
				visit((Node_c)n); 
			}
			public void visit(Term_c n) { visit((Node_c)n); }
				public void visit(ClassBody_c n) { visit((Term_c)n); }
					public void visit(X10ClassBody_c n) { visit((ClassBody_c)n); }
				public void visit(ClassDecl_c n) { visit((Term_c)n); }
					public void visit(X10ClassDecl_c n) { visit((ClassDecl_c)n); }
				public void visit(ConstructorDecl_c n) { visit((Term_c)n); }
				public void visit(Expr_c n) { visit((Term_c)n); }
					public void visit(AmbExpr_c n) { visit((Expr_c)n); }
					public void visit(ArrayInit_c n) { visit((Expr_c)n); }
					public void visit(Assign_c n) { visit((Expr_c)n); }
						public void visit(AmbAssign_c n) { visit((Assign_c)n); }
						public void visit(ArrayAccessAssign_c n) { visit((Assign_c)n); }
					public void visit(Binary_c n) { visit((Expr_c)n); }
					public void visit(Call_c n) { visit((Expr_c)n); }
					public void visit(Cast_c n) { visit((Expr_c)n); }
					public void visit(Conditional_c n) { visit((Expr_c)n); }
					public void visit(Field_c n) { visit((Expr_c)n); }
					public void visit(Instanceof_c n) { visit((Expr_c)n); }
					public void visit(Lit_c n) { visit((Expr_c)n); }
						public void visit(BooleanLit_c n) { visit((Lit_c)n); }
						public void visit(FloatLit_c n) { visit((Lit_c)n); }
						public void visit(NumLit_c n) { visit((Lit_c)n); }
							public void visit(CharLit_c n) { visit((NumLit_c)n); }
						public void visit(StringLit_c n) { visit((Lit_c)n); }
					public void visit(Local_c n) { visit((Expr_c)n); }
					public void visit(New_c n) { visit((Expr_c)n); }
					public void visit(NewArray_c n) { visit((Expr_c)n); }
					public void visit(Special_c n) { visit((Expr_c)n); }
					public void visit(Unary_c n) { visit((Expr_c)n); }
				public void visit(FieldDecl_c n) { visit((Term_c)n); }
				public void visit(Formal_c n) { visit((Term_c)n); }
				public void visit(Initializer_c n) { visit((Term_c)n); }
				public void visit(MethodDecl_c n) { visit((Term_c)n); }
					public void visit(X10MethodDecl_c n) { visit((MethodDecl_c)n); }
				public void visit(Stmt_c n) { visit((Term_c)n); }
					public void visit(AbstractBlock_c n) { visit((Stmt_c)n); }
					public void visit(ConstructorCall_c n) { visit((Stmt_c)n); }
					public void visit(If_c n) { visit((Stmt_c)n); }
					public void visit(LocalClassDecl_c n) { visit((Stmt_c)n); }
					public void visit(LocalDecl_c n) { visit((Stmt_c)n); }
					public void visit(Loop_c n) { visit((Stmt_c)n); }
						public void visit(Do_c n) { visit((Loop_c)n); }
						public void visit(While_c n) { visit((Loop_c)n); }
					public void visit(X10Loop_c n) { visit((Stmt_c)n); }
						public void visit(X10ClockedLoop_c n) { visit((X10Loop_c)n); }
				public void visit(TypeNode_c n) { visit((Term_c)n); }
//					public void visit(AmbTypeNode_c n) { visit((TypeNode_c)n); }
					public void visit(CanonicalTypeNode_c n) { visit((TypeNode_c)n); }
//					public void visit(AmbDepTypeNode_c n) { visit((TypeNode_c)n); }
				public void visit(AnnotationNode_c n) { visit((Node_c) n); }
				public void visit(NodeList_c n) { visit((Node_c) n); }
				public void visit(Id_c n) { visit((Node_c) n); }
//				public void visit(FunctionTypeNode_c n) { visit((FunctionTypeNode_c)n); }

				
				public  void searchFileList(String packageName, String typeName) throws IOException {
					System.out.println("TypeVisitor.searchFileList()");
					return;
					// MH-20140901 comment out for skipping to lookup library classes from library classess
//					for (Job job : jobList) {
//						FileSource source = (FileSource) job.source();
//						String sourceName = source.toString();
//						boolean isFoundSourceFile = false;
//						for (int i = 0; i <= fileIndex; ++i) { // including currently processing file
//							String sourceFileGiven = x10rose.ExtensionInfo.X10Scheduler.sourceList.get(i).source().path();
//							if (sourceName.equals(sourceFileGiven)) 
//								isFoundSourceFile = true;
//						}
//						if (isFoundSourceFile) 
//							continue;
//						
//						Reader reader = source.open();
//						ErrorQueue eq = job.extensionInfo().compiler().errorQueue();
//						Parser p = job.extensionInfo().parser(reader, source, eq);
//						Node ast = p.parse();
//						TypeVisitor tVisitor = new TypeVisitor(packageName, typeName, (SourceFile_c) job.ast(), job);
//		
//						ast.visit(tVisitor);
//						source.close();
//						if (tVisitor.isFound()) {
//							//////
////							for (int i = 0; i < 5; i++) {
////								System.out.println("loop test for parser " + i);
////								reader = source.open();
////								eq = job.extensionInfo().compiler().errorQueue();
////								p = job.extensionInfo().parser(reader, source, eq);
////								tVisitor = new TypeVisitor(packageName, typeName, source);
////								ast.visit(tVisitor);
////							}
//							//////
//							tVisitor.createTypeReferenceEnd();	
//							return;
//						}
//					}				
				}
				
				public void visit(AmbDepTypeNode_c n) {
					toRose(n, "TypeVisitor.AmbDepTypeNode_c : ", n.toString());
					String raw = n.toString();
					String ambTypeName = raw.replaceAll("\\{amb\\}", "");
					ambTypeName = trimTypeParameterClause(ambTypeName);
										
					if (   ambTypeName.equals("void") 
							|| ambTypeName.equals("boolean")|| ambTypeName.equals("Boolean")
							|| ambTypeName.equals("byte") 	|| ambTypeName.equals("Byte")
							|| ambTypeName.equals("char")	|| ambTypeName.equals("Char")
							|| ambTypeName.equals("int")	|| ambTypeName.equals("Int")
							|| ambTypeName.equals("short")	|| ambTypeName.equals("Short")
							|| ambTypeName.equals("float")	|| ambTypeName.equals("Float")
							|| ambTypeName.equals("long")	|| ambTypeName.equals("Long")
							|| ambTypeName.equals("double")	|| ambTypeName.equals("Double")) {
							JNI.cactionTypeReference("", ambTypeName, this, createJavaToken());
						}
						// TODO: convert Rail and GrowableRail as object types, not a normal array in ROSE
						// This will cause collision of the same overloaded method. To disable the check for collision,
						// commented out a line in x10_support.C in X10_ROSE_Connection/x10_support.C in ROSE side:
						//   ROSE_ASSERT(name.getString().compare(inputName.getString()) == 0); 
						// 
						else if (	ambTypeName.indexOf("Rail[") >= 0
								 || ambTypeName.indexOf("GrowableRail[") >= 0
								  ) {
							handleAmbType(n);
							// n.type() and n.nameString() return null, so manipulate string
//							String type = ambTypeName.substring(ambTypeName.indexOf('[')+1, ambTypeName.indexOf(']'));
//							int lastDot = type.lastIndexOf(".");
//							// so far, eliminate package name
//							type = type.substring(lastDot+1);
//							
//							JNI.cactionTypeReference(package_name, type, this, createJavaToken());
//							JNI.cactionArrayTypeReference(1, createJavaToken());
						}
						else if (ambTypeName.indexOf("self==this") >= 0) { 
							JNI.cactionTypeReference("", n.nameString(), this, createJavaToken());
						}
						else {
							handleAmbType(n);
						}
				}
				
				public void visit(AmbTypeNode_c n) {
					String raw = n.toString(); // n.nameString() throws NullPointerException, so use toString() instead
			       String ambTypeName = raw.replaceAll("\\{amb\\}", "");
					ambTypeName = trimTypeParameterClause(ambTypeName);
					System.out.println("AmbTypeNode_c : " + ambTypeName +", type=" + n.type());
					
					if (   ambTypeName.equals("void") 
						|| ambTypeName.equals("boolean")|| ambTypeName.equals("Boolean")
						|| ambTypeName.equals("byte") 	|| ambTypeName.equals("Byte")
						|| ambTypeName.equals("char")	|| ambTypeName.equals("Char")
						|| ambTypeName.equals("int")	|| ambTypeName.equals("Int")
						|| ambTypeName.equals("short")	|| ambTypeName.equals("Short")
						|| ambTypeName.equals("float")	|| ambTypeName.equals("Float")
						|| ambTypeName.equals("long")	|| ambTypeName.equals("Long")
						|| ambTypeName.equals("double")	|| ambTypeName.equals("Double")) {
						JNI.cactionTypeReference("", ambTypeName, this, createJavaToken());
					}
					// TODO: convert Rail and GrowableRail as object types, not a normal array in ROSE
					// This will cause collision of the same overloaded method. To disable the check for collision,
					// commented out a line in x10_support.C in X10_ROSE_Connection/x10_support.C in ROSE side:
					//   ROSE_ASSERT(name.getString().compare(inputName.getString()) == 0); 
					// 
					else if (	ambTypeName.indexOf("Rail[") >= 0
							 || ambTypeName.indexOf("GrowableRail[") >= 0
							  ) {
						handleAmbType(n);
						// n.type() and n.nameString() return null, so manipulate string
//						String type = ambTypeName.substring(ambTypeName.indexOf('[')+1, ambTypeName.indexOf(']'));
//						int lastDot = type.lastIndexOf(".");
//						// so far, eliminate package name
//						type = type.substring(lastDot+1);
//						
//						JNI.cactionTypeReference(package_name, type, this, createJavaToken());
//						JNI.cactionArrayTypeReference(1, createJavaToken());
					}
					else if (ambTypeName.indexOf("self==this") >= 0) { 
						JNI.cactionTypeReference("", n.nameString(), this, createJavaToken());
					}
					else {
						handleAmbType(n);
					}
				}
				
				public void visit(X10Formal_c n) {
					toRose(n, "TypeVisitor.formal: ", n.name().id().toString(), n.type().toString());

//		                args_location = createJavaToken(args[0], args[args.length - 1]); 
		//
//		                for (int j = 0; j < args.length; j++) {
//		                    Argument arg = args[j];
//		                    JavaToken arg_location = createJavaToken(arg);
//		                    generateAndPushType(arg.type.resolvedType, arg_location);
//		                    String argument_name = new String(arg.name);
//		                    JavaParser.cactionBuildArgumentSupport(argument_name,
//		                                                           arg.isVarArgs(),
//		                                                           arg.binding.isFinal(),
//		                                                           arg_location);
//		                }
					visitChild(n, n.type());
					// so far, all parameters's modifier are set as final
					JNI.cactionBuildArgumentSupport(n.name().toString(), n.vars().size()>0, false, createJavaToken(n, n.name().id().toString()));
				}

				public void visit(X10Call_c n) {
					toRose(n, "x10call: ", n.name().id().toString());
					if (n.name().id().toString().equals("operator()")) {
						System.out.println("So far, return in case of \"operator()\"");
						visitChild(n, n.target());
						return;
					}
					System.out.println("package=" + package_name + ", " + n.target());
					JNI.cactionMessageSend(package_name, n.target().type().name().toString(), n.name().id().toString(), createJavaToken(n, n.name().id().toString()));
					visitChild(n, n.target());
					
					List<Expr> args = n.arguments();
					List<Expr> argTypes = new ArrayList<Expr>();
					
					for (int i = 0; i < args.size(); ++i) {
						Node n2 = args.get(i);
						visitChild(n2, n2.node());
					}
					
					for (int i = 0; i < args.size(); ++i) {
						Type t = args.get(i).type();
//						System.out.println(i + ":" + t + ", " + args.get(i));
						Node n2 = args.get(i);
						JNI.cactionTypeReference(package_name, t.name().toString(), this, createJavaToken());
					}
//		 			visitChildren(n, argTypes);
//					visitChildren(n, n.typeArguments());
//					visitChildren(n, n.arguments());
					JNI.cactionTypeReference(package_name, n.target().type().name().toString(), this, createJavaToken());
					
					int num_parameters = n.arguments().size();
					int numTypeArguments = n.typeArguments().size();
					int numArguments = n.arguments().size();
					JNI.cactionMessageSendEnd(n.methodInstance().flags().isStatic(), n.target() != null, n.name().toString(), num_parameters, numTypeArguments, numArguments, createJavaToken(n, n.name().id().toString()));
				}

				public void visit(X10ConstructorDecl_c n) {
					toRose(n, "X10ConstructorDecl:", n.name().id().toString());
					
					List<Formal> formals = n.formals();
					
					String method_name = n.name().id().toString();
					StringBuffer param = new StringBuffer();
					for (Formal f : n.formals()) {
						param.append(f.type().toString().toLowerCase());
					}
//					if (n.returnType().toString().indexOf("{") >= 0) {
//						
//					}

//		          JNI.cactionBuildMethodSupportStart(method_name, method_index, createJavaToken());
//					visitChild(n, n.returnType());
//					visitChildren(n, n.formals());
					int method_index = memberMap.get(method_name+"("+param+")");

					JNI.cactionMethodDeclaration(n.name().id().toString(), method_index, formals.size(), 
							createJavaToken(n, n.name().id().toString()),
							createJavaToken(n, n.name().id().toString()+"_args"));

//		           JNI.cactionBuildMethodSupportEnd(method_name, method_index, // method index 
//														 false, false, false, 0, formals.size(),
//		                                           true, /* user-defined-method */
//							new JavaToken(n.name().id().toString(), new JavaSourcePositionInformation(n.position().line())), 
//							new JavaToken(n.name().id().toString()+"_args", new JavaSourcePositionInformation(n.position().line())));

//					visitChild(n, n.guard());
//					visitChild(n, n.offerType());
//					visitChildren(n, n.throwsTypes());
					visitChild(n, n.body());

					JNI.cactionMethodDeclarationEnd(n.body().statements().size(), createJavaToken(n, method_name+"("+param+")"));
//					String constructor_name = n.name().toString();
		//
//					JNI.cactionConstructorDeclarationHeader(constructor_name, false,
//							false, n.typeParameters().size(), n.formals().size(), n
//									.throwsTypes().size(), createJavaToken());
//					JNI.cactionConstructorDeclarationEnd(n.body().statements().size(),
//							createJavaToken());
		//
//					visitChildren(n, n.formals());
////					visitChild(n, n.guard());
////					visitChild(n, n.offerType());
////					visitChildren(n, n.throwsTypes());
//					visitChild(n, n.body());			
				}

				public void visit(X10ConstructorCall_c n) {
					toRose(n, "X10ConstructorCall:");
					visitChild(n, n.target());
					visitChildren(n, n.typeArguments());
					visitChildren(n, n.arguments());
				}

				public void visit(Block_c n) {
					toRose(n, "Block:");
					
//					   if (javaParserSupport.verboseLevel > 0)
//				            System.out.println("Inside of enter (Block,BlockScope)");
		//
		//
//				        if (javaParserSupport.verboseLevel > 0)
//				            System.out.println("Leaving enter (Block,BlockScope)");
		//
//				        return true; // do nothing by node, keep traversing
		//
//					System.out.println("Block start : " + n.statements().size() + ", " + n.statements());
				   JNI.cactionBlock(createJavaToken(n, n.toString()));
					visitChildren(n, n.statements());
					JNI.cactionBlockEnd(n.statements().size(), createJavaToken(n, n.toString()));
				}

				public void visit(StmtSeq_c n) {
					toRose(n, "StmtSeq: ");
					visitChildren(n, n.statements());
				}

				public void visit(AssignPropertyCall_c n) {
					toRose(n, "AssignPropertyCall:");
					// MH-20140313 go through at this moment
					// Tentatively process empty statement instead of propertycall
					JNI.cactionEmptyStatement(createJavaToken(n, n.toString()));
					JNI.cactionEmptyStatementEnd(createJavaToken(n, n.toString()));
//					visitChildren(n, n.arguments());
				}

				public void visit(Empty_c n) {
					toRose(n, "Empty:");
					JNI.cactionEmptyStatement(createJavaToken(n, n.toString()));
					JNI.cactionEmptyStatementEnd(createJavaToken(n, n.toString()));
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
					if (	class_name.equals("x10.lang.Boolean")
						||	class_name.equals("x10.lang.Byte")
						||	class_name.equals("x10.lang.Char")
						||	class_name.equals("x10.lang.Int")
						||	class_name.equals("x10.lang.Short")
						||	class_name.equals("x10.lang.Float")
						||	class_name.equals("x10.lang.Long")
						||	class_name.equals("x10.lang.Double")) {
						String canonicalTypeName = n.nameString();
						JNI.cactionTypeReference("", canonicalTypeName, this, createJavaToken());
					}
					else if (   n.type().toString().indexOf("x10.lang.Rail[") == 0
							  || n.type().toString().indexOf("x10.util.GrowableRail[") == 0
							  ) {
						String railString = n.type().toString();
						String type = railString.substring(railString.indexOf('[')+1, railString.indexOf(']'));
						int lastDot = type.lastIndexOf(".");
						
						String package_name = type.substring(0, lastDot);
						type = type.substring(lastDot+1);
						if (package_name.equals("x10.lang")
								&& (   type.equals("boolean")	|| type.equals("Boolean")
									|| type.equals("byte") 		|| type.equals("Byte")
									|| type.equals("char")		|| type.equals("Char")
									|| type.equals("int")		|| type.equals("Int")
									|| type.equals("short")		|| type.equals("Short")
									|| type.equals("float")		|| type.equals("Float")
									|| type.equals("long")		|| type.equals("Long")
									|| type.equals("double")	|| type.equals("Double")))	{
							package_name = "";
						}
						
						if (package_name.length() != 0) {
							JNI.cactionPushPackage(package_name, createJavaToken(n, package_name));
							JNI.cactionPopPackage();
						}
						JNI.cactionTypeReference(package_name, type, this, createJavaToken());

						JNI.cactionArrayTypeReference(1, createJavaToken());
					}
					else if (n.node().toString().indexOf("self==this") >= 0) { 
						JNI.cactionTypeReference("", n.nameString(), this, createJavaToken());
					}
//					else if (n.node().toString().indexOf("{amb}") >= 0) {
//						System.out.println("n.node()=" + n.node());
//						JNI.cactionTypeReference("", n.node().toString(), this, createJavaToken());
//					}
					else {
//						System.out.println(">>>> " + n.node() + ", " + n.nameString() + ", " + n.type().arrayOf());
						int index = n.node().toString().indexOf("[");
						String className = n.node().toString();
						if (index >= 0)
							className = className.substring(0, index);
						int lastDot = className.lastIndexOf('.');
						
						String pkg = "";
						String type = "";
						if (lastDot >= 0) {
							pkg = className.substring(0, lastDot);
							type = className.substring(lastDot+1);
						}
						else {
							type = className;
						}
						
//						System.out.println("className=" + className + ", pkg=" + pkg + ", type=" + type);
						// So far, I use a representation without package name such as "Rail". 
						// If I use a representation such as "x10.lang.Rail", lookupTypeByName() function tries
						// to look for a type name whether the type name is already registered or not. (and, there is
						// no registration for Rail, so ROSE compiler fails.)
						JNI.cactionTypeReference("", n.nameString(), this, createJavaToken());
//						JNI.cactionTypeReference(pkg, n.nameString(), this, createJavaToken());
					}
//					JNI.cactionTypeDeclaration("", n.nameString(), false, false, false, false, false, false, false, false, false, false);
				}

				public void visit(Return_c n) {
					toRose(n, "Return:", (String)null);
					JNI.cactionReturnStatement(createJavaToken(n, n.toString()));
					visitChild(n, n.expr());
					JNI.cactionReturnStatementEnd((n.expr() != null), createJavaToken(n, n.toString()));
				}
				
				public void visit(X10Binary_c n) {
					toRose(n, "X10Binary:", n.operator().toString());
					JNI.cactionBinaryExpression(createJavaToken(n, n.toString()));
					visitChild(n, n.left());
					visitChild(n, n.right());
					JNI.cactionBinaryExpressionEnd(getOperatorKind(n.operator()), createJavaToken(n, n.toString()));
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

					if (kind.equals(Special.Kind.THIS.toString())) {
						JNI.cactionThisReference(createJavaToken(n, n.kind().toString()));
					} else if (kind.equals(Special.Kind.SUPER.toString())) {
						JNI.cactionSuperReference(createJavaToken(n, n.kind().toString()));
					} else if (kind.equals(Special.Kind.SELF.toString())) {
						System.out.println("X10Special : Unhandled token " + kind);				
					}
				}

				public void visit(Here_c n) {
					toRose(n, "here:");
					JNI.cactionHere(createJavaToken(n, n.toString()));
				}

				public void visit(X10Local_c n) {
					toRose(n, "X10Local :", n.name().id().toString());
//		            JavaParser.cactionSingleNameReference(package_name, type_name, varRefName, javaParserSupport.createJavaToken(node));
					JNI.cactionSingleNameReference("", "", n.name().id().toString(), createJavaToken(n, n.name().id().toString()));
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
					toRose(n, "Branch:", n.kind()+(n.labelNode()!=null ? "\\n"+n.labelNode().id().toString() : ""));
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
//					System.out.println("target=" + n.target() + ", right=" + n.right());
					String fieldName = n.name().id().toString();
					visitChild(n, n.target());
					JNI.cactionTypeReference("", n.target().type().name().toString(), this, createJavaToken());
					JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, createJavaToken(n, fieldName));
					visitChild(n, n.right());			
					JNI.cactionAssignmentEnd(createJavaToken(n, n.right().toString()));
				}

				public void visit(X10Field_c n) {
					toRose(n, "X10Field:", n.name().id().toString());
//					System.out.println("target=" + n.target() 
//											+ ", target type=" + n.target().type().name() 
//											+ ", name=" + n.name().id() 
//											+ ", instnace=" + n.fieldInstance());
					String fieldName = n.name().id().toString();
					visit(n.target());
					JNI.cactionTypeReference("", n.target().type().name().toString(), this, createJavaToken());
					JNI.cactionFieldReferenceEnd(true /* explicit type passed */, fieldName, createJavaToken(n, fieldName));
				}

				public void visit(X10FieldDecl_c n) {
					toRose(n, "X10FieldDecl:", n.name().id().toString());
//					visitChild(n, n.type());
//					visitChild(n, n.init());
				}

				public void visit(X10LocalDecl_c n) {
					toRose(n, "X10LocalDecl:", n.name().id().toString());		
//					System.out.println("init=" + n.init());
					visitChild(n, n.type());		
					JNI.cactionLocalDeclaration(0, n.name().id().toString(), false, createJavaToken(n, n.name().id().toString()));
					JNI.cactionLocalDeclarationEnd(n.name().id().toString(), false, createJavaToken(n, n.name().id().toString()));
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
					
					JNI.cactionIfStatement(createJavaToken(n, n.toString()));
					JNI.cactionIfStatementEnd(n.alternative() != null, createJavaToken(n, n.toString()));
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
					toRose(n, "Labeled:",  n.labelNode().id().toString());
					visitChild(n, n.statement());
				}
				

				
				public void visit(X10BooleanLit_c n) {
					toRose(n, "X10BooleanLit:", Boolean.toString(n.value()));
					if (n.value()) 
						JNI.cactionTrueLiteral(createJavaToken(n, n.value() + ""));
					else
						JNI.cactionFalseLiteral(createJavaToken(n, n.value() + ""));
					
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
					JNI.cactionNullLiteral(createJavaToken(n, n.toString()));
				}

				public void visit(X10CharLit_c n) {
					toRose(n, "X10CharLit:", "" + n.value());
				}

				public void visit(IntLit_c n) {
					toRose(n, "IntLit:", Long.toString(n.value()));
					
					if (n.kind() == IntLit.INT) {
						JNI.cactionIntLiteral((int)n.value(), n.toString(), createJavaToken(n, n.value() + ""));
					} else if (n.kind() == IntLit.LONG) {
						JNI.cactionLongLiteral(n.value(), n.toString(), createJavaToken(n, n.value() + ""));
						
					} else {
						System.out.println("Unhandled literal : " + n.toString());
					}
				}

				public void visit(X10StringLit_c n) {
					toRose(n, "TypeVisitor.X10StringLit:", StringUtil.escape(n.value()));
				}

				
				public void visit(Finish_c n) {
					toRose(n, "Finish in TypeVisitor:");
					JNI.cactionFinish(createJavaToken(n, n.toString()));
					visitChild(n, n.body());
					JNI.cactionFinishEnd(createJavaToken(n, n.toString()));
				}

				public void visit(AtStmt_c n) {
					toRose(n, "AtStmt in TypeVisitor:");
					JNI.cactionAt(createJavaToken(n, n.toString()));
					visitChild(n, n.place());
					visitChild(n, n.body());
					JNI.cactionAtEnd(createJavaToken(n, n.toString()));
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
					JNI.cactionAsync(createJavaToken(n, n.toString()));
					visitChild(n, n.body());
					JNI.cactionAsyncEnd(createJavaToken(n, n.toString()));
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
					JNI.cactionAssignment(createJavaToken(n, n.toString()));
					visitChild(n, n.local());
					visitChild(n, n.right());
					JNI.cactionAssignmentEnd(createJavaToken(n, n.toString()));
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
	
	public class DV extends X10DelegatingVisitor {

		Node parent;
		CodeWriter w;

		DV(CodeWriter w, Node parent) {
			this.parent = parent;
			this.w = w;
		}

		void createDotNode(Node n, String name, String... extra) {
			w.write(getDotNode(n) + " [");
			Map<String, String> extraMap = new HashMap<String, String>();
			// defaults
			extraMap.put("style", "filled");
			extraMap.put("penwidth", "2.0");
			extraMap.put("label", n.getClass().toString() + (name != null ? "\\n" + name : ""));
			extraMap.put("shape", "box");
			extraMap.put("color", "#000000");
			extraMap.put("fillcolor", "#FFFFFF");
			// override defaults here
			for (String s : extra) {
				if (!s.contains("=")) {
					System.err.println("Unrecognised dot node option: " + s);
					continue;
				}
				String before = s.substring(0, s.indexOf("="));
				String after = s.substring(s.indexOf("=") + 1);
				extraMap.put(before, after);
			}
			for (String key : extraMap.keySet()) {
				w.writeln(key + "=\"" + extraMap.get(key) + "\", ");
			}
			w.writeln("];");
			
			if (parent != null)
				w.writeln(getDotNode(parent) + " -> " + getDotNode(n));
			
			try {
				w.flush();
			} catch (IOException e) {
				throw new Error(e);
			}
		}

		void visitChild(Node p, Node n) {
			if (n == null)
				return;
			new DV(w, p).visitAppropriate(n);
		}

		void visitChildren(Node p, List<? extends Node> l) {
			if (l == null)
				return;
			for (Node n : l)
				visitChild(p, n);
		}

		public void visit(Node_c n) {
			createDotNode(n, null, "fillcolor=#FF0000", "fontcolor=#ffffff", "shape=Mdiamond");
			System.err.println("UNRECOGNISED NODE in DotTranslator: " + n.getClass());
		}

		
		public void visit(SourceFile_c n) {
			createDotNode(n, n.source().path(), "shape=folder", "fillcolor=#000040", "fontcolor=#ffffff");
			visitChildren(n, n.decls());
		}

		public void visit(Import_c n) {
			createDotNode(n, n.kind() + " " + n.name().toString());
		}

		public void visit(PackageNode_c n) {
			createDotNode(n, n.package_().get().toString());
		}

		public void visit(X10ClassDecl_c n) {
			createDotNode(n, n.name().id().toString(), "shape=house", "fillcolor=#004000", "fontcolor=#ffffff");
			visitChildren(n, n.typeParameters());
			visitChildren(n, n.properties());
			visitChild(n, n.classInvariant());
//			visitChild(n, n.superClass());
			visitChildren(n, n.interfaces());
			visitChild(n, n.body());
		}

		public void visit(LocalClassDecl_c n) {
			createDotNode(n, null);
			visitChild(n, n.decl());
		}

		public void visit(X10ClassBody_c n) {
			createDotNode(n, null, "shape=house");
			visitChildren(n, n.members());
		}

		public void visit(X10MethodDecl_c n) {
			createDotNode(n, n.name().id().toString(), "shape=trapezium", "fillcolor=#FFC0A0");
			visitChildren(n, n.formals());
			visitChild(n, n.guard());
			visitChild(n, n.offerType());
			visitChildren(n, n.throwsTypes());
			visitChild(n, n.body());
		}

		public void visit(X10Formal_c n) {
			createDotNode(n, n.name().id().toString(), "shape=trapezium", "fillcolor=#FFC0A0");
			visitChild(n, n.type());
		}

		public void visit(X10Call_c n) {
			createDotNode(n, n.name().id().toString(), "fillcolor=#FFC0A0", "shape=oval");
			visitChild(n, n.target());
			visitChildren(n, n.typeArguments());
			visitChildren(n, n.arguments());
		}

		public void visit(X10ConstructorDecl_c n) {
			createDotNode(n, n.name().id().toString(), "shape=trapezium", "fillcolor=#FFFFA0");
			visitChildren(n, n.formals());
			visitChild(n, n.guard());
			visitChild(n, n.offerType());
			visitChildren(n, n.throwsTypes());
			visitChild(n, n.body());
		}

		public void visit(X10ConstructorCall_c n) {
			createDotNode(n, null, "fillcolor=#FFFFC0", "shape=oval");
			visitChild(n, n.target());
			visitChildren(n, n.typeArguments());
			visitChildren(n, n.arguments());
		}

		public void visit(Block_c n) {
			createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
			visitChildren(n, n.statements());
		}

		public void visit(StmtSeq_c n) {
			createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
			visitChildren(n, n.statements());
		}

		public void visit(AssignPropertyCall_c n) {
			createDotNode(n, null);
			visitChildren(n, n.arguments());
		}

		public void visit(Empty_c n) {
			createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
		}

		public void visit(X10CanonicalTypeNode_c n) {
			createDotNode(n, n.nameString(), "shape=oval", "fillcolor=#C0FFC0");

		}

		public void visit(Return_c n) {
			createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
			visitChild(n, n.expr());
		}

		public void visit(X10Binary_c n) {
			createDotNode(n, n.operator().toString(), "shape=oval");
			visitChild(n, n.left());
			visitChild(n, n.right());
		}

		public void visit(X10Unary_c n) {
			createDotNode(n, n.operator().toString(), "shape=oval");
			visitChild(n, n.expr());
		}

		public void visit(ParExpr_c n) { // parentheses
			createDotNode(n, "( )", "shape=oval");
			visitChild(n, n.expr());
		}		

		public void visit(X10Special_c n) {
			createDotNode(n, n.kind().toString(), "shape=oval", "fillcolor=#C0C0FF");
		}

		public void visit(Here_c n) {
			createDotNode(n, "here", "shape=oval", "fillcolor=#C0C0FF");
		}

		public void visit(X10Local_c n) {
			createDotNode(n, n.name().id().toString(), "shape=oval", "fillcolor=#C0C0C0");
		}

		public void visit(Eval_c n) {
			createDotNode(n, null);
			visitChild(n, n.expr());
		}

		public void visit(For_c n) {
			createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
			visitChildren(n, n.inits());
			visitChild(n, n.cond());
			visitChildren(n, n.iters());
			visitChild(n, n.body());
		}

		public void visit(ForLoop_c n) {
			createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
			visitChild(n, n.formal());
			visitChild(n, n.cond());
			visitChild(n, n.domain());
			visitChild(n, n.body());
		}

		public void visit(Branch_c n) {
			createDotNode(n, n.kind()+(n.labelNode()!=null ? "\\n"+n.labelNode().id().toString() : ""), "fillcolor=#000000", "fontcolor=#ffffff");
		}
		
		public void visit(X10Do_c n) {
			createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
			visitChild(n, n.cond());
			visitChild(n, n.body());
		}

		public void visit(X10While_c n) {
			createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
			visitChild(n, n.cond());
			visitChild(n, n.body());
		}


		public void visit(Tuple_c n) {
			createDotNode(n, null, "shape=oval");
			visitChildren(n, n.arguments());
		}

		public void visit(SettableAssign_c n) {
			createDotNode(n, null, "shape=oval");
			visitChild(n, n.left());
			visitChildren(n, n.index());
			visitChild(n, n.right());
		}

		
		public void visit(FieldAssign_c n) {
			createDotNode(n, n.name().id().toString(), "shape=oval", "fillcolor=#008000", "fontcolor=#ffffff");
			visit(n.target());
			visit(n.right());
		}

		public void visit(X10Field_c n) {
			createDotNode(n, n.name().id().toString(), "shape=oval", "fillcolor=#008000", "fontcolor=#ffffff");
			visit(n.target());
		}

		public void visit(X10FieldDecl_c n) {
			createDotNode(n, n.name().id().toString(), "fillcolor=#008000", "fontcolor=#ffffff");
			visitChild(n, n.type());
			visitChild(n, n.init());
		}

		public void visit(X10LocalDecl_c n) {
			createDotNode(n, n.name().id().toString());
			visitChild(n, n.type());
			visitChild(n, n.init());
		}

		public void visit(PropertyDecl_c n) {
			createDotNode(n, n.name().id().toString());
			visitChild(n, n.type());
			visitChild(n, n.init());
		}

		
		public void visit(X10If_c n) {
			createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
			visitChild(n, n.cond());
			visitChild(n, n.consequent());
			visitChild(n, n.alternative());
		}

		public void visit(X10Conditional_c n) {
			createDotNode(n, "? :", "shape=oval");
			visitChild(n, n.cond());
			visitChild(n, n.consequent());
			visitChild(n, n.alternative());
		}

		public void visit(Assert_c n) {
			createDotNode(n, null, "color=#FF0000");
			visitChild(n, n.cond());
			visitChild(n, n.errorMessage());
		}


		public void visit(Throw_c n) {
			createDotNode(n, null, "fillcolor=#400000", "fontcolor=#ffffff");
			visitChild(n, n.expr());
		}

		public void visit(Try_c n) {
			createDotNode(n, null, "fillcolor=#400000", "fontcolor=#ffffff");
			visitChild(n, n.tryBlock());
			visitChildren(n, n.catchBlocks());
			visitChild(n, n.finallyBlock());
		}

		public void visit(Catch_c n) {
			createDotNode(n, null, "fillcolor=#400000", "fontcolor=#ffffff");
			visitChild(n, n.formal());
			visitChild(n, n.body());
		}

		
		public void visit(Labeled_c n) {
			createDotNode(n, n.labelNode().id().toString(), "color=#A0A0A0");
			visitChild(n, n.statement());
		}
		

		
		public void visit(X10BooleanLit_c n) {
			createDotNode(n, Boolean.toString(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
		}

		public void visit(ClassLit_c n) {
			createDotNode(n, null, "shape=invtriangle", "fillcolor=#A0A0FF");
			visitChild(n, n.typeNode());
		}

		public void visit(X10FloatLit_c n) {
			createDotNode(n, Double.toString(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
		}

		public void visit(NullLit_c n) {
			createDotNode(n, "null", "shape=invtriangle", "fillcolor=#A0A0FF");
		}

		public void visit(X10CharLit_c n) {
			createDotNode(n, "" + n.value(), "shape=invtriangle", "fillcolor=#A0A0FF");
		}

		public void visit(IntLit_c n) {
			createDotNode(n, Long.toString(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
		}

		public void visit(X10StringLit_c n) {
			createDotNode(n, StringUtil.escape(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
		}

		
		public void visit(Finish_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.body());
		}

		public void visit(AtStmt_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.place());
			visitChild(n, n.body());
		}

		public void visit(AtHomeStmt_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.place());
			visitChild(n, n.body());
		}

		public void visit(AtExpr_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.place());
			visitChild(n, n.body());
		}

		public void visit(AtHomeExpr_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.place());
			visitChild(n, n.body());
		}

		public void visit(AtEach_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.formal());
			visitChild(n, n.domain());
			visitChild(n, n.body());
		}

		public void visit(Async_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.body());
		}
		
		public void visit(Atomic_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.body());
		}

		public void visit(When_c n) {
			createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
			visitChild(n, n.expr());
			visitChild(n, n.stmt());
		}



		public void visit(X10New_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#FFFFC0");
			visitChildren(n, n.typeArguments());
			visitChildren(n, n.arguments());
			visitChild(n, n.objectType());
			visitChild(n, n.body());
		}

		public void visit(Allocation_c n) {
			createDotNode(n, null, "shape=oval");
			visitChild(n, n.objectType());
		}
				
		public void visit(LocalAssign_c n) {
			createDotNode(n, null, "shape=oval");
			visitChild(n, n.local());
			visitChild(n, n.right());
		}

		public void visit(X10Cast_c n) {
			createDotNode(n, null, "shape=oval");
			visitChild(n, n.castType());
			visitChild(n, n.expr());
		}

		public void visit(X10Instanceof_c n) {
			createDotNode(n, null, "shape=oval");
			visitChild(n, n.compareType());
			visitChild(n, n.expr());
		}

		public void visit(SubtypeTest_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#C0FFC0");
			visitChild(n, n.subtype());
			visitChild(n, n.supertype());
		}

		public void visit(DepParameterExpr_c n) {
			createDotNode(n, null, "shape=oval");
			visitChildren(n, n.formals());
			visitChildren(n, n.condition());
		}

		public void visit(HasZeroTest_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#C0FFC0");
			visitChild(n, n.parameter());
		}

		public void visit(Closure_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#FFA0A0");
			visitChildren(n, n.formals());
			visitChild(n, n.body());
		}

		public void visit(ClosureCall_c n) {
			createDotNode(n, null, "shape=oval", "fillcolor=#FFA0A0");
			visitChild(n, n.target());
			visitChildren(n, n.arguments());
		}

		public void visit(StmtExpr_c n) {
			createDotNode(n, null, "shape=oval");
			visitChildren(n, n.statements());
		}

		
		public void visit(AmbReceiver_c n) {
			createDotNode(n, n.nameNode().id().toString());
		}

		
		public void visit(Switch_c n) {
			createDotNode(n, null);
			visitChild(n, n.expr());
			visitChildren(n, n.elements());
		}

		public void visit(SwitchBlock_c n) {
			createDotNode(n, null);
			visitChildren(n, n.statements());
		}

		public void visit(Case_c n) {
			createDotNode(n, null);
			visitChild(n, n.expr());
		}

		public void visit(LocalTypeDef_c n) {
			createDotNode(n, null);
			visitChild(n, n.typeDef());
		}

		public void visit(Next_c n) {
			createDotNode(n, "here", "shape=oval", "fillcolor=#C0C0FF");
		}

		public void visit(TypeDecl_c n) {
			createDotNode(n, n.name().id().toString(), "fillcolor=#C0FFC0");
			visitChild(n, n.type());
		}

	}
}
