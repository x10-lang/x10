
#include "RandomAccessNoSPMD_Dist.h"
#include <stdlib.h>
using namespace x10;

#include "RandomAccess_Dist.inc"
#define printStart(str) //cerr<<"entering " << str  << " here = " << __here__ <<endl;
#define printEnd(str) //cerr<<"leaving " << str  << " here = " << __here__ <<endl;
static x10::ref<Exception> EXCEPTION = NULL;

//#line 88 "RandomAccess_Dist.x10"
x10_double RandomAccess_Dist::mysecond()
{
 {
    
//#line 89 "RandomAccess_Dist.x10"
    return (x10_double) ((x10_double) (System::nanoTime() / 1000) * 1.0E-6);
    
}
}

//#line 92 "RandomAccess_Dist.x10"
x10_long RandomAccess_Dist::x10__POLY;

//#line 93 "RandomAccess_Dist.x10"
x10_long RandomAccess_Dist::x10__PERIOD;

//#line 95 "RandomAccess_Dist.x10"
x10::ref<x10::lang::dist> RandomAccess_Dist::x10__UNIQUE;

//#line 96 "RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::x10__NUMPLACES;

//#line 97 "RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::x10__PLACEIDMASK;

//#line 103 "RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::x10__UPDATE;

//#line 104 "RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::x10__VERIFICATION_P;

//#line 105 "RandomAccess_Dist.x10"
x10_int RandomAccess_Dist::x10__UPDATE_AND_VERIFICATION;

//x10::ref<RandomAccess_Dist::localTable> RandomAccess_Dist::Table;
//x10::ref<x10::array<x10_long> > RandomAccess_Dist::SUM;
//#line 110 "RandomAccess_Dist.x10"
x10_long RandomAccess_Dist::HPCC_starts(x10_long n)
{
 {
    
//#line 111 "RandomAccess_Dist.x10"
    x10_int i;
    
//#line 111 "RandomAccess_Dist.x10"
    x10_int j;
    
//#line 112 "RandomAccess_Dist.x10"
    x10::ref<x10::array<x10_long> > m2 = x10::alloc_array<x10_long >(64);
    
//#line 113 "RandomAccess_Dist.x10"
    x10_long temp;
    
//#line 113 "RandomAccess_Dist.x10"
    x10_long ran;
    
//#line 115 "RandomAccess_Dist.x10"
    while (n < 0) 
    
//#line 115 "RandomAccess_Dist.x10"
    n += RandomAccess_Dist::x10__PERIOD;
    
//#line 116 "RandomAccess_Dist.x10"
    while (n > RandomAccess_Dist::x10__PERIOD) 
    
//#line 116 "RandomAccess_Dist.x10"
    n -= RandomAccess_Dist::x10__PERIOD;
    
//#line 117 "RandomAccess_Dist.x10"
    if (n == 0) {
        
//#line 117 "RandomAccess_Dist.x10"
        return 1;
        
    }
    
//#line 119 "RandomAccess_Dist.x10"
    temp = 1;
    
//#line 120 "RandomAccess_Dist.x10"
    {
        for (
//#line 120 "RandomAccess_Dist.x10"
             i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                            i++){
         
        {
            
//#line 121 "RandomAccess_Dist.x10"
            m2->_data[i] = temp;
            
//#line 122 "RandomAccess_Dist.x10"
            temp = (temp << 1) ^ ((x10_long) temp < 0 ? RandomAccess_Dist::x10__POLY
                                    : 0);
            
//#line 123 "RandomAccess_Dist.x10"
            temp = (temp << 1) ^ ((x10_long) temp < 0 ? RandomAccess_Dist::x10__POLY
                                    : 0);
        }
        
    }
    }
    
//#line 126 "RandomAccess_Dist.x10"
    {
        for (
//#line 126 "RandomAccess_Dist.x10"
             i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                             i--){
         
        {
            
//#line 127 "RandomAccess_Dist.x10"
            if (((n >> i) & 1) != 0) {
                
//#line 128 "RandomAccess_Dist.x10"
                break;
                
            }
            
        }
        
    }
    }
    
//#line 130 "RandomAccess_Dist.x10"
    ran = 2;
    
//#line 131 "RandomAccess_Dist.x10"
    while (i > 0) 
    {
        
//#line 132 "RandomAccess_Dist.x10"
        temp = 0;
        
//#line 133 "RandomAccess_Dist.x10"
        {
            for (
//#line 133 "RandomAccess_Dist.x10"
                 j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                j++){
             
            {
                
//#line 134 "RandomAccess_Dist.x10"
                if (((ran >> j) & 1) != 0) {
                    
//#line 135 "RandomAccess_Dist.x10"
                    temp ^= m2->_data[j];
                }
                
            }
            
        }
        }
        
//#line 136 "RandomAccess_Dist.x10"
        ran = temp;
        
//#line 137 "RandomAccess_Dist.x10"
        i -= 1;
        
//#line 138 "RandomAccess_Dist.x10"
        if (((n >> i) & 1) != 0) {
            
//#line 139 "RandomAccess_Dist.x10"
            ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                  : 0);
        }
        
    }
    
//#line 142 "RandomAccess_Dist.x10"
    return ran;
    
}
}

//#line 247 "RandomAccess_Dist.x10"
/* template:Main { */
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
        if (__here__ == 0) 
            RandomAccess_Dist::main(args);
        else
            RandomAccess_Dist::main_np0();
        } catch(int exitCode) {
            x10::exitCode = exitCode;
        } catch(x10::__ref& e) {
            fprintf(stderr, "%d: ", (int)__here__);
            //fprintf(stderr, "Caught %p\n", ((const x10::ref<Exception>&)e)._val);
            ((const x10::ref<Exception>&)e)->printStackTrace(System::x10__out);
            x10::exitCode = 1;
        } catch(...) {
            fprintf(stderr, "%d: Caught exception\n", (int)__here__);
            x10::exitCode = 1;
        }
        x10::free_args(args);
        return x10::exitCode;
    }
}
// the original app-main method
/* } */
#define IF_COND_THEN 0
#define IF_COND_ELSE 1
#define IF_COND_THEN2 2
#define IF_COND_THEN3 3
#define CCODE1 4
#define F_START 5
#define F_END 6
typedef void (*pt2Function)();
pt2Function fp[]= {
&RandomAccess_Dist::if_cond_then,
&RandomAccess_Dist::if_cond_else,
&RandomAccess_Dist::if_cond_then2,
&RandomAccess_Dist::if_cond_then3,
&RandomAccess_Dist::ccode1,
&RandomAccess_Dist::f_start,
&RandomAccess_Dist::f_end
};
struct Comm {

        int type;
        union{
                struct {
                        int offset;
			x10_long val;
                } initLong;
                struct {
                        int offset;
			int val;
                } initInt;
                struct {
			int index;

                } code;
                struct{
                        int retCode;
                } terminate;
        } body;
};
#define GINIT_INT 0
#define GINIT_LONG 1
#define CODE 2
#define TERM 3
int RandomAccess_Dist::main_np0() {
        Comm c;
        while (1){
                x10lib::Broadcast(&c, sizeof(c));
                switch (c.type) {

                        case GINIT_LONG: 
                                GLOBAL_STATE.updateLong(c.body.initLong.offset, 
						    c.body.initLong.val);
                                break;
                        case GINIT_INT: 
                                GLOBAL_STATE.updateInt(c.body.initInt.offset, 
						    c.body.initInt.val);
                                break;
                        case CODE: (*fp[c.body.code.index])(); break;
                        case TERM: return c.body.terminate.retCode;
                }
        }
}
void create_func(Comm &c, int index ) {
        c.type=CODE;
        c.body.code.index = index;               
        return;
}
void create_term(Comm &c, int retc) {
                
        c.type = TERM;
        c.body.terminate.retCode=retc;
        return;

}
void create_initLong(Comm &c, int offset, x10_long val ) {
        c.type=GINIT_LONG;
        c.body.initLong.offset = offset;
        c.body.initLong.val = val;
        return;
}
void create_initInt(Comm &c, int offset, int val ) {
        c.type=GINIT_INT;
        c.body.initInt.offset = offset;
        c.body.initInt.val = val;
        return;
}




//#line 65 "RandomAccess_Dist.x10"
RandomAccess_Dist::RandomAccess_Dist() : x10::lang::Object()
{
    
}
void* RandomAccess_Dist::__static_init() {
    x10__POLY = 7;
    x10__PERIOD = 1317624576693539401ll;
    x10__UNIQUE = dist::x10__UNIQUE;
    x10__NUMPLACES = place::x10__MAX_PLACES;
    x10__PLACEIDMASK = RandomAccess_Dist::x10__NUMPLACES - 1;
    x10__UPDATE = 0;
    x10__VERIFICATION_P = 1;
    x10__UPDATE_AND_VERIFICATION = 2;
    return NULL;
}
static void* __init__ = RandomAccess_Dist::__static_init();

struct RandomAccess_Dist::_GLOBAL_STATE RandomAccess_Dist::GLOBAL_STATE;


//#line 68 "RandomAccess_Dist.x10"

//#line 69 "RandomAccess_Dist.x10"

//#line 70 "RandomAccess_Dist.x10"

//#line 71 "RandomAccess_Dist.x10"
RandomAccess_Dist::localTable::localTable(x10_long size) : x10::lang::Object()
{
   x10_double  
//#line 72 "RandomAccess_Dist.x10"
    x10__tableSize = size;
    
//#line 73 "RandomAccess_Dist.x10"
    x10__mask = x10__tableSize - 1;
    
//#line 74 "RandomAccess_Dist.x10"
    x10__array = (x10::ref<x10::x10array<x10_long> >) (x10::x10newArray<x10_long>(((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(
                                                                                                             0,
                                                                                                             (x10_int)
                                                                                                               x10__mask)))->toDistribution()));
    
}

//#line 78 "RandomAccess_Dist.x10"
void RandomAccess_Dist::localTable::update(x10_long ran)
{
 {
    
//#line 79 "RandomAccess_Dist.x10"
    x10__array->rawRegion()[(x10_int) (ran & x10__mask)] ^= ran;
}
}

//#line 83 "RandomAccess_Dist.x10"
void RandomAccess_Dist::localTable::verify(x10_long ran)
{
 {
    
//#line 84 "RandomAccess_Dist.x10"
    (x10__array->rawRegion()[(x10_int) (ran & x10__mask)])++;
}
}
void* ArrayCopySwitch(x10_async_handler_t h, void* __arg) {
    switch (h) {
        
    }
return NULL;
}
void AsyncSwitch(x10_async_handler_t h, void* arg, int niter) {
    switch (h) {
        case 0:
        case 1:
        case 2:
        case 3:
            RandomAccess_Dist::AsyncSwitch(h, arg, niter);
            break;
        
    }
}
/*
void RandomAccess_Dist::if_cond_else() {
{
       printStart("else"); 
//#line 375 "RandomAccess_Dist.x10"
        CS = x10::finish_start(-1);//finish# 2
        try {
            
//#line 375 "RandomAccess_Dist.x10"
            x10::ref<point> __var1__ = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
            
//#line 375 "RandomAccess_Dist.x10"
            x10_int p;
            p = __var1__->operator[](0);
            {
                
//#line 377 "RandomAccess_Dist.x10"
                x10_long ran;
                x10_long __returnVar__4;
                { // Inlined RandomAccess_Dist.HPCC_starts(long)
                    x10_long __var5__ = p * RandomAccess_Dist::GLOBAL_STATE.NumUpdates;
                    x10_long n = __var5__;
                    {
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int i;
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int j;
                        
//#line 112 "RandomAccess_Dist.x10"
                        x10::ref<x10::array<x10_long> > m2;
                        m2 = x10::alloc_array<x10_long >(64);
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long temp;
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long ran;
                        
//#line 115 "RandomAccess_Dist.x10"
                        while (n < 0) 
                        
//#line 115 "RandomAccess_Dist.x10"
                        n += RandomAccess_Dist::x10__PERIOD;
                        
//#line 116 "RandomAccess_Dist.x10"
                        while (n > RandomAccess_Dist::x10__PERIOD) 
                        
//#line 116 "RandomAccess_Dist.x10"
                        n -= RandomAccess_Dist::x10__PERIOD;
                        
//#line 117 "RandomAccess_Dist.x10"
                        if (n == 0) {
                            
//#line 117 "RandomAccess_Dist.x10"
                            __returnVar__4 = 1;
                            goto RETURN_3;
                            
                        }
                        
//#line 119 "RandomAccess_Dist.x10"
                        temp = 1;
                        
//#line 120 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 120 "RandomAccess_Dist.x10"
                                 i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                                                i++){
                             
                            {
                                
//#line 121 "RandomAccess_Dist.x10"
                                m2->_data[i] = temp;
                                
//#line 122 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                                
//#line 123 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                            }
                            
                        }
                        }
                        
//#line 126 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 126 "RandomAccess_Dist.x10"
                                 i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                                                 i--){
                             
                            {
                                
//#line 127 "RandomAccess_Dist.x10"
                                if (((n >> i) & 1) != 0) {
                                    
//#line 128 "RandomAccess_Dist.x10"
                                    break;
                                    
                                }
                                
                            }
                            
                        }
                        }
                        
//#line 130 "RandomAccess_Dist.x10"
                        ran = 2;
                        
//#line 131 "RandomAccess_Dist.x10"
                        while (i > 0) 
                        {
                            
//#line 132 "RandomAccess_Dist.x10"
                            temp = 0;
                            
//#line 133 "RandomAccess_Dist.x10"
                            {
                                for (
//#line 133 "RandomAccess_Dist.x10"
                                     j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                                    j++){
                                 
                                {
                                    
//#line 134 "RandomAccess_Dist.x10"
                                    if (((ran >> j) & 1) != 0) {
                                        
//#line 135 "RandomAccess_Dist.x10"
                                        temp ^= m2->_data[j];
                                    }
                                    
                                }
                                
                            }
                            }
                            
//#line 136 "RandomAccess_Dist.x10"
                            ran = temp;
                            
//#line 137 "RandomAccess_Dist.x10"
                            i -= 1;
                            
//#line 138 "RandomAccess_Dist.x10"
                            if (((n >> i) & 1) != 0) {
                                
//#line 139 "RandomAccess_Dist.x10"
                                ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                                      : 0);
                            }
                            
                        }
                        
//#line 142 "RandomAccess_Dist.x10"
                        __returnVar__4 = ran;
                        goto RETURN_3;
                        
                    }
                }
                RETURN_3: ;
                ran = __returnVar__4;
printStart("Starting for loop");
                
//#line 378 "RandomAccess_Dist.x10"
                {
                    x10_long i;
                    for (
//#line 378 "RandomAccess_Dist.x10"
                         i = 0; i < RandomAccess_Dist::GLOBAL_STATE.NumUpdates; 
//#line 378 "RandomAccess_Dist.x10"
                                                i++){
                     
                    {
                        
//#line 380 "RandomAccess_Dist.x10"
                        x10_int placeID;
                        
//#line 381 "RandomAccess_Dist.x10"
                        if (RandomAccess_Dist::GLOBAL_STATE.Embarrassing) {
                            
//#line 382 "RandomAccess_Dist.x10"
                            placeID = p;
                        } else {
                            
//#line 384 "RandomAccess_Dist.x10"
                            placeID = (x10_int) ((ran >> RandomAccess_Dist::GLOBAL_STATE.LogTableSize) & RandomAccess_Dist::x10__PLACEIDMASK);
                        }
                        
//#line 386 "RandomAccess_Dist.x10"
                        x10_long temp;
                        temp = ran;
                        
//#line 389 "RandomAccess_Dist.x10"
//if (__here__ == 0 && i % 1000000  == 0)
//cerr << temp << endl;
                        async_invocation(RandomAccess_Dist, 0, (((x10::lang::place)placeID))->x10__id, (temp));
                        
//#line 390 "RandomAccess_Dist.x10"
                        ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                              : 0);
                    }
                    
                }
                }
                
            }
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
printStart("before finish");
        x10::finish_end(EXCEPTION);// finish#2
printEnd("after finish");
        CS = 0;
        
    }
printEnd("else");
}
*/
/*
void RandomAccess_Dist::if_cond_then() {
printStart ("then");
//cerr << "Numupdates = " << RandomAccess_Dist::GLOBAL_STATE.NumUpdates << endl << "Embarassing = " << RandomAccess_Dist::GLOBAL_STATE.Embarrassing << endl;
{
        
//#line 359 "RandomAccess_Dist.x10"
        CS = 1;
        CS = x10::finish_start(CS);//finish# 1
        try {
            
//#line 359 "RandomAccess_Dist.x10"
            x10::ref<point> __var0__ = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
            
//#line 359 "RandomAccess_Dist.x10"
            x10_int p;
            p = __var0__->operator[](0);
            {
                
//#line 360 "RandomAccess_Dist.x10"
                x10_long ran;
                x10_long __returnVar__1;
                { // Inlined RandomAccess_Dist.HPCC_starts(long)
                    x10_long __var2__ = p * RandomAccess_Dist::GLOBAL_STATE.NumUpdates;
                    x10_long n = __var2__;
                    {
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int i;
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int j;
                        
//#line 112 "RandomAccess_Dist.x10"
                        x10::ref<x10::array<x10_long> > m2;
                        m2 = x10::alloc_array<x10_long >(64);
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long temp;
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long ran;
                        
//#line 115 "RandomAccess_Dist.x10"
                        while (n < 0) 
                        
//#line 115 "RandomAccess_Dist.x10"
                        n += RandomAccess_Dist::x10__PERIOD;
                        
//#line 116 "RandomAccess_Dist.x10"
                        while (n > RandomAccess_Dist::x10__PERIOD) 
                        
//#line 116 "RandomAccess_Dist.x10"
                        n -= RandomAccess_Dist::x10__PERIOD;
                        
//#line 117 "RandomAccess_Dist.x10"
                        if (n == 0) {
                            
//#line 117 "RandomAccess_Dist.x10"
                            __returnVar__1 = 1;
                            goto RETURN_0;
                            
                        }
                        
//#line 119 "RandomAccess_Dist.x10"
                        temp = 1;
                        
//#line 120 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 120 "RandomAccess_Dist.x10"
                                 i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                                                i++){
                             
                            {
                                
//#line 121 "RandomAccess_Dist.x10"
                                m2->_data[i] = temp;
                                
//#line 122 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                                
//#line 123 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                            }
                            
                        }
                        }
                        
//#line 126 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 126 "RandomAccess_Dist.x10"
                                 i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                                                 i--){
                             
                            {
                                
//#line 127 "RandomAccess_Dist.x10"
                                if (((n >> i) & 1) != 0) {
                                    
//#line 128 "RandomAccess_Dist.x10"
                                    break;
                                    
                                }
                                
                            }
                            
                        }
                        }
                        
//#line 130 "RandomAccess_Dist.x10"
                        ran = 2;
                        
//#line 131 "RandomAccess_Dist.x10"
                        while (i > 0) 
                        {
                            
//#line 132 "RandomAccess_Dist.x10"
                            temp = 0;
                            
//#line 133 "RandomAccess_Dist.x10"
                            {
                                for (
//#line 133 "RandomAccess_Dist.x10"
                                     j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                                    j++){
                                 
                                {
                                    
//#line 134 "RandomAccess_Dist.x10"
                                    if (((ran >> j) & 1) != 0) {
                                        
//#line 135 "RandomAccess_Dist.x10"
                                        temp ^= m2->_data[j];
                                    }
                                    
                                }
                                
                            }
                            }
                            
//#line 136 "RandomAccess_Dist.x10"
                            ran = temp;
                            
//#line 137 "RandomAccess_Dist.x10"
                            i -= 1;
                            
//#line 138 "RandomAccess_Dist.x10"
                            if (((n >> i) & 1) != 0) {
                                
//#line 139 "RandomAccess_Dist.x10"
                                ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                                      : 0);
                            }
                            
                        }
                        
//#line 142 "RandomAccess_Dist.x10"
                        __returnVar__1 = ran;
                        goto RETURN_0;
                        
                    }
                }
                RETURN_0: ;
                ran = __returnVar__1;
                
//#line 361 "RandomAccess_Dist.x10"
                {
                    x10_long i;
                    for (
//#line 361 "RandomAccess_Dist.x10"
                         i = 0; i < RandomAccess_Dist::GLOBAL_STATE.NumUpdates; 
//#line 361 "RandomAccess_Dist.x10"
                                                i++){
                     
                    {
                        
//#line 362 "RandomAccess_Dist.x10"
                        x10_int placeID;
                        
//#line 363 "RandomAccess_Dist.x10"
                        if (RandomAccess_Dist::GLOBAL_STATE.Embarrassing) {
                            
//#line 364 "RandomAccess_Dist.x10"
                            placeID = p;
                        } else {
                            
//#line 366 "RandomAccess_Dist.x10"
                            placeID = (x10_int) ((ran >> RandomAccess_Dist::GLOBAL_STATE.LogTableSize) & RandomAccess_Dist::x10__PLACEIDMASK);
                        }
                        
//#line 367 "RandomAccess_Dist.x10"
                        x10_long temp;
                        temp = ran;
                        
//#line 369 "RandomAccess_Dist.x10"
                        async_invocation(RandomAccess_Dist, 1, (((x10::lang::place)placeID))->x10__id, (temp));
                        
//#line 371 "RandomAccess_Dist.x10"
                        ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                              : 0);
                    }
                    
                }
                }
                
            }
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10::finish_end(EXCEPTION);// finish#1
        CS = 0;
        
    }
printEnd ("then");


}
*/
void RandomAccess_Dist::ccode1(){
printStart ("ccode1");
            x10::finish_start(-1);
            RandomAccess_Dist::GLOBAL_STATE.Table = __init__0(__init__0_args().ptr(),
                                                                      (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__)))
            ; // SPMD Global
            x10::finish_end(EXCEPTION);
printEnd ("ccode1");
}
void RandomAccess_Dist::if_cond_then3()
  {      
//#line 402 "RandomAccess_Dist.x10"
printStart ("then3");
        
//#line 404 "RandomAccess_Dist.x10"
        CS = x10::finish_start(CS);//finish# 3
        if (3 != CS) goto SKIP_3;
        try {
            
//#line 404 "RandomAccess_Dist.x10"
            {
                x10::ref<x10::lang::region> __var8__;
                x10_int __var9__;
                x10_int p;
                if (__here__ != 0) goto SKIP_s21;
                __var8__ = RandomAccess_Dist::x10__UNIQUE->x10__region;
                __var9__ = __var8__->rank(0)->high();
                for (p = __var8__->rank(0)->low(); p <= __var9__; p++) {
                    {
                        
//#line 405 "RandomAccess_Dist.x10"
                        x10_long ran;
                        SKIP_s21: ;
                        x10_long __returnVar__11;
                        { // Inlined RandomAccess_Dist.HPCC_starts(long)
                            x10_long __var12__ = p * RandomAccess_Dist::GLOBAL_STATE.NumUpdates;
                            x10_long n = __var12__;
                            {
                                
//#line 111 "RandomAccess_Dist.x10"
                                x10_int i;
                                
//#line 111 "RandomAccess_Dist.x10"
                                x10_int j;
                                
//#line 112 "RandomAccess_Dist.x10"
                                x10::ref<x10::array<x10_long> > m2;
                                m2 = x10::alloc_array<x10_long >(64);
                                
//#line 113 "RandomAccess_Dist.x10"
                                x10_long temp;
                                
//#line 113 "RandomAccess_Dist.x10"
                                x10_long ran;
                                
//#line 115 "RandomAccess_Dist.x10"
                                while (n < 0) 
                                
//#line 115 "RandomAccess_Dist.x10"
                                n += RandomAccess_Dist::x10__PERIOD;
                                
//#line 116 "RandomAccess_Dist.x10"
                                while (n > RandomAccess_Dist::x10__PERIOD)
                                
//#line 116 "RandomAccess_Dist.x10"
                                n -= RandomAccess_Dist::x10__PERIOD;
                                
//#line 117 "RandomAccess_Dist.x10"
                                if (n == 0) {
                                    
//#line 117 "RandomAccess_Dist.x10"
                                    __returnVar__11 = 1;
                                    goto RETURN_10;
                                    
                                }
                                
//#line 119 "RandomAccess_Dist.x10"
                                temp = 1;
                                
//#line 120 "RandomAccess_Dist.x10"
                                {
                                    for (
//#line 120 "RandomAccess_Dist.x10"
                                         i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                                                        i++){
                                     
                                    {
                                        
//#line 121 "RandomAccess_Dist.x10"
                                        m2->_data[i] = temp;
                                        
//#line 122 "RandomAccess_Dist.x10"
                                        temp = (temp << 1) ^ ((x10_long) temp <
                                                                0 ? RandomAccess_Dist::x10__POLY
                                                                : 0);
                                        
//#line 123 "RandomAccess_Dist.x10"
                                        temp = (temp << 1) ^ ((x10_long) temp <
                                                                0 ? RandomAccess_Dist::x10__POLY
                                                                : 0);
                                    }
                                    
                                }
                                }
                                
//#line 126 "RandomAccess_Dist.x10"
                                {
                                    for (
//#line 126 "RandomAccess_Dist.x10"
                                         i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                                                         i--){
                                     
                                    {
                                        
//#line 127 "RandomAccess_Dist.x10"
                                        if (((n >> i) & 1) != 0) {
                                            
//#line 128 "RandomAccess_Dist.x10"
                                            break;
                                            
                                        }
                                        
                                    }
                                    
                                }
                                }
                                
//#line 130 "RandomAccess_Dist.x10"
                                ran = 2;
                                
//#line 131 "RandomAccess_Dist.x10"
                                while (i > 0) 
                                {
                                    
//#line 132 "RandomAccess_Dist.x10"
                                    temp = 0;
                                    
//#line 133 "RandomAccess_Dist.x10"
                                    {
                                        for (
//#line 133 "RandomAccess_Dist.x10"
                                             j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                                            j++){
                                         
                                        {
                                            
//#line 134 "RandomAccess_Dist.x10"
                                            if (((ran >> j) & 1) != 0) {
                                                
//#line 135 "RandomAccess_Dist.x10"
                                                temp ^= m2->_data[j];
                                            }
                                            
                                        }
                                        
                                    }
                                    }
                                    
//#line 136 "RandomAccess_Dist.x10"
                                    ran = temp;
                                    
//#line 137 "RandomAccess_Dist.x10"
                                    i -= 1;
                                    
//#line 138 "RandomAccess_Dist.x10"
                                    if (((n >> i) & 1) != 0) {
                                        
//#line 139 "RandomAccess_Dist.x10"
                                        ran = (ran << 1) ^ ((x10_long) ran <
                                                              0 ? RandomAccess_Dist::x10__POLY
                                                              : 0);
                                    }
                                    
                                }
                                
//#line 142 "RandomAccess_Dist.x10"
                                __returnVar__11 = ran;
                                goto RETURN_10;
                                
                            }
                        }
                        RETURN_10: ;
                        ran = __returnVar__11;
                        
//#line 406 "RandomAccess_Dist.x10"
                        {
                            x10_long i;
                            for (
//#line 406 "RandomAccess_Dist.x10"
                                 i = 0; i < RandomAccess_Dist::GLOBAL_STATE.NumUpdates; 
//#line 406 "RandomAccess_Dist.x10"
                                                        i++){
                             
                            {
                                
//#line 407 "RandomAccess_Dist.x10"
                                x10_int placeID;
                                
//#line 408 "RandomAccess_Dist.x10"
                                if (RandomAccess_Dist::GLOBAL_STATE.Embarrassing) {
                                    
//#line 409 "RandomAccess_Dist.x10"
                                    placeID = p;
                                } else {
                                    
//#line 411 "RandomAccess_Dist.x10"
                                    placeID = (x10_int) ((ran >> RandomAccess_Dist::GLOBAL_STATE.LogTableSize) &
                                                           RandomAccess_Dist::x10__PLACEIDMASK);
                                }
                                
//#line 412 "RandomAccess_Dist.x10"
                                x10_long temp;
                                temp = ran;
                                
//if (__here__ == 0 && i % 1000000  == 0)
//cerr << temp << endl;
//#line 414 "RandomAccess_Dist.x10"
                                async_invocation(RandomAccess_Dist, 2, (((x10::lang::place)placeID))->x10__id, (temp));
                                
//#line 417 "RandomAccess_Dist.x10"
                                ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                                      : 0);
                            }
                            
                        }
                        }
                        
                    }
                }
            }
            
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10::finish_end(EXCEPTION);// finish#3
        CS = 0;
        SKIP_3: ;
        
printEnd ("then3");
    }

void RandomAccess_Dist::main(x10::ref<x10::array<x10::ref<String> > > args)
{
        Comm c;
 {
    
//#line 251 "RandomAccess_Dist.x10"
    if ((RandomAccess_Dist::x10__NUMPLACES & (RandomAccess_Dist::x10__NUMPLACES -
                                                1)) > 0) {
        
//#line 252 "RandomAccess_Dist.x10"
        System::x10__out->println(String("the number of places must be a power of 2!"));
        
//#line 253 "RandomAccess_Dist.x10"
        System::exit(-1);
    }
    
//#line 304 "RandomAccess_Dist.x10"

    //create_initInt(c, (int*)&(GLOBAL_STATE.Embarrassing) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.Embarrassing );
    //x10lib::Broadcast(&c, sizeof(c));
    x10_int VERIFY ;
if (strcmp(getenv("VERIFY"), "1"))
     VERIFY = RandomAccess_Dist::x10__VERIFICATION_P;
else
    VERIFY = RandomAccess_Dist::x10__UPDATE_AND_VERIFICATION;
    RandomAccess_Dist::GLOBAL_STATE.VERIFY = VERIFY;
    create_initInt(c, (x10_int*)&(GLOBAL_STATE.VERIFY) - (x10_int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.VERIFY);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 305 "RandomAccess_Dist.x10"
    x10_boolean doIO = true;
    RandomAccess_Dist::GLOBAL_STATE.doIO = doIO;
    create_initInt(c, (int*)&(GLOBAL_STATE.doIO) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.doIO);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 306 "RandomAccess_Dist.x10"
    x10_boolean embarrassing = false;
    RandomAccess_Dist::GLOBAL_STATE.embarrassing = embarrassing;
    create_initInt(c, (int*)&(GLOBAL_STATE.embarrassing) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.embarrassing);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 307 "RandomAccess_Dist.x10"
    x10_int logTableSize = 20;
    RandomAccess_Dist::GLOBAL_STATE.logTableSize = logTableSize;
    create_initInt(c, (int*)&(GLOBAL_STATE.logTableSize) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.logTableSize);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 310 "RandomAccess_Dist.x10"
    x10_long tableSize = 1 << logTableSize;
    RandomAccess_Dist::GLOBAL_STATE.tableSize = tableSize;
    create_initLong(c, (int*)&(GLOBAL_STATE.tableSize) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.tableSize);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 312 "RandomAccess_Dist.x10"
    x10_long numUpdates = tableSize * 4 * RandomAccess_Dist::x10__NUMPLACES;
    RandomAccess_Dist::GLOBAL_STATE.numUpdates = numUpdates;
    create_initLong(c, (int*)&(GLOBAL_STATE.numUpdates) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.numUpdates);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 314 "RandomAccess_Dist.x10"
            create_func(c, CCODE1);
            x10lib::Broadcast(&c, sizeof(c));
            ccode1();
    
//#line 320 "RandomAccess_Dist.x10"
    x10_double GUPs;
    
//#line 321 "RandomAccess_Dist.x10"
    x10_double cputime;
    
//#line 324 "RandomAccess_Dist.x10"
    if (doIO) {
        
//#line 326 "RandomAccess_Dist.x10"
        System::x10__out->println(String("Distributed table size   = 2^") +
                                  logTableSize + String("*") + RandomAccess_Dist::x10__NUMPLACES +
                                  String(" = ") + tableSize * RandomAccess_Dist::x10__NUMPLACES +
                                  String(" words"));
        
//#line 329 "RandomAccess_Dist.x10"
        System::x10__out->println(String("Number of total updates = ") + (4 *
                                                                            tableSize *
                                                                            RandomAccess_Dist::x10__NUMPLACES) +
                                  String(""));
    }
    
//#line 333 "RandomAccess_Dist.x10"
    cputime = -RandomAccess_Dist::mysecond();
    
//#line 347 "RandomAccess_Dist.x10"
    System::x10__out->println(String("The mode of update (0 = update; 1 = parallel verification; 2 = update and sequential verification): ") +
                              VERIFY);
    
//#line 350 "RandomAccess_Dist.x10"
    x10_long LogTableSize = logTableSize;
    RandomAccess_Dist::GLOBAL_STATE.LogTableSize = LogTableSize;
    create_initLong(c, (int*)&(GLOBAL_STATE.LogTableSize) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.LogTableSize);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 352 "RandomAccess_Dist.x10"
    x10_boolean Embarrassing = embarrassing;
    RandomAccess_Dist::GLOBAL_STATE.Embarrassing = Embarrassing;
    create_initInt(c, (int*)&(GLOBAL_STATE.Embarrassing) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.Embarrassing);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 354 "RandomAccess_Dist.x10"
    x10_long NumUpdates = tableSize * 4;
    RandomAccess_Dist::GLOBAL_STATE.NumUpdates = NumUpdates;
    create_initLong(c, (int*)&(GLOBAL_STATE.NumUpdates) - (int*)&(GLOBAL_STATE), RandomAccess_Dist::GLOBAL_STATE.NumUpdates);
    x10lib::Broadcast(&c, sizeof(c));
    
//#line 357 "RandomAccess_Dist.x10"
    bool cond14;
    cond14 = VERIFY == RandomAccess_Dist::x10__VERIFICATION_P;
    CS = cond14?1:2;
    if (cond14) // comm if_cond_then;
	{
            create_func(c, IF_COND_THEN);
            x10lib::Broadcast(&c, sizeof(c));
            if_cond_then();
	}

        else
	{
            create_func(c, IF_COND_ELSE);
            x10lib::Broadcast(&c, sizeof(c));
            if_cond_else();
	}
    
    
//#line 396 "RandomAccess_Dist.x10"
    x10_double __returnVar__7;
    { // Inlined RandomAccess_Dist.mysecond()
        {
            
//#line 89 "RandomAccess_Dist.x10"
            __returnVar__7 = (x10_double) ((x10_double) (System::nanoTime() /
                                                           1000) * 1.0E-6);
            goto RETURN_6;
            
        }
    }
    RETURN_6: ;
    cputime += __returnVar__7;
    
//#line 399 "RandomAccess_Dist.x10"
    bool cond36;
    
    cond36 = VERIFY == RandomAccess_Dist::x10__UPDATE_AND_VERIFICATION;
    CS = cond36?3:4;
if (cond36)
    {
        
//#line 402 "RandomAccess_Dist.x10"
        System::x10__out->println(String("Verifying result by repeating the update sequentially..."));
        
//#line 404 "RandomAccess_Dist.x10"
            create_func(c, F_START);
            x10lib::Broadcast(&c, sizeof(c));
            f_start();

        if (3 != CS) goto SKIP_3;
        try {
            
//#line 404 "RandomAccess_Dist.x10"
            {
                x10::ref<x10::lang::region> __var8__;
                x10_int __var9__;
                x10_int p;
                __var8__ = RandomAccess_Dist::x10__UNIQUE->x10__region;
                __var9__ = __var8__->rank(0)->high();
                for (p = __var8__->rank(0)->low(); p <= __var9__; p++) {
                    {
                        
//#line 405 "RandomAccess_Dist.x10"
                        x10_long ran;
                        x10_long __returnVar__11;
                        { // Inlined RandomAccess_Dist.HPCC_starts(long)
                            x10_long __var12__ = p * NumUpdates;
                            x10_long n = __var12__;
                            {
                                
//#line 111 "RandomAccess_Dist.x10"
                                x10_int i;
                                
//#line 111 "RandomAccess_Dist.x10"
                                x10_int j;
                                
//#line 112 "RandomAccess_Dist.x10"
                                x10::ref<x10::array<x10_long> > m2;
                                m2 = x10::alloc_array<x10_long >(64);
                                
//#line 113 "RandomAccess_Dist.x10"
                                x10_long temp;
                                
//#line 113 "RandomAccess_Dist.x10"
                                x10_long ran;
                                
//#line 115 "RandomAccess_Dist.x10"
                                while (n < 0) 
                                
//#line 115 "RandomAccess_Dist.x10"
                                n += RandomAccess_Dist::x10__PERIOD;
                                
//#line 116 "RandomAccess_Dist.x10"
                                while (n > RandomAccess_Dist::x10__PERIOD)
                                
//#line 116 "RandomAccess_Dist.x10"
                                n -= RandomAccess_Dist::x10__PERIOD;
                                
//#line 117 "RandomAccess_Dist.x10"
                                if (n == 0) {
                                    
//#line 117 "RandomAccess_Dist.x10"
                                    __returnVar__11 = 1;
                                    goto RETURN_10;
                                    
                                }
                                
//#line 119 "RandomAccess_Dist.x10"
                                temp = 1;
                                
//#line 120 "RandomAccess_Dist.x10"
                                {
                                    for (
//#line 120 "RandomAccess_Dist.x10"
                                         i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                                                        i++){
                                     
                                    {
                                        
//#line 121 "RandomAccess_Dist.x10"
                                        m2->_data[i] = temp;
                                        
//#line 122 "RandomAccess_Dist.x10"
                                        temp = (temp << 1) ^ ((x10_long) temp <
                                                                0 ? RandomAccess_Dist::x10__POLY
                                                                : 0);
                                        
//#line 123 "RandomAccess_Dist.x10"
                                        temp = (temp << 1) ^ ((x10_long) temp <
                                                                0 ? RandomAccess_Dist::x10__POLY
                                                                : 0);
                                    }
                                    
                                }
                                }
                                
//#line 126 "RandomAccess_Dist.x10"
                                {
                                    for (
//#line 126 "RandomAccess_Dist.x10"
                                         i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                                                         i--){
                                     
                                    {
                                        
//#line 127 "RandomAccess_Dist.x10"
                                        if (((n >> i) & 1) != 0) {
                                            
//#line 128 "RandomAccess_Dist.x10"
                                            break;
                                            
                                        }
                                        
                                    }
                                    
                                }
                                }
                                
//#line 130 "RandomAccess_Dist.x10"
                                ran = 2;
                                
//#line 131 "RandomAccess_Dist.x10"
                                while (i > 0) 
                                {
                                    
//#line 132 "RandomAccess_Dist.x10"
                                    temp = 0;
                                    
//#line 133 "RandomAccess_Dist.x10"
                                    {
                                        for (
//#line 133 "RandomAccess_Dist.x10"
                                             j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                                            j++){
                                         
                                        {
                                            
//#line 134 "RandomAccess_Dist.x10"
                                            if (((ran >> j) & 1) != 0) {
                                                
//#line 135 "RandomAccess_Dist.x10"
                                                temp ^= m2->_data[j];
                                            }
                                            
                                        }
                                        
                                    }
                                    }
                                    
//#line 136 "RandomAccess_Dist.x10"
                                    ran = temp;
                                    
//#line 137 "RandomAccess_Dist.x10"
                                    i -= 1;
                                    
//#line 138 "RandomAccess_Dist.x10"
                                    if (((n >> i) & 1) != 0) {
                                        
//#line 139 "RandomAccess_Dist.x10"
                                        ran = (ran << 1) ^ ((x10_long) ran <
                                                              0 ? RandomAccess_Dist::x10__POLY
                                                              : 0);
                                    }
                                    
                                }
                                
//#line 142 "RandomAccess_Dist.x10"
                                __returnVar__11 = ran;
                                goto RETURN_10;
                                
                            }
                        }
                        RETURN_10: ;
                        ran = __returnVar__11;
                        
//#line 406 "RandomAccess_Dist.x10"
                        {
                            x10_long i;
                            for (
//#line 406 "RandomAccess_Dist.x10"
                                 i = 0; i < NumUpdates; 
//#line 406 "RandomAccess_Dist.x10"
                                                        i++){
                             
                            {
                                
//#line 407 "RandomAccess_Dist.x10"
                                x10_int placeID;
                                
//#line 408 "RandomAccess_Dist.x10"
                                if (Embarrassing) {
                                    
//#line 409 "RandomAccess_Dist.x10"
                                    placeID = p;
                                } else {
                                    
//#line 411 "RandomAccess_Dist.x10"
                                    placeID = (x10_int) ((ran >> LogTableSize) &
                                                           RandomAccess_Dist::x10__PLACEIDMASK);
                                }
                                
//#line 412 "RandomAccess_Dist.x10"
                                x10_long temp;
                                temp = ran;
                                
//if (__here__ == 0 && i % 1000000  == 0)
//cerr << temp << endl;
//#line 414 "RandomAccess_Dist.x10"
                                async_invocation(RandomAccess_Dist, 2, (((x10::lang::place)placeID))->x10__id, (temp));
                                
//#line 417 "RandomAccess_Dist.x10"
                                ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                                      : 0);
                            }
                            
                        }
                        }
                        
                    }
                }
            }
            
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
	
            create_func(c, F_END);
            x10lib::Broadcast(&c, sizeof(c));
            f_end();
        SKIP_3: ;
        
    }
    
//#line 424 "RandomAccess_Dist.x10"
    GUPs = (cputime > 0.0 ? 1.0 / cputime : -1.0);
    
//#line 425 "RandomAccess_Dist.x10"
    GUPs *= 1.0E-9 * (4 * tableSize * RandomAccess_Dist::x10__NUMPLACES);
    
//#line 427 "RandomAccess_Dist.x10"
    if (doIO) {
        
//#line 429 "RandomAccess_Dist.x10"
        System::x10__out->println(String("    CPU time used  = ") + cputime +
                                  String(" seconds"));
    }
    
//#line 431 "RandomAccess_Dist.x10"
    if (VERIFY == RandomAccess_Dist::x10__UPDATE) {
        
//#line 431 "RandomAccess_Dist.x10"
        System::x10__out->println(String("    ") + GUPs + String(" Billion(10^9) Updates    per second [GUP/s]"));
    }
    
//#line 435 "RandomAccess_Dist.x10"
    bool cond52;
    cond52 = VERIFY > 0;
    CS = cond52?4:5;
    if (cond52)
    {
            create_func(c, IF_COND_THEN2);
            x10lib::Broadcast(&c, sizeof(c));
            if_cond_then2(); 
        
//#line 446 "RandomAccess_Dist.x10"
        x10_long globalSum = 0;
        
//#line 447 "RandomAccess_Dist.x10"
        {
            x10_int i;
            for (
//#line 447 "RandomAccess_Dist.x10"
                 i = 0; i < RandomAccess_Dist::x10__NUMPLACES; 
//#line 447 "RandomAccess_Dist.x10"
                                                               i++){
             
            {
                
//#line 447 "RandomAccess_Dist.x10"
                globalSum += RandomAccess_Dist::GLOBAL_STATE.SUM->_data[i];
            }
            
        }
        }
        
//#line 448 "RandomAccess_Dist.x10"
        if (VERIFY == RandomAccess_Dist::x10__VERIFICATION_P) {
            
//#line 450 "RandomAccess_Dist.x10"
            x10_double missedUpdateRate;
            missedUpdateRate = (globalSum - numUpdates) / (x10_double) numUpdates *
              100;
            
//#line 451 "RandomAccess_Dist.x10"
            System::x10__out->println(String("    The rate of missed updates  ") +
                                      missedUpdateRate + String("%"));
        } else {
            
//#line 454 "RandomAccess_Dist.x10"
            System::x10__out->println(String("    The global sum is ") + globalSum +
                                      String(" (correct=0)"));
        }
        
    }
    
}
        create_term(c, 0);
        x10lib::Broadcast(&c, sizeof(c));
}
void RandomAccess_Dist::if_cond_then()
{
printStart("if_cond_then");
        
//#line 359 "RandomAccess_Dist.x10"
        CS = x10::finish_start(CS);//finish# 1
        try {
            
//#line 359 "RandomAccess_Dist.x10"
            x10::ref<point> __var0__ = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
            
//#line 359 "RandomAccess_Dist.x10"
            x10_int p;
            p = __var0__->operator[](0);
            {
                
//#line 360 "RandomAccess_Dist.x10"
                x10_long ran;
                x10_long __returnVar__1;
                { // Inlined RandomAccess_Dist.HPCC_starts(long)
                    x10_long __var2__ = p * RandomAccess_Dist::GLOBAL_STATE.NumUpdates;
                    x10_long n = __var2__;
                    {
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int i;
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int j;
                        
//#line 112 "RandomAccess_Dist.x10"
                        x10::ref<x10::array<x10_long> > m2;
                        m2 = x10::alloc_array<x10_long >(64);
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long temp;
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long ran;
                        
//#line 115 "RandomAccess_Dist.x10"
                        while (n < 0) 
                        
//#line 115 "RandomAccess_Dist.x10"
                        n += RandomAccess_Dist::x10__PERIOD;
                        
//#line 116 "RandomAccess_Dist.x10"
                        while (n > RandomAccess_Dist::x10__PERIOD) 
                        
//#line 116 "RandomAccess_Dist.x10"
                        n -= RandomAccess_Dist::x10__PERIOD;
                        
//#line 117 "RandomAccess_Dist.x10"
                        if (n == 0) {
                            
//#line 117 "RandomAccess_Dist.x10"
                            __returnVar__1 = 1;
                            goto RETURN_0;
                            
                        }
                        
//#line 119 "RandomAccess_Dist.x10"
                        temp = 1;
                        
//#line 120 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 120 "RandomAccess_Dist.x10"
                                 i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                                                i++){
                             
                            {
                                
//#line 121 "RandomAccess_Dist.x10"
                                m2->_data[i] = temp;
                                
//#line 122 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                                
//#line 123 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                            }
                            
                        }
                        }
                        
//#line 126 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 126 "RandomAccess_Dist.x10"
                                 i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                                                 i--){
                             
                            {
                                
//#line 127 "RandomAccess_Dist.x10"
                                if (((n >> i) & 1) != 0) {
                                    
//#line 128 "RandomAccess_Dist.x10"
                                    break;
                                    
                                }
                                
                            }
                            
                        }
                        }
                        
//#line 130 "RandomAccess_Dist.x10"
                        ran = 2;
                        
//#line 131 "RandomAccess_Dist.x10"
                        while (i > 0) 
                        {
                            
//#line 132 "RandomAccess_Dist.x10"
                            temp = 0;
                            
//#line 133 "RandomAccess_Dist.x10"
                            {
                                for (
//#line 133 "RandomAccess_Dist.x10"
                                     j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                                    j++){
                                 
                                {
                                    
//#line 134 "RandomAccess_Dist.x10"
                                    if (((ran >> j) & 1) != 0) {
                                        
//#line 135 "RandomAccess_Dist.x10"
                                        temp ^= m2->_data[j];
                                    }
                                    
                                }
                                
                            }
                            }
                            
//#line 136 "RandomAccess_Dist.x10"
                            ran = temp;
                            
//#line 137 "RandomAccess_Dist.x10"
                            i -= 1;
                            
//#line 138 "RandomAccess_Dist.x10"
                            if (((n >> i) & 1) != 0) {
                                
//#line 139 "RandomAccess_Dist.x10"
                                ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                                      : 0);
                            }
                            
                        }
                        
//#line 142 "RandomAccess_Dist.x10"
                        __returnVar__1 = ran;
                        goto RETURN_0;
                        
                    }
                }
                RETURN_0: ;
                ran = __returnVar__1;
                
//#line 361 "RandomAccess_Dist.x10"
                {
                    x10_long i;
                    for (
//#line 361 "RandomAccess_Dist.x10"
                         i = 0; i < RandomAccess_Dist::GLOBAL_STATE.NumUpdates; 
//#line 361 "RandomAccess_Dist.x10"
                                                i++){
                     
                    {
                        
//#line 362 "RandomAccess_Dist.x10"
                        x10_int placeID;
                        
//#line 363 "RandomAccess_Dist.x10"
                        if (RandomAccess_Dist::GLOBAL_STATE.Embarrassing) {
                            
//#line 364 "RandomAccess_Dist.x10"
                            placeID = p;
                        } else {
                            
//#line 366 "RandomAccess_Dist.x10"
                            placeID = (x10_int) ((ran >> RandomAccess_Dist::GLOBAL_STATE.LogTableSize) & RandomAccess_Dist::x10__PLACEIDMASK);
                        }
                        
//#line 367 "RandomAccess_Dist.x10"
                        x10_long temp;
                        temp = ran;
                        
//#line 369 "RandomAccess_Dist.x10"
                        async_invocation(RandomAccess_Dist, 0, (((x10::lang::place)placeID))->x10__id, (temp));
                        
//#line 371 "RandomAccess_Dist.x10"
                        ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                              : 0);
                    }
                    
                }
                }
                
            }
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10::finish_end(EXCEPTION);// finish#1
        CS = 0;
        SKIP_1: ;
printEnd("if_cond_then");
    }

void RandomAccess_Dist::if_cond_else()
{
printStart("if_cond_else");
        
//#line 375 "RandomAccess_Dist.x10"
        CS = x10::finish_start(CS);//finish# 2
        if (2 != CS) goto SKIP_2;
        try {
            
//#line 375 "RandomAccess_Dist.x10"
            x10::ref<point> __var1__ = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
            
//#line 375 "RandomAccess_Dist.x10"
            x10_int p;
            p = __var1__->operator[](0);
            {
                
//#line 377 "RandomAccess_Dist.x10"
                x10_long ran;
                x10_long __returnVar__4;
                { // Inlined RandomAccess_Dist.HPCC_starts(long)
                    x10_long __var5__ = p * RandomAccess_Dist::GLOBAL_STATE.NumUpdates;
                    x10_long n = __var5__;
                    {
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int i;
                        
//#line 111 "RandomAccess_Dist.x10"
                        x10_int j;
                        
//#line 112 "RandomAccess_Dist.x10"
                        x10::ref<x10::array<x10_long> > m2;
                        m2 = x10::alloc_array<x10_long >(64);
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long temp;
                        
//#line 113 "RandomAccess_Dist.x10"
                        x10_long ran;
                        
//#line 115 "RandomAccess_Dist.x10"
                        while (n < 0) 
                        
//#line 115 "RandomAccess_Dist.x10"
                        n += RandomAccess_Dist::x10__PERIOD;
                        
//#line 116 "RandomAccess_Dist.x10"
                        while (n > RandomAccess_Dist::x10__PERIOD) 
                        
//#line 116 "RandomAccess_Dist.x10"
                        n -= RandomAccess_Dist::x10__PERIOD;
                        
//#line 117 "RandomAccess_Dist.x10"
                        if (n == 0) {
                            
//#line 117 "RandomAccess_Dist.x10"
                            __returnVar__4 = 1;
                            goto RETURN_3;
                            
                        }
                        
//#line 119 "RandomAccess_Dist.x10"
                        temp = 1;
                        
//#line 120 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 120 "RandomAccess_Dist.x10"
                                 i = 0; i < 64; 
//#line 120 "RandomAccess_Dist.x10"
                                                i++){
                             
                            {
                                
//#line 121 "RandomAccess_Dist.x10"
                                m2->_data[i] = temp;
                                
//#line 122 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                                
//#line 123 "RandomAccess_Dist.x10"
                                temp = (temp << 1) ^ ((x10_long) temp < 0
                                                        ? RandomAccess_Dist::x10__POLY
                                                        : 0);
                            }
                            
                        }
                        }
                        
//#line 126 "RandomAccess_Dist.x10"
                        {
                            for (
//#line 126 "RandomAccess_Dist.x10"
                                 i = 62; i >= 0; 
//#line 126 "RandomAccess_Dist.x10"
                                                 i--){
                             
                            {
                                
//#line 127 "RandomAccess_Dist.x10"
                                if (((n >> i) & 1) != 0) {
                                    
//#line 128 "RandomAccess_Dist.x10"
                                    break;
                                    
                                }
                                
                            }
                            
                        }
                        }
                        
//#line 130 "RandomAccess_Dist.x10"
                        ran = 2;
                        
//#line 131 "RandomAccess_Dist.x10"
                        while (i > 0) 
                        {
                            
//#line 132 "RandomAccess_Dist.x10"
                            temp = 0;
                            
//#line 133 "RandomAccess_Dist.x10"
                            {
                                for (
//#line 133 "RandomAccess_Dist.x10"
                                     j = 0; j < 64; 
//#line 133 "RandomAccess_Dist.x10"
                                                    j++){
                                 
                                {
                                    
//#line 134 "RandomAccess_Dist.x10"
                                    if (((ran >> j) & 1) != 0) {
                                        
//#line 135 "RandomAccess_Dist.x10"
                                        temp ^= m2->_data[j];
                                    }
                                    
                                }
                                
                            }
                            }
                            
//#line 136 "RandomAccess_Dist.x10"
                            ran = temp;
                            
//#line 137 "RandomAccess_Dist.x10"
                            i -= 1;
                            
//#line 138 "RandomAccess_Dist.x10"
                            if (((n >> i) & 1) != 0) {
                                
//#line 139 "RandomAccess_Dist.x10"
                                ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                                      : 0);
                            }
                            
                        }
                        
//#line 142 "RandomAccess_Dist.x10"
                        __returnVar__4 = ran;
                        goto RETURN_3;
                        
                    }
                }
                RETURN_3: ;
                ran = __returnVar__4;
                
//#line 378 "RandomAccess_Dist.x10"
                {
                    x10_long i;
                    for (
//#line 378 "RandomAccess_Dist.x10"
                         i = 0; i < RandomAccess_Dist::GLOBAL_STATE.NumUpdates; 
//#line 378 "RandomAccess_Dist.x10"
                                                i++){
                     
                    {
                        
//#line 380 "RandomAccess_Dist.x10"
                        x10_int placeID;
                        
//#line 381 "RandomAccess_Dist.x10"
                        if (RandomAccess_Dist::GLOBAL_STATE.Embarrassing) {
                            
//#line 382 "RandomAccess_Dist.x10"
                            placeID = p;
                        } else {
                            
//#line 384 "RandomAccess_Dist.x10"
                            placeID = (x10_int) ((ran >> RandomAccess_Dist::GLOBAL_STATE.LogTableSize) & RandomAccess_Dist::x10__PLACEIDMASK);
                        }
                        
//#line 386 "RandomAccess_Dist.x10"
                        x10_long temp;
                        temp = ran;
                        
//#line 389 "RandomAccess_Dist.x10"
                        async_invocation(RandomAccess_Dist, 1, (((x10::lang::place)placeID))->x10__id, (temp));
                        
//#line 390 "RandomAccess_Dist.x10"
                        ran = (ran << 1) ^ ((x10_long) ran < 0 ? RandomAccess_Dist::x10__POLY
                                              : 0);
                    }
                    
                }
                }
                
            }
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10::finish_end(EXCEPTION);// finish#2
        CS = 0;
        SKIP_2: ;
        
printEnd("if_cond_else");
    }
void RandomAccess_Dist::if_cond_then2()
{
printStart("if_cond_then2");
        
//#line 437 "RandomAccess_Dist.x10"
        x10::ref<x10::array<x10_long> > SUM = x10::alloc_array<x10_long >(RandomAccess_Dist::x10__NUMPLACES);
        RandomAccess_Dist::GLOBAL_STATE.SUM = SUM;
        
//#line 438 "RandomAccess_Dist.x10"
        CS = x10::finish_start(CS);//finish# 4
        if (4 != CS) goto SKIP_4;
        try {
            
//#line 438 "RandomAccess_Dist.x10"
            x10::ref<point> __var3__ = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
            
//#line 438 "RandomAccess_Dist.x10"
            x10_int p;
            p = __var3__->operator[](0);
            {
                
//#line 439 "RandomAccess_Dist.x10"
                x10_long sum;
                sum = 0;
                
//#line 441 "RandomAccess_Dist.x10"
                {
                    x10_int i;
                    for (
//#line 441 "RandomAccess_Dist.x10"
                         i = 0; i < RandomAccess_Dist::GLOBAL_STATE.tableSize; 
//#line 441 "RandomAccess_Dist.x10"
                                               i++){
                     
                    {
                        
//#line 441 "RandomAccess_Dist.x10"
                        sum += RandomAccess_Dist::GLOBAL_STATE.Table->x10__array->rawRegion()[i];
                    }
                    
                }
                }
                
//#line 443 "RandomAccess_Dist.x10"
                x10_long temp;
                temp = sum;
                
//#line 444 "RandomAccess_Dist.x10"
printStart("async"); 
                async_invocation(RandomAccess_Dist, 3, (((x10::lang::place)0))->x10__id, (p,
                                                                                            temp));
printEnd("async"); 
            }
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
printStart("finish"); 
        x10::finish_end(EXCEPTION);// finish#4
printEnd("finish"); 
        CS = 0;
        SKIP_4: ;
printEnd("if_cond_then2");
    }
void RandomAccess_Dist::f_start(){
printStart("f_start");
        CS = x10::finish_start(CS);//finish# 3
printEnd("f_end");
}
void RandomAccess_Dist::f_end(){
printStart("f_end");
        x10::finish_end(EXCEPTION);// finish#3
        CS = 0;
printEnd("f_end");
}
