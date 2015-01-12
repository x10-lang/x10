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

/**
 * @author bdlucas
 */

import java.util.*;
import java.io.*;

class Report {

    static PrintStream out = System.out;
    static PrintWriter changed;

    static Set<String> tests = new LinkedHashSet();
    static Set<String> lgcs = new LinkedHashSet();

    static class Entries {

        HashMap<String,Collection<Entry>> map = new HashMap<String,Collection<Entry>>();
        HashMap<String,Entry> shown = new HashMap<String,Entry>();

        private String key(String test, String lgc) {
            return test + "," + lgc;
        }
        
        void put(Entry e) {
            String key = key(e.get("test"), e.get("lg-code"));
            Collection<Entry> l = map.get(key);
            if (l==null) {
                l = new TreeSet<Entry>();
                map.put(key, l);
            }
            l.add(e);
        }

        Collection<Entry> get(String test, String lgc) {
            String key = key(test, lgc);
            Collection<Entry> l = map.get(key);
            return l;
        }

        void putShown(Entry e) {
            String key = key(e.get("test"), e.get("lg-code"));
            shown.put(key, e);
        }

        Entry getShown(String test, String lgc) {
            String key = key(test, lgc);        
            return shown.get(key);
        }
    }

    static Entries entries = new Entries();
        
    static class Entry extends HashMap<String,String> implements Comparable<Entry> {

        // identifying letter for this entry
        char letter;

        // create entry, parsing n/v pairs
        Entry(String line) {
            String [] nvs = line.split(" ");
            for (int i=0; i<nvs.length; i++) {
                String [] nv = nvs[i].split("=");
                put(nv[0], nv[1]);
            }
            put("lg-code", get("lg") + "-" + get("code"));
            put("date-time", get("date") + " " + get("time"));
            tests.add(get("test"));
            lgcs.add(get("lg-code"));
            entries.put(this);
        }

        double ops() {
            return Double.parseDouble(get("ops"));
        }

        public int compareTo(Entry that) {
            return this.get("date-time").compareTo(that.get("date-time"));
        }

        public String toString() {
            return "[" + letter + "," + get("test") + "," + get("lg-code") + "]";
        }
    }

    public static void main(String args[]) throws Exception {

        // log significant changes, print at end
        StringWriter changes = new StringWriter();
        changed = new PrintWriter(changes);

        // order of known tests
        tests.add("SeqRail2");
        tests.add("SeqPseudoArray2a");
        tests.add("SeqPseudoArray2b");
        tests.add("SeqArray2a");
        tests.add("SeqArray2b");
        tests.add("SeqUTSBin1");
        tests.add("ParUTSBin1");
        tests.add("SeqMatMultAdd1a");
        tests.add("SeqStream1");
        tests.add("ParStream1");
        tests.add("DistStream1");
        tests.add("SeqRandomAccess1");
        tests.add("ParRandomAccess1");
        tests.add("DistRandomAccess1");

        // order of known columns
        lgcs.add("cpp-opt");
        lgcs.add("x10-cpp-opt");
        lgcs.add("java-opt");
        lgcs.add("x10-java-opt");

        // read data
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = in.readLine();
        while (line!=null) {
            new Entry(line);
            line = in.readLine();
        }

        // section heading
        out.println("------- PERFORMANCE DATA:");
        out.println();

        // column headers
        out.printf("%20s", "");
        for (String lgc : lgcs)
            out.printf("%-15s", lgc);
        out.printf("\n");
        out.println();

        // identifying letter
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int letterCount = 0;
        char letter = letters.charAt(letterCount++);

        // for each row
        for (String test : tests) {

            // row header
            out.printf("%-20s", test);

            // keep track of dates for this row
            Set<String> dates = new LinkedHashSet<String>();

            // for each column
            for (String lgc : lgcs) {

                // find shown (last) and prev (next to last)
                Collection<Entry> l = entries.get(test, lgc);
                Entry show = null;
                Entry prev = null;
                if (l!=null) {
                    for (Entry e : l) {
                        show = e;
                        prev = e;
                    }
                }

                // look for significant changes from prev to shown
                if (prev!=null && show!=null) {
                    double ratio = show.ops() / prev.ops();
                    if (ratio>1.1 || ratio <0.9)
                        changed.printf("    %-20s %-14s %.2fx from %s to %s\n",
                            show.get("test"), lgc, ratio, prev.get("date-time"), show.get("date-time"));
                }
                
                // show a row/column entry
                if (show!=null) {
                    double ops = show.ops();
                    entries.putShown(show);
                    dates.add(show.get("date"));
                    if (ops<1e6)      out.printf("%c: %-4.3g kop/s  ", letter, ops/1e3);
                    else if (ops<1e9) out.printf("%c: %-4.3g Mop/s  ", letter, ops/1e6);
                    else              out.printf("%c: %-4.3g Gop/s  ", letter, ops/1e9);
                    show.letter = letter;
                    letter = letters.charAt(letterCount++);
                } else
                    out.printf("%15s", "");

            }

            // show dates for this row
            for (String d : dates)
                out.printf("%s ", d);

            // done with row
            out.printf("\n");
        }
        out.println();

        out.println("------- COMPARISONS:");
        out.println();

        out.println("C++ back end relative to hand-coded");
        compare("    rail access",              "SeqRail2",        "x10-cpp-opt", "SeqRail2",        "cpp-opt");
        compare("    non-generic pseudo-array", "SeqPseudoArray2a","x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        compare("    generic pseudo-array",     "SeqPseudoArray2b","x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        compare("    array w/ c-style loop",    "SeqArray2a",      "x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        //compare("    array w/ x10-style loop",  "SeqArray2b",      "x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        compare("    sequential UTS",           "SeqUTSBin1",      "x10-cpp-opt", "SeqUTSBin1",      "cpp-opt");
        compare("    matrix multiply",          "SeqMatMultAdd1a", "x10-cpp-opt", "SeqMatMultAdd1a", "cpp-opt");
        compare("    sequential frag. stream",  "SeqStream1",      "x10-cpp-opt", "SeqStream1",      "cpp-opt");
        compare("    sequential random access", "SeqRandomAccess1","x10-cpp-opt", "SeqRandomAccess1","cpp-opt");
        out.println();

        out.println("Java back end relative to hand-coded");
        compare("    rail access",              "SeqRail2",        "x10-java-opt", "SeqRail2",        "java-opt");
        compare("    non-generic pseudo-array", "SeqPseudoArray2a","x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    generic pseudo-array",     "SeqPseudoArray2b","x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    array w/ c-style loop",    "SeqArray2a",      "x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    array w/ x10-style loop",  "SeqArray2b",      "x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    sequential UTS",           "SeqUTSBin1",      "x10-java-opt", "SeqUTSBin1",      "java-opt");
        compare("    matrix multiply",          "SeqMatMultAdd1a", "x10-java-opt", "SeqMatMultAdd1a", "java-opt");
        compare("    sequential frag. stream",  "SeqStream1",      "x10-java-opt", "SeqStream1",      "java-opt");
        compare("    sequential random access", "SeqRandomAccess1","x10-java-opt", "SeqRandomAccess1","java-opt");
        out.println();

        out.println("C++ back end relative to Java back end");
        compare("    rail access",              "SeqRail2",        "x10-cpp-opt", "SeqRail2",        "x10-java-opt");
        compare("    non-generic pseudo-array", "SeqPseudoArray2a","x10-cpp-opt", "SeqPseudoArray2a","x10-java-opt");
        compare("    generic pseudo-array",     "SeqPseudoArray2b","x10-cpp-opt", "SeqPseudoArray2b","x10-java-opt");
        compare("    array w/ c-style loop",    "SeqArray2a",      "x10-cpp-opt", "SeqArray2a",      "x10-java-opt");
        //compare("    array w/ x10-style loop",  "SeqArray2b",      "x10-cpp-opt", "SeqArray2a",      "x10-java-opt");
        compare("    sequential UTS",           "SeqUTSBin1",      "x10-cpp-opt", "SeqUTSBin1",      "x10-java-opt");
        compare("    matrix multiply",          "SeqMatMultAdd1a", "x10-cpp-opt", "SeqMatMultAdd1a", "x10-java-opt");
        compare("    sequential frag. stream",  "SeqStream1",      "x10-cpp-opt", "SeqStream1",      "x10-java-opt");
        compare("    sequential random access", "SeqRandomAccess1","x10-cpp-opt", "SeqRandomAccess1","x10-java-opt");
        out.println();

        compare("C++ generic vs non-generic",   "SeqPseudoArray2b","x10-cpp-opt", "SeqPseudoArray2a","x10-cpp-opt");
        //compare("Java generic vs non-generic", "SeqPseudoArray2b","x10-java-opt", "SeqPseudoArray2a","x10-java-opt");
        out.println();

        compare("UTS par. speedup (Java)", "ParUTSBin1","x10-java-opt", "SeqUTSBin1","x10-java-opt");
        out.println();

        compare("Stream par. speedup (Java)",   "ParStream1",  "x10-java-opt", "SeqStream1", "x10-java-opt");
        compare("Stream par. speedup (C++)",    "ParStream1",  "x10-cpp-opt",  "SeqStream1", "x10-cpp-opt");
        out.println();

        compare("Stream dist. speedup (Java)",  "DistStream1", "x10-java-opt", "SeqStream1", "x10-java-opt");
        compare("Stream dist. speedup (C++)",   "DistStream1", "x10-cpp-opt",  "SeqStream1", "x10-cpp-opt");
        out.println();

        compare("Rand. access par. speedup (Java)",   "ParRandomAccess1",  "x10-java-opt", "SeqRandomAccess1", "x10-java-opt");
        compare("Rand. access par. speedup (C++)",    "ParRandomAccess1",  "x10-cpp-opt",  "SeqRandomAccess1", "x10-cpp-opt");
        out.println();

        compare("Rand. access dist. speedup (Java)",  "DistRandomAccess1", "x10-java-opt", "SeqRandomAccess1", "x10-java-opt");
        compare("Rand. access dist. speedup (C++)",   "DistRandomAccess1", "x10-cpp-opt",  "SeqRandomAccess1", "x10-cpp-opt");
        out.println();

        String s = changes.toString();
        if (s.length()>0)
            out.println("significant changes:\n" + s);
    }

    static void compare(String name, String t1, String l1, String t2, String l2) {
        Entry e1 = entries.getShown(t1,l1);
        Entry e2 = entries.getShown(t2,l2);
        if (e2.ops()>e1.ops())
            out.printf("%-30s %5.1fx slower (%s/%s)\n", name, e2.ops()/e1.ops(), e2.letter, e1.letter);
        else
            out.printf("%-30s %5.1fx faster (%s/%s)\n", name, e1.ops()/e2.ops(), e1.letter, e2.letter);
    }

}
