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
package x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.*;
import polyglot.ast.Assign.Operator;
import polyglot.types.*;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.ExtensionInfo;
import x10.types.ParameterType;
import x10.types.checker.Converter;
import x10cuda.ast.CUDAKernel;

/**
 * NodeFactory for X10 extension.
 *
 * @author ??
 * @author vj
 * @author Christian Grothoff
 */
public class X10NodeFactory_c extends NodeFactory_c {

	public X10NodeFactory_c(ExtensionInfo extInfo) {
		this(extInfo, new X10ExtFactory_c(), new X10DelFactory_c());
	}
	
	protected X10NodeFactory_c(ExtensionInfo extInfo, ExtFactory extFact, DelFactory delFact) {
		super(extInfo, extFact, delFact);
	}

	public ExtensionInfo extensionInfo() { return (ExtensionInfo) super.extensionInfo(); }

	public Disamb disamb() {
		return new X10Disamb_c();
	}
	
	public Initializer Initializer(Position pos, FlagsNode flags, Block body) {
	    Initializer n = new X10Initializer_c(pos, flags, body);
	    n = (Initializer)n.ext(extFactory().extInitializer());
	    n = (Initializer)n.del(delFactory().delInitializer());
	    return n;
	}
	
	public LocalAssign LocalAssign(Position pos, Local left, Assign.Operator op, Expr right) {
	    LocalAssign n = new X10LocalAssign_c(this, pos, left, op, right);
	    n = (LocalAssign)n.ext(extFactory().extLocalAssign());
	    n = (LocalAssign)n.del(delFactory().delLocalAssign());
	    return n;
	}
	public FieldAssign FieldAssign(Position pos, Receiver target, Id field, Assign.Operator op, Expr right) {
	    FieldAssign n = new X10FieldAssign_c(this, pos, target, field, op, right);
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

	public AmbExpr AmbExpr(Position pos, Id name) {
	    AmbExpr n = new AmbExpr_c(pos, name);
	    n = (AmbExpr)n.ext(extFactory().extAmbExpr());
	    n = (AmbExpr)n.del(delFactory().delAmbExpr());
	    return n;
	}

	public X10AmbTypeNode AmbTypeNode(Position pos, Prefix p, Id name) {
	    X10AmbTypeNode_c n = new X10AmbTypeNode_c(pos, p, name);
	    n = (X10AmbTypeNode_c) n.ext(extFactory().extAmbTypeNode());
	    n = (X10AmbTypeNode_c) n.del(delFactory().delAmbTypeNode());
	    return n;
	}

	public AmbReceiver AmbReceiver(Position pos, Prefix prefix, Id name) {
	    AmbReceiver n = new X10AmbReceiver_c(pos, prefix, name);
	    n = (AmbReceiver)n.ext(extFactory().extAmbReceiver());
	    n = (AmbReceiver)n.del(delFactory().delAmbReceiver());
	    return n;
	}

	public UnknownTypeNode UnknownTypeNode(Position pos) {
		UnknownTypeNode_c n = new UnknownTypeNode_c(pos);
		n = (UnknownTypeNode_c)n.ext(extFactory().extTypeNode());
		n = (UnknownTypeNode_c)n.del(delFactory().delTypeNode());
		return n;
	}
	public TypeNode HasType(TypeNode tn) {
		HasTypeNode_c n = new HasTypeNode_c(tn);
		n = (HasTypeNode_c)n.ext(extFactory().extTypeNode());
		n = (HasTypeNode_c)n.del(delFactory().delTypeNode());
		return n;
	}

	public Return X10Return(Position pos, Expr expr, boolean implicit) {
		Return n = new X10Return_c(pos, expr, implicit);
		n = (Return)n.ext(extFactory().extReturn());
		n = (Return)n.del(delFactory().delReturn());
		return n;
	}

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

	public HasZeroTest HasZeroTest(Position pos, TypeNode t) {
		HasZeroTest n = new HasZeroTest_c(pos, t);
		n = (HasZeroTest) n.ext(extFactory().extExpr());
		n = (HasZeroTest) n.del(delFactory().delExpr());
		return n;
	}
	public SubtypeTest SubtypeTest(Position pos, TypeNode sub, TypeNode sup, boolean equals) {
		SubtypeTest n = new SubtypeTest_c(pos, sub, sup, equals);
		n = (SubtypeTest) n.ext(extFactory().extExpr());
		n = (SubtypeTest) n.del(delFactory().delExpr());
		return n;
	}
	
	public X10MLSourceFile X10MLSourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls) {
	    X10MLSourceFile n = new X10MLSourceFile_c(position, packageName, CollectionUtil.nonNullList(imports), CollectionUtil.nonNullList(decls));
	    n = (X10MLSourceFile)n.ext(extFactory().extSourceFile());
	    n = (X10MLSourceFile)n.del(delFactory().delSourceFile());
	    return n;
	}
	
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
	public TypeNode AmbDepTypeNode(Position pos, AmbMacroTypeNode base, DepParameterExpr dep) {
	    if (dep == null) {
	        return base;
	    }
	    AmbDepTypeNode n = new AmbDepTypeNode_c(pos, base, dep);
	    n = (AmbDepTypeNode)n.ext(extFactory().extTypeNode());
	    n = (AmbDepTypeNode)n.del(delFactory().delTypeNode());
	    return n;
	}
	public TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr dep) {
	    return AmbDepTypeNode(pos, AmbMacroTypeNode(pos, prefix, name, typeArgs, args), dep);
	}
	public TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, DepParameterExpr dep) {
		return AmbDepTypeNode(pos, prefix, name, Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList(), dep);
	}

	public X10Instanceof Instanceof(Position pos, Expr expr, TypeNode type) {
		X10Instanceof n = new X10Instanceof_c(pos, expr, type);
		n = (X10Instanceof) n.ext(extFactory().extInstanceof());
		n = (X10Instanceof) n.del(delFactory().delInstanceof());
		return n;
	}

	private Block asBlock(Stmt statement) {
		if (statement == null || statement instanceof Block)
			return (Block)statement;
		List<Stmt> l = new ArrayList<Stmt>();
		l.add(statement);
		return Block(statement.position(), l);
	}

	// Wrap the body of the async in a Block so as to ease further code transformations.
	public Async Async(Position pos, List<Expr> clocks, Stmt body) {
		Async a = new Async_c(pos,  clocks, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		a = (Async) a.ext(ext_fac.extAsyncImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		a = (Async) a.del(del_fac.delAsyncImpl());
		return a;
	}
	public Async Async(Position pos, Stmt body, boolean clocked) {
		Async a = new Async_c(pos,  asBlock(body), clocked);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		a = (Async) a.ext(ext_fac.extAsyncImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		a = (Async) a.del(del_fac.delAsyncImpl());
		return a;
	}
	// Wrap the body of the at in a Block so as to ease further code transformations.
	public AtStmt AtStmt(Position pos, Expr place, Stmt body) {
		AtStmt a = new AtStmt_c(pos, place, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		a = (AtStmt) a.ext(ext_fac.extAsyncImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		a = (AtStmt) a.del(del_fac.delAsyncImpl());
		return a;
	}
	public AtStmt AtStmt(Position pos, Expr place, List<Node> captures, Stmt body) {
	    AtStmt a = new AtStmt_c(pos, place, captures, asBlock(body));
	    X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
	    a = (AtStmt) a.ext(ext_fac.extAsyncImpl());
	    X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
	    a = (AtStmt) a.del(del_fac.delAsyncImpl());
	    return a;
	}

	public AtHomeStmt AtHomeStmt(Position pos, List<Expr> vars, Stmt body) {
	    AtHomeStmt a = new AtHomeStmt_c(pos, vars, asBlock(body));
	    X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
	    a = (AtHomeStmt) a.ext(ext_fac.extAsyncImpl());
	    X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
	    a = (AtHomeStmt) a.del(del_fac.delAsyncImpl());
	    return a;
	}
	public AtHomeStmt AtHomeStmt(Position pos, List<Expr> vars, List<Node> captures, Stmt body) {
	    AtHomeStmt a = new AtHomeStmt_c(pos, vars, captures, asBlock(body));
	    X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
	    a = (AtHomeStmt) a.ext(ext_fac.extAsyncImpl());
	    X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
	    a = (AtHomeStmt) a.del(del_fac.delAsyncImpl());
	    return a;
	}

	// Wrap the body of an atomic in a block to facilitate code transformation.
	public Atomic Atomic(Position pos, Expr place, Stmt body) {
		Atomic a = new Atomic_c(pos, place, asBlock(body));
		a = (Atomic) a.ext(extFactory().extExpr());
		a = (Atomic) a.del(delFactory().delExpr());
		return a;
	}
	
	public AtExpr AtExpr(Position pos, Expr place, Block body) {
		AtExpr f = new AtExpr_c(this, pos, place, body);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		f = (AtExpr) f.ext(ext_fac.extExpr()); // FIXME
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        f = (AtExpr) f.del(del_fac.delFutureImpl()); // FIXME
		return f;
	}
	public AtExpr AtExpr(Position pos, Expr place, List<Node> captures, Block body) {
	    AtExpr f = new AtExpr_c(this, pos, place, captures, body);
	    X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
	    f = (AtExpr) f.ext(ext_fac.extExpr()); // FIXME
	    X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
	    f = (AtExpr) f.del(del_fac.delFutureImpl()); // FIXME
	    return f;
	}

	public AtHomeExpr AtHomeExpr(Position pos, List<Expr> vars, Block body) {
	    AtHomeExpr f = new AtHomeExpr_c(this, pos, vars, body);
	    X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
	    f = (AtHomeExpr) f.ext(ext_fac.extExpr()); // FIXME
	    X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
	    f = (AtHomeExpr) f.del(del_fac.delFutureImpl()); // FIXME
	    return f;
	}
	public AtHomeExpr AtHomeExpr(Position pos, List<Expr> vars, List<Node> captures, Block body) {
	    AtHomeExpr f = new AtHomeExpr_c(this, pos, vars, captures, body);
	    X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
	    f = (AtHomeExpr) f.ext(ext_fac.extExpr()); // FIXME
	    X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
	    f = (AtHomeExpr) f.del(del_fac.delFutureImpl()); // FIXME
	    return f;
	}
	
	public Here_c Here(Position pos) {
		Here_c f = new Here_c(pos);
		f = (Here_c) f.ext(extFactory().extStmt());
		return (Here_c) f.del(delFactory().delStmt());
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
	public Resume Resume(Position pos) {
		Resume n = new Resume_c(pos);
		n = (Resume) n.ext(extFactory().extStmt());
		return (Resume) n.del(delFactory().delStmt());
	}
	
	public Offer Offer(Position pos, Expr e) {
		Offer n = new Offer_c(pos,e);
		n = (Offer) n.ext(extFactory().extStmt());
		return (Offer) n.del(delFactory().delStmt());
	}

	public FinishExpr FinishExpr(Position pos, Expr e, Stmt s) {
		FinishExpr n = new FinishExpr_c(pos, e, s);
		n = (FinishExpr) n.ext(extFactory().extStmt());
		return (FinishExpr) n.del(delFactory().delStmt());
	}

	public ClassBody ClassBody(Position pos, List<ClassMember> members) {
		ClassBody n = new X10ClassBody_c(pos,members);
		n = (ClassBody)n.ext(extFactory().extClassBody());
		n = (ClassBody)n.del(delFactory().delClassBody());
		return n;
	}
	
	public X10ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
		return X10ClassDecl(pos, flags, name, Collections.<TypeParamNode>emptyList(), Collections.<PropertyDecl>emptyList(), null, superClass, interfaces, body);
	}

	public X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<PropertyDecl> properties, DepParameterExpr ci,
			TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
		return (X10ClassDecl) ClassDecl(pos, flags, name, typeParameters, properties, superClass, interfaces, body, ci);
	}

	private X10ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<PropertyDecl> properties,
			TypeNode superClass, List<TypeNode> interfaces, ClassBody body, DepParameterExpr tci) {
	    /*if (flags.flags().isInterface()) {
	    	body = PropertyDecl_c.addAbstractGetters(properties, body, this);
	    } else {
	    	  body = PropertyDecl_c.addPropertyGetters(properties, body, this);
	    }*/
	    X10ClassDecl n = new X10ClassDecl_c(pos, flags, name, typeParameters, properties, tci, superClass, interfaces, body);
		n = (X10ClassDecl)n.ext(extFactory().extClassDecl());
		n = (X10ClassDecl)n.del(delFactory().delClassDecl());
		return n;
	}
	public X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body, DepParameterExpr tci) {
		return (X10ClassDecl) ClassDecl(pos, flags, name, Collections.<TypeParamNode>emptyList(), Collections.<PropertyDecl>emptyList(), superClass, interfaces, body, tci);
	}

	public X10Call X10ConversionCall(Position pos, Receiver target, Id name, TypeNode conversionType, List<TypeNode> typeArguments, List<Expr> args) {
		X10Call n = new X10ConversionCall_c(pos, target, name, conversionType, typeArguments, args);
		n = (X10Call) n.ext(extFactory().extExpr());
		n = (X10Call) n.del(delFactory().delExpr());
		return n;
	}
	
	public X10Call X10Call(Position pos, Receiver target, Id name, List<TypeNode> typeArguments, List<Expr> args) {
		X10Call n = new X10Call_c(pos, target, name, typeArguments, args);
		n = (X10Call) n.ext(extFactory().extExpr());
		n = (X10Call) n.del(delFactory().delExpr());
		return n;
	}
	
	public X10Call Call(Position pos, Receiver target, Id name, List<Expr> args) {
		return X10Call(pos, target, name, Collections.<TypeNode>emptyList(), args);
	}
	
	public X10New X10New(Position pos, boolean newOmitted, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
		X10New n = new X10New_c(pos, newOmitted, qualifier, objectType, typeArguments, arguments, body);
		n = (x10.ast.X10New) n.ext(extFactory().extNew());
		n = (x10.ast.X10New) n.del(delFactory().delNew());
		return n;
	}

	public X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
		return X10New(pos, false, qualifier, objectType, typeArguments, arguments, body);
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

	public X10New New(Position pos, Expr qualifier, TypeNode objectType, List<Expr> arguments, ClassBody body) {
		return X10New(pos, qualifier, objectType, Collections.<TypeNode>emptyList(), arguments, body);
	}
	
	// Wrap the body in a block to facilitate code transformations
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
	public AtEach AtEach(Position pos, Formal formal, Expr domain,
			 Stmt body)
	{
		AtEach n = new AtEach_c(pos, formal, domain, asBlock(body));
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

	// Wrap the body in a block to facilitate code transformations
	public X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body)
	{
		X10Loop n = new ForLoop_c(pos, formal, domain, asBlock(body));
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (X10Loop) n.ext(ext_fac.extForLoopImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (X10Loop) n.del(del_fac.delForLoopImpl());
		return n;
	}


	// Wrap the body in a block to facilitate code transformations
	public Finish Finish(Position pos, Stmt body, boolean clocked) {
		Finish n = new Finish_c(pos, asBlock(body), clocked);
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

	public Assign Assign(Position pos, Expr left, Assign.Operator op, Expr right)
	{
	    if (left instanceof InlinableCall) {
	        InlinableCall c = (InlinableCall) left;
	        return SettableAssign(pos, (Expr) c.target(), c.arguments(), op, right);
	    }
	    return SUPER_Assign(pos, left, op, right);
	}
	
	public polyglot.ast.AmbAssign AmbAssign(Position pos, Expr left, Operator op, Expr right) {
	    AmbAssign n = new X10AmbAssign_c(this, pos, left, op, right);
	    n = (AmbAssign)n.ext(extFactory().extAmbAssign());
	    n = (AmbAssign)n.del(delFactory().delAmbAssign());
	    return n;
	}
	public SettableAssign SettableAssign(Position pos, Expr array, List<Expr> index, Operator op, Expr right) {
	    SettableAssign n = new SettableAssign_c(this, pos, array, index, op, right);
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
        return Tuple(pos,null, a);

    }
	public Tuple Tuple(Position pos, TypeNode indexType, List<Expr> a) {
		//Report.report(1, "X10NodeFactory_c making tuple " + p + " " + r + " " + a);
		Tuple n = new Tuple_c(pos, a, indexType);
		n = (Tuple) n.ext(extFactory().extCall());
		n = (Tuple) n.del(delFactory().delCall());
		return n;
	}

	public X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Type type) {
	    X10CanonicalTypeNode tn = (X10CanonicalTypeNode) CanonicalTypeNode(pos, type);
	    return tn;
	}

	public X10CanonicalTypeNode CanonicalTypeNode(Position pos, Ref<? extends Type> type) {
	    X10CanonicalTypeNode n = new X10CanonicalTypeNode_c(pos, type);
	    n = (X10CanonicalTypeNode)n.ext(extFactory().extCanonicalTypeNode());
	    n = (X10CanonicalTypeNode)n.del(delFactory().delCanonicalTypeNode());
	    return n;
	}
	
	public X10Formal X10Formal(Position pos, FlagsNode flags, TypeNode type, Id name,
						 List<Formal> vars, boolean unnamed)
	{
		X10Formal n = new X10Formal_c(unnamed?pos.markCompilerGenerated():pos, flags, type, name, vars, unnamed);
		n = (X10Formal) n.ext(extFactory().extFormal());
		n = (X10Formal) n.del(delFactory().delFormal());
		return n;
	}

	public X10Formal Formal(Position pos, FlagsNode flags, TypeNode type, Id name)
	{
		return X10Formal(pos, flags, type, name, null, false);
	}

	public ParExpr ParExpr(Position pos, Expr expr) {
		ParExpr n = new ParExpr_c(pos, expr);
		n = (ParExpr) n.ext(extFactory().extExpr());
		return (ParExpr) n.del(delFactory().delExpr());
	}

	public Field Field(Position pos, Receiver target, Id name) {
		Field n = new X10Field_c(pos, target, name);
		n = (Field) n.ext(extFactory().extField());
		n = (Field) n.del(delFactory().delField());
		return n;
	}

	public X10FieldDecl FieldDecl(Position pos, FlagsNode flags, TypeNode type,
							   Id name, Expr init)
	{
		X10FieldDecl n = new X10FieldDecl_c(this, pos, flags, type, name, init);
		n = (X10FieldDecl) n.ext(extFactory().extFieldDecl());
		n = (X10FieldDecl) n.del(delFactory().delFieldDecl());
		return n;
	}

	
	public X10Cast X10Cast(Position pos, TypeNode castType, Expr expr) {
	    return X10Cast(pos, castType, expr, Converter.ConversionType.UNKNOWN_CONVERSION);
	}
	public X10Cast X10Cast(Position pos, TypeNode castType, Expr expr, Converter.ConversionType conversionType) {
	    X10Cast n = new X10Cast_c(pos, castType, expr, conversionType);
	    n = (X10Cast)n.ext(extFactory().extCast());
	    n = (X10Cast)n.del(delFactory().delCast());
	    return n;
	}

	public X10Cast Cast(Position pos, TypeNode castType, Expr expr) {
	    return X10Cast(pos, castType, expr, Converter.ConversionType.UNKNOWN_CONVERSION);
	}

	// @Override
	public X10MethodDecl MethodDecl(Position pos, FlagsNode flags, TypeNode returnType,
			Id name,
			List<Formal> formals,  Block body)
	{
		return X10MethodDecl(pos, flags, returnType, name, Collections.<TypeParamNode>emptyList(), formals, null,  null, Collections.<TypeNode>emptyList(), body);
	}

	public X10MethodDecl X10MethodDecl(Position pos, FlagsNode flags, TypeNode returnType, Id name, List<TypeParamNode> typeParams, List<Formal> formals,
			DepParameterExpr guard,  TypeNode offerType, List<TypeNode> throwsOpt, Block body) {
		X10MethodDecl n = new X10MethodDecl_c(this, pos, flags, returnType, name, typeParams,
				formals, guard,  offerType, throwsOpt, body);
		n = (X10MethodDecl)n.ext(extFactory().extMethodDecl());
		n = (X10MethodDecl)n.del(delFactory().delMethodDecl());
		return n;
	}

	public LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init) {
        return LocalDecl(pos, flags, type, name, init, null);
    }
	public LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init, List<Id> exploded)
	{
		LocalDecl n = new X10LocalDecl_c(this, pos, flags, type, name, init, exploded);
		n = (LocalDecl)n.ext(extFactory().extLocalDecl());
		n = (LocalDecl)n.del(delFactory().delLocalDecl());
		return n;
	}
	
	public X10ConstructorDecl ConstructorDecl(Position pos, FlagsNode flags, Id name, List<Formal> formals, Block body) {
		return X10ConstructorDecl(pos, flags, name, null, Collections.<TypeParamNode>emptyList(), formals, null,  null, Collections.<TypeNode>emptyList(), body);
	}

	public X10ConstructorDecl X10ConstructorDecl(Position pos, FlagsNode flags,
			Id name, TypeNode returnType,
			List<TypeParamNode> typeParams, List<Formal> formals,
			DepParameterExpr guard, TypeNode offerType, List<TypeNode> throwTypes, Block body)
	{
		X10ConstructorDecl n =
			new X10ConstructorDecl_c(pos, flags,
					name, returnType,
					typeParams, formals,
					guard,  offerType, throwTypes, body);
		n = (X10ConstructorDecl)n.ext(extFactory().extConstructorDecl());
		n = (X10ConstructorDecl)n.del(delFactory().delConstructorDecl());
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
	public final X10Special Self(Position pos) {
		return Special(pos, X10Special.SELF, null);
	}
	public X10Special Special(Position pos, Special.Kind kind, TypeNode outer) {
		X10Special n = new X10Special_c(pos, kind, outer);
		n = (X10Special)n.ext(extFactory().extSpecial());
		n = (X10Special)n.del(delFactory().delSpecial());
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
	public StmtExpr StmtExpr(Position pos, List<Stmt> statements, Expr result) {
		StmtExpr n = new StmtExpr_c(pos, statements, result);
		n = (StmtExpr)n.ext(extFactory().extBlock());
		n = (StmtExpr)n.del(delFactory().delBlock());
		return n;
	}
	public StmtSeq StmtSeq(Position pos, List<Stmt> statements) {
		StmtSeq n = new StmtSeq_c(this, pos, statements);
		n = (StmtSeq)n.ext(extFactory().extBlock());
		n = (StmtSeq)n.del(delFactory().delBlock());
		return n;
	}
	// Place the consequent and the alternative in blocks to ease
	// further rewrites of the AST.
	// FIXME: early desugaring
	public If If(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
		If n = new X10If_c(pos, cond, asBlock(consequent), asBlock(alternative));
		n = (If)n.ext(extFactory().extIf());
		n = (If)n.del(delFactory().delIf());
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
		IntLit n = new IntLit_c(pos, kind, value);
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
	public X10Conditional Conditional(Position pos, Expr cond, Expr consequent, Expr alternative) {
		X10Conditional n = new X10Conditional_c(pos, cond, consequent, alternative);
		n = (X10Conditional)n.ext(extFactory().extConditional());
		n = (X10Conditional)n.del(delFactory().delConditional());
		return n;
	}

	public X10ConstructorCall X10ThisCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.THIS, outer, typeArgs, args);
	}

	public X10ConstructorCall X10ThisCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.THIS, null, typeArgs, args);
	}

	public X10ConstructorCall X10SuperCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.SUPER, outer, typeArgs, args);
	}

	public X10ConstructorCall X10SuperCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
		return X10ConstructorCall(pos, ConstructorCall.SUPER, null, typeArgs, args);
	}

	public X10ConstructorCall X10ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
		X10ConstructorCall n = new X10ConstructorCall_c(pos, kind, outer, CollectionUtil.nonNullList(typeArgs), CollectionUtil.nonNullList(args));
		n = (X10ConstructorCall)n.ext(extFactory().extConstructorCall());
		n = (X10ConstructorCall)n.del(delFactory().delConstructorCall());
		return n;
	}
	
	public ConstructorCall ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<Expr> args) {
		return X10ConstructorCall(pos, kind, outer, Collections.<TypeNode>emptyList(), args);
	}

	public Closure Closure(Position pos, List<Formal> formals, 
			DepParameterExpr guard, TypeNode returnType,   Block body) {
		return Closure(pos, formals, guard, returnType,  null, body);
	}
	public Closure Closure(Position pos, List<Formal> formals, 
			DepParameterExpr guard, TypeNode returnType,  TypeNode offerType, Block body) {
		Closure n = new Closure_c(this, pos, formals, returnType, guard,  offerType, body);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (Closure) n.ext(ext_fac.extClosureImpl());
		X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
		n = (Closure) n.del(del_fac.delClosureImpl());
		return n;
	}

	public Closure Closure(Closure c, Position pos) {
       return Closure(pos,  c.formals(), c.guard(), c.returnType(), 
    		   c.body());
	}
	public ClosureCall ClosureCall(Position pos, Expr closure, List<TypeNode> typeArgs, List<Expr> args) {
		ClosureCall n = new ClosureCall_c(pos, closure, typeArgs, args);
		n = (ClosureCall) n.ext(extFactory().extExpr());
		n = (ClosureCall) n.del(delFactory().delExpr());
		return n;
	}
	public ClosureCall ClosureCall(Position pos, Expr closure, List<Expr> args) {
		return ClosureCall(pos, closure, Collections.<TypeNode>emptyList(), args);
	
	}

	public AnnotationNode AnnotationNode(Position pos, TypeNode tn) {
		AnnotationNode n = new AnnotationNode_c(pos, tn);
		n = (AnnotationNode) n.ext(extFactory().extNode());
		n = (AnnotationNode) n.del(delFactory().delNode());
		return n;
	}

	public TypeNode FunctionTypeNode(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, TypeNode returnType,
			TypeNode offersType) {
		FunctionTypeNode n = new FunctionTypeNode_c(pos, typeParams, formals, returnType, guard,  offersType);
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

	public CUDAKernel CUDAKernel(Position position, List<Stmt> statements, Block body) {
		CUDAKernel n = new CUDAKernel(position, CollectionUtil.nonNullList(statements), body);
		n = (CUDAKernel)n.ext(extFactory().extBlock());
		n = (CUDAKernel)n.del(delFactory().delBlock());
		return n;
	}

	@Override
	public IsRefTest IsRefTest(Position pos, TypeNode t) {
		IsRefTest n = new IsRefTest(pos, t);
		n = (IsRefTest) n.ext(extFactory().extExpr());
		n = (IsRefTest) n.del(delFactory().delExpr());
		return n;
	}
}
