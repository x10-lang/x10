package polyglot.ext.x10cpp.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import polyglot.util.QuotedStringTokenizer;
import polyglot.util.StringUtil;

/**
 * Map from generated line to source line and filename.
 * Also stores the C++ filename (String).
 * C++ line number (Integer) -> (X10 filename (String), X10 line number (Integer)).
 * 
 * @author igor
 */
public class LineNumberMap {
    private static class Entry {
        public int fileId;
        public int line;
        public Entry(int f, int l) {
            fileId = f;
            line = l;
        }
    }
    private final String filename;
    private final ArrayList<String> files;
    private final HashMap<Integer, Entry> map;

    /**
     * @param filename C++ filename
     */
    public LineNumberMap(String filename) {
        this.filename = filename;
        this.map = new HashMap<Integer, Entry>();
        this.files = new ArrayList<String>();
    }

    /**
     * @return C++ filename
     */
    public String file() { return filename; }

    private int fileId(String file) {
        String f = file.intern();
        int n = files.size();
        for (int i = 0; i < n; i++)
            if (files.get(i) == f)
                return i;
        files.add(f);
        return n;
    }

    /**
     * @param lineNumber C++ line number
     * @param sourceFile X10 filename
     * @param sourceLine X10 line number
     */
    public void put(int lineNumber, String sourceFile, int sourceLine) {
        map.put(lineNumber, new Entry(fileId(sourceFile), sourceLine));
    }

    /**
     * @param lineNumber C++ line number
     * @return X10 filename
     */
    public String getSourceFile(int lineNumber) {
        return files.get(map.get(lineNumber).fileId);
    }

    /**
     * @param lineNumber C++ line number
     * @return X10 line number
     */
    public int getSourceLine(int lineNumber) {
        return map.get(lineNumber).line;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(filename).append(":\n");
        for (Integer line : map.keySet()) {
            Entry entry = map.get(line);
            sb.append("  ").append(line).append("->");
            sb.append(files.get(entry.fileId)).append(":").append(entry.line).append("\n");
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Produces a string suitable for initializing a field in the generated file.
     */
    public String exportMap() {
        final StringBuilder sb = new StringBuilder();
        sb.append("F{");
        for (int i = 0; i < files.size(); i++)
            sb.append(i).append(":\"").append(StringUtil.escape(files.get(i))).append("\","); // FIXME: might need to escape
        sb.append("} M{");
        for (Integer line : map.keySet()) {
            Entry entry = map.get(line);
            sb.append(line).append("->");
            sb.append(entry.fileId).append(":").append(entry.line).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Parses a string into a LineNumberMap.
     * @param file the filename to associate the map with
     * @param input the input string
     */
    public static LineNumberMap importMap(String file, String input) {
        LineNumberMap res = new LineNumberMap(file);
        StringTokenizer st = new QuotedStringTokenizer(input, " ", "\"\'", '\\', true);
        String s = st.nextToken("{}");
        assert (s.equals("F"));
        s = st.nextToken();
        assert (s.equals("{"));
        while (st.hasMoreTokens()) {
            String t = st.nextToken(":}");
            if (t.equals("}"))
                break;
            int i = Integer.parseInt(t);
            t = st.nextToken();
            assert (t.equals(":"));
            String f = st.nextToken(",");
            res.files.add(i, f);
            t = st.nextToken();
            assert (t.equals(","));
        }
        s = st.nextToken("{}");
        assert (s.equals(" M"));
        s = st.nextToken();
        assert (s.equals("{"));
        while (st.hasMoreTokens()) {
            String t = st.nextToken("-}");
            if (t.equals("}"))
                break;
            int i = Integer.parseInt(t);
            t = st.nextToken(">");
            assert (t.equals("-"));
            t = st.nextToken(">");
            assert (t.equals(">"));
            int f = Integer.parseInt(st.nextToken(":"));
            t = st.nextToken();
            assert (t.equals(":"));
            int l = Integer.parseInt(st.nextToken(","));
            res.map.put(i, new Entry(f, l));
            t = st.nextToken();
            assert (t.equals(","));
        }
        assert (!st.hasMoreTokens());
        return res;
    }
}
