package x10.constraint;



public class XNativeConstraintSystem implements XConstraintSystem {

	public XConstraint mkConstraint() {
		return new XNativeConstraint();
	}
	
	public XConstraint makeTrueConstraint() {
		return new XNativeConstraint(); 
	}
}
