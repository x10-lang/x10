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
public value class View {
	final public Vec	from;
	final public Vec	at;
	final public Vec	up;
	final public double	dist;
	final public double	angle;
	final public double	aspect;

	public View (Vec from_, Vec at_, Vec up_, double dist_, double angle_, double aspect_)
	{
		from = from_;
		at = at_;
		up = up_;
		dist = dist_;
		angle = angle_;
		aspect = aspect_;
	}
}

