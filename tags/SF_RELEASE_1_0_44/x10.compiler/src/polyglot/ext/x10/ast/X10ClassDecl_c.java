/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
/**
 * The same as a Java class, except that it needs to handle properties.
 * Properties are converted into public final instance fields immediately.
 * TODO: Use the retType for the class during type checking.
 * @author vj
 *
 */
public class X10ClassDecl_c extends ClassDecl_c implements TypeDecl {
   
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
    public static TypeDecl make(Position pos, Flags flags, String name, 
            List<PropertyDecl> properties, TypeNode tci,
            TypeNode superClass, List interfaces, ClassBody body, X10NodeFactory nf, boolean valueClass) {
    	// Add the properties as fields in the class, together with a propertyName$ field
    	// encoding the fields that are properties.
    	boolean isInterface = flags.isInterface();
    	body = flags.isInterface() ? PropertyDecl_c.addGetters(properties, body, nf)
                : PropertyDecl_c.addProperties(properties, body, nf);
    	
        // Add the class invariant as the type of a faux static field in the class.
        if (tci != null)
        	body = addCI(tci,body,nf);
  
        TypeDecl result = valueClass ? 
        		new ValueClassDecl_c(pos, flags, name, properties, tci, superClass, 
                        interfaces, body, nf) :
        		new X10ClassDecl_c(pos, flags, name, properties,   tci, superClass, 
                interfaces, body);
        return result;
    }
    protected TypeNode classInvariant;
    protected X10ClassDecl_c(Position pos, Flags flags, String name,
            List<PropertyDecl> properties, TypeNode tci,
            TypeNode superClass, List interfaces, ClassBody body) {
        super(pos, flags, name, superClass, interfaces, body);
        this.classInvariant = tci;
    }
    /**
     * Add a class invariant field to the body of this class.
     * @param tn
     * @param body
     * @param nf
     * @return
     */
    public static ClassBody addCI(TypeNode tn, ClassBody body, X10NodeFactory nf) {
  	  X10TypeSystem ts = (X10TypeSystem) nf.extensionInfo().typeSystem();
  	  final Position pos = tn.position();
        FieldDecl f = new PropertyDecl_c(pos, 
      		  Flags.PUBLIC.Static().Final(), tn, 
      		  X10FieldInstance.MAGIC_CI_PROPERTY_NAME, nf.NullLit(pos), nf);
        body=body.addMember(f);
      return body;
  } 
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
    	tb = tb.pushClass(position(), flags, name);
    	
    	ParsedClassType type = tb.currentClass();
    	// TODO: NEED TO ADD STUFF TO SUPPORT THE CLASSINVARIANT.
    	
    	// Member classes of interfaces are implicitly public and static.
    	if (type.isMember() && type.outer().flags().isInterface()) {
    		type.flags(type.flags().Public().Static());
    	}
    	
    	// Member interfaces are implicitly static. 
    	if (type.isMember() && type.flags().isInterface()) {
    		type.flags(type.flags().Static());
    	}
    	
    	// Interfaces are implicitly abstract. 
    	if (type.flags().isInterface()) {
    		type.flags(type.flags().Abstract());
    	}
    	
    	return tb;
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
    	ClassDecl n = (ClassDecl) super.disambiguate(ar);
    	// Now we have successfully performed the base disambiguation.
    	X10Flags xf = X10Flags.toX10Flags(n.flags());
    	if (xf.isSafe()) {
    		ClassBody b = n.body();
    		List<ClassMember> m = b.members();
    		final int count = m.size();
    		List<ClassMember> newM = new ArrayList<ClassMember>(count);
    		for(int i=0; i < count; i++) {
    			ClassMember mem = m.get(i);
        		if (mem instanceof MethodDecl) {
        			MethodDecl decl = (MethodDecl) mem;
        			X10Flags mxf = X10Flags.toX10Flags(decl.flags()).Safe();
        			mem = decl.flags(mxf);
        		} 
        		newM.add(mem);
    		}
    		n = n.body(b.members(newM));
    	}
    	return n;
    }
 /*   public Context enterChildScope(Node child, Context c) {
        if (child == this.body || child == this.classInvariant) {
            TypeSystem ts = c.typeSystem();
            c = c.pushClass(type, ts.staticTarget(type).toClass());
        }
        return super.enterChildScope(child, c);
    }*/
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	X10ClassDecl_c result = (X10ClassDecl_c) super.typeCheck(tc);
    	
    	if (this.type instanceof X10ParsedClassType) {
    		X10ParsedClassType xpType = (X10ParsedClassType) type;
    		xpType.checkRealClause();
    	}
    	return result;
    }
  /*  protected ClassDecl_c reconstruct(TypeNode ci) {
	    if (classInvariant != ci) {
		    X10ClassDecl_c n = (X10ClassDecl_c) copy();
		    n.classInvariant = ci;
		 
		    return n;
	    }

	    return this;
    }
    		
    public Node visitChildren(NodeVisitor v) {
	    TypeNode ci = (TypeNode) visitChild(this.classInvariant, v);
	    return reconstruct(ci);
    }*/
} 
