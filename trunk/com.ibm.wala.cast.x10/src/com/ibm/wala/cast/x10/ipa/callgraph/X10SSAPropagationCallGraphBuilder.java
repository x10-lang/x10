package com.ibm.domo.ast.x10.ipa.callgraph;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.capa.util.debug.Trace;
import com.ibm.domo.analysis.typeInference.TypeInference;
import com.ibm.domo.ast.java.ipa.callgraph.AstJavaSSAPropagationCallGraphBuilder;
import com.ibm.domo.ast.x10.analysis.typeInference.AstX10TypeInference;
import com.ibm.domo.ast.x10.ssa.AstX10InstructionVisitor;
import com.ibm.domo.ast.x10.ssa.SSAAtomicInstruction;
import com.ibm.domo.ast.x10.ssa.SSAFinishInstruction;
import com.ibm.domo.ast.x10.ssa.SSAForceInstruction;
import com.ibm.domo.ipa.callgraph.AnalysisOptions;
import com.ibm.domo.ipa.callgraph.impl.ExplicitCallGraph;
import com.ibm.domo.ipa.callgraph.propagation.PointerKeyFactory;
import com.ibm.domo.ipa.cha.ClassHierarchy;
import com.ibm.domo.ssa.DefUse;
import com.ibm.domo.ssa.IR;
import com.ibm.domo.util.warnings.WarningSet;

public class X10SSAPropagationCallGraphBuilder extends AstJavaSSAPropagationCallGraphBuilder {

    protected X10SSAPropagationCallGraphBuilder(ClassHierarchy cha, WarningSet warnings, AnalysisOptions options, PointerKeyFactory pointerKeyFactory) {
	super(cha, warnings, options, pointerKeyFactory);
    }

    protected TypeInference makeTypeInference(IR ir, ClassHierarchy cha) {
	TypeInference ti= new AstX10TypeInference(ir, cha);
	ti.solve();

	if (DEBUG_TYPE_INFERENCE) {
	    Trace.println("IR of " + ir.getMethod());
	    Trace.println(ir);
	    Trace.println("TypeInference of " + ir.getMethod());
	    for(int i= 0; i < ir.getSymbolTable().getMaxValueNumber(); i++) {
		if (ti.isUndefined(i)) {
		    Trace.println("  value " + i + " is undefined");
		} else {
		    Trace.println("  value " + i + " has type " + ti.getType(i));
		}
	    }
	}

	return ti;
    }

    protected class AstX10InterestingVisitor extends AstJavaInterestingVisitor implements AstX10InstructionVisitor {
	protected AstX10InterestingVisitor(int vn) {
	    super(vn);
	}

	public void visitAtomic(SSAAtomicInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Atomic???");
	}

	public void visitFinish(SSAFinishInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Finish???");
	}

	public void visitForce(SSAForceInstruction instruction) {
	    Assertions._assert(instruction.getUse(0) == vn, "force instruction has bogus use/def info?");
	    bingo= true;
	}
    }

    protected InterestingVisitor makeInterestingVisitor(int vn) {
	return new AstX10InterestingVisitor(vn);
    }

    protected class AstX10ConstraintVisitor extends AstJavaConstraintVisitor implements AstX10InstructionVisitor {

	public AstX10ConstraintVisitor(ExplicitCallGraph.ExplicitNode node, IR ir, ExplicitCallGraph callGraph, DefUse du) {
	    super(node, ir, callGraph, du);
	}

	public void visitAtomic(SSAAtomicInstruction instruction) {
	    // NOOP
	}

	public void visitFinish(SSAFinishInstruction instruction) {
	    // NOOP
	}

	public void visitForce(SSAForceInstruction instruction) {
	    // TODO model data flow for future/force
	}
    }

    protected ConstraintVisitor makeVisitor(ExplicitCallGraph.ExplicitNode node, IR ir, DefUse du, ExplicitCallGraph callGraph) {
	return new AstX10ConstraintVisitor(node, ir, callGraph, du);
    }
}
