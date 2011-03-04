/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: utility.cc,v 1.1 2007-06-26 07:38:08 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Set of utilities to export some globals. **/

#include <x10/utility.h>

namespace x10lib {

/* Retrieve number of places. */
int numPlaces(void)
{
	extern int __x10_num_places;

	return __x10_num_places;
}

/* Retrieve the current place id. */
x10_place_t here(void)
{
	extern int __x10_my_place;

	return (x10_place_t)__x10_my_place;
}

/* Retrieve the current communication (LAPI) handle. */
lapi_handle_t GetHandle(void)
{
	extern lapi_handle_t __x10_hndl;

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
