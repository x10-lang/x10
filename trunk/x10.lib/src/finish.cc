/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: finish.cc,v 1.11 2007-06-26 16:05:57 ganeshvb Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/xmacros.h>
#include <x10/finish.h>
#include <lapi.h>

using namespace std;
using namespace x10lib; 

const int MAX_TASKS=256;
const int MAX_TASKS_NODE = 16;
const int MAX_NODES = 16;
const int EX_BUFFER_SIZE = 1024;
int bufSize = 0;
char buffer[MAX_TASKS * EX_BUFFER_SIZE];
int numExceptions;
 
lapi_cntr_t cntr1;
lapi_cntr_t cntr2;
lapi_long_t exceptionCntr[MAX_TASKS];
lapi_long_t continueCntr[MAX_TASKS];
//void* exceptionCntr[MAX_TASKS];
//void* continueCntr[MAX_TASKS];

int CONTINUE_STATUS;

/* process tree structure*/
struct ptree_t
{
  int numPeers;
  int numChild;
  int children[MAX_TASKS_NODE+MAX_NODES];
  int parent;
};

ptree_t* ftree=NULL;

/* count the number of children for process 0*/
void*
numChildHeaderHandler (lapi_handle_t handle, void* uhdr,
                              uint* hdr_len, ulong* msg_len,
                              compl_hndlr_t** comp_h, void** uinfo)
{
  ftree->children[ftree->numChild] = *((int*) uhdr);
  ftree->numChild++;
  lapi_return_info_t* ret_info = (lapi_return_info_t*) msg_len;
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;
  return NULL;
}

void*
continueHeaderHandler (lapi_handle_t handle, void* uhdr,
                              uint* hdr_len, ulong* msg_len,
                              compl_hndlr_t** comp_h, void** uinfo)
{
  CONTINUE_STATUS = *((uint*) uhdr);
  lapi_return_info_t* ret_info = (lapi_return_info_t*) msg_len;
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;
  return NULL;
}


void* 
exceptionHeaderHandler (lapi_handle_t handle, void* uhdr,
                              uint* hdr_len, ulong* msg_len,
                              compl_hndlr_t** comp_h, void** uinfo)
{
  int child_exceptions = *((int*) uhdr);
  lapi_return_info_t* ret_info = (lapi_return_info_t*) msg_len;
  assert (ret_info->udata_one_pkt_ptr);
  memcpy (buffer+bufSize, ret_info->udata_one_pkt_ptr, *msg_len);
  bufSize += *msg_len;
  numExceptions += child_exceptions;
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;
  return NULL;
}

x10_err_t            
finishInit ()
{
  LRC (LAPI_Addr_set (__x10_hndl, (void*) exceptionHeaderHandler, 3));
  LRC (LAPI_Addr_set (__x10_hndl, (void*) continueHeaderHandler, 4));
  LRC (LAPI_Addr_set (__x10_hndl, (void*) numChildHeaderHandler, 5));
  LRC (LAPI_Address_init64 (__x10_hndl, (lapi_long_t) &cntr1, exceptionCntr));
  LRC (LAPI_Address_init64 (__x10_hndl, (lapi_long_t) &cntr2, continueCntr));
  //LRC (LAPI_Address_init (__x10_hndl,  &cntr1, exceptionCntr));
  //LRC (LAPI_Address_init (__x10_hndl,  &cntr2, continueCntr));
  LRC (LAPI_Setcntr (__x10_hndl, &cntr1, 0));
  LRC (LAPI_Setcntr (__x10_hndl, &cntr2, 0));

  //allocate the fence tree structure
  ftree = new ptree_t;

  //find my peers, i.e all the processes in the same node
  char* envstr = getenv ("MP_COMMON_TASKS");
  ftree->numPeers = envstr ? atoi(envstr): 0;

  //choose the one with the minimum rank as the parent of this group
  ftree->parent = __x10_my_place;

  for (int i = 0; i < ftree->numPeers; i++)  {
    envstr = strchr (envstr, ':') + 1;
    ftree->children[i] = atoi(envstr);
    ftree->parent = ftree->parent < ftree->children[i] ?  ftree->parent : ftree->children[i]; 
  }
   
  ftree->numChild = 0; //leaves have no children
  if (ftree->parent == __x10_my_place && __x10_my_place !=0) { //non-Task0 parent
    x10_place_t p = __x10_my_place;
    ftree->numChild = ftree->numPeers;
    //send an active message to 0 to let it know that
    // I am a remote parent
    LAPI_Amsend (__x10_hndl,
                 0,
                 (void*) 5,
                 &p,
                 sizeof(x10_place_t),
                 NULL,
                 0,
                 NULL,
                 NULL,
                 NULL);
    ftree->parent = 0; //make 0 as my parent
  } else if(__x10_my_place == 0) { //Task0 parent
    //number of children of Task0 = #peers + #remote parents 
    ftree->numChild += ftree->numPeers ;
  }
  
  LAPI_Gfence(__x10_hndl);
  return X10_OK;
} 

void 
finishTerminate()
{
  delete ftree;
}
                   
x10_err_t
finishStart_ (int* cs)
{
  int tmp;
  
  if (ftree->parent != __x10_my_place) {
    numExceptions = 0;

    if (*cs > 0) return X10_OK;
    LRC (LAPI_Waitcntr (__x10_hndl, 
                  &cntr2,
                  1, 
                  &tmp));
    *cs = CONTINUE_STATUS;
  }

 
  if (ftree->numChild) {
    lapi_cntr_t originCntr;
     numExceptions = 0;
     bufSize = 0;
    for (int i = 0 ; i < ftree->numChild; i++) {

      LRC (LAPI_Setcntr (__x10_hndl, &originCntr, 0));
      LRC (LAPI_Amsend (__x10_hndl, 
                 ftree->children[i], 
                 (void*) 4,
                 cs,
                 sizeof(int),
                 NULL,
                 0,
                 (lapi_cntr_t*) continueCntr[i],
                 &originCntr,
                 NULL));
     LRC (LAPI_Waitcntr (__x10_hndl, &originCntr, 1, &tmp));
   }
     LRC (LAPI_Fence(__x10_hndl));
  }
  CONTINUE_STATUS = 0;
  return X10_OK;
}

x10_err_t
finishEnd_ (Exception* e)
{
  void* ex_buf = (void*) e;
  int esize = e ? e->size() : 0;

  LAPI_Fence (__x10_hndl);
  if (ftree->numChild) {
    if (e != NULL)  {
      memcpy (buffer+bufSize, e, e->size()); 
      bufSize += e->size();
      numExceptions++;
    }
    int tmp;
    LRC (LAPI_Waitcntr (__x10_hndl, &cntr1, ftree->numChild, &tmp));
    
    if (numExceptions) {
      if (__x10_my_place == 0) {
        Exception** e = new Exception*[numExceptions];
        int ex_size = bufSize / numExceptions;
        for (int  i = 0; i < numExceptions; i++) {
          e[i] = (Exception*) (buffer+i*ex_size);
        }
        throw MultiException(e, numExceptions);
      } else {
        ex_buf = buffer;
        esize = bufSize;
      } 
    } 
  }

  if (ftree->parent != __x10_my_place) {
    if (e != NULL) {
      if (ftree->numChild == 0)
        numExceptions++;
      lapi_cntr_t originCntr;
      int tmp;
      LRC (LAPI_Setcntr (__x10_hndl, &originCntr, 0));
      LRC (LAPI_Amsend (__x10_hndl,
			ftree->parent,
			(void*) 3, /*LAPI handler for exceptions */
			&numExceptions,
			sizeof(int),
			ex_buf,
			esize,
			(lapi_cntr_t*) exceptionCntr[0],
			&originCntr,
			NULL));
      LRC (LAPI_Waitcntr (__x10_hndl, &originCntr, 1, &tmp));
    } else {
      LRC (LAPI_Put (__x10_hndl, ftree->parent, 0, NULL, NULL, (lapi_cntr_t*) exceptionCntr[0], NULL, NULL)); 
    }
  }
  return X10_OK;
}

int
x10lib::finishStart (int cs)
{

  x10_err_t err = finishStart_ (&cs); 
  if (err != X10_OK) {
    throw err;
  }
 
  return cs;
}

void
x10lib::finishEnd (Exception* e)
{
  x10_err_t err = finishEnd_ (e);
  if (err != X10_OK) {
    throw err;
  } 
}

