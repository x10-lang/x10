package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * A representation of a linear equation.
 * <p>
 * $Id: Equation.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
 * <p>
 * Copyright 2005 by the <a href="http://spa-www.cs.umass.edu/">Scale Compiler Group</a>,<br>
 * <a href="http://www.cs.umass.edu/">Department of Computer Science</a><br>
 * <a href="http://www.umass.edu/">University of Massachusetts</a>,<br>
 * Amherst MA. 01003, USA<br>
 * All Rights Reserved.<br>
 * <p>
 * This version of the Omega Libray is a translation from C++ to Java
 * of the Omega Library developed at the University of Maryland.
 * <blockquote cite="http://www.cs.umd.edu/projects/omega">
 * Copyright (C) 1994-1996 by the Omega Project
 * <p>
 * All rights reserved.
 * <p>
 * NOTICE:  This software is provided ``as is'', without any
 * warranty, including any implied warranty for merchantability or
 * fitness for a particular purpose.  Under no circumstances shall
 * the Omega Project or its agents be liable for any use of, misuse
 * of, or inability to use this software, including incidental and
 * consequential damages.
 * <p>
 * License is hereby given to use, modify, and redistribute this
 * software, in whole or in part, for any purpose, commercial or
 * non-commercial, provided that the user agrees to the terms of this
 * copyright notice, including disclaimer of warranty, and provided
 * that this copyright notice, including disclaimer of warranty, is
 * preserved in the source code and documentation of anything derived
 * from this software.  Any redistributor of this software or
 * anything derived from this software assumes responsibility for
 * ensuring that any parties to whom such a redistribution is made
 * are fully aware of the terms of this license and disclaimer.
 * <p>
 * The Omega project can be contacted at
 * <a href="mailto:omega@cs.umd.edu">omega@cs.umd.edu</a>
 * or <a href="http://www.cs.umd.edu/projects/omega">http://www.cs.umd.edu/projects/omega</a>
 * </blockquote>
 * An equation represents a linear equation of the form
 * <br>
 *   <tt>C + a<sub>1</sub>V<sub>1</sub> + ... + a<sub>n</sub>V<sub>n</sub></tt>
 * <br>
 * An Equation has three other pices of information associated with it:
 * <dl>
 * <dt>color<dd>the color - BLACK or red
 * <dt>key<dd>an integer value used in various algorithms
 * <dt>varCount<dd>????
 * </dl>
 * An equation has two boolean flags associated with it:
 * <dl>
 * <dt>touched<dd>???
 * <dt>essential<dd>???
 * </dl>
 */
public final class Equation
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  private static final int keyMult = 31;

  public int v1;
  public int v2;

  private int     key;
  private int     color;
  private int     varCount;
  private int[]     coef;
  private boolean touched;
  private boolean essential;

  /**
   * Create a new Equation.
   * @param key is the key value
   * @param color is the color of the Equation
   * @param nVars is the number of variables
   * @param constant is the value of the constant term coefficient
   */
  public Equation(int key, int color, int nVars, int constant)
  {
    if (nVars < 1)
      nVars = 1;

    this.key       = key;
    this.touched   = false;
    this.color     = color;
    this.varCount  = 0;
    this.essential = false;
    this.coef      = new int[nVars + 1];
    this.coef[0]   = constant;
    createdCount++;
  }

  /**
   * Create a new Equation that is a duplicate of this Equation.
   */
  public Equation copy()
  {
    Equation n = new Equation(key, color, coef.length - 1, 0);
    n.touched = touched;
    n.essential = essential;
    System.arraycopy(coef, 0, n.coef, 0, coef.length);
    return n;
  }

  /**
   * Reset the Equation for resuse.
   * @param nVars is the number of variables in the equation.
   */
  public void reset(int color, int nVars, int constant)
  {
    this.key       = 0;
    this.touched   = false;
    this.color     = color;
    this.essential = false;

    if (nVars >= coef.length)
      this.coef = new int[nVars + 1];
    else
      for (int i = 1; i <= nVars; i++)
        this.coef[i] = 0;

    this.coef[0] = constant;
  }

  /**
   * Set the color, key, and touched values.
   */
  public void set(int color, int key, boolean touched)
  {
    this.color   = color;
    this.key     = key;
    this.touched = touched;
  }

  /**
   * Make this Equation the duplicate of the specified Equation.
   */
  public void eqncpy(Equation eq)
  {
    this.key       = eq.key;
    this.color     = eq.color;
    this.touched   = eq.touched;
    this.essential = eq.essential;
    this.varCount  = eq.varCount;

    if (this.coef.length == eq.coef.length)
      System.arraycopy(eq.coef, 0, this.coef, 0, this.coef.length);
    else
      this.coef = (int[]) eq.coef.clone();
  }

  /**
   * Return a String representation of this Equation.
   */
  public String toString()
  {
    StringBuffer buf = new StringBuffer("(Equation ");
    buf.append(key);
    buf.append(color == Problem.BLACK ? " BLACK " : " red ");
    buf.append(touched);
    buf.append(' ');
    buf.append(essential);
    buf.append(' ');

    for (int i = 0; i < coef.length; i++) {
      buf.append(i);
      buf.append('-');
      buf.append(coef[i]);
      buf.append(' ');
    }
    buf.append(')');
    return buf.toString();
  }

  /**
   * Return a String representing the coefficients.
   * @param last is the index of the last coefficient copied
   * @param first is the index of the first coefficient copied
   */
  public String coefsToString(int last, int first)
  {
    StringBuffer buf = new StringBuffer("");
    for (int i = first; i <= last + 1; i++) {
      buf.append(coef[i]);
      buf.append(' ');
    }
    return buf.toString();
  }

  /**
   * Return the varCount.
   */
  public int getVarCount()
  {
    return varCount;
  }

  /**
   * Set the varCount value.
   */
  public void setVarCount(int varCount)
  {
    this.varCount = varCount;
  }

  /**
   * Increment the varCount value by 1.
   */
  public void incVarCount()
  {
    varCount++;
  }

  /**
   * Return the key.
   */
  public int getKey()
  {
    return key;
  }

  /**
   * Set the key value.
   */
  public void setKey(int key)
  {
    this.key = key;
  }

  /**
   * Return true if this Equation is touched.
   */
  public boolean isTouched()
  {
    return touched;
  }

  /**
   * Set the touched flag.
   */
  public void setTouched(boolean flg)
  {
    touched = flg;
  }

  /**
   * Return true if this Equation is essential.
   */
  public boolean isEssential()
  {
    return essential;
  }

  /**
   * Set the essential flag.
   */
  public void setEssential(boolean flg)
  {
    essential = flg;
  }

  /**
   * Return true if specified coefficient is zero.
   */
  public boolean isZero(int var)
  {
    return coef[var] == 0;
  }

  /**
   * Return true if specified coefficient is not zero.
   */
  public boolean isNotZero(int var)
  {
    return coef[var] != 0;
  }

  /**
   * Turn the Equation BLACK.
   */
  public void turnBlack()
  {
    color = Problem.BLACK;
  }

  /**
   * Turn the Equation red.
   */
  public void turnRed()
  {
    color = Problem.RED;
  }

  /**
   * Return the color of the Equation.
   */
  public int getColor()
  {
    return color;
  }

  /**
   * Set the color of the Equation.
   */
  public void setColor(int color)
  {
    this.color = color;
  }

  /**
   * Set the color of the Equation.
   */
  public void setColor(Equation f1)
  {
    this.color = f1.color;
  }

  /**
   * Set the color of this Equation to the union of the colors of the two Equations.
   */
  public void setColor(Equation f1, Equation f2)
  {
    this.color = f1.color | f2.color;
  }

  /**
   * Set the color of this Equation to the union of the colors of the three Equations.
   */
  public void setColor(Equation f1, Equation f2, Equation f3)
  {
    this.color = f1.color | f2.color | f3.color;
  }

  /**
   * Return true if the Equation is BLACK.
   */
  public boolean isBlack()
  {
    return color == Problem.BLACK;
  }

  /**
   * Return true if the Equation is not BLACK.
   */
  public boolean isNotBlack()
  {
    return color != Problem.BLACK;
  }

  /**
   * Return true if the Equation is not red.
   */
  public boolean isNotRed()
  {
    return color != Problem.RED;
  }

  /**
   * Return the number of variable slots that exist.
   */
  public int numVars()
  {
    return coef.length;
  }

  /**
   * Return the specified coefficient.
   */
  public int getCoefficient(int i)
  {
    if (i >= coef.length)
      return 0;
    return coef[i];
  }

  /**
   * Return the coefficient of the constant term.
   */
  public int getConstant()
  {
    return coef[0];
  }

  /**
   * Set the value of the specified coefficient.
   */
  public void setCoef(int i, int value)
  {
    if (i >= coef.length) {
      int[] nc = new int[i + 1];
      System.arraycopy(coef, 0, nc, 0, coef.length);
      coef = nc;
    }

    coef[i] = value;
  }

  /**
   * Set the value of the constant term.
   */
  public void setConstant(int value)
  {
    coef[0] = value;
  }

  /**
   * Add the value to the specified coefficient.
   */
  public void addToCoef(int var, int value)
  {
    coef[var] += value;
  }

  /**
   * Set the coefficients to the sum of the coefficients from two Equations.
   */
  public void sumOfCoefs(Equation f1, Equation f2, int nVars)
  {
    for (int i = nVars; i >= 0; i--)
      coef[i] = f1.coef[i] + f2.coef[i];
  }

  /**
   * Divide the specified coefficient by the value.
   */
  public void divideCoef(int var, int value)
  {
    coef[var] /= value;
  }

  /**
   * Divide the coefficients by the value.
   */
  public void divideCoefs(int nVars, int value)
  {
    for (int jj = nVars; jj >= 0; jj--)
      coef[jj] /= value;
  }

  /**
   * Divide the coefficients by the value.
   * Abort if they are not evenly divided.
   */
  public void divideCoefsEven(int nVars, int value)
  {
    for (int jj = nVars; jj >= 0; jj--) {
      int x = coef[jj];
      coef[jj] = x / value;
      if (x != coef[jj] * value)
        throw new polyglot.util.InternalCompilerError("divideCoefsEven - " + x + " / " + value);
    }
  }

  /**
   * Set the coefficients to the <tt>(c1 + c2) / divisor</tt> where
   * <tt>n1</tt> and <tt>n2</tt> are the coressponding coefficients from
   * two Equations.
   */
  public void sumAndDivide(Equation f1, Equation f2, int divisor, int nVars)
  {
    for (int i = nVars; i >= 1; i--)
      coef[i] = (f1.coef[i] + f2.coef[i]) / divisor;

    coef[0] = intDivide(f1.coef[0] + f2.coef[0], divisor);
  }

  /**
   * Set the destination coefficient to the same value as the soure coefficient.
   */
  public void copyCoef(int src, int dest)
  {
    if (dest >= coef.length) {
      int[] nc = new int[dest + 1];
      System.arraycopy(coef, 0, nc, 0, coef.length);
      coef = nc;
    }

    coef[dest] = coef[src];
  }

  /**
   * Set the destination coefficient to the same value as the soure coefficient.
   */
  public void copyCoef(Equation from, int src, int dest)
  {
    if (dest >= coef.length) {
      int[] nc = new int[dest + 1];
      System.arraycopy(coef, 0, nc, 0, coef.length);
      coef = nc;
    }

    coef[dest] = from.coef[src];
  }

  /**
   * Set the destination coefficients to the same value as the soure coefficients.
   */
  public void copyCoef(int src, int dest, int number)
  {
    if ((dest + number) >= coef.length) {
      int[] nc = new int[dest + number];
      System.arraycopy(coef, 0, nc, 0, coef.length);
      coef = nc;
    }

    System.arraycopy(coef, src, coef, dest, number);
  }

  /**
   * Copy the coefficients from one Equation to another.
   * @param from is the source of the coefficients
   */
  public void copyCoefs(Equation from, int num)
  {
    System.arraycopy(from.coef, 0, this.coef, 0, num);
  }
   
  /**
   * Copy the coefficients from one Equation to another.
   * @param from is the source of the coefficients
   * @param last is the index of the last coefficient copied
   * @param first is the index of the first coefficient copied
   */
  public void copyCoefs(Equation from, int last, int first)
  {
    for (int i = first; i <= last; i++)
      coef[i] = from.coef[i];
  }

  /**
   * Copy the coefficients from the specified Equation to this Equation.
   * The coefficients are re-ordered as specified.
   * <pre>
   * coef[index[i]] = from.coef[i]
   * </pre>
   * @param from is the source of the coefficients
   * @param index specifies the mapping
   */
  public void copyCoefsIndexed(Equation from, int nVars, int[] index)
  {
    for (int i = 0; i <= nVars; i++) 
      coef[index[i]] = from.coef[i];
  }

  /**
   * Copy the coefficients from the specified Equation to this Equation.
   * <pre>
   * coef[i] = (6 * from.coef[i] + g / 2) / g;
   * </pre>
   * @param from is the source of the coefficients
   * @param last is the index of the last coefficient copied
   * @param first is the index of the first coefficient copied
   * @param g is the weird value
   */
  public void weirdCopyCoefs(Equation from, int last, int first, int g)
  {
    for (int i = first; i <= last; i++)
      coef[i] = intDivide(6 * from.coef[i] + g / 2, g);
  }

  public void copyColumn(Equation fp, int src, int dest)
  {
    if (dest >= coef.length) {
      int[] nc = new int[dest + 1];
      System.arraycopy(coef, 0, nc, 0, coef.length);
      coef = nc;
    }

    if (src >= fp.coef.length)
      coef[dest] = 0;
    else 
      coef[dest] = fp.coef[src];
  }

  /**
   * Set the specified coefficient to zero.
   */
  public void zeroColumn(int col)
  {
    if (col >= coef.length) {
      int[] nc = new int[col + 4];
      System.arraycopy(coef, 0, nc, 0, coef.length);
      coef = nc;
    }
    coef[col] = 0;
  }

  /**
   * Negate the specified coefficients.
   * @param last is the index of the last coefficient negated
   * @param first is the index of the first coefficient negated
   */
  public void negateCoefs(int last, int first)
  {
    for (int i = first; i <= last; i++)
      coef[i] = - coef[i];
  }

  /**
   * Negate the of the specified Equation to get this Equation's coefficients.
   * @param last is the index of the last coefficient negated
   * @param first is the index of the first coefficient negated
   */
  public void negateCoefs(Equation from, int last, int first)
  {
    for (int i = first; i <= last; i++)
      coef[i] = - from.coef[i];
  }

  /**
   * Negate all of the coefficients.
   * The constant term coefficient is set to <tt>-C - 1</tt>.
   */
  public void negate(int nVars)
  {
    coef[0] = -coef[0] - 1;

    for (int i = 1; i <= nVars; i++)
      coef[i] = -coef[i];
  }

  /**
   * Negate all of the coefficients and set touched to true.
   */
  public void negateCoefficients(int nVars)
  {
    for (int i = 0; i <= nVars; i++)
      coef[i] = -coef[i];
    touched = true;
  }

  /**
   * Multiply all of the coefficients by the scalar specified.
   */
  public void multCoefs(int nVars, int mult)
  {
    for (int j = 0; j <= nVars; j++)
      coef[j] *= mult;
  }

  /**
   * Set the coefficients to the product of a scalar and the coefficients of another Equation.
   * The constant term is not multiplied.
   * @param mult is the scalar used
   */
  public void multCoefs(Equation f1, int nVars, int mult)
  {
    for (int j = 1; j <= nVars; j++)
      coef[j] = f1.coef[j] * mult;
  }

  private int checkMultiply(long x, long y)
  {
    long p = x * y;
    int  r = (int) p;
    if (r != p)
      throw new polyglot.util.InternalCompilerError("integer multiply overflow " + x + " " + y);
    return r;
  }

  /**
   * Multiply the coefficients by a scalar value.
   * @param mult is the scalar used
   */
  public void checkMultCoefs(int nVars, int mult)
  {
    for (int j = 0; j <= nVars; j++)
      coef[j] = checkMultiply(coef[j], mult);
  }

  /**
   * Set the coefficients to the product of a scalar and the coefficients of another Equation.
   * @param mult is the scalar used
   */
  public void checkMultCoefs(Equation f1, int nVars, int mult)
  {
    for (int i = 0; i <= nVars; i++)
      coef[i] = checkMultiply(f1.coef[i], mult);
  }

  /**
   * Subtract the product of a scalar and the coefficients of another Equation 
   * from these coefficients.
   * @param mult is the scalar used
   */
  public void multAndSub(Equation f1, int mult, int nVars)
  {
    for (int i = 0; i <= nVars; i++)
      coef[i] -= checkMultiply(f1.coef[i], mult);
  }

  /**
   * Set the coefficients to 
   * <code>(coef[i] = mult1 * f1.coef[i] + mult2 * f2.coef[i];)</code>.
   */
  public void sumOfMult(Equation f1, int mult1, Equation f2, int mult2, int nVars)
  {
    for (int i = 0 ; i <= nVars; i++)
      coef[i] = checkMultiply(f1.coef[i], mult1) + checkMultiply(f2.coef[i], mult2);
  }

  /**
   * Add the sorce column coefficient to the destination column coefficient.
   * Set the source coefficient to zero.
   */
  public void combineColumns(int src, int dest)
  {
    coef[dest] += coef[src];
    coef[src] = 0;
  }

  /**
   * Swap the values of the specified coefficients and set touched true.
   */
  public void swapVars(int v1, int v2)
  {
    int a = coef[v1];
    int b = coef[v2];

    if (a == b)
      return;

    coef[v1] = b;
    coef[v2] = a;
    touched = true;
  }

  /**
   * Return true if equation is of form <code>(v1 - coef >= v2)</code>.
   */
  public boolean findDifference(int nVars) 
  {
    for (v1 = 1; v1 <= nVars; v1++)
      if (coef[v1] != 0)
        break;

    for (v2 = v1 + 1; v2 <= nVars; v2++)
      if (coef[v2] != 0)
        break;

    if (v2 > nVars) {
      if (coef[v1] == -1) {
        v2 = v1;
        v1 = 0; 
        return true;
      }
      if (coef[v1] == 1) {
        v2 = 0;
        return true;
      }
      return false;
    }

    if ((coef[v1] * coef[v2]) != -1)
      return false;

    if (coef[v1] < 0) {
      int t = v1;
      v1 = v2;
      v2 = t;
    }

    return true;
  }

  private int intDivide(int a, int b)
  {
    if (a > 0)
      return a / b;

    return -((-a + b - 1) / b);
  }

  public void intModHat(int var, int b)
  {
    int a = coef[var];
    int r = a - b * intDivide(a, b);
    if (r > -(r - b))
      r -= b;
    coef[var] = r;
  }

  public void intModHatI(int i, int nVars)
  {
    int g = coef[i];
    if (g < 0)
      g = -g;

    for (int j = 0; j <= nVars; j++) {
      if (i == j)
        continue;

      int a = coef[j];
      int r = a - g * intDivide(a, g);
      if (r > -(r - g))
        r -= g;
      coef[j] = r;
    }
  }

  public boolean intModHat(int first, int last, int b)
  {
    if (b <= 0)
      throw new polyglot.util.InternalCompilerError("intModHat");

    boolean change = false;
    for (int i = first; i <= last; i++) {
      int a = coef[i];
      if ((a == 1) || (a == -1))
        continue;

      int t = a - b * intDivide(a, b);
      if (t > -(t - b))
        t -= b;

      if (t != a) {
        coef[i] = t;
        change = true;
      }
    }
    return change;
  }

  private int gcd(int b, int a) /* First argument is non-negative */
  {
    if ((a < 0) || (b < 0))
      throw new polyglot.util.InternalCompilerError("gcd error " + a + " " + b);

    if (b == 1)
      return 1;

    while (b != 0) {
      int t = b;
      b = a % b;
      a = t;
    }
    return a;
  }

  /**
   * Return the greates common denominator of the specified coefficients.
   * @param last is index of the last coefficient
   * @param first is index of the first coefficient
   */
  public int gcdCoefs(int last, int first)
  {
    int g = 0;
    for (int i = last; i >= first; i--) {
      int x = coef[i];
      if (x < 0)
        x = -x;
      g = gcd(x, g);
    }
    return g;
  }

  public int gcdSumOfProd(Equation eq2, int mult1, int mult2, int last, int first)
  {
    int g = 0;

    for (int j = last; j >= first; j--) {
      int diff = mult1 * coef[j] + mult2 * eq2.coef[j];
      if (diff < 0)
        diff = -diff;
      g = gcd(g, diff);
      if (g == 1)
        return g;
    }

    return g;
  }

  public int computeHashcode(int topVar, int[] packing)
  {
    int i0       = topVar;
    int i        = packing[i0--];
    int g        = coef[i];
    int hashCode = g * (i + 3);

    if (g < 0)
      g = -g;

    for (; i0 >= 0; i0--) {
      i = packing[i0];
      int x = coef[i];
      hashCode = hashCode * keyMult * (i + 3) + x;

      if (x < 0)
        x = -x;

      if (x == 1) {
        g = 1;
        i0--;
        break;
      }

      g = gcd(x, g);
    }

    for (; i0 >= 0; i0--) {
      i = packing[i0];
      int x = coef[i];
      hashCode = hashCode * keyMult * (i + 3) + x;
    }

    if (g > 1) {
      coef[0] = intDivide(coef[0], g);
      i0 = topVar;
      i = packing[i0--];
      coef[i] = coef[i] / g;
      hashCode = coef[i] * (i + 3);
      for (; i0 >= 0; i0--) {
        i = packing[i0];
        coef[i] = coef[i] / g;
        hashCode = hashCode * keyMult * (i + 3) + coef[i];
      }
    }

    return hashCode;
  }

  /**
   * Return true if any coefficient's value is 1 or -1.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   */
  public boolean anyNZCoef(int last, int first)
  {
    for (int i = first; i <= last; i++)
      if (coef[i] != 0)
        return true;
    return false;
  }

  /**
   * Return count of coefficients that are not zero.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   */
  public int numNZCoefs(int last, int first)
  {
    int count = 0;
    for (int i = first; i <= last; i++)
      if (coef[i] != 0)
        count++;
    return count;
  }

  /**
   * Return count of coefficients whose value is 1 or -1.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   */
  public int numOneCoefs(int last, int first)
  {
    int count = 0;
    for (int i = first; i <= last; i++) {
      int c = coef[i];
      if ((c == 1) || (c == -1))
        count++;
    }
    return count;
  }

  /**
   * Return the index of the last coefficient whose value is 1 or -1.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   * If none qualifiy, return <tt>first - 1</tt>.
   */
  public int lastOneCoef(int last, int first)
  {
    for (int i = last; i >= first; i--)
      if ((coef[i] == 1) || (coef[i] == -1))
        return i;
    return first - 1;
  }

  /**
   * Return the index of the last non-zero coefficient.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   * If none qualifiy, return <tt>first - 1</tt>.
   */
  public int lastNZCoef(int last, int first)
  {
    for (int i = last; i >= first; i--)
      if (coef[i] != 0)
        return i;
    return first - 1;
  }

  /**
   * Return the index of the last non-zero coefficient whose index is not specified.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   * If none qualifiy, return <tt>first - 1</tt>.
   * @param v1 specifies the first index to be skipped
   * @param v2 specifies the second index to be skipped
   */
  public int lastNZCoef(int v1, int v2, int last, int first)
  {
    for (int i = last; i >= first; i--) {
      if ((i == v1) || (i == v2))
        continue;

      if (coef[i] != 0)
        return i;
    }
    return first - 1;
  }

  /**
   * Return the index of the last coefficient whose absolute value is greater than 1.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   * If none qualifiy, return <tt>first - 1</tt>.
   */
  public int lastCoefGt1(int last, int first)
  {
    for (int i = last; i >= first; i--) {
      if (coef[i] < -1)
        return i;
      if (coef[i] > 1)
        return i;
    }
    return first - 1;
  }

  /**
   * Return the index of the last coefficient with the smallest non-zero value.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   * If none qualifiy, return <tt>first - 1</tt>.
   */
  public int findSmallestNZ(int last, int first)
  {
    int c = Integer.MAX_VALUE;
    for (int i = last; i > first; i--) {
      int x = coef[i];
      if (x == 0)
        continue;
      if (x < 0)
        x = -x;
      if (x < c)
        c = x;
    }
    return c;
  }

  /**
   * Return the index of the last coefficient with the largest non-zero value.
   * Start at the coefficient specified by <tt>last</t> and
   * stop at the coefficient specified by <tt>first</tt>.
   * If none qualifiy, return <tt>first - 1</tt>.
   */
  public int findLargestNZ(int last, int first)
  {
    int c = Integer.MIN_VALUE;
    for (int i = last; i > first; i--) {
      int x = coef[i];
      if (x == 0)
        continue;
      if (x < 0)
        x = -x;
      if (x > c)
        c = x;
    }
    return c;
  }

  public int lastDiffModZero(Equation eq2, int g, int i, int last, int first)
  {
    for (int j = last; j >= first; j--) {
      if (i == j)
        continue;
      if ((coef[j] - (eq2.coef[j] % g)) == 0)
        return j;
    }
    return first - 1;
  }

  public int crossProduct(int last, int first)
  {
    int crossProduct = 0;
    for (int i = last; i >= first; i--) {
      int c = coef[i];
      if (c != 0)
        crossProduct += c * c;
    }
    return crossProduct;
  }

  /**
   * Set the array element true if the corresponding coefficient is not zero.
   */
  public void setTrueIfNotZero(boolean[] res, int last, int first)
  {
    for (int i = first; i <= last; i++)
      if (coef[i] != 0)
        res[i] = true;
  }

  /**
   * 
   * @return the number of non-zero indexes
   */
  public int packNZIndexes(int[] indexes, int last, int first)
  {
    int nz = 0;
    for (int i = first; i <= last; i++)
      if (coef[i] != 0) {
        indexes[nz] = i;
        nz++;
      }
    return nz;
  }

  public boolean isGoodEquation(Equation eq2, int mult, int last, int first)
  {
    if (mult < 0)
      mult = - mult;

    for (int i = last; i >= first; i--) {
      int ck1 = coef[i];
      int ck2 = eq2.coef[i];
      if (ck2 + checkMultiply(ck1, mult) != 0)
        return false;
    }
    return true;
  }
}
