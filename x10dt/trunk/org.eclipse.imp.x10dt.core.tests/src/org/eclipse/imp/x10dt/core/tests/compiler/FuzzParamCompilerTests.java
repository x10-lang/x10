package org.eclipse.imp.x10dt.core.tests.compiler;

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
	private static String FUZZ_PATH = "fuzz"  + File.separator;
	private static String DATA_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "Constructs" + File.separator;
	private static String LIB_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "x10lib" + File.separator;
	private static String SOURCE_PATH_BASE = getRuntimeJar() + ":" + LIB_PATH + ":" + DATA_PATH;

	
	public FuzzParamCompilerTests(File[] sources, String[] options) {
		super();
		this.sources = sources;
		this.options = options;
	}

	@Parameters
	public static Collection inputs() {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		for (File f : getSources(new File(FUZZ_PATH))) {
			inputs.add(new Object[] { new File[] { f }, STATIC_CALLS });
			inputs.add(new Object[] { new File[] { f }, NOT_STATIC_CALLS });
		}
		return inputs;
	}

	@Test
	public void compilerTest() throws Exception {
		String sourcepath = SOURCE_PATH_BASE;
		compile(sources, options, new ArrayList<ErrorInfo>(), sourcepath);
	}


	/**
	 * This method takes a file f and creates a collection of files resulting
	 * from introducing syntactic errors in f (introduce blank space in various places in the file.
	 * 
	 * @param f file to fuzz
	 */
	private static void fuzz(File f){
		try {
			long length = f.length();
			BufferedReader reader = new BufferedReader(new FileReader(f));
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			CharBuffer buffer = CharBuffer.allocate((int) length);
			reader.read(buffer);
			reader.close();
			new File("fuzz" + File.separator + "test").mkdir();
//			for(FuzzIterator t = new FuzzIterator(buffer); t.hasNext();){
//				CharBuffer newBuffer = t.next();
//				File.
//				writer
//			}
		} catch (IOException e){
			System.err.println(e);
		}
	}
	
	public static void main(String[] args){
		for (File f : getSources(new File(DATA_PATH))) {
			fuzz(f);
		}
	}
}
