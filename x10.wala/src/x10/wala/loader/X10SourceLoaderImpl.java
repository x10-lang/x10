package x10.wala.loader;

import java.io.IOException;
import java.util.Set;

import x10.wala.classLoader.X10LanguageImpl;

import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.SourceModuleTranslator;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X10SourceLoaderImpl extends JavaSourceLoaderImpl {
    public final static Atom X10SourceLoaderName = Atom.findOrCreateAsciiAtom("X10Source");

    public final static Atom X10 = Atom.findOrCreateUnicodeAtom("X10");

    public static ClassLoaderReference X10SourceLoader = new ClassLoaderReference(X10SourceLoaderName, X10, null);

    public X10SourceLoaderImpl(ClassLoaderReference loaderRef, IClassLoader parent, SetOfClasses exclusions, IClassHierarchy cha)
            throws IOException {
        super(loaderRef, parent, exclusions, cha);
    }

    @Override
    public ClassLoaderReference getReference() {
        return X10SourceLoader;
    }

    @Override
    public Language getLanguage() {
        return X10LanguageImpl.X10Lang;
    }

    public void defineAsync(CAstEntity fn, TypeReference asyncRef, CAstSourcePositionMap.Position fileName) {
        X10AsyncObject asyncType = new X10AsyncObject(asyncRef, X10LanguageImpl.X10Lang.getAnyType(), this, fileName, cha);
        fTypeMap.put(fn, asyncType);
        loadedClasses.put(asyncType.getName(), asyncType);
    }

    public void defineClosure(CAstEntity fn, TypeReference closureRef, CAstSourcePositionMap.Position fileName) {
        X10ClosureObject closureType = new X10ClosureObject(closureRef, X10LanguageImpl.X10Lang.getAnyType(), this, fileName,
                cha);
        fTypeMap.put(fn, closureType);
        loadedClasses.put(closureType.getName(), closureType);
    }

    public String toString() {
        return "X10 Source Loader (classes " + loadedClasses.values() + ")";
    }

    @Override
    protected SourceModuleTranslator getTranslator() {
        return new SourceModuleTranslator() {
            public void loadAllSources(Set s) {
            }
        };
    }

    @Override
    public InstructionFactory getInstructionFactory() {
        return X10LanguageImpl.X10Lang.instructionFactory();
    }
}
