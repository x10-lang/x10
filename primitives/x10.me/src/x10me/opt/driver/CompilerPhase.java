/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.opt.driver;

import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.ir.IR;

/**
 * Compiler phases all extend this abstract class.
 * All compiler phases must provide implementations of
 * two abstract methods:
 * <ul>
 *  <li> getName:  return a String that is the name of the phase
 *  <li> perform:  actually do the work of the phase
 * </ul>
 *
 * <p> By default, a new instance of the phase is created each time
 * shouldPerform is called.  This instance is discarded as soon
 * as shouldPerform completes. Therefore, it is allowable
 * (and is suggested when necessary) for subclasses
 * to use their instance fields to hold per-compilation state.
 * To be more concrete, the pattern of use is:
 * <pre>
 *  newExecution(ir).performPhase(ir).
 * </pre>
 * @see OptimizationPlanAtomicElement#perform
 *
 * <p> NOTE: compiler phases that do not need to use instance
 * fields to hold per-compilation state may override
 * <code> newExecution() </code> to return this.  Doing so may lead to
 * memory leaks and concurrent access problems, so this should be done
 * with great care!
 */
public abstract class CompilerPhase {

  /**
   * The ir object that this pass is optimizing.
   */
  protected IR ir;
  
  /**
   * nstructor
   * @param ir object this pass is optimizing.
   */
  protected CompilerPhase(IR ir) {
    this.ir = ir;
  }

  /**
   * @return a String which is the name of the phase.
   */
  public abstract String getName();

  /**
   * This is the method that actually does the work of the phase.
   */
  public abstract void perform();

  /**
   * This method determines if the phase should be run, based on the
   * Options object it is passed.
   * By default, phases are always performed.
   * Subclasses should override this method if they only want
   * to be performed conditionally.
   *
   * @param options the compiler options for the compilation
   * @return true if the phase should be performed
   */
  public boolean shouldPerform(OptOptions options) {
    return true;
  }

  /**
   * Returns true if the phase wants the IR dumped before it's run 
   * based on the <code>ir.options</code> passed into the constructor.
   * 
   * Subclasses may override either method or provide their own 
   * options if they want to different processing.
   */
  public boolean dumpBefore() {
    return ir.options.dumpBefore;
  }

  /**
   * Returns true if the phase wants the IR dumped after it's run 
   * based on the <code>ir.options</code> passed into the constructor.
   * 
   * Subclasses may override either method or provide their own 
   * options if they want to different processing.
   */
  public boolean dumpAfter() {
    return ir.options.dumpAfter;
  }

  /**
   * Returns true if the phase wants the IR dumped during it's run 
   * based on the <code>options</code> passed into the constructor.
   * 
   * Subclasses may override either method or provide their own 
   * options if they want to different processing.
   */
  public boolean dumpTrace() {
    return ir.options.dumpTrace;
  }
  
  /**
   * Print additional information after the final dump for the pass.
   */
  public final void reportAdditionalStats() {
    int counter1 = 0;
    int counter2 = 0;
    for (BasicBlockEnumeration e = ir.getBasicBlocks(); e.hasMoreElements();) {
      BasicBlock bb = e.next();
      if (bb.isEmpty()) continue;
      counter2++;
      if (bb.getInfrequent()) {
        counter1++;
      }
    }

    ir.dumpFile.current.println("  " + (counter1 / counter2 * 100) + "% Infrequent BBs");
  }

  /**
   * Runs a phase by calling perform on the supplied IR surrounded by
   * printing/messaging/debugging glue.
   */
  public final void performPhase() {
    if (dumpAfter ()) {
      ir.dumpFile.enableDumping();
      dumpIR(ir, "Before ");
      ir.dumpFile.disableDumping();
    }
      
    if (dumpTrace())
      ir.dumpFile.enableDumping();
   
    perform();                // DOIT!!
    ir.dumpFile.disableDumping();

    if (dumpAfter ()) {
      ir.dumpFile.enableDumping();
      dumpIR(ir, "After ");
      reportAdditionalStats();
      ir.dumpFile.disableDumping();
    }

    if (OptOptions.verifyIR > OptOptions.NOVERIFY) verify(ir);
  }

  /**
   * Prints the IR, optionally including the CFG
   *
   * @param ir the IR to print
   * @param tag a String to use in the start/end message of the IR dump
   */
  public static void dumpIR(IR ir, String tag) {
    dumpIR(ir, tag, false);
  }

  /**
   * Prints the IR, optionally including the CFG
   *
   * @param ir the IR to print
   * @param forceCFG should the CFG be printed, independent of the value of ir.options.PRINT_CFG?
   * @param tag a String to use in the start/end message of the IR dump
   */
  public static void dumpIR(IR ir, String tag, boolean forceCFG) {
    ir.dumpFile.current.println("********* START OF IR DUMP  " + tag + "   FOR " + ir.method);
    ir.printInstructions();
    ir.dumpFile.current.println("*********   END OF IR DUMP  " + tag + "   FOR " + ir.method);
  }

  /**
   * Verify the IR.
   * Written as a non-final virtual method to allow late stages in the
   * compilation pipeline (eg ConvertMIR2MC) to skip verification.
   *
   * @param ir the IR to verify
   */
  public void verify(IR ir) {
    ir.verify(getName(), true);
  }
}
