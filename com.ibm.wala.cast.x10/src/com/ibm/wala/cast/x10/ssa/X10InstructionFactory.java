package com.ibm.wala.cast.x10.ssa;

import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.ssa.AstJavaInstructionFactory;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;

public interface X10InstructionFactory extends AstJavaInstructionFactory {

	AsyncInvokeInstruction AsyncInvoke(int result, int[] params, int exception, CallSiteReference site, int placeExpr, int[] clocks);
	
	AsyncInvokeInstruction AsyncInvoke(int[] params, int exception, CallSiteReference site, int placeExpr, int[] clocks);
	
	AsyncInvokeInstruction AsyncInvoke(int[] results, int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites, CallSiteReference csr);
	
	SSAAtomicInstruction Atomic(boolean isEnter);
	
	SSAFinishInstruction Finish(boolean isEnter);
	
	SSAForceInstruction Force(int retValue, int targetValue, TypeReference valueType);
	
	SSAHereInstruction Here(int retValue);
	
	SSAPlaceOfPointInstruction PlaceOfPoint(int hasNextValue, int regionIter);
	
	SSARegionIterHasNextInstruction RegionIterHasNext(int hasNextValue, int regionIter);
	
	SSARegionIterInitInstruction RegionIterInit(int iterVal, int regionVal);
	
	SSARegionIterNextInstruction RegionIterNext(int nextValue, int regionIter);
	
	X10ArrayLoadByIndexInstruction ArrayLoadByIndex(int result, int arrayRef, int dims[], TypeReference declaredType);
	
	X10ArrayLoadByPointInstruction ArrayLoadByPoint(int result, int arrayRef, int pointIndex, TypeReference declaredType);

	X10ArrayStoreByIndexInstruction ArrayStoreByIndex(int arrayRef, int[] indices, int value, TypeReference declaredType);
	
	X10ArrayStoreByPointInstruction ArrayStoreByPoint(int arrayRef, int pointIndex, int value, TypeReference declaredType);

	NewTupleInstruction NewTuple(int retValue, int[] slotValues);
	
	SSAAtStmtInstruction AtStmt(boolean isEnter);
}
