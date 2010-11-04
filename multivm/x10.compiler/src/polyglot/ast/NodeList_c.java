/*
 * This file is part of the Polyglot extensible compiler framework. Copyright
 * (c) 2000-2007 Polyglot project group, Cornell University Copyright (c)
 * 2006-2007 IBM Corporation
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.Context;
import polyglot.util.*;
import polyglot.visit.NodeVisitor;

/**
 * A <code>NodeList</code> represents a list of AST nodes.
 * <code>NodeList</code>s are not intended to appear as part of the AST. When
 * a node is visited, it may replace itself with multiple nodes by returning a
 * <code>NodeList</code> to the visitor. The rewritten node's parent would
 * then be responsible for properly splicing those nodes into the AST.
 * FIXME: make generic
 */
public class NodeList_c extends Node_c implements NodeList {
  protected NodeFactory nf;
  protected List<Node> nodes;

  public NodeList_c(Position pos, NodeFactory nf, List<Node> nodes) {
    super(pos);
    assert (nodes != null);
    this.nf = nf;
    this.nodes = TypedList.copyAndCheck(nodes, Node.class, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.NodeList#nodes()
   */
  public List<Node> nodes() {
    return nodes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.NodeList#nodes(java.util.List)
   */
  public NodeList nodes(List<Node> nodes) {
    NodeList_c result = (NodeList_c) copy();
    result.nodes = TypedList.copyAndCheck(nodes, Node.class, true);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.NodeList#nodeFactory()
   */
  public NodeFactory nodeFactory() {
    return nf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.NodeList#toBlock()
   */
  public Block toBlock() {
    return nf.Block(position, (List<Stmt>)(List) nodes);
  }
  
  public String toString() {
      StringBuffer sb = new StringBuffer();

      int count = 0;

      for (Node n : nodes) {
	  if (count++ > 2) {
	      sb.append(" ...");
	      break;
	  }

	  sb.append(" ");
	  sb.append(n.toString());
      }

      return sb.toString();
  }
  
  @Override
    public void addDecls(Context c) {
      for (Node n : nodes) {
	n.addDecls(c);
    }
  }

  @Override
  public Node visitChildren(NodeVisitor v) {
      List<Node> l = visitList(nodes, v);
      if (CollectionUtil.allEqual(l, nodes))
	  return this;      
      return nodes(l);
    }
}
