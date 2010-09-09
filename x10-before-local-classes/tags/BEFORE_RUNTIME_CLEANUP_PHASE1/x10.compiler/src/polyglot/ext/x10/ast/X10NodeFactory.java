package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.AmbExpr;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Instanceof;
import polyglot.ast.MethodDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.parse.Name;
import polyglot.types.Flags;
import polyglot.util.Position;

/**
 * NodeFactory for x10 extension.
 * @author vj
 * @author Christian Grothoff
 * @author Igor
 */
public interface X10NodeFactory extends NodeFactory {
	Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
	Async Async(Position pos, Expr place, List clocks, Stmt body);
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
	FutureNode Future(Position pos, TypeNode type);

    ClassDecl ClassDecl(Position pos, Flags flags, String name,
              List properties, Expr ci,
              TypeNode superClass, List interfaces,
              ClassBody body);
	ValueClassDecl ValueClassDecl(Position pos, Flags flags, String name,
            List properties, Expr ci, TypeNode superClass, List interfaces,
								  ClassBody body);
	Await Await(Position pos, Expr expr);
	ArrayConstructor ArrayConstructor(Position pos, TypeNode base,
									  boolean unsafe, boolean isValue,
									  Expr distribution, Expr init);
	Point Point(Position pos, List expr);

	RemoteCall RemoteCall(Position pos, Receiver target, String name,
						  List arguments);
	X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body);
	X10Loop ForEach(Position pos, Formal formal, Expr domain, List clocks,
					Stmt body);
	X10Loop AtEach(Position pos, Formal formal, Expr domain, List clocks,
				   Stmt body);
	Finish Finish(Position pos, Stmt body);

	//TODO: vj -> vj Change to a single call, with default values for missing
	// parameters.
	DepParameterExpr DepParameterExpr(Position pos, List args, Expr cond);
	DepParameterExpr DepParameterExpr(Position pos, List args);
	DepParameterExpr DepParameterExpr(Position pos, Expr cond);
	GenParameterExpr GenParameterExpr(Position pos, List args);
	TypeNode GenericArrayPointwiseOpTypeNode(Position pos, TypeNode typeParam);
    MethodDecl MethodDecl(Position pos, TypeNode thisClause,
            Flags flags, TypeNode returnType, String name,
            List formals, Expr where, List throwTypes, Block body);
    FieldDecl FieldDecl(Position pos, TypeNode thisClause, Flags flags, TypeNode type, String name, Expr init);
	X10ArrayTypeNode X10ArrayTypeNode(Position pos, TypeNode base,
									  boolean isValueType,
									  DepParameterExpr indexedSet);
	X10ArrayAccess X10ArrayAccess(Position pos, Expr a, List indices);
	X10ArrayAccess1 X10ArrayAccess1(Position pos, Expr a, Expr index);

	Tuple Tuple(Position pos, Name p, Name r, List args);
	TypeNode array(TypeNode n, Position pos, int dims);
	Formal Formal(Position pos, Flags flags, TypeNode type, String name);
	Formal Formal(Position pos, Flags flags, TypeNode type, String name,
				  AmbExpr[] vars);
	ParExpr ParExpr(Position pos, Expr e);
	PlaceCast PlaceCast(Position pos, Expr place, Expr target);
    
    ConstructorDecl ConstructorDecl(Position pos, Flags flags, String name,
            Expr retWhereClause, List formals, Expr argWhereClause, List throwTypes, Block body);
    PropertyDecl PropertyDecl(Position pos, Flags flags, TypeNode type, String name);
}

