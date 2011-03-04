#include <stdio.h>

//
// parameters
//

const int PARALLELISM = 2;
const int logLocalTableSize = 16;
    
// derived
const int localTableSize = 1 << logLocalTableSize;
const int tableSize = PARALLELISM * localTableSize;
const int numUpdates = 4 * tableSize;
const int placeMask = PARALLELISM - 1;

double expected() {return 0.0;}
double operations() {return 1.0 * numUpdates;}

char *name = "SeqRandomAccess1";


//
// the benchmark
//

const long long POLY = 0x0000000000000007LL;
const long long PERIOD = 1317624576693539401LL;

class LocalTable {
    
public:

    long long * const a;
    const int mask;

    LocalTable(int size) : mask(size-1), a(new long long[size]) {
        for (int i=0; i<size; i++)
            a[i] = i;
    }
    
    void update(long long ran) {
        //a(ran&mask to int) ^= ran;
        int index = (int)(ran&mask);
        a[index] ^= ran;
    }
};

LocalTable ** const tables = new LocalTable*[PARALLELISM];

void init() {
    for (int i=0; i<PARALLELISM; i++)
        tables[i] = new LocalTable(localTableSize);
}

long long HPCCStarts(long long n) {
    int i, j;
    long long * const m2 = new long long[64];
    while (n < 0) n += PERIOD;
    while (n > PERIOD) n -= PERIOD;
    if (n == 0) return 0x1LL;
    long long temp = 0x1;
    for (i=0; i<64; i++) {
        m2[i] = temp;
        temp = (temp << 1) ^ (temp < 0 ? POLY : 0LL);
        temp = (temp << 1) ^ (temp < 0 ? POLY : 0LL);
    }
    for (i=62; i>=0; i--) if (((n >> i) & 1) != 0) break;
    long long ran = 0x2;
    while (i > 0) {
        temp = 0;
        for (j=0; j<64; j++) if (((ran >> j) & 1) != 0) temp ^= m2[j];
        ran = temp;
        i -= 1;
        if (((n >> i) & 1) != 0)
            ran = (ran << 1) ^ (ran < 0 ? POLY : 0);
    }
    return ran;
}

void randomAccessUpdate(LocalTable **tables) {
    for (int p=0; p<PARALLELISM; p++) {
        long long ran = HPCCStarts(p * (numUpdates/PARALLELISM));
        for (long long i=0; i<numUpdates/PARALLELISM; i++) {
            int placeId = (int) (((ran>>logLocalTableSize) & placeMask));
            tables[placeId]->update(ran);
            ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
        }
    }
}

bool first = true;

double once() {

    // do the updates
    randomAccessUpdate(tables);
    
    // First time through do verfification. The test by design
    // runs without synchronization and is allowed .01*tableSize errors
    if (first) {
        randomAccessUpdate(tables);
        int errors = 0;
        for (int p=0; p<PARALLELISM; p++) {
            LocalTable *table = tables[p];
            for (int j=0; j<localTableSize; j++)
                if (table->a[j] != j)
                    errors++;
        }
        first = false;
        printf("%d error(s); allowed %d\n", errors, tableSize/100);
        fflush(stdout);
        return (double)(errors * 100 / tableSize); // <.01*tableSize counts as 0
    } else
        return 0.0;
    
}
