package org.eclipse.imp.x10dt.core.tests.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.util.ErrorInfo;

@RunWith(Parameterized.class)
public class FuzzParamCompilerTests extends CompilerTestsBase {

	private File[] sources;
	private String[] options;

	/*
	 * Paths
	 */
	private static String DATA_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "Constructs" + File.separator;;
	private static String LIB_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "x10lib" + File.separator;
	private static String SOURCE_PATH_BASE = getRuntimeJar() + ":" + LIB_PATH + ":" + DATA_PATH;

	/*
	 * Options
	 */

	private static String[] STATIC_CALLS = { "-STATIC_CALLS=true" };
	private static String[] NOT_STATIC_CALLS = { "-STATIC_CALLS=false" };

	private static int CAPACITY = 1024;
	
	public FuzzParamCompilerTests(File[] sources, String[] options) {
		super();
		this.sources = sources;
		this.options = options;
	}

	@Parameters
	public static Collection inputs() {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		for (File f : getSources(new File(DATA_PATH))) {
			for (File f1: fuzz(f)){
				inputs.add(new Object[] { new File[] { f1 }, STATIC_CALLS });
				inputs.add(new Object[] { new File[] { f1 }, NOT_STATIC_CALLS });
			}
		}
		return inputs;
	}

	@Test
	public void compilerTest() throws Exception {
		String sourcepath = SOURCE_PATH_BASE;
		compile(sources, options, new ArrayList<ErrorInfo>(), sourcepath);
	}

	private static Collection<File> fuzz(File f){
		Collection<File> results = new ArrayList<File>();
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(f));
//			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
//			writer.
//			CharBuffer buffer = CharBuffer.allocate(CAPACITY);
//			int length = reader.read(buffer);
//			reader.close();
//			for(FuzzIterator t = new FuzzIterator(buffer); t.hasNext();){
//				File newFile = t.next();
//				results.add
//			}
//		} catch (IOException e){
//			System.err.println(e);
//		}
		return results;
	}
}
