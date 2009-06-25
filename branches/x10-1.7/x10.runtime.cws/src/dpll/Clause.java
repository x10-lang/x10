package dpll;

public class Clause {
    int falsePos, truePos, falseNeg, trueNeg;
    // invariant: falsePos + truePos <= pos.length, falseNeg+trueNeg <= neg.length;
    V[] pos, neg;
    public Clause(V[] pos, V[] neg) {
	this.pos = pos;
	this.neg = neg;
    }
    boolean valid() {
	return (truePos > 0 || falseNeg > 0);
    }
    boolean inconsistent() {
	return falsePos == pos.length && trueNeg == neg.length;
    }

    /** Update the state of this clause to reflect the var=value
     * assignment. Guaranteed that the variable occurs in pos or neg.
     */
    void Do(V var, boolean isPos, boolean value) {
	// formula may already be valid, or inconsistent.
        if (isPos) {
	    if (value) {
		truePos++;
		// this is now valid.
	    } else {
		++falsePos;
		if (inconsistent()) 
		    // mark as inconsistent
		    markInconsistent();
		else  checkForced();
	    }
	} else {
	    if (value) {
		++trueNeg;
		if (inconsistent()) markInconsistent();
		else checkForced();
	    } else falseNeg++;
	}
	
    }
    public void markInconsistent() {
    	
    }
    /** Update the state of this clause to undo the var=value
     * assignment. Guaranteed that the variable occurs in pos or neg.
     * No propagation necessary.
     */
    void Undo(V var, boolean isPos, boolean value) {
	if (isPos) {
	    if (value) {
		truePos--;
	    } else {
		falsePos--;
	    }
	} else {
	    if (value) {
		trueNeg--;
	    } else {
		falseNeg--;
	    }
	} 
    }

    public void checkForced() {
	if (falsePos == pos.length -1 && trueNeg==neg.length) {
	    // TODO! Check the variables in pos to determine which one (=v)
	    // is forced. Propagate v=true.
	    return;
	}
	if (trueNeg == neg.length -1 && trueNeg==neg.length) {
	    // TODO! Check the variables in pos to determine which one (=v)
	    // is forced. Propagate v=false;
	    return;
	}
    }
}
