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
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Ext;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ClassType;
import polyglot.types.QName;
import polyglot.types.Type;
import x10.ast.AnnotationNode;
import x10.types.X10ClassType;
import polyglot.types.TypeSystem;

public interface X10Ext extends Ext {
    /**
      * Comment adjacent to the node in the source code.
      * @return the comment
      */
    public String comment();
    
    /**
      * Clone the extension object and set its comment.
      * @param comment
      * @return a new extension object
      */
    public X10Ext comment(String comment);
    
    /**
      * Clone the extension object and the node and set its comment.
      * @param comment
      * @return a new Node
      */
    public Node setComment(String comment);
    
    /**
     * Annotation on the node.
     * @return
     */
    public List<AnnotationNode> annotations();
    public List<X10ClassType> annotationTypes();
    public List<X10ClassType> annotationMatching(Type t);
    public List<X10ClassType> annotationNamed(QName fullName);
    
    /**
     * Set the annotations.
     * @param annotations
     * @return
     */
    public Node annotations(List<AnnotationNode> annotations);
    
    /**
     * Are this node and all of its children valid?
     * @return
     */
    public boolean subtreeValid();
    
    /**
     * Set the validity status for this node and all of its children.
     * @param val
     * @return
     */
    public Node setSubtreeValid(boolean val);
}
