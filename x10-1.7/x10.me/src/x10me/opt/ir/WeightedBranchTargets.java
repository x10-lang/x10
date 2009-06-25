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
import x10me.opt.ir.operand.BranchProfileOperand;

/**
 * Used to iterate over the branch targets (including the fall through edge)
 * and associated probabilites of a basic block.
 * Takes into account the ordering of branch instructions when
 * computing the edge weights such that the total target weight will always
 * be equal to 1.0 (flow in == flow out).
 */
public final class WeightedBranchTargets {
  private BasicBlock[] targets;
  private float[] weights;
  private int cur;
  private int max;

  public void reset() { cur = 0; }

  public boolean hasMoreElements() { return cur < max; }

  public void advance() { cur++; }

  public BasicBlock curBlock() { return targets[cur]; }

  public float curWeight() { return weights[cur]; }

  public WeightedBranchTargets(BasicBlock bb) {
    targets = new BasicBlock[3];
    weights = new float[3];
    cur = 0;
    max = 0;

    float prob = 1f;
    for (InstructionEnumeration ie = bb.enumerateBranchInstructions(); ie.hasMoreElements();) {
      Instruction s = ie.next();
      if (s instanceof IfCmp) {
	IfCmp t = (IfCmp)s;
	BasicBlock target = t.getTarget().target.getBasicBlock();
	BranchProfileOperand prof = t.getBranchProfile();
	float taken = prob * prof.takenProbability;
	prob = prob * (1f - prof.takenProbability);
	addEdge(target, taken);
      } else if (s instanceof Goto) {
	Goto t = (Goto)s;
	BasicBlock target = t.getTarget().target.getBasicBlock();
	addEdge(target, prob);
      } else if (s instanceof InlineGuard) {
	InlineGuard t = (InlineGuard)s;
	BasicBlock target = t.getTarget().target.getBasicBlock();
	BranchProfileOperand prof = t.getBranchProfile();
	float taken = prob * prof.takenProbability;
	prob = prob * (1f - prof.takenProbability);
	addEdge(target, taken);
      } else if (s instanceof IfCmp2) {
	IfCmp2 t = (IfCmp2)s;
	BasicBlock target = t.getTarget1().target.getBasicBlock();
	BranchProfileOperand prof = t.getBranchProfile1();
	float taken = prob * prof.takenProbability;
	prob = prob * (1f - prof.takenProbability);
	addEdge(target, taken);
	target = t.getTarget2().target.getBasicBlock();
	prof = t.getBranchProfile2();
	taken = prob * prof.takenProbability;
	prob = prob * (1f - prof.takenProbability);
	addEdge(target, taken);
      } else if (s instanceof TableSwitch) {
	TableSwitch t = (TableSwitch)s;
	int lowLimit = t.getLow().value;
	int highLimit = t.getHigh().value;
	int number = highLimit - lowLimit + 1;
	float total = 0f;
	for (int i = 0; i < number; i++) {
	  BasicBlock target = t.getTarget(i).target.getBasicBlock();
	  BranchProfileOperand prof = t.getBranchProfile(i);
	  float taken = prob * prof.takenProbability;
	  total += prof.takenProbability;
	  addEdge(target, taken);
	}
	BasicBlock target = t.getDefaultTarget().target.getBasicBlock();
	BranchProfileOperand prof = t.getDefaultBranchProfile();
	float taken = prob * prof.takenProbability;
	total += prof.takenProbability;
	assert epsilon(total, 1f) : "Total outflow (" + total + ") does not sum to 1 for: " + s;
	addEdge(target, taken);
      } else if (s instanceof LowTableSwitch) {
	LowTableSwitch t = (LowTableSwitch)s;
	int number = t.getNumberOf();
	float total = 0f;
	for (int i = 0; i < number; i++) {
	  BasicBlock target = t.getTarget(i).target.getBasicBlock();
	  BranchProfileOperand prof = t.getBranchProfile(i);
	  float taken = prob * prof.takenProbability;
	  total += prof.takenProbability;
	  addEdge(target, taken);
	}
	assert epsilon(total, 1f) : "Total outflow (" + total + ") does not sum to 1 for: " + s;
      } else if (s instanceof LookupSwitch) {
	LookupSwitch t = (LookupSwitch)s;
	int number = t.getNumberOf();
	float total = 0f;
	for (int i = 0; i < number; i++) {
	  BasicBlock target = t.getTarget(i).target.getBasicBlock();
	  BranchProfileOperand prof = t.getBranchProfile(i);
	  float taken = prob * prof.takenProbability;
	  total += prof.takenProbability;
	  addEdge(target, taken);
	}
	BasicBlock target = t.getDefaultTarget().target.getBasicBlock();
	BranchProfileOperand prof = t.getDefaultBranchProfile();
	float taken = prob * prof.takenProbability;
	total += prof.takenProbability;
	assert epsilon(total, 1f) : "Total outflow (" + total + ") does not sum to 1 for: " + s;
	addEdge(target, taken);
      } else {
	throw new Error("TODO " + s + "\n");
      }
    }
    BasicBlock ft = bb.getFallThroughBlock();
    if (ft != null) addEdge(ft, prob);
  }

  private void addEdge(BasicBlock target, float weight) {
    if (max == targets.length) {
      BasicBlock[] tmp = new BasicBlock[targets.length << 1];
      for (int i = 0; i < targets.length; i++) {
	tmp[i] = targets[i];
      }
      targets = tmp;
      float[] tmp2 = new float[weights.length << 1];
      for (int i = 0; i < weights.length; i++) {
	tmp2[i] = weights[i];
      }
      weights = tmp2;
    }
    targets[max] = target;
    weights[max] = weight;
    max++;
  }

  private boolean epsilon(float a, float b) {
    return Math.abs(a - b) < 0.1;
  }
}
