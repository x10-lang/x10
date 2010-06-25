package org.eclipse.imp.x10dt.core.tests.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;

public class CompilerTestsBase {

	private static String OUTPUT_DIR = "output";
	/**
	 * 
	 * @param files
	 * @param static_calls
	 * @return true if compilation succeeds without errors
	 * @throws Exception 
	 */
	public boolean compile(File[] files, String[] options,
			final Collection<ErrorInfo> errors, String sourcepath) throws Exception {
		
		try {
			Collection<String> sources = new ArrayList<String>();
			for (File f : files) {
				sources.add(f.getPath());
			}
			ExtensionInfo extInfo = new x10.ExtensionInfo();
			buildOptions(extInfo, options, sourcepath);
			final Compiler compiler = new Compiler(extInfo,
					new AbstractErrorQueue(1000000, extInfo.compilerName()) {
						protected void displayError(ErrorInfo error) {
							errors.add(error);
						}
					});
			Globals.initialize(compiler);
			compiler.compileFiles(sources);
			for (String s : sources) {
				System.err.print(s + " - ");
			}
			for (String s : options) {
				System.err.print(s + " - ");
			}
			System.err.println(sourcepath);
			for (ErrorInfo e : errors) {
				System.err.println(e);
			}
			Assert.assertFalse(getTestId(files, options), duplicateErrors(errors));
			Assert.assertFalse(getTestId(files, options), notWellFormed(errors));
			return errors.isEmpty();
		} catch (Throwable e) {
			throw new Exception(getTestId(files, options), e);
		}
	}
	

	
	private void buildOptions(ExtensionInfo extinfo, String[] options, String sourcepath) {
		Options opts = extinfo.getOptions();
		List<String> optsList = new ArrayList<String>();
		String[] stdOptsArray = new String[] { 
				"-noserial", 
				"-d",
				OUTPUT_DIR,
				"-sourcepath",
				sourcepath,
				"-commandlineonly", 
				"-c",
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
	
	protected String getTestId(File[] files, String[] options){
		String testId = "";
		for (File f : files) {
			testId += f.getPath() + " - ";
		}
		for (String s : options) {
			testId += s + " - ";
		}
		return testId;
	}
	
	protected static Collection<File> getSources(File dir) {
		Collection<File> results = new ArrayList<File>();
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.isFile() && f.getName().endsWith(".x10")) {
					results.add(f);
				} else if (f.isDirectory()) {
					results.addAll(getSources(f));
				}
			}
		}
		return results;
	}
	
	protected static String getRuntimeJar(){
		return ".." + File.separator + "x10.dist" + File.separator + "lib" + File.separator + "x10.jar";
	}
	
	
	
	private boolean duplicateErrors(Collection<ErrorInfo> errors){
		Map<String,Integer> count = new HashMap<String,Integer>(); //Map of message string to count
		for(ErrorInfo e1: errors){
			String m1 = e1.toString();
			for(ErrorInfo e2: errors){
				if(m1.equals(e2.toString())){
					if (count.get(m1) == null){
						count.put(m1, 1);
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean notWellFormed(Collection<ErrorInfo> errors){
		for(ErrorInfo e: errors){
			//TODO: add other well-formedness conditions here.
			if (e.getMessage().contains("{amb}")){
				return true;
			}
		}
		return false;
	}


	

	
	
}
