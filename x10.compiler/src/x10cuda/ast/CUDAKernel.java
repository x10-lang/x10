/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cuda.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.LocalInstance;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.ast.X10Call;
import x10.visit.X10AlphaRenamer;
import x10cuda.types.CUDAData;
import x10cuda.types.SharedMem;

public class CUDAKernel extends Block_c {

    public CUDAKernel(Position pos, List<Stmt> statements, Block body) {
        super(pos, statements);
        this.body = body;
    }

    public CUDAKernel reconstruct(List<Stmt> statements) {
        CUDAKernel this_ = (CUDAKernel) super.reconstruct(statements);
        return this_;
    }

    public Node visitChildren(NodeVisitor v) {
        CUDAKernel this_ = (CUDAKernel) super.visitChildren(v);
        
        this_.autoBlocks = (LocalDecl) visitChild(this.autoBlocks, v);
        this_.autoThreads = (LocalDecl) visitChild(this.autoThreads, v);
        this_.blocks = (Expr) visitChild(this.blocks, v);
        this_.threads = (Expr) visitChild(this.threads, v);
        this_.blocksVar = (Formal) visitChild(this.blocksVar, v);
        this_.threadsVar = (Formal) visitChild(this.threadsVar, v);
        this_.shm = shm.clone();
        this_.shm.visitChildren(this, v);
        this_.cmem = cmem.clone();
        this_.cmem.visitChildren(this, v);
        this_.body = (Block) visitChild(this.body, v);

        /* [DC] Bug trap, May 21 2013
        if (this_.blocks instanceof X10Call) {
        	X10Call c = (X10Call) this_.blocks;
        	if (c.target() instanceof Local) {
        		Local l = (Local)c.target();
        		LocalInstance var = l.localInstance();
    		    assert var.name() == l.name().id() : "Name mismatch: "+var.name()+" != "+l.name().id();
        	}
        }
        */
        
        return this_;
    }

    public Expr blocks;
    public Expr threads;
    public Formal blocksVar;
    public Formal threadsVar;
    public SharedMem shm;
    public SharedMem cmem;
    public boolean directParams;
    public LocalDecl autoBlocks;
    public LocalDecl autoThreads;
    protected Block body;

    public Block body() {
        return body;
    }
    public CUDAKernel body(Block body) {
        if (body == this.body) return this;
        CUDAKernel copy = (CUDAKernel) copy();
        copy.body = body;
        return copy;
    }

    public String toString() {
        return super.toString()+"  "+
               "/"+"*"+"blocks: "+blocks+"  "+
               "threads: "+threads+"  "+
               "blocksVar: "+blocksVar+"  "+
               "threadsVar: "+threadsVar+"  "+
               "shm: "+shm+"  "+
               "cmem: "+cmem+"  "+
               "directParams: "+directParams+"  "+
               "autoBlocks: "+autoBlocks+"  "+
               "autoThreads: "+autoBlocks+"*"+"/"+"  "+
               body.toString();
    }
}
