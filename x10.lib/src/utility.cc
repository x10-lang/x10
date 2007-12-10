/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: utility.cc,v 1.2 2007-12-10 12:12:05 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Set of utilities to export some globals. **/

#include <x10/utility.h>
#include "__x10lib.h__"

namespace x10lib {

/* Retrieve number of places. */
int numPlaces(void)
{
	return __x10_num_places;
}

/* Retrieve the current place id. */
x10_place_t here(void)
{
	return (x10_place_t)__x10_my_place;
}

/* Retrieve the current communication (LAPI) handle. */
lapi_handle_t GetHandle(void)
{
	return __x10_hndl;
}

} /* closing brace for namespace x10lib */

/* Retrieve number of places. */
int x10_num_places(void)
{
	return x10lib::numPlaces();
}

/* Retrieve the current place id. */
x10_place_t x10_here(void)
{
	return x10lib::here();
}

/* Retrieve the current communication (LAPI) handle. */
lapi_handle_t x10_get_handle(void)
{
	return x10lib::GetHandle();
}
