/*
============================================================================
 Name        : Sys.h
 Author      : Rajkishore Barik, Sriram Krishnamoorthy
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Sys_h
#define x10lib_Sys_h

#include <assert.h>
#include <time.h>

namespace x10lib_cws {

#if defined(__powerpc__) || defined(__ppc__)

#define MEM_BARRIER()  __asm__ __volatile__ ("sync" : : : "memory")
#define READ_BARRIER()  __asm__ __volatile__ ("lwsync" : : : "memory")
#define WRITE_BARRIER()  __asm__ __volatile__ ("lwsync" : : : "memory")

static __inline__ int 
atomic_exchange(volatile int *ptr, int x) {
  int result=0;
  __asm__ __volatile__ (
			"lwarx %0,0,%1  \n\t"
			"stwcx. %2,0,%1 \n\t"
			".long 0x40a2fff8 \n\t"
			"isync\n" :
			"=&r"(result) : 
			"r"(ptr), "r"(x) :
			"cr0");
  return result;
}

static  int
compare_exchange(int *p, int  old_value, int new_value) {
  int prev;                                        
  __asm__ __volatile__ (                           
			"\n"
        		"l1:\n\t"
			"lwarx   %0,0,%2\n\t"
        		"cmpw    0,%0,%3\n\t"
        		"bne-    l2 \n\t"
        		"stwcx.  %4,0,%2\n\t"   
        		"bne-    l1 \n\t"
        		"isync\n"
        		"l2:"
        		: "=&r" (prev), "=m" (*p)
        		: "r" (p), "r" (old_value),
			"r" (new_value), "m" (*p)
        		: "cc", "memory");
  return prev;
}

static void 
atomic_add(volatile int* mem, int val) {
  int tmp;
  __asm__ __volatile__ (                                      
			" #Inline atomic add\n"  
			"l1:\n\t"
			"lwarx    %0,0,%2 \n\t"
			"add%I3   %0,%0,%3 \n\t"
			"stwcx.   %0,0,%2 \n\t"
			"bne-     l1 \n\t"  
			"isync \n\t"
			: "=&b"(tmp), "=m" (*mem)
			: "r" (mem), "r"(val), "m" (*mem) 
			: "cr0");
}

/*sriramk: What is this thing supposed to do*/
static __inline__ int 
atomic_fetch(volatile int* mem) {
  return *mem;
}

#elif defined(__sparc_v9__)

 /*This requires sparc v9 extension. So we assume we have sparc v9 for now.*/
 static  __inline__ int
   compare_exchange(int *p, int  old_value, int new_value) {
   __asm__ __volatile__ ( "cas [%1],%2,%0"
			  : "+r" ( new_value )
			  : "r" ( p ), "r" ( old_value )
			  : "memory");
   return old_value;
 }

 static __inline__ int 
   atomic_fetch(volatile int* mem) {
   __asm__ __volatile__ ( "membar #StoreLoad | #LoadLoad" : : : "memory" );
   return *(volatile int *)mem;
 }
 
 static __inline__ int
   atomic_add(volatile int * mem, int val) {
   int nrv;
   do {
     nrv = atomic_fetch(mem);
   } while (compare_exchange(mem,nrv+val,nrv)!=nrv);
   return nrv;
 }

#if 1
 static __inline__ int 
   atomic_exchange(volatile int *ptr, register int xchg) {
   __asm__ volatile ("swap [%1],%0"
		     :"=r" (xchg)	/* registers written */
		     :"r"(ptr), "0"(xchg)		/* registers read */
		     : "memory"
		     );
   return xchg;
 }

#else
 static __inline__ int 
   atomic_exchange(volatile int * mem,
		   int val) {
   int nrv;
   do {
     nrv = atomic_fetch(mem);
   } while (compare_exchange(mem,val,nrv)!=nrv);
   return nrv;
 }
#endif
 

#  ifdef __sparc_v9__
#    define MEM_BARRIER()  \
       __asm__ __volatile__("membar #StoreLoad | #StoreStore | #LoadLoad | #LoadStore" ::: "memory")
#    define READ_BARRIER() __asm__ __volatile__ ("membar #StoreLoad" ::: "memory")
#    define WRITE_BARRIER() __asm__ __volatile__ ("": : :"memory")
#  else
#    define MEM_BARRIER()  __asm__ __volatile__("stbar" ::: "memory")
     static inline void READ_BARRIER(void) { int dummy; atomic_exchange(&dummy, 0); }
#    define WRITE_BARRIER() __asm__ __volatile__ ("": : :"memory")
#endif



#else
#error "Architecture not supported yet. Fix atomics in Sys.h to continue."
#endif



/*Simple portable timers for now. Could add accurate system-specific
  timers later*/

inline long long nanoTime() {
  struct timespec ts;
  // clock_gettime is POSIX!
  ::clock_gettime(CLOCK_REALTIME, &ts);
  return (long long)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
}


}
#endif
