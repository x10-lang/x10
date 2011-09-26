package x10.ast;

import java.util.Collections;

import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;
import polyglot.frontend.Goal;
import polyglot.types.Context;
import polyglot.types.LazyRef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.MacroType;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType;

/**
 * The AST node for a linked type.
 * 
 * @author Sai Zhang (szhang@cs.washington.edu)
 * */
public class X10AmbTypeNodeLinked_c extends X10AmbTypeNode_c {
	public X10AmbTypeNodeLinked_c(Position pos, Prefix qual, Id name) {
		super(pos, qual, name);
	}
	
	@Override
    public Object copy() {
        Object o = super.copy();
        X10AmbTypeNodeLinked_c ambNodeLinked = (X10AmbTypeNodeLinked_c)o;
        ambNodeLinked.setFlagsNode(this.getFlagsNode());
        return ambNodeLinked;
    }
	
	@Override
	public Id name() {
		return super.name();
	}
	
	
	@Override
	public Node disambiguate(ContextVisitor ar) {
	      SemanticException ex;
	      
	      Position pos = position();
	      ContextVisitor tc = ar;
	      TypeSystem ts =  tc.typeSystem();
	      NodeFactory nf = (NodeFactory) tc.nodeFactory();
	    
	      try {
		      TypeNode tn = disambiguateAnnotation(tc);
		  if (tn != null) {
			  if(this.getFlagsNode() != null) {
			     Errors.issue(tc.job(), new SemanticException("Can not use linked to decorate annotation: "
					   + tn, pos));
			  }
		       return tn;
		    }
	      }
	      catch (SemanticException e) {
	          LazyRef<Type> sym = (LazyRef<Type>) type;
	          X10ClassType ut = ts.createFakeClass(QName.make(fullName(this.prefix), name().id()), e);
	          ut.def().position(pos);
	          sym.update(ut);
	          Errors.issue(tc.job(), e, this);
	          X10CanonicalTypeNode retNode = nf.CanonicalTypeNode(pos, sym); /*data-centric synchronization*/
	          return retNode;
	      }

	      Prefix prefix = this.prefix;
	      // First look for a typedef.  FIXME: remove
	      try {
	          X10ParsedClassType typeDefContainer = null;
	    
	          if (prefix instanceof PackageNode || prefix == null) {
	        	  // TODO: vj check isf this should be uncommented.
//	              PackageNode pn = (PackageNode) prefix;
//	              String dummyName = DUMMY_PACKAGE_CLASS_NAME;
//	              String fullName = (pn != null ? Types.get(pn.package_()).fullName() + "." : "") + dummyName;
//	              Named n = ts.systemResolver().find(fullName);
//	              if (n instanceof X10ParsedClassType) {
//	        	  typeDefContainer = (X10ParsedClassType) n;
//	              }
	          }
	          else if (prefix instanceof TypeNode) {
	              TypeNode tn = (TypeNode) prefix;
	              Type bt = tn.type();
	              //note this is for inner class, like Class.InnerClass
	              //has not been implemented yet
	              if (Types.isConstrainedType(bt)) {
	            	  Errors.issue(tc.job(), new SemanticException("Have not implemented linked for constrained types.", pos));
	                  bt = Types.baseType(tn.type());
	              }
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

	              TypeNode node = postprocess(nf.CanonicalTypeNode(pos, sym), this, ar); /*data-centric synchronization*/
	              node.setFlagsNode(this.getFlagsNode());
	              return node;
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
	              //Type t2 = tn.type();
	              // Reset the resolver goal to one that can run when the ref is deserialized.
	              Goal resolver = tc.job().extensionInfo().scheduler().LookupGlobalType(sym);
	              resolver.update(Goal.Status.SUCCESS);
	              sym.setResolver(resolver);
	              //set the flag for a linked variable for data-centric sych
	              TypeNode node =  postprocess((X10CanonicalTypeNode) tn, this, ar);
	              node.setFlagsNode(this.getFlagsNode());
	              return node;
	          }
	    
	          ex = new SemanticException("Could not find type \"" +(prefix == null ? name.toString() : prefix.toString() + "." + name.toString()) +"\".", pos);
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
	      //data-centric synchronization
	      X10CanonicalTypeNode retNode = nf.CanonicalTypeNode(position(), sym);
	      retNode.setFlagsNode(this.getFlagsNode());
	      return retNode;
	  }
}