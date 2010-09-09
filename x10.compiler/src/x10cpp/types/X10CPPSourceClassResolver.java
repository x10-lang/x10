/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10cpp.types; 

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.frontend.Resource;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.types.QName;
import x10.types.X10SourceClassResolver;
import x10.util.StreamWrapper;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.visit.X10CPPTranslator;

public class X10CPPSourceClassResolver extends X10SourceClassResolver {

    public X10CPPSourceClassResolver(Compiler compiler, ExtensionInfo ext, String classpath, boolean compileCommandLineOnly, boolean ignoreModTimes) {
        super(compiler, ext, classpath, compileCommandLineOnly, ignoreModTimes);
    }
    
    private static File oldestFile (File[] files) {
    	long record = 0;
    	File oldest = null;
    	for (File f : files) {
    		if (f.lastModified()  > record) {
    			record = f.lastModified();
    			oldest = f;
    		}
    	}
    	return oldest;
    }

    /**
     * Load a class file for class <code>name</code>.
     */
    protected Resource loadFile(QName name) {
        if (nocache.contains(name)) {
            return null;
        }
        
        Options options = Globals.Options();
        
        Source source = ext.sourceLoader().classSource(name);
        if (source == null)
            return null;
        
        String packageName = name.qualifier() != null ? name.qualifier().toString() : null;
        final File cc = X10CPPTranslator.outputFile(ext.getOptions(), packageName, name.name().toString(), StreamWrapper.CC);
        final File h = X10CPPTranslator.outputFile(ext.getOptions(), packageName, name.name().toString(), StreamWrapper.Header);
        final File inc = X10CPPTranslator.outputFile(ext.getOptions(), packageName, name.name().toString(), StreamWrapper.Closures);

        if (cc.exists() && h.exists() && inc.exists()) {
            final File oldest = oldestFile(new File[] {cc,h,inc});
            return new Resource() {
            	public File file() {
            		return oldest;
                }

                public InputStream getInputStream() throws IOException {
                	throw new IOException();
                }

				public String name() {
					return cc.getPath();
				}
            };
        }

        nocache.add(name);

        return null;
    }

    protected void handleUpToDateTarget(QName name, Resource file) {
        // FIXME: [IP] HACK
        // Add the files associated to this class to outputFiles even if they won't be compiled
        if (!isOutput(name))
            return;

        String packageName = name.qualifier() != null ? name.qualifier().toString() : null;
        String cc = X10CPPTranslator.outputFileName(packageName, name.name().toString(), StreamWrapper.CC);
        String h = X10CPPTranslator.outputFileName(packageName, name.name().toString(), StreamWrapper.Header);
        String inc = X10CPPTranslator.outputFileName(packageName, name.name().toString(), StreamWrapper.Closures);

        System.out.println("Not recompiling: "+name);

        ((X10CPPCompilerOptions)ext.getOptions()).compilationUnits().add(cc);
        ext.compiler().outputFiles().add(cc);
        ext.compiler().outputFiles().add(h);
        ext.compiler().outputFiles().add(inc);
    }
}
