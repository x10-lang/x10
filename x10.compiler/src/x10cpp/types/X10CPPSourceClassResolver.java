/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cpp.types; 

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.FileSource;
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
    protected Resource loadX10CompiledFile(QName name) {
        if (nocache.contains(name)) {
            return null;
        }
        
        Source source = ext.sourceLoader().classSource(name);
        if (source == null)
            return null;
        
        String packageName = name.qualifier() != null ? name.qualifier().toString() : null;
        final File cc = X10CPPTranslator.outputFile(ext.getOptions(), packageName, name.name().toString(), StreamWrapper.CC);
        final File h = X10CPPTranslator.outputFile(ext.getOptions(), packageName, name.name().toString(), StreamWrapper.Header);

        // DISABLE again due to XTENLANG-2326.
        // To re-enable, the proper fix is in handleUpToDateTarget we need to schedule a subset of the compilation
        // phases so that we will do sufficient processing of the class to be able to walk it's class decls
        // correctly identify output files and process @NativeCPP directives (if any).
        // Until the typechecker isn't the bottleneck in compilation, skipping the compilation isn't worth the hassle
        // because we're going to include the .cc file in the post compilation command anyways.
        if (false && cc.exists() && h.exists()) {
            final File oldest = oldestFile(new File[] {cc,h});
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

    protected void handleUpToDateTarget(FileSource source, QName name, Resource file) {
        // FIXME: [IP] HACK
        // Add the files associated to this class to outputFiles even if they won't be compiled
        if (!isOutput(name))
            return;

        String packageName = name.qualifier() != null ? name.qualifier().toString() : null;
        String cc = X10CPPTranslator.outputFileName(packageName, name.name().toString(), StreamWrapper.CC);
        String h = X10CPPTranslator.outputFileName(packageName, name.name().toString(), StreamWrapper.Header);

        if (reporter.should_report(report_topics, 4)) {
            reporter.report(4, "Not recompiling: "+name);
        }

        ((X10CPPCompilerOptions)ext.getOptions()).compilationUnits().add(cc);
        ext.compiler().addOutputFile(name, cc);
        ext.compiler().addOutputFile(name, h);
    }
}
