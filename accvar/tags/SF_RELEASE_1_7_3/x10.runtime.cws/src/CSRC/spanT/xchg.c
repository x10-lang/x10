__inline__ int Cilk_xchg(volatile int *ptr, int x)
   {
        int result;
        __asm__ __volatile__ (
             "0: lwarx %0,0,%1\n stwcx. %2,0,%1\n bne- 0b\n isync\n" :
             "=&r"(result) :
             "r"(ptr), "r"(x) :
             "cr0");

        return result;
   }

