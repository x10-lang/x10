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
*            Florian Doyon (Florian.Doyon@sophia.inria.fr)                *
*              and  Wilfried Klauser (wklauser@acm.org)                   *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package raytracer;

//ok
public value class Sphere extends Primitive {
	final Vec      c;
	final double   r, r2;

	public Sphere(Vec center, double radius, Surface s) {
		super(s);
		c = center;
		r = radius;
		r2 = r*r;
	}

	public Sphere(Vec center, double radius) {
		this(center, radius, new Surface());
	}

	public nullable<Isect> intersect(Ray ry) {
		Vec v = Vec.sub(c, ry.p);
		double b = Vec.dot(v, ry.d);
		double disc = b*b - Vec.dot(v, v) + r2;
		if (disc < 0.0) {
			return null;
		}
		disc = Math.sqrt(disc);
		double t = (b - disc < 1e-6) ? b + disc : b - disc;
		if (t < 1e-6) {
			return null;
		}
		Isect ip = new Isect(t, Vec.dot(v, v) > r2 + 1e-6 ? 1 : 0, this, surf);
		return ip;
	}

	public Vec normal(Vec p) {
		return Vec.sub(p, c).normalized();
	}

	public String toString() {
		return "Sphere { " + c.toString() + "," + r + " }";
	}

	public Vec getCenter() {
		return c;
	}
}

