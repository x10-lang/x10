/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks invalid cast is detected at runtime by dynamic cast checking code.
 * Issue: Region type cast declares an invalid rank.
 * region(:rank==3&&rect&&zeroBased) <-- region(:rank==2&&rect&&zeroBased)
 * @author vcave
 **/
public class RegionDynamicCast1 extends x10Test {

	public boolean run() {
		try {
			region(:rank==3&&rect&&zeroBased) rank3RectZero = 
				(region(:rank==3&&rect&&zeroBased)) region.factory.region(
						new region[] {region.factory.region(0, 10), region.factory.region(0, 10)});			
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new RegionDynamicCast1().execute();
	}

}
 