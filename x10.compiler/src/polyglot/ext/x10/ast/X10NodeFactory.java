package polyglot.ext.x10.ast;

import polyglot.ast.*;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for x10 extension.
 */
public interface X10NodeFactory extends NodeFactory {
    public Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
    public Async Async(Position pos, Expr place, Block body);
    public Atomic Atomic(Position pos, Expr place, Block body);
    public Future Future(Position pos, Expr place, Expr body);
    public Force Force(Position pos, Expr expr);
    public When When(Position pos, List exprs, List statements);
    public Drop Drop(Position pos, List clocks);
    public Next Next(Position pos, List clocks);
    public Now Now(Position pos, Expr expr, Block body);
    public Clocked Clocked(Position pos, Expr expr, Block body);
}
