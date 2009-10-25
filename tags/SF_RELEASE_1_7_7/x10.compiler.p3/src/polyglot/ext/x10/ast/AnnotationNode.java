/*
 *
 * (C) Copyright IBM Corporation 2007-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.types.ClassType;

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
