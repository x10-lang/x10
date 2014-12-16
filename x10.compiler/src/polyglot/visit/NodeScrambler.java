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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Compiler;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/**
 * The <code>NodeScrambler</code> is test case generator of sorts. Since it
 * is ofter useful to introduce ``random'' errors into source code, this
 * class provides a way of doing so in a semi-structed manner. The process
 * takes place in two phases. First, a "FirstPass" is made to collect 
 * a list of nodes and their parents. Then a second pass is made to randomly 
 * replace a branch of the tree with another suitable branch. 
 */
public class NodeScrambler extends NodeVisitor
{
  public FirstPass fp;

  protected Map<Node, LinkedList<Node>> pairs;
  protected LinkedList<Node> nodes;
  protected LinkedList<Node> currentParents;
  protected long seed;
  protected Random ran;
  protected boolean scrambled = false;
  protected CodeWriter cw;

  public NodeScrambler()
  {
    this(new Random().nextLong());
  }

  /**
   * Create a new <code>NodeScrambler</code> with the given random number
   * generator seed.
   */
  public NodeScrambler( long seed)
  {
    this.fp = new FirstPass();
    
    this.pairs = CollectionFactory.newHashMap();
    this.nodes = new LinkedList<Node>();
    this.currentParents = new LinkedList<Node>();
    this.cw = Compiler.createCodeWriter(System.err, 72);
    this.seed = seed;
    
    this.ran = new Random( seed);
  }

  /**
   * Scans throught the AST, create a list of all nodes present, along with
   * the set of parents for each node in the tree. <b>This visitor should be
   * run before the main <code>NodeScrambler</code> visits the tree.</b>
   */
  public class FirstPass extends NodeVisitor 
  {
    @SuppressWarnings("unchecked") // Casting to a generic type
    @Override
    public NodeVisitor enter( Node n)
    {
      pairs.put( n, (LinkedList<Node>) currentParents.clone());
      nodes.add( n);
      
      currentParents.add( n);
      return this;
    }
    
    @Override
    public Node leave( Node old, Node n, NodeVisitor v)
    {
      currentParents.remove( n);
      return n;
    }
  }

  public long getSeed()
  {
    return seed;
  }

  @Override
  public Node override( Node n)
  {
    if( coinFlip()) {
      Node m = potentialScramble( n);
      if( m == null) {
        /* No potential replacement. */
        return null;
      }
      else {
        scrambled = true;

        try {
          System.err.println( "Replacing:");
          n.del().dump(System.err);
          System.err.println( "With:");
          m.del().dump(System.err);
        }
        catch( Exception e)
        {
          e.printStackTrace();
          return null;
        }
        return m;
      }
    }
    else {
      return null;
    }  
  }

  protected boolean coinFlip()
  {
    if( scrambled) {
      return false;
    }
    else {
      if( ran.nextDouble() > 0.9) {
        return true;
      }
      else {
        return false;
      }
    }
  }

  protected Node potentialScramble( Node n)
  {
    Class<?> required = Node.class;

    if( n instanceof SourceFile) {
      return null;
    }
    if( n instanceof Import) {
      required = Import.class;
    }
    else if( n instanceof TypeNode) {
      required = TypeNode.class;
    }
    else if( n instanceof ClassDecl) {
      required = ClassDecl.class;
    }
    else if( n instanceof ClassMember) {
      required = ClassMember.class;
    }
    else if( n instanceof Formal) {
      required = Formal.class;
    }
    else if( n instanceof Expr) {
      required = Expr.class;
    }
    else if( n instanceof Block) {
      required = Block.class;
    }
    else if( n instanceof Catch) {
      required = Catch.class;
    }
    else if( n instanceof LocalDecl) {
      required = LocalDecl.class;
    }
    else if( n instanceof Stmt) {
      required = Stmt.class;
    }

    LinkedList<Node> parents = (LinkedList<Node>)pairs.get( n);
    boolean isParent;

    for (Node m : nodes) {
      if( required.isAssignableFrom( m.getClass())) {

        isParent = false;
        for (Node p : parents) {
          if( m == p) {
            isParent = true;
          }
        }

        if( !isParent && m != n) {
          return m;
        }
      }
    }

    return null;
  }
}
