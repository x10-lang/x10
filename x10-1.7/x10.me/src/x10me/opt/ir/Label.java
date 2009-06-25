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

package x10me.opt.ir;

import x10me.opt.controlflow.BasicBlock;
import x10me.opt.ir.operand.*;

/**
 * Class file for Label Instruction class.
 */
public final class Label extends BBMark {

  /**
    * Constructor for Label.
    *
    * @param block
    */
  public Label (BasicBlockOperand block) {
    super (block);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "Label";
  }

  @Override
  public char getOpcode() {
    return Operators.Label;
  }
  
  @Override
  public final Instruction prevInstructionInCodeOrder() {
	BasicBlock nBlock = getBasicBlock().prevBasicBlockInCodeOrder();
	if (nBlock == null) {
	  return null;
	} else {
	  return nBlock.lastInstruction();
	}
  }
 }
