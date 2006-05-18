/*
 * Created on Sep 28, 2004
 */
package polyglot.ext.x10;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
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

	private static final String COMPILER_FRAGMENT_DATA_DIRECTORY_desc = "Directory for compiler templates";
	public static String COMPILER_FRAGMENT_DATA_DIRECTORY = "data/";

	private static final String BAD_PLACE_RUNTIME_CHECK_desc = "Generate runtime place checks";
	public static boolean BAD_PLACE_RUNTIME_CHECK = true;

	/**
	 * Parses one argument from the command line.  This allows the user
	 * to specify options also on the command line (in addition to the
	 * configuration file and the defaults).
	 *
	 * @param arg the current argument, e.g., -STATISTICS_DISABLE=all
	 * @return true if the argument is recognized; false otherwise
	 */
	public static boolean parseArgument(String arg) {
		if (arg.charAt(0) != '-')
			return false;
		int eq = arg.indexOf('=');
		String optionName;
		String optionValue = null;
		if (eq == -1) {
			optionName = arg.substring(1);
		} else {
			optionName = arg.substring(1, eq);
			optionValue = arg.substring(eq+1);
		}
		return set(optionName, optionValue);
	}

	private static String typeToString(Class t) {
		if (t.isPrimitive())
			return t.toString();
		if (t.isArray())
			return typeToString(t.getComponentType())+"[]";
		if (t.getPackage() == Package.getPackage("java.lang"))
			return t.getName().substring("java.lang.".length());
		return t.getName();
	}

	public static String[][] options() {
		Class c = Configuration.class;
		Field[] flds = c.getFields();
		String[][] opts = new String[flds.length][];
		for (int i = 0; i < flds.length; i++) {
			Field f = flds[i];
			Class t = f.getType();
			String type = typeToString(t);
			String desc = "";
			Object v = null;
			try {
				Field d = c.getDeclaredField(f.getName()+"_desc");
				desc = (String) d.get(null) + " ";
			} catch (NoSuchFieldException nsfe) {
			} catch (IllegalAccessException iae) {
			}
			try {
				v = f.get(null);
			} catch (IllegalAccessException iae) {
			}
			opts[i] = new String[] { "-"+f.getName()+"="+type, desc+"(default = \""+v+"\")" };
		}
		return opts;
	}

	/**
	 * Method to obtain the name of the configuration file used
	 * for the current configuration.
	 * @return null if no file is given and only default values are used
	 */
	private static String getConfigurationFileName_() {
		return System.getProperty("x10.configuration");
	}

	/**
	 * Read the configuration file (if specified) and initialize the
	 * globals.
	 */
	static {
		readConfiguration();
	}

	public static void readConfiguration() throws Error {
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
	}

	private static boolean set(String key, String val) {
		Class c = Configuration.class;
		int idx=0;
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
					return false;
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
			// System.err.println("Parameter " + key + " not found, configuration directive ignored.");
			return false;
		} catch (InstantiationException ie) {
			System.err.println("Failed to create object for " + key);
			throw new Error(ie);
		} catch (IllegalAccessException iae) {
			System.err.println("Wrong permissions for field " + key + ": " + iae);
			throw new Error(iae);
		} catch (NumberFormatException z) {
			System.err.println("Parameter |" + key + "| expects a number, not |" + val + "|. Ignored.");
		}
		return true;
	}
}

