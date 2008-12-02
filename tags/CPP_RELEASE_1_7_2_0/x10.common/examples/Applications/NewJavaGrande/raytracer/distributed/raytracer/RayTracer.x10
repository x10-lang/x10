/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                 Original version of this code by                        *
*            Florian Doyon (Florian.doyon@sophia.inria.fr)                *
*              and  Wilfried Klauser (wklauser@acm.org)                   *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package raytracer;

public class RayTracer {

	Scene scene;

	/**
	 * Lights for the rendering scene
	 */
	Light lights[];

	/**
	 * Objects (spheres) for the rendering scene
	 */
	Primitive prim[];

	/**
	 * The view for the rendering scene
	 */
	View view;

	/**
	 * Alpha channel
	 */
	static final int alpha = 255<<24;

	/**
	 * Null vector (for speedup, instead of <code>new Vec(0,0,0)</code>
	 */
	static final Vec voidVec = new Vec();

	/**
	 * Height of the <code>Image</code> to be rendered
	 */
	int height;

	/**
	 * Width of the <code>Image</code> to be rendered
	 */
	int width;

	//int datasizes[] = { 150, 500 };
	int datasizes[] = { 20, 500 }; //reducing data size

	long checksum = 0;

	int size;

	int numobjects;

	/**
	 * Create and initialize the scene for the rendering picture.
	 * @return The scene just created
	 */
	Scene createScene() {
		final int x = 0;
		final int y = 0;

		Scene scene = new Scene(new View(new Vec(x, 20, -30),
					new Vec(x, y, 0),
					new Vec(0, 1, 0),
					1.0,
					35.0 * 3.14159265 / 180.0,
					1.0));

		/* create spheres */
		final int nx = 4;
		final int ny = 4;
		final int nz = 4;
		for (int i = 0; i<nx; i++) {
			for (int j = 0; j<ny; j++) {
				for (int k = 0; k<nz; k++) {
					double xx = 20.0 / (nx - 1) * i - 10.0;
					double yy = 20.0 / (ny - 1) * j - 10.0;
					double zz = 20.0 / (nz - 1) * k - 10.0;
					Primitive p = new Sphere(new Vec(xx,yy,zz), 3,
							new Surface(15.0, 1.5 - 1.0, 1.5 - 1.0,
								new Vec(0,0,(i+j)/(double) (nx+ny-2))));
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

	public void setScene(Scene scene)
	{
		// Get the objects count
		int nLights = scene.getLights();
		int nObjects = scene.getObjects();

		lights = new Light[nLights];
		prim = new Primitive[nObjects];

		// Get the lights
		for (int l = 0; l < nLights; l++) {
			lights[l] = scene.getLight(l);
		}

		// Get the primitives
		for (int o = 0; o < nObjects; o++) {
			prim[o] = scene.getObject(o);
		}

		// Set the view
		view = scene.getView();
	}

	public void render(final Interval interval)
	{

		//final int ub = (interval.width * (interval.yto-interval.yfrom))-1;
		final region R = [0:(interval.width * (interval.yto-interval.yfrom))-1];
		final dist DBlock = dist.factory.block(R);
		final dist U = dist.factory.unique();
		final int[.] row = new int[DBlock];

		finish ateach (point[pl] : U) {
			final dist my_dist = DBlock | here;
			long checksum1 = 0;
			double frustrumwidth = view.dist * Math.tan(view.angle);
			Vec viewVec = Vec.sub(view.at, view.from).normalized();
			Vec tmpVec = new Vec(viewVec).scale(Vec.dot(view.up, viewVec));
			Vec upVec = Vec.sub(view.up, tmpVec).normalized().scale(-frustrumwidth);
			Vec leftVec = Vec.cross(view.up, viewVec).normalized().scale(view.aspect * frustrumwidth);

			Ray r = new Ray(view.from, voidVec);

			for (point[pixCounter] : my_dist.region) {
				int y = pixCounter / interval.width;
				int x = pixCounter % interval.width;
				double ylen = (double)(2.0 * y) / (double)interval.width - 1.0;
				double xlen = (double)(2.0 * x) / (double)interval.width - 1.0;
				r = r.d (Vec.comb(xlen, leftVec, ylen, upVec).added(viewVec).normalized());
				Vec col = trace(0, 1.0, r, new Isect(), new Ray());

				// computes the color of the ray
				int red = (int)(col.x * 255.0);
				if (red > 255) red = 255;
				int green = (int)(col.y * 255.0);
				if (green > 255) green = 255;
				int blue = (int)(col.z * 255.0);
				if (blue > 255) blue = 255;

				checksum1 += red + green + blue;
				// RGB values for .ppm file
				// Sets the pixels
				//row[pixCounter/*++*/] =  alpha | (red << 16) | (green << 8) | (blue);
			}
			final long checksumx = checksum1;
			finish async(place.FIRST_PLACE) {
				atomic { checksum += checksumx; }
			}
		}
	}

	boolean intersect(Ray r, double maxt, Isect inter) {
		nullable<Isect> tp;
		int i, nhits;

		nhits = 0;
		inter.t = 1e9;
		for (i = 0; i < prim.length; i++) {
			// uses global temporary Prim (tp) as temp.object for speedup
			tp = prim[i].intersect(r);
			if (tp != null && tp.t < inter.t) {
				inter.t = tp.t;
				inter.prim = tp.prim;
				inter.surf = tp.surf;
				inter.enter = tp.enter;
				nhits++;
			}
		}
		return nhits > 0 ? (boolean) true : (boolean) false;
	}

	/**
	 * Checks if there is a shadow
	 * @param r The ray
	 * @return Returns 1 if there is a shadow, 0 if there isn't
	 */
	int Shadow(Ray r, double tmax, Isect inter) {
		if (intersect(r, tmax, inter))
			return 0;
		return 1;
	}

	/**
	 * Return the Vector's reflection direction
	 * @return The specular direction
	 */
	Vec SpecularDirection(Vec I, Vec N) {
		return Vec.comb(1.0/Math.abs(Vec.dot(I, N)), I, 2.0, N).normalized();
	}

	/**
	 * Return the Vector's transmission direction
	 */
	nullable<Vec> TransDir(nullable<Surface> m1, nullable<Surface> m2, Vec I, Vec N) {
		double n1 = m1 == null ? 1.0 : m1.ior;
		double n2 = m2 == null ? 1.0 : m2.ior;
		double eta = n1/n2;
		double c1 = -Vec.dot(I, N);
		double cs2 = 1.0 - eta * eta * (1.0 - c1 * c1);
		if (cs2 < 0.0) return null;
		return Vec.comb(eta, I, eta * c1 - Math.sqrt(cs2), N).normalized();
	}

	/**
	 * Returns the shaded color
	 * @return The color in Vec form (rgb)
	 */
	Vec shade(int level, double weight, Vec P, Vec N, Vec I, Isect hit, Ray tRay) {

		nullable<Surface> surf = hit.surf;
		Vec bigr = new Vec();
		if (surf.shine > 1e-6) {
			bigr = SpecularDirection(I, N);
		}

		// Computes the effectof each light
		Vec col = new Vec();
		for (int l = 0; l < lights.length; l++) {
			Vec L = Vec.sub(lights[l].pos, P);
			if (Vec.dot(N, L) >= 0.0) {
				L = L.normalized();
				double t = L.length();

				tRay.p = P;
				tRay.d = L;

				// Checks if there is a shadow
				if (Shadow(tRay, t, hit) > 0) {
					double diff = Vec.dot(N, L) * surf.kd *
						lights[l].brightness;

					col = col.adds(diff, surf.color);
					if (surf.shine > 1e-6) {
						double spec = Vec.dot(bigr, L);
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
			Vec tcol = trace(level + 1, surf.ks * weight, tRay, hit, tRay);
			col = col.adds(surf.ks, tcol);
		}
		if (surf.kt * weight > 1e-3) {
			if (hit.enter > 0)
				tRay.d = (Vec) TransDir(null, surf, I, N);
			else
				tRay.d = (Vec) TransDir(surf, null, I, N);
			Vec tcol = trace(level + 1, surf.kt * weight, tRay, hit, tRay);
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
	Vec trace(int level, double weight, Ray r, Isect inter, Ray tRay) {
		// Checks the recursion level
		if (level > 6) {
			return new Vec();
		}

		boolean hit = intersect(r, 1e6, inter);
		if (hit) {
			Vec P = r.point(inter.t);
			Vec N = inter.prim.normal(P);
			if (Vec.dot(r.d, N) >= 0.0) {
				N = N.negate();
			}
			return shade(level, weight, P, N, r.d, inter, tRay);
		}
		// no intersection --> col = 0,0,0
		return voidVec;
	}
}

