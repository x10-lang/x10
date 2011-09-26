/** 
 * The Computer Language Benchmarks Game
* http://shootout.alioth.debian.org/
* 
* Based on Java-6 version, from Mark C. Lewis and Chad Whipkey
* 
* v3: v2 with additional single-place parallelism */
class nbody {
    public static def main(args: Array[String](1){rail}) {
    	val start = System.nanoTime();
        val n= Int.parse(args(0));

        val bodies = new NBodySystem();
        Console.OUT.printf("%.9f\n", bodies.energy());
        for (i in 1..n) {
            bodies.advance(0.01);
        }
        Console.OUT.printf("%.9f\n", bodies.energy());
        val end = System.nanoTime();
        Console.OUT.println("Program runtime: " + (end - start));
    }

    static struct vec(x:double, y:double, z:double) {
        public operator this + (that:vec) = vec(x+that.x,y+that.y,z+that.z);
        public operator this - (that:vec) = vec(x-that.x,y-that.y,z-that.z);
        public operator this * (d:double) = vec(x*d, y*d, z*d);
        public operator this / (d:double) = vec(x/d, y/d, z/d);
        public operator - this = vec(-x,-y,-z);
        public def sos() { return x*x + y*y + z*z; }
    }


    static class NBodySystem {
        private val bodies = [Body.sun(), Body.jupiter(), Body.saturn(), Body.uranus(), Body.neptune()];

        def this() { 
            var p:vec = vec(0.0,0.0,0.0);
            for (b in bodies) {
                p += bodies(b).v * bodies(b).mass;
            }
            bodies(0).offsetMomentum(p);
        }

        def advance(dt : double) {
            finish for ([i] in 0..(bodies.size-1)) async {
                val iBody = bodies(i);
                for ([j] in (i+1)..(bodies.size-1)) async {
                    val d = iBody.p - bodies(j).p;

                    val dSquared = d.sos();
                    val distance = Math.sqrt(dSquared);
                    val mag = dt / (dSquared * distance);

                    atomic(iBody) 
                    { //atomic section, update v
                        iBody.v -= d * bodies(j).mass * mag;
                        bodies(j).v += d * iBody.mass * mag;
                    }

                }
            }

            for (body in bodies) {
                bodies(body).p += bodies(body).v * dt;
            }
        }

        def energy() {
            var e : double = 0.0;

            for ([i] in 0..(bodies.size-1)) {
                val iBody = bodies(i);
                e += 0.5 * iBody.mass * iBody.v.sos();

                for ([j] in (i+1)..(bodies.size-1)) {
                    val jBody = bodies(j);
                    val d = iBody.p - jBody.p;
                    val distance = Math.sqrt(d.sos());
                    e -= (iBody.mass * jBody.mass) / distance;
                }
            }
            return e;
        }

    }



    static class Body {
        static val PI = 3.141592653589793;
        static val SOLAR_MASS = 4 * PI * PI;
        static val DAYS_PER_YEAR = 365.24;


        val mass : double;
        var p :vec = vec(0,0,0);
        var v: vec = vec(0,0,0);
        def this(mass:double) {
            this.mass = mass;
        }

        static def jupiter()  {
            val p = new Body(9.54791938424326609e-04 * SOLAR_MASS);
            p.p = vec(4.84143144246472090e+00,-1.16032004402742839e+00,-1.03622044471123109e-01);
            p.v = vec(1.66007664274403694e-03, 7.69901118419740425e-03, -6.90460016972063023e-05) * DAYS_PER_YEAR;
            return p;
        }

        static def saturn()  {
            val p = new Body(2.85885980666130812e-04 * SOLAR_MASS);
            p.p = vec(8.34336671824457987e+00,4.12479856412430479e+00,-4.03523417114321381e-01);
            p.v = vec(-2.76742510726862411e-03 , 4.99852801234917238e-03, 2.30417297573763929e-05) * DAYS_PER_YEAR;
			return p;
		}
		
		static def uranus()  {
			val p = new Body(4.36624404335156298e-05 * SOLAR_MASS);
			p.p = vec(1.28943695621391310e+01,-1.51111514016986312e+01,-2.23307578892655734e-01);
			p.v = vec( 2.96460137564761618e-03 ,  2.37847173959480950e-03, -2.96589568540237556e-05 ) * DAYS_PER_YEAR;
			return p;
		}
		
		static def neptune() {
			val p = new Body(5.15138902046611451e-05 * SOLAR_MASS);
			p.p = vec( 1.53796971148509165e+01,-2.59193146099879641e+01,1.79258772950371181e-01);
			p.v = vec( 2.68067772490389322e-03  ,  1.62824170038242295e-03, -9.51592254519715870e-05 ) * DAYS_PER_YEAR;
			return p;
		}
		
		static def sun()  {
			return new Body(SOLAR_MASS);
		}
		
		def offsetMomentum(p:vec)  {
		    v = -p / SOLAR_MASS;
			return this as nbody.Body; //edit here
		}
	}
}

