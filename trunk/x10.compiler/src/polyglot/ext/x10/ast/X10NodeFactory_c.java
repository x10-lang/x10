package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.*;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.ext.jl.ast.Instanceof_c;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.NodeFactory_c;
import polyglot.ext.x10.extension.X10InstanceofDel_c;
import polyglot.util.Position;
import polyglot.types.Flags;

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
    
    public Async Async(Position pos, Expr place, Stmt body) {
        Async a = new Async_c(pos, place, body);
        a = (Async) a.ext(extFactory().extExpr());
        return (Async) a.del(delFactory().delExpr());
    }
    
    public Atomic Atomic(Position pos, Expr place, Stmt body) {
        Atomic a = new Atomic_c(pos, place, body);
        a = (Atomic) a.ext(extFactory().extExpr());
        return (Atomic) a.del(delFactory().delExpr());
    }
    
    public Future Future(Position pos, Expr place, Expr body) {
        Future f = new Future_c(pos, place, body);
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        f = (Future) f.ext(ext_fac.extFutureImpl());
        return (Future) f.del(delFactory().delStmt());
    }
    
    public Here Here(Position pos) {
        Here f = new Here_c(pos);
        f = (Here) f.ext(extFactory().extStmt());
        return (Here) f.del(delFactory().delStmt());
    }
    
    public When When(Position pos, Expr expr, Stmt statement) {
        When w = new When_c(pos, expr, statement);
        w = (When) w.ext(extFactory().extStmt());
        return (When) w.del(delFactory().delStmt());
    }
    
    public Drop Drop(Position pos, List clocks) {
        Drop d = new Drop_c(pos, clocks);
        d = (Drop) d.ext(extFactory().extStmt());
        return (Drop) d.del(delFactory().delStmt());
    }
    
    public Next Next(Position pos) {
        Next n = new Next_c(pos);
        n = (Next) n.ext(extFactory().extStmt());
        return (Next) n.del(delFactory().delStmt());
    }
    
    public Now Now(Position pos, Expr expr, Stmt stmt) {
        Now n = new Now_c(pos, expr, stmt);
        n = (Now) n.ext(extFactory().extStmt());
        return (Now) n.del(delFactory().delStmt());
    }
    
    
    public Clocked Clocked(Position pos, List clocks, Stmt stmt) {
        Clocked n = new Clocked_c(pos, clocks, stmt);
        n = (Clocked) n.ext(extFactory().extStmt());
        return (Clocked) n.del(delFactory().delStmt());
    }
    
    /** Called when a future X has been parsed, where X should be a type.
     *  TODO cvp - do these nodes need specific delegate and extension objects? 
     */
    public FutureNode Future(Position pos, TypeNode type ) {
        return new FutureNode_c(pos, type);
    }
    
    /** Called when a nullable X has been parsed, where X should be a type.
     *  TODO cvp - do these nodes need specific delegate and extension objects? 
     */
    public NullableNode Nullable(Position pos, TypeNode type ) {
        return new NullableNode_c(pos, type);
    }
    
    public ValueClassDecl ValueClassDecl(Position pos, Flags flags, String name,
            TypeNode superClass, List interfaces,
            ClassBody body) {
        return new ValueClassDecl_c( pos, flags, name, superClass, 
                interfaces, body );
    }
    
    public Await Await(Position pos, Expr expr ) {
        Await n = new Await_c( pos, expr );
        n = (Await) n.ext(extFactory().extStmt());
        return (Await) n.del(delFactory().delStmt());
    }
    
    public ArrayAccess ArrayAccess( Position pos, Expr array, Expr index ) {
        ArrayAccess n = new X10ArrayAccess_c( pos, array, index );
        n = (ArrayAccess) n.ext(extFactory().extArrayAccess());
        return (ArrayAccess) n.del(delFactory().delArrayAccess());
    }
    
    public ArrayAccessAssign ArrayAccessAssign(Position pos, ArrayAccess left, Assign.Operator op, Expr right) {
        ArrayAccessAssign n = new X10ArrayAccessAssign_c(pos, left, op, right);
        n = (ArrayAccessAssign) n.ext(extFactory().extArrayAccessAssign());
        return (ArrayAccessAssign) n.del(delFactory().delArrayAccessAssign());
    }
    
    public ArrayConstructor ArrayConstructor(Position pos, TypeNode base, Expr distribution, Variable formal,
            Block body) {
        ArrayConstructor n = new ArrayConstructor_c(pos, base, distribution, formal, body);
        n = (ArrayConstructor) n.ext(extFactory().extExpr());
        return (ArrayConstructor) n.del(delFactory().delExpr());
    }
    
    public Point Point(Position pos, List exprs) {
        Point n = new Point_c( pos, exprs);
        n = (Point) n.ext(extFactory().extExpr());
        return (Point) n.del(delFactory().delExpr());
    }
    
    public ReductionCall ScanCall(Position pos, Receiver target, String name, List arguments) {
        ReductionCall n = new ReductionCall_c( pos, target, name, arguments, ReductionCall.SCAN);
        n = (ReductionCall) n.ext(extFactory().extExpr());
        return (ReductionCall) n.del(delFactory().delExpr());
    }
    
    public ReductionCall ReduceCall(Position pos, Receiver target, String name, List arguments) {
        ReductionCall n = new ReductionCall_c( pos, target, name, arguments, ReductionCall.REDUCE);
        n = (ReductionCall) n.ext(extFactory().extExpr());
        return (ReductionCall) n.del(delFactory().delExpr());
    }
    
    public RemoteCall RemoteCall(Position pos, Receiver target, String name, List arguments) {
        RemoteCall n = new RemoteCall_c( pos, target, name, arguments);
        n = (RemoteCall) n.ext(extFactory().extExpr());
        return (RemoteCall) n.del(delFactory().delExpr());
        
    }
    
    public X10Loop AtEach(Position pos, Formal formal, Expr domain, Stmt body) {
        X10Loop n = new AtEach_c( pos, formal, domain, body);
        n = (X10Loop) n.ext(extFactory().extStmt());
        return (X10Loop) n.del(delFactory().delStmt());
    }
    
    public X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body) {
        X10Loop n = new ForLoop_c( pos, formal, domain, body);
        n = (X10Loop) n.ext(extFactory().extStmt());
        return (X10Loop) n.del(delFactory().delStmt());
    }
    
    public X10Loop ForEach(Position pos, Formal formal, Expr domain, Stmt body) {
        X10Loop n = new ForEach_c( pos, formal, domain, body);
        n = (X10Loop) n.ext(extFactory().extStmt());
        return (X10Loop) n.del(delFactory().delStmt());
    }
    
    public Finish Finish(Position pos, Stmt body) {
        Finish n = new Finish_c( pos,  body);
        n = (Finish) n.ext(extFactory().extStmt());
        return (Finish) n.del(delFactory().delStmt());
    }
}
