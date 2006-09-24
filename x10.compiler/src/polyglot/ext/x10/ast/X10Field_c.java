/*
 * Created by vj on May 23, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Field_c;
import polyglot.ext.x10.types.PropertyInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;


/**
 * An immutable representation of an X10 Field access. It is the same as a Java
 * field access except for accesses of the field "location" for value types.
 * In this implementation such field accesses are implemented by the method call
 * x10.lang.Runtime.here().
 * 
 * @author vj May 23, 2005
 */
public class X10Field_c extends Field_c {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 */
	public X10Field_c(Position pos, Receiver target, String name) {
		super(pos, target, name);
	}

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		/*
		if (name.equals("location") && xts.isValueType(target.type())) {
			return xnf.Here(position()).typeCheck(tc);
		}
		*/
		try {
			//Report.report(1, "X10Field_c.tpeCheck: context" + tc.context());
			X10Field_c result = (X10Field_c) super.typeCheck(tc);
			// Check that field accesses in dep clauses refer to final fields.
			X10Context xtc = (X10Context) tc.context();
			if (xtc.inDepType()) {
				FieldInstance fi = result.fieldInstance();
				if (! fi.flags().contains(Flags.FINAL))
					throw 
					new SemanticException("Field " + fi.name() 
							+ " is not final. Only final fields are permitted in a depclause.", 
							position());
				if ((target instanceof X10Special) &&
						((X10Special)target).kind()==X10Special.SELF) {
					// The fieldInstance must be a property.
					if (! (fi instanceof PropertyInstance))
						throw new SemanticException("Field \"" + fi.name() 
								+  "\" is not a property. " 
								+ "Only properties may appear unqualified or prefixed with self in a depclause."
								);
				}
			}
			return result;
        } catch (NoMemberException e) {
            if (e.getKind() != NoMemberException.FIELD || this.target == null)
                throw e;
            Type type = target.type();
            if (!xts.isX10Array(type))
                throw e;
            // Special fields on arrays
            if (name.equals("distribution") || name.equals("region")) {
            	Type array = xts.array();
				TypeNode typenode = xnf.CanonicalTypeNode(position(), array);
            	return this.target(xnf.Cast(position(), typenode, (Expr) target).type(array)).typeCheck(tc);
            }
            throw e;
        }
	}


	public boolean equals(Object o) {
		if (!(o instanceof Field_c)) return false;
		Field_c other = (Field_c) o;
		return target.equals(other.target()) && name.equals(other.name());
	}
}
