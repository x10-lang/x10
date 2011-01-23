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
 * <p>
 * A promise contains fields: 
 * <ol>
 * <li>XPromise value  -- if non-null, points to a promise this one has been equated with.
 * <li>Collection<XPromise> disequals -- contains set of other nodes this has been disequated with
 * <li>XTerm var  -- externally visible term labeling this promise
 * <li>Map<XName, XPromise> fields -- hashmap of fields of this promise (if any). 
 * </ol>
 * It maintains the invariant <code>> value != null</code> implies <code>disequals==null && fields == null</code>.
 * That is, if <code>value !=null</code> (in this case we say the promise ie <em>bound</em>), all further
 * information about this node in the graph is found by going to to <code>value</code>, except for the <code>XTerm</code>  
 * labeling this node (<code>var</code>). See <code>term()</code>.
 * 
 * <p>If <code>a.value==b</code>
 * we sometimes say that <code>b</code> has an incoming "eq" link, and <code>a</code> has an outgoing "eq" link.
 * 
 * <p> Constraint graphs may contain sequences of such bindings. These can be shortened without any
 * semantic consequence.
 * 
 * <p> Constraint graphs are always acyclic. 
 * 
 * <p> Note: This interface is not public only so that extensions of this constraint system may use it.
 * This interface is <em>not</em> intended to be used by customers of the constraint system.
 * @author vj
 *
 */
interface XPromise extends Cloneable {

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

	/**
	 * Lookup the field named s on this promise.
	 * @param s
	 * @return
	 */
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
	 * ! t1.hasEQV(), then output t1==t2 to result, unless hideFake is true and one of t1 or t2 is 
	 * a fake field.
	 * If this promise has fields, then recursively continue dumping with 
	 * the children, passing them a path t1.f (for field f) if  
	 * t1 is an instance of XVar, otherwise passing a path null (in this case t1 is an
	 * atom and the children are its subterms). 
	 * (This takes care of multiple paths entering the promise.)
	 * @param result
	 * @param oldSelf 
	 */
	//void dump(XVar path, List<XTerm> result,  boolean dumpEQV, boolean hideFake);
	boolean visit(XVar path, boolean dumpEQV, boolean hideFake, XGraphVisitor xg);
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
	 * @param env: mapping from x to y
	 */
	//XPromise cloneRecursively(Map<XPromise, XPromise> env);
	
	/**
	 * Transfer the state of this to env(this). env(this) is guaranteed to be non-null.
	 * If when transfering state, a promise p is encountered, check if p is in env.
	 * If so, then stop and use env(p) as the transfered value of p. If not, then
	 * create a new XPromise np through p.shallowCopy(), update env to point p to np,
	 * and recursively call transfer on np with the updated env.
	 * @param env
	 */
	void transfer(Map<XPromise, XPromise> env);
	
	/**
	 * Return a shallow clone, no deep copying permitted. The clone must have
	 * the same concrete type as this. The promise will be visited
	 * subsequently (via transfer), to flesh out its data-structures
	 * @return
	 */
	XPromise cloneShallow();
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
