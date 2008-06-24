/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c implements X10FieldDecl {

    DepParameterExpr thisClause;

    public X10FieldDecl_c(Position pos, DepParameterExpr thisClause, FlagsNode flags, TypeNode type,
            Id name, Expr init)
    {
        super(pos, flags, type, name, init);
        this.thisClause = thisClause;
    }
    
    protected X10FieldDecl_c(Position pos,  FlagsNode flags, TypeNode type,
            Id name, Expr init) {
        this(pos, null, flags, type, name, init);
    }
    
    public DepParameterExpr thisClause() {
        return thisClause;
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

    public X10FieldDecl thisClause(DepParameterExpr thisClause) {
        if (thisClause == this.thisClause)
            return this;
        X10FieldDecl_c n = (X10FieldDecl_c) copy();
        n.thisClause = thisClause;
        return n;
    }
    
    @Override
    public Node visitSignature(NodeVisitor v) {
        DepParameterExpr thisClause = (DepParameterExpr) visitChild(this.thisClause, v);
        X10FieldDecl_c n = (X10FieldDecl_c) super.visitSignature(v);
        return n.thisClause(thisClause);
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        DepParameterExpr thisClause = (DepParameterExpr) visitChild(this.thisClause, v);
        X10FieldDecl_c n = (X10FieldDecl_c) super.visitChildren(v);
        return n.thisClause(thisClause);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Node result = super.typeCheck(tc);

        // Any occurrence of a non-final static field in X10
        // should be reported as an error.
        if (flags().flags().isStatic() && (!flags().flags().isFinal())) {
            throw new SemanticException("Non-final static field is illegal in X10",
                                        this.position());
        }
        return result;
    }

    @Override
    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();

        X10FieldDecl_c n = (X10FieldDecl_c) super.buildTypesOverride(tb);

        FieldDef fi = n.fieldDef();

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

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        X10FieldDecl_c f = (X10FieldDecl_c) super.disambiguate(ar);

        // [IP] All fields in value types should be final
        // [IP] FIXME: this will produce an "assignment to final" message --
        //      is that good enough?

        FieldDef fi = f.fieldDef();
        ReferenceType ref = fi.container().get();

        X10TypeSystem xts = (X10TypeSystem) ref.typeSystem();
        if (xts.isValueType(ref) && !fi.flags().isFinal()) {
            fi.setFlags(fi.flags().Final());
            f = (X10FieldDecl_c) f.flags(f.flags().flags(fi.flags()));
        }

        return f;
    }
}

