package x10.wala.analysis.typeInference;

import x10.wala.classLoader.X10LanguageImpl;
import x10.wala.ssa.AstX10InstructionVisitor;
import x10.wala.ssa.AtStmtInstruction;
import x10.wala.ssa.AtomicInstruction;
import x10.wala.ssa.FinishInstruction;
import x10.wala.ssa.HereInstruction;
import x10.wala.ssa.NextInstruction;
import x10.wala.ssa.IterHasNextInstruction;
import x10.wala.ssa.IterInitInstruction;
import x10.wala.ssa.IterNextInstruction;
import x10.wala.ssa.TupleInstruction;

import com.ibm.wala.analysis.typeInference.ConeType;
import com.ibm.wala.analysis.typeInference.JavaPrimitiveType;
import com.ibm.wala.cast.java.analysis.typeInference.AstJavaTypeInference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

public class AstX10TypeInference extends AstJavaTypeInference {

	protected X10LanguageImpl x10Lang;
	
    public AstX10TypeInference(IR ir, IClassHierarchy cha, boolean doPrimitives) {
        super(ir, cha, doPrimitives);
        x10Lang = (X10LanguageImpl) language;
    }
    
    protected class AstX10TypeOperatorFactory extends AstJavaTypeOperatorFactory implements AstX10InstructionVisitor {

        public void visitAtomic(AtomicInstruction instruction) {
            Assertions.UNREACHABLE("Type operator requested for X10 atomic statement");
        }

        public void visitFinish(FinishInstruction instruction) {
            Assertions.UNREACHABLE("Type operator requested for X10 finish statement");
        }

        public void visitNext(NextInstruction instruction) {
            Assertions.UNREACHABLE("Type operator requested for X10 next statement");
        }

        public void visitIterInit(IterInitInstruction instruction) {
            TypeReference type = x10Lang.getIteratorType();
            IClass klass = cha.lookupClass(type);
            result = new DeclaredTypeOperator(new ConeType(klass));
        }

        public void visitIterHasNext(IterHasNextInstruction instruction) {
            if (!doPrimitives) {
                result = null;
              } else {
                result = new DeclaredTypeOperator(JavaPrimitiveType.BOOLEAN);
            }
        }

        public void visitIterNext(IterNextInstruction instruction) {
            // Pretend that this instruction produces a value of type x10.lang.Any.
            TypeReference type = x10Lang.getAnyType();
            IClass klass = cha.lookupClass(type);
            result = new DeclaredTypeOperator(new ConeType(klass));
        }

        public void visitHere(HereInstruction instruction) {
            TypeReference type = x10Lang.getPlaceType();
            IClass klass = cha.lookupClass(type);
            result = new DeclaredTypeOperator(new ConeType(klass));
        }

        public void visitTuple(TupleInstruction tupleInstruction) {
            TypeReference type = x10Lang.getArrayType();
            IClass klass= cha.lookupClass(type);
            result = new DeclaredTypeOperator(new ConeType(klass));
        }

        public void visitAtStmt(final AtStmtInstruction atStmtInstruction) {
            Assertions.UNREACHABLE("Type operator requested for X10 at statement");
        }
    }

    protected void initialize() {
        init(ir, new TypeVarFactory(), new AstX10TypeOperatorFactory());
    }
}
