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
package x10.util.synthesizer;

import polyglot.util.Position;
import x10.ast.X10NodeFactory;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import x10.util.Synthesizer;

/**
 * Base class for all state synthesizer
 *
 */
public abstract class AbstractStateSynth {
    //Some constants;
    static protected Position compilerPos = Position.COMPILER_GENERATED; 

    Position pos; //the position of this block
    X10NodeFactory xnf;
    X10Context xct;
    X10TypeSystem xts;
    Synthesizer synth;

    boolean closed; //indicator of the state
    
    public AbstractStateSynth(X10NodeFactory xnf, X10Context xct, Position pos) {
        this.xnf = xnf;
        this.pos = pos;
        
        setContext(xct);

    }
    
    
    public void setContext(X10Context xct){
        this.xct = xct;
        if(xct != null){
            //Some synthesizer doesn't use xct and xts
            xts = (X10TypeSystem) xct.typeSystem();
            synth = new Synthesizer(xnf, xts);
        }
    }
    
    /**
     * Check close. If the synthesizer is closed, any modification will trigger the StateSynthClosedException
     * @throws StateSynthClosedException
     */
    protected void checkClose() throws StateSynthClosedException {
        if(closed){
            throw new StateSynthClosedException("[State Synthesizer]Closed. Cannot be modified anymore!");
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
