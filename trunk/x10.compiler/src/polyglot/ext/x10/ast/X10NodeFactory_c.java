package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.*;
import polyglot.ext.jl.ast.Instanceof_c;
import polyglot.ext.jl.ast.NodeFactory_c;
import polyglot.ext.x10.extension.X10InstanceofDel_c;
import polyglot.util.Position;

/**
 * NodeFactory for x10 extension.
 */
public class X10NodeFactory_c extends NodeFactory_c implements X10NodeFactory {
    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overriden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
	
    public X10NodeFactory_c() {
        super(new X10ExtFactory_c(),
              new X10DelFactory_c());
    }

    protected X10NodeFactory_c(ExtFactory extFact) {
        super(extFact);
    }

    public Instanceof Instanceof(Position pos, Expr expr, TypeNode type) {
        Instanceof n = new Instanceof_c(pos, expr, type);
        n = (Instanceof)n.ext(extFactory().extInstanceof());
        return (Instanceof)n.del(new X10InstanceofDel_c());
    }
    
    public Async Async(Position pos, Expr place, Block body) {
        Async a = new Async_c(pos, place, body);
        a = (Async) a.ext(extFactory().extExpr());
        return (Async) a.del(delFactory().delExpr());
    }
    
    public Atomic Atomic(Position pos, Expr place, Block body) {
        Atomic a = new Atomic_c(pos, place, body);
        a = (Atomic) a.ext(extFactory().extExpr());
        return (Atomic) a.del(delFactory().delExpr());
    }
    
    public Future Future(Position pos, Expr place, Expr body) {
        Future f = new Future_c(pos, place, body);
        f = (Future) f.ext(extFactory().extStmt());
        return (Future) f.del(delFactory().delStmt());
    }
    
    public Force Force(Position pos, Expr expr) {
        Force f = new Force_c(pos, expr);
        f = (Force) f.ext(extFactory().extStmt());
        return (Force) f.del(delFactory().delStmt());
    }
    
    public When When(Position pos, List exprs, List statements) {
        When w = new When_c(pos, exprs, statements);
        w = (When) w.ext(extFactory().extStmt());
        return (When) w.del(delFactory().delStmt());
    }

    public Drop Drop(Position pos, List clocks) {
        Drop d = new Drop_c(pos, clocks);
        d = (Drop) d.ext(extFactory().extStmt());
        return (Drop) d.del(delFactory().delStmt());
    }

    public Next Next(Position pos, List clocks) {
        Next n = new Next_c(pos, clocks);
        n = (Next) n.ext(extFactory().extStmt());
        return (Next) n.del(delFactory().delStmt());
    }

    public Now Now(Position pos, Expr expr, Block body) {
        Now n = new Now_c(pos, expr, body);
        n = (Now) n.ext(extFactory().extStmt());
        return (Now) n.del(delFactory().delStmt());
    }

    public Clocked Clocked(Position pos, Expr expr, Block body) {
        Clocked n = new Clocked_c(pos, expr, body);
        n = (Clocked) n.ext(extFactory().extStmt());
        return (Clocked) n.del(delFactory().delStmt());
    }
}
