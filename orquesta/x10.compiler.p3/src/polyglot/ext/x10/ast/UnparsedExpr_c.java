package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.AmbExpr;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.x10.ast.UnparsedExpr_c.OperatorInstance.FormalElement;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.MethodInstance;
import polyglot.types.ObjectType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.util.WorkList;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

public class UnparsedExpr_c extends Expr_c {

    protected List<Expr> elements;

    public UnparsedExpr_c(Position pos, List<Expr> elements) {
	super(pos);
	assert(elements != null);
	this.elements = TypedList.copyAndCheck(elements, Expr.class, true);
    }

    /** Get the elements of the initializer. */
    public List<Expr> elements() {
	return this.elements;
    }

    /** Set the elements of the initializer. */
    public UnparsedExpr_c elements(List<Expr> elements) {
	UnparsedExpr_c n = (UnparsedExpr_c) copy();
	n.elements = TypedList.copyAndCheck(elements, Expr.class, true);
	return n;
    }

    /** Reconstruct the initializer. */
    protected UnparsedExpr_c reconstruct(List<Expr> elements) {
	if (! CollectionUtil.allEqual(elements, this.elements)) {
	    UnparsedExpr_c n = (UnparsedExpr_c) copy();
	    n.elements = TypedList.copyAndCheck(elements, Expr.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the initializer. */
    public Node visitChildren(NodeVisitor v) {
	List<Expr> elements = visitList(this.elements, v);
	return reconstruct(elements);
    }

    /** Type check the initializer. */
    public Node typeCheckOverride(TypeChecker tc) throws SemanticException {
	List<Boolean> reallyExpr = new ArrayList<Boolean>(elements.size());
	List<Expr> newExprs = new ArrayList<Expr>(elements.size());
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	Type type = null;

	for (Expr e : elements) {
	    Expr e2;
	    if (e instanceof AmbExpr) {
		AmbExpr a = (AmbExpr) e;
		e2 = a;
		try {
		    e2 = (Expr) e2.del().disambiguate(tc);
		    e2 = (Expr) e2.del().typeCheck(tc);
		    e2 = (Expr) e2.del().checkConstants(tc);
		    newExprs.add(e2);
		    reallyExpr.add(true);
		}
		catch (SemanticException ex) {
		    newExprs.add(a);
		    reallyExpr.add(false);
		}
	    }
	    else {
		e2 = (Expr) this.visitChild(e, tc);
		newExprs.add(e2);
		reallyExpr.add(true);
	    }
	}
	
	return parse(newExprs, reallyExpr, ts);
    }

    interface Prec { boolean lessThan(Prec p); }
    interface OperatorInstance extends MethodInstance {
	public Type returnType();
	Name name();
	List<Element> elements();
	
	static class Element {
	}
	static class FormalElement extends Element {
	    Type type;
	    Prec prec;
	    Type type() { return type; }
	    Prec prec() { return prec; }
	}
	static class IdElement extends Element {
	    String id;
	    String id() { return id; }
	}
    }
    interface FooType extends StructType {
	List<OperatorInstance> operators();
    }
    
    void allOperators(Type t, List<OperatorInstance> ois) {
	if (t instanceof FooType) {
	    FooType ft = (FooType) t;
	    ois.addAll(ft.operators());
	    if (ft instanceof ObjectType) {
		ObjectType ot = (ObjectType) ft;
		allOperators(ot.superClass(), ois);
		for (Type ti : ot.interfaces()) {
		    allOperators(ti, ois);
		}
	    }
	}
    }

    private Expr parse(List<Expr> newExprs, List<Boolean> reallyExpr, X10TypeSystem ts) throws SemanticException {
	List<Type> types = new ArrayList<Type>();
	Set<Name> operators = new HashSet<Name>();
	for (int i = 0; i < newExprs.size(); i++) {
	    if (reallyExpr.get(i))
		types.add(newExprs.get(i).type());
	    else
		operators.add(((AmbExpr) newExprs.get(i)).name().id());
	}
	
	// build grammar from types and operators
	List<OperatorInstance> ois = new ArrayList<OperatorInstance>();
	collectOperators(types, operators, ois);
	buildLR0Machine(ois);
	
	// GLR parse

	return this;
    }

    static class LRItem {
	OperatorInstance rule;
	int dot;
	
	public LRItem(OperatorInstance rule2, int i) {
	    rule = rule2;
	    dot = i;
	}
	
	LRItem advance() {
	    return new LRItem(rule, dot+1);
	}
	
	public int hashCode() {
	    return rule.hashCode() + dot;
	}
	
	public boolean equals(Object o) {
	    if (o instanceof LRItem) {
		LRItem i = (LRItem) o;
		return rule == i.rule && dot == i.dot;
	    }
	    return false;
	}
	
	public String toString() {
	    return rule + " @ " + dot;
	}
    }
    
    private void buildLR0Machine(List<OperatorInstance> ois) {
	Set<LRItem> itemSet = new HashSet<LRItem>();
	
	
    }

    // Collect all operators that could match the string of expressions and names.
    private void collectOperators(List<Type> types, Set<Name> operators, List<OperatorInstance> mis) {
	LinkedList<Type> worklist = new LinkedList<Type>();
	worklist.addAll(types);
	Set<Type> inWorklist = new HashSet<Type>();
	
	while (! worklist.isEmpty()) {
	    Type t = worklist.removeFirst();

	    List<OperatorInstance> ois = new ArrayList<OperatorInstance>();
	    allOperators(t, ois);

	    for (OperatorInstance oi : ois) {
		boolean match = true;

		for (OperatorInstance.Element e : oi.elements()) {
		    if (e instanceof OperatorInstance.IdElement) {
			String id = ((OperatorInstance.IdElement) e).id();
			if (! operators.contains(id)) {
			    match = false;
			    break;
			}
		    }
		}
		
		if (match) {
		    mis.add(oi);
		    
		    for (OperatorInstance.Element e : oi.elements()) {
			if (e instanceof OperatorInstance.FormalElement) {
			    Type formalType = ((FormalElement) e).type();
			    if (! inWorklist.contains(formalType)) {
				worklist.add(formalType);
				inWorklist.add(formalType);
			    }
			}
		    }
		    
		    Type returnType = oi.returnType();
		    if (! inWorklist.contains(returnType)) {
			worklist.add(returnType);
			inWorklist.add(returnType);
		    }
		}
	    }
	}
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
	return child.type();
    }

    public String toString() {
	return "(...)";
    }

    /** Write the initializer to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("(");

	for (Iterator<Expr> i = elements.iterator(); i.hasNext(); ) {
	    Expr e = i.next();

	    print(e, w, tr);

	    if (i.hasNext()) {
		w.allowBreak(0, " ");
	    }
	}

	w.write(")");
    }

    public Term firstChild() {
	return listChild(elements, null);
    }

    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
	v.visitCFGList(elements, this, EXIT);
	return succs;
    }
}
