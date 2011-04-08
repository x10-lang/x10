package x10cuda.ast;

import java.util.List;

import polyglot.ast.Block_c;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10cuda.types.CUDAData;
import x10cuda.types.SharedMem;

public class CUDAKernel extends Block_c {

	public CUDAKernel(Position pos, List<Stmt> statements) {
		super(pos, statements);
	}


	public CUDAKernel reconstruct (List<Stmt> statements) {
		CUDAKernel this_ = (CUDAKernel) super.reconstruct(statements);
		return this_;
	}

    public Node visitChildren(NodeVisitor v) {
//    	return reconstruct(left, right);

    	CUDAKernel this_ = (CUDAKernel)super.visitChildren(v);

    	this_.blocks = (Expr) visitChild(this.blocks, v);
    	this_.threads = (Expr) visitChild(this.threads, v);
    	this_.blocksVar = (Formal)visitChild(this.blocksVar, v);
    	this_.threadsVar = (Formal)visitChild(this.threadsVar, v);
    	this_.shm = shm.clone();
    	this_.cmem = cmem.clone();
    	this_.shm.visitChildren(this, v);
    	this_.cmem.visitChildren(this, v);
    	this_.autoBlocks = (LocalDecl)visitChild(this.autoBlocks, v);
    	this_.autoThreads = (LocalDecl)visitChild(this.autoThreads, v);
        return this_;
    }

	// cannot be 0 because we want 0 to mean uninitialised
	private static int fresh = 1;
	public static int fresh() { return fresh++; }
	
	public Expr blocks;
    public Expr threads;
    public Formal blocksVar;
    public Formal threadsVar;
    public SharedMem shm;
    public SharedMem cmem;
    public boolean directParams;
    public LocalDecl autoBlocks;
    public LocalDecl autoThreads;
    public int innerStatementTag;

    public String toString() {
		return "/*blocks: "+blocks+"  "+
		       "threads: "+threads+"  "+
		       "blocksVar: "+blocksVar+"  "+
		       "threadsVar: "+threadsVar+"  "+
		       "shm: "+shm+"  "+
		       "cmem: "+cmem+"  "+
		       "directParams: "+directParams+"  "+
		       "autoBlocks: "+autoBlocks+"  "+
		       "autoThreads: "+autoBlocks+"  "+
		       "innerStatementTag: "+innerStatementTag+"*/  "+
		       super.toString();
       
    }
    
    
}
