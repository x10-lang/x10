package org.eclipse.imp.x10dt.core.tests.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.util.ErrorInfo;

/**
 * This class performs fuzz testing.
 * When run as a Java application, this class creates a series of "fuzz" files for the files in data/fuzz,
 * and created the corresponding files in data/fuzzgen.
 * When run as a Junit test, this class runs the tests generated from available files in data/fuzzgen.
 * 
 * @author mvaziri
 *
 */
@RunWith(Parameterized.class)
public class FuzzParamCompilerTests extends CompilerTestsBase {

	private File[] sources;
	private String[] options;

	/*
	 * Paths
	 */
	private static String FUZZ_PATH = "data" + File.separator + "fuzzgen"  + File.separator;
	private static String DATA_PATH = "data" + File.separator + "fuzz"  + File.separator;
	private static String LIB_PATH = ".." + File.separator + "x10.tests" + File.separator + "examples" + File.separator + "x10lib" + File.separator;
	private static String SOURCE_PATH_BASE = getRuntimeJar() + File.pathSeparator + LIB_PATH + File.pathSeparator + DATA_PATH;

	
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

	@Test(timeout=10000)
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
			char[] buffer = new char[(int)length];
			reader.read(buffer);
			reader.close();
			String path = "data" + File.separator + "fuzzgen" + File.separator;
			int count = 0;
			for(FuzzIterator t = new FuzzIterator(buffer); t.hasNext();){
				char[] newBuffer = t.next();
				String dir = path + f.getName() + (count++);
				boolean success = (new File(dir)).mkdir();
				if (success){
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + File.separator + f.getName())));
					writer.write(newBuffer);
					writer.close();
				}
			}
		} catch (IOException e){
			System.err.println(e);
		}
	}
	
	private static class FuzzIterator {
		int cache_length = 20; //This the length of the sequence of characters that will be erased each time
		char[] buffer;
		char[] cache = new char[cache_length];
		int begin = 0;
		
		FuzzIterator(char[] buffer){
			this.buffer = buffer;
		}
		
		boolean hasNext(){
			return begin < buffer.length;
		}
		
		char[] next(){
			//restore what was erased last time
			for (int i = begin - cache_length; i >= 0 && i < begin; i++){
				buffer[i] = cache[i-begin + cache_length];
			}
			//cache and erase
			for(int i = begin; i < begin + cache_length && i < buffer.length; i++){
				cache[i - begin] = buffer[i];
				buffer[i] = ' ';
			}
			begin += cache_length;
			return buffer;
		}
	}
	
	public static void main(String[] args){
		for (File f : getSources(new File(DATA_PATH))) {
			fuzz(f);
		}
	}
}
