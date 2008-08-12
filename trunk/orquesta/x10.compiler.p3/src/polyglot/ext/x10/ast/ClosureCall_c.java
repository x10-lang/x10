/*
 * Created on Apr 12, 2007
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.ClosureInstance;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.X10MethodInstance_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ErrorRef_c;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class ClosureCall_c extends Expr_c implements ClosureCall {
    protected Expr target;
    protected List<TypeNode> typeArgs;
    protected List<Expr> arguments;

    protected ClosureInstance ci;

    public ClosureCall_c(Position pos, Expr target, List<TypeNode> typeArgs, List<Expr> arguments) {
	super(pos);
	assert (target != null && arguments != null);
	this.target= target;
	this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
    }

    public Term firstChild() {
	if (!(target instanceof Closure)) {
	    return ((Term) target);
	}
	return listChild(typeArgs, listChild(arguments, null));
    }

    @Override
    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
	List<Term> args = new ArrayList<Term>();
	args.addAll(typeArgs);
	args.addAll(arguments);
	
	if (!(target instanceof Closure)) { // Don't visit a literal closure here
	    Term t = (Term) target;
            if (args.isEmpty()) {
                v.visitCFG(t, this, EXIT);
            }
            else {
                v.visitCFG(t, listChild(args, null), ENTRY);
            }
	}
	v.visitCFGList(args, this, EXIT);
	return succs;
    }

    public Expr target() {
	return target;
    }

    public ClosureCall target(Expr target) {
	ClosureCall_c n= (ClosureCall_c) copy();
	n.target= target;
	return n;
    }

    /** Get the method instance of the call. */
    public ClosureInstance closureInstance() {
	return this.ci;
    }

    /** Set the method instance of the call. */
    public ClosureCall closureInstance(ClosureInstance ci) {
	if (ci == this.ci)
	    return this;
	ClosureCall_c n = (ClosureCall_c) copy();
	n.ci= ci;
	return n;
    }

    public ProcedureInstance<?> procedureInstance() {
	return this.ci;
    }

    /** Get the actual arguments of the call. */
    public List<Expr> arguments() {
	return this.arguments;
    }

    /** Set the actual arguments of the call. */
    public ProcedureCall arguments(List<Expr> arguments) {
	ClosureCall_c n= (ClosureCall_c) copy();
	n.arguments= TypedList.copyAndCheck(arguments, Expr.class, true);
	return n;
    }
    
    /** Get the actual arguments of the call. */
    public List<TypeNode> typeArgs() {
	    return this.typeArgs;
    }
    
    /** Set the actual arguments of the call. */
    public ProcedureCall typeArgs(List<TypeNode> typeArgs) {
	    ClosureCall_c n= (ClosureCall_c) copy();
	    n.typeArgs= TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	    return n;
    }

    /** Reconstruct the call. */
    protected ClosureCall_c reconstruct(Expr target, List<TypeNode> typeArgs, List<Expr> arguments) {
	if (target != this.target || !CollectionUtil.allEqual(typeArgs, this.typeArgs) || !CollectionUtil.allEqual(arguments, this.arguments)) {
	    ClosureCall_c n= (ClosureCall_c) copy();
	    n.target= target;
	    n.typeArgs= TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	    n.arguments= TypedList.copyAndCheck(arguments, Expr.class, true);
	    return n;
	}
	return this;
    }

    /** Visit the children of the call. */
    public Node visitChildren(NodeVisitor v) {
	Expr target= (Expr) visitChild(this.target, v);
	List typeArgs= visitList(this.typeArgs, v);
	List arguments= visitList(this.arguments, v);
	return reconstruct(target, typeArgs, arguments);
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
	ClosureCall_c n= (ClosureCall_c) super.buildTypes(tb);

	X10TypeSystem x10ts = (X10TypeSystem) tb.typeSystem();

	ClosureInstance ci = x10ts.createClosureInstance(position(), new ErrorRef_c<ClosureDef>(x10ts, position(), "Cannot get ClosureDef before type-checking closure call."));
	return n.closureInstance(ci);
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
	Type targetType = target.type();

	X10TypeSystem x10ts = (X10TypeSystem) tc.typeSystem();

	if (! x10ts.isFunction(targetType)) {
	    throw new SemanticException("The target of a closure call must be a function type, not " + targetType + ".", target.position());
	}
	
	ClosureType closureType = x10ts.toFunction(targetType);
	
	if (closureType == null) {
	    throw new SemanticException("The target of a closure call must be a function type, not " + targetType + ".", target.position());
	}
	
	class RefTransform implements Transformation<Type, Ref<? extends Type>> {
	    public Ref<? extends Type> transform(Type o) {
		return Types.ref(o);
	    }
	};
	
	ClosureInstance ci = closureType.closureInstance();

	List<Type> actualTypes = new ArrayList<Type>();
	for (Expr ei : arguments) {
	    actualTypes.add(ei.type());
	}

	ClosureInstance ci2 = X10MethodInstance_c.instantiate(ci, targetType, Collections.EMPTY_LIST, actualTypes);
	
//	if (! ci.callValid(targetType, actualTypes)) {
//	    throw new SemanticException("Invalid closure call.", position());
//	}

	return this.closureInstance(ci2).type(ci2.returnType());
    }

    public String toString() {
	StringBuffer buff= new StringBuffer();
	buff.append(target)
	    .append("(");
	for(Iterator<Expr> iter= arguments.iterator(); iter.hasNext(); ) {
	    Expr arg= (Expr) iter.next();
	    buff.append(arg);
	    if (iter.hasNext()) buff.append(", ");
	}
	buff.append(")");
	return buff.toString();
    }

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    w.begin(0);
	    printSubExpr((Expr) target, w, tr);
	    w.write("(");
	    if (arguments.size() > 0) {
		w.allowBreak(2, 2, "", 0); // miser mode
		w.begin(0);
		for(Iterator<Expr> i = arguments.iterator(); i.hasNext();) {
		    Expr e = (Expr) i.next();
		    print(e, w, tr);
		    if (i.hasNext()) {
			w.write(",");
			w.allowBreak(0, " ");
		    }
		}
		w.end();
	    }
	    w.write(")");
	    w.end();
	}
}
