/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.array;

import java.util.Iterator;

import x10.core.fun.Fun_0_1;
import x10.runtime.Place;
import x10.types.Type;

public class ArrayFactory {
    public static <T> Array<T> makeVarArray(Type type, Dist d) {
        Array<T> a = new GenericArray<T>(type, d);
        Place here = x10.runtime.Runtime.runtime.currentPlace();
        try {
        	for (Point p : d) {
        		x10.runtime.Runtime.runtime.setCurrentPlace(d.get(p));
        		a.set$(((Type<T>)type).zeroValue(), p);
        	}
        } finally {
                x10.runtime.Runtime.runtime.setCurrentPlace(here);
        }

        return a;
    }

    public static <T> Array<T> makeVarArray(Type type, Dist d, Fun_0_1<Point, T> init) {
        Array<T> a = new GenericArray<T>(type, d);
        Place here = x10.runtime.Runtime.runtime.currentPlace();
        try {
			for (Point p : d) {
				x10.runtime.Runtime.runtime.setCurrentPlace(d.get(p));
				a.set$(init.apply$(p), p);
			}
		} finally {
			x10.runtime.Runtime.runtime.setCurrentPlace(here);
		}
        return a;
    }

    public static <T> ValArray<T> makeValArray(Type type, Dist d) {
        ValArray<T> a = new GenericValArray<T>(type, d);
        Place here = x10.runtime.Runtime.runtime.currentPlace();
        try {
        	for (Point p : d) {
        		x10.runtime.Runtime.runtime.setCurrentPlace(d.get(p));
        		a.set$(((Type<T>)type).zeroValue(), p);
        	}
        } finally {
        	x10.runtime.Runtime.runtime.setCurrentPlace(here);
        }
        return a;
    }

    public static <T> ValArray<T> makeValArray(Type type, Dist d, Fun_0_1<Point, T> init) {
        ValArray<T> a = new GenericValArray<T>(type, d);
        Place here = x10.runtime.Runtime.runtime.currentPlace();
        try {
        	for (Point p : d) {
        		x10.runtime.Runtime.runtime.setCurrentPlace(d.get(p));
        		a.set$(init.apply$(p), p);
        	}
        } finally {
        	x10.runtime.Runtime.runtime.setCurrentPlace(here);
        }
        return a;
    }

    public static <T> ValArray<T> makeValArray(Type< T> type, Region r) {
        return makeValArray(type, r.toDistribution());
    }

    public static <T> ValArray<T> makeValArray(Type< T> type, Region r, Fun_0_1<Point, T> init) {
        return makeValArray(type, r.toDistribution(), init);
    }

    public static <T> Array<T> makeVarArray(Type<  T> type, Region r) {
        return makeVarArray(type, r.toDistribution());
    }

    public static <T> Array<T> makeVarArray(Type< T> type, Region r, Fun_0_1<Point, T> init) {
        return makeVarArray(type, r.toDistribution(), init);
    }
}
