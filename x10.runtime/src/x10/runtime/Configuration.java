/*
 * Created on Sep 28, 2004
 */
package x10.runtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Properties;

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
    
    public static int NUMBER_OF_LOCAL_PLACES = 2;
    
    public static int PLACE_MINIMAL_THREAD_POOL_SIZE = 2;
    
    public static int PLACE_THREAD_KEEPALIVE_TIME = 5000; // in millis

    /**
     * Which statistics plugins should be enabled or disabled?
     * Reserved values are "none" and "all".  Otherwise list
     * the specific plugins that you want to disable.
     */
    public static String STATISTICS_DISABLE = "none";
    
    /**
     * Method to obtain the name of the configuration file used
     * for the current configuration.
     * @return null if no file is given and only default values
     *  are used
     */
    public static String getConfigurationFileName() {
        return System.getProperty("x10.configuration");
    }
    
    /**
     * Read the configuration file (if specified) and initialize the
     * globals.
     */
    static {        
        String cfg = getConfigurationFileName();
        if (cfg != null) {
            Class c = Configuration.class;
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(cfg));
                Iterator i = props.keySet().iterator();
                while (i.hasNext()) {
                    String key = (String) i.next();
                    String val = props.getProperty(key);
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
                            f.setBoolean(null, new Boolean(val).booleanValue());
                        }
                    } catch (NoSuchFieldException nsfe) {
                        System.err.println("Field " + key + " not found, configuration directive ignored.");
                    } catch (IllegalAccessException iae) {
                        System.err.println("Wrong permissions for field " + key + ": " + iae);
                        System.exit(-1);                        
                    }
                } // end of 'for each configuration directive'
            } catch (IOException io) {
                System.err.println("Failed to read configuration file " + cfg + ": " + io);
                System.exit(-1);
            } 
        } // end of 'have configuration file'       
    } // end of static initializer
    
} // end of Configuration
