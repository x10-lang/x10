/*
 * Created on Oct 25, 2005
 */
package com.ibm.domo.ast.x10.ssa;

import com.ibm.domo.classLoader.CallSiteReference;
import com.ibm.domo.types.MethodReference;

public class AsyncCallSiteReference extends CallSiteReference {
    // this must be distinct from java invoke codes.
    // see com.ibm.shrikeBT.BytecodeConstants
    public static final byte ASYNC_CALL = 4;

    public AsyncCallSiteReference(MethodReference ref, int pc) {
      super(pc, ref);
    }

    public byte getInvocationCode() {
      return ASYNC_CALL;
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
}
