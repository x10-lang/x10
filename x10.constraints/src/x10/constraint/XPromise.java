/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All nodes that occur in the graph maintained by a constraint
 * must implement Promise. Thus a Promise is a pointer into the state of some specific constraint.
 * 
 * @author vj
 *
 */
public interface XPromise extends Cloneable {

	/**
	 * this must be the promise corresponding to the index'th element in the list vars.
	 * Return the node in the graph of constraint c obtained
	 * by following the path specified by vars[index],.., vars[vars.length-1] from this
	 * node. 
	 * 
	 * <p> Create new nodes if necessary so that this path is defined in the graph of c.  
	 * @param vars vars must be a sequence XVar, XField, ... XField, satisfying the property
	 * that the receiver of each element (other than the first) is the preceding element.
	 * @param index
	 * @param c
	 * @return the node at the end of the path. This must not be a forwarded node.
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
	XPromise lookup(XVar[] vars, int index);

	XPromise lookup(XName s)  ;

	XPromise lookup();

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
	 * An eq link entering this has just been established. Now the 
	 * disEquals targets of the source of the link have to be transferred to this.
	 * 
	 * @param other -- the XPromise this is to be disequated to
	 */
	void addDisEquals(XPromise other) throws XFailure;

	/**
	 * Bind this promise to the given target. All the children of this, if any, have to be
	 * added into the target. target should satisfy the condition that it is not forwarded.
	 * Return true if the execution of this operation caused a change to the constraint graph;
	 * false otherwise.
	 * @param target
	 */
	boolean bind(XPromise target) throws XFailure;
	
	/**
	 * Bind this promise to the given target using a NOT link. target should satisfy the condition
	 * that it is not forwarded. Note that the following is consistent: X !=Y, X.f=Y.f.
	 * @param target The term to be notBound to.
	 * @return true if the execution of this operation caused a change in the constraint graph;
	 * false otherwise.
	 * @throws XFailure in case this is already bound to target.
	 */
	boolean disBind(XPromise target) throws XFailure;

	/** Has this promise been forwarded?
	 * 
	 * @return true if it has been forwarded (its value !=null)
	 */
	boolean forwarded();
	

	/** Does this node have children?
	 * A node cannot both have children and be forwarded.
	 */
	boolean hasChildren();
	
	/** Does this node have disEquals bindings?
	 * A node cannot both have disEquals bindings and be forwarded.
	 * @return true iff this node has disEquals bindings.
	 */
	boolean hasDisBindings();

	/**
	 * Is there a path from here to p? 
	 * @param p
	 * @return
	 */
	boolean canReach(/*@nonnull*/XPromise p);

	/**
	 * Let t1 be path (if path is not null), else the term labeling this promise.
	 * If this promise has an outgoing edge to t2, and either dumpEQV is true or 
	 * ! t1.hasEQV(), then output t1==t2 to result.
	 * If this promise has fields, then recursively continue dumping with 
	 * the children, passing them a path t1.f (for field f) if  
	 * t1 is an instance of XVar, otherwise passing a path null (in this case t1 is an
	 * atom and the children are its subterms). 
	 * (This takes care of multiple paths entering the promise.)
	 * @param result
	 * @param oldSelf 
	 */
	void dump(XVar path, List<XTerm> result,  boolean dumpEQV);

	/**
	 * Return the term that labels this promise. This term is intended to be the canonical XTerm
	 * labeling this promise, following the direct path from the root node.
	 * Note: this promise may be forwarded to another; yet the term returned is this's term, 
	 * not the term corresponding to the target promise.
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

	/** A promise to which this promise is bound. */
	XPromise value();

	/** Map from field names f to promises term().f */
	Map<XName, XPromise> fields();

	/**
	 * Replace a reference to any descendant that is equal to x with a reference to y.
	 * @param y
	 * @param x
	 */
	XPromise cloneRecursively(HashMap<XPromise, XPromise> env);
	
	/**
	 * The externally visible term (if any) that this term represents.
	 * @return null -- if this promise is forwarded.
	 */
	XTerm var();
	
	/**
	 * Is this promise asserted to be disequal to other?
	 * @param other
	 * @return
	 */
	boolean isDisBoundTo(XPromise other);
}
