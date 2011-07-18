package x10.wala.ssa;

import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.ssa.AstJavaInstructionFactory;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;

public interface AstX10InstructionFactory extends AstJavaInstructionFactory {
	AsyncInstruction AsyncInvoke(int result, int[] params, int exception, CallSiteReference site, int[] clocks);
	AsyncInstruction AsyncInvoke(int[] params, int exception, CallSiteReference site, int[] clocks);
	AsyncInstruction AsyncInvoke(int[] results, int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites, CallSiteReference csr);
	AtomicInstruction Atomic(boolean isEnter);
	FinishInstruction Finish(boolean isEnter);
	NextInstruction Next();
	HereInstruction Here(int retValue);
	IterHasNextInstruction IterHasNext(int hasNextValue, int regionIter);
	IterInitInstruction IterInit(int iterVal, int regionVal);
	IterNextInstruction IterNext(int nextValue, int regionIter);
	TupleInstruction Tuple(int retValue, int[] slotValues);
	AtStmtInstruction AtStmt(boolean isEnter);
}
