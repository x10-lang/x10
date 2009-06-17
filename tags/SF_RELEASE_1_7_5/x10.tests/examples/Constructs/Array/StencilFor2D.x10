/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Implementing a 5-point stencil operation using for loop
 * @author Tong 11/29/2006
 */

public class StencilFor2D extends x10Test {
    
    public def run(): boolean = {

        val R = [-1..256, -1..256] as Region;
        val r = [0..255, 0..255] as Region;
        val north = [0, 1] as Point;
        val south = [0, -1] as Point;
        val west = [-1, 0] as Point;
        val east  = [1, 0] as Point;
        val A = Array.make[double](R->here, (Point)=>0.0D);
        val h  = 0.1;
            
        for (val p: Point(2) in r) 
            A(p)=(A(p+north)+A(p+south)+A(p+west)+A(p+east)-4*A(p))*h;
            
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new StencilFor2D().execute();
    }

}
