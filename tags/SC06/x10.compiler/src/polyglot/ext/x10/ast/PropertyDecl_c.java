package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.FieldDecl_c;
import polyglot.ext.jl.ast.MethodDecl_c;
import polyglot.ext.jl.parse.Name;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;

public class PropertyDecl_c extends X10FieldDecl_c  implements PropertyDecl {
	protected X10NodeFactory nf;
   
    public PropertyDecl_c(Position pos, Flags flags, TypeNode type,
            String name, X10NodeFactory nf) {
        this(pos, flags, type, name, null, nf);
    }
    private PropertyDecl_c(Position pos, Flags flags, TypeNode type,
            String name, Expr init, X10NodeFactory nf) {
        super(pos, flags, type, name, init);
        this.nf = nf;
    }
   /**
    * Return body, augmented with properties and their getters. Used for classes. May be called during
    * initial AST construction phase.
    * 
    * @param properties -- properties declared with the class or interface.
    * @param body   -- the body of the class or interface
    * @return body, with properties and getters added.
    */
    public static ClassBody addProperties(List/*<PropertyDecl>*/ properties, ClassBody body,
    		X10NodeFactory nf) {
    	
    	if (properties != null && ! properties.isEmpty()) {
    		for (Iterator e = properties.iterator(); e.hasNext();) {
    			PropertyDecl  p = (PropertyDecl) e.next();
    			MethodDecl getter = p.getter();
    			body = body.addMember(getter);
    			body = body.addMember(p);
    		}
    		body = body.addMember(PropertyDecl_c.makePropertyNamesField(properties, nf));
    	}
    	return body;
    }
    /**
     * Return body, augmented with getters. Used for interfaces. May be called during
     * initial AST construction phase.
     * 
     * @param properties -- properties declared with the class or interface.
     * @param body   -- the body of the class or interface
     * @return body, with properties and getters added.
     */
    public static ClassBody addGetters(List/*<PropertyDecl>*/ properties, ClassBody body, X10NodeFactory nf) {
        if (properties != null && ! properties.isEmpty()) {
            for (Iterator e = properties.iterator(); e.hasNext();) {
                PropertyDecl  p = (PropertyDecl) e.next();
                body = body.addMember(p.abstractGetter());
            }
            body = body.addMember(PropertyDecl_c.makePropertyNamesField(properties, nf));
        }
        return body;
    }
    /** Construct the synthetic field:
    public static final String propertyNames$ = " ... ";
    The string contains the names of the fields separated by " ".
    This is an attempt to circumvent the Java restriction that inner classes
    cannot contain static members whose initializers are not compile time constants.
    Arrays of strings are not compile time constants.
    */    
   public static ClassMember makePropertyNamesField(List/*<PropertyDecl>*/ properties,
		   X10NodeFactory nf) {
    
       final Position pos = Position.COMPILER_GENERATED;
       TypeSystem ts =  nf.extensionInfo().typeSystem();
       
       TypeNode tn  = nf.CanonicalTypeNode(pos, ts.String());
       // get the initial value.
       StringBuffer s = new StringBuffer();
     
       for (Iterator e = properties.iterator(); e.hasNext(); ) {
           PropertyDecl p = (PropertyDecl) e.next();
           s = s.append(p.name()).append(" ");
       }
       
       FieldDecl f = new PropertyDecl_c(pos, 
    		   Flags.PUBLIC.Static().Final(), tn, 
    		   X10FieldInstance.MAGIC_PROPERTY_NAME,
               nf.StringLit(pos, s.toString()).type(ts.String()), nf)
               .type(nf.CanonicalTypeNode(pos, ts.String()));
      
       return f;
   }
   
    public MethodDecl getter() {
        X10TypeSystem ts = (X10TypeSystem) nf.extensionInfo().typeSystem();
        Position pos = Position.COMPILER_GENERATED;
        Flags flags = Flags.PUBLIC.Final();
        List formals = Collections.EMPTY_LIST;
        List throwTypes = Collections.EMPTY_LIST;
        Expr e = new Name(nf, ts, pos, name).toExpr();
        //Report.report(1, "PropertyDecl_c: GOLDEN e=|" + e + " " + e.getClass());
        
        Stmt s = nf.Return(pos, e);
        Block body = nf.Block(pos, s);
        MethodDecl result = nf.MethodDecl(pos, flags, type, name, formals, throwTypes, body);

        return result;
    }
    /**
     * For Interfaces with properties, an abstract method signature for each property 
     * is generated in the interface body.
     * Any class implementing the interface has to have the same property 
     * <RAJ> 
     */
    public MethodDecl abstractGetter() {
      return nf.MethodDecl(Position.COMPILER_GENERATED, Flags.PUBLIC.Abstract(), type, name, 
                              Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
    }
   
}
