/*
 * Created on Oct 25, 2005
 */
package x10.wala.ssa;

import com.ibm.wala.cast.java.ssa.AstJavaInstructionVisitor;

public interface AstX10InstructionVisitor extends AstJavaInstructionVisitor {
    void visitAtomic(AtomicInstruction instruction);
    void visitFinish(FinishInstruction instruction);
    void visitNext(NextInstruction instruction);
    void visitIterInit(IterInitInstruction instruction);
    void visitIterHasNext(IterHasNextInstruction instruction);
    void visitIterNext(IterNextInstruction instruction);
    void visitHere(HereInstruction instruction);
    void visitTuple(TupleInstruction newTupleInstruction);
    void visitAtStmt(AtStmtInstruction atStmtInstruction);
}
