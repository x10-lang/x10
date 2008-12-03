/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import polyglot.ast.ArrayInit;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler.ClassInvariantDef;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassDef_c;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10ParsedClassType_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.ext.x10.types.constr.BindingConstraintSystem;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Scheduler;
import polyglot.frontend.JLScheduler.SignatureDef;
import polyglot.frontend.JLScheduler.SupertypeDef;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.ParsedClassType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
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
public class X10ClassDecl_c extends ClassDecl_c implements TypeDecl, X10ClassDecl {
   
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
    public static TypeDecl make(Position pos, Flags flags, Id name, 
            List<PropertyDecl> properties, DepParameterExpr tci,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body, X10NodeFactory nf, boolean valueClass) {
    	// Add the properties as fields in the class, together with a propertyName$ field
    	// encoding the fields that are properties.
    	boolean isInterface = flags.isInterface();
    	body = flags.isInterface() ? PropertyDecl_c.addGetters(properties, body, nf)
                : PropertyDecl_c.addProperties(properties, body, nf);
    	
        TypeDecl result = valueClass 
        ? new ValueClassDecl_c(pos, flags, name, tci, superClass, interfaces, body, nf)
        : new X10ClassDecl_c(pos, flags, name, tci, superClass, interfaces, body);
        return result;
    }
	
    protected DepParameterExpr classInvariant;
    protected X10ClassDecl_c(Position pos, Flags flags, Id name,
            DepParameterExpr tci,
            TypeNode superClass, List interfaces, ClassBody body) {
        super(pos, flags, name, superClass, interfaces, body);
        this.classInvariant = tci;
    }
    
    public DepParameterExpr classInvariant() {
    	return classInvariant;
    }
    
    public X10ClassDecl classInvariant(DepParameterExpr tn) {
    	if (this.classInvariant == tn) {
    		return this;
    	}
    	X10ClassDecl_c n = (X10ClassDecl_c) copy();
    	n.classInvariant = tn;
    	return n;
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
      		  nf.Id(pos, X10FieldInstance.MAGIC_CI_PROPERTY_NAME), nf.NullLit(pos), nf);
        body=body.addMember(f);
      return body;
  } 

    @Override
    protected void setSuperClass(TypeSystem ts, ClassDef thisType) throws SemanticException {
        TypeNode superClass = this.superClass;

        X10TypeSystem xts = (X10TypeSystem) ts;

        if (superClass != null || thisType.asType().typeEquals(ts.Object()) || thisType.fullName().equals(ts.Object().fullName())) {
            super.setSuperClass(ts, thisType);
        }
        else if (thisType.asType().typeEquals(xts.X10Object()) || thisType.fullName().equals(xts.X10Object().fullName())) {
            thisType.superType(Types.<Type>ref(ts.Object()));
        }
        else if (thisType.fullName().equals("x10.compilergenerated.Parameter1")) {
            thisType.superType(Types.<Type>ref(ts.Object()));
        }
        else {
            thisType.superType(Types.<Type>ref(xts.X10Object()));
        }
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
    
    public Context enterChildScope(Node child, Context c) {
    	X10Context xc = (X10Context) c;
    	
    	   if (child == this.superClass || this.interfaces.contains(child)) {
    		   
    	       X10ClassDef_c type = (X10ClassDef_c) this.type;
               xc = xc.pushSuperTypeDeclaration(type);
               
               // Add this class to the context, but don't push a class scope.
               // This allows us to detect loops in the inheritance
               // hierarchy, but avoids an infinite loop.
               xc = (X10Context) xc.pushBlock();
               xc.addNamed(type.asType());

               // For X10, also add the properties.
               for (FieldDef f : type.properties()) {
                   xc.addVariable(f.asInstance());
               }
               return child.del().enterScope(xc); 
           }
    	   
    	   if (child == this.classInvariant) {
    	       X10ClassDef_c type = (X10ClassDef_c) this.type;
    	       xc = (X10Context) xc.pushClass(type, type.asType());
    	       return child.del().enterScope(xc); 
    	   }
           return super.enterChildScope(child, xc);
    }
    
    public Node visitSignature(NodeVisitor v) {
        X10ClassDecl_c n = (X10ClassDecl_c) super.visitSignature(v);
        DepParameterExpr ci = (DepParameterExpr) visitChild(this.classInvariant, v);
        return ci == this.classInvariant ? n : n.classInvariant(ci);
    }
    
    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10ClassDecl_c n = (X10ClassDecl_c) super.buildTypesOverride(tb);
        
        X10ClassDef def = (X10ClassDef) n.type;
        
        X10Scheduler scheduler = (X10Scheduler) Globals.Scheduler();
        
        Goal g = scheduler.ClassInvariantDef(tb.job(), def);
        TypeBuilder tb2 = tb.pushGoal(g);

        DepParameterExpr ci = (DepParameterExpr) n.visitChild(n.classInvariant, tb2);

        if (ci != null) {
            def.setClassInvariant(ci.constraint());
            return n.classInvariant(ci);
        }

        return n;
    }
    
    public List<Goal> pregoals(TypeChecker tc, Def def) {
        X10Scheduler scheduler = (X10Scheduler) Globals.Scheduler();
        List<Goal> goals = Arrays.asList(new Goal[] {
                                                     scheduler.SupertypeDef(tc.job(), def),
                                                     scheduler.TypeCheckDef(tc.job(), def),
                                                     // do the class invariant LAST
                                                     scheduler.ClassInvariantDef(tc.job(), (X10ClassDef) def)
        });
        return goals;
    }
    
    public Node typeCheckClassInvariant(Node parent, TypeChecker tc, TypeChecker childtc) throws SemanticException {
        X10ClassDecl_c n = this;
        
        DepParameterExpr classInvariant = (DepParameterExpr) n.visitChild(n.classInvariant, childtc);

        // Lookup the node again in case another pass ran
        n = (X10ClassDecl_c) tc.getFragment(type).node();

        n = (X10ClassDecl_c) n.classInvariant(classInvariant);
        
        return n;
    }
    
    @Override
    protected Node typeCheckCurrentRoot(Node parent, TypeChecker tc, TypeChecker childtc, Goal goal) throws SemanticException {
        X10ClassDecl_c n = this;
        
        if (goal instanceof ClassInvariantDef) {
            n = (X10ClassDecl_c) n.typeCheckClassInvariant(parent, tc, childtc);
        }
        else {
            n = (X10ClassDecl_c) super.typeCheckCurrentRoot(parent, tc, childtc, goal);
        }
        
        return n;
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	X10ClassDecl_c result = (X10ClassDecl_c) super.typeCheck(tc);
    	X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

    	X10ParsedClassType ct = (X10ParsedClassType) this.type.asType();
    	Constraint c = ct.realClause();
    	ct.checkRealClause();
    	
    	return result;
    }
} 
