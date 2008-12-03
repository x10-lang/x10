/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks X10 array identity cast works.
 * @author vcave
 **/
public class ArrayIdentityCast extends x10Test {

	public boolean run() {
		region(:rank==1&&zeroBased) rank1Zero = 
			(region(:rank==1&&zeroBased)) region.factory.region(0, 10);
		
		dist(:rank==1&&zeroBased) d1 = 
			(dist(:rank==1&&zeroBased)) dist.factory.constant(rank1Zero, here);

		int[:rank==1&&zeroBased] ia = (int[:rank==1&&zeroBased]) new int[d1];
		
		return true;
	}

	public static void main(String[] args) {
		new ArrayIdentityCast().execute();
	}

}
 