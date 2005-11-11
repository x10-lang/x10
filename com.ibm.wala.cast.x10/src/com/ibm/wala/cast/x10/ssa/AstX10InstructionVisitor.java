/*
 * Created on Oct 25, 2005
 */
package com.ibm.domo.ast.x10.ssa;

import com.ibm.domo.ast.java.ssa.AstJavaInstructionVisitor;

public interface AstX10InstructionVisitor extends AstJavaInstructionVisitor {
    void visitAtomic(SSAAtomicInstruction instruction);

    void visitFinish(SSAFinishInstruction instruction);

    void visitForce(SSAForceInstruction instruction);
}
