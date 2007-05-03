/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: update.c,v 1.2 2007-05-03 11:40:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/** Handlers and Update functions **/
#include <x10/x10lib.h>
#include "gups.h"

/* buffer for remote header handler */
uint64_t update_buffer[AGG_LIMIT];

/* routine to perform batch updates */
void
batch_update(uint64_t *value, ulong msg_len)
{
	for (; msg_len > 0; msg_len -= sizeof(uint64_t)) {
		uint64_t offset = *value & (table_size - 1);
		
		UPDATE(offset, *value);
		value++;
	}
}

/* remote completion handler */
void
complete_update(lapi_handle_t *hndl, void *completion_param)
{
	batch_update(update_buffer, (ulong)completion_param);
}


/* remote header handler */
void *
receive_update(lapi_handle_t *hndl, void *uhdr,
		uint *uhdr_len, ulong *msg_len, compl_hndlr_t **ucomp,
		void **uinfo)
{
	lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;
	
	if (ret_info->udata_one_pkt_ptr) {
		batch_update((uint64_t *)ret_info->udata_one_pkt_ptr, *msg_len);
		ret_info->ctl_flags = LAPI_BURY_MSG;
		*ucomp = NULL;
		return NULL;
	} else {
		ret_info->ret_flags = LAPI_LOCAL_STATE;
		*ucomp = complete_update;
		*uinfo = (void *)*msg_len;
		return (update_buffer);
	}
}

/* send an update */
void
send_update(int dest, uint64_t value)
{
	X10RC(x10_xfer((void *)&value, MakeGasRef(dest, 0),
			sizeof(uint64_t), (void *)1));
	nsends++;
}
