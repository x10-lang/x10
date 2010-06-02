package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.analysis.typeInference.TypeInference;
import com.ibm.wala.cast.ipa.callgraph.AstSSAPropagationCallGraphBuilder;
import com.ibm.wala.cast.java.ipa.callgraph.AstJavaSSAPropagationCallGraphBuilder;
import com.ibm.wala.cast.x10.analysis.typeInference.AstX10TypeInference;
import com.ibm.wala.cast.x10.ssa.AstX10InstructionVisitor;
import com.ibm.wala.cast.x10.ssa.NewTupleInstruction;
import com.ibm.wala.cast.x10.ssa.SSAAtStmtInstruction;
import com.ibm.wala.cast.x10.ssa.SSAAtomicInstruction;
import com.ibm.wala.cast.x10.ssa.SSAFinishInstruction;
import com.ibm.wala.cast.x10.ssa.SSAForceInstruction;
import com.ibm.wala.cast.x10.ssa.SSAHereInstruction;
import com.ibm.wala.cast.x10.ssa.SSAPlaceOfPointInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterHasNextInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterInitInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterNextInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayLoadByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayLoadByPointInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayStoreByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayStoreByPointInstruction;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PointerKeyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.debug.Assertions;

public class X10SSAPropagationCallGraphBuilder extends AstJavaSSAPropagationCallGraphBuilder {

    protected X10SSAPropagationCallGraphBuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache, PointerKeyFactory pointerKeyFactory) {
	super(cha, options, cache, pointerKeyFactory);
    }

    protected TypeInference makeTypeInference(IR ir, IClassHierarchy cha) {
	TypeInference ti= new AstX10TypeInference(ir, cha, true);
	ti.solve();

	if (DEBUG_TYPE_INFERENCE) {
	    System.err.println("IR of " + ir.getMethod());
	    System.err.println(ir);
	    System.err.println("TypeInference of " + ir.getMethod());
	    for(int i= 0; i < ir.getSymbolTable().getMaxValueNumber(); i++) {
		if (ti.isUndefined(i)) {
		    System.err.println("  value " + i + " is undefined");
		} else {
		    System.err.println("  value " + i + " has type " + ti.getType(i));
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
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "force instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitRegionIterInit(SSARegionIterInitInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "regionIterInit instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitRegionIterHasNext(SSARegionIterHasNextInstruction instruction) {
	}

	public void visitRegionIterNext(SSARegionIterNextInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "regionIterNext instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitHere(SSAHereInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Here???");
	}

	public void visitArrayLoadByIndex(X10ArrayLoadByIndexInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && instruction.getArrayRef() == vn) {
		bingo= true;
	    }
	}

	public void visitArrayLoadByPoint(X10ArrayLoadByPointInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && instruction.getArrayRef() == vn) {
		bingo= true;
	    }
	}

	public void visitArrayStoreByIndex(X10ArrayStoreByIndexInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && (instruction.getArrayRef() == vn || instruction.getStoreValue() == vn)) {
		bingo= true;
	    }
	}

	public void visitArrayStoreByPoint(X10ArrayStoreByPointInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && (instruction.getArrayRef() == vn || instruction.getStoreValue() == vn)) {
	    	bingo = true;
	    }
	}
	    
	public void visitPlaceOfPoint(SSAPlaceOfPointInstruction instruction) {
		// not interesting for now	
	}

	public void visitNewTuple(NewTupleInstruction instruction) {
            Assertions.productionAssertion(instruction.getUse(0) == vn, "newTuple instruction has bogus use/def info?");
	    bingo= true;
	}
	
	public void visitAtStmt(final SSAAtStmtInstruction atStmtInstruction) {
      // Do nothing.
    }
    }	

    @Override
    protected InterestingVisitor makeInterestingVisitor(CGNode node, int vn) {
	return new AstX10InterestingVisitor(vn);
    }

    protected static class AstX10ConstraintVisitor extends AstJavaConstraintVisitor implements AstX10InstructionVisitor {
	public AstX10ConstraintVisitor(AstSSAPropagationCallGraphBuilder builder,
					ExplicitCallGraph.ExplicitNode node) 
	{
	   super(builder, node);
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

	public void visitRegionIterInit(SSARegionIterInitInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitRegionIterHasNext(SSARegionIterHasNextInstruction instruction) {
	    // NOOP: no flow through this kind of instruction
	}

	public void visitRegionIterNext(SSARegionIterNextInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitHere(SSAHereInstruction instruction) {
	    // TODO model data flow for here
	}

	public void visitArrayLoadByIndex(X10ArrayLoadByIndexInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayLoad(instruction.getDef(), instruction.getArrayRef());
	}

	public void visitArrayLoadByPoint(X10ArrayLoadByPointInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayLoad(instruction.getDef(), instruction.getArrayRef());
	}

	public void visitArrayStoreByIndex(X10ArrayStoreByIndexInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayStore(instruction.getArrayRef(), instruction.getStoreValue());
	}

	public void visitArrayStoreByPoint(X10ArrayStoreByPointInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayStore(instruction.getArrayRef(), instruction.getStoreValue());
	}

	public void visitPlaceOfPoint(SSAPlaceOfPointInstruction instruction) {
		// not interesting for now	
	}

	public void visitNewTuple(NewTupleInstruction newTupleInstruction) {
            // TODO model data flow for newTuple
	}
	
	public void visitAtStmt(final SSAAtStmtInstruction atStmtInstruction) {
      // Do nothing.
    }
    }

    protected ConstraintVisitor makeVisitor(ExplicitCallGraph.ExplicitNode node) {
	return new AstX10ConstraintVisitor(this, node);
    }
}
