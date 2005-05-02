/*
 * Minimal test to check if storage classes are implemented.
 *
 * @author kemal 4/2005
 */
class foo {
	int val;
}
class bar {
	place pl;
	public bar(place p) {this.pl=p;}
}
public class StorageClassesTest {

	public boolean run() {
		bar b=new bar(here);
		place p0=here;
		
		foo@place x1= new foo@place();
		foo@activity x2= new foo@activity();
		foo@here x3= new foo();
		foo@(b.pl) x4= new foo();
		foo@p0 x5= future(p0){new foo()}.force();
		foo@? x6= new foo();
		foo x7= new foo();
		nullable foo@place[.] u1;
		nullable foo@activity[.] u2;
		nullable foo@here[.] u3;
		nullable foo@(b.pl)[.] u4;
		nullable foo@p0[.] u5;
		nullable foo@?[.] u6;
		nullable foo[.] u7;
		foo@place y1=(foo@place)x1;
		foo@activity y2=(foo@activity)x2;
		foo@here y3=(foo@here)x3;
		foo@(b.pl) y4=(foo@(b.pl))x4;
		foo@p0 y5=(foo@p0)x5;
		foo@? y6=(foo@?)x6;
		foo y7=(foo)x7;
		
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new StorageClassesTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}
