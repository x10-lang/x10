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

import java.util.*;

import polyglot.types.*;
import polyglot.util.Position;
import polyglot.util.StringUtil;

/**
 * This is a node factory that creates no nodes.  It, rather than
 * NodeFactory_c, should be subclassed by any extension which should
 * override the creation of <a>all</a> nodes.
 */
public abstract class AbstractNodeFactory_c implements NodeFactory
{
    public abstract Disamb disamb();

    public Id Id(Position pos, String name) {
	return Id(pos, Name.make(name));
    }
    
    public Prefix PrefixFromQualifiedName(Position pos, QName qualifiedName) {
	if (qualifiedName.qualifier() == null)
            return AmbPrefix(pos, null, Id(pos, qualifiedName.name()));
        
        Position pos2 = pos.truncateEnd(qualifiedName.name().toString().length()+1);
        
        return AmbPrefix(pos, PrefixFromQualifiedName(pos2, qualifiedName.qualifier()), Id(pos, qualifiedName.name()));
    }
    
    public Receiver ReceiverFromQualifiedName(Position pos, QName qualifiedName) {
	if (qualifiedName.qualifier() == null)
	    return AmbReceiver(pos, null, Id(pos, qualifiedName.name()));
        
        Position pos2 = pos.truncateEnd(qualifiedName.name().toString().length()+1);
        
        return AmbReceiver(pos, PrefixFromQualifiedName(pos2, qualifiedName.qualifier()), Id(pos, qualifiedName.name()));
  
    }
    
    public Expr ExprFromQualifiedName(Position pos, QName qualifiedName) {
	if (qualifiedName.qualifier() == null)
	    return AmbExpr(pos, Id(pos, qualifiedName.name()));
        
	Position pos2 = pos.truncateEnd(qualifiedName.name().toString().length()+1);
        
        return Field(pos, ReceiverFromQualifiedName(pos2, qualifiedName.qualifier()), Id(pos, qualifiedName.name()));
    }
    
    public CanonicalTypeNode CanonicalTypeNode(Position pos, Type type) {
        return CanonicalTypeNode(pos, Types.<Type>ref(type));
    }

    public final AmbPrefix AmbPrefix(Position pos, Id name) {
        return AmbPrefix(pos, null, name);
    }

    public final AmbReceiver AmbReceiver(Position pos, Id name) {
        return AmbReceiver(pos, null, name);
    }

    public final AmbTypeNode AmbTypeNode(Position pos, Id name) {
        return AmbTypeNode(pos, null, name);
    }

    public final ArrayInit ArrayInit(Position pos) {
	return ArrayInit(pos, Collections.<Expr>emptyList());
    }

    public final Assert Assert(Position pos, Expr cond) {
        return Assert(pos, cond, null);
    }

    public final Block Block(Position pos) {
	return Block(pos, Collections.<Stmt>emptyList());
    }

    public final Block Block(Position pos, Stmt s1) {
        List<Stmt> l = new ArrayList<Stmt>(1);
	l.add(s1);
	return Block(pos, l);
    }

    public final Block Block(Position pos, Stmt s1, Stmt s2) {
        List<Stmt> l = new ArrayList<Stmt>(2);
	l.add(s1);
	l.add(s2);
	return Block(pos, l);
    }

    public final Block Block(Position pos, Stmt s1, Stmt s2, Stmt s3) {
        List<Stmt> l = new ArrayList<Stmt>(3);
	l.add(s1);
	l.add(s2);
	l.add(s3);
	return Block(pos, l);
    }

    public final Block Block(Position pos, Stmt s1, Stmt s2, Stmt s3, Stmt s4) {
        List<Stmt> l = new ArrayList<Stmt>(4);
	l.add(s1);
	l.add(s2);
	l.add(s3);
	l.add(s4);
	return Block(pos, l);
    }
    
    public final Branch Break(Position pos) {
	return Branch(pos, Branch.BREAK, (Id) null);
    }

    public final Branch Break(Position pos, Id label) {
        return Branch(pos, Branch.BREAK, label);
    }

    public final Branch Continue(Position pos) {
	return Branch(pos, Branch.CONTINUE, (Id) null);
    }

    public final Branch Continue(Position pos, Id label) {
        return Branch(pos, Branch.CONTINUE, label);
    }

    public final Branch Branch(Position pos, Branch.Kind kind) {
	return Branch(pos, kind, (Id) null);
    }
    
    public final Call Call(Position pos, Id name) {
        return Call(pos, null, name, Collections.<Expr>emptyList());
    }

    public final Call Call(Position pos, Id name, Expr a1) {
        List<Expr> l = new ArrayList<Expr>(1);
        l.add(a1);
        return Call(pos, null, name, l);
    }

    public final Call Call(Position pos, Id name, Expr a1, Expr a2) {
        List<Expr> l = new ArrayList<Expr>(2);
        l.add(a1);
        l.add(a2);
        return Call(pos, null, name, l);
    }

    public final Call Call(Position pos, Id name, Expr a1, Expr a2, Expr a3) {
        List<Expr> l = new ArrayList<Expr>(3);
        l.add(a1);
        l.add(a2);
        l.add(a3);
        return Call(pos, null, name, l);
    }
    
    public final Call Call(Position pos, Id name, Expr a1, Expr a2, Expr a3, Expr a4) {
        List<Expr> l = new ArrayList<Expr>(4);
        l.add(a1);
        l.add(a2);
        l.add(a3);
        l.add(a4);
        return Call(pos, null, name, l);
    }

    public final Call Call(Position pos, Id name, List<Expr> args) {
        return Call(pos, null, name, args);
    }
    
    public final Call Call(Position pos, Receiver target, Id name) {
        return Call(pos, target, name, Collections.<Expr>emptyList());
    }

    public final Call Call(Position pos, Receiver target, Id name, Expr a1) {
        List<Expr> l = new ArrayList<Expr>(1);
        l.add(a1);
        return Call(pos, target, name, l);
    }
    
    public final Call Call(Position pos, Receiver target, Id name, Expr a1, Expr a2) {
        List<Expr> l = new ArrayList<Expr>(2);
        l.add(a1);
        l.add(a2);
        return Call(pos, target, name, l);
    }

    public final Call Call(Position pos, Receiver target, Id name, Expr a1, Expr a2, Expr a3) {
        List<Expr> l = new ArrayList<Expr>(3);
        l.add(a1);
        l.add(a2);
        l.add(a3);
        return Call(pos, target, name, l);
    }
    
    public final Call Call(Position pos, Receiver target, Id name, Expr a1, Expr a2, Expr a3, Expr a4) {
        List<Expr> l = new ArrayList<Expr>(4);
        l.add(a1);
        l.add(a2);
        l.add(a3);
        l.add(a4);
        return Call(pos, target, name, l);
    }

    public final Case Default(Position pos) {
	return Case(pos, null);
    }

    public final ConstructorCall ThisCall(Position pos, List<Expr> args) {
	return ConstructorCall(pos, ConstructorCall.THIS, null, args);
    }

    public final ConstructorCall ThisCall(Position pos, Expr outer, List<Expr> args) {
	return ConstructorCall(pos, ConstructorCall.THIS, outer, args);
    }

    public final ConstructorCall SuperCall(Position pos, List<Expr> args) {
	return ConstructorCall(pos, ConstructorCall.SUPER, null, args);
    }

    public final ConstructorCall SuperCall(Position pos, Expr outer, List<Expr> args) {
	return ConstructorCall(pos, ConstructorCall.SUPER, outer, args);
    }

    public final ConstructorCall ConstructorCall(Position pos, ConstructorCall.Kind kind, List<Expr> args) {
	return ConstructorCall(pos, kind, null, args);
    }

    public final FieldDecl FieldDecl(Position pos, FlagsNode flags, TypeNode type, Id name) {
        return FieldDecl(pos, flags, type, name, null);
    }

    public final If If(Position pos, Expr cond, Stmt consequent) {
	return If(pos, cond, consequent, null);
    }

    public final LocalDecl LocalDecl(Position pos, FlagsNode flags, TypeNode type, Id name) {
        return LocalDecl(pos, flags, type, name, null);
    }

    public final New New(Position pos, TypeNode type, List<Expr> args) {
        return New(pos, null, type, args, null);
    }

    public final New New(Position pos, TypeNode type, List<Expr> args, ClassBody body) {
	return New(pos, null, type, args, body);
    }

    public final New New(Position pos, Expr outer, TypeNode objectType, List<Expr> args) {
        return New(pos, outer, objectType, args, null);
    }

    public final NewArray NewArray(Position pos, TypeNode base, List<Expr> dims) {
	return NewArray(pos, base, dims, 0, null);
    }

    public final NewArray NewArray(Position pos, TypeNode base, List<Expr> dims, int addDims) {
	return NewArray(pos, base, dims, addDims, null);
    }

    public final NewArray NewArray(Position pos, TypeNode base, int addDims, ArrayInit init) {
	return NewArray(pos, base, Collections.<Expr>emptyList(), addDims, init);
    }
    
    public final NodeList NodeList(Position pos, List<Node> nodes) {
        return NodeList(pos, this, nodes);
    }

    public final Return Return(Position pos) {
	return Return(pos, null);
    }

    public final SourceFile SourceFile(Position pos, List<TopLevelDecl> decls) {
        return SourceFile(pos, null, Collections.<Import>emptyList(), decls);
    }

    public final SourceFile SourceFile(Position pos, List<Import> imports, List<TopLevelDecl> decls) {
        return SourceFile(pos, null, imports, decls);
    }

    public final Special This(Position pos) {
        return Special(pos, Special.THIS, null);
    }

    public final Special This(Position pos, TypeNode outer) {
        return Special(pos, Special.THIS, outer);
    }

    public final Special Super(Position pos) {
        return Special(pos, Special.SUPER, null);
    }

    public final Special Super(Position pos, TypeNode outer) {
        return Special(pos, Special.SUPER, outer);
    }

    public final Special Special(Position pos, Special.Kind kind) {
        return Special(pos, kind, null);
    }

    public final Try Try(Position pos, Block tryBlock, List<Catch> catchBlocks) {
        return Try(pos, tryBlock, catchBlocks, null);
    }

    public final Unary Unary(Position pos, Expr expr, Unary.Operator op) {
        return Unary(pos, op, expr);
    }
}
