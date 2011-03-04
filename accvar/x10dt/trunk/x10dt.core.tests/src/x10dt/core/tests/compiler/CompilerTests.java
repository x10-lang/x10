package x10dt.core.tests.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Resource;
import polyglot.frontend.Source;
import polyglot.types.SemanticException;
import polyglot.util.ErrorInfo;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

public class CompilerTests extends CompilerTestsBase {
	private static String DATA_PATH = "data" + File.separator + "base" + File.separator;

	@Test
	public void montePi() throws Exception {
		String[] sources = {"MontePiCluster.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void montePiStatic() throws Exception {
		String[] sources = {"MontePiCluster.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void fileBug() throws Exception {
		String[] sources = {"EqSolver.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void fileBugStatic() throws Exception {
		String[] sources = {"EqSolver.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1761() throws Exception  {
		String[] sources = {"Hello9.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1761Static() throws Exception  {
		String[] sources = {"Hello9.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void contentAssistBug() throws Exception {
		String[] sources = {"Hello8.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void contentAssistBugStatic() throws Exception {
		String[] sources = {"Hello8.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1783() throws Exception {
		String[] sources = {"Hello7.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1783Static() throws Exception {
		String[] sources = {"Hello7.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1768() throws Exception {
		String[] files = {"Hello5.x10", "pac" + File.separator + "MyStruct.x10"};
		Collection<ErrorInfo> errors = new ArrayList<ErrorInfo>();
		Collection<Job> jobs = new ArrayList<Job>();
		final URL dataURL = getClass().getClassLoader().getResource(DATA_PATH);
		final String dataPath = toFile(dataURL).getAbsolutePath();
		compileStreams(toSources(dataPath, files), NOT_STATIC_CALLS, errors,
		               getRuntimeJar() + File.pathSeparator + dataPath,
		               jobs);
		for(ErrorInfo error: errors){
			if (error.getMessage().contains("Duplicate class")){
				Assert.assertTrue("Duplicate class error when there is no duplication.", false);
			}
		}
	}
	
	private Source[] toSources(final String dataPath, final String[] files) {
		Source[] result = new Source[files.length];
		for(int i = 0; i < files.length; i++){
			String file = files[i];
			try {
				FileInputStream fis = new FileInputStream(dataPath + File.separator + file);
		        result[i] = new StreamSource(fis, dataPath + File.separator + file);
		      } catch (Exception except) {
		        System.err.println(except);
		      }
		}
		
	    return result;
	  }
	
	/**
	 * This is a copy of x10dt.core.builder.StreamSource.
	 * Copied to avoid introducing a dependence.
	 *
	 */
	private class StreamSource extends FileSource {
	    public StreamSource(final InputStream s, final String fullPath) throws IOException {
	    	super(new Resource() {
				public File file() {
					return new File(fullPath);
				}

				public InputStream getInputStream() throws IOException {
					return s;
				}

				public String name() {
					int idx= fullPath.lastIndexOf(File.separatorChar);
					return (idx > 0) ? fullPath.substring(idx+1) : fullPath;
				}

				public String toString() {
				  return fullPath;
				}
				
	    	}, true);
	    }
	}

	@Test
	public void jira1771() throws Exception {
		String[] sources = {"Hello6.x10"};
		Collection<ErrorInfo> errors = new ArrayList<ErrorInfo>();
		compile(sources, NOT_STATIC_CALLS, errors);
		if (errors.isEmpty()) {
				Assert.assertTrue("Should have complained that 'A' is an unknown type.", false);
		}
		
	}
	
	@Test
	public void hello1_static_calls() throws Exception {
		String[] sources = {"Hello1.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void hello1_not_static_calls() throws Exception {
		String[] sources = {"Hello1.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1441_static_calls() throws Exception {
		String[] sources = {"GenericClass.x10", "Coord.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1441_not_static_calls() throws Exception {
		String[] sources = {"GenericClass.x10", "Coord.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1430_static_calls() throws Exception {
		String[] sources = {"Hi.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1430_not_static_calls() throws Exception {
		String[] sources = {"Hi.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void foo_static_calls() throws Exception {
		String[] sources = {"Foo.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void foo_not_static_calls() throws Exception {
		String[] sources = {"Foo.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira646_static_calls() throws Exception {
		String[] sources = {"IntOw.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira646_not_static_calls() throws Exception {
		String[] sources = {"IntOw.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1133_static_calls() throws Exception {
		String[] sources = {"Example.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1133_not_static_calls() throws Exception {
		String[] sources = {"Example.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira727_static_calls() throws Exception {
		String[] sources = {"Xolal.x10"};
		if (compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
		
	}
	
	@Test
	public void jira727_not_static_calls() throws Exception {
		String[] sources = {"Xolal.x10"};
		if (compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
		
	}
	
	@Test
	public void jira1150_static_calls() throws Exception {
		String[] sources = {"Mandana1.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1150_not_static_calls() throws Exception {
		String[] sources = {"Mandana1.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira461_static_calls() throws Exception {
		String[] sources = {"ICE.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira461_not_static_calls() throws Exception {
		String[] sources = {"ICE.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira699_static_calls() throws Exception {
		String[] sources = {"Foo1.x10", "Bar.x10"};
		if (compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira699_not_static_calls() throws Exception {
		String[] sources = {"Foo1.x10", "Bar.x10"};
		if (compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira1427_static_calls() throws Exception {
		String[] sources = {"Hello.x10"};
		if (compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira1427_not_static_calls() throws Exception {
		String[] sources = {"Hello.x10"};
		if (compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira246_static_calls() throws Exception {
		String[] sources = {"Bug.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira246_not_static_calls() throws Exception {
		String[] sources = {"Bug.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira579_static_calls() throws Exception {
		String[] sources = {"TestDist.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira579_not_static_calls() throws Exception {
		String[] sources = {"TestDist.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira496_static_calls() throws Exception {
		String[] sources = {"Foo2.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira496_not_static_calls() throws Exception {
		String[] sources = {"Foo2.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira912_static_calls() throws Exception {
		String[] sources = {"DimCheckN4_MustFailCompile.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira912_not_static_calls() throws Exception {
		String[] sources = {"DimCheckN4_MustFailCompile.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1213_static_calls() throws Exception {
		String[] sources = {"X10DTParserBug.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1213_not_static_calls() throws Exception {
		String[] sources = {"X10DTParserBug.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira867_static_calls() throws Exception {
		String[] sources = {"JHereEqHere.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira867_not_static_calls() throws Exception {
		String[] sources = {"JHereEqHere.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1265_static_calls() throws Exception {
		String[] sources = {"Hello2.x10", "Hi1.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1265_not_static_calls() throws Exception {
		String[] sources = {"Hello2.x10","Hi1.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1509_static_calls() throws Exception {
		String[] sources = {"Hello4.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1509_not_static_calls() throws Exception {
		String[] sources = {"Hello4.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void hello3_static_calls() throws Exception {
		String[] sources = {"Hello3.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	
	@Test
	public void hello3_not_static_calls() throws Exception {
		String[] sources = {"Hello3.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	private void jira1551_check(Collection<Job> jobs) {
		for (Job job: jobs){
			job.ast().visit(new ContextVisitor(job,job.extensionInfo().typeSystem(),job.extensionInfo().typeSystem().extensionInfo().nodeFactory()){
				 @Override
				 public NodeVisitor enterCall(Node node) throws SemanticException {
					 if (node instanceof ClassDecl) {
						 ClassDecl decl = (ClassDecl) node;
						 if (decl.body().members().isEmpty()){
							 Assert.assertTrue(false);
						 }
					 }
					 return super.enterCall(node);
				 }
			}.begin());
		}
	}
	@Test
	public void jira1551_static_calls() throws Exception {
		String[] sources = {"Diagonal.x10"};
		Collection<Job> jobs = new ArrayList<Job>();
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>(), jobs);
		jira1551_check(jobs);
	}
	
	@Test
	public void jira1551_not_static_calls() throws Exception {
		String[] sources = {"Diagonal.x10"};
		Collection<Job> jobs = new ArrayList<Job>();
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>(), jobs);
		jira1551_check(jobs);
	}
	
	@Test(timeout=10000)
	public void jira1543_static_calls() throws Exception {
		String[] sources = {"List.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	
	@Test(timeout=10000)
	public void jira1543_not_static_calls() throws Exception {
		String[] sources = {"List.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	private boolean compile(String[] files, String[] options, Collection<ErrorInfo> errors) throws Exception{
		return compile(files, options, errors, new ArrayList<Job>());
	}
	
	private boolean compile(String[] files, String[] options, Collection<ErrorInfo> errors, Collection<Job> jobs) throws Exception{
		Collection<File> fs = new ArrayList<File>();
		final ClassLoader classLoader = getClass().getClassLoader();
		for(String s: files){
			fs.add(toFile(classLoader.getResource(DATA_PATH + s)));
		}
		return compile(fs.toArray(new File[0]), options, errors, getRuntimeJar() + File.pathSeparator + DATA_PATH, jobs);
	}
	  
}
