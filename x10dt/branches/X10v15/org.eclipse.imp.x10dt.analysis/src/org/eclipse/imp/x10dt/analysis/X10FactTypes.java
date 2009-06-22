package org.eclipse.imp.x10dt.analysis;

import org.eclipse.imp.pdb.facts.type.NamedType;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class X10FactTypes {
    public static final TypeFactory tf= TypeFactory.getInstance();

    public static final NamedType X10TypeName= tf.namedType("org.eclipse.imp.x10dt.typeName", tf.stringType());

    public static final Type X10Type= // tf.namedType("org.eclipse.imp.x10dt.typeDesc",
            tf.tupleTypeOf(X10TypeName, tf.sourceLocationType());

    public static final NamedType X10Method= tf.namedType("org.eclipse.imp.x10dt.method",
            tf.tupleTypeOf(tf.stringType(), X10Type, tf.listType(X10Type)));

    public static final NamedType X10Types= tf.namedType("org.eclipse.imp.x10dt.allTypes",
            tf.setTypeOf(X10Type));

    public static final NamedType X10TypeHierarchy= tf.namedType("org.eclipse.imp.x10dt.typeHierarchy",
            tf.relTypeOf(X10TypeName, X10TypeName)); // superType, derivedType

    public static final NamedType X10CallGraphType= tf.namedType("org.eclipse.imp.x10dt.callGraphType", tf.relTypeOf(X10Method, X10Method));
}
