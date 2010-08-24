package org.eclipse.imp.x10dt.core.tests.compiler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Source;
import polyglot.util.ErrorInfo;

/**
 * Extracts all files in data/pppp and compiles them one by one (one compiler instance per file) with STATIC_CALLS turned on
 * and off.
 * 
 * @author mvaziri
 * 
 */

@RunWith(Parameterized.class)
public class DataParamCompilerTests extends CompilerTestsBase {

	private Collection<Source> sources;
	private String[] options;

	/*
	 * Paths
	 */
	private static String DATA_PATH = "data" + File.separator + "pppp" + File.separator;
	private static String LIB_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + 
	                                 "x10lib" + File.separator;

	public DataParamCompilerTests(Collection<Source> sources, String[] options) {
		super();
		this.sources = sources;
		this.options = options;
	}

	protected String getDataSourcePath() {
		final StringBuilder sb = new StringBuilder();
		sb.append(File.pathSeparator).append(DATA_PATH).append(File.pathSeparator).append(LIB_PATH);
		return sb.toString();
	}

	@Parameters
	public static Collection<?> inputs() throws IOException, URISyntaxException {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		for (File f : getSources(createFile(DATA_PATH))) {
			final Collection<Source> sources = new ArrayList<Source>(1);
			sources.add(new FileSource(new FileResource(f)));
			inputs.add(new Object[] { sources, STATIC_CALLS });
			inputs.add(new Object[] { sources, NOT_STATIC_CALLS });

		}
		return inputs;
	}

	@Test
	public void compilerTest() throws Exception {
		compile(sources, options, new ArrayList<ErrorInfo>());
	}

}
