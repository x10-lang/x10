/*
 * Created on Sep 28, 2004
 */
package x10.runtime;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
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
 */
public final class Configuration {

    /**
     * The directory where the compiler fragments for translating nodes
     * can be found in.
     */
    public static String COMPILER_FRAGMENT_DATA_DIRECTORY = "data/";

    /**
     * File name for generating PE files.  Use null to not generate
     * PE traces.
     */
    public static String PE_FILE = null; 
    
    public static int NUMBER_OF_LOCAL_PLACES = 4;
    
    /** this check does not work -- it causes spurious warnings */
    public static boolean BAD_PLACE_RUNTIME_CHECK = false;
    
    /**
     * How often should the sampling instrumentations be run?
     * Use -1 for no sampling.
     */
    public static int SAMPLING_FREQUENCY_MS = 50;
    
    /**
     * Should statistics be dumped on exit?
     */
    public static boolean DUMP_STATS_ON_EXIT = false;
    
    /**
     * The name of the main class of the application.
     */
    public static String MAIN_CLASS_NAME = null;
    
    /**
     * Which statistics plugins should be enabled or disabled?
     * Reserved values are "none" and "all".  Otherwise list
     * the specific plugins that you want to disable.
     */
    public static String STATISTICS_DISABLE = "none";

    /**
     * Which shared libraries should be loaded?  The format
     * is libraryname[:libraryname]*.  Null or "" are valid
     * entries (for not loading any libraries).   
     */
    public static String LOAD = null;
    
    private static boolean done_;
    /**
     * Parses the command line.  This allows the user to specify 
     * options also on the command line (in addition to the
     * configuration file and the defaults).  The name of the
     * main class is the first argument that does not start with
     * a "-".  All arguments after the main class are returned
     * and should be passed to the application.
     * 
     * @param args arguments.  Example: -STATISTICS_DISABLE=all
     * @return the arguments for the application
     */
    public static String[] parseCommandLine(String[] args) {
        assert !done_;
        
        if (args == null)
            return new String[0];
        
        int pos = 0;
        while (args[pos].charAt(0) == '-') {
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
                break;
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
        if (SAMPLING_FREQUENCY_MS < 0) {
        	SAMPLING_FREQUENCY_MS = 50;
        	System.err.println("Negative value |" + SAMPLING_FREQUENCY_MS 
        			+ "| for SAMPLING_FREQUENCY_MS rejected. Set to 4." );
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
        try {
            Field f = c.getField(key);
            Class t = f.getType();
            if (t == String.class) {
                f.set(null, val);
            } else if (t == Integer.TYPE) {
                f.setInt(null, new Integer(val).intValue());
            } else if (t == Float.TYPE) {
                f.setFloat(null, new Float(val).floatValue());
            } else if (t == Double.TYPE) {
                f.setDouble(null, new Double(val).doubleValue());
            } else if (t == Long.TYPE) {
                f.setLong(null, new Long(val).longValue());
            } else if (t == Short.TYPE) {
                f.setShort(null, new Short(val).shortValue());
            } else if (t == Byte.TYPE) {
                f.setByte(null, new Byte(val).byteValue());
            } else if (t == Character.TYPE) {
                if (val.length() != 1)
                    System.err.println("Field " + key + " only takes on character,"+
                                       " using only the first character of configuration"+
                                       " value >>" + val + "<<");
                f.setChar(null, new Character(val.charAt('0')).charValue());
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
            System.err.println("Field " + key + " not found, configuration directive ignored.");
        } catch (IllegalAccessException iae) {
            System.err.println("Wrong permissions for field " + key + ": " + iae);
            throw new Error(iae);
        } catch (NumberFormatException z) {
        	System.err.println("Parameter |" + key + "| expects a number, not |" + val + "|. Ignored.");
        }
    }


} // end of Configuration
