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
import java.net.*;

/**
 * A simple URL retriever.
 * Gets two parameters: URL and filename.  Retrieves the content of the URL
 * into the file unless the file is newer.
 */
public class HTTPGet {
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				System.err.println("Usage: HTTPGet url filename");
			}
			boolean verbose = System.getProperty("x10.runtime.HTTPGet.verbose", "false").equals("true");
			URL src = new URL(args[0]);
			File dst = new File(args[1]);
			URLConnection source = src.openConnection();
			System.out.println(source + " => " + dst);
			long timestamp = source.getLastModified();
			if (verbose) {
				System.out.println("Source URL: "+timestamp);
				if (!dst.exists())
					System.out.println("Destination file does not exist");
				else
					System.out.println("Destination file: "+dst.lastModified());
			}
			if (!dst.exists() || timestamp > dst.lastModified()) {
				InputStream in = source.getInputStream();
				FileOutputStream out = new FileOutputStream(dst);
				byte[] buf = new byte[2048];
				int n = 0;
				while ((n = in.read(buf)) != -1)
					out.write(buf, 0, n);
				out.close();
				in.close();
			} else {
				System.out.println("    Not modified - so not downloaded");
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}

