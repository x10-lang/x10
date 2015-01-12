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
package x10.util.synthesizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.ast.AnnotationNode;
import x10.extension.X10Del;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.types.X10ClassType;
import x10.types.checker.PlaceChecker;

/**
 * An simple synthesizer to create a new instance: new classType
 *
 */
public class NewInstanceSynth extends AbstractStateSynth implements IStmtSynth, IExprSynth {

    ClassType classType;     //The new instance's type
    
    //Default value for other needed information;
    
    List<AnnotationNode> annotations;  // annotations of the new instance
    List<Type> argTypes; //arguments' type --> If we could reason the args' type from args, the list could be eliminated
    List<Expr> args;     //arguments
    
    public NewInstanceSynth(NodeFactory xnf, Context xct, Position pos, ClassType classType){
        super(xnf, xct, pos);
        this.classType = classType;

        annotations = new ArrayList<AnnotationNode>();
        argTypes = new ArrayList<Type>();
        args = new ArrayList<Expr>();
    }
    
    public void addAnnotation(AnnotationNode annotation){
        annotations.add(annotation);
    }
    
    public void addArgument(Type argType, Expr arg){
        argTypes.add(argType);
        args.add(arg.type(argType));
    }
    
    public void addArguments(List<Type> argTypes, List<Expr> args){
        assert(argTypes.size() == args.size());
        for(int i = 0; i < argTypes.size(); i++){
            addArgument(argTypes.get(i), args.get(i));
        }
    }
    
    
    public Expr genExpr() throws SemanticException {

        TypeSystem xts = (TypeSystem) xct.typeSystem();

        List<Type> typeArgs = ((X10ClassType)classType).typeArguments();
        if (typeArgs == null) {
            typeArgs = Collections.<Type>emptyList();
        }
        

        ConstructorInstance constructorIns = xts.findConstructor(classType, // receiver's
                                                                 // type
                                                                 xts.ConstructorMatcher(classType, typeArgs, argTypes, xct));
//        ConstructorDef constructorDef = constructorIns.def();
        
//        //need set formals to the constructorIns
//        List<Type> formalTypes = new ArrayList<Type>();
//        for(Ref<? extends Type> r : constructorDef.formalTypes()){
//            formalTypes.add(r.get());
//        }
//        constructorIns = constructorIns.formalTypes(formalTypes);
//        constructorIns.typeParameters(typeArgs);
        
        New aNew = xnf.New(pos, xnf.CanonicalTypeNode(pos, Types.ref(classType)), args);
        
       // aNew.qualifier(qualifier);
        Ref<? extends ClassDef> outerRef = classType.def().outer();
        if(outerRef != null && classType.def().isInnerClass()){
            ClassDef outerDef = outerRef.get();
            Expr q = xnf.This(Position.COMPILER_GENERATED,
                        xnf.CanonicalTypeNode(Position.COMPILER_GENERATED, outerDef.asType())).type(outerDef.asType());
            aNew = aNew.qualifier(q);
        }
        
        // Add annotations to New
        if (annotations.size() > 0) {
            aNew = (New) ((X10Del) aNew.del()).annotations(annotations);
        }
        Expr construct = aNew.constructorInstance(constructorIns).type(classType); // PlaceChecker.AddIsHereClause(classType, xct));            
        return construct;
    }
    
    public Stmt genStmt() throws SemanticException {
        return xnf.Eval(pos, genExpr());
    }
   
}
