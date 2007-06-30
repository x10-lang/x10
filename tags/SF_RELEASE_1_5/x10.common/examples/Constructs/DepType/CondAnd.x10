/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that properly typed arguments are accepted by &&
 *
 * @author vj
 */
public class CondAnd extends x10Test {
   
   
	public boolean run() {
	   region(:rank==1) r1 = [0:100];
	   region(:rank==1) r2 = [2:99];
	   region r3 = r1 && r2;
	   return true;
	}
	public static void main(String[] args) {
		new CondAnd().execute();
	}
}