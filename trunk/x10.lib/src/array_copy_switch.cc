/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy_switch.cc,v 1.3 2008-01-06 03:28:51 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>
#include "__x10lib.h__"
using namespace std;

extern "C"
void*
ArrayCopySwitch (int handler, void* args)
{
   cout << "!!ArrayCopySwitch should be overridden!!\n";
   exit(-1);
}
