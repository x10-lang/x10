package polyglot.ext.x10cpp.debug;

import java.util.ArrayList;
import java.util.StringTokenizer;

import polyglot.util.StringUtil;

public class StringTable {

	private final ArrayList<String> strings;

	public StringTable(ArrayList<String> strings) {
		this.strings = strings;
	}

	protected int stringId(String str) {
	    String f = str.intern();
	    int n = strings.size();
	    for (int i = 0; i < n; i++)
	        if (lookupString(i) == f)
	            return i;
	    strings.add(f);
	    return n;
	}

	protected String lookupString(int i) {
		return strings.get(i);
	}

	protected String[] lookupStrings(int[] indices) {
		String[] res = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			res[i] = lookupString(indices[i]);
		}
		return res;
	}

	protected void exportStringMap(final StringBuilder sb) {
		sb.append("{");
	    for (int i = 0; i < strings.size(); i++)
	        sb.append(i).append(":\"").append(StringUtil.escape(lookupString(i))).append("\","); // FIXME: might need to escape
	    sb.append("}");
	}

	protected static ArrayList<String> importStringMap(StringTokenizer st) {
		ArrayList<String> strings = new ArrayList<String>();
		String s = st.nextToken();
		assert (s.equals("{"));
		while (st.hasMoreTokens()) {
			String t = st.nextToken(":}");
			if (t.equals("}"))
				break;
			int i = Integer.parseInt(t);
			t = st.nextToken();
			assert (t.equals(":"));
			String f = st.nextToken(",");
			strings.add(i, f);
			t = st.nextToken();
			assert (t.equals(","));
		}
		return strings;
	}
}