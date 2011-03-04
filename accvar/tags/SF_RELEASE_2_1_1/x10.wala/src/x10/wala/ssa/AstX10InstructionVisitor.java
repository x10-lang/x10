/*
 * Created on Oct 25, 2005
 */
package x10.wala.ssa;

import com.ibm.wala.cast.java.ssa.AstJavaInstructionVisitor;

public interface AstX10InstructionVisitor extends AstJavaInstructionVisitor {
    void visitAtomic(AtomicInstruction instruction);
    void visitFinish(FinishInstruction instruction);
    void visitNext(NextInstruction instruction);
    void visitForce(ForceInstruction instruction);
    void visitRegionIterInit(RegionIterInitInstruction instruction);
    void visitRegionIterHasNext(RegionIterHasNextInstruction instruction);
    void visitRegionIterNext(RegionIterNextInstruction instruction);
    void visitHere(HereInstruction instruction);
    void visitPlaceOfPoint(PlaceOfPointInstruction instruction);
    void visitArrayLoadByPoint(ArrayLoadByPointInstruction instruction);
    void visitArrayStoreByPoint(ArrayStoreByPointInstruction instruction);
    void visitArrayStoreByIndex(ArrayStoreByIndexInstruction instruction);
    void visitArrayLoadByIndex(ArrayLoadByIndexInstruction instruction);
    void visitTuple(TupleInstruction newTupleInstruction);
    void visitAtStmt(AtStmtInstruction atStmtInstruction);
}
