/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 *
 * @author igor
 */
public class RailAlias extends x10Test {
     public RailAlias() { }
     public boolean run() {
          region(:rail) r = [0:10];
          double[:rect&&zeroBased&&rank==1] a = new double[r];
          double d = a[1];
          for (point [p] : a) a[p] = 1.0; 
          return true;
     }
     public static void main(String[] a) {
    	 new RailAlias().run();
     }
}