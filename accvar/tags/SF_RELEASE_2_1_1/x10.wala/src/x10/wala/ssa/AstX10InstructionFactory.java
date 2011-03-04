package x10.wala.ssa;

import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.ssa.AstJavaInstructionFactory;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;

public interface AstX10InstructionFactory extends AstJavaInstructionFactory {
	AsyncInvokeInstruction AsyncInvoke(int result, int[] params, int exception, CallSiteReference site, int[] clocks);
	AsyncInvokeInstruction AsyncInvoke(int[] params, int exception, CallSiteReference site, int[] clocks);
	AsyncInvokeInstruction AsyncInvoke(int[] results, int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites, CallSiteReference csr);
	AtomicInstruction Atomic(boolean isEnter);
	FinishInstruction Finish(boolean isEnter);
	NextInstruction Next();
	ForceInstruction Force(int retValue, int targetValue, TypeReference valueType);
	HereInstruction Here(int retValue);
	PlaceOfPointInstruction PlaceOfPoint(int hasNextValue, int regionIter);
	RegionIterHasNextInstruction RegionIterHasNext(int hasNextValue, int regionIter);
	RegionIterInitInstruction RegionIterInit(int iterVal, int regionVal);
	RegionIterNextInstruction RegionIterNext(int nextValue, int regionIter);
	ArrayLoadByIndexInstruction ArrayLoadByIndex(int result, int arrayRef, int dims[], TypeReference declaredType);
	ArrayLoadByPointInstruction ArrayLoadByPoint(int result, int arrayRef, int pointIndex, TypeReference declaredType);
	ArrayStoreByIndexInstruction ArrayStoreByIndex(int arrayRef, int[] indices, int value, TypeReference declaredType);
	ArrayStoreByPointInstruction ArrayStoreByPoint(int arrayRef, int pointIndex, int value, TypeReference declaredType);
	TupleInstruction Tuple(int retValue, int[] slotValues);
	AtStmtInstruction AtStmt(boolean isEnter);
}
