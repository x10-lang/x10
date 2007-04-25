/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: hello_c.c,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/x10lib.h>
#include <stdio.h>

int
main(int argc, char *argv[])
{
	x10_init(NULL, 0);
	printf("Hello, World!\n");
	x10_finalize();
	return 0;
}
