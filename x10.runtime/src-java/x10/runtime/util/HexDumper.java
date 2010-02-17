/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.runtime.util;

import java.io.*;

public class HexDumper {
	public static void main(String[] args) {
		try {
			byte[] ba = null;
			if (args.length > 0)
				ba = getFileBytes(args[0]);
			if (ba != null)
				dumpBytes(System.out, ba);
		} catch (IOException e) { e.printStackTrace(); }
	}
	private static byte[] getFileBytes(String name) throws IOException {
		InputStream cin = new FileInputStream(name);
		if (cin == null)
			throw new IOException("File "+name+" cannot be found");
		byte[] bytes = new byte[cin.available()];
		if (cin.read(bytes) != bytes.length)
			throw new IOException("Could not read file");
		return bytes;
	}
	private static final char[] hexDigits = { '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };
	public static void dumpBytes(PrintStream out, byte[] ba) {
		StringBuffer hex = new StringBuffer();
		StringBuffer asc = new StringBuffer();
		for (int i = 0; i < ba.length; i++) {
			byte b = ba[i];
			int d1 = (b>>4)&0x0F;
			int d0 = (b>>0)&0x0F;
			hex.append(hexDigits[d1]).append(hexDigits[d0]).append(' ');
			asc.append(Character.isISOControl((char)b)?'.':(char)b);
			if (i % 16 == 15) {
				hex.append(' ').append(asc.toString());
				out.println(hex.toString());
				hex = new StringBuffer();
				asc = new StringBuffer();
			}
		}
		for (int j = ba.length % 16; j < 16; j++)
			hex.append("   ");
		hex.append(' ').append(asc.toString());
		out.println(hex.toString());
	}
}

