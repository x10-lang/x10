/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util.synthesizer;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.ast.Closure;
import x10.ast.Tuple;
import polyglot.types.Context;

/**
 * Some codes based on desugar
 */
public class AsyncSynth extends AbstractStateSynth implements IStmtSynth {

    Stmt body;
    List<Expr> clocks;
    Expr place;
    
    public AsyncSynth(NodeFactory xnf, Context xct, Position pos,
                      Stmt body, List<Expr> clocks, Expr place) {
        super(xnf, xct, pos);
        this.body = body;
        this.clocks = clocks;
        this.place = place;
    }
    
    public AsyncSynth(NodeFactory xnf, Context xct, Position pos,
                      Stmt body, Expr place) {
        super(xnf, xct, pos);
        this.body = body;
        this.clocks = new ArrayList<Expr>();
        this.place = place;
    }
    
    public AsyncSynth(NodeFactory xnf, Context xct, Position pos,
                      Stmt body, List<Expr> clocks) {
        super(xnf, xct, pos);
        this.body = body;
        this.clocks = clocks;
    }
    
    public AsyncSynth(NodeFactory xnf, Context xct, Position pos,
                      Stmt body) {
        super(xnf, xct, pos);
        this.body = body;
        this.clocks = new ArrayList<Expr>();
    }
    

    public Stmt genStmt() throws SemanticException {
        
        //different situations
        List<Expr> exprs = new ArrayList<Expr>();
        List<Type> types = new ArrayList<Type>();
        
        
        if(place == null){
            if(clocks.size() > 0){
                Type clockRailType = xts.Rail(xts.Clock());
                Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);
                exprs.add(clockRail);
                types.add(clockRailType);
            }
        }
        else{ //place != null;
            //process places
            exprs.add(place);
            types.add(xts.Place());
            
            if(clocks.size() > 0){
                Type clockRailType = xts.RegionArray(xts.Clock());
                Tuple clockRail = (Tuple) xnf.Tuple(pos, clocks).type(clockRailType);
                exprs.add(clockRail);
                types.add(clockRailType);
            }
        }

        System.out.println(xct.currentCode());
        Closure closure = synth.makeClosure(body.position(), xts.Void(), synth.toBlock(body), xct);
        exprs.add(closure);
        types.add(closure.closureDef().asType());
        Stmt result = xnf.Eval(pos, synth.makeStaticCall(pos, xts.Runtime(), Name.make("runAsync"), exprs, xts.Void(),
                                                         types, xct));
        
        return result;
    }

}
