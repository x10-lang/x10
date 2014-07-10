/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.matrix.util;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;

public class DistPConsole {
	public val pconsole:DistArray[PConsole](1);

	public def this() {
		val dist = Dist.makeUnique();
		pconsole = DistArray.make[PConsole](dist, ([i]:Point)=>(new PConsole()));
	}

    public def print(st:String)        { pconsole(here.id()).print(st); }
    public def print(d:Rail[Int])      { pconsole(here.id()).print(d); }
    public def print(d:Rail[Double])   { pconsole(here.id()).print(d); }
    public def println(st:String)      { pconsole(here.id()).println(st); }
    public def println(d:Rail[Int])    { pconsole(here.id()).println(d);}
    public def println(d:Rail[Double]) { pconsole(here.id()).println(d);}
	//public def println(A:Matrix)           = pconsole(here.id()).println(A);

    public def flush()             { pconsole(here.id()).flush();}
	public def flush(st:String)    { pconsole(here.id()).flush(st); }
	public def flushln(st:String)  { pconsole(here.id()).flushln(st); }

	public def getAllStream():String {
		var ostream:String = "";
		for (var p:Long=0; p<Place.numPlaces(); p++) {
			val pid = p;
			val pp:Point(pconsole.dist.region.rank) = Point.make(p);
			ostream += at(pconsole.dist(pp)) pconsole(pid).getOutStream(true);
		}
		return ostream;
	}

	public def flushall() {
		val ost = getAllStream();
		Console.OUT.print(ost);
		Console.OUT.flush();
	}

	public def fflush() {
		for (var p:Long=0; p<Place.numPlaces(); p++) {
			val pid = p;
			val pp:Point(pconsole.dist.region.rank) = Point.make(p);
			at(pconsole.dist(pp)) {
				pconsole(pid).fflush();
			}
		}
	} 
}
