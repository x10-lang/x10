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
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package raytracer;

import jgfutil.*;

public class JGFRayTracerBench extends RayTracer implements JGFSection3 {

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
		JGFInstrumentor.startTimer("Section3:RayTracer:Init");

		// set image size
		width = height = datasizes[size];

		// create the objects to be rendered
		scene = createScene();

		// get lights, objects etc. from scene.
		setScene(scene);

		numobjects = scene.getObjects();

		JGFInstrumentor.stopTimer("Section3:RayTracer:Init");
	}

	public void JGFapplication() {
		JGFInstrumentor.startTimer("Section3:RayTracer:Run");

		// Set interval to be rendered to the whole picture
		// (overkill, but will be useful to retain this for parallel versions)
		Interval interval = new Interval(0, width, height, 0, height, 1);

		// Do the business!
		render(interval);

		JGFInstrumentor.stopTimer("Section3:RayTracer:Run");
	}

	public void JGFvalidate() {
		//long refval[] = { 2676692, 29827635 };
		long refval[] = { 51398, 29827635 }; // reduced data size
		long dev = checksum - refval[size];
		if (dev != 0) {
			System.out.println("Validation failed");
			System.out.println("Pixel checksum = " + checksum);
			System.out.println("Reference value = " + refval[size]);
			throw new Error("Validation failed");
		}
	}

	public void JGFtidyup() {
		/*
		scene = null;
		lights = null;
		prim = null;
		tRay = null;
		inter = null;
		*/
		System.gc();
	}

	public void JGFrun(int size) {
		JGFInstrumentor.addTimer("Section3:RayTracer:Total", "Solutions", size);
		JGFInstrumentor.addTimer("Section3:RayTracer:Init", "Objects", size);
		JGFInstrumentor.addTimer("Section3:RayTracer:Run", "Pixels", size);

		JGFsetsize(size);

		JGFInstrumentor.startTimer("Section3:RayTracer:Total");

		JGFinitialise();
		JGFapplication();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.stopTimer("Section3:RayTracer:Total");

		JGFInstrumentor.addOpsToTimer("Section3:RayTracer:Init", (double) numobjects);
		JGFInstrumentor.addOpsToTimer("Section3:RayTracer:Run", (double) (width*height));
		JGFInstrumentor.addOpsToTimer("Section3:RayTracer:Total", 1);

		JGFInstrumentor.printTimer("Section3:RayTracer:Init");
		JGFInstrumentor.printTimer("Section3:RayTracer:Run");
		JGFInstrumentor.printTimer("Section3:RayTracer:Total");
	}
}

