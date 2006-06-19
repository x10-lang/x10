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
public value class Surface {

	final public Vec	color;
	final public double	kd;
	final public double	ks;
	final public double	shine;
	final public double	kt;
	final public double	ior;

	public Surface() {
		color = new Vec(1, 0, 0);
		kd = 1.0;
		ks = 0.0;
		shine = 0.0;
		kt = 0.0;
		ior = 1.0;
	}

	public Surface(double shine_, double ks_, double kt_, Vec c_) {
		kd = 1.0;
		ks = ks_;
		shine = shine_;
		kt = kt_;
		ior = 1.0;
		color = c_;
	}

	public String toString() {
		return "Surface { color = " + color + " }";
	}
}

