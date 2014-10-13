/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

package x10.x10rt.yarn;

import java.lang.reflect.Method;

// This class is needed to wrap the user's X.$Main() in yarn
public class X10MainRunner {
     
	// first argument is user's main class.  The rest are passed on
	public static void main(String[] args) {
		try {
			Class<?> UsersMainClass = Class.forName(args[0]+"$$Main");
			Method usersMainMethod = UsersMainClass.getDeclaredMethod("main", String[].class);
			usersMainMethod.setAccessible(true);
			
			String[] fewerArgs = new String[args.length-1];
			for (int i=0; i<fewerArgs.length; i++)
				fewerArgs[i] = args[i+1];
			
			usersMainMethod.invoke(null, (Object)fewerArgs);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
}
