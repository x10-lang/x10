package x10.wala.ipa.callgraph;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.strings.Atom;

import java.util.*;

import x10.wala.classLoader.X10LanguageImpl;
import x10.wala.loader.X10SourceLoaderImpl;

public class X10SourceAnalysisScope extends AnalysisScope {
    public X10SourceAnalysisScope() {
        super(Collections.singleton(X10LanguageImpl.X10Lang));
        loadersByName.put(X10SourceLoaderImpl.X10SourceLoader.getName(), X10SourceLoaderImpl.X10SourceLoader);
        setLoaderImpl(X10SourceLoaderImpl.X10SourceLoader, "x10.wala.loader.X10SourceLoaderImpl");
    }
}
