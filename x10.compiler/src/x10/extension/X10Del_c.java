/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.JL_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Context;
import polyglot.types.QName;
import polyglot.types.Type;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.types.X10ClassType;
import polyglot.types.Context;
import polyglot.types.TypeSystem;

/**
 * @author nystrom
 *
 */
public class X10Del_c extends JL_c implements X10Del {

	/**
	 * 
	 */
	public X10Del_c() {
		super();
	}

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#comment()
	 */
	public String comment() {
		return ((X10Ext) node().ext()).comment();
	}

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#comment(java.lang.String)
	 */
	public X10Ext comment(String comment) {
		return ((X10Ext) node().ext()).comment(comment);
	}

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#setComment(java.lang.String)
	 */
	public Node setComment(String comment) {
		return ((X10Ext) node().ext()).setComment(comment);
	}

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#annotations()
	 */
	public List<AnnotationNode> annotations() {
		if (node().ext() == null) return Collections.<AnnotationNode>emptyList();
		return ((X10Ext) node().ext()).annotations();
	}
	
	public List<X10ClassType> annotationTypes() {
		if (node().ext() == null) return Collections.<X10ClassType>emptyList();
		return ((X10Ext) node().ext()).annotationTypes();
	}
	
	public List<X10ClassType> annotationMatching(Type t) {
		if (node().ext() == null) return null;
		return ((X10Ext) node().ext()).annotationMatching(t);
	}

    public List<X10ClassType> annotationNamed(QName fullName) {
        if (node().ext() == null) return null;
        return ((X10Ext) node().ext()).annotationNamed(fullName);
    }

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#annotations(java.util.List)
	 */
	public Node annotations(List<AnnotationNode> annotations) {
		return ((X10Ext) node().ext()).annotations(annotations);
	}
	
	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#subtreeValid(boolean)
	 */
	public boolean subtreeValid() {
		return ((X10Ext) node().ext()).subtreeValid();
	}

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#setSubtreeValid(boolean)
	 */
	public Node setSubtreeValid(boolean val) {
		return ((X10Ext) node().ext()).setSubtreeValid(val);
	}

	public static Node visitAnnotations(Node n, NodeVisitor v) {
	    if (n.del() instanceof X10Del_c) {
		return ((X10Del_c) n.del()).visitAnnotations(v);
	    }
	    return n;
	}

	public  Node visitAnnotations(NodeVisitor v) {
		List<AnnotationNode> oldAnnotations = annotations();
		Node n = node();
		if (oldAnnotations == null || oldAnnotations.isEmpty()) {
			return n;
		}
		List<AnnotationNode> newAnnotations = node().visitList(oldAnnotations, v);
		if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
			return ((X10Del) n.del()).annotations(newAnnotations);
		}
		return n;
	}
	
	public static Node copyAnnotations(Node toNode, Node fromNode) {
	    if (fromNode.ext() instanceof X10Ext) {
		List<AnnotationNode> a = ((X10Ext) fromNode.ext()).annotations();
		if (a != null && ! a.isEmpty())
		    return ((X10Del) toNode.del()).annotations(a);
	    }
	    return toNode;
	}
	
	/** Override visitChildren for all nodes so annotations are visited. */
	public Node visitChildren(NodeVisitor v) {
		List<AnnotationNode> oldAnnotations = annotations();
		Node n = super.visitChildren(v);
		if (oldAnnotations == null || oldAnnotations.isEmpty()) {
			return n;
		}
		List<AnnotationNode> newAnnotations = node().visitList(oldAnnotations, v);
		if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
			return ((X10Del) n.del()).annotations(newAnnotations);
		}
		return n;
	}
}
