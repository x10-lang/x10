/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime.annotations;

/* enumerations for each of TR's optimization levels
 */

public enum OptLevel {
   NOOPT,
   COLD,
   WARM,
   HOT,
   VERYHOT,
   SCORCHING,
   AOT
};

