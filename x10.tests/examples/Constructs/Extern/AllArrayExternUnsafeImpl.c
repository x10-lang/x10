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

#include "AllArrayExternUnsafe_x10stub.c"

#ifdef __cplusplus
extern "C" {
#endif

  int checkDescriptor(int desc[]){
    
    if(desc[0] != 1) {
      printf("Error: expected rank of 1, not %d\n",desc[0]);
      return 0;
    }	
    return 1;
  }
  int checkSize(int desc[],int size,char *name){
    
    if(desc[1] != size) {
      printf("Error: %s expected size of %d, not %d\n",name,size,desc[1]);
      return 0;
    }	
    return 1;
  }
extern void   AllArrayExternUnsafe_howdy(signed int* yyi_x10PoInTeR, int* yyi_x10DeScRiPtOr, 
       signed short* yys_x10PoInTeR, int* yys_x10DeScRiPtOr, 
       jlong* yyl_x10PoInTeR, int* yyl_x10DeScRiPtOr, 
       signed short* yyc_x10PoInTeR, int* yyc_x10DeScRiPtOr, 
       signed char* yyb_x10PoInTeR, int* yyb_x10DeScRiPtOr, 
       float* yyf_x10PoInTeR, int* yyf_x10DeScRiPtOr, 
       double* yyd_x10PoInTeR, int* yyd_x10DeScRiPtOr,
       unsigned char* yybool_x10PoInTeR, int* yybool_x10DeScRiPtOr);

  void AllArrayExternUnsafe_howdy(int intarray[], int intdescriptor[],
                                  signed short shortarray[], int shortdescriptor[],
                                  jlong longarray[], int longdescriptor[],
                                  signed short chararray[], int chardescriptor[],
                                  signed char bytearray[], int bytedescriptor[],
                                  float floatarray[], int floatdescriptor[],
                                  double doublearray[], int doubledescriptor[],
                                  unsigned char booleanarray[], int booleandescriptor[]) {
    int i;	
    int size;
    if(!checkDescriptor(intdescriptor)) return;
    if(!checkDescriptor(shortdescriptor)) return;
    if(!checkDescriptor(longdescriptor)) return;
    if(!checkDescriptor(chardescriptor)) return;
    if(!checkDescriptor(bytedescriptor)) return;
    if(!checkDescriptor(floatdescriptor)) return;
    if(!checkDescriptor(doubledescriptor)) return;
    if(!checkDescriptor(booleandescriptor)) return;

    size = intdescriptor[1];

    if(!checkSize(shortdescriptor,size,"short")) return;
    if(!checkSize(longdescriptor,size,"long")) return;
    if(!checkSize(bytedescriptor,size,"byte")) return;
    if(!checkSize(chardescriptor,size,"char")) return;
    if(!checkSize(floatdescriptor,size,"float")) return;
    if(!checkSize(doubledescriptor,size,"double")) return;
    if(!checkSize(booleandescriptor,size,"boolean")) return;


    for(i = 0; i < size; ++i) {
      intarray[i] += 100;
      shortarray[i] += 100;
      longarray[i] += 100;
      bytearray[i] += 100;
      chararray[i] += 100;
      floatarray[i] += 100;
      doublearray[i] += 100;
      booleanarray[i] = 1;
      //printf("[%d]:%d (%p=%d)\n",i,intarray[i],&(intarray[i]),&(intarray[i]));
    }
  }

#ifdef __cplusplus
}
#endif


   
