// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

import java.util.*;
import java.io.*;

class Report {

    static PrintStream out = System.out;

    static Set<String> tests = new LinkedHashSet();
    static Set<String> lgcs = new LinkedHashSet();

    static class Entries {

        HashMap<String,Collection<Entry>> map = new HashMap<String,Collection<Entry>>();

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
    }

    static Entries entries = new Entries();
        
    static class Entry extends HashMap<String,String> implements Comparable<Entry> {

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

        tests.add("SeqRail2");
        tests.add("SeqPseudoArray2a");
        tests.add("SeqPseudoArray2b");
        tests.add("SeqArray2");

        lgcs.add("cpp-opt");


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
                double ops = 0.0;
                Collection<Entry> l = entries.get(test, lgc);
                if (l!=null) {
                    for (Entry e : l) {
                        dates.add(e.get("date"));
                        ops = e.ops();
                    }
                }
                if (ops!=0.0) {
                    if (ops<1e6)      out.printf("%c: %-4.3g kop/s  ", letter, ops/1e3);
                    else if (ops<1e9) out.printf("%c: %-4.3g Mop/s  ", letter, ops/1e6);
                    else              out.printf("%c: %-4.3g Gop/s  ", letter, ops/1e9);
                    letter++;
                } else
                    out.printf("%15s", "");
            }
            for (String d : dates)
                out.printf("%s ", d);
            out.printf("\n");
        }
    }

    static void pr(String s) {
        out.println(s);
    }

}

        
