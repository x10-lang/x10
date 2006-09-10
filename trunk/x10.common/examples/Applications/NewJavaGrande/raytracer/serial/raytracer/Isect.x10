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

public class Isect {
	public double	t;
	public int		enter;
	public nullable<Primitive>	prim;
	public nullable<Surface>		surf;

	public Isect() {
		t = 0.0;
		enter = 0;
		prim = null;
		surf = null;
	}

	public Isect(double t_, int enter_, nullable<Primitive> prim_, nullable<Surface> surf_) {
		t = t_;
		enter = enter_;
		prim = prim_;
		surf = surf_;
	}
}

