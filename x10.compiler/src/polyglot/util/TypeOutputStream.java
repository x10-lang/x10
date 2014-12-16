/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;

import java.io.*;
import java.util.*;

import polyglot.main.Reporter;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import x10.util.CollectionFactory;

/** Output stream for writing type objects. */
public class TypeOutputStream extends ObjectOutputStream
{
    protected TypeSystem ts;
    protected Reporter reporter;
    protected Set<TypeObject> roots;
    protected Map<Object,Object> placeHolders;
    
    public TypeOutputStream(OutputStream out, TypeSystem ts, TypeObject root) 
        throws IOException
    {
        super( out);
        
        this.ts = ts;
        this.reporter = ts.extensionInfo().getOptions().reporter;
        this.roots = ts.getTypeEncoderRootSet(root);
        this.placeHolders = CollectionFactory.newHashMap();
        
        if (reporter.should_report(Reporter.serialize, 2)) {
            reporter.report(2, "Began TypeOutputStream with roots: " + roots);
        }
        
        enableReplaceObject( true);
    }
    
    protected Object placeHolder(TypeObject o, boolean useRoots) {
        Object k = new IdentityKey(o);
        Object p = placeHolders.get(k);
        if (p == null) {
            p = ts.placeHolder(o, useRoots ? roots : Collections.<TypeObject>emptySet());
            placeHolders.put(k, p);
        }
        return p;
    }
    
    protected Object replaceObject(Object o) throws IOException {
        if (o instanceof TypeObject) {
            Object r;
            
            if (roots.contains(o)) {
                if (reporter.should_report(Reporter.serialize, 2)) {
                    reporter.report(2, "+ In roots: " + o + " : " + o.getClass());
                }
                
                r = o;
            }
            else {
                r = placeHolder((TypeObject) o, true);
            }
            
            if (reporter.should_report(Reporter.serialize, 2)) {
                if (r != o) {
                    reporter.report(2, "+ Replacing: " + o + " : " + o.getClass()
                                  + " with " + r);
                } 
                else {
                    reporter.report(2, "+ " + o + " : " + o.getClass());
                }
            }
                
            return r;
        }
        else {
            if (reporter.should_report(Reporter.serialize, 2)) {
                reporter.report(2, "+ " + o + " : " + o.getClass());
            }
            return o;
        }
    }
}
