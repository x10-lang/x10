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
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;

import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.types.MethodInstance;

/**
 * A simple synthesizer to create a instance call.
 */
public class InstanceCallSynth extends AbstractStateSynth implements IStmtSynth, IExprSynth {


    
    Receiver insRef; //the reference to the instance
    Name methodName;
    
    //Default value for other needed information;
    Type methodLocationType;     // the type of the method
    List<TypeNode> typeNodes;  // typeNodes for the method
    List<Type> argTypes; //arguments' type --> If we could reason the args' type from args, the list could be eliminated
    List<Expr> args;     //arguments
    Type returnType;
    
    public InstanceCallSynth(NodeFactory xnf, Context xct, Position pos, Receiver insRef, Name methodName){
        super(xnf, xct, pos);
        this.insRef = insRef;
        this.methodName = methodName;

        methodLocationType = insRef.type(); 
        typeNodes = new ArrayList<TypeNode>();
        argTypes = new ArrayList<Type>();
        args = new ArrayList<Expr>();
        returnType = null; //default null, refer from expression
    }
    
    public InstanceCallSynth(NodeFactory xnf, Context xct, Position pos, Receiver insRef, String methodName){
        this(xnf, xct, pos, insRef, Name.make(methodName));
    }
    
    public InstanceCallSynth(NodeFactory xnf, Context xct, Receiver insRef, String methodName){
        this(xnf, xct, compilerPos, insRef, Name.make(methodName));
    }

    public void setMethodLocationType(Type type){
        methodLocationType = type;
    }
    
    public void addTypeNode(TypeNode tNode){
        typeNodes.add(tNode);
    }
    
    public void setReturnType(Type type){
        returnType = type;
    }
    public void addArgument(Type argType, Expr arg){
        argTypes.add(argType);
        args.add(arg);
    }
    
    public void addArguments(List<Type> argTypes, List<Expr> args){
        assert(argTypes.size() == args.size());
        for(int i = 0; i < argTypes.size(); i++){
            addArgument(argTypes.get(i), args.get(i));
        }
    }
    
    public Expr genExpr() throws SemanticException {

        TypeSystem xts = (TypeSystem) xct.typeSystem();
        
        List<Type> typeArgs = new ArrayList<Type>();
        for (TypeNode t : typeNodes){
            typeArgs.add(t.type());
        }

        MethodInstance mi = xts.findMethod(methodLocationType, 
                                              xts.MethodMatcher(methodLocationType, methodName, typeArgs,
                                                                argTypes, xct));

        //handle return type
        if(returnType == null){ //not set
            returnType = mi.returnType();
        }
        
        Expr result =  xnf.X10Call(pos, insRef, xnf.Id(pos, methodName), typeNodes, args).methodInstance(mi)
                .type(returnType);
        return result;
    }
    
    
    public Stmt genStmt() throws SemanticException {
        return xnf.Eval(pos, genExpr());
    }

}
