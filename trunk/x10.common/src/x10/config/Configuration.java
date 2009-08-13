/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Properties;


/**
 * Base class for the X10 configuration classes.
 * Contains static methods to load the declared fields of the configuration
 * class from a file.
 * 
 * The configuration classes provide mechanisms that allow the user to change
 * the default configuration values in a simple and systematic manner.
 *
 * A typical use of the Configuration is to just read the respective
 * static fields, i.e.:
 *
 * <code>
 * setMinimumPoolSize(Configuration.MINIMUM_POOL_SIZE);
 * setKeepAliveTime(Configuration.KEEP_ALIVE_TIME);
 * </code>
 *
 * <br>
 * Clients are expected to add public static fields to the configuration
 * class and initialize them to the respective default values, i.e.:
 *
 * <code>
 * public static int MINIMUM_POOL_SIZE = 2;
 * </code>
 *
 * Note that these fields MUST NOT be final.  The reason is that
 * these are just the default values, the Configuration classes
 * may initialize them to different values based on a configuration
 * file provided by the user.
 *
 * <br>
 * Users can change these values by providing the name of a configuration
 * file in the system properties under "x10.configuration".  By passing
 * the option -Dx10.configuration=myconfig the file 'myconfig' will be
 * read by the static initializer of the configuration class.  The file is
 * expected to contain a mapping of (static) field names to values.  The
 * static initializer will then set the respective static fields to those
 * values (using reflection).  The format of the configuration file is
 * described in the JDK documentation for the method
 * "java.util.Properties.load()".
 *
 * <br>
 * Note that clients should NEVER set the static fields (even though they
 * are public and non-final), after the static initializer runs the
 * value of static Configuration fields should never change.
 *
 * <br>
 * Each configuration (public static) field in configuration classes can be
 * a String, any primitive type, or an array of those.  If a configuration
 * field ARRAY_OPTION is of an array type, the value for ARRAY_OPTION should
 * be an integer, which declares the size of the array, and its element at
 * index INDEX can be set using the option name ARRAY_OPTION[INDEX].
 *
 * <br>
 * To provide a description for option field OPTION, define a private static
 * final String field OPTION_desc.
 *
 * @author Christian Grothoff
 * @author igor
 */
public abstract class Configuration {

	/**
	 * Set a given field or component in a given class to the given value.
	 * @throws OptionError if the argument is invalid
	 * @throws ConfigurationError if there was a problem processing the argument
	 */
	protected static void set(Class cls, String key, String val)
		throws ConfigurationError, OptionError
	{
		assert (key != null);
		int idx = 0;
		String fld = null;
		try {
			if (key.indexOf('[') > 0) {
				idx = Integer.parseInt(key.substring(key.indexOf('[')+1,key.indexOf(']')));
				fld = key.substring(key.indexOf('.')+1);
				key = key.substring(0, key.indexOf('['));
			}
			Field f = cls.getField(key);
			Class t = f.getType();
			Object o = null;
			// TODO: implement arrays as described above
			// FIXME: do we need support for Object arrays?
			if (fld != null) {
				if (t.isArray()) {
					if (!t.getComponentType().isPrimitive()) {
						o = Array.get(f.get(null), idx);
						if (o == null)
							Array.set(f.get(null), idx,
									  o = t.getComponentType().newInstance());
						f = o.getClass().getField(fld);
						t = f.getType();
					}
				} else
					throw new OptionError(key + " is not an array");
			}
			if (val == null) {
				if (t == Boolean.TYPE)
					val = "true";
				else
					throw new OptionError("Parameter "+key+" expects a value");
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
					throw new OptionError("Parameter "+key+
							" expects exactly one character; got '"+val+"'");
				f.setChar(o, new Character(val.charAt('0')).charValue());
			} else if (t == Boolean.TYPE) {
				if (val.equalsIgnoreCase("true")) {
					f.setBoolean(null, true);
				} else if (val.equalsIgnoreCase("false")) {
					f.setBoolean(null, false);
				} else
					throw new OptionError("Parameter "+key+
							" expects a boolean, not '"+val+"'");
			}
		} catch (NoSuchFieldException nsfe) {
			throw new OptionError("Parameter "+key+" not found");
		} catch (InstantiationException ie) {
			System.err.println("Failed to create object for " + key);
			throw new ConfigurationError(ie);
		} catch (IllegalAccessException iae) {
			System.err.println("Wrong permissions for field " + key + ": " + iae);
			throw new ConfigurationError(iae);
		} catch (NumberFormatException z) {
			throw new OptionError("Parameter "+key+
					" expects a number, not '" + val + "'");
		}
	}

	/**
	 * Obtain the name of the configuration resource used for the current
	 * configuration.
	 * @return null if no resource is given and default values should be used
	 */
	public static String getConfigurationResource() {
		return System.getProperty("x10.configuration");
	}

	/**
	 * Read the configuration from a given resource (if specified) and
	 * initialize the global fields in a given class.
	 *
	 * @param cls the configuration class
	 * @param cfg the configuration resource name
	 * @throws ConfigurationError if unable to process the resource
	 */
	public static void readConfiguration(Class cls, String cfg)
		throws ConfigurationError
	{
		if (cfg == null)
			return;
		try {
			Properties props = new Properties();
			InputStream is = cls.getClassLoader().getResourceAsStream(cfg);
			if (is == null)
				throw new ConfigurationError("Configuration "+cfg+" not found");
			byte[] data = new byte[is.available()];
			if (data.length != is.read(data))
				throw new ConfigurationError("Cannot read entire file");
			String s = new String(data).replace('\\','/');
			props.load(new ByteArrayInputStream(s.getBytes()));
			Iterator i = props.keySet().iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				String val = props.getProperty(key);
				try {
					set(cls, key, val);
				} catch (OptionError e) {
					System.err.println(e.getMessage()+", ignoring.");
				}
			} // end of 'for each configuration directive'
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
	}

	/**
	 * Parse one command line argument into the given configuration class.
	 * This allows the user to specify options also on the command line (in
	 * addition to the configuration file and the defaults).
	 * The argument has to be of the form <code>-FIELD_NAME=value</code>.
	 *
	 * @param cls the configuration class
	 * @param arg the current argument
	 * @throws OptionError if the argument is invalid
	 * @throws ConfigurationError if there was a problem processing the argument
	 */
	protected static void parseArgument(Class cls, String arg)
		throws OptionError, ConfigurationError
	{
		if (arg.length() < 1 || arg.charAt(0) != '-')
			throw new OptionError("Invalid argument: '"+arg+"'");
		int eq = arg.indexOf('=');
		String optionName;
		String optionValue = null;
		if (eq == -1) {
			optionName = arg.substring(1);
		} else {
			optionName = arg.substring(1, eq);
			optionValue = arg.substring(eq+1);
		}
		set(cls, optionName, optionValue);
	}

	/**
	 * Return a human-readable string representation of a given type.
	 */
	private static String typeToString(Class t) {
		if (t.isPrimitive())
			return t.toString();
		if (t.isArray())
			return typeToString(t.getComponentType())+"[]";
		if (t.getPackage() == Package.getPackage("java.lang"))
			return t.getName().substring("java.lang.".length());
		return t.getName();
	}

	/**
	 * Return an array of (option,type,description,default_value) tuples for
	 * the given configuration class.
	 * The options are public static non-final fields, and the descriptions
	 * are private final String fields named OPTION_desc, where OPTION is the
	 * corresponding option field.
	 * TODO: Sort the options?
	 *
	 * @param cls the configuration class
	 * @return array of two-element String arrays
	 */
	protected static String[][] options(Class cls) {
		Field[] flds = cls.getFields();
		int num = 0;
		for (int i = 0; i < flds.length; i++) {
			Field f = flds[i];
			int m = f.getModifiers();
			// f is guaranteed to be public
			if (Modifier.isStatic(m) && !Modifier.isFinal(m))
				num++;
		}
		String[][] opts = new String[num][];
		int j = 0;
		for (int i = 0; i < flds.length; i++) {
			Field f = flds[i];
			int m = f.getModifiers();
			// f is guaranteed to be public
			if (!Modifier.isStatic(m) || Modifier.isFinal(m))
				continue;
			Class t = f.getType();
			String type = typeToString(t);
			String desc = "";
			Object v = null;
			try {
				Field d = cls.getDeclaredField(f.getName()+"_desc");
				int dm = d.getModifiers();
				if (!Modifier.isPrivate(dm) || !Modifier.isStatic(dm) ||
						!Modifier.isFinal(dm))
					throw new NoSuchFieldException();
				boolean s = d.isAccessible();
				d.setAccessible(true);
				desc = (String) d.get(null) + " ";
				d.setAccessible(s);
			} catch (NoSuchFieldException nsfe) {
			} catch (IllegalAccessException iae) {
			}
			try {
				v = f.get(null);
			} catch (IllegalAccessException iae) {
			}
			if (!t.isPrimitive() && v != null)
				v = "\"" + v + "\"";
			opts[j++] = new String[] { f.getName(), type, desc, ""+v };
		}
		return opts;
	}
}

