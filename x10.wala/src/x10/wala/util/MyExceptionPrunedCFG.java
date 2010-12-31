package x10.wala.util;


import java.util.Iterator;

import x10.wala.ssa.AsyncInstruction;

import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.ipa.cfg.EdgeFilter;
import com.ibm.wala.ipa.cfg.PrunedCFG;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;


/**
 * A view of a CFG that ignores exceptional edges
 */
public class MyExceptionPrunedCFG {

  private static class MyExceptionEdgePruner<I, T extends IBasicBlock<I>> implements EdgeFilter<T>{
    private final ControlFlowGraph<I, T> cfg;

    MyExceptionEdgePruner(ControlFlowGraph<I, T> cfg) {
      this.cfg = cfg;
    }

    public boolean hasNormalEdge(T src, T dst) {
      return cfg.getNormalSuccessors(src).contains(dst);
    }
    /**
     * an exception edge will be removed if:
     * 1 the src node contains a method call
     * 2 the dst node either has a getCaughtException Instruction
     *   or is the ExitBlock
     */
    public boolean hasExceptionalEdge(T src, T dst) {
	Iterator<I> allSrc = src.iterator();
	Iterator<I> allDst = dst.iterator();
	boolean hasMethodInvoke = false;
	boolean hasCaughtException = false;
	
	while(allSrc.hasNext()){
	    I inst = allSrc.next();
	    if(inst instanceof AstJavaInvokeInstruction ||
	       inst instanceof AsyncInstruction){
		hasMethodInvoke = true;
	    }else{
		hasMethodInvoke = false;
	    }
	}
	while(allDst.hasNext()){
	    I inst = allDst.next();
	    if(inst instanceof SSAGetCaughtExceptionInstruction)
		hasCaughtException = true;
	}
      if(hasMethodInvoke && (dst.isExitBlock() || hasCaughtException)){
	  return false;
      }
      return true;
    }
  };

  public static <I, T extends IBasicBlock<I>> PrunedCFG<I, T> make(ControlFlowGraph<I, T> cfg) {
    return PrunedCFG.make(cfg, new MyExceptionEdgePruner<I, T>(cfg));
  }
}


