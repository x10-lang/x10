/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * A <code>LocalDecl</code> is an immutable representation of the declaration
 * of a local variable.
 */
public abstract class LocalDecl_c extends Stmt_c implements LocalDecl {
    protected FlagsNode flags;
    protected TypeNode type;
    protected Id name;
    protected Expr init;
    protected LocalDef li;

    public LocalDecl_c(Position pos, FlagsNode flags, TypeNode type,
                       Id name, Expr init)
    {
        super(pos);
        assert(flags != null && type != null && name != null); // init may be null
        this.flags = flags;
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public List<Def> defs() {
        return Collections.<Def>singletonList(li);
    }

    /** Get the type of the declaration. */
    public Type declType() {
        return type.type();
    }

    /** Get the flags of the declaration. */
    public FlagsNode flags() {
        return flags;
    }

    /** Set the flags of the declaration. */
    public LocalDecl flags(FlagsNode flags) {
        LocalDecl_c n = (LocalDecl_c) copy();
        n.flags = flags;
        return n;
    }

    /** Get the type node of the declaration. */
    public TypeNode type() {
        return type;
    }

    /** Set the type of the declaration. */
    public LocalDecl type(TypeNode type) {
        if (type == this.type) return this;
        LocalDecl_c n = (LocalDecl_c) copy();
        n.type = type;
        return n;
    }

    /** Get the name of the declaration. */
    public Id name() {
        return name;
    }

    /** Set the name of the declaration. */
    public LocalDecl name(Id name) {
        LocalDecl_c n = (LocalDecl_c) copy();
        n.name = name;
        return n;
    }

    /** Get the initializer of the declaration. */
    public Expr init() {
        return init;
    }

    /** Set the initializer of the declaration. */
    public LocalDecl init(Expr init) {
        if (init == this.init) return this;
        LocalDecl_c n = (LocalDecl_c) copy();
        n.init = init;
        return n;
    }

    /** Set the local instance of the declaration. */
    public LocalDecl localDef(LocalDef li) {
        if (li == this.li) return this;
        LocalDecl_c n = (LocalDecl_c) copy();
        assert li != null;
        n.li = li;
        return n;
    }

    /** Get the local instance of the declaration. */
    public LocalDef localDef() {
        return li;
    }

    public VarDef varDef() {
        return li;
    }

    /** Reconstruct the declaration. */
    protected LocalDecl_c reconstruct(FlagsNode flags, TypeNode type, Id name, Expr init) {
        if (this.flags != flags || this.type != type || this.name != name || this.init != init) {
            LocalDecl_c n = (LocalDecl_c) copy();
            n.flags = flags;
            n.type = type;
            n.name = name;
            n.init = init;
            return n;
        }

        return this;
    }

    /** Visit the children of the declaration. */
    public Node visitChildren(NodeVisitor v) {
        TypeNode type = (TypeNode) visitChild(this.type, v);
        FlagsNode flags = (FlagsNode) visitChild(this.flags, v);
        Id name = (Id) visitChild(this.name, v);
        Expr init = (Expr) visitChild(this.init, v);
        return reconstruct(flags, type, name, init);
    }


    public void addDecls(Context c) {
        // Add the declaration of the variable in case we haven't already done
        // so in enterScope, when visiting the initializer.
        c.addVariable(li.asInstance());
    }

    public Node buildTypes(TypeBuilder tb) {
        LocalDecl_c n = (LocalDecl_c) super.buildTypes(tb);
        TypeSystem ts = tb.typeSystem();

        LocalDef li = ts.localDef(position(), flags().flags(), type.typeRef(), name.id());
        return n.localDef(li);
    }

    /**
     * Override superclass behavior to check if the variable is multiply
     * defined.
     */
    public NodeVisitor typeCheckEnter(TypeChecker tc) {
        // Check if the variable is multiply defined.
        // we do it in type check enter, instead of type check since
        // we add the declaration before we enter the scope of the
        // initializer.
        Context c = tc.context();

        LocalInstance outerLocal = null;

        try {
            outerLocal = c.findLocal(li.name());
        }
        catch (SemanticException e) {
            // not found, so not multiply defined
        }

        if (outerLocal != null && c.isLocal(li.name())) {
            Errors.issue(tc.job(),
                    new SemanticException("Local variable \"" + name + "\" multiply defined. Previous definition at " + outerLocal.position() + ".", position()));
        }

        return super.typeCheckEnter(tc);
    }

    /** Type check the declaration. */
    public abstract Node typeCheck(ContextVisitor tc);

    public Node checkConstants(ContextVisitor tc) {
        if (init == null || ! init.isConstant() || ! li.flags().isFinal()) {
            li.setNotConstant();
        }
        else {
            li.setConstantValue(init.constantValue());
        }

        return this;
    }

    public abstract String toString();

    public abstract void prettyPrint(CodeWriter w, PrettyPrinter tr);

    public void dump(CodeWriter w) {
        super.dump(w);

        if (li != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + li + ")");
            w.end();
        }
    }

    public Term firstChild() {
        return type();
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (init() != null) {
            v.visitCFG(type(), init(), ENTRY);
            v.visitCFG(init(), this, EXIT);
        } else {
            v.visitCFG(type(), this, EXIT);
        }

        return succs;
    }

}
