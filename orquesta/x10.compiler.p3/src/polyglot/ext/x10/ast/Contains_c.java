/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Feb 4, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.x10.types.PathType_c;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassDef;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;

/**
 * @author vj Feb 4, 2005
 * 
 */
public class Contains_c extends Expr_c implements Contains {

	Expr item;
	Expr collection;
	boolean subset;
	
	/**
	 * @param pos
	 */
	public Contains_c(Position pos, Expr item, Expr collection) {
		super(pos);
		this.item = item;
		this.collection = collection;
		this.subset = false;
	}
	
	public boolean isSubsetTest() {
	    return subset;
	}

	public Contains isSubsetTest(boolean f) {
	    if (this.subset == f) return this;
	    Contains_c n = (Contains_c) copy();
	    n.subset = f;
	    return n;
	}

	public Expr collection() {
		return collection;
	}

	public Expr item() {
		return item;
	}

	public Contains collection(Expr collection) {
		Contains_c n = (Contains_c) copy();
		n.collection = collection;
		return n;
	}

	public Contains item(Expr item) {
		Contains_c n = (Contains_c) copy();
		n.item = item;
		return n;
	}

	/** Reconstruct the statement. */
	protected Contains reconstruct(Expr item, Expr collection) {
		if (item != this.item || collection != this.collection) {
			Contains_c n = (Contains_c) copy();
			n.item = item;
			n.collection = collection;
			return n;
		}

		return this;
	}

	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		Expr item = (Expr) visitChild(this.item, v);
		Expr collection = (Expr) visitChild(this.collection, v);
		return reconstruct(item, collection);
	}

	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		Type itemType = item.type();
		Type collType = collection.type();

		// Check if there is a method with the appropriate name and type with the left operand as receiver.   
		try {
		    List<Type> args = Collections.singletonList(itemType);
		    ClassDef curr = tc.context().currentClassDef();
		    X10MethodInstance mi = (X10MethodInstance) ts.findMethod(collType, ts.MethodMatcher(collType, Name.make("$in"), args), curr);
		    return type(mi.returnType());
		}
		catch (SemanticException e) {
		    // Cannot find the method.  Fall through.
		    System.out.println(e);
		}

/*
		if (itemType.isImplicitCastValid(ts.Point()) && collType.isImplicitCastValid(ts.Region()))
		    return isSubsetTest(false).type(ts.Boolean());
		
		if (itemType.isImplicitCastValid(ts.Region()) && collType.isImplicitCastValid(ts.Region()))
		    return isSubsetTest(true).type(ts.Boolean());

		if (! X10ClassDecl_c.CLASS_TYPE_PARAMETERS) {
		    if (collType.isImplicitCastValid(ts.Contains())) {
			Type paramType = X10TypeMixin.getParameterType(collType, "T");
			if (paramType != null && itemType.isSubtype(paramType))
			    return isSubsetTest(false).type(ts.Boolean());
		    }

		    if (collType.isImplicitCastValid(ts.ContainsAll())) {
			Type paramType = X10TypeMixin.getParameterType(collType, "T");
			if (paramType != null && itemType.isSubtype(paramType))
			    return isSubsetTest(true).type(ts.Boolean());
		    }
		}
		else {
		    Type contains = X10TypeMixin.instantiate(ts.Contains(), itemType);
		    if (collType.isImplicitCastValid(contains)) {
			return isSubsetTest(false).type(ts.Boolean());
		    }
		    
		    Type containsAll = X10TypeMixin.instantiate(ts.Contains(), itemType);
		    if (collType.isImplicitCastValid(containsAll)) {
			return isSubsetTest(true).type(ts.Boolean());
		    }
		}
*/

		throw new SemanticException("Collection " + collType + " does not support the 'in' operator for " + itemType + ".", position());
	}

	public boolean isConstant() {
		return false;
	}

	public Object constantValue() {
		return null;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return item;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(item, collection, ENTRY);
		v.visitCFG(collection, this, EXIT);
		return succs;
	}

	public String toString() {
		return item + " in " + collection;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		printBlock(item, w, tr);
		w.write(" in ");
		printBlock(collection, w, tr);
	}

	@Override
	public void translate(CodeWriter w, Translator tr) {
	    // TODO: swap arguments: should eval item first
	    printBlock(collection, w, tr);
	    w.write(".contains(");
	    printBlock(item, w, tr);
	    w.write(")");
	}
}
