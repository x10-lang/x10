/*
 * Created on Sep 28, 2004
 */
package x10.runtime;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;



/**
 * This class provides the configuration for the X10 runtime.
 * The configuration is a set of values that can be used to
 * configure the runtime, for example in order to tune performance.
 * The configuration class provides mechanisms that allow the user
 * to change the default configuration values in a simple and
 * systematic manner.
 *
 * A typical use of the Configuration is to just read the
 * respective static fields, i.e.:
 * *
 * <code>
 *  this.setMinimumPoolSize(Configuration.MINIMUM_POOL_SIZE);
 *  this.setKeepAliveTime(Configuration.KEEP_ALIVE_TIME);
 * </code>
 *
 * <br>
 * Clients are expected to add static fields to the configuration
 * class and initialize them to the respective default values,
 * i.e.:
 *
 * <code>
 * public static int MINIMUM_POOL_SIZE = 2;
 * </code>
 *
 * Note that these fields MUST NOT be final.  The reason is that
 * these are just the default values, the Configuration class
 * may initialize them to different values based on a configuration
 * file provided by the user.
 *
 * <p>
 * Users can change these values by providing the name of a configuration
 * file in the system properties under "x10.configuration".  By passing
 * the option -Dx10.configuration=myconfig the file 'myconfig' will be
 * read by the static initializer of configuration.  The file is expected
 * to contain a mapping of (static) field names to values.  The static
 * initializer will then set the respective static fields to those values
 * (using reflection).  The format of the configuration file is described
 * in the JDK documentation for the method "java.util.Properties.load()".
 * <br>
 *
 * Note that clients should NEVER set the static fields (even though they
 * are public and non-final), after the static initializer runs the
 * value of static Configuration fields should never change.
 *
 * <p>
 * All static fields in this class must be of types String, int, float, double,
 * long, short, char, byte or boolean.
 *
 * @author Christian Grothoff
 *
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: replaced NUMBER_OF_ACTIVITIES_PER_PLACE by INIT_THREADS_PER_PLACE.
 */
public final class Configuration {

    public static int NUMBER_OF_LOCAL_PLACES = 4;
    public static int NUMBER_OF_VMS = 1;
    public static int INIT_THREADS_PER_PLACE = 3;
   
    /**
     * If ABSTRACT_EXECUTION_STATS = true, then X10 runtime performs additional instrumentation to dump
     * out abstract parallel execution statistics for the X10 program.  The default value is false.
     */
    public static boolean ABSTRACT_EXECUTION_STATS = false;

    /**
     * If ABSTRACT_EXECUTION_TIMES = true, then also dump out unblocked execution in each place.  The time that an activity is spent blocked
	 * in the X10 runtime is not counted.  However, this is still an approximate estimate because it does account for time that an activity is not
	 * executing because its Java thread in the thread pool does not have an available processor.
	 * 
	 * NOTE: this option is only enabled when ABSTRACT_EXECUTION_STATS = true;
     */
    public static boolean ABSTRACT_EXECUTION_TIMES = false;

    /** this check does not work -- it causes spurious warnings */
    public static boolean BAD_PLACE_RUNTIME_CHECK = true;

    /**
     * The name of the main class of the application.
     */
    public static String MAIN_CLASS_NAME = null;

    /**
     * Enables new (experimental) ateach and foreach optimization (code by
     * Christian Grothoff and Raj Barik).  Default to off until that code is
     * sufficiently tested...
     */
    public static boolean OPTIMIZE_FOREACH = false;

    /**
     * Pre-load all classes recursively on startup.  This is needed for JIT
     * optimizations.  Default to off until the initialization sequence is
     * sufficiently tested...
     */
    public static boolean PRELOAD_CLASSES = false;

    /**
     * Pre-load all strings in all classes on startup.  This is needed for
     * JIT optimizations.  This option only takes effect if PRELOAD_CLASSES
     * is also on.  Default to off until the JIT can actually take advantage
     * of it.
     */
    public static boolean PRELOAD_STRINGS = false;

    /**
     * Which shared libraries should be loaded?  The format
     * is libraryname[:libraryname]*.  Null or "" are valid
     * entries (for not loading any libraries).
     */
    public static String LOAD = null;

    private static boolean done_ = false;

    public static boolean isMultiNodeVM() { return NUMBER_OF_VMS > 1;}
    public static boolean runBootHere = true;

    /**
     * Parses the command line.  This allows the user to specify
     * options also on the command line (in addition to the
     * configuration file and the defaults).  The name of the
     * main class is the first argument that does not start with
     * a "-".  All arguments after the main class are returned
     * and should be passed to the application.
     *
     * @param args arguments.  Example: -BAD_PLACE_RUNTIME_CHECK=true
     * @return the arguments for the application
     */
    public static String[] parseCommandLine(String[] args) {
        assert !done_;

        if (args == null)
            return new String[0];

        int pos = 0;
        while (pos < args.length && args[pos].length() > 0 && args[pos].charAt(0) == '-') {
        	// vj: added to allow the runtime to use Polyglot's report mechanism
            if (args[pos].equals("-report")) {
                pos++;
                StringTokenizer st = new StringTokenizer(args[pos], "=");
                String topic = ""; int level = 0;
                if (st.hasMoreTokens()) topic = st.nextToken();
                if (st.hasMoreTokens()) {
                    try {
                        level = Integer.parseInt(st.nextToken());
                    }
                    catch (NumberFormatException e) {}
                }

                Report.addTopic(topic, level);
                pos++;
                continue;
            }
            
            int eq = args[pos].indexOf('=');
            String optionName;
            String optionValue = null;
            if (eq == -1) {
                optionName = args[pos].substring(1);
            } else {
                optionName = args[pos].substring(1, eq);
                optionValue = args[pos].substring(eq+1);
            }
            set(optionName, optionValue);
            pos++;
        }
        MAIN_CLASS_NAME = args[pos++];
        // vj hack to let Eclipse x10 command run with ${resource_loc}

        // Sanity checks for values.
        if (NUMBER_OF_LOCAL_PLACES < 0) {
        	System.err.println("Negative value |" + NUMBER_OF_LOCAL_PLACES
        			+ "| for NUMBER_OF_LOCAL_PLACES rejected. Set to 4." );
        	NUMBER_OF_LOCAL_PLACES = 4;
        }
        if (INIT_THREADS_PER_PLACE < 0) {
        	System.err.println("Negative value |" + INIT_THREADS_PER_PLACE
        			+ "| for INIT_THREADS_PER_PLACE rejected. Set to 3." );
        	INIT_THREADS_PER_PLACE = 3;
        }
        // get rid of prefix pathname.
        MAIN_CLASS_NAME = MAIN_CLASS_NAME.substring(MAIN_CLASS_NAME.lastIndexOf("\\")+1);
        if (MAIN_CLASS_NAME.endsWith(".java"))
        	MAIN_CLASS_NAME = MAIN_CLASS_NAME.substring(0, MAIN_CLASS_NAME.length()-5);
        else  if (MAIN_CLASS_NAME.endsWith(".x10"))
        	MAIN_CLASS_NAME = MAIN_CLASS_NAME.substring(0, MAIN_CLASS_NAME.length()-4);
        int aa = args.length-pos;
        String[] appArgs = new String[aa];
        System.arraycopy(args, pos, appArgs, 0, aa);
        if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() +  " user class is |"
    				+ Configuration.MAIN_CLASS_NAME+ "|.");
    	}
        return appArgs;
    }

    /**
     * Method to obtain the name of the configuration file used
     * for the current configuration.
     * @return null if no file is given and only default values
     *  are used
     */
    private static String getConfigurationFileName_() {
        return System.getProperty("x10.configuration");
    }

    /**
     * Read the configuration file (if specified) and initialize the
     * globals.
     */
    static {
        String cfg = getConfigurationFileName_();
        if (cfg != null) {
            try {
                Properties props = new Properties();
                FileInputStream fis = new FileInputStream(cfg);
                byte[] data = new byte[fis.available()];
                if (data.length != fis.read(data))
                	throw new Error();
                String s = new String(data).replace('\\','/');
                props.load(new ByteArrayInputStream(s.getBytes()));
               
                Iterator i = props.keySet().iterator();
                while (i.hasNext()) {
                    String key = (String) i.next();
                    String val = props.getProperty(key);
                    set(key, val);
                } // end of 'for each configuration directive'
            } catch (IOException io) {
                System.err.println("Failed to read configuration file " + cfg + ": " + io);
                throw new Error(io);
            }
        } // end of 'have configuration file'
    } // end of static initializer

    private static void set(String key, String val) {
        Class c = Configuration.class;
        int idx = 0;
        String fld = null;
        try {
            if (key.indexOf('[') > 0) {
                idx = Integer.parseInt(key.substring(key.indexOf('[')+1,key.indexOf(']')));
                fld = key.substring(key.indexOf('.')+1);
                key = key.substring(0, key.indexOf('['));
            }
            Field f = c.getField(key);
            Class t = f.getType();
            Object o = null;
            if (fld != null) {
                if (t.isArray()) {
                    if (t.getComponentType().isPrimitive()) {
                    } else {
                        o = Array.get(f.get(null), idx);
                        if (o == null) {
                            Array.set(f.get(null), idx, o = t.getComponentType().newInstance());
                        }
                        f = o.getClass().getField(fld);
                        t = f.getType();
                    }
                } else {
                    System.err.println(key + " is not an array");
                }
            }
            if (t == String.class) {
                f.set(o, val);
            } else if (t == Integer.TYPE) {
                f.setInt(o, new Integer(val).intValue());
            } else if (t == Float.TYPE) {
                f.setFloat(o, new Float(val).floatValue());
            } else if (t == Double.TYPE) {
                f.setDouble(o, new Double(val).doubleValue());
            } else if (t == Long.TYPE) {
                f.setLong(o, new Long(val).longValue());
            } else if (t == Short.TYPE) {
                f.setShort(o, new Short(val).shortValue());
            } else if (t == Byte.TYPE) {
                f.setByte(o, new Byte(val).byteValue());
            } else if (t == Character.TYPE) {
                if (val.length() != 1)
                    System.err.println("Parameter" + key + " only takes on character,"+
                                       " using only the first character of configuration"+
                                       " value >>" + val + "<<");
                f.setChar(o, new Character(val.charAt('0')).charValue());
            } else if (t == Boolean.TYPE) {
            	if (val.equalsIgnoreCase("true")) {
            		f.setBoolean(null, true);
            	} else if (val.equalsIgnoreCase("false")) {
            		f.setBoolean(null, false);
            	} else {
            		System.err.println("Parameter |" + key + "| expects a boolean, not |"
            				+ val + "|. Ignored.");
            	}
            }
        } catch (NoSuchFieldException nsfe) {
            System.err.println("Parameter " + key + " not found, configuration directive ignored.");
        } catch (InstantiationException ie) {
            System.err.println("Failed to create object for " + key);
            throw new Error(ie);
        } catch (IllegalAccessException iae) {
            System.err.println("Wrong permissions for field " + key + ": " + iae);
            throw new Error(iae);
        } catch (NumberFormatException z) {
        	System.err.println("Parameter |" + key + "| expects a number, not |" + val + "|. Ignored.");
        }
    }
} // end of Configuration

