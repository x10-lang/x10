package org.eclipse.imp.x10dt.analysis;

import org.eclipse.imp.pdb.analysis.IFactGenerator;
import org.eclipse.imp.pdb.analysis.IFactGeneratorFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public class X10FactGeneratorFactory implements IFactGeneratorFactory {

    public IFactGenerator create(Type type) {
        if (type.equals(X10FactTypes.X10Types) || type.equals(X10FactTypes.X10TypeHierarchy)) {
            return new X10TypeFactGenerator();
        }
        throw new IllegalArgumentException("X10 Fact Generator Factory: don't know how to produce fact " + type);
    }

    public TypeStore declareTypes() {
        // Force static initializers on X10FactTypes to run
        @SuppressWarnings("unused") Type dummy= X10FactTypes.X10CallGraphType;
        return X10FactTypes.ts;
    }

    public String getName() {
        return "X10 Fact Generator Factory";
    }
}
