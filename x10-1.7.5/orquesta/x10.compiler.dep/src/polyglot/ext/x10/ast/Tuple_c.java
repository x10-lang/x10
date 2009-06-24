/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 19, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.parse.Name;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.main.Report;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** An immutable representation of the X10 construct [e1,..., ek ]. 
 * If the type of ei is region, then this represents region.factory.region(new region[] {e1,...,ek}).
 * If the type of ei is int, then this represents point.factory.point(new int[] {e1,...,ek}).
 * Implementation strategy: Pretend that the data is being carried for two calls, with a common
 * argument list. During typechecking, determine which of these calls to resolve into. Need to make sure that 
 * the data for these calls exists, with the same effect as would be obtained by running all previous phases,
 * when the new call is established.
 * @author vj Jan 19, 2005
 * 
 */
public class Tuple_c extends Expr_c implements Tuple {
	protected List args;
	protected Receiver pointReceiver;
	protected Receiver regionReceiver;
	protected MethodInstance pointMI, regionMI;
	
	
	public Tuple_c(Position pos, Receiver pointReceiver, Receiver regionReceiver, List args) {
		super(pos);
		this.pointReceiver = pointReceiver;
		this.regionReceiver = regionReceiver;
		this.args = args;
		assert (args.size() > 0);
		//Report.report(1, "Tuple_c created:" + pointName + "| " + regionName + "| " + args);
	}
	
	public List arguments() { return args; }
	public Receiver pointReceiver() { return pointReceiver; }
	public Receiver regionReceiver() { return regionReceiver; }
	public MethodInstance pointMI() { return pointMI; }
	public MethodInstance regionMI() { return regionMI; }
	
	/** Type check the expression. Fork into an ArrayAccess if the underlying
	 * array is a Java array, or if the index is an int and not a distribution.
	 * 
	 * */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		//Report.report(1, "Tuple_c.typeCheck:***" + args);
                X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		Type argType = ((Expr) args.get(0)).type();
		if (argType.isInt()) {
			// This is a point construction.
			return nf.Call(position(), pointReceiver, nf.Id(position(), "point"), args).del().typeCheck(tc);
		}
		RectRegionMaker result= 
			(RectRegionMaker) nf.RectRegionMaker(position(), regionReceiver, nf.Id(position(), "region"), args).del().typeCheck(tc);
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return listChild(args, null);
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		// pointReceiver and regionReceiver are AmbReceiver's when they 
		// are created. AmbReceievers are not expressions.
		// So they are not visited during CFG construction.
		v.visitCFGList(args, this, EXIT);
		return succs;
	}
	
	public Tuple_c methodInstance( MethodInstance p, MethodInstance r) {
		Tuple_c n = (Tuple_c) copy();
		n.pointMI = p;
		n.regionMI = r;
		return n;
	}
	public Tuple_c reconstruct(Receiver pointR, Receiver regionR, List args ) {
		if (this.pointReceiver == pointR && this.regionReceiver == regionR && this.args == args)
			return this;
		Tuple_c n = (Tuple_c) copy();
		n.pointReceiver = pointR;
		n.regionReceiver = regionR;
		n.args = args;
		//Report.report(1, "Tuple_c returning (#" + n.hashCode() + ") " + n.args );
		return n;
		
	}

	/** Visit the children -- the two receivers and the argument list. */
	public Node visitChildren(NodeVisitor v) {
	//	Report.report(1, "Tuple_c: (#" + hashCode() + ")" + this + " visited by " + v);
		Receiver pointR = (Receiver) visitChild(this.pointReceiver, v);
		Receiver regionR = (Receiver) visitChild(this.regionReceiver, v);
		List args = visitList(this.args, v);
		return reconstruct(pointR, regionR, args);
	}
	public void dump(CodeWriter w) {
		w.write("Tuple: ");
		super.dump(w);
		// throw new InternalError("Ambiguous Node cannot be rewritten.");
	}
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		throw new InternalError("Ambiguous Node cannot be rewritten.");
	}
	// throwTypes, ExceptionCheck, checkConsistency should not be defined, I believe.
	// childExpectedType is needed by AscriptionVisitor; that pass happens after typechecking
	// so we do not need to define the behavior for this method.
	// findTargetType is not needed, the targets are known to be references, and this will be
	// checked by the calls this node resolves to.
}
