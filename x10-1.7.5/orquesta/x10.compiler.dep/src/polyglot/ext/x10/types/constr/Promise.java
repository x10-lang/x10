/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.HashMap;

/**
 * All nodes that occur in the graph maintained by a constraint
 * must implement Promise. 
 * 
 * @author vj
 *
 */
public interface Promise  extends Cloneable {
	
	/**
	 * vars must be a sequence C_Var, C_Field, ... C_Field, satisfying the property
	 * that the receiver of each element (other than the first) is the preceding element.
	 * this must be the promise corresponding to the index'th element in this list. 
	 * Return the node in the graph of constraint c obtained
	 * by following the path specified by vars[index],.., vars[path.length-1] from this
	 * node. Create new nodes if necessary so that this path is defined
	 * in the graph of c. Return the node at the end of the path. The returned node
	 * must not be a forwarded node.
	 * @param path
	 * @param index
	 * @param c
	 * @return
	 */
	Promise intern(C_Var[] vars, int index);
	/**
	 * vars is as above. If last is not null, then do not create a new promise
	 * for vars[index-1], instead use last.
	 * @return
	 */
	Promise intern(C_Var[] vars, int index, Promise last);
	/**
	 * vars and this must be as for intern. Return the node in the graph of constraint
	 * c obtained by following the path specified by vars[index],....vars[path.length-1] 
	 * from this node. Return null if the path does not exist; do not create new nodes.
	 * The returned node must not be a forwarded node.
	 * @param vars
	 * @param index
	 * @return
	 */
	Promise lookup(C_Var[] vars, int index);
	Promise lookup(String s);
	Promise lookup();
	
	/**
	 * An eq link entering this has just been established. Now the 
	 * children of the source of the link have to be transferred to this.
	 * If this does not have an s-child, then install the child as its s-child.
	 * If it does, then recursively bind the s-child to this's s child.
	 * 
	 * @param s -- the name of the field
	 * @param child -- the s child of the source of the eq link.
	 */
	void addIn(String s, Promise child) throws Failure;
	
	
	/**
	 * Bind this promise to the given target. All the children of this, if any, have to be
	 * added into the target. target should satisfy the condition that it is not forwarded.
	 * Return true if the execution of this operation caused a change to the constraint graph;
	 * false otherwise.
	 * @param target
	 */
	boolean bind(Promise target) throws Failure;
	
	/** Has this promise been forwarded?
	 * 
	 * @return true if it has been forwarded (its value !=null)
	 */
	boolean forwarded();
	
	/** Does this node have children?
	 * A node cannot both have children and be forwaded.
	 */
	boolean hasChildren();
	
	/**
	 * Is there a path from here to p? 
	 * @param p
	 * @return
	 */
	boolean canReach(/*@nonnull*/ Promise p);
	
	/**
	 * Traverse the subtree under this promise, and add t1 -> t2 into result for any term t1 
	 * which has an outgoing edge to a term tw.
	 * @param result
	 */
	void dump(HashMap<C_Var,C_Var> result, C_Term prefix);
	void dump(HashMap<C_Var, C_Var> result, C_Term prefix, C_Var newSelf, C_Var newThis);
	
	/**
	 * Return the term that labels this promise. This term is intended to be the canonical C_Var
	 * labeling this promise, following the direct path from the root node.
	 * Note: this promise may be forwarded to another; yet the term returned is this's term, 
	 * not the term corresponding to the target promise.
	 * TODO: Change its return type to C_Var.
	 * @return null if this promise is an internal promise.
	 */
	C_Var term();
	
	/**
	 * Set the term corresponding to this promise. The term may be null, such a promise is 
	 * called a silent promise. Silent promises are not dumped (that is, they do not 
	 * correspond to constraints on externally visible terms). They correspond to internal
	 * nodes that are used just for implementational efficiency.
	 * @param term
	 */
	void setTerm(C_Var term);
	
	/**
	 * Replace a reference to any descendant that is equal to x with a reference to y.
	 * @param y
	 * @param x
	 */
	void replaceDescendant(Promise y, Promise x);
	
	Promise value();
	HashMap<String, Promise> fields();
	

	/**
	 * Replace a reference to any descendant that is equal to x with a reference to y.
	 * @param y
	 * @param x
	 */
	Promise cloneRecursively(HashMap<Promise, Promise> env);
}
