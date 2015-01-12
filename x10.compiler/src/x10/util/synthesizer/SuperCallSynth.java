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
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.types.Context;

/**
 * An simple synthesizer to create a super call.
 */
public class SuperCallSynth extends AbstractStateSynth implements IStmtSynth {


    

    ClassDef classDef;
    //Default value for other needed information;
    List<TypeNode> typeNodes;  // typeNodes for the method
    List<Type> argTypes; //arguments' type --> If we could reason the args' type from args, the list could be eliminated
    List<Expr> args;     //arguments
    
    public SuperCallSynth(NodeFactory xnf, Context xct, Position pos, ClassDef classDef,
                          List<TypeNode> typeNodes, List<Type> argTypes, List<Expr> args){
        super(xnf, xct, pos);
        this.classDef = classDef;
        this.typeNodes = typeNodes;
        this.argTypes = argTypes;
        this.args = args;
    }
    
    public SuperCallSynth(NodeFactory xnf, Context xct, Position pos, ClassDef classDef){
        this(xnf, xct, pos, classDef,
             new ArrayList<TypeNode>(), new ArrayList<Type>(), new ArrayList<Expr>());
    }
    
    public SuperCallSynth(NodeFactory xnf, Context xct, ClassDef classDef){
        this(xnf, xct, compilerPos, classDef);
    }

    
    public void addTypeNode(TypeNode tNode){
        typeNodes.add(tNode);
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
    
    public Stmt genStmt() throws SemanticException {
        // Find the right super constructor: def (args)
        Type sType = classDef.superType().get();
        
        ConstructorInstance ci = xts.findConstructor(sType,    // receiver's type
                xts.ConstructorMatcher(sType, 
                        argTypes,  // constraint's type (!)
                        xct));

        return xnf.SuperCall(pos, args).constructorInstance(ci);
     
    }


}
