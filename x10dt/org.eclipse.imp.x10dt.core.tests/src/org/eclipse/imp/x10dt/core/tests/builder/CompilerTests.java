package org.eclipse.imp.x10dt.core.tests.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.junit.Test;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;

public class CompilerTests {
	private static String DATA_PATH = "data" + File.separator;
	private static String[] STATIC_CALLS = {"-STATIC_CALLS=true"};
	private static String[] NOT_STATIC_CALLS = {"-STATIC_CALLS=false"};
	
	@Test
	public void hello1_static_calls(){
		String[] sources = {"Hello1.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void hello1_not_static_calls(){
		String[] sources = {"Hello1.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1441_static_calls(){
		String[] sources = {"GenericClass.x10", "Coord.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1441_not_static_calls(){
		String[] sources = {"GenericClass.x10", "Coord.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1430_static_calls(){
		String[] sources = {"Hi.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1430_not_static_calls(){
		String[] sources = {"Hi.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void foo_static_calls(){
		String[] sources = {"Foo.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void foo_not_static_calls(){
		String[] sources = {"Foo.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira646_static_calls(){
		String[] sources = {"IntOw.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira646_not_static_calls(){
		String[] sources = {"IntOw.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1133_static_calls(){
		String[] sources = {"Example.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1133_not_static_calls(){
		String[] sources = {"Example.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira727_static_calls(){
		String[] sources = {"Xolal.x10"};
		if (compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
		
	}
	
	@Test
	public void jira727_not_static_calls(){
		String[] sources = {"Xolal.x10"};
		if (compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
		
	}
	
	@Test
	public void jira1150_static_calls(){
		String[] sources = {"Mandana1.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira1150_not_static_calls(){
		String[] sources = {"Mandana1.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira461_static_calls(){
		String[] sources = {"ICE.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira461_not_static_calls(){
		String[] sources = {"ICE.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira699_static_calls(){
		String[] sources = {"Foo1.x10", "Bar.x10"};
		if (compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira699_not_static_calls(){
		String[] sources = {"Foo1.x10", "Bar.x10"};
		if (compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira1427_static_calls(){
		String[] sources = {"Hello.x10"};
		if (compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira1427_not_static_calls(){
		String[] sources = {"Hello.x10"};
		if (compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>()))
			assert false;
	}
	
	@Test
	public void jira246_static_calls(){
		String[] sources = {"Bug.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira246_not_static_calls(){
		String[] sources = {"Bug.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira579_static_calls(){
		String[] sources = {"TestDist.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira579_not_static_calls(){
		String[] sources = {"TestDist.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira496_static_calls(){
		String[] sources = {"Foo2.x10"};
		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
	@Test
	public void jira496_not_static_calls(){
		String[] sources = {"Foo2.x10"};
		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
	}
	
//	@Test
//	public void jira496_static_calls(){
//		String[] sources = {"Foo2.x10"};
//		compile(sources, STATIC_CALLS, new ArrayList<ErrorInfo>());
//	}
//	
//	@Test
//	public void jira496_not_static_calls(){
//		String[] sources = {"Foo2.x10"};
//		compile(sources, NOT_STATIC_CALLS, new ArrayList<ErrorInfo>());
//	}
	
	/**
	 * 
	 * @param files
	 * @param static_calls
	 * @return true if compilation succeeds without errors
	 */
	public boolean compile(String[] files, String[] options, final Collection<ErrorInfo> errors){
		Collection<String> sources = new ArrayList<String>();
		for(String f: files){
			sources.add(DATA_PATH + f);
		}
		ExtensionInfo extInfo = new x10.ExtensionInfo();
		buildOptions(extInfo, options);
    	final Compiler compiler= new Compiler(extInfo, new AbstractErrorQueue(1000000, extInfo.compilerName()) {
            protected void displayError(ErrorInfo error) {
                errors.add(error);
            }
        });
        Globals.initialize(compiler);
        compiler.compileFiles(sources);
		for (ErrorInfo e: errors){
			System.err.println(e);
		}
		return errors.isEmpty();
	}
	

	
	private void buildOptions(ExtensionInfo extinfo, String[] options) {
		Options opts = extinfo.getOptions();
		List<String> optsList = new ArrayList<String>();
		String[] stdOptsArray = new String[] { "-noserial", 
				"-sourcepath",
				getJar("runtime") + ":" +
				getJar("compiler"),
				"-commandlineonly", 
				"-c",
				//"-COMPILER_FRAGMENT_DATA_DIRECTORY="
				};
		for (String s : stdOptsArray) {
			optsList.add(s);
		}
		for (String s: options){
			optsList.add(0, s);
		}
        
		String[] optsArray = optsList.toArray(new String[optsList.size()]);
		try {
			opts.parseCommandLine(optsArray, new HashSet());
		} catch (UsageError e) {
			
		}
	}
	
	private String getJar(String jar){
		File lib = new File("lib");
		for (File f: lib.listFiles()){
			String path = f.getPath();
			if (path.contains("x10." + jar)){
				return path;
			}
		}
		System.err.println("no " + jar + " jar found in lib directory.");
		return null;
	}
}
