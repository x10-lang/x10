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
public value class Interval {
	final public int number;
	final public int width;
	final public int height;
	final public int yfrom;
	final public int yto;
	final public int total;

	public Interval(int number_, int width_, int height_, int yfrom_, int yto_, int total_)
	{
		number = number_;
		width = width_;
		height = height_;
		yfrom = yfrom_;
		yto = yto_;
		total = total_;
	}
}

