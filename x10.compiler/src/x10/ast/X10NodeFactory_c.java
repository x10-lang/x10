/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.AmbAssign;
import polyglot.ast.AmbQualifierNode;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Cast;
import polyglot.ast.CharLit;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.DelFactory;
import polyglot.ast.Disamb;
import polyglot.ast.Do;
import polyglot.ast.Expr;
import polyglot.ast.ExtFactory;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.Import;
import polyglot.ast.Initializer;
import polyglot.ast.Initializer_c;
import polyglot.ast.Instanceof;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.NodeFactory_c;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.While;
import polyglot.ast.Assign.Operator;
import polyglot.types.FieldInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.ExtensionInfo;
import x10.ast.X10Cast.ConversionType;
import x10.types.ParameterType;
import x10.types.X10ConstructorDef;

/**
 * NodeFactory for X10 extension.
 *
 * @author ??
 * @author vj
 * @author Christian Grothoff
 */
public class X10NodeFactory_c extends NodeFactory_c implements X10NodeFactory {

	protected ExtensionInfo extInfo;
	public X10NodeFactory_c(ExtensionInfo extInfo) {
		super(new X10ExtFactory_c(), new X10DelFactory_c());
		this.extInfo = extInfo;
	}
	
	protected X10NodeFactory_c(ExtensionInfo extInfo, ExtFactory extFact, DelFactory delFact) {
		super(extFact, delFact);
		this.extInfo = extInfo;
	}

	protected X10NodeFactory_c(ExtFactory extFact) {
		super(extFact);
	}

	public final ExtensionInfo extensionInfo() { return extInfo; }

	public Disamb disamb() {
		return new X10Disamb_c();
	}
	
	
	
	@Override
	public polyglot.ast.Initializer Initializer(Position pos, FlagsNode flags, Block body) {
	    Initializer n = new X10Initializer_c(pos, flags, body);
	    n = (Initializer)n.ext(extFactory().extInitializer());
	    n = (Initializer)n.del(delFactory().delInitializer());
	    return n;
	}
	
	    public LocalAssign LocalAssign(Position pos, Local left, Assign.Operator op, Expr right) {
	        LocalAssign n = new X10LocalAssign_c(pos, left, op, right);
	        n = (LocalAssign)n.ext(extFactory().extLocalAssign());
	        n = (LocalAssign)n.del(delFactory().delLocalAssign());
	        return n;
	    }
	    @Override
	    public FieldAssign FieldAssign(Position pos, Receiver target, Id field, Assign.Operator op, Expr right) {
	        FieldAssign n = new X10FieldAssign_c(pos, target, field, op, right);
	        n = (FieldAssign)n.ext(extFactory().extFieldAssign());
	        n = (FieldAssign)n.del(delFactory().delFieldAssign());
	        return n;
	    }

	public LocalTypeDef LocalTypeDef(Position pos, TypeDecl typeDefDeclaration) {
	    LocalTypeDef_c n = new LocalTypeDef_c(pos, typeDefDeclaration);
	    n = (LocalTypeDef_c) n.ext(extFactory().extStmt());
	    n = (LocalTypeDef_c) n.del(delFactory().delStmt());
	    return n;
	}

	    public AmbTypeNode AmbTypeNode(Position pos, Prefix qualifier, Id name) {
		X10AmbTypeNode_c n = new X10AmbTypeNode_c(pos, qualifier, name);
	        n = (X10AmbTypeNode_c)n.ext(extFactory().extAmbTypeNode());
	        n = (X10AmbTypeNode_c)n.del(delFactory().delAmbTypeNode());
	        return n;
	    }

	public X10AmbTypeNode X10AmbTypeNode(Position pos, Prefix prefix, Id name) {
		X10AmbTypeNode_c n = new X10AmbTypeNode_c(pos, prefix, name);
		n = (X10AmbTypeNode_c) n.ext(extFactory().extAmbTypeNode());
		n = (X10AmbTypeNode_c) n.del(delFactory().delAmbTypeNode());
		return n;
	}

	public AmbQualifierNode AmbQualifierNode(Position pos, Prefix prefix, Id name) {
		X10AmbQualifierNode_c n = new X10AmbQualifierNode_c(pos, prefix, name);
		n = (X10AmbQualifierNode_c) n.ext(extFactory().extAmbQualifierNode());
		n = (X10AmbQualifierNode_c) n.del(delFactory().delAmbQualifierNode());
		return n;
	}
	public X10AmbQualifierNode X10AmbQualifierNode(Position pos, Prefix prefix, Id name) {
	    X10AmbQualifierNode_c n = new X10AmbQualifierNode_c(pos, prefix, name);
	    n = (X10AmbQualifierNode_c) n.ext(extFactory().extAmbQualifierNode());
	    n = (X10AmbQualifierNode_c) n.del(delFactory().delAmbQualifierNode());
	    return n;
	}

	public UnknownTypeNode UnknownTypeNode(Position pos) {
		UnknownTypeNode_c n = new UnknownTypeNode_c(pos);
		n = (UnknownTypeNode_c)n.ext(extFactory().extTypeNode());
		n = (UnknownTypeNode_c)n.del(delFactory().delTypeNode());
		return n;
	}

	public Return X10Return(Position pos, Expr expr, boolean implicit) {
		Return n = new X10Return_c(pos, expr, implicit);
		n = (Return)n.ext(extFactory().extReturn());
		n = (Return)n.del(delFactory().delReturn());
		return n;
	}

	@Override
	public Return Return(Position pos, Expr expr) {
	    return X10Return(pos, expr, false);
	}

	public TypeParamNode TypeParamNode(Position pos, Id name) {
	    return TypeParamNode(pos, name, ParameterType.Variance.INVARIANT);
	}
	
	public TypeParamNode TypeParamNode(Position pos, Id name, ParameterType.Variance variance) {
		TypeParamNode_c n = new TypeParamNode_c(pos, name, variance);
		n = (TypeParamNode_c) n.ext(extFactory().extNode());
		n = (TypeParamNode_c) n.del(delFactory().delNode());
		return n;
	}
	
	public Expr SubtypeTest(Position pos, TypeNode sub, TypeNode sup, boolean equals) {
		SubtypeTest n = new SubtypeTest_c(pos, sub, sup, equals);
		n = (SubtypeTest) n.ext(extFactory().extExpr());
		n = (SubtypeTest) n.del(delFactory().delExpr());
		return n;
	}
	
	public Expr Contains(Position pos, Expr item, Expr collection) {
	    Contains n = new Contains_c(pos, item, collection);
	    n = (Contains) n.ext(extFactory().extExpr());
	    n = (Contains) n.del(delFactory().delExpr());
	    return n;
	}
	
	public X10MLSourceFile X10MLSourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls) {
	    X10MLSourceFile n = new X10MLSourceFile_c(position, packageName, CollectionUtil.nonNullList(imports), CollectionUtil.nonNullList(decls));
	    n = (X10MLSourceFile)n.ext(extFactory().extSourceFile());
	    n = (X10MLSourceFile)n.del(delFactory().delSourceFile());
	    return n;
	}
	
	@Override
	public SourceFile SourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls) {
		 SourceFile n = new X10SourceFile_c(position, packageName, CollectionUtil.nonNullList(imports), CollectionUtil.nonNullList(decls));
		 n = (SourceFile)n.ext(extFactory().extSourceFile());
		 n = (SourceFile)n.del(delFactory().delSourceFile());
		 return n;
	}
	
	public AmbMacroTypeNode AmbMacroTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args) {
	    AmbMacroTypeNode n = new AmbMacroTypeNode_c(pos, prefix, name, typeArgs, args);
	    n = (AmbMacroTypeNode)n.ext(extFactory().extTypeNode());
	    n = (AmbMacroTypeNode)n.del(delFactory().delTypeNode());
	    return n;
	}
	public TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr dep) {
	    if (dep == null) {
	        return AmbMacroTypeNode(pos, prefix, name, typeArgs, args);
	    }
		AmbDepTypeNode n = new AmbDepTypeNode_c(pos, AmbMacroTypeNode(pos, prefix, name, typeArgs, args), dep);
		n = (AmbDepTypeNode)n.ext(extFactory().extTypeNode());
		n = (AmbDepTypeNode)n.del(delFactory().delTypeNode());
		return n;
	}
	public TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, DepParameterExpr dep) {
		return AmbDepTypeNode(pos, prefix, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, dep);
	}

	public Instanceof Instanceof(Position pos, Expr expr, TypeNode type) {
		Instanceof n = new X10Instanceof_c(pos, expr, type);
		n = (Instanceof) n.ext(extFactory().extInstanceof());
		n = (Instanceof) n.del(delFactory().delInstanceof());
		return n;
	}

	private Block asBlock(Stmt statement) {
		if (statement == null || statement instanceof Block)
			return (Block)statement;
		List<Stmt> l = new ArrayList<Stmt>();
		l.add(statement);
		return Block(statement.position(), l);
	}

	// Wrap the body of the async in a Block so as to ease further code transforamtions.
	public Async Async(Position pos, Expr place, List<Expr> clocks, Stmt body) {
		Async a = new Async_c(pos, place, clocks, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		a = (Async) a.ext(ext_fac.extAsyncImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		a = (Async) a.del(del_fac.delAsyncImpl());
		return a;
	}
	// Wrap the body of the async in a Block so as to ease further code transforamtions.
	public AtStmt AtStmt(Position pos, Expr place, Stmt body) {
		AtStmt a = new AtStmt_c(pos, place, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		a = (AtStmt) a.ext(ext_fac.extAsyncImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		a = (AtStmt) a.del(del_fac.delAsyncImpl());
		return a;
	}

	// Wrap the body of an atomic in a block to facilitate code transformation.
	public Atomic Atomic(Position pos, Expr place, Stmt body) {
		Atomic a = new Atomic_c(pos, place, asBlock(body));
		a = (Atomic) a.ext(extFactory().extExpr());
		a = (Atomic) a.del(delFactory().delExpr());
		return a;
	}
	
	public Future Future(Position pos, Expr place, TypeNode returnType, Block body) {
		Future f = new Future_c(pos, place, returnType, body);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		f = (Future) f.ext(ext_fac.extFutureImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        f = (Future) f.del(del_fac.delFutureImpl());
		return f;
	}

	public AtExpr AtExpr(Position pos, Expr place, TypeNode returnType, Block body) {
		AtExpr f = new AtExpr_c(pos, place, returnType, body);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		f = (AtExpr) f.ext(ext_fac.extExpr()); // FIXME
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        f = (AtExpr) f.del(del_fac.delFutureImpl()); // FIXME
		return f;
	}

	public Here Here(Position pos) {
		Here f = new Here_c(pos);
		f = (Here) f.ext(extFactory().extStmt());
		return (Here) f.del(delFactory().delStmt());
	}

	// Wrap the body of a When in a conditional to facilitate code transformations
	public When When(Position pos, Expr expr, Stmt statement) {
		When w = new When_c(pos, expr, asBlock(statement));
		w = (When) w.ext(extFactory().extStmt());
		return (When) w.del(delFactory().delStmt());
	}

	public Next Next(Position pos) {
		Next n = new Next_c(pos);
		n = (Next) n.ext(extFactory().extStmt());
		return (Next) n.del(delFactory().delStmt());
	}

	public Now Now(Position pos, Expr expr, Stmt stmt) {
		Now n = new Now_c(pos, expr, stmt);
		n = (Now) n.ext(extFactory().extStmt());
		return (Now) n.del(delFactory().delStmt());
	}

	public ClassBody ClassBody(Position pos, List<ClassMember> members) {
		ClassBody n = new X10ClassBody_c(pos,members);
		n = (ClassBody)n.ext(extFactory().extClassBody());
		n = (ClassBody)n.del(delFactory().delClassBody());
		return n;
	}
	
	@Override
	public ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
		return X10ClassDecl(pos, flags, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null, superClass, interfaces, body);
	}

	public X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<PropertyDecl> properties, DepParameterExpr ci,
			TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
		return (X10ClassDecl) ClassDecl(pos, flags, name, typeParameters, properties, superClass, interfaces, body, ci);
	}

	private ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<PropertyDecl> properties, TypeNode superClass, List<TypeNode> interfaces, ClassBody body, DepParameterExpr tci) {
	    boolean isInterface = flags.flags().isInterface();
	    body = flags.flags().isInterface() 
	            ? PropertyDecl_c.addAbstractGetters(properties, body, this)
	            : PropertyDecl_c.addPropertyGetters(properties, body, this);
	    
	    ClassDecl n = new X10ClassDecl_c(pos, flags, name, typeParameters, properties, tci, superClass, interfaces, body);
		n = (ClassDecl)n.ext(extFactory().extClassDecl());
		n = (ClassDecl)n.del(delFactory().delClassDecl());
		return n;
	}
	public X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body, DepParameterExpr tci) {
		return (X10ClassDecl) ClassDecl(pos, flags, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, superClass, interfaces, body, tci);
	}

	public Await Await(Position pos, Expr expr) {
		Await n = new Await_c(pos, expr);
		n = (Await) n.ext(extFactory().extStmt());
		return (Await) n.del(delFactory().delStmt());
	}

	public Call X10Call(Position pos, Receiver target, Id name, List<TypeNode> typeArguments, List<Expr> args) {
		Call n = new X10Call_c(pos, target, name, typeArguments, args);
		n = (Call) n.ext(extFactory().extExpr());
		return (Call) n.del(delFactory().delExpr());
	}
	
	public Call Call(Position pos, Receiver target, Id name, List<Expr> args) {
		return X10Call(pos, target, name, Collections.EMPTY_LIST, args);
	}
	
	public Expr ConstantDistMaker(Position pos, Expr e1, Expr e2) {
		NodeFactory nf = this;
		TypeSystem ts = this.extensionInfo().typeSystem();
		Receiver x10LangDistributionFactory = ReceiverFromQualifiedName(pos, QName.make("x10.lang.Dist"));
		List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
		l.add(e1);
		l.add(e2);
		ConstantDistMaker n = new ConstantDistMaker_c(pos,
				x10LangDistributionFactory,
				Id(pos, Name.make("makeConstant")), l);
		n = (ConstantDistMaker) n.ext(extFactory().extExpr());
		return (ConstantDistMaker) n.del(delFactory().delExpr());
	}

	public X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
		X10New n = new X10New_c(pos, qualifier, objectType, typeArguments, arguments, body);
		n = (x10.ast.X10New) n.ext(extFactory().extNew());
		n = (x10.ast.X10New) n.del(delFactory().delNew());
		return n;
	}
	
	public X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments) {
		return X10New(pos, qualifier, objectType, typeArguments, arguments, null);
	}

	public X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
		return X10New(pos, null, objectType, typeArguments, arguments, body);
	}

	public X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments) {
		return X10New(pos, null, objectType, typeArguments, arguments, null);
	}

	public New New(Position pos, Expr qualifier, TypeNode objectType, List<Expr> arguments, ClassBody body) {
		return X10New(pos, qualifier, objectType, Collections.EMPTY_LIST, arguments, body);
	}

//	 Wrap the body in a block to facilitate code transformations
	public AtEach AtEach(Position pos, Formal formal, Expr domain,
						 List<Expr> clocks, Stmt body)
	{
		AtEach n = new AtEach_c(pos, formal, domain, clocks, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (AtEach) n.ext(ext_fac.extAtEachImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (AtEach) n.del(del_fac.delAtEachImpl());
		return n;
	}

	public For For(Position pos, List<ForInit> inits, Expr cond, List<ForUpdate> iters, Stmt body) {
		For n = new For_c(pos, inits, cond, iters, asBlock(body));
		n = (For)n.ext(extFactory().extFor());
		n = (For)n.del(delFactory().delFor());
		return n;
	}

//	 Wrap the body in a block to facilitate code transformations
	public X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body)
	{
		X10Loop n = new ForLoop_c(pos, formal, domain, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (X10Loop) n.ext(ext_fac.extForLoopImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (X10Loop) n.del(del_fac.delForLoopImpl());
		return n;
	}

//	 Wrap the body in a block to facilitate code transformations
	public ForEach ForEach(Position pos, Formal formal, Expr domain,
						   List<Expr> clocks, Stmt body)
	{
		ForEach n = new ForEach_c(pos, formal, domain, clocks, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (ForEach) n.ext(ext_fac.extForEachImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (ForEach) n.del(del_fac.delForEachImpl());
		return n;
	}

	// Wrap the body in a block to facilitate code transformations
	public Finish Finish(Position pos, Stmt body) {
		Finish n = new Finish_c(pos, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (Finish) n.ext(ext_fac.extFinishImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (Finish) n.del(del_fac.delFinishImpl());
		return n;
	}

	public DepParameterExpr DepParameterExpr(Position pos, List<Expr> e) {
		DepParameterExpr n = new DepParameterExpr_c(pos, e);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}
	public DepParameterExpr DepParameterExpr(Position pos, List<Formal> formals, List<Expr> e) {
		DepParameterExpr n = new DepParameterExpr_c(pos, formals, e);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}

	public Assign Assign(Position pos, Expr left, Assign.Operator op,
						 Expr right)
	{
	    if (left instanceof Call) {
		Call c = (Call) left;
		return SettableAssign(pos, (Expr) c.target(), c.arguments(), op, right);
	    }
	    return super.Assign(pos, left, op, right);
	}
	
	@Override
	public polyglot.ast.AmbAssign AmbAssign(Position pos, Expr left, Operator op, Expr right) {
	    AmbAssign n = new X10AmbAssign_c(pos, left, op, right);
	    n = (AmbAssign)n.ext(extFactory().extAmbAssign());
	    n = (AmbAssign)n.del(delFactory().delAmbAssign());
	    return n;
	}
	public SettableAssign SettableAssign(Position pos, Expr array, List<Expr> index, Operator op, Expr right) {
	    SettableAssign n = new SettableAssign_c(pos, array, index, op, right);
	    n = (SettableAssign)n.ext(extFactory().extAssign());
	    n = (SettableAssign)n.del(delFactory().delAssign());
	    return n;
	}

	public Binary Binary(Position pos, Expr left, Binary.Operator op,
						 Expr right)
	{
		Binary n = new X10Binary_c(pos, left, op, right);
		n = (Binary) n.ext(extFactory().extBinary());
		n = (Binary) n.del(delFactory().delBinary());
		return n;
	}

	public Unary Unary(Position pos, Unary.Operator op, Expr expr) {
		boolean incOp = (op == Unary.POST_INC || op == Unary.PRE_INC ||
						 op == Unary.POST_DEC || op == Unary.PRE_DEC);
		Unary n = new X10Unary_c(pos, op, expr);
		n = (Unary) n.ext(extFactory().extUnary());
		n = (Unary) n.del(delFactory().delUnary());
		return n;
	}

	public Tuple Tuple(Position pos, List<Expr> a) {
		//Report.report(1, "X10NodeFactory_c making tuple " + p + " " + r + " " + a);
		Tuple n = new Tuple_c(pos, a);
		n = (Tuple) n.ext(extFactory().extCall());
		n = (Tuple) n.del(delFactory().delCall());
		return n;
	}

	public X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Type type, DepParameterExpr e) {
	    X10CanonicalTypeNode tn = (X10CanonicalTypeNode) CanonicalTypeNode(pos, type);
	    return tn.constraintExpr(e);
	}

	public X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Ref<? extends Type> type, DepParameterExpr e) {
	    X10CanonicalTypeNode tn = (X10CanonicalTypeNode) CanonicalTypeNode(pos, type);
	    return tn.constraintExpr(e);
	}

	@Override
	public CanonicalTypeNode CanonicalTypeNode(Position pos, Ref<? extends Type> type) {
	    CanonicalTypeNode n = new X10CanonicalTypeNode_c(pos, type);
	    n = (CanonicalTypeNode)n.ext(extFactory().extCanonicalTypeNode());
	    n = (CanonicalTypeNode)n.del(delFactory().delCanonicalTypeNode());
	    return n;
	}

	public X10Formal X10Formal(Position pos, FlagsNode flags, TypeNode type, Id name,
						 List<Formal> vars, boolean unnamed)
	{
		X10Formal n = new X10Formal_c(pos, flags, type, name, vars, unnamed);
		n = (X10Formal) n.ext(extFactory().extFormal());
		n = (X10Formal) n.del(delFactory().delFormal());
		return n;
	}

	public Formal Formal(Position pos, FlagsNode flags, TypeNode type, Id name)
	{
		return X10Formal(pos, flags, type, name, null, false);
	}

	public ParExpr ParExpr(Position pos, Expr expr) {
		ParExpr n = new ParExpr_c(pos, expr);
		n = (ParExpr) n.ext(extFactory().extExpr());
		return (ParExpr) n.del(delFactory().delExpr());
	}

	public PlaceCast PlaceCast(Position pos, Expr place, Expr expr) {
		PlaceCast n = new PlaceCast_c(pos, place, expr);
		n = (PlaceCast) n.ext(extFactory().extExpr());
		return (PlaceCast) n.del(delFactory().delExpr());
	}

	public Field Field(Position pos, Receiver target, Id name) {
		Field n = new X10Field_c(pos, target, name);
		n = (Field) n.ext(extFactory().extField());
		n = (Field) n.del(delFactory().delField());
		return n;
	}

	public FieldDecl FieldDecl(Position pos, FlagsNode flags, TypeNode type,
							   Id name, Expr init)
	{
		FieldDecl n = new X10FieldDecl_c(pos, flags, type, name, init);
		n = (FieldDecl) n.ext(extFactory().extFieldDecl());
		n = (FieldDecl) n.del(delFactory().delFieldDecl());
		return n;
	}

	
	public X10Cast X10Cast(Position pos, TypeNode castType, Expr expr) {
	    return X10Cast(pos, castType, expr, X10Cast.ConversionType.UNKNOWN_CONVERSION);
	}
	public X10Cast X10Cast(Position pos, TypeNode castType, Expr expr, ConversionType conversionType) {
	    X10Cast n = new X10Cast_c(pos, castType, expr, conversionType);
	    n = (X10Cast)n.ext(extFactory().extCast());
	    n = (X10Cast)n.del(delFactory().delCast());
	    return n;
	}

	@Override
	public Cast Cast(Position pos, TypeNode castType, Expr expr) {
	    return X10Cast(pos, castType, expr, X10Cast.ConversionType.UNKNOWN_CONVERSION);
	}

	@Override
	public MethodDecl MethodDecl(Position pos, FlagsNode flags, TypeNode returnType,
			Id name,
			List<Formal> formals, List<TypeNode> throwTypes, Block body)
	{
		return X10MethodDecl(pos, flags, returnType, name, Collections.EMPTY_LIST, formals, null, throwTypes, body);
	}

	public MethodDecl X10MethodDecl(Position pos, FlagsNode flags, TypeNode returnType, Id name, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, List<TypeNode> throwTypes, Block body) {
		MethodDecl n = new X10MethodDecl_c(pos, flags, returnType, name, typeParams,
				formals, guard, throwTypes, body);
		n = (MethodDecl)n.ext(extFactory().extMethodDecl());
		n = (MethodDecl)n.del(delFactory().delMethodDecl());
		return n;
	}
	
	public LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type,
							   Id name, Expr init)
	{
		LocalDecl n = new X10LocalDecl_c(pos, flags, type, name, init);
		n = (LocalDecl)n.ext(extFactory().extLocalDecl());
		n = (LocalDecl)n.del(delFactory().delLocalDecl());
		return n;
	}
	
	@Override
	public ConstructorDecl ConstructorDecl(Position pos, FlagsNode flags, Id name,
			List<Formal> formals, List<TypeNode> throwTypes,
			Block body) {
		return X10ConstructorDecl(pos, flags, name, null, Collections.EMPTY_LIST, formals, null, throwTypes, body);
	}

	public ConstructorDecl X10ConstructorDecl(Position pos, FlagsNode flags,
			Id name, TypeNode returnType,
			List<TypeParamNode> typeParams, List<Formal> formals,
			DepParameterExpr guard, List<TypeNode> throwTypes, Block body)
	{
		ConstructorDecl n =
			new X10ConstructorDecl_c(pos, flags,
					name, returnType,
					typeParams, formals,
					guard, throwTypes, body);
		n = (ConstructorDecl)n.ext(extFactory().extConstructorDecl());
		n = (ConstructorDecl)n.del(delFactory().delConstructorDecl());
		return n;
	}
	public PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name) {
		PropertyDecl n = new PropertyDecl_c(pos, flags, type, name, this);
		n = (PropertyDecl)n.ext(extFactory().extFieldDecl());
		n = (PropertyDecl)n.del(delFactory().delFieldDecl());
		return n;
	}
	public PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init) {
		PropertyDecl n = new PropertyDecl_c(pos, flags, type, name, init, this);
		n = (PropertyDecl)n.ext(extFactory().extFieldDecl());
		n = (PropertyDecl)n.del(delFactory().delFieldDecl());
		return n;
	}
	public final Special Self(Position pos) {
		return Special(pos, X10Special.SELF, null);
	}
	public Special Special(Position pos, Special.Kind kind, TypeNode outer) {
		Special n = new X10Special_c(pos, kind, outer);
		n = (Special)n.ext(extFactory().extSpecial());
		n = (Special)n.del(delFactory().delSpecial());
		return n;
	}

	public Local Local(Position pos, Id name) {
		Local n = new X10Local_c(pos, name);
		n = (Local)n.ext(extFactory().extLocal());
		n = (Local)n.del(delFactory().delLocal());
		return n;
	}
	public BooleanLit BooleanLit(Position pos, boolean value) {
		BooleanLit n = new X10BooleanLit_c(pos, value);
		n = (BooleanLit)n.ext(extFactory().extBooleanLit());
		n = (BooleanLit)n.del(delFactory().delBooleanLit());
		return n;
	}
	public StmtSeq StmtSeq(Position pos, List<Stmt> statements) {
		StmtSeq n = new StmtSeq_c(pos, statements);
		n = (StmtSeq)n.ext(extFactory().extBlock());
		n = (StmtSeq)n.del(delFactory().delBlock());
		return n;
	}
	// Place the consequent and the alternative in blocks to ease
	// further rewrites of the AST.
	public If If(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
		If n = new X10If_c(pos, cond, asBlock(consequent), asBlock(alternative));
		n = (If)n.ext(extFactory().extIf());
		n = (If)n.del(delFactory().delIf());
		return n;
	}
	public Expr RegionMaker(Position pos, Expr e1, Expr e2) {
		NodeFactory nf = this;
		TypeSystem ts = this.extensionInfo().typeSystem();
		
		List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
		l.add(e1);
		l.add(e2);

		Call n = new RegionMaker_c(pos, nf.TypeNodeFromQualifiedName(pos, QName.make("x10.lang.Region")), nf.Id(pos, "makeRectangular"), l);
		n = (Call) n.ext(extFactory().extExpr());
		n = (Call) n.del(delFactory().delExpr());
		return n;
	}
	public Do Do(Position pos, Stmt body, Expr cond) {
		Do n = new X10Do_c(pos, body, cond);
		n = (Do)n.ext(extFactory().extDo());
		n = (Do)n.del(delFactory().delDo());
		return n;
	}
	public While While(Position pos, Expr cond, Stmt body) {
		While n = new X10While_c(pos, cond, body);
		n = (While)n.ext(extFactory().extWhile());
		n = (While)n.del(delFactory().delWhile());
		return n;
	}
	public IntLit IntLit(Position pos, IntLit.Kind kind, long value) {
		IntLit n = new X10IntLit_c(pos, kind, value);
		n = (IntLit)n.ext(extFactory().extIntLit());
		n = (IntLit)n.del(delFactory().delIntLit());
		return n;
	}

	public StringLit StringLit(Position pos, String value) {
		StringLit n = new X10StringLit_c(pos, value);
		n = (StringLit)n.ext(extFactory().extStringLit());
		n = (StringLit)n.del(delFactory().delStringLit());
		return n;
	}
	public FloatLit FloatLit(Position pos, FloatLit.Kind kind, double value) {
		FloatLit n = new X10FloatLit_c(pos, kind, value);
		n = (FloatLit)n.ext(extFactory().extFloatLit());
		n = (FloatLit)n.del(delFactory().delFloatLit());
		return n;
	}
	public CharLit CharLit(Position pos, char value) {
		CharLit n = new X10CharLit_c(pos, value);
		n = (CharLit)n.ext(extFactory().extCharLit());
		n = (CharLit)n.del(delFactory().delCharLit());
		return n;
	}

	public AssignPropertyCall AssignPropertyCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
		AssignPropertyCall  n = new AssignPropertyCall_c(pos, args);
		n = (AssignPropertyCall) n.ext(extFactory().extExpr());
		n= (AssignPropertyCall) n.del(delFactory().delExpr());
		return n;
	}
	public AssignPropertyBody AssignPropertyBody(Position pos, List<Stmt> statements, 
			X10ConstructorDef ci, List<FieldInstance> fi) {
		AssignPropertyBody  n = new AssignPropertyBody_c(pos, statements, ci, fi);
		n = (AssignPropertyBody) n.ext(extFactory().extStmt());
		n= (AssignPropertyBody) n.del(delFactory().delStmt());
		return n;
	}
	public Conditional Conditional(Position pos, Expr cond, Expr consequent, Expr alternative) {
		Conditional n = new X10Conditional_c(pos, cond, consequent, alternative);
		n = (Conditional)n.ext(extFactory().extConditional());
		n = (Conditional)n.del(delFactory().delConditional());
		return n;
	}

	public ConstructorCall X10ThisCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.THIS, outer, typeArgs, args);
	}

	public ConstructorCall X10ThisCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.THIS, null, typeArgs, args);
	}

	public ConstructorCall X10SuperCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.SUPER, outer, typeArgs, args);
	}

	public ConstructorCall X10SuperCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.SUPER, null, typeArgs, args);
	}

	public ConstructorCall X10ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
		ConstructorCall n = new X10ConstructorCall_c(pos, kind, outer, CollectionUtil.nonNullList(typeArgs), CollectionUtil.nonNullList(args));
		n = (ConstructorCall)n.ext(extFactory().extConstructorCall());
		n = (ConstructorCall)n.del(delFactory().delConstructorCall());
		return n;
	}
	
	public ConstructorCall ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<Expr> args) {
		return X10ConstructorCall(pos, kind, outer, Collections.EMPTY_LIST, args);
	}

	public Closure Closure(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, 
			DepParameterExpr guard, TypeNode returnType, List<TypeNode> throwTypes, Block body) {
		Closure n = new Closure_c(pos, typeParams, formals, returnType, guard, throwTypes, body);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (Closure) n.ext(ext_fac.extClosureImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (Closure) n.del(del_fac.delClosureImpl());
		return n;
	}

	public Closure Closure(Closure c, Position pos) {
       return Closure(pos, c.typeParameters(), c.formals(), c.guard(), c.returnType(), c.throwTypes(),
    		   c.body());
	}
	public ClosureCall ClosureCall(Position pos, Expr closure, List<TypeNode> typeArgs, List<Expr> args) {
		ClosureCall n = new ClosureCall_c(pos, closure, typeArgs, args);
		n = (ClosureCall) n.ext(extFactory().extExpr());
		n = (ClosureCall) n.del(delFactory().delExpr());
		return n;
	}

	public AnnotationNode AnnotationNode(Position pos, TypeNode tn) {
		AnnotationNode n = new AnnotationNode_c(pos, tn);
		n = (AnnotationNode) n.ext(extFactory().extNode());
		n = (AnnotationNode) n.del(delFactory().delNode());
		return n;
	}

	public TypeNode FunctionTypeNode(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, TypeNode returnType,
			List<TypeNode> throwTypes) {
		FunctionTypeNode n = new FunctionTypeNode_c(pos, typeParams, formals, returnType, guard, throwTypes);
		n = (FunctionTypeNode) n.ext(extFactory().extTypeNode());
		n = (FunctionTypeNode) n.del(delFactory().delTypeNode());
		return n;
	}

	public TypeDecl TypeDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr guard,
			TypeNode type) {
		TypeDecl n = new TypeDecl_c(pos, flags, name, typeParameters, formals, guard, type);
		n = (TypeDecl) n.ext(extFactory().extNode());
		n = (TypeDecl) n.del(delFactory().delNode());
		return n;
	}

	
}
