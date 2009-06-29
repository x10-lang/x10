/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Node;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10LocalDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;

public class X10LocalDecl_c extends LocalDecl_c implements X10VarDecl {
	
	public X10LocalDecl_c(Position pos, FlagsNode flags, TypeNode type,
			Id name, Expr init) {
		super(pos, flags, type, name, init);
	}

	@Override
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		if (type instanceof UnknownTypeNode && init == null)
			throw new SemanticException("Cannot infer variable type; variable has no initializer.", position());
		// TODO: For now, since there can be more than once assignment to a mutable variable, do not allow mutable variable types to be inferred.
		// This can be fixed later for local variables by doing better type inference.  This should never be done for fields.
		if (type instanceof UnknownTypeNode && ! flags.flags().isFinal())
			throw new SemanticException("Cannot infer type of non-final variable.", position());
		
		X10LocalDecl_c n = (X10LocalDecl_c) super.buildTypes(tb);

		X10LocalDef fi = (X10LocalDef) n.localDef();

	        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
	        if (as != null) {
	            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
	            for (AnnotationNode an : as) {
	                ats.add(an.annotationType().typeRef());
	            }
	            fi.setDefAnnotations(ats);
	        }
	        
	        return n;
	}

	    @Override
	    public Node setResolverOverride(Node parent, TypeCheckPreparer v) {
		    if (type() instanceof UnknownTypeNode && init != null) {
			    UnknownTypeNode tn = (UnknownTypeNode) type();

			    NodeVisitor childv = v.enter(parent, this);
	    	            childv = childv.enter(this, type());
	    		    			    
			    if (childv instanceof TypeCheckPreparer) {
				    TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
				    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
				    TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
				    tc = (TypeChecker) tc.context(tcp.context().freeze());
				    r.setResolver(new TypeCheckExprGoal(this, init, tc, r));
			    }
		    }
		    return super.setResolverOverride(parent, v);
	    }


	public String shortToString() {
		return "<X10LocalDecl_c #" + hashCode() 
		// + " flags= |" + flags + "|"
		+(type() == null ? "" : " <TypeNode #" + type().hashCode()+"type=" + type().type() + ">")
		+ " name=|" + name().id() + "|"
		+ (init() == null ? "" : " <Expr #" + init().hashCode() +">")
		+ (localDef() == null ? "" : " <LocalInstance #" + localDef().hashCode() +">")
		+ ">";
	}

	public Context enterChildScope(Node child, Context c) {
		if (child == this.type) {
			X10Context xc = (X10Context) c.pushBlock();
			LocalDef li = localDef();
			xc.addVariable(li.asInstance());
			xc.setVarWhoseTypeIsBeingElaborated(li);
			c = xc;
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	
}
