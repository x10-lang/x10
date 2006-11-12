package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.util.Position;

/**
 * An immutable representation of an X10 property. A property is a 
 * public final instance field that may be used in constructing
 * depTypes from the class. It is declared in the class header, using
 * the class name(PropertyList) extends ... syntax. 
 * 
 * @author vj
 *
 */
public class X10PropertyDecl_c extends X10FieldDecl_c {

    public X10PropertyDecl_c(Position pos, Flags flags, TypeNode type,
            String name, Expr init) {
        super(pos, null, flags, type, name, init);
        
    }

}
