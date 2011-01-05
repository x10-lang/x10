package x10.wala.ipa.callgraph;

import x10.wala.analysis.typeInference.AstX10TypeInference;
import x10.wala.ssa.AstX10InstructionVisitor;
import x10.wala.ssa.AtStmtInstruction;
import x10.wala.ssa.AtomicInstruction;
import x10.wala.ssa.FinishInstruction;
import x10.wala.ssa.HereInstruction;
import x10.wala.ssa.NextInstruction;
import x10.wala.ssa.IterHasNextInstruction;
import x10.wala.ssa.IterInitInstruction;
import x10.wala.ssa.IterNextInstruction;
import x10.wala.ssa.TupleInstruction;

import com.ibm.wala.analysis.typeInference.TypeInference;
import com.ibm.wala.cast.ipa.callgraph.AstSSAPropagationCallGraphBuilder;
import com.ibm.wala.cast.java.ipa.callgraph.AstJavaSSAPropagationCallGraphBuilder;
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

	public void visitIterInit(IterInitInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "regionIterInit instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitIterHasNext(IterHasNextInstruction instruction) {
	}

	public void visitIterNext(IterNextInstruction instruction) {
	    Assertions.productionAssertion(instruction.getUse(0) == vn, "regionIterNext instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitHere(HereInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Here???");
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
	
	public void visitIterInit(IterInitInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitIterHasNext(IterHasNextInstruction instruction) {
	    // NOOP: no flow through this kind of instruction
	}

	public void visitIterNext(IterNextInstruction instruction) {
	    // TODO model data flow for future/force
	}

	public void visitHere(HereInstruction instruction) {
	    // TODO model data flow for here
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
