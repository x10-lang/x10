package polyglot.ext.x10.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.coffer.types.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for x10 extension.
 */
public interface X10NodeFactory extends NodeFactory {
    public Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
    public Async Async(Position pos, Expr place, Stmt body);
    public Atomic Atomic(Position pos, Expr place, Stmt body);
    public Future Future(Position pos, Expr place, Expr body);
    public Force Force(Position pos, Expr expr);
    public When When(Position pos, List exprs, List statements);
    public Drop Drop(Position pos, List clocks);
    public Next Next(Position pos, List clocks);
    public Now Now(Position pos, Expr expr, Stmt stmt);
    public Clocked Clocked(Position pos, Expr expr, Stmt stmt);
}
