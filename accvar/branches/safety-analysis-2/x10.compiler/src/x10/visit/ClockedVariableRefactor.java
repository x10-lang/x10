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
import polyglot.ast.Initializer_c;
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
import polyglot.types.InitializerDef_c;
import polyglot.types.Name;
import polyglot.types.ParsedClassType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.ClassDef;
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
import x10.ast.X10New;
import x10.extension.X10Ext;
import x10.types.AnnotatedType;
import x10.types.ConstrainedType;
import x10.types.ConstrainedType_c;
import x10.types.X10ClassType;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10InitializerDef;
import x10.types.X10LocalDef;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10ParsedClassType_c;
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
	 private static final QName CLOCKEDINT = QName.make("x10.compiler.ClockedInt");
	 private static final QName CLOCKEDOPLESSVAR = QName.make("x10.compiler.ClockedOpLessVar");
	 private static final QName RAIL = QName.make("x10.lang.Rail");
	 private static final QName ARRAY = QName.make("x10.array.Array");
	 private static final QName CLOCKEDARRAY = QName.make("x10.array.ClockedArray");
	
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
		Type type;
		Type retType;
	        try {
	            type = xts.typeForName(CLOCKEDVAR);
		    retType = local.type();
	            mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(type, Name.make("getClocked"), Collections.EMPTY_LIST, context));
	        } catch (SemanticException e) {
	            throw new InternalCompilerError("Something is terribly wrong", e);
	        }
	        Call call = xnf.Call(local.position(), local, xnf.Id(local.position(), "getClocked"));
	        call = (Call) call.methodInstance(mi).type(retType);
	   	System.out.println (mi + "  " + mi.flags().isFinal());

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
	 
	 
	 private boolean isOpIntPlus(Expr op) {
		 boolean isPlus = false;
		 boolean  isInt = false;
		 Expr init = null;
		 if (op instanceof Field) {
			init =((X10FieldDef)(((Field) op).fieldInstance()).def()).fieldDecl().init();
		 } else if (op instanceof  Local) {		
			LocalDecl  lDecl = ((X10LocalDef)((Local)op).localInstance().def()).localDecl(); 
			if (lDecl != null)
				init = lDecl.init();
		 }
		 isInt = op.type().toString().contains("=> x10.lang.Int");
		 if (init !=  null) {
			 isPlus = init.toString().contains("x + y");
		 }
		
		 //return false; // Do not use this optimization
		  return isInt && isPlus;
	 }
	 
	 private boolean isNoOp(Expr op) {
		 boolean isNoOp = false;
		 Expr init = null;
		 if (op instanceof Field) {
			init =((X10FieldDef)(((Field) op).fieldInstance()).def()).fieldDecl().init();
		 } else if (op instanceof  Local) {		
			LocalDecl  lDecl = ((X10LocalDef)((Local)op).localInstance().def()).localDecl(); 
			if (lDecl != null)
				init = lDecl.init();
		 }
		 if (init !=  null) {
			 isNoOp = init.toString().contains("noOp");
		 }
		return isNoOp;
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
			Type nType = n.type().type();
			while (nType instanceof AnnotatedType)
				nType = ((AnnotatedType)nType).baseType();
	                argsType.add(nType);	               
	                Type type2;
	                if (this.isOpIntPlus(op))
	                	type2 = xts.typeForName(CLOCKEDINT);
	                else if (this.isNoOp(op)) {
	                	type2 = xts.typeForName(CLOCKEDOPLESSVAR);
	                	type2 = ((X10ClassType) type2).typeArguments(typeArgs);
			}
	                else
	                	type2 = ((X10ClassType) type).typeArguments(typeArgs);
	                Expr construct = xnf.New(position,xnf.CanonicalTypeNode(position, type2), args)
	               
	                .constructorInstance(xts.findConstructor(type2, xts.ConstructorMatcher(type2, argsType, context)))
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

	                Type type2;
	                if (this.isOpIntPlus(op))
	                	type2 = xts.typeForName(CLOCKEDINT);
	                else if (this.isNoOp(op)) {
	                	type2 = xts.typeForName(CLOCKEDOPLESSVAR);
	                	type2 = ((X10ClassType) type2).typeArguments(typeArgs);
			}
	                else
	                	type2 = ((X10ClassType) type).typeArguments(typeArgs);
	                Expr construct = xnf.New(position,xnf.CanonicalTypeNode(position, type2), args)
	               
	                .constructorInstance(xts.findConstructor(type2, xts.ConstructorMatcher(type2, argsType, context)))
	                .type(type2);
	                // clear shared flag and set final flag
	                FieldDecl fieldDecl = xnf.FieldDecl(position,xnf.FlagsNode(position,X10Flags.toX10Flags(f).Final()), xnf.CanonicalTypeNode(position, type2), n.name(), construct) 
	                .fieldDef(xts.fieldDef(n.position(), n.fieldDef().container(), n.flags().flags().Final(), Types.ref(type2), n.name().id()));
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
	                mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(right.type(), Name.make("setClocked"), typeArguments, context));
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Something is terribly wrong", e);
	            }
	            if (bo == null) { // i.e. =         a = b -> a.set(b)
	                Call c =  (Call) xnf.Call(position, local, xnf.Id(position, "setClocked"), right).methodInstance(mi).type(right.type());
			//System.out.println(c);
			return c;
	            } else {          // i.e. += *= ... a += b -> a.set(a.get() + b)
			System.out.println("FIXME");
	                return (Call) xnf.Call(position, local, xnf.Id(position, "setClocked"), xnf.Binary(position, createWrappingCall(local), bo, right))
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
	                mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(right.type(), Name.make("setClocked"), typeArguments, context));
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Something is terribly wrong", e);
	            }
	            if (bo == null) { // i.e. =         a = b -> a.set(b)
	                return (Call) xnf.Call(position, field, xnf.Id(position, "setClocked"), right).methodInstance(mi).type(right.type());
	            } else {          // i.e. += *= ... a += b -> a.set(a.get() + b)
	            	System.out.println("FIXME");
	               /* return (Call) xnf.Call(position, field, xnf.Id(position, "setClocked"), xnf.Binary(position, createWrappingCall(local), bo, right))
	                .methodInstance(mi).type(right.type());*/ // FIXME
	            }
	        }
	        return n;
	    }
	  
	 	  
	  private Node visitField(X10Field_c field) {
		  if (isClockedType (field.fieldInstance().def().type().get())) {
			 X10MethodInstance mi;
          	try {
              Type type = xts.typeForName(CLOCKEDVAR);
             
              mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(type, Name.make("getClocked"), Collections.EMPTY_LIST, context));
          	} catch (SemanticException e) {
          		throw new InternalCompilerError("Something is terribly wrong", e);
          	}
       	   	Position position = field.position();
       	   	return (Call) xnf.Call(position, field, xnf.Id(position, "getClocked")).methodInstance(mi).type(field.type());
		  }
		  return field;
       	  // return createWrappingCall(local);
          }
	  
	  
	 private Node visitNew (X10New neew) {
	   Type type;
	   Type enclosingClass = neew.constructorInstance().container().toType();
	   if (!(enclosingClass instanceof  X10ParsedClassType))
		return neew;
	   if (!(((X10ParsedClassType) enclosingClass).def().toString().contentEquals("x10.array.Array"))) 
		return neew;
	   X10ParsedClassType pct = (X10ParsedClassType) enclosingClass;
	    try {
	  	type = xts.typeForName(CLOCKEDARRAY);
		X10ParsedClassType newType = new X10ParsedClassType_c(type.toClass().def());	
		for (Type t: pct.typeArguments()) {
			if (this.isClockedType(t)) {
				X10MethodInstance mi;
				List<Expr> args;
					
				Expr c = this.extractClock(t);
				Expr op = this.extractOp(t);
				Expr opInit = this.extractOpInit(t);

		
				List<Type> typeArgs = new ArrayList<Type>();
				for (Type ta: pct.typeArguments()) {
					while (ta instanceof AnnotatedType)
						ta = ((AnnotatedType)ta).baseType();
					typeArgs.add(ta);
				}
							
				List<Type> argTypes = new ArrayList<Type>();
				for (Expr arg: neew.arguments()) {
					argTypes.add(arg.type());
				}
				argTypes.add(c.type());
				if (!this.isNoOp(op)) {
					argTypes.add(op.type());
					argTypes.add(opInit.type());
				}
							
				args = new ArrayList<Expr>();
				for (Expr arg: neew.arguments()) {
					args.add(arg);
				}
				args.add(c);
				if (!this.isNoOp(op)) {
					args.add(op);
					args.add(opInit);
				}
				X10ClassType finalType = newType.typeArguments(typeArgs);				
				Expr construct = xnf.New(neew.position(), xnf.CanonicalTypeNode(neew.position(), finalType), args)
	                	.constructorInstance(xts.findConstructor(finalType, xts.ConstructorMatcher(newType, argTypes, context)))
	                	.type(finalType);
				return (X10New) construct;
		 }
	       }	
	      } catch (SemanticException e) {
		throw new InternalCompilerError("Something is terribly wrong", e);
	   }
						
	      return neew;					
				


	} 
	    
	 private Node visitMake(X10Call call) {	
		 Type type;
			try {
				if (call.target().toString().contentEquals("x10.lang.Rail")) {
					type = xts.typeForName(RAIL);	
				}
				else if (call.target().toString().contentEquals("x10.lang.Array")) 
					type = xts.typeForName(ARRAY);
				else 
					return call;
				for (TypeNode tn: call.typeArguments())
					if (this.isClockedType(tn.type())) {
						Receiver target = call.target();
						
						X10MethodInstance mi;
						List<Expr> args;
					
							Expr c = this.extractClock(tn.type());
							Expr op = this.extractOp(tn.type());
							Expr opInit = this.extractOpInit(tn.type());
						
						
						
							
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
							String mName;
							System.out.println("here" + isNoOp(op));
							 if (this.isOpIntPlus(op))
				                			mName = "makeIntClocked";
							 else if (this.isNoOp(op))
								 	mName = "makeOpLessClocked";
							 else
								 	mName = "makeClocked";
							
							mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(call.type(), Name.make(mName), typeArgs, argTypes, context));
							
					
						return (Call) xnf.Call(call.position(), target,  xnf.Id(call.position(), mName), args).methodInstance(mi).type(mi.returnType());
					}
			} catch (SemanticException e) {
				throw new InternalCompilerError("Something is terribly wrong", e);
			}
	
		
		return call;
	 }
	 
	 
	 private Node visitApply(X10Call call) {	
				if (call.type() instanceof AnnotatedType) 	{
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
		 Type type;
		  List<Type> typeArgs = new ArrayList<Type>();
		
		try {
			
			if (!(sa.array().type() instanceof ConstrainedType))
				return sa;
			ConstrainedType ct = (ConstrainedType)sa.array().type();
			if (!(ct.baseType().get() instanceof  X10ParsedClassType))
				return sa;
			
			if (((X10ParsedClassType) ct.baseType().get()).def().toString().contentEquals("x10.lang.Rail")) {
				type = xts.typeForName(RAIL);
				typeArgs.add(sa.right().type());
			
			}
			/*else if (((X10ParsedClassType)((ConstrainedType)sa.array().type()).baseType().get()).def().toString().contentEquals("x10.lang.Array"))
				type = xts.typeForName(ARRAY);*/
	
			else
				return sa;

		if (sa.type() instanceof AnnotatedType) {
			if (sa.array() instanceof Local) {
				Local array = (Local) sa.array();
				
				X10MethodInstance mi;
				
			
				
				List<Type> argTypes = new ArrayList<Type>();
				List<Expr> args = new ArrayList<Expr>();
			
			
				
				for (Expr index: sa.index())
						argTypes.add(index.type());
				argTypes.add(sa.right().type());
				for (Expr index: sa.index())
							args.add(index);
				args.add(sa.right());
					
				
				
				
				mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(sa.methodInstance().returnType(), Name.make("setClocked"), typeArgs, argTypes, context));
				
				return (Call) xnf.Call(sa.position(), array,  xnf.Id(sa.position(), "setClocked"), args).methodInstance(mi).type(mi.returnType());
			}	else if (sa.array() instanceof Field) {
					Field array = (Field) sa.array();
					
					X10MethodInstance mi;
								
					List<Type> argTypes = new ArrayList<Type>();
					List<Expr> args = new ArrayList<Expr>();		
				
						
					
						for (Expr index: sa.index())
								argTypes.add(index.type());
						argTypes.add(sa.right().type());
						for (Expr index: sa.index())
									args.add(index);
						args.add(sa.right());
						
						mi = (X10MethodInstance) xts.findMethod(type, xts.MethodMatcher(sa.methodInstance().returnType(), Name.make("setClocked"), typeArgs, argTypes, context));
					
					
					return (Call) xnf.Call(sa.position(), array,  xnf.Id(sa.position(), "setClocked"), args).methodInstance(mi).type(mi.returnType());
				}	
			}	
		} catch (SemanticException e) {
			throw new InternalCompilerError("Something is terribly wrong", e);
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
	
	} else if (n instanceof X10New) {
		return visitNew((X10New) n);

        } else if (n instanceof X10Call) {
        	// Deal with Rail.make and Array.make
        	
        	X10Call call = (X10Call) n;

        		if (call.name().toString().contentEquals("make"))
        				return visitMake((X10Call) n);
        
			
			if (call.target().type() instanceof ConstrainedType) {
        		Type t = ((ConstrainedType)call.target().type()).baseType().get();
        		if (t instanceof ParsedClassType) {
        			ParsedClassType pct = (ParsedClassType) t;
        			if(pct.def().toString().contentEquals("x10.lang.Rail"))
        				if (call.name().toString().contentEquals("apply"))
        					return visitApply((X10Call) n);
        	     }
			}
        	
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
