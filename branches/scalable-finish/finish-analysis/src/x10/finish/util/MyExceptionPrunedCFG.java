package x10.finish.util;


import java.util.Iterator;

import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.ipa.cfg.EdgeFilter;
import com.ibm.wala.ipa.cfg.PrunedCFG;


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

    public boolean hasExceptionalEdge(T src, T dst) {
	Iterator<I> all = src.iterator();
	boolean flag = false;
	while(all.hasNext()){
	    I inst = all.next();
	    if(inst instanceof AstJavaInvokeInstruction){
		flag = true;
	    }else{
		flag = false;
	    }
	}
      if(flag && dst.isExitBlock()){
	  return false;
      }
      return true;
    }
  };

  public static <I, T extends IBasicBlock<I>> PrunedCFG<I, T> make(ControlFlowGraph<I, T> cfg) {
    return PrunedCFG.make(cfg, new MyExceptionEdgePruner<I, T>(cfg));
  }
}


