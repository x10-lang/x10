/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks widening dynamic cast works for region.
 * @author vcave
 **/
public class RegionCastWideningClause extends x10Test {

	public boolean run() {
		// widening cast
		region rank1Zero_1 = region.factory.region(0, 10);
		region(:rank==1) rank1Zero_2 = (region(:rank==1)) region.factory.region(0, 10);
		region(:zeroBased) rank1Zero_3 = (region(:zeroBased)) region.factory.region(0, 10);
		
		// widening cast
		// new region construct from previously defined one
		region(:rank==2&&rect&&zeroBased) rank2RectZero_1 = 
			(region(:rank==2&&rect&&zeroBased)) region.factory.region(new region[] {rank1Zero_2, rank1Zero_3});

		region(:rank==2) rank2RectZero_2 = 
			(region(:rank==2)) region.factory.region(new region[] {rank1Zero_2, rank1Zero_3});

		region(:rect&&zeroBased) rank2RectZero_3 = 
			(region(:rect&&zeroBased)) region.factory.region(new region[] {rank1Zero_2, rank1Zero_3});
		return true;
	}

	public static void main(String[] args) {
		new RegionCastWideningClause().execute();
	}

}
 