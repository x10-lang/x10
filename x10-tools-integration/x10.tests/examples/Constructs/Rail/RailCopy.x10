import x10.io.Console;
import x10.util.Pair;
import harness.x10Test;

import x10.compiler.Native;

public class RailCopy extends x10Test {

    // If the two rails are not the same, return 1 and print an error
    public static def verify[T] (r : ValRail[T], master : ValRail[T], where : String)
    {
        if (r.length != master.length) {
            Console.ERR.println("Failed verification at "+where+": "+"length mismatch");
            return 1;
        }
        for ((i) in 0..r.length-1) {
            if (r(i) != master(i)) {
                Console.ERR.println("\033[0;31m----------\033[0m");
                Console.ERR.println("Failed verification at "+where+": "
                                    + "test_subject("+i+") == " + r(i)
                                    + " (should be "+master(i)+")");
                return 1;
            }
        }
        Console.ERR.println("Success at "+where);
        return 0;
    }

    @Native("c++", "fprintf(stderr,\"addr: %p\\n\",(#1)._val)")
    public static def printPtr(o:Object) { }

    @Native("c++", "fprintf(stderr,\"addr remote: %p\\n\",(void*)(size_t)x10aux::get_remote_ref((#1)._val))")
    public static def printRemotePtr(o:Object) { }

    @Native("c++", "fprintf(stderr,\"name: \\\"%s\\\"\\n\",(#1)->_type()->name())")
    public static def printName(o:Object) { }

    public static def test[T] (sz: Int, there : Place, init1: (Int)=>T, init2: (Int)=>T, prefix : String) {

        // for verification
        val master1 = ValRail.make(sz, init1);
        val master2 = ValRail.make(sz, init2);
        var failures:Int = 0;

        val local = Rail.make(sz, init1);
        val localv = local as ValRail[T];
        val remote = at (there) Rail.make(sz, init2);
        val handle = PlaceLocalHandle.make[Rail[T]](Dist.makeUnique(here==there ? [here] : [here,there]), ()=>Rail.make(sz, init2));

        failures += verify(at (here) local as ValRail[T], master1, prefix+": Local rail initialization");
        failures += verify(at (here) localv, master1, prefix+": Local valrail initialization");
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": Remote rail initialization");

        val c = new Cell[Boolean](false); // for notifiers
        //printPtr(c);
        //printName(c);
        val update = (c2:Cell[Boolean]!) => { atomic { c2.value = true; c.value = true; } ; /*printPtr(c2); printPtr(c); printName(c)*/ };
        val notifier = () => { /*printRemotePtr(c); */ if (here==c.home) update(c); else Runtime.runAtNative(c.home.id, ()=>update(c)); };

///* temporarily commented out
        finish local.copyTo(0,remote,0,sz);
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 1");
        at (remote) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 1 (reset)");

        finish local.copyTo(0,there,()=>Pair[Rail[T],Int](remote,0),sz);
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 2");
        at (remote) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 2 (reset)");
//*/

        c(false); local.copyTo(0,remote,0,sz,notifier); await c.value;
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 3");
        at (there) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 3 (reset)");

        c(false); local.copyTo(0,there,()=>Pair[Rail[T],Int](remote,0),sz,notifier);  await c.value; 
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 4");
        at (remote) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 4 (reset)");

///* temporarily commented out
        finish local.copyTo(0,there,handle,0,sz);
        failures += verify(at (there) handle() as ValRail[T], master1, prefix+": test 5");
        at (there) handle().reset(init2);
        failures += verify(at (there) handle() as ValRail[T], master2, prefix+": test 5 (reset)");

        c(false); local.copyTo(0,there,handle,0,sz,notifier); await c.value;
        failures += verify(at (there) handle() as ValRail[T], master1, prefix+": test 6");
        at (there) handle().reset(init2);
        failures += verify(at (there) handle() as ValRail[T], master2, prefix+": test 6 (reset)");


        finish localv.copyTo(0,remote,0,sz);
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 1v");
        at (remote) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 1v (reset)");

        finish localv.copyTo(0,there,()=>Pair[Rail[T],Int](remote,0),sz);
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 2v");
        at (remote) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 2v (reset)");
//*/

/* not implemented
        c(false); localv.copyTo(0,remote,0,sz,notifier); await c.value;
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 3v");
        at (there) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 3v (reset)");
*/

/* not implemented
        c(false); localv.copyTo(0,there,()=>Pair[Rail[T],Int](remote,0),sz,notifier); await c.value;
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": test 4v");
        at (there) remote.reset(init2);
        failures += verify(at (there) remote as ValRail[T], master2, prefix+": test 4v (reset)");
*/

/* not implemented
        finish localv.copyTo(0,there,handle,0,sz);
        failures += verify(at (there) handle() as ValRail[T], master1, prefix+": test 5v");
        at (there) handle().reset(init2);
        failures += verify(at (there) handle() as ValRail[T], master2, prefix+": test 5v (reset)");
*/

/* not implemented
        c(false); localv.copyTo(0,there,handle,0,sz,notifier); await c.value;
        failures += verify(at (there) handle() as ValRail[T], master1, prefix+": test 6v");
        at (there) handle().reset(init2);
        failures += verify(at (there) handle() as ValRail[T], master2, prefix+": test 6v (reset)");
*/

///* temporarily commented out
        local.reset(init2);
        at (remote) remote.reset(init1);

        failures += verify(at (here) local as ValRail[T], master2, prefix+": Local rail initialization (swapped)");
        failures += verify(at (there) remote as ValRail[T], master1, prefix+": Remote rail initialization (swapped)");

        finish local.copyFrom(0,remote,0,sz);
        failures += verify(local as ValRail[T], master1, prefix+": test 1f");
        local.reset(init2);
        failures += verify(local as ValRail[T], master2, prefix+": test 1f (reset)");

        finish local.copyFrom(0,there,()=>Pair[Rail[T],Int](remote,0),sz);
        failures += verify(local as ValRail[T], master1, prefix+": test 2f");
        local.reset(init2);
        failures += verify(local as ValRail[T], master2, prefix+": test 2f (reset)");

        finish local.copyFrom(0,localv,0,sz);
        failures += verify(local as ValRail[T], master1, prefix+": test 1fv");
        local.reset(init2);
        failures += verify(local as ValRail[T], master2, prefix+": test 1fv (reset)");

        finish local.copyFrom(0,there,()=>Pair[ValRail[T],Int](remote as ValRail[T],0),sz);
        failures += verify(local as ValRail[T], master1, prefix+": test 2fv");
        local.reset(init2);
        failures += verify(local as ValRail[T], master2, prefix+": test 2fv (reset)");
//*/



        return failures;

    }

    public def run () : Boolean {
        var failures:Int = 0;
        val places = here.next() == here ? [here] : [here.next(), here];
        for (p in places) {
            Console.ERR.println("=========================");
            Console.ERR.println("| "+(here==p?"  Local copy test    ":"  Remote copy test   ")+" |");
            Console.ERR.println("=========================");
            for (i in [1, 4200, 500000]) {
                failures += test(i, p, (i:Int)=>(65+(i%26)) as Char, (i:Int)=>'x', "Char"+i);
                failures += test(i, p, (i:Int)=>i+1000 as Byte, (i:Int)=>0 as Byte, "Byte"+i);
                failures += test(i, p, (i:Int)=>i-1000 as Int, (i:Int)=>0 as Int, "Int"+i);
                failures += test(i, p, (i:Int)=>i/1000.0 as Float, (i:Int)=>0 as Float, "Float"+i);
                failures += test(i, p, (i:Int)=>Math.pow(-i,3) as Double, (i:Int)=>0 as Double, "Double"+i);
            }
        }
        Console.ERR.println("Number of failures: "+failures);
        return failures == 0;
    }

    public static def main(Rail[String])  {
        new RailCopy().execute();
    }

}



// vim: shiftwidth=4:tabstop=4:expandtab

