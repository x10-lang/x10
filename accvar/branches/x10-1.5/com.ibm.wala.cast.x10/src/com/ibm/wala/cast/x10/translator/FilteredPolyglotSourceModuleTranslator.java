package com.ibm.wala.cast.x10.translator;

import java.io.File;

import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceModuleTranslator;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;

public class FilteredPolyglotSourceModuleTranslator extends PolyglotSourceModuleTranslator {

    public FilteredPolyglotSourceModuleTranslator(AnalysisScope scope, IRTranslatorExtension extInfo, PolyglotSourceLoaderImpl sourceLoader) {
	super(scope, extInfo, sourceLoader);
    }

    @Override
    protected boolean skipSourceFile(SourceFileModule entry) {
	String path= entry.getAbsolutePath();
	return path.endsWith(".java") && (new File(path.replace(".java", ".x10")).exists());
    }
}
