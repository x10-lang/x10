package x10.constraint;

import java.util.List;

/**
 * Represents t1 + t2
 * 
 * Intended as placeholder for special treatment for propagation rules and simplification rules
 * for +.
 * @author vj
 *
 */
public class XPlus_c extends XFormula_c {

	public XPlus_c(List<XTerm> args) {
		super(XTerms.plusName, args);
	}

	public XPlus_c(XTerm... args) {
		super(XTerms.plusName, args);
	}

	public String toString() {
		return arguments.get(0) + "+" + arguments.get(1);
	}
}
