package x10cuda.types;

import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.types.Name;

/**
 * A record containing all information inferred by pattern matching a CUDA kernel. 
 *
 * @author Dave Cunningham
 */

public class CUDAData {
	
	private static int fresh = 1;
	public static int fresh() { return fresh++; }
	
	public Expr blocks;
    public Expr threads;
    public Name blocksVar;
    public Name threadsVar;
    public SharedMem shm;
    public SharedMem cmem;
    public boolean directParams;
    public LocalDecl autoBlocks;
    public LocalDecl autoThreads;
    public int innerStatementTag;

    public String toString() {
		return "blocks: "+blocks+"  "+
		       "threads: "+threads+"  "+
		       "blocksVar: "+blocksVar+"  "+
		       "threadsVar: "+threadsVar+"  "+
		       "shm: "+shm+"  "+
		       "cmem: "+cmem+"  "+
		       "directParams: "+directParams+"  "+
		       "autoBlocks: "+autoBlocks+"  "+
		       "autoThreads: "+autoBlocks+"  "+
		       "innerStatementTag: "+innerStatementTag;
       
    }
}
