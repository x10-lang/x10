package x10.wala.ipa.callgraph;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.types.TypeReference;

import java.util.*;

import x10.wala.classLoader.X10LanguageImpl;
import x10.wala.loader.X10SourceLoaderImpl;

public class X10SourceAnalysisScope extends AnalysisScope {
    public X10SourceAnalysisScope() {
        super(Collections.singleton(X10LanguageImpl.X10Lang));
        loadersByName.put(X10SourceLoaderImpl.X10SourceLoader.getName(), X10SourceLoaderImpl.X10SourceLoader);
        setLoaderImpl(X10SourceLoaderImpl.X10SourceLoader, "x10.wala.loader.X10SourceLoaderImpl");

        this.setExclusions(new SetOfClasses() {

            @Override
             public boolean contains(String klassName) {
                return klassName.matches("x10/lang/(Void)?Fun_.*");
            }

            @Override
            public boolean contains(TypeReference klass) {
                return contains(klass.getName().toString().substring(1));
            }

            @Override
            public void add(IClass klass) {
            }
        });
    }
}
