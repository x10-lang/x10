#ifndef __RANDOMACCESS_DIST_H
#define __RANDOMACCESS_DIST_H
#include <x10lang.h>
using namespace x10::lang;
class RandomAccess_Dist : public x10::lang::Object {
    public : class localTable : public x10::lang::Object {
        public : x10::ref<x10::x10array<x10_long> > array;
        public : x10_long tableSize;
        public : x10_long mask;
        public : localTable(x10_long size);
        public : virtual void update(x10_long ran);
        public : virtual void verify(x10_long ran);
        
    };
    public : static x10_double mysecond();
    private : static x10_long POLY;
    private : static x10_long PERIOD;
    private : static x10::ref<dist> UNIQUE;
    private : static x10_int NUMPLACES;
    private : static x10_int PLACEIDMASK;
    private : static x10_int UPDATE;
    private : static x10_int VERIFICATION_P;
    private : static x10_int UPDATE_AND_VERIFICATION;
    public : static x10_long HPCC_starts(x10_long n);
    public : static void main(const x10::ref<x10::array<x10::ref<String> > > args);
    public : RandomAccess_Dist();
    
};
struct {

    x10_int VERIFY;
    x10_boolean doIO;
    x10_boolean embarrassing;
    x10_int logTableSize;
    x10_long tableSize;
    x10_long numUpdates;
    x10::ref<RandomAccess_Dist::localTable> Table;
    x10_long LogTableSize;
    x10_boolean Embarrassing;
    x10_long NumUpdates;
    x10::ref<x10::array<x10_long> > SUM;
    
} GLOBAL_STATE;
#endif //RANDOMACCESS_DIST_H
