package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.ClassDecl_c;
import polyglot.ext.jl.ast.FieldDecl_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.util.Position;
import polyglot.util.TypedList;
/**
 * The same as a Java class, except that it needs to handle properties.
 * Properties are converted into public final instance fields immediately.
 * TODO: Use the retType for the class during type checking.
 * @author vj
 *
 */
public class X10ClassDecl_c extends ClassDecl_c {
   
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
    public static X10ClassDecl_c make(Position pos, Flags flags, String name, 
            List/*<PropertyDecl>*/ properties, Expr ci,
            TypeNode superClass, List interfaces, ClassBody body) {
        body = flags.isInterface() ? PropertyDecl_c.addProperties(properties, body)
                : PropertyDecl_c.addGetters(properties, body);
        X10ClassDecl_c result = new X10ClassDecl_c(pos, flags, name, properties,  ci, superClass, 
                interfaces, body);
        return result;
    }
   
    /** The list of propertie for this class.
     * 
     */
    protected List properties;
    protected Expr classInvariant;
    
    public X10ClassDecl_c(Position pos, Flags flags, String name,
            List/*<PropertyDecl>*/ properties, Expr ci,
            TypeNode superClass, List interfaces, ClassBody body) {
        super(pos, flags, name, superClass, interfaces, body);
        this.classInvariant = ci;
        this.properties = properties;
        
    }
    
   
}
