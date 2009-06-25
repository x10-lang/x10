/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;

public class PropertyDecl_c extends X10FieldDecl_c  implements PropertyDecl {
	MethodDecl getter;
	MethodDecl abstractGetter;
	
    public PropertyDecl_c(Position pos, Flags flags, TypeNode type,
            Id name, X10NodeFactory nf) {
        this(pos, flags, type, name, null, nf);
    }
    public PropertyDecl_c(Position pos, Flags flags, TypeNode type,
            Id name, Expr init, X10NodeFactory nf) {
        super(pos, flags, type, name, init);
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        PropertyDecl_c n = (PropertyDecl_c) super.buildTypesOverride(tb);
        X10FieldDef fi = (X10FieldDef) n.fieldDef();
        fi.setProperty();
        return n;
    }
    
   /**
    * Return body, augmented with properties and their getters. Used for classes. May be called during
    * initial AST construction phase.
    * 
    * @param properties -- properties declared with the class or interface.
    * @param body   -- the body of the class or interface
    * @return body, with properties and getters added.
    */
    public static ClassBody addProperties(List<PropertyDecl> properties, ClassBody body,
    		X10NodeFactory nf) {
    	
    	if (properties != null && ! properties.isEmpty()) {
    		int n = properties.size();
    		for (int i=0; i < n; i++) {
    		
    			PropertyDecl  p =  properties.get(i);
    			MethodDecl getter = ((PropertyDecl_c) p).getter(nf);
    			body = body.addMember(getter);
    			body = body.addMember(p);
    		}
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
    public static ClassBody addGetters(List<PropertyDecl> properties, ClassBody body, X10NodeFactory nf) {
        if (properties != null && ! properties.isEmpty()) {
        	int n = properties.size();
    		for (int i=0; i < n; i++) {
                PropertyDecl  p = properties.get(i);
                body = body.addMember(((PropertyDecl_c) p).abstractGetter(nf));
                body = body.addMember(p);
            }
        }
        return body;
    }
   
   /**
    * Return the synthetic getter metod for this property.
    * @return -- the getter method for this property.
    */
    public MethodDecl getter() {
	    return getter;
    }
   
    protected MethodDecl getter(X10NodeFactory nf) {
        X10TypeSystem ts = (X10TypeSystem) nf.extensionInfo().typeSystem();
        Position pos = Position.COMPILER_GENERATED;
        Flags flags = Flags.PUBLIC.Final();
        List<Formal> formals = Collections.EMPTY_LIST;
        List<TypeNode> throwTypes = Collections.EMPTY_LIST;
        Expr e = nf.Field(pos, nf.This(pos), name);
        //Report.report(1, "PropertyDecl_c: GOLDEN e=|" + e + " " + e.getClass());
        
        Stmt s = nf.Return(pos, e);
        Block body = nf.Block(pos, s);
        getter = nf.MethodDecl(pos, flags, type, name, formals, throwTypes, body);

        return getter;
    }
    
    /**
     * For Interfaces with properties, an abstract method signature for each property 
     * is generated in the interface body.
     * Any class implementing the interface has to have the same property 
     * <RAJ> 
     */
    public MethodDecl abstractGetter() {
    	return abstractGetter;
    }

    /**
     * For Interfaces with properties, an abstract method signature for each property 
     * is generated in the interface body.
     * Any class implementing the interface has to have the same property 
     * <RAJ> 
     */
    protected MethodDecl abstractGetter(X10NodeFactory nf) {
      abstractGetter = nf.MethodDecl(Position.COMPILER_GENERATED, Flags.PUBLIC.Abstract(), type, name, 
                              Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
      return abstractGetter;
    }
}
