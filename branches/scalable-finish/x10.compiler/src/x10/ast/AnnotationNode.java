/*
 *
 * (C) Copyright IBM Corporation 2007-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import x10.types.X10ClassType;

/**
 * A node representing an annotation.  Every X10 Node has an associated list of AnnotationNodes.
 * An annotation is simply an interface type.
 * @author nystrom
 */
public interface AnnotationNode extends Node {
    TypeNode annotationType();
    AnnotationNode annotationType(TypeNode tn);
    X10ClassType annotationInterface();
}
