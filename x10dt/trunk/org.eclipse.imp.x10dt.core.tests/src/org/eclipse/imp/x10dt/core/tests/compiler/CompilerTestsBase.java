package org.eclipse.imp.x10dt.core.tests.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import x10.errors.X10ErrorInfo;
import x10.util.StreamResource;

/**
 * This class provides some base functionality for compilation
 * 
 * @author mvaziri
 * 
 */
public abstract class CompilerTestsBase {

	protected static String[] STATIC_CALLS = { "-STATIC_CALLS=true", "-CHECK_INVARIANTS" };

	protected static String[] NOT_STATIC_CALLS = { "-STATIC_CALLS=false", "-CHECK_INVARIANTS" };

	private static String OUTPUT_DIR = "output";

	private static final String X10_RUNTIME = "x10.runtime";

	private static final String X10_COMMON = "x10.common";

	private static final String X10_CONSTRAINTS = "x10.constraints";

	private static final String CORE_TESTS = "org.eclipse.imp.x10dt.core.tests";

	// --- Abstract methods definition

	protected abstract String getDataSourcePath();

	// --- Services

	protected final boolean compile(final Collection<Source> sources, String[] options, final Collection<ErrorInfo> errors)
			throws Exception {
		return compile(sources, options, errors, new ArrayList<Job>());
	}

	protected final boolean compile(final Collection<Source> sources, final String[] options,
			final Collection<ErrorInfo> errors, final Collection<Job> jobs) throws Exception {
		Collection<String> paths = new ArrayList<String>();
		for (Source s : sources) {
			paths.add(s.path());
		}
		try {
			ExtensionInfo extInfo = new x10.ExtensionInfo();
			final String sourcepath = createSourcePath();
			Compiler compiler = getCompiler(extInfo, options, errors, sourcepath);
			Globals.initialize(compiler);
			compiler.compile(sources);
			return outcome(paths, options, sourcepath, errors, jobs, extInfo);

		} catch (Throwable e) {
			throw new Exception(getTestId(paths, options), e);
		}
	}

	protected final boolean compile(final String[] files, final String[] options, final Collection<ErrorInfo> errors)
			throws Exception {
		return compile(files, options, errors, new ArrayList<Job>());
	}

	protected final boolean compile(final String[] files, final String[] options, final Collection<ErrorInfo> errors,
			final Collection<Job> jobs) throws Exception {
		Collection<Source> fs = new ArrayList<Source>();
		final ClassLoader classLoader = getClass().getClassLoader();
		for (String s : files) {
			final InputStream is = classLoader.getResourceAsStream(s);
			if (is != null) {
				fs.add(new FileSource(new StreamResource(is, s)));
			}
		}
		return compile(fs, options, errors, jobs);
	}

	protected final Compiler getCompiler(final ExtensionInfo extInfo, final String[] options,
			final Collection<ErrorInfo> errors, final String sourcepath) {
		buildOptions(extInfo, options, sourcepath);
		return new Compiler(extInfo, new AbstractErrorQueue(10000, extInfo.compilerName()) {
			protected void displayError(ErrorInfo error) {
				errors.add(error);
			}
		});
	}

	public static Class<?> loadClass(String name) {
	    try {
	        return Class.forName(name);
	    } catch (ClassNotFoundException e) {
	        return null;
	    } catch (LinkageError e) {
	        return null;
	    }
	}

	private static Class<?>[] getTypes(Object[] args) {
	    Class<?>[] types = new Class<?>[args.length];
	    for (int i = 0; i < args.length; i++) {
	        types[i] = args[i]==null ? Object.class : args[i].getClass();
	    }
	    return types;
	}
	private static boolean isCompatible(Class<?>[] formals, Class<?>[] types) {
	    for (int j = 0; j < formals.length; j++) {
	        if (!formals[j].isAssignableFrom(types[j])) return false;
	    }
	    return true;
	}
	private static Method getMethod(Class<?> container, String name, Object... args) {
	    Class<?>[] types = getTypes(args);
	    try {
	        try {
	            return container.getMethod(name, types);
	        } catch (NoSuchMethodException e) {
	            Method[] methods = container.getMethods();
	            for (Method m : methods) {
	                if (!m.getName().equals(name)) continue;
	                if (!isCompatible(m.getParameterTypes(), types)) continue;
	                return m;
	            }
	        }
	    } catch (SecurityException e) { }
	    return null;
	}
	public static Object invokeMethod(Object receiver, String name, Object... args) {
	    Method method = getMethod(receiver.getClass(), name, args);
	    try {
	        return method.invoke(receiver, args);
	    } catch (IllegalArgumentException e) {
	    } catch (IllegalAccessException e) {
	    } catch (InvocationTargetException e) {
	        throw new RuntimeException(e.getCause());
	    }
	    return null;
	}
	public static Object invokeMethod(Class<?> container, String name, Object... args) {
	    Method method = getMethod(container, name, args);
	    try {
	        return method.invoke(null, args);
	    } catch (IllegalArgumentException e) {
	    } catch (IllegalAccessException e) {
	    } catch (InvocationTargetException e) {
	        throw new RuntimeException(e.getCause());
	    }
	    return null;
	}

	protected static final Class<?> FILE_LOCATOR = loadClass("org.eclipse.core.runtime.FileLocator");

	protected static final File getFile(final URL url) throws URISyntaxException, IOException {
		if (url == null || FILE_LOCATOR == null) {
			return null;
		} else {
			return new File(((URL)invokeMethod(FILE_LOCATOR, "resolve", url)).toURI());
		}
	}

	protected static final File createFile(final String fileName) throws URISyntaxException, IOException {
		final URL testBundle = getBundleRoot(CORE_TESTS);
		if (testBundle == null) {
			return new File(fileName);
		} else {
			return new File(getFile(testBundle), fileName);
		}
	}

	protected final String getTestId(Collection<String> sources, String[] options) {
		String testId = "";
		for (String f : sources) {
			testId += f + " - ";
		}
		for (String s : options) {
			testId += s + " - ";
		}
		return testId;
	}

	protected static final Collection<File> getSources(File dir) {
		Collection<File> results = new ArrayList<File>();
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.isFile() && f.getName().endsWith(".x10")) {
					results.add(f);
				} else if (f.isDirectory()) {
					results.addAll(getSources(f));
				}
			}
		}
		return results;
	}

	// --- Private code

	private static final Class<?> PLATFORM = loadClass("org.eclipse.core.runtime.Platform");

	private static URL getBundleRoot(String bundleName) {
	    if (PLATFORM == null) return null;
		Object bundle = invokeMethod(PLATFORM, "getBundle", bundleName);
		return bundle == null ? null : (URL) invokeMethod(bundle, "getResource", "");
	}
	private String createSourcePath() throws URISyntaxException, IOException {
		final StringBuilder sourcePathBuilder = new StringBuilder();
		final URL runtimeBundle = getBundleRoot(X10_RUNTIME);
		if (runtimeBundle == null) {
			sourcePathBuilder.append("..").append(File.separator).append("x10.dist").append(File.separator).append("lib")
					.append(File.separator).append("x10.jar");
		} else {
			final File runtimeFile = getFile(runtimeBundle);
			sourcePathBuilder.append(runtimeFile.getAbsolutePath());

			final URL commonBundle = getBundleRoot(X10_COMMON);
			Assert.assertNotNull("Could not access the X10 Common bundle", commonBundle);
			final File commonFile = getFile(commonBundle);
			sourcePathBuilder.append(File.pathSeparator).append(commonFile.getAbsolutePath());

			final URL constraintsBundle = getBundleRoot(X10_CONSTRAINTS);
			Assert.assertNotNull("Could not access the X10 Constraints bundle", constraintsBundle);
			final File constraintsFile = getFile(constraintsBundle);
			sourcePathBuilder.append(File.pathSeparator).append(constraintsFile.getAbsolutePath());
		}
		final String dataSourcePath = getDataSourcePath();
		if (dataSourcePath != null) {
			sourcePathBuilder.append(File.pathSeparator).append(dataSourcePath);
		}
		return sourcePathBuilder.toString();
	}

	private String getErrorString(ErrorInfo e) {
		return e.toString() + (e.getPosition() == null ? "" : (":" + e.getPosition()));
	}

	private boolean duplicateErrors(Collection<ErrorInfo> errors) {
		Map<String, Integer> count = new HashMap<String, Integer>(); // Map of
																		// message
																		// string
																		// to
																		// count
		for (ErrorInfo e1 : errors) {
			if (e1.getErrorKind() == ErrorInfo.INTERNAL_ERROR)
				continue;
			String m1 = getErrorString(e1);
			for (ErrorInfo e2 : errors) {
				String m2 = getErrorString(e2);
				if (m1.equals(m2)) {
					if (count.get(m1) == null) {
						count.put(m1, 1);
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean notWellFormed(ErrorInfo e) {
		// TODO: add other well-formedness conditions here.
		if (e.getMessage() == null)
			return true;
		if (e.getMessage().contains("{amb}"))
			return true;
		if (e.getMessage().contains("<unknown>"))
			return true;
		if (e.getMessage().contains("place_"))
			return true;
		if (e.getMessage().contains("self_"))
			return true;
		return false;
	}

	private boolean outcome(Collection<String> sources, String[] options, String sourcepath, Collection<ErrorInfo> errors,
			Collection<Job> jobs, ExtensionInfo extInfo) {
		jobs.addAll(extInfo.scheduler().commandLineJobs());

		for (String s : sources) {
			System.err.print(s + " - ");
		}
		for (String s : options) {
			System.err.print(s + " - ");
		}
		System.err.println(sourcepath);
		for (ErrorInfo e : errors) {
			System.err.println(e + ":" + e.getPosition());
		}
		for (ErrorInfo error : errors) {
			Assert.assertFalse("INVARIANT: " + getTestId(sources, options) + error.getMessage(), invariantViolation(error));
			Assert.assertFalse("INTERNAL ERROR: " + getTestId(sources, options) + error.getMessage(), internalError(error));
			Assert.assertFalse("WELL-FORMEDNESS: " + getTestId(sources, options) + error.getMessage(), notWellFormed(error));
		}
		Assert.assertFalse("DUPLICATE: " + getTestId(sources, options), duplicateErrors(errors));
		return errors.isEmpty();
	}

	private void buildOptions(ExtensionInfo extinfo, String[] options, String sourcepath) {
		Options opts = extinfo.getOptions();
		List<String> optsList = new ArrayList<String>();
		String[] stdOptsArray = new String[] { "-noserial", "-d", OUTPUT_DIR, "-sourcepath", sourcepath, "-commandlineonly",
				"-c", };
		for (String s : stdOptsArray) {
			optsList.add(s);
		}
		for (String s : options) {
			optsList.add(0, s);
		}

		String[] optsArray = optsList.toArray(new String[optsList.size()]);
		try {
			opts.parseCommandLine(optsArray, new HashSet<String>());
		} catch (UsageError e) {

		}
	}

	private boolean internalError(ErrorInfo e) {
		if (e.getErrorKind() == ErrorInfo.INTERNAL_ERROR)
			return true;
		return false;
	}

	private boolean invariantViolation(ErrorInfo e) {
		if (e.getErrorKind() == X10ErrorInfo.INVARIANT_VIOLATION_KIND)
			return true;
		return false;
	}

}
