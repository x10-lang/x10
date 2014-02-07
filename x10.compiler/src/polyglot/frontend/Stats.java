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

package polyglot.frontend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Stack;

import polyglot.main.Reporter;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;

/**
 * Statistics collection and reporting object. Extensions can override this to
 * collect more stats or to change reporting.
 */
public class Stats {
    protected static class Counts {
        public long count;
        public Counter counter;
    }

    /*** Count accumulator for an object */
    private class Counter {
        /** Map from Objects to counts for the Object */
        public Map<Object, Counts> counts;

        /** List of Objects used as keys to counts. */
        public List<Object> keys;

        public Counter() {
            counts = CollectionFactory.newHashMap();
            keys = new ArrayList<Object>(50);
        }

        /** Accumulate counts for a key */
        public void accumulate(Object key, long count) {
            Counts t = counts.get(key);
            if (t == null) {
                keys.add(key);
                t = new Counts();
                counts.put(key, t);
            }
            t.count += count;
        }
    }

    private Reporter reporter;
    private Counter phase, site, freq;
    private long startTime, totalTime, reportTimeThreshold;
    private int currDepth, maxDepth;
    private boolean t2;
    private String name;

    /*** Stack of phase names for timing nested phases (goals) */
    private class stackStruct {
        long startTime;
        Object phaseName;
        Object siteName;
        Counter phaseCounter;

        stackStruct(long sTime, Object pName, Object sName, Counter pCounter) {
            startTime = sTime;
            phaseName = pName;
            siteName = sName;
            phaseCounter = pCounter;
        }
    }

    private Stack<stackStruct> start;

    /***
     * Initialize statistics if reporting is requested. Subsequently, we just
     * need to check for non-null pointers to see if we need to do the statistic
     * gathering work.
     * 
     * @param startTime
     */
    public void initialize(ExtensionInfo ext, long startTime) {
        this.startTime = startTime;
        this.name = ext.compilerName();
        reporter = ext.getOptions().reporter;
        if (reporter.should_report(Reporter.time, 1)) {
            t2 = reporter.should_report(Reporter.time, 2);
            reporter.removeTopic(Reporter.time);
            if (reporter.should_report(Reporter.threshold, 1)) {
                reportTimeThreshold = reporter.level(Reporter.threshold);
                reporter.removeTopic(Reporter.threshold);
            }
            phase = new Counter();
            site = new Counter();
            start = new Stack<stackStruct>();
        }
        if (reporter.should_report(Reporter.frequency, 1)) {
            reporter.removeTopic(Reporter.frequency);
            freq = new Counter();
        }
    }

    /*** Increment frequency counter */
    public void incrFrequency(Object key, long count) {
        if (freq == null) return;
        freq.accumulate(key, count);
    }

    /***
     * Start timing a phase This should be paired with stopTiming
     * 
     * @param phaseName
     * @param siteName
     */
    public void startTiming(Object phaseName, Object siteName) {
        if (phase == null) return;
        Counter c;
        if (start.empty()) {
            c = phase;
        } else {
            stackStruct s = start.peek();
            Counts t = s.phaseCounter.counts.get(s.phaseName);
            if (t == null) {
                s.phaseCounter.keys.add(s.phaseName);
                t = new Counts();
                s.phaseCounter.counts.put(s.phaseName, t);
            }
            if (t.counter == null) {
                t.counter = new Counter();
            }
            c = t.counter;
        }
        start.push(new stackStruct(System.nanoTime(), phaseName, siteName, c));
        currDepth++;
        maxDepth = (currDepth > maxDepth) ? currDepth : maxDepth;
    }

    /***
     * Stop timing a phase. This should be paired with startTiming.
     */
    public void stopTiming() {
        if (phase == null) return;
        stackStruct s = start.pop();
        long elapsed = System.nanoTime() - s.startTime;
        s.phaseCounter.accumulate(s.phaseName, elapsed);
        site.accumulate(s.siteName, elapsed);
        currDepth--;
    }

    /** Reporter the frequency counts. */
    public void reportFrequency() {
        if (freq == null) return;
        reporter.report(1, "\nFrequency Statistics for  "+name);
        reporter.report(1, String.format("%16s", "Count") + " Name");
        reporter.report(1, String.format("%16s", "-----") + " ----");

        for (Iterator<Object> i = freq.keys.iterator(); i.hasNext();) {
            Object key = i.next();
            Counts t = freq.counts.get(key);
            reporter.report(1, String.format("%16d", t.count) + " " + key.toString());
        }
    }

    /** Reporter the times. */
    public void reportTime() {
        totalTime = System.nanoTime() - startTime;
        
        // Scale the threshold to a percentage of total time
        reportTimeThreshold *= totalTime;
        reportTimeThreshold /= 100;

        if (phase != null) reportTiming();

        if (reporter.should_report(Reporter.verbose, 1) || phase != null)
            reporter.report(1, "Total time=" + String.format("%.3f", totalTime / 1e9) + " seconds");
    }

    /** Reporter the times. */
    private void reportTiming() {
        if (currDepth != 0) reporter.report(1, "\nWarning: mismatched start/stop times");

        reporter.report(1, "\nPhase Statistics for "+name);
        String pad = "";
        for (int i = t2 ? maxDepth : 1; i > 0; i--)
            pad += "   ";
        reporter.report(1, "Percent" + pad + " Seconds   Name");
        reporter.report(1, "-------" + pad + "---------  ----");
        reportPhase(0, phase);
        long unattributedTime = totalTime;
        for (Iterator<Object> i = phase.keys.iterator(); i.hasNext();) {
            Object key = i.next();
            Counts t = phase.counts.get(key);
            unattributedTime -= t.count;
        }
        reporter.report(1,
                      String.format("%6.3f%%", (unattributedTime * 100) / (double) totalTime) + pad
                              + String.format("%9.3f", unattributedTime / 1e9) + "  Unattributed");

        reporter.report(1, "\nSite Statistics for x10c");
        reporter.report(1, "  Seconds  Name");
        reporter.report(1, "  -------  ----");

        for (Iterator<Object> i = sortByCount(site.counts).iterator(); i.hasNext();) {
            Object key = i.next();
            Counts t = site.counts.get(key);
            if (t.count >= reportTimeThreshold) {
                reporter.report(1, String.format("%9.3f", t.count / 1e9) + "  " + key.toString());

            }
        }
    }

    /*** This handles reporting nestedphases (goals) */
    private void reportPhase(int depth, Counter c) {
        int d;
        String indent = "";
        for (d = 0; d < depth; d++)
            indent += "   ";
        String pad = "   ";
        if (t2) {
            for (d = depth; d < maxDepth - 1; d++) {
                pad += "   ";
            }
        }
        for (Iterator<Object> i = sortByCount(c.counts).iterator(); i.hasNext();) {
            Object key = i.next();
            Counts t = c.counts.get(key);
            if (t.count > reportTimeThreshold) {
                reporter.report(1,
                              indent + String.format("%6.3f%%", (t.count * 100) / (double) totalTime) + pad
                                      + String.format("%9.3f", t.count / 1e9) + "  " + key.toString());
                if (t2 && t.counter != null) {
                    reportPhase(depth + 1, t.counter);
                }
            }
        }
    }

    /*** Sort the hash maps by frequency */
    private static List<Object> sortByCount(final Map<Object, Counts> m) {
        List<Object> keys = new ArrayList<Object>();
        keys.addAll(m.keySet());
        Collections.sort(keys, new Comparator<Object>() {
            public int compare(Object k1, Object k2) {
                Counts c1 = (Counts) m.get(k1);
                Counts c2 = (Counts) m.get(k2);
                return ((Long) c2.count).compareTo(c1.count);
            }
        });
        return keys;
    }
}
