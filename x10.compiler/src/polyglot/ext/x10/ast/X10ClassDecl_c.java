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
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
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
            TypeNode superClass, List interfaces, ClassBody body, X10NodeFactory nf) {
        body = flags.isInterface() ? PropertyDecl_c.addGetters(properties, body, nf)
                : PropertyDecl_c.addProperties(properties, body, nf);
       
        X10ClassDecl_c result = new X10ClassDecl_c(pos, flags, name, properties,  ci, superClass, 
                interfaces, body);
        // Report.report(1, "X10ClassDecl_c: Added synthetic field to "  + result);
        return result;
    }
   
    /** The list of properties for this class.
     * 
     */
    protected List properties;
    protected Expr classInvariant;
    
    protected X10ClassDecl_c(Position pos, Flags flags, String name,
            List/*<PropertyDecl>*/ properties, Expr ci,
            TypeNode superClass, List interfaces, ClassBody body) {
        super(pos, flags, name, superClass, interfaces, body);
        this.classInvariant = ci;
        this.properties = properties;
        
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
    		List m = b.members();
    		List newM = new ArrayList(m.size());
    		Iterator it = m.iterator();
        	while (it.hasNext()) {
        		ClassMember mem = (ClassMember) it.next();
        		if (mem instanceof MethodDecl) {
        			MethodDecl decl = (MethodDecl) mem;
        			X10Flags mxf = X10Flags.toX10Flags(decl.flags()).Safe();
        			newM.add(decl.flags(mxf));
        		} else {
        			newM.add(mem);
        		}
        	}
    		n = n.body(b.members(newM));
    	}
    //	Report.report(1, "X10ClassDecl_c: disambiguate returns |"+ n + "|");
    	return n;
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	X10ClassDecl_c result = (X10ClassDecl_c) super.typeCheck(tc);
    	
    	if (this.type instanceof X10ParsedClassType) {
    		X10ParsedClassType xpType = (X10ParsedClassType) type;
    		xpType.checkRealClause();
    	}
    	return result;
    }
    		
} 
