/*
 * Created on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.ClassDecl_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.util.Position;
import polyglot.util.TypedList;

/**
 * We must preserve information about value classes at runtime.
 * The best way would be to use meta-data annoations from Java 5,
 * but in the meantime (for 1.4 compatibility) we simply add an
 * interface ("implements ValueType") to mark value types.  
 * 
 * @author vj
 * @author Christian Grothoff
 * 
 */
public class ValueClassDecl_c extends ClassDecl_c implements ValueClassDecl {

    protected final List elist;
    
    
	/**
	 * @param pos
	 * @param flags
	 * @param name
	 * @param superClass
	 * @param interfaces
	 * @param body
	 */
	public ValueClassDecl_c(Position pos, Flags flags, String name,
			TypeNode superClass, List interfaces, ClassBody body) {
		super(pos, flags, name, superClass, interfaces, body);
                TypedList l = (TypedList) super.interfaces();
                this.elist = TypedList.copy(l,l.getAllowedType(), false);
                X10TypeSystem_c ts = X10TypeSystem_c.getFactory();
                ClassType value = ts.value();
                NodeFactory nf = X10NodeFactory_c.getFactory();
                CanonicalTypeNode ctn
                    = nf.CanonicalTypeNode(Position.COMPILER_GENERATED,
                                                  value);
                elist.add(0, ctn); 
	}

        public List interfaces() {
            return elist;
        }
    
}
