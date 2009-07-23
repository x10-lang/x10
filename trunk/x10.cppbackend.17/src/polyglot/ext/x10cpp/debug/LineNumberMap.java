package polyglot.ext.x10cpp.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import polyglot.ext.x10cpp.visit.Emitter;
import polyglot.types.ConstructorDef;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.util.QuotedStringTokenizer;

/**
 * Map from generated line to source line and filename.
 * Also stores the C++ filename (String).
 * C++ line number (Integer) -> (X10 filename (String), X10 line number (Integer)).
 * Also stores the method mapping.
 * 
 * @author igor
 */
public class LineNumberMap extends StringTable {
    private static class Entry {
        public int fileId;
        public int line;
        public Entry(int f, int l) {
            fileId = f;
            line = l;
        }
        public String toString() {
        	return fileId + ":" + line;
        }
    }
    private final String filename;
    private final HashMap<Integer, Entry> map;
    private static class MethodDescriptor {
    	public final int returnType;
    	public final int container;
    	public final int name;
    	public final int[] args;
    	public MethodDescriptor(int returnType, int container, int name, int[] args) {
			this.returnType = returnType;
			this.container = container;
			this.name = name;
			this.args = args;
    	}
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass())
				return false;
			MethodDescriptor md = (MethodDescriptor) obj;
			if (md.name != name || md.container != container ||
				md.returnType != returnType || md.args.length != args.length)
			{
				return false;
			}
			for (int i = 0; i < args.length; i++) {
				if (args[i] != md.args[i])
					return false;
			}
			return true;
		}
		public int hashCode() {
			int h = name;
			h = 31*h + container;
			h = 31*h + returnType;
			for (int arg : args) {
				h = 31*h + arg;
			}
			return h;
		}
    	public String toString() {
    		StringBuilder res = new StringBuilder();
    		res.append(returnType).append(" ");
    		res.append(container).append(".");
    		res.append(name).append("(");
    		boolean first = true;
    		for (int arg : args) {
    			if (!first) res.append(",");
    			first = false;
    			res.append(arg);
    		}
    		res.append(")");
    		return res.toString();
    	}
		public static MethodDescriptor parse(String str) {
			StringTokenizer st = new StringTokenizer(str, ". (),", true);
	        String s = st.nextToken(" ");
	        int r = Integer.parseInt(s);
	        s = st.nextToken();
	        assert (s.equals(" "));
	        s = st.nextToken(".");
	        int c = Integer.parseInt(s);
	        s = st.nextToken();
	        assert (s.equals("."));
	        s = st.nextToken("(");
	        int n = Integer.parseInt(s);
	        s = st.nextToken();
	        assert (s.equals("("));
	        ArrayList<String> al = new ArrayList<String>(); 
	        while (st.hasMoreTokens()) {
	            String t = st.nextToken(",)");
	            if (t.equals(")"))
	                break;
	            al.add(t);
	            t = st.nextToken();
	            if (t.equals(")"))
	            	break;
	            assert (t.equals(","));
	        }
	        assert (!st.hasMoreTokens());
	        int[] a = new int[al.size()];
	        for (int i = 0; i < a.length; i++) {
				a[i] = Integer.parseInt(al.get(i));
			}
			return new MethodDescriptor(r, c, n, a);
		}
    	public String toPrettyString(LineNumberMap map) {
			return toPrettyString(map, true);
		}
		public String toPrettyString(LineNumberMap map, boolean includeReturnType) {
    		StringBuilder res = new StringBuilder();
    		if (includeReturnType)
    			res.append(map.lookupString(returnType)).append(" ");
    		res.append(map.lookupString(container)).append("::");
    		res.append(map.lookupString(name)).append("(");
    		boolean first = true;
    		for (int arg : args) {
    			if (!first) res.append(",");
    			first = false;
    			res.append(map.lookupString(arg));
    		}
    		res.append(")");
    		return res.toString();
    	}
    }
    private final HashMap<MethodDescriptor, MethodDescriptor> methods;

    /**
     * @param filename C++ filename
     */
    public LineNumberMap(String filename) {
    	this(filename, new ArrayList<String>());
    }

    private LineNumberMap(String filename, ArrayList<String> strings) {
    	super(strings);
        this.filename = filename;
        this.map = new HashMap<Integer, Entry>();
        this.methods = new HashMap<MethodDescriptor, MethodDescriptor>();
    }

    /**
     * @return C++ filename
     */
    public String file() { return filename; }

    /**
     * @param lineNumber C++ line number
     * @param sourceFile X10 filename
     * @param sourceLine X10 line number
     */
    public void put(int lineNumber, String sourceFile, int sourceLine) {
        map.put(lineNumber, new Entry(stringId(sourceFile), sourceLine));
    }

    /**
     * @param lineNumber C++ line number
     * @return X10 filename
     */
    public String getSourceFile(int lineNumber) {
        Entry entry = map.get(lineNumber);
        if (entry == null)
        	return null;
		return lookupString(entry.fileId);
    }

    /**
     * @param lineNumber C++ line number
     * @return X10 line number
     */
    public int getSourceLine(int lineNumber) {
        Entry entry = map.get(lineNumber);
        if (entry == null)
        	return -1;
		return entry.line;
    }

	private MethodDescriptor createMethodDescriptor(String container, String name, String returnType, String[] args) {
		int c = stringId(container);
		int n = stringId(name);
		int r = stringId(returnType);
		int[] a = new int[args.length];
		for (int i = 0; i < a.length; i++) {
			a[i] = stringId(args[i]);
		}
		return new MethodDescriptor(r, c, n, a);
	}

	/**
	 * @param sourceMethod X10 method signature
	 */
	public void addMethodMapping(MethodDef sourceMethod) {
		addMethodMapping(sourceMethod.container().get(), sourceMethod.name().toString(),
		                 sourceMethod.returnType().get(), sourceMethod.formalTypes());
	}

	/**
	 * @param sourceConstructor X10 constructor signature
	 */
	public void addMethodMapping(ConstructorDef sourceConstructor) {
		addMethodMapping(sourceConstructor.container().get(), null, null, sourceConstructor.formalTypes());
	}

	private void addMethodMapping(Type c, String n, Type r, List<Ref<? extends Type>> f) {
		assert (c != null);
		assert (f != null);
		String sc = c.toString();
		String sn = n == null ? "this" : n;
		String sr = r == null ? "" : r.toString();
		String[] sa = new String[f.size()];
		for (int i = 0; i < sa.length; i++) {
			sa[i] = f.get(i).get().toString();
		}
		MethodDescriptor src = createMethodDescriptor(sc, sn, sr, sa);
		String tc = Emitter.translateType(c);
		String tn = n == null ? "_constructor" : Emitter.mangled_method_name(n);
		String tr = r == null ? "void" : Emitter.translateType(r, true);
		String[] ta = new String[sa.length];
		for (int i = 0; i < ta.length; i++) {
			ta[i] = Emitter.translateType(f.get(i).get(), true);
		}
		MethodDescriptor tgt = createMethodDescriptor(tc, tn, tr, ta);
		assert (methods.get(tgt) == null);
		methods.put(tgt, src);
	}

	/**
	 * @param method target method signature
	 * @return source method signature
	 */
	public String getMappedMethod(String method) {
		for (MethodDescriptor m : methods.keySet()) {
			String mm = m.toPrettyString(this, false);
			if (mm.equals(method))
				return methods.get(m).toPrettyString(this, false);
		}
		return null;
	}

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(filename).append(":\n");
        for (Integer line : map.keySet()) {
            Entry entry = map.get(line);
            sb.append("  ").append(line).append("->");
            sb.append(lookupString(entry.fileId)).append(":").append(entry.line).append("\n");
        }
        sb.append("\n");
        for (MethodDescriptor md : methods.keySet()) {
			MethodDescriptor sm = methods.get(md);
			sb.append("  ").append(md.toPrettyString(this, true)).append("->");
			sb.append(sm.toPrettyString(this, true)).append("\n");
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
        sb.append("N{\"").append(filename).append("\"} F");
        exportStringMap(sb);
        sb.append(" L{");
        for (Integer line : map.keySet()) {
            Entry entry = map.get(line);
            sb.append(line).append("->");
            sb.append(entry.fileId).append(":").append(entry.line).append(",");
        }
        sb.append("} M{");
        for (MethodDescriptor md : methods.keySet()) {
			MethodDescriptor sm = methods.get(md);
			sb.append(md.toString()).append("->").append(sm.toString()).append(";");
		}
        sb.append("}");
        return sb.toString();
    }

    /**
     * Parses a string into a {@link LineNumberMap}.
     * @param input the input string
     */
    public static LineNumberMap importMap(String input) {
        StringTokenizer st = new QuotedStringTokenizer(input, " ", "\"\'", '\\', true);
        String s = st.nextToken("{}");
        assert (s.equals("N"));
        s = st.nextToken();
        assert (s.equals("{"));
        String file = st.nextToken();
        s = st.nextToken();
        assert (s.equals("}"));
        s = st.nextToken("{}");
        assert (s.equals(" F"));
        ArrayList<String> strings = importStringMap(st);
        LineNumberMap res = new LineNumberMap(file, strings);
        if (!st.hasMoreTokens())
        	return res;
        s = st.nextToken("{}");
        assert (s.equals(" L"));
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
        if (!st.hasMoreTokens())
        	return res;
        s = st.nextToken("{}");
        assert (s.equals(" M"));
        s = st.nextToken();
        assert (s.equals("{"));
        while (st.hasMoreTokens()) {
            String t = st.nextToken("-}");
            if (t.equals("}"))
                break;
            MethodDescriptor md = MethodDescriptor.parse(t);
            t = st.nextToken(">");
            assert (t.equals("-"));
            t = st.nextToken(">");
            assert (t.equals(">"));
            MethodDescriptor sm = MethodDescriptor.parse(st.nextToken(";"));
            res.methods.put(md, sm);
            t = st.nextToken();
            assert (t.equals(";"));
        }
        assert (!st.hasMoreTokens());
        return res;
    }

    /**
     * Inverts the current map.  Creates a full (file,line)->(file,line) map.
     * @return the resulting inverted map
     */
    public HashMap<String, LineNumberMap> invert() {
    	HashMap<String, LineNumberMap> res = new HashMap<String, LineNumberMap>();
    	for (Integer line : map.keySet()) {
			Entry e = map.get(line);
			String f = lookupString(e.fileId);
			int l = e.line;
			LineNumberMap m = res.get(f);
			if (m == null)
				res.put(f, m = new LineNumberMap(f));
			m.put(l, filename, line.intValue());
		}
    	for (MethodDescriptor md : methods.keySet()) {
			MethodDescriptor sm = methods.get(md);
			String f = lookupString(0); // FIXME: hack
			LineNumberMap m = res.get(f);
			if (m == null)
				res.put(f, m = new LineNumberMap(f));
			MethodDescriptor tmd = m.createMethodDescriptor(lookupString(md.container),
					lookupString(md.name), lookupString(md.returnType), lookupStrings(md.args));
			MethodDescriptor tsm = m.createMethodDescriptor(lookupString(sm.container),
					lookupString(sm.name), lookupString(sm.returnType), lookupStrings(sm.args));
			m.methods.put(tsm, tmd);
		}
    	return res;
    }

	/**
	 * Creates a new map.
	 * @return new map
	 */
	public static HashMap<String, LineNumberMap> initMap() {
		return new HashMap<String, LineNumberMap>();
	}

	/**
	 * Merges a set of new entries into a given map.  Changes the map in place!
	 * @param map the target map (changed in place!)
	 * @param newEntries the set of new entries
	 */
	public static void mergeMap(HashMap<String, LineNumberMap> map, HashMap<String, LineNumberMap> newEntries) {
		assert (map != null);
		for (String f : newEntries.keySet()) {
			LineNumberMap n = newEntries.get(f);
			LineNumberMap m = map.get(f);
			if (m == null)
				map.put(f, m = new LineNumberMap(f));
			for (int l : n.map.keySet()) {
//				assert (!m.map.containsKey(l));
				Entry e = n.map.get(l);
				m.put(l, n.lookupString(e.fileId), e.line);
			}
			for (MethodDescriptor d : n.methods.keySet()) {
				assert (!m.methods.containsKey(d));
				MethodDescriptor e = n.methods.get(d);
				MethodDescriptor dp = m.createMethodDescriptor(n.lookupString(d.container),
						n.lookupString(d.name), n.lookupString(d.returnType), n.lookupStrings(d.args));
				MethodDescriptor ep = m.createMethodDescriptor(n.lookupString(e.container),
						n.lookupString(e.name), n.lookupString(e.returnType), n.lookupStrings(e.args));
				m.methods.put(dp, ep);
			}
		}
	}
}
