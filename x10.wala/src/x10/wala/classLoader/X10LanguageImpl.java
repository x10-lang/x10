package x10.wala.classLoader;

import java.util.Collection;
import java.util.Collections;

import x10.wala.loader.X10SourceLoaderImpl;
import x10.wala.ssa.AstX10InstructionFactory;
import x10.wala.ssa.AsyncInstruction;
import x10.wala.ssa.AtStmtInstruction;
import x10.wala.ssa.AtomicInstruction;
import x10.wala.ssa.FinishInstruction;
import x10.wala.ssa.HereInstruction;
import x10.wala.ssa.IterHasNextInstruction;
import x10.wala.ssa.IterInitInstruction;
import x10.wala.ssa.IterNextInstruction;
import x10.wala.ssa.NextInstruction;
import x10.wala.ssa.TupleInstruction;

import com.ibm.wala.analysis.typeInference.JavaPrimitiveType;
import com.ibm.wala.analysis.typeInference.PrimitiveType;
import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.classLoader.BytecodeLanguage;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.LanguageImpl;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction.ClassToken;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X10LanguageImpl extends LanguageImpl implements BytecodeLanguage {
    // signed primitives are assumed to be the same as java. is this ok?
    // unsigned primitives are not accounted for. is this ok?
    // root type is set to Object. maybe this should be Any
    // getImplicitExceptionTypes and inferInvokeExceptions return empty lists

    private X10LanguageImpl() {
    }

    public static X10LanguageImpl X10Lang = new X10LanguageImpl();

    public static ClassLoaderReference X10Loader = X10SourceLoaderImpl.X10SourceLoader;

    public Atom getName() {
        return X10SourceLoaderImpl.X10;
    }

    private final static TypeName X10LangAnyName = TypeName.string2TypeName("Lx10/lang/Any");
    public final static TypeReference X10LangAny = TypeReference.findOrCreate(X10Loader, X10LangAnyName);

    private final static TypeName X10LangIteratorName = TypeName.string2TypeName("Lx10/lang/Iterator");
    public final static TypeReference X10LangIterator = TypeReference.findOrCreate(X10Loader, X10LangIteratorName);

    private final static TypeName X10LangPlaceName = TypeName.string2TypeName("Lx10/lang/Place");
    public final static TypeReference X10LangPlace = TypeReference.findOrCreate(X10Loader, X10LangPlaceName);

    private final static TypeName X10ArrayArrayName = TypeName.string2TypeName("Lx10.regionarray/Array");
    public final static TypeReference X10ArrayArray = TypeReference.findOrCreate(X10Loader, X10ArrayArrayName);

    private final static TypeName X10LangObjectName = TypeName.string2TypeName("Lx10/lang/Object");
    public final static TypeReference X10LangObject = TypeReference.findOrCreate(X10Loader, X10LangObjectName);

    private final static TypeName X10LangThrowableName = TypeName.string2TypeName("Lx10/lang/Throwable");
    public final static TypeReference X10LangThrowable = TypeReference.findOrCreate(X10Loader, X10LangThrowableName);

    private final static TypeName X10LangStringName = TypeName.string2TypeName("Lx10/lang/String");
    public final static TypeReference X10LangString = TypeReference.findOrCreate(X10Loader, X10LangStringName);

    public TypeReference getAnyType() {
        return X10LangAny;
    }

    public TypeReference getIteratorType() {
        return X10LangIterator;
    }

    public TypeReference getPlaceType() {
        return X10LangPlace;
    }

    public TypeReference getArrayType() {
        return X10ArrayArray;
    }

    public TypeReference getRootType() {
        return X10LangObject;
    }

    public TypeReference getThrowableType() {
        return X10LangThrowable;
    }

    public TypeReference getConstantType(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Boolean) {
            return TypeReference.Boolean;
        } else if (o instanceof Long) {
            return TypeReference.Long;
        } else if (o instanceof Double) {
            return TypeReference.Double;
        } else if (o instanceof Float) {
            return TypeReference.Float;
        } else if (o instanceof Number) {
            return TypeReference.Int;
        } else if (o instanceof String) {
            return X10LangString;
        } else if (o instanceof ClassToken || o instanceof TypeReference) {
            throw new UnsupportedOperationException("X10 does not permit meta data");
        } else if (o instanceof IMethod) {
            throw new UnsupportedOperationException("X10 does not permit reflection");
        } else {
            throw new UnsupportedOperationException("Unknown constant " + o + ": " + o.getClass());
        }
    }

    public boolean isNullType(TypeReference type) {
        return type == null || type == TypeReference.Null;
    }

    public TypeReference[] getArrayInterfaces() {
        return new TypeReference[0];
    }

    public TypeName lookupPrimitiveType(String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<TypeReference> getImplicitExceptionTypes(IInstruction pei) {
        // return Language.JAVA.getImplicitExceptionTypes(pei);
        return Collections.<TypeReference> emptyList();
    }

    public Collection<TypeReference> inferInvokeExceptions(MethodReference target, IClassHierarchy cha)
            throws InvalidClassFileException {
        // return Language.JAVA.inferInvokeExceptions(target, cha);
        return Collections.<TypeReference> emptyList();
    }

    public Object getMetadataToken(Object value) {
        throw new UnsupportedOperationException("X10 does not permit meta data");
    }

    public boolean isDoubleType(TypeReference type) {
        return type == TypeReference.Double;
    }

    public boolean isFloatType(TypeReference type) {
        return type == TypeReference.Float;
    }

    public boolean isIntType(TypeReference type) {
        return type == TypeReference.Int;
    }

    public boolean isLongType(TypeReference type) {
        return type == TypeReference.Long;
    }

    public boolean isMetadataType(TypeReference type) {
        throw new UnsupportedOperationException("X10 does not permit meta data");
    }

    public boolean isStringType(TypeReference type) {
        return type == X10LangString;
    }

    public TypeReference getMetadataType() {
        throw new UnsupportedOperationException("X10 does not permit meta data");
    }

    public TypeReference getPointerType(final TypeReference pointee) {
        throw new UnsupportedOperationException("X10 does not permit explicit pointers");
    }

    public PrimitiveType getPrimitive(final TypeReference reference) {
        return JavaPrimitiveType.getPrimitive(reference);
    }

    public TypeReference getStringType() {
        return X10LangString;
    }

    public boolean isBooleanType(final TypeReference type) {
        return type == TypeReference.Boolean;
    }

    public boolean isCharType(final TypeReference type) {
        return type == TypeReference.Char;
    }

    public boolean isVoidType(TypeReference type) {
        return type == TypeReference.Void;
    }

    public static class InstructionFactory extends JavaSourceLoaderImpl.InstructionFactory implements AstX10InstructionFactory {

        public AsyncInstruction AsyncInvoke(int result, int[] params, int exception, CallSiteReference site, int[] clocks) {
            return new AsyncInstruction(result, params, exception, site, clocks);
        }

        public AsyncInstruction AsyncInvoke(int[] params, int exception, CallSiteReference site, int[] clocks) {
            return new AsyncInstruction(params, exception, site, clocks);
        }

        public AsyncInstruction AsyncInvoke(int[] results, int[] params, int exception, Access[] lexicalReads,
                Access[] lexicalWrites, CallSiteReference csr) {
            return new AsyncInstruction(results, params, exception, lexicalReads, lexicalWrites, csr);
        }

        public AtomicInstruction Atomic(boolean isEnter) {
            return new AtomicInstruction(isEnter);
        }

        public FinishInstruction Finish(boolean isEnter) {
            return new FinishInstruction(isEnter);
        }

        public NextInstruction Next() {
            return new NextInstruction();
        }

        public HereInstruction Here(int retValue) {
            return new HereInstruction(retValue);
        }

        public IterHasNextInstruction IterHasNext(int hasNextValue, int regionIter) {
            return new IterHasNextInstruction(hasNextValue, regionIter);
        }

        public IterInitInstruction IterInit(int iterVal, int regionVal) {
            return new IterInitInstruction(iterVal, regionVal);
        }

        public IterNextInstruction IterNext(int nextValue, int regionIter) {
            return new IterNextInstruction(nextValue, regionIter);
        }

        public TupleInstruction Tuple(int retValue, int[] slotValues) {
            return new TupleInstruction(retValue, slotValues);
        }

        public AtStmtInstruction AtStmt(final boolean isEnter) {
            return new AtStmtInstruction(isEnter);
        }
    }

    private static final InstructionFactory insts = new InstructionFactory();

    public InstructionFactory instructionFactory() {
        return insts;
    }
}
