package x10dt.core.tests.compiler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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
	
	
	public DataParamCompilerTests(File[] sources, String[] options){
		super();
		this.sources = sources;
		this.options = options;
	}
	
	
	@Parameters
	public static Collection<Object[]> inputs() throws URISyntaxException {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		final URL dataURL = CompletenessCompilerTests.class.getClassLoader().getResource(DATA_PATH);
		for (File f: getSources(toFile(dataURL))){
			inputs.add(new Object[]{new File[]{f}, STATIC_CALLS});
			inputs.add(new Object[]{new File[]{f}, NOT_STATIC_CALLS});
				
		}
		return inputs;
	}

	@Test
	public void compilerTest() throws Exception {
	  final URL dataURL = CompletenessCompilerTests.class.getClassLoader().getResource(DATA_PATH);
		final String sourcepath = getRuntimeJar() + File.pathSeparator + toFile(dataURL).getAbsolutePath();
		compile(sources, options, new ArrayList<ErrorInfo>(), sourcepath);
	}
	
}

