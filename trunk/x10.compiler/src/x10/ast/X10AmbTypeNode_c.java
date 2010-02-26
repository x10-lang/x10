/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.Collections;

import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.TypeCheckTypeGoal;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.types.MacroType;
import x10.types.X10Context;
import x10.types.X10Flags;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.visit.X10TypeChecker;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public class X10AmbTypeNode_c extends AmbTypeNode_c implements X10AmbTypeNode, AddFlags {
  public X10AmbTypeNode_c(Position pos, Prefix qual,
                       Id name) {
    super(pos, qual, name);
    assert(name != null); // qual may be null
  }
  
  protected TypeNode disambiguateAnnotation(ContextVisitor tc) throws SemanticException {
      Position pos = position();

      X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
      X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
      X10Context c = (X10Context) tc.context();

      if (! c.inAnnotation())
	  return null;

      SemanticException ex;

      Prefix prefix = this.prefix;

      // Look for a simply-named type.
      try {
	  Disamb disamb = tc.nodeFactory().disamb();
	  Node n = disamb.disambiguate(this, tc, pos, prefix, name);

	  if (n instanceof TypeNode) {
	      TypeNode tn = (TypeNode) n;
	      Ref<Type> tref = (Ref<Type>) tn.typeRef();
	      Type t = tref.get();
	      if (t instanceof X10ParsedClassType) {
		  X10ParsedClassType ct = (X10ParsedClassType) t;
		  if (ct.flags().isInterface()) {
		      return postprocess((CanonicalTypeNode) tn, this, tc);
		  }
	      }

	      throw new SemanticException("Annotation type must be an interface.", position());
	  }

	  ex = new SemanticException("Could not find type \"" +
	                             (prefix == null ? name.toString() : prefix.toString() + "." + name.toString()) +
	                             "\".", pos);
      }
      catch (SemanticException e) {
	  ex = e;
      }

      throw ex;
  }

  public Node disambiguate(ContextVisitor ar) throws SemanticException {
      SemanticException ex;
      
      Position pos = position();
      ContextVisitor tc = ar;
    
      X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
      X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
    
      try {
	  TypeNode tn = disambiguateAnnotation(tc);
	  if (tn != null)
	      return tn;
      }
      catch (SemanticException e) {
	  ((Ref<Type>) type).update(ts.unknownType(pos));
	  return this;
      }

      Prefix prefix = this.prefix;
      // First look for a typedef.
      try {
          X10ParsedClassType typeDefContainer = null;
    
          if (prefix instanceof PackageNode || prefix == null) {
        	  // TODO: vj check isf this should be uncommented.
//              PackageNode pn = (PackageNode) prefix;
//              String dummyName = DUMMY_PACKAGE_CLASS_NAME;
//              String fullName = (pn != null ? Types.get(pn.package_()).fullName() + "." : "") + dummyName;
//              Named n = ts.systemResolver().find(fullName);
//              if (n instanceof X10ParsedClassType) {
//        	  typeDefContainer = (X10ParsedClassType) n;
//              }
          }
          else if (prefix instanceof TypeNode) {
              TypeNode tn = (TypeNode) prefix;
              if (tn.type() instanceof X10ParsedClassType) {
        	  typeDefContainer = (X10ParsedClassType) tn.type();
              }
          }
          else if (prefix instanceof Expr) {
              throw new SemanticException("Non-static type members not implemented: " + prefix + " cannot be understood.", pos);
          }
    
          if (typeDefContainer != null) {
              Context context = tc.context();
            MacroType mt = ts.findTypeDef(typeDefContainer, ts.TypeDefMatcher(typeDefContainer, name.id(), Collections.EMPTY_LIST, Collections.EMPTY_LIST, context), context);
              
              LazyRef<Type> sym = (LazyRef<Type>) type;
              sym.update(mt);
              
              // Reset the resolver goal to one that can run when the ref is deserialized.
              Goal resolver = Globals.Scheduler().LookupGlobalType(sym);
              resolver.update(Goal.Status.SUCCESS);
              sym.setResolver(resolver);

              return postprocess(nf.CanonicalTypeNode(pos, sym), this, ar);   
          }
      }
      catch (SemanticException e) {
      }
    
      // Otherwise, look for a simply-named type.
      try {
          Disamb disamb = ar.nodeFactory().disamb();
          Node n = disamb.disambiguate(this, ar, pos, prefix, name);

          if (n instanceof TypeNode) {
              TypeNode tn = (TypeNode) n;
              LazyRef<Type> sym = (LazyRef<Type>) type;
              Type t2 = tn.type();
              
       /*       if (t2 instanceof X10ParsedClassType) {
        	  X10ParsedClassType ct = (X10ParsedClassType) tn.type();
        	  if (ct.x10Def().typeParameters().size() != 0) {
        	      throw new SemanticException("Invalid type " + ct + "; incorrect number of type arguments.", position());
        	  }
              }*/
              
              // Reset the resolver goal to one that can run when the ref is deserialized.
              Goal resolver = Globals.Scheduler().LookupGlobalType(sym);
              resolver.update(Goal.Status.SUCCESS);
              sym.setResolver(resolver);
              return postprocess((CanonicalTypeNode) tn, this, ar);   
          }
    
          ex = new SemanticException("Could not find type \"" +
                                     (prefix == null ? name.toString() : prefix.toString() + "." + name.toString()) +
                                     "\".", pos);
      }
      catch (SemanticException e) {
          ex = e;
      }
    
      // Mark the type as an error, so we don't try looking it up again.
      LazyRef<Type> sym = (LazyRef<Type>) type;
      sym.update(ar.typeSystem().unknownType(position()));
    
      throw ex;
  }
  
  @Override
  public void setResolver(Node parent, final TypeCheckPreparer v) {
  	if (typeRef() instanceof LazyRef) {
  		LazyRef<Type> r = (LazyRef<Type>) typeRef();
  		TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
  		tc = (TypeChecker) tc.context(v.context().freeze());
  		r.setResolver(new TypeCheckTypeGoal(parent, this, tc, r, false));
  	}
  }
  static TypeNode postprocess(CanonicalTypeNode result, TypeNode n, ContextVisitor childtc) 
  throws SemanticException {
	  Flags  f = ((X10AmbTypeNode_c) n).flags;
	  if (f != null) {
		  LazyRef<Type> sym = (LazyRef<Type>) result.typeRef();
		  Type t =  Types.get(sym);
		  t = X10TypeMixin.processFlags(f, t);
	      sym.update(t);
	  }
      return AmbDepTypeNode_c.postprocess(result, n, childtc);
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
  Flags flags;
  public void addFlags(Flags f) {
	  this.flags = f;
  }
 
}
