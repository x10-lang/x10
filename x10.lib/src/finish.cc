/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: finish.cc,v 1.1 2007-06-11 13:38:53 ganeshvb Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/xmacros.h>
#include <x10/finish.h>

using namespace std;
using namespace x10lib; 

const int MAX_TASKS=256;
const int EX_BUFFER_SIZE = 1024;
//these need to be in place 0 only
char buffer[MAX_TASKS][EX_BUFFER_SIZE];
int numExceptions;
int placeException[MAX_TASKS];
 
lapi_cntr_t cntr1;
lapi_cntr_t cntr2;
lapi_long_t exceptionCntr[MAX_TASKS];
lapi_long_t continueCntr[MAX_TASKS];

int CONTINUE_STATUS;

void*
continueHeaderHandler (lapi_handle_t handle, void* uhdr,
                              uint* hdr_len, ulong* msg_len,
                              compl_hndlr_t** comp_h, void** uinfo)
{
  CONTINUE_STATUS = *((uint*) uhdr);
  *comp_h = NULL;
  return NULL;
}

void* 
exceptionHeaderHandler (lapi_handle_t handle, void* uhdr,
                              uint* hdr_len, ulong* msg_len,
                              compl_hndlr_t** comp_h, void** uinfo)
{
  place_t from = *((uint*) uhdr);  
  placeException[numExceptions] = from;
  numExceptions++;
  lapi_return_info_t* ret_info = (lapi_return_info_t*) msg_len;
  assert (ret_info->udata_one_pkt_ptr);
  memcpy (buffer[from], ret_info->udata_one_pkt_ptr, *msg_len);
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;
  return NULL;
}

error_t            
finishInit ()
{
  LRC (LAPI_Addr_set (GetHandle(), (void*) exceptionHeaderHandler, 3));
  LRC (LAPI_Addr_set (GetHandle(), (void*) continueHeaderHandler, 4));
  LRC (LAPI_Address_init64 (GetHandle(), (lapi_long_t) &cntr1, exceptionCntr));
  LRC (LAPI_Address_init64 (GetHandle(), (lapi_long_t) &cntr2, continueCntr));
  LRC (LAPI_Setcntr (GetHandle(), &cntr1, 0));
  LRC (LAPI_Setcntr (GetHandle(), &cntr2, 0));
  return X10_OK;
} 
                     
 
error_t
x10lib::finishEnd (Exception* e)
{
  lapi_cntr_t originCntr;
  int tmp;
  place_t p = here();
  //cout << "e: " << e << endl;
  if (here() != 0) {
   LRC (LAPI_Fence(GetHandle()));
    if (e != NULL) {
      LRC (LAPI_Setcntr (GetHandle(), &originCntr, 0));
      LRC (LAPI_Amsend (GetHandle(),
                 0,
                 (void*) 3, /*LAPI handler for exceptions */
                 &p, 
                 sizeof(place_t),
                 e,
                 e->size(),
                 (lapi_cntr_t*) exceptionCntr[0],
                 &originCntr,
                 NULL));
     LRC (LAPI_Waitcntr (GetHandle(), &originCntr, 1, &tmp));
    } else {
     LRC (LAPI_Put (GetHandle(), 0, 0, NULL, NULL, (lapi_cntr_t*) exceptionCntr[0], NULL, NULL)); 
    }

  } else { 
    LAPI_Fence (GetHandle());
    if (e != NULL)  {
      memcpy (buffer[0], e, e->size()); 
      placeException[numExceptions] = 0;
      numExceptions++;
    }
   
    int tmp; 

   LRC (LAPI_Waitcntr (GetHandle(), &cntr1, numPlaces() - 1, &tmp)); 

   //cout << numExceptions << endl;
   if (numExceptions > 0) {
     Exception** e = new Exception*[numExceptions];
     for (int  i = 0; i < numExceptions; i++) {
       e[i] = (Exception*) buffer[placeException[i]];
     }
     throw MultiException(e, numExceptions);
   }
 }

  return X10_OK;
}

error_t
x10lib::finishBegin (int* cs)
{
  int tmp;
  if (here() != 0) {
    LRC (LAPI_Waitcntr (GetHandle(), 
                  &cntr2,
                  1, 
                  &tmp));
    *cs = CONTINUE_STATUS;
  } else {
    lapi_cntr_t originCntr;
    *cs = numExceptions > 0 ? -1 : *cs;
     numExceptions = 0;
    for (int i = 0 ; i < numPlaces(); i++) {

     if (i == here()) continue;

      LRC (LAPI_Setcntr (GetHandle(), &originCntr, 0));
      LRC (LAPI_Amsend (GetHandle(), 
                 i, 
                 (void*) 4,
                 *cs == 0 ? NULL : cs,
                 *cs == 0 ? 0 : sizeof(int),
                 NULL,
                 0,
                 (lapi_cntr_t*) continueCntr[i],
                 &originCntr,
                 NULL));
     LRC (LAPI_Waitcntr (GetHandle(), &originCntr, 1, &tmp));
   }
     LRC (LAPI_Fence(GetHandle()));
  }

  CONTINUE_STATUS = 0;
  return X10_OK;
}
