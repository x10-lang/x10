import x10.io.Console;
import x10.compiler.Cuda;

public class CudaKernelTest {

    static def doWork (init:Rail[Float]!, recv:Rail[Float]!, p:Place, len:Int) {
        val remote = Rail.makeRemote(p,len,(Int)=>0.0 as Float); // allocate 

        finish init.copyTo(0, remote, 0, len); // dma there

        at (p) @Cuda {
            for ((block):Point in 0..7) {
                for ((thread):Point in 0..63) async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Int=tid ; i<len ; i+=tids) {
                        remote(i) = Math.sqrt(remote(i));
                    }
                }
            }
        }

        finish recv.copyFrom(0, remote, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for ((i) in 0..remote.length-1)
            if ((1-Math.abs((recv(i)*recv(i)) / (i as Float))) > 1E-6f) success = false;
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at place "+p);
    }

    public static def main (args : Rail[String]!) {
        val len = args.length==1 ? Int.parseInt(args(0)) : 1000000;

        for (host in Place.places) at (host) {

            val init = Rail.makeVar(len,(i:Int)=>i as Float);
            val recv = Rail.makeVar(len,(i:Int)=>0.0 as Float);

            var done_work:Boolean = false;

            for (gpu in here.children()) if (gpu.isCUDA()) {
                doWork(init, recv, gpu, len);
                done_work = true;
            }

            if (!done_work) {
                doWork(init, recv, here, len);
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

