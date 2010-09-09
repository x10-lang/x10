package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Application;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;

/**
 * 
 * Factory method for sample model:
 * 
 * A[P1]
 * 		{topFn: a1v1=a1v1Val, a1v2=a1v2Val}
 * 	A1[C1,P1]
 *  	A11[P2]
 *		A12[P3]
 * 	A2[C1,P1]
 *		A21[P4]
 *		A22[P5]
 *		A23[P6]
 * 	A3[P1]
 *		A31[C1,C2,P1]
 *			A311[P1]
 *			A312[P2]
 *			A313[P3]
 *		A32[C1,C2,P1]	
 *		A33[C1,C2,P1]
 *
 * @author mmk
 * @since 10/12/08
 */
public class SampleX10ModelFactory {
	public static SampleX10Application _application;
	
	public static IX10Application getApplication() {
		if (_application==null) {
			IX10Activity finishRoot = createActivities();
			_application = new SampleX10Application(finishRoot);
		}
		return _application;
	}
	
	private static IX10Activity createActivities() {
		IX10Place p1 = new SampleX10Place("P1");
		IX10Place p2 = new SampleX10Place("P2");
		IX10Place p3 = new SampleX10Place("P3");
		IX10Place p4 = new SampleX10Place("P4");
		IX10Place p5 = new SampleX10Place("P5");
		IX10Place p6 = new SampleX10Place("P6");
		IX10Clock c1 = new SampleX10Clock("C1");
		IX10Clock c2 = new SampleX10Clock("C2");
		IX10Activity a = new SampleX10Activity("A", null, p1);
		
		SampleX10Activity a1 = new SampleX10Activity("A1", a, p1);
		a1.addClock(c1);
		SampleX10StackFrame a1sf = new SampleX10StackFrame("topFn", null);
		IX10Variable v1 = new SampleX10Variable("a1v1", new SampleX10Value(a1sf,"a1v1Val"));
		IX10Variable v2 = new SampleX10Variable("a1v2", new SampleX10Value(a1sf, "a1v2Val"));
		a1sf.addVariable(v1);
		a1sf.addVariable(v2);
		a1.pushStackFrame(a1sf);
		SampleX10Activity a11 = new SampleX10Activity("A11", a1, p2);
		SampleX10Activity a12 = new SampleX10Activity("A12", a1, p3);
		
		SampleX10Activity a2 = new SampleX10Activity("A2", a, p1);
		a2.addClock(c1);
		SampleX10Activity a21 = new SampleX10Activity("A21", a2, p4);
		SampleX10Activity a22 = new SampleX10Activity("A22", a2, p4);
		SampleX10Activity a23 = new SampleX10Activity("A23", a2, p6);
		
		SampleX10Activity a3 = new SampleX10Activity("A3", a, p1);
		SampleX10Activity a31 = new SampleX10Activity("A31", a3, p1);
		a31.addClock(c1);
		a31.addClock(c2);
		SampleX10Activity a311 = new SampleX10Activity("A311", a31, p1);
		SampleX10Activity a312 = new SampleX10Activity("A312", a31, p2);
		SampleX10Activity a313 = new SampleX10Activity("A313", a31, p3);
		//
		SampleX10Activity a32 = new SampleX10Activity("A32", a3, p1);
		a32.addClock(c1);
		a32.addClock(c2);
		//
		SampleX10Activity a33 = new SampleX10Activity("A33", a3, p1);
		a33.addClock(c1);
		a33.addClock(c2);
		
		return a;
	}
	
}
