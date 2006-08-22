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
import polyglot.ext.x10.types.PropertyInstance;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;

public class PropertyDecl_c extends FieldDecl_c  implements PropertyDecl {
    public static final String propFieldName = "propertyNames$";
    public PropertyDecl_c(Position pos, Flags flags, TypeNode type,
            String name) {
        super(pos, flags, type, name, null);
        
    }
   /**
    * Return body, augmented with properties and their getters. Used for classes. May be called during
    * initial AST construction phase.
    * 
    * @param properties -- properties declared with the class or interface.
    * @param body   -- the body of the class or interface
    * @return body, with properties and getters added.
    */
    public static ClassBody addProperties(List/*<PropertyDecl>*/ properties, ClassBody body) {
        if (properties != null && ! properties.isEmpty()) {
            for (Iterator e = properties.iterator(); e.hasNext();) {
                PropertyDecl  p = (PropertyDecl) e.next();
                body = body.addMember(p.getter()).addMember(p);
            }
            body = body.addMember(PropertyDecl_c.makePropertyNamesField(properties));
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
    public static ClassBody addGetters(List/*<PropertyDecl>*/ properties, ClassBody body) {
        if (properties != null && ! properties.isEmpty()) {
            for (Iterator e = properties.iterator(); e.hasNext();) {
                PropertyDecl  p = (PropertyDecl) e.next();
                body = body.addMember(p.getter());
            }
            body = body.addMember(PropertyDecl_c.makePropertyNamesField(properties));
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
   public static ClassMember makePropertyNamesField(List/*<PropertyDecl>*/ properties) {
    
       final Position pos = Position.COMPILER_GENERATED;
       X10TypeSystem ts =  X10TypeSystem_c.getTypeSystem();
       X10NodeFactory nf = X10NodeFactory_c.getNodeFactory();
       /*
       TypeNode tn  = nf.ArrayTypeNode(pos, nf.CanonicalTypeNode(pos, ts.String()));
       // get the initial value.
       List l = new TypedList(new LinkedList(), Expr.class, false);
       for (Iterator e = properties.iterator(); e.hasNext(); ) {
           PropertyDecl p = (PropertyDecl) e.next();
           l.add(nf.StringLit(pos, p.name()));
       }
       ArrayInit init = nf.ArrayInit(pos, l);
       FieldDecl f = new FieldDecl_c(pos, Flags.PUBLIC.Static().Final(), tn, propFieldName, init);*/
       
       TypeNode tn  = nf.CanonicalTypeNode(pos, ts.String());
       // get the initial value.
       StringBuffer s = new StringBuffer();
     
       for (Iterator e = properties.iterator(); e.hasNext(); ) {
           PropertyDecl p = (PropertyDecl) e.next();
           s = s.append(p.name()).append(" ");
           
       }
       
       FieldDecl f = new FieldDecl_c(pos, Flags.PUBLIC.Static().Final(), tn, propFieldName, 
               nf.StringLit(pos, s.toString()));
       
       return f;
   }
   
    public MethodDecl getter() {
        X10NodeFactory nf = X10NodeFactory_c.getNodeFactory();
        Position pos = Position.COMPILER_GENERATED;
        Flags flags = Flags.PUBLIC.Final();
        List formals = Collections.EMPTY_LIST;
        List throwTypes = Collections.EMPTY_LIST;
        Expr e = nf.Field(pos, name);
        Stmt s = nf.Return(pos, e);
        Block body = nf.Block(pos, s);
        MethodDecl result = new MethodDecl_c(pos, flags, type, name, formals, throwTypes, body);
        
        return result;
    }
   
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();

        ParsedClassType ct = tb.currentClass();

        // vj: No idea why this clause is in here.. its from FieldDecl_c.
        if (ct == null) {
            return this;
        }

       

        // XXX: MutableFieldInstance
        PropertyInstance fi = ((X10TypeSystem) ts).propertyInstance(position(), ct, flags,
                                            ts.unknownType(position()), name);
        ct.addField(fi);

        return this.fieldInstance(fi);
    }

}
