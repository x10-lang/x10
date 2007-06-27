/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: hello_c.c,v 1.2 2007-06-27 14:48:33 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/x10lib.h>
#include <stdio.h>

int
main(int argc, char *argv[])
{
	x10_init(NULL, 0);
	printf("Hello, X10 World!\n");
	x10_finalize();
	return 0;
}
