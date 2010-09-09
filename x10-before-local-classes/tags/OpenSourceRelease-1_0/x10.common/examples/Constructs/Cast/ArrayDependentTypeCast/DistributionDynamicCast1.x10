import harness.x10Test;

/**
 * Purpose: Checks invalid cast is detected at runtime by dynamic cast checking code.
 * Issue: distribution type cast declares an invalid rank:
 * distribution(:rank==3&&rect&&zeroBased) <-- distribution(:rank==2&&rect&&zeroBased)
 * @author vcave
 **/
public class DistributionDynamicCast1 extends x10Test {

	public boolean run() {
		try {
			dist(:rank==3&&rect&&zeroBased) dist3d = 
				(dist(:rank==3&&rect&&zeroBased)) dist.factory.constant(
					region.factory.region(region.factory.region(0, 10), 
							      region.factory.region(0, 10)), 
							      here);			
		} catch (ClassCastException e ) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new DistributionDynamicCast1().execute();
	}

}
 