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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.types.QName;
import polyglot.types.Type;
import polyglot.ast.Ext_c;
import x10.ast.AnnotationNode;
import x10.types.X10ClassType;
import polyglot.types.LocalDef;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

public class X10Ext_c extends Ext_c implements X10Ext {
    /*
    asyncInitVal is used by the backend to handle async initialization.
    For example:
    val x:Int;
    finish async { finish async { x = 42; } }
     */
    public Set<LocalDef> initVals = null;

    String comment;
    List<AnnotationNode> annotations;
    boolean subtreeValid = true;

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
    		return Collections.<AnnotationNode>emptyList();
    	}
    	return Collections.unmodifiableList(this.annotations);
    }
    
    public List<X10ClassType> annotationTypes() {
    	if (this.annotations == null) {
    		return Collections.<X10ClassType>emptyList();
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
    
    public List<X10ClassType> annotationNamed(QName fullName) {
        List<X10ClassType> l = new ArrayList<X10ClassType>();
        for (Iterator<AnnotationNode> i = annotations().iterator(); i.hasNext(); ) {
            AnnotationNode an = i.next();
            X10ClassType ct = an.annotationInterface();
            if (ct.fullName().equals(fullName)) {
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

    public boolean subtreeValid() {
        return subtreeValid;
    }

    public X10Ext subtreeValid(boolean val) {
        X10Ext_c n = (X10Ext_c) copy();
        n.subtreeValid = val;
        return n;
    }
    
    public Node setSubtreeValid(boolean val) {
        Node n = this.node();
        return n.ext(this.subtreeValid(val));
    }

    public Node asyncInitVal(Set<LocalDef> initVars) {
        X10Ext_c c = (X10Ext_c) copy();
        c.initVals = (initVars == null) ? null : CollectionFactory.newHashSet(initVars);
        return this.node().ext(c);
    }
}
