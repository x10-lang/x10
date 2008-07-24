/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ext.x10.ast;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.LazyRef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public class X10AmbTypeNode_c extends TypeNode_c implements X10AmbTypeNode {
	protected Prefix qual;
	protected Id name;

//  protected Expr dummy;
  
  public X10AmbTypeNode_c(Position pos, Prefix qual,
                       Id name) {
    super(pos);
    assert(name != null); // qual may be null
    this.qual = qual;
    this.name = name;
  }

  public Id id() {
      return this.name;
  }
  
  public X10AmbTypeNode id(Id name) {
      X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
      n.name = name;
      return n;
  }
  
  public Prefix qual() {
    return this.qual;
  }

  public X10AmbTypeNode qual(Prefix qual) {
    X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
    n.qual = qual;
    return n;
  }

  protected X10AmbTypeNode_c reconstruct(Prefix qual, Id name) {
    if (qual != this.qual || name != this.name) {
      X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
      n.qual = qual;
      n.name = name;
      return n;
    }

    return this;
  }

  public Node visitChildren(NodeVisitor v) {
      Prefix qual = (Prefix) visitChild(this.qual, v);
      Id name = (Id) visitChild(this.name, v);

//      Expr dummy = (Expr) visitChild(this.dummy, v);
//      if (dummy != this.dummy) {
//          AmbTypeNode_c tn = (AmbTypeNode_c) reconstruct(qual, name).copy();
//          tn.dummy = dummy;
//          return tn;
//      }

      return reconstruct(qual, name);
  }
  
  public Node disambiguate(TypeChecker ar) throws SemanticException {
      SemanticException ex;
      
      try {
          Node n = ar.nodeFactory().disamb().disambiguate(this, ar, position(), qual, name);

          if (n instanceof TypeNode) {
              TypeNode tn = (TypeNode) n;
              LazyRef<Type> sym = (LazyRef<Type>) type;
              sym.update(tn.typeRef().get());
              
              // Reset the resolver goal to one that can run when the ref is deserialized.
              Goal resolver = Globals.Scheduler().LookupGlobalType(sym);
              resolver.update(Goal.Status.SUCCESS);
              sym.setResolver(resolver);
              return n;
          }

          ex = new SemanticException("Could not find type \"" +
                                     (qual == null ? name.toString() : qual.toString() + "." + name.toString()) +
                                     "\".", position());
      }
      catch (SemanticException e) {
          ex = e;
      }

      // Mark the type as an error, so we don't try looking it up again.
      LazyRef<Type> sym = (LazyRef<Type>) type;
      sym.update(ar.typeSystem().unknownType(position()));

      throw ex;
  }

  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    if (qual != null) {
        print(qual, w, tr);
        w.write(".");
	w.allowBreak(2, 3, "", 0);
    }
            
    tr.print(this, name, w);
  }

  public String toString() {
    return (qual == null
            ? name.toString()
            : qual.toString() + "." + name.toString()) + "{amb}";
  }
  
  public String name() {
      return name.id();
  }
}
