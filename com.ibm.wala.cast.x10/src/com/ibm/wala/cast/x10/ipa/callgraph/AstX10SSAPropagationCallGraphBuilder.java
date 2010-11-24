package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.analysis.typeInference.TypeInference;
import com.ibm.wala.cast.ipa.callgraph.AstSSAPropagationCallGraphBuilder;
import com.ibm.wala.cast.java.ipa.callgraph.AstJavaSSAPropagationCallGraphBuilder;
import com.ibm.wala.cast.x10.analysis.typeInference.AstX10TypeInference;
import com.ibm.wala.cast.x10.ssa.AstX10InstructionVisitor;
import com.ibm.wala.cast.x10.ssa.TupleInstruction;
import com.ibm.wala.cast.x10.ssa.AtStmtInstruction;
import com.ibm.wala.cast.x10.ssa.AtomicInstruction;
import com.ibm.wala.cast.x10.ssa.FinishInstruction;
import com.ibm.wala.cast.x10.ssa.ForceInstruction;
import com.ibm.wala.cast.x10.ssa.HereInstruction;
import com.ibm.wala.cast.x10.ssa.NextInstruction;
import com.ibm.wala.cast.x10.ssa.PlaceOfPointInstruction;
import com.ibm.wala.cast.x10.ssa.RegionIterHasNextInstruction;
import com.ibm.wala.cast.x10.ssa.RegionIterInitInstruction;
import com.ibm.wala.cast.x10.ssa.RegionIterNextInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayLoadByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayLoadByPointInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayStoreByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayStoreByPointInstruction;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PointerKeyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.debug.Assertions;

public class AstX10SSAPropagationCallGraphBuilder extends AstJavaSSAPropagationCallGraphBuilder {

    protected AstX10SSAPropagationCallGraphBuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache, PointerKeyFactory pointerKeyFactory) {
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

	public void visitAtomic(AtomicInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Atomic???");
	}

	public void visitFinish(FinishInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Finish???");
	}
	
	public void visitNext(NextInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Finish???");
	}

	public void visitForce(ForceInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "force instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitRegionIterInit(RegionIterInitInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "regionIterInit instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitRegionIterHasNext(RegionIterHasNextInstruction instruction) {
	}

	public void visitRegionIterNext(RegionIterNextInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "regionIterNext instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitHere(HereInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Here???");
	}

	public void visitArrayLoadByIndex(ArrayLoadByIndexInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && instruction.getArrayRef() == vn) {
		bingo= true;
	    }
	}

	public void visitArrayLoadByPoint(ArrayLoadByPointInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && instruction.getArrayRef() == vn) {
		bingo= true;
	    }
	}

	public void visitArrayStoreByIndex(ArrayStoreByIndexInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && (instruction.getArrayRef() == vn || instruction.getStoreValue() == vn)) {
		bingo= true;
	    }
	}

	public void visitArrayStoreByPoint(ArrayStoreByPointInstruction instruction) {
	    if (!instruction.typeIsPrimitive() && (instruction.getArrayRef() == vn || instruction.getStoreValue() == vn)) {
	    	bingo = true;
	    }
	}
	    
	public void visitPlaceOfPoint(PlaceOfPointInstruction instruction) {
		// not interesting for now	
	}

	public void visitTuple(TupleInstruction instruction) {
            Assertions.productionAssertion(instruction.getUse(0) == vn, "newTuple instruction has bogus use/def info?");
	    bingo= true;
	}
	
	public void visitAtStmt(final AtStmtInstruction atStmtInstruction) {
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

	public void visitAtomic(AtomicInstruction instruction) {
	    // NOOP
	}

	public void visitFinish(FinishInstruction instruction) {
	    // NOOP
	}

	public void visitNext(NextInstruction instruction) {
	    // NOOP
	}
	
	public void visitForce(ForceInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitRegionIterInit(RegionIterInitInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitRegionIterHasNext(RegionIterHasNextInstruction instruction) {
	    // NOOP: no flow through this kind of instruction
	}

	public void visitRegionIterNext(RegionIterNextInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitHere(HereInstruction instruction) {
	    // TODO model data flow for here
	}

	public void visitArrayLoadByIndex(ArrayLoadByIndexInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayLoad(instruction.getDef(), instruction.getArrayRef());
	}

	public void visitArrayLoadByPoint(ArrayLoadByPointInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayLoad(instruction.getDef(), instruction.getArrayRef());
	}

	public void visitArrayStoreByIndex(ArrayStoreByIndexInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayStore(instruction.getArrayRef(), instruction.getStoreValue());
	}

	public void visitArrayStoreByPoint(ArrayStoreByPointInstruction instruction) {
	    // skip arrays of primitive type
	    if (instruction.typeIsPrimitive()) {
		return;
	    }
	    doVisitArrayStore(instruction.getArrayRef(), instruction.getStoreValue());
	}

	public void visitPlaceOfPoint(PlaceOfPointInstruction instruction) {
		// not interesting for now	
	}

	public void visitTuple(TupleInstruction tupleInstruction) {
            // TODO model data flow for newTuple
	}
	
	public void visitAtStmt(final AtStmtInstruction atStmtInstruction) {
      // Do nothing.
    }
    }

    protected ConstraintVisitor makeVisitor(ExplicitCallGraph.ExplicitNode node) {
	return new AstX10ConstraintVisitor(this, node);
    }
}
