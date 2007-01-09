/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package jgfutil;
/**
 * Helper class to avoid static non-final fields
 */
public class BoxedDouble {
  public double val;
  public BoxedDouble(double x) {
    val=x;
  }
}
