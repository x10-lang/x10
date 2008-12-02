/*
 * Created on Feb 26, 2007
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.*;
import polyglot.ext.x10.types.ClosureInstance;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.ClosureType_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.util.TypedList;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class Closure_c extends Expr_c implements Closure {
    List formals;
    TypeNode returnType;
    List throwTypes;
    Block body;
    MethodInstance container;
    ClosureInstance codeInstance;
    ClassType typeContainer;

    private static final Collection TOPICS = 
        CollectionUtil.list(Report.types, Report.context);

    public Closure_c(Position pos) {
	super(pos);
    }

    public Closure_c(Position pos, List formals, TypeNode returnType, List throwTypes, Block body) {
	super(pos);
	this.formals= formals;
	this.returnType= returnType;
	this.throwTypes= throwTypes;
	this.body= body;
    }

    public List formals() {
	return formals;
    }

    public Closure formals(List formals) {
	Closure_c c= (Closure_c) copy();
	c.formals= formals;
	return c;
    }

    public TypeNode returnType() {
	return returnType;
    }

    public Closure returnType(TypeNode returnType) {
	Closure_c c= (Closure_c) copy();
	c.returnType= returnType;
	return c;
    }

    public List throwTypes() {
	return throwTypes;
    }

    public Closure throwTypes(List throwTypes) {
	Closure_c c= (Closure_c) copy();
	c.throwTypes= throwTypes;
	return c;
    }

    public Block body() {
	return body;
    }

    public CodeBlock body(Block body) {
	Closure_c c= (Closure_c) copy();
	c.body= body;
	return c;
    }

    public Term codeBody() {
	return body;
    }

    public MethodInstance methodContainer() {
	return container;
    }

    public Closure methodContainer(MethodInstance methodInstance) {
	Closure_c c= (Closure_c) copy();
	c.container = methodInstance;
	return c;
    }

    public ClassType typeContainer() {
	return typeContainer;
    }

    public Closure typeContainer(ClassType classType) {
	Closure_c c= (Closure_c) copy();
	c.typeContainer = classType;
	return c;
    }

    public CodeInstance codeInstance() {
	return codeInstance;
    }

    public ClosureInstance closureInstance() {
	return this.codeInstance;
    }

    public Closure closureInstance(ClosureInstance ci) {
        if (ci == this.codeInstance) return this;
	Closure_c n = (Closure_c) copy();
	n.codeInstance = ci;
	return n;
    }

    /** Reconstruct the closure. */
    protected Closure_c reconstruct(List formals, TypeNode returnType, List throwTypes, Block body) {
	if (!CollectionUtil.equals(formals, this.formals) || returnType != this.returnType || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body) {
	    Closure_c n = (Closure_c) copy();
	    n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    n.returnType = returnType;
	    n.throwTypes = TypedList.copyAndCheck(throwTypes, Type.class, true);
	    n.body = body;
	    return n;
	}
	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	List formals = (List) visitList(this.formals, v);
	TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
	List throwTypes = visitList(this.throwTypes, v);
	Block body = (Block) visitChild(this.body, v);

	return reconstruct(formals, returnType, throwTypes, body);
    }

    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        return tb.pushCode();
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        Closure_c n = this;
        TypeSystem ts = tb.typeSystem();
        X10TypeSystem x10ts = (X10TypeSystem) ts;

        List<Type> formalTypes = new ArrayList<Type>(n.formals.size());
        for (int i = 0; i < n.formals.size(); i++) {
            formalTypes.add(ts.unknownType(position()));
        }

        List<Type> throwTypes = new ArrayList<Type>(n.throwTypes.size());
        for (int i = 0; i < n.throwTypes.size(); i++) {
            throwTypes.add(ts.unknownType(position()));
        }

        n= (Closure_c) n.typeContainer(tb.currentClass());

        ClosureInstance ci = x10ts.closureInstance(position(), n.typeContainer, n.container, n.returnType.type(), formalTypes, throwTypes);
        n = (Closure_c) n.closureInstance(ci);

        ClosureType ct= x10ts.closure(position(), returnType.type(), formalTypes, throwTypes);

        return n.type(ct);
    }

    public Context enterScope(Context c) {
        if (Report.should_report(TOPICS, 5))
	    Report.report(5, "enter scope of closure at " + position());
        // TODO maybe we want a new type of "code context thingy" that is not a type system object, but can live on the Context stack.
        c = c.pushCode(codeInstance);
        return c;
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (this.codeInstance.isCanonical()) {
            // already done
            return this;
        }

        if (! returnType.isDisambiguated()) {
            return this;
        }

        codeInstance.setReturnType(returnType.type());

        List formalTypes = new LinkedList();
        List throwTypes = new LinkedList();

        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            if (! f.isDisambiguated()) {
                return this;
            }
            formalTypes.add(f.declType());
        }

        codeInstance.setFormalTypes(formalTypes);

        for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            if (! tn.isDisambiguated()) {
                return this;
            }
            throwTypes.add(tn.type());
        }

        codeInstance.setThrowTypes(throwTypes);

        return this;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem x10ts = (X10TypeSystem) tc.typeSystem();
        Context c = tc.context();
        Closure closure = this;

        for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            Type t = tn.type();
            if (! t.isThrowable()) {
                throw new SemanticException("Type \"" + t +
                    "\" is not a subclass of \"" + x10ts.Throwable() + "\".",
                    tn.position());
            }
        }

        ClosureType closureType= (ClosureType) this.type;

        closureType.argumentTypes(codeInstance.formalTypes());
        closureType.returnType(codeInstance.returnType());

        return closure;
    }

    public Term firstChild() {
        return listChild(formals(), returnType);
    }

    /**
     * Visit this term in evaluation order.
     * [IP] Treat this as a conditional to make sure the following
     *      statements are always reachable.
     * FIXME: We should really build our own CFG, push a new context,
     * and disallow uses of "continue", "break", etc. in closures.
     */
    @Override
    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFGList(formals(), returnType, ENTRY);

        // If building the CFG for the enclosing code, don't thread
        // in the closure body.  Otherwise, we're building the CFG
        // for the closure itself.
        if (! succs.isEmpty()) {
            v.visitCFG(returnType, this, EXIT);
        }
        else {
            v.visitCFG(returnType, body, ENTRY);
            v.visitCFG(body, this, EXIT);
        }

        /*
        v.visitCFG(returnType, FlowGraph.EDGE_KEY_TRUE, body, ENTRY,
                   FlowGraph.EDGE_KEY_FALSE, this, EXIT);
                   */
        return succs;
    }

    @Override
    public SubtypeSet exceptions() {
	return body.exceptions();
    }

    public Precedence precedence() {
	return Precedence.LITERAL;
    }

    public boolean constantValueSet() {
	return false;
    }

    public boolean isConstant() {
	return false;
    }

    public Object constantValue() {
	return null;
    }

    public String toString() {
	StringBuffer buff= new StringBuffer();
	buff.append(returnType.toString())
	    .append(" (");
	for(Iterator iter= formals.iterator(); iter.hasNext(); ) {
	    Formal formal= (Formal) iter.next();
	    buff.append(formal.toString());
	    if (iter.hasNext()) buff.append(',');
	}
	buff.append(") ");
	if (throwTypes.size() > 0) {
	    buff.append("throws ");
	    buff.append(X10TypeSystem_c.listToString(throwTypes));
	    buff.append(' ');
	}
	buff.append(body);
	return buff.toString();
    }

	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.begin(0);
		w.write("(");
		w.allowBreak(2, 2, "", 0);
		w.begin(0);
		for (Iterator i = formals.iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			print(f, w, tr);
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(") ");

		if (! throwTypes().isEmpty()) {
		    w.allowBreak(6);
		    w.write("throws ");
		    for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
		    	TypeNode tn = (TypeNode) i.next();
		    	print(tn, w, tr);
		    	
		    	if (i.hasNext()) {
		    		w.write(",");
		    		w.allowBreak(4, " ");
		    	}
		    }
		}
		w.end();
	    printSubStmt(body, w, tr);
	}
}
