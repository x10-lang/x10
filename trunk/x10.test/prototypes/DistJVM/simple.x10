/**
 * @author cmd
 */

public class simple {
	
	
  public static void main(String[] args) {
	final int index=93;	
	final region e= region.factory.region(0,100);
	final region copy_e = e;
	final dist d=dist.factory.block(e);
	final int[.] array = new int[d];
	finish async(array.distribution[index]){array[index] = 99;System.out.println("set a");}
	async(array.distribution[index]){
	  System.out.println("a["+index+"] is:"+array[index]);
	  System.out.println("region e:"+e);
	  System.out.println("region copy_e:"+copy_e);
	  }
	x10.lang.Runtime.setExitCode(0);
}
	
}
