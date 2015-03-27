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

package x10.parser.antlr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JDialog;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.Assert;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Case;
import polyglot.ast.Catch;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Do;
import polyglot.ast.Empty;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.Labeled;
import polyglot.ast.Lit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.SwitchElement;
import polyglot.ast.Throw;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary.Operator;
import polyglot.ast.While;
import polyglot.frontend.FileSource;
import polyglot.lex.BooleanLiteral;
import polyglot.lex.CharacterLiteral;
import polyglot.lex.DoubleLiteral;
import polyglot.lex.FloatLiteral;
import polyglot.lex.StringLiteral;
import polyglot.parse.ParsedName;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.X10CompilerOptions;
import x10.ast.AmbMacroTypeNode;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.Finish;
import x10.ast.FinishExpr;
import x10.ast.HasZeroTest;
import x10.ast.Offer;
import x10.ast.PropertyDecl;
import x10.ast.SettableAssign;
import x10.ast.Tuple;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.ast.When;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Formal;
import x10.ast.X10Loop;
import x10.ast.X10Unary_c;
import x10.extension.X10Ext;
import x10.parser.X10Parsersym;
import x10.parser.antlr.generated.*;
import x10.parser.antlr.generated.X10Parser.AnnotationContext;
import x10.parser.antlr.generated.X10Parser.AnnotationStatementContext;
import x10.parser.antlr.generated.X10Parser.AnnotationsContext;
import x10.parser.antlr.generated.X10Parser.AnnotationsoptContext;
import x10.parser.antlr.generated.X10Parser.ApplyOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ArgumentListContext;
import x10.parser.antlr.generated.X10Parser.ArgumentListoptContext;
import x10.parser.antlr.generated.X10Parser.ArgumentsContext;
import x10.parser.antlr.generated.X10Parser.ArgumentsoptContext;
import x10.parser.antlr.generated.X10Parser.AssertStatement0Context;
import x10.parser.antlr.generated.X10Parser.AssertStatement1Context;
import x10.parser.antlr.generated.X10Parser.AssertStatementContext;
import x10.parser.antlr.generated.X10Parser.AssignPropertyCallContext;
import x10.parser.antlr.generated.X10Parser.Assignment0Context;
import x10.parser.antlr.generated.X10Parser.Assignment1Context;
import x10.parser.antlr.generated.X10Parser.Assignment2Context;
import x10.parser.antlr.generated.X10Parser.AssignmentContext;
import x10.parser.antlr.generated.X10Parser.AssignmentExpression0Context;
import x10.parser.antlr.generated.X10Parser.AssignmentExpression1Context;
import x10.parser.antlr.generated.X10Parser.AssignmentExpressionContext;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator0Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator10Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator11Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator12Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator13Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator14Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator15Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator16Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator17Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator18Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator19Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator1Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator20Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator2Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator3Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator4Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator5Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator6Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator7Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator8Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperator9Context;
import x10.parser.antlr.generated.X10Parser.AssignmentOperatorContext;
import x10.parser.antlr.generated.X10Parser.AsyncStatement0Context;
import x10.parser.antlr.generated.X10Parser.AsyncStatement1Context;
import x10.parser.antlr.generated.X10Parser.AsyncStatementContext;
import x10.parser.antlr.generated.X10Parser.AtEachStatement0Context;
import x10.parser.antlr.generated.X10Parser.AtEachStatement1Context;
import x10.parser.antlr.generated.X10Parser.AtEachStatementContext;
import x10.parser.antlr.generated.X10Parser.AtExpressionContext;
import x10.parser.antlr.generated.X10Parser.AtStatementContext;
import x10.parser.antlr.generated.X10Parser.AtomicStatementContext;
import x10.parser.antlr.generated.X10Parser.BasicForStatementContext;
import x10.parser.antlr.generated.X10Parser.BinOp0Context;
import x10.parser.antlr.generated.X10Parser.BinOp10Context;
import x10.parser.antlr.generated.X10Parser.BinOp11Context;
import x10.parser.antlr.generated.X10Parser.BinOp12Context;
import x10.parser.antlr.generated.X10Parser.BinOp13Context;
import x10.parser.antlr.generated.X10Parser.BinOp14Context;
import x10.parser.antlr.generated.X10Parser.BinOp15Context;
import x10.parser.antlr.generated.X10Parser.BinOp16Context;
import x10.parser.antlr.generated.X10Parser.BinOp17Context;
import x10.parser.antlr.generated.X10Parser.BinOp18Context;
import x10.parser.antlr.generated.X10Parser.BinOp19Context;
import x10.parser.antlr.generated.X10Parser.BinOp1Context;
import x10.parser.antlr.generated.X10Parser.BinOp20Context;
import x10.parser.antlr.generated.X10Parser.BinOp21Context;
import x10.parser.antlr.generated.X10Parser.BinOp22Context;
import x10.parser.antlr.generated.X10Parser.BinOp23Context;
import x10.parser.antlr.generated.X10Parser.BinOp24Context;
import x10.parser.antlr.generated.X10Parser.BinOp25Context;
import x10.parser.antlr.generated.X10Parser.BinOp26Context;
import x10.parser.antlr.generated.X10Parser.BinOp27Context;
import x10.parser.antlr.generated.X10Parser.BinOp28Context;
import x10.parser.antlr.generated.X10Parser.BinOp29Context;
import x10.parser.antlr.generated.X10Parser.BinOp2Context;
import x10.parser.antlr.generated.X10Parser.BinOp3Context;
import x10.parser.antlr.generated.X10Parser.BinOp4Context;
import x10.parser.antlr.generated.X10Parser.BinOp5Context;
import x10.parser.antlr.generated.X10Parser.BinOp6Context;
import x10.parser.antlr.generated.X10Parser.BinOp7Context;
import x10.parser.antlr.generated.X10Parser.BinOp8Context;
import x10.parser.antlr.generated.X10Parser.BinOp9Context;
import x10.parser.antlr.generated.X10Parser.BinOpContext;
import x10.parser.antlr.generated.X10Parser.BinaryOperatorDeclContext;
import x10.parser.antlr.generated.X10Parser.BinaryOperatorDeclThisLeftContext;
import x10.parser.antlr.generated.X10Parser.BinaryOperatorDeclThisRightContext;
import x10.parser.antlr.generated.X10Parser.BinaryOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.BlockContext;
import x10.parser.antlr.generated.X10Parser.BlockInteriorStatement0Context;
import x10.parser.antlr.generated.X10Parser.BlockInteriorStatement1Context;
import x10.parser.antlr.generated.X10Parser.BlockInteriorStatement2Context;
import x10.parser.antlr.generated.X10Parser.BlockInteriorStatement3Context;
import x10.parser.antlr.generated.X10Parser.BlockInteriorStatement4Context;
import x10.parser.antlr.generated.X10Parser.BlockInteriorStatementContext;
import x10.parser.antlr.generated.X10Parser.BlockStatementsContext;
import x10.parser.antlr.generated.X10Parser.BlockStatementsoptContext;
import x10.parser.antlr.generated.X10Parser.BooleanLiteralContext;
import x10.parser.antlr.generated.X10Parser.BreakStatementContext;
import x10.parser.antlr.generated.X10Parser.ByteLiteralContext;
import x10.parser.antlr.generated.X10Parser.CastExpression0Context;
import x10.parser.antlr.generated.X10Parser.CastExpression1Context;
import x10.parser.antlr.generated.X10Parser.CastExpression2Context;
import x10.parser.antlr.generated.X10Parser.CastExpressionContext;
import x10.parser.antlr.generated.X10Parser.CatchClauseContext;
import x10.parser.antlr.generated.X10Parser.CatchesContext;
import x10.parser.antlr.generated.X10Parser.CatchesoptContext;
import x10.parser.antlr.generated.X10Parser.CharacterLiteralContext;
import x10.parser.antlr.generated.X10Parser.ClassBodyContext;
import x10.parser.antlr.generated.X10Parser.ClassBodyoptContext;
import x10.parser.antlr.generated.X10Parser.ClassDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ClassMemberDeclaration0Context;
import x10.parser.antlr.generated.X10Parser.ClassMemberDeclaration1Context;
import x10.parser.antlr.generated.X10Parser.ClassMemberDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ClassNameContext;
import x10.parser.antlr.generated.X10Parser.ClassTypeContext;
import x10.parser.antlr.generated.X10Parser.ClockedClauseoptContext;
import x10.parser.antlr.generated.X10Parser.ClosureBody0Context;
import x10.parser.antlr.generated.X10Parser.ClosureBody1Context;
import x10.parser.antlr.generated.X10Parser.ClosureBody2Context;
import x10.parser.antlr.generated.X10Parser.ClosureBodyContext;
import x10.parser.antlr.generated.X10Parser.ClosureExpressionContext;
import x10.parser.antlr.generated.X10Parser.CompilationUnitContext;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression0Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression10Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression11Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression12Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression13Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression14Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression16Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression17Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression18Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression19Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression1Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression20Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression21Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression25Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression26Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression2Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression3Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression4Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression5Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression6Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression7Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpression8Context;
import x10.parser.antlr.generated.X10Parser.ConditionalExpressionContext;
import x10.parser.antlr.generated.X10Parser.ConstantExpressionContext;
import x10.parser.antlr.generated.X10Parser.ConstraintConjunctionoptContext;
import x10.parser.antlr.generated.X10Parser.ConstructorBlockContext;
import x10.parser.antlr.generated.X10Parser.ConstructorBody0Context;
import x10.parser.antlr.generated.X10Parser.ConstructorBody1Context;
import x10.parser.antlr.generated.X10Parser.ConstructorBody2Context;
import x10.parser.antlr.generated.X10Parser.ConstructorBody3Context;
import x10.parser.antlr.generated.X10Parser.ConstructorBodyContext;
import x10.parser.antlr.generated.X10Parser.ConstructorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ContinueStatementContext;
import x10.parser.antlr.generated.X10Parser.ConversionOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ConversionOperatorDeclarationExplicitContext;
import x10.parser.antlr.generated.X10Parser.ConversionOperatorDeclarationImplicitContext;
import x10.parser.antlr.generated.X10Parser.DepParametersContext;
import x10.parser.antlr.generated.X10Parser.DoStatementContext;
import x10.parser.antlr.generated.X10Parser.DoubleLiteralContext;
import x10.parser.antlr.generated.X10Parser.EmptyStatementContext;
import x10.parser.antlr.generated.X10Parser.EnhancedForStatement0Context;
import x10.parser.antlr.generated.X10Parser.EnhancedForStatement1Context;
import x10.parser.antlr.generated.X10Parser.EnhancedForStatementContext;
import x10.parser.antlr.generated.X10Parser.ExplicitConstructorInvocationContext;
import x10.parser.antlr.generated.X10Parser.ExplicitConstructorInvocationPrimarySuperContext;
import x10.parser.antlr.generated.X10Parser.ExplicitConstructorInvocationPrimaryThisContext;
import x10.parser.antlr.generated.X10Parser.ExplicitConstructorInvocationSuperContext;
import x10.parser.antlr.generated.X10Parser.ExplicitConstructorInvocationThisContext;
import x10.parser.antlr.generated.X10Parser.ExplicitConversionOperatorDecl0Context;
import x10.parser.antlr.generated.X10Parser.ExplicitConversionOperatorDecl1Context;
import x10.parser.antlr.generated.X10Parser.ExplicitConversionOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ExpressionContext;
import x10.parser.antlr.generated.X10Parser.ExpressionNameContext;
import x10.parser.antlr.generated.X10Parser.ExpressionStatementContext;
import x10.parser.antlr.generated.X10Parser.ExpressionoptContext;
import x10.parser.antlr.generated.X10Parser.ExtendsInterfacesoptContext;
import x10.parser.antlr.generated.X10Parser.FieldAccess0Context;
import x10.parser.antlr.generated.X10Parser.FieldAccess1Context;
import x10.parser.antlr.generated.X10Parser.FieldAccess2Context;
import x10.parser.antlr.generated.X10Parser.FieldAccessContext;
import x10.parser.antlr.generated.X10Parser.FieldDeclarationContext;
import x10.parser.antlr.generated.X10Parser.FieldDeclarator0Context;
import x10.parser.antlr.generated.X10Parser.FieldDeclarator1Context;
import x10.parser.antlr.generated.X10Parser.FieldDeclaratorContext;
import x10.parser.antlr.generated.X10Parser.FieldDeclaratorsContext;
import x10.parser.antlr.generated.X10Parser.FinallyBlockContext;
import x10.parser.antlr.generated.X10Parser.FinishStatement0Context;
import x10.parser.antlr.generated.X10Parser.FinishStatement1Context;
import x10.parser.antlr.generated.X10Parser.FinishStatementContext;
import x10.parser.antlr.generated.X10Parser.FloatingPointLiteralContext;
import x10.parser.antlr.generated.X10Parser.ForInit0Context;
import x10.parser.antlr.generated.X10Parser.ForInit1Context;
import x10.parser.antlr.generated.X10Parser.ForInitContext;
import x10.parser.antlr.generated.X10Parser.ForInitoptContext;
import x10.parser.antlr.generated.X10Parser.ForStatement0Context;
import x10.parser.antlr.generated.X10Parser.ForStatement1Context;
import x10.parser.antlr.generated.X10Parser.ForStatementContext;
import x10.parser.antlr.generated.X10Parser.ForUpdateContext;
import x10.parser.antlr.generated.X10Parser.ForUpdateoptContext;
import x10.parser.antlr.generated.X10Parser.FormalDeclarator0Context;
import x10.parser.antlr.generated.X10Parser.FormalDeclarator1Context;
import x10.parser.antlr.generated.X10Parser.FormalDeclarator2Context;
import x10.parser.antlr.generated.X10Parser.FormalDeclaratorContext;
import x10.parser.antlr.generated.X10Parser.FormalDeclaratorsContext;
import x10.parser.antlr.generated.X10Parser.FormalParameter0Context;
import x10.parser.antlr.generated.X10Parser.FormalParameter1Context;
import x10.parser.antlr.generated.X10Parser.FormalParameter2Context;
import x10.parser.antlr.generated.X10Parser.FormalParameterContext;
import x10.parser.antlr.generated.X10Parser.FormalParameterListContext;
import x10.parser.antlr.generated.X10Parser.FormalParameterListoptContext;
import x10.parser.antlr.generated.X10Parser.FormalParametersContext;
import x10.parser.antlr.generated.X10Parser.FullyQualifiedNameContext;
import x10.parser.antlr.generated.X10Parser.FunctionTypeContext;
import x10.parser.antlr.generated.X10Parser.HasResultType0Context;
import x10.parser.antlr.generated.X10Parser.HasResultType1Context;
import x10.parser.antlr.generated.X10Parser.HasResultTypeContext;
import x10.parser.antlr.generated.X10Parser.HasResultTypeoptContext;
import x10.parser.antlr.generated.X10Parser.HasZeroConstraintContext;
import x10.parser.antlr.generated.X10Parser.IdentifierContext;
import x10.parser.antlr.generated.X10Parser.IdentifierListContext;
import x10.parser.antlr.generated.X10Parser.IdentifieroptContext;
import x10.parser.antlr.generated.X10Parser.IfThenStatementContext;
import x10.parser.antlr.generated.X10Parser.ImplicitConversionOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ImportDeclaration0Context;
import x10.parser.antlr.generated.X10Parser.ImportDeclaration1Context;
import x10.parser.antlr.generated.X10Parser.ImportDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ImportDeclarationsoptContext;
import x10.parser.antlr.generated.X10Parser.IntLiteralContext;
import x10.parser.antlr.generated.X10Parser.InterfaceBodyContext;
import x10.parser.antlr.generated.X10Parser.InterfaceDeclarationContext;
import x10.parser.antlr.generated.X10Parser.InterfaceMemberDeclaration0Context;
import x10.parser.antlr.generated.X10Parser.InterfaceMemberDeclaration1Context;
import x10.parser.antlr.generated.X10Parser.InterfaceMemberDeclaration2Context;
import x10.parser.antlr.generated.X10Parser.InterfaceMemberDeclaration3Context;
import x10.parser.antlr.generated.X10Parser.InterfaceMemberDeclarationContext;
import x10.parser.antlr.generated.X10Parser.InterfaceMemberDeclarationsoptContext;
import x10.parser.antlr.generated.X10Parser.InterfacesoptContext;
import x10.parser.antlr.generated.X10Parser.LabeledStatementContext;
import x10.parser.antlr.generated.X10Parser.LastExpressionContext;
import x10.parser.antlr.generated.X10Parser.LeftHandSide0Context;
import x10.parser.antlr.generated.X10Parser.LeftHandSide1Context;
import x10.parser.antlr.generated.X10Parser.LeftHandSideContext;
import x10.parser.antlr.generated.X10Parser.Literal10Context;
import x10.parser.antlr.generated.X10Parser.LiteralContext;
import x10.parser.antlr.generated.X10Parser.LocalVariableDeclaration0Context;
import x10.parser.antlr.generated.X10Parser.LocalVariableDeclaration1Context;
import x10.parser.antlr.generated.X10Parser.LocalVariableDeclaration2Context;
import x10.parser.antlr.generated.X10Parser.LocalVariableDeclarationContext;
import x10.parser.antlr.generated.X10Parser.LocalVariableDeclarationStatementContext;
import x10.parser.antlr.generated.X10Parser.LongLiteralContext;
import x10.parser.antlr.generated.X10Parser.LoopIndex0Context;
import x10.parser.antlr.generated.X10Parser.LoopIndex1Context;
import x10.parser.antlr.generated.X10Parser.LoopIndexContext;
import x10.parser.antlr.generated.X10Parser.LoopIndexDeclarator0Context;
import x10.parser.antlr.generated.X10Parser.LoopIndexDeclarator1Context;
import x10.parser.antlr.generated.X10Parser.LoopIndexDeclarator2Context;
import x10.parser.antlr.generated.X10Parser.LoopIndexDeclaratorContext;
import x10.parser.antlr.generated.X10Parser.LoopStatement0Context;
import x10.parser.antlr.generated.X10Parser.LoopStatement1Context;
import x10.parser.antlr.generated.X10Parser.LoopStatement2Context;
import x10.parser.antlr.generated.X10Parser.LoopStatement3Context;
import x10.parser.antlr.generated.X10Parser.LoopStatementContext;
import x10.parser.antlr.generated.X10Parser.MethodBody0Context;
import x10.parser.antlr.generated.X10Parser.MethodBody2Context;
import x10.parser.antlr.generated.X10Parser.MethodBody3Context;
import x10.parser.antlr.generated.X10Parser.MethodBodyContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationApplyOpContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationBinaryOpContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationConversionOpContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationMethodContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationPrefixOpContext;
import x10.parser.antlr.generated.X10Parser.MethodDeclarationSetOpContext;
import x10.parser.antlr.generated.X10Parser.MethodModifierContext;
import x10.parser.antlr.generated.X10Parser.MethodModifierModifierContext;
import x10.parser.antlr.generated.X10Parser.MethodModifierPropertyContext;
import x10.parser.antlr.generated.X10Parser.MethodModifiersoptContext;
import x10.parser.antlr.generated.X10Parser.MethodNameContext;
import x10.parser.antlr.generated.X10Parser.ModifierAbstractContext;
import x10.parser.antlr.generated.X10Parser.ModifierAnnotationContext;
import x10.parser.antlr.generated.X10Parser.ModifierAtomicContext;
import x10.parser.antlr.generated.X10Parser.ModifierClockedContext;
import x10.parser.antlr.generated.X10Parser.ModifierContext;
import x10.parser.antlr.generated.X10Parser.ModifierFinalContext;
import x10.parser.antlr.generated.X10Parser.ModifierNativeContext;
import x10.parser.antlr.generated.X10Parser.ModifierPrivateContext;
import x10.parser.antlr.generated.X10Parser.ModifierProtectedContext;
import x10.parser.antlr.generated.X10Parser.ModifierPublicContext;
import x10.parser.antlr.generated.X10Parser.ModifierStaticContext;
import x10.parser.antlr.generated.X10Parser.ModifierTransientContext;
import x10.parser.antlr.generated.X10Parser.ModifiersoptContext;
import x10.parser.antlr.generated.X10Parser.NamedTypeContext;
import x10.parser.antlr.generated.X10Parser.NamedTypeNoConstraintsContext;
import x10.parser.antlr.generated.X10Parser.NonAssignmentExpression1Context;
import x10.parser.antlr.generated.X10Parser.NonAssignmentExpression2Context;
import x10.parser.antlr.generated.X10Parser.NonAssignmentExpression3Context;
import x10.parser.antlr.generated.X10Parser.NonAssignmentExpression4Context;
import x10.parser.antlr.generated.X10Parser.NonAssignmentExpressionContext;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen0Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen10Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen11Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen13Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen14Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen15Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen16Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen17Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen18Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen19Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen1Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen20Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen21Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen22Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen2Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen3Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen4Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen5Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen6Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen7Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen8Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatemen9Context;
import x10.parser.antlr.generated.X10Parser.NonExpressionStatementContext;
import x10.parser.antlr.generated.X10Parser.NullLiteralContext;
import x10.parser.antlr.generated.X10Parser.OBSOLETE_FinishExpressionContext;
import x10.parser.antlr.generated.X10Parser.OBSOLETE_OfferStatementContext;
import x10.parser.antlr.generated.X10Parser.OBSOLETE_OffersoptContext;
import x10.parser.antlr.generated.X10Parser.OBSOLETE_TypeParamWithVariance0Context;
import x10.parser.antlr.generated.X10Parser.OBSOLETE_TypeParamWithVariance1Context;
import x10.parser.antlr.generated.X10Parser.OBSOLETE_TypeParamWithVarianceContext;
import x10.parser.antlr.generated.X10Parser.PackageDeclarationContext;
import x10.parser.antlr.generated.X10Parser.PackageNameContext;
import x10.parser.antlr.generated.X10Parser.PackageOrTypeNameContext;
import x10.parser.antlr.generated.X10Parser.PrefixOp0Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp1Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp2Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp3Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp4Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp5Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp6Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp7Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp8Context;
import x10.parser.antlr.generated.X10Parser.PrefixOp9Context;
import x10.parser.antlr.generated.X10Parser.PrefixOpContext;
import x10.parser.antlr.generated.X10Parser.PrefixOperatorDeclContext;
import x10.parser.antlr.generated.X10Parser.PrefixOperatorDeclThisContext;
import x10.parser.antlr.generated.X10Parser.PrefixOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.Primary0Context;
import x10.parser.antlr.generated.X10Parser.Primary10Context;
import x10.parser.antlr.generated.X10Parser.Primary11Context;
import x10.parser.antlr.generated.X10Parser.Primary12Context;
import x10.parser.antlr.generated.X10Parser.Primary13Context;
import x10.parser.antlr.generated.X10Parser.Primary17Context;
import x10.parser.antlr.generated.X10Parser.Primary18Context;
import x10.parser.antlr.generated.X10Parser.Primary19Context;
import x10.parser.antlr.generated.X10Parser.Primary1Context;
import x10.parser.antlr.generated.X10Parser.Primary20Context;
import x10.parser.antlr.generated.X10Parser.Primary21Context;
import x10.parser.antlr.generated.X10Parser.Primary22Context;
import x10.parser.antlr.generated.X10Parser.Primary23Context;
import x10.parser.antlr.generated.X10Parser.Primary24Context;
import x10.parser.antlr.generated.X10Parser.Primary25Context;
import x10.parser.antlr.generated.X10Parser.Primary26Context;
import x10.parser.antlr.generated.X10Parser.Primary27Context;
import x10.parser.antlr.generated.X10Parser.Primary28Context;
import x10.parser.antlr.generated.X10Parser.Primary29Context;
import x10.parser.antlr.generated.X10Parser.Primary2Context;
import x10.parser.antlr.generated.X10Parser.Primary30Context;
import x10.parser.antlr.generated.X10Parser.Primary31Context;
import x10.parser.antlr.generated.X10Parser.Primary32Context;
import x10.parser.antlr.generated.X10Parser.Primary33Context;
import x10.parser.antlr.generated.X10Parser.Primary34Context;
import x10.parser.antlr.generated.X10Parser.Primary35Context;
import x10.parser.antlr.generated.X10Parser.Primary36Context;
import x10.parser.antlr.generated.X10Parser.Primary37Context;
import x10.parser.antlr.generated.X10Parser.Primary38Context;
import x10.parser.antlr.generated.X10Parser.Primary39Context;
import x10.parser.antlr.generated.X10Parser.Primary3Context;
import x10.parser.antlr.generated.X10Parser.Primary4Context;
import x10.parser.antlr.generated.X10Parser.Primary5Context;
import x10.parser.antlr.generated.X10Parser.Primary6Context;
import x10.parser.antlr.generated.X10Parser.Primary7Context;
import x10.parser.antlr.generated.X10Parser.Primary8Context;
import x10.parser.antlr.generated.X10Parser.Primary9Context;
import x10.parser.antlr.generated.X10Parser.PrimaryContext;
import x10.parser.antlr.generated.X10Parser.PrimaryError0Context;
import x10.parser.antlr.generated.X10Parser.PrimaryError1Context;
import x10.parser.antlr.generated.X10Parser.PrimaryError2Context;
import x10.parser.antlr.generated.X10Parser.PrimaryError3Context;
import x10.parser.antlr.generated.X10Parser.PropertiesoptContext;
import x10.parser.antlr.generated.X10Parser.PropertyContext;
import x10.parser.antlr.generated.X10Parser.PropertyMethodDecl0Context;
import x10.parser.antlr.generated.X10Parser.PropertyMethodDecl1Context;
import x10.parser.antlr.generated.X10Parser.PropertyMethodDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ResultTypeContext;
import x10.parser.antlr.generated.X10Parser.ReturnStatementContext;
import x10.parser.antlr.generated.X10Parser.SetOperatorDeclarationContext;
import x10.parser.antlr.generated.X10Parser.ShortLiteralContext;
import x10.parser.antlr.generated.X10Parser.SimpleNamedType0Context;
import x10.parser.antlr.generated.X10Parser.SimpleNamedType1Context;
import x10.parser.antlr.generated.X10Parser.SimpleNamedType2Context;
import x10.parser.antlr.generated.X10Parser.SimpleNamedTypeContext;
import x10.parser.antlr.generated.X10Parser.Statement0Context;
import x10.parser.antlr.generated.X10Parser.Statement1Context;
import x10.parser.antlr.generated.X10Parser.StatementContext;
import x10.parser.antlr.generated.X10Parser.StatementExpressionListContext;
import x10.parser.antlr.generated.X10Parser.StringLiteralContext;
import x10.parser.antlr.generated.X10Parser.StructDeclarationContext;
import x10.parser.antlr.generated.X10Parser.SuperExtendsoptContext;
import x10.parser.antlr.generated.X10Parser.SwitchBlockContext;
import x10.parser.antlr.generated.X10Parser.SwitchBlockStatementGroupContext;
import x10.parser.antlr.generated.X10Parser.SwitchBlockStatementGroupsoptContext;
import x10.parser.antlr.generated.X10Parser.SwitchLabel0Context;
import x10.parser.antlr.generated.X10Parser.SwitchLabel1Context;
import x10.parser.antlr.generated.X10Parser.SwitchLabelContext;
import x10.parser.antlr.generated.X10Parser.SwitchLabelsContext;
import x10.parser.antlr.generated.X10Parser.SwitchLabelsoptContext;
import x10.parser.antlr.generated.X10Parser.SwitchStatementContext;
import x10.parser.antlr.generated.X10Parser.ThrowStatementContext;
import x10.parser.antlr.generated.X10Parser.ThrowsoptContext;
import x10.parser.antlr.generated.X10Parser.TryStatement0Context;
import x10.parser.antlr.generated.X10Parser.TryStatement1Context;
import x10.parser.antlr.generated.X10Parser.TryStatementContext;
import x10.parser.antlr.generated.X10Parser.TypeAnnotationsContext;
import x10.parser.antlr.generated.X10Parser.TypeArgumentsContext;
import x10.parser.antlr.generated.X10Parser.TypeArgumentsoptContext;
import x10.parser.antlr.generated.X10Parser.TypeConstrainedTypeContext;
import x10.parser.antlr.generated.X10Parser.TypeContext;
import x10.parser.antlr.generated.X10Parser.TypeDeclaration0Context;
import x10.parser.antlr.generated.X10Parser.TypeDeclaration1Context;
import x10.parser.antlr.generated.X10Parser.TypeDeclaration2Context;
import x10.parser.antlr.generated.X10Parser.TypeDeclaration3Context;
import x10.parser.antlr.generated.X10Parser.TypeDeclaration4Context;
import x10.parser.antlr.generated.X10Parser.TypeDeclarationContext;
import x10.parser.antlr.generated.X10Parser.TypeDeclarationsoptContext;
import x10.parser.antlr.generated.X10Parser.TypeDefDeclarationContext;
import x10.parser.antlr.generated.X10Parser.TypeFunctionTypeContext;
import x10.parser.antlr.generated.X10Parser.TypeNameContext;
import x10.parser.antlr.generated.X10Parser.TypeParamWithVarianceList0Context;
import x10.parser.antlr.generated.X10Parser.TypeParamWithVarianceList1Context;
import x10.parser.antlr.generated.X10Parser.TypeParamWithVarianceList2Context;
import x10.parser.antlr.generated.X10Parser.TypeParamWithVarianceList3Context;
import x10.parser.antlr.generated.X10Parser.TypeParamWithVarianceListContext;
import x10.parser.antlr.generated.X10Parser.TypeParameterContext;
import x10.parser.antlr.generated.X10Parser.TypeParameterListContext;
import x10.parser.antlr.generated.X10Parser.TypeParametersoptContext;
import x10.parser.antlr.generated.X10Parser.TypeParamsWithVarianceoptContext;
import x10.parser.antlr.generated.X10Parser.TypeVoidContext;
import x10.parser.antlr.generated.X10Parser.UnsignedByteLiteralContext;
import x10.parser.antlr.generated.X10Parser.UnsignedIntLiteralContext;
import x10.parser.antlr.generated.X10Parser.UnsignedLongLiteralContext;
import x10.parser.antlr.generated.X10Parser.UnsignedShortLiteralContext;
import x10.parser.antlr.generated.X10Parser.VarKeyword0Context;
import x10.parser.antlr.generated.X10Parser.VarKeyword1Context;
import x10.parser.antlr.generated.X10Parser.VarKeywordContext;
import x10.parser.antlr.generated.X10Parser.VariableDeclarator0Context;
import x10.parser.antlr.generated.X10Parser.VariableDeclarator1Context;
import x10.parser.antlr.generated.X10Parser.VariableDeclarator2Context;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorContext;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorWithType0Context;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorWithType1Context;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorWithType2Context;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorWithTypeContext;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorsContext;
import x10.parser.antlr.generated.X10Parser.VariableDeclaratorsWithTypeContext;
import x10.parser.antlr.generated.X10Parser.VariableInitializerContext;
import x10.parser.antlr.generated.X10Parser.WhenStatementContext;
import x10.parser.antlr.generated.X10Parser.WhereClauseoptContext;
import x10.parser.antlr.generated.X10Parser.WhileStatementContext;
import x10.types.ParameterType;
import x10.types.checker.Converter;

/**
 * This class provides the {@code parse} method that parses an X10 file and
 * builds the corresponding AST.
 *
 * @author Louis Mandel
 *
 */
public class ASTBuilder extends X10BaseListener implements X10Listener, polyglot.frontend.Parser {

    protected X10Parser p;
    protected X10Lexer lexer;
    protected CommonTokenStream tokens;

    protected X10CompilerOptions compilerOpts;
    protected ErrorQueue eq;
    protected ParserErrorListener err;
    protected DefaultErrorStrategy errorStrategy;
    protected TypeSystem ts;
    protected NodeFactory nf;
    protected FileSource srce;
    protected String fileName;

    public static void clearState() {
        ParserCleaner.clearDFA();
    }

    public ASTBuilder(ANTLRInputStream inputStream, X10CompilerOptions opts, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q) {
        compilerOpts = opts;
        ts = t;
        nf = n;
        srce = source;
        eq = q;
        fileName = source.toString();

        lexer = new X10Lexer(inputStream);
        tokens = new CommonTokenStream(lexer);

        p = new X10Parser(tokens);

        /* Use new caches */
        lexer.setInterpreter(new LexerATNSimulator(lexer, lexer.getATN(), lexer.getInterpreter().decisionToDFA, new PredictionContextCache()));
        p.setInterpreter(new ParserATNSimulator(p, p.getATN(), p.getInterpreter().decisionToDFA, new PredictionContextCache()));

        p.removeErrorListeners();
        err = new ParserErrorListener(eq, fileName);
        p.addErrorListener(err);

        errorStrategy = new DefaultErrorStrategy();
        p.setErrorHandler(errorStrategy);
    }

    public CommonTokenStream getTokens() {
        return tokens;
    }

    private CompilationUnitContext getParseTree() {
        // Two stage parsing
        CompilationUnitContext tree = null;
        p.getInterpreter().setPredictionMode(PredictionMode.SLL);
        p.removeErrorListeners();
        p.setErrorHandler(new BailErrorStrategy());
        try {
            // First stage
            tree = p.compilationUnit();
        } catch (Exception ex) {
            // Second stage
            tokens.reset();
            p.reset();
            p.addErrorListener(err);
            p.setErrorHandler(errorStrategy);
            p.getInterpreter().setPredictionMode(PredictionMode.LL);
            tree = p.compilationUnit();
        }
        return tree;
    }

    @Override
    public Node parse() {
        CompilationUnitContext tree = getParseTree();

        if (compilerOpts.x10_config.DISPLAY_PARSE_TREE) {
            Future<JDialog> dialogHdl = tree.inspect(p);
            try {
                JDialog dialog = dialogHdl.get();
                dialog.setTitle(srce.toString());
                Utils.waitForClose(dialog);
            } catch (Exception e) {
                eq.enqueue(ErrorInfo.WARNING, srce + ": unable to display the parse tree.");
            }
        }
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        SourceFile sf = tree.ast;
        return sf.source(srce);
    }

    // Utility functions

    /** Returns the position of a given parse tree node. */
    protected Position pos(ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        int offset = ctx.getStart().getStartIndex();
        int endLine = ctx.getStop() == null ? ctx.getStart().getLine() : ctx.getStop().getLine();
        int endColumn = ctx.getStop() == null ? ctx.getStart().getCharPositionInLine() : ctx.getStop().getCharPositionInLine();
        int endOffset = ctx.getStop() == null ? ctx.getStart().getStopIndex() : ctx.getStop().getStopIndex();
        return new Position("", fileName, line, column, endLine, endColumn, offset, endOffset);
    }

    /** Returns the position of a given token. */
    private Position pos(Token t) {
        int line = t.getLine();
        int column = t.getCharPositionInLine();
        int offset = t.getStartIndex();
        int endLine = line;
        int endOffset = t.getStopIndex();
        int endColumn = column + endOffset - offset;
        return new Position("", fileName, line, column, endLine, endColumn, offset, endOffset);
    }

    /** Returns the position going from {@code ctx} to {@code t}. */
    protected Position pos(ParserRuleContext ctx, Token t) {
        Position p1 = pos(ctx);
        Position p2 = pos(t);
        return new Position(p1, p2);
    }

    /** Returns the position going from {@code n} to {@code ctx} */
    protected Position pos(ParsedName n, ParserRuleContext ctx) {
        Position p1 = n.pos;
        Position p2 = pos(ctx);
        return new Position(p1, p2);
    }

    private String comment(ParserRuleContext ctx) {
        String s = null;
        int i = ctx.getStart().getTokenIndex();
        List<Token> cmtChannel = tokens.getHiddenTokensToLeft(i, X10Lexer.DOCCOMMENTS);
        if (cmtChannel != null) {
            Token cmt = cmtChannel.get(0);
            if (cmt != null) {
                s = cmt.getText();
            }
        }
        return s;
    }

    private Node setComment(Node d, ParserRuleContext ctx) {
        String c = comment(ctx);
        if (c == null) {
            return d;
        }
        return ((X10Ext) d.ext()).setComment(c);
    }

    private void checkTypeName(Id identifier) {
        String filename = srce.name();
        String idname = identifier.id().toString();
        int dot = filename.lastIndexOf('.'), slash = filename.lastIndexOf('/', dot);
        if (slash == -1)
            slash = filename.lastIndexOf('\\', dot);
        String clean_filename = (slash >= 0 && dot >= 0 ? filename.substring(slash + 1, dot) : "");
        if ((!clean_filename.equals(idname)) && clean_filename.equalsIgnoreCase(idname))
            err.syntaxError("This type name does not match the name of the containing file: " + filename.substring(slash + 1), identifier.position());
    }


    /** Build a parsed name from a list of identifiers. */
    protected ParsedName toParsedName(List<IdentifierContext> identifiers) {
        Iterator<IdentifierContext> iter = identifiers.iterator();
        assert (iter.hasNext());
        IdentifierContext ident = iter.next();
        ParsedName name = new ParsedName(nf, ts, pos(ident), ast(ident));
        while (iter.hasNext()) {
            ident = iter.next();
            name = new ParsedName(nf, ts, pos(name, ident), name, ast(ident));
        }
        return name;
    }


    // Build dummy node in case of errors

    private Id errorId(Position p) {
        return (Id) nf.Id(p, "*").error(true);

    }

    private FlagsNode errorFlags(Position p) {
        return nf.FlagsNode(p, Flags.NONE);
    }

    private ParsedName errorParsedName(Position p) {
        return new ParsedName(nf, ts, p, errorId(p));
    }

    private Expr errorExpr(Position p) {
        return (Expr) nf.NullLit(p).error(true);
    }

    private Stmt errorStmt(Position p) {
        return (Stmt) nf.Empty(p).error(true);
    }

    private Block errorBlock(Position p) {
        return (Block) nf.Block(p).error(true);
    }

    private X10Formal errorFormal(Position p) {
        return (X10Formal) nf.Formal(p, errorFlags(p), errorTypeNode(p), errorId(p)).error(true);
    }

    private AmbTypeNode errorAmbTypeNode(Position p) {
        return (AmbTypeNode) nf.AmbTypeNode(p, nf.Id(p, "Any")).error(true);
    }

    private TypeNode errorTypeNode(Position p) {
        return errorAmbTypeNode(p);
    }

    private TypeDecl errorTypeDecl(Position p) {
        return (TypeDecl) nf.TypeDecl(p, errorFlags(p), errorId(p), null, null, null, null).error(true);
    }

    private TypeParamNode errorTypeParamNode(Position p) {
        return (TypeParamNode) nf.TypeParamNode(p, errorId(p)).error(true);
    }

    private ClassDecl errorClassDecl(Position p) {
        return (ClassDecl) nf.ClassDecl(p, errorFlags(p), errorId(p), errorTypeNode(p), new ArrayList<TypeNode>(), errorClassBody(p)).error(true);
    }

    private ClassBody errorClassBody(Position p) {
        return (ClassBody) nf.ClassBody(p, new ArrayList<ClassMember>()).error(true);
    }

    private DepParameterExpr errorDepParameterExpr(Position p) {
        return (DepParameterExpr) nf.DepParameterExpr(p, new ArrayList<Expr>());
    }

    private MethodDecl errorMethodDecl(Position p) {
        return (MethodDecl) nf.MethodDecl(p, errorFlags(p), errorTypeNode(p), errorId(p), new ArrayList<Formal>(), errorBlock(p)).error(true);
    }


    // Access to the ast field

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Modifier>} is returned.
     */
    private final List<Modifier> ast(ModifiersoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Modifier> l = new TypedList<Modifier>(new LinkedList<Modifier>(), Modifier.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Modifier} is returned.
     */
    private final Modifier ast(ModifierContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Modifier n = new FlagModifier(pos(ctx), FlagModifier.ABSTRACT);
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Modifier>} is returned.
     */
    private final List<Modifier> ast(MethodModifiersoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Modifier> l = new TypedList<Modifier>(new LinkedList<Modifier>(), Modifier.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Modifier} is returned.
     */
    private final Modifier ast(MethodModifierContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Modifier n = new FlagModifier(pos(ctx), FlagModifier.ABSTRACT);
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeDecl} is returned.
     */
    private final TypeDecl ast(TypeDefDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<PropertyDecl>} is returned.
     */
    private final List<PropertyDecl> ast(PropertiesoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code PropertyDecl} is returned.
     */
    private final PropertyDecl ast(PropertyContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            PropertyDecl n = nf.PropertyDecl(p, errorFlags(p), errorTypeNode(p), errorId(p));
            return (PropertyDecl) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ProcedureDecl} is returned.
     */
    private final ProcedureDecl ast(MethodDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(BinaryOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(PrefixOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(ApplyOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(SetOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(ConversionOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(ExplicitConversionOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(ImplicitConversionOperatorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code MethodDecl} is returned.
     */
    private final MethodDecl ast(PropertyMethodDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorMethodDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ConstructorCall} is returned.
     */
    private final ConstructorCall ast(ExplicitConstructorInvocationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            ConstructorCall n = nf.ConstructorCall(p, ConstructorCall.THIS, new ArrayList<Expr>());
            return (ConstructorCall) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ClassDecl} is returned.
     */
    private final ClassDecl ast(InterfaceDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorClassDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Stmt} is returned.
     */
    private final Stmt ast(AssignPropertyCallContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Stmt n = errorStmt(p);
            return (Stmt) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(TypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(FunctionTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(ClassTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code AmbTypeNode} is returned.
     */
    private final AmbTypeNode ast(SimpleNamedTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorAmbTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(NamedTypeNoConstraintsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(NamedTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code DepParameterExpr} is returned.
     */
    private final DepParameterExpr ast(DepParametersContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorDepParameterExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeParamNode>} is returned.
     */
    private final List<TypeParamNode> ast(TypeParamsWithVarianceoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeParamNode>} is returned.
     */
    private final List<TypeParamNode> ast(TypeParametersoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Formal>} is returned.
     */
    private final List<Formal> ast(FormalParametersContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Expr>} is returned.
     */
    private final List<Expr> ast(ConstraintConjunctionoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code HasZeroTest} is returned.
     */
    private final HasZeroTest ast(HasZeroConstraintContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            HasZeroTest n = nf.HasZeroTest(p, errorTypeNode(p));
            return (HasZeroTest) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code DepParameterExpr} is returned ({@code ctx.ast} can be null).
     */
    private final DepParameterExpr ast(WhereClauseoptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorDepParameterExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ClassDecl} is returned.
     */
    private final ClassDecl ast(ClassDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorClassDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ClassDecl} is returned.
     */
    private final ClassDecl ast(StructDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorClassDecl(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ConstructorDecl} is returned.
     */
    private final ConstructorDecl ast(ConstructorDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            ConstructorDecl n = nf.ConstructorDecl(p, errorFlags(p), errorId(p), new ArrayList<Formal>(), errorBlock(p));
            return (ConstructorDecl) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code TypeNode} is returned ({@code ctx.ast} can be null).
     */
    private final TypeNode ast(SuperExtendsoptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code List<FlagsNode>} is returned ({@code ctx.ast} can be null).
     */
    private final List<FlagsNode> ast(VarKeywordContext ctx) {
        if (ctx == null) {
            List<FlagsNode> l = new TypedList<FlagsNode>(new LinkedList<FlagsNode>(), FlagsNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<ClassMember>} is returned.
     */
    private final List<ClassMember> ast(FieldDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Stmt} is returned.
     */
    private final Stmt ast(StatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Stmt n = errorStmt(p);
            return (Stmt) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Stmt} is returned.
     */
    private final Stmt ast(AnnotationStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Stmt n = errorStmt(p);
            return (Stmt) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Stmt} is returned.
     */
    private final Stmt ast(NonExpressionStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Stmt n = errorStmt(p);
            return (Stmt) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Offer} is returned.
     */
    private final Offer ast(OBSOLETE_OfferStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Offer n = nf.Offer(p, errorExpr(p));
            return (Offer) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code If} is returned.
     */
    private final If ast(IfThenStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            If n = nf.If(p, errorExpr(p), errorStmt(p));
            return (If) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Empty} is returned.
     */
    private final Empty ast(EmptyStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Empty n = nf.Empty(p);
            return (Empty) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Labeled} is returned.
     */
    private final Labeled ast(LabeledStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Labeled n = nf.Labeled(p, errorId(p), errorStmt(p));
            return (Labeled) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Stmt} is returned.
     */
    private final Stmt ast(LoopStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Stmt n = errorStmt(p);
            return (Stmt) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Eval} is returned.
     */
    private final Eval ast(ExpressionStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Eval n = nf.Eval(p, errorExpr(p));
            return (Eval) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Assert} is returned.
     */
    private final Assert ast(AssertStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Assert n = nf.Assert(p, errorExpr(p));
            return (Assert) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Switch} is returned.
     */
    private final Switch ast(SwitchStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Switch n = nf.Switch(p, errorExpr(p), new ArrayList<SwitchElement>());
            return (Switch) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<SwitchElement>} is returned.
     */
    private final List<SwitchElement> ast(SwitchBlockContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<SwitchElement>} is returned.
     */
    private final List<SwitchElement> ast(SwitchBlockStatementGroupsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<SwitchElement>} is returned.
     */
    private final List<SwitchElement> ast(SwitchBlockStatementGroupContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Case>} is returned.
     */
    private final List<Case> ast(SwitchLabelsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Case>} is returned.
     */
    private final List<Case> ast(SwitchLabelsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Case} is returned.
     */
    private final Case ast(SwitchLabelContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Case n = nf.Case(p, errorExpr(p));
            return (Case) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code While} is returned.
     */
    private final While ast(WhileStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            While n = nf.While(p, errorExpr(p), errorStmt(p));
            return (While) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Do} is returned.
     */
    private final Do ast(DoStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Do n = nf.Do(p, errorStmt(p), errorExpr(p));
            return (Do) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Loop} is returned.
     */
    private final Loop ast(ForStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Loop n = nf.Do(p, errorStmt(p), errorExpr(p));
            return (Loop) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code For} is returned.
     */
    private final For ast(BasicForStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            For n = nf.For(p, new ArrayList<ForInit>(), errorExpr(p), new ArrayList<ForUpdate>(), errorStmt(p));
            return (For) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<? extends ForInit>} is returned.
     */
    private final List<? extends ForInit> ast(ForInitContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<? extends ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<? extends ForUpdate>} is returned.
     */
    private final List<? extends ForUpdate> ast(ForUpdateContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<? extends ForUpdate> l = new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<? extends Eval>} is returned.
     */
    private final List<? extends Eval> ast(StatementExpressionListContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<? extends Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Branch} is returned.
     */
    private final Branch ast(BreakStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Branch n = nf.Break(p);
            return (Branch) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Branch} is returned.
     */
    private final Branch ast(ContinueStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Branch n = nf.Continue(p);
            return (Branch) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Return} is returned.
     */
    private final Return ast(ReturnStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Return n = nf.Return(p);
            return (Return) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Throw} is returned.
     */
    private final Throw ast(ThrowStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Throw n = nf.Throw(p, errorExpr(p));
            return (Throw) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Try} is returned.
     */
    private final Try ast(TryStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Try n = nf.Try(p, errorBlock(p), new ArrayList<Catch>());
            return (Try) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Catch>} is returned.
     */
    private final List<Catch> ast(CatchesContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Catch} is returned.
     */
    private final Catch ast(CatchClauseContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Catch n = nf.Catch(p, errorFormal(p), errorBlock(p));
            return (Catch) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Block} is returned.
     */
    private final Block ast(FinallyBlockContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorBlock(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Expr>} is returned.
     */
    private final List<Expr> ast(ClockedClauseoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Async} is returned.
     */
    private final Async ast(AsyncStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Async n = nf.Async(p, errorStmt(p), false);
            return (Async) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code AtStmt} is returned.
     */
    private final AtStmt ast(AtStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            AtStmt n = nf.AtStmt(p, errorExpr(p), errorStmt(p));
            return (AtStmt) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Atomic} is returned.
     */
    private final Atomic ast(AtomicStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Atomic n = nf.Atomic(p, errorExpr(p), errorStmt(p));
            return (Atomic) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code When} is returned.
     */
    private final When ast(WhenStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            When n = nf.When(p, errorExpr(p), errorStmt(p));
            return (When) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code X10Loop} is returned.
     */
    private final X10Loop ast(AtEachStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            X10Loop n = nf.AtEach(p, errorFormal(p), errorExpr(p), errorStmt(p));
            return (X10Loop) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code X10Loop} is returned.
     */
    private final X10Loop ast(EnhancedForStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            X10Loop n = nf.ForLoop(p, errorFormal(p), errorExpr(p), errorStmt(p));
            return (X10Loop) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Finish} is returned.
     */
    private final Finish ast(FinishStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Finish n = nf.Finish(p, errorStmt(p), false);
            return (Finish) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(CastExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeParamNode>} is returned.
     */
    private final List<TypeParamNode> ast(TypeParamWithVarianceListContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeParamNode>} is returned.
     */
    private final List<TypeParamNode> ast(TypeParameterListContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeParamNode} is returned.
     */
    private final TypeParamNode ast(OBSOLETE_TypeParamWithVarianceContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeParamNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeParamNode} is returned.
     */
    private final TypeParamNode ast(TypeParameterContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeParamNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Closure} is returned.
     */
    private final Closure ast(ClosureExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Closure n = nf.Closure(p, new ArrayList<Formal>(), errorDepParameterExpr(p), errorTypeNode(p), errorBlock(p));
            return (Closure) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Return} is returned.
     */
    private final Return ast(LastExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Return n = nf.Return(p);
            return (Return) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Block} is returned.
     */
    private final Block ast(ClosureBodyContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorBlock(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code AtExpr} is returned.
     */
    private final AtExpr ast(AtExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            AtExpr n = nf.AtExpr(p, errorExpr(p), errorBlock(p));
            return (AtExpr) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code FinishExpr} is returned.
     */
    private final FinishExpr ast(OBSOLETE_FinishExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            FinishExpr n = nf.FinishExpr(p, errorExpr(p), errorStmt(p));
            return (FinishExpr) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(TypeNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(ClassNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeNode>} is returned.
     */
    private final List<TypeNode> ast(TypeArgumentsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(PackageNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(ExpressionNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(MethodNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(PackageOrTypeNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ParsedName} is returned.
     */
    private final ParsedName ast(FullyQualifiedNameContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorParsedName(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code SourceFile} is returned.
     */
    private final SourceFile ast(CompilationUnitContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            SourceFile n = nf.SourceFile(p, new ArrayList<TopLevelDecl>());
            return (SourceFile) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code PackageNode} is returned.
     */
    private final PackageNode ast(PackageDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            PackageNode n = nf.PackageNode(p, null);
            return (PackageNode) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Import>} is returned.
     */
    private final List<Import> ast(ImportDeclarationsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Import} is returned.
     */
    private final Import ast(ImportDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Import n = nf.Import(p, Import.CLASS, QName.make("*"));
            return (Import) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TopLevelDecl>} is returned.
     */
    private final List<TopLevelDecl> ast(TypeDeclarationsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code TopLevelDecl} is returned ({@code ctx.ast} can be null).
     */
    private final TopLevelDecl ast(TypeDeclarationContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            TopLevelDecl n = errorTypeDecl(p);
            return (TopLevelDecl) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeNode>} is returned.
     */
    private final List<TypeNode> ast(InterfacesoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ClassBody} is returned.
     */
    private final ClassBody ast(ClassBodyContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            ClassBody n = nf.ClassBody(p, new ArrayList<ClassMember>());
            return (ClassBody) n.error(true);
        }
        return ctx.ast;
    }


    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<ClassMember>} is returned.
     */
    private final List<ClassMember> ast(ClassMemberDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Object[]>} is returned.
     */
    private final List<Object[]> ast(FormalDeclaratorsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Object[]>} is returned.
     */
    private final List<Object[]> ast(FieldDeclaratorsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Object[]>} is returned.
     */
    private final List<Object[]> ast(VariableDeclaratorsWithTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Object[]>} is returned.
     */
    private final List<Object[]> ast(VariableDeclaratorsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(VariableInitializerContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(ResultTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code TypeNode} is returned.
     */
    private final TypeNode ast(HasResultTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Formal>} is returned.
     */
    private final List<Formal> ast(FormalParameterListContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Object[]} is returned.
     */
    private final Object[] ast(LoopIndexDeclaratorContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Object[] n = new Object[] { p, null, Collections.<Id> emptyList(), null, null, null };
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code X10Formal} is returned.
     */
    private final X10Formal ast(LoopIndexContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorFormal(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code X10Formal} is returned.
     */
    private final X10Formal ast(FormalParameterContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorFormal(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code TypeNode} is returned ({@code ctx.ast} can be null).
     */
    private final TypeNode ast(OBSOLETE_OffersoptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeNode>} is returned.
     */
    private final List<TypeNode> ast(ThrowsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code Block} is returned ({@code ctx.ast} can be null).
     */
    private final Block ast(MethodBodyContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorBlock(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code Block} is returned ({@code ctx.ast} can be null).
     */
    private final Block ast(ConstructorBodyContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorBlock(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Block} is returned.
     */
    private final Block ast(ConstructorBlockContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorBlock(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Expr>} is returned.
     */
    private final List<Expr> ast(ArgumentsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeNode>} is returned.
     */
    private final List<TypeNode> ast(ExtendsInterfacesoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code ClassBody} is returned.
     */
    private final ClassBody ast(InterfaceBodyContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorClassBody(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<ClassMember>} is returned.
     */
    private final List<ClassMember> ast(InterfaceMemberDeclarationsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<ClassMember>} is returned.
     */
    private final List<ClassMember> ast(InterfaceMemberDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<AnnotationNode>} is returned.
     */
    private final List<AnnotationNode> ast(AnnotationsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<AnnotationNode>} is returned.
     */
    private final List<AnnotationNode> ast(AnnotationsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code AnnotationNode} is returned.
     */
    private final AnnotationNode ast(AnnotationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            AnnotationNode n = nf.AnnotationNode(p, errorTypeNode(p));
            return (AnnotationNode) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Id} is returned.
     */
    private final Id ast(IdentifierContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorId(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Block} is returned.
     */
    private final Block ast(BlockContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorBlock(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Stmt>} is returned.
     */
    private final List<Stmt> ast(BlockStatementsContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Stmt>} is returned.
     */
    private final List<Stmt> ast(BlockInteriorStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Id>} is returned.
     */
    private final List<Id> ast(IdentifierListContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Object[]} is returned.
     */
    private final Object[] ast(FormalDeclaratorContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Object[] n = new Object[] { p, null, Collections.<Id> emptyList(), null, errorTypeNode(p), null };
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Object[]} is returned.
     */
    private final Object[] ast(FieldDeclaratorContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Object[] n = new Object[] { p, null, Collections.<Id> emptyList(), null, errorExpr(p) };
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Object[]} is returned.
     */
    private final Object[] ast(VariableDeclaratorContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Object[] n = new Object[] { p, null, Collections.<Id> emptyList(), null, errorTypeNode(p), errorExpr(p) };
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Object[]} is returned.
     */
    private final Object[] ast(VariableDeclaratorWithTypeContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Object[] n = new Object[] { p, null, Collections.<Id> emptyList(), null, errorTypeNode(p), errorExpr(p) };
            return n;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Stmt>} is returned.
     */
    private final List<Stmt> ast(LocalVariableDeclarationStatementContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<LocalDecl>} is returned.
     */
    private final List<LocalDecl> ast(LocalVariableDeclarationContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(PrimaryContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Lit} is returned.
     */
    private final Lit ast(LiteralContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Lit n = nf.NullLit(p);
            return (Lit) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code BooleanLit} is returned.
     */
    private final BooleanLit ast(BooleanLiteralContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            BooleanLit n = nf.BooleanLit(p, false);
            return (BooleanLit) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Expr>} is returned.
     */
    private final List<Expr> ast(ArgumentListContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Field} is returned.
     */
    private final Field ast(FieldAccessContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            Field n = nf.Field(p, null, errorId(p));
            return (Field) n.error(true);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(ConditionalExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(NonAssignmentExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(AssignmentExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(AssignmentContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(LeftHandSideContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Assign} is returned.
     */
    private final Assign.Operator ast(AssignmentOperatorContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return Assign.ASSIGN;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(ExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Expr} is returned.
     */
    private final Expr ast(ConstantExpressionContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Unary} is returned.
     */
    private final Unary.Operator ast(PrefixOpContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return Unary.POS;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code Binary} is returned.
     */
    private final Binary.Operator ast(BinOpContext ctx) {
        if (ctx == null || ctx.ast == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return Binary.EQ;
        }
        return ctx.ast;
    }


    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code TypeNode} is returned ({@code ctx.ast} can be null).
     */
    private final TypeNode ast(HasResultTypeoptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorTypeNode(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<TypeNode>} is returned.
     */
    private final List<TypeNode> ast(TypeArgumentsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Expr>} is returned.
     */
    private final List<Expr> ast(ArgumentListoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Expr>} is returned.
     */
    private final List<Expr> ast(ArgumentsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code Id} is returned ({@code ctx.ast} can be null).
     */
    private final Id ast(IdentifieroptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorId(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<? extends ForInit>} is returned.
     */
    private final List<? extends ForInit> ast(ForInitoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<? extends ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<? extends ForUpdate>} is returned.
     */
    private final List<? extends ForUpdate> ast(ForUpdateoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<? extends ForUpdate> l = new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code Expr} is returned ({@code ctx.ast} can be null).
     */
    private final Expr ast(ExpressionoptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorExpr(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Catch>} is returned.
     */
    private final List<Catch> ast(CatchesoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Stmt>} is returned.
     */
    private final List<Stmt> ast(BlockStatementsoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
            return l;
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} is null, a dummy value of type {@code ClassBody} is returned ({@code ctx.ast} can be null).
     */
    private final ClassBody ast(ClassBodyoptContext ctx) {
        if (ctx == null) {
            Position p = Position.COMPILER_GENERATED; // (ctx == null) ? Position.COMPILER_GENERATED : pos(ctx);
            return errorClassBody(p);
        }
        return ctx.ast;
    }

    /**
     * Return the {@code ast} field of {@code ctx}. If {@code ctx} or {@code ctx.ast} is null, a dummy value of type {@code List<Formal>} is returned.
     */
    private final List<Formal> ast(FormalParameterListoptContext ctx) {
        if (ctx == null || ctx.ast == null) {
            List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
            return l;
        }
        return ctx.ast;
    }




    // Temporary classes used to wrap modifiers.

    public static class Modifier {
    }

    public static class FlagModifier extends Modifier {
        public static int ABSTRACT = 0;
        public static int ATOMIC = 1;
        // public static int EXTERN = 2;
        public static int FINAL = 3;
        // public static int GLOBAL = 4;
        // public static int INCOMPLETE = 5;
        public static int NATIVE = 6;
        // public static int NON_BLOCKING = 7;
        public static int PRIVATE = 8;
        public static int PROPERTY = 9;
        public static int PROTECTED = 10;
        public static int PUBLIC = 11;
        // public static int SAFE = 12;
        // public static int SEQUENTIAL = 13;
        public static int CLOCKED = 14;
        public static int STATIC = 15;
        public static int TRANSIENT = 16;
        public static int NUM_FLAGS = TRANSIENT + 1;

        private Position pos;
        private int flag;

        public Position position() {
            return pos;
        }

        public int flag() {
            return flag;
        }

        public Flags flags() {
            if (flag == ABSTRACT)
                return Flags.ABSTRACT;
            if (flag == ATOMIC)
                return Flags.ATOMIC;
            // if (flag == EXTERN) return X10Flags.EXTERN;
            if (flag == FINAL)
                return Flags.FINAL;
            // if (flag == GLOBAL) return X10Flags.GLOBAL;
            // if (flag == INCOMPLETE) return X10Flags.INCOMPLETE;
            if (flag == NATIVE)
                return Flags.NATIVE;
            // if (flag == NON_BLOCKING) return X10Flags.NON_BLOCKING;
            if (flag == PRIVATE)
                return Flags.PRIVATE;
            if (flag == PROPERTY)
                return Flags.PROPERTY;
            if (flag == PROTECTED)
                return Flags.PROTECTED;
            if (flag == PUBLIC)
                return Flags.PUBLIC;
            // if (flag == SAFE) return X10Flags.SAFE;
            // if (flag == SEQUENTIAL) return X10Flags.SEQUENTIAL;
            if (flag == CLOCKED)
                return Flags.CLOCKED;
            if (flag == TRANSIENT)
                return Flags.TRANSIENT;
            if (flag == STATIC)
                return Flags.STATIC;
            assert (false);
            return null;
        }

        public String name() {
            if (flag == ABSTRACT)
                return "abstract";
            if (flag == ATOMIC)
                return "atomic";
            // if (flag == EXTERN) return "extern";
            if (flag == FINAL)
                return "final";
            // if (flag == GLOBAL) return "global";
            // if (flag == INCOMPLETE) return "incomplete";
            if (flag == NATIVE)
                return "native";
            // if (flag == NON_BLOCKING) return "nonblocking";
            if (flag == PRIVATE)
                return "private";
            if (flag == PROPERTY)
                return "property";
            if (flag == PROTECTED)
                return "protected";
            if (flag == PUBLIC)
                return "public";
            // if (flag == SAFE) return "safe";
            // if (flag == SEQUENTIAL) return "sequential";
            if (flag == CLOCKED)
                return "clocked";
            if (flag == STATIC)
                return "static";
            if (flag == TRANSIENT)
                return "transient";
            assert (false);
            return "?";
        }

        public static boolean classModifiers[] = new boolean[NUM_FLAGS];
        static {
            classModifiers[ABSTRACT] = true;
            classModifiers[FINAL] = true;
            classModifiers[PRIVATE] = true;
            classModifiers[PROTECTED] = true;
            classModifiers[PUBLIC] = true;
            // classModifiers[SAFE] = true;
            classModifiers[STATIC] = true;
            classModifiers[CLOCKED] = true;
            // classModifiers[GLOBAL] = true;
        }

        public boolean isClassModifier(int flag) {
            return classModifiers[flag];
        }

        public static boolean typeDefModifiers[] = new boolean[NUM_FLAGS];
        static {
            typeDefModifiers[ABSTRACT] = true;
            typeDefModifiers[FINAL] = true;
            typeDefModifiers[PRIVATE] = true;
            typeDefModifiers[PROTECTED] = true;
            typeDefModifiers[PUBLIC] = true;
            typeDefModifiers[STATIC] = true;
        }

        public boolean isTypeDefModifier(int flag) {
            return typeDefModifiers[flag];
        }

        public static boolean fieldModifiers[] = new boolean[NUM_FLAGS];
        static {
            fieldModifiers[TRANSIENT] = true;
            // fieldModifiers[GLOBAL] = true;
            fieldModifiers[CLOCKED] = true;
            fieldModifiers[PRIVATE] = true;
            fieldModifiers[PROTECTED] = true;
            fieldModifiers[PROPERTY] = true;
            fieldModifiers[PUBLIC] = true;
            fieldModifiers[STATIC] = true;
        }

        public boolean isFieldModifier(int flag) {
            return fieldModifiers[flag];
        }

        public static boolean variableModifiers[] = new boolean[NUM_FLAGS];
        static {
            variableModifiers[CLOCKED] = true;
        }

        public boolean isVariableModifier(int flag) {
            return variableModifiers[flag];
        }

        public static boolean methodModifiers[] = new boolean[NUM_FLAGS];
        static {
            methodModifiers[ABSTRACT] = true;
            methodModifiers[ATOMIC] = true;
            // methodModifiers[EXTERN] = true;
            methodModifiers[FINAL] = true;
            // methodModifiers[GLOBAL] = true;
            // methodModifiers[INCOMPLETE] = true;
            methodModifiers[NATIVE] = true;
            // methodModifiers[NON_BLOCKING] = true;
            methodModifiers[PRIVATE] = true;
            methodModifiers[PROPERTY] = true;
            methodModifiers[PROTECTED] = true;
            methodModifiers[PUBLIC] = true;
            // methodModifiers[SAFE] = true;
            // methodModifiers[SEQUENTIAL] = true;
            methodModifiers[STATIC] = true;
            // methodModifiers[CLOCKED] = true;
        }

        public boolean isMethodModifier(int flag) {
            return methodModifiers[flag];
        }

        public static boolean constructorModifiers[] = new boolean[NUM_FLAGS];
        static {
            constructorModifiers[NATIVE] = true;
            constructorModifiers[PRIVATE] = true;
            constructorModifiers[PROTECTED] = true;
            constructorModifiers[PUBLIC] = true;
        }

        public boolean isConstructorModifier(int flag) {
            return constructorModifiers[flag];
        }

        public static boolean interfaceModifiers[] = new boolean[NUM_FLAGS];
        static {
            interfaceModifiers[ABSTRACT] = true;
            interfaceModifiers[PRIVATE] = true;
            interfaceModifiers[PROTECTED] = true;
            interfaceModifiers[PUBLIC] = true;
            interfaceModifiers[STATIC] = true;
            interfaceModifiers[CLOCKED] = true;

        }

        public boolean isInterfaceModifier(int flag) {
            return interfaceModifiers[flag];
        }

        public FlagModifier(Position pos, int flag) {
            this.pos = pos;
            this.flag = flag;
        }
    }

    private static class AnnotationModifier extends Modifier {
        private AnnotationNode annotation;

        public AnnotationNode annotation() {
            return annotation;
        }

        public AnnotationModifier(AnnotationNode annotation) {
            this.annotation = annotation;
        }
    }

    //
    // TODO: Say something!
    //
    private List<Node> checkModifiers(String kind, List<Modifier> modifiers, boolean legal_flags[]) {
        List<Node> l = new LinkedList<Node>();

        assert (modifiers.size() > 0);

        boolean flags[] = new boolean[FlagModifier.NUM_FLAGS]; // initialized to
                                                               // false
        for (int i = 0; i < modifiers.size(); i++) {
            Object element = modifiers.get(i);
            if (element instanceof FlagModifier) {
                FlagModifier modifier = (FlagModifier) element;
                l.addAll(Collections.singletonList(nf.FlagsNode(modifier.position(), modifier.flags())));

                if (!flags[modifier.flag()]) {
                    flags[modifier.flag()] = true;
                } else {
                    err.syntaxError("Duplicate specification of modifier: " + modifier.name(), modifier.position());
                }

                if (!legal_flags[modifier.flag()]) {
                    err.syntaxError("\"" + modifier.name() + "\" is not a valid " + kind + " modifier", modifier.position());
                }
            } else {
                AnnotationModifier modifier = (AnnotationModifier) element;
                l.addAll(Collections.singletonList(modifier.annotation()));
            }
        }

        return l;
    }

    private List<Node> checkClassModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.NONE)) : checkModifiers("class", modifiers,
                FlagModifier.classModifiers));
    }

    private List<Node> checkTypeDefModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.NONE)) : checkModifiers("typedef", modifiers,
                FlagModifier.typeDefModifiers));
    }

    private List<Node> checkFieldModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("field", modifiers, FlagModifier.fieldModifiers));
    }

    private List<Node> checkVariableModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("variable", modifiers, FlagModifier.variableModifiers));
    }

    private List<Node> checkMethodModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("method", modifiers, FlagModifier.methodModifiers));
    }

    private List<Node> checkConstructorModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("constructor", modifiers, FlagModifier.constructorModifiers));
    }

    private List<Node> checkInterfaceModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("interface", modifiers, FlagModifier.interfaceModifiers));
    }

    private List<AnnotationNode> extractAnnotations(List<? extends Node> l) {
        List<AnnotationNode> l2 = new LinkedList<AnnotationNode>();
        for (Node n : l) {
            if (n instanceof AnnotationNode) {
                l2.add((AnnotationNode) n);
            }
        }
        return l2;
    }

    private FlagsNode extractFlags(List<? extends Node> l, Flags f) {
        FlagsNode fn = extractFlags(l);
        fn = fn.flags(fn.flags().set(f));
        return fn;
    }

    private FlagsNode extractFlags(List<? extends Node> l1, List<? extends Node> l2) {
        List<Node> l = new ArrayList<Node>();
        l.addAll(l1);
        l.addAll(l2);
        return extractFlags(l);
    }

    private FlagsNode extractFlags(List<? extends Node> l) {
        Position pos = null;
        Flags xf = Flags.NONE;
        for (Node n : l) {
            if (n instanceof FlagsNode) {
                FlagsNode fn = (FlagsNode) n;
                pos = pos == null ? fn.position() : new Position(pos, fn.position());
                Flags f = fn.flags();
                xf = xf.set(f);
            }
        }
        return nf.FlagsNode(pos == null ? Position.COMPILER_GENERATED : pos, xf);
    }


    // Lexer utility functions

    private long parseLong(String s, int radix, Position pos) {
        long x = 0L;

        s = s.toLowerCase();

        boolean reportedError = false;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);

            if (c < '0' || c > '9') {
                c = c - 'a' + 10;
            } else {
                c = c - '0';
            }

            if (c >= radix) {
                if (!reportedError) {
                    err.syntaxError("Invalid digit: '" + s.charAt(i) + "'", pos);
                    reportedError = true;
                }
            }

            x *= radix;
            x += c;
        }

        return x;
    }

    private long parseLong(String s, Position pos) {
        int radix;
        int start_index;
        int end_index;

        end_index = s.length();

        boolean isUnsigned = false;
        boolean isLong = true;
        long min = Long.MIN_VALUE;
        while (end_index > 0) {
            char lastCh = s.charAt(end_index - 1);
            if (lastCh == 'u' || lastCh == 'U')
                isUnsigned = true;
            // todo: long need special treatment cause we have overflows
            // for signed values that start with 0, we need to make them negative if they are above max value
            if (lastCh == 'n' || lastCh == 'N') {
                isLong = false;
                min = Integer.MIN_VALUE;
            }
            if (lastCh == 'y' || lastCh == 'Y') {
                isLong = false;
                min = Byte.MIN_VALUE;
            }
            if (lastCh == 's' || lastCh == 'S') {
                isLong = false;
                min = Short.MIN_VALUE;
            }
            if (lastCh != 'y' && lastCh != 'Y' && lastCh != 's' && lastCh != 'S' && lastCh != 'l' && lastCh != 'L' && lastCh != 'n' && lastCh != 'N' && lastCh != 'u'
                    && lastCh != 'U') {
                break;
            }
            end_index--;
        }
        long max = -min;

        if (s.charAt(0) == '0') {
            if (s.length() > 1 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                radix = 16;
                start_index = 2;
            } else {
                radix = 8;
                start_index = 0;
            }
        } else {
            radix = 10;
            start_index = 0;
        }

        final long res = parseLong(s.substring(start_index, end_index), radix, pos);
        if (!isUnsigned && !isLong && radix != 10 && res >= max) {
            // need to make this value negative
            // e.g., 0xffUY == 255, 0xffY== 255-256 = -1 , 0xfeYU==254, 0xfeY== 254-256 = -2
            return res + min * 2;
        }
        return res;
    }

    private polyglot.lex.FloatLiteral float_lit(LiteralContext ctx) {
        String s = ctx.getText();
        float x;
        try {
            int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F' ? s.length() - 1 : s.length());
            x = Float.parseFloat(s.substring(0, end_index));
        } catch (NumberFormatException e) {
            eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal float literal \"" + s + "\"", pos(ctx));
            x = Float.NaN;
        }
        return new FloatLiteral(pos(ctx), x, X10Parsersym.TK_FloatingPointLiteral); // TODO: check this!!
    }

    private polyglot.lex.DoubleLiteral double_lit(LiteralContext ctx) {
        String s = ctx.getText();
        double x;
        try {
            int end_index = (s.charAt(s.length() - 1) == 'd' || s.charAt(s.length() - 1) == 'D' ? s.length() - 1 : s.length());
            x = Double.parseDouble(s.substring(0, end_index));
        } catch (NumberFormatException e) {
            x = Double.NaN;
            eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal float literal \"" + s + "\"", pos(ctx));
        }
        return new DoubleLiteral(pos(ctx), x, X10Parsersym.TK_DoubleLiteral); // TODO: Check this!!
    }

    private polyglot.lex.BooleanLiteral boolean_lit(BooleanLiteralContext ctx) {
        return new BooleanLiteral(pos(ctx), ctx.start.getType() == X10Lexer.TRUE, ctx.start.getType());
    }

    private polyglot.lex.CharacterLiteral char_lit(LiteralContext ctx) {
        char x;
        String s = ctx.getText();
        if (s.charAt(1) == '\\') {
            switch (s.charAt(2)) {
            case 'u':
                x = (char) parseLong(s.substring(3, s.length() - 1), 16, pos(ctx));
                break;
            case 'b':
                x = '\b';
                break;
            case 't':
                x = '\t';
                break;
            case 'n':
                x = '\n';
                break;
            case 'f':
                x = '\f';
                break;
            case 'r':
                x = '\r';
                break;
            case '\"':
                x = '\"';
                break;
            case '\'':
                x = '\'';
                break;
            case '\\':
                x = '\\';
                break;
            default:
                x = (char) parseLong(s.substring(2, s.length() - 1), 8, pos(ctx));
                if (x > 255) {
                    eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal character literal " + s, pos(ctx));
                    x = s.charAt(2);
                }
            }
        } else {
            assert (s.length() == 3);
            x = s.charAt(1);
        }

        return new CharacterLiteral(pos(ctx), x, X10Parsersym.TK_CharacterLiteral);
    }

    private polyglot.lex.StringLiteral string_lit(LiteralContext ctx) {
        String s = ctx.getText();
        char x[] = new char[s.length()];
        int j = 1, k = 0;
        while (j < s.length() - 1) {
            if (s.charAt(j) != '\\')
                x[k++] = s.charAt(j++);
            else {
                switch (s.charAt(j + 1)) {
                case 'u':
                    x[k++] = (char) parseLong(s.substring(j + 2, j + 6), 16, pos(ctx));
                    j += 6;
                    break;
                case 'b':
                    x[k++] = '\b';
                    j += 2;
                    break;
                case 't':
                    x[k++] = '\t';
                    j += 2;
                    break;
                case 'n':
                    x[k++] = '\n';
                    j += 2;
                    break;
                case 'f':
                    x[k++] = '\f';
                    j += 2;
                    break;
                case 'r':
                    x[k++] = '\r';
                    j += 2;
                    break;
                case '\"':
                    x[k++] = '\"';
                    j += 2;
                    break;
                case '\'':
                    x[k++] = '\'';
                    j += 2;
                    break;
                case '`':
                    x[k++] = '`';
                    j += 2;
                    break;
                case '\\':
                    x[k++] = '\\';
                    j += 2;
                    break;
                default: {
                    int n = j + 1;
                    for (int l = 0; l < 3 && Character.isDigit(s.charAt(n)); l++)
                        n++;
                    char c = (char) parseLong(s.substring(j + 1, n), 8, pos(ctx));
                    if (c > 255) {
                        eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal character (" + s.substring(j, n) + ") in string literal " + s, pos(ctx));
                    }
                    x[k++] = c;
                    j = n;
                }
                }
            }
        }

        return new StringLiteral(pos(ctx), new String(x, 0, k), X10Parsersym.TK_StringLiteral);
    }

    private IntLit getIntLit(LiteralContext ctx, Kind k) {
        return nf.IntLit(pos(ctx), k, parseLong(ctx.getText(), pos(ctx)));
    }


    // Grammar actions


    /** Production: modifiersopt ::= modifier* (#modifiersopt) */
    @Override
    public void exitModifiersopt(ModifiersoptContext ctx) {
        List<Modifier> l = new LinkedList<Modifier>();
        for (ModifierContext m : ctx.modifier()) {
            l.add(ast(m));
        }
        ctx.ast = l;
    }

    /** Production: modifier ::= 'abstract' (#modifierAbstract) */
    @Override
    public void exitModifierAbstract(ModifierAbstractContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.ABSTRACT);
    }

    /** Production: modifier ::= annotation (#modifierAnnotation) */
    @Override
    public void exitModifierAnnotation(ModifierAnnotationContext ctx) {
        ctx.ast = new AnnotationModifier(ast(ctx.annotation()));
    }

    /** Production: modifier ::= 'atomic' (#modifierAtomic) */
    @Override
    public void exitModifierAtomic(ModifierAtomicContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.ATOMIC);
    }

    /** Production: modifier ::= 'final' (#modifierFinal) */
    @Override
    public void exitModifierFinal(ModifierFinalContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.FINAL);
    }

    /** Production: modifier ::= 'native' (#modifierNative) */
    @Override
    public void exitModifierNative(ModifierNativeContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.NATIVE);
    }

    /** Production: modifier ::= 'private' (#modifierPrivate) */
    @Override
    public void exitModifierPrivate(ModifierPrivateContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PRIVATE);
    }

    /** Production: modifier ::= 'protected' (#modifierProtected) */
    @Override
    public void exitModifierProtected(ModifierProtectedContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PROTECTED);
    }

    /** Production: modifier ::= 'public' (#modifierPublic) */
    @Override
    public void exitModifierPublic(ModifierPublicContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PUBLIC);
    }

    /** Production: modifier ::= 'static' (#modifierStatic) */
    @Override
    public void exitModifierStatic(ModifierStaticContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.STATIC);
    }

    /** Production: modifier ::= 'transient' (#modifierTransient) */
    @Override
    public void exitModifierTransient(ModifierTransientContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.TRANSIENT);
    }

    /** Production: modifier ::= 'clocked' (#modifierClocked) */
    @Override
    public void exitModifierClocked(ModifierClockedContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.CLOCKED);
    }

    /** Production: methodModifiersopt ::= methodModifier* (#methodModifiersopt) */
    @Override
    public void exitMethodModifiersopt(MethodModifiersoptContext ctx) {
        List<Modifier> l = new LinkedList<Modifier>();
        for (MethodModifierContext m : ctx.methodModifier()) {
            l.add(ast(m));
        }
        ctx.ast = l;
    }

    /** Production: methodModifier ::= modifier (#methodModifierModifier) */
    @Override
    public void exitMethodModifierModifier(MethodModifierModifierContext ctx) {
        ctx.ast = ast(ctx.modifier());
    }

    /** Production: methodModifier ::= 'property' (#methodModifierProperty) */
    @Override
    public void exitMethodModifierProperty(MethodModifierPropertyContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PROPERTY);
    }

    /** Production: typeDefDeclaration ::= modifiersopt 'type' identifier typeParametersopt ('(' formalParameterList ')')? whereClauseopt '=' type ';' (#typeDefDeclaration) */
    @Override
    public void exitTypeDefDeclaration(TypeDefDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        Id Identifier = ast(ctx.identifier());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameterList = ctx.formalParameterList() == null ? new ArrayList<Formal>() : ast(ctx.formalParameterList());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode Type = ast(ctx.type());
        List<Node> modifiers = checkTypeDefModifiers(Modifiersopt);
        FlagsNode f = extractFlags(modifiers);
        List<AnnotationNode> annotations = extractAnnotations(modifiers);
        List<Formal> formals = new ArrayList<Formal>();
        for (Formal v : FormalParameterList) {
            FlagsNode flags = v.flags();
            if (!flags.flags().isFinal()) {
                err.syntaxError("Type definition parameters must be final.", v.position());
                v = v.flags(flags.flags(flags.flags().Final()));
            }
            formals.add(v);
        }
        TypeDecl cd = nf.TypeDecl(pos(ctx), f, Identifier, TypeParametersopt, formals, WhereClauseopt, Type);
        cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
        cd = (TypeDecl) setComment(cd, ctx);
        ctx.ast = cd;
    }

    /** Production: propertiesopt ::= ('(' property (',' property)* ')')? (#propertiesopt) */
    @Override
    public void exitPropertiesopt(PropertiesoptContext ctx) {
        List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
        for (PropertyContext e : ctx.property()) {
            l.add(ast(e));
        }
        ctx.ast = l;
    }

    /** Production: property ::= annotationsopt identifier resultType (#property) */
    @Override
    public void exitProperty(PropertyContext ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        Id Identifier = ast(ctx.identifier());
        TypeNode ResultType = ast(ctx.resultType());
        List<AnnotationNode> annotations = extractAnnotations(Annotationsopt);
        PropertyDecl cd = nf.PropertyDecl(pos(ctx), nf.FlagsNode(pos(ctx), Flags.PUBLIC.Final()), ResultType, Identifier);
        cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
        cd = (PropertyDecl) setComment(cd, ctx);
        ctx.ast = cd;
    }

    /**
     * Production: methodDeclaration ::= methodModifiersopt 'def' identifier typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt
     * methodBody (#methodDeclarationMethod)
     */
    @Override
    public void exitMethodDeclarationMethod(MethodDeclarationMethodContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        Id Identifier = ast(ctx.identifier());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameters = ast(ctx.formalParameters());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> Throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        ProcedureDecl pd = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                Identifier, TypeParametersopt, FormalParameters, WhereClauseopt, OBSOLETE_Offersopt, Throwsopt, MethodBody);
        pd = (ProcedureDecl) ((X10Ext) pd.ext()).annotations(extractAnnotations(modifiers));
        pd = (ProcedureDecl) setComment(pd, ctx);
        ctx.ast = pd;
    }

    /** Production: methodDeclaration ::= binaryOperatorDeclaration (#methodDeclarationBinaryOp) */
    @Override
    public void exitMethodDeclarationBinaryOp(MethodDeclarationBinaryOpContext ctx) {
        ctx.ast = ast(ctx.binaryOperatorDeclaration());
    }

    /** Production: methodDeclaration ::= prefixOperatorDeclaration (#methodDeclarationPrefixOp) */
    @Override
    public void exitMethodDeclarationPrefixOp(MethodDeclarationPrefixOpContext ctx) {
        ctx.ast = ast(ctx.prefixOperatorDeclaration());
    }

    /** Production: methodDeclaration ::= applyOperatorDeclaration (#methodDeclarationApplyOp) */
    @Override
    public void exitMethodDeclarationApplyOp(MethodDeclarationApplyOpContext ctx) {
        ctx.ast = ast(ctx.applyOperatorDeclaration());
    }

    /** Production: methodDeclaration ::= setOperatorDeclaration (#methodDeclarationSetOp) */
    @Override
    public void exitMethodDeclarationSetOp(MethodDeclarationSetOpContext ctx) {
        ctx.ast = ast(ctx.setOperatorDeclaration());
    }

    /** Production: methodDeclaration ::= conversionOperatorDeclaration (#methodDeclarationConversionOp) */
    @Override
    public void exitMethodDeclarationConversionOp(MethodDeclarationConversionOpContext ctx) {
        ctx.ast = ast(ctx.conversionOperatorDeclaration());
    }

    /**
     * Production: binaryOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt '(' fp1=formalParameter ')' binOp '(' fp2=formalParameter ')' whereClauseopt
     * oBSOLETE_Offersopt throwsopt hasResultTypeopt methodBody (#binaryOperatorDecl)
     */
    @Override
    public void exitBinaryOperatorDecl(BinaryOperatorDeclContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        X10Formal fp1 = ast(ctx.fp1);
        Binary.Operator BinOp = ast(ctx.binOp());
        X10Formal fp2 = ast(ctx.fp2);
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot override binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.binOp()), opName), TypeParametersopt, Arrays.<Formal> asList(fp1, fp2), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Binary operator with two parameters must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: binaryOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt 'this' binOp '(' fp2=formalParameter ')' whereClauseopt oBSOLETE_Offersopt
     * throwsopt hasResultTypeopt methodBody (#binaryOperatorDeclThisLeft)
     */
    @Override
    public void exitBinaryOperatorDeclThisLeft(BinaryOperatorDeclThisLeftContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        Binary.Operator BinOp = ast(ctx.binOp());
        X10Formal fp2 = ast(ctx.fp2);
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot override binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.binOp()), opName), TypeParametersopt, Collections.<Formal> singletonList(fp2), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Binary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: binaryOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt '(' fp1=formalParameter ')' binOp 'this' whereClauseopt oBSOLETE_Offersopt
     * throwsopt hasResultTypeopt methodBody (#binaryOperatorDeclThisRight)
     */
    @Override
    public void exitBinaryOperatorDeclThisRight(BinaryOperatorDeclThisRightContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        X10Formal fp1 = ast(ctx.fp1);
        Binary.Operator BinOp = ast(ctx.binOp());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            // [DC] doesn't look like this can ever happen?
            err.syntaxError("Cannot override binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.binOp()), opName), TypeParametersopt, Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Binary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: prefixOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt prefixOp '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt
     * hasResultTypeopt methodBody (#prefixOperatorDecl)
     */
    @Override
    public void exitPrefixOperatorDecl(PrefixOperatorDeclContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        Unary.Operator PrefixOp = ast(ctx.prefixOp());
        X10Formal fp2 = ast(ctx.formalParameter());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            err.syntaxError("Cannot override unary operator '" + PrefixOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.prefixOp()), opName), TypeParametersopt, Collections.<Formal> singletonList(fp2), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Unary operator with one parameter must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: prefixOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt prefixOp 'this' whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt
     * methodBody (#prefixOperatorDeclThis)
     */
    @Override
    public void exitPrefixOperatorDeclThis(PrefixOperatorDeclThisContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        Unary.Operator PrefixOp = ast(ctx.prefixOp());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            err.syntaxError("Cannot override unary operator '" + PrefixOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.prefixOp()), opName), TypeParametersopt, Collections.<Formal> emptyList(), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Unary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: applyOperatorDeclaration ::= methodModifiersopt 'operator' 'this' typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt
     * methodBody (#applyOperatorDeclaration)
     */
    @Override
    public void exitApplyOperatorDeclaration(ApplyOperatorDeclarationContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameters = ast(ctx.formalParameters());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), ClosureCall.APPLY), TypeParametersopt, FormalParameters, WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("operator() cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) ((X10Ext) md.ext()).setComment(comment(ctx));
        ctx.ast = md;
    }

    /**
     * Production: setOperatorDeclaration ::= methodModifiersopt 'operator' 'this' typeParametersopt formalParameters '=' '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt
     * throwsopt hasResultTypeopt methodBody (#setOperatorDeclaration)
     */
    @Override
    public void exitSetOperatorDeclaration(SetOperatorDeclarationContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameters = ast(ctx.formalParameters());
        X10Formal fp2 = ast(ctx.formalParameter());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), SettableAssign.SET), TypeParametersopt, CollectionUtil.append(FormalParameters, Collections.singletonList(fp2)), WhereClauseopt,
                OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Set operator cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /** Production: conversionOperatorDeclaration ::= explicitConversionOperatorDeclaration (#conversionOperatorDeclarationExplicit) */
    @Override
    public void exitConversionOperatorDeclarationExplicit(ConversionOperatorDeclarationExplicitContext ctx) {
        ctx.ast = ast(ctx.explicitConversionOperatorDeclaration());
    }

    /** Production: conversionOperatorDeclaration ::= implicitConversionOperatorDeclaration (#conversionOperatorDeclarationImplicit) */
    @Override
    public void exitConversionOperatorDeclarationImplicit(ConversionOperatorDeclarationImplicitContext ctx) {
        ctx.ast = ast(ctx.implicitConversionOperatorDeclaration());
    }

    /**
     * Production: explicitConversionOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' 'as' type whereClauseopt oBSOLETE_Offersopt
     * throwsopt methodBody (#explicitConversionOperatorDecl0)
     */
    @Override
    public void exitExplicitConversionOperatorDecl0(ExplicitConversionOperatorDecl0Context ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        X10Formal fp1 = ast(ctx.formalParameter());
        TypeNode Type = ast(ctx.type());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), Type, nf.Id(pos(ctx), Converter.operator_as), TypeParametersopt,
                Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: explicitConversionOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' 'as' '?' whereClauseopt oBSOLETE_Offersopt
     * throwsopt hasResultTypeopt methodBody (#explicitConversionOperatorDecl1)
     */
    @Override
    public void exitExplicitConversionOperatorDecl1(ExplicitConversionOperatorDecl1Context ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        X10Formal fp1 = ast(ctx.formalParameter());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), Converter.operator_as), TypeParametersopt, Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /**
     * Production: implicitConversionOperatorDeclaration ::= methodModifiersopt 'operator' typeParametersopt '(' formalParameter ')' whereClauseopt oBSOLETE_Offersopt throwsopt
     * hasResultTypeopt methodBody (#implicitConversionOperatorDeclaration)
     */
    @Override
    public void exitImplicitConversionOperatorDeclaration(ImplicitConversionOperatorDeclarationContext ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        X10Formal fp1 = ast(ctx.formalParameter());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> throwsopt = ast(ctx.throwsopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), Converter.implicit_operator_as), TypeParametersopt, Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /** Production: propertyMethodDeclaration ::= methodModifiersopt identifier typeParametersopt formalParameters whereClauseopt hasResultTypeopt methodBody (#propertyMethodDecl0) */
    @Override
    public void exitPropertyMethodDecl0(PropertyMethodDecl0Context ctx) {
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        Id Identifier = ast(ctx.identifier());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameters = ast(ctx.formalParameters());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers, Flags.PROPERTY), HasResultTypeopt == null ? nf.UnknownTypeNode(pos(ctx).markCompilerGenerated())
                : HasResultTypeopt, Identifier, TypeParametersopt, FormalParameters, WhereClauseopt, null, // offersOpt
                Collections.<TypeNode> emptyList(), MethodBody);
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /** Production: propertyMethodDeclaration ::= methodModifiersopt identifier whereClauseopt hasResultTypeopt methodBody (#propertyMethodDecl1) */
    @Override
    public void exitPropertyMethodDecl1(PropertyMethodDecl1Context ctx) {
        err.syntaxError("This syntax is no longer supported. You must supply the property method formals, and if there are none, you can use an empty parenthesis '()'.", pos(ctx));
        List<Modifier> MethodModifiersopt = ast(ctx.methodModifiersopt());
        Id Identifier = ast(ctx.identifier());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        Block MethodBody = ast(ctx.methodBody());
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers, Flags.PROPERTY), HasResultTypeopt == null ? nf.UnknownTypeNode(pos(ctx).markCompilerGenerated())
                : HasResultTypeopt, Identifier, Collections.<TypeParamNode> emptyList(), Collections.<Formal> emptyList(), WhereClauseopt, null, // offersOpt
                Collections.<TypeNode> emptyList(), MethodBody);
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        md = (MethodDecl) setComment(md, ctx);
        ctx.ast = md;
    }

    /** Production: explicitConstructorInvocation ::= 'this' typeArgumentsopt '(' argumentListopt ')' ';' (#explicitConstructorInvocationThis) */
    @Override
    public void exitExplicitConstructorInvocationThis(ExplicitConstructorInvocationThisContext ctx) {
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = nf.X10ThisCall(pos(ctx), TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: explicitConstructorInvocation ::= 'super' typeArgumentsopt '(' argumentListopt ')' ';' (#explicitConstructorInvocationSuper) */
    @Override
    public void exitExplicitConstructorInvocationSuper(ExplicitConstructorInvocationSuperContext ctx) {
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = nf.X10SuperCall(pos(ctx), TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: explicitConstructorInvocation ::= primary '.' 'this' typeArgumentsopt '(' argumentListopt ')' ';' (#explicitConstructorInvocationPrimaryThis) */
    @Override
    public void exitExplicitConstructorInvocationPrimaryThis(ExplicitConstructorInvocationPrimaryThisContext ctx) {
        Expr Primary = ast(ctx.primary());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = nf.X10ThisCall(pos(ctx), Primary, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: explicitConstructorInvocation ::= primary '.' 'super' typeArgumentsopt '(' argumentListopt ')' ';' (#explicitConstructorInvocationPrimarySuper) */
    @Override
    public void exitExplicitConstructorInvocationPrimarySuper(ExplicitConstructorInvocationPrimarySuperContext ctx) {
        Expr Primary = ast(ctx.primary());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = nf.X10SuperCall(pos(ctx), Primary, TypeArgumentsopt, ArgumentListopt);
    }

    /**
     * Production: interfaceDeclaration ::= modifiersopt 'interface' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt extendsInterfacesopt interfaceBody
     * (#interfaceDeclaration)
     */
    @Override
    public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        Id Identifier = ast(ctx.identifier());
        List<TypeParamNode> TypeParamsWithVarianceopt = ast(ctx.typeParamsWithVarianceopt());
        List<PropertyDecl> Propertiesopt = ast(ctx.propertiesopt());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        List<TypeNode> ExtendsInterfacesopt = ast(ctx.extendsInterfacesopt());
        ClassBody InterfaceBody = ast(ctx.interfaceBody());
        List<Node> modifiers = checkInterfaceModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        // we use the property syntax for annotation-interfaces:
        // public interface Pragma(pragma:Int) extends StatementAnnotation { ...
        // }
        DepParameterExpr ci = WhereClauseopt;
        FlagsNode fn = extractFlags(modifiers, Flags.INTERFACE);
        ClassDecl cd = nf.X10ClassDecl(pos(ctx), fn, Identifier, TypeParametersopt, props, ci, null, ExtendsInterfacesopt, InterfaceBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        cd = (ClassDecl) setComment(cd, ctx);
        ctx.ast = cd;
    }

    /** Production: assignPropertyCall ::= 'property' typeArgumentsopt '(' argumentListopt ')' ';' (#assignPropertyCall) */
    @Override
    public void exitAssignPropertyCall(AssignPropertyCallContext ctx) {
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = nf.AssignPropertyCall(pos(ctx), TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: type ::= functionType (#typeFunctionType) */
    @Override
    public void exitTypeFunctionType(TypeFunctionTypeContext ctx) {
        ctx.ast = ast(ctx.functionType());
    }

    /** Production: type ::= namedType (#typeConstrainedType) */
    @Override
    public void exitTypeConstrainedType(TypeConstrainedTypeContext ctx) {
        ctx.ast = ast(ctx.namedType());
    }

    /** Production: type ::= 'void' (#typeVoid) */
    @Override
    public void exitTypeVoid(TypeVoidContext ctx) {
        ctx.ast = nf.CanonicalTypeNode(pos(ctx), ts.Void());
    }

    /** Production: type ::= type annotations (#typeAnnotations) */
    @Override
    public void exitTypeAnnotations(TypeAnnotationsContext ctx) {
        TypeNode Type = ast(ctx.type());
        List<AnnotationNode> Annotations = ast(ctx.annotations());
        TypeNode tn = Type;
        tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
        ctx.ast = (TypeNode) tn.position(pos(ctx));
    }

    /** Production: functionType ::= typeParametersopt '(' formalParameterListopt ')' whereClauseopt oBSOLETE_Offersopt '=>' type (#functionType) */
    @Override
    public void exitFunctionType(FunctionTypeContext ctx) {
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameterListopt = ast(ctx.formalParameterListopt());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        TypeNode Type = ast(ctx.type());
        ctx.ast = nf.FunctionTypeNode(pos(ctx), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type, OBSOLETE_Offersopt);
    }

    /** Production: classType ::= namedType (#classType) */
    @Override
    public void exitClassType(ClassTypeContext ctx) {
        ctx.ast = ast(ctx.namedType());
    }

    /** Production: simpleNamedType ::= typeName (#simpleNamedType0) */
    @Override
    public void exitSimpleNamedType0(SimpleNamedType0Context ctx) {
        ParsedName TypeName = ast(ctx.typeName());
        ctx.ast = (AmbTypeNode) TypeName.toType();
    }

    /** Production: simpleNamedType ::= primary '.' identifier (#simpleNamedType1) */
    @Override
    public void exitSimpleNamedType1(SimpleNamedType1Context ctx) {
        Expr Primary = ast(ctx.primary());
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.AmbTypeNode(pos(ctx), Primary, Identifier);
    }

    /** Production: simpleNamedType ::= simpleNamedType typeArgumentsopt argumentsopt depParameters? '.' identifier (#simpleNamedType2) */
    @Override
    public void exitSimpleNamedType2(SimpleNamedType2Context ctx) {
        AmbTypeNode SimpleNamedType = ast(ctx.simpleNamedType());
        List<TypeNode> TypeArguments = ast(ctx.typeArgumentsopt());
        List<Expr> Arguments = ast(ctx.argumentsopt());
        TypeNode qualifier;
        if (ctx.depParameters() == null) {
            qualifier = nf.AmbMacroTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), TypeArguments, Arguments);
        } else {
            DepParameterExpr DepParameters = ast(ctx.depParameters());
            qualifier = nf.AmbDepTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), TypeArguments, Arguments, DepParameters);
        }
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.AmbTypeNode(pos(ctx), qualifier, Identifier);
    }

    /** Production: namedTypeNoConstraints ::= simpleNamedType typeArguments? arguments? (#namedTypeNoConstraints) */
    @Override
    public void exitNamedTypeNoConstraints(NamedTypeNoConstraintsContext ctx) {
        AmbTypeNode SimpleNamedType = ast(ctx.simpleNamedType());
        if (ctx.typeArguments() == null && ctx.arguments() == null) {
            ctx.ast = SimpleNamedType;
        } else {
            List<TypeNode> typeArguments = ctx.typeArguments() == null ? new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false) : ast(ctx.typeArguments());
            List<Expr> Arguments = ctx.arguments() == null ? new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false) : ast(ctx.arguments());
            AmbMacroTypeNode type = nf.AmbMacroTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), typeArguments, Arguments);
            ctx.ast = type;
        }
    }

    /** Production: namedType ::= simpleNamedType typeArguments? arguments? depParameters? (#namedType) */
    @Override
    public void exitNamedType(NamedTypeContext ctx) {
        AmbTypeNode SimpleNamedType = ast(ctx.simpleNamedType());
        if (ctx.typeArguments() == null && ctx.arguments() == null && ctx.depParameters() == null) {
            ctx.ast = SimpleNamedType;
        } else {
            List<TypeNode> typeArguments = ctx.typeArguments() == null ? new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false) : ast(ctx.typeArguments());
            List<Expr> Arguments = ctx.arguments() == null ? new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false) : ast(ctx.arguments());
            AmbMacroTypeNode ParameterizedNamedType = nf.AmbMacroTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), typeArguments, Arguments);
            if (ctx.depParameters() == null) {
                ctx.ast = ParameterizedNamedType;
            } else {
                DepParameterExpr DepParameters = ast(ctx.depParameters());
                TypeNode type = nf.AmbDepTypeNode(pos(ctx), ParameterizedNamedType, DepParameters);
                ctx.ast = type;
            }
        }
    }

    /** Production: depParameters ::= '{' constraintConjunctionopt '}' (#depParameters) */
    @Override
    public void exitDepParameters(DepParametersContext ctx) {
        List<Formal> FUTURE_ExistentialListopt = new ArrayList<Formal>();
        List<Expr> ConstraintConjunctionopt = ast(ctx.constraintConjunctionopt());
        ctx.ast = nf.DepParameterExpr(pos(ctx), FUTURE_ExistentialListopt, ConstraintConjunctionopt);
    }

    /** Production: typeParamsWithVarianceopt ::= ('[' typeParamWithVarianceList ']')? (#typeParamsWithVarianceopt) */
    @Override
    public void exitTypeParamsWithVarianceopt(TypeParamsWithVarianceoptContext ctx) {
        if (ctx.typeParamWithVarianceList() == null) {
            ctx.ast = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        } else {
            ctx.ast = ast(ctx.typeParamWithVarianceList());
        }
    }

    /** Production: typeParametersopt ::= ('[' typeParameterList ']')? (#typeParametersopt) */
    @Override
    public void exitTypeParametersopt(TypeParametersoptContext ctx) {
        if (ctx.typeParameterList() == null) {
            ctx.ast = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        } else {
            ctx.ast = ast(ctx.typeParameterList());
        }
    }

    /** Production: formalParameters ::= '(' formalParameterListopt ')' (#formalParameters) */
    @Override
    public void exitFormalParameters(FormalParametersContext ctx) {
        ctx.ast = ast(ctx.formalParameterListopt());
    }

    /** Production: constraintConjunctionopt ::= (expression (',' expression)*)? (#constraintConjunctionopt) */
    @Override
    public void exitConstraintConjunctionopt(ConstraintConjunctionoptContext ctx) {
        List<Expr> l = new ArrayList<Expr>();
        for (ExpressionContext e : ctx.expression()) {
            l.add(ast(e));
        }
        ctx.ast = l;
    }

    /** Production: whereClauseopt ::= depParameters? (#whereClauseopt) */
    @Override
    public void exitWhereClauseopt(WhereClauseoptContext ctx) {
        if (ctx.depParameters() == null) {
            ctx.ast = null;
        } else {
            DepParameterExpr DepParameters = ast(ctx.depParameters());
            ctx.ast = DepParameters;
        }
    }

    /**
     * Production: classDeclaration ::= modifiersopt 'class' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt superExtendsopt interfacesopt classBody
     * (#classDeclaration)
     */
    @Override
    public void exitClassDeclaration(ClassDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        Id Identifier = ast(ctx.identifier());
        List<TypeParamNode> TypeParamsWithVarianceopt = ast(ctx.typeParamsWithVarianceopt());
        List<PropertyDecl> Propertiesopt = ast(ctx.propertiesopt());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode Superopt = ast(ctx.superExtendsopt());
        List<TypeNode> Interfacesopt = ast(ctx.interfacesopt());
        ClassBody ClassBody = ast(ctx.classBody());
        List<Node> modifiers = checkClassModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        DepParameterExpr ci = WhereClauseopt;
        FlagsNode f = extractFlags(modifiers);
        List<AnnotationNode> annotations = extractAnnotations(modifiers);
        ClassDecl cd = nf.X10ClassDecl(pos(ctx), f, Identifier, TypeParametersopt, props, ci, Superopt, Interfacesopt, ClassBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
        cd = (ClassDecl) setComment(cd, ctx);
        ctx.ast = cd;
    }

    /** Production: structDeclaration ::= modifiersopt 'struct' identifier typeParamsWithVarianceopt propertiesopt whereClauseopt interfacesopt classBody (#structDeclaration) */
    @Override
    public void exitStructDeclaration(StructDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        Id Identifier = ast(ctx.identifier());
        List<TypeParamNode> TypeParamsWithVarianceopt = ast(ctx.typeParamsWithVarianceopt());
        List<PropertyDecl> Propertiesopt = ast(ctx.propertiesopt());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        List<TypeNode> Interfacesopt = ast(ctx.interfacesopt());
        ClassBody ClassBody = ast(ctx.classBody());
        List<Node> modifiers = checkClassModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        DepParameterExpr ci = WhereClauseopt;
        ClassDecl cd = nf.X10ClassDecl(pos(ctx), extractFlags(modifiers, Flags.STRUCT), Identifier, TypeParametersopt, props, ci, null, Interfacesopt, ClassBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        cd = (ClassDecl) setComment(cd, ctx);
        ctx.ast = cd;
    }

    /**
     * Production: constructorDeclaration ::= modifiersopt 'def' id='this' typeParametersopt formalParameters whereClauseopt oBSOLETE_Offersopt throwsopt hasResultTypeopt
     * constructorBody (#constructorDeclaration)
     */
    @Override
    public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<TypeParamNode> TypeParametersopt = ast(ctx.typeParametersopt());
        List<Formal> FormalParameters = ast(ctx.formalParameters());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        List<TypeNode> Throwsopt = ast(ctx.throwsopt());
        Block ConstructorBody = ast(ctx.constructorBody());
        List<Node> modifiers = checkConstructorModifiers(Modifiersopt);
        ConstructorDecl cd = nf.X10ConstructorDecl(pos(ctx), extractFlags(modifiers), nf.Id(pos(ctx.id), TypeSystem.CONSTRUCTOR_NAME), HasResultTypeopt, TypeParametersopt,
                FormalParameters, WhereClauseopt, OBSOLETE_Offersopt, Throwsopt, ConstructorBody);
        cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        cd = (ConstructorDecl) setComment(cd, ctx);
        ctx.ast = cd;
    }

    /** Production: superExtendsopt ::= ('extends' classType)? (#superExtendsopt) */
    @Override
    public void exitSuperExtendsopt(SuperExtendsoptContext ctx) {
        if (ctx.classType() == null) {
            ctx.ast = null;
        } else {
            ctx.ast = ast(ctx.classType());
        }
    }

    /** Production: varKeyword ::= 'val' (#varKeyword0) */
    @Override
    public void exitVarKeyword0(VarKeyword0Context ctx) {
        ctx.ast = Collections.singletonList(nf.FlagsNode(pos(ctx), Flags.FINAL));
    }

    /** Production: varKeyword ::= 'var' (#varKeyword1) */
    @Override
    public void exitVarKeyword1(VarKeyword1Context ctx) {
        ctx.ast = Collections.singletonList(nf.FlagsNode(pos(ctx), Flags.NONE));
    }

    /** Production: fieldDeclaration ::= modifiersopt varKeyword? fieldDeclarators ';' (#fieldDeclaration) */
    @Override
    public void exitFieldDeclaration(FieldDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<FlagsNode> FieldKeyword = ctx.varKeyword() == null ? Collections.singletonList(nf.FlagsNode(pos(ctx), Flags.FINAL)) : ast(ctx.varKeyword());
        List<Object[]> FieldDeclarators = ast(ctx.fieldDeclarators());
        List<Node> modifiers = checkFieldModifiers(Modifiersopt);
        FlagsNode fn = extractFlags(modifiers, FieldKeyword);
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (Object[] o : FieldDeclarators) {
            if (o != null) {
                Position pos = (Position) o[0];
                Id name = (Id) o[1];
                if (name == null)
                    name = nf.Id(pos, Name.makeFresh());
                TypeNode type = (TypeNode) o[3];
                if (type == null)
                    type = nf.UnknownTypeNode(name.position().markCompilerGenerated());
                Expr init = (Expr) o[4];
                FieldDecl fd = nf.FieldDecl(pos, fn, type, name, init);
                fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(modifiers));
                fd = (FieldDecl) setComment(fd, ctx);
                l.add(fd);
            }
        }
        ctx.ast = l;
    }

    /** Production: statement ::= annotationStatement (#statement0) */
    @Override
    public void exitStatement0(Statement0Context ctx) {
        ctx.ast = ast(ctx.annotationStatement());
    }

    /** Production: statement ::= expressionStatement (#statement1) */
    @Override
    public void exitStatement1(Statement1Context ctx) {
        ctx.ast = ast(ctx.expressionStatement());
    }

    /** Production: annotationStatement ::= annotationsopt nonExpressionStatement (#annotationStatement) */
    @Override
    public void exitAnnotationStatement(AnnotationStatementContext ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        Stmt NonExpressionStatement = ast(ctx.nonExpressionStatement());
        if (NonExpressionStatement.ext() instanceof X10Ext) {
            NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations(Annotationsopt);
        }
        ctx.ast = (Stmt) NonExpressionStatement.position(pos(ctx));
    }

    /** Production: nonExpressionStatement ::= block (#nonExpressionStatemen0) */
    @Override
    public void exitNonExpressionStatemen0(NonExpressionStatemen0Context ctx) {
        ctx.ast = ast(ctx.block());
    }

    /** Production: nonExpressionStatement ::= emptyStatement (#nonExpressionStatemen1) */
    @Override
    public void exitNonExpressionStatemen1(NonExpressionStatemen1Context ctx) {
        ctx.ast = ast(ctx.emptyStatement());
    }

    /** Production: nonExpressionStatement ::= assertStatement (#nonExpressionStatemen2) */
    @Override
    public void exitNonExpressionStatemen2(NonExpressionStatemen2Context ctx) {
        ctx.ast = ast(ctx.assertStatement());
    }

    /** Production: nonExpressionStatement ::= switchStatement (#nonExpressionStatemen3) */
    @Override
    public void exitNonExpressionStatemen3(NonExpressionStatemen3Context ctx) {
        ctx.ast = ast(ctx.switchStatement());
    }

    /** Production: nonExpressionStatement ::= doStatement (#nonExpressionStatemen4) */
    @Override
    public void exitNonExpressionStatemen4(NonExpressionStatemen4Context ctx) {
        ctx.ast = ast(ctx.doStatement());
    }

    /** Production: nonExpressionStatement ::= breakStatement (#nonExpressionStatemen5) */
    @Override
    public void exitNonExpressionStatemen5(NonExpressionStatemen5Context ctx) {
        ctx.ast = ast(ctx.breakStatement());
    }

    /** Production: nonExpressionStatement ::= continueStatement (#nonExpressionStatemen6) */
    @Override
    public void exitNonExpressionStatemen6(NonExpressionStatemen6Context ctx) {
        ctx.ast = ast(ctx.continueStatement());
    }

    /** Production: nonExpressionStatement ::= returnStatement (#nonExpressionStatemen7) */
    @Override
    public void exitNonExpressionStatemen7(NonExpressionStatemen7Context ctx) {
        ctx.ast = ast(ctx.returnStatement());
    }

    /** Production: nonExpressionStatement ::= throwStatement (#nonExpressionStatemen8) */
    @Override
    public void exitNonExpressionStatemen8(NonExpressionStatemen8Context ctx) {
        ctx.ast = ast(ctx.throwStatement());
    }

    /** Production: nonExpressionStatement ::= tryStatement (#nonExpressionStatemen9) */
    @Override
    public void exitNonExpressionStatemen9(NonExpressionStatemen9Context ctx) {
        ctx.ast = ast(ctx.tryStatement());
    }

    /** Production: nonExpressionStatement ::= labeledStatement (#nonExpressionStatemen10) */
    @Override
    public void exitNonExpressionStatemen10(NonExpressionStatemen10Context ctx) {
        ctx.ast = ast(ctx.labeledStatement());
    }

    /** Production: nonExpressionStatement ::= ifThenStatement (#nonExpressionStatemen11) */
    @Override
    public void exitNonExpressionStatemen11(NonExpressionStatemen11Context ctx) {
        ctx.ast = ast(ctx.ifThenStatement());
    }

    /** Production: nonExpressionStatement ::= whileStatement (#nonExpressionStatemen13) */
    @Override
    public void exitNonExpressionStatemen13(NonExpressionStatemen13Context ctx) {
        ctx.ast = ast(ctx.whileStatement());
    }

    /** Production: nonExpressionStatement ::= forStatement (#nonExpressionStatemen14) */
    @Override
    public void exitNonExpressionStatemen14(NonExpressionStatemen14Context ctx) {
        ctx.ast = ast(ctx.forStatement());
    }

    /** Production: nonExpressionStatement ::= asyncStatement (#nonExpressionStatemen15) */
    @Override
    public void exitNonExpressionStatemen15(NonExpressionStatemen15Context ctx) {
        ctx.ast = ast(ctx.asyncStatement());
    }

    /** Production: nonExpressionStatement ::= atStatement (#nonExpressionStatemen16) */
    @Override
    public void exitNonExpressionStatemen16(NonExpressionStatemen16Context ctx) {
        ctx.ast = ast(ctx.atStatement());
    }

    /** Production: nonExpressionStatement ::= atomicStatement (#nonExpressionStatemen17) */
    @Override
    public void exitNonExpressionStatemen17(NonExpressionStatemen17Context ctx) {
        ctx.ast = ast(ctx.atomicStatement());
    }

    /** Production: nonExpressionStatement ::= whenStatement (#nonExpressionStatemen18) */
    @Override
    public void exitNonExpressionStatemen18(NonExpressionStatemen18Context ctx) {
        ctx.ast = ast(ctx.whenStatement());
    }

    /** Production: nonExpressionStatement ::= atEachStatement (#nonExpressionStatemen19) */
    @Override
    public void exitNonExpressionStatemen19(NonExpressionStatemen19Context ctx) {
        ctx.ast = ast(ctx.atEachStatement());
    }

    /** Production: nonExpressionStatement ::= finishStatement (#nonExpressionStatemen20) */
    @Override
    public void exitNonExpressionStatemen20(NonExpressionStatemen20Context ctx) {
        ctx.ast = ast(ctx.finishStatement());
    }

    /** Production: nonExpressionStatement ::= assignPropertyCall (#nonExpressionStatemen21) */
    @Override
    public void exitNonExpressionStatemen21(NonExpressionStatemen21Context ctx) {
        ctx.ast = ast(ctx.assignPropertyCall());
    }

    /** Production: nonExpressionStatement ::= oBSOLETE_OfferStatement (#nonExpressionStatemen22) */
    @Override
    public void exitNonExpressionStatemen22(NonExpressionStatemen22Context ctx) {
        ctx.ast = ast(ctx.oBSOLETE_OfferStatement());
    }

    /** Production: oBSOLETE_OfferStatement ::= 'offer' expression ';' (#oBSOLETE_OfferStatement) */
    @Override
    public void exitOBSOLETE_OfferStatement(OBSOLETE_OfferStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        ctx.ast = nf.Offer(pos(ctx), Expression);
    }

    /** Production: ifThenStatement ::= 'if' '(' expression ')' s1=statement ('else' s2=statement)? (#ifThenStatement) */
    @Override
    public void exitIfThenStatement(IfThenStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        Stmt s1 = ast(ctx.s1);
        if (ctx.s2 == null) {
            ctx.ast = nf.If(pos(ctx), Expression, s1);
        } else {
            Stmt s2 = ast(ctx.s2);
            ctx.ast = nf.If(pos(ctx), Expression, s1, s2);
        }
    }

    /** Production: emptyStatement ::= ';' (#emptyStatement) */
    @Override
    public void exitEmptyStatement(EmptyStatementContext ctx) {
        ctx.ast = nf.Empty(pos(ctx));
    }

    /** Production: labeledStatement ::= identifier ':' loopStatement (#labeledStatement) */
    @Override
    public void exitLabeledStatement(LabeledStatementContext ctx) {
        Id Identifier = ast(ctx.identifier());
        Stmt LoopStatement = ast(ctx.loopStatement());
        ctx.ast = nf.Labeled(pos(ctx), Identifier, LoopStatement);
    }

    /** Production: loopStatement ::= forStatement (#loopStatement0) */
    @Override
    public void exitLoopStatement0(LoopStatement0Context ctx) {
        ctx.ast = ast(ctx.forStatement());
    }

    /** Production: loopStatement ::= whileStatement (#loopStatement1) */
    @Override
    public void exitLoopStatement1(LoopStatement1Context ctx) {
        ctx.ast = ast(ctx.whileStatement());
    }

    /** Production: loopStatement ::= doStatement (#loopStatement2) */
    @Override
    public void exitLoopStatement2(LoopStatement2Context ctx) {
        ctx.ast = ast(ctx.doStatement());
    }

    /** Production: loopStatement ::= atEachStatement (#loopStatement3) */
    @Override
    public void exitLoopStatement3(LoopStatement3Context ctx) {
        ctx.ast = ast(ctx.atEachStatement());
    }

    /** Production: expressionStatement ::= expression ';' (#expressionStatement) */
    @Override
    public void exitExpressionStatement(ExpressionStatementContext ctx) {
        Expr StatementExpression = ast(ctx.expression());
        ctx.ast = nf.Eval(pos(ctx), StatementExpression);
    }

    /** Production: assertStatement ::= 'assert' expression ';' (#assertStatement0) */
    @Override
    public void exitAssertStatement0(AssertStatement0Context ctx) {
        Expr Expression = ast(ctx.expression());
        ctx.ast = nf.Assert(pos(ctx), Expression);
    }

    /** Production: assertStatement ::= 'assert' e1=expression ':' e2=expression ';' (#assertStatement1) */
    @Override
    public void exitAssertStatement1(AssertStatement1Context ctx) {
        Expr expr1 = ast(ctx.e1);
        Expr expr2 = ast(ctx.e2);
        ctx.ast = nf.Assert(pos(ctx), expr1, expr2);
    }

    /** Production: switchStatement ::= 'switch' '(' expression ')' switchBlock (#switchStatement) */
    @Override
    public void exitSwitchStatement(SwitchStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        List<SwitchElement> SwitchBlock = ast(ctx.switchBlock());
        ctx.ast = nf.Switch(pos(ctx), Expression, SwitchBlock);
    }

    /** Production: switchBlock ::= '{' switchBlockStatementGroupsopt switchLabelsopt '}' (#switchBlock) */
    @Override
    public void exitSwitchBlock(SwitchBlockContext ctx) {
        List<SwitchElement> SwitchBlockStatementGroupsopt = ast(ctx.switchBlockStatementGroupsopt());
        List<Case> SwitchLabelsopt = ast(ctx.switchLabelsopt());
        SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
        ctx.ast = SwitchBlockStatementGroupsopt;
    }

    /** Production: switchBlockStatementGroupsopt ::= switchBlockStatementGroup* (#switchBlockStatementGroupsopt) */
    @Override
    public void exitSwitchBlockStatementGroupsopt(SwitchBlockStatementGroupsoptContext ctx) {
        List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
        for (SwitchBlockStatementGroupContext e : ctx.switchBlockStatementGroup()) {
            l.addAll(ast(e));
        }
        ctx.ast = l;
    }

    /** Production: switchBlockStatementGroup ::= switchLabels blockStatements (#switchBlockStatementGroup) */
    @Override
    public void exitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {
        List<Case> SwitchLabels = ast(ctx.switchLabels());
        List<Stmt> BlockStatements = ast(ctx.blockStatements());
        List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
        l.addAll(SwitchLabels);
        l.add(nf.SwitchBlock(pos(ctx), BlockStatements));
        ctx.ast = l;
    }

    /** Production: switchLabelsopt ::= switchLabels? (#switchLabelsopt) */
    @Override
    public void exitSwitchLabelsopt(SwitchLabelsoptContext ctx) {
        if (ctx.switchLabels() == null) {
            ctx.ast = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
        } else {
            ctx.ast = ast(ctx.switchLabels());
        }
    }

    /** Production: switchLabels ::= switchLabel+ (#switchLabels) */
    @Override
    public void exitSwitchLabels(SwitchLabelsContext ctx) {
        List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
        for (SwitchLabelContext switchLabel : ctx.switchLabel()) {
            l.add(ast(switchLabel));
        }
        ctx.ast = l;
    }

    /** Production: switchLabel ::= 'case' constantExpression ':' (#switchLabel0) */
    @Override
    public void exitSwitchLabel0(SwitchLabel0Context ctx) {
        Expr ConstantExpression = ast(ctx.constantExpression());
        ctx.ast = nf.Case(pos(ctx), ConstantExpression);
    }

    /** Production: switchLabel ::= 'default' ':' (#switchLabel1) */
    @Override
    public void exitSwitchLabel1(SwitchLabel1Context ctx) {
        ctx.ast = nf.Default(pos(ctx));
    }

    /** Production: whileStatement ::= 'while' '(' expression ')' statement (#whileStatement) */
    @Override
    public void exitWhileStatement(WhileStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.While(pos(ctx), Expression, Statement);
    }

    /** Production: doStatement ::= 'do' statement 'while' '(' expression ')' ';' (#doStatement) */
    @Override
    public void exitDoStatement(DoStatementContext ctx) {
        Stmt Statement = ast(ctx.statement());
        Expr Expression = ast(ctx.expression());
        ctx.ast = nf.Do(pos(ctx), Statement, Expression);
    }

    /** Production: forStatement ::= basicForStatement (#forStatement0) */
    @Override
    public void exitForStatement0(ForStatement0Context ctx) {
        ctx.ast = ast(ctx.basicForStatement());
    }

    /** Production: forStatement ::= enhancedForStatement (#forStatement1) */
    @Override
    public void exitForStatement1(ForStatement1Context ctx) {
        ctx.ast = ast(ctx.enhancedForStatement());
    }

    /** Production: basicForStatement ::= 'for' '(' forInitopt ';' expressionopt ';' forUpdateopt ')' statement (#basicForStatement) */
    @Override
    public void exitBasicForStatement(BasicForStatementContext ctx) {
        @SuppressWarnings("unchecked")
        List<ForInit> ForInitopt = (List<ForInit>) ast(ctx.forInitopt());
        Expr Expressionopt = ast(ctx.expressionopt());
        @SuppressWarnings("unchecked")
        List<ForUpdate> ForUpdateopt = (List<ForUpdate>) ast(ctx.forUpdateopt());
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.For(pos(ctx), ForInitopt, Expressionopt, ForUpdateopt, Statement);
    }

    /** Production: forInit ::= statementExpressionList (#forInit0) */
    @SuppressWarnings("unchecked")
    @Override
    public void exitForInit0(ForInit0Context ctx) {
        ctx.ast = (List<ForInit>) ((Object) ast(ctx.statementExpressionList()));
    }

    /** Production: forInit ::= localVariableDeclaration (#forInit1) */
    @Override
    public void exitForInit1(ForInit1Context ctx) {
        List<LocalDecl> LocalVariableDeclaration = ast(ctx.localVariableDeclaration());
        List<ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
        l.addAll(LocalVariableDeclaration);
        ctx.ast = l;
    }

    /** Production: forUpdate ::= statementExpressionList (#forUpdate) */
    @SuppressWarnings("unchecked")
    @Override
    public void exitForUpdate(ForUpdateContext ctx) {
        ctx.ast = (List<ForUpdate>) ((Object) ast(ctx.statementExpressionList()));
    }

    /** Production: statementExpressionList ::= expression (',' expression)* (#statementExpressionList) */
    @Override
    public void exitStatementExpressionList(StatementExpressionListContext ctx) {
        List<Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
        for (ExpressionContext e : ctx.expression()) {
            l.add(nf.Eval(pos(e), ast(e)));
        }
        ctx.ast = l;
    }

    /** Production: breakStatement ::= 'break' identifieropt ';' (#breakStatement) */
    @Override
    public void exitBreakStatement(BreakStatementContext ctx) {
        Id Identifieropt = ast(ctx.identifieropt());
        ctx.ast = nf.Break(pos(ctx), Identifieropt);
    }

    /** Production: continueStatement ::= 'continue' identifieropt ';' (#continueStatement) */
    @Override
    public void exitContinueStatement(ContinueStatementContext ctx) {
        Id Identifieropt = ast(ctx.identifieropt());
        ctx.ast = nf.Continue(pos(ctx), Identifieropt);
    }

    /** Production: returnStatement ::= 'return' expressionopt ';' (#returnStatement) */
    @Override
    public void exitReturnStatement(ReturnStatementContext ctx) {
        Expr Expressionopt = ast(ctx.expressionopt());
        ctx.ast = nf.Return(pos(ctx), Expressionopt);
    }

    /** Production: throwStatement ::= 'throw' expression ';' (#throwStatement) */
    @Override
    public void exitThrowStatement(ThrowStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        ctx.ast = nf.Throw(pos(ctx), Expression);
    }

    /** Production: tryStatement ::= 'try' block catches (#tryStatement0) */
    @Override
    public void exitTryStatement0(TryStatement0Context ctx) {
        Block Block = ast(ctx.block());
        List<Catch> Catches = ast(ctx.catches());
        ctx.ast = nf.Try(pos(ctx), Block, Catches);
    }

    /** Production: tryStatement ::= 'try' block catchesopt finallyBlock (#tryStatement1) */
    @Override
    public void exitTryStatement1(TryStatement1Context ctx) {
        Block Block = ast(ctx.block());
        List<Catch> Catchesopt = ast(ctx.catchesopt());
        Block Finally = ast(ctx.finallyBlock());
        ctx.ast = nf.Try(pos(ctx), Block, Catchesopt, Finally);
    }

    /** Production: catches ::= catchClause+ (#catches) */
    @Override
    public void exitCatches(CatchesContext ctx) {
        List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
        for (CatchClauseContext CatchClause : ctx.catchClause()) {
            l.add(ast(CatchClause));
        }
        ctx.ast = l;
    }

    /** Production: catchClause ::= 'catch' '(' formalParameter ')' block (#catchClause) */
    @Override
    public void exitCatchClause(CatchClauseContext ctx) {
        X10Formal FormalParameter = ast(ctx.formalParameter());
        Block Block = ast(ctx.block());
        ctx.ast = nf.Catch(pos(ctx), FormalParameter, Block);
    }

    /** Production: finallyBlock ::= 'finally' block (#finallyBlock) */
    @Override
    public void exitFinallyBlock(FinallyBlockContext ctx) {
        Block Block = ast(ctx.block());
        ctx.ast = Block;
    }

    /** Production: clockedClauseopt ::= ('clocked' arguments)? (#clockedClauseopt) */
    @Override
    public void exitClockedClauseopt(ClockedClauseoptContext ctx) {
        List<Expr> Arguments;
        if (ctx.arguments() == null) {
            Arguments = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        } else {
            Arguments = ast(ctx.arguments());
        }
        ctx.ast = Arguments;
    }

    /** Production: asyncStatement ::= 'async' clockedClauseopt statement (#asyncStatement0) */
    @Override
    public void exitAsyncStatement0(AsyncStatement0Context ctx) {
        List<Expr> ClockedClauseopt = ast(ctx.clockedClauseopt());
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.Async(pos(ctx), ClockedClauseopt, Statement);
    }

    /** Production: asyncStatement ::= 'clocked' 'async' statement (#asyncStatement1) */
    @Override
    public void exitAsyncStatement1(AsyncStatement1Context ctx) {
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.Async(pos(ctx), Statement, true);
    }

    /** Production: atStatement ::= 'at' '(' expression ')' statement (#atStatement) */
    @Override
    public void exitAtStatement(AtStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.AtStmt(pos(ctx), Expression, Statement);
    }

    /** Production: atomicStatement ::= 'atomic' statement (#atomicStatement) */
    @Override
    public void exitAtomicStatement(AtomicStatementContext ctx) {
        Stmt Statement = ast(ctx.statement());
        // Position of here might be wrong
        ctx.ast = nf.Atomic(pos(ctx), nf.Here(pos(ctx)), Statement);
    }

    /** Production: whenStatement ::= 'when' '(' expression ')' statement (#whenStatement) */
    @Override
    public void exitWhenStatement(WhenStatementContext ctx) {
        Expr Expression = ast(ctx.expression());
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.When(pos(ctx), Expression, Statement);
    }

    /** Production: atEachStatement ::= 'ateach' '(' loopIndex 'in' expression ')' clockedClauseopt statement (#atEachStatement0) */
    @Override
    public void exitAtEachStatement0(AtEachStatement0Context ctx) {
        Formal LoopIndex = ast(ctx.loopIndex());
        Expr Expression = ast(ctx.expression());
        List<Expr> ClockedClauseopt = ast(ctx.clockedClauseopt());
        Stmt Statement = ast(ctx.statement());
        FlagsNode fn = LoopIndex.flags();
        if (!fn.flags().isFinal()) {
            err.syntaxError("Enhanced ateach loop may not have var loop index. " + LoopIndex, LoopIndex.position());
            fn = fn.flags(fn.flags().Final());
            LoopIndex = LoopIndex.flags(fn);
        }
        ctx.ast = nf.AtEach(pos(ctx), LoopIndex, Expression, ClockedClauseopt, Statement);
    }

    /** Production: atEachStatement ::= 'ateach' '(' expression ')' statement (#atEachStatement1) */
    @Override
    public void exitAtEachStatement1(AtEachStatement1Context ctx) {
        Expr Expression = ast(ctx.expression());
        Stmt Statement = ast(ctx.statement());
        Id name = nf.Id(pos(ctx), Name.makeFresh());
        TypeNode type = nf.UnknownTypeNode(pos(ctx).markCompilerGenerated());
        X10Formal LoopIndex = nf.X10Formal(pos(ctx), nf.FlagsNode(pos(ctx), Flags.FINAL), type, name, null, true);
        TypedList<Expr> ClockedClauseopt = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        ctx.ast = nf.AtEach(pos(ctx), LoopIndex, Expression, ClockedClauseopt, Statement);
    }

    /** Production: enhancedForStatement ::= 'for' '(' loopIndex 'in' expression ')' statement (#enhancedForStatement0) */
    @Override
    public void exitEnhancedForStatement0(EnhancedForStatement0Context ctx) {
        Formal LoopIndex = ast(ctx.loopIndex());
        Expr Expression = ast(ctx.expression());
        Stmt Statement = ast(ctx.statement());
        FlagsNode fn = LoopIndex.flags();
        if (!fn.flags().isFinal()) {
            err.syntaxError("Enhanced for loop may not have var loop index. " + LoopIndex, LoopIndex.position());
            fn = fn.flags(fn.flags().Final());
            LoopIndex = LoopIndex.flags(fn);
        }
        ctx.ast = nf.ForLoop(pos(ctx), LoopIndex, Expression, Statement);
    }

    /** Production: enhancedForStatement ::= 'for' '(' expression ')' statement (#enhancedForStatement1) */
    @Override
    public void exitEnhancedForStatement1(EnhancedForStatement1Context ctx) {
        Expr Expression = ast(ctx.expression());
        Stmt Statement = ast(ctx.statement());
        Id name = nf.Id(pos(ctx), Name.makeFresh());
        TypeNode type = nf.UnknownTypeNode(pos(ctx).markCompilerGenerated());
        Formal LoopIndex = nf.X10Formal(pos(ctx), nf.FlagsNode(pos(ctx), Flags.FINAL), type, name, null, true);
        ctx.ast = nf.ForLoop(pos(ctx), LoopIndex, Expression, Statement);
    }

    /** Production: finishStatement ::= 'finish' statement (#finishStatement0) */
    @Override
    public void exitFinishStatement0(FinishStatement0Context ctx) {
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.Finish(pos(ctx), Statement, false);
    }

    /** Production: finishStatement ::= 'clocked' 'finish' statement (#finishStatement1) */
    @Override
    public void exitFinishStatement1(FinishStatement1Context ctx) {
        Stmt Statement = ast(ctx.statement());
        ctx.ast = nf.Finish(pos(ctx), Statement, true);
    }

    /** Production: castExpression ::= primary (#castExpression0) */
    @Override
    public void exitCastExpression0(CastExpression0Context ctx) {
        ctx.ast = ast(ctx.primary());
    }

    /** Production: castExpression ::= expressionName (#castExpression1) */
    @Override
    public void exitCastExpression1(CastExpression1Context ctx) {
        ParsedName ExpressionName = ast(ctx.expressionName());
        ctx.ast = ExpressionName.toExpr();
    }

    /** Production: castExpression ::= castExpression 'as' type (#castExpression2) */
    @Override
    public void exitCastExpression2(CastExpression2Context ctx) {
        Expr CastExpression = ast(ctx.castExpression());
        TypeNode Type = ast(ctx.type());
        ctx.ast = nf.X10Cast(pos(ctx), Type, CastExpression);
    }

    /** Production: typeParamWithVarianceList ::= typeParameter (#typeParamWithVarianceList0) */
    @Override
    public void exitTypeParamWithVarianceList0(TypeParamWithVarianceList0Context ctx) {
        TypeParamNode TypeParameter = ast(ctx.typeParameter());
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(TypeParameter);
        ctx.ast = l;
    }

    /** Production: typeParamWithVarianceList ::= oBSOLETE_TypeParamWithVariance (#typeParamWithVarianceList1) */
    @Override
    public void exitTypeParamWithVarianceList1(TypeParamWithVarianceList1Context ctx) {
        TypeParamNode OBSOLETE_TypeParamWithVariance = ast(ctx.oBSOLETE_TypeParamWithVariance());
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(OBSOLETE_TypeParamWithVariance);
        ctx.ast = l;
    }

    /** Production: typeParamWithVarianceList ::= typeParamWithVarianceList ',' typeParameter (#typeParamWithVarianceList2) */
    @Override
    public void exitTypeParamWithVarianceList2(TypeParamWithVarianceList2Context ctx) {
        List<TypeParamNode> TypeParamWithVarianceList = ast(ctx.typeParamWithVarianceList());
        TypeParamNode TypeParameter = ast(ctx.typeParameter());
        TypeParamWithVarianceList.add(TypeParameter);
        ctx.ast = TypeParamWithVarianceList;
    }

    /** Production: typeParamWithVarianceList ::= typeParamWithVarianceList ',' oBSOLETE_TypeParamWithVariance (#typeParamWithVarianceList3) */
    @Override
    public void exitTypeParamWithVarianceList3(TypeParamWithVarianceList3Context ctx) {
        List<TypeParamNode> TypeParamWithVarianceList = ast(ctx.typeParamWithVarianceList());
        TypeParamNode OBSOLETE_TypeParamWithVariance = ast(ctx.oBSOLETE_TypeParamWithVariance());
        TypeParamWithVarianceList.add(OBSOLETE_TypeParamWithVariance);
        ctx.ast = TypeParamWithVarianceList;
    }

    /** Production: typeParameterList ::= typeParameter (',' typeParameter)* (#typeParameterList) */
    @Override
    public void exitTypeParameterList(TypeParameterListContext ctx) {
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        for (TypeParameterContext TypeParameter : ctx.typeParameter()) {
            l.add(ast(TypeParameter));
        }
        ctx.ast = l;
    }

    /** Production: oBSOLETE_TypeParamWithVariance ::= '+' typeParameter (#oBSOLETE_TypeParamWithVariance0) */
    @Override
    public void exitOBSOLETE_TypeParamWithVariance0(OBSOLETE_TypeParamWithVariance0Context ctx) {
        TypeParamNode TypeParameter = ast(ctx.typeParameter());
        err.syntaxError("Covariance is no longer supported.", pos(ctx));
        ctx.ast = (TypeParamNode) TypeParameter.variance(ParameterType.Variance.COVARIANT).position(pos(ctx));
    }

    /** Production: oBSOLETE_TypeParamWithVariance ::= '-' typeParameter (#oBSOLETE_TypeParamWithVariance1) */
    @Override
    public void exitOBSOLETE_TypeParamWithVariance1(OBSOLETE_TypeParamWithVariance1Context ctx) {
        TypeParamNode TypeParameter = ast(ctx.typeParameter());
        err.syntaxError("Contravariance is no longer supported.", pos(ctx));
        ctx.ast = (TypeParamNode) TypeParameter.variance(ParameterType.Variance.CONTRAVARIANT).position(pos(ctx));
    }

    /** Production: typeParameter ::= identifier (#typeParameter) */
    @Override
    public void exitTypeParameter(TypeParameterContext ctx) {
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.TypeParamNode(pos(ctx), Identifier);
    }


    /** Production: closureExpression ::= formalParameters whereClauseopt hasResultTypeopt oBSOLETE_Offersopt '=>' closureBody (#closureExpression) */
    @Override
    public void exitClosureExpression(ClosureExpressionContext ctx) {
        List<Formal> FormalParameters = ast(ctx.formalParameters());
        DepParameterExpr WhereClauseopt = ast(ctx.whereClauseopt());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        TypeNode HasResultType = HasResultTypeopt == null ? nf.UnknownTypeNode(Position.COMPILER_GENERATED) : HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = ast(ctx.oBSOLETE_Offersopt());
        Block ClosureBody = ast(ctx.closureBody());
        ctx.ast = nf.Closure(pos(ctx), FormalParameters, WhereClauseopt, HasResultType, ClosureBody);
    }

    /** Production: lastExpression ::= expression (#lastExpression) */
    @Override
    public void exitLastExpression(LastExpressionContext ctx) {
        Expr Expression = ast(ctx.expression());
        ctx.ast = nf.X10Return(pos(ctx), Expression, true);
    }

    /** Production: closureBody ::= expression (#closureBody0) */
    @Override
    public void exitClosureBody0(ClosureBody0Context ctx) {
        Expr ConditionalExpression = ast(ctx.expression());
        ctx.ast = nf.Block(pos(ctx), nf.X10Return(pos(ctx), ConditionalExpression, true));
    }

    /** Production: closureBody ::= annotationsopt '{' blockInteriorStatement* lastExpression '}' (#closureBody1) */
    @Override
    public void exitClosureBody1(ClosureBody1Context ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        Stmt LastExpression = ast(ctx.lastExpression());
        List<Stmt> l = new ArrayList<Stmt>();
        for (BlockInteriorStatementContext blockInteriorStatement : ctx.blockInteriorStatement()) {
            l.addAll(ast(blockInteriorStatement));
        }
        l.add(LastExpression);
        Block b = nf.Block(pos(ctx), l);
        b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
        ctx.ast = b;
    }

    /** Production: closureBody ::= annotationsopt block (#closureBody2) */
    @Override
    public void exitClosureBody2(ClosureBody2Context ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        Block Block = ast(ctx.block());
        Block b = Block;
        b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
        ctx.ast = (polyglot.ast.Block) b.position(pos(ctx));
    }

    /** Production: atExpression ::= annotationsopt 'at' '(' expression ')' closureBody (#atExpression) */
    @Override
    public void exitAtExpression(AtExpressionContext ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        Expr Expression = ast(ctx.expression());
        Block ClosureBody = ast(ctx.closureBody());
        AtExpr r = nf.AtExpr(pos(ctx), Expression, ClosureBody);
        r = (AtExpr) ((X10Ext) r.ext()).annotations(Annotationsopt);
        ctx.ast = r;
    }

    /** Production: oBSOLETE_FinishExpression ::= 'finish' '(' expression ')' block (#oBSOLETE_FinishExpression) */
    @Override
    public void exitOBSOLETE_FinishExpression(OBSOLETE_FinishExpressionContext ctx) {
        Expr Expression = ast(ctx.expression());
        Block Block = ast(ctx.block());
        ctx.ast = nf.FinishExpr(pos(ctx), Expression, Block);
    }

    /** Production: typeName ::= identifier ('.' identifier)* (#typeName) */
    @Override
    public void exitTypeName(TypeNameContext ctx) {
        List<IdentifierContext> identifiers = ctx.identifier();
        ctx.ast = toParsedName(identifiers);
    }

    /** Production: className ::= typeName (#className) */
    @Override
    public void exitClassName(ClassNameContext ctx) {
        ctx.ast = ast(ctx.typeName());
    }

    /** Production: typeArguments ::= '[' type (',' type)* ']' (#typeArguments) */
    @Override
    public void exitTypeArguments(TypeArgumentsContext ctx) {
        List<TypeNode> l = new ArrayList<TypeNode>();
        for (TypeContext Type : ctx.type()) {
            l.add(ast(Type));
        }
        ctx.ast = l;
    }

    /** Production: packageName ::= identifier ('.' identifier)* (#packageName) */
    @Override
    public void exitPackageName(PackageNameContext ctx) {
        List<IdentifierContext> identifiers = ctx.identifier();
        ctx.ast = toParsedName(identifiers);
    }

    /** Production: expressionName ::= identifier ('.' identifier)* (#expressionName) */
    @Override
    public void exitExpressionName(ExpressionNameContext ctx) {
        List<IdentifierContext> identifiers = ctx.identifier();
        ctx.ast = toParsedName(identifiers);
    }

    /** Production: methodName ::= identifier ('.' identifier)* (#methodName) */
    @Override
    public void exitMethodName(MethodNameContext ctx) {
        List<IdentifierContext> identifiers = ctx.identifier();
        ctx.ast = toParsedName(identifiers);
    }

    /** Production: packageOrTypeName ::= identifier ('.' identifier)* (#packageOrTypeName) */
    @Override
    public void exitPackageOrTypeName(PackageOrTypeNameContext ctx) {
        List<IdentifierContext> identifiers = ctx.identifier();
        ctx.ast = toParsedName(identifiers);
    }

    /** Production: fullyQualifiedName ::= identifier ('.' identifier)* (#fullyQualifiedName) */
    @Override
    public void exitFullyQualifiedName(FullyQualifiedNameContext ctx) {
        List<IdentifierContext> identifiers = ctx.identifier();
        ctx.ast = toParsedName(identifiers);
    }

    /** Production: compilationUnit ::= packageDeclaration? importDeclarationsopt typeDeclarationsopt (#compilationUnit) */
    @Override
    public void exitCompilationUnit(CompilationUnitContext ctx) {
        List<Import> importDeclarationsopt = ast(ctx.importDeclarationsopt());
        List<TopLevelDecl> typeDeclarationsopt = ast(ctx.typeDeclarationsopt());

        PackageNode packageDeclaration = ctx.packageDeclaration() == null ? null : ast(ctx.packageDeclaration());
        ctx.ast = nf.SourceFile(pos(ctx), packageDeclaration, importDeclarationsopt, typeDeclarationsopt);

    }

    /** Production: packageDeclaration ::= annotationsopt 'package' packageName ';' (#packageDeclaration) */
    @Override
    public void exitPackageDeclaration(PackageDeclarationContext ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        ParsedName PackageName = ast(ctx.packageName());
        PackageNode pn = PackageName.toPackage();
        pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
        ctx.ast = pn;
    }

    /** Production: importDeclarationsopt ::= importDeclaration* (#importDeclarationsopt) */
    @Override
    public void exitImportDeclarationsopt(ImportDeclarationsoptContext ctx) {
        List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
        for (ImportDeclarationContext importDeclaration : ctx.importDeclaration()) {
            l.add(ast(importDeclaration));
        }
        ctx.ast = l;
    }

    /** Production: importDeclaration ::= 'import' typeName ';' (#importDeclaration0) */
    @Override
    public void exitImportDeclaration0(ImportDeclaration0Context ctx) {
        ParsedName TypeName = ast(ctx.typeName());
        ctx.ast = nf.Import(pos(ctx), Import.CLASS, QName.make(TypeName.toString()));
    }

    /** Production: importDeclaration ::= 'import' packageOrTypeName '.' '*' ';' (#importDeclaration1) */
    @Override
    public void exitImportDeclaration1(ImportDeclaration1Context ctx) {
        ParsedName PackageOrTypeName = ast(ctx.packageOrTypeName());
        ctx.ast = nf.Import(pos(ctx), Import.PACKAGE, QName.make(PackageOrTypeName.toString()));
    }

    /** Production: typeDeclarationsopt ::= typeDeclaration* (#typeDeclarationsopt) */
    @Override
    public void exitTypeDeclarationsopt(TypeDeclarationsoptContext ctx) {
        List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
        for (TypeDeclarationContext typeDecl : ctx.typeDeclaration()) {
            l.add(ast(typeDecl));
        }
        ctx.ast = l;
    }

    /** Production: typeDeclaration ::= classDeclaration (#typeDeclaration0) */
    @Override
    public void exitTypeDeclaration0(TypeDeclaration0Context ctx) {
        ctx.ast = ast(ctx.classDeclaration());
    }

    /** Production: typeDeclaration ::= structDeclaration (#typeDeclaration1) */
    @Override
    public void exitTypeDeclaration1(TypeDeclaration1Context ctx) {
        ctx.ast = ast(ctx.structDeclaration());
    }

    @Override
    public void exitTypeDeclaration2(TypeDeclaration2Context ctx) {
        ctx.ast = ast(ctx.interfaceDeclaration());
    }

    /** Production: typeDeclaration ::= interfaceDeclaration (#typeDeclaration3) */
    @Override
    public void exitTypeDeclaration3(TypeDeclaration3Context ctx) {
        ctx.ast = ast(ctx.typeDefDeclaration());
    }

    /** Production: typeDeclaration ::= ';' (#typeDeclaration4) */
    @Override
    public void exitTypeDeclaration4(TypeDeclaration4Context ctx) {
        ctx.ast = null;
    }

    /** Production: interfacesopt ::= ('implements' type (',' type)*)? (#interfacesopt) */
    @Override
    public void exitInterfacesopt(InterfacesoptContext ctx) {
        List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        for (TypeContext Type : ctx.type()) {
            l.add(ast(Type));
        }
        ctx.ast = l;
    }

    /** Production: classBody ::= '{' classMemberDeclarationsopt '}' (#classBody) */
    @Override
    public void exitClassBody(ClassBodyContext ctx) {
        List<ClassMember> ClassMemberDeclarationsopt = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (ClassMemberDeclarationContext ClassMember : ctx.classMemberDeclaration()) {
            ClassMemberDeclarationsopt.addAll(ast(ClassMember));
        }
        ctx.ast = nf.ClassBody(pos(ctx), ClassMemberDeclarationsopt);
    }

    /** Production: classMemberDeclaration ::= interfaceMemberDeclaration (#classMemberDeclaration0) */
    @Override
    public void exitClassMemberDeclaration0(ClassMemberDeclaration0Context ctx) {
        ctx.ast = ast(ctx.interfaceMemberDeclaration());
    }

    /** Production: classMemberDeclaration ::= constructorDeclaration (#classMemberDeclaration1) */
    @Override
    public void exitClassMemberDeclaration1(ClassMemberDeclaration1Context ctx) {
        ConstructorDecl ConstructorDeclaration = ast(ctx.constructorDeclaration());
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(ConstructorDeclaration);
        ctx.ast = l;
    }

    /** Production: formalDeclarators ::= formalDeclarator (',' formalDeclarator)* (#formalDeclarators) */
    @Override
    public void exitFormalDeclarators(FormalDeclaratorsContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (FormalDeclaratorContext FormalDeclarator : ctx.formalDeclarator()) {
            l.add(ast(FormalDeclarator));
        }
        ctx.ast = l;
    }

    /** Production: fieldDeclarators ::= fieldDeclarator (',' fieldDeclarator)* (#fieldDeclarators) */
    @Override
    public void exitFieldDeclarators(FieldDeclaratorsContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (FieldDeclaratorContext FieldDeclarator : ctx.fieldDeclarator()) {
            l.add(ast(FieldDeclarator));
        }
        ctx.ast = l;
    }

    /** Production: variableDeclaratorsWithType ::= variableDeclaratorWithType (',' variableDeclaratorWithType)* (#variableDeclaratorsWithType) */
    @Override
    public void exitVariableDeclaratorsWithType(VariableDeclaratorsWithTypeContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (VariableDeclaratorWithTypeContext VariableDeclaratorWithType : ctx.variableDeclaratorWithType()) {
            l.add(ast(VariableDeclaratorWithType));
        }
        ctx.ast = l;
    }

    /** Production: variableDeclarators ::= variableDeclarator (',' variableDeclarator)* (#variableDeclarators) */
    @Override
    public void exitVariableDeclarators(VariableDeclaratorsContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (VariableDeclaratorContext VariableDeclarator : ctx.variableDeclarator()) {
            l.add(ast(VariableDeclarator));
        }
        ctx.ast = l;
    }

    /** Production: variableInitializer ::= expression (#variableInitializer) */
    @Override
    public void exitVariableInitializer(VariableInitializerContext ctx) {
        ctx.ast = ast(ctx.expression());
    }

    /** Production: resultType ::= ':' type (#resultType) */
    @Override
    public void exitResultType(ResultTypeContext ctx) {
        ctx.ast = ast(ctx.type());
    }

    /** Production: hasResultType ::= resultType (#hasResultType0) */
    @Override
    public void exitHasResultType0(HasResultType0Context ctx) {
        TypeNode Type = ast(ctx.resultType());
        ctx.ast = Type;
    }

    /** Production: hasResultType ::= '<:' type (#hasResultType1) */
    @Override
    public void exitHasResultType1(HasResultType1Context ctx) {
        TypeNode Type = ast(ctx.type());
        ctx.ast = nf.HasType(Type);
    }

    /** Production: formalParameterList ::= formalParameter (',' formalParameter)* (#formalParameterList) */
    @Override
    public void exitFormalParameterList(FormalParameterListContext ctx) {
        List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
        for (FormalParameterContext FormalParameter : ctx.formalParameter()) {
            l.add(ast(FormalParameter));
        }
        ctx.ast = l;
    }

    /** Production: loopIndexDeclarator ::= identifier hasResultTypeopt (#loopIndexDeclarator0) */
    @Override
    public void exitLoopIndexDeclarator0(LoopIndexDeclarator0Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, null };
    }

    /** Production: loopIndexDeclarator ::= '[' identifierList ']' hasResultTypeopt (#loopIndexDeclarator1) */
    @Override
    public void exitLoopIndexDeclarator1(LoopIndexDeclarator1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, null };
    }

    /** Production: loopIndexDeclarator ::= identifier '[' identifierList ']' hasResultTypeopt (#loopIndexDeclarator2) */
    @Override
    public void exitLoopIndexDeclarator2(LoopIndexDeclarator2Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, null };
    }

    TypeNode explodedType(Position p) {
        // exploded formals/locals are either Int or T (when exploding Array[T]).
        // nf.TypeNodeFromQualifiedName(p,QName.make("x10.lang.Int"));
        return nf.UnknownTypeNode(p);

    }

    List<Formal> createExplodedFormals(List<Id> exploded) {
        List<Formal> explodedFormals = new ArrayList<Formal>();
        for (Id id : exploded) {
            // exploded formals are always final (VAL)
            explodedFormals.add(nf.Formal(id.position(), nf.FlagsNode(id.position(), Flags.FINAL), explodedType(id.position()), id));
        }
        return explodedFormals;
    }

    /** Production: loopIndex ::= modifiersopt loopIndexDeclarator (#loopIndex0) */
    @Override
    public void exitLoopIndex0(LoopIndex0Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        Object[] LoopIndexDeclarator = ast(ctx.loopIndexDeclarator());
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
        Object[] o = LoopIndexDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos, Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    /** Production: loopIndex ::= modifiersopt varKeyword loopIndexDeclarator (#loopIndex1) */
    @Override
    public void exitLoopIndex1(LoopIndex1Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<FlagsNode> VarKeyword = ast(ctx.varKeyword());
        Object[] LoopIndexDeclarator = ast(ctx.loopIndexDeclarator());
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, VarKeyword);
        Object[] o = LoopIndexDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos, Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    /** Production: formalParameter ::= modifiersopt formalDeclarator (#formalParameter0) */
    @Override
    public void exitFormalParameter0(FormalParameter0Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        Object[] FormalDeclarator = ast(ctx.formalDeclarator());
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
        Object[] o = FormalDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos.markCompilerGenerated(), Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    /** Production: formalParameter ::= modifiersopt varKeyword formalDeclarator (#formalParameter1) */
    @Override
    public void exitFormalParameter1(FormalParameter1Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<FlagsNode> VarKeyword = ast(ctx.varKeyword());
        Object[] FormalDeclarator = ast(ctx.formalDeclarator());
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, VarKeyword);
        Object[] o = FormalDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos.markCompilerGenerated(), Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    /** Production: formalParameter ::= type (#formalParameter2) */
    @Override
    public void exitFormalParameter2(FormalParameter2Context ctx) {
        TypeNode Type = ast(ctx.type());
        X10Formal f;
        FlagsNode fn = nf.FlagsNode(pos(ctx).markCompilerGenerated(), Flags.FINAL);
        Id name = nf.Id(pos(ctx).markCompilerGenerated(), Name.makeFresh("id"));
        List<Formal> explodedFormals = Collections.<Formal> emptyList();
        boolean unnamed = true;
        f = nf.X10Formal(pos(ctx).markCompilerGenerated(), fn, Type, name, explodedFormals, unnamed);
        ctx.ast = f;
    }

    /** Production: oBSOLETE_Offersopt ::= ('offers' type)? (#oBSOLETE_Offersopt) */
    @Override
    public void exitOBSOLETE_Offersopt(OBSOLETE_OffersoptContext ctx) {
        TypeNode Type = ctx.type() == null ? null : ast(ctx.type());
        ctx.ast = Type;
    }

    /** Production: throwsopt ::= ('throws' type (',' type)*)? (#throwsopt) */
    @Override
    public void exitThrowsopt(ThrowsoptContext ctx) {
        List<TypeNode> throwsList = new ArrayList<TypeNode>();
        for (TypeContext type : ctx.type()) {
            throwsList.add(ast(type));
        }
        ctx.ast = throwsList;
    }

    /** Production: methodBody ::= '=' lastExpression ';' (#methodBody0) */
    @Override
    public void exitMethodBody0(MethodBody0Context ctx) {
        Stmt LastExpression = ast(ctx.lastExpression());
        ctx.ast = nf.Block(pos(ctx), LastExpression);
    }

    /** Production: methodBody ::= annotationsopt block (#methodBody2) */
    @Override
    public void exitMethodBody2(MethodBody2Context ctx) {
        List<AnnotationNode> Annotationsopt = ast(ctx.annotationsopt());
        Block Block = ast(ctx.block());
        ctx.ast = (Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos(ctx));
    }

    /** Production: methodBody ::= ';' (#methodBody3) */
    @Override
    public void exitMethodBody3(MethodBody3Context ctx) {
        ctx.ast = null;
    }

    /** Production: constructorBody ::= constructorBlock (#constructorBody0) */
    @Override
    public void exitConstructorBody0(ConstructorBody0Context ctx) {
        Block ConstructorBlock = ast(ctx.constructorBlock());
        ctx.ast = ConstructorBlock;
    }

    /** Production: constructorBody ::= '=' explicitConstructorInvocation (#constructorBody1) */
    @Override
    public void exitConstructorBody1(ConstructorBody1Context ctx) {
        ConstructorCall ExplicitConstructorInvocation = ast(ctx.explicitConstructorInvocation());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(ExplicitConstructorInvocation);
        ctx.ast = nf.Block(pos(ctx), l);
    }

    /** Production: constructorBody ::= '=' assignPropertyCall (#constructorBody2) */
    @Override
    public void exitConstructorBody2(ConstructorBody2Context ctx) {
        Stmt AssignPropertyCall = ast(ctx.assignPropertyCall());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(AssignPropertyCall);
        ctx.ast = nf.Block(pos(ctx), l);
    }

    /** Production: constructorBody ::= ';' (#constructorBody3) */
    @Override
    public void exitConstructorBody3(ConstructorBody3Context ctx) {
        ctx.ast = null;
    }

    /** Production: constructorBlock ::= '{' explicitConstructorInvocation? blockStatementsopt '}' (#constructorBlock) */
    @Override
    public void exitConstructorBlock(ConstructorBlockContext ctx) {
        Stmt ExplicitConstructorInvocationopt = ctx.explicitConstructorInvocation() == null ? null : ast(ctx.explicitConstructorInvocation());
        List<Stmt> BlockStatementsopt = ast(ctx.blockStatementsopt());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        if (ExplicitConstructorInvocationopt != null) {
            l.add(ExplicitConstructorInvocationopt);
        }
        l.addAll(BlockStatementsopt);
        ctx.ast = nf.Block(pos(ctx), l);
    }

    /** Production: arguments ::= '(' argumentList ')' (#arguments) */
    @Override
    public void exitArguments(ArgumentsContext ctx) {
        ctx.ast = ast(ctx.argumentList());
    }

    /** Production: extendsInterfacesopt ::= ('extends' type (',' type)*)? (#extendsInterfacesopt) */
    @Override
    public void exitExtendsInterfacesopt(ExtendsInterfacesoptContext ctx) {
        List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        for (TypeContext type : ctx.type()) {
            l.add(ast(type));
        }
        ctx.ast = l;
    }

    /** Production: interfaceBody ::= '{' interfaceMemberDeclarationsopt '}' (#interfaceBody) */
    @Override
    public void exitInterfaceBody(InterfaceBodyContext ctx) {
        List<ClassMember> InterfaceMemberDeclarationsopt = ast(ctx.interfaceMemberDeclarationsopt());
        ctx.ast = nf.ClassBody(pos(ctx), InterfaceMemberDeclarationsopt);
    }

    /** Production: interfaceMemberDeclarationsopt ::= interfaceMemberDeclaration* (#interfaceMemberDeclarationsopt) */
    @Override
    public void exitInterfaceMemberDeclarationsopt(InterfaceMemberDeclarationsoptContext ctx) {
        TypedList<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (InterfaceMemberDeclarationContext decl : ctx.interfaceMemberDeclaration()) {
            l.addAll(ast(decl));
        }
        ctx.ast = l;
    }

    /** Production: interfaceMemberDeclaration ::= methodDeclaration (#interfaceMemberDeclaration0) */
    @Override
    public void exitInterfaceMemberDeclaration0(InterfaceMemberDeclaration0Context ctx) {
        ClassMember MethodDeclaration = ast(ctx.methodDeclaration());
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(MethodDeclaration);
        ctx.ast = l;
    }

    /** Production: interfaceMemberDeclaration ::= fieldDeclaration (#interfaceMemberDeclaration2) */
    @Override
    public void exitInterfaceMemberDeclaration1(InterfaceMemberDeclaration1Context ctx) {
        ClassMember PropertyMethodDeclaration = ast(ctx.propertyMethodDeclaration());
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(PropertyMethodDeclaration);
        ctx.ast = l;
    }

    /** Production: interfaceMemberDeclaration ::= propertyMethodDeclaration (#interfaceMemberDeclaration1) */
    @Override
    public void exitInterfaceMemberDeclaration2(InterfaceMemberDeclaration2Context ctx) {
        List<ClassMember> FieldDeclaration = ast(ctx.fieldDeclaration());
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.addAll(FieldDeclaration);
        ctx.ast = l;
    }

    /** Production: interfaceMemberDeclaration ::= typeDeclaration (#interfaceMemberDeclaration3) */
    @Override
    public void exitInterfaceMemberDeclaration3(InterfaceMemberDeclaration3Context ctx) {
        ClassMember TypeDeclaration = (ClassMember) ast(ctx.typeDeclaration());
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        if (TypeDeclaration != null) {
            l.add(TypeDeclaration);
        }
        ctx.ast = l;
    }

    /** Production: annotationsopt ::= annotations? (#annotationsopt) */
    @Override
    public void exitAnnotationsopt(AnnotationsoptContext ctx) {
        List<AnnotationNode> l;
        if (ctx.annotations() == null) {
            l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
        } else {
            l = ast(ctx.annotations());
        }
        ctx.ast = l;
    }

    /** Production: annotations ::= annotation+ (#annotations) */
    @Override
    public void exitAnnotations(AnnotationsContext ctx) {
        List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
        for (AnnotationContext annotation : ctx.annotation()) {
            l.add(ast(annotation));
        }
        ctx.ast = l;
    }

    /** Production: annotation ::= '@' namedTypeNoConstraints (#annotation) */
    @Override
    public void exitAnnotation(AnnotationContext ctx) {
        TypeNode NamedTypeNoConstraints = ast(ctx.namedTypeNoConstraints());
        ctx.ast = nf.AnnotationNode(pos(ctx), NamedTypeNoConstraints);
    }

    /** Production: identifier ::= IDENTIFIER (#identifier) */
    @Override
    public void exitIdentifier(IdentifierContext ctx) {
        ctx.ast = nf.Id(pos(ctx), ctx.start.getText());
    }


    /** Production: block ::= '{' blockStatementsopt '}' (#block) */
    @Override
    public void exitBlock(BlockContext ctx) {
        List<Stmt> BlockStatementsopt = ast(ctx.blockStatementsopt());
        ctx.ast = nf.Block(pos(ctx), BlockStatementsopt);
    }

    /** Production: blockStatements ::= blockInteriorStatement+ (#blockStatements) */
    @Override
    public void exitBlockStatements(BlockStatementsContext ctx) {
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        for (BlockInteriorStatementContext blockInteriorStatement : ctx.blockInteriorStatement()) {
            l.addAll(ast(blockInteriorStatement));
        }
        ctx.ast = l;
    }

    /** Production: blockInteriorStatement ::= localVariableDeclarationStatement (#blockInteriorStatement0) */
    @Override
    public void exitBlockInteriorStatement0(BlockInteriorStatement0Context ctx) {
        ctx.ast = ast(ctx.localVariableDeclarationStatement());
    }

    /** Production: blockInteriorStatement ::= classDeclaration (#blockInteriorStatement1) */
    @Override
    public void exitBlockInteriorStatement1(BlockInteriorStatement1Context ctx) {
        ClassDecl ClassDeclaration = ast(ctx.classDeclaration());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalClassDecl(pos(ctx), ClassDeclaration));
        ctx.ast = l;
    }

    /** Production: blockInteriorStatement ::= structDeclaration (#blockInteriorStatement2) */
    @Override
    public void exitBlockInteriorStatement2(BlockInteriorStatement2Context ctx) {
        ClassDecl StructDeclaration = ast(ctx.structDeclaration());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalClassDecl(pos(ctx), StructDeclaration));
        ctx.ast = l;
    }

    /** Production: blockInteriorStatement ::= typeDefDeclaration (#blockInteriorStatement3) */
    @Override
    public void exitBlockInteriorStatement3(BlockInteriorStatement3Context ctx) {
        TypeDecl TypeDefDeclaration = ast(ctx.typeDefDeclaration());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalTypeDef(pos(ctx), TypeDefDeclaration));
        ctx.ast = l;
    }

    /** Production: blockInteriorStatement ::= statement (#blockInteriorStatement4) */
    @Override
    public void exitBlockInteriorStatement4(BlockInteriorStatement4Context ctx) {
        Stmt Statement = ast(ctx.statement());
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(Statement);
        ctx.ast = l;
    }

    /** Production: identifierList ::= identifier (',' identifier)* (#identifierList) */
    @Override
    public void exitIdentifierList(IdentifierListContext ctx) {
        List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
        for (IdentifierContext identifier : ctx.identifier()) {
            l.add(ast(identifier));
        }
        ctx.ast = l;
    }

    /** Production: formalDeclarator ::= identifier resultType (#formalDeclarator0) */
    @Override
    public void exitFormalDeclarator0(FormalDeclarator0Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode ResultType = ast(ctx.resultType());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, ResultType, null };
    }

    /** Production: formalDeclarator ::= '[' identifierList ']' resultType (#formalDeclarator1) */
    @Override
    public void exitFormalDeclarator1(FormalDeclarator1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode ResultType = ast(ctx.resultType());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, ResultType, null };
    }

    /** Production: formalDeclarator ::= identifier '[' identifierList ']' resultType (#formalDeclarator2) */
    @Override
    public void exitFormalDeclarator2(FormalDeclarator2Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode ResultType = ast(ctx.resultType());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, ResultType, null };
    }

    /** Production: fieldDeclarator ::= identifier hasResultType (#fieldDeclarator0) */
    @Override
    public void exitFieldDeclarator0(FieldDeclarator0Context ctx) {
        Id Identifier = ast(ctx.identifier());
        TypeNode HasResultTypeopt = (TypeNode) ast(ctx.hasResultType());
        Expr VariableInitializer = null;
        ctx.ast = new Object[] { pos(ctx), Identifier, Collections.<Id> emptyList(), HasResultTypeopt, VariableInitializer };
    }

    /** Production: fieldDeclarator ::= identifier hasResultTypeopt '=' variableInitializer (#fieldDeclarator1) */
    @Override
    public void exitFieldDeclarator1(FieldDeclarator1Context ctx) {
        Id Identifier = ast(ctx.identifier());
        TypeNode HasResultTypeopt = (TypeNode) ast(ctx.hasResultTypeopt());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, Collections.<Id> emptyList(), HasResultTypeopt, VariableInitializer };
    }

    /** Production: variableDeclarator ::= identifier hasResultTypeopt '=' variableInitializer (#variableDeclarator0) */
    @Override
    public void exitVariableDeclarator0(VariableDeclarator0Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    /** Production: variableDeclarator ::= '[' identifierList ']' hasResultTypeopt '=' variableInitializer (#variableDeclarator1) */
    @Override
    public void exitVariableDeclarator1(VariableDeclarator1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    /** Production: variableDeclarator ::= identifier '[' identifierList ']' hasResultTypeopt '=' variableInitializer (#variableDeclarator2) */
    @Override
    public void exitVariableDeclarator2(VariableDeclarator2Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode HasResultTypeopt = ast(ctx.hasResultTypeopt());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    /** Production: variableDeclaratorWithType ::= identifier hasResultType '=' variableInitializer (#variableDeclaratorWithType0) */
    @Override
    public void exitVariableDeclaratorWithType0(VariableDeclaratorWithType0Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode HasResultTypeopt = ast(ctx.hasResultType());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    /** Production: variableDeclaratorWithType ::= '[' identifierList ']' hasResultType '=' variableInitializer (#variableDeclaratorWithType1) */
    @Override
    public void exitVariableDeclaratorWithType1(VariableDeclaratorWithType1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode HasResultTypeopt = ast(ctx.hasResultType());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    /** Production: variableDeclaratorWithType ::= identifier '[' identifierList ']' hasResultType '=' variableInitializer (#variableDeclaratorWithType2) */
    @Override
    public void exitVariableDeclaratorWithType2(VariableDeclaratorWithType2Context ctx) {
        Id Identifier = ast(ctx.identifier());
        List<Id> IdentifierList = ast(ctx.identifierList());
        TypeNode HasResultTypeopt = ast(ctx.hasResultType());
        Expr VariableInitializer = ast(ctx.variableInitializer());
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    /** Production: localVariableDeclarationStatement ::= localVariableDeclaration ';' (#localVariableDeclarationStatement) */
    @SuppressWarnings("unchecked")
    @Override
    public void exitLocalVariableDeclarationStatement(LocalVariableDeclarationStatementContext ctx) {
        // Check if this cast if correct
        ctx.ast = (List<Stmt>) ((Object) ast(ctx.localVariableDeclaration()));
    }

    List<LocalDecl> localVariableDeclaration(List<Modifier> Modifiersopt, List<FlagsNode> VarKeyword, List<Object[]> VariableDeclarators) {
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        FlagsNode fn = VarKeyword == null ? extractFlags(modifiers, Flags.FINAL) : extractFlags(modifiers, VarKeyword);
        List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
        for (Object[] o : VariableDeclarators) {
            Position pos = (Position) o[0];
            Position compilerGen = pos.markCompilerGenerated();
            Id name = (Id) o[1];
            if (name == null)
                name = nf.Id(pos, Name.makeFresh());
            @SuppressWarnings("unchecked")
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null)
                type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
            Expr init = (Expr) o[5];
            LocalDecl ld = nf.LocalDecl(pos, fn, type, name, init, exploded);
            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
            int index = 0;
            l.add(ld);
            if (exploded.size() > 0 && init == null) {
                err.syntaxError("An exploded point must have an initializer.", pos);
            }
            FlagsNode efn = extractFlags(modifiers, Flags.FINAL); // exploded vars are always final
            for (Id id : exploded) {
                TypeNode tni = init == null ? nf.CanonicalTypeNode(compilerGen, ts.Int()) : // we infer the type of the exploded components, however if there is no init, then we
                                                                                            // just assume Int to avoid cascading errors.
                        explodedType(id.position()); // UnknownType
                l.add(nf.LocalDecl(id.position(), efn, tni, id,
                        init != null ? nf.ClosureCall(compilerGen, nf.Local(compilerGen, name), Collections.<Expr> singletonList(nf.IntLit(compilerGen, IntLit.INT, index))) : null));
                index++;
            }
        }
        return l;
    }

    /** Production: localVariableDeclaration ::= modifiersopt varKeyword variableDeclarators (#localVariableDeclaration0) */
    @Override
    public void exitLocalVariableDeclaration0(LocalVariableDeclaration0Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<FlagsNode> VarKeyword = ast(ctx.varKeyword());
        List<Object[]> VariableDeclarators = ast(ctx.variableDeclarators());
        List<LocalDecl> decls = localVariableDeclaration(Modifiersopt, VarKeyword, VariableDeclarators);
        if (decls.size() == 1) {
            LocalDecl decl = (LocalDecl) decls.get(0).position(pos(ctx));
            decls = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
            decls.add(decl);
        }
        ctx.ast = decls;
    }

    /** Production: localVariableDeclaration ::= modifiersopt variableDeclaratorsWithType (#localVariableDeclaration1) */
    @Override
    public void exitLocalVariableDeclaration1(LocalVariableDeclaration1Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<FlagsNode> VarKeyword = null;
        List<Object[]> VariableDeclarators = ast(ctx.variableDeclaratorsWithType());
        ctx.ast = localVariableDeclaration(Modifiersopt, VarKeyword, VariableDeclarators);
    }

    /** Production: localVariableDeclaration ::= modifiersopt varKeyword formalDeclarators (#localVariableDeclaration2) */
    @Override
    public void exitLocalVariableDeclaration2(LocalVariableDeclaration2Context ctx) {
        List<Modifier> Modifiersopt = ast(ctx.modifiersopt());
        List<FlagsNode> VarKeyword = ast(ctx.varKeyword());
        List<Object[]> VariableDeclarators = ast(ctx.formalDeclarators());
        ctx.ast = localVariableDeclaration(Modifiersopt, VarKeyword, VariableDeclarators);
    }

    /** Production: primary ::= 'here' (#primary0) */
    @Override
    public void exitPrimary0(Primary0Context ctx) {
        ctx.ast = nf.Here(pos(ctx));
    }

    /** Production: primary ::= '[' argumentListopt ']' (#primary1) */
    @Override
    public void exitPrimary1(Primary1Context ctx) {
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        Tuple tuple = nf.Tuple(pos(ctx), ArgumentListopt);
        ctx.ast = tuple;
    }

    /** Production: primary ::= literal (#primary2) */
    @Override
    public void exitPrimary2(Primary2Context ctx) {
        ctx.ast = ast(ctx.literal());
    }

    /** Production: primary ::= 'self' (#primary3) */
    @Override
    public void exitPrimary3(Primary3Context ctx) {
        ctx.ast = nf.Self(pos(ctx));
    }

    /** Production: primary ::= 'this' (#primary4) */
    @Override
    public void exitPrimary4(Primary4Context ctx) {
        ctx.ast = nf.This(pos(ctx));
    }

    /** Production: primary ::= className '.' 'this' (#primary5) */
    @Override
    public void exitPrimary5(Primary5Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        ctx.ast = nf.This(pos(ctx), ClassName.toType());
    }

    /** Production: primary ::= '(' expression ')' (#primary6) */
    @Override
    public void exitPrimary6(Primary6Context ctx) {
        Expr Expression = ast(ctx.expression());
        ctx.ast = nf.ParExpr(pos(ctx), Expression);
    }

    /** Production: primary ::= 'new' typeName typeArgumentsopt '(' argumentListopt ')' classBodyopt (#primary7) */
    @Override
    public void exitPrimary7(Primary7Context ctx) {
        ParsedName TypeName = ast(ctx.typeName());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ClassBody ClassBodyopt = ast(ctx.classBodyopt());
        if (ClassBodyopt == null) {
            ctx.ast = nf.X10New(pos(ctx), TypeName.toType(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.X10New(pos(ctx), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt);
        }
    }

    /** Production: primary ::= primary '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt (#primary8) */
    @Override
    public void exitPrimary8(Primary8Context ctx) {
        Expr Primary = ast(ctx.primary());
        Id Identifier = ast(ctx.identifier());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ClassBody ClassBodyopt = ast(ctx.classBodyopt());
        ParsedName b = new ParsedName(nf, ts, pos(ctx), Identifier);
        if (ClassBodyopt == null) {
            ctx.ast = nf.X10New(pos(ctx), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.X10New(pos(ctx), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt);
        }
    }

    /** Production: primary ::= fullyQualifiedName '.' 'new' identifier typeArgumentsopt '(' argumentListopt ')' classBodyopt (#primary9) */
    @Override
    public void exitPrimary9(Primary9Context ctx) {
        ParsedName FullyQualifiedName = ast(ctx.fullyQualifiedName());
        Id Identifier = ast(ctx.identifier());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ClassBody ClassBodyopt = ast(ctx.classBodyopt());
        ParsedName b = new ParsedName(nf, ts, pos(ctx), Identifier);
        if (ClassBodyopt == null) {
            ctx.ast = nf.X10New(pos(ctx), FullyQualifiedName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.X10New(pos(ctx), FullyQualifiedName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt);
        }
    }

    /** Production: primary ::= primary '.' identifier (#primary10) */
    @Override
    public void exitPrimary10(Primary10Context ctx) {
        Expr Primary = ast(ctx.primary());
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.Field(pos(ctx), Primary, Identifier);
    }

    /** Production: primary ::= s='super' '.' identifier (#primary11) */
    @Override
    public void exitPrimary11(Primary11Context ctx) {
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), Identifier);
    }

    /** Production: primary ::= className '.' s='super' '.' identifier (#primary12) */
    @Override
    public void exitPrimary12(Primary12Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()), Identifier);
    }

    /** Production: primary ::= methodName typeArgumentsopt '(' argumentListopt ')' (#primary13) */
    @Override
    public void exitPrimary13(Primary13Context ctx) {
        ParsedName MethodName = ast(ctx.methodName());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = nf.X10Call(pos(ctx), MethodName.prefix == null ? null : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= primary typeArgumentsopt '(' argumentListopt ')' (#primary17) */
    @Override
    public void exitPrimary17(Primary17Context ctx) {
        Expr Primary = ast(ctx.primary());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        if (Primary instanceof Field) {
            Field f = (Field) Primary;
            ctx.ast = nf.X10Call(pos(ctx), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt);
        } else if (Primary instanceof AmbExpr) {
            AmbExpr f = (AmbExpr) Primary;
            ctx.ast = nf.X10Call(pos(ctx), null, f.name(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.ClosureCall(pos(ctx), Primary, TypeArgumentsopt, ArgumentListopt);
        }
    }

    /** Production: primary ::= className '.' 'operator' 'as' '[' type ']' typeArgumentsopt '(' argumentListopt ')' (#primary18) */
    @Override
    public void exitPrimary18(Primary18Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        TypeNode Type = ast(ctx.type());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        Name opName = Converter.operator_as;
        ctx.ast = nf.X10ConversionCall(pos(ctx), ClassName.toType(), nf.Id(pos(ctx.type()), opName), Type, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= className '.' 'operator' '[' type ']' typeArgumentsopt '(' argumentListopt ')' (#primary19) */
    @Override
    public void exitPrimary19(Primary19Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        TypeNode Type = ast(ctx.type());
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        Name opName = Converter.implicit_operator_as;
        ctx.ast = nf.X10ConversionCall(pos(ctx), ClassName.toType(), nf.Id(pos(ctx.type()), opName), Type, TypeArgumentsopt, ArgumentListopt);
    }


    private X10Call prefixOperatorInvocation(Position pos, Expr OperatorPrefix, List<TypeNode> TypeArgumentsopt, List<Expr> ArgumentListopt) {
        if (OperatorPrefix instanceof Field) {
            Field f = (Field) OperatorPrefix;
            return nf.X10Call(pos, f.target(), f.name(), TypeArgumentsopt, ArgumentListopt);
        } else if (OperatorPrefix instanceof AmbExpr) {
            AmbExpr f = (AmbExpr) OperatorPrefix;
            return nf.X10Call(pos, null, f.name(), TypeArgumentsopt, ArgumentListopt);
        } else {
            throw new InternalCompilerError("Invalid operator prefix", OperatorPrefix.position());
        }
    }

    /** Production: primary ::= 'operator' binOp typeArgumentsopt '(' argumentListopt ')' (#primary20) */
    @Override
    public void exitPrimary20(Primary20Context ctx) {
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= fullyQualifiedName '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')' (#primary21) */
    @Override
    public void exitPrimary21(Primary21Context ctx) {
        ParsedName FullyQualifiedName = ast(ctx.fullyQualifiedName());
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= primary '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')' (#primary22) */
    @Override
    public void exitPrimary22(Primary22Context ctx) {
        Expr Primary = ast(ctx.primary());
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= s='super' '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')' (#primary23) */
    @Override
    public void exitPrimary23(Primary23Context ctx) {
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= className '.' s='super' '.' 'operator' binOp typeArgumentsopt '(' argumentListopt ')' (#primary24) */
    @Override
    public void exitPrimary24(Primary24Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')' (#primary25) */
    @Override
    public void exitPrimary25(Primary25Context ctx) {
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= fullyQualifiedName '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')' (#primary26) */
    @Override
    public void exitPrimary26(Primary26Context ctx) {
        ParsedName FullyQualifiedName = ast(ctx.fullyQualifiedName());
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= primary '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')' (#primary27) */
    @Override
    public void exitPrimary27(Primary27Context ctx) {
        Expr Primary = ast(ctx.primary());
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= s='super' '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')' (#primary28) */
    @Override
    public void exitPrimary28(Primary28Context ctx) {
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= className '.' s='super' '.' 'operator' '(' ')' binOp typeArgumentsopt '(' argumentListopt ')' (#primary29) */
    @Override
    public void exitPrimary29(Primary29Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        Binary.Operator BinOp = ast(ctx.binOp());
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')' (#primary30) */
    @Override
    public void exitPrimary30(Primary30Context ctx) {
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= fullyQualifiedName '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')' (#primary31) */
    @Override
    public void exitPrimary31(Primary31Context ctx) {
        ParsedName FullyQualifiedName = ast(ctx.fullyQualifiedName());
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= primary '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')' (#primary32) */
    @Override
    public void exitPrimary32(Primary32Context ctx) {
        Expr Primary = ast(ctx.primary());
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= s='super' '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')' (#primary33) */
    @Override
    public void exitPrimary33(Primary33Context ctx) {
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= className '.' s='super' '.' 'operator' parenthesisOp typeArgumentsopt '(' argumentListopt ')' (#primary34) */
    @Override
    public void exitPrimary34(Primary34Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')' (#primary35) */
    @Override
    public void exitPrimary35(Primary35Context ctx) {
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= fullyQualifiedName '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')' (#primary36) */
    @Override
    public void exitPrimary36(Primary36Context ctx) {
        ParsedName FullyQualifiedName = ast(ctx.fullyQualifiedName());
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= primary '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')' (#primary37) */
    @Override
    public void exitPrimary37(Primary37Context ctx) {
        Expr Primary = ast(ctx.primary());
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= s='super' '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')' (#primary38) */
    @Override
    public void exitPrimary38(Primary38Context ctx) {
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= className '.' s='super' '.' 'operator' parenthesisOp '=' typeArgumentsopt '(' argumentListopt ')' (#primary39) */
    @Override
    public void exitPrimary39(Primary39Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ast(ctx.typeArgumentsopt());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    /** Production: primary ::= 'super' dot='.' (#primaryError0) */
    @Override
    public void exitPrimaryError0(PrimaryError0Context ctx) {
        err.syntaxError("identifier expected", pos(ctx.dot));
        ctx.ast = (Expr) nf.Super(pos(ctx.s)).error(true);
    }

    /** Production: primary ::= className '.' s='super' dot='.' (#primaryError1) */
    @Override
    public void exitPrimaryError1(PrimaryError1Context ctx) {
        err.syntaxError("identifier expected", pos(ctx.dot));
        ParsedName ClassName = ast(ctx.className());
        ctx.ast = (Expr) nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()).error(true);
    }

    /** Production: primary ::= className dot='.' (#primaryError2) */
    @Override
    public void exitPrimaryError2(PrimaryError2Context ctx) {
        err.syntaxError("identifier expected", pos(ctx.dot));
        ParsedName ExpressionName = ast(ctx.className());
        ctx.ast = (Expr) ExpressionName.toExpr().error(true);
    }

    /** Production: primary ::= primary dot='.' (#primaryError3) */
    @Override
    public void exitPrimaryError3(PrimaryError3Context ctx) {
        err.syntaxError("identifier expected", pos(ctx.dot));
        Expr expr = ast(ctx.primary());
        ctx.ast = (Expr) expr.error(true);
    }

    /** Production: literal ::= IntLiteral (#IntLiteral) */
    @Override
    public void exitIntLiteral(IntLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.INT);
    }

    /** Production: literal ::= LongLiteral (#LongLiteral) */
    @Override
    public void exitLongLiteral(LongLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.LONG);
    }

    /** Production: literal ::= ByteLiteral (#ByteLiteral) */
    @Override
    public void exitByteLiteral(ByteLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.BYTE);
    }

    /** Production: literal ::= UnsignedByteLiteral (#UnsignedByteLiteral) */
    @Override
    public void exitUnsignedByteLiteral(UnsignedByteLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.UBYTE);
    }

    /** Production: literal ::= ShortLiteral (#ShortLiteral) */
    @Override
    public void exitShortLiteral(ShortLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.SHORT);
    }

    /** Production: literal ::= UnsignedShortLiteral (#UnsignedShortLiteral) */
    @Override
    public void exitUnsignedShortLiteral(UnsignedShortLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.USHORT);
    }

    /** Production: literal ::= UnsignedIntLiteral (#UnsignedIntLiteral) */
    @Override
    public void exitUnsignedIntLiteral(UnsignedIntLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.UINT);
    }

    /** Production: literal ::= UnsignedLongLiteral (#UnsignedLongLiteral) */
    @Override
    public void exitUnsignedLongLiteral(UnsignedLongLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.ULONG);
    }

    /** Production: literal ::= FloatingPointLiteral (#FloatingPointLiteral) */
    @Override
    public void exitFloatingPointLiteral(FloatingPointLiteralContext ctx) {
        polyglot.lex.FloatLiteral a = float_lit(ctx);
        ctx.ast = nf.FloatLit(pos(ctx), FloatLit.FLOAT, a.getValue().floatValue());
    }

    /** Production: literal ::= DoubleLiteral (#DoubleLiteral) */
    @Override
    public void exitDoubleLiteral(DoubleLiteralContext ctx) {
        polyglot.lex.DoubleLiteral a = double_lit(ctx);
        ctx.ast = nf.FloatLit(pos(ctx), FloatLit.DOUBLE, a.getValue().doubleValue());
    }

    /** Production: literal ::= booleanLiteral (#Literal10) */
    @Override
    public void exitLiteral10(Literal10Context ctx) {
        ctx.ast = ast(ctx.booleanLiteral());
    }


    /** Production: literal ::= CharacterLiteral (#CharacterLiteral) */
    @Override
    public void exitCharacterLiteral(CharacterLiteralContext ctx) {
        ctx.ast = nf.CharLit(pos(ctx), char_lit(ctx).getValue().charValue());
    }

    /** Production: literal ::= StringLiteral (#StringLiteral) */
    @Override
    public void exitStringLiteral(StringLiteralContext ctx) {
        ctx.ast = nf.StringLit(pos(ctx), string_lit(ctx).getValue());
    }

    /** Production: literal ::= 'null' (#NullLiteral) */
    @Override
    public void exitNullLiteral(NullLiteralContext ctx) {
        ctx.ast = nf.NullLit(pos(ctx));
    }

    /** Production: booleanLiteral ::= 'true' | 'false' (#booleanLiteral) */
    @Override
    public void exitBooleanLiteral(BooleanLiteralContext ctx) {
        ctx.ast = nf.BooleanLit(pos(ctx), boolean_lit(ctx).getValue().booleanValue());
    }

    /** Production: argumentList ::= expression (',' expression)* (#argumentList) */
    @Override
    public void exitArgumentList(ArgumentListContext ctx) {
        List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        for (ExpressionContext e : ctx.expression()) {
            l.add(ast(e));
        }
        ctx.ast = l;
    }

    /** Production: fieldAccess ::= primary '.' identifier (#fieldAccess0) */
    @Override
    public void exitFieldAccess0(FieldAccess0Context ctx) {
        Expr Primary = ast(ctx.primary());
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.Field(pos(ctx), Primary, Identifier);
    }

    /** Production: fieldAccess ::= s='super' '.' identifier (#fieldAccess1) */
    @Override
    public void exitFieldAccess1(FieldAccess1Context ctx) {
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), Identifier);
    }

    /** Production: fieldAccess ::= className '.' s='super' '.' identifier (#fieldAccess2) */
    @Override
    public void exitFieldAccess2(FieldAccess2Context ctx) {
        ParsedName ClassName = ast(ctx.className());
        Id Identifier = ast(ctx.identifier());
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.className(), ctx.s), ClassName.toType()), Identifier);
    }

    /** Production: conditionalExpression ::= castExpression (#conditionalExpression0) */
    @Override
    public void exitConditionalExpression0(ConditionalExpression0Context ctx) {
        ctx.ast = ast(ctx.castExpression());
    }

    /** Production: conditionalExpression ::= conditionalExpression op=('++'|'--') (#conditionalExpression1) */
    @Override
    public void exitConditionalExpression1(ConditionalExpression1Context ctx) {
        Expr PostfixExpression = ast(ctx.conditionalExpression());
        Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.PLUS_PLUS:
            op = Unary.POST_INC;
            break;
        case X10Parser.MINUS_MINUS:
            op = Unary.POST_DEC;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Unary(pos(ctx), PostfixExpression, op);
    }

    /** Production: conditionalExpression ::= annotations conditionalExpression (#conditionalExpression2) */
    @Override
    public void exitConditionalExpression2(ConditionalExpression2Context ctx) {
        List<AnnotationNode> Annotations = ast(ctx.annotations());
        Expr UnannotatedUnaryExpression = ast(ctx.conditionalExpression());
        Expr e = UnannotatedUnaryExpression;
        e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
        ctx.ast = (Expr) e.position(pos(ctx));
    }

    /** Production: conditionalExpression ::= op=('+'|'-'|'++'|'--') conditionalExpression (#conditionalExpression3) */
    @Override
    public void exitConditionalExpression3(ConditionalExpression3Context ctx) {
        Expr UnaryExpressionNotPlusMinus = ast(ctx.conditionalExpression());
        Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.PLUS:
            op = Unary.POS;
            break;
        case X10Parser.MINUS:
            op = Unary.NEG;
            break;
        case X10Parser.PLUS_PLUS:
            op = Unary.PRE_INC;
            break;
        case X10Parser.MINUS_MINUS:
            op = Unary.PRE_DEC;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Unary(pos(ctx), op, UnaryExpressionNotPlusMinus);
    }

    /** Production: conditionalExpression ::= op=('~'|'!'|'^'|'|'|'&'|'*'|'/'|'%') conditionalExpression (#conditionalExpression4) */
    @Override
    public void exitConditionalExpression4(ConditionalExpression4Context ctx) {
        Expr UnaryExpressionNotPlusMinus = ast(ctx.conditionalExpression());
        Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.TWIDDLE:
            op = Unary.BIT_NOT;
            break;
        case X10Parser.NOT:
            op = Unary.NOT;
            break;
        case X10Parser.XOR:
            op = Unary.CARET;
            break;
        case X10Parser.OR:
            op = Unary.BAR;
            break;
        case X10Parser.AND:
            op = Unary.AMPERSAND;
            break;
        case X10Parser.MULTIPLY:
            op = Unary.STAR;
            break;
        case X10Parser.DIVIDE:
            op = Unary.SLASH;
            break;
        case X10Parser.REMAINDER:
            op = Unary.SLASH;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Unary(pos(ctx), op, UnaryExpressionNotPlusMinus);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression '..' e2=conditionalExpression (#conditionalExpression5) */
    @Override
    public void exitConditionalExpression5(ConditionalExpression5Context ctx) {
        Expr RangeExpression = ast(ctx.e1);
        Expr UnaryExpression = ast(ctx.e2);
        Expr regionCall = nf.Binary(pos(ctx), RangeExpression, Binary.DOT_DOT, UnaryExpression);
        ctx.ast = regionCall;
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression op=('*'|'/'|'%'|'**') e2=conditionalExpression (#conditionalExpression6) */
    @Override
    public void exitConditionalExpression6(ConditionalExpression6Context ctx) {
        Expr MultiplicativeExpression = ast(ctx.e1);
        Expr RangeExpression = ast(ctx.e2);
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.MULTIPLY:
            op = Binary.MUL;
            break;
        case X10Parser.DIVIDE:
            op = Binary.DIV;
            break;
        case X10Parser.REMAINDER:
            op = Binary.MOD;
            break;
        case X10Parser.STARSTAR:
            op = Binary.STARSTAR;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), MultiplicativeExpression, op, RangeExpression);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression op=('+'|'-') e2=conditionalExpression (#conditionalExpression7) */
    @Override
    public void exitConditionalExpression7(ConditionalExpression7Context ctx) {
        Expr AdditiveExpression = ast(ctx.e1);
        Expr MultiplicativeExpression = ast(ctx.e2);
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.PLUS:
            op = Binary.ADD;
            break;
        case X10Parser.MINUS:
            op = Binary.SUB;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), AdditiveExpression, op, MultiplicativeExpression);
    }

    /** Production: conditionalExpression ::= conditioanalExpression op=('haszero'|'isref') (#conditionalExpression8) */
    @Override
    public void exitConditionalExpression8(ConditionalExpression8Context ctx) {
        if (kind(ctx.conditionalExpression()) == ConditionalExpressionKind.EXPRESSION) {
            err.syntaxError("This expression should be a type.", pos(ctx.conditionalExpression()));
        } else {
            TypeNode t1 = toTypeNode(ctx.conditionalExpression());
            switch (ctx.op.getType()) {
            case X10Parser.HASZERO:
                ctx.ast = nf.HasZeroTest(pos(ctx), t1);
                break;
            case X10Parser.ISREF:
                ctx.ast = nf.IsRefTest(pos(ctx), t1);
                break;
            default:
                assert false;
            }
        }
    }

    /** Production: conditionalExpression ::= t1=conditionalExpression op=('<:'|':>') t2=conditionalExpression (#conditionalExpression10) */
    @Override
    public void exitConditionalExpression10(ConditionalExpression10Context ctx) {
        ConditionalExpressionKind kind1 = kind(ctx.t1);
        ConditionalExpressionKind kind2 = kind(ctx.t2);
        if (kind1 == ConditionalExpressionKind.EXPRESSION) {
            err.syntaxError("This expression should be a type.", pos(ctx.t1));
        } else {
            if (kind2 == ConditionalExpressionKind.EXPRESSION) {
                err.syntaxError("This expression should be a type.", pos(ctx.t2));
            } else {
                TypeNode t1 = toTypeNode(ctx.t1);
                TypeNode t2 = toTypeNode(ctx.t2);
                switch (ctx.op.getType()) {
                case X10Parser.SUBTYPE:
                    ctx.ast = nf.SubtypeTest(pos(ctx), t1, t2, false);
                    break;
                case X10Parser.SUPERTYPE:
                    ctx.ast = nf.SubtypeTest(pos(ctx), t2, t1, false);
                    break;
                default:
                    assert (false);
                }
            }
        }
    }

/** Production: conditionalExpression ::= e1=conditionalExpression op=('<<'|'>>'|'>>>'|'->'|'<-'|'-<'|'>-'|'!'|'<>'|'><') e2=conditionalExpression    (#conditionalExpression11) */
    @Override
    public void exitConditionalExpression11(ConditionalExpression11Context ctx) {
        Expr expr1 = ast(ctx.e1);
        Expr expr2 = ast(ctx.e2);
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.LEFT_SHIFT:
            op = Binary.SHL;
            break;
        case X10Parser.RIGHT_SHIFT:
            op = Binary.SHR;
            break;
        case X10Parser.UNSIGNED_RIGHT_SHIFT:
            op = Binary.USHR;
            break;
        case X10Parser.ARROW:
            op = Binary.ARROW;
            break;
        case X10Parser.LARROW:
            op = Binary.LARROW;
            break;
        case X10Parser.FUNNEL:
            op = Binary.FUNNEL;
            break;
        case X10Parser.LFUNNEL:
            op = Binary.LFUNNEL;
            break;
        case X10Parser.NOT:
            op = Binary.BANG;
            break;
        case X10Parser.DIAMOND:
            op = Binary.DIAMOND;
            break;
        case X10Parser.BOWTIE:
            op = Binary.DIAMOND;
            break;
        default:
            op = null;
            assert false;
        }
        Expr call = nf.Binary(pos(ctx), expr1, op, expr2);
        ctx.ast = call;
    }

    /** Production: conditionalExpression ::= conditionalExpression 'instanceof' type (#conditionalExpression12) */
    @Override
    public void exitConditionalExpression12(ConditionalExpression12Context ctx) {
        Expr RelationalExpression = ast(ctx.conditionalExpression());
        TypeNode Type = ast(ctx.type());
        ctx.ast = nf.Instanceof(pos(ctx), RelationalExpression, Type);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression op=('<'|'>'|'<='|'>=') e2=conditionalExpression (#conditionalExpression13) */
    @Override
    public void exitConditionalExpression13(ConditionalExpression13Context ctx) {
        Expr RelationalExpression = ast(ctx.e1);
        Expr ShiftExpression = ast(ctx.e2);
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.LESS:
            op = Binary.LT;
            break;
        case X10Parser.GREATER:
            op = Binary.GT;
            break;
        case X10Parser.LESS_EQUAL:
            op = Binary.LE;
            break;
        case X10Parser.GREATER_EQUAL:
            op = Binary.GE;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), RelationalExpression, op, ShiftExpression);
    }

    /** The different kind of conditional expressions. */
    private enum ConditionalExpressionKind {
        EXPRESSION, TYPE, AMBVAR
    }

    /** Retrun the kind of a conditional expression. */
    private ConditionalExpressionKind kind(ConditionalExpressionContext ctx) {
        if (ctx instanceof ConditionalExpression26Context) {
            return ConditionalExpressionKind.TYPE;
        }
        if (ctx instanceof ConditionalExpression0Context) {
            if (((ConditionalExpression0Context) ctx).castExpression() instanceof CastExpression1Context) {
                return ConditionalExpressionKind.AMBVAR;
            }
        }
        return ConditionalExpressionKind.EXPRESSION;
    }

    /** Convert a conditional expression that should be of kind TYPE or AMBVAR into a type. */
    private TypeNode toTypeNode(ConditionalExpressionContext ctx) {
        if (ctx instanceof ConditionalExpression26Context) {
            return ast(((ConditionalExpression26Context) ctx).type());
        }
        if (ctx instanceof ConditionalExpression0Context) {
            CastExpressionContext castExpression = ((ConditionalExpression0Context) ctx).castExpression();
            if (castExpression instanceof CastExpression1Context) {
                AmbExpr e = (AmbExpr) ast(((CastExpression1Context) castExpression));
                return nf.AmbTypeNode(e.position(), e.name());
            }
        }
        return null;
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression op=('=='|'!=') e2=conditionalExpression (#conditionalExpression14) */
    @Override
    public void exitConditionalExpression14(ConditionalExpression14Context ctx) {
        ConditionalExpressionKind kind1 = kind(ctx.e1);
        ConditionalExpressionKind kind2 = kind(ctx.e2);
        if (kind1 != ConditionalExpressionKind.TYPE && kind2 != ConditionalExpressionKind.TYPE || ctx.op.getType() != X10Parser.EQUAL_EQUAL) {
            // Comparison between expressions
            Expr EqualityExpression = ast(ctx.e1);
            Expr RelationalExpression = ast(ctx.e2);
            polyglot.ast.Binary.Operator op;
            switch (ctx.op.getType()) {
            case X10Parser.EQUAL_EQUAL:
                op = Binary.EQ;
                break;
            case X10Parser.NOT_EQUAL:
                op = Binary.NE;
                break;
            default:
                op = null;
                assert false;
            }
            ctx.ast = nf.Binary(pos(ctx), EqualityExpression, op, RelationalExpression);
        } else {
            if (kind1 != ConditionalExpressionKind.EXPRESSION && kind2 != ConditionalExpressionKind.EXPRESSION) {
                // Comparison between types
                TypeNode t1 = toTypeNode(ctx.e1);
                TypeNode t2 = toTypeNode(ctx.e2);
                ctx.ast = nf.SubtypeTest(pos(ctx), t1, t2, true);
            } else {
                err.syntaxError("Can not test the equality between types and expressions.", pos(ctx));
            }
        }
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression op=('~'|'!~') e2=conditionalExpression (#conditionalExpression16) */
    @Override
    public void exitConditionalExpression16(ConditionalExpression16Context ctx) {
        Expr EqualityExpression = ast(ctx.e1);
        Expr RelationalExpression = ast(ctx.e2);
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.TWIDDLE:
            op = Binary.TWIDDLE;
            break;
        case X10Parser.NTWIDDLE:
            op = Binary.NTWIDDLE;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), EqualityExpression, op, RelationalExpression);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression '&' e2=conditionalExpression (#conditionalExpression17) */
    @Override
    public void exitConditionalExpression17(ConditionalExpression17Context ctx) {
        Expr AndExpression = ast(ctx.e1);
        Expr EqualityExpression = ast(ctx.e2);
        ctx.ast = nf.Binary(pos(ctx), AndExpression, Binary.BIT_AND, EqualityExpression);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression '^' e2=conditionalExpression (#conditionalExpression18) */
    @Override
    public void exitConditionalExpression18(ConditionalExpression18Context ctx) {
        Expr ExclusiveOrExpression = ast(ctx.e1);
        Expr AndExpression = ast(ctx.e2);
        ctx.ast = nf.Binary(pos(ctx), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression '|' e2=conditionalExpression (#conditionalExpression19) */
    @Override
    public void exitConditionalExpression19(ConditionalExpression19Context ctx) {
        Expr InclusiveOrExpression = ast(ctx.e1);
        Expr ExclusiveOrExpression = ast(ctx.e2);
        ctx.ast = nf.Binary(pos(ctx), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression '&&' e2=conditionalExpression (#conditionalExpression20) */
    @Override
    public void exitConditionalExpression20(ConditionalExpression20Context ctx) {
        Expr ConditionalAndExpression = ast(ctx.e1);
        Expr InclusiveOrExpression = ast(ctx.e2);
        ctx.ast = nf.Binary(pos(ctx), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression);
    }

    /** Production: conditionalExpression ::= e1=conditionalExpression '||' e2=conditionalExpression (#conditionalExpression21) */
    @Override
    public void exitConditionalExpression21(ConditionalExpression21Context ctx) {
        Expr ConditionalOrExpression = ast(ctx.e1);
        Expr ConditionalAndExpression = ast(ctx.e2);
        ctx.ast = nf.Binary(pos(ctx), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression);
    }


    /** Production: conditionalExpression ::= <assoc=right> e1=conditionalExpression '?' e2=expression ':' e3=nonAssignmentExpression (#conditionalExpression25) */
    @Override
    public void exitConditionalExpression25(ConditionalExpression25Context ctx) {
        Expr ConditionalOrExpression = ast(ctx.e1);
        Expr Expression = ast(ctx.e2);
        Expr ConditionalExpression = ast(ctx.e3);
        ctx.ast = nf.Conditional(pos(ctx), ConditionalOrExpression, Expression, ConditionalExpression);
    }

    /** Check that a type can be used in this context (i.e., haszero, isref, <:, :>, ==). */
    private static boolean isTypeContext(ParserRuleContext ctx) {
        if (ctx instanceof ConditionalExpression8Context) {
            return true;
        }
        if (ctx instanceof ConditionalExpression10Context) {
            return true;
        }
        if (ctx instanceof ConditionalExpression14Context) {
            return true;
        }
        return false;
    }

    /** Production: conditionalExpression ::= type (#conditionalExpression26) */
    @Override
    public void exitConditionalExpression26(ConditionalExpression26Context ctx) {
        if (isTypeContext(ctx.getParent())) {
            ctx.ast = null;
        } else {
            err.syntaxError("A type is not allowed as an expression", pos(ctx));
            ctx.ast = errorExpr(pos(ctx));
        }
    }

    /** Production: nonAssignmentExpression ::= closureExpression (#nonAssignmentExpression1) */
    @Override
    public void exitNonAssignmentExpression1(NonAssignmentExpression1Context ctx) {
        ctx.ast = ast(ctx.closureExpression());
    }

    /** Production: nonAssignmentExpression ::= atExpression (#nonAssignmentExpression2) */
    @Override
    public void exitNonAssignmentExpression2(NonAssignmentExpression2Context ctx) {
        ctx.ast = ast(ctx.atExpression());
    }

    /** Production: nonAssignmentExpression ::= oBSOLETE_FinishExpression (#nonAssignmentExpression3) */
    @Override
    public void exitNonAssignmentExpression3(NonAssignmentExpression3Context ctx) {
        ctx.ast = ast(ctx.oBSOLETE_FinishExpression());
    }

    @Override
    public void exitNonAssignmentExpression4(NonAssignmentExpression4Context ctx) {
        ctx.ast = ast(ctx.conditionalExpression());
    }

    /** Production: assignmentExpression ::= assignment (#assignmentExpression0) */
    @Override
    public void exitAssignmentExpression0(AssignmentExpression0Context ctx) {
        ctx.ast = ast(ctx.assignment());
    }

    /** Production: assignmentExpression ::= conditionalExpression (#assignmentExpression1) */
    @Override
    public void exitAssignmentExpression1(AssignmentExpression1Context ctx) {
        ctx.ast = ast(ctx.nonAssignmentExpression());
    }

    /** Production: assignment ::= leftHandSide assignmentOperator assignmentExpression (#assignment0) */
    @Override
    public void exitAssignment0(Assignment0Context ctx) {
        Expr LeftHandSide = ast(ctx.leftHandSide());
        Assign.Operator AssignmentOperator = ast(ctx.assignmentOperator());
        Expr AssignmentExpression = ast(ctx.assignmentExpression());
        ctx.ast = nf.Assign(pos(ctx), LeftHandSide, AssignmentOperator, AssignmentExpression);
    }

    /** Production: assignment ::= expressionName '(' argumentListopt ')' assignmentOperator assignmentExpression (#assignment1) */
    @Override
    public void exitAssignment1(Assignment1Context ctx) {
        ParsedName e1 = ast(ctx.expressionName());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        Assign.Operator AssignmentOperator = ast(ctx.assignmentOperator());
        Expr AssignmentExpression = ast(ctx.assignmentExpression());
        ctx.ast = nf.SettableAssign(pos(ctx), e1.toExpr(), ArgumentListopt, AssignmentOperator, AssignmentExpression);
    }

    /** Production: assignment ::= primary '(' argumentListopt ')' assignmentOperator assignmentExpression (#assignment2) */
    @Override
    public void exitAssignment2(Assignment2Context ctx) {
        Expr e1 = ast(ctx.primary());
        List<Expr> ArgumentListopt = ast(ctx.argumentListopt());
        Assign.Operator AssignmentOperator = ast(ctx.assignmentOperator());
        Expr AssignmentExpression = ast(ctx.assignmentExpression());
        ctx.ast = nf.SettableAssign(pos(ctx), e1, ArgumentListopt, AssignmentOperator, AssignmentExpression);
    }

    /** Production: leftHandSide ::= expressionName (#leftHandSide0) */
    @Override
    public void exitLeftHandSide0(LeftHandSide0Context ctx) {
        ParsedName ExpressionName = ast(ctx.expressionName());
        ctx.ast = ExpressionName.toExpr();
    }

    /** Production: leftHandSide ::= fieldAccess (#leftHandSide1) */
    @Override
    public void exitLeftHandSide1(LeftHandSide1Context ctx) {
        ctx.ast = ast(ctx.fieldAccess());
    }

    /** Production: expression ::= assignmentExpression (#expression) */
    @Override
    public void exitExpression(ExpressionContext ctx) {
        ctx.ast = ast(ctx.assignmentExpression());
    }

    /** Production: constantExpression ::= expression (#constantExpression) */
    @Override
    public void exitConstantExpression(ConstantExpressionContext ctx) {
        ctx.ast = ast(ctx.expression());
    }

    /** Production: assignmentOperator ::= '=' (#assignmentOperator0) */
    @Override
    public void exitAssignmentOperator0(AssignmentOperator0Context ctx) {
        ctx.ast = Assign.ASSIGN;
    }

    /** Production: assignmentOperator ::= '*=' (#assignmentOperator1) */
    @Override
    public void exitAssignmentOperator1(AssignmentOperator1Context ctx) {
        ctx.ast = Assign.MUL_ASSIGN;
    }

    /** Production: assignmentOperator ::= '/=' (#assignmentOperator2) */
    @Override
    public void exitAssignmentOperator2(AssignmentOperator2Context ctx) {
        ctx.ast = Assign.DIV_ASSIGN;
    }

    /** Production: assignmentOperator ::= '%=' (#assignmentOperator3) */
    @Override
    public void exitAssignmentOperator3(AssignmentOperator3Context ctx) {
        ctx.ast = Assign.MOD_ASSIGN;
    }

    /** Production: assignmentOperator ::= '+=' (#assignmentOperator4) */
    @Override
    public void exitAssignmentOperator4(AssignmentOperator4Context ctx) {
        ctx.ast = Assign.ADD_ASSIGN;
    }

    /** Production: assignmentOperator ::= '-=' (#assignmentOperator5) */
    @Override
    public void exitAssignmentOperator5(AssignmentOperator5Context ctx) {
        ctx.ast = Assign.SUB_ASSIGN;
    }

    /** Production: assignmentOperator ::= '<<=' (#assignmentOperator6) */
    @Override
    public void exitAssignmentOperator6(AssignmentOperator6Context ctx) {
        ctx.ast = Assign.SHL_ASSIGN;
    }

    /** Production: assignmentOperator ::= '>>=' (#assignmentOperator7) */
    @Override
    public void exitAssignmentOperator7(AssignmentOperator7Context ctx) {
        ctx.ast = Assign.SHR_ASSIGN;
    }

    /** Production: assignmentOperator ::= '>>>=' (#assignmentOperator8) */
    @Override
    public void exitAssignmentOperator8(AssignmentOperator8Context ctx) {
        ctx.ast = Assign.USHR_ASSIGN;
    }

    /** Production: assignmentOperator ::= '&=' (#assignmentOperator9) */
    @Override
    public void exitAssignmentOperator9(AssignmentOperator9Context ctx) {
        ctx.ast = Assign.BIT_AND_ASSIGN;
    }

    /** Production: assignmentOperator ::= '^=' (#assignmentOperator10) */
    @Override
    public void exitAssignmentOperator10(AssignmentOperator10Context ctx) {
        ctx.ast = Assign.BIT_XOR_ASSIGN;
    }

    /** Production: assignmentOperator ::= '|=' (#assignmentOperator11) */
    @Override
    public void exitAssignmentOperator11(AssignmentOperator11Context ctx) {
        ctx.ast = Assign.BIT_OR_ASSIGN;
    }

    /** Production: assignmentOperator ::= '..=' (#assignmentOperator12) */
    @Override
    public void exitAssignmentOperator12(AssignmentOperator12Context ctx) {
        ctx.ast = Assign.DOT_DOT_ASSIGN;
    }

    /** Production: assignmentOperator ::= '->=' (#assignmentOperator13) */
    @Override
    public void exitAssignmentOperator13(AssignmentOperator13Context ctx) {
        ctx.ast = Assign.ARROW_ASSIGN;
    }

    /** Production: assignmentOperator ::= '<-=' (#assignmentOperator14) */
    @Override
    public void exitAssignmentOperator14(AssignmentOperator14Context ctx) {
        ctx.ast = Assign.LARROW_ASSIGN;
    }

    /** Production: assignmentOperator ::= '-<=' (#assignmentOperator15) */
    @Override
    public void exitAssignmentOperator15(AssignmentOperator15Context ctx) {
        ctx.ast = Assign.FUNNEL_ASSIGN;
    }

    /** Production: assignmentOperator ::= '>-=' (#assignmentOperator16) */
    @Override
    public void exitAssignmentOperator16(AssignmentOperator16Context ctx) {
        ctx.ast = Assign.LFUNNEL_ASSIGN;
    }

    /** Production: assignmentOperator ::= '**=' (#assignmentOperator17) */
    @Override
    public void exitAssignmentOperator17(AssignmentOperator17Context ctx) {
        ctx.ast = Assign.STARSTAR_ASSIGN;
    }

    /** Production: assignmentOperator ::= '<>=' (#assignmentOperator18) */
    @Override
    public void exitAssignmentOperator18(AssignmentOperator18Context ctx) {
        ctx.ast = Assign.DIAMOND_ASSIGN;
    }

    /** Production: assignmentOperator ::= '><=' (#assignmentOperator19) */
    @Override
    public void exitAssignmentOperator19(AssignmentOperator19Context ctx) {
        ctx.ast = Assign.BOWTIE_ASSIGN;
    }

    /** Production: assignmentOperator ::= '~=' (#assignmentOperator20) */
    @Override
    public void exitAssignmentOperator20(AssignmentOperator20Context ctx) {
        ctx.ast = Assign.TWIDDLE_ASSIGN;
    }

    /** Production: prefixOp ::= '+' (#prefixOp0) */
    @Override
    public void exitPrefixOp0(PrefixOp0Context ctx) {
        ctx.ast = Unary.POS;
    }

    /** Production: prefixOp ::= '-' (#prefixOp1) */
    @Override
    public void exitPrefixOp1(PrefixOp1Context ctx) {
        ctx.ast = Unary.NEG;
    }

    /** Production: prefixOp ::= '!' (#prefixOp2) */
    @Override
    public void exitPrefixOp2(PrefixOp2Context ctx) {
        ctx.ast = Unary.NOT;
    }

    /** Production: prefixOp ::= '~' (#prefixOp3) */
    @Override
    public void exitPrefixOp3(PrefixOp3Context ctx) {
        ctx.ast = Unary.BIT_NOT;
    }

    /** Production: prefixOp ::= '^' (#prefixOp4) */
    @Override
    public void exitPrefixOp4(PrefixOp4Context ctx) {
        ctx.ast = Unary.CARET;
    }

    /** Production: prefixOp ::= '|' (#prefixOp5) */
    @Override
    public void exitPrefixOp5(PrefixOp5Context ctx) {
        ctx.ast = Unary.BAR;
    }

    /** Production: prefixOp ::= '&' (#prefixOp6) */
    @Override
    public void exitPrefixOp6(PrefixOp6Context ctx) {
        ctx.ast = Unary.AMPERSAND;
    }

    /** Production: prefixOp ::= '*' (#prefixOp7) */
    @Override
    public void exitPrefixOp7(PrefixOp7Context ctx) {
        ctx.ast = Unary.STAR;
    }

    /** Production: prefixOp ::= '/' (#prefixOp8) */
    @Override
    public void exitPrefixOp8(PrefixOp8Context ctx) {
        ctx.ast = Unary.SLASH;
    }

    /** Production: prefixOp ::= '%' (#prefixOp9) */
    @Override
    public void exitPrefixOp9(PrefixOp9Context ctx) {
        ctx.ast = Unary.PERCENT;
    }

    /** Production: binOp ::= '+' (#binOp0) */
    @Override
    public void exitBinOp0(BinOp0Context ctx) {
        ctx.ast = Binary.ADD;
    }

    /** Production: binOp ::= '-' (#binOp1) */
    @Override
    public void exitBinOp1(BinOp1Context ctx) {
        ctx.ast = Binary.SUB;
    }

    /** Production: binOp ::= '*' (#binOp2) */
    @Override
    public void exitBinOp2(BinOp2Context ctx) {
        ctx.ast = Binary.MUL;
    }

    /** Production: binOp ::= '/' (#binOp3) */
    @Override
    public void exitBinOp3(BinOp3Context ctx) {
        ctx.ast = Binary.DIV;
    }

    /** Production: binOp ::= '%' (#binOp4) */
    @Override
    public void exitBinOp4(BinOp4Context ctx) {
        ctx.ast = Binary.MOD;
    }

    /** Production: binOp ::= '&' (#binOp5) */
    @Override
    public void exitBinOp5(BinOp5Context ctx) {
        ctx.ast = Binary.BIT_AND;
    }

    /** Production: binOp ::= '|' (#binOp6) */
    @Override
    public void exitBinOp6(BinOp6Context ctx) {
        ctx.ast = Binary.BIT_OR;
    }

    /** Production: binOp ::= '^' (#binOp7) */
    @Override
    public void exitBinOp7(BinOp7Context ctx) {
        ctx.ast = Binary.BIT_XOR;
    }

    /** Production: binOp ::= '&&' (#binOp8) */
    @Override
    public void exitBinOp8(BinOp8Context ctx) {
        ctx.ast = Binary.COND_AND;
    }

    /** Production: binOp ::= '||' (#binOp9) */
    @Override
    public void exitBinOp9(BinOp9Context ctx) {
        ctx.ast = Binary.COND_OR;
    }

    /** Production: binOp ::= '<<'    (#binOp10) */
    @Override
    public void exitBinOp10(BinOp10Context ctx) {
        ctx.ast = Binary.SHL;
    }

    /** Production: binOp ::= '>>' (#binOp11) */
    @Override
    public void exitBinOp11(BinOp11Context ctx) {
        ctx.ast = Binary.SHR;
    }

    /** Production: binOp ::= '>>>' (#binOp12) */
    @Override
    public void exitBinOp12(BinOp12Context ctx) {
        ctx.ast = Binary.USHR;
    }

    /** Production: binOp ::= '>=' (#binOp13) */
    @Override
    public void exitBinOp13(BinOp13Context ctx) {
        ctx.ast = Binary.GE;
    }

    /** Production: binOp ::= '<=' (#binOp14) */
    @Override
    public void exitBinOp14(BinOp14Context ctx) {
        ctx.ast = Binary.LE;
    }

    /** Production: binOp ::= '>' (#binOp15) */
    @Override
    public void exitBinOp15(BinOp15Context ctx) {
        ctx.ast = Binary.GT;
    }

    /** Production: binOp ::= '<'    (#binOp16) */
    @Override
    public void exitBinOp16(BinOp16Context ctx) {
        ctx.ast = Binary.LT;
    }

    /** Production: binOp ::= '==' (#binOp17) */
    @Override
    public void exitBinOp17(BinOp17Context ctx) {
        ctx.ast = Binary.EQ;
    }

    /** Production: binOp ::= '!=' (#binOp18) */
    @Override
    public void exitBinOp18(BinOp18Context ctx) {
        ctx.ast = Binary.NE;
    }

    /** Production: binOp ::= '..' (#binOp19) */
    @Override
    public void exitBinOp19(BinOp19Context ctx) {
        ctx.ast = Binary.DOT_DOT;
    }

    /** Production: binOp ::= '->' (#binOp20) */
    @Override
    public void exitBinOp20(BinOp20Context ctx) {
        ctx.ast = Binary.ARROW;
    }

    /** Production: binOp ::= '<-' (#binOp21) */
    @Override
    public void exitBinOp21(BinOp21Context ctx) {
        ctx.ast = Binary.LARROW;
    }

    /** Production: binOp ::= '-<'    (#binOp22) */
    @Override
    public void exitBinOp22(BinOp22Context ctx) {
        ctx.ast = Binary.FUNNEL;
    }

    /** Production: binOp ::= '>-' (#binOp23) */
    @Override
    public void exitBinOp23(BinOp23Context ctx) {
        ctx.ast = Binary.LFUNNEL;
    }

    /** Production: binOp ::= '**' (#binOp24) */
    @Override
    public void exitBinOp24(BinOp24Context ctx) {
        ctx.ast = Binary.STARSTAR;
    }

    /** Production: binOp ::= '~' (#binOp25) */
    @Override
    public void exitBinOp25(BinOp25Context ctx) {
        ctx.ast = Binary.TWIDDLE;
    }

    /** Production: binOp ::= '!~' (#binOp26) */
    @Override
    public void exitBinOp26(BinOp26Context ctx) {
        ctx.ast = Binary.NTWIDDLE;
    }

    /** Production: binOp ::= '!' (#binOp27) */
    @Override
    public void exitBinOp27(BinOp27Context ctx) {
        ctx.ast = Binary.BANG;
    }

    /** Production: binOp ::= '<>' (#binOp28) */
    @Override
    public void exitBinOp28(BinOp28Context ctx) {
        ctx.ast = Binary.DIAMOND;
    }

    /** Production: binOp ::= '><'    (#binOp29) */
    @Override
    public void exitBinOp29(BinOp29Context ctx) {
        ctx.ast = Binary.BOWTIE;
    }

    /** Production: hasResultTypeopt ::= hasResultType? (#hasResultTypeopt) */
    @Override
    public void exitHasResultTypeopt(HasResultTypeoptContext ctx) {
        ctx.ast = ctx.hasResultType() == null ? null : ast(ctx.hasResultType());
    }

    /** Production: typeArgumentsopt ::= typeArguments? (#typeArgumentsopt) */
    @Override
    public void exitTypeArgumentsopt(TypeArgumentsoptContext ctx) {
        if (ctx.typeArguments() == null) {
            ctx.ast = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        } else {
            ctx.ast = ast(ctx.typeArguments());
        }
    }

    /** Production: argumentListopt ::= argumentList? (#argumentListopt) */
    @Override
    public void exitArgumentListopt(ArgumentListoptContext ctx) {
        if (ctx.argumentList() == null) {
            ctx.ast = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        } else {
            ctx.ast = ast(ctx.argumentList());
        }
    }

    /** Production: argumentsopt ::= arguments? (#argumentsopt) */
    @Override
    public void exitArgumentsopt(ArgumentsoptContext ctx) {
        if (ctx.arguments() == null) {
            ctx.ast = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        } else {
            ctx.ast = ast(ctx.arguments());
        }
    }

    /** Production: identifieropt ::= identifier? (#identifieropt) */
    @Override
    public void exitIdentifieropt(IdentifieroptContext ctx) {
        ctx.ast = ctx.identifier() == null ? null : ast(ctx.identifier());
    }

    /** Production: forInitopt ::= forInit? (#forInitopt) */
    @SuppressWarnings("unchecked")
    @Override
    public void exitForInitopt(ForInitoptContext ctx) {
        if (ctx.forInit() == null) {
            ctx.ast = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
        } else {
            ctx.ast = (List<ForInit>) ast(ctx.forInit());
        }
    }

    /** Production: forUpdateopt ::= forUpdate? (#forUpdateopt) */
    @SuppressWarnings("unchecked")
    @Override
    public void exitForUpdateopt(ForUpdateoptContext ctx) {
        if (ctx.forUpdate() == null) {
            ctx.ast = new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false);
        } else {
            ctx.ast = (List<ForUpdate>) ast(ctx.forUpdate());
        }
    }

    /** Production: expressionopt ::= expression? (#expressionopt) */
    @Override
    public void exitExpressionopt(ExpressionoptContext ctx) {
        ctx.ast = ctx.expression() == null ? null : ast(ctx.expression());
    }

    /** Production: catchesopt ::= catches? (#catchesopt) */
    @Override
    public void exitCatchesopt(CatchesoptContext ctx) {
        if (ctx.catches() == null) {
            ctx.ast = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
        } else {
            ctx.ast = ast(ctx.catches());
        }
    }

    /** Production: blockStatementsopt ::= blockStatements? (#blockStatementsopt) */
    @Override
    public void exitBlockStatementsopt(BlockStatementsoptContext ctx) {
        if (ctx.blockStatements() == null) {
            ctx.ast = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        } else {
            ctx.ast = ast(ctx.blockStatements());
        }
    }

    /** Production: classBodyopt ::= classBody? (#classBodyopt) */
    @Override
    public void exitClassBodyopt(ClassBodyoptContext ctx) {
        ctx.ast = ctx.classBody() == null ? null : ast(ctx.classBody());
    }

    /** Production: formalParameterListopt ::= formalParameterList? (#formalParameterListopt) */
    @Override
    public void exitFormalParameterListopt(FormalParameterListoptContext ctx) {
        if (ctx.formalParameterList() == null) {
            ctx.ast = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
        } else {
            ctx.ast = ast(ctx.formalParameterList());
        }
    }

}
