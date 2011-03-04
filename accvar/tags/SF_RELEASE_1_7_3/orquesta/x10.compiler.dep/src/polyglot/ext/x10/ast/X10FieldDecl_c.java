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
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c implements X10FieldDecl {

    DepParameterExpr thisClause;

    public X10FieldDecl_c(Position pos, DepParameterExpr thisClause, Flags flags, TypeNode type,
            Id name, Expr init)
    {
        super(pos, flags, type, name, init);
        this.thisClause = thisClause;
    }
    
    protected X10FieldDecl_c(Position pos,  Flags flags, TypeNode type,
            Id name, Expr init) {
        this(pos, null, flags, type, name, init);
    }
    
    public DepParameterExpr thisClause() {
        
        return thisClause;
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
        if (flags().isStatic() && (!flags().isFinal())) {
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

        return n;
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
            f = (X10FieldDecl_c) f.flags(fi.flags());
        }

        return f;
    }
}

