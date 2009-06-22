/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ClassType;
import polyglot.types.Type;
import polyglot.ast.Ext_c;
import polyglot.ext.x10.ast.AnnotationNode;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10TypeSystem;

public class X10Ext_c extends Ext_c implements X10Ext {
    String comment;
    List<AnnotationNode> annotations;
    
    public String comment() {
        return this.comment;
    }
    
    public X10Ext comment(String comment) {
        X10Ext_c n = (X10Ext_c) copy();
        n.comment = comment;
        return n;
    }
    
    public Node setComment(String comment) {
        Node n = this.node();
        return n.ext(this.comment(comment));
    }
    
    public List<AnnotationNode> annotations() {
    	if (this.annotations == null) {
    		return Collections.EMPTY_LIST;
    	}
    	return Collections.unmodifiableList(this.annotations);
    }
    
    public List<X10ClassType> annotationTypes() {
    	if (this.annotations == null) {
    		return Collections.EMPTY_LIST;
    	}
    	List<X10ClassType> l = new ArrayList<X10ClassType>(this.annotations.size());
    	for (Iterator<AnnotationNode> i = this.annotations.iterator(); i.hasNext(); ) {
    		AnnotationNode a = i.next();
    		l.add(a.annotationInterface());
    	}
    	return l;
    }
    
    public List<X10ClassType> annotationMatching(Type t) {
		List<X10ClassType> l = new ArrayList<X10ClassType>();
		for (Iterator<AnnotationNode> i = annotations().iterator(); i.hasNext(); ) {
			AnnotationNode an = i.next();
			X10ClassType ct = an.annotationInterface();
			if (ct.isSubtype(t, t.typeSystem().emptyContext())) {
				l.add(ct);
			}
		}
		return l;
	}
    
    public X10Ext extAnnotations(List<AnnotationNode> annotations) {
    	X10Ext_c n = (X10Ext_c) copy();
    	n.annotations = new ArrayList<AnnotationNode>(annotations);
    	return n;
    }
    
    public Node annotations(List<AnnotationNode> annotations) {
    	Node n = this.node();
    	return n.ext(this.extAnnotations(annotations));
    }
}
