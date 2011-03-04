package x10dt.core.tests.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.util.ErrorInfo;

/**
 * Extracts all files in ../x10.tests/examples/ and compiles them one by one (one compiler instance per file) 
 * with STATIC_CALLS turned on and off.
 * @author mvaziri
 *
 */
@RunWith(Parameterized.class)
public class X10TestsParamCompilerTests extends CompilerTestsBase {
	
	private File[] sources;
	private String[] options;
	
	/*
	 * Paths
	 * 
	 */
	private static String DATA_PATH = ".." + File.separator+ "x10.tests" + File.separator + "examples" + File.separator;
	private static String LIB_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "x10lib" + File.separator;

	
	/*
	 * Options
	 */
	
	private static String[] STATIC_CALLS = {"-STATIC_CALLS=true"};
	private static String[] NOT_STATIC_CALLS = {"-STATIC_CALLS=false"};
	
	
	
	public X10TestsParamCompilerTests(File[] sources, String[] options){
		super();
		this.sources = sources;
		this.options = options;
	}
	
	
	@Parameters
	public static Collection<Object[]> inputs() {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		for (File f: getSources(new File(DATA_PATH))){
			inputs.add(new Object[]{new File[]{f}, STATIC_CALLS});
			inputs.add(new Object[]{new File[]{f}, NOT_STATIC_CALLS});
				
		}
		return inputs;
	}

	@Test(timeout=10000)
	public void compilerTest() throws Exception {
		String sourcepath = getRuntimeJar() + File.pathSeparator + LIB_PATH + File.pathSeparator + getCurrentDirsPath(sources);
		compile(sources, options, new ArrayList<ErrorInfo>(), sourcepath);
	}

	

	private static String getCurrentDirsPath(File[] files) {
		String result = "";
		for (File f : files) {
			// TODO: Need to get the package out of the file here
			result += f.getParent();
		}
		return result;
	}

}

