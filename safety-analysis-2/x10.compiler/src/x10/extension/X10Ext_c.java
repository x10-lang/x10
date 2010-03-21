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
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.ast.VarDecl;
import polyglot.ast.While;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
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
		    } else if (n instanceof Finish) {
	                result=computeEffect((Finish) n, ec);
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
	            } else if (n instanceof ProcedureDecl) {
	            	ProcedureDecl pd= (ProcedureDecl) n;
		
	            	// vj: This doesnt make sense, need to set up effects separately, just like return types of methods.
	            	result= effect(pd.body());

	            	if (result == null) {
	            		ec.emitMessage("Unable to compute effects of method " + pd.name(), pd.position());
	            	} else if (result.unsafe()) {
	            		ec.emitMessage("Method " + pd.name() + " is unsafe:"+ result, pd.position());
	            	} else if (isMainMethod(pd, ec) && !result.safe()) {
	            		ec.emitMessage("Main method is not safely parallelized; effect is: " + result, pd.position());
	            	} else {
				//  System.out.println("Method " + pd.name() + " is "+ result);
			}

	            } else if (n instanceof SettableAssign) {
	                result=computeEffect((SettableAssign) n, ec);
	            } else if (n instanceof Unary) {
	                 result=computeEffect((Unary) n, ec);
	            } else if (n instanceof While) {
	            	// vj: this is odd.
	                result = Effects.makeUnsafe();
	            } else {
			/* nv: FIXME */
			/* System.out.println(n);
				if (n instanceof AnnotationNode)
					System.out.println("AnnotationNode");
				if (n instanceof FieldDecl)
					System.out.println("FieldDecl"); */
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
      System.out.println("in local assign " + li + li.x10Def().type().get());
      Expr rhs= la.right();
      X10TypeSystem ts = ec.typeSystem();
      if (ts.isValVariable(li)) {
    	  return effect(rhs);
      } else {
          Effect rhsEff= effect(rhs);
          Effect writeEff= Effects.makeSafe();
         if (li.x10Def().type().get() instanceof AnnotatedType) {
			AnnotatedType at = (AnnotatedType) li.x10Def().type().get(); 
			
			for (Type an: at.annotations()) {
      	  			if (an.toString().contains("clocked.Clocked")) { /* FIXME */
					System.out.println("Here");
              				X10ClassType anc = (X10ClassType) an;
					Expr e = anc.propertyInitializer(0);
                  	        	Locs mc = computeLocFor(e, ec);
                  	        	Locs cv = computeLocFor(l, ec);
          				writeEff.addClockedVar(cv);
          				writeEff.addMustClock(mc);
			}
		}
	}
	writeEff.addWrite(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(l))));
          return ts.env(ec.context()).followedBy(rhsEff, writeEff);
      }  
  }

  private Effect computeEffect(FieldAssign fa, EffectComputer ec) throws XFailure {
      Effect result= null;
      X10FieldInstance fi= (X10FieldInstance) fa.fieldInstance();
      Receiver target= fa.target();
      Expr rhs= fa.right();
    
      System.out.println("in field assign " + fi + (Type) (fi.x10Def().type().get()));
      X10TypeSystem ts = ec.typeSystem();

      if (ts.isValVariable(fi)) {
		return effect(rhs);
	} else {
          Effect rhsEff= effect(rhs);
          Effect writeEff= Effects.makeSafe();
          writeEff.addWrite(Effects.makeFieldLocs(createTermForReceiver(target, ec), new XVarDefWrapper(fi.def())));
		if (fi.x10Def().type().get() instanceof AnnotatedType) {
			AnnotatedType at = (AnnotatedType) fi.x10Def().type().get(); 
			for (Type an: at.annotations()) {
      	  			if (an.toString().contains("clocked.Clocked")) { /* FIXME */
              				X10ClassType anc = (X10ClassType) an;
					Expr e = anc.propertyInitializer(0);
                  	        	Locs locs= computeLocFor(e, ec);
          				writeEff.addClockedVar(Effects.makeFieldLocs(createTermForReceiver(target, ec), new XVarDefWrapper(fi.def())));
          				writeEff.addMustClock(locs);
				}
			}
			
		}
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
     if (arrayExpr instanceof Local) {
	Local l = (Local) arrayExpr;
	X10LocalInstance li= (X10LocalInstance) l.localInstance();

	System.out.println(li.x10Def().type().get());
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
                  	        	Locs cv = computeLocFor(l, ec);
          				result.addClockedVar(cv);
          				result.addMustClock(mc);
			}
		}
	}
     } 

      result= env.followedBy(result, rhsEff);
      result= env.followedBy(result, writeEff);
      return result;
  }

  // ============
  // Declarations
  // ============
  private Effect computeEffect(LocalDecl localDecl, EffectComputer ec) throws XFailure {
      Expr init = localDecl.init();
      Effect result= null;

      if (init != null) {
          Effect initEff= effect(init);
          Effect write= Effects.makeSafe();
          write.addWrite(computeLocFor(localDecl));
          result= ec.env().followedBy(initEff, write);
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
          result.addRead(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(local))));
      }
      return result;
  }

  private Effect computeEffect(Field field, EffectComputer ec) {
      Receiver rcvr= field.target();
      Effect result= Effects.makeSafe();

      result.addRead(Effects.makeFieldLocs(createTermForReceiver(rcvr, ec), new XVarDefWrapper(field)));
      return result;
  }

  private Effect computeEffect(New neew, EffectComputer ec) throws XFailure {
      Effect result= null;
      ConstructorInstance ctorInstance = neew.constructorInstance();
      List<Expr> args = neew.arguments();

      result= computeEffect(args, ec);
      result= ec.env().followedBy(result, getMethodEffects(ctorInstance, ec));
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
      Effect result= null;

      // TODO Perform substitutions of actual parameters for formal parameters
      if (methodOwner instanceof ClassType) {
          ClassType ownerClassType = (ClassType) methodOwner;
          String ownerClassName = ownerClassType.fullName().toString();

          if (ownerClassName.equals("x10.lang.Array")) {
              if (methodInstance.flags().isStatic()) {
                  if (call.name().id().toString().equals("make")) {
                      
                  }
              } else {
                  Expr targetExpr= (Expr) target;
                  if (call.name().id().toString().equals("apply")) {
                      result= computeEffectOfArrayRead(call, targetExpr, args.get(0), ec);
                  } else if (call.name().id().toString().equals("set")) {
                      result= computeEffectOfArrayWrite(call, targetExpr, args.get(0), args.get(1), ec);
                  }
              }
          } else {
              // First compute the effects of argument evaluation
              result= effect(target);
              X10TypeEnv env = ec.env();
              result= env.followedBy(result, computeEffect(args, ec));
              result= env.followedBy(result, getMethodEffects(methodInstance, ec));
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

      for (Locs mc: bodyEff.mustClockSet()) {
	boolean found = false;
      	for (Locs rc: registeredClocks) {
		if (mc.equals(rc)) {
			found = true;
			break;
		}
	}
	if (found == false)
	        ec.emitMessage( mc + " is not in registeredClocks " + registeredClocks, async.position());
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
	        ec.emitMessage( mc + " is not in registeredClocks " + registeredClocks, fe.position());
      }		
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

  private Effect computeEffect(Block b, EffectComputer ec) throws XFailure {
      Effect result= null;
      // aggregate effects of the individual statements.
      // prune out the effects on local vars whose scope is this block.
      ec.diag("Computing effect of block " + b);
      List<LocalDecl> blockDecls= collectDecls(b);
      for(Stmt s: b.statements()) {
          Effect stmtEffect= effect(s);
          ec.diag("   statement = " + s + "; effect = " + stmtEffect);
          Effect filteredEffect= removeLocalVarsFromEffect(blockDecls, stmtEffect, ec);
          ec.diag("             filtered effect = " + filteredEffect);
          result= ec.env().followedBy(result, filteredEffect);
          ec.diag("   aggregate effect = " + result);
      }
      return result;
  }




  private Effect removeLocalVarsFromEffect(List<LocalDecl> decls, Effect effect, EffectComputer ec) {
      Effect result= effect;
      for(LocalDecl ld: decls) {
          XVarDefWrapper localName = new XVarDefWrapper(ld.localDef());
          if (ec.typeSystem().isValVariable(ld)) {
              Expr init= ld.init();
              XTerm initTerm= createTermForExpr(init);
              result= result.exists(XTerms.makeLocal(localName), initTerm);
          } else {
              // FIXME result= result.exists(Effects.makeLocalLocs(XTerms.makeLocal(localName)));
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

  private XTerm createTermForReceiver(Receiver r, EffectComputer ec) {
      if (r instanceof Expr) {
          return createTermForExpr((Expr) r);
      }
      // must be a CanonicalTypeNode
      CanonicalTypeNode typeNode= (CanonicalTypeNode) r;
      XTypeTranslator xtt= new XTypeTranslator(ec.typeSystem());
      return xtt.trans(typeNode.type());
//    throw new UnsupportedOperationException("Can't produce an XTerm for type references.");
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
	      System.out.println(call.arguments().get(0));
          if (mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.lang.Rail") &&
              mi.name().toString().equals("apply") && mi.formalTypes().size() == 1) { // an array ref
              List<Expr> args= call.arguments();
	   
              return createArrayLoc((Expr) call.target(), args.get(0));
          }
      }
      return null;
  }

  private Effect getMethodEffects(ProcedureInstance procInstance, EffectComputer ec) {
      X10ProcedureInstance xpi= (X10ProcedureInstance) procInstance;
      X10ProcedureDef xpd= (X10ProcedureDef) xpi.def();
      List<Type> annotations= xpd.annotations();
      boolean foundAnnotation= false;
      Effect e= Effects.makeParSafe();

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
      return e; // foundAnnotation ? e : Effects.BOTTOM_EFFECT; // return 'bottom' here - don't know what the effects are, so be safe
  }

    
  
}
