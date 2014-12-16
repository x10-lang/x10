/*
 * Created on Oct 25, 2005
 */
package x10.wala.classLoader;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.types.MethodReference;

public class AsyncCallSiteReference extends CallSiteReference {
    public static enum Dispatch implements IInvokeInstruction.IDispatch {
      ASYNC_CALL {
    	  public boolean hasImplicitThis() {
    		  return false;
    	  }
      }
    }

    public AsyncCallSiteReference(MethodReference ref, int pc) {
      super(pc, ref);
    }

    public IInvokeInstruction.IDispatch getInvocationCode() {
      return Dispatch.ASYNC_CALL;
    }

    public boolean isStatic() {
        return true;
    }

    public String getInvocationString() {
        return "async";
    }

    public String toString() {
      return "Async@" + getProgramCounter();
    }
    
    public CallSiteReference cloneReference(int pc) {
      return new AsyncCallSiteReference(getDeclaredTarget(), pc);
    }

}
