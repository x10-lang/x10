/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.main.Report;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/**
 * Statistics collection and reporting object.
 * Extensions can override this to collect more stats or to change
 * reporting.
 */
public class Stats
{
    protected static class Counts {
        public long count;
    }

    /** Extension we're collecting stats for. */
    protected ExtensionInfo ext;

    /** Map from Objects to pair of inclusive and exclusive times. */
    protected Map<Object,Counts> counts = CollectionFactory.newHashMap();

    /**
     * List of Objects used as keys to passTimes.  We have an explicit
     * list in order to report the keys in order.
     */
    protected List<Object> keys = new ArrayList<Object>(20);

    public Stats(ExtensionInfo ext) {
        this.ext = ext;
    }

    /** Reset the accumulated times for a pass. */
    public void resetCounts(Object key) {
        counts.remove(key);
    }

    /** Return the accumulated times for a pass. */
    public long getCount(Object key) {
        Counts t = counts.get(key);
        if (t == null) {
            return 0;
        }

        return t.count;
    }

    /** Accumulate inclusive and exclusive times for a pass. */
    public void accumulate(Object key, long count) {
        if (Report.should_report(Report.time, 1)) {
        Counts t = counts.get(key);
        if (t == null) {
            keys.add(key);
            t = new Counts();
            counts.put(key, t);
        }
        t.count += count;
        }
    }

    /** Report the stats. */
    public void report() {
        if (Report.should_report(Report.time, 1)) {
            Report.report(1, "\nStatistics for " + ext.compilerName() +
                          " (" + ext.getClass().getName() + ")");
            Report.report(1, format("Count", "Key"));
            Report.report(1, format("-----", "---"));

            for (Iterator<Object> i = keys.iterator(); i.hasNext(); ) {
                Object key = i.next();
                Counts t = counts.get(key);

                String k = key.toString();
                if (k.endsWith(" nanos")) {
                    long millis = (long)(t.count / 1e6);
                    Report.report(1, format(k.substring(0,k.length()-5)+" ms", Long.toString(millis)));
                } else {
                    Report.report(1, format(key.toString(), Long.toString(t.count)));
                }
            }
        }
    }
    
    public String format(String key, String value) {
        StringBuilder sb = new StringBuilder();
        for (int i = value.length(); i < 16; i++) {
            sb.append(' ');
        }
        sb.append(value);
        sb.append(' ');
        sb.append(key);
        return sb.toString();
    }
}
