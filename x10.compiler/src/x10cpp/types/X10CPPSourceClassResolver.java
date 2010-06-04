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
import x10cpp.visit.X10CPPTranslator;
import x10cpp.visit.X10CPPTranslator.DelegateTargetFactory;

public class X10CPPSourceClassResolver extends X10SourceClassResolver {

    public X10CPPSourceClassResolver(Compiler compiler, ExtensionInfo ext, String classpath, boolean compileCommandLineOnly, boolean ignoreModTimes) {
        super(compiler, ext, classpath, compileCommandLineOnly, ignoreModTimes);
    }

    /**
     * Load a class file for class <code>name</code>.
     */
    protected Resource loadFile(QName name) {
        if (nocache.contains(name)) {
            return null;
        }
        
        Options options = Globals.Options();
        
        X10CPPTranslator.DelegateTargetFactory tf = new X10CPPTranslator.DelegateTargetFactory(options.output_directory,
                                             "cc", "h", options.output_stdout);

        Source source = ext.sourceLoader().classSource(name);
        if (source == null)
            return null;
        
        String packageName = name.qualifier() != null ? name.qualifier().toString() : null;
        final File f = tf.outputFile(packageName, source);
        final File h = tf.outputHeaderFile(packageName, source);

        if (f.exists() && h.exists()) {
            return new Resource() {
            	public File file() {
                    long ft = f.lastModified();
                    long ht = h.lastModified();
                    // pick the older file
                    if (ft < ht)
                    	return f;
                    else
                    	return h;
                }

                public InputStream getInputStream() throws IOException {
                	throw new IOException();
                }

				public String name() {
					return f.getPath();
				}
            };
        }

        nocache.add(name);

        return null;
    }

    protected void handleUpToDateTarget(QName name, Resource file) {
        // FIXME: [IP] HACK
        // Add this file to outputFiles even if it won't be compiled
        if (!isOutput(name))
            return;
        String path = file.name();
        String out = ext.getOptions().output_directory.getPath();
        assert (path.startsWith(out)) : "Unknown path: "+path;
        path = path.substring(out.length()+1);
        compiler.outputFiles().add(path);
    }
}
