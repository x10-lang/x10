/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.Collections;
import java.util.Map;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CodedErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.X10ClassType;
import polyglot.types.Context;

import x10.types.X10ParsedClassType;
import x10.util.CollectionFactory;
import polyglot.types.TypeSystem;
import x10.visit.X10TypeChecker;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public class X10AmbTypeNode_c extends AmbTypeNode_c implements X10AmbTypeNode, AddFlags {
	
	protected Prefix prefix;
	protected Id name;
	
	public X10AmbTypeNode_c(Position pos, Prefix qual, Id name) {
		super(pos, qual, name);
		assert(name != null); // qual may be null
		this.prefix = qual;
		this.name = name;
	}
  
  protected TypeNode disambiguateAnnotation(ContextVisitor tc) throws SemanticException {
      Position pos = position();

      TypeSystem ts = (TypeSystem) tc.typeSystem();
      NodeFactory nf = (NodeFactory) tc.nodeFactory();
      Context c = (Context) tc.context();

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
		      return postprocess((X10CanonicalTypeNode) tn, this, tc);
		  }
	      }

	      throw new SemanticException("Annotation type must be an interface.", position());
	  }

	  String typeName = (prefix == null ? name.toString() : prefix.toString() + "." + name.toString());
	  ex = new SemanticException("Could not find type \"" + typeName +"\".", pos);
	  Map<String, Object> map = CollectionFactory.newHashMap();
      map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_TYPE_NOT_FOUND);
      map.put("TYPE", typeName);
      ex.setAttributes(map);
      }
      catch (SemanticException e) {
	  ex = e;
      }

      throw ex;
  }

  public Node disambiguate(ContextVisitor ar) {
      SemanticException ex;
      
      Position pos = position();
      ContextVisitor tc = ar;
    
      TypeSystem ts =  tc.typeSystem();
      NodeFactory nf = (NodeFactory) tc.nodeFactory();
    
      try {
	  TypeNode tn = disambiguateAnnotation(tc);
	  if (tn != null)
	      return tn;
      }
      catch (SemanticException e) {
          LazyRef<Type> sym = (LazyRef<Type>) type;
          X10ClassType ut = ts.createFakeClass(QName.make(fullName(this.prefix), name().id()), e);
          ut.def().position(pos);
          sym.update(ut);
          Errors.issue(tc.job(), e, this);
          return nf.CanonicalTypeNode(pos, sym);
      }

      Prefix prefix = this.prefix;
      // First look for a typedef.  FIXME: remove
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
              
              Type bt = tn.type();
              if (Types.isConstrainedType(bt)) 
                  bt = Types.baseType(tn.type());
              if (bt instanceof X10ParsedClassType) {
        	  typeDefContainer = (X10ParsedClassType) bt;
              }
              
          }
          else if (prefix instanceof Expr) {
              throw new SemanticException("Non-static type members not implemented: " + prefix + " cannot be understood.", pos);
          }
    
          if (typeDefContainer != null) {
              Context context = tc.context();
              MacroType mt = ts.findTypeDef(typeDefContainer, name.id(), Collections.<Type>emptyList(), Collections.<Type>emptyList(), context);
              
              LazyRef<Type> sym = (LazyRef<Type>) type;
              sym.update(mt);
              
              // Reset the resolver goal to one that can run when the ref is deserialized.
              Goal resolver = tc.job().extensionInfo().scheduler().LookupGlobalType(sym);
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
              Goal resolver = tc.job().extensionInfo().scheduler().LookupGlobalType(sym);
              resolver.update(Goal.Status.SUCCESS);
              sym.setResolver(resolver);
              return postprocess((X10CanonicalTypeNode) tn, this, ar);   
          }
    
          String typeName = (prefix == null ? name.toString() : prefix.toString() + "." + name.toString());
          ex = new SemanticException("Could not find type \"" + typeName +"\".", pos);
          Map<String, Object> map = CollectionFactory.newHashMap();
          map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_TYPE_NOT_FOUND);
          map.put("TYPE", typeName);
          ex.setAttributes(map);
      }
      catch (SemanticException e) {
          ex = e;
      }
    
      // Mark the type as an error, so we don't try looking it up again.
      LazyRef<Type> sym = (LazyRef<Type>) type;
      X10ClassType ut = ts.createFakeClass(QName.make(fullName(prefix), name().id()), ex);
      ut.def().position(position());
      sym.update(ut);
      Errors.issue(tc.job(), ex, this);
      return nf.CanonicalTypeNode(position(), sym);
  }
  
  public static QName fullName(Prefix prefix) {
      if (prefix instanceof PackageNode) {
          PackageNode pn = (PackageNode) prefix;
          return Types.get(pn.package_()).fullName();
      }
      else if (prefix instanceof TypeNode) {
          TypeNode tn = (TypeNode) prefix;
          return tn.type().fullName();
      }
      return null;
  }
  
  static TypeNode postprocess(X10CanonicalTypeNode result, TypeNode n, ContextVisitor childtc) {
	  Flags  f = ((X10AmbTypeNode_c) n).flags;
	  if (f != null) {
		  LazyRef<Type> sym = (LazyRef<Type>) result.typeRef();
		  Type t =  Types.get(sym);
		  t = Types.processFlags(f, t);
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
 

  public Id name() {
      return this.name;
  }
  
  public AmbTypeNode name(Id name) {
      X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
      n.name = name;
      return n;
  }
  
  public Prefix prefix() {
    return this.prefix;
  }

  public AmbTypeNode prefix(Prefix prefix) {
    X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
    n.prefix = prefix;
    return n;
  }

  protected AmbTypeNode_c reconstruct(Prefix qual, Id name) {
    if (qual != this.prefix || name != this.name) {
      X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
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

}
