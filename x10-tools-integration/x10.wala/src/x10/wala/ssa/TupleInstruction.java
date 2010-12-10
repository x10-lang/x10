package x10.wala.ssa;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class TupleInstruction extends SSAInstruction {
    private int retValue;

    private int childValues[];

    public TupleInstruction(int result, int[] childValues) {
        super();
        this.retValue = result;
        this.childValues = childValues;
    }

    public int getResultValue() {
        return retValue;
    }

    public int[] getChildValues() {
        return childValues;
    }

    @Override
    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
        TupleInstruction copy = ((AstX10InstructionFactory) insts).Tuple((defs != null) ? defs[0] : retValue,
                childValues);

        if (childValues != null) {
            copy.childValues = new int[childValues.length];
            for (int i = 0; i < childValues.length; i++) {
                copy.childValues[i] = (uses != null) ? uses[i] : childValues[i];
            }
        }
        return copy;
    }

    @Override
    public int getNumberOfUses() {
        return childValues.length;
    }

    @Override
    public int getUse(int j) {
        return childValues[j];
    }

    @Override
    public int getNumberOfDefs() {
        return 1;
    }

    @Override
    public int getDef() {
        return retValue;
    }
    
    public int getDef(int j) {
      return retValue;
    }

    public void visit(IVisitor v) {
        ((AstX10InstructionVisitor) v).visitTuple(this);
    }

    @Override
    public boolean isFallThrough() {
        return true;
    }

    @Override
    public String toString(SymbolTable symbolTable) {
        StringBuffer sb = new StringBuffer();

        if (childValues != null) {
            sb.append(" (");
            for (int i = 0; i < childValues.length; i++) {
                sb.append(" " + getValueString(symbolTable, childValues[i]));
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result= retValue * 4423;
        for(int i=0; i < childValues.length; i++) {
            result = result * 6827 + 3617;
        }
        return result;
    }
}
