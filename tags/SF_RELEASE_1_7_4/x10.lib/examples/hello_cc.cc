/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: hello_cc.cc,v 1.2 2007-06-27 14:48:33 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/x10lib.h>
#include <iostream>

using namespace std;

int
main(int argc, char *argv[])
{
	x10lib::Init(NULL, 0);
	cout << "Hello, X10 World!" << endl;
	x10lib::Finalize();
	return 0;
}
