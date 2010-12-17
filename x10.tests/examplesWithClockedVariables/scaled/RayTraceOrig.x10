import x10.util.GrowableRail;

class Sphere extends Primitive {
	val c: Vec!;
	val r: double;
	val r2: double;

	public def this(var center: Vec, var radius: double, var s: Surface): Sphere = {
		super(s);
		c = center as Vec!;
		r = radius;
		r2 = r*r;
	}

	public def this(var center: Vec, var radius: double): Sphere = {
		this(center, radius, new Surface());
	}

	public def intersect(var ry: Ray!): Isect = {
		var v: Vec! = Vec.sub(c, ry.p) as Vec!;
		var b: double = Vec.dot(v, ry.d);
		var disc: double = b*b - Vec.dot(v, v) + r2;
		if (disc < 0.0) {
			return null;
		}
		disc = Math.sqrt(disc);
		var t: double = (b - disc < 1e-6) ? b + disc : b - disc;
		if (t < 1e-6) {
			return null;
		}
		var ip: Isect = new Isect(t, Vec.dot(v, v) > r2 + 1e-6 ? 1 : 0, this, surf);
		return ip;
	}

	public def normal(var p: Vec!): Vec! = {
		val v =  Vec.sub(p, c) as Vec!;
		return v.normalized();
	}

	public def myToString(): String = {
		return "Sphere { " + c.myToString() + "," + r + " }";
	}

	public def getCenter(): Vec = {
		return c;
	}
}



class Ray {
	public var p: Vec!;
	public var d: Vec!;

	public def this(var pnt: Vec, var dir: Vec): Ray = {
		this(pnt, dir, true);
	}

	public def this(var pnt: Vec, var dir: Vec, var normalize: boolean): Ray = {
		if (normalize) {
			val pntt = pnt as Vec!;
			val dirr = dir as Vec!;
			p = new Vec(pntt.x, pntt.y, pntt.z) as Vec!;
			val newVec = new Vec(dirr.x, dirr.y, dirr.z) as Vec!;
			d = newVec.normalized() as Vec!;
		} else {
			p = pnt as Vec!;
			d = dir as Vec!;
		}
	}

	public def this(): Ray = {
		p = new Vec();
		d = new Vec();
	}

	public def d(var d_: Vec): Ray! = {
		return new Ray(p, d_, false) as Ray!;
	}

	public def Point(var t: double): Vec = {
		return new Vec(p.x + d.x * t, p.y + d.y * t, p.z + d.z * t);
	}

	public def myToString(): String = {
		return "{ " + p.myToString() + "->" + d.myToString() + " }";
	}
}


class Surface {

	public val color: Vec;
	public val kd: double;
	public val ks: double;
	public val shine: double;
	public val kt: double;
	public val ior: double;

	public def this(): Surface = {
		color = new Vec(1, 0, 0);
		kd = 1.0;
		ks = 0.0;
		shine = 0.0;
		kt = 0.0;
		ior = 1.0;
	}

	public def this(var shine_: double, var ks_: double, var kt_: double, var c_: Vec): Surface = {
		kd = 1.0;
		ks = ks_;
		shine = shine_;
		kt = kt_;
		ior = 1.0;
		color = c_;
	}

	public def myToString(): String = {
		return "Surface { color = " + color + " }";
	}
}


abstract class Primitive {
	public var surf: Surface;

	public def this(): Primitive = {
		surf = new Surface();
	}
	public def this(var s: Surface): Primitive = {
		surf = (s == null) ? new Surface() : s;
	}

	abstract public def normal(var pnt: Vec!): Vec;
	abstract public def intersect(var ry: Ray!): Isect;
	abstract public def myToString(): String;
	abstract public def getCenter(): Vec;
	//public abstract void setCenter(Vec c);
}


class Isect {
	public var t: double;
	public var enter: int;
	public var prim: Primitive;
	public var surf: Surface;

	public def this(): Isect = {
		t = 0.0;
		enter = 0;
		prim = null;
		surf = null;
	}

	public def this(var t_: double, var enter_: int, var prim_: Primitive, var surf_: Surface): Isect = {
		t = t_;
		enter = enter_;
		prim = prim_;
		surf = surf_;
	}
}


class Vec {

	/**
	 * The x coordinate
	 */
	public val x: double;

	/**
	 * The y coordinate
	 */
	public val y: double;

	/**
	 * The z coordinate
	 */
	public val z: double;

	/**
	 * Constructor
	 * @param a the x coordinate
	 * @param b the y coordinate
	 * @param c the z coordinate
	 */
	public def this(var a: double, var b: double, var c: double): Vec = {
		x = a;
		y = b;
		z = c;
	}

	/**
	 * Copy constructor
	 */
	public def this(var a: Vec): Vec = {
		val aa = a as Vec!;
		x = aa.x;
		y = aa.y;
		z = aa.z;
	}

	/**
	 * Default (0,0,0) constructor
	 */
	public def this(): Vec = {
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}

	/**
	 * Add a vector to the current vector
	 * @param: a The vector to be added
	 */
	final public def added(val a: Vec): Vec! = {
		val aa = a as Vec!;
		return new Vec(x+aa.x, y+aa.y, z+aa.z) as Vec!;
	}

	/**
	 * adds: Returns a new vector such as
	 * new = sA + B
	 */
	public static def adds(var s: double, val a: Vec!, var b: Vec!): Vec! = {
		return new Vec(s * a.x + b.x, s * a.y + b.y, s * a.z + b.z) as Vec!;
	}

	/**
	 * Adds vector such as:
	 * this += sB
	 * @param: s The multiplier
	 * @param: b The vector to be added
	 */
	final public def adds(var s: double, var b: Vec!): Vec! = {
		return new Vec(x+s*b.x, y+s*b.y, z+s*b.z) as Vec!;
	}

	/**
	 * Substracs two vectors
	 */
	public static def sub(var a: Vec!, var b: Vec!): Vec! = {
		return new Vec(a.x - b.x, a.y - b.y, a.z - b.z) as Vec!;
	}

	public static def mult(var a: Vec!, var b: Vec!): Vec = {
		return new Vec(a.x * b.x, a.y * b.y, a.z * b.z);
	}

	public static def cross(var a: Vec!, var b: Vec!): Vec! = {
		return
			new Vec(a.y*b.z - a.z*b.y,
					a.z*b.x - a.x*b.z,
					a.x*b.y - a.y*b.x) as Vec!;
	}

	public static def dot(var a: Vec!, var b: Vec!): double = {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}

	public static def comb(var a: double, var A: Vec!, var b: double, var B: Vec!): Vec! = {
		return
			new Vec(a * A.x + b * B.x,
					a * A.y + b * B.y,
					a * A.z + b * B.z) as Vec!;
	}

	final public def scale(var t: double): Vec = {
		return new Vec(x*t, y*t, z*t);
	}

	final public def negate(): Vec! = {
		return new Vec(-x,-y,-z) as Vec!;
	}

	public def normalized(): Vec! = {
		var len: double;
		len = Math.sqrt(x*x + y*y + z*z);
		return ((len > 0.0) ? new Vec(x /len, y/len, z/len)  : this) as Vec!;
	}

	public def length(): double = {
		return Math.sqrt(x*x+y*y+z*z);
	}

	final public def myToString(): String = {
		return "<" + x + "," + y + "," + z + ">";
	}
}

class Interval {
	public val number: int;
	public val width: int;
	public val height: int;
	public val yfrom: int;
	public val yto: int;
	public val total: int;

	public def this(var number_: int, var width_: int, var height_: int, var yfrom_: int, var yto_: int, var total_: int): Interval = {
		number = number_;
		width = width_;
		height = height_;
		yfrom = yfrom_;
		yto = yto_;
		total = total_;
	}
}

class Light {
	public val pos: Vec;
	public val brightness: double;

	public def this(): Light = {
		pos = null;
		brightness = 0.0;
	}

	public def this(var x: double, var y: double, var z: double, var b: double): Light = {
		pos = new Vec(x, y, z);
		brightness = b;
	}
}


class View {
	public val from: Vec;
	public val at: Vec;
	public val up: Vec;
	public val dist: double;
	public val angle: double;
	public val aspect: double;

	public def this(var from_: Vec, var at_: Vec, var up_: Vec, var dist_: double, var angle_: double, var aspect_: double): View = {
		from = from_;
		at = at_;
		up = up_;
		dist = dist_;
		angle = angle_;
		aspect = aspect_;
	}
}


class Scene {
	public val lights: GrowableRail[Light]!;
	public val objects: GrowableRail[Primitive]!;
	private val view: View;

	public def this(var v: View): Scene = {
		lights = new GrowableRail[Light] ();
		objects = new GrowableRail[Primitive] ();
		view = v;
	}

	public def addLight(var l: Light): void = {
		this.lights.add(l);
	}

	public def addObject(var object: Primitive): void = {
		this.objects.add(object);
	}

	public def getView(): View = {
		return this.view;
	}

	public def getLight(var number: int): Light = {
		return (this.lights(number));
	}

	public def getObject(var number: int): Primitive = {
		return (this.objects(number));
	}

	public def getLights(): int = {
		return this.lights.length();
	}

	public def getObjects(): int = {
		return this.objects.length();
	}

	public def setObject(var object: Primitive, var pos: int): void = {
		this.objects(pos) = object;
	}
}

class RayTracer {

	var scene: Scene!;

	/**
	 * Lights for the rendering scene
	 */
	var lights: Rail[Light]!;

	/**
	 * Objects (spheres) for the rendering scene
	 */
	var prim: Rail[Primitive]!;

	/**
	 * The view for the rendering scene
	 */
	var view: View!;

	/**
	 * Alpha channel
	 */
	const alpha: int = 255<<24;

	/**
	 * Null vector (for speedup, instead of <code>new Vec(0,0,0)</code>
	 */
	const voidVec: Vec = new Vec();

	/**
	 * Height of the <code>Image</code> to be rendered
	 */
	var height: int;

	/**
	 * Width of the <code>Image</code> to be rendered
	 */
	var width: int;

	//int datasizes[] = { 150, 500 };
	val datasizes: ValRail[int] = [256, 128]; //reducing data size

	protected var checksum: long = 0;

	var size: int;

	var numobjects: int;

	/**
	 * Create and initialize the scene for the rendering picture.
	 * @return The scene just created
	 */
	def createScene(): Scene! = {
		val x: int = 0;
		val y: int = 0;

		var scene: Scene! = new Scene(new View(new Vec(x, 20, -30),
					new Vec(x, y, 0),
					new Vec(0, 1, 0),
					1.0,
					35.0 * 3.14159265 / 180.0,
					1.0));

		/* create spheres */
		val nx: int = 4;
		val ny: int = 4;
		val nz: int = 4;
		for (var i: int = 0; i<nx; i++) {
			for (var j: int = 0; j<ny; j++) {
				for (var k: int = 0; k<nz; k++) {
					var xx: double = 20.0 / (nx - 1) * i - 10.0;
					var yy: double = 20.0 / (ny - 1) * j - 10.0;
					var zz: double = 20.0 / (nz - 1) * k - 10.0;
					var p: Primitive = new Sphere(new Vec(xx,yy,zz), 3,
							new Surface(15.0, 1.5 - 1.0, 1.5 - 1.0,
								new Vec(0,0,(i+j)/ (nx+ny-2+0.0))));
					scene.addObject(p);
				}
			}
		}

		/* Creates five lights for the scene */
		scene.addLight(new Light(100, 100, -50, 1.0));
		scene.addLight(new Light(-100, 100, -50, 1.0));
		scene.addLight(new Light(100, -100, -50, 1.0));
		scene.addLight(new Light(-100, -100, -50, 1.0));
		scene.addLight(new Light(200, 200, 0, 1.0));

		return scene;
	}

	public def setScene(var scene: Scene!): void = {
		// Get the objects count
		var nLights: int = scene.getLights();
		var nObjects: int = scene.getObjects();
		lights = Rail.make[Light](nLights);
		prim = Rail.make[Primitive](nObjects);

		// Get the lights
		for (var l: int = 0; l < nLights; l++) {
			lights(l) = scene.getLight(l);
		}

		// Get the primitives
		for (var o: int = 0; o < nObjects; o++) {
				prim(o) = scene.getObject(o);
		}

		// Set the view
		view = scene.getView() as View!;
	}

	public def render(val interval: Interval!): void = {

		// Screen variables
		val row: Rail[int]! = Rail.make[int](interval.width * (interval.yto-interval.yfrom));
		var pixCounter: int = 0; //iterator

		// Rendering variables
		val frustrumwidth: double = view.dist * Math.tan(view.angle);
		val viewVec: Vec = Vec.sub(view.at as Vec!, view.from as Vec!).normalized();
		val tmpVec: Vec = new Vec(viewVec).scale(Vec.dot(view.up as Vec!, viewVec as Vec!));
		val upVec: Vec = Vec.sub(view.up as Vec!, tmpVec as Vec!).normalized().scale(-frustrumwidth);
		val leftVec: Vec = Vec.cross(view.up as Vec!, viewVec as Vec!).normalized().scale(view.aspect*frustrumwidth);

		// All loops are reversed for 'speedup' (cf. thinking in java p331)
		// For each line
		//for (y = interval.yfrom; y < interval.yto; y++) { // }
		finish for(val (y): Point in [interval.yfrom..interval.yto-1]) async {
			var r: Ray! = new Ray(view.from, voidVec) as Ray!;
			var ylen: double = (2.0 * y) / interval.width - 1.0;
			// For each pixel of the line
			for (val (x): Point in [0..interval.width-1]) {
				var xlen: double = (2.0 * x) / interval.width - 1.0;
				r = r.d(Vec.comb(xlen, leftVec as Vec!, ylen, upVec as Vec!).added(viewVec).normalized());
				var col: Vec! = trace(0, 1.0, r, new Isect(), new Ray()) as Vec!;

				// computes the color of the ray
				var red: int = (col.x * 255.0) as Int;
				if (red > 255) red = 255;
				var green: int = (col.y * 255.0) as Int;
				if (green > 255) green = 255;
				var blue: int =  (col.z * 255.0) as Int;
				if (blue > 255) blue = 255;

				atomic checksum += red + green + blue;

				// RGB values for .ppm file
				// Sets the pixels
				val index = y*interval.width+x;
				row(index) =  (alpha | (red << 16) | (green << 8) | (blue)) as Int;
			} // end for (x)
		} // end for (y)
	}

	def intersect(val r: Ray!, var maxt: double, var inter: Isect!): boolean = {
		var tp: Isect!;
		var i: int;
		var nhits: int;

		nhits = 0;
		inter.t = 1e9;
		for (i = 0; i < prim.length; i++) {
			// uses global temporary Prim (tp) as temp.object for speedup
			val pri: Primitive! = prim(i) as Primitive!;
			tp = pri.intersect(r) as Isect!;
			if (tp != null && tp.t < inter.t) {
				inter.t = tp.t;
				inter.prim = tp.prim;
				inter.surf = tp.surf;
				inter.enter = tp.enter;
				nhits++;
			}
		}
		return nhits > 0 ?  true :  false;
	}

	/**
	 * Checks if there is a shadow
	 * @param r The ray
	 * @return Returns 1 if there is a shadow, 0 if there isn't
	 */
	def Shadow(var r: Ray!, var tmax: double, var inter: Isect!): int = {
		if (this.intersect(r, tmax, inter))
			return 0;
		return 1;
	}

	/**
	 * Return the Vector's reflection direction
	 * @return The specular direction
	 */
	def SpecularDirection(var I: Vec!, var N: Vec!): Vec! = {
		return Vec.comb(1.0/Math.abs(Vec.dot(I, N)), I, 2.0, N).normalized();
	}

	/**
	 * Return the Vector's transmission direction
	 */
	def TransDir(var m1: Surface!, var m2: Surface!, var I: Vec!, var N: Vec!): Vec! = {
		var n1: double = m1 == null ? 1.0 : m1.ior;
		var n2: double = m2 == null ? 1.0 : m2.ior;
		var eta: double = n1/n2;
		var c1: double = -Vec.dot(I, N);
		var cs2: double = 1.0 - eta * eta * (1.0 - c1 * c1);
		if (cs2 < 0.0) return null;
		return Vec.comb(eta, I, eta * c1 - Math.sqrt(cs2), N).normalized() as Vec!;
	}

	/**
	 * Returns the shaded color
	 * @return The color in Vec form (rgb)
	 */
	def shade(var level: int, var weight: double, var P: Vec!, var N: Vec!, var I: Vec!, var hit: Isect!, val tRay: Ray!): Vec! = {
		var surf: Surface! = hit.surf as Surface!;
		var bigr: Vec! = new Vec() as Vec!;
		if (surf.shine > 1e-6) {
			bigr = SpecularDirection(I, N);
		}

		// Computes the effectof each light 
		var col: Vec! = new Vec() as Vec!;
		for (var l: int = 0; l < lights.length; l++) {
			val lightl = lights(l) as Light!;
			var L: Vec! = Vec.sub(lightl.pos as Vec!, P);
			if (Vec.dot(N, L) >= 0.0) {
				L = L.normalized();
				var t: double = L.length();

				tRay.p = P;
				tRay.d = L;

				// Checks if there is a shadow
				if (Shadow(tRay, t, hit) > 0) {
					var diff: double = Vec.dot(N, L) * surf.kd *
						lightl.brightness;

					col = col.adds(diff, surf.color as Vec!);
					if (surf.shine > 1e-6) {
						var spec: double = Vec.dot(bigr, L);
						if (spec > 1e-6) {
							spec = Math.pow(spec, surf.shine);
							col = col.added(new Vec(spec,spec,spec));
						}
					}
				}
			} // if
		} // for

		tRay.p = P;
		if (surf.ks * weight > 1e-3) {
			tRay.d = SpecularDirection(I, N);
			var tcol: Vec! = trace(level + 1, surf.ks * weight, tRay, hit, tRay);
			col = col.adds(surf.ks, tcol);
		}
		if (surf.kt * weight > 1e-3) {
			if (hit.enter > 0)
				tRay.d = (TransDir(null, surf, I, N));
			else
				tRay.d = (TransDir(surf, null, I, N));
			var tcol: Vec! = trace(level + 1, surf.kt * weight, tRay, hit, tRay);
			col = col.adds(surf.kt, tcol);
		}
		// garbaging...
		// tcol = null;
		// surf = null;

		return col;
	}

	/**
	 * Launches a ray
	 */
	def trace(var level: int, var weight: double, var r: Ray!, var inter: Isect!, var tRay: Ray!): Vec! = {
		// Checks the recursion level
		if (level > 6) {
			return new Vec() as Vec!;
		}

		var hit: boolean = this.intersect(r, 1e6, inter);
		if (hit) {
			var P: Vec = r.Point(inter.t);
			val prim = inter.prim as Primitive!;
			var N: Vec! = prim.normal(P as Vec!) as Vec!;
			if (Vec.dot(r.d, N) >= 0.0) {
				N = N.negate();
			}
			return shade(level, weight, P as Vec!, N as Vec!, r.d as Vec!, inter as Isect!, tRay as Ray!) as Vec!;
		}
		// no intersection --> col = 0,0,0
		return voidVec as Vec!;
	}
}

public class RayTraceOrig extends RayTracer {

	public def JGFsetsize(var size: int): void = {
		this.size = size;
	}

	public def JGFinitialise(): void = {
		//JGFInstrumentor.startTimer("Section3:RayTracerr:Init");

		// set image size
		//width = height = datasizes(size);
		width = datasizes(size);
		height = datasizes(size);

		// create the objects to be rendered
		scene = createScene();

		// get lights, objects etc. from scene.
		setScene(scene);

		numobjects = scene.getObjects();

		//JGFInstrumentor.stopTimer("Section3:RayTracerr:Init");
	}

	public def JGFapplication(): void = {
		//JGFInstrumentor.startTimer("Section3:RayTracerr:Run");

		// Set interval to be rendered to the whole picture
		// (overkill, but will be useful to retain this for parallel versions)
		var interval: Interval = new Interval(0, width, height, 0, height, 1);

		// Do the business!
		this.render(interval as Interval!);

		//JGFInstrumentor.stopTimer("Section3:RayTracerr:Run");
	}

	public def JGFvalidate(): void = {
		//long refval[] = { 2676692, 29827635 };
		val refval: ValRail[int] = [7790739, 29827635 ]; // reduced data size
		var dev: long = checksum - refval(size); //FIXME
		if (Math.abs(dev) > 50) {
			Console.OUT.println("Validation failed");
			Console.OUT.println("Pixel checksum = " + checksum);
			Console.OUT.println("Reference value = " + refval(size));
			throw new Error("Validation failed");
		}
	}

	public def JGFtidyup(): void = {
		/*
		scene = null;
		lights = null;
		prim = null;
		tRay = null;
		inter = null;
		*/
		//System.gc();
	}

	public def JGFrun(var size: int): void = {
		// JGFInstrumentor.addTimer("Section3:RayTracerr:Total", "Solutions", size);
		// JGFInstrumentor.addTimer("Section3:RayTracerr:Init", "Objects", size);
		// JGFInstrumentor.addTimer("Section3:RayTracerr:Run", "Pixels", size);

		JGFsetsize(size);

		// JGFInstrumentor.startTimer("Section3:RayTracerr:Total");

		JGFinitialise();
		JGFapplication();
		JGFvalidate();
		JGFtidyup();

		// JGFInstrumentor.stopTimer("Section3:RayTracerr:Total");

		// JGFInstrumentor.addOpsToTimer("Section3:RayTracerr:Init", (double) numobjects);
		// JGFInstrumentor.addOpsToTimer("Section3:RayTracerr:Run", (double) (width*height));
		// JGFInstrumentor.addOpsToTimer("Section3:RayTracerr:Total", 1);

		// JGFInstrumentor.printTimer("Section3:RayTracerr:Init");
		// JGFInstrumentor.printTimer("Section3:RayTracerr:Run");
		// JGFInstrumentor.printTimer("Section3:RayTracerr:Total");
	}
	
	  public static def main(args:Rail[String]!)= {
    			val start_time = System.currentTimeMillis(); 
			new RayTraceOrig().JGFrun(0);
    			val compute_time = (System.currentTimeMillis() - start_time);
    			Console.OUT.println( compute_time + " ");
		
	}
}

