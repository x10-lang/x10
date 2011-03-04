package x10dt.core.tests.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * This class performs fuzz testing.
 * When run as a Java application, this class creates a series of "fuzz" files for the files in data/fuzz,
 * and created the corresponding files in data/fuzzgen.
 * When run as a Junit test, this class runs the tests generated from available files in data/fuzzgen.
 * 
 * To run a specific test number, use the "-test" argument to the Eclipse JUnit runner,
 * e.g., to run test number 5996, invoke with
 * -test "x10dt.core.tests.compiler.FuzzParamCompilerTests:compilerTest[5996]"
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
	private static final String DATA_DIRNAME = "data"; //$NON-NLS-1$
	private static final String CODE_DIRNAME = ".." + File.separator + ".." + File.separator + "code" + File.separator + DATA_DIRNAME;
	private static final String FUZZGEN_DIRNAME = "fuzzgen"; //$NON-NLS-1$
	private static final String FUZZ_DIRNAME = "fuzz"; //$NON-NLS-1$
	private static final String FUZZ_PATH = DATA_DIRNAME + File.separator + FUZZGEN_DIRNAME  + File.separator;
	private static final String DATA_PATH = DATA_DIRNAME + File.separator + FUZZ_DIRNAME  + File.separator;

	
	public FuzzParamCompilerTests(File[] sources, String[] options) {
		super();
		this.sources = sources;
		this.options = options;
	}

	@Parameters
	public static Collection<Object[]> inputs() throws URISyntaxException {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		final URL fuzzURL = FuzzParamCompilerTests.class.getClassLoader().getResource(FUZZ_PATH);
		for (File f : getSources(toFile(fuzzURL))) {
			inputs.add(new Object[] { new File[] { f }, STATIC_CALLS });
			inputs.add(new Object[] { new File[] { f }, NOT_STATIC_CALLS });
		}
		return inputs;
	}

	@Test(timeout=10000)
	public void compilerTest() throws Exception {
	  final URL dataURL = FuzzParamCompilerTests.class.getClassLoader().getResource(DATA_PATH);
		final String sourcepath = getRuntimeJar() + File.pathSeparator + File.pathSeparator + toFile(dataURL).getAbsolutePath();
		compile(sources, options, new ArrayList<ErrorInfo>(), sourcepath);
	}


	/**
	 * This method takes a file f and creates a collection of files resulting
	 * from introducing syntactic errors in f (introduce blank space in various places in the file.
	 * 
	 * @param f file to fuzz
	 */
	private static void fuzz(final File f, final String fuzzPath){
		try {
			long length = f.length();
			BufferedReader reader = new BufferedReader(new FileReader(f));
			char[] buffer = new char[(int)length];
			reader.read(buffer);
			reader.close();
			int count = 0;
			for(FuzzIterator t = new FuzzIterator(buffer); t.hasNext();){
				char[] newBuffer = t.next();
				String dir = fuzzPath + File.separatorChar + f.getName() + (count++);
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
	
	public static void main(String[] args) throws URISyntaxException, IOException {
	    final URL dataURL = FuzzParamCompilerTests.class.getClassLoader().getResource(DATA_PATH);
	    final URL fuzzURL = FuzzParamCompilerTests.class.getClassLoader().getResource(DATA_DIRNAME);
	    File fuzzDir = new File(toFile(fuzzURL), CODE_DIRNAME);
	    final File fuzzgenFile = args.length > 0 ? new File(args[0]) : new File(fuzzDir, FUZZGEN_DIRNAME);
	    if (!fuzzgenFile.exists() && ! fuzzgenFile.mkdir()) {
	        throw new IOException("Could not create directory: " + fuzzgenFile.getAbsolutePath());
	    }
	    final String fuzzPath = fuzzgenFile.getAbsolutePath();
	    for (File f : getSources(toFile(dataURL))) {
	        fuzz(f, fuzzPath);
	    }
	}
}
