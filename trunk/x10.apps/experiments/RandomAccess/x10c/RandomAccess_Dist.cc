#include "RandomAccess_Dist.h"
using namespace x10::lang;
#include "RandomAccess_Dist.inc"
using namespace x10;
static x10::ref<Exception> EXCEPTION = NULL;
//#line 67 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
//#line 68 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
//#line 69 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
//#line 70 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
//#line 71 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
RandomAccess_Dist::localTable::localTable(x10_long size) : x10::lang::Object() {
    
//#line 72 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    tableSize = size
    ;
    
//#line 73 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    mask = tableSize - 1
    ;
    
//#line 74 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    array = (x10::ref<x10::x10array<x10_long> >) (x10::x10newArray<x10_long>(x10::ref<_region<1> >(new (x10::alloc<_region<1> >()) _region<1>(
                                                                                                     0,
                                                                                                     (x10_int)
                                                                                                       mask))->toDistribution()))
    ;
}
//#line 78 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
void RandomAccess_Dist::localTable::update(x10_long ran) {
    
//#line 79 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    array->_data[(x10_int) (ran & mask)] ^= ran
    ;
}
//#line 83 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
void RandomAccess_Dist::localTable::verify(x10_long ran) {
    
//#line 84 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    (array->_data[(x10_int) (ran & mask)])++;
}
//#line 88 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_double RandomAccess_Dist::mysecond() {
    
//#line 89 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    return (x10_double) ((x10_double) (System::nanoTime() /
                                         1000) * 1.0E-6);
    
}
//#line 92 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_long RandomAccess_Dist::POLY = 7;
//#line 93 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_long RandomAccess_Dist::PERIOD = 1317624576693539401ll;
//#line 95 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10::ref<dist> RandomAccess_Dist::UNIQUE = dist::
                                             UNIQUE;
//#line 96 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::NUMPLACES = place::MAX_PLACES;
//#line 97 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::PLACEIDMASK = NUMPLACES -
  1;
//#line 103 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::UPDATE = 0;
//#line 104 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::VERIFICATION_P = 1;
//#line 105 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::UPDATE_AND_VERIFICATION =
  2;
//#line 110 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
x10_long RandomAccess_Dist::HPCC_starts(x10_long n) {
    
//#line 111 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    x10_int i;
    
//#line 111 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    x10_int j;
    
//#line 112 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    x10::ref<x10::array<x10_long> > m2 = x10::alloc_array<x10_long>(64);
    
//#line 113 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    x10_long temp;
    
//#line 113 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    x10_long ran;
    
//#line 115 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    while (n < 0) {
    
//#line 115 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    n += PERIOD
    ;
    }
    
//#line 116 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    while (n > PERIOD) {
    
//#line 116 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    n -= PERIOD
    ;
    }
    
//#line 117 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    if (n == 0) {
        
//#line 117 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        return 1;
        
    }
    
//#line 119 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    temp = 1
    ;
    
//#line 120 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    for (
//#line 120 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
         i = 0
         ; i < 64; 
//#line 120 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                   i++) {
    {
        
//#line 121 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        m2->_data[i] = temp;
        
//#line 122 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        temp = (temp << 1) ^ ((x10_long) temp < 0
                                ? POLY : 0)
        ;
        
//#line 123 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        temp = (temp << 1) ^ ((x10_long) temp < 0
                                ? POLY : 0)
        ;
    }
    }
    
//#line 126 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    for (
//#line 126 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
         i = 62
         ; i >= 0; 
//#line 126 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                   i--) {
    {
        
//#line 127 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        if (((n >> i) & 1) != 0) {
            
//#line 128 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            break;
            
        }
        
    }
    }
    
//#line 130 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    ran = 2
    ;
    
//#line 131 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    while (i > 0) {
    {
        
//#line 132 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        temp = 0
        ;
        
//#line 133 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        for (
//#line 133 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
             j = 0
             ; j < 64; 
//#line 133 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                       j++) {
        {
            
//#line 134 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            if (((ran >> j) & 1) != 0) {
                
//#line 135 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                temp ^= m2->_data[j]
                ;
            }
            
        }
        }
        
//#line 136 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        ran = temp
        ;
        
//#line 137 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        i -= 1
        ;
        
//#line 138 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        if (((n >> i) & 1) != 0) {
            
//#line 139 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            ran = (ran << 1) ^ ((x10_long) ran < 0
                                  ? POLY : 0)
            ;
        }
        
    }
    }
    
//#line 142 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    return ran;
    
}
//#line 247 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
/* template:Main { */
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        RandomAccess_Dist::main(args);
        x10::free_args(args);
        return x10::exitCode;
    }
}
// the original app-main method

/* } */void RandomAccess_Dist::main(const x10::ref<x10::array<x10::ref<String> > > args) {
if (__here__ != 0) goto SKIP_s1;
    
//#line 251 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s1: ;
    bool cond10;
    if (__here__ != 0) goto SKIP_c10;
    cond10 = (NUMPLACES & (NUMPLACES - 1)) > 0;
    CS = cond10?1:2;
    if (!cond10) goto SKIP_TO_END_OF_c10;
    SKIP_c10: ;
    if (__here__ != 0) goto SKIP_s2;
    {
        
//#line 252 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::out->println(String("the number of places must be a power of 2!"));
        
//#line 253 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::exit(-1);
    SKIP_s2: ;
    }
    SKIP_TO_END_OF_c10: ;
    if (__here__ != 0) goto SKIP_s3;
    
//#line 304 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s3: ;
    GLOBAL_STATE.VERIFY = UPDATE;
    const x10_int VERIFY = GLOBAL_STATE.VERIFY;
    if (__here__ != 0) goto SKIP_s4;
    
//#line 305 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s4: ;
    GLOBAL_STATE.doIO = true;
    const x10_boolean doIO = GLOBAL_STATE.doIO;
    if (__here__ != 0) goto SKIP_s5;
    
//#line 306 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s5: ;
    GLOBAL_STATE.embarrassing = false;
    const x10_boolean embarrassing = GLOBAL_STATE.embarrassing;
    if (__here__ != 0) goto SKIP_s6;
    
//#line 307 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s6: ;
    GLOBAL_STATE.logTableSize = 26;
    const x10_int logTableSize = GLOBAL_STATE.logTableSize;
    if (__here__ != 0) goto SKIP_s7;
    
//#line 310 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s7: ;
    GLOBAL_STATE.tableSize = 1 << logTableSize;
    const x10_long tableSize = GLOBAL_STATE.tableSize;
    if (__here__ != 0) goto SKIP_s8;
    
//#line 312 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s8: ;
    GLOBAL_STATE.numUpdates = tableSize * 4 * NUMPLACES;
    const x10_long numUpdates = GLOBAL_STATE.numUpdates;
    if (__here__ != 0) goto SKIP_s9;
    
//#line 314 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s9: ;
    x10::finish_start(-1);
    GLOBAL_STATE.Table = __init__1(__init__1_args(tableSize).ptr(),
    new (x10::alloc<_point<1> >()) _point<1>(__here__))
    ;
    const x10::ref<RandomAccess_Dist::localTable> Table =
      GLOBAL_STATE.Table;
    x10::finish_end(EXCEPTION);
    if (__here__ != 0) goto SKIP_s10;
    
//#line 320 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s10: ;
    x10_double GUPs;
    
//#line 321 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    x10_double cputime;
    
//#line 324 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    bool cond12;
    if (__here__ != 0) goto SKIP_c12;
    cond12 = doIO;
    CS = cond12?1:2;
    if (!cond12) goto SKIP_TO_END_OF_c12;
    SKIP_c12: ;
    if (__here__ != 0) goto SKIP_s11;
    {
        
//#line 326 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::out->println(String("Distributed table size   = 2^") +
                             logTableSize +
                             String("*") +
                             NUMPLACES + String(" = ") +
                             tableSize *
                               NUMPLACES + String(" words"));
        
//#line 329 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::out->println(String("Number of total updates = ") +
                             (4 *
                                tableSize *
                                NUMPLACES) + String(""));
    SKIP_s11: ;
    }
    SKIP_TO_END_OF_c12: ;
    if (__here__ != 0) goto SKIP_s12;
    
//#line 333 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    cputime = -mysecond()
    ;
    
//#line 347 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    System::out->println(String("The mode of update (0 = update; 1 = parallel verification; 2 = update and sequential verification): ") +
                         VERIFY);
    
//#line 350 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s12: ;
    GLOBAL_STATE.LogTableSize = logTableSize;
    const x10_long LogTableSize = GLOBAL_STATE.LogTableSize;
    if (__here__ != 0) goto SKIP_s13;
    
//#line 352 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s13: ;
    GLOBAL_STATE.Embarrassing = embarrassing;
    const x10_boolean Embarrassing = GLOBAL_STATE.Embarrassing;
    if (__here__ != 0) goto SKIP_s14;
    
//#line 354 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s14: ;
    GLOBAL_STATE.NumUpdates = tableSize *
      4;
    const x10_long NumUpdates = GLOBAL_STATE.NumUpdates;
    if (__here__ != 0) goto SKIP_s15;
    
//#line 357 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s15: ;
    bool cond14;
    if (__here__ != 0) goto SKIP_c14;
    cond14 = VERIFY == VERIFICATION_P;
    CS = cond14?1:2;
    if (!cond14) goto SKIP_c15;
    SKIP_c14: ;
    if (__here__ != 0) goto SKIP_s16;
    {
        
//#line 359 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s16: ;
        CS = x10::finish_start(CS); // finish#1
        if (1 != CS) goto SKIP_1;
        try {
        if (__here__ != 0) goto SKIP_s17;
            
//#line 359 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            SKIP_s17: ;
            const x10::ref<point> __var0__ = new (x10::alloc<_point<1> >()) _point<1>(__here__);
            
//#line 359 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            x10_int p;
            p = __var0__->operator[](0);
            {
                
//#line 360 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                x10_long ran;
                ran = HPCC_starts(p * NumUpdates);
                
//#line 361 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                for (
//#line 361 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                     x10_long i = 0; i < NumUpdates;
                     
//#line 361 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                     i++) {
                {
                    
//#line 362 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    x10_int placeID;
                    
//#line 363 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    if (Embarrassing) {
                        
//#line 364 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                        placeID = p
                        ;
                    } else {
                        
//#line 366 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                        placeID = (x10_int)
                                    ((ran >>
                                        LogTableSize) &
                                       PLACEIDMASK)
                        ;
                    }
                    
//#line 367 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    x10_long temp = ran;
                    
//#line 369 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    async_invocation(0, placeID, (temp));
                    
//#line 371 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    ran = (ran << 1) ^ ((x10_long)
                                          ran <
                                          0
                                          ? POLY
                                          : 0)
                    ;
                }
                }
            }
            if (__here__ != 0) goto SKIP_s18;
            
        SKIP_s18: ;
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10lib::asyncFlush(0, sizeof(async__0_args));
        x10::finish_end(EXCEPTION); // finish#1
        CS = 0;
        SKIP_1: ;
        if (__here__ != 0) goto SKIP_s19;
        
    SKIP_s19: ;
    }
    if (__here__ == 0) goto SKIP_TO_END_OF_c14;
    SKIP_c15: ;
    
    if (__here__ != 0) goto SKIP_s20;
    {
        
//#line 375 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s20: ;
        CS = x10::finish_start(CS); // finish#2
        if (2 != CS) goto SKIP_2;
        try {
        if (__here__ != 0) goto SKIP_s21;
            
//#line 375 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            SKIP_s21: ;
            const x10::ref<point> __var1__ = new (x10::alloc<_point<1> >()) _point<1>(__here__);
            
//#line 375 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            x10_int p;
            p = __var1__->operator[](0);
            {
                
//#line 377 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                x10_long ran;
                ran = HPCC_starts(p * NumUpdates);
                
//#line 378 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                for (
//#line 378 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                     x10_long i = 0; i < NumUpdates;
                     
//#line 378 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                     i++) {
                {
                    
//#line 380 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    x10_int placeID;
                    
//#line 381 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    if (Embarrassing) {
                        
//#line 382 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                        placeID = p
                        ;
                    } else {
                        
//#line 384 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                        placeID = (x10_int)
                                    ((ran >>
                                        LogTableSize) &
                                       PLACEIDMASK)
                        ;
                    }
                    
//#line 386 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    x10_long temp = ran;
                    
//#line 389 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    async_invocation(1, placeID, (temp));
                    
//#line 390 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    ran = (ran << 1) ^ ((x10_long)
                                          ran <
                                          0
                                          ? POLY
                                          : 0)
                    ;
                }
                }
            }
            if (__here__ != 0) goto SKIP_s22;
            
        SKIP_s22: ;
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10lib::asyncFlush(1, sizeof(async__1_args));
        x10::finish_end(EXCEPTION); // finish#2
        CS = 0;
        SKIP_2: ;
        if (__here__ != 0) goto SKIP_s23;
        
    SKIP_s23: ;
    }
    SKIP_TO_END_OF_c14: ;
    if (__here__ != 0) goto SKIP_s24;
    
//#line 396 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    cputime += mysecond()
    ;
    
//#line 399 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s24: ;
    bool cond20;
    if (__here__ != 0) goto SKIP_c20;
    cond20 = VERIFY == UPDATE_AND_VERIFICATION;
    CS = cond20?3:4;
    if (!cond20) goto SKIP_TO_END_OF_c20;
    SKIP_c20: ;
    if (__here__ != 0) goto SKIP_s25;
    {
        
//#line 402 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::out->println(String("Verifying result by repeating the update sequentially..."));
        
//#line 404 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s25: ;
        CS = x10::finish_start(CS); // finish#3
        if (3 != CS) goto SKIP_3;
        try {
        if (__here__ != 0) goto SKIP_s26;
            
//#line 404 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            {
                Iterator<point>& __i__var2__ = (UNIQUE->
                                                  region).iterator();
                for (; __i__var2__.hasNext();
                       ) {
                    const x10::ref<point> __var2__ = &__i__var2__.next();
                    
//#line 404 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    x10_int p = __var2__->operator[](0);
                     {
                    {
                        
//#line 405 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                        x10_long ran = HPCC_starts(
                                         p *
                                           NumUpdates);
                        
//#line 406 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                        for (
//#line 406 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                             x10_long i =
                               0; i < NumUpdates;
                             
//#line 406 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                             i++) {
                        {
                            
//#line 407 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                            x10_int placeID;
                            
//#line 408 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                            if (Embarrassing)
                            {
                                
//#line 409 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                                placeID =
                                  p
                                ;
                            } else {
                                
//#line 411 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                                placeID =
                                  (x10_int)
                                    ((ran >>
                                        LogTableSize) &
                                       PLACEIDMASK)
                                ;
                            }
                            
//#line 412 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                            x10_long temp =
                              ran;
                            
//#line 414 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                            async_invocation(2, placeID, (temp));
                            
//#line 417 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                            ran = (ran <<
                                     1) ^
                              ((x10_long)
                                 ran < 0 ? POLY
                                 : 0)
                            ;
                        }
                        }
                    }
                    }
                }
                x10::dealloc(&__i__var2__);
                
            }
            
        SKIP_s26: ;
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10lib::asyncFlush(2, sizeof(async__2_args));
        x10::finish_end(EXCEPTION); // finish#3
        CS = 0;
        SKIP_3: ;
        if (__here__ != 0) goto SKIP_s27;
        
    SKIP_s27: ;
    }
    SKIP_TO_END_OF_c20: ;
    if (__here__ != 0) goto SKIP_s28;
    
//#line 424 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    GUPs = (cputime > 0.0 ? 1.0 / cputime
              : -1.0)
    ;
    
//#line 425 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    GUPs *= 1.0E-9 * (4 * tableSize * NUMPLACES)
    ;
    
//#line 427 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s28: ;
    bool cond24;
    if (__here__ != 0) goto SKIP_c24;
    cond24 = doIO;
    CS = cond24?4:5;
    if (!cond24) goto SKIP_TO_END_OF_c24;
    SKIP_c24: ;
    if (__here__ != 0) goto SKIP_s29;
    {
        
//#line 429 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::out->println(String("    CPU time used  = ") +
                             cputime +
                             String(" seconds"));
    SKIP_s29: ;
    }
    SKIP_TO_END_OF_c24: ;
    if (__here__ != 0) goto SKIP_s30;
    
//#line 431 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s30: ;
    bool cond26;
    if (__here__ != 0) goto SKIP_c26;
    cond26 = VERIFY == UPDATE;
    CS = cond26?4:5;
    if (!cond26) goto SKIP_TO_END_OF_c26;
    SKIP_c26: ;
    if (__here__ != 0) goto SKIP_s31;
    {
        
//#line 431 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        System::out->println(String("    ") +
                             GUPs +
                             String(" Billion(10^9) Updates    per second [GUP/s]"));
    SKIP_s31: ;
    }
    SKIP_TO_END_OF_c26: ;
    if (__here__ != 0) goto SKIP_s32;
    
//#line 435 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
    SKIP_s32: ;
    bool cond28;
    if (__here__ != 0) goto SKIP_c28;
    cond28 = VERIFY > 0;
    CS = cond28?4:5;
    if (!cond28) goto SKIP_TO_END_OF_c28;
    SKIP_c28: ;
    if (__here__ != 0) goto SKIP_s33;
    {
        
//#line 437 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s33: ;
        GLOBAL_STATE.SUM = x10::alloc_array<x10_long>(NUMPLACES);
        const x10::ref<x10::array<x10_long> > SUM =
          GLOBAL_STATE.SUM;
        if (__here__ != 0) goto SKIP_s34;
        
//#line 438 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s34: ;
        CS = x10::finish_start(CS); // finish#4
        if (4 != CS) goto SKIP_4;
        try {
        if (__here__ != 0) goto SKIP_s35;
            
//#line 438 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            SKIP_s35: ;
            const x10::ref<point> __var3__ = new (x10::alloc<_point<1> >()) _point<1>(__here__);
            
//#line 438 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            x10_int p;
            p = __var3__->operator[](0);
            {
                
//#line 439 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                x10_long sum;
                sum = 0;
                
//#line 441 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                for (
//#line 441 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                     x10_int i = 0; i < tableSize;
                     
//#line 441 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                     i++) {
                {
                    
//#line 441 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                    sum += Table->array->_data[i]
                    ;
                }
                }
                
//#line 443 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                x10_long temp;
                temp = sum;
                
//#line 444 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
                async_invocation(3, 0, (p,
                                          temp));
            }
            if (__here__ != 0) goto SKIP_s36;
            
        SKIP_s36: ;
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10lib::asyncFlush(3, sizeof(async__3_args));
        x10::finish_end(EXCEPTION); // finish#4
        CS = 0;
        SKIP_4: ;
        if (__here__ != 0) goto SKIP_s37;
        
//#line 446 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s37: ;
        x10_long globalSum;
        if (__here__ != 0) goto SKIP_s38;
        globalSum = 0;
        
//#line 447 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        for (
//#line 447 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
             x10_int i = 0; i < NUMPLACES;
             
//#line 447 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
             i++) {
        {
            
//#line 447 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            globalSum += SUM->_data[i]
            ;
        }
        }
        
//#line 448 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
        SKIP_s38: ;
        bool cond30;
        if (__here__ != 0) goto SKIP_c30;
        cond30 = VERIFY == VERIFICATION_P;
        CS = cond30?5:6;
        if (!cond30) goto SKIP_c31;
        SKIP_c30: ;
        if (__here__ != 0) goto SKIP_s39;
        {
            
//#line 450 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            SKIP_s39: ;
            x10_double missedUpdateRate;
            if (__here__ != 0) goto SKIP_s40;
            missedUpdateRate = (globalSum -
                                  numUpdates) /
              (x10_double)
                numUpdates * 100;
            
//#line 451 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            System::out->println(String("    The rate of missed updates  ") +
                                 missedUpdateRate +
                                 String("%"));
        SKIP_s40: ;
        }
        if (__here__ == 0) goto SKIP_TO_END_OF_c30;
        SKIP_c31: ;
        
        if (__here__ != 0) goto SKIP_s41;
        {
            
//#line 454 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
            System::out->println(String("    The global sum is ") +
                                 globalSum +
                                 String(" (correct=0)"));
        SKIP_s41: ;
        }
        SKIP_TO_END_OF_c30: ;
        if (__here__ != 0) goto SKIP_s42;
        
    SKIP_s42: ;
    }
    SKIP_TO_END_OF_c28: ;
    if (__here__ != 0) goto SKIP_s43;
    
SKIP_s43: ;
}
//#line 65 "C:\VP\X10\SourceForge\x10.backend\examples\RandomAccess_Dist.x10"
RandomAccess_Dist::RandomAccess_Dist() : x10::lang::Object() { }
