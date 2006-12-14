/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that information in the types of variables occuring in depclauses is used
 * during entailment. Here b is of type region(:rank==a.rank), so it would seem that it does
 * not entail self.rank==2. However, a is of type region(:rank==2), and so it is definitely the case
 * that a.rank==2. Hence self.rank==2, and the assignment should succeed.
 *
 * @author vj
 */
public class Transitive extends x10Test {
	
	public boolean run() {
	    final region(:rank==2) a = [0:10, 0:10];
	    final region(:rank==a.rank) b = a;
	    region(:rank==2) c =b;
	    return true;
	}
	public static void main(String[] args) {
		new Transitive().execute();
	}
}

