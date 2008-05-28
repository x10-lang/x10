#include <stdlib.h>

#include "x10_internal.h"

#define ADDRMASK 0x01

bool X10::IsLocalRef(Addr ref)
{
  return (((long)ref) & ADDRMASK==0);
}

X10::RemoteRef X10::SerializeRef(Addr ref) 
{
  RemoteRef remote_ref;
  
  int loc_mask = ((long)ref) & ADDRMASK;
  
  if (loc_mask == 0) { //check local or remote
    remote_ref.SetLoc(__x10::here); 
    remote_ref.SetAddr(ref);
  } else {
    remote_ref.SetLoc(((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetLoc());
    remote_ref.SetAddr((Addr) (((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetAddr()));
  }
  
  return remote_ref;
}

X10::Addr X10::DeserializeRef (X10::RemoteRef ref)
{
  if (ref.GetLoc() == Place(__x10::here))
    return ref.GetAddr();
  else {
    RemoteRef* remote_ref = (RemoteRef*) malloc(sizeof(RemoteRef));
    remote_ref->SetLoc(ref.GetLoc());
    remote_ref->SetAddr(ref.GetAddr());
    remote_ref = (RemoteRef*) (((long) remote_ref) | ADDRMASK);
    return (Addr) remote_ref;
  }
}

X10::Place X10::GetLoc(X10::Addr ref)
{
  int loc_mask = ((long)ref) & ADDRMASK;
  return loc_mask == 0 ?  Place(__x10::here) : ((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetLoc();
}

X10::Addr X10::GetAddr(X10::Addr ref)
{
  int loc_mask = ((long)ref) & ADDRMASK;
  
  if (loc_mask == 0) { //check local or remote
    return ref;
  } else {    
    return ((X10::RemoteRef*) (((long) ref) ^ ADDRMASK))->GetAddr();
  }
}

//C Bindings

bool x10_is_localref(x10_addr_t ref)
{
  return X10::IsLocalRef(ref);
}

x10_remote_ref_t x10_serialize_ref (x10_addr_t ref) 
{
  return X10::SerializeRef(ref);
}

x10_addr_t x10_deserialize_ref (x10_remote_ref_t ref)
{
  return X10::DeserializeRef(ref);
}

x10_place_t x10_get_loc (x10_addr_t ref)
{
  return X10::GetLoc(ref);
}

x10_addr_t x10_get_addr(x10_addr_t ref)
{
  return X10::GetAddr(ref);
}
