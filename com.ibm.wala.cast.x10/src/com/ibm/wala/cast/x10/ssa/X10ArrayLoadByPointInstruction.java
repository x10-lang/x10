package com.ibm.domo.ast.x10.ssa;

import java.util.Collection;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.ssa.ValueDecorator;
import com.ibm.wala.types.TypeReference;

public class X10ArrayLoadByPointInstruction extends X10ArrayReferenceByPointInstruction {
    private final int result;

    public X10ArrayLoadByPointInstruction(int result, int arrayRef, int pointIndex, TypeReference declaredType) {
	super(arrayRef, pointIndex, declaredType);
	this.result= result;
    }

    public int getResult() {
	return result;
    }

    @Override
    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new X10ArrayLoadByPointInstruction(
		defs == null ? result : defs[0],
		uses == null ? arrayRef : uses[0],
		uses == null ? pointIndex : uses[1],
		declaredType);
    }

    @Override
    public Collection<TypeReference> getExceptionTypes() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitArrayLoadByPoint(this);
    }

    @Override
    public int hashCode() {
        return 6469 + 2393 * result + 5399 * super.hashCode();
    }
}
