/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 *  
 */


package x10.x10rt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
//import java.lang.ProcessBuilder.Redirect;  // Java 7.  Sigh.

public class Launcher {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Example Usage: java -cp .:../stdlib/x10.jar x10.x10rt.Launcher HelloWholeWorld hi");
			return;
		}
		
		// determine the number of places to launch
		int numPlaces = 1;
		try { numPlaces = Integer.parseInt(System.getenv(SocketTransport.X10_NPLACES));
		} catch (NumberFormatException e) {} // run single place
		
		final InputStream[] inFrom = new InputStream[numPlaces];
		OutputStream[] outTo = new OutputStream[numPlaces];
		Process[] child = new Process[numPlaces];
		String[] connectionInfo = new String[numPlaces];
		String[] connectionDebugInfo = new String[numPlaces];
		
		// gather up the class and arguments to run
		
		boolean isDebug = args[0].equals("-debug")? true : false;
		
		String[] newArgs;
		
		if (isDebug){
			newArgs = new String[args.length+8];
			newArgs[0] = System.getProperty("java.home").concat("/bin/java");
			newArgs[1] = "-XX:+UseParallelGC";
			newArgs[2] = "-Xdebug";
			newArgs[3] = "-Xrunjdwp:transport=dt_socket,server=y,suspend=y";
			newArgs[4] = "-ea";
			newArgs[5] = "-Djava.library.path="+System.getProperty("java.library.path");
			newArgs[6] = "-Djava.class.path="+System.getProperty("java.class.path");
			newArgs[7] = "-Djava.util.logging.config.file="+System.getProperty("java.util.logging.config.file");
			newArgs[8] = SlaveLauncher.class.getName();
			for (int i=1; i<args.length; i++)
				newArgs[i+8] = args[i];
		} else {
			newArgs = new String[args.length+6];
			newArgs[0] = System.getProperty("java.home").concat("/bin/java");
			newArgs[1] = "-ea";
			newArgs[2] = "-Djava.library.path="+System.getProperty("java.library.path");
			newArgs[3] = "-Djava.class.path="+System.getProperty("java.class.path");
			newArgs[4] = "-Djava.util.logging.config.file="+System.getProperty("java.util.logging.config.file");
			newArgs[5] = SlaveLauncher.class.getName();
			for (int i=0; i<args.length; i++)
				newArgs[i+6] = args[i];
		}
		
		// launch the places
		ProcessBuilder pb = new ProcessBuilder(newArgs);
		pb.environment().remove("X10_NPLACES");
		pb.environment().remove("X10_LAUNCHER_PLACE");
		//pb.redirectError(Redirect.INHERIT);
		for (int i=0; i<numPlaces; i++) {
			try {
				//System.err.println("Launcher: launching place "+i+" with: ");
				//for (int j=0; j<newArgs.length; j++)
				//	System.err.print(newArgs[j]+" ");
				////System.err.println();
				child[i] = pb.start();
			    //child[i] = Runtime.getRuntime().exec(newArgs, null);
			    inFrom[i] = child[i].getInputStream();
			    outTo[i] = child[i].getOutputStream();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (isDebug){
			// gather up the debug connection info from each place
			for (int i=0; i<numPlaces; i++) {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(inFrom[i]), 1024);
					connectionDebugInfo[i] = reader.readLine();
					while (connectionDebugInfo[i] == null) {
						Thread.yield();
						connectionDebugInfo[i] = reader.readLine();
					} 
					// The following line is needed by remote debugger in X10DT -- do not remove!
					System.err.println("Place "+i+" - "+connectionDebugInfo[i]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// gather up the connection info from each place
				for (int i=0; i<numPlaces; i++) {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(inFrom[i]), 1024);
						connectionInfo[i] = reader.readLine();
						while (connectionInfo[i] == null) {
							Thread.yield();
							connectionInfo[i] = reader.readLine();
						} 
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
		
		// tell each place its ID, and where to find the others
		for (int i=0; i<numPlaces; i++) {
			try {
				PrintStream writer = new PrintStream(outTo[i]);
				//System.err.println("sending placecount of "+numPlaces+" to place "+i);
			    writer.println(numPlaces);
			    writer.flush();
			    for (int j=0; j<numPlaces; j++) {
		    		writer.println(connectionInfo[j]);
			    	writer.flush();
			    }
			    //System.err.println("finished sending connection information to place "+i);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// since we can't use ProcessBuilder.redirectError(Redirect.INHERIT), we do this instead...
		// TODO: there are better ways to do this.
		for (int i=0; i<numPlaces; i++) {
			Thread t = new Thread(new Piper(child[i].getErrorStream(), System.err));
			t.setName("pipe stderr for place "+i);
			t.start();
		}
		
		// TODO: there are better ways to do this.
		for (int i=0; i<numPlaces; i++) {
			Thread t = new Thread(new Piper(inFrom[i], System.out));
			t.setName("pipe stdout for place "+i);
			t.start();
		}
		
		// TODO: send stdin to place 0
		
		// places have exited.  Pass the exit code of place 0 on
		while (true) {
			try {
				System.exit(child[0].waitFor());
			} catch (InterruptedException e) {
				Thread.yield();
			}
		}
	}
	
	private static class Piper implements Runnable {
		private BufferedReader reader;
		private PrintStream out;
		
		Piper(InputStream in, PrintStream out){
			reader = new BufferedReader(new InputStreamReader(in), 10240);
			this.out = out;
		}
		@Override
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					if (line == null)
						break;
					else {
						this.out.println(line);
						this.out.flush();
					}
				}
			} catch (IOException e) {}
		}
	}
	
	public static class SlaveLauncher {
		public static void main(String[] args) {
			try {
				//System.err.println("SlaveLauncher alive, will launch "+args[0]);
				
				// start up X10RT
				System.setProperty("x10.NO_PRELOAD_CLASSES", "false");
				System.setProperty("X10RT_IMPL", "JavaSockets"); // use java communication library
				//x10.runtime.impl.java.Runtime userClass = (x10.runtime.impl.java.Runtime) Class.forName(args[0]).newInstance();
				//String connectionInfo = X10RT.init_library(userClass, false);
				String connectionInfo = X10RT.init_library(null);

				// write connection string to the parent
				System.out.println(connectionInfo);
				System.out.flush();
				
				// get all connection strings
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in), 1024);
				//System.err.println("about to read in the number of places");
				String line = reader.readLine();
				while (line == null) {
					Thread.yield();
					line = reader.readLine();
				}
				int numPlaces = Integer.parseInt(line);
				//System.err.println("determined there are "+numPlaces+" places");
				int myPlace = 0;
				String[] placeStrings = new String[numPlaces];
				for (int i=0; i<numPlaces; i++) {
					placeStrings[i] = reader.readLine();
					while (placeStrings[i] == null) {
						Thread.yield();
						placeStrings[i] = reader.readLine();
					}
					if (placeStrings[i].equals(connectionInfo)) {
						myPlace = i;
						//System.err.println("I am place "+i);
					}
					//else
						//System.err.println("determined place "+i+" is at \""+placeStrings[i]+"\"");
				}
				
				// link up
				if (!X10RT.connect_library(myPlace, placeStrings)) {
					//System.err.println("Place "+myPlace+" was unable to establish links across places");
					return;
				}
				
				// run the user program
				String[] lessArgs = new String[args.length-1];
				for (int i=0; i<lessArgs.length; i++)
					lessArgs[i] = args[i+1];
				try {
					Class.forName(args[0]+"$$Main").getMethod("main", String[].class).invoke(null, (Object) lessArgs);
				} catch (Throwable e) {
					e.printStackTrace();
				}				
        	}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}	
}
