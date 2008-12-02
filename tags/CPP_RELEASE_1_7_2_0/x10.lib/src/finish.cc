/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: finish.cc,v 1.29 2008-02-15 09:49:27 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's finish logic. **/

#include <lapi.h>

#include <x10/am.h>
#include <x10/err.h>
#include <x10/finish.h>
#include <x10/types.h>
#include <x10/xassert.h>
#include <x10/xmacros.h>
#include "__x10lib.h__"

/* local types */
/* process tree structure */
struct ptree_t
{
	int numPeers;
	int numChild;
	int *children;
	int parent;
};

/* local data */
static int bufSize = 0;
static char *buffer;
static int numExceptions;

static lapi_cntr_t cntr1;
static lapi_cntr_t cntr2;
static lapi_long_t *exceptionCntr;
static lapi_long_t *continueCntr;

static int continue_status;

/* global data */
ptree_t *ftree=NULL;

using namespace x10lib;

/* local methods */
static void*
NumChildHeaderHandler(lapi_handle_t handle, void *uhdr,
			uint *hdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **uinfo);

static void*
ContinueHeaderHandler(lapi_handle_t handle, void *uhdr,
			uint *hdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **uinfo);

static void*
ExceptionHeaderHandler(lapi_handle_t handle, void *uhdr,
			uint *hdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **uinfo);

static x10_err_t FinishStart_(int *cs);
static x10_err_t FinishEnd_(Exception *e);

using namespace x10lib;

/* The following two methods should be available during
 * x10lib initialization; mark them as external.
 */

/* Initialize Finish Logic */
x10_err_t FinishInit()
{
	X10_DEBUG(1, "Entry");

	LRC(LAPI_Addr_set(__x10_hndl, (void *)ExceptionHeaderHandler,
			EXCEPTION_HEADER_HANDLER));
	LRC(LAPI_Addr_set(__x10_hndl, (void *)ContinueHeaderHandler,
			CONTINUE_HEADER_HANDLER));
	LRC(LAPI_Addr_set(__x10_hndl, (void *)NumChildHeaderHandler,
			NUM_CHILD_HEADER_HANDLER));

	exceptionCntr = new lapi_long_t [__x10_num_places];
	assert(exceptionCntr != 0);

	continueCntr = new lapi_long_t [__x10_num_places];
	assert(continueCntr != 0);

	buffer = new char [__x10_num_places * X10_EX_BUFFER_SIZE];
	assert(buffer != 0);

	// allocate the fence tree strucure
	ftree = new ptree_t;
	assert(ftree != 0);

	// find my peers, i.e. all the processes in the same node
	char *envstr = getenv("MP_COMMON_TASKS");
	ftree->numPeers = envstr ? atoi(envstr) : 0;

	// choose the one one with the minimum rank as the parent of this group
	ftree->parent = __x10_my_place;

	ftree->children = NULL;
	
	if (__x10_my_place)
	  ftree->children = ftree->numPeers ? new int [ftree->numPeers] : NULL;
	else
	  // an over-estimated value
	  ftree->children = new int [__x10_num_places];
	
	for (int i = 0; i < ftree->numPeers; i++) {
	  envstr = strchr(envstr, ':') + 1;
	  ftree->children[i] = atoi(envstr);
	  ftree->parent = ftree->parent < ftree->children[i] ?
	    ftree->parent : ftree->children[i];
	}
	ftree->numChild = 0; //leaves have no children

	LAPI_Gfence(__x10_hndl);

	X10_DEBUG(1, "Before if");
	if (ftree->parent == __x10_my_place && __x10_my_place != 0) {
		// non-task0 parent
		ftree->numChild = ftree->numPeers;

		// send an active message to 0 to let it know that
		// i am a remote parent
		// 
		LAPI_Amsend(__x10_hndl,
				0,
				(void *)NUM_CHILD_HEADER_HANDLER,
				&__x10_my_place,
				sizeof(x10_place_t),
				NULL,
				0,
				NULL, NULL, NULL);
		ftree->parent = 0; // make 0 as my parent
	} else if (__x10_my_place == 0) {
		// task0 parent
		// number of children of tasks0 = #peers + #remote parents
		// 
		ftree->numChild += ftree->numPeers;
	}
	X10_DEBUG(1, "After if");

	LRC(LAPI_Setcntr(__x10_hndl, &cntr1, 0));
	LRC(LAPI_Setcntr(__x10_hndl, &cntr2, 0));
	LRC(LAPI_Address_init64(__x10_hndl, (lapi_long_t)&cntr1,
			 exceptionCntr));
	LRC(LAPI_Address_init64(__x10_hndl, (lapi_long_t)&cntr2,
			 continueCntr));

	X10_DEBUG(1, "Exit");
	LAPI_Gfence(__x10_hndl);
	return X10_OK;
}

/* Terminate Finish Logic */
void FinishFinalize()
{
	delete [] exceptionCntr;
	delete [] continueCntr;
	delete [] buffer;

	if (ftree->children) {
		delete [] ftree->children;
	}
	delete ftree;
}

/* Beginning of x10lib's finish logic interface */
namespace x10lib {

/* Start Finish */
int FinishStart(int cs)
{
	int cs_ = cs;
	x10_err_t err = FinishStart_(&cs_);
	if (err != X10_OK) {
		throw err;
	}
	return cs_;
}

/* End Finish */
void FinishEnd(Exception *e)
{
	x10_err_t err = FinishEnd_(e);
	if (err != X10_OK) {
		throw err;
	}
	return;
}

} /* closing brace for namespace x10lib */

/* End of x10lib's finish logic interface */

/* local method implementations */
/* count the number of children for process 0 */
static void*
NumChildHeaderHandler(lapi_handle_t handle, void *uhdr,
			uint *hdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **uinfo)
{
	assert(ftree);
	ftree->children[ftree->numChild] = *((int *)uhdr);
	ftree->numChild++;
	lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
	ret_info->ctl_flags = LAPI_BURY_MSG;
	ret_info->ret_flags = LAPI_LOCAL_STATE;
	*comp_h = NULL;
	return NULL;
}

static void*
ContinueHeaderHandler(lapi_handle_t handle, void *uhdr,
			uint *hdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **uinfo)
{
	continue_status = *((uint *)uhdr);
	lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
	ret_info->ctl_flags = LAPI_BURY_MSG;
	ret_info->ret_flags = LAPI_LOCAL_STATE;
	*comp_h = NULL;
	return NULL;
}

static void*
ExceptionHeaderHandler(lapi_handle_t handle, void *uhdr,
			uint *hdr_len, ulong *msg_len,
			compl_hndlr_t **comp_h, void **uinfo)
{
	int child_exceptions = *((int *)uhdr);
	lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
	assert(ret_info->udata_one_pkt_ptr);
	memcpy((buffer + bufSize), ret_info->udata_one_pkt_ptr, *msg_len);
	bufSize += *msg_len;
	numExceptions += child_exceptions;
	ret_info->ctl_flags = LAPI_BURY_MSG;
	ret_info->ret_flags = LAPI_LOCAL_STATE;
	*comp_h = NULL;
	return NULL;
}

/* Internal implementation of finish start */
static x10_err_t FinishStart_(int *cs)
{
	X10_DEBUG(1, "Entry" << ftree->parent);
	int tmp;

	//CHILDREN 
	if (ftree->parent != __x10_my_place) {
		X10_DEBUG(1, "CHILD" << "CS: " << *cs << " ");
		numExceptions = 0;

		if (*cs > 0) return X10_OK;

   		//wait on CONTINUE_STATUS 
		LRC(LAPI_Waitcntr(__x10_hndl, &cntr2, 1, &tmp));
		*cs = continue_status;
		X10_DEBUG(1, "CHILD DONE");
	}

	//PARENT 
	if (ftree->numChild) {
		lapi_cntr_t originCntr;
		numExceptions = 0;
		bufSize = 0;
		X10_DEBUG(1, "PARENT");

	        //write CS into CONTINUE_STATUES for each child 
		for (int i = 0; i < ftree->numChild; i++) {
			X10_DEBUG(1, "PARENT SENDS TO " << " " <<
				ftree->children[i]);
			LRC(LAPI_Setcntr(__x10_hndl, &originCntr, 0));
			LRC(LAPI_Amsend(__x10_hndl,
					ftree->children[i],
					(void *)CONTINUE_HEADER_HANDLER,
					cs, sizeof(int), NULL, 0,
					(lapi_cntr_t *)continueCntr[i],
					&originCntr, NULL));
			LRC(LAPI_Waitcntr(__x10_hndl, &originCntr, 1, &tmp));
		}

		X10_DEBUG(1, "PARENT BEFORE FENCE");

		//LAPI LOCAL_FENCE 
		LRC(LAPI_Fence(__x10_hndl));
		X10_DEBUG(1, "PARENT DONE");
	}
	continue_status = 0;
	X10_DEBUG(1, "Exit");
	return X10_OK;
}

/* Internal implementation of finish end */
static x10_err_t FinishEnd_(Exception *e)
{
	X10_DEBUG(1, "Entry");
	void *ex_buf = (void *)e;
	int esize = e ? e->size() : 0;

	// LAPI LOCAL_FENCE
	LAPI_Fence(__x10_hndl);

	//PARENT
	if (ftree->numChild) {

    		//append the exception to exception buffer 
		if (e != NULL) {
			memcpy((buffer + bufSize), e, e->size());
			bufSize += e->size();
			numExceptions++;
		}
		
		//wait until the counter reaches N-1 
		int tmp;
		LRC(LAPI_Waitcntr(__x10_hndl, &cntr1, ftree->numChild, &tmp));

		//if buffer not empty throw MultiException
		if (numExceptions) {
			if (__x10_my_place == 0) {
				Exception **e = new Exception*[numExceptions];
				assert (e != 0);
				int ex_size = bufSize / numExceptions;
				for (int i = 0; i < numExceptions; i++) {
					e[i] = (Exception *) (buffer + (i * ex_size));
				}
				throw MultiException(e, numExceptions);
			} else {
				ex_buf = buffer;
				esize = bufSize;
			}
		}
	}

	//CHILD 
	if (ftree->parent != __x10_my_place) {
		X10_DEBUG(1, "CHILD");

		if (e != NULL) {//if exception not null, perform an active message send 
			if (ftree->numChild == 0)
				numExceptions++;
			lapi_cntr_t originCntr;
			int tmp;

		        //append the exception on parent and increment the counter 
			LRC(LAPI_Setcntr(__x10_hndl, &originCntr, 0));
			LRC(LAPI_Amsend(__x10_hndl,
				ftree->parent,
				(void *)EXCEPTION_HEADER_HANDLER,
				&numExceptions,
				sizeof(int),
				ex_buf,
				esize,
				(lapi_cntr_t *)exceptionCntr[0],
				&originCntr, NULL));
			LRC(LAPI_Waitcntr(__x10_hndl, &originCntr, 1, &tmp));
		} else { //else just increment the Gfence counter 
			X10_DEBUG(1, "CHILD");
			LRC(LAPI_Put(__x10_hndl, ftree->parent, 0, NULL,
					NULL, (lapi_cntr_t *)exceptionCntr[0],
					NULL, NULL));
		}
		LAPI_Fence(__x10_hndl);
		X10_DEBUG(1, "CHILD DONE");
	}
	X10_DEBUG(1, "Exit");
	return X10_OK;
}
