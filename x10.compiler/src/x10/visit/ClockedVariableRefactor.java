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

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Field_c;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Receiver;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.SettableAssign;
import x10.ast.X10Call;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10NodeFactory;
import x10.extension.X10Ext;
import x10.types.AnnotatedType;
import x10.types.X10ClassType;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeSystem;

public class ClockedVariableRefactor extends ContextVisitor {
	
	 
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;
	
	public ClockedVariableRefactor(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	    xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
	}
	
	 private static final QName CLOCKEDVAR = QName.make("x10.compiler.ClockedVar");
	 private static final QName RAIL = QName.make("x10.lang.Rail");
	
	 /*FIXME*/
	 	private boolean isClockedType(Type type) {
	 		if (type instanceof AnnotatedType) {
	 			AnnotatedType at = (AnnotatedType) type;
	 			if (at.annotations().toString().contains("clocked.Clocked")) 
	 			return true;
	 		}
	 		return false;
	 		
	 	}
	 
	    private Call createWrappingCall(Local local) {
	        X10MethodInstance mi;
	        try {
	            Type type = xts.typeForName(CLOCKEDVAR);
	            
	            mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(local.type(), Name.make("get"), Collections.EMPTY_LIST, context));
	        } catch (SemanticException e) {
	            throw new InternalCompilerError("Something is terribly wrong", e);
	        }
	        Call call = xnf.Call(local.position(), local, xnf.Id(local.position(), "get"));
	        call = (Call) call.methodInstance(mi).type(local.type());
	        return call;
	    }

	 
	 
	 private Expr extractClock(Type t) {
		 AnnotatedType at = (AnnotatedType) t;
		 for (Type an : at.annotations()) {
			 if (an instanceof X10ParsedClassType) {
				    Expr p = ((X10ParsedClassType) an).propertyInitializer(0);
				    return p;
			 }
			 
		 }
		 
		 return null;
	 }
	 
	 
	 private Expr extractOp(Type t) {
		 AnnotatedType at = (AnnotatedType) t;
		 for (Type an : at.annotations()) {
			 if (an instanceof X10ParsedClassType) {
				    Expr p = ((X10ParsedClassType) an).propertyInitializer(1);
				    return p;
			 }
			 
		 }
		 
		 return null;
	 }
	 
	 private Expr extractOpInit(Type t) {
		 AnnotatedType at = (AnnotatedType) t;
		 for (Type an : at.annotations()) {
			 if (an instanceof X10ParsedClassType) {
				    Expr p = ((X10ParsedClassType) an).propertyInitializer(2);
				    return p;
			 }
			 
		 }
		 
		 return null;
	 }
	 
	 
	 private Node visitLocalDecl(X10LocalDecl_c n) {
	        if (isClockedType(n.type().type())) {
	            Flags f = n.flags().flags();
	            try {
	            	//n.type().ty
	                Position position = n.position();
	                Expr c = extractClock(n.type().type());
	                Expr op = extractOp(n.type().type());
	                Expr init = extractOpInit(n.type().type());
	                Type type = xts.typeForName(CLOCKEDVAR);
	                
	                List<Type> typeArgs = new ArrayList<Type>();
	                typeArgs.add(n.type().type());
	                
	                List<Expr> args = new ArrayList<Expr>();
	                args.add(c);
	                args.add(op);
	                args.add(init); /* init for the op */
	                args.add(n.init());
	               
	                
	                List<Type> argsType = new ArrayList<Type>();
	                argsType.add(c.type());
	                argsType.add(op.type());
	                argsType.add(init.type());
	                argsType.add(n.type().type());	               

	                X10ClassType type2 = ((X10ClassType) type).typeArguments(typeArgs);
	                Expr construct = xnf.New(position,xnf.CanonicalTypeNode(position, type2), args)
	               
	                .constructorInstance(xts.findConstructor(type2, xts.ConstructorMatcher(n.type().type(), argsType, context)))
	                .type(type2);
	                // clear shared flag and set final flag
	                LocalDecl localDecl = xnf.LocalDecl(position,xnf.FlagsNode(position,X10Flags.toX10Flags(f).clearShared().Final()), xnf.CanonicalTypeNode(position, type2), n.name(), construct) 
	                .localDef(xts.localDef(n.position(), n.flags().flags().Final(), Types.ref(type2), n.name().id()));
	                return localDecl;
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Something is terribly wrong", e);
	            }
	        }
	        return n;
	    }

	 
	 private Node visitFieldDecl(X10FieldDecl_c n) {
	        if (isClockedType(n.type().type())) {
	            Flags f = n.flags().flags();
	            try {
	            	//n.type().ty
	                Position position = n.position();
	                Expr c = extractClock(n.type().type());
	                Expr op = extractOp(n.type().type());
	                Expr init = extractOpInit(n.type().type());
	                Type type = xts.typeForName(CLOCKEDVAR);
	                
	                List<Type> typeArgs = new ArrayList<Type>();
	                typeArgs.add(n.type().type());
	                
	                List<Expr> args = new ArrayList<Expr>();
	                args.add(c);
	                args.add(op);
	                args.add(init); /* init for the op */
	                args.add(n.init());
	               
	                
	                List<Type> argsType = new ArrayList<Type>();
	                argsType.add(c.type());
	                argsType.add(op.type());
	                argsType.add(init.type());
	                argsType.add(n.type().type());	               

	                X10ClassType type2 = ((X10ClassType) type).typeArguments(typeArgs);
	                Expr construct = xnf.New(position,xnf.CanonicalTypeNode(position, type2), args)
	               
	                .constructorInstance(xts.findConstructor(type2, xts.ConstructorMatcher(n.type().type(), argsType, context)))
	                .type(type2);
	                // clear shared flag and set final flag
	                FieldDecl fieldDecl = xnf.FieldDecl(position,xnf.FlagsNode(position,X10Flags.toX10Flags(f).Final()), xnf.CanonicalTypeNode(position, type2), n.name(), construct) 
	                .fieldDef(xts.fieldDef(n.position(), Types.ref(type2), n.flags().flags().Final(), Types.ref(type2), n.name().id()));
	                return fieldDecl;
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Something is terribly wrong", e);
	            }
	        }
	        return n;
	    }

	 
	 
	 
	  private Node visitLocalAssign(X10LocalAssign_c n) {
	        if (isClockedType(n.type())) {
	            Local local = n.local();
	            Position position = n.position();
	            Operator bo = n.operator().binaryOperator();
	            Expr right = n.right();
	            
	            // if b is also shared, a += b -> a += b.get()
	            if (right instanceof X10Local_c) {
	                X10Local_c local2 = (X10Local_c) right;
	                if (isClockedType(local2.type()))
	                	right = createWrappingCall(local2);
	            }
	            
	            X10MethodInstance mi;
	            try {
	                Type type = xts.typeForName(CLOCKEDVAR);
	                List<Type> typeArguments = ((X10ClassType) type).typeArguments();
	                mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(right.type(), Name.make("set"), typeArguments, context));
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Something is terribly wrong", e);
	            }
	            if (bo == null) { // i.e. =         a = b -> a.set(b)
	                return (Call) xnf.Call(position, local, xnf.Id(position, "set"), right).methodInstance(mi).type(right.type());
	            } else {          // i.e. += *= ... a += b -> a.set(a.get() + b)
	                return (Call) xnf.Call(position, local, xnf.Id(position, "set"), xnf.Binary(position, createWrappingCall(local), bo, right))
	                .methodInstance(mi).type(right.type());
	            }
	        }
	        return n;
	    }
	 
	
	 
	  
	  private Node visitFieldAssign(X10FieldAssign_c n) {
		  	Type fieldType = ((X10FieldInstance)n.fieldInstance()).x10Def().type().get();
	        if (isClockedType(fieldType)) {
	        	
	            Receiver field = n.left(xnf);
	            Position position = n.position();
	            Operator bo = n.operator().binaryOperator();
	            Expr right = n.right();
	       
	            
	            // if b is also shared, a += b -> a += b.get()
	           /* if (right instanceof X10Local_c) {
	                X10Local_c local2 = (X10Local_c) right;
	                if (isClockedType(local2.type()))
	                	right = createWrappingCall(local2);
	            }*/
	            
	            X10MethodInstance mi;
	        
	            try {
	                Type type = xts.typeForName(CLOCKEDVAR);
	                List<Type> typeArguments = ((X10ClassType) type).typeArguments();
	                mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(right.type(), Name.make("set"), typeArguments, context));
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Something is terribly wrong", e);
	            }
	            if (bo == null) { // i.e. =         a = b -> a.set(b)
	                return (Call) xnf.Call(position, field, xnf.Id(position, "set"), right).methodInstance(mi).type(right.type());
	            } else {          // i.e. += *= ... a += b -> a.set(a.get() + b)
	            	System.out.println("FIXME");
	               /* return (Call) xnf.Call(position, field, xnf.Id(position, "set"), xnf.Binary(position, createWrappingCall(local), bo, right))
	                .methodInstance(mi).type(right.type());*/ // FIXME
	            }
	        }
	        return n;
	    }
	  
	  
	  private Node visitLocal(X10Local_c local) {
		   X10MethodInstance mi;
           try {
               Type type = xts.typeForName(CLOCKEDVAR);
              
               mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(local.type(), Name.make("get"), Collections.EMPTY_LIST, context));
           } catch (SemanticException e) {
        	   throw new InternalCompilerError("Something is terribly wrong", e);
           }
        	   Position position = local.position();
        	   return (Call) xnf.Call(position, local, xnf.Id(position, "get"), local).methodInstance(mi).type(local.type());
        	  // return createWrappingCall(local);
           }
	  
	  private Node visitField(X10Field_c field) {
		  if (isClockedType (field.fieldInstance().def().type().get())) {
			 X10MethodInstance mi;
          	try {
              Type type = xts.typeForName(CLOCKEDVAR);
             
              mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(field.type(), Name.make("get"), Collections.EMPTY_LIST, context));
          	} catch (SemanticException e) {
          		throw new InternalCompilerError("Something is terribly wrong", e);
          	}
       	   	Position position = field.position();
     
       	   	return (Call) xnf.Call(position, field, xnf.Id(position, "get")).methodInstance(mi).type(field.type());
		  }
		  return field;
       	  // return createWrappingCall(local);
          }
	  
	  
	  
	    
	 private Node visitMake(X10Call call) {	
		 
				for (TypeNode tn: call.typeArguments())
					if (this.isClockedType(tn.type())) {
						Receiver target = call.target();
						Type type;
						X10MethodInstance mi;
						List<Expr> args;
						try {
							Expr c = this.extractClock(tn.type());
							Expr op = this.extractOp(tn.type());
							Expr opInit = this.extractOpInit(tn.type());
							type = xts.typeForName(RAIL);
						
							
							List<Type> typeArguments = ((X10ClassType) type).typeArguments();
							List<Type> typeArgs = new ArrayList<Type>();
							for (TypeNode callTypeArgs: call.typeArguments()) {
								typeArgs.add(callTypeArgs.type());
							}
							//typeArgs.add(op.type());
							//System.out.println(op.type());
							
							List<Type> argTypes = new ArrayList<Type>();
							for (Expr arg: call.arguments()) {
								argTypes.add(arg.type());
							}
							argTypes.add(c.type());
							argTypes.add(op.type());
							argTypes.add(opInit.type());
							
					
							
							args = new ArrayList<Expr>();
							for (Expr arg: call.arguments()) {
								args.add(arg);
								
							}
							args.add(c);
							args.add(op);
							args.add(opInit);
							
							mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(call.type(), Name.make("makeClockedRail"), typeArgs, argTypes, context));
							} catch (SemanticException e) {
								throw new InternalCompilerError("Something is terribly wrong", e);
							}
					
					
						return (Call) xnf.Call(call.position(), target,  xnf.Id(call.position(), "makeClockedVar"), args).methodInstance(mi).type(mi.returnType());
					}
		
		return call;
	 }
	 
	 
	 private Node visitApply(X10Call call) {	
			
				if (call.type() instanceof AnnotatedType) {
					Receiver target = call.target();
					Type type;
					X10MethodInstance mi;
					try {
						
						type = xts.typeForName(RAIL);
					
						
						List<Type> typeArguments = ((X10ClassType) type).typeArguments();
						List<Type> typeArgs = new ArrayList<Type>();
						
						typeArgs.add(call.type());
						
						List<Type> argTypes = new ArrayList<Type>();
						for (Expr arg: call.arguments()) {
							argTypes.add(arg.type());
						}
						
						mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(call.type(), Name.make("getClocked"), typeArgs, argTypes, context));
						} catch (SemanticException e) {
							throw new InternalCompilerError("Something is terribly wrong", e);
						}
				
				
					return (Call) xnf.Call(call.position(), target,  xnf.Id(call.position(), "getClocked"), call.arguments()).methodInstance(mi).type(mi.returnType());
				}
	
	return call;
}

	
	 private Node visitSettableAssign(SettableAssign sa) {	
			
		if (sa.type() instanceof AnnotatedType) {
			if (sa.array() instanceof Local) {
				Local array = (Local) sa.array();
				Type type;
				X10MethodInstance mi;
				List<Type> typeArgs = new ArrayList<Type>();
				List<Type> argTypes = new ArrayList<Type>();
				List<Expr> args = new ArrayList<Expr>();
				try {
					
					type = xts.typeForName(RAIL);
					
					typeArgs.add(sa.right().type());
					argTypes.add(sa.index().get(0).type());
					argTypes.add(sa.right().type());
					args.add(sa.index().get(0));
					args.add(sa.right());
					
					mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(sa.methodInstance().returnType(), Name.make("setClocked"), typeArgs, argTypes, context));
					} catch (SemanticException e) {
						throw new InternalCompilerError("Something is terribly wrong", e);
					}
				return (Call) xnf.Call(sa.position(), array,  xnf.Id(sa.position(), "makeClockedVar"), args).methodInstance(mi).type(mi.returnType());
			}	else if (sa.array() instanceof Field) {
					Field array = (Field) sa.array();
					Type type;
					X10MethodInstance mi;
					List<Type> typeArgs = new ArrayList<Type>();
					List<Type> argTypes = new ArrayList<Type>();
					List<Expr> args = new ArrayList<Expr>();
					try {
						
						type = xts.typeForName(RAIL);
						
						typeArgs.add(sa.right().type());
						argTypes.add(sa.index().get(0).type());
						argTypes.add(sa.right().type());
						args.add(sa.index().get(0));
						args.add(sa.right());
						
						mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(sa.methodInstance().returnType(), Name.make("setClocked"), typeArgs, argTypes, context));
						} catch (SemanticException e) {
							throw new InternalCompilerError("Something is terribly wrong", e);
						}
					return (Call) xnf.Call(sa.position(), array,  xnf.Id(sa.position(), "makeClockedVar"), args).methodInstance(mi).type(mi.returnType());
				}	
		}	
		return sa;
}
	 
	 
	 
	public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {

		if (! (n.ext() instanceof X10Ext)) {
			return n;
		}
		
		init();
		
	    if (n instanceof LocalDecl) {
	 
	    	return visitLocalDecl((X10LocalDecl_c) n);	
	    } else if (n instanceof FieldDecl) {
	    	 return visitFieldDecl((X10FieldDecl_c)n);
	    } else if (n instanceof LocalAssign) {
	    	return visitLocalAssign((X10LocalAssign_c)n);
	    } else if (n instanceof FieldAssign) {
	    	return visitFieldAssign((X10FieldAssign_c)n);
	    	
	    } else if (!(parent instanceof X10LocalAssign_c) && n instanceof X10Local_c) {
            X10Local_c local = (X10Local_c) n;
            if(isClockedType(local.localInstance().def().type().get()) ) {
                return createWrappingCall(local);
            }
        } else if (!(parent instanceof X10FieldAssign_c) && n instanceof X10Field_c) {
        	return visitField((X10Field_c)n);
        	
        } else if (n instanceof X10Call) {
        	// Deal with Rail.make and Array.make
        	
        	X10Call call = (X10Call) n;
        	if (call.target().toString().contentEquals("x10.lang.Rail"))
        		if (call.name().toString().contentEquals("make"))
        				return visitMake((X10Call) n);
        		if (call.name().toString().contentEquals("apply"))
        				return visitApply((X10Call) n);
        	
        } else if (n instanceof SettableAssign) {
        	   return visitSettableAssign((SettableAssign) n);
        	
        }
	    
	    
 		List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
		
		for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext(); ) {
			AnnotationNode a = i.next(); 
			X10ClassType at = a.annotationInterface();
			if (n instanceof TypeNode && ! at.isSubtype(TA, context)) {
				throw new SemanticException("Annotations on types must implement " + TA, n.position());
			}
			
		}
		
		return n;
	}
	
	ClassType A, TA, EA, SA, MA, FA, CA, IA, PA;
	
	public void init() throws SemanticException {
		if (A != null) return;
		TA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.TypeAnnotation"));
		EA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.ExpressionAnnotation"));
		SA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.StatementAnnotation"));
		MA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.MethodAnnotation"));
		FA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.FieldAnnotation"));
		CA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.ClassAnnotation"));
		IA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.ImportAnnotation"));
		PA = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.PackageAnnotation"));
		A  = (ClassType) ts.systemResolver().find(QName.make("x10.lang.annotations.Annotation"));
	}
}
