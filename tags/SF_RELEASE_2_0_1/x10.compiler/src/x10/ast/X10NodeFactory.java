/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.ClassBody;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.Instanceof;
import polyglot.ast.MethodDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.FieldInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.ast.X10Cast.ConversionType;
import x10.parser.X10Parser.JPGPosition;
import x10.types.ParameterType;
import x10.types.X10ConstructorDef;
import x10.types.ParameterType.Variance;

/**
 * NodeFactory for x10 extension.
 * @author vj
 * @author Christian Grothoff
 * @author Igor
 */
public interface X10NodeFactory extends NodeFactory {

    /** Return the language extension this node factory is for. */
    ExtensionInfo extensionInfo();
    
    Expr AmbHereThis(Position pos) ;
    AtStmt AtStmt(Position pos, Expr place, Stmt body);
	AtExpr AtExpr(Position pos, Expr place, TypeNode returnType, Block body);

    X10AmbQualifierNode X10AmbQualifierNode(Position pos, Prefix prefix, Id name);
    X10AmbTypeNode X10AmbTypeNode(Position pos, Prefix prefix, Id name);

    ConstructorCall X10ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10ThisCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10ThisCall(Position pos, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10SuperCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10SuperCall(Position pos, List<TypeNode> typeArgs, List<Expr> args);

    X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Type t, DepParameterExpr e);
    X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Ref<? extends Type> t, DepParameterExpr e);

    X10Cast X10Cast(Position pos, TypeNode castType, Expr expr);
    X10Cast X10Cast(Position pos, TypeNode castType, Expr expr, X10Cast.ConversionType conversionType);
    Return X10Return(Position pos, Expr expr, boolean implicit);

    UnknownTypeNode UnknownTypeNode(Position pos);
    TypeParamNode TypeParamNode(Position pos, Id name);
    TypeParamNode TypeParamNode(Position pos, Id name, ParameterType.Variance variance);
    TypeNode FunctionTypeNode(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, TypeNode returnType, List<TypeNode> throwTypes);   
    Expr SubtypeTest(Position pos, TypeNode sub, TypeNode sup, boolean equals);
    Expr Contains(Position pos, Expr item, Expr collection);
	TypeDecl TypeDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr guard, TypeNode type);

    Call X10Call(Position pos, Receiver target, Id name, List<TypeNode> typeArgs, List<Expr> args);
    
    Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
	Async Async(Position pos, Expr place, List<Expr> clocks, Stmt body);
	Atomic Atomic(Position pos, Expr place, Stmt body);
	Future Future(Position pos, Expr place, TypeNode returnType, Block body);
	Here Here(Position pos);

	/**
	 * Return an immutable representation of a 1-armed When.
	 * (Additional arms are added by invoking the add method on the
	 * returned When.)
	 * @param pos
	 * @param expr
	 * @param statement
	 * @return
	 */
	When When(Position pos, Expr expr, Stmt statement);

	Next Next(Position pos);
	Now Now(Position pos, Expr expr, Stmt stmt);

    X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name,
	    List<TypeParamNode> typeParameters,
		    List<PropertyDecl> properties,
              DepParameterExpr ci, TypeNode superClass,
              List<TypeNode> interfaces, ClassBody body);
	Await Await(Position pos, Expr expr);

	X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body);
	X10Loop ForEach(Position pos, Formal formal, Expr domain, List<Expr> clocks,
					Stmt body);
	X10Loop AtEach(Position pos, Formal formal, Expr domain, List<Expr> clocks,
				   Stmt body);
	Finish Finish(Position pos, Stmt body);

	DepParameterExpr DepParameterExpr(Position pos, List<Expr> cond);
	DepParameterExpr DepParameterExpr(Position pos, List<Formal> formals, List<Expr> cond);
    MethodDecl X10MethodDecl(Position pos, FlagsNode flags,
    		TypeNode returnType, Id name, List<TypeParamNode> typeParams,
    		List<Formal> formals, DepParameterExpr guard, List<TypeNode> throwTypes, Block body);
	SettableAssign SettableAssign(Position pos, Expr a, List<Expr> indices, Assign.Operator op, Expr rhs);

	Tuple Tuple(Position pos, List<Expr> args);
	Formal Formal(Position pos, FlagsNode flags, TypeNode type, Id name);
	X10Formal X10Formal(Position pos, FlagsNode flags, TypeNode type, Id name,
				  List<Formal> vars, boolean unnamed);
	ParExpr ParExpr(Position pos, Expr e);
	PlaceCast PlaceCast(Position pos, Expr place, Expr target);
    
    ConstructorDecl X10ConstructorDecl(Position pos, FlagsNode flags, Id name,
            TypeNode returnType, List<TypeParamNode> typeParams, List<Formal> formals, 
            DepParameterExpr guard, List<TypeNode> throwTypes, Block body);
    PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name);
    PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init);
    Special Self(Position pos);
    
    StmtSeq StmtSeq(Position pos, List<Stmt> l);
    Expr ConstantDistMaker(Position pos, Expr left, Expr right);
    Expr RegionMaker(Position pos, Expr left, Expr right);
    AssignPropertyCall AssignPropertyCall(Position pos, List<TypeNode> typeArgs, List<Expr> argList);

	Closure Closure(Position pos,  List<Formal> formals, DepParameterExpr guard, TypeNode returnType, 
			List<TypeNode> throwTypes, Block body);

	ClosureCall ClosureCall(Position position, Expr closure,  List<Expr> args);
	ClosureCall ClosureCall(Position position, Expr closure,  List<TypeNode> typeargs, List<Expr> args);

    AnnotationNode AnnotationNode(Position pos, TypeNode tn);
    
    AmbMacroTypeNode AmbMacroTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args);
    TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr dep);
    TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, DepParameterExpr dep);

	AssignPropertyBody AssignPropertyBody(Position position, List<Stmt> statements, X10ConstructorDef ci, List<FieldInstance> fi);

	X10MLSourceFile X10MLSourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls);

	X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body);
	X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments);
	X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body);
	X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments);

    LocalTypeDef LocalTypeDef(Position pos, TypeDecl typeDefDeclaration);
    
    Closure Closure(Closure c, Position pos);
}
