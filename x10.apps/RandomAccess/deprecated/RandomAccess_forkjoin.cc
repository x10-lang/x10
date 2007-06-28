#include <iostream>

#include <x10/x10lib.h>
#include <x10/array.h>
#include <x10/async.h>

using namespace std;

func_t handlers[128];

class localTable;



class RandomAccess_Dist {
  
  class localTable {
   
  private:
    const int tableSize;
    const int mask;
    uint64_t *array;

  public:
    localTable (int size) :
      tableSize (size),
      mask (size-1),
      array (new uint64_t [size]) 
    { }
    
    void update (uint64_t ran) {

      uint64_t off = ran & mask;
      assert (off >=0 && off < tableSize);
      array[off]  ^= ran;
    }
  };  
  
  typedef Array<localTable*, 1> array_t;
  static double mysecond () {
    assert (false);
  }
  
private :
  static const uint64_t POLY = 0x0000000000000007; 
  static const long PERIOD = 131762457669539401;
 
public:
 
  static Dist<1>*  UNIQUE;
  static int NUMPLACES;
  static int PLACEIDMASK;
  
public:
  static uint64_t HPCC_starts(int64_t n)
  {
    int i, j;
    uint64_t m2[64];
    uint64_t temp, ran;
    
    while (n < 0) n += PERIOD;
    while (n > PERIOD) n -= PERIOD;
    if (n == 0) return 0x1;
    
    temp = 0x1;
    for (i=0; i<64; i++) {
      m2[i] = temp;
      temp = (temp << 1) ^ ((int64_t) temp < 0 ? POLY : 0);
      temp = (temp << 1) ^ ((int64_t) temp < 0 ? POLY : 0);
    }
    
    for (i=62; i>=0; i--)
      if ((n >> i) & 1)
	break;
    
    ran = 0x2;
    while (i > 0) {
      temp = 0;
      for (j=0; j<64; j++)
	if ((ran >> j) & 1)
	  temp ^= m2[j];
      ran = temp;
      i -= 1;
      if ((n >> i) & 1)
	ran = (ran << 1) ^ ((int64_t) ran < 0 ? POLY : 0);
    }
    
    return ran;
  }

  //verify not now


  static void RandomAccessUpdate (const uint64_t LogTableSize, const bool Embarrasing, array_t* Table) {

    const uint64_t TableSize = (1UL<<LogTableSize);
    const uint64_t numUpdates = TableSize*4;

    //finish (RAJ??)

    //start_finish();

    for (place_t p = 0; p < numPlaces(); p++ ) //This is not what we want!
      {
        asyncSpawn<4, true> (p, 1, Embarrasing, numUpdates, LogTableSize, Table->base(p) );
      }

    //stop_finish();

  }
 
  static void main (int argc, char* argv[])
  {
    UNIQUE = Dist<1>::makeUnique();
    NUMPLACES = numPlaces();
    PLACEIDMASK = NUMPLACES-1;
    if ((NUMPLACES & (NUMPLACES-1)) > 0) {
      cout << "the number of places must be a power of 2!";
      exit (-1);
    } 

    bool doIO=false; 
    bool embarrasing = false;
    int logTableSize = 10;
    for (int q = 0; q < argc; ++q) {
      if (strcmp(argv[q], "-o") == NULL) {
	doIO = true;
      }

      if (strcmp (argv[q], "-e") == NULL) {
	embarrasing = true;
      }

      if (strcmp (argv[q], "-m") == NULL) {
	++q;
	logTableSize = atoi (argv[q]);
      }
    }

    const uint64_t tableSize = (1UL << logTableSize);
    array_t* Table = makeArray<localTable*, 1, RectangularRegion, UniqueDist> (UNIQUE->region(), UNIQUE);

   cout << "Point 1 " << Table->base(2) << endl;

   for (place_t p = 0; p < numPlaces(); p++){  //this should be mereged with the constructor above
      cout << "base: " << p << " " << Table->base(p) << endl;
      asyncSpawn<2, true> (p, 0, Table->base(p), tableSize);
   }

    //timing

    RandomAccessUpdate (logTableSize, embarrasing, Table);
 
   //printing and reporting 
  }

static void 
async0 (async_arg_t arg0, async_arg_t arg1)
{
  array_t* Table = (array_t*) (arg0);
  uint64_t tableSize = (uint64_t) arg1;

  cout << "Inside async0 " << here() << " " << Table << " " << tableSize << endl;

  Table->putLocalElementAt (0, new localTable(tableSize)); 
}

static void 
async1 (async_arg_t arg0, async_arg_t arg1, async_arg_t arg2, async_arg_t arg3)
{
   //Prologue -- interpret the arguments
   bool Embarrassing = (bool) arg0; //NOT  QUITE
   uint64_t numUpdates = arg1; 
   uint64_t LogTableSize = arg2; 
   array_t* Table  = (array_t*) (arg3);

  cout << "Inside async1 " << here() << " " << Table << " " << numUpdates << " " << LogTableSize << endl;

  uint64_t ran = HPCC_starts (here()*numUpdates);
  for (uint64_t i = 0; i < numUpdates; i++) {
     int placeID;
     if (Embarrassing)
       placeID = here();
     else
       placeID = (int) ((ran>>LogTableSize) & PLACEIDMASK);

     uint64_t temp = ran;

     cout << "place " << placeID << endl;
     ran = ((ran << 1) ^ ((long) ran < 0 ? POLY : 0));
     asyncSpawn<2, true> (UNIQUE->place(placeID), 2, Table->base(UNIQUE->place(placeID)), temp);
  }   
}

static void 
async2 (async_arg_t arg0, async_arg_t arg1)
{
   array_t* Table  = (array_t*) (arg0);
   uint64_t ran = arg1; 
  
  cout << "Inside async2 " << here() << " " << Table << " " << ran <<endl;
  Table->getLocalElementAt(0)->update (ran);
}
 
};

Dist<1>* RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();
int RandomAccess_Dist::NUMPLACES = numPlaces();
int RandomAccess_Dist::PLACEIDMASK = NUMPLACES-1;

int main (int argc, char* argv[])
{
  handlers[0].fptr = (void_func_t) RandomAccess_Dist::async0;
  handlers[1].fptr = (void_func_t) RandomAccess_Dist::async1;
  handlers[2].fptr =  (void_func_t) RandomAccess_Dist::async2;

  Init (handlers, 3);
  //Setenv (INTERRUPT_SET, 1);
  
  RandomAccess_Dist::UNIQUE = Dist<1>::makeUnique();
  RandomAccess_Dist::NUMPLACES = numPlaces();
  RandomAccess_Dist::PLACEIDMASK = numPlaces()-1;

  if (here() == 0) {
     RandomAccess_Dist::main (argc, argv);
  }

  Gfence();
  Finalize();

  return 0;
}

