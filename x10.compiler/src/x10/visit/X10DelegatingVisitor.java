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

package x10.visit;

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
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr_c;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal_c;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.Instanceof_c;
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
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.SourceCollection_c;
import polyglot.ast.SourceFile_c;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt_c;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Term_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode_c;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
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
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10Loop_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10Special_c;
import x10.ast.X10StringLit_c;
import x10.ast.X10Unary_c;
import x10.ast.X10While_c;

/**
 * Visitor on the AST nodes that dispatches to the appropriate
 * visit() method.
 * Generated from the inheritance hierarchy of X10.
 *
 * @author Igor Peshansky
 */
public class X10DelegatingVisitor {
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
			public void visit(AmbReceiver_c n) { visit((AmbPrefix_c)n); }
		public void visit(Import_c n) { visit((Node_c)n); }
		public void visit(PackageNode_c n) { visit((Node_c)n); }
		public void visit(SourceCollection_c n) { visit((Node_c)n); }
		public void visit(SourceFile_c n) { visit((Node_c)n); }
		public void visit(Term_c n) { visit((Node_c)n); }
			public void visit(ClassBody_c n) { visit((Term_c)n); }
				public void visit(X10ClassBody_c n) { visit((ClassBody_c)n); }
			public void visit(ClassDecl_c n) { visit((Term_c)n); }
				public void visit(X10ClassDecl_c n) { visit((ClassDecl_c)n); }
			public void visit(ConstructorDecl_c n) { visit((Term_c)n); }
				public void visit(X10ConstructorDecl_c n) { visit((ConstructorDecl_c)n); }
			public void visit(Expr_c n) { visit((Term_c)n); }
				public void visit(Allocation_c n) { visit((Expr_c)n); }
				public void visit(AmbExpr_c n) { visit((Expr_c)n); }
				public void visit(ArrayAccess_c n) { visit((Expr_c)n); }
				public void visit(ArrayInit_c n) { visit((Expr_c)n); }
				public void visit(Assign_c n) { visit((Expr_c)n); }
					public void visit(AmbAssign_c n) { visit((Assign_c)n); }
					public void visit(ArrayAccessAssign_c n) { visit((Assign_c)n); }
					public void visit(FieldAssign_c n) { visit((Assign_c)n); }
					public void visit(LocalAssign_c n) { visit((Assign_c)n); }
					public void visit(SettableAssign_c n) { visit((Assign_c)n); }
				public void visit(Binary_c n) { visit((Expr_c)n); }
					public void visit(X10Binary_c n) { visit((Binary_c)n); }
				public void visit(Call_c n) { visit((Expr_c)n); }
					public void visit(X10Call_c n) { visit((Call_c)n); }
				public void visit(Cast_c n) { visit((Expr_c)n); }
					public void visit(X10Cast_c n) { visit((Cast_c)n); }
				public void visit(Conditional_c n) { visit((Expr_c)n); }
					public void visit(X10Conditional_c n) { visit((Conditional_c)n); }
				public void visit(DepParameterExpr_c n) { visit((Node_c)n); }
				public void visit(Field_c n) { visit((Expr_c)n); }
					public void visit(X10Field_c n) { visit((Field_c)n); }
				public void visit(Here_c n) { visit((Expr_c)n); }
				public void visit(Instanceof_c n) { visit((Expr_c)n); }
					public void visit(X10Instanceof_c n) { visit((Instanceof_c)n); }
				public void visit(Lit_c n) { visit((Expr_c)n); }
					public void visit(BooleanLit_c n) { visit((Lit_c)n); }
						public void visit(X10BooleanLit_c n) { visit((BooleanLit_c)n); }
					public void visit(ClassLit_c n) { visit((Lit_c)n); }
					public void visit(FloatLit_c n) { visit((Lit_c)n); }
						public void visit(X10FloatLit_c n) { visit((FloatLit_c)n); }
					public void visit(NullLit_c n) { visit((Lit_c)n); }
					public void visit(NumLit_c n) { visit((Lit_c)n); }
						public void visit(CharLit_c n) { visit((NumLit_c)n); }
							public void visit(X10CharLit_c n) { visit((CharLit_c)n); }
						public void visit(IntLit_c n) { visit((NumLit_c)n); }
					public void visit(StringLit_c n) { visit((Lit_c)n); }
						public void visit(X10StringLit_c n) { visit((StringLit_c)n); }
				public void visit(Local_c n) { visit((Expr_c)n); }
					public void visit(X10Local_c n) { visit((Local_c)n); }
				public void visit(New_c n) { visit((Expr_c)n); }
					public void visit(X10New_c n) { visit((New_c)n); }
				public void visit(NewArray_c n) { visit((Expr_c)n); }
				public void visit(ParExpr_c n) { visit((Expr_c)n); }
				public void visit(Special_c n) { visit((Expr_c)n); }
					public void visit(X10Special_c n) { visit((Special_c)n); }
				public void visit(SubtypeTest_c n) { visit((Expr_c)n); }
				public void visit(HasZeroTest_c n) { visit((Expr_c)n); }
				public void visit(Tuple_c n) { visit((Expr_c)n); }
				public void visit(Unary_c n) { visit((Expr_c)n); }
					public void visit(X10Unary_c n) { visit((Unary_c)n); }
				public void visit(Closure_c n) { visit((Expr_c)n); }
					public void visit(AtExpr_c n) { visit((Expr_c)n); }
						public void visit(AtHomeExpr_c n) { visit((AtExpr_c)n); }
				public void visit(ClosureCall_c n) { visit((Expr_c)n); }
				public void visit(StmtExpr_c n) { visit((Expr_c)n); }
			public void visit(FieldDecl_c n) { visit((Term_c)n); }
				public void visit(X10FieldDecl_c n) { visit((FieldDecl_c)n); }
					public void visit(PropertyDecl_c n) { visit((X10FieldDecl_c)n); }
			public void visit(Formal_c n) { visit((Term_c)n); }
				public void visit(X10Formal_c n) { visit((Formal_c)n); }
			public void visit(Initializer_c n) { visit((Term_c)n); }
			public void visit(MethodDecl_c n) { visit((Term_c)n); }
				public void visit(X10MethodDecl_c n) { visit((MethodDecl_c)n); }
			public void visit(Stmt_c n) { visit((Term_c)n); }
				public void visit(AbstractBlock_c n) { visit((Stmt_c)n); }
					public void visit(Block_c n) { visit((AbstractBlock_c)n); }
					public void visit(StmtSeq_c n) { visit((AbstractBlock_c)n); }
					public void visit(SwitchBlock_c n) { visit((AbstractBlock_c)n); }
				public void visit(Assert_c n) { visit((Stmt_c)n); }
				public void visit(AssignPropertyCall_c n) { visit((Stmt_c)n); }
				public void visit(AtStmt_c n) { visit((Stmt_c)n); }
					public void visit(AtHomeStmt_c n) { visit((AtStmt_c)n); }
				public void visit(Async_c n) { visit((Stmt_c)n); }
				public void visit(Atomic_c n) { visit((Stmt_c)n); }
				public void visit(Branch_c n) { visit((Stmt_c)n); }
				public void visit(Case_c n) { visit((Stmt_c)n); }
				public void visit(Catch_c n) { visit((Stmt_c)n); }
				public void visit(ConstructorCall_c n) { visit((Stmt_c)n); }
					public void visit(X10ConstructorCall_c n) { visit((ConstructorCall_c)n); }
				public void visit(Empty_c n) { visit((Stmt_c)n); }
				public void visit(Eval_c n) { visit((Stmt_c)n); }
				public void visit(Finish_c n) { visit((Stmt_c)n); }
				public void visit(If_c n) { visit((Stmt_c)n); }
					public void visit(X10If_c n) { visit((If_c)n); }
				public void visit(Labeled_c n) { visit((Stmt_c)n); }
				public void visit(LocalClassDecl_c n) { visit((Stmt_c)n); }
				public void visit(LocalDecl_c n) { visit((Stmt_c)n); }
					public void visit(X10LocalDecl_c n) { visit((LocalDecl_c)n); }
				public void visit(LocalTypeDef_c n) { visit((Stmt_c)n); }
				public void visit(Loop_c n) { visit((Stmt_c)n); }
					public void visit(Do_c n) { visit((Loop_c)n); }
						public void visit(X10Do_c n) { visit((Do_c)n); }
					public void visit(For_c n) { visit((Loop_c)n); }
					public void visit(While_c n) { visit((Loop_c)n); }
						public void visit(X10While_c n) { visit((While_c)n); }
				public void visit(Next_c n) { visit((Stmt_c)n); }
				public void visit(Return_c n) { visit((Stmt_c)n); }
				public void visit(Switch_c n) { visit((Stmt_c)n); }
				public void visit(Throw_c n) { visit((Stmt_c)n); }
				public void visit(Try_c n) { visit((Stmt_c)n); }
				public void visit(When_c n) { visit((Stmt_c)n); }
				public void visit(X10Loop_c n) { visit((Stmt_c)n); }
					public void visit(ForLoop_c n) { visit((X10Loop_c)n); }
					public void visit(X10ClockedLoop_c n) { visit((X10Loop_c)n); }
						public void visit(AtEach_c n) { visit((X10ClockedLoop_c)n); }
			public void visit(TypeDecl_c n) { visit((Term_c)n); }
			public void visit(TypeNode_c n) { visit((Term_c)n); }
				public void visit(AmbTypeNode_c n) { visit((TypeNode_c)n); }
				public void visit(ArrayTypeNode_c n) { visit((TypeNode_c)n); }
				public void visit(CanonicalTypeNode_c n) { visit((TypeNode_c)n); }
				public void visit(X10CanonicalTypeNode_c n) { visit((CanonicalTypeNode_c)n); }
				public void visit(AmbDepTypeNode_c n) { visit((TypeNode_c)n); }
			public void visit(AnnotationNode_c n) { visit((Node_c) n); }
			public void visit(NodeList_c n) { visit((Node_c) n); }
			public void visit(Id_c n) { visit((Node_c) n); }
}
