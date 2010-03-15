/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.x10rt.ActiveMessage;
import x10.x10rt.MessageRegistry;
import x10.x10rt.Place;
import x10.x10rt.X10RT;
import x10.x10rt.UnknownMessageException;

public class SimpleMessageTest {

  /**
   * @param args
   * @throws UnknownMessageException
   */
  public static void main(String[] args) throws UnknownMessageException {
    System.out.println(X10RT.here()+" is booted");

    ActiveMessage add = MessageRegistry.register(SimpleMessageTest.class, "addNumbers", Integer.TYPE, Integer.TYPE);
    ActiveMessage addMany = MessageRegistry.register(SimpleMessageTest.class, "addNumbers", new int[0].getClass());
    ActiveMessage addManyD = MessageRegistry.register(SimpleMessageTest.class, "addNumbers", new double[0].getClass());
    ActiveMessage addManyS = MessageRegistry.register(SimpleMessageTest.class, "addNumbers", new short[0].getClass());
    ActiveMessage ping = MessageRegistry.register(SimpleMessageTest.class, "ping");
    ActiveMessage pong = MessageRegistry.register(SimpleMessageTest.class, "pong", Integer.TYPE);
    ActiveMessage addScaled = MessageRegistry.register(SimpleMessageTest.class, "addScaledNumbers", new int[0].getClass(), Integer.TYPE);
    ActiveMessage max = MessageRegistry.register(SimpleMessageTest.class, "max", Short.TYPE, Double.TYPE);
    ActiveMessage smallStuff = MessageRegistry.register(SimpleMessageTest.class, "smallInts", Short.TYPE, Float.TYPE);

    X10RT.barrier();

    if (X10RT.here() == X10RT.getPlace(0)) {
      for (int i=0; i<X10RT.numPlaces(); i++) {
        Place where = X10RT.getPlace(i);
        ping.send(where);
        add.send(where, 10+i, 20+i);
        addScaled.send(where, new int[] { 1, 2, 3, 4, 5 }, 10);
        addMany.send(where, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, i*100});
        addManyD.send(where, new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, i*100});
        addManyS.send(where, new short[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, (short)(i*100)});
        max.send(where, (short)100, (double)200);
        smallStuff.send(where, (short)10, (float)20);
     }
      X10RT.fence();
    }

    X10RT.barrier();
    System.out.println(X10RT.here()+ ": returning from main[]");
  }


  public static void addNumbers(int a, int b) {
    System.out.printf(X10RT.here()+ ": adding %d and %d to get %d\n", a, b, a+b);
  }

  public static void addNumbers(short [] numbers) {
    short sum = 0;
    for (short elem : numbers) {
      sum += elem;
    }
    System.out.println(X10RT.here()+ ": the sum of "+numbers.length+" shorts is "+sum);
  }
  public static void addNumbers(int [] numbers) {
    int sum = 0;
    for (int elem : numbers) {
      sum += elem;
    }
    System.out.println(X10RT.here()+ ": the sum of "+numbers.length+" ints is "+sum);
  }

  public static void addNumbers(double [] numbers) {
    double sum = 0;
    for (double elem : numbers) {
      sum += elem;
    }
    System.out.println(X10RT.here()+ ": the sum of "+numbers.length+" doubles is "+sum);
  }

  public static void ping() throws UnknownMessageException, IllegalArgumentException {
    System.out.println(X10RT.here()+ ": PING");
    MessageRegistry.lookup(SimpleMessageTest.class, "pong", Integer.TYPE).send(X10RT.getPlace(0), X10RT.here().getId());
  }

  public static void pong(int where) {
    System.out.println(X10RT.here()+ ": was PONGed by "+where);
  }

  public static void addScaledNumbers(int[] data, int scale) {
    int sum = 0;
    for (int d : data) {
      sum += scale*d;
    }
    System.out.println("The scaled sum is "+sum);
  }

  public static void max(short a, double b) {
    double t1 = Math.max(a, b);
    System.out.println("The max of "+a+" and "+b+" is "+t1);
  }

  public static void smallInts(short a, float b) {
   System.out.printf("Got some small stuff %d %f\n", a, b);
  }
}
