/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.LazyRef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10FieldDef;
import x10.types.X10InitializerDef;
import x10.types.X10TypeSystem;

public class X10FieldDecl_c extends FieldDecl_c implements X10FieldDecl {

    public X10FieldDecl_c(Position pos, FlagsNode flags, TypeNode type,
            Id name, Expr init)
    {
        super(pos, flags, type, name, init);
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
        X10Context context = (X10Context) tc.context();
        if (xts.isValueType(ref, context) && !fi.flags().isFinal()) {
            throw new SemanticException("Cannot declare a non-final field in a value class.", position());
        }

        checkVariance(tc);
        
        X10MethodDecl_c.checkVisibility(tc.typeSystem(), context, this);
        
        return result;
    }
    
    protected void checkVariance(ContextVisitor tc) throws SemanticException {
	X10Context c = (X10Context) tc.context();
	X10ClassDef cd;
	if (c.inSuperTypeDeclaration())
	    cd = c.supertypeDeclarationType();
	else
	    cd = (X10ClassDef) c.currentClassDef();
        final Map<Name,ParameterType.Variance> vars = new HashMap<Name, ParameterType.Variance>();
        for (int i = 0; i < cd.typeParameters().size(); i++) {
    	ParameterType pt = cd.typeParameters().get(i);
    	ParameterType.Variance v = cd.variances().get(i);
    	vars.put(pt.name(), v);
        }
        if (flags().flags().isFinal()) {
            X10MethodDecl_c.checkVariancesOfType(type.position(), type.type(), ParameterType.Variance.COVARIANT, "as the type of a final field", vars, tc);
        }
        else {
            X10MethodDecl_c.checkVariancesOfType(type.position(), type.type(), ParameterType.Variance.INVARIANT, "as the type of a non-final field", vars, tc);
        }
    }

    protected FieldDef createFieldDef(TypeSystem ts, ClassDef ct, Flags flags) {
        X10FieldDef fi = (X10FieldDef) ts.fieldDef(position(), Types.ref(ct.asType()), flags, type.typeRef(), name.id());
        fi.setThisVar(((X10ClassDef) ct).thisVar());
        return fi;
    }
    
    protected InitializerDef createInitializerDef(TypeSystem ts, ClassDef ct, Flags iflags) {
        X10InitializerDef ii;
        ii = (X10InitializerDef) super.createInitializerDef(ts, ct , iflags);
        ii.setThisVar(((X10ClassDef) ct).thisVar());
        return ii;
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();

        X10FieldDecl_c n = (X10FieldDecl_c) super.buildTypesOverride(tb);
        
        X10FieldDef fi = (X10FieldDef) n.fieldDef();
        
        final ClassDef container = tb.currentClass();

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
	    	            childv = childv.enter(this, init);
	    		    			    
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

	        @Override
	        public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
	            if (type() instanceof UnknownTypeNode) {
	                NodeVisitor childtc = tc.enter(parent, this);
	                
	                Expr init = (Expr) this.visitChild(init(), childtc);
	                if (init != null) {
	                    Type t = init.type();
	                    if (t instanceof X10ClassType) {
	                        X10ClassType ct = (X10ClassType) t;
	                        if (ct.isAnonymous()) {
	                            if (ct.interfaces().size() > 0)
	                                t = ct.interfaces().get(0);
	                            else
	                                t = ct.superClass();
	                        }
	                    }
	                    LazyRef<Type> r = (LazyRef<Type>) type().typeRef();
	                    r.update(t);
	                    
	                    FlagsNode flags = (FlagsNode) this.visitChild(flags(), childtc);
	                    Id name = (Id) this.visitChild(name(), childtc);
	                    TypeNode tn = (TypeNode) this.visitChild(type(), childtc);
	                    
	                    Node n = tc.leave(parent, this, reconstruct(flags, tn, name, init), childtc);
	                    List<AnnotationNode> oldAnnotations = ((X10Ext) ext()).annotations();
	                    if (oldAnnotations == null || oldAnnotations.isEmpty()) {
	                            return n;
	                    }
	                    List<AnnotationNode> newAnnotations = node().visitList(oldAnnotations, childtc);
	                    if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
	                            return ((X10Del) n.del()).annotations(newAnnotations);
	                    }
	                    return n;
	                }
	            }
	            return super.typeCheckOverride(parent, tc);
	        }
	        
	    @Override
	        public Node typeCheck(ContextVisitor tc) throws SemanticException {
	            if (this.type().type().isVoid())
	                throw new SemanticException("Field cannot have type " + this.type().type() + ".", position());

	            X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	            X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
	            X10Context context = (X10Context) tc.context();
	            
	            X10FieldDecl_c n = this;

	            // Add an initializer to uninitialized var fields.
                    if (! n.flags().flags().isFinal() && n.init() == null) {
                        Type t = n.type().type();
                        Expr e = null;
                        if (t.isBoolean()) {
                            e = (Expr) nf.BooleanLit(position(), false).del().typeCheck(tc).checkConstants(tc);
                        }
                        if (t.isIntOrLess()) {
                            e = (Expr) nf.IntLit(position(), IntLit.INT, 0L).del().typeCheck(tc).checkConstants(tc);
                        }
                        if (t.isLong()) {
                            e = (Expr) nf.IntLit(position(), IntLit.LONG, 0L).del().typeCheck(tc).checkConstants(tc);
                        }
                        if (t.isFloat()) {
                            e = (Expr) nf.FloatLit(position(), FloatLit.FLOAT, 0.0).del().typeCheck(tc).checkConstants(tc);
                        }
                        if (t.isDouble()) {
                            e = (Expr) nf.FloatLit(position(), FloatLit.DOUBLE, 0.0).del().typeCheck(tc).checkConstants(tc);
                        }
                        if (ts.isSubtype(t, ts.String(), tc.context())) {
                            e = (Expr) nf.StringLit(position(), "").del().typeCheck(tc).checkConstants(tc);
                        }
                        if (ts.isReferenceType(t, context)) {
                            e = (Expr) nf.NullLit(position()).del().typeCheck(tc).checkConstants(tc);
                        }
                        
                        if (e != null) {
                            n = (X10FieldDecl_c) n.init(e);
                        }
                    }
                    
                    if (init != null) {
                        try {
                            Expr newInit = X10New_c.attemptCoercion(tc, init, this.type().type());
                            return this.init(newInit);
                        }
                        catch (SemanticException e) {
                            throw new SemanticException("The type of the variable " +
                                                        "initializer \"" + init.type() +
                                                        "\" does not match that of " +
                                                        "the declaration \"" +
                                                        type.type() + "\".",
                                                        init.position());
                        }
                    }

                    return this;
        }

	    public Type childExpectedType(Expr child, AscriptionVisitor av) {
	        if (child == init) {
	            TypeSystem ts = av.typeSystem();
	            return type.type();
	        }

	        return child.type();
	    }
}

