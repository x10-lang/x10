/**
 * 
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CollectionUtil;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.effects.EffectComputer;
import x10.effects.constraints.Effect;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10TypeSystem;

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
		if (node().ext() == null) return Collections.EMPTY_LIST;
		return ((X10Ext) node().ext()).annotations();
	}
	
	public List<X10ClassType> annotationTypes() {
		if (node().ext() == null) return Collections.EMPTY_LIST;
		return ((X10Ext) node().ext()).annotationTypes();
	}
	
	public List<X10ClassType> annotationMatching(Type t) {
		if (node().ext() == null) return null;
		return ((X10Ext) node().ext()).annotationMatching(t);
	}

	/* (non-Javadoc)
	 * @see x10.extension.X10Ext#annotations(java.util.List)
	 */
	public Node annotations(List<AnnotationNode> annotations) {
		return ((X10Ext) node().ext()).annotations(annotations);
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
	public Node computeEffectsOverride(Node n, EffectComputer ec) throws SemanticException {
		return ((X10Ext) node().ext()).computeEffectsOverride(n, ec);
	}
	public EffectComputer computeEffectsEnter(EffectComputer ec) {
		return ((X10Ext) node().ext()).computeEffectsEnter(ec);
	}
	public Node computeEffects(EffectComputer ec) {
		return ((X10Ext) node().ext()).computeEffects(ec);
	}
	public Effect effect() {
		return ((X10Ext) node().ext()).effect();
	}
	
}
