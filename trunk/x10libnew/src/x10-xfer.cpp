#include "x10-xfer.h"

using namespace x10lib;


 place_t here()
{
	assert(false);
}

 int get(gas_ref_t src,  void* dest, int nbytes)
{
	assert(false);
}

 int get_nb(gas_ref_t src, void* dest, int nbytes, 
                      switch_t handle)
 {
	assert(false);
}
                     
template <typename T>
 T getValue_int (gas_ref_t src)
{
	assert(false);
}

// Also operations for strided copy, generalized i/o vector copy.

 int put(void* src, gas_ref_t dest, int bytes)
{
	assert(false);
}
 
int put_nb(void* src, gas_ref_t dest, int bytes, 
                      switch_t handle)
{
	assert(false);
}

template <typename T>
 int putValue_int(T value, gas_ref_t dest)
{
	assert(false);
}