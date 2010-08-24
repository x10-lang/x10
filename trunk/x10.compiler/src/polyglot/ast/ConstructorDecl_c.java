/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>ConstructorDecl</code> is an immutable representation of a
 * constructor declaration as part of a class body.
 */
public class ConstructorDecl_c extends Term_c implements ConstructorDecl
{
    protected FlagsNode flags;
    protected Id name;
    protected List<Formal> formals;
    protected List<TypeNode> throwTypes;
    protected Block body;
    protected ConstructorDef ci;

    public ConstructorDecl_c(Position pos, FlagsNode flags, Id name, List<Formal> formals, List<TypeNode> throwTypes, Block body) {
        super(pos);
        assert(flags != null && name != null && formals != null && throwTypes != null); // body may be null
        this.flags = flags;
        this.name = name;
        this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
        this.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
        this.body = body;
    }

    public List<Def> defs() {
        return Collections.<Def>singletonList(ci);
    }

    public MemberDef memberDef() {
        return ci;
    }

    /** Get the flags of the constructor. */
    public FlagsNode flags() {
        return this.flags;
    }

    /** Set the flags of the constructor. */
    public ConstructorDecl flags(FlagsNode flags) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.flags = flags;
        return n;
    }

    /** Get the name of the constructor. */
    public Id name() {
        return this.name;
    }

    /** Set the name of the constructor. */
    public ConstructorDecl name(Id name) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.name = name;
        return n;
    }

    /** Get the formals of the constructor. */
    public List<Formal> formals() {
        return Collections.unmodifiableList(this.formals);
    }

    /** Set the formals of the constructor. */
    public ConstructorDecl formals(List<Formal> formals) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
        return n;
    }

    /** Get the throwTypes of the constructor. */
    public List<TypeNode> throwTypes() {
        return Collections.unmodifiableList(this.throwTypes);
    }

    /** Set the throwTypes of the constructor. */
    public ConstructorDecl throwTypes(List<TypeNode> throwTypes) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
        return n;
    }

    public Term codeBody() {
        return this.body;
    }

    /** Get the body of the constructor. */
    public Block body() {
        return this.body;
    }

    /** Set the body of the constructor. */
    public CodeBlock body(Block body) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.body = body;
        return n;
    }

    /** Get the constructorInstance of the constructor. */
    public ConstructorDef constructorDef() {
        return ci;
    }


    /** Get the procedureInstance of the constructor. */
    public ProcedureDef procedureInstance() {
        return ci;
    }

    public CodeDef codeDef() {
        return procedureInstance();
    }

    /** Set the constructorInstance of the constructor. */
    public ConstructorDecl constructorDef(ConstructorDef ci) {
        if (ci == this.ci) return this;
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.ci = ci;
        return n;
    }

    /** Reconstruct the constructor. */
    protected ConstructorDecl_c reconstruct(FlagsNode flags, Id name, List<Formal> formals, List<TypeNode> throwTypes, Block body) {
        if (flags != this.flags || name != this.name || ! CollectionUtil.allEqual(formals, this.formals) || ! CollectionUtil.allEqual(throwTypes, this.throwTypes) || body != this.body) {
            ConstructorDecl_c n = (ConstructorDecl_c) copy();
            n.flags = flags;
            n.name = name;
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
            n.body = body;
            return n;
        }

        return this;
    }

    /** Visit the children of the constructor. */
    public Node visitChildren(NodeVisitor v) {
        ConstructorDecl_c n = (ConstructorDecl_c) visitSignature(v);
        Block body = (Block) n.visitChild(n.body, v);
        return body == n.body ? n : n.body(body);
    }

    public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();

        ClassDef ct = tb.currentClass();
        assert ct != null;

        Flags flags = this.flags.flags();

        if (ct.flags().isInterface()) {
            flags = flags.Public().Abstract();
        }

        ConstructorDef ci = createConstructorDef(ts, ct, flags);
        ct.addConstructor(ci);

        TypeBuilder tbChk = tb.pushCode(ci);
        
        final TypeBuilder tbx = tb;
        final ConstructorDef mix = ci;
        
        ConstructorDecl_c n = (ConstructorDecl_c) this.visitSignature(new NodeVisitor() {
            int key = 0;
            public Node override(Node n) {
                return ConstructorDecl_c.this.visitChild(n, tbx.pushCode(mix));
            }
        });

        List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>(n.formals().size());
        for (Formal f : n.formals()) {
             formalTypes.add(f.type().typeRef());
        }

        List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>(n.throwTypes().size());
        for (TypeNode tn : n.throwTypes()) {
            throwTypes.add(tn.typeRef());
        }

        ci.setFormalTypes(formalTypes);
        ci.setThrowTypes(throwTypes);

        Block body = (Block) n.visitChild(n.body, tbChk);
        
        n = (ConstructorDecl_c) n.body(body);
        return n.constructorDef(ci);
    }

    protected ConstructorDef createConstructorDef(TypeSystem ts, ClassDef ct, Flags flags) {
	ConstructorDef ci = ts.constructorDef(position(), Types.ref(ct.asType()), flags,
                                              Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList());
	return ci;
    }

    public Context enterScope(Context c) {
        return c.pushCode(ci);
    }

    public Node visitSignature(NodeVisitor v) {
	FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
        Id name = (Id) this.visitChild(this.name, v);
        List<Formal> formals = this.visitList(this.formals, v);
        List<TypeNode> throwTypes = this.visitList(this.throwTypes, v);
        return reconstruct(flags, name, formals, throwTypes, this.body);
    }

    /** Type check the declaration. */
    public Node typeCheckBody(Node parent, TypeChecker tc, TypeChecker childtc) throws SemanticException {
        ConstructorDecl_c n = this;
        Block body = (Block) n.visitChild(n.body, childtc);
        n = (ConstructorDecl_c) n.body(body);
        return n;
    }

    /** Type check the constructor. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        for (TypeNode tn : throwTypes()) {
            Type t = tn.type();
            if (! t.isThrowable()) {
                throw new SemanticException("Type \"" + t +
                                            "\" is not a subclass of \"" + ts.Throwable() + "\".",
                                            tn.position());
            }
        }

        return this;
    }
    
    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
	Context c = tc.context();
	TypeSystem ts = tc.typeSystem();
	
	ClassType ct = c.currentClass();
	
	if (ct.flags().isInterface()) {
	    throw new SemanticException("Cannot declare a constructor inside an interface.",
	                                position());
	}
	
	if (ct.isAnonymous()) {
	    throw new SemanticException("Cannot declare a constructor inside an anonymous class.",
	                                position());
	}
	
	Name ctName = ct.name();
	
	if (! ctName.equals(name.id())) {
	    throw new SemanticException("Constructor name \"" + name +
	                                "\" does not match name of containing class \"" +
	                                ctName + "\".", position());
	}
	
	Flags flags = flags().flags();
	
	try {
	    ts.checkConstructorFlags(flags);
	}
	catch (SemanticException e) {
	    throw new SemanticException(e.getMessage(), position());
	}
	
	if (body == null && ! flags.isNative()) {
	    throw new SemanticException("Missing constructor body.",
	                                position());
	}
	
	if (body != null && flags.isNative()) {
	    throw new SemanticException("A native constructor cannot have a body.", position());
	}
	
	return this;
    }

    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) throws SemanticException {
        return ec.push(new ExceptionChecker.CodeTypeReporter("Constructor " + ci.signature())).push(constructorDef().asInstance().throwTypes());
    }

    public String toString() {
        return flags.flags().translate() + name + "(...)";
    }

    /** Write the constructor to an output file. */
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        
        tr.print(this, flags, w);
        tr.print(this, name, w);
        w.write("(");

        w.begin(0);

        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            print(f, w, tr);

            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }

        w.end();
        w.write(")");

        if (! throwTypes().isEmpty()) {
            w.allowBreak(6);
            w.write("throws ");

            for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(4, " ");
                }
            }
        }

        w.end();
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        prettyPrintHeader(w, tr);

        if (body != null) {
            printSubStmt(body, w, tr);
        }
        else {
            w.write(";");
        }
    }

    public void dump(CodeWriter w) {
        super.dump(w);

        if (ci != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + ci + ")");
            w.end();
        }
    }

    public Term firstChild() {
        return listChild(formals(), body() != null ? body() : null);
    }

    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
        if (body() != null) {
            v.visitCFGList(formals(), body(), ENTRY);
            v.visitCFG(body(), this, EXIT);
        }
        else {
            v.visitCFGList(formals(), this, EXIT);
        }

        return succs;
    }

}
