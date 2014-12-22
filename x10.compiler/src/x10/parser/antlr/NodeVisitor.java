package x10.parser.antlr;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.FileSource;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import x10.parserGen.X10ParserParser.AcceptContext;
import x10.parserGen.X10ParserParser.AnnotationContext;
import x10.parserGen.X10ParserParser.AnnotationStatementContext;
import x10.parserGen.X10ParserParser.AnnotationsContext;
import x10.parserGen.X10ParserParser.AnnotationsoptContext;
import x10.parserGen.X10ParserParser.ApplyOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.ArgumentListContext;
import x10.parserGen.X10ParserParser.ArgumentListoptContext;
import x10.parserGen.X10ParserParser.ArgumentsContext;
import x10.parserGen.X10ParserParser.AssertStatementContext;
import x10.parserGen.X10ParserParser.AssignPropertyCallContext;
import x10.parserGen.X10ParserParser.AssignmentContext;
import x10.parserGen.X10ParserParser.AssignmentExpressionContext;
import x10.parserGen.X10ParserParser.AssignmentOperatorContext;
import x10.parserGen.X10ParserParser.AsyncStatementContext;
import x10.parserGen.X10ParserParser.AtCaptureDeclaratorContext;
import x10.parserGen.X10ParserParser.AtCaptureDeclaratorsContext;
import x10.parserGen.X10ParserParser.AtCaptureDeclaratorsoptContext;
import x10.parserGen.X10ParserParser.AtEachStatementContext;
import x10.parserGen.X10ParserParser.AtExpressionContext;
import x10.parserGen.X10ParserParser.AtStatementContext;
import x10.parserGen.X10ParserParser.AtomicStatementContext;
import x10.parserGen.X10ParserParser.BasicForStatementContext;
import x10.parserGen.X10ParserParser.BinOpContext;
import x10.parserGen.X10ParserParser.BinaryOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.BlockContext;
import x10.parserGen.X10ParserParser.BlockInteriorStatementContext;
import x10.parserGen.X10ParserParser.BlockStatementsContext;
import x10.parserGen.X10ParserParser.BlockStatementsoptContext;
import x10.parserGen.X10ParserParser.BreakStatementContext;
import x10.parserGen.X10ParserParser.CastExpressionContext;
import x10.parserGen.X10ParserParser.CatchClauseContext;
import x10.parserGen.X10ParserParser.CatchesContext;
import x10.parserGen.X10ParserParser.CatchesoptContext;
import x10.parserGen.X10ParserParser.ClassBodyContext;
import x10.parserGen.X10ParserParser.ClassBodyoptContext;
import x10.parserGen.X10ParserParser.ClassDeclarationContext;
import x10.parserGen.X10ParserParser.ClassInstanceCreationExpressionContext;
import x10.parserGen.X10ParserParser.ClassMemberDeclarationContext;
import x10.parserGen.X10ParserParser.ClassMemberDeclarationsContext;
import x10.parserGen.X10ParserParser.ClassMemberDeclarationsoptContext;
import x10.parserGen.X10ParserParser.ClassNameContext;
import x10.parserGen.X10ParserParser.ClassTypeContext;
import x10.parserGen.X10ParserParser.ClockedClauseContext;
import x10.parserGen.X10ParserParser.ClockedClauseoptContext;
import x10.parserGen.X10ParserParser.ClosureBodyContext;
import x10.parserGen.X10ParserParser.ClosureExpressionContext;
import x10.parserGen.X10ParserParser.CompilationUnitContext;
import x10.parserGen.X10ParserParser.ConditionalExpressionContext;
import x10.parserGen.X10ParserParser.ConstantExpressionContext;
import x10.parserGen.X10ParserParser.ConstrainedTypeContext;
import x10.parserGen.X10ParserParser.ConstraintConjunctionContext;
import x10.parserGen.X10ParserParser.ConstraintConjunctionoptContext;
import x10.parserGen.X10ParserParser.ConstructorBlockContext;
import x10.parserGen.X10ParserParser.ConstructorBodyContext;
import x10.parserGen.X10ParserParser.ConstructorDeclarationContext;
import x10.parserGen.X10ParserParser.ContinueStatementContext;
import x10.parserGen.X10ParserParser.ConversionOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.DepNamedTypeContext;
import x10.parserGen.X10ParserParser.DepParametersContext;
import x10.parserGen.X10ParserParser.DoStatementContext;
import x10.parserGen.X10ParserParser.EmptyStatementContext;
import x10.parserGen.X10ParserParser.EnhancedForStatementContext;
import x10.parserGen.X10ParserParser.ExplicitConstructorInvocationContext;
import x10.parserGen.X10ParserParser.ExplicitConstructorInvocationoptContext;
import x10.parserGen.X10ParserParser.ExplicitConversionOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.ExpressionContext;
import x10.parserGen.X10ParserParser.ExpressionNameContext;
import x10.parserGen.X10ParserParser.ExpressionStatementContext;
import x10.parserGen.X10ParserParser.ExpressionoptContext;
import x10.parserGen.X10ParserParser.ExtendsInterfacesContext;
import x10.parserGen.X10ParserParser.ExtendsInterfacesoptContext;
import x10.parserGen.X10ParserParser.FUTURE_ExistentialListContext;
import x10.parserGen.X10ParserParser.FUTURE_ExistentialListoptContext;
import x10.parserGen.X10ParserParser.FieldAccessContext;
import x10.parserGen.X10ParserParser.FieldDeclarationContext;
import x10.parserGen.X10ParserParser.FieldDeclaratorContext;
import x10.parserGen.X10ParserParser.FieldDeclaratorsContext;
import x10.parserGen.X10ParserParser.FinallyBlockContext;
import x10.parserGen.X10ParserParser.FinishStatementContext;
import x10.parserGen.X10ParserParser.ForInitContext;
import x10.parserGen.X10ParserParser.ForInitoptContext;
import x10.parserGen.X10ParserParser.ForStatementContext;
import x10.parserGen.X10ParserParser.ForUpdateContext;
import x10.parserGen.X10ParserParser.ForUpdateExpressionContext;
import x10.parserGen.X10ParserParser.ForUpdateExpressionListContext;
import x10.parserGen.X10ParserParser.ForUpdateoptContext;
import x10.parserGen.X10ParserParser.FormalDeclaratorContext;
import x10.parserGen.X10ParserParser.FormalDeclaratorsContext;
import x10.parserGen.X10ParserParser.FormalParameterContext;
import x10.parserGen.X10ParserParser.FormalParameterListContext;
import x10.parserGen.X10ParserParser.FormalParameterListoptContext;
import x10.parserGen.X10ParserParser.FormalParametersContext;
import x10.parserGen.X10ParserParser.FormalParametersoptContext;
import x10.parserGen.X10ParserParser.FullyQualifiedNameContext;
import x10.parserGen.X10ParserParser.FunctionTypeContext;
import x10.parserGen.X10ParserParser.HasResultTypeContext;
import x10.parserGen.X10ParserParser.HasResultTypeoptContext;
import x10.parserGen.X10ParserParser.HasZeroConstraintContext;
import x10.parserGen.X10ParserParser.HomeVariableContext;
import x10.parserGen.X10ParserParser.HomeVariableListContext;
import x10.parserGen.X10ParserParser.IdentifierContext;
import x10.parserGen.X10ParserParser.IdentifierListContext;
import x10.parserGen.X10ParserParser.IdentifieroptContext;
import x10.parserGen.X10ParserParser.IfThenElseStatementContext;
import x10.parserGen.X10ParserParser.IfThenStatementContext;
import x10.parserGen.X10ParserParser.ImplicitConversionOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.ImportDeclarationContext;
import x10.parserGen.X10ParserParser.ImportDeclarationsContext;
import x10.parserGen.X10ParserParser.ImportDeclarationsoptContext;
import x10.parserGen.X10ParserParser.InterfaceBodyContext;
import x10.parserGen.X10ParserParser.InterfaceDeclarationContext;
import x10.parserGen.X10ParserParser.InterfaceMemberDeclarationContext;
import x10.parserGen.X10ParserParser.InterfaceMemberDeclarationsContext;
import x10.parserGen.X10ParserParser.InterfaceMemberDeclarationsoptContext;
import x10.parserGen.X10ParserParser.InterfaceTypeListContext;
import x10.parserGen.X10ParserParser.InterfacesContext;
import x10.parserGen.X10ParserParser.InterfacesoptContext;
import x10.parserGen.X10ParserParser.IsRefConstraintContext;
import x10.parserGen.X10ParserParser.LabeledStatementContext;
import x10.parserGen.X10ParserParser.LastExpressionContext;
import x10.parserGen.X10ParserParser.LeftHandSideContext;
import x10.parserGen.X10ParserParser.LiteralContext;
import x10.parserGen.X10ParserParser.LocalVariableDeclarationContext;
import x10.parserGen.X10ParserParser.LocalVariableDeclarationStatementContext;
import x10.parserGen.X10ParserParser.LoopIndexContext;
import x10.parserGen.X10ParserParser.LoopIndexDeclaratorContext;
import x10.parserGen.X10ParserParser.LoopStatementContext;
import x10.parserGen.X10ParserParser.MethodBodyContext;
import x10.parserGen.X10ParserParser.MethodDeclarationContext;
import x10.parserGen.X10ParserParser.MethodInvocationContext;
import x10.parserGen.X10ParserParser.MethodModifiersoptContext;
import x10.parserGen.X10ParserParser.MethodNameContext;
import x10.parserGen.X10ParserParser.ModifierContext;
import x10.parserGen.X10ParserParser.ModifiersContext;
import x10.parserGen.X10ParserParser.ModifiersoptContext;
import x10.parserGen.X10ParserParser.NamedTypeContext;
import x10.parserGen.X10ParserParser.NamedTypeNoConstraintsContext;
import x10.parserGen.X10ParserParser.NonExpressionStatementContext;
import x10.parserGen.X10ParserParser.OBSOLETE_FinishExpressionContext;
import x10.parserGen.X10ParserParser.OBSOLETE_OfferStatementContext;
import x10.parserGen.X10ParserParser.OBSOLETE_OffersContext;
import x10.parserGen.X10ParserParser.OBSOLETE_OffersoptContext;
import x10.parserGen.X10ParserParser.OBSOLETE_TypeParamWithVarianceContext;
import x10.parserGen.X10ParserParser.OperatorPrefixContext;
import x10.parserGen.X10ParserParser.PackageDeclarationContext;
import x10.parserGen.X10ParserParser.PackageDeclarationoptContext;
import x10.parserGen.X10ParserParser.PackageNameContext;
import x10.parserGen.X10ParserParser.PackageOrTypeNameContext;
import x10.parserGen.X10ParserParser.ParameterizedNamedTypeContext;
import x10.parserGen.X10ParserParser.PrefixOpContext;
import x10.parserGen.X10ParserParser.PrefixOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.PrimaryContext;
import x10.parserGen.X10ParserParser.PropertiesContext;
import x10.parserGen.X10ParserParser.PropertiesoptContext;
import x10.parserGen.X10ParserParser.PropertyContext;
import x10.parserGen.X10ParserParser.PropertyListContext;
import x10.parserGen.X10ParserParser.PropertyMethodDeclarationContext;
import x10.parserGen.X10ParserParser.ResultTypeContext;
import x10.parserGen.X10ParserParser.ReturnStatementContext;
import x10.parserGen.X10ParserParser.SetOperatorDeclarationContext;
import x10.parserGen.X10ParserParser.SimpleNamedTypeContext;
import x10.parserGen.X10ParserParser.SingleTypeImportDeclarationContext;
import x10.parserGen.X10ParserParser.StatementContext;
import x10.parserGen.X10ParserParser.StatementExpressionContext;
import x10.parserGen.X10ParserParser.StatementExpressionListContext;
import x10.parserGen.X10ParserParser.StructDeclarationContext;
import x10.parserGen.X10ParserParser.SubtypeConstraintContext;
import x10.parserGen.X10ParserParser.SuperExtendsContext;
import x10.parserGen.X10ParserParser.SuperExtendsoptContext;
import x10.parserGen.X10ParserParser.SwitchBlockContext;
import x10.parserGen.X10ParserParser.SwitchBlockStatementGroupContext;
import x10.parserGen.X10ParserParser.SwitchBlockStatementGroupsContext;
import x10.parserGen.X10ParserParser.SwitchBlockStatementGroupsoptContext;
import x10.parserGen.X10ParserParser.SwitchLabelContext;
import x10.parserGen.X10ParserParser.SwitchLabelsContext;
import x10.parserGen.X10ParserParser.SwitchLabelsoptContext;
import x10.parserGen.X10ParserParser.SwitchStatementContext;
import x10.parserGen.X10ParserParser.ThrowStatementContext;
import x10.parserGen.X10ParserParser.ThrowsListContext;
import x10.parserGen.X10ParserParser.Throws_Context;
import x10.parserGen.X10ParserParser.ThrowsoptContext;
import x10.parserGen.X10ParserParser.TryStatementContext;
import x10.parserGen.X10ParserParser.TypeArgumentListContext;
import x10.parserGen.X10ParserParser.TypeArgumentsContext;
import x10.parserGen.X10ParserParser.TypeArgumentsoptContext;
import x10.parserGen.X10ParserParser.TypeContext;
import x10.parserGen.X10ParserParser.TypeDeclarationContext;
import x10.parserGen.X10ParserParser.TypeDeclarationsContext;
import x10.parserGen.X10ParserParser.TypeDeclarationsoptContext;
import x10.parserGen.X10ParserParser.TypeDefDeclarationContext;
import x10.parserGen.X10ParserParser.TypeImportOnDemandDeclarationContext;
import x10.parserGen.X10ParserParser.TypeNameContext;
import x10.parserGen.X10ParserParser.TypeParamWithVarianceListContext;
import x10.parserGen.X10ParserParser.TypeParameterContext;
import x10.parserGen.X10ParserParser.TypeParameterListContext;
import x10.parserGen.X10ParserParser.TypeParametersContext;
import x10.parserGen.X10ParserParser.TypeParametersoptContext;
import x10.parserGen.X10ParserParser.TypeParamsWithVarianceContext;
import x10.parserGen.X10ParserParser.TypeParamsWithVarianceoptContext;
import x10.parserGen.X10ParserParser.VarKeywordContext;
import x10.parserGen.X10ParserParser.VarKeywordoptContext;
import x10.parserGen.X10ParserParser.VariableDeclaratorContext;
import x10.parserGen.X10ParserParser.VariableDeclaratorWithTypeContext;
import x10.parserGen.X10ParserParser.VariableDeclaratorsContext;
import x10.parserGen.X10ParserParser.VariableDeclaratorsWithTypeContext;
import x10.parserGen.X10ParserParser.VariableInitializerContext;
import x10.parserGen.X10ParserParser.Void_Context;
import x10.parserGen.X10ParserParser.WhenStatementContext;
import x10.parserGen.X10ParserParser.WhereClauseContext;
import x10.parserGen.X10ParserParser.WhereClauseoptContext;
import x10.parserGen.X10ParserParser.WhileStatementContext;
import x10.parserGen.*;

public class NodeVisitor implements X10ParserVisitor<Node> {

	private TypeSystem ts;
	private NodeFactory nf;
	private FileSource srce;
	private ErrorQueue eq;

	public NodeVisitor(TypeSystem ts, NodeFactory nf, FileSource srce,
			ErrorQueue eq) {
		this.ts = ts;
		this.nf = nf;
		this.srce = srce;
		this.eq = eq;
	}

	@Override
	public Node visit(ParseTree arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitChildren(RuleNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitErrorNode(ErrorNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTerminal(TerminalNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitApplyOperatorDeclaration(ApplyOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitIdentifieropt(IdentifieroptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFieldAccess(FieldAccessContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitImplicitConversionOperatorDeclaration(
			ImplicitConversionOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalParametersopt(FormalParametersoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVariableDeclaratorsWithType(
			VariableDeclaratorsWithTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeDeclaration(TypeDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchBlockStatementGroups(
			SwitchBlockStatementGroupsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassMemberDeclaration(ClassMemberDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitOBSOLETE_OfferStatement(OBSOLETE_OfferStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaces(InterfacesContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitThrows_(Throws_Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitOBSOLETE_TypeParamWithVariance(
			OBSOLETE_TypeParamWithVarianceContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassInstanceCreationExpression(
			ClassInstanceCreationExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitMethodName(MethodNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFinallyBlock(FinallyBlockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSetOperatorDeclaration(SetOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVarKeyword(VarKeywordContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAssignmentExpression(AssignmentExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitNamedTypeNoConstraints(NamedTypeNoConstraintsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaceTypeList(InterfaceTypeListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParametersopt(TypeParametersoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitCatchClause(CatchClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBlockInteriorStatement(BlockInteriorStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstantExpression(ConstantExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtCaptureDeclaratorsopt(AtCaptureDeclaratorsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExpressionopt(ExpressionoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClockedClause(ClockedClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfacesopt(InterfacesoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitNamedType(NamedTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExplicitConstructorInvocation(
			ExplicitConstructorInvocationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAnnotationsopt(AnnotationsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVariableInitializer(VariableInitializerContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaceMemberDeclarations(
			InterfaceMemberDeclarationsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExpressionName(ExpressionNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFunctionType(FunctionTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFieldDeclaration(FieldDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtEachStatement(AtEachStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalParameterList(FormalParameterListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParamWithVarianceList(TypeParamWithVarianceListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitCatches(CatchesContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitWhenStatement(WhenStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLoopStatement(LoopStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConversionOperatorDeclaration(
			ConversionOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassDeclaration(ClassDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFieldDeclarators(FieldDeclaratorsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPackageDeclarationopt(PackageDeclarationoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExplicitConversionOperatorDeclaration(
			ExplicitConversionOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchStatement(SwitchStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAssignment(AssignmentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitArguments(ArgumentsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalParameters(FormalParametersContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitCastExpression(CastExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitHomeVariable(HomeVariableContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchBlockStatementGroupsopt(
			SwitchBlockStatementGroupsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitThrowStatement(ThrowStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAnnotations(AnnotationsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeArgumentList(TypeArgumentListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVariableDeclarator(VariableDeclaratorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExpression(ExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitWhereClause(WhereClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitEnhancedForStatement(EnhancedForStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalParameter(FormalParameterContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParamsWithVariance(TypeParamsWithVarianceContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitReturnStatement(ReturnStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForUpdateExpression(ForUpdateExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitImportDeclarations(ImportDeclarationsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForUpdateopt(ForUpdateoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAssertStatement(AssertStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitOperatorPrefix(OperatorPrefixContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassBody(ClassBodyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstructorBlock(ConstructorBlockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitDepNamedType(DepNamedTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitMethodModifiersopt(MethodModifiersoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitThrowsList(ThrowsListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFieldDeclarator(FieldDeclaratorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeArguments(TypeArgumentsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeDeclarations(TypeDeclarationsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitEmptyStatement(EmptyStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitModifier(ModifierContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLastExpression(LastExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchLabels(SwitchLabelsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitResultType(ResultTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitHasResultTypeopt(HasResultTypeoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstrainedType(ConstrainedTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitHasZeroConstraint(HasZeroConstraintContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitIdentifier(IdentifierContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitCompilationUnit(CompilationUnitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBlockStatementsopt(BlockStatementsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParameterList(TypeParameterListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSingleTypeImportDeclaration(
			SingleTypeImportDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtomicStatement(AtomicStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLabeledStatement(LabeledStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTryStatement(TryStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalParameterListopt(FormalParameterListoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassName(ClassNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitMethodDeclaration(MethodDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstraintConjunctionopt(ConstraintConjunctionoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLeftHandSide(LeftHandSideContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassMemberDeclarations(ClassMemberDeclarationsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassType(ClassTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitContinueStatement(ContinueStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitArgumentList(ArgumentListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForUpdateExpressionList(ForUpdateExpressionListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitWhileStatement(WhileStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExtendsInterfaces(ExtendsInterfacesContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBinaryOperatorDeclaration(BinaryOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSimpleNamedType(SimpleNamedTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitNonExpressionStatement(NonExpressionStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFUTURE_ExistentialList(FUTURE_ExistentialListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPropertiesopt(PropertiesoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPackageName(PackageNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExpressionStatement(ExpressionStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAccept(AcceptContext ctx) {
		
		return null;
	}

	@Override
	public Node visitLoopIndexDeclarator(LoopIndexDeclaratorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForUpdate(ForUpdateContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAnnotation(AnnotationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitImportDeclaration(ImportDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitThrowsopt(ThrowsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtExpression(AtExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitArgumentListopt(ArgumentListoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPrefixOperatorDeclaration(PrefixOperatorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVariableDeclarators(VariableDeclaratorsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchBlock(SwitchBlockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeArgumentsopt(TypeArgumentsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPrefixOp(PrefixOpContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassMemberDeclarationsopt(
			ClassMemberDeclarationsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBreakStatement(BreakStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitOBSOLETE_Offersopt(OBSOLETE_OffersoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParameter(TypeParameterContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForStatement(ForStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitStatementExpression(StatementExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBlock(BlockContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConditionalExpression(ConditionalExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLocalVariableDeclarationStatement(
			LocalVariableDeclarationStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAsyncStatement(AsyncStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFullyQualifiedName(FullyQualifiedNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitModifiers(ModifiersContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLoopIndex(LoopIndexContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBasicForStatement(BasicForStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchLabel(SwitchLabelContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClosureBody(ClosureBodyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParameters(TypeParametersContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPropertyMethodDeclaration(PropertyMethodDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtCaptureDeclarators(AtCaptureDeclaratorsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitMethodInvocation(MethodInvocationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitHomeVariableList(HomeVariableListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitWhereClauseopt(WhereClauseoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitDepParameters(DepParametersContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeName(TypeNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstructorBody(ConstructorBodyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClassBodyopt(ClassBodyoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVarKeywordopt(VarKeywordoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForInit(ForInitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAssignPropertyCall(AssignPropertyCallContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSubtypeConstraint(SubtypeConstraintContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeDeclarationsopt(TypeDeclarationsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstraintConjunction(ConstraintConjunctionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBinOp(BinOpContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitImportDeclarationsopt(ImportDeclarationsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFinishStatement(FinishStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPropertyList(PropertyListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtStatement(AtStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitType(TypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitIsRefConstraint(IsRefConstraintContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPrimary(PrimaryContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPackageDeclaration(PackageDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeImportOnDemandDeclaration(
			TypeImportOnDemandDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchLabelsopt(SwitchLabelsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClosureExpression(ClosureExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitProperty(PropertyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitProperties(PropertiesContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalDeclarators(FormalDeclaratorsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitStatement(StatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaceMemberDeclarationsopt(
			InterfaceMemberDeclarationsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitParameterizedNamedType(ParameterizedNamedTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaceBody(InterfaceBodyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExtendsInterfacesopt(ExtendsInterfacesoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitPackageOrTypeName(PackageOrTypeNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVoid_(Void_Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitModifiersopt(ModifiersoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitExplicitConstructorInvocationopt(
			ExplicitConstructorInvocationoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitStructDeclaration(StructDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitClockedClauseopt(ClockedClauseoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitOBSOLETE_Offers(OBSOLETE_OffersContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAssignmentOperator(AssignmentOperatorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeParamsWithVarianceopt(TypeParamsWithVarianceoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitIdentifierList(IdentifierListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAtCaptureDeclarator(AtCaptureDeclaratorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitBlockStatements(BlockStatementsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitIfThenElseStatement(IfThenElseStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitForInitopt(ForInitoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitOBSOLETE_FinishExpression(OBSOLETE_FinishExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitDoStatement(DoStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitIfThenStatement(IfThenStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitCatchesopt(CatchesoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitTypeDefDeclaration(TypeDefDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSuperExtendsopt(SuperExtendsoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFUTURE_ExistentialListopt(FUTURE_ExistentialListoptContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitAnnotationStatement(AnnotationStatementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitHasResultType(HasResultTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSuperExtends(SuperExtendsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitMethodBody(MethodBodyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitFormalDeclarator(FormalDeclaratorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitVariableDeclaratorWithType(
			VariableDeclaratorWithTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitStatementExpressionList(StatementExpressionListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitInterfaceMemberDeclaration(
			InterfaceMemberDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node visitLiteral(LiteralContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
