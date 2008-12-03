/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XPromise;
import x10.constraint.XSelf;
import x10.constraint.XVar;
/**
 * An X10ConstructorDecl differs from a ConstructorDecl in that it has a returnType.
 *
 * @author vj
 */
public class X10ConstructorDecl_c extends ConstructorDecl_c implements X10ConstructorDecl {
   
    protected DepParameterExpr guard; // ignored for now.
    protected TypeNode returnType;
    protected List<TypeParamNode> typeParameters;
    
    public X10ConstructorDecl_c(Position pos, FlagsNode flags, 
            Id name, TypeNode returnType, 
            List<TypeParamNode> typeParams, List<Formal> formals, 
            DepParameterExpr guard, List<TypeNode> throwTypes, Block body) {
        super(pos, flags,  name, formals, throwTypes, body);
        this.returnType = returnType;
        this.guard = guard;
        this.typeParameters = TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
    }
    
    public TypeNode returnType() {
        return returnType;
    }

    public DepParameterExpr guard() {
        return guard;
    }

    public X10ConstructorDecl guard(DepParameterExpr e) {
        X10ConstructorDecl_c n = (X10ConstructorDecl_c) copy();
        n.guard = e;
        return n;
    }
    
    public List<TypeParamNode> typeParameters() {
	    return typeParameters;
    }
    
    public X10ConstructorDecl typeParameters(List<TypeParamNode> typeParams) {
	    X10ConstructorDecl_c n = (X10ConstructorDecl_c) copy();
	    n.typeParameters=TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
	    return n;
    }

    /** Reconstruct the constructor. */
    public X10ConstructorDecl returnType(TypeNode returnType) {
        if (returnType != this.returnType) {
            X10ConstructorDecl_c n = (X10ConstructorDecl_c) copy();
            n.returnType = returnType;
            return n;
        }
        return this;
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
	NodeFactory nf = tb.nodeFactory();
	
        X10ConstructorDecl_c n = (X10ConstructorDecl_c) super.buildTypesOverride(tb);
        
        X10ConstructorDef ci = (X10ConstructorDef) n.constructorDef();

        n = (X10ConstructorDecl_c) X10Del_c.visitAnnotations(n, tb);

        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
        if (as != null) {
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
            for (AnnotationNode an : as) {
                ats.add(an.annotationType().typeRef());
            }
            ci.setDefAnnotations(ats);
        }

        ClassDef currentClass = tb.currentClass();

        // Set the constructor name to the short name of the class, to shut up the Java type-checker.
        // The X10 parser has "this" for the name.
	n = (X10ConstructorDecl_c) n.name(nf.Id(n.position(), currentClass.name()));
        
        
        if (returnType == null)
        	n = (X10ConstructorDecl_c) n.returnType(nf.CanonicalTypeNode(n.position(), currentClass.asType()));
        
        ci.setReturnType((Ref<? extends X10ClassType>) n.returnType().typeRef());
        
        if (n.guard() != null)
            ci.setGuard(n.guard().xconstraint());

        if (X10ClassDecl_c.CLASS_TYPE_PARAMETERS) {
            if (n.typeParameters().size() > 0)
                throw new SemanticException("Constructors cannot have type parameters.", n.position());
        }
        
        List<Ref<? extends Type>> typeParameters = new ArrayList<Ref<? extends Type>>(n.typeParameters().size());
        for (TypeParamNode tpn : n.typeParameters()) {
        	typeParameters.add(Types.ref(tpn.type()));
        }
        ci.setTypeParameters(typeParameters);
        
        List<LocalDef> formalNames = new ArrayList<LocalDef>(n.formals().size());
        for (Formal f : n.formals()) {
            formalNames.add(f.localDef());
        }
        ci.setFormalNames(formalNames);
        
        return n;
    }

    public Context enterChildScope(Node child, Context c) {
        // We should have entered the constructor scope already.
        assert c.currentCode() == this.constructorDef();

        if (child != body) {
            // Push formals so they're in scope in the types of the other formals.
            c = c.pushBlock();
            for (TypeParamNode f : typeParameters) {
                f.addDecls(c);
            }
            for (Formal f : formals) {
                f.addDecls(c);
            }
        }

        return super.enterChildScope(child, c);
    }

    /** Visit the children of the method. */
    public Node visitSignature(NodeVisitor v) {
    	X10ConstructorDecl_c result = (X10ConstructorDecl_c) super.visitSignature(v);
        List<TypeParamNode> typeParams = (List<TypeParamNode>) visitList(result.typeParameters, v);
        if (! CollectionUtil.allEqual(typeParams, result.typeParameters))
            result = (X10ConstructorDecl_c) result.typeParameters(typeParams);
    	TypeNode returnType = (TypeNode) visitChild(result.returnType, v);
    	if (returnType != result.returnType)
    	    result = (X10ConstructorDecl_c) result.returnType(returnType);
    	DepParameterExpr guard = (DepParameterExpr) visitChild(result.guard, v);
    	if (guard != result.guard)
    	    result = (X10ConstructorDecl_c) result.guard(guard);
    	return result;
    }


    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
    	X10ConstructorDecl nn = this;
    	X10ConstructorDecl old = nn;

        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        
        // Step I.a.  Check the formals.
        TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
        
    	// First, record the final status of each of the type params and formals.
        List<TypeParamNode> processedTypeParams = nn.visitList(nn.typeParameters(), childtc);
        nn = (X10ConstructorDecl) nn.typeParameters(processedTypeParams);
    	List<Formal> processedFormals = nn.visitList(nn.formals(), childtc);
        nn = (X10ConstructorDecl) nn.formals(processedFormals);
        if (tc.hasErrors()) throw new SemanticException();
        
        nn = (X10ConstructorDecl) X10Del_c.visitAnnotations(nn, childtc);

        // [NN]: Don't do this here, do it on lookup of the formal.  We don't want spurious self constraints in the signature.
//        for (Formal n : processedFormals) {
//    		Ref<Type> ref = (Ref<Type>) n.type().typeRef();
//    		Type newType = ref.get();
//    		
//    		if (n.localDef().flags().isFinal()) {
//    			XConstraint c = X10TypeMixin.xclause(newType);
//    			if (c == null) c = new XConstraint_c();
//    			try {
//				c.addSelfBinding(xts.xtypeTranslator().trans(n.localDef().asInstance()));
//			}
//			catch (XFailure e) {
//				throw new SemanticException(e.getMessage(), position());
//			}
//    			newType = X10TypeMixin.xclause(newType, c);
//    		}
//    		
//    		ref.update(newType);
//        }
        
        // Step I.b.  Check the guard.
        if (nn.guard() != null) {
        	DepParameterExpr processedWhere = (DepParameterExpr) nn.visitChild(nn.guard(), childtc);
        	nn = (X10ConstructorDecl) nn.guard(processedWhere);
        	if (tc.hasErrors()) throw new SemanticException();
        
        	// Now build the new formal arg list.
        	// TODO: Add a marker to the TypeChecker which records
        	// whether in fact it changed the type of any formal.
        	List<Formal> formals = processedFormals;
        	
        	//List newFormals = new ArrayList(formals.size());
        	X10ProcedureDef pi = (X10ProcedureDef) nn.memberDef();
        	XConstraint c = pi.guard().get();
            	try {
            		if (c != null) {
            			c = c.copy();

            			for (Formal n : formals) {
            				Ref<Type> ref = (Ref<Type>) n.type().typeRef();
            				X10Type newType = (X10Type) ref.get();

            				// Fold the formal's constraint into the guard.
            				XVar var = xts.xtypeTranslator().trans(n.localDef().asInstance());
            				XConstraint dep = X10TypeMixin.xclause(newType).copy();
            				XPromise p = dep.intern(var);
            				dep = dep.substitute(p.term(), XSelf.Self);
            				c.addIn(dep);

            				ref.update(newType);
            			}
            		}

            		// Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());

            		// Fold this's constraint (the class invariant) into the guard.
            		{
            			X10Type t = (X10Type) tc.context().currentClass();
            			XConstraint dep = X10TypeMixin.xclause(t);
            			if (c != null && dep != null) {
            				dep = dep.copy();
            				XPromise p = dep.intern(xts.xtypeTranslator().transThis(t));
            				dep = dep.substitute(p.term(), XSelf.Self);
            				c.addIn(dep);
            			}
            		}
            	}
            	catch (XFailure e) {
            		throw new SemanticException(e.getMessage(), position());
            	}

        	// Check if the guard is consistent.
        	if (c != null && ! c.consistent()) {
        		throw new SemanticException("The constructor's dependent clause is inconsistent.",
        				 guard != null ? guard.position() : position());
        	}
        }

        X10ConstructorDef nnci = (X10ConstructorDef) nn.constructorDef();

        // Step II. Check the return type. 
        // Now visit the returntype to ensure that its depclause, if any is processed.
        // Visit the formals so that they get added to the scope .. the return type
        // may reference them.
    	//TypeChecker tc1 = (TypeChecker) tc.copy();
    	// childtc will have a "wrong" mi pushed in, but that doesnt matter.
    	// we simply need to push in a non-null mi here.
    	TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
    	if (childtc1.context() == tc.context())
    	    childtc1 = (TypeChecker) childtc1.context((Context) tc.context().copy());
    	// Add the type params and formals to the context.
    	nn.visitList(nn.typeParameters(),childtc1);
    	nn.visitList(nn.formals(),childtc1);
    	(( X10Context ) childtc1.context()).setVarWhoseTypeIsBeingElaborated(null);
    	final TypeNode r = (TypeNode) nn.visitChild(nn.returnType(), childtc1);
    	if (childtc1.hasErrors()) throw new SemanticException();
        nn = (X10ConstructorDecl) nn.returnType(r);
        ((Ref<Type>) nnci.returnType()).update(r.type());
       // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
        
        Type retTypeBase = X10TypeMixin.xclause(r.type(), (XConstraint) null);
        Type clazz = X10TypeMixin.xclause(Types.get(nnci.container()), (XConstraint) null);
        if (! xts.typeEquals(retTypeBase, clazz)) {
        	throw new SemanticException("The return type of the constructor (" + retTypeBase 
        			+ " must be derived from"
        			+ " the type of the class (" + clazz + ") on which the constructor is defined.",
        			position());
        }
    
       	// Step III. Check the body. 
       	// We must do it with the correct mi -- the return type will be
       	// checked by return e; statements in the body.
       	
       	TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn);
       	// Add the formals to the context.
       	nn.visitList(nn.typeParameters(),childtc2);
       	nn.visitList(nn.formals(),childtc2);
       	//Report.report(1, "X10MethodDecl_c: after visiting formals " + childtc2.context());
       	// Now process the body.
        nn = (X10ConstructorDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
        if (childtc2.hasErrors()) throw new SemanticException();
        nn = (X10ConstructorDecl) childtc2.leave(parent, old, nn, childtc2);
        
        X10MethodDecl_c.dupFormalCheck(typeParameters, formals);

        return nn;
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        X10ConstructorDecl_c n = (X10ConstructorDecl_c) super.conformanceCheck(tc);

        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        Type retTypeBase = X10TypeMixin.xclause(n.returnType().type(), (XConstraint) null);
        X10ConstructorDef nnci = (X10ConstructorDef) n.constructorDef();
        Type clazz = nnci.asInstance().container();
        if (! ts.typeEquals(retTypeBase, clazz)) {
            throw new SemanticException("The return type of the constructor (" + retTypeBase 
                                        + " must be derived from"
                                        + " the type of the class (" + clazz + ") on which the constructor is defined.",
                                        n.position());
        }
        return n;
    }
}
