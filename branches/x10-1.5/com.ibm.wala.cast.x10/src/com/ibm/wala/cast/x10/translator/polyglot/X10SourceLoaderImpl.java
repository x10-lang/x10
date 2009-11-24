/*
 * Created on Apr 28, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import java.io.IOException;

import com.ibm.wala.cast.x10.loader.X10Language;
import com.ibm.wala.cast.x10.loader.X10PrimordialClassLoader;
import com.ibm.wala.cast.x10.ssa.X10InstructionFactory;
import com.ibm.wala.cast.x10.translator.X10CAstEntity;
import com.ibm.wala.cast.ir.translator.AstTranslator.AstLexicalInformation;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.cast.loader.AstMethod.DebuggingInformation;
import com.ibm.wala.cast.loader.AstMethod.LexicalInformation;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.cfg.AbstractCFG;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X10SourceLoaderImpl extends PolyglotSourceLoaderImpl {
    public static Atom X10SourceLoaderName= Atom.findOrCreateAsciiAtom("X10Source");

    public static ClassLoaderReference X10SourceLoader= new ClassLoaderReference(X10SourceLoaderName, X10Language.X10, X10PrimordialClassLoader.X10Primordial);

    public X10SourceLoaderImpl(ClassLoaderReference loaderRef, IClassLoader parent, SetOfClasses exclusions,
	    IClassHierarchy cha, IRTranslatorExtension extInfo) throws IOException {
	super(loaderRef, parent, exclusions, cha, extInfo);
    }

    @Override
    public ClassLoaderReference getReference() {
        return X10SourceLoader;
    }

    @Override
    public Language getLanguage() {
        return X10Language.X10Lang;
    }

    public void defineAsync(CAstEntity fn, TypeReference asyncRef, CAstSourcePositionMap.Position fileName) {
	X10AsyncObject asyncType= new X10AsyncObject(asyncRef, TypeReference.JavaLangObject, this, fileName, cha);
	fTypeMap.put(fn, asyncType);
	loadedClasses.put(asyncType.getName(), asyncType);
    }

    public void defineClosure(CAstEntity fn, TypeReference closureRef, CAstSourcePositionMap.Position fileName) {
	X10ClosureObject closureType= new X10ClosureObject(closureRef, TypeReference.JavaLangObject, this, fileName, cha);
	fTypeMap.put(fn, closureType);
	loadedClasses.put(closureType.getName(), closureType);
    }

    /**
     * Ideally we'd want to create some sort of IMethod to represent the async from
     * within X10Cast2IRTranslatingVisitor, but sadly that doesn't have a defineFunction()
     * method (since it doesn't actually inherit AstTranslator). So we create the
     * IMethod here inside the loader, where we can actually trap defineFunction().
     * Gack.
     */
    public void defineFunction(CAstEntity n, IClass owner, AbstractCFG cfg, SymbolTable symtab, boolean hasCatchBlock, TypeReference[][] catchTypes, AstLexicalInformation lexicalInfo, DebuggingInformation debugInfo) {
	if (n.getKind() == X10CAstEntity.ASYNC_BODY) {
	    X10AsyncObject asyncObject= (X10AsyncObject) fTypeMap.get(n);

	    asyncObject.setCodeBody(new ConcreteJavaMethod(n, asyncObject, cfg, symtab, hasCatchBlock, catchTypes, false, lexicalInfo, debugInfo));
	} else if (n.getKind() == X10CAstEntity.CLOSURE_BODY) {
	    X10ClosureObject closureObject= (X10ClosureObject) fTypeMap.get(n);

	    closureObject.setCodeBody(new ConcreteJavaMethod(n, closureObject, cfg, symtab, hasCatchBlock, catchTypes, false, lexicalInfo, debugInfo));
	} else
	    super.defineFunction(n, owner, cfg, symtab, hasCatchBlock, catchTypes, false, lexicalInfo, debugInfo);
    }

    public String toString() {
      return "X10 Source Loader (classes " + loadedClasses.values() + ")";
    }
    
    public X10Language.InstructionFactory getInstructionFactory() {
    	return X10Language.X10Lang.instructionFactory();
    }
}
