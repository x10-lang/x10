/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * All nodes that occur in the graph maintained by a constraint
 * must implement Promise. 
 * 
 * @author vj
 *
 */
public interface XPromise extends Cloneable {

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
	 * @throws XFailure 
	 */
	XPromise intern(XVar[] vars, int index) throws XFailure;

	/**
	 * vars is as above. If last is not null, then do not create a new promise
	 * for vars[index-1], instead use last.
	 * @return
	 * @throws XFailure 
	 */
	XPromise intern(XVar[] vars, int index, XPromise last) throws XFailure;

	/**
	 * vars and this must be as for intern. Return the node in the graph of constraint
	 * c obtained by following the path specified by vars[index],....vars[path.length-1] 
	 * from this node. Return null if the path does not exist; do not create new nodes.
	 * The returned node must not be a forwarded node.
	 * @param vars
	 * @param index
	 * @return
	 * @throws XFailure 
	 */
	XPromise lookup(XVar[] vars, int index) throws XFailure;

	XPromise lookup(XName s) throws XFailure;

	XPromise lookup() throws XFailure;

	/**
	 * An eq link entering this has just been established. Now the 
	 * children of the source of the link have to be transferred to this.
	 * If this does not have an s-child, then install the child as its s-child.
	 * If it does, then recursively bind the s-child to this's s child.
	 * 
	 * @param s -- the name of the field
	 * @param child -- the s child of the source of the eq link.
	 */
	void addIn(XName s, XPromise child) throws XFailure;

	/**
	 * Bind this promise to the given target. All the children of this, if any, have to be
	 * added into the target. target should satisfy the condition that it is not forwarded.
	 * Return true if the execution of this operation caused a change to the constraint graph;
	 * false otherwise.
	 * @param target
	 */
	boolean bind(XPromise target) throws XFailure;

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
	boolean canReach(/*@nonnull*/XPromise p);

	/**
	 * Traverse the subtree under this promise, and add t1 -> t2 into result for any term t1 
	 * which has an outgoing edge to a term tw.
	 * @param result
	 */
	void dump(List<XTerm> result, XTerm prefix);

	/**
	 * Return the term that labels this promise. This term is intended to be the canonical C_Var
	 * labeling this promise, following the direct path from the root node.
	 * Note: this promise may be forwarded to another; yet the term returned is this's term, 
	 * not the term corresponding to the target promise.
	 * TODO: Change its return type to C_Var.
	 * @return null if this promise is an internal promise.
	 */
	XTerm term();

	/**
	 * Set the term corresponding to this promise. The term may be null, such a promise is 
	 * called a silent promise. Silent promises are not dumped (that is, they do not 
	 * correspond to constraints on externally visible terms). They correspond to internal
	 * nodes that are used just for implementation efficiency.
	 * @param term
	 */
	void setTerm(XTerm term);
	void setTerm(XTerm term, Set<XPromise> visited);

	/**
	 * Replace a reference to any descendant that is equal to x with a reference to y.
	 * xroot is the root of the x in the constraint, returning a NEW promise.
	 * @param y
	 * @param x
	 * @param c TODO
	 */
	void replaceDescendant(XPromise y, XPromise x, XConstraint c);

	XPromise value();

	HashMap<XName, XPromise> fields();

	/**
	 * Replace a reference to any descendant that is equal to x with a reference to y.
	 * @param y
	 * @param x
	 */
	XPromise cloneRecursively(HashMap<XPromise, XPromise> env);
}
