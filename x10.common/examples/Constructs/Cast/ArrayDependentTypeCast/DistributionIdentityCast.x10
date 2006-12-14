/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks widening dynamic cast works for distribution.
 * @author vcave
 **/
public class DistributionIdentityCast extends x10Test {

	public boolean run() {		
		region(:rank==1&&zeroBased) rank1Zero = 
			(region(:rank==1&&zeroBased)) region.factory.region(0, 10);
		
		dist(:rank==1&&zeroBased) d1 = 
			(dist(:rank==1&&zeroBased)) dist.factory.constant(rank1Zero, here);

		dist(:rank==2&&zeroBased) d2 = (dist(:rank==2&&zeroBased)) dist.factory.constant(
				region.factory.region(rank1Zero,rank1Zero), here);
		return true;
	}

	public static void main(String[] args) {
		new DistributionIdentityCast().execute();
	}

}
 