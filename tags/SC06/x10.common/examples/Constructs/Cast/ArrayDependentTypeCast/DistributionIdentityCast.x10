import harness.x10Test;

/**
 * Purpose: Check primitive dependent type assignment to primitive variable works.
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
 