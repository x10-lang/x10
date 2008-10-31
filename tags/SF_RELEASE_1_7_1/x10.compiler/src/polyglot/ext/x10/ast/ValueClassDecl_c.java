/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
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
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.ClassDecl_c;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

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

//    /**
//     * If there are any properties, add the property instances to the body,
//     * and the synthetic field, before creating the class instance.
//     * 
//     * @param pos
//     * @param flags
//     * @param name
//     * @param properties
//     * @param retType
//     * @param superClass
//     * @param interfaces
//     * @param body
//     * @return
//     */
//    public static ValueClassDecl_c make(Position pos, Flags flags, Id name, 
//            List/*<PropertyDecl>*/ properties, Expr ci,
//            TypeNode superClass, List interfaces, ClassBody body) {
//        body = PropertyDecl_c.addProperties(properties, body);
//        ValueClassDecl_c result = new ValueClassDecl_c(pos, flags, name, properties,  ci, superClass, 
//                interfaces, body);
//        return result;
//    }
   // protected final TypedList elist;
    
    // Keep this here for type checking.
 //protected TypeNode classInvariant;
    
	/**
	 * @param pos
	 * @param flags
	 * @param name
	 * @param superClass
	 * @param interfaces
	 * @param body
	 */
	public ValueClassDecl_c(Position pos, Flags flags, Id name,
            TypeNode ci, TypeNode superClass, List interfaces, ClassBody body,
            X10NodeFactory nf) {
            
		this(pos, flags, name, ci, superClass, addValueToInterfaces(interfaces, nf), body);
	}
	
	static List addValueToInterfaces(List interfaces, X10NodeFactory nf) {
		List elist = TypedList.copy(interfaces, TypeNode.class, false);
		X10TypeSystem ts = (X10TypeSystem) nf.extensionInfo().typeSystem();
		ClassType value = ts.value();
		CanonicalTypeNode ctn = nf.CanonicalTypeNode(Position.COMPILER_GENERATED, value);
		elist.add(0, ctn); 
		return elist;
	}

	public ValueClassDecl_c(Position pos, Flags flags, Id name,
            TypeNode ci, TypeNode superClass, List interfaces, ClassBody body) {
            
		super(pos, flags, name, superClass, interfaces, body);
	}
      
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10ParsedClassType type = (X10ParsedClassType) tb.currentClass();
		type.value(true);
		return super.buildTypes(tb);
	}

        public Node typeCheck(TypeChecker tc) throws SemanticException {
        	ValueClassDecl_c result = (ValueClassDecl_c) super.typeCheck(tc);
        	
        	if (this.type instanceof X10ParsedClassType) {
        		X10ParsedClassType xpType = (X10ParsedClassType) type;
        		xpType.checkRealClause();
        	}
        	return result;
        }
  /*      protected ClassDecl_c reconstruct(TypeNode ci) {
    	    if (classInvariant != ci) {
    		    ValueClassDecl_c n = (ValueClassDecl_c) copy();
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
