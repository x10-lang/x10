/*
 * Created on Oct 25, 2005
 */
package com.ibm.wala.cast.x10.ssa;

import com.ibm.wala.cast.java.ssa.AstJavaInstructionVisitor;

public interface AstX10InstructionVisitor extends AstJavaInstructionVisitor {
    void visitAtomic(SSAAtomicInstruction instruction);

    void visitFinish(SSAFinishInstruction instruction);

    void visitForce(SSAForceInstruction instruction);

    void visitRegionIterInit(SSARegionIterInitInstruction instruction);

    void visitRegionIterHasNext(SSARegionIterHasNextInstruction instruction);

    void visitRegionIterNext(SSARegionIterNextInstruction instruction);

    void visitHere(SSAHereInstruction instruction);

    void visitPlaceOfPoint(SSAPlaceOfPointInstruction instruction);

    void visitArrayLoadByPoint(X10ArrayLoadByPointInstruction instruction);

    void visitArrayStoreByPoint(X10ArrayStoreByPointInstruction instruction);

    void visitArrayStoreByIndex(X10ArrayStoreByIndexInstruction instruction);

    void visitArrayLoadByIndex(X10ArrayLoadByIndexInstruction instruction);
}
