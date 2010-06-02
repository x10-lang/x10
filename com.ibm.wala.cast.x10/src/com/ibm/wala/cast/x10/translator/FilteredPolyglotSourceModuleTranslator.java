package com.ibm.wala.cast.x10.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.ResourcesPlugin;

import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceModuleTranslator;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.types.ClassLoaderReference;

public class FilteredPolyglotSourceModuleTranslator extends PolyglotSourceModuleTranslator {
    private static final String SERIALIZED_TYPE_FIELD_NAME = "jlc$ClassType$x10";

    public FilteredPolyglotSourceModuleTranslator(AnalysisScope scope, IRTranslatorExtension extInfo, PolyglotSourceLoaderImpl sourceLoader, ClassLoaderReference searchPathStart) {
	super(scope, extInfo, sourceLoader, searchPathStart);
    }

    @Override
    protected boolean skipSourceFile(SourceFileModule entry) {
        String path= entry.getAbsolutePath();

        // Look for a "fingerprint" of the X10 compiler in the source.
        // If serialization isn't turned off, Polyglot sticks a serialized
        // type object into a synthetic field with a reliable name.

        InputStream contentsStream= entry.getInputStream();
        String contents= readStreamContents(contentsStream);

        if (contents.contains(SERIALIZED_TYPE_FIELD_NAME)) {
                return true;
        }
        // As a last resort, just see whether there's a corresponding .java
        // file in the same folder. Unfortunately, if the user mistakenly
        // puts the .x10 source in the wrong folder (according to what the
        // package decl says), this won't work.
        return (new File(path.replace(".java", ".x10")).exists());
    }

    /**
     * Reads the contents of the given input stream into a string using the given encoding.
     * Returns null if an error occurred.
     */
    public static String readStreamContents(InputStream is, String encoding) {
        BufferedReader reader= null;
        try {
            StringBuffer buffer= new StringBuffer();
            char[] part= new char[2048];
            int read= 0;
            reader= new BufferedReader(new InputStreamReader(is, encoding));
    
            while ((read= reader.read(part)) != -1)
                buffer.append(part, 0, read);
    
            return buffer.toString();
        } catch (IOException ex) {
            System.err.println("I/O Exception: " + ex.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // silently ignored
                }
            }
        }
        return null;
    }

    public static String readStreamContents(InputStream is) {
        return readStreamContents(is, ResourcesPlugin.getEncoding());
    }
}
