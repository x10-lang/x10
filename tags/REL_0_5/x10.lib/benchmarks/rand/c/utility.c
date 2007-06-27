/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: utility.c,v 1.2 2007-05-03 11:40:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/** Utility routines **/
#include <sys/time.h>
#include <time.h>
#include "gups.h"

/* current time in microseconds */
double
cur_time(void)
{
	struct timeval tv;
	
	gettimeofday(&tv, NULL);
	return (tv.tv_sec * 1e6 + tv.tv_usec);
}

/* start random number generator at nth step */
uint64_t
start_ran(int64_t n)
{
	int i, j;
	uint64_t m2[64];
	uint64_t temp, ran;
	
	while (n < 0) n += PERIOD;
	while (n > PERIOD) n-= PERIOD;
	if (n == 0) return 0x1;
	
	temp = 0x1;
	for (i = 0; i < 64; i++) {
		m2[i] = temp;
		temp = (temp << 1) ^ ((int64_t)temp < 0 ? POLY : 0);
		temp = (temp << 1) ^ ((int64_t)temp < 0 ? POLY : 0);
	}
	
  	for (i = 62; i >= 0; i--)
		if ((n >> i) & 1)
			break;
			
	ran = 0x2;
	while (i > 0) {
		temp = 0;
		for (j = 0; j < 64; j++)
			if ((ran >> j) & 1)
				temp ^= m2[j];
		ran = temp;
		i -= 1;
		if ((n >> i) & 1)
			ran = (ran << 1) ^ ((int64_t)ran < 0 ? POLY : 0);
	}
	return ran;
}
