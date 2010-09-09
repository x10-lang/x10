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
import java.util.Vector;

public value class Scene {
	final public Vector lights;
	final public Vector objects;
	final private View view;

	public Scene (View v)
	{
		lights = new Vector ();
		objects = new Vector ();
		view = v;
	}

	public void addLight(Light l)
	{
		this.lights.addElement(l);
	}

	public void addObject(Primitive object)
	{
		this.objects.addElement(object);
	}

	public View getView()
	{
		return this.view;
	}

	public Light getLight(int number)
	{
		return (Light) this.lights.elementAt(number);
	}

	public Primitive getObject(int number)
	{
		return (Primitive) objects.elementAt(number);
	}

	public int getLights()
	{
		return this.lights.size();
	}

	public int getObjects()
	{
		return this.objects.size();
	}

	public void setObject(Primitive object, int pos)
	{
		this.objects.setElementAt(object, pos);
	}
}

