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
    Async Async(Position pos, Expr place, Block body);
    Atomic Atomic(Position pos, Expr place, Block body);
    Future Future(Position pos, Expr place, Expr body);
    Here Here(Position pos);
 
    When When(Position pos, List exprs, List statements);
    Drop Drop(Position pos, List clocks);
    Next Next(Position pos, List clocks);
    Now Now(Position pos, Expr expr, Block body);
    Clocked Clocked(Position pos, Expr expr, Block body);
    
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
    
}
