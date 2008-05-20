#include <stdlib.h>

#include "x10.h"

bool x10_is_localref(void* ref)
{
  return (((int)ref) & 0x01==0);
}

x10_remote_ref_t x10_serialize_ref (void* ref) 
{
  x10_remote_ref_t remote_ref;
  
  int loc_mask = ((long)ref) & 0x01;
  
  if (loc_mask == 0) { //check local or remote
    remote_ref.loc = __x10_here; 
    remote_ref.addr = (void*) ref;
  } else {
    remote_ref.loc = ((x10_proxy_t*) (((int) ref) ^ 0x01))->loc;
    remote_ref.addr = ((x10_proxy_t*) (((int) ref) ^ 0x01))->addr;
  }
  
  return remote_ref;
}

void* x10_deserialize_ref (x10_remote_ref_t ref)
{
  if (ref.loc == __x10_here)
    return ref.addr;
  else {
    x10_proxy_t* remote_ref = (x10_proxy_t*) malloc(sizeof(x10_proxy_t));
    remote_ref->loc = ref.loc;
    remote_ref->addr = ref.addr;
    remote_ref = (x10_proxy_t*) (((long) remote_ref) | 0x01);
    return remote_ref;
  }
}

int x10_get_loc (void* ref)
{
  int loc_mask = ((long)ref) & 0x01;
  return loc_mask == 0 ?  __x10_here : ((x10_proxy_t*) (((long) ref) ^ 0x01))->loc;
}
