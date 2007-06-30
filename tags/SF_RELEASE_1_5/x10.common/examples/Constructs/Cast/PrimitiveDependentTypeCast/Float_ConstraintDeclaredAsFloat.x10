/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Illustrates float dependent type usage
 * Note: Append an 'F' forces constraint representation to be a float.
 * @author vcave
 **/
public class Float_ConstraintDeclaredAsFloat extends x10Test {

	public boolean run() {
		float j = 0.00001F;
		// the constraint is represented as a long
		float (: self == 0.00002F) i = 0.00002F;
		i = (float (: self == 0.00002F)) (j*2);
		return ((j == 0.00001F) && (i==0.00002F));
	}

	public static void main(String[] args) {
		new Float_ConstraintDeclaredAsFloat().execute();
	}

}
 