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

import java.util.List;

import x10.ExtensionInfo;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.Position;
import x10.ast.*;
import x10.types.ParameterType;
import x10.types.checker.Converter;
import x10cuda.ast.CUDAKernel;

/**
 * A <code>NodeFactory</code> constructs AST nodes.  All node construction
 * should go through this factory or be done with the <code>copy()</code>
 * method of <code>Node</code>.
 */
public interface NodeFactory
{

	/**
	 * Returns the ExtensionInfo object associated with this factory.
	 */
	public ExtensionInfo extensionInfo();
	
    /**
     * Returns a disambiguator for nodes from this factory.
     */
    Disamb disamb();
    
    //////////////////////////////////////////////////////////////////
    // Factory Methods
    //////////////////////////////////////////////////////////////////

    FlagsNode FlagsNode(Position pos, Flags flags);

    Id Id(Position pos, Name id);
    Id Id(Position pos, String id); // for backward compat

    Allocation Allocation(Position pos, TypeNode objType, List<TypeNode> typeArgs);

    AmbExpr AmbExpr(Position pos, Id name);
    Expr ExprFromQualifiedName(Position pos, QName qualifiedName);
    
    // type or expr
    AmbReceiver AmbReceiver(Position pos, Id name);
    AmbReceiver AmbReceiver(Position pos, Prefix prefix, Id name);
    Receiver ReceiverFromQualifiedName(Position pos, QName qualifiedName);
    
    // package or type or expr
    AmbPrefix AmbPrefix(Position pos, Id name);
    AmbPrefix AmbPrefix(Position pos, Prefix prefix, Id name);
    Prefix PrefixFromQualifiedName(Position pos, QName qualifiedName);
    
    AmbTypeNode AmbTypeNode(Position pos, Id name);
    AmbTypeNode AmbTypeNode(Position pos, Prefix qualifier, Id name);
    
    ArrayTypeNode ArrayTypeNode(Position pos, TypeNode base);
    CanonicalTypeNode CanonicalTypeNode(Position pos, Type type);

    ArrayAccess ArrayAccess(Position pos, Expr base, Expr index);

    ArrayInit ArrayInit(Position pos);
    ArrayInit ArrayInit(Position pos, List<Expr> elements);

    Assert Assert(Position pos, Expr cond);
    Assert Assert(Position pos, Expr cond, Expr errorMessage);

    Assign Assign(Position pos, Expr target, Assign.Operator op, Expr source);
    LocalAssign LocalAssign(Position pos, Local target, Assign.Operator op, Expr source);
    FieldAssign FieldAssign(Position pos, Receiver target, Id name, Assign.Operator op, Expr source);
    ArrayAccessAssign ArrayAccessAssign(Position pos, Expr array, Expr index, Assign.Operator op, Expr source);
    AmbAssign AmbAssign(Position pos, Expr target, Assign.Operator op, Expr source);

    Binary Binary(Position pos, Expr left, Binary.Operator op, Expr right);

    Block Block(Position pos);
    Block Block(Position pos, Stmt s1);
    Block Block(Position pos, Stmt s1, Stmt s2);
    Block Block(Position pos, Stmt s1, Stmt s2, Stmt s3);
    Block Block(Position pos, Stmt s1, Stmt s2, Stmt s3, Stmt s4);
    Block Block(Position pos, List<Stmt> statements);

    SwitchBlock SwitchBlock(Position pos, List<Stmt> statements);

    BooleanLit BooleanLit(Position pos, boolean value);

    Branch Break(Position pos);
    Branch Break(Position pos, Id label);

    Branch Continue(Position pos);
    Branch Continue(Position pos, Id label);

    Branch Branch(Position pos, Branch.Kind kind);
    Branch Branch(Position pos, Branch.Kind kind, Id label);

    Call Call(Position pos, Id name);
    Call Call(Position pos, Id name, Expr a1);
    Call Call(Position pos, Id name, Expr a1, Expr a2);
    Call Call(Position pos, Id name, Expr a1, Expr a2, Expr a3);
    Call Call(Position pos, Id name, Expr a1, Expr a2, Expr a3, Expr a4);
    Call Call(Position pos, Id name, List<Expr> args);
    
    Call Call(Position pos, Receiver target, Id name);
    Call Call(Position pos, Receiver target, Id name, Expr a1);
    Call Call(Position pos, Receiver target, Id name, Expr a1, Expr a2);
    Call Call(Position pos, Receiver target, Id name, Expr a1, Expr a2, Expr a3);
    Call Call(Position pos, Receiver target, Id name, Expr a1, Expr a2, Expr a3, Expr a4);
    Call Call(Position pos, Receiver target, Id name, List<Expr> args);

    Case Default(Position pos);
    Case Case(Position pos, Expr expr);
    
    Cast Cast(Position pos, TypeNode type, Expr expr);

    Catch Catch(Position pos, Formal formal, Block body);

    CharLit CharLit(Position pos, char value);

    ClassBody ClassBody(Position pos, List<ClassMember> members);

    ClassDecl ClassDecl(Position pos, FlagsNode flags, Id name,
            TypeNode superClass, List<TypeNode> interfaces, ClassBody body);

    ClassLit ClassLit(Position pos, TypeNode typeNode);

    Conditional Conditional(Position pos, Expr cond, Expr consequent, Expr alternative);

    ConstructorCall ThisCall(Position pos, List<Expr> args);
    ConstructorCall ThisCall(Position pos, Expr outer, List<Expr> args);
    ConstructorCall SuperCall(Position pos, List<Expr> args);
    ConstructorCall SuperCall(Position pos, Expr outer, List<Expr> args);
    ConstructorCall ConstructorCall(Position pos, ConstructorCall.Kind kind, List<Expr> args);
    ConstructorCall ConstructorCall(Position pos, ConstructorCall.Kind kind,
	                            Expr outer, List<Expr> args);

    ConstructorDecl ConstructorDecl(Position pos, FlagsNode flags, Id name,
            List<Formal> formals, 
            Block body);

    FieldDecl FieldDecl(Position pos, FlagsNode flags, TypeNode type, Id name);
    FieldDecl FieldDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init);

    Do Do(Position pos, Stmt body, Expr cond);

    Empty Empty(Position pos);

    Eval Eval(Position pos, Expr expr);

    Field Field(Position pos, Receiver target, Id name);

    FloatLit FloatLit(Position pos, FloatLit.Kind kind, double value);

    For For(Position pos, List<ForInit> inits, Expr cond, List<ForUpdate> iters, Stmt body);

    If If(Position pos, Expr cond, Stmt consequent);
    If If(Position pos, Expr cond, Stmt consequent, Stmt alternative);

    Import Import(Position pos, Import.Kind kind, QName name);

    Initializer Initializer(Position pos, FlagsNode flags, Block body);

    IntLit IntLit(Position pos, IntLit.Kind kind, long value);

    Labeled Labeled(Position pos, Id label, Stmt body);

    Local Local(Position pos, Id name);

    LocalClassDecl LocalClassDecl(Position pos, ClassDecl decl);

    LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type, Id name);
    LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init);
    LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init, List<Id> exploded);

    New New(Position pos, TypeNode type, List<Expr> args);
    New New(Position pos, TypeNode type, List<Expr> args, ClassBody body);

    New New(Position pos, Expr outer, TypeNode objectType, List<Expr> args);
    New New(Position pos, Expr outer, TypeNode objectType, List<Expr> args, ClassBody body);

    NewArray NewArray(Position pos, TypeNode base, List<Expr> dims);
    NewArray NewArray(Position pos, TypeNode base, List<Expr> dims, int addDims);
    NewArray NewArray(Position pos, TypeNode base, int addDims, ArrayInit init);
    NewArray NewArray(Position pos, TypeNode base, List<Expr> dims, int addDims, ArrayInit init);
    
    NodeList NodeList(Position pos, List<Node> nodes);
    NodeList NodeList(Position pos, NodeFactory nf, List<Node> nodes);

    NullLit NullLit(Position pos);

    Return Return(Position pos);
    Return Return(Position pos, Expr expr);

    SourceCollection SourceCollection(Position pos, List<SourceFile> sources);

    SourceFile SourceFile(Position pos, List<TopLevelDecl> decls);
    SourceFile SourceFile(Position pos, List<Import> imports, List<TopLevelDecl> decls);
    SourceFile SourceFile(Position pos, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls);

    Special This(Position pos);
    Special This(Position pos, TypeNode outer);

    Special Super(Position pos);
    Special Super(Position pos, TypeNode outer);
    Special Special(Position pos, Special.Kind kind);
    Special Special(Position pos, Special.Kind kind, TypeNode outer);

    StringLit StringLit(Position pos, String value);

    Switch Switch(Position pos, Expr expr, List<SwitchElement> elements);

    Throw Throw(Position pos, Expr expr);

    Try Try(Position pos, Block tryBlock, List<Catch> catchBlocks);
    Try Try(Position pos, Block tryBlock, List<Catch> catchBlocks, Block finallyBlock);

    PackageNode PackageNode(Position pos, Ref<? extends Package> p);

    Unary Unary(Position pos, Unary.Operator op, Expr expr);
    Unary Unary(Position pos, Expr expr, Unary.Operator op);

    While While(Position pos, Expr cond, Stmt body);

    AtStmt AtStmt(Position pos, Expr place, Stmt body);
    AtStmt AtStmt(Position pos, Expr place, List<Node> captures, Stmt body);
    AtExpr AtExpr(Position pos, Expr place, Block body);
    AtExpr AtExpr(Position pos, Expr place, List<Node> captures, Block body);

    AtHomeStmt AtHomeStmt(Position pos, List<Expr> vars, Stmt body);
    AtHomeStmt AtHomeStmt(Position pos, List<Expr> vars, List<Node> captures, Stmt body);
    AtHomeExpr AtHomeExpr(Position pos, List<Expr> vars, Block body);
    AtHomeExpr AtHomeExpr(Position pos, List<Expr> vars, List<Node> captures, Block body);

    ConstructorCall X10ConstructorCall(Position pos, ConstructorCall.Kind kind, Expr outer, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10ThisCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10ThisCall(Position pos, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10SuperCall(Position pos, Expr outer, List<TypeNode> typeArgs, List<Expr> args);
    ConstructorCall X10SuperCall(Position pos, List<TypeNode> typeArgs, List<Expr> args);

    X10CanonicalTypeNode X10CanonicalTypeNode(Position pos, Type t);
    X10CanonicalTypeNode CanonicalTypeNode(Position position, Ref<? extends Type> type);

    X10Cast X10Cast(Position pos, TypeNode castType, Expr expr);
    X10Cast X10Cast(Position pos, TypeNode castType, Expr expr, Converter.ConversionType conversionType);
    Return X10Return(Position pos, Expr expr, boolean implicit);

    UnknownTypeNode UnknownTypeNode(Position pos);
    TypeParamNode TypeParamNode(Position pos, Id name);
    TypeParamNode TypeParamNode(Position pos, Id name, ParameterType.Variance variance);
    TypeNode FunctionTypeNode(Position pos, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, 
            TypeNode returnType,  TypeNode offersType);
    HasZeroTest HasZeroTest(Position pos, TypeNode sub);
    SubtypeTest SubtypeTest(Position pos, TypeNode sub, TypeNode sup, boolean equals);
    TypeDecl TypeDecl(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr guard, TypeNode type);

    X10Call X10Call(Position pos, Receiver target, Id name, List<TypeNode> typeArgs, List<Expr> args);
    X10Call X10ConversionCall(Position pos, Receiver target, Id name, TypeNode convType, List<TypeNode> typeArgs, List<Expr> args);
    
    X10Instanceof Instanceof(Position pos, Expr expr, TypeNode type);
    Async Async(Position pos, List<Expr> clocks, Stmt body);
    Async Async(Position pos, Stmt body, boolean clocked);
    Atomic Atomic(Position pos, Expr place, Stmt body);
    Here_c Here(Position pos);

    /**
     * Return an immutable representation of a 1-armed When.
     * (Additional arms are added by invoking the add method on the
     * returned When.)
     * @param pos
     * @param expr
     * @param statement
     * @return
     */
    When When(Position pos, Expr expr, Stmt statement);

    Next Next(Position pos);
    Resume Resume(Position pos);

    X10ClassDecl X10ClassDecl(Position pos, FlagsNode flags, Id name,
        List<TypeParamNode> typeParameters,
            List<PropertyDecl> properties,
              DepParameterExpr ci, TypeNode superClass,
              List<TypeNode> interfaces, ClassBody body);

    X10Loop ForLoop(Position pos, Formal formal, Expr domain, Stmt body);
    X10Loop AtEach(Position pos, Formal formal, Expr domain, List<Expr> clocks,
                   Stmt body);
    X10Loop AtEach(Position pos, Formal formal, Expr domain, Stmt body);
    Finish Finish(Position pos, Stmt body, boolean clocked);

    DepParameterExpr DepParameterExpr(Position pos, List<Expr> cond);
    DepParameterExpr DepParameterExpr(Position pos, List<Formal> formals, List<Expr> cond);

    X10MethodDecl MethodDecl(Position pos, FlagsNode flags, TypeNode returnType,
            Id name,
            List<Formal> formals,  Block body);
    X10MethodDecl X10MethodDecl(Position pos, FlagsNode flags,
            TypeNode returnType, Id name, List<TypeParamNode> typeParams,
            List<Formal> formals, DepParameterExpr guard,  TypeNode offerType, List<TypeNode> throwsopt, Block body);
    SettableAssign SettableAssign(Position pos, Expr a, List<Expr> indices, Assign.Operator op, Expr rhs);

    Tuple Tuple(Position pos, List<Expr> args);
    Tuple Tuple(Position pos, TypeNode indexType, List<Expr> args);
    X10Formal Formal(Position pos, FlagsNode flags, TypeNode type, Id name);
    X10Formal X10Formal(Position pos, FlagsNode flags, TypeNode type, Id name,
                  List<Formal> vars, boolean unnamed);
    ParExpr ParExpr(Position pos, Expr e);
    
    X10ConstructorDecl X10ConstructorDecl(Position pos, FlagsNode flags, Id name,
            TypeNode returnType, List<TypeParamNode> typeParams, List<Formal> formals, 
            DepParameterExpr guard,  TypeNode offerType, List<TypeNode> throwTypes, Block body);
    PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name);
    PropertyDecl PropertyDecl(Position pos, FlagsNode flags, TypeNode type, Id name, Expr init);
    X10Special Self(Position pos);
    
    StmtExpr StmtExpr(Position pos, List<Stmt> statements, Expr result);
    StmtSeq StmtSeq(Position pos, List<Stmt> statements);
    AssignPropertyCall AssignPropertyCall(Position pos, List<TypeNode> typeArgs, List<Expr> argList);

    Closure Closure(Position pos,  List<Formal> formals, DepParameterExpr guard, TypeNode returnType, 
             Block body);
    Closure Closure(Position pos,  List<Formal> formals, DepParameterExpr guard, TypeNode returnType, 
             TypeNode offerType, Block body);

    ClosureCall ClosureCall(Position position, Expr closure,  List<Expr> args);
    ClosureCall ClosureCall(Position position, Expr closure,  List<TypeNode> typeargs, List<Expr> args);

    AnnotationNode AnnotationNode(Position pos, TypeNode tn);
    
    AmbMacroTypeNode AmbMacroTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args);
    TypeNode AmbDepTypeNode(Position pos, AmbMacroTypeNode base, DepParameterExpr dep);
    TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr dep);
    TypeNode AmbDepTypeNode(Position pos, Prefix prefix, Id name, DepParameterExpr dep);

    X10MLSourceFile X10MLSourceFile(Position position, PackageNode packageName, List<Import> imports, List<TopLevelDecl> decls);

    X10New X10New(Position pos, boolean newOmitted, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body);
    X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body);
    X10New X10New(Position pos, Expr qualifier, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments);
    X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments, ClassBody body);
    X10New X10New(Position pos, TypeNode objectType, List<TypeNode> typeArguments, List<Expr> arguments);

    LocalTypeDef LocalTypeDef(Position pos, TypeDecl typeDefDeclaration);
    
    Closure Closure(Closure c, Position pos);
    TypeNode HasType(TypeNode tn);
    Offer Offer(Position pos, Expr e);
    FinishExpr FinishExpr(Position p, Expr e, Stmt s);

	CUDAKernel CUDAKernel(Position position, List<Stmt> statements, Block body);

	public IsRefTest IsRefTest(Position pos, TypeNode t1);
}
