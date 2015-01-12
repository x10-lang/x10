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

package x10cpp.debug;

import java.util.ArrayList;
import java.util.StringTokenizer;

import polyglot.util.StringUtil;

public class StringTable {

	private final ArrayList<String> strings;

	public StringTable(ArrayList<String> strings) {
		this.strings = strings;
	}

	public int stringId(String str) {
	    String f = str.intern();
	    int n = strings.size();
	    for (int i = 0; i < n; i++)
	        if (lookupString(i) == f)
	            return i;
	    strings.add(f);
	    return n;
	}

	public String lookupString(int i) {
		return strings.get(i);
	}

	public String[] lookupStrings(int[] indices) {
		String[] res = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			res[i] = lookupString(indices[i]);
		}
		return res;
	}

	public int size() {
	    return strings.size();
	}

	public void exportStringMap(final StringBuilder sb) {
	    sb.append("{");
	    for (int i = 0; i < strings.size(); i++)
	        sb.append(i).append(":\"").append(StringUtil.escape(lookupString(i))).append("\","); // FIXME: might need to escape
	    sb.append("}");
	}

	public static ArrayList<String> importStringMap(StringTokenizer st) {
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