/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.frontend.ExtensionInfo;
import polyglot.types.*;
import polyglot.util.*;
import x10.ast.Assign.Operator;
import x10.types.Flags;
import x10.types.Name;
import x10.types.Package;
import x10.types.ParameterType;
import x10.types.QName;
import x10.types.Ref;
import x10.types.Type;
import x10.types.TypeObject;
import x10.types.checker.Converter;

/**
 * A <code>NodeFactory</code> constructs AST nodes.  All node construction
 * should go through this factory or by done with the <code>copy()</code>
 * method of <code>Node</code>.
 */
public class NodeFactory_c extends AbstractNodeFactory_c
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

    public Assign Assign(Position pos, Expr left, Assign.Operator op, Expr right) {
        if (left instanceof Call) {
            Call c = (Call) left;
            return SettableAssign(pos, (Expr) c.target(), c.arguments(), op, right);
        }
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

    public Synchronized Synchronized(Position pos, Expr expr, Block body) {
        Synchronized n = new Synchronized_c(pos, expr, body);
        n = (Synchronized)n.ext(extFactory.extSynchronized());
        n = (Synchronized)n.del(delFactory.delSynchronized());
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


    public NodeFactory_c(ExtensionInfo extInfo) {
        this(extInfo, new X10ExtFactory_c(), new X10DelFactory_c());
    }
    
    public ExtensionInfo extensionInfo() { return extInfo; }

    public Disamb disamb() {
        return new X10Disamb_c();
    }
    
     public Expr AmbHereThis(Position pos) {
            Expr n = new AmbHereThis_c(pos);
            n = (Expr)n.ext(extFactory().extAmbExpr());
            n = (Expr)n.del(delFactory().delAmbExpr());
            return n;
        }
    
    public Initializer Initializer(Position pos, FlagsNode flags, Block body) {
        Initializer n = new X10Initializer_c(pos, flags, body);
        n = (Initializer)n.ext(extFactory().extInitializer());
        n = (Initializer)n.del(delFactory().delInitializer());
        return n;
    }
    
        public LocalAssign LocalAssign(Position pos, Local left, Assign.Operator op, Expr right) {
            LocalAssign n = new X10LocalAssign_c(this, pos, left, op, right);
            n = (LocalAssign)n.ext(extFactory().extLocalAssign());
            n = (LocalAssign)n.del(delFactory().delLocalAssign());
            return n;
        }

        public FieldAssign FieldAssign(Position pos, Receiver target, Id field, Assign.Operator op, Expr right) {
            FieldAssign n = new X10FieldAssign_c(this, pos, target, field, op, right);
            n = (FieldAssign)n.ext(extFactory().extFieldAssign());
            n = (FieldAssign)n.del(delFactory().delFieldAssign());
            return n;
        }

    public LocalTypeDef LocalTypeDef(Position pos, TypeDecl typeDefDeclaration) {
        LocalTypeDef_c n = new LocalTypeDef_c(pos, typeDefDeclaration);
        n = (LocalTypeDef_c) n.ext(extFactory().extStmt());
        n = (LocalTypeDef_c) n.del(delFactory().delStmt());
        return n;
    }

    
    public AmbExpr AmbExpr(Position pos, Id name) {
        AmbExpr n = new X10AmbExpr_c(pos, name);
        n = (AmbExpr)n.ext(extFactory().extAmbExpr());
        n = (AmbExpr)n.del(delFactory().delAmbExpr());
        return n;
    }

    public X10AmbTypeNode AmbTypeNode(Position pos, Prefix p, Id name) {
        X10AmbTypeNode_c n = new X10AmbTypeNode_c(pos, p, name);
        n = (X10AmbTypeNode_c) n.ext(extFactory().extAmbTypeNode());
        n = (X10AmbTypeNode_c) n.del(delFactory().delAmbTypeNode());
        return n;
    }

    public AmbReceiver AmbReceiver(Position pos, Prefix prefix, Id name) {
        AmbReceiver n = new X10AmbReceiver_c(pos, prefix, name);
        n = (AmbReceiver)n.ext(extFactory().extAmbReceiver());
        n = (AmbReceiver)n.del(delFactory().delAmbReceiver());
        return n;
    }

    public X10AmbQualifierNode AmbQualifierNode(Position pos, Prefix prefix, Id name) {
        X10AmbQualifierNode_c n = new X10AmbQualifierNode_c(pos, prefix, name);
        n = (X10AmbQualifierNode_c) n.ext(extFactory().extAmbQualifierNode());
        n = (X10AmbQualifierNode_c) n.del(delFactory().delAmbQualifierNode());
        return n;
    }

    public UnknownTypeNode UnknownTypeNode(Position pos) {
        UnknownTypeNode_c n = new UnknownTypeNode_c(pos);
        n = (UnknownTypeNode_c)n.ext(extFactory().extTypeNode());
        n = (UnknownTypeNode_c)n.del(delFactory().delTypeNode());
        return n;
    }
    public TypeNode HasType(TypeNode tn) {
        HasTypeNode_c n = new HasTypeNode_c(tn);
        n = (HasTypeNode_c)n.ext(extFactory().extTypeNode());
        n = (HasTypeNode_c)n.del(delFactory().delTypeNode());
        return n;
    }

    public Return X10Return(Position pos, Expr expr, boolean implicit) {
        Return n = new X10Return_c(pos, expr, implicit);
        n = (Return)n.ext(extFactory().extReturn());
        n = (Return)n.del(delFactory().delReturn());
        return n;
    }

    
    public Return Return(Position pos, Expr expr) {
        return X10Return(pos, expr, false);
    }

    public TypeParamNode TypeParamNode(Position pos, Id name) {
        return TypeParamNode(pos, name, ParameterType.Variance.INVARIANT);
    }
    
    public TypeParamNode TypeParamNode(Position pos, Id name, ParameterType.Variance variance) {
        TypeParamNode_c n = new TypeParamNode_c(pos, name, variance);
        n = (TypeParamNode_c) n.ext(extFactory().extNode());
        n = (TypeParamNode_c) n.del(delFactory().delNode());
        return n;
    }
    
    public Expr SubtypeTest(Position pos, TypeNode sub, TypeNode sup, boolean equals) {
        SubtypeTest n = new SubtypeTest_c(pos, sub, sup, equals);
        n = (SubtypeTest) n.ext(extFactory().extExpr());
        n = (SubtypeTest) n.del(delFactory().delExpr());
        return n;
    }
    
    public Expr Contains(Position pos, Expr item, Expr collection) {
        Contains n = new Contains_c(pos, item, collection);
        n = (Contains) n.ext(extFactory().extExpr());
        n = (Contains) n.del(delFactory().delExpr());
        return n;
    }
    
    public X10MLSourceFile X10MLSourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls) {
        X10MLSourceFile n = new X10MLSourceFile_c(position, packageName, CollectionUtil.nonNullList(imports), CollectionUtil.nonNullList(decls));
        n = (X10MLSourceFile)n.ext(extFactory().extSourceFile());
        n = (X10MLSourceFile)n.del(delFactory().delSourceFile());
        return n;
    }
    
    
    public SourceFile SourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls) {
         SourceFile n = new X10SourceFile_c(position, packageName, CollectionUtil.nonNullList(imports), CollectionUtil.nonNullList(decls));
         n = (SourceFile)n.ext(extFactory().extSourceFile());
         n = (SourceFile)n.del(delFactory().delSourceFile());
         return n;
    }
    
    public AmbMacroTypeNode AmbMacroTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args) {
        AmbMacroTypeNode n = new AmbMacroTypeNode_c(pos, prefix, name, typeArgs, args);
        n = (AmbMacroTypeNode)n.ext(extFactory().extTypeNode());
        n = (AmbMacroTypeNode)n.del(delFactory().delTypeNode());
        return n;
    }
    public TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr dep) {
        if (dep == null) {
            return AmbMacroTypeNode(pos, prefix, name, typeArgs, args);
        }
        AmbDepTypeNode n = new AmbDepTypeNode_c(pos, AmbMacroTypeNode(pos, prefix, name, typeArgs, args), dep);
        n = (AmbDepTypeNode)n.ext(extFactory().extTypeNode());
        n = (AmbDepTypeNode)n.del(delFactory().delTypeNode());
        return n;
    }
    public TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, DepParameterExpr dep) {
        return AmbDepTypeNode(pos, prefix, name, Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList(), dep);
    }

    public Instanceof Instanceof(Position pos, Expr expr, TypeNode type) {
        Instanceof n = new X10Instanceof_c(pos, expr, type);
        n = (Instanceof) n.ext(extFactory().extInstanceof());
        n = (Instanceof) n.del(delFactory().delInstanceof());
        return n;
    }

    private Block asBlock(Stmt statement) {
        if (statement == null || statement instanceof Block)
            return (Block)statement;
        List<Stmt> l = new ArrayList<Stmt>();
        l.add(statement);
        return Block(statement.position(), l);
    }

    // Wrap the body of the async in a Block so as to ease further code transforamtions.
    public Async Async(Position pos,  List<Expr> clocks, Stmt body) {
        Async a = new Async_c(pos,  clocks, asBlock(body));
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        a = (Async) a.ext(ext_fac.extAsyncImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        a = (Async) a.del(del_fac.delAsyncImpl());
        return a;
    }
    public Async Async(Position pos,  Stmt body, boolean clocked) {
        Async a = new Async_c(pos,  asBlock(body), clocked);
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        a = (Async) a.ext(ext_fac.extAsyncImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        a = (Async) a.del(del_fac.delAsyncImpl());
        return a;
    }
    // Wrap the body of the async in a Block so as to ease further code transforamtions.
    public AtStmt AtStmt(Position pos, Expr place, Stmt body) {
        AtStmt a = new AtStmt_c(pos, place, asBlock(body));
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        a = (AtStmt) a.ext(ext_fac.extAsyncImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        a = (AtStmt) a.del(del_fac.delAsyncImpl());
        return a;
    }

    // Wrap the body of an atomic in a block to facilitate code transformation.
    public Atomic Atomic(Position pos, Expr place, Stmt body) {
        Atomic a = new Atomic_c(pos, place, asBlock(body));
        a = (Atomic) a.ext(extFactory().extExpr());
        a = (Atomic) a.del(delFactory().delExpr());
        return a;
    }
    
    public Future Future(Position pos, Expr place, TypeNode returnType, Block body) {
        return Future(pos, place, returnType, null, body);
    }
    
    public Future Future(Position pos, Expr place, TypeNode returnType, TypeNode offerType, Block body) {
        Future f = new Future_c(this, pos, place, returnType, offerType, body);
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        f = (Future) f.ext(ext_fac.extFutureImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        f = (Future) f.del(del_fac.delFutureImpl());
        return f;
    }

    public AtExpr AtExpr(Position pos, Expr place, TypeNode returnType, Block body) {
        return AtExpr(pos, place, returnType, null, body);
    }
    public AtExpr AtExpr(Position pos, Expr place, TypeNode returnType, TypeNode offerType, Block body) {
        AtExpr f = new AtExpr_c(this, pos, place, returnType,offerType, body);
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        f = (AtExpr) f.ext(ext_fac.extExpr()); // FIXME
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        f = (AtExpr) f.del(del_fac.delFutureImpl()); // FIXME
        return f;
    }

    public Here Here(Position pos) {
        Here f = new Here_c(pos);
        f = (Here) f.ext(extFactory().extStmt());
        return (Here) f.del(delFactory().delStmt());
    }

    // Wrap the body of a When in a conditional to facilitate code transformations
    public When When(Position pos, Expr expr, Stmt statement) {
        When w = new When_c(pos, expr, asBlock(statement));
        w = (When) w.ext(extFactory().extStmt());
        return (When) w.del(delFactory().delStmt());
    }

    public Next Next(Position pos) {
        Next n = new Next_c(pos);
        n = (Next) n.ext(extFactory().extStmt());
        return (Next) n.del(delFactory().delStmt());
    }
    public Resume Resume(Position pos) {
        Resume n = new Resume_c(pos);
        n = (Resume) n.ext(extFactory().extStmt());
        return (Resume) n.del(delFactory().delStmt());
    }
    
    public Offer Offer(Position pos, Expr e) {
        Offer n = new Offer_c(pos,e);
        n = (Offer) n.ext(extFactory().extStmt());
        return (Offer) n.del(delFactory().delStmt());
    }

    public FinishExpr FinishExpr(Position pos, Expr e, Stmt s) {
        FinishExpr n = new FinishExpr_c(pos, e, s);
        n = (FinishExpr) n.ext(extFactory().extStmt());
        return (FinishExpr) n.del(delFactory().delStmt());
    }

    public ClassBody ClassBody(Position pos, List<ClassMember> members) {
        ClassBody n = new X10ClassBody_c(pos,members);
        n = (ClassBody)n.ext(extFactory().extClassBody());
        n = (ClassBody)n.del(delFactory().delClassBody());
        return n;
    }
    
    
    public ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        return X10ClassDecl(pos, flags, name, Collections.<TypeParamNode>emptyList(), Collections.<PropertyDecl>emptyList(), null, superClass, interfaces, body);
    }

    public X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<PropertyDecl> properties, DepParameterExpr ci,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body) {
        return (X10ClassDecl) ClassDecl(pos, flags, name, typeParameters, properties, superClass, interfaces, body, ci);
    }

    private ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<PropertyDecl> properties,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body, DepParameterExpr tci) {
        boolean isInterface = flags.flags().isInterface();
        if (flags.flags().isInterface()) {
            body = PropertyDecl_c.addAbstractGetters(properties, body, this);
        } else {
              body = PropertyDecl_c.addPropertyGetters(properties, body, this);
        }
      
        
        ClassDecl n = new X10ClassDecl_c(pos, flags, name, typeParameters, properties, tci, superClass, interfaces, body);
        n = (ClassDecl)n.ext(extFactory().extClassDecl());
        n = (ClassDecl)n.del(delFactory().delClassDecl());
        return n;
    }
    public X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name, TypeNode superClass, List<TypeNode> interfaces, ClassBody body, DepParameterExpr tci) {
        return (X10ClassDecl) ClassDecl(pos, flags, name, Collections.<TypeParamNode>emptyList(), Collections.<PropertyDecl>emptyList(), superClass, interfaces, body, tci);
    }

    public Call X10Call(Position pos, Receiver target, Id name, List<TypeNode> typeArguments, List<Expr> args) {
        Call n = new X10Call_c(pos, target, name, typeArguments, args);
        n = (Call) n.ext(extFactory().extExpr());
        return (Call) n.del(delFactory().delExpr());
    }
    
    public Call Call(Position pos, Receiver target, Id name, List<Expr> args) {
        return X10Call(pos, target, name, Collections.<TypeNode>emptyList(), args);
    }
    
    public Expr ConstantDistMaker(Position pos, Expr e1, Expr e2) {
        Receiver x10LangDistributionFactory = ReceiverFromQualifiedName(pos, QName.make("x10.array.Dist"));
        List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        l.add(e1);
        l.add(e2);
        ConstantDistMaker n = new ConstantDistMaker_c(pos,
                x10LangDistributionFactory,
                Id(pos, Name.make("makeConstant")), l);
        n = (ConstantDistMaker) n.ext(extFactory().extExpr());
        return (ConstantDistMaker) n.del(delFactory().delExpr());
    }

    public X10New X10New(Position pos, boolean newOmitted, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
        X10New n = new X10New_c(pos, newOmitted, qualifier, objectType, typeArguments, arguments, body);
        n = (x10.ast.X10New) n.ext(extFactory().extNew());
        n = (x10.ast.X10New) n.del(delFactory().delNew());
        return n;
    }

    public X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
        return X10New(pos, false, qualifier, objectType, typeArguments, arguments, body);
    }

    public X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments) {
        return X10New(pos, qualifier, objectType, typeArguments, arguments, null);
    }

    public X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body) {
        return X10New(pos, null, objectType, typeArguments, arguments, body);
    }

    public X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments) {
        return X10New(pos, null, objectType, typeArguments, arguments, null);
    }

    public New New(Position pos, Expr qualifier, TypeNode objectType, List<Expr> arguments, ClassBody body) {
        return X10New(pos, qualifier, objectType, Collections.<TypeNode>emptyList(), arguments, body);
    }
    
//   Wrap the body in a block to facilitate code transformations
    public AtEach AtEach(Position pos, Formal formal, Expr domain,
                         List<Expr> clocks, Stmt body)
    {
        AtEach n = new AtEach_c(pos, formal, domain, clocks, asBlock(body));
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        n = (AtEach) n.ext(ext_fac.extAtEachImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        n = (AtEach) n.del(del_fac.delAtEachImpl());
        return n;
    }
    public AtEach AtEach(Position pos, Formal formal, Expr domain,
             Stmt body)
    {
        AtEach n = new AtEach_c(pos, formal, domain, asBlock(body));
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        n = (AtEach) n.ext(ext_fac.extAtEachImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        n = (AtEach) n.del(del_fac.delAtEachImpl());
        return n;
    }

    public For For(Position pos, List<ForInit> inits, Expr cond, List<ForUpdate> iters, Stmt body) {
        For n = new For_c(pos, inits, cond, iters, asBlock(body));
        n = (For)n.ext(extFactory().extFor());
        n = (For)n.del(delFactory().delFor());
        return n;
    }

//   Wrap the body in a block to facilitate code transformations
    public X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body)
    {
        X10Loop n = new ForLoop_c(pos, formal, domain, asBlock(body));
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        n = (X10Loop) n.ext(ext_fac.extForLoopImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        n = (X10Loop) n.del(del_fac.delForLoopImpl());
        return n;
    }



    // Wrap the body in a block to facilitate code transformations
    public Finish Finish(Position pos, Stmt body, boolean clocked) {
        Finish n = new Finish_c(pos, asBlock(body), clocked);
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        n = (Finish) n.ext(ext_fac.extFinishImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        n = (Finish) n.del(del_fac.delFinishImpl());
        return n;
    }

    public DepParameterExpr DepParameterExpr(Position pos, List<Expr> e) {
        DepParameterExpr n = new DepParameterExpr_c(pos, e);
        n = (DepParameterExpr) n.ext(extFactory().extStmt());
        return (DepParameterExpr) n.del(delFactory().delStmt());
    }
    public DepParameterExpr DepParameterExpr(Position pos, List<Formal> formals, List<Expr> e) {
        DepParameterExpr n = new DepParameterExpr_c(pos, formals, e);
        n = (DepParameterExpr) n.ext(extFactory().extStmt());
        return (DepParameterExpr) n.del(delFactory().delStmt());
    }
    
    public x10.ast.AmbAssign AmbAssign(Position pos, Expr left, Operator op, Expr right) {
        AmbAssign n = new X10AmbAssign_c(this, pos, left, op, right);
        n = (AmbAssign)n.ext(extFactory().extAmbAssign());
        n = (AmbAssign)n.del(delFactory().delAmbAssign());
        return n;
    }
    public SettableAssign SettableAssign(Position pos, Expr array, List<Expr> index, Operator op, Expr right) {
        SettableAssign n = new SettableAssign_c(this, pos, array, index, op, right);
        n = (SettableAssign)n.ext(extFactory().extAssign());
        n = (SettableAssign)n.del(delFactory().delAssign());
        return n;
    }

    public Binary Binary(Position pos, Expr left, Binary.Operator op,
                         Expr right)
    {
        Binary n = new X10Binary_c(pos, left, op, right);
        n = (Binary) n.ext(extFactory().extBinary());
        n = (Binary) n.del(delFactory().delBinary());
        return n;
    }

    public Unary Unary(Position pos, Unary.Operator op, Expr expr) {
        boolean incOp = (op == Unary.POST_INC || op == Unary.PRE_INC ||
                         op == Unary.POST_DEC || op == Unary.PRE_DEC);
        Unary n = new X10Unary_c(pos, op, expr);
        n = (Unary) n.ext(extFactory().extUnary());
        n = (Unary) n.del(delFactory().delUnary());
        return n;
    }

    public Tuple Tuple(Position pos, List<Expr> a) {
        return Tuple(pos,null, a);

    }
    public Tuple Tuple(Position pos, TypeNode indexType, List<Expr> a) {
        //Report.report(1, "NodeFactory_c making tuple " + p + " " + r + " " + a);
        Tuple n = new Tuple_c(pos, a, indexType);
        n = (Tuple) n.ext(extFactory().extCall());
        n = (Tuple) n.del(delFactory().delCall());
        return n;
    }

    public X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Type type) {
        X10CanonicalTypeNode tn = (X10CanonicalTypeNode) CanonicalTypeNode(pos, type);
        return tn;
    }

    public Switch Switch(Position pos, Expr expr, List<SwitchElement> elements) {
        Switch n = new Switch_c(pos, expr, CollectionUtil.nonNullList(elements));
        n = (Switch)n.ext(extFactory.extSwitch());
        n = (Switch)n.del(delFactory.delSwitch());
        return n;
    }
    
    public CanonicalTypeNode CanonicalTypeNode(Position pos, Ref<? extends Type> type) {
        CanonicalTypeNode n = new X10CanonicalTypeNode_c(pos, type);
        n = (CanonicalTypeNode)n.ext(extFactory().extCanonicalTypeNode());
        n = (CanonicalTypeNode)n.del(delFactory().delCanonicalTypeNode());
        return n;
    }
    
    public X10Formal X10Formal(Position pos, FlagsNode flags, TypeNode type, Id name,
                         List<Formal> vars, boolean unnamed)
    {
        X10Formal n = new X10Formal_c(pos, flags, type, name, vars, unnamed);
        n = (X10Formal) n.ext(extFactory().extFormal());
        n = (X10Formal) n.del(delFactory().delFormal());
        return n;
    }

    public Formal Formal(Position pos, FlagsNode flags, TypeNode type, Id name)
    {
        return X10Formal(pos, flags, type, name, null, false);
    }

    public ParExpr ParExpr(Position pos, Expr expr) {
        ParExpr n = new ParExpr_c(pos, expr);
        n = (ParExpr) n.ext(extFactory().extExpr());
        return (ParExpr) n.del(delFactory().delExpr());
    }

    public Field Field(Position pos, Receiver target, Id name) {
        Field n = new X10Field_c(pos, target, name);
        n = (Field) n.ext(extFactory().extField());
        n = (Field) n.del(delFactory().delField());
        return n;
    }

    public FieldDecl FieldDecl(Position pos, FlagsNode flags, TypeNode type,
                               Id name, Expr init)
    {
        FieldDecl n = new X10FieldDecl_c(this, pos, flags, type, name, init);
        n = (FieldDecl) n.ext(extFactory().extFieldDecl());
        n = (FieldDecl) n.del(delFactory().delFieldDecl());
        return n;
    }

    
    public X10Cast X10Cast(Position pos, TypeNode castType, Expr expr) {
        return X10Cast(pos, castType, expr, Converter.ConversionType.UNKNOWN_CONVERSION);
    }
    public X10Cast X10Cast(Position pos, TypeNode castType, Expr expr, Converter.ConversionType conversionType) {
        X10Cast n = new X10Cast_c(pos, castType, expr, conversionType);
        n = (X10Cast)n.ext(extFactory().extCast());
        n = (X10Cast)n.del(delFactory().delCast());
        return n;
    }

    
    public Cast Cast(Position pos, TypeNode castType, Expr expr) {
        return X10Cast(pos, castType, expr, Converter.ConversionType.UNKNOWN_CONVERSION);
    }

    // 
    public X10MethodDecl MethodDecl(Position pos, FlagsNode flags, TypeNode returnType,
            Id name,
            List<Formal> formals,  Block body)
    {
        return X10MethodDecl(pos, flags, returnType, name, Collections.<TypeParamNode>emptyList(), formals, null,  null, body);
    }

    public X10MethodDecl X10MethodDecl(Position pos, FlagsNode flags, TypeNode returnType, Id name, List<TypeParamNode> typeParams, List<Formal> formals,
            DepParameterExpr guard,  TypeNode offerType, Block body) {
        X10MethodDecl n = new X10MethodDecl_c(this, pos, flags, returnType, name, typeParams,
                formals, guard,  offerType, body);
        n = (X10MethodDecl)n.ext(extFactory().extMethodDecl());
        n = (X10MethodDecl)n.del(delFactory().delMethodDecl());
        return n;
    }
    
    public LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type,
                               Id name, Expr init)
    {
        LocalDecl n = new X10LocalDecl_c(this, pos, flags, type, name, init);
        n = (LocalDecl)n.ext(extFactory().extLocalDecl());
        n = (LocalDecl)n.del(delFactory().delLocalDecl());
        return n;
    }
    
    //
    public ConstructorDecl ConstructorDecl(Position pos, FlagsNode flags, Id name,
            List<Formal> formals, 
            Block body) {
        return X10ConstructorDecl(pos, flags, name, null, Collections.<TypeParamNode>emptyList(), formals, null,  null, body);
    }

    public ConstructorDecl X10ConstructorDecl(Position pos, FlagsNode flags,
            Id name, TypeNode returnType,
            List<TypeParamNode> typeParams, List<Formal> formals,
            DepParameterExpr guard, TypeNode offerType, Block body)
    {
        ConstructorDecl n =
            new X10ConstructorDecl_c(pos, flags,
                    name, returnType,
                    typeParams, formals,
                    guard,  offerType, body);
        n = (ConstructorDecl)n.ext(extFactory().extConstructorDecl());
        n = (ConstructorDecl)n.del(delFactory().delConstructorDecl());
        return n;
    }
    public PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name) {
        PropertyDecl n = new PropertyDecl_c(pos, flags, type, name, this);
        n = (PropertyDecl)n.ext(extFactory().extFieldDecl());
        n = (PropertyDecl)n.del(delFactory().delFieldDecl());
        return n;
    }
    public PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init) {
        PropertyDecl n = new PropertyDecl_c(pos, flags, type, name, init, this);
        n = (PropertyDecl)n.ext(extFactory().extFieldDecl());
        n = (PropertyDecl)n.del(delFactory().delFieldDecl());
        return n;
    }
    public final Special Self(Position pos) {
        return Special(pos, X10Special.SELF, null);
    }
    public Special Special(Position pos, Special.Kind kind, TypeNode outer) {
        Special n = new X10Special_c(pos, kind, outer);
        n = (Special)n.ext(extFactory().extSpecial());
        n = (Special)n.del(delFactory().delSpecial());
        return n;
    }

    public Local Local(Position pos, Id name) {
        Local n = new X10Local_c(pos, name);
        n = (Local)n.ext(extFactory().extLocal());
        n = (Local)n.del(delFactory().delLocal());
        return n;
    }
    public BooleanLit BooleanLit(Position pos, boolean value) {
        BooleanLit n = new X10BooleanLit_c(pos, value);
        n = (BooleanLit)n.ext(extFactory().extBooleanLit());
        n = (BooleanLit)n.del(delFactory().delBooleanLit());
        return n;
    }
    public StmtExpr StmtExpr(Position pos, List<Stmt> statements, Expr result) {
        StmtExpr n = new StmtExpr_c(pos, statements, result);
        n = (StmtExpr)n.ext(extFactory().extBlock());
        n = (StmtExpr)n.del(delFactory().delBlock());
        return n;
    }
    public StmtSeq StmtSeq(Position pos, List<Stmt> statements) {
        StmtSeq n = new StmtSeq_c(this, pos, statements);
        n = (StmtSeq)n.ext(extFactory().extBlock());
        n = (StmtSeq)n.del(delFactory().delBlock());
        return n;
    }
    // Place the consequent and the alternative in blocks to ease
    // further rewrites of the AST.
    public If If(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
        If n = new X10If_c(pos, cond, asBlock(consequent), asBlock(alternative));
        n = (If)n.ext(extFactory().extIf());
        n = (If)n.del(delFactory().delIf());
        return n;
    }
    public Expr RegionMaker(Position pos, Expr e1, Expr e2) {
        List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        l.add(e1);
        l.add(e2);

        Call n = new RegionMaker_c(pos, TypeNodeFromQualifiedName(pos, QName.make("x10.array.Region")), Id(pos, "makeRectangular"), l);
        n = (Call) n.ext(extFactory().extExpr());
        n = (Call) n.del(delFactory().delExpr());
        return n;
    }
    public Do Do(Position pos, Stmt body, Expr cond) {
        Do n = new X10Do_c(pos, body, cond);
        n = (Do)n.ext(extFactory().extDo());
        n = (Do)n.del(delFactory().delDo());
        return n;
    }
    public While While(Position pos, Expr cond, Stmt body) {
        While n = new X10While_c(pos, cond, body);
        n = (While)n.ext(extFactory().extWhile());
        n = (While)n.del(delFactory().delWhile());
        return n;
    }
    public IntLit IntLit(Position pos, IntLit.Kind kind, long value) {
        IntLit n = new X10IntLit_c(pos, kind, value);
        n = (IntLit)n.ext(extFactory().extIntLit());
        n = (IntLit)n.del(delFactory().delIntLit());
        return n;
    }

    public StringLit StringLit(Position pos, String value) {
        StringLit n = new X10StringLit_c(pos, value);
        n = (StringLit)n.ext(extFactory().extStringLit());
        n = (StringLit)n.del(delFactory().delStringLit());
        return n;
    }
    public FloatLit FloatLit(Position pos, FloatLit.Kind kind, double value) {
        FloatLit n = new X10FloatLit_c(pos, kind, value);
        n = (FloatLit)n.ext(extFactory().extFloatLit());
        n = (FloatLit)n.del(delFactory().delFloatLit());
        return n;
    }
    public CharLit CharLit(Position pos, char value) {
        CharLit n = new X10CharLit_c(pos, value);
        n = (CharLit)n.ext(extFactory().extCharLit());
        n = (CharLit)n.del(delFactory().delCharLit());
        return n;
    }

    public AssignPropertyCall AssignPropertyCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
        AssignPropertyCall  n = new AssignPropertyCall_c(pos, args);
        n = (AssignPropertyCall) n.ext(extFactory().extExpr());
        n= (AssignPropertyCall) n.del(delFactory().delExpr());
        return n;
    }
    public Conditional Conditional(Position pos, Expr cond, Expr consequent, Expr alternative) {
        Conditional n = new X10Conditional_c(pos, cond, consequent, alternative);
        n = (Conditional)n.ext(extFactory().extConditional());
        n = (Conditional)n.del(delFactory().delConditional());
        return n;
    }

    public ConstructorCall X10ThisCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
        return X10ConstructorCall(pos, ConstructorCall.THIS, outer, typeArgs, args);
    }

    public ConstructorCall X10ThisCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
        return X10ConstructorCall(pos, ConstructorCall.THIS, null, typeArgs, args);
    }

    public ConstructorCall X10SuperCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
        return X10ConstructorCall(pos, ConstructorCall.SUPER, outer, typeArgs, args);
    }

    public ConstructorCall X10SuperCall(Position pos, List<TypeNode> typeArgs, List<Expr> args) {
        return X10ConstructorCall(pos, ConstructorCall.SUPER, null, typeArgs, args);
    }

    public ConstructorCall X10ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<TypeNode> typeArgs, List<Expr> args) {
        ConstructorCall n = new X10ConstructorCall_c(pos, kind, outer, CollectionUtil.nonNullList(typeArgs), CollectionUtil.nonNullList(args));
        n = (ConstructorCall)n.ext(extFactory().extConstructorCall());
        n = (ConstructorCall)n.del(delFactory().delConstructorCall());
        return n;
    }
    
    public ConstructorCall ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<Expr> args) {
        return X10ConstructorCall(pos, kind, outer, Collections.<TypeNode>emptyList(), args);
    }

    public Closure Closure(Position pos, List<Formal> formals, 
            DepParameterExpr guard, TypeNode returnType,   Block body) {
        return Closure(pos, formals, guard, returnType,  null, body);
    }
    public Closure Closure(Position pos, List<Formal> formals, 
            DepParameterExpr guard, TypeNode returnType,  TypeNode offerType, Block body) {
        Closure n = new Closure_c(this, pos, formals, returnType, guard,  offerType, body);
        X10ExtFactory_c ext_fac = (X10ExtFactory_c) extFactory();
        n = (Closure) n.ext(ext_fac.extClosureImpl());
        X10DelFactory_c del_fac = (X10DelFactory_c) delFactory();
        n = (Closure) n.del(del_fac.delClosureImpl());
        return n;
    }

    public Closure Closure(Closure c, Position pos) {
       return Closure(pos,  c.formals(), c.guard(), c.returnType(), 
               c.body());
    }
    public ClosureCall ClosureCall(Position pos, Expr closure, /*List<TypeNode> typeArgs,*/ List<Expr> args) {
        ClosureCall n = new ClosureCall_c(pos, closure,  args);
        n = (ClosureCall) n.ext(extFactory().extExpr());
        n = (ClosureCall) n.del(delFactory().delExpr());
        return n;
    }
    public ClosureCall ClosureCall(Position pos, Expr closure, List<TypeNode> typeArgs, List<Expr> args) {
        assert typeArgs==null || typeArgs.size()==0 : "Closures do not have type args.";
        return ClosureCall(pos, closure, args);
    
    }

    public AnnotationNode AnnotationNode(Position pos, TypeNode tn) {
        AnnotationNode n = new AnnotationNode_c(pos, tn);
        n = (AnnotationNode) n.ext(extFactory().extNode());
        n = (AnnotationNode) n.del(delFactory().delNode());
        return n;
    }

    public TypeNode FunctionTypeNode(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, TypeNode returnType,
            TypeNode offersType) {
        FunctionTypeNode n = new FunctionTypeNode_c(pos, typeParams, formals, returnType, guard,  offersType);
        n = (FunctionTypeNode) n.ext(extFactory().extTypeNode());
        n = (FunctionTypeNode) n.del(delFactory().delTypeNode());
        return n;
    }

    public TypeDecl TypeDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr guard,
            TypeNode type) {
        TypeDecl n = new TypeDecl_c(pos, flags, name, typeParameters, formals, guard, type);
        n = (TypeDecl) n.ext(extFactory().extNode());
        n = (TypeDecl) n.del(delFactory().delNode());
        return n;
    }


    public static Position compilerGenerated(Node n) {
        return compilerGenerated(n==null ? null : n.position());
    }
    public static Position compilerGenerated(TypeObject n) {
        return compilerGenerated(n==null ? null : n.position());
    }
    public static Position compilerGenerated(Position pos) {
        return pos==null ? Position.COMPILER_GENERATED : pos.startOf().markCompilerGenerated();
    }
}
