/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Cast;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.Instanceof;
import polyglot.ast.MethodDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.util.Position;

/**
 * NodeFactory for x10 extension.
 * @author vj
 * @author Christian Grothoff
 * @author Igor
 */
public interface X10NodeFactory extends NodeFactory {

    /** Return the language extension this node factory is for. */
    ExtensionInfo extensionInfo();

    Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
	Async Async(Position pos, Expr place, List<Expr> clocks, Stmt body);
	Atomic Atomic(Position pos, Expr place, Stmt body);
	Future Future(Position pos, Expr place, Expr body);
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

	NullableNode Nullable(Position pos, TypeNode type);
	FutureNode FutureNode(Position pos, TypeNode type);

    ClassDecl ClassDecl(Position pos, Flags flags, Id name,
              List<PropertyDecl> properties, Expr ci,
              TypeNode superClass, List<TypeNode> interfaces,
              ClassBody body);
	ValueClassDecl ValueClassDecl(Position pos, Flags flags, Id name,
            List<PropertyDecl> properties, Expr ci, 
            TypeNode superClass, List<TypeNode> interfaces,
			ClassBody body);
	Await Await(Position pos, Expr expr);
	ArrayConstructor ArrayConstructor(Position pos, TypeNode base,
									  boolean unsafe, boolean isValue,
									  Expr distribution, Expr init);
	Point Point(Position pos, List<Expr> expr);

	RemoteCall RemoteCall(Position pos, Receiver target, Id name,
						  List<Expr> arguments);
	X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body);
	X10Loop ForEach(Position pos, Formal formal, Expr domain, List<Expr> clocks,
					Stmt body);
	X10Loop AtEach(Position pos, Formal formal, Expr domain, List<Expr> clocks,
				   Stmt body);
	Finish Finish(Position pos, Stmt body);

	//TODO: vj -> vj Change to a single call, with default values for missing
	// parameters.
	Existential Existential(Position pos, List<Formal> args, Expr cond);
	DepParameterExpr DepParameterExpr(Position pos, List<Expr> args, Expr cond);
	DepParameterExpr DepParameterExpr(Position pos, List<Expr> args);
	DepParameterExpr DepParameterExpr(Position pos, Expr cond);
	GenParameterExpr GenParameterExpr(Position pos, List<TypeNode> args);
    MethodDecl MethodDecl(Position pos, DepParameterExpr thisClause,
            Flags flags, TypeNode returnType, Id name,
            List<Formal> formals, DepParameterExpr where, List<TypeNode> throwTypes, Block body);
    MethodDecl X10MethodDecl(Position pos, DepParameterExpr thisClause,
    		Flags flags, TypeNode returnType, Id name,
    		List<Formal> formals, DepParameterExpr where, List<TypeNode> throwTypes, Block body);
    MethodDecl AtomicX10MethodDecl(Position pos, DepParameterExpr thisClause,
    		Flags flags, TypeNode returnType, Id name,
    		List<Formal> formals, DepParameterExpr where, List<TypeNode> throwTypes, Block body);
    FieldDecl FieldDecl(Position pos, DepParameterExpr thisClause, Flags flags, TypeNode type, Id name, Expr init);
	X10ArrayTypeNode X10ArrayTypeNode(Position pos, TypeNode base,
									  boolean isValueType,
									  DepParameterExpr indexedSet);
	X10ArrayAccess X10ArrayAccess(Position pos, Expr a, List<Expr> indices);
	X10ArrayAccess1 X10ArrayAccess1(Position pos, Expr a, Expr index);

	Tuple Tuple(Position pos, Receiver p, Receiver r, List<Expr> args);
	TypeNode array(TypeNode n, Position pos, int dims);
	Formal Formal(Position pos, Flags flags, TypeNode type, Id name);
	X10Formal X10Formal(Position pos, Flags flags, TypeNode type, Id name,
				  List<Formal> vars);
	ParExpr ParExpr(Position pos, Expr e);
	PlaceCast PlaceCast(Position pos, Expr place, Expr target);
    
    ConstructorDecl ConstructorDecl(Position pos, Flags flags, Id name,
            TypeNode returnType, List<Formal> formals, DepParameterExpr argWhereClause, List<TypeNode> throwTypes, Block body);
    ConstructorDecl ConstructorDecl(Position pos, Flags flags, Id name,
            TypeNode returnType, List<Formal> formals,  List<TypeNode> throwTypes, Block body);
    PropertyDecl PropertyDecl(Position pos, Flags flags, TypeNode type, Id name);
    PropertyDecl PropertyDecl(Position pos, Flags flags, TypeNode type, Id name, Expr init);
    Special Self(Position pos);
    
    StmtSeq StmtSeq(Position pos, List<Stmt> l);
    ConstantDistMaker ConstantDistMaker(Position pos, Expr left, Expr right);
    RegionMaker RegionMaker(Position pos, Expr left, Expr right);
    RectRegionMaker RectRegionMaker(Position pos, Receiver receiver, Id name, List<Expr> args);
    MethodDecl AtomicMethodDecl(Position pos, DepParameterExpr thisClause,
            Flags flags, TypeNode returnType, Id name,
            List<Formal> formals, DepParameterExpr where, List<TypeNode> throwTypes, Block body);
    AssignPropertyCall AssignPropertyCall(Position pos, List<Expr> argList);

	Cast DepCast(Position position, TypeNode xn, DepParameterExpr e, Expr expr);

	Instanceof DepInstanceof(Position position, TypeNode xn, DepParameterExpr e, Expr expr);

	Closure Closure(Position pos, List<Formal> formals, TypeNode returnType, List<TypeNode> throwTypes, Block body);

	ClosureCall ClosureCall(Position position, Expr closure, List<Expr> args);

    AnnotationNode AnnotationNode(Position pos, TypeNode tn);
    
    AmbDepTypeNode AmbDepTypeNode(Position pos, TypeNode type, GenParameterExpr gen, DepParameterExpr dep);

	AssignPropertyBody AssignPropertyBody(Position position, List<Stmt> statements, X10ConstructorDef ci, List<FieldInstance> fi);

	X10MLSourceFile X10MLSourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls);   
}
