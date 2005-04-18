
/**
 * @author kemal 4/2005
 *
 * Distribution+Region algebra test
 */

public class DistAlgebra {
	
	public boolean run() {
		distribution P=distribution.factory.unique();
		region R1 = [0:1,0:7];
		region R2 = [4:5,0:7];
		region R3 = [0:7,4:5];
		region TR1= (R1||R2) && R3;
		chk(TR1.equals([0:1,4:5] || [4:5,4:5]));
		chk((R1||R2).contains(TR1) && R3.contains(TR1));
		region TR2= R1 || R2 || R3;
		chk(TR2.equals([0:1,0:7]||[4:5,0:7]||
			[2:3,4:5] || [6:7,4:5]));
		chk(TR2.contains(R1) &&  TR2.contains(R2) &&
			TR2.contains(R3));
		region TR3 = (R1||R2)-R3;
		chk(TR3.equals([0:1,0:3]||[0:1,6:7] ||
		   [4:5,0:3]||[4:5,6:7]));
		chk((R1||R2).contains(TR3) && TR3.disjoint(R3));
		distribution TD2=distribution.factory.cyclic(TR2);
		int placeNum=0;
		int offsetWithinPlace=0;
		final int np=place.MAX_PLACES;
		for(point [i,j]:TD2) {
			chk(TD2[i,j]==P[placeNum]);
			placeNum++;
			if (placeNum==np) {
				placeNum=0;
				offsetWithinPlace++;
			}
		}
		distribution TD1=TD2|TR1;
		distribution TD3=TD2|TR3;
		distribution TD4=TD2-TD3;
		distribution TD5=TD2|R3;
		chk(TD4.equals(TD5));
		distribution TD6=(TD3 && TD2);
		chk(TD6.equals(TD3));
		distribution TD7=(TD3 || TD2);
		chk(TD7.equals(TD2));
                return true;
		
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
 	}
	public static void main(String args[]) {
		boolean b= (new DistAlgebra()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
