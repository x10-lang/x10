package org.eclipse.imp.x10dt.analysis;

import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public class X10FactTypes {
    public static final TypeFactory tf= TypeFactory.getInstance();

    public static final TypeStore ts= new TypeStore();

    public static final Type X10TypeName= tf.aliasType(ts, "org.eclipse.imp.x10dt.typeName", tf.stringType());

    public static final Type X10Type= // tf.aliasType("org.eclipse.imp.x10dt.typeDesc",
            tf.tupleType(X10TypeName, tf.sourceLocationType());

    public static final Type X10Method= tf.aliasType(ts, "org.eclipse.imp.x10dt.method",
            tf.tupleType(tf.stringType(), X10Type, tf.listType(X10Type)));

    public static final Type X10Types= tf.aliasType(ts, "org.eclipse.imp.x10dt.allTypes",
            tf.setType(X10Type));

    public static final Type X10TypeHierarchy= tf.aliasType(ts, "org.eclipse.imp.x10dt.typeHierarchy",
            tf.relType(X10TypeName, X10TypeName)); // superType, derivedType

    public static final Type X10CallGraphType= tf.aliasType(ts, "org.eclipse.imp.x10dt.callGraphType", tf.relType(X10Method, X10Method));
}
