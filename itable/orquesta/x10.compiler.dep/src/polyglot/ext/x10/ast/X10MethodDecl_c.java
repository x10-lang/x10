/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.MethodDecl_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10ProcedureInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.ext.x10.types.constr.BindingConstraintSystem;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/** A representation of a method declaration. Includes an extra field to represent the where clause
 * in the method definition.
 * 
 * @author vj
 *
 */
public class X10MethodDecl_c extends MethodDecl_c implements X10MethodDecl {
    // The representation of this( DepParameterExpr ) in the production.
    DepParameterExpr thisClause;
    // The representation of the : Constraint in the parameter list.
    DepParameterExpr whereClause;

    public X10MethodDecl_c(Position pos, DepParameterExpr thisClause, 
            Flags flags, TypeNode returnType,
            Id name, List formals, DepParameterExpr e, List throwTypes, Block body) {
        super(pos, flags, returnType, name, formals, throwTypes, body);
        whereClause = e;
    }
    
    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10MethodDecl_c n = (X10MethodDecl_c) super.buildTypesOverride(tb);
        X10MethodDef ci = (X10MethodDef) n.methodDef();
        if (n.whereClause() != null)
            ci.setWhereClause(n.whereClause().constraint());
        return n;
    }

    /** Visit the children of the method. */
    public Node visitSignature(NodeVisitor v) {
        X10MethodDecl_c result = (X10MethodDecl_c) super.visitSignature(v);
        DepParameterExpr thisClause = (DepParameterExpr) visitChild(result.thisClause, v);
        if (thisClause != result.thisClause)
            result = (X10MethodDecl_c) result.thisClause(thisClause);
        DepParameterExpr whereClause = (DepParameterExpr) visitChild(result.whereClause, v);
        if (whereClause != result.whereClause)
            result = (X10MethodDecl_c) result.whereClause(whereClause);
        return result;
    }

        public DepParameterExpr thisClause() { return thisClause; }
        public X10MethodDecl thisClause(DepParameterExpr e) {
        	X10MethodDecl_c n = (X10MethodDecl_c) copy();
        	n.thisClause = e;
        	return n;
        }
        public DepParameterExpr whereClause() { return whereClause; }
        public X10MethodDecl whereClause(DepParameterExpr e) {
        	X10MethodDecl_c n = (X10MethodDecl_c) copy();
        	n.whereClause = e;
        	return n;
        }
        
        @Override
        public Context enterChildScope(Node child, Context c) {
            // We should have entered the method scope already.
            assert c.currentCode() == this.methodDef();

            if (! formals.isEmpty() && child != body()) {
                // Push formals so they're in scope in the types of the other formals.
                c = c.pushBlock();
                for (Formal f : formals) {
                    f.addDecls(c);
                }
            }

            return super.enterChildScope(child, c);
        }

        public void translate(CodeWriter w, Translator tr) {
        	Context c = tr.context();
        	Flags flags = flags();

        	if (c.currentClass().flags().isInterface()) {
        		flags = flags.clearPublic();
        		flags = flags.clearAbstract();
        	}

        	// Hack to ensure that X10Flags are not printed out .. javac will
        	// not know what to do with them.

        	this.flags = X10Flags.toX10Flags(flags);
        	super.translate(w, tr);
        }
        
//        public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
//        	X10MethodDecl nn = this;
//            X10MethodDecl old = nn;
//
//            X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
//            
//            // Step I.a.  Check the where clause.
//            if (whereClause != null) {
//            	TypeChecker childtc3 = (TypeChecker) tc.enter(parent, nn);
//            	DepParameterExpr w = (DepParameterExpr) nn.visitChild(whereClause, childtc3);
//            	if (childtc3.hasErrors()) throw new SemanticException();
//            	nn = nn.whereClause(w);
//            	Constraint c = new TypeTranslator(xts).constraint(w.condition());
//            	((X10MethodDef) nn.methodDef()).setWhereClause(c);
//            }
//            
//            TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
//        	// First, record the final status of each of the formals.
//        	List<Formal> processedFormals = nn.visitList(nn.formals(), childtc);
//            nn = (X10MethodDecl) nn.formals(processedFormals);
//            // Now build the new formal arg list.
//            // TODO: Add a marker to the TypeChecker which records
//            // whether in fact it changed the type of any formal.
//            if (tc.hasErrors()) throw new SemanticException();
//            if (nn != old) {
//            	List<Formal> formals = nn.formals();
//            	
//            	//List newFormals = new ArrayList(formals.size());
//            	List<Type> formalTypes = new ArrayList<Type>(formals.size());
//
//            	X10ProcedureInstance pi = (X10ProcedureInstance) nn.methodInstance();
//            	Constraint c = pi.whereClause();
//                if (c != null)
//                    c = c.copy();
//    			
//            	for (Iterator<Formal> it = formals.iterator(); it.hasNext(); ) {
//            		Formal n =  it.next();
//            		X10Type newType = (X10Type) n.type().type();
//            		
//            		if (c != null) {
//            			// Fold the formal's constraint into the where clause.
//            			C_Var var = new TypeTranslator(xts).trans(n.localInstance());
//            			Constraint dep = newType.depClause().copy();
//            			Promise p = dep.intern(var);
//            			dep = dep.substitute(p.term(), C_Special.Self);
//            			c.addIn(dep);
//            		}
//            		
//            		//LocalInstance li = n.localInstance().type(newType);
//            		//newFormals.add(n.localInstance(li));
//            		formalTypes.add(newType);
//            	}
//
//            	nn.methodInstance().setFormalTypes(formalTypes);
//            	 // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
//
//            	// Fold this's constraint (the class invariant) into the where clause.
//            	{
//            		X10Type t = (X10Type) tc.context().currentClass();
//            		if (c != null && t.depClause() != null) {
//            			Constraint dep = t.depClause().copy();
//            			Promise p = dep.intern(C_Special.This);
//            			dep = dep.substitute(p.term(), C_Special.Self);
//            			c.addIn(dep);
//            		}
//            	}
//
//            	// Check if the where clause is consistent.
//            	if (c != null && ! c.consistent()) {
//            		throw new SemanticException("The method's dependent clause is inconsistent.",
//            				 whereClause != null ? whereClause.position() : position());
//            	}
//            }
//
//
//            // Step II. Check the return tpe. 
//            // Now visit the returntype to ensure that its depclause, if any is processed.
//            // Visit the formals so that they get added to the scope .. the return type
//            // may reference them.
//        	//TypeChecker tc1 = (TypeChecker) tc.copy();
//        	// childtc will have a "wrong" mi pushed in, but that doesnt matter.
//        	// we simply need to push in a non-null mi here.
//        	TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
//        	// Add the formals to the context.
//        	nn.visitList(nn.formals(),childtc1);
//        	(( X10Context ) childtc1.context()).setVarWhoseTypeIsBeingElaborated(null);
//        	final TypeNode r = (TypeNode) nn.visitChild(nn.returnType(), childtc1);
//            nn = (X10MethodDecl) nn.returnType(r);
//            final Type rt = r.type();
//            if (childtc1.hasErrors()) throw new SemanticException(); 
//            if (! rt.isCanonical()) {
//                return nn;
//            }
//            // Update the methodInstance with the depclause-enriched returnType.
//           	nn.methodInstance().setReturnType(rt);
//           // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
//           	// Step III. Check the body. 
//           	// We must do it with the correct mi -- the return type will be
//           	// checked by return e; statements in the body.
//           	
//           	TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn);
//           	// Add the formals to the context.
//           	nn.visitList(nn.formals(),childtc2);
//           	//Report.report(1, "X10MethodDecl_c: after visiting formals " + childtc2.context());
//           	// Now process the body.
//            nn = (X10MethodDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
//            if (childtc2.hasErrors()) throw new SemanticException();
//            nn = (X10MethodDecl) childtc2.leave(parent, old, nn, childtc2);
//            
//            return nn;
//        }
       
        private static final Collection TOPICS = 
            CollectionUtil.list(Report.types, Report.context);
}
