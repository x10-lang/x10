package com.ibm.domo.ast.x10.ipa.callgraph;

import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.debug.Trace;
import com.ibm.wala.analysis.typeInference.TypeInference;
import com.ibm.wala.cast.ipa.callgraph.AstSSAPropagationCallGraphBuilder;
import com.ibm.wala.cast.java.ipa.callgraph.AstJavaSSAPropagationCallGraphBuilder;
import com.ibm.domo.ast.x10.analysis.typeInference.AstX10TypeInference;
import com.ibm.domo.ast.x10.ssa.AstX10InstructionVisitor;
import com.ibm.domo.ast.x10.ssa.SSAAtomicInstruction;
import com.ibm.domo.ast.x10.ssa.SSAFinishInstruction;
import com.ibm.domo.ast.x10.ssa.SSAForceInstruction;
import com.ibm.domo.ast.x10.ssa.SSAHereInstruction;
import com.ibm.domo.ast.x10.ssa.SSARegionIterHasNextInstruction;
import com.ibm.domo.ast.x10.ssa.SSARegionIterInitInstruction;
import com.ibm.domo.ast.x10.ssa.SSARegionIterNextInstruction;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph;
import com.ibm.wala.ipa.callgraph.propagation.*;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.*;
import com.ibm.wala.ssa.SSACFG.BasicBlock;
import com.ibm.wala.util.graph.*;
import com.ibm.wala.util.warnings.WarningSet;

public class X10SSAPropagationCallGraphBuilder extends AstJavaSSAPropagationCallGraphBuilder {

    protected X10SSAPropagationCallGraphBuilder(IClassHierarchy cha, WarningSet warnings, AnalysisOptions options, PointerKeyFactory pointerKeyFactory) {
	super(cha, warnings, options, pointerKeyFactory);
    }

    protected TypeInference makeTypeInference(IR ir, IClassHierarchy cha) {
	TypeInference ti= new AstX10TypeInference(ir, cha, true);
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

	public void visitRegionIterInit(SSARegionIterInitInstruction instruction) {
	    Assertions._assert(instruction.getUse(0) == vn, "regionIterInit instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitRegionIterHasNext(SSARegionIterHasNextInstruction instruction) {
	}

	public void visitRegionIterNext(SSARegionIterNextInstruction instruction) {
	    Assertions._assert(instruction.getUse(0) == vn, "regionIterNext instruction has bogus use/def info?");
	    bingo= true;
	}

	public void visitHere(SSAHereInstruction instruction) {
	    Assertions.UNREACHABLE("Query of interestingness of value number for Here???");
	}
    }

    protected InterestingVisitor makeInterestingVisitor(int vn) {
	return new AstX10InterestingVisitor(vn);
    }

    protected static class AstX10ConstraintVisitor extends AstJavaConstraintVisitor implements AstX10InstructionVisitor {

	public AstX10ConstraintVisitor(
		       AstSSAPropagationCallGraphBuilder builder,
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
    }

    protected ConstraintVisitor makeVisitor(ExplicitCallGraph.ExplicitNode node) {
	return new AstX10ConstraintVisitor(this, node);
    }


  /////////////////////////////////////////////////////////////////////////////
  //
  // specialized pointer analysis
  //
  /////////////////////////////////////////////////////////////////////////////

  protected class AstX10PointerFlowGraph extends AstJavaPointerFlowGraph {
    
    protected class AstX10PointerFlowVisitor
      extends AstJavaPointerFlowVisitor 
      implements AstX10InstructionVisitor
    {
      protected AstX10PointerFlowVisitor(PointerAnalysis pa, CallGraph cg, Graph<PointerKey> delegate, CGNode node, IR ir, BasicBlock bb) {
	super(pa, cg, delegate, node, ir, bb);
      }

      public void visitAtomic(SSAAtomicInstruction instruction) {
	  
      }

      public void visitFinish(SSAFinishInstruction instruction) {

      }

      public void visitForce(SSAForceInstruction instruction) {

      }

      public void visitRegionIterInit(SSARegionIterInitInstruction instruction) {
      }

      public void visitRegionIterHasNext(SSARegionIterHasNextInstruction instruction) {

      }

      public void visitRegionIterNext(SSARegionIterNextInstruction instruction) {

      }

      public void visitHere(SSAHereInstruction instruction) {

      }
    }

    protected AstX10PointerFlowGraph(PointerAnalysis pa, CallGraph cg) {
      super(pa,cg);
    }

    protected InstructionVisitor makeInstructionVisitor(CGNode node, IR ir, BasicBlock bb) {
      return new AstX10PointerFlowVisitor(pa, cg, delegate, node, ir, bb);
    }
  }

  public PointerFlowGraphFactory getPointerFlowGraphFactory() {
    return new PointerFlowGraphFactory() {
      public PointerFlowGraph make(PointerAnalysis pa, CallGraph cg) {
	return new AstX10PointerFlowGraph(pa, cg);
      }
    };
  }

}
