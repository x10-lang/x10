package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Cast;
import polyglot.ast.ClassBody;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.ExtFactory;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Instanceof;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.QualifierNode;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.jl.ast.Disamb_c;
import polyglot.ext.jl.ast.Instanceof_c;
import polyglot.ext.jl.ast.NodeFactory_c;
import polyglot.ext.jl.parse.Name;
import polyglot.ext.x10.extension.X10InstanceofDel_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * NodeFactory for X10 extension.
 *
 * @author ??
 * @author vj
 * @author Christian Grothoff
 */
public class X10NodeFactory_c extends NodeFactory_c implements X10NodeFactory {

	private static Error marker;

	private static X10NodeFactory_c factory = null;

    public Disamb disamb() {
        return new Disamb_c();
    }
	public static X10NodeFactory_c getFactory() {
		return factory;
	}

	public X10NodeFactory_c() {
		super(new X10ExtFactory_c(), new X10DelFactory_c());
		factory = this;
	}

	protected X10NodeFactory_c(ExtFactory extFact) {
		super(extFact);
	}
    
     public AmbTypeNode AmbTypeNode(Position pos, QualifierNode qualifier, String name) {
            AmbTypeNode n = new X10AmbTypeNode_c(pos, qualifier, name);
            n = (AmbTypeNode)n.ext(extFactory().extAmbTypeNode());
            n = (AmbTypeNode)n.del(delFactory().delAmbTypeNode());
            return n;
        }
     public AmbTypeNode AmbTypeNode(Position pos, QualifierNode qualifier, String name,
             DepParameterExpr d) {
         AmbTypeNode n = new X10AmbTypeNode_c(pos, qualifier, name,d);
         n = (AmbTypeNode)n.ext(extFactory().extAmbTypeNode());
         n = (AmbTypeNode)n.del(delFactory().delAmbTypeNode());
         return n;
     }


       
	public Instanceof Instanceof(Position pos, Expr expr, TypeNode type) {
		Instanceof n = new Instanceof_c(pos, expr, type);
		n = (Instanceof) n.ext(extFactory().extInstanceof());
		return (Instanceof) n.del(new X10InstanceofDel_c());
	}

	public Async Async(Position pos, Expr place, List clocks, Stmt body) {
		Async a = new Async_c(pos, place, clocks, body);
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

	/**
	 * Called when a future X has been parsed, where X should be a type.
	 * TODO cvp - do these nodes need specific delegate and extension
	 * objects?
	 */
	public FutureNode Future(Position pos, TypeNode type) {
		return new FutureNode_c(pos, type);
	}

	/**
	 * Called when a nullable X has been parsed, where X should be a type.
	 * TODO cvp - do these nodes need specific delegate and extension
	 * objects?
	 */
	public NullableNode Nullable(Position pos, TypeNode type) {
		NullableNode n = new NullableNode_c(pos, type);
		X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
		n = (NullableNode) n.ext(ext_fac.extNullableNodeImpl());
		return n;
	}

	public ValueClassDecl ValueClassDecl(Position pos, Flags flags,
										 String name, TypeNode superClass,
										 List interfaces, ClassBody body)
	{
		return new ValueClassDecl_c(pos, flags, name, superClass, interfaces,
									body);
	}

	public Await Await(Position pos, Expr expr) {
		Await n = new Await_c(pos, expr);
		n = (Await) n.ext(extFactory().extStmt());
		return (Await) n.del(delFactory().delStmt());
	}

	public X10ArrayAccess1 X10ArrayAccess1(Position pos, Expr array,
										   Expr index)
	{
		X10ArrayAccess1 n = new X10ArrayAccess1_c(pos, array, index);
		n = (X10ArrayAccess1) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess1) n.del(delFactory().delArrayAccess());
	}

	public X10ArrayAccess X10ArrayAccess(Position pos, Expr array,
										 List/*<Expr>*/ index)
	{
		// return Call(pos, array, "get", index);
		X10ArrayAccess n = new X10ArrayAccess_c(pos, array, index);
		n = (X10ArrayAccess) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess) n.del(delFactory().delArrayAccess());
	}

	/*
	public ArrayAccessAssign ArrayAccessAssign(Position pos,
											   X10ArrayAccess left,
											   Assign.Operator op, Expr right)
	{
		ArrayAccessAssign n = new X10ArrayAccessAssign_c(pos, left, op, right);
		n = (ArrayAccessAssign) n.ext(extFactory().extArrayAccessAssign());
		return (ArrayAccessAssign) n.del(delFactory().delArrayAccessAssign());
	}
	*/

	public ArrayConstructor ArrayConstructor(Position pos, TypeNode base,
											 boolean unsafe, boolean isValue,
											 Expr d, Expr i)
	{
		ArrayConstructor n = new ArrayConstructor_c(pos, base, unsafe, isValue,
													d, i);
		n = (ArrayConstructor) n.ext(extFactory().extExpr());
		return (ArrayConstructor) n.del(delFactory().delExpr());
	}

	public Point Point(Position pos, List exprs) {
		Point n = new Point_c(pos, exprs);
		n = (Point) n.ext(extFactory().extExpr());
		return (Point) n.del(delFactory().delExpr());
	}

	public RemoteCall RemoteCall(Position pos, Receiver target, String name,
								 List arguments)
	{
		RemoteCall n = new RemoteCall_c(pos, target, name, arguments);
		n = (RemoteCall) n.ext(extFactory().extExpr());
		return (RemoteCall) n.del(delFactory().delExpr());
	}

	public Call Call(Position pos, Receiver target, String name, List args) {
		Call n = new X10Call_c(pos, target, name, args);
		n = (Call) n.ext(extFactory().extExpr());
		return (Call) n.del(delFactory().delExpr());
	}

	public X10Loop AtEach(Position pos, Formal formal, Expr domain,
						  List clocks, Stmt body)
	{
		X10Loop n = new AtEach_c(pos, formal, domain, clocks, body);
		n = (X10Loop) n.ext(extFactory().extStmt());
		return (X10Loop) n.del(delFactory().delStmt());
	}

	public X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body)
	{
		X10Loop n = new ForLoop_c(pos, formal, domain, body);
		n = (X10Loop) n.ext(extFactory().extStmt());
		return (X10Loop) n.del(delFactory().delStmt());
	}

	public X10Loop ForEach(Position pos, Formal formal, Expr domain,
						   List clocks, Stmt body)
	{
		X10Loop n = new ForEach_c(pos, formal, domain, clocks, body);
		n = (X10Loop) n.ext(extFactory().extStmt());
		return (X10Loop) n.del(delFactory().delStmt());
	}

	public Finish Finish(Position pos, Stmt body) {
		Finish n = new Finish_c(pos, body);
		n = (Finish) n.ext(extFactory().extStmt());
		return (Finish) n.del(delFactory().delStmt());
	}

	public DepParameterExpr DepParameterExpr(Position pos, List l) {
		DepParameterExpr n = new DepParameterExpr_c(pos, l);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}

	public DepParameterExpr DepParameterExpr(Position pos, Expr e) {
		DepParameterExpr n = new DepParameterExpr_c(pos, e);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}

	public DepParameterExpr DepParameterExpr(Position pos, List l, Expr e) {
		DepParameterExpr n = new DepParameterExpr_c(pos, l, e);
		n = (DepParameterExpr) n.ext(extFactory().extStmt());
		return (DepParameterExpr) n.del(delFactory().delStmt());
	}

	public GenParameterExpr GenParameterExpr(Position pos, List l) {
		List cpy = new LinkedList();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			cpy.add(this.CanonicalTypeNode(pos, (Type) it.next()));
		}
		GenParameterExpr n = new GenParameterExpr_c(pos, cpy);
		n = (GenParameterExpr) n.ext(extFactory().extStmt());
		return (GenParameterExpr) n.del(delFactory().delStmt());
	}

	public TypeNode GenericArrayPointwiseOpTypeNode(Position pos,
													TypeNode typeParam)
	{
		List l = new LinkedList();
		l.add(typeParam);
		GenParameterExpr gen = new GenParameterExpr_c(pos, l);
		gen = (GenParameterExpr) gen.ext(extFactory().extStmt());
		gen = (GenParameterExpr) gen.del(delFactory().delStmt());
		X10TypeNode tn =
			(X10TypeNode) this.CanonicalTypeNode(pos,
				X10TypeSystem_c.getFactory().GenericArrayPointwiseOp(),gen,null);
		return tn;
	}

	public X10TypeNode ParametricTypeNode(Position pos,
												 X10TypeNode t,
												 GenParameterExpr g,
												 DepParameterExpr d)
	{
	
       return t.dep(g,d);
	}
	public TypeNode TypeNode(Position pos) {
	    TypeNode n = new X10TypeNode_c(pos);
	    n = (TypeNode) n.ext(extFactory().extTypeNode());
	    return (TypeNode) n.del(delFactory().delTypeNode());
	}

	public X10ArrayTypeNode X10ArrayTypeNode(Position pos, TypeNode base,
											 boolean isValueType,
											 DepParameterExpr indexedSet)
	{
		X10ArrayTypeNode n = new X10ArrayTypeNode_c(pos, base, isValueType,
													indexedSet);
		n = (X10ArrayTypeNode) n.ext(extFactory().extTypeNode());
		return (X10ArrayTypeNode) n.del(delFactory().delTypeNode());
	}

	public Assign Assign(Position pos, Expr left, Assign.Operator op,
						 Expr right)
	{
		if (left instanceof X10ArrayAccess) {
			return X10ArrayAccessAssign(pos, (X10ArrayAccess) left, op, right);
		} else if (left instanceof X10ArrayAccess1) {
			return X10ArrayAccess1Assign(pos, (X10ArrayAccess1) left, op,
										 right);
		}
		return super.Assign(pos, left, op, right);
	}

	public X10ArrayAccessAssign X10ArrayAccessAssign(Position pos,
													 X10ArrayAccess left,
													 Assign.Operator op,
													 Expr right)
	{
		X10ArrayAccessAssign n = new X10ArrayAccessAssign_c(pos, left, op,
															right);
		n = (X10ArrayAccessAssign) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccessAssign) n.del(delFactory().delArrayAccess());
	}

	public X10ArrayAccess1Assign X10ArrayAccess1Assign(Position pos,
													   X10ArrayAccess1 left,
													   Assign.Operator op,
													   Expr right)
	{
		X10ArrayAccess1Assign n = new X10ArrayAccess1Assign_c(pos, left, op,
															  right);
		n = (X10ArrayAccess1Assign) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess1Assign) n.del(delFactory().delArrayAccess());
	}

	/*
	class AssignableCall_c extends Call_c implements Assign {
		protected Expr left, right;
		protected Assign.Operator op;
		protected Position pos;
		AssignableCall_c(Position pos, Variable left, Assign.Operator op,
						 Expr right, Receiver array, List args)
		{
			super(pos, array, "set", args);
			this.left = left;
			this.right = right;
			this.op = op;
		}
		public Expr left() { return this.left; }
		public Expr right() { return this.right; }
		public Assign.Operator operator() { return this.op; }
		public Assign operator(Assign.Operator op) {
			AssignableCall_c n = (AssignableCall_c) copy();
			n.op = op;
			return n;
		}
		public Assign left(Expr left) {
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

	public Assign X10ArrayAccess1Assign(Position pos, X10ArrayAccess1 left,
										Assign.Operator op, Expr right)
	{
		if (op != Assign.ASSIGN)
			throw new Error("X10 arrays do not yet support assignment operators.");
		Expr array = left.array();
		Expr index = left.index();
		// TODO NOW: vj change implementation of X10ArrayAcess in
		// X10NodeFactory to create this node to begin with.
		List args = new ArrayList(2);
		args.add(right); args.add(index);
		return new AssignableCall_c(pos, left, op, right, array, args);
	}

	public Assign X10ArrayAccessAssign(Position pos, X10ArrayAccess left,
									   Assign.Operator op, Expr right)
	{
		if (op != Assign.ASSIGN)
			throw new Error("X10 arrays do not yet support assignment operators.");
		Expr array = left.array();
		List args = left.index();
		args.add(0, right);
		return new AssignableCall_c(pos, left, op, right, array, args);
	}
	*/

	public Binary Binary(Position pos, Expr left, Binary.Operator op,
						 Expr right)
	{
		Binary n = new X10Binary_c(pos, left, op, right);
		n = (Binary) n.ext(extFactory().extBinary());
		n = (Binary) n.del(delFactory().delBinary());
		return n;
	}

	public Unary Unary(Position pos, Unary.Operator op, Expr expr) {
		boolean incOp = (op == Unary.POST_INC || op == Unary.PRE_INC ||
						 op == Unary.POST_DEC || op == Unary.PRE_DEC);
		if (incOp && expr instanceof X10ArrayAccess) {
			return X10ArrayAccessUnary(pos, op, (X10ArrayAccess) expr);
		} else if (incOp && expr instanceof X10ArrayAccess1) {
			return X10ArrayAccess1Unary(pos, op, (X10ArrayAccess1) expr);
		}
		Unary n = new X10Unary_c(pos, op, expr);
		n = (Unary) n.ext(extFactory().extUnary());
		n = (Unary) n.del(delFactory().delUnary());
		return n;
	}

	public X10ArrayAccessUnary X10ArrayAccessUnary(Position pos,
												   Unary.Operator op,
												   X10ArrayAccess expr)
	{
		X10ArrayAccessUnary n = new X10ArrayAccessUnary_c(pos, op, expr);
		n = (X10ArrayAccessUnary) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccessUnary) n.del(delFactory().delArrayAccess());
	}

	public X10ArrayAccess1Unary X10ArrayAccess1Unary(Position pos,
													 Unary.Operator op,
													 X10ArrayAccess1 expr)
	{
		X10ArrayAccess1Unary n = new X10ArrayAccess1Unary_c(pos, op, expr);
		n = (X10ArrayAccess1Unary) n.ext(extFactory().extArrayAccess());
		return (X10ArrayAccess1Unary) n.del(delFactory().delArrayAccess());
	}

	public Tuple Tuple(Position pos, Name p, Name r, List a) {
		Tuple n = new Tuple_c(pos, p, r, a);
		n = (Tuple) n.ext(extFactory().extCall());
		n = (Tuple) n.del(delFactory().delCall());
		return n;
	}

	/**
	 * Return a TypeNode representing a <code>dims</code>-dimensional
	 * array of <code>n</code>.
	 */
	public TypeNode array(TypeNode n, Position pos, int dims) {
		if (dims > 0) {
			if (n instanceof CanonicalTypeNode) {
				Type t = ((CanonicalTypeNode) n).type();
				return CanonicalTypeNode(pos,
						X10TypeSystem_c.getFactory().arrayOf(t, dims));
			}
			return ArrayTypeNode(pos, array(n, pos, dims - 1));
		}
		return n;
	}

	public Formal Formal(Position pos, Flags flags, TypeNode type, String name,
						 AmbExpr[] vars)
	{
		Formal n = new X10Formal_c(pos, flags, type, name, vars);
		n = (Formal) n.ext(extFactory().extFormal());
		n = (Formal) n.del(delFactory().delFormal());
		return n;
	}

	public Formal Formal(Position pos, Flags flags, TypeNode type, String name)
	{
		return Formal(pos, flags, type, name, null);
	}

	public ParExpr ParExpr(Position pos, Expr expr) {
		ParExpr n = new ParExpr_c(pos, expr);
		n = (ParExpr) n.ext(extFactory().extExpr());
		return (ParExpr) n.del(delFactory().delExpr());
	}

	public PlaceCast PlaceCast(Position pos, Expr place, Expr expr) {
		PlaceCast n = new PlaceCast_c(pos, place, expr);
		n = (PlaceCast) n.ext(extFactory().extExpr());
		return (PlaceCast) n.del(delFactory().delExpr());
	}

	public Field Field(Position pos, Receiver target, String name) {
		Field n = new X10Field_c(pos, target, name);
		n = (Field) n.ext(extFactory().extField());
		n = (Field) n.del(delFactory().delField());
		return n;
	}

	public FieldDecl FieldDecl(Position pos, Flags flags, TypeNode type,
							   String name, Expr init)
	{
		FieldDecl n = new X10FieldDecl_c(pos, flags, type, name, init);
		n = (FieldDecl) n.ext(extFactory().extFieldDecl());
		n = (FieldDecl) n.del(delFactory().delFieldDecl());
		return n;
	}

	public Cast Cast(Position pos, TypeNode type, Expr expr) {
		Cast n = new X10Cast_c(pos, type, expr);
		n = (Cast)n.ext(extFactory().extCast());
		n = (Cast)n.del(delFactory().delCast());
		return n;
	}

	public MethodDecl MethodDecl(Position pos, Flags flags,
								 TypeNode returnType, String name,
								 List formals, List throwTypes, Block body)
	{
		MethodDecl n = new X10MethodDecl_c(pos, flags, returnType, name,
										   formals, throwTypes, body);
		n = (MethodDecl)n.ext(extFactory().extMethodDecl());
		n = (MethodDecl)n.del(delFactory().delMethodDecl());
		return n;
	}
    public MethodDecl MethodDecl(Position pos, Flags flags,
             TypeNode returnType, String name,
             List formals, Expr e, List throwTypes, Block body)
{
MethodDecl n = new X10MethodDecl_c(pos, flags, returnType, name,
                       formals, e, throwTypes, body);
n = (MethodDecl)n.ext(extFactory().extMethodDecl());
n = (MethodDecl)n.del(delFactory().delMethodDecl());
return n;
}

	public LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type,
							   String name, Expr init)
	{
		LocalDecl n = new X10LocalDecl_c(pos, flags, type, name, init);
		n = (LocalDecl)n.ext(extFactory().extLocalDecl());
		n = (LocalDecl)n.del(delFactory().delLocalDecl());
		return n;
	}
     public ConstructorDecl ConstructorDecl(Position pos, Flags flags, String name,  List formals, List throwTypes, Block body) {
            ConstructorDecl n = new X10ConstructorDecl_c(pos, flags, name,  formals, throwTypes, body);
            n = (ConstructorDecl)n.ext(extFactory().extConstructorDecl());
            n = (ConstructorDecl)n.del(delFactory().delConstructorDecl());
            return n;
        }
    public ConstructorDecl ConstructorDecl(Position pos, Flags flags, String name, Expr e, List formals, List throwTypes, Block body) {
        ConstructorDecl n = new X10ConstructorDecl_c(pos, flags, name, e, formals, throwTypes, body);
        n = (ConstructorDecl)n.ext(extFactory().extConstructorDecl());
        n = (ConstructorDecl)n.del(delFactory().delConstructorDecl());
        return n;
    }
    public CanonicalTypeNode CanonicalTypeNode(Position pos, Type type) {
       // Report.report(1,"X10NodeFactory_c: Golden:" + type + type.getClass());
        if (! type.isCanonical()) {
            throw new InternalCompilerError("Cannot construct a canonical " +
                "type node for a non-canonical type.");
        }

        CanonicalTypeNode n = new X10CanonicalTypeNode_c(pos, type);
        n = (CanonicalTypeNode)n.ext(extFactory().extCanonicalTypeNode());
        n = (CanonicalTypeNode)n.del(delFactory().delCanonicalTypeNode());
        return n;
    }
    public CanonicalTypeNode CanonicalTypeNode(Position pos, Type type, GenParameterExpr gen, DepParameterExpr dep) {
        // Report.report(1,"X10NodeFactory_c: Golden:" + type + type.getClass());
         if (! type.isCanonical()) {
             throw new InternalCompilerError("Cannot construct a canonical " +
                 "type node for a non-canonical type.");
         }

         CanonicalTypeNode n = new X10CanonicalTypeNode_c(pos, type,gen,dep);
         n = (CanonicalTypeNode)n.ext(extFactory().extCanonicalTypeNode());
         n = (CanonicalTypeNode)n.del(delFactory().delCanonicalTypeNode());
         return n;
     }
}

