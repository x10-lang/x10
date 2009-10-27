import x10.compiler.Native;
import x10.compiler.NativeRep;

import x10.io.Console;
import x10.util.Random;

class GCSpheres {
    static type Real = Double;

    // original code was for a student coursework
    static interface Submission {
        public def processFrame (x:Double,y:Double,z:Double) : Rail[Int];
    }

    static class Original implements Submission {
        protected var spheres : ValRail[Sphere];

        @Native("java", "java.lang.System.arraycopy(#4.getBackingArray(),#5,#6.getBackingArray(),#7,#8)")
        public static def arraycopy[T] (src:Rail[T], srcPos:Int,
                                        dest:Rail[T], destPos:Int,
                                        length:Int) : Void {
            for (var i:Int=0 ; i<length ; ++i) {
                dest(i+destPos) = src(i+srcPos);
            }
        }

        var result : Rail[Int];

        public def this () = {
            spheres = getSpheres();
            result = Rail.makeVar[Int](spheres.length);
        }

        public def processFrame (x:Real, y:Real, z:Real) {
            val vec = new Vector3(x,y,z);
            for (val p(i):Point in [0..spheres.length-1]) {
                if (spheres(i).intersects(vec)) {
                    result(i) = i;
                }
            }
            return result;
        }
    }

    static value Vector3 {
        public def this (x:Real, y:Real, z:Real) = {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public def getX () = x;
        public def getY () = y;
        public def getZ () = z;
        public def add (other:Vector3) =new Vector3(this.x+other.x,
                                                    this.y+other.y,
                                                    this.z+other.z);
        public def neg () =new Vector3(-this.x, -this.y, -this.z);
        public def sub (other:Vector3) = add(other.neg());
        public def length () = Math.sqrt(length2());
        public def length2 () = x*x + y*y + z*z;

        protected val x : Real;
        protected val y : Real;
        protected val z : Real;
    }

    static value Sphere {
    	def this (x:Real, y:Real, z:Real, r:Real) {
            pos = new Vector3(x,y,z);
            radius = r;
        }
        public def intersects (home:Vector3) : Boolean {
            return home.sub(pos).length2() < radius*radius;
        }
        protected val pos : Vector3;
        protected val radius : Real;
    }

    static class VarBox[T] {
        var f: T;
        def this (x : T) { set(x); }
        def get () = f;
        def set (x : T) { f = x; }
    }

    private static val spheres = new VarBox[ValRail[Sphere]](Rail.makeVal[Sphere](0));

    public static def getSpheres() = spheres.get(); 

    public static def main (args : Rail[String]) throws Exception = {
/*
        if (args.length!=2) {
           Console.ERR.println("Usage: runx10 GCSpheres <Frames> <Seed>");
           Console.ERR.println("<Frames> (int): Number of iterations");
           Console.ERR.println("<Seed> (int): The random seed that controls spheres and point generation");
           Console.ERR.println();
           return;
        }
*/
        val reps = 75; //Int.parseInt(args(0));
        val ran = new Random(0); //Long.parseLong(args(1)));

        generateSpheres(ran);

        //System.gc(); // run a GC cycle

        val init_start = System.nanoTime();
        val student = new Original();
        val init_time = System.nanoTime()-init_start;
        //System.gc(); // run a GC cycle

        // warm up
/*
        for (var frame:Int = 0 ; frame<75 ; ++frame) {
            val student_result = student.processFrame(0,0,0);
        }
*/

        var total_frame_time : Long = 0;

        for (_(frame):Point(1) in  [1..reps]) {
            val x = ran.nextDouble()*10000;
            val y = ran.nextDouble()*10000;
            val z = ran.nextDouble()*10000;

            val frame_start = System.nanoTime();
            val student_result = student.processFrame(x,y,z);

            total_frame_time += System.nanoTime() - frame_start;
        }

        //Console.OUT.println("=== Times in seconds ===");
        //Console.OUT.println("Initialisation: "+init_time/1E9);
        //Console.OUT.println("processFrame time: "+total_frame_time/1E9);
        Console.OUT.println("Total time: "+ (total_frame_time+init_time)/1E9);
    }

    private static def generateSpheres (ran : Random) {
        val tmp = ValRail.make[Sphere](100000, (i:Int) => {
            val x = ran.nextDouble()*10000;
            val y = ran.nextDouble()*10000;
            val z = ran.nextDouble()*10000;
            val r = ran.nextDouble()*1000;

            return new Sphere(x,y,z,r);
        });
        spheres.set(tmp);
    }
}
