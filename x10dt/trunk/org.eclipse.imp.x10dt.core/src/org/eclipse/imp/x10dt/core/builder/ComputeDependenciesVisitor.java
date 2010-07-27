/*******************************************************************************
* Copyright (c) 2008,2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.x10dt.core.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import x10.ast.AnnotationNode;
import x10.ast.Closure;
import x10.ast.DepParameterExpr;
import x10.ast.FunctionTypeNode;
import x10.types.ClosureType_c;
import x10.types.X10ClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.ArrayType;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * A Polyglot visitor that scans for type reference information in order to
 * build a map of compilation unit-level dependencies to be used in compilation.
 * @author rfuhrer
 */
public class ComputeDependenciesVisitor extends ContextVisitor {
    private final Job fJob;
    //private final TypeSystem fTypeSystem;
    private SourceFile fFromFile;
    private Type fFromType;
    private final PolyglotDependencyInfo fDependencyInfo;

    private static boolean DEBUG= false;

    public ComputeDependenciesVisitor(Job job, TypeSystem ts, PolyglotDependencyInfo di) {
    	super(job, ts, ts.extensionInfo().nodeFactory());
        fJob= job;
        fDependencyInfo= di;
        
    }
    private void recordTypeDependency(Type type) {
        if (type.isArray()) {
            ArrayType arrayType= (ArrayType) type;
            type= arrayType.base();
        }
        if (type.isClass()) {
            //if (type instanceof NullableType)
            //    type = ((NullableType) type).base();
            ClassType classType= (ClassType) X10TypeMixin.baseType(type);
            
            if (!isBinary(classType) && !fFromType.typeEquals(classType,this.context)) { 
                fDependencyInfo.addDependency(fFromType, type);
            }
        }
    }
    private boolean isBinary(ClassType classType) {
        // HACK for some reason ParsedClassType's show up even for class files(???)...
        return !(classType instanceof ParsedClassType) || classType.position().file().endsWith(".class");
    }
    @Override
    public NodeVisitor begin() {
        String wsPath= ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();

        String path= fJob.source().path();
        //PORT1.7 don't bother looking for dependencies if we're in jar/zip
        if(path.endsWith(".jar")|| path.endsWith(".zip")) {
//        	if(printOnce) {
//        		// print this out only once, so we don't forget about it.
//        		printOnce=false;   		
//        		String msg = "ComputeDependenciesVisitor - looking in zip/jar file.";
//        		//X10Plugin.getInstance().writeErrorMsg(msg);
//        		String id=X10Plugin.getInstance().getID();
//        		//X10Plugin.getInstance().getLog().log(new Status(IStatus.WARNING,id,msg+" logStatus"));
//        		X10Plugin.getInstance().logException(msg, new Exception("zip/jar file in dependencies"));
//        	}
        	return null;
        }
        if (path.startsWith(wsPath)) {
            path= path.substring(wsPath.length());
        }
        fDependencyInfo.clearDependenciesOf(path);
        return super.begin();
    }
    @Override
    public NodeVisitor enterCall(Node n) throws SemanticException {
    	try {
        if (n instanceof SourceFile) {
            fFromFile= (SourceFile) n;
            if (DEBUG)
        	System.out.println("Scanning file " + fFromFile.position() + " for dependencies.");
//        } else if (n instanceof FunctionTypeNode) { //After type checking, FunctionTypeNodes become CanonicalTypeNodes. Closures are handled below, so no need to handle them here.
//        	FunctionTypeNode function = (FunctionTypeNode) n;
//        	for(Formal formal: function.formals()){
//        		recordTypeDependency(formal.type().type());
//        	}
//        	recordTypeDependency(function.returnType().type());
    	} else if (n instanceof TypeNode) {
            TypeNode typeNode= (TypeNode) n;
            Type type= typeNode.type();
            
            List<Type> types = new ArrayList<Type>();
            extractAllClassTypes(type, types);
            
            for(Type t: types){
            	recordTypeDependency(t);
            }

//            if (type.isClass()){
//            	X10ClassType classType = (X10ClassType) X10TypeMixin.baseType(type);
//            	for(Type param: classType.typeArguments()){
//            		recordTypeDependency(param);
//            	}
//            }
//            if (type instanceof ClosureType_c){
//            	ClosureType_c closure = (ClosureType_c) type;
//            	for(Type formal: closure.argumentTypes()){
//            		recordTypeDependency(formal);
//            	}
//            	recordTypeDependency(closure.returnType());
//            	for(Type thrown:closure.throwTypes()){
//            		recordTypeDependency(thrown);
//            	}
//            } else {
//            	recordTypeDependency(type);
//            }
        } else if (n instanceof Field) {
            Field field= (Field) n;
            Receiver rcvr= field.target();
            Type type= rcvr.type();

            recordTypeDependency(type);
        } else if (n instanceof Call) {
            Call call= (Call) n;
            MethodInstance mi= call.methodInstance();

            recordTypeDependency(mi.container());
        } else if (n instanceof New) {
            New nw= (New) n;
            ConstructorInstance ci= nw.constructorInstance();

            recordTypeDependency(ci.container());
        } else if (n instanceof ClassDecl) {
            ClassDecl classDecl = (ClassDecl) n;
			ClassDef classDef= classDecl.classDef();

			if (classDef != null) {
				fFromType = classDef.asType(); // PORT1.7 classDecl.type()->classDecl.classDef().asType()
				List<ClassType> supers = new ArrayList<ClassType>();
				superTypes(classDef.asType(), supers);
				for(ClassType supert: supers){
					recordTypeDependency(supert);
				}
			}
//			if (classDecl.superClass() != null) {// interfaces have no superclass
//				recordTypeDependency(classDecl.superClass().type());
//			}
//			for (Iterator intfs = classDecl.interfaces().iterator(); intfs
//					.hasNext();) {
//				TypeNode typeNode = (TypeNode) intfs.next();
//
//				recordTypeDependency(typeNode.type());
//            }
        } else if (n instanceof FieldDecl) {
            FieldDecl fieldDecl= (FieldDecl) n;

            recordTypeDependency(fieldDecl.type().type());
        } else if (n instanceof ProcedureDecl) {
            ProcedureDecl procedureDecl= (ProcedureDecl) n;

            for(Iterator iter= procedureDecl.formals().iterator(); iter.hasNext();) {
            	Formal formal= (Formal) iter.next();
            	recordTypeDependency(formal.type().type());
            }
            if (procedureDecl instanceof MethodDecl){
            	MethodDecl method = (MethodDecl) procedureDecl;
            	recordTypeDependency(method.returnType().type());
            }
            for(TypeNode thrown: procedureDecl.throwTypes()){
            	recordTypeDependency(thrown.type());
            }
        } else if (n instanceof AnnotationNode) {
        	Type type = ((AnnotationNode)n).annotationType().type();
        	
        	recordTypeDependency(type);
        }
//        } else if (n instanceof DepParameterExpr){
//        	DepParameterExpr param = (DepParameterExpr) n;
//        	for (Formal formal: param.formals()){
//        		recordTypeDependency(formal.type().type());
//        	}
//        }
    	} catch(Exception e) {
    		//System.err.println(e);
    	} catch(InternalError e){
    		//System.err.println(e);
    		//n.dump(System.err);
    		//System.err.println("here1");
    	}
        return super.enterCall(n);
    }
    

	private void extractAllClassTypes(Type type, List<Type> types) {
		if (!type.isClass())
			return;
		if (type instanceof ClosureType_c){
         	ClosureType_c closure = (ClosureType_c) type;
         	for(Type formal: closure.argumentTypes()){
         		extractAllClassTypes(formal, types);
         	}
         	extractAllClassTypes(closure.returnType(), types);
         	for(Type thrown:closure.throwTypes()){
         		extractAllClassTypes(thrown, types);
         	}	
         	return;
		}
		X10ClassType classType = (X10ClassType) X10TypeMixin.baseType(type);
		if (!types.contains(classType))
			types.add(classType);

		for (Type param : classType.typeArguments())
			extractAllClassTypes(param, types);
	}
	
	private void superTypes(ClassType type, List<ClassType> types){
		Type parentClass = type.superClass();
		if (parentClass != null){
			X10ClassType classType = (X10ClassType) X10TypeMixin.baseType(parentClass);
			if (!types.contains(classType)){
				types.add(classType);
				superTypes(classType, types);
			}
		}
		for(Type inter: type.interfaces()){
			X10ClassType interc = (X10ClassType) X10TypeMixin.baseType(inter);
			if (!types.contains(interc)){
				types.add(interc);
				superTypes(interc, types);
			}
		}
	}
}
