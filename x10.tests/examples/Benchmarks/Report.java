// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

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

        char letter;

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
    }

    public static void main(String args[]) throws Exception {

        StringWriter changes = new StringWriter();
        changed = new PrintWriter(changes);

        tests.add("SeqRail2");
        tests.add("SeqPseudoArray2a");
        tests.add("SeqPseudoArray2b");
        tests.add("SeqArray2a");
        tests.add("SeqArray2b");
        tests.add("SeqUTSBin1");
        tests.add("ParUTSBin1");
        tests.add("SeqMatMultAdd1a");
        tests.add("SeqStream1");

        lgcs.add("cpp-opt");
        lgcs.add("x10-cpp-opt");
        lgcs.add("java-opt");
        lgcs.add("x10-java-opt");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = in.readLine();
        while (line!=null) {
            new Entry(line);
            line = in.readLine();
        }

        out.printf("%20s", "");
        for (String lgc : lgcs)
            out.printf("%-15s", lgc);
        out.printf("\n");

        char letter = 'a';
        for (String test : tests) {
            out.printf("%-20s", test);
            Set<String> dates = new LinkedHashSet<String>();
            for (String lgc : lgcs) {
                Collection<Entry> l = entries.get(test, lgc);
                Entry show = null;
                Entry prev = null;
                if (l!=null) {
                    for (Entry e : l) {
                        if (prev!=null) {
                            double ratio = e.ops() / prev.ops();
                            if (ratio>1.1 || ratio <0.9)
                                changed.printf("    %-20s %-14s %.2fx from %s to %s\n",
                                    e.get("test"), lgc, ratio, prev.get("date-time"), e.get("date-time"));
                        }
                        show = e;
                        prev = e;
                    }
                }
                if (show!=null) {
                    double ops = show.ops();
                    entries.putShown(show);
                    dates.add(show.get("date"));
                    if (ops<1e6)      out.printf("%c: %-4.3g kop/s  ", letter, ops/1e3);
                    else if (ops<1e9) out.printf("%c: %-4.3g Mop/s  ", letter, ops/1e6);
                    else              out.printf("%c: %-4.3g Gop/s  ", letter, ops/1e9);
                    show.letter = letter++;
                } else
                    out.printf("%15s", "");
            }
            for (String d : dates)
                out.printf("%s ", d);
            out.printf("\n");
        }

        pr("C++ back end relative to hand-coded");
        compare("    rail access",              "SeqRail2",        "x10-cpp-opt", "SeqRail2",        "cpp-opt");
        compare("    non-generic pseudo-array", "SeqPseudoArray2a","x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        compare("    generic pseudo-array",     "SeqPseudoArray2b","x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        compare("    array w/ c-style loop",    "SeqArray2a",      "x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        //compare("    array w/ x10-style loop",  "SeqArray2b",      "x10-cpp-opt", "SeqPseudoArray2a","cpp-opt");
        compare("    sequential UTS",           "SeqUTSBin1",      "x10-cpp-opt", "SeqUTSBin1",      "cpp-opt");
        compare("    matrix multiply",          "SeqMatMultAdd1a", "x10-cpp-opt", "SeqMatMultAdd1a", "cpp-opt");
        compare("    sequential frag. stream",  "SeqStream1",      "x10-cpp-opt", "SeqStream1",      "cpp-opt");

        pr("Java back end relative to hand-coded");
        compare("    rail access",              "SeqRail2",        "x10-java-opt", "SeqRail2",        "java-opt");
        compare("    non-generic pseudo-array", "SeqPseudoArray2a","x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    generic pseudo-array",     "SeqPseudoArray2b","x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    array w/ c-style loop",    "SeqArray2a",      "x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    array w/ x10-style loop",  "SeqArray2b",      "x10-java-opt", "SeqPseudoArray2a","java-opt");
        compare("    sequential UTS",           "SeqUTSBin1",      "x10-java-opt", "SeqUTSBin1",      "java-opt");
        compare("    matrix multiply",          "SeqMatMultAdd1a", "x10-java-opt", "SeqMatMultAdd1a", "java-opt");
        compare("    sequential frag. stream",  "SeqStream1",      "x10-java-opt", "SeqStream1",      "java-opt");

        pr("C++ back end relative to Java back end");
        compare("    rail access",              "SeqRail2",        "x10-cpp-opt", "SeqRail2",        "x10-java-opt");
        compare("    non-generic pseudo-array", "SeqPseudoArray2a","x10-cpp-opt", "SeqPseudoArray2a","x10-java-opt");
        compare("    generic pseudo-array",     "SeqPseudoArray2b","x10-cpp-opt", "SeqPseudoArray2b","x10-java-opt");
        compare("    array w/ c-style loop",    "SeqArray2a",      "x10-cpp-opt", "SeqArray2a",      "x10-java-opt");
        //compare("    array w/ x10-style loop",  "SeqArray2b",      "x10-cpp-opt", "SeqArray2a",      "x10-java-opt");
        compare("    sequential UTS",           "SeqUTSBin1",      "x10-cpp-opt", "SeqUTSBin1",      "x10-java-opt");
        compare("    matrix multiply",          "SeqMatMultAdd1a", "x10-cpp-opt", "SeqMatMultAdd1a", "x10-java-opt");
        compare("    sequential frag. stream",  "SeqStream1",      "x10-cpp-opt", "SeqStream1",      "x10-java-opt");

        compare("C++ generic vs non-generic", "SeqPseudoArray2b","x10-cpp-opt", "SeqPseudoArray2a","x10-cpp-opt");
        //compare("Java generic vs non-generic", "SeqPseudoArray2b","x10-java-opt", "SeqPseudoArray2a","x10-java-opt");

        compare("UTS parallel speedup (Java)", "ParUTSBin1","x10-java-opt", "SeqUTSBin1","x10-java-opt");

        compare("Stream parallel speedup (Java)",    "ParStream1", "x10-java-opt", "SeqStream1", "x10-java-opt");
        compare("Stream parallel speedup (C++)",    "ParStream1", "x10-cpp-opt", "SeqStream1", "x10-cpp-opt");

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

    static void pr(String s) {
        out.println(s);
    }

}

        
