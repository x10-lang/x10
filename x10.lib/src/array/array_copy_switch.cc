/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy_switch.cc,v 1.2 2007-12-10 13:15:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>

using namespace std;

extern "C"
void*
arrayCopySwitch (int handler, void* args)
{
   cout << "arrayCopySwitch should be overriddern \n";
   exit(-1);
}
