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

    ValueClassDecl ValueClassDecl(Position pos, Flags flags, String name,
				  TypeNode superClass, List interfaces,
				  ClassBody body);
    Await Await(Position pos, Expr expr);
    ArrayConstructor ArrayConstructor( Position pos, TypeNode type, Expr distribution, Variable formal, Block b);
    Point Point( Position pos, List expr);
    ReductionCall ScanCall(Position pos, Receiver target, String name, List arguments);
    ReductionCall ReduceCall(Position pos, Receiver target, String name, List arguments);
    
    RemoteCall RemoteCall(Position pos, Receiver target, String name, List arguments);
    X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body);
    X10Loop ForEach(Position pos, Formal formal, Expr domain, Stmt body);
    X10Loop AtEach(Position pos, Formal formal, Expr domain, Stmt body);
    Finish Finish(Position pos, Stmt body);
    
}
