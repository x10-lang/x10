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
public abstract value class Primitive {
	public Surface	surf;

	public Primitive() {
		surf = new Surface();
	}
	public Primitive(nullable<Surface> s) {
		surf = ((s == null) ? new Surface() : (Surface) s);
	}

	public abstract Vec normal(Vec pnt);
	public abstract nullable<Isect> intersect(Ray ry);
	public abstract String toString();
	public abstract Vec getCenter();
	//public abstract void setCenter(Vec c);
}

