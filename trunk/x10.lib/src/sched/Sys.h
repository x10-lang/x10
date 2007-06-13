/*
============================================================================
 Name        : Sys.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Sys_h
#define x10lib_Sys_h

namespace x10lib_cws {
#ifndef __POWERPC__ 
#define __POWERPC__
	
#define MEM_BARRIER()  __asm__ __volatile__ ("sync" : : : "memory")
#define READ_BARRIER()  __asm__ __volatile__ ("lwsync" : : : "memory")
#define WRITE_BARRIER()  __asm__ __volatile__ ("lwsync" : : : "memory")
 
static __inline__ int atomic_exchange(volatile int *ptr, int x)
{
		int result;
		
		__asm__ __volatile__ (
				"lwarx %0,0,%1\n stwcx. %2,0,%1\n .long 0x40a2fff8 \n isync\n" :
				"=&r"(result) : 
				"r"(ptr), "r"(x) :
				"cr0");

		return result;
}


static __inline__ int
compare_exchange(volatile int *p, int  old_value, int new_value)
{
         int prev;
        __asm__ __volatile__ (

        		"1:     lwarx   %0,0,%2\n\
        				cmpw    0,%0,%3\n\
        				bne-    2f\n\
        				stwcx.  %4,0,%2\n\
        				bne-    1b\
        				isync\n\
        		2:"	
        		: "=&r" (prev), "=m" (*p)
        		: "r" (p), "r" (old_value), "r" (new_value), "m" (*p)
        		: "cc", "memory");
 
         return prev;
}

static __inline__ void atomic_add(volatile int* mem, int val)
{
    int tmp;
    __asm__ __volatile__ (
			  "/* Inline atomic add */\n"
			  "0:\t"
				  "lwarx    %0,0,%2 \n\t"
				  "add%I3   %0,%0,%3 \n\t"
				  "stwcx.   %0,0,%2 \n\t"
				  "bne-     0b \n\t"
   		  	"isync \n\t"
			  : "=&b"(tmp), "=m" (*mem)
			  : "r" (mem), "Ir"(val), "m" (*mem)
			  : "cr0");
  
}
#endif

}
#endif
