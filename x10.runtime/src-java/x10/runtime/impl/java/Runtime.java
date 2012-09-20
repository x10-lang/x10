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

package x10.runtime.impl.java;

import x10.core.io.InputStream;
import x10.core.io.OutputStream;
import x10.io.InputStreamReader;
import x10.io.OutputStreamWriter;
import x10.io.Reader;
import x10.io.Writer;
import x10.lang.FinishState;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;
import x10.x10rt.X10RT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class Runtime implements x10.core.fun.VoidFun_0_0 {

    private static final long serialVersionUID = 1L;

    public RuntimeType<?> $getRTT() {
        return null;
    }

    public Type<?> $getParam(int i) {
        return null;
    }

    protected String[] args;

    // not used
//    // constructor just for allocation
//    public Runtime(java.lang.System[] $dummy) {
//        // TODO
//        // super($dummy);
//    }
//
//    public Runtime $init() {
//        return this;
//    }

    public Runtime() {}

    /**
     * Body of main java thread
     */
    protected void start(final String[] args) {
        this.args = args;

        // load libraries
        String property = System.getProperty("x10.LOAD");
        if (null != property) {
            String[] libs = property.split(":");
            for (int i = libs.length - 1; i >= 0; i--)
                System.loadLibrary(libs[i]);
        }

        // @MultiVM, the following is right ??
        // FIXME: By here it is already too late because statics in Runtime
        // refer to X10RT. Need to restructure this so that we can call
        // X10RT.init explicitly from here.
        X10RT.init();

        x10.lang.Runtime.get$staticMonitor();
        x10.lang.Runtime.get$STRICT_FINISH();
        x10.lang.Runtime.get$NTHREADS();
        x10.lang.Runtime.get$MAX_THREADS();
        x10.lang.Runtime.get$STATIC_THREADS();
        x10.lang.Runtime.get$WARN_ON_THREAD_CREATION();
        x10.lang.Runtime.get$BUSY_WAITING();

        java.lang.Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
            public void run() {
                System.out.flush();
            }
        });

        // start and join main x10 thread in place 0
        x10.lang.Runtime.Worker worker = new x10.lang.Runtime.Worker(0);
        worker.body = this;
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
        }

        // shutdown
        X10RT.X10_EXITING_NORMALLY = true;
        System.exit(exitCode);
    }

    // body of main activity
    static class $Closure$Main implements x10.core.fun.VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
        private final Runtime out$;
        private final x10.array.Array<String> aargs;

        public void $apply() {
            // catch and rethrow checked exceptions (closures cannot throw
            // checked exceptions)
            try {
                // execute root x10 activity
                out$.runtimeCallback(aargs);
            } catch (java.lang.RuntimeException e) {
                throw e;
            } catch (java.lang.Error e) {
                throw e;
            } catch (java.lang.Throwable t) {
                throw new x10.lang.WrappedThrowable(t);
            }
        }

        $Closure$Main(Runtime out$, x10.array.Array<String> aargs) {
            this.out$ = out$;
            this.aargs = aargs;
        }

        public RuntimeType<?> $getRTT() { return $RTT; }

        public Type<?> $getParam(int i) { return null; }

        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            throw new UnsupportedOperationException("Serialization not supported for " + getClass());
        }

        public short $_get_serialization_id() {
            throw new UnsupportedOperationException("Serialization not supported for " + getClass());
        }
    }

    public void $apply() {
        // try { Class.forName("x10.lang.Place"); } catch
        // (ClassNotFoundException e) { }

        // preload classes by default
        if (!Boolean.getBoolean("x10.NO_PRELOAD_CLASSES")) {
            // System.out.println("start preloading of classes");
            Class<?> userMain = this.getClass().getEnclosingClass();
            String extraClassesString = System.getProperty("x10.EXTRA_PRELOAD_CLASSES");
            java.util.ArrayList<String> extraClasses;
            if (extraClassesString != null) {
                extraClasses = new java.util.ArrayList<String>();
                java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(extraClassesString, java.io.File.pathSeparator);
                while (tokenizer.hasMoreTokens()) {
                    String name = tokenizer.nextToken();
                    extraClasses.add(name);
                }
            } else {
                extraClasses = null;
            }
            x10.runtime.impl.java.PreLoader.preLoad(userMain, extraClasses, Boolean.getBoolean("x10.PRELOAD_STRINGS"));
        }

        // Obtain message ID's for each async
        if (X10RT.numPlaces() > 1) {
            DeserializationDispatcher.registerHandlers();
        }

        // build up Array[String] for args
        final x10.array.Array<String> aargs = new x10.array.Array<String>((java.lang.System[]) null, Types.STRING).x10$array$Array$$init$S(args.length);
        for (int i = 0; i < args.length; i++) {
            aargs.$set__1x10$array$Array$$T$G(i, args[i]);
        }

        // execute root x10 activity
        try {
            // start xrx
            x10.lang.Runtime.start(
            // body of main activity
                                   new $Closure$Main(this, aargs));
        } catch (java.lang.Throwable t) {
            // XTENLANG=2686: Unwrap UnknownJavaThrowable to get the original Throwable object
            if (t instanceof x10.lang.WrappedThrowable) t = t.getCause();
            t.printStackTrace();
            setExitCode(1);
        }
    }

    /**
     * User code provided by Main template - start xrx runtime - run main
     * activity
     */
    public abstract void runtimeCallback(x10.array.Array<java.lang.String> args) throws java.lang.Throwable;

    /**
     * Application exit code
     */
    private static int exitCode = 0;

    /**
     * Set the application exit code
     */
    public static void setExitCode(int code) {
        exitCode = code;
    }

    /**
     * The number of places in the system
     */
    public static int MAX_PLACES = X10RT.numPlaces();
    
    /**
     * Disable Assertions
     */
    public static final boolean DISABLE_ASSERTIONS = Boolean.getBoolean("x10.DISABLE_ASSERTIONS");


    /**
     * Trace serialization
     */
    public static final boolean TRACE_SER = Boolean.getBoolean("x10.TRACE_SER");

    /**
     * Trace static init
     */
    public static final boolean TRACE_STATIC_INIT = Boolean.getBoolean("X10_TRACE_STATIC_INIT");

    /**
     * Emit detail serialization traces for java serialization. Using for debugging in preliminary stage
     */
    public static final boolean TRACE_SER_DETAIL = Boolean.getBoolean("x10.TRACE_SER_DETAIL");
    
    /**
     * Time serialization/deserialization operations.
     */
    public static final boolean PROF_SER = Boolean.getBoolean("x10.PROF_SER");
    
    /**
     * Minimum threshold in for reporting serialization/deserialization times.
     * The property is a value in milliseconds, we convert to nanoSeconds for efficiency when using System.nanoTime.
     * The default value is 10ms.
     */
    public static final long PROF_SER_FILTER = 1000 * 1000 * Long.getLong("x10.PROF_SER_FILTER", 10);

    public static final boolean X10_TRACE_ANSI_COLORS = Boolean.getBoolean("X10_TRACE_ANSI_COLORS");

    public static final String ANSI_RESET = X10_TRACE_ANSI_COLORS? "\u001b[1;0m" :"";
    public static final String ANSI_BOLD = X10_TRACE_ANSI_COLORS? "\u001b[1;1m" :"";
    public static final String ANSI_CYAN = X10_TRACE_ANSI_COLORS? "\u001b[1;36m" :"";
    public static final String TRACE_MESSAGE = "SS";
    public static final String STATIC_INIT_MESSAGE = "SI";

    public static void printTraceMessage(String message) {
        print(TRACE_MESSAGE, ANSI_CYAN, message);
    }

    public static void printStaticInitMessage(String message) {
        print(STATIC_INIT_MESSAGE, ANSI_CYAN, message);
    }

    private static void print(String type, String col, String message) {
        System.out.println(ANSI_BOLD + X10RT.here() + ": " + col + type + ": " + ANSI_RESET + message);
    }

    public static void runAsyncAt(int place, x10.core.fun.VoidFun_0_0 body, FinishState finishState, int endpoint) {
         runAsyncAt(place, body, finishState);
    }

    public static void runAsyncAt(int place, x10.core.fun.VoidFun_0_0 body, FinishState finishState) {
        short sid = body.$_get_serialization_id();
        int messageID = DeserializationDispatcher.getMessageID(sid);
        try {
            byte[] bytes = serialize(body, finishState);
            x10.x10rt.MessageHandlers.runClosureAtSend(place, bytes.length, bytes, messageID);
        } catch (IOException e) {
            java.lang.RuntimeException xe = x10.runtime.impl.java.ThrowableUtils.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    /**
     * Synchronously executes body at place(id)
     */
    public static void runClosureAt(int place, x10.core.fun.VoidFun_0_0 body) {
        runAt(place, body);
    }

    /**
     * Synchronously executes body at place(id)
     */
    public static void runClosureCopyAt(int place, x10.core.fun.VoidFun_0_0 body) {
        runAt(place, body);
    }

    /**
     * Copy body (same place)
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T body) {

    	try {
    		if (TRACE_SER_DETAIL) {
    			System.out.println("Starting deepCopy of " + body.getClass());
    		}
    		long start = PROF_SER ? System.nanoTime() : 0;

    		byte[] ba = serialize(body);
    		DataInputStream ois = new DataInputStream(new ByteArrayInputStream(ba));
    		X10JavaDeserializer deserializer = new X10JavaDeserializer(ois);
    		body = (T) deserializer.readRef();

    		if (PROF_SER) {
    			long stop = System.nanoTime();
    			long duration = stop-start;
    			if (duration >= PROF_SER_FILTER) {
    				System.out.println("Deep copy took "+(((double)duration)/1e6)+" ms.");
    			}
    		}
    		if (TRACE_SER_DETAIL) {
    			System.out.println("Done with deserialization for deepCopy of " + body.getClass());
    		}
    		return body;
    	} catch (java.io.IOException e) {
    		java.lang.RuntimeException xe = x10.runtime.impl.java.ThrowableUtils.getCorrespondingX10Throwable(e);
    		xe.printStackTrace();
    		throw xe;
    	}
    }

    public static <T> byte[] serialize(T body) throws IOException {
    	if (TRACE_SER_DETAIL) {
    		System.out.println("Starting serialization for runAtAll  " + body.getClass());
    	}
    	long start = PROF_SER ? System.nanoTime() : 0;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	DataOutputStream oos = new DataOutputStream(baos);
    	X10JavaSerializer serializer = new X10JavaSerializer(oos);
    	if (body instanceof X10JavaSerializable) {
    		serializer.write((X10JavaSerializable) body);
    	} else {
    		serializer.write(body);
    	}
    	oos.close();
    	byte[] ba = baos.toByteArray();
    	if (PROF_SER) {
    		long stop = System.nanoTime();
    		long duration = stop-start;
    		if (duration >= PROF_SER_FILTER) {
    			System.out.println("Serialization took "+(((double)duration)/1e6)+" ms.");
    		}
    	}
    	if (TRACE_SER_DETAIL) {
    		System.out.println("Done with serialization for runAtAll " + body.getClass());
    	}
    	return ba;
    }

    // not used
//    public static <T> byte[] serializeUsingReflection(T body) throws IOException {
//    	long start = PROF_SER ? System.nanoTime() : 0;
//    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    	DataOutputStream oos = new DataOutputStream(baos);
//    	X10JavaSerializer serializer = new X10JavaSerializer(oos);
//    	serializer.writeObjectUsingReflection(body);
//    	oos.close();
//    	byte[] ba = baos.toByteArray();
//    	if (PROF_SER) {
//    		long stop = System.nanoTime();
//    		long duration = stop-start;
//    		if (duration >= PROF_SER_FILTER) {
//    			System.out.println("Serialization took "+(((double)duration)/1e6)+" ms.");
//    		}
//    	}
//    	return ba;
//    }


	private static Class<? extends Object> hadoopWritableClass = getHadoopClass();
	private static Class<? extends Object> getHadoopClass() {
		try {
			return Class.forName("org.apache.hadoop.io.Writable");
		} catch (ClassNotFoundException e) {
			return null;
		}    			
	}
	
	public static boolean implementsHadoopWritable(Class<? extends Object> clazz) {
		if(hadoopWritableClass == null) {
			return false;
		}
		return hadoopWritableClass.isAssignableFrom(clazz);
	}
	
	private static byte[] serialize(x10.core.fun.VoidFun_0_0 body, FinishState finishState) throws IOException {
		if (TRACE_SER_DETAIL) {
			System.out.println("Starting serialization for runAtAll  " + body.getClass());
		}
		long start = PROF_SER ? System.nanoTime() : 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream oos = new DataOutputStream(baos);
		X10JavaSerializer serializer = new X10JavaSerializer(oos);
		serializer.write(finishState);
		serializer.recordReference(body);
		body.$_serialize(serializer);
		oos.close();
		byte[] ba = baos.toByteArray();
		if (PROF_SER) {
			long stop = System.nanoTime();
			long duration = stop-start;
			if (duration >= PROF_SER_FILTER) {
				System.out.println("Serialization took "+(((double)duration)/1e6)+" ms.");
			}
		}
		if (TRACE_SER_DETAIL) {
			System.out.println("Done with serialization for runAtAll " + body.getClass());
		}
		return ba;
	}
    
    // @MultiVM, add this method
	public static void runAt(int place, x10.core.fun.VoidFun_0_0 body) {
		byte[] msg;
		try {
			// Cannot use the serialize() method here cause we need to serialize the outer serialization ID too
			// (This is not serialized by the serialize method as an optimization)
			if (TRACE_SER_DETAIL) {
				System.out.println("Starting serialization for runAtAll  " + body.getClass());
			}
			long start = PROF_SER ? System.nanoTime() : 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(baos);
			X10JavaSerializer serializer = new X10JavaSerializer(oos);
			serializer.recordReference(body);
			body.$_serialize(serializer);
			oos.close();
			msg = baos.toByteArray();
			if (PROF_SER) {
				long stop = System.nanoTime();
				long duration = stop-start;
				if (duration >= PROF_SER_FILTER) {
					System.out.println("Serialization took "+(((double)duration)/1e6)+" ms.");
				}
			}
			if (TRACE_SER_DETAIL) {
				System.out.println("Done with serialization for runAtAll " + body.getClass());
			}

			int msg_id = DeserializationDispatcher.getMessageID(body.$_get_serialization_id());
			int msgLen = msg.length;
			if (X10RT.VERBOSE) System.out.println("@MultiVM: sendJavaRemote");
			x10.x10rt.MessageHandlers.runClosureAtSend(place, msgLen, msg, msg_id);
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new x10.lang.WrappedThrowable(e);
		} finally {
			if (X10RT.VERBOSE) System.out.println("@MULTIVM: finally section");
		}
	}

    // Special version of runAt for broadcast type communication
    // (Serialize once, run everywhere)

    public static void runAtAll(boolean includeHere, byte[] msg, short serializationID) {
        int msg_id = DeserializationDispatcher.getMessageID(serializationID);
        int hereId = X10RT.here();
        for (int place = hereId + 1; place < Runtime.MAX_PLACES; ++place) {
            x10.x10rt.MessageHandlers.runClosureAtSend(place, msg.length, msg, msg_id);
        }
        int endPlace = includeHere ? hereId : hereId - 1;
        for (int place = 0; place <= endPlace; ++place) {
            x10.x10rt.MessageHandlers.runClosureAtSend(place, msg.length, msg, msg_id);
        }
    }

    /**
     * @MultiVM: Return true if place(id) is local to this node
     */
    public static boolean local(int id) {
        int hereId = X10RT.here();
        return (hereId == id);
    }

    /**
     * @MultiVM: mapped to Runtime.x10 -> event_probe(): void
     */
    public static void eventProbe() {
        X10RT.probe();
    }

    /**
     * @MultiVM: mapped to Runtime.x10 -> blocking_probe(): void
     */
    public static void blockingProbe() {
        X10RT.blockingProbe();
    }

    /**
     * Load environment variables.
     */
    public static x10.util.HashMap<String, String> loadenv() {
        Map<String, String> env = System.getenv();
        x10.util.HashMap<String, String> map = new x10.util.HashMap<String, String>((java.lang.System[]) null, Types.STRING, Types.STRING).x10$util$HashMap$$init$S();
        for (Map.Entry<String, String> e : env.entrySet()) {
            map.put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(e.getKey(), e.getValue());
        }
        return map;
    }

    public static Reader execForRead(String command) {
        try {
            Process proc = java.lang.Runtime.getRuntime().exec(command);
            return new x10.io.InputStreamReader(new x10.core.io.InputStream(proc.getInputStream()));
        } catch (IOException e) {
            java.lang.RuntimeException xe = x10.runtime.impl.java.ThrowableUtils.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    public static Writer execForWrite(String command) {
        try {
            Process proc = java.lang.Runtime.getRuntime().exec(command);
            return new x10.io.OutputStreamWriter(new x10.core.io.OutputStream(proc.getOutputStream()));
        } catch (IOException e) {
            java.lang.RuntimeException xe = x10.runtime.impl.java.ThrowableUtils.getCorrespondingX10Throwable(e);
            xe.printStackTrace();
            throw xe;
        }
    }

    /**
     * Redirect to the specified user class's main().
     */
    public static void main(String[] args) throws Throwable {
        boolean verbose = false;
        String className = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-v") || arg.equals("-verbose") || arg.equals("--verbose")) {
                verbose = true;
            } else if (arg.charAt(0) == '-') {
                int eq = arg.indexOf('=');
                String key = "x10." + (eq < 0 ? arg.substring(1) : arg.substring(1, eq));
                String value = eq < 0 ? "true" : arg.substring(eq + 1);
                System.setProperty(key, value);
            } else {
                int dotx10 = arg.indexOf(".x10");
                className = (dotx10 < 0 ? arg : arg.substring(0, dotx10)) + "$$Main";
                int len = args.length - i - 1;
                System.arraycopy(args, i + 1, args = new String[len], 0, len);
            }
        }
        if (verbose) {
            System.err.println("Invoking user class: " + className + " with classpath '"
                    + System.getProperty("java.class.path") + "'");
        }
        try {
            Class.forName(className).getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (Exception e) {
            System.err.println("Unable to invoke user program: " + e);
            if (verbose) e.printStackTrace();
        }
    }

    public short $_get_serialization_id() {
		throw new java.lang.UnsupportedOperationException("Cannot serialize " + getClass());
	}

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        throw new java.lang.UnsupportedOperationException("Cannot serialize " + getClass());
	}
}
