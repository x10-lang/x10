/*
 * Filename:JacobiExtern_x10stub.c
 * Generated: 1/20/05 4:46 PM */

#include <sys/types.h>
#include <jni.h>
#include <stdlib.h>
#include <math.h>
#include <stdio.h>

#include "JacobiExtern_x10stub.c"

#ifdef __cplusplus
extern "C" {
#endif

  static int calcArraySize(int descriptor[]) {
    int i; int size;
    int numElements = 1;
    size = descriptor[0];
    if (size == 0) return 0;
    for (i = 1; i <= size; ++i)
      numElements *= descriptor[i];

    return numElements;
  }

  static void dumpArray(char *title, double array[], int descriptor[]) {
    int i, j;
    int outerSize, innerSize;
    int rowSize = descriptor[2]; //size of last row
    outerSize = descriptor[1];   
    innerSize = descriptor[2];      

    if (descriptor[0] != 2) {
      printf("Unexpected rank size %d != 2\n", descriptor[0]);
      return;
    }
    for (i = 0; i < outerSize; ++i)
      for (j = 0; j < innerSize; ++j)
	printf("%s[%d][%d]=%e\n", title, i, j, array[i*rowSize + j]);

    fflush(stdout);
  }

  /*
   * compute the error betweenorigArray and b, where each of neighbourSum's elements is the sum of the corresponding 
   * neighbours in origArray
   */
  double JacobiExtern_computeError(double origArray[], int origArrDescriptor[], double neighbourSum[], int neighbourDescriptor[]) {

    int i, j, origInnerSize, origOuterSize, neighbourInnerSize, neighbourOuterSize;
    double err;
    int traceit = 0;

    /* assume rank 2 for both arrays */
    if (origArrDescriptor[0] != 2) {
      printf("Unexpected rank size %d != 2\n", origArrDescriptor[0]);
      return 0;
    }
    if (neighbourDescriptor[0] != 2) {
      printf("Unexpected rank size %d != 2\n", neighbourDescriptor[0]);
      return 0;
    }

    origOuterSize = origArrDescriptor[1];
    origInnerSize = origArrDescriptor[2];
    neighbourOuterSize = neighbourDescriptor[1];
    neighbourInnerSize = neighbourDescriptor[2];

    if (traceit) {
      dumpArray("orig", origArray, origArrDescriptor);
      dumpArray("neighbour", neighbourSum, neighbourDescriptor);
    }

    err = 0.0;

    for (i = 0; i < neighbourOuterSize; ++i)
      for (j = 0; j < neighbourInnerSize; ++j) {
	int origIndexI, origIndexJ; /* adjust by 1*/
	double aVal, bVal;
	origIndexI = i + 1;
	origIndexJ = j + 1;
	aVal = origArray[(origIndexI*origInnerSize)+origIndexJ];
	bVal = neighbourSum[i*neighbourInnerSize+j];
	err += fabs(aVal - bVal);
	if (traceit) {
	  printf("orig[%d][%d]:%e neighbour[%d][%d]:%e err=%e\n",
	      origIndexI, origIndexJ, aVal, i, j, bVal, err);
	  fflush(stdout);
	}
      }
    return err;
  }

#ifdef __cplusplus
}
#endif
