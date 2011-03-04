/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: Test_array.cc,v 1.6 2007-10-11 08:27:16 ganeshvb Exp $ */

#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>

using namespace std;
using namespace x10lib;

int 
main (int argc, char* argv[])
{
  cout << "Test Case Not Working -- please do not use it now " << endl;

  x10lib::Init(NULL, 0);

  Region<1>* r = new RectangularRegion<1>(Point<1>(99));

  x10_place_t p [4] = {0, 1, 2, 3};

  Region<1>* base = new RectangularRegion<1>(Point<1>(99));
  Region<1>* grid = new RectangularRegion<1>(Point<1>(3));
  TiledRegion<1>* t =  TiledRegion<1>::makeBlock(base, grid);
  UniqueDist<1>* u = new UniqueDist<1>(t, p);
  int b =0;
  if (here() == 0) {
    Array<int, 1>* a = makeArray <int,1, TiledRegion, UniqueDist>(t, u);
    //Array<int, 1>* a = makeArray <int,1, TiledRegion, UniqueDist >(b);
    //a->putElementAt (Point<1>(10), 10);
    //assert (a->getElementAt (Point<1>(10)) == 10);
    a->putRemoteElementAt (Point<1>(30), 10);
  } 
 
  cout << "Test_array PASSED" << endl;

  LAPI_Gfence (GetHandle()); 

  x10lib::Finalize();

  return 0;
}
