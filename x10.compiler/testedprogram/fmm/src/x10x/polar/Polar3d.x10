/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright Josh Milthorpe 2010.
 */
package x10x.polar;

import x10x.vector.Point3d;
import x10x.vector.Tuple3d;

/** 
 *  This class represents a point in 3D Polar coordinates.
 * @author milthorpe
 */
public struct Polar3d {
    public val r: Double;
    public val theta: Double;
    public val phi: Double;

    public def this(r : Double, theta : Double, phi : Double) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }

    /* Returns a cartesian representation of this point. */
    public def toPoint3d() {
        sineTheta : Double = Math.sin(theta);
        return Point3d(r * sineTheta * Math.cos(phi), 
                       r * sineTheta * Math.sin(phi),
                       r * Math.cos(theta)
                      );
    }

    /** Returns a polar representation of the given cartesian tuple. */
    public static def getPolar3d(point : Tuple3d) {
        val rxy2 : Double = (point.i() * point.i()) + (point.j() * point.j());
        val r2 : Double = rxy2 + (point.k() * point.k());
        val r : Double = Math.sqrt(r2);
        var phi : Double;
        var theta : Double;
        if (rxy2 == 0.0) {
            if (point.k() >= 0.0) {
                theta = 0.0;
            } else {
                theta = Math.PI;
            }
            phi = 0.0;
        } else {
            val rxy : Double = Math.sqrt(rxy2);
            theta = Math.acos(point.k() / r);
            phi = Math.acos(point.i() / rxy);
            if (point.j() < 0.0) {
                phi = 2.0*Math.PI - phi;
            }
        }
        return Polar3d(r, theta, phi);
    }

    /**
     * Return this point rotated in three dimensions,
     * composed of a rotation around the Z-axis followed by
     * a rotation around the Y-axis.
     * @param alpha the angle in radians to rotate around the Z-axis
     * @param beta the angle in radians to rotate around the Y-axis
     */
    public def rotate(alpha : Double, beta : Double) {
        var newPhi : Double = phi + alpha;
        // assume 0 <= phi <= Math.PI * 2.0
        val tempTheta = (Math.PI/2.0 < phi && phi <  3.0*Math.PI/2.0) ? theta : -theta;
        var newTheta : Double = tempTheta + beta;
        if (newTheta > 2.0*Math.PI) {
            newTheta -= 2.0*Math.PI;
        }

        // constrain theta to 0..PI
        if (newTheta < 0.0) {
            newTheta = -newTheta;
            //newPhi += Math.PI;
        } else if (newTheta > Math.PI) {
            newTheta = 2.0*Math.PI - newTheta;
            //newPhi += Math.PI;
        }

        if (newPhi >= 2.0*Math.PI) {
            newPhi -= 2.0*Math.PI;
        } else if (newPhi < 0.0) {
            newPhi += 2.0*Math.PI;
        }
        return Polar3d(r, newTheta, newPhi);
    } 

    public def toString() : String {
        return ("(r:" + r + ",theta:" + theta + ",phi:" + phi + ")");
    }
}

