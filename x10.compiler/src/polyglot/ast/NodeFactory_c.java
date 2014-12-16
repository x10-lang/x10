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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.List;

import x10.ExtensionInfo;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.*;

/**
 * A <code>NodeFactory</code> constructs AST nodes.  All node construction
 * should go through this factory or by done with the <code>copy()</code>
 * method of <code>Node</code>.
 */
public abstract class NodeFactory_c extends AbstractNodeFactory_c implements NodeFactory
{
    private final ExtFactory extFactory;
    private final DelFactory delFactory;
	private final ExtensionInfo extInfo;
    
    public NodeFactory_c(ExtensionInfo extInfo, ExtFactory extFactory, DelFactory delFactory) {
        this.extInfo = extInfo;
        this.extFactory = extFactory;
        this.delFactory = delFactory;
    }
    
    protected ExtFactory extFactory() {
        return this.extFactory;
    }

    protected DelFactory delFactory() {
        return this.delFactory;
    }

	public ExtensionInfo extensionInfo() {
		return extInfo;
	}

    /**
     * Utility method to find an instance of an Extension Factory
     */
    protected final ExtFactory findExtFactInstance(Class<?> c) {
        ExtFactory e = extFactory();
        while (e != null) {
            if (c.isInstance(e)) {
                // the factory e is an instance of the class c
                return e;
            }
            e = e.nextExtFactory();
        }
        return null;
    }
    
    public FlagsNode FlagsNode(Position pos, Flags flags) {
	    FlagsNode n = new FlagsNode_c(pos, flags);
	    n = (FlagsNode) n.ext(extFactory.extFlagsNode());
	    n = (FlagsNode) n.del(delFactory.delFlagsNode());
	    return n;
    }
    public Id Id(Position pos, Name name) {
        Id n = new Id_c(pos, name);
        n = (Id) n.ext(extFactory.extId());
        n = (Id) n.del(delFactory.delId());
        return n;
    }

    public Allocation Allocation(Position pos, TypeNode objType, List<TypeNode> typeArgs) {
        Allocation a = new Allocation_c(pos, objType, typeArgs);
        a = (Allocation) a.ext(extFactory().extExpr());
        a = (Allocation) a.del(delFactory().delExpr());
        return a;
    }

    public AmbPrefix AmbPrefix(Position pos, Prefix prefix, Id name) {
        AmbPrefix n = new AmbPrefix_c(pos, prefix, name);
        n = (AmbPrefix)n.ext(extFactory.extAmbPrefix());
        n = (AmbPrefix)n.del(delFactory.delAmbPrefix());
        return n;
    }
    
    public ArrayAccess ArrayAccess(Position pos, Expr base, Expr index) {
        ArrayAccess n = new ArrayAccess_c(pos, base, index);
        n = (ArrayAccess)n.ext(extFactory.extArrayAccess());
        n = (ArrayAccess)n.del(delFactory.delArrayAccess());
        return n;
    }

    public ArrayInit ArrayInit(Position pos, List<Expr> elements) {
        ArrayInit n = new ArrayInit_c(pos, CollectionUtil.nonNullList(elements));
        n = (ArrayInit)n.ext(extFactory.extArrayInit());
        n = (ArrayInit)n.del(delFactory.delArrayInit());
        return n;
    }

    public Assert Assert(Position pos, Expr cond, Expr errorMessage) {
        Assert n = new Assert_c(pos, cond, errorMessage);
        n = (Assert)n.ext(extFactory.extAssert());
        n = (Assert)n.del(delFactory.delAssert());
        return n;
    }

    protected Assign SUPER_Assign(Position pos, Expr left, Assign.Operator op, Expr right) {
        if (left instanceof Local) {
            return LocalAssign(pos, (Local)left, op, right);
        }
        else if (left instanceof Field) {
            return FieldAssign(pos, ((Field)left).target(), ((Field)left).name(), op, right);
        } 
        else if (left instanceof ArrayAccess) {
            return ArrayAccessAssign(pos, ((ArrayAccess)left).array(), ((ArrayAccess)left).index(), op, right);
        }
        else if (left instanceof Ambiguous) {
            return AmbAssign(pos, left, op, right);
        }
        else {
            throw new InternalCompilerError("Cannot create assignment to " + left);
        }
    }

    public ArrayAccessAssign ArrayAccessAssign(Position pos, Expr array, Expr index, Assign.Operator op, Expr right) {
        ArrayAccessAssign n = new ArrayAccessAssign_c(this, pos, array, index, op, right);
        n = (ArrayAccessAssign)n.ext(extFactory.extArrayAccessAssign());
        n = (ArrayAccessAssign)n.del(delFactory.delArrayAccessAssign());
        return n;
    }

    public Block Block(Position pos, List<Stmt> statements) {
        Block n = new Block_c(pos, CollectionUtil.nonNullList(statements));
        n = (Block)n.ext(extFactory.extBlock());
        n = (Block)n.del(delFactory.delBlock());
        return n;
    }

    public SwitchBlock SwitchBlock(Position pos, List<Stmt> statements) {
        SwitchBlock n = new SwitchBlock_c(pos, CollectionUtil.nonNullList(statements));
        n = (SwitchBlock)n.ext(extFactory.extSwitchBlock());
        n = (SwitchBlock)n.del(delFactory.delSwitchBlock());
        return n;
    }

    public Branch Branch(Position pos, Branch.Kind kind, Id label) {
        Branch n = new Branch_c(pos, kind, label);
        n = (Branch)n.ext(extFactory.extBranch());
        n = (Branch)n.del(delFactory.delBranch());
        return n;
    }

    public Case Case(Position pos, Expr expr) {
        Case n = new Case_c(pos, expr);
        n = (Case)n.ext(extFactory.extCase());
        n = (Case)n.del(delFactory.delCase());
        return n;
    }

    public Catch Catch(Position pos, Formal formal, Block body) {
        Catch n = new Catch_c(pos, formal, body);
        n = (Catch)n.ext(extFactory.extCatch());
        n = (Catch)n.del(delFactory.delCatch());
        return n;
    }

    public ClassLit ClassLit(Position pos, TypeNode typeNode) {
        ClassLit n = new ClassLit_c(pos, typeNode);
        n = (ClassLit)n.ext(extFactory.extClassLit());
        n = (ClassLit)n.del(delFactory.delClassLit());
        return n;
    }

    public Empty Empty(Position pos) {
        Empty n = new Empty_c(pos);
        n = (Empty)n.ext(extFactory.extEmpty());
        n = (Empty)n.del(delFactory.delEmpty());
        return n;
    }

    public Eval Eval(Position pos, Expr expr) {
        Eval n = new Eval_c(pos, expr);
        n = (Eval)n.ext(extFactory.extEval());
        n = (Eval)n.del(delFactory.delEval());
        return n;
    }
    
    public Import Import(Position pos, Import.Kind kind, QName name) {
        Import n = new Import_c(pos, kind, name, this);
        n = (Import)n.ext(extFactory.extImport());
        n = (Import)n.del(delFactory.delImport());
        return n;
    }

    public Labeled Labeled(Position pos, Id label, Stmt body) {
        Labeled n = new Labeled_c(pos, label, body);
        n = (Labeled)n.ext(extFactory.extLabeled());
        n = (Labeled)n.del(delFactory.delLabeled());
        return n;
    }

    public LocalClassDecl LocalClassDecl(Position pos, ClassDecl decl) {
        LocalClassDecl n = new LocalClassDecl_c(pos, decl);
        n = (LocalClassDecl)n.ext(extFactory.extLocalClassDecl());
        n = (LocalClassDecl)n.del(delFactory.delLocalClassDecl());
        return n;
    }
    
    public NewArray NewArray(Position pos, TypeNode base, List<Expr> dims, int addDims, ArrayInit init) {
        NewArray n = new NewArray_c(pos, base, CollectionUtil.nonNullList(dims), addDims, init);
        n = (NewArray)n.ext(extFactory.extNewArray());
        n = (NewArray)n.del(delFactory.delNewArray());
        return n;
    }
    
    public NodeList NodeList(Position pos, NodeFactory nf, List<Node> nodes) {
        NodeList n = new NodeList_c(pos, nf, nodes);
        n = (NodeList) n.ext(extFactory.extNodeList());
        n = (NodeList) n.del(delFactory.delNodeList());
        return n;
    }

    public NullLit NullLit(Position pos) {
        NullLit n = new NullLit_c(pos);
        n = (NullLit)n.ext(extFactory.extNullLit());
        n = (NullLit)n.del(delFactory.delNullLit());
        return n;
    }

    public SourceCollection SourceCollection(Position pos, List<SourceFile> sources) {
        SourceCollection n = new SourceCollection_c(pos, CollectionUtil.nonNullList(sources));
        n = (SourceCollection)n.ext(extFactory.extSourceCollection());
        n = (SourceCollection)n.del(delFactory.delSourceCollection());
        return n;
    }

    public Switch Switch(Position pos, Expr expr, List<SwitchElement> elements) {
        Switch n = new Switch_c(pos, expr, CollectionUtil.nonNullList(elements));
        n = (Switch)n.ext(extFactory.extSwitch());
        n = (Switch)n.del(delFactory.delSwitch());
        return n;
    }

    public Throw Throw(Position pos, Expr expr) {
        Throw n = new Throw_c(pos, expr);
        n = (Throw)n.ext(extFactory.extThrow());
        n = (Throw)n.del(delFactory.delThrow());
        return n;
    }

    public Try Try(Position pos, Block tryBlock, List<Catch> catchBlocks, Block finallyBlock) {
        Try n = new Try_c(pos, tryBlock, CollectionUtil.nonNullList(catchBlocks), finallyBlock);
        n = (Try)n.ext(extFactory.extTry());
        n = (Try)n.del(delFactory.delTry());
        return n;
    }

    public ArrayTypeNode ArrayTypeNode(Position pos, TypeNode base) {
        ArrayTypeNode n = new ArrayTypeNode_c(pos, base);
        n = (ArrayTypeNode)n.ext(extFactory.extArrayTypeNode());
        n = (ArrayTypeNode)n.del(delFactory.delArrayTypeNode());
        return n;
    }

    public PackageNode PackageNode(Position pos, Ref<? extends Package> p) {
        PackageNode n = new PackageNode_c(pos, p);
        n = (PackageNode)n.ext(extFactory.extPackageNode());
        n = (PackageNode)n.del(delFactory.delPackageNode());
        return n;
    }
}
