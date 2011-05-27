/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * A constant value that represents a String constant
 */
public class StringValue extends ConstantValue {
    
    private final String val;
    
    StringValue(String s) {
        val = s;
    }
    
    public String value() {
        return val;
    }
    
    public String toJavaObject() {
        return val;
    }
    
    public StringLit toLit(NodeFactory nf, TypeSystem ts, Position pos) {
        return (StringLit)nf.StringLit(pos, val).type(ts.String());
    }

    public StringLit toUntypedLit(NodeFactory nf, Position pos) {
        return (StringLit)nf.StringLit(pos, val);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof StringValue) {
            return val.equals(((StringValue) that).val);
        } else {
            return false;
        }
    }
    
    @Override 
    public int hashCode() {
        return val.hashCode();
    }
    
    @Override
    public String toString() {
        return "\""+val+"\"";
    }

}
