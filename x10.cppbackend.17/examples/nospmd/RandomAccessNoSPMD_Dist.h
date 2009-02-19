#ifndef __RANDOMACCESS_DIST_H
#define __RANDOMACCESS_DIST_H

#include <x10lang.h>

using namespace x10::lang;

class RandomAccess_Dist : public x10::lang::Object {
    /*public */ public : class localTable;
    /*public */ public : static x10_double mysecond();
    /*private */ public : static x10_long x10__POLY;
    /*private */ public : static x10_long x10__PERIOD;
    /*private */ public : static x10::ref<x10::lang::dist> x10__UNIQUE;
    /*private */ public : static x10_int x10__NUMPLACES;
    /*private */ public : static x10_int x10__PLACEIDMASK;
    /*private */ public : static x10_int x10__UPDATE;
    /*private */ public : static x10_int x10__VERIFICATION_P;
    /*private */ public : static x10_int x10__UPDATE_AND_VERIFICATION;
    /*public */ public : static x10_long HPCC_starts(x10_long n);
    /*public */ public : static void main(x10::ref<x10::array<x10::ref<String> > > args);
    /*public */ public : RandomAccess_Dist();
    public:  static int main_np0();

    public :    
    public:  static void if_cond_else();
    public:  static void if_cond_then();
    public:  static void if_cond_then2();
    public:  static void if_cond_then3();
    public:  static void ccode1();
    public:  static void f_start();
    public:  static void f_end();
    public : static void* __static_init();
    public : static void* ArrayCopySwitch(x10_async_handler_t h, void* __arg);
    public : static void AsyncSwitch(x10_async_handler_t h, void* arg, int niter);
    public : static void async__0(x10_long);
    public : static void async__1(x10_long);
    public : static void async__2(x10_long);
    public : static void async__3(x10_int, x10_long);
    public : static x10::ref<RandomAccess_Dist::localTable> __init__0(void*, x10::ref<point>);
    
    public: static struct _GLOBAL_STATE {
    
        x10_int VERIFY;
        x10_int logTableSize;
        x10_long tableSize;
        x10_long numUpdates;
        x10::ref<RandomAccess_Dist::localTable> Table;
        x10::ref<x10::array<x10_long> > SUM;
        x10_long LogTableSize;
        x10_long NumUpdates;
        x10_boolean Embarrassing;
        x10_boolean doIO;
        x10_boolean embarrassing;
        void updateLong (int offset, x10_long value) {
                *((x10_long*)(((x10_int*)&VERIFY)+offset)) = value;
/*
		cerr<< "here  = " <<__here__ <<endl;
		cerr<< "offset = " <<offset <<endl;
		cerr<< "value = " <<value <<endl;
*/
        }

        void updateInt (int offset, int value) {
                *((&VERIFY)+offset) = value;
/*	cerr <<"VERIFY = " << VERIFY <<endl
	     <<"doIO   = " << doIO << endl
	     <<"embarrassing   = " << embarrassing << endl
	     <<"tableSize   = " << tableSize << endl
	     <<"numUpdates   = " << numUpdates << endl
	     <<"LogTableSize   = " << LogTableSize << endl
	     <<"logTableSize   = " << logTableSize << endl
	     <<"Embarrassing   = " << Embarrassing << endl
	     <<"NumUpdates   = " << NumUpdates << endl;
*/
        }
        
    } GLOBAL_STATE;
    
};
class RandomAccess_Dist::localTable : public x10::lang::Object {
    /*public */ public : x10::ref<x10::x10array<x10_long> > x10__array;
    /*public */ public : x10_long x10__tableSize;
    /*public */ public : x10_long x10__mask;
    /*public */ public : localTable(x10_long size);
    /*public */ public : virtual void update(x10_long ran);
    /*public */ public : virtual void verify(x10_long ran);
    public : static void* ArrayCopySwitch(x10_async_handler_t h, void* __arg);
    public : static void AsyncSwitch(x10_async_handler_t h, void* arg, int niter);
    public : static x10::ref<RandomAccess_Dist::localTable> __init__0(void*, x10::ref<point>);
    
};
#endif //RANDOMACCESS_DIST_H

class RandomAccess_Dist;
