/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_copy_switch.cc,v 1.4 2008-06-02 16:07:23 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>
#include "__x10lib.h__"
using namespace std;

extern "C"
void*
ArrayCopySwitch (x10_async_handler_t handler, void* args)
{
   cout << "!!ArrayCopySwitch should be overridden!!\n";
   exit(-1);
}
