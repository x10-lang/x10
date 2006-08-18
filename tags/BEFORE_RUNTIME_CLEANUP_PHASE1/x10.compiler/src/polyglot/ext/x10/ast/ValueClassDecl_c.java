/*
 * Created on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.ClassDecl_c;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;

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

    /**
     * If there are any properties, add the property instances to the body,
     * and the synthetic field, before creating the class instance.
     * 
     * @param pos
     * @param flags
     * @param name
     * @param properties
     * @param retType
     * @param superClass
     * @param interfaces
     * @param body
     * @return
     */
    public static ValueClassDecl_c make(Position pos, Flags flags, String name, 
            List/*<PropertyDecl>*/ properties, Expr ci,
            TypeNode superClass, List interfaces, ClassBody body) {
        body = PropertyDecl_c.addProperties(properties, body);
        ValueClassDecl_c result = new ValueClassDecl_c(pos, flags, name, properties,  ci, superClass, 
                interfaces, body);
        return result;
    }
    protected final List elist;
    protected final List properties;
    protected final Expr classInvariant;
    
	/**
	 * @param pos
	 * @param flags
	 * @param name
	 * @param superClass
	 * @param interfaces
	 * @param body
	 */
	public ValueClassDecl_c(Position pos, Flags flags, String name,
            List properties, Expr ci, TypeNode superClass, List interfaces, ClassBody body) {
		super(pos, flags, name, superClass, interfaces, body);
                TypedList l = (TypedList) super.interfaces();
                this.elist = TypedList.copy(l,l.getAllowedType(), false);
                X10TypeSystem_c ts = X10TypeSystem_c.getFactory();
                ClassType value = ts.value();
                NodeFactory nf = X10NodeFactory_c.getNodeFactory();
                CanonicalTypeNode ctn
                    = nf.CanonicalTypeNode(Position.COMPILER_GENERATED,
                                                  value);
                elist.add(0, ctn); 
                this.properties = properties;
                this.classInvariant = ci;
                
	}

        public List interfaces() {
            return elist;
        }
    
        public Node visitChildren(NodeVisitor v) {
            TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
            List interfaces = visitList(interfaces(), v);
            ClassBody body = (ClassBody) visitChild(this.body, v);
            return reconstruct(superClass, interfaces, body);
        }
}
