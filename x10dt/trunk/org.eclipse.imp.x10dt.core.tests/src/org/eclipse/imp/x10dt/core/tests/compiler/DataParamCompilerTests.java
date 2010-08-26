package org.eclipse.imp.x10dt.core.tests.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.util.ErrorInfo;

/**
 * Extracts all files in data/pppp and compiles them one by one (one compiler instance per file)
 * with STATIC_CALLS turned on and off.
 * @author mvaziri
 *
 */

@RunWith(Parameterized.class)
public class DataParamCompilerTests extends CompilerTestsBase {
	
	private File[] sources;
	private String[] options;
	
	/*
	 * Paths
	 * 
	 */
	private static String DATA_PATH = "data" + File.separator + "pppp" + File.separator;
	private static String LIB_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "x10lib" + File.separator;
	private static String SOURCE_PATH_BASE = getRuntimeJar() + File.pathSeparator + LIB_PATH + File.pathSeparator + DATA_PATH;

	
	
	public DataParamCompilerTests(File[] sources, String[] options){
		super();
		this.sources = sources;
		this.options = options;
	}
	
	
	@Parameters
	 public static Collection inputs() {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		for (File f: getSources(new File(DATA_PATH))){
			inputs.add(new Object[]{new File[]{f}, STATIC_CALLS});
			inputs.add(new Object[]{new File[]{f}, NOT_STATIC_CALLS});
				
		}
		return inputs;
	 }

	@Test
	public void compilerTest() throws Exception {
		String sourcepath = SOURCE_PATH_BASE;
		compile(sources, options, new ArrayList<ErrorInfo>(), sourcepath);
	}

	
}

