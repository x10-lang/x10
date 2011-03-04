package x10dt.core.tests.compiler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.util.ErrorInfo;

/**
 * Extracts first N files in data/pppp-completeness and compiles all of them at once,
 * then compiles them one at a time and compares error queues.
 * It considers first 2 files, then first 3 files, etc... in that directory.
 * @author mvaziri
 *
 */
@RunWith(Parameterized.class)
public class CompletenessCompilerTests extends CompilerTestsBase {

	private File[] sources;
	private String[] options;
	
	/*
	 * Paths
	 */
	
	// This is a selection of files from the pppp project whose compilation does not result in an exception or assertion failure.
	private static String DATA_PATH = "data" + File.separator + "pppp-completeness";
	
	/*
	 * Options
	 */
	
	private static String[] STATIC_CALLS = {"-STATIC_CALLS=true"};
	private static String[] NOT_STATIC_CALLS = {"-STATIC_CALLS=false"};
	
	
	public CompletenessCompilerTests(File[] sources, String[] options){
		super();
		this.sources = sources;
		this.options = options;
	}
	
	@Parameters
	public static Collection<Object[]> inputs() throws URISyntaxException {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		final URL dataURL = CompletenessCompilerTests.class.getClassLoader().getResource(DATA_PATH);
		for(FileCollectionIterator f = new FileCollectionIterator(toFile(dataURL)); f.hasNext(); ){
			File[] fs = f.next();
			inputs.add(new Object[]{fs, STATIC_CALLS});
			inputs.add(new Object[]{fs, NOT_STATIC_CALLS});
		}
		return inputs;
	}

	@Test
	public void compilerTest() throws Exception {
		final URL dataURL = CompletenessCompilerTests.class.getClassLoader().getResource(DATA_PATH);
		String sourcepath = getRuntimeJar() + File.pathSeparatorChar + toFile(dataURL).getAbsolutePath();
		
		//Submit everything to the compiler at once
		System.err.println("***Compiling all files");
		Collection<ErrorInfo> allErrors = new ArrayList<ErrorInfo>();
		compile(sources, options, allErrors, sourcepath);
		
		System.err.println("***Compiling files one at a time");
		//Submit files one by one
		Collection<ErrorInfo> errors = new ArrayList<ErrorInfo>();
		Collection<String> paths = new ArrayList<String>();
		for(File f: sources){
			File[] fs = {f};
			compile(fs, options, errors, sourcepath);
			paths.add(f.getPath());
		}
		
		//Compare error queues
		if (!contained(allErrors,errors) || !contained(errors, allErrors)){
			Assert.assertFalse(getTestId(paths,options), true);
		}
		
	}
	
	/**
	 * Checks that c1 is contained in c2
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean contained(Collection<ErrorInfo> c1, Collection<ErrorInfo> c2){
		for(ErrorInfo e1: c1){
			boolean found = false;
			for(ErrorInfo e2: c2){
				if (e1.toString().equals(e2.toString())){
					found = true;
					break;
				}
			}
			if (!found) return false;
		}
		return true;
	}
	
	
	/**
	 * Iterator class that returns the first N leaf files of a directory.
	 * Assume that dir has at least one x10 file in it.
	 */
	private static class FileCollectionIterator{
		private File[] files;
		private Collection<File> currentFiles = new ArrayList<File>();
		private int i = 0;
		
		public FileCollectionIterator(File dir){
			files = getSources(dir).toArray(new File[0]);
		}
		
		public boolean hasNext(){
			return currentFiles.size() < files.length;
		}
		
		public File[] next(){
			currentFiles.add(files[i++]);
			return currentFiles.toArray(new File[0]);
		}
	}
}
