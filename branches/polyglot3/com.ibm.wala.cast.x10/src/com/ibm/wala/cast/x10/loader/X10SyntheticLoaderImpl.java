package com.ibm.wala.cast.x10.loader;

import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.BypassSyntheticClassLoader;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.strings.Atom;

public class X10SyntheticLoaderImpl extends BypassSyntheticClassLoader {

    public static Atom X10SyntheticLoaderName= Atom.findOrCreateAsciiAtom("X10Synthetic");

    public static ClassLoaderReference X10SyntheticLoader= new ClassLoaderReference(AnalysisScope.SYNTHETIC, X10SourceLoaderImpl.X10, X10SourceLoaderImpl.X10SourceLoader);

    public X10SyntheticLoaderImpl(ClassLoaderReference loader, IClassLoader parent,
            SetOfClasses exclusions, IClassHierarchy cha) {
        super(loader, parent, exclusions, cha);
    }

    @Override
    public Language getLanguage() {
        return X10Language.X10Lang;
    }
}
