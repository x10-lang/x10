/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public class AmbTypeNode_c extends TypeNode_c implements AmbTypeNode {
	protected Prefix prefix;
	protected Id name;

//  protected Expr dummy;
  
  public AmbTypeNode_c(Position pos, Prefix qual,
                       Id name) {
    super(pos);
    assert(name != null); // qual may be null
    this.prefix = qual;
    this.name = name;
  }

  public Id name() {
      return this.name;
  }
  
  public AmbTypeNode name(Id name) {
      AmbTypeNode_c n = (AmbTypeNode_c) copy();
      n.name = name;
      return n;
  }
  
  public Prefix prefix() {
    return this.prefix;
  }

  public AmbTypeNode prefix(Prefix prefix) {
    AmbTypeNode_c n = (AmbTypeNode_c) copy();
    n.prefix = prefix;
    return n;
  }

  protected AmbTypeNode_c reconstruct(Prefix qual, Id name) {
    if (qual != this.prefix || name != this.name) {
      AmbTypeNode_c n = (AmbTypeNode_c) copy();
      n.prefix = qual;
      n.name = name;
      return n;
    }

    return this;
  }

  public Node visitChildren(NodeVisitor v) {
      Prefix prefix = (Prefix) visitChild(this.prefix, v);
      Id name = (Id) visitChild(this.name, v);
      return reconstruct(prefix, name);
  }
  
  public Node disambiguate(ContextVisitor ar) throws SemanticException {
      SemanticException ex;
      
      try {
          Node n = ar.nodeFactory().disamb().disambiguate(this, ar, position(), prefix, name);

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
                                     (prefix == null ? name.id() : prefix.toString() + "." + name.id()) +
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
    if (prefix != null) {
        print(prefix, w, tr);
        w.write(".");
	w.allowBreak(2, 3, "", 0);
    }
            
    tr.print(this, name, w);
  }

  public String toString() {
    return (prefix == null
            ? name.toString()
            : prefix.toString() + "." + name.toString()) + "{amb}";
  }
}
