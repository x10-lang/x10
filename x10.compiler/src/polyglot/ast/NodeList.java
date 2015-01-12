/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.List;

/**
 * A <code>NodeList</code> represents a list of AST nodes.
 * <code>NodeList</code>s are not intended to appear as part of the AST. When
 * a node is visited, it may replace itself with multiple nodes by returning a
 * <code>NodeList</code> to the visitor. The rewritten node's parent would
 * then be responsible for properly splicing those nodes into the AST.
 * FIXME: make generic
 */
public interface NodeList extends Node {
  /**
   * Get the <code>NodeFactory</code> to use when converting the list to a
   * proper AST node.
   */
  NodeFactory nodeFactory();
  
  /** Get the nodes contained in the list. */
  List<Node> nodes();
  
  /** Set the nodes contained in the list. */
  NodeList nodes(List<Node> nodes);
  
  /** Convert the list into a <code>Block</code>. */
  Block toBlock();
}
