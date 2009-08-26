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

package x10me.opt.controlflow;

import x10me.opt.ir.IR;

/**
 * Extends the functionality of a {@link LSTGraph} so that it comprises
 * {@link AnnotatedLSTNode}s which have extra information in them.
 *
 * @see LSTGraph
 * @see AnnotatedLSTNode
 */
public class AnnotatedLSTGraph extends LSTGraph {
 
  /**
   * The main entry point
   * @param ir the IR to process
   */
  public static void perform(IR ir) {
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Creating an AnnotatedLSTGraph for " + ir.method);
    }
    ir.loopStructureTree = new AnnotatedLSTGraph(ir, ir.loopStructureTree);
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println(ir.loopStructureTree.toString());
    }
  }

  /**
   * Constructor
   *
   * @param ir    The containing IR
   * @param graph The {@link LSTGraph} to convert into an annotated graph
   */
  public AnnotatedLSTGraph(IR ir, LSTGraph graph) {
    super(graph);
    rootNode = new AnnotatedLSTNode(ir, rootNode);
  }
}
