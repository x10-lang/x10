/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.util;

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.main.Report;
import polyglot.types.*;

import java.util.*;
import java.io.*;

/** Input stream for reading type objects. */
public class TypeInputStream extends ObjectInputStream {
    protected TypeSystem ts;
    protected Map cache;
    protected boolean failed;
    protected boolean enableReplace;
    protected Set placeHoldersUsed;
    
    public TypeInputStream(InputStream in, TypeSystem ts, Map cache)
        throws IOException
    {
        super(in);

        enableResolveObject(true);

        this.ts = ts;
        this.cache = cache;
        this.failed = false;
        this.enableReplace = true;
        this.placeHoldersUsed = new HashSet();
    }
    
    public Set placeHoldersUsed() {
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
        if (Report.should_report(Report.serialize, 2)) {
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
            if (Report.should_report(Report.serialize, 2)) {    
                Report.report(2, "- Interning " + s + " : " + o.getClass());
            }
            return ((Internable) o).intern();
        }
        else if (o instanceof Goal) {
            return ((Goal) o).intern(Globals.Scheduler());
        }
        else {
            if (Report.should_report(Report.serialize, 2)) {    
                Report.report(2, "- " + s + " : " + o.getClass());
            }

            return o;
        }
    }
}
