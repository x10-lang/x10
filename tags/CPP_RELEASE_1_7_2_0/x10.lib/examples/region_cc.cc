/*
 * (c) Copyright IBM Corporation 2007
 * $Id: region_cc.cc,v 1.1 2007-12-08 14:28:15 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Example program that illustrates the construction of 
 ** points and regions, and region iteration.  
 ** A 2D region [1:4, 2:5] is created 
 ** and iterated using different iterators. 
 ** The points are printed during the iteration. 
 **/

#include <x10/x10lib.h>

#include <iostream>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{ 
  /* create 2D points */
  Point<2> origin(1, 2);
  Point<2> diagonal (4, 5);
  Point<2> step (1, 1);

  /* create 2D region */
  RectangularRegion <2> r(origin, diagonal, step);

  /* lexico-graphic iteration over all the points in the region*/
  cout << "All points" << endl;
  for (int i = 0; i < r.card(); i++) {
    Point<2> coord = r.coord(i);
    cout << "(" << coord.value(0) << ", " << coord.value(1) << ")" << endl;
  }

  /* opitmized iteration using static iterators */

  /* rectangular iteration */
  cout << "All points" << endl;
  for_local_2d (I, J, :, r) {
    cout << "( " << I << ", " << J << ") " << endl;
  }

  /* lower triangular region */
  cout << "Lower Triangular Region " << endl;
  for_local_2d_lower (I, J, :, r) {
    cout << "( " << I << ", " << J << ") " << endl;
  }

  /* upper triangular region */
  cout << "Upper Triangular Region " << endl;
  for_local_2d_upper (I, J, :, r) {
    cout << "( " << I << ", " << J << ") " << endl;
  }

  /* diagonal */
  cout << "Diagonal Region " << endl;
  for_local_2d_diag (I, J, :, r) {
    cout << "( " << I << ", " << J << ") " << endl;
  }

  /* nested iteration */
  cout << "Nested iteration " << endl;
  for_local_2d (I, J, :, r) {
     for_local_2d (M, N, :, r) {
       cout << "( " << I << ", " << J <<  ", " << M << ", " << N << ") " << endl;
     }
  }

  return 0;
}
