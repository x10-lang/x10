/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.extension;

import polyglot.ast.NamedVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Initializer;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Loop;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.VarDecl;
import polyglot.ast.While;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.InitializerDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.ArrayType;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.ast.Ext_c;
import polyglot.ast.Unary.Operator;
import x10.ast.*;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.effects.EffectComputer;
import x10.effects.TermCreator;
import x10.effects.XVarDefWrapper;
import x10.effects.constraints.ArrayElementLocs;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Effects;
import x10.effects.constraints.FieldLocs;
import x10.effects.constraints.LocalLocs;
import x10.effects.constraints.Locs;
import x10.effects.constraints.Safety;
import x10.types.ConstrainedType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10LocalDef;
import x10.types.X10MethodDef_c;
import x10.types.X10ParsedClassType;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import x10.types.X10TypeEnv;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.AnnotatedType;
import x10.types.X10TypeMixin;
import x10.types.X10ClassDef;
/**
 * The extension node for X10.
 * 
 * Handles comments and effects.
 * @author igorp
 * @author vj
 * @author nalini
 */
public class X10Ext_c extends Ext_c implements X10Ext {
    String comment;
    List<AnnotationNode> annotations;
    
    public String comment() {
        return this.comment;
    }
 
    
    public X10Ext comment(String comment) {
        X10Ext_c n = (X10Ext_c) copy();
        n.comment = comment;
        return n;
    }
   
    public Node setComment(String comment) {
        Node n = this.node();
        return n.ext(this.comment(comment));
    }
    
    public List<AnnotationNode> annotations() {
    	if (this.annotations == null) {
    		return Collections.EMPTY_LIST;
    	}
    	return Collections.unmodifiableList(this.annotations);
    }
    
    public List<X10ClassType> annotationTypes() {
    	if (this.annotations == null) {
    		return Collections.EMPTY_LIST;
    	}
    	List<X10ClassType> l = new ArrayList<X10ClassType>(this.annotations.size());
    	for (Iterator<AnnotationNode> i = this.annotations.iterator(); i.hasNext(); ) {
    		AnnotationNode a = i.next();
    		l.add(a.annotationInterface());
    	}
    	return l;
    }
    
    public List<X10ClassType> annotationMatching(Type t) {
		List<X10ClassType> l = new ArrayList<X10ClassType>();
		for (Iterator<AnnotationNode> i = annotations().iterator(); i.hasNext(); ) {
			AnnotationNode an = i.next();
			X10ClassType ct = an.annotationInterface();
			if (ct.isSubtype(t, t.typeSystem().emptyContext())) {
				l.add(ct);
			}
		}
		return l;
	}
    
    public X10Ext extAnnotations(List<AnnotationNode> annotations) {
    	X10Ext_c n = (X10Ext_c) copy();
    	n.annotations = new ArrayList<AnnotationNode>(annotations);
    	return n;
    }
    
    public Node annotations(List<AnnotationNode> annotations) {
    	Node n = this.node();
    	return n.ext(this.extAnnotations(annotations));
    }
    
    
    
    
    Effect effect;
    Safety safety;
    
    public Effect effect() {
    	return this.effect;
    }
	
    public X10Ext effect(Effect e) {
        X10Ext_c n = (X10Ext_c) copy();
        n.effect = e;
        return n;
    }
    	

	public Node computeEffectsOverride(Node n, EffectComputer ec) throws SemanticException {
		return null;
	}
	public EffectComputer computeEffectsEnter(EffectComputer ec) {
		return null;
	}
	public Node computeEffects(EffectComputer ec) {
	      Effect result= null;
	      Node n = node();
	      /* System.out.print("Here: "); */
	        try {
	            if (n instanceof Async) {
	                result=computeEffect((Async) n, ec);
		    } else if (n instanceof ForEach) {
	                result=computeEffect((ForEach) n, ec);
		    } else if (n instanceof AtEach) {
                result=computeEffect((AtEach) n, ec);
		    } else if (n instanceof Finish) {
	                result=computeEffect((Finish) n, ec);
	                //System.out.println("Result of finish" + result);
	            } else if (n instanceof Atomic) {
	                result=computeEffect((Atomic) n, ec);
	            } else if (n instanceof AtStmt) {
	                result= effect(((AtStmt) n).body());
	            } else if (n instanceof Binary) {
	                result=computeEffect((Binary) n, ec);
	            } else if (n instanceof Block) {
	                result=computeEffect((Block) n, ec);
	            } else if (n instanceof Call) {
	                result=computeEffect((Call) n, ec);
	            } else if (n instanceof Eval) {
	                result=  effect(((Eval) n).expr());
	            } else if (n instanceof Field) {
	                result=computeEffect((Field) n, ec);
	            } else if (n instanceof FieldAssign) {
	                result=computeEffect((FieldAssign) n, ec);
	            } else if (n instanceof FieldDecl) {
	                result=computeEffect((FieldDecl) n, ec);
	            } else if (n instanceof ForEach) {
	                 result=computeEffect((ForEach) n, ec);
	            } else if (n instanceof ForLoop) {
	                 result=computeEffect((ForLoop) n, ec);
	            } else if (n instanceof If) {
	                 result=computeEffect((If) n, ec);
	            } else if (n instanceof Local) {
	                 result=computeEffect((Local) n, ec);
	            } else if (n instanceof LocalAssign) {
	                 result=computeEffect((LocalAssign) n, ec);
	            } else if (n instanceof LocalDecl) {
	            	 LocalDecl x = (LocalDecl)n;
	                 result=computeEffect((LocalDecl) n, ec);
	            } else if (n instanceof LocalAssign) {
	            } else if (n instanceof New) {
	                 result=computeEffect((New) n, ec);
	                 //System.out.println("***" + result);
	            } else if (n instanceof ProcedureDecl) {
	            	ProcedureDecl pd= (ProcedureDecl) n;
	            	
	            	// vj: This doesnt make sense, need to set up effects separately, just like return types of methods.
	            	result= effect(pd.body());
	            
	            	Set<Locs> methodClocks = new HashSet<Locs>();
	            	List<AnnotationNode> methodAnnotations = ((X10Ext)pd.body().ext()).annotations();
	        		for (AnnotationNode an: methodAnnotations) {
	      	  			if (an.toString().contains("clocked.Clocked")) { /* FIXME */
	              				X10ParsedClassType anc = (X10ParsedClassType)an.annotationType().type();
	              				Expr e = anc.propertyInitializer(0);
	                  	        	Locs locs= computeLocFor(e, ec);
	                  	        	methodClocks.add(locs);
	      	  			}
	        		}
	         

	        		
	        for (Locs mc: result.mustClockSet()) {
	      	  	boolean found = false;
	        		for (Locs rc: methodClocks) {
	        			if (mc.equals(rc)) {
	        				found = true;
	        				break;
	        			}
	        		}
	        	if (found == false)
	        			ec.emitMessage( mc + " is not clocked on the method " + pd.name(), pd.position());
	        }	
	         
	            	if (result == null) {
	            		ec.emitMessage("Unable to compute effects of method " + pd.name(), pd.position());
	            	} else if (result.unsafe()) {
	            		ec.emitMessage("Method " + pd.name() + " is unsafe:"+ result, pd.position());
	            	} else if (isMainMethod(pd, ec) && !result.safe()) {
	            		ec.emitMessage("Main method is not safely parallelized; effect is: " + result, pd.position());
	            	} else {
				       System.out.println(pd.position() + ": Method " + pd.name() + " is "+ result);
	            	}

	            } else if (n instanceof SettableAssign) {
	                result=computeEffect((SettableAssign) n, ec);
	            } else if (n instanceof Unary) {
	                 result=computeEffect((Unary) n, ec);
	            } else if (n instanceof While) {
	            	// vj: this is odd.
	                //result = Effects.makeUnsafe();
	            	result  = computeEffect((While)n, ec);
	            } else if (n instanceof Loop) {
	            	result = computeEffect((Loop) n, ec); 
	            
	            } else {
	                //System.out.println(n);
	                result = Effects.makeSafe();
		        }
	            return node().ext(effect(result));

	        } catch (XFailure f) {
	        	throw new InternalCompilerError(f.getMessage(), n.position(), f);
	        }
	}


	private boolean isMainMethod(ProcedureDecl pd, EffectComputer ec) {
		if (!(pd instanceof MethodDecl)) return false;

		MethodDecl md= (MethodDecl) pd;

		return md.name().id().toString().equals("main") &&
			md.flags().flags().isStatic() &&
			ec.typeSystem().isVoid(md.returnType().type()) &&
			md.formals().size() == 1 &&
			ec.typeSystem().isRail(pd.formals().get(0).type().type());
	}


  private Effect computeEffect(LocalAssign la, EffectComputer ec) throws XFailure {
      Local l= la.local();
     
      X10LocalInstance li= (X10LocalInstance) l.localInstance();
      //System.out.println("in local assign " + li + li.x10Def().type().get());
      Expr rhs= la.right();
      X10TypeSystem ts = ec.typeSystem();
      if (ts.isValVariable(li)) {
    	  return effect(rhs);
      } else {
          Effect rhsEff= effect(rhs);
          Effect writeEff= Effects.makeSafe();
          boolean isClockedVar = analyzeClockedLocal (writeEff, li, l, ec);
          if (!isClockedVar) writeEff.addWrite(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(l))));
          return ts.env(ec.context()).followedBy(rhsEff, writeEff);
      }  
  }

  private Effect computeEffect(FieldAssign fa, EffectComputer ec) throws XFailure {
      Effect result= null;
      X10FieldInstance fi= (X10FieldInstance) fa.fieldInstance();
      Receiver target= fa.target();
      Expr rhs= fa.right();
      //System.out.println("in field assign " + fi + (Type) (fi.x10Def().type().get()));
      X10TypeSystem ts = ec.typeSystem();

      if (ts.isValVariable(fi)) {
		return effect(rhs);
	} else {
          Effect rhsEff= effect(rhs);
          Effect writeEff= Effects.makeSafe();
          if (fa.type().toString().contentEquals("x10.lang.Clock")) {
        	  writeEff.addInitializedClock(Effects.makeFieldLocs(createTermForReceiver(target, ec), new XVarDefWrapper(fi.def())));
          }
          boolean isClockedVar = analyzeClockedField (writeEff, fi, target, ec);
          if (!isClockedVar)
        	  writeEff.addWrite(Effects.makeFieldLocs(createTermForReceiver(target, ec), new XVarDefWrapper(fi.def())));
         
         // System.out.println(writeEff);
          return ec.env().followedBy(rhsEff, writeEff);
      } 
      // return Effects.makeUnsafe();
  }

  private Effect computeEffect(SettableAssign sa, EffectComputer ec) throws XFailure {
      Expr indexExpr = sa.index().get(0);
      Expr arrayExpr = sa.array();
     
      Effect rhsEff= effect(sa.right());
      Effect indexEff= effect(indexExpr);
      Effect arrayEff= effect(arrayExpr);
      Effect writeEff= Effects.makeSafe();
			//System.out.println(arrayExpr);
      writeEff.addWrite(createArrayLoc(arrayExpr, indexExpr));
      X10TypeEnv env = ec.env();
      Effect result= env.followedBy(arrayEff, indexEff);
      boolean isClockedVar = analyzeClockedArrays (result, arrayExpr, indexExpr, ec);
      result= env.followedBy(result, rhsEff);
      if (!isClockedVar)
    	  result= env.followedBy(result, writeEff);
      return result;
  }

  // ============
  // Declarations
  // ============
  private Effect computeEffect(FieldDecl fieldDecl, EffectComputer ec) throws XFailure {
    
     Effect result= Effects.makeParSafe();
     TypeNode t = fieldDecl.type();
     if (t.toString().contentEquals("x10.lang.Clock")) {
    	  if (!fieldDecl.fieldDef().flags().isFinal())
    		  ec.emitMessage("Clock " + fieldDecl.fieldDef().name() + " should be declared final", fieldDecl.position());
    	  else if (fieldDecl.fieldDef().initializer() == null)
    		  ec.emitMessage("Clock " + fieldDecl.fieldDef().name() + " should be initialized", fieldDecl.position());
     }
     return result;
  }
  
  private Effect computeEffect(LocalDecl localDecl, EffectComputer ec) throws XFailure {
      Expr init = localDecl.init();
      Effect result= null;
     TypeNode t = localDecl.type();
     boolean isClockType = t.toString().contentEquals("x10.lang.Clock");
     if (isClockType && !localDecl.localDef().flags().isFinal())
    	 	ec.emitMessage("Clock " + localDecl.localDef().name() + " should be declared final", localDecl.position());
      if (init != null) {
          Effect initEff= effect(init);
          Effect write= Effects.makeSafe();
          write.addWrite(computeLocFor(localDecl));
          result= ec.env().followedBy(initEff, write);
      } else {
    	  if (isClockType) 
    		  ec.emitMessage("Clock " + localDecl.localDef().name() + " should be initialized at declaration", localDecl.position());
      }
      return result;
  }
  
  

  private Locs computeLocFor(VarDecl vd) {
      if (vd instanceof LocalDecl) {
          LocalDecl localDecl = (LocalDecl) vd;
          return Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(localDecl.varDef())));
      }
      throw new UnsupportedOperationException("Don't know how to make a Locs for " + vd);
  }

  // ============
  // Expressions
  // ============
  private Effect computeEffect(Local local, EffectComputer ec) {
      Effect result = null;

      if (! ec.typeSystem().isValVariable(local.localInstance())) {
          result= Effects.makeSafe();
          boolean isClockedVar = analyzeClockedLocal(result, (X10LocalInstance)local.localInstance(), local, ec);
          if (!isClockedVar)
        	  result.addRead(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(local))));
      }
      return result;
  }

  private Effect computeEffect(Field field, EffectComputer ec) {
      Receiver rcvr= field.target();
      Effect result= Effects.makeSafe();
      boolean isClockedVar = analyzeClockedField(result, (X10FieldInstance) field.fieldInstance(), field.target(), ec);
      if (!isClockedVar)
    	  result.addRead(Effects.makeFieldLocs(createTermForReceiver(rcvr, ec), new XVarDefWrapper(field)));
      
      return result;
  }

  private Effect computeEffect(New neew, EffectComputer ec) throws XFailure {
      Effect result= null;
      ConstructorInstance ctorInstance = neew.constructorInstance();
      List<Expr> args = neew.arguments();
      result= computeEffect(args, ec);
      result= ec.env().followedBy(result, getMethodEffects(ctorInstance, args, ec));
      List<FieldInstance> fields = neew.constructorInstance().def().container().get().fields();
      for (FieldInstance fi: fields) {
    	  if (fi.type().toString().contentEquals("x10.lang.Clock"))
    		  result.addInitializedClock(Effects.makeFieldLocs(createTermForReceiver(neew.type(), ec), new XVarDefWrapper(fi.def())));
    	  		//fi.def().initializer();
    	  // computeEffect(fi.def().initializer().container(), ec);
      }
      return result;
  }




private Effect computeEffect(Unary unary, EffectComputer ec) {
      Effect result = null;
      Expr opnd= unary.expr();
      Operator op= unary.operator();
      Effect opEffect = effect(opnd);

      if (op == Unary.BIT_NOT || op == Unary.NEG || op == Unary.NOT || op == Unary.POS) {
          result= opEffect;
      } else {
          // one of the unary inc/dec ops
          Effect write= Effects.makeSafe();
          		computeLocFor(opnd, ec);
          //write.addAtomicInc(computeLocFor(opnd, ec));
          try {
              if (op == Unary.POST_DEC || op == Unary.POST_INC) {
                  result = ec.env().followedBy(opEffect, write);
              } else /*if (op == Unary.PRE_DEC || op == Unary.PRE_INC)*/ {
            	  result = ec.env().followedBy(opEffect, write);
              }
          } catch (Exception f) { /* FIXME */
          	throw new InternalCompilerError(f.getMessage(), unary.position(), f);
          }
      }
      return result;
     
  }

  private Effect computeEffect(Binary binary, EffectComputer ec) throws XFailure {
      Effect result;
      Expr lhs= binary.left();
      Expr rhs= binary.right();
      Effect lhsEff= effect(lhs);
      Effect rhsEff= effect(rhs);

      result= ec.env().followedBy(lhsEff, rhsEff);

      return result;
  }

  private Effect computeEffect(Call call, EffectComputer ec) throws XFailure {
      MethodInstance methodInstance= call.methodInstance();
      StructType methodOwner= methodInstance.container();
      Receiver target = call.target();
      List<Expr> args = call.arguments();
      Effect result= Effects.makeSafe();

      // TODO Perform substitutions of actual parameters for formal parameters
      if (methodOwner instanceof ClassType) {
          ClassType ownerClassType = (ClassType) methodOwner;
          String ownerClassName = ownerClassType.fullName().toString();

          if (ownerClassName.equals("x10.lang.Array") || ownerClassName.equals("x10.lang.Rail")) {
              if (methodInstance.flags().isStatic()) {
                  if (call.name().id().toString().equals("make")) {
                      
                  }
              } else {
                  Expr targetExpr= (Expr) target;
                  if (call.name().id().toString().equals("apply")) {
                	  boolean isClockedVar = analyzeClockedArrays (result, targetExpr, args.get(0), ec);
                	  if (!isClockedVar)
                		  result= computeEffectOfArrayRead(call, targetExpr, args.get(0), ec);
                      
                  } else if (call.name().id().toString().equals("set")) {
                	  boolean isClockedVar = analyzeClockedArrays (result, targetExpr, args.get(0), ec);
                	  if (!isClockedVar)
                		  result= computeEffectOfArrayWrite(call, targetExpr, args.get(0), args.get(1), ec);
                    
                  }
                 
                }
          } else {
              // First compute the effects of argument evaluation
              result= effect(target);
              X10TypeEnv env = ec.env();
              result= env.followedBy(result, computeEffect(args, ec));
              result= env.followedBy(result, getMethodEffects(methodInstance, args, ec));
          }
      }
      
      ec.diag("Effect of call to method " + methodInstance.container() + "." + methodInstance.signature() + ": " + result);
      
      return result;
  }

  private Effect computeEffect(List<Expr> args, EffectComputer ec) throws XFailure {
      Effect result= null;
      for(Expr arg: args) {
          Effect argEff= effect(arg);
          result= ec.env().followedBy(result, argEff);
      }
      return result;
  }

  private Effect computeEffectOfArrayWrite(Call call, Expr array, Expr index, Expr val, EffectComputer ec) {
      			System.out.println("FIXME"); 
      Effect result= Effects.makeSafe();
     System.out.println(array);
     System.out.println(index.type());
      result.addWrite(createArrayLoc(array, index));
      return result;
  }

  private Effect computeEffectOfArrayRead(Call call, Expr array, Expr index, EffectComputer ec) {
      Effect result= Effects.makeSafe();
      result.addRead(createArrayLoc(array, index));
     //  X10LocalInstance li= (X10LocalInstance) l.localInstance();
     // XTerm it = createTermForExpr(index);
    
      return result;
  }
  
  private boolean analyzeClockedField (Effect result, X10FieldInstance fi, Receiver target, EffectComputer ec )  {
	boolean isClockedVar = false;
	if (fi.x10Def().type().get() instanceof AnnotatedType) {
		AnnotatedType at = (AnnotatedType) fi.x10Def().type().get(); 
		for (Type an: at.annotations()) {
  	  			if (an.toString().contains("clocked.Clocked")) { /* FIXME */
          				X10ClassType anc = (X10ClassType) an;
          				Expr e = anc.propertyInitializer(0);
              	        	Locs locs= computeLocFor(e, ec);
              	        	result.addClockedVar(Effects.makeFieldLocs(createTermForReceiver(target, ec), new XVarDefWrapper(fi.def())));
              	        	result.addMustClock(locs);
              	        	isClockedVar = true;
			}
		}
		
	}
	return isClockedVar;
} 
  
private boolean analyzeClockedLocal (Effect result, X10LocalInstance li, Local l, EffectComputer ec) { 
  boolean isClockedVar = false;
  if (li.x10Def().type().get() instanceof AnnotatedType) {
		AnnotatedType at = (AnnotatedType) li.x10Def().type().get(); 
		
		for (Type an: at.annotations()) {
	  			if (an.toString().contains("clocked.Clocked")) { /* FIXME */
				// System.out.println("Here");
        				X10ClassType anc = (X10ClassType) an;
        				Expr e = anc.propertyInitializer(0);
            	        	Locs mc = computeLocFor(e, ec);
            	        	Locs cv = computeLocFor(l, ec);
    				result.addClockedVar(cv);
    				result.addMustClock(mc);
    				isClockedVar = true;
		}
	}
  }
  return isClockedVar;
}  
  
  
  private boolean analyzeClockedArrays (Effect result, Expr array, Expr index, EffectComputer ec) {
	  boolean isClockedVar = false;
	  if (array instanceof Local) {
	  		Local l = (Local) array;
	  		X10LocalInstance li= (X10LocalInstance) l.localInstance();

	  		//System.out.println(li.x10Def().type().get());
	  		ConstrainedType ct = (ConstrainedType) li.x10Def().type().get();
	  		X10ParsedClassType pct = (X10ParsedClassType) ct.baseType().get();
	  		Type it = pct.typeArguments().get(0);
	  		
	  		if (it instanceof AnnotatedType) {
	  				AnnotatedType at = (AnnotatedType) it; 
	  			
	  				for (Type an: at.annotations()) {
	  	      	  			if (an.toString().contains("clocked.Clocked")) { 
	  	              				X10ClassType anc = (X10ClassType) an;
	  	              				Expr e = anc.propertyInitializer(0);
	  	                  	        Locs mc = computeLocFor(e, ec);             
	  	                  	        Locs cv =(createArrayLoc(array, index));
	  	                  	        result.addClockedVar(cv);
	  	                  	        result.addMustClock(mc);
	  	                  	        isClockedVar = true;
	  				}
	  			}
	  		 }
	  	   } 
	  return isClockedVar;
  }
  

  // ============
  // Statements
  // ============
  private Effect computeEffect(If n, EffectComputer ec) throws XFailure {
      Effect condEff= effect(n.cond());
      Effect thenEff= effect(n.consequent());
      Effect elseEff= (n.alternative() != null) ? effect(n.alternative()) : null;
      X10TypeEnv env = ec.env();
      return env.followedBy(env.followedBy(condEff, thenEff), elseEff);
  }

  private Effect computeEffect(Atomic atomic, EffectComputer ec) {
      Effect bodyEff= effect(atomic.body());
      // TODO Should really mark that we've entered an atomic and change the
      // processing of what's inside, rather than trying to do this after the
      // body effects have already been created.
      // TODO Create a means to mark an entire effect atomic to handle atomic blocks properly
      return bodyEff;
  }

  private Effect computeEffect(Async async, EffectComputer ec) {
      Effect bodyEff= effect(async.body());
      Set<Locs> registeredClocks = new HashSet<Locs>(); 
       for (Expr c: async.clocks()) {
                  Locs locs= computeLocFor(c, ec);
		  registeredClocks.add(locs);
       }
      System.out.println(async.position() + ":Body of async is " + bodyEff);
      for (Locs mc: bodyEff.mustClockSet()) {
    	  	boolean found = false;
      		for (Locs rc: registeredClocks) {
      			if (mc.equals(rc)) {
      				found = true;
      				break;
      			}
      		}
      	if (found == false)
      			ec.emitMessage( mc + " is not in registered clocks of async ", async.position());
      }	
        
      return bodyEff.makeParSafe();
  }

  private Effect computeEffect(ForEach fe, EffectComputer ec) {
      Effect bodyEff= effect(fe.body());
      Set<Locs> registeredClocks = new HashSet<Locs>(); 
       for (Expr c: fe.clocks()) {
                  Locs locs= computeLocFor(c, ec);
                  registeredClocks.add(locs);
      }

      for (Locs mc: bodyEff.mustClockSet()) {
    	  	boolean found = false;
      		for (Locs rc: registeredClocks) {
      			if (mc.equals(rc)) {
      				found = true;
      				break;
      			}
      		}
      		if (found == false)
      			ec.emitMessage( mc + " is not in registered clocks of async", fe.position());
      }
      System.out.println(fe.position() + ": ForEach is " + bodyEff);
      return bodyEff.makeParSafe();
  	}

  

  private Effect computeEffect(AtEach ae, EffectComputer ec) {
      Effect bodyEff= effect(ae.body());
      Set<Locs> registeredClocks = new HashSet<Locs>(); 
       for (Expr c: ae.clocks()) {
                  Locs locs= computeLocFor(c, ec);
                  registeredClocks.add(locs);
      }

      for (Locs mc: bodyEff.mustClockSet()) {
    	  	boolean found = false;
      		for (Locs rc: registeredClocks) {
      			if (mc.equals(rc)) {
      				found = true;
      				break;
      			}
      		}
      		if (found == false)
      			ec.emitMessage( mc + " is not in registered clocks of async", ae.position());
      }
      System.out.println(ae.position() + ": ForEach is " + bodyEff);
      return bodyEff.makeParSafe();
  	}
  
  
  

  private Effect computeEffect(Finish f, EffectComputer ec) {
	  Effect bodyEff= effect(f.body());

      return bodyEff.makeSafe();
  }

 /* private Effect computeEffect(ForEach n, EffectComputer ec) {
      Effect bodyEff=effect(n.body());

      return bodyEff.makeParSafe();
  }*/

  private Effect computeEffect(ForLoop forLoop, EffectComputer ec) {
      Effect bodyEff= effect(forLoop.body());
      // Abstract any effects that involve the loop induction variable
      // TODO How to properly bound the domain of the loop induction variable?
      // It isn't quite correct to use universal quantification for that...
      Formal loopVar= forLoop.formal();

      return bodyEff.forall(XTerms.makeLocal(new XVarDefWrapper(loopVar.localDef())));
  }


  private Effect computeEffect(Loop loop, EffectComputer ec) {
      Effect bodyEff= effect(loop.body());
      // Abstract any effects that involve the loop induction variable
      // TODO How to properly bound the domain of the loop induction variable?
      // It isn't quite correct to use universal quantification for that...
      //Formal loopVar= Loop.
      /* FIXME - need to deal with init, cond, etc */
      return bodyEff;
      
  }
  
  
  
  
  Effect computeEffect(Block b, EffectComputer ec) throws XFailure {
      Effect result= null;
      // aggregate effects of the individual statements.
      // prune out the effects on local vars whose scope is this block.
      ec.diag("Computing effect of block " + b);
      List<LocalDecl> blockDecls= collectDecls(b);
 
     
      for(Stmt s: b.statements()) {
          Effect stmtEffect= effect(s);
          //System.out.println(stmtEffect);
          ec.diag("   statement = " + s + "; effect = " + stmtEffect);
          
          Effect filteredEffect= removeLocalVarsFromEffect(blockDecls, stmtEffect, ec);
          ec.diag("             filtered effect = " + filteredEffect);
          result= ec.env().followedBy(result, filteredEffect);
          //System.out.println(stmtEffect + " After: " + result);
          ec.diag("   aggregate effect = " + result);
      }
     //System.out.println("---- Start of Block " + b + result );
      return result;
  }




  private Effect removeLocalVarsFromEffect(List<LocalDecl> decls, Effect effect, EffectComputer ec) {
      Effect result= effect;
      for(LocalDecl ld: decls) {
          XVarDefWrapper localName = new XVarDefWrapper(ld.localDef());
          if (ec.typeSystem().isValVariable(ld)) {
              Expr init= ld.init();
              XTerm initTerm= createTermForExpr(init);
             // result= result.exists(XTerms.makeLocal(localName), initTerm);
         	  //result= result.exists(XTerms.makeLocal(localName));
          } else {
        	  // FIXME 
        	  if (result != null)
        		  result= result.exists(Effects.makeLocalLocs(XTerms.makeLocal(localName)));
        	 // System.out.println(localName + " " + result);
          }
      }
      return result;
  }

  private List<LocalDecl> collectDecls(Block b) {
      List<LocalDecl> result= new LinkedList<LocalDecl>();
      for(Stmt s: b.statements()) {
          if (s instanceof LocalDecl) {
              result.add((LocalDecl) s);
          }
      }        
      return result;
  }
  


 
  private static Effect effect(Node n) {
	  return ((X10Ext) n.ext()).effect();
  }
  
  // ================
  // Locations/Terms
  // ================
  private Locs createArrayLoc(Expr array, Expr index) {
      XTerm arrayTerm = createTermForExpr(array);
      XTerm indexTerm = createTermForExpr(index);
      return Effects.makeArrayElementLocs(arrayTerm, indexTerm);
  }

  private static XTerm createTermForExpr(Expr e) {
      TermCreator tc= new TermCreator(e);
      return tc.getTerm();
  }

  private XTerm createTermForReceiver(Type type, EffectComputer ec) {
		// TODO Auto-generated method stub
	  XTypeTranslator xtt= new XTypeTranslator(ec.typeSystem());
	  if (type instanceof ConstrainedType)
    	  return xtt.trans(((ConstrainedType)type).baseType().get());
		return null;
	}
  
  
  private XTerm createTermForReceiver(Receiver r, EffectComputer ec) {
   
	  XTypeTranslator xtt= new XTypeTranslator(ec.typeSystem());
      // must be a CanonicalTypeNode
     if (r instanceof TypeNode) {
    	TypeNode typeNode= (TypeNode) r;
    	
    	 return xtt.trans(typeNode.type());
    }  else if (r.type() instanceof ConstrainedType)
    	  return xtt.trans(((ConstrainedType)r.type()).baseType().get());
    	
      // (r instanceof Expr) {

       return createTermForExpr((Expr) r);
    
     
    //  throw new UnsupportedOperationException("Can't produce an XTerm for type references.");
  }

  private Locs computeLocFor(Expr expr, EffectComputer ec) {
      if (expr instanceof Local) {
          Local local= (Local) expr;
          LocalLocs ll= Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(local)));
          return ll;
      } else if (expr instanceof Field) {
          Field field= (Field) expr;
          FieldLocs fl= Effects.makeFieldLocs(createTermForReceiver(field.target(), ec), new XVarDefWrapper(field));
          return fl;
      } else if (expr instanceof SettableAssign) {
          SettableAssign sa = (SettableAssign) expr;
          Expr array= sa.array();
          List<Expr> indices= sa.index();
          ArrayElementLocs ael= Effects.makeArrayElementLocs(createTermForExpr(array), createTermForExpr(indices.get(0)));
          return ael;
      } else if (expr instanceof Call) {
          Call call= (Call) expr;
          MethodInstance mi= call.methodInstance();
	      //System.out.println(call.arguments().get(0));
          if (mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.lang.Rail") &&
              mi.name().toString().equals("apply") && mi.formalTypes().size() == 1) { // an array ref
              List<Expr> args= call.arguments();
	   
              return createArrayLoc((Expr) call.target(), args.get(0));
          }
      }
      return null;
  }

  private Effect getMethodEffects(ProcedureInstance procInstance, List<Expr> formalArgs, EffectComputer ec) {
      X10ProcedureInstance xpi= (X10ProcedureInstance) procInstance;
      X10ProcedureDef xpd= (X10ProcedureDef) xpi.def();
      List<Type> annotations= xpd.annotations();
      if (xpd.toString().contains("incr")) {
    	  int x = 1;
      }
      boolean foundAnnotation= false;
      //Effect e = Effects.makeSafe(); 
      Effect e = Effects.makeParSafe();
      
      for (Type annoType : annotations) {
          if (annoType instanceof ClassType) {
              X10ClassType annoClassType = (X10ClassType) annoType;
              String annoName= annoClassType.name().toString();
              if (!annoName.equals("read") && !annoName.equals("write") && !annoName.equals("atomicInc")) {
                  continue;
              }
              List<Expr> declaredLocs= annoClassType.propertyInitializers();
              for(Expr declaredLoc: declaredLocs) {
                  Locs locs= computeLocFor(declaredLoc, ec);

                  if (annoName.equals("read")) {
                      e.addRead(locs);
                  } else if (annoName.equals("write")) {
                      e.addWrite(locs);
                  } else if (annoName.equals("atomicInc")) {
                      e.addAtomicInc(locs);
                  }
              }
              foundAnnotation= true;
          }
      }
      
   //	Set<Locs> methodClocks = new HashSet<Locs>();
	if (xpd instanceof X10MethodDef_c && ((X10MethodDef_c)xpd).bodyAnnotations()!=null)
		for (AnnotationNode an: ((X10MethodDef_c)xpd).bodyAnnotations()) {
			if (an.toString().contains("clocked.Clocked")) { /* FIXME */
  				X10ParsedClassType anc = (X10ParsedClassType)an.annotationType().type();
  				Expr expr = anc.propertyInitializer(0);
  				Locs locs= computeLocFor(expr, ec);
  				int index = -1;
  				if (expr instanceof Local) {
              		Local l = (Local) expr;
              	
              		LocalDef clockDef = l.localInstance().def();
              	
              		for (LocalDef f : xpd.formalNames()) {
              			index++;
              			if (f == clockDef)  {
              				locs = computeLocFor (formalArgs.get(index), ec);
             
              				break;
              			}
              		}
              	}
             
             	e.addMustClock(locs);
			}
	}
      
      return e; // foundAnnotation ? e : Effects.BOTTOM_EFFECT; // return 'bottom' here - don't know what the effects are, so be safe
  }

    
  
}
