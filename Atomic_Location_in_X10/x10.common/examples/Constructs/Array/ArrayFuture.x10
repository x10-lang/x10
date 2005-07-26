/*
 * Testing arrays of future<T>
 *
 * @author kemal, 5/2005
 */
public class ArrayFuture {

	public boolean run() {
		
		final distribution d=[1:10,1:10]->here;
		final future<int>[.] ia = new future<int>[d] (point [i,j]){return future(here){i+j};};
		for(point [i,j]:ia) chk(ia[i,j].force()==i+j);
		return true;
	}

    static void chk(boolean b) {if(!b) throw new Error();}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ArrayFuture()).run();
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
