/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array.cpp,v 1.2 2007-04-28 09:28:45 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;

int 
main (int argc, char* argv[])
{
  x10lib::Init(NULL, 0);


  Region<1>* r = new RectangularRegion<1>(Point<1>(99));

  place_t p [4] = {0, 1, 2, 3};

  RectangularRegion<1>* t0 = new RectangularRegion<1>(Point<1>(24));
  RectangularRegion<1>* t1 = new RectangularRegion<1>(Point<1>(49));
  RectangularRegion<1>* t2 = new RectangularRegion<1>(Point<1>(74));
  RectangularRegion<1>* t3 = new RectangularRegion<1>(Point<1>(99));
  const RectangularRegion<1>* bases[] = {t0, t1, t2, t3};
  Region<1>* t = new TiledRegion<1> (Point<1>(3), bases);
  Dist<1>* u = new UniqueDist<1>(t, p);

  Array<int, 1>* a = Array<int, 1>::makeArray (t, u);

  cout << "Test_array PASSED" << endl;
  x10lib::Finalize();

  return 0;
}
