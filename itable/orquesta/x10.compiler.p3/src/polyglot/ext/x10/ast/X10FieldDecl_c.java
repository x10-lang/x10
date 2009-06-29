/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c implements X10FieldDecl {

    DepParameterExpr guard;

    public X10FieldDecl_c(Position pos, DepParameterExpr guard, FlagsNode flags, TypeNode type,
            Id name, Expr init)
    {
        super(pos, flags, type, name, init);
        this.guard = guard;
    }
    
    protected X10FieldDecl_c(Position pos,  FlagsNode flags, TypeNode type,
            Id name, Expr init) {
        this(pos, null, flags, type, name, init);
    }
    
    public DepParameterExpr guard() {
        return guard;
    }

	public Context enterChildScope(Node child, Context c) {
		if (child == this.type) {
			X10Context xc = (X10Context) c.pushBlock();
			FieldDef fi = fieldDef();
			xc.addVariable(fi.asInstance());
			xc.setVarWhoseTypeIsBeingElaborated(fi);
			c = xc;
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}

    public X10FieldDecl guard(DepParameterExpr guard) {
        if (guard == this.guard)
            return this;
        X10FieldDecl_c n = (X10FieldDecl_c) copy();
        n.guard = guard;
        return n;
    }
    
    @Override
    public Node visitSignature(NodeVisitor v) {
        X10FieldDecl_c n = (X10FieldDecl_c) super.visitSignature(v);
        DepParameterExpr guard = (DepParameterExpr) visitChild(this.guard, v);
        return n.guard(guard);
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        Node result = super.conformanceCheck(tc);

        // Any occurrence of a non-final static field in X10
        // should be reported as an error.
        if (flags().flags().isStatic() && (!flags().flags().isFinal())) {
            throw new SemanticException("Cannot declare static non-final field.",
                                        this.position());
        }
        
        FieldDef fi = fieldDef();
        StructType ref = fi.container().get();

        X10TypeSystem xts = (X10TypeSystem) ref.typeSystem();
        if (xts.isValueType(ref) && !fi.flags().isFinal()) {
            throw new SemanticException("Cannot declare a non-final field in a value class.", position());
        }

        checkVariance(tc);
        
        return result;
    }
    
    protected void checkVariance(ContextVisitor tc) throws SemanticException {
	X10Context c = (X10Context) tc.context();
	X10ClassDef cd;
	if (c.inSuperTypeDeclaration())
	    cd = c.supertypeDeclarationType();
	else
	    cd = (X10ClassDef) c.currentClassDef();
        final Map<Name,TypeProperty.Variance> vars = new HashMap<Name, TypeProperty.Variance>();
        for (int i = 0; i < cd.typeParameters().size(); i++) {
    	ParameterType pt = cd.typeParameters().get(i);
    	TypeProperty.Variance v = cd.variances().get(i);
    	vars.put(pt.name(), v);
        }
        if (flags().flags().isFinal()) {
            X10MethodDecl_c.checkVariancesOfType(type.position(), type.type(), TypeProperty.Variance.COVARIANT, "as the type of a final field", vars, tc);
        }
        else {
            X10MethodDecl_c.checkVariancesOfType(type.position(), type.type(), TypeProperty.Variance.INVARIANT, "as the type of a non-final field", vars, tc);
        }
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();

        X10FieldDecl_c n = (X10FieldDecl_c) super.buildTypesOverride(tb);
        
        X10FieldDef fi = (X10FieldDef) n.fieldDef();

        n = (X10FieldDecl_c) X10Del_c.visitAnnotations(n, tb);

        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
        if (as != null) {
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
            for (AnnotationNode an : as) {
                ats.add(an.annotationType().typeRef());
            }
            fi.setDefAnnotations(ats);
        }

        // Clear the static bit on properties
        if (this instanceof PropertyDecl) {
            Flags flags = flags().flags().clearStatic();
            n = (X10FieldDecl_c) n.flags(n.flags.flags(flags));
            fi.setFlags(flags);
        }

        // vj - shortcut and initialize the field instance if the decl has an initializer
        // This is the hack to permit reading the list of properties from the StringLit initializer
        // of a field, without waiting for a ConstantsChecked pass to run.
        if (init instanceof StringLit) {
            String val = ((StringLit) init).value();
            fi.setConstantValue(val);
        }

        // TODO: Could infer type of final fields as LCA of types assigned in the constructor.
        if (type instanceof UnknownTypeNode && init == null)
        	throw new SemanticException("Cannot infer field type; field has no initializer.", position());
        
        // Do not infer types of mutable fields, since there could be more than one assignment and the compiler might not see them all.
        if (type instanceof UnknownTypeNode && ! flags.flags().isFinal())
        	throw new SemanticException("Cannot infer type of non-final fields.", position());

        return n;
    }

	    @Override
	    public Node setResolverOverride(Node parent, TypeCheckPreparer v) {
		    if (type() instanceof UnknownTypeNode && init != null) {
			    UnknownTypeNode tn = (UnknownTypeNode) type();

			    NodeVisitor childv = v.enter(parent, this);
	    	            childv = childv.enter(this, type());
	    		    			    
			    if (childv instanceof TypeCheckPreparer) {
				    TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
				    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
				    TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
				    tc = (TypeChecker) tc.context(tcp.context().freeze());
				    r.setResolver(new TypeCheckExprGoal(this, init, tc, r));
			    }
		    }
		    return super.setResolverOverride(parent, v);
	    }

}

