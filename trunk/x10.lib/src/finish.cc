/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: finish.cc,v 1.21 2007-11-22 04:12:25 ganeshvb Exp $ */

#include <iostream>
#include <x10/xassert.h>
#include <x10/xmacros.h>
#include <x10/finish.h>
#include <lapi.h>

using namespace std;
using namespace x10lib; 

namespace x10lib {
  extern lapi_handle_t __x10_hndl;
}

#define X10_EX_BUFFER_SIZE  1024

static int bufSize = 0;
static char* buffer; 
static int numExceptions;
 
static lapi_cntr_t cntr1;
static lapi_cntr_t cntr2;
static lapi_long_t* exceptionCntr;
static lapi_long_t* continueCntr;

static int CONTINUE_STATUS;

/* process tree structure*/
struct ptree_t
{
  int numPeers;
  int numChild;
  int* children;
  int parent;
};

ptree_t* ftree=NULL;

/* count the number of children for process 0*/
static void*
numChildHeaderHandler (lapi_handle_t handle, void* uhdr,
                              uint* hdr_len, ulong* msg_len,
                              compl_hndlr_t** comp_h, void** uinfo)
{
  assert (ftree);
  ftree->children[ftree->numChild] = *((int*) uhdr);
  ftree->numChild++;
  lapi_return_info_t* ret_info = (lapi_return_info_t*) msg_len;
  ret_info->ctl_flags = LAPI_BURY_MSG;
  *comp_h = NULL;
  return NULL;
}

static void*
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


static void* 
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
  X10_DEBUG (1, "ENTRY");
  
  LRC (LAPI_Addr_set (__x10_hndl, (void*) exceptionHeaderHandler, EXCEPTION_HEADER_HANDLER));
  LRC (LAPI_Addr_set (__x10_hndl, (void*) continueHeaderHandler, CONTINUE_HEADER_HANDLER));
  LRC (LAPI_Addr_set (__x10_hndl, (void*) numChildHeaderHandler, NUM_CHILD_HEADER_HANDLER));
  
  exceptionCntr = new lapi_long_t [__x10_num_places];

  continueCntr = new lapi_long_t [__x10_num_places];

  buffer = new char [__x10_num_places * X10_EX_BUFFER_SIZE];

  //allocate the fence tree structure
  ftree = new ptree_t;

  //find my peers, i.e all the processes in the same node
  char* envstr = getenv ("MP_COMMON_TASKS");
  ftree->numPeers = envstr ? atoi(envstr): 0;

  //choose the one with the minimum rank as the parent of this group
  ftree->parent = __x10_my_place;

  ftree->children = NULL;
  if (__x10_my_place)
    ftree->children = ftree->numPeers ? new int [ftree->numPeers] : NULL;
  else 
    ftree->children = new int [__x10_num_places];//an over-estimated value

  for (int i = 0; i < ftree->numPeers; i++)  {
    envstr = strchr (envstr, ':') + 1;
    ftree->children[i] = atoi(envstr);
    ftree->parent = ftree->parent < ftree->children[i] ?  ftree->parent : ftree->children[i]; 
  }
   
  ftree->numChild = 0; //leaves have no children

  LAPI_Gfence (__x10_hndl);

  X10_DEBUG (1, "Before if");
  if (ftree->parent == __x10_my_place && __x10_my_place !=0) { //non-Task0 parent
    ftree->numChild = ftree->numPeers;
    //send an active message to 0 to let it know that
    // I am a remote parent
    LAPI_Amsend (__x10_hndl,
                 0,
                 (void*) NUM_CHILD_HEADER_HANDLER,
                 &__x10_my_place,
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
  X10_DEBUG (1, "After if");

  LRC (LAPI_Setcntr (__x10_hndl, &cntr1, 0));
  LRC (LAPI_Setcntr (__x10_hndl, &cntr2, 0));
  LRC (LAPI_Address_init64 (__x10_hndl, (lapi_long_t) &cntr1, exceptionCntr));
  LRC (LAPI_Address_init64 (__x10_hndl, (lapi_long_t) &cntr2, continueCntr));
  
  X10_DEBUG (1, "Exit" );
  LAPI_Gfence(__x10_hndl);


  //cout << "Stat: " << __x10_my_place << " " << ftree->parent << " " << ftree->numChild << " "
       //<< ftree->numPeers << " " << endl;
  return X10_OK;
} 

void 
finishFinalize()
{
  delete [] exceptionCntr;
  delete [] continueCntr;
  delete [] buffer;

  if (ftree->children) {
    delete [] ftree->children;
  }
  delete ftree;
}
                   
static x10_err_t
finishStart_ (int* cs)
{
  X10_DEBUG (1, "ENTRY" << ftree->parent);
  int tmp;
  
  assert (ftree);

  if (ftree->parent != __x10_my_place) {
     X10_DEBUG (1, "CHILD" << "CS: " << *cs << " " );
    numExceptions = 0;

   if (*cs > 0) return X10_OK;
    LRC (LAPI_Waitcntr (__x10_hndl, 
                  &cntr2,
                  1, 
                  &tmp));
    *cs = CONTINUE_STATUS;
     X10_DEBUG (1, "CHILD_DONE");
  }

 
  if (ftree->numChild) {
    lapi_cntr_t originCntr;
     numExceptions = 0;
     bufSize = 0;
    X10_DEBUG (1, "PARENT");
    for (int i = 0 ; i < ftree->numChild; i++) {

      X10_DEBUG(1, "PARENT SENDS TO " << " " << ftree->children[i]);
      LRC (LAPI_Setcntr (__x10_hndl, &originCntr, 0));
      LRC (LAPI_Amsend (__x10_hndl, 
                 ftree->children[i], 
                 (void*) CONTINUE_HEADER_HANDLER,
                 cs,
                 sizeof(int),
                 NULL,
                 0,
                 (lapi_cntr_t*) continueCntr[i],
                 &originCntr,
                 NULL));
     LRC (LAPI_Waitcntr (__x10_hndl, &originCntr, 1, &tmp));
   }
    X10_DEBUG (1, "PARENT_BEFORE_FENCE");
     LRC (LAPI_Fence(__x10_hndl));
    X10_DEBUG (1, "PARENT_DONE");
  }
 
  CONTINUE_STATUS = 0;

  X10_DEBUG (1, "EXIT");
  return X10_OK;
}

static x10_err_t
finishEnd_ (Exception* e)
{
  X10_DEBUG (1, "ENTRY");
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
      X10_DEBUG (1, "PARENT");
    if (e != NULL) {
      if (ftree->numChild == 0)
        numExceptions++;
      lapi_cntr_t originCntr;
      int tmp;
      LRC (LAPI_Setcntr (__x10_hndl, &originCntr, 0));
      LRC (LAPI_Amsend (__x10_hndl,
			ftree->parent,
			(void*) EXCEPTION_HEADER_HANDLER, /*LAPI handler for exceptions */
			&numExceptions,
			sizeof(int),
			ex_buf,
			esize,
			(lapi_cntr_t*) exceptionCntr[0],
			&originCntr,
			NULL));
      LRC (LAPI_Waitcntr (__x10_hndl, &originCntr, 1, &tmp));
      X10_DEBUG (1, "PARENT");
    } else {
      X10_DEBUG (1, "CHILD");
      LRC (LAPI_Put (__x10_hndl, ftree->parent, 0, NULL, NULL, (lapi_cntr_t*) exceptionCntr[0], NULL, NULL)); 
      LAPI_Fence (__x10_hndl);
      X10_DEBUG (1, "CHILD_DONE");
    }
  }
  X10_DEBUG (1, "EXIT");
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

