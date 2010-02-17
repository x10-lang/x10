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

class GetProp {
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println(System.getProperty(args[i]));
		}
	}

}
