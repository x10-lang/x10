package x10.wala.ipa.callgraph;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.types.ClassLoaderReference;

import java.util.*;

import x10.wala.classLoader.X10LanguageImpl;
import x10.wala.loader.X10SourceLoaderImpl;

public class X10SourceAnalysisScope extends JavaSourceAnalysisScope {
    private static final Set<Language> languages = new HashSet<Language>(2);

    static {
        languages.add(X10LanguageImpl.X10Lang);
    }

    public X10SourceAnalysisScope() {
        super(languages);
        loadersByName.put(X10SourceLoaderImpl.X10SourceLoaderName, X10SourceLoaderImpl.X10SourceLoader);
        setLoaderImpl(X10SourceLoaderImpl.X10SourceLoader, "x10.wala.loader.X10SourceLoaderImpl");
    }
}
