package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.*;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.ast.Formal_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.jl.ast.Instanceof_c;
import polyglot.ext.jl.parse.Name;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.NodeFactory_c;
import polyglot.ext.x10.extension.X10InstanceofDel_c;
import polyglot.util.Position;
import polyglot.types.Flags;
import polyglot.types.Type;
import x10.parser.X10VarDeclarator;

/**
 * NodeFactory for X10 extension.
 * @author ??
 * @author vj
 */
public class X10NodeFactory_c extends NodeFactory_c implements X10NodeFactory {
	
	private static Error marker;
	private static X10NodeFactory_c factory = null;
	public static X10NodeFactory_c getFactory() {
		return factory;
	}
	public X10NodeFactory_c() {
		super(new X10ExtFactory_c(),
				new X10DelFactory_c());
		factory = this;
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
	
    public When.Branch WhenBranch(Position pos, Expr expr, Stmt statement) {
        When.Branch w = new When_c.Branch_c(pos, expr, statement);
        w = (When.Branch) w.ext(extFactory().extStmt());
        return (When.Branch) w.del(delFactory().delStmt());
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
	public FutureNode Future(Position pos,  TypeNode type ) {
		return new FutureNode_c(pos, type);
	}
	
	/** Called when a nullable X has been parsed, where X should be a type.
	 *  TODO cvp - do these nodes need specific delegate and extension objects? 
	 */
	
	public NullableNode Nullable(Position pos,  TypeNode type ) {
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
	public X10ArrayAccess1 X10ArrayAccess1( Position pos, Expr array, Expr index )  {
		X10ArrayAccess1 n = new X10ArrayAccess1_c( pos, array, index );
		n = (X10ArrayAccess1) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess1) n.del(delFactory().delArrayAccess());
	}
	public X10ArrayAccess X10ArrayAccess( Position pos, Expr array, List/*<Expr>*/ index ) {
		// return Call(pos, array, "get", index);
		X10ArrayAccess n = new X10ArrayAccess_c( pos, array, index );
		n = (X10ArrayAccess) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess) n.del(delFactory().delArrayAccess());
		
	}
	/*
	 * public ArrayAccessAssign ArrayAccessAssign(Position pos, X10ArrayAccess left, Assign.Operator op, Expr right) {
		ArrayAccessAssign n = new X10ArrayAccessAssign_c(pos, left, op, right);
		n = (ArrayAccessAssign) n.ext(extFactory().extArrayAccessAssign());
		return (ArrayAccessAssign) n.del(delFactory().delArrayAccessAssign());
	}
	*/
	public ArrayConstructor ArrayConstructor(Position pos,  TypeNode base, boolean unsafe, boolean isValue, Expr d, Expr i ) {
		ArrayConstructor n = new ArrayConstructor_c(pos, base, unsafe, isValue, d, i);
		n = (ArrayConstructor) n.ext(extFactory().extExpr());
		return (ArrayConstructor) n.del(delFactory().delExpr());
	}
	public Point Point( Position pos, List exprs) {
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
	
	public DepParameterExpr DepParameterExpr(Position pos, List l) {
		DepParameterExpr n = new DepParameterExpr_c( pos,  l);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}
	
	public DepParameterExpr DepParameterExpr(Position pos, Expr e) {
		DepParameterExpr n = new DepParameterExpr_c( pos,  e);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}
	public DepParameterExpr DepParameterExpr(Position pos, List l, Expr e) {
		DepParameterExpr n = new DepParameterExpr_c( pos,  l, e);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}
	public ParametricTypeNode ParametricTypeNode( Position pos,  TypeNode node, DepParameterExpr d) {
		ParametricTypeNode n = new ParametricTypeNode_c( pos, node, d);
		n = (ParametricTypeNode) n.ext(extFactory().extTypeNode());
		return (ParametricTypeNode) n.del(delFactory().delTypeNode());
	} 
	public X10ArrayTypeNode X10ArrayTypeNode(Position pos,  TypeNode base, boolean isValueType, 
			DepParameterExpr indexedSet ) {
		X10ArrayTypeNode n = new X10ArrayTypeNode_c( pos, base, isValueType, indexedSet);
		n = (X10ArrayTypeNode) n.ext(extFactory().extTypeNode());
		return (X10ArrayTypeNode) n.del(delFactory().delTypeNode());
	}
	 public Assign Assign(Position pos, Expr left, Assign.Operator op, Expr right) {
        if (left instanceof Local) {
            return LocalAssign(pos, (Local)left, op, right);
        }
        else if (left instanceof Field) {
            return FieldAssign(pos, (Field)left, op, right);
        }
        else if (left instanceof X10ArrayAccess) {
            return X10ArrayAccessAssign(pos, (X10ArrayAccess)left, op, right);
        } else if (left instanceof X10ArrayAccess1) {
        	return X10ArrayAccess1Assign(pos, (X10ArrayAccess1) left, op, right);
        }
        return AmbAssign(pos, left, op, right);
    }
	public X10ArrayAccessAssign X10ArrayAccessAssign(Position pos,  X10ArrayAccess left, Assign.Operator op,
	 		Expr right) {
	 	
		X10ArrayAccessAssign n = new X10ArrayAccessAssign_c( pos, left, op, right );
		n = (X10ArrayAccessAssign) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccessAssign) n.del(delFactory().delArrayAccess());
	}
	 public X10ArrayAccess1Assign X10ArrayAccess1Assign(Position pos,  X10ArrayAccess1 left, Assign.Operator op,
	 		Expr right) {
		X10ArrayAccess1Assign n = new X10ArrayAccess1Assign_c( pos, left, op, right );
		n = (X10ArrayAccess1Assign) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess1Assign) n.del(delFactory().delArrayAccess());
	}
	/*
		class AssignableCall_c extends Call_c implements Assign {
	 		protected Expr left, right;
	 		protected Assign.Operator op;
	 		protected Position pos;
	 		AssignableCall_c(Position pos, Variable left, Assign.Operator op, Expr right, 
	 				Receiver array,  List args) {
	 			super( pos, array, "set", args);
	 			this.left = left;
	 			this.right = right;
	 			this.op = op;
	 		}
	 		public Expr left() { return this.left; }
	 		public Expr right() { return this.right;}
	 		public Assign.Operator operator() { return this.op;}
	 		public Assign operator(Assign.Operator op) {
	 		  AssignableCall_c n = (AssignableCall_c) copy();
	 		  n.op = op;
	 		  return n;
	 		}
	 		public Assign left (Expr left ) {
		 		  AssignableCall_c n = (AssignableCall_c) copy();
		 		  n.left = left;
		 		  return n;
		 		}
	 		public Assign right(Expr right) {
		 		  AssignableCall_c n = (AssignableCall_c) copy();
		 		  n.right = right;
		 		  return n;
		 		}
	 		public boolean throwsArithmeticException() {
	 		   return op == DIV_ASSIGN || op == MOD_ASSIGN;
	 		}
	 	}
	 
	 public Assign X10ArrayAccess1Assign(Position pos,  X10ArrayAccess1 left, Assign.Operator op,
	 		Expr right) {
	 
	 	if (op != Assign.ASSIGN) throw new Error("X10 arrays do not yet support assignment operators.");
		Expr array = left.array();
		Expr index = left.index();
		
		// TODO NOW: vj change implementation of X10ArrayAcess in X10NodeFactory to create this node to begin with.
		List args = new ArrayList(2);
		args.add( right);
		args.add( index );
		return new AssignableCall_c(pos, left, op, right, array, args);
	}
	 public Assign X10ArrayAccessAssign(Position pos,  X10ArrayAccess left, Assign.Operator op,
	 		Expr right) {
	 	if (op != Assign.ASSIGN) throw new Error("X10 arrays do not yet support assignment operators.");
	 	Expr array = left.array();
	 	List args = left.index();
	 	args.add(0, right);
	 	return new AssignableCall_c(pos, left, op, right, array, args);

	}
	 */
	 
    public Binary Binary(Position pos, Expr left, Binary.Operator op, Expr right) {
        Binary n = new X10Binary_c(pos, left, op, right);
        n = (Binary)n.ext(extFactory().extBinary());
        n = (Binary)n.del(delFactory().delBinary());
        return n;
    }
    public Tuple Tuple(Position pos, Name p, Name r, List a) {
        Tuple n = new Tuple_c( pos, p, r, a);
        n = (Tuple)n.ext(extFactory().extCall());
        n = (Tuple)n.del(delFactory().delCall());
        return n; 
    }
    /**
     * Return a TypeNode representing a <code>dims</code>-dimensional
     * array of <code>n</code>.
     */
    public TypeNode array(TypeNode n, Position pos, int dims) {
    	if (dims > 0) {
    		if (n instanceof CanonicalTypeNode) {
    			Type t = ((CanonicalTypeNode) n).type ();
    			return CanonicalTypeNode (pos, X10TypeSystem_c.getFactory().arrayOf (t, dims));
    		}
    		return ArrayTypeNode (pos, array (n, pos, dims - 1));
    	}
    	return n;
    }
    public Formal Formal( Position pos, TypeNode type, X10VarDeclarator v) {
    	Formal n = new X10Formal_c( pos, type, v);
    	 n = (Formal)n.ext(extFactory().extFormal());
         n = (Formal)n.del(delFactory().delFormal());
         return n;
    	
    }
    public Formal Formal(Position pos, Flags flags, TypeNode type, String name) {
    	X10VarDeclarator v = new X10VarDeclarator(pos, name);
    	v.setFlag( flags );
        Formal n = Formal(pos, type, v);
        n = (Formal)n.ext(extFactory().extFormal());
        n = (Formal)n.del(delFactory().delFormal());
        return n;
    }
}
