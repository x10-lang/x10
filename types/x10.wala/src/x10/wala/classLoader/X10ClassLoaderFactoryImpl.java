package x10.wala.classLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import x10.wala.loader.X10SourceLoaderImpl;

import com.ibm.wala.classLoader.ClassLoaderFactoryImpl;
import com.ibm.wala.classLoader.ClassLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;

public class X10ClassLoaderFactoryImpl extends ClassLoaderFactoryImpl {

    public X10ClassLoaderFactoryImpl(SetOfClasses exclusions) {
        super(exclusions);
    }

    protected IClassLoader makeNewClassLoader(ClassLoaderReference classLoaderReference, IClassHierarchy cha, IClassLoader parent, AnalysisScope scope) throws IOException {
        return new X10SourceLoaderImpl(classLoaderReference, parent, getExclusions(), cha);
    }
}
