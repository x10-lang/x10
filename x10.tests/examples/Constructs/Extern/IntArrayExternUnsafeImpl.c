/*
 * Filename:TestExternUnsafe1_x10stub.c
 * Generated: 1/19/05 4:19 PM */
/* compiled on windows with
cl "-Ic:/Program Files/IBM/Java142/include" "-Ic:/Program Files/IBM/Java142/include/win32" 
   -LD TestExternUnsafe1Impl.c -FeTestExternUnsafe1.dll
*/
#include <sys/types.h>
#include <jni.h>
#include <stdio.h>

#include "IntArrayExternUnsafe_x10stub.c"

#ifdef __cplusplus
extern "C" {
#endif

  void IntArrayExternUnsafe_howdy(int array[], int descriptor[]) {
    int i;	
    int rank = descriptor[0];
    int size;
    if(rank != 1) {
      printf("Error: expected rank of 1, not %d\n",rank);
      return;
    }	

    size = descriptor[1];
    printf("Extern call: passed in array address %p, size %d\n",array,size);
    fflush(stdout);

    for(i = 0; i < size; ++i) {
      array[i] += 100;
      //printf("[%d]:%d (%p=%d)\n",i,array[i],&(array[i]),&(array[i]));
    }
  }

#ifdef __cplusplus
}
#endif


   
