/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: finish.cc,v 1.8 2007-06-25 14:08:25 ganeshvb Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/xmacros.h>
#include <x10/finish.h>

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

error_t            
finishInit ()
{
  LRC (LAPI_Addr_set (GetHandle(), (void*) exceptionHeaderHandler, 3));
  LRC (LAPI_Addr_set (GetHandle(), (void*) continueHeaderHandler, 4));
  LRC (LAPI_Addr_set (GetHandle(), (void*) numChildHeaderHandler, 5));
  LRC (LAPI_Address_init64 (GetHandle(), (lapi_long_t) &cntr1, exceptionCntr));
  LRC (LAPI_Address_init64 (GetHandle(), (lapi_long_t) &cntr2, continueCntr));
  //LRC (LAPI_Address_init (GetHandle(),  &cntr1, exceptionCntr));
  //LRC (LAPI_Address_init (GetHandle(),  &cntr2, continueCntr));
  LRC (LAPI_Setcntr (GetHandle(), &cntr1, 0));
  LRC (LAPI_Setcntr (GetHandle(), &cntr2, 0));

  //allocate the fence tree structure
  ftree = new ptree_t;

  //find my peers, i.e all the processes in the same node
  char* envstr = getenv ("MP_COMMON_TASKS");
  ftree->numPeers = envstr ? atoi(envstr): 0;

  //choose the one with the minimum rank as the parent of this group
  ftree->parent = here();

  for (int i = 0; i < ftree->numPeers; i++)  {
    envstr = strchr (envstr, ':') + 1;
    ftree->children[i] = atoi(envstr);
    ftree->parent = ftree->parent < ftree->children[i] ?  ftree->parent : ftree->children[i]; 
  }
   
  ftree->numChild = 0; //leaves have no children
  if (ftree->parent == here() && here() !=0) { //non-Task0 parent
    place_t p = here();
    ftree->numChild = ftree->numPeers;
    //send an active message to 0 to let it know that
    // I am a remote parent
    LAPI_Amsend (GetHandle(),
                 0,
                 (void*) 5,
                 &p,
                 sizeof(place_t),
                 NULL,
                 0,
                 NULL,
                 NULL,
                 NULL);
    ftree->parent = 0; //make 0 as my parent
  } else if(here() == 0) { //Task0 parent
    //number of children of Task0 = #peers + #remote parents 
    ftree->numChild += ftree->numPeers ;
  }
  
  LAPI_Gfence(GetHandle());
  return X10_OK;
} 

void 
finishTerminate()
{
  delete ftree;
}
                   
error_t
finishStart_ (int* cs)
{
  int tmp;
  
  if (ftree->parent != here()) {
    numExceptions = 0;

    if (*cs > 0) return X10_OK;
    LRC (LAPI_Waitcntr (GetHandle(), 
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

      LRC (LAPI_Setcntr (GetHandle(), &originCntr, 0));
      LRC (LAPI_Amsend (GetHandle(), 
                 ftree->children[i], 
                 (void*) 4,
                 cs,
                 sizeof(int),
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

error_t
finishEnd_ (Exception* e)
{
  void* ex_buf = (void*) e;
  int esize = e ? e->size() : 0;

  LAPI_Fence (GetHandle());
  if (ftree->numChild) {
    if (e != NULL)  {
      memcpy (buffer+bufSize, e, e->size()); 
      bufSize += e->size();
      numExceptions++;
    }
    int tmp;
    LRC (LAPI_Waitcntr (GetHandle(), &cntr1, ftree->numChild, &tmp));
    
    if (numExceptions) {
      if (here() == 0) {
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

  if (ftree->parent != here()) {
    if (e != NULL) {
      if (ftree->numChild == 0)
        numExceptions++;
      lapi_cntr_t originCntr;
      int tmp;
      LRC (LAPI_Setcntr (GetHandle(), &originCntr, 0));
      LRC (LAPI_Amsend (GetHandle(),
			ftree->parent,
			(void*) 3, /*LAPI handler for exceptions */
			&numExceptions,
			sizeof(int),
			ex_buf,
			esize,
			(lapi_cntr_t*) exceptionCntr[0],
			&originCntr,
			NULL));
      LRC (LAPI_Waitcntr (GetHandle(), &originCntr, 1, &tmp));
    } else {
      LRC (LAPI_Put (GetHandle(), ftree->parent, 0, NULL, NULL, (lapi_cntr_t*) exceptionCntr[0], NULL, NULL)); 
    }
  }
  return X10_OK;
}

int
x10lib::finishStart (int cs)
{

  error_t err = finishStart_ (&cs); 
  if (err != X10_OK) {
    throw err;
  }
 
  return cs;
}

void
x10lib::finishEnd (Exception* e)
{
  error_t err = finishEnd_ (e);
  if (err != X10_OK) {
    throw err;
  } 
}

