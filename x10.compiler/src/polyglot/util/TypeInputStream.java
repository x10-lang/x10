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

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.main.Reporter;
import polyglot.types.*;

import java.util.*;
import java.io.*;

import x10.util.CollectionFactory;

/** Input stream for reading type objects. */
public class TypeInputStream extends ObjectInputStream {
    protected TypeSystem ts;
    protected Reporter reporter;
    protected Map<Object, Object> cache;
    protected boolean failed;
    protected boolean enableReplace;
    protected Set<Object> placeHoldersUsed;
    
    public TypeInputStream(InputStream in, TypeSystem ts, Map<Object, Object> cache)
        throws IOException
    {
        super(in);

        enableResolveObject(true);

        this.ts = ts;
        this.reporter = ts.extensionInfo().getOptions().reporter;
        this.cache = cache;
        this.failed = false;
        this.enableReplace = true;
        this.placeHoldersUsed = CollectionFactory.newHashSet();
    }
    
    public Set<Object> placeHoldersUsed() {
        return placeHoldersUsed;
    }

    public boolean deserializationFailed() {
        return failed;
    }

    public TypeSystem getTypeSystem() {
        return ts;
    }
    
    private final static Object UNRESOLVED = new Object();
    
    public void enableReplace(boolean f) {
        this.enableReplace = f;
    }

    protected Object resolveObject(Object o) {
        if (! enableReplace) {
            return o;
        }
        String s = "";
        if (reporter.should_report(reporter.serialize, 2)) {
            try {
                s = o.toString();
            }
            catch (NullPointerException e) {
                s = "<NullPointerException thrown>";
            }
        }	  

        if (! enableReplace) {
            return o;
        }
        else if (o instanceof Internable) {
            if (reporter.should_report(Reporter.serialize, 2)) {    
                reporter.report(2, "- Interning " + s + " : " + o.getClass());
            }
            return ((Internable) o).intern();
        }
        else if (o instanceof Goal) {
            return ((Goal) o).intern(ts.extensionInfo().scheduler());
        }
        else {
            if (reporter.should_report(Reporter.serialize, 2)) {    
                reporter.report(2, "- " + s + " : " + o.getClass());
            }

            return o;
        }
    }
}
