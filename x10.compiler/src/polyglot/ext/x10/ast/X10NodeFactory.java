package polyglot.ext.x10.ast;

import polyglot.ast.*;
import polyglot.util.*;

import java.util.*;

import polyglot.ast.TypeNode;
import polyglot.types.ReferenceType;
import polyglot.types.Flags;
import polyglot.ast.Block;


/**
 * NodeFactory for x10 extension.
 * @author ??
 * @author vj
 */
public interface X10NodeFactory extends NodeFactory {
    Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
    Async Async(Position pos, Expr place, Stmt body);
    Atomic Atomic(Position pos, Expr place, Stmt body);
    Future Future(Position pos, Expr place, Expr body);
    Here Here(Position pos);
 
    /** Return an immutable representation of a 1-armed When. (Additional arms are added
     * by invoking the add method on the returned When.)
     * @param pos
     * @param expr
     * @param statement
     * @return
     */
    When When(Position pos, Expr expr, Stmt statement);
    Drop Drop(Position pos, List clocks);
    Next Next(Position pos);
    Now Now(Position pos, Expr expr, Stmt stmt);
    Clocked Clocked(Position pos, List clocks, Stmt body);
    
    NullableNode Nullable(Position pos, TypeNode type);
    FutureNode Future(Position pos, TypeNode type);
    ParametricTypeNode ParametricTypeNode(Position pos, TypeNode type, DepParameterExpr expr);

    ValueClassDecl ValueClassDecl(Position pos, Flags flags, String name,
				  TypeNode superClass, List interfaces,
				  ClassBody body);
    Await Await(Position pos, Expr expr);
    ArrayConstructor ArrayConstructor( Position pos, TypeNode base, boolean isValue, Expr distribution, Expr init);
    Point Point( Position pos, List expr);
    ReductionCall ScanCall(Position pos, Receiver target, String name, List arguments);
    ReductionCall ReduceCall(Position pos, Receiver target, String name, List arguments);
    
    RemoteCall RemoteCall(Position pos, Receiver target, String name, List arguments);
    X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body);
    X10Loop ForEach(Position pos, Formal formal, Expr domain, Stmt body);
    X10Loop AtEach(Position pos, Formal formal, Expr domain, Stmt body);
    Finish Finish(Position pos, Stmt body);

    //TODO: vj -> vj Change to a single call, with default values for missing parameters.
    DepParameterExpr DepParameterExpr(Position pos, List args, Expr cond);    
    DepParameterExpr DepParameterExpr(Position pos, List args);
    DepParameterExpr DepParameterExpr(Position pos, Expr cond);    
    X10ArrayTypeNode X10ArrayTypeNode(Position pos, TypeNode base, boolean isValueType, 
			DepParameterExpr indexedSet );
    X10ArrayAccess X10ArrayAccess( Position pos, Expr a, List indices);
    X10ArrayAccess1 X10ArrayAccess1( Position pos, Expr a, Expr index);
}
