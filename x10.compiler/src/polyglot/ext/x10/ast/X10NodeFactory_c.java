package polyglot.ext.x10.ast;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.ExtFactory;
import polyglot.ast.Instanceof;
import polyglot.ast.TypeNode;
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
    
    public Future Future(Position pos, Expr place, Expr body) {
        Future f = new Future_c(pos, place, body);
        f = (Future) f.ext(extFactory().extStmt());
        return (Future) f.del(delFactory().delStmt());
    }
    
}
