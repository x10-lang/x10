/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef X10AUX_HASH_H
#define X10AUX_HASH_H

#include <x10aux/config.h>

namespace x10aux {


    // the following code taken from http://burtleburtle.net/bob/hash/doobs.html


    /*
    ------------------------------------------------------------------------------------------------
    hash() -- hash a variable-length key into a 32-bit value
      k       : the key (the unaligned variable-length array of bytes)
      len     : the length of the key, counting by bytes
      initval : can be any 4-byte value

    Returns a 32-bit value.  Every bit of the key affects every bit of the return value.  Every
    1-bit and 2-bit delta achieves avalanche.  About 6*len+35 instructions.

    By Bob Jenkins, 1996.  bob_jenkins@burtleburtle.net.  You may use this code any way you wish,
    private, educational, or commercial.  It's free.

    See http://burtleburtle.net/bob/hash/evahash.html Use for hash table lookup, or anything where
    one collision in 2^^32 is acceptable.  Do NOT use for cryptographic purposes.
    ------------------------------------------------------------------------------------------------
    */


    /*
    ------------------------------------------------------------------------------------------------
    mix -- mix 3 32-bit values reversibly.

    For every delta with one or two bits set, and the deltas of all three high bits or all three low
    bits, whether the original value of a,b,c is almost all zero or is uniformly distributed,

    * If mix() is run forward or backward, at least 32 bits in a,b,c have at least 1/4 probability
      of changing.

    * If mix() is run forward, every bit of c will change between 1/3 and 2/3 of the time.  (Well,
      22/100 and 78/100 for some 2-bit deltas.)

    mix() was built out of 36 single-cycle latency instructions in a structure that could supported
    2x parallelism, like so:

          a -= b; 
          a -= c; x = (c>>13);
          b -= c; a ^= x;
          b -= a; x = (a<<8);
          c -= a; b ^= x;
          c -= b; x = (b>>13);
          ...

    Unfortunately, superscalar Pentiums and Sparcs can't take advantage of that parallelism.
    They've also turned some of those single-cycle latency instructions into multi-cycle latency
    instructions.  Still, this is the fastest good hash I could find.  There were about 2^^68 to
    choose from.  I only looked at a billion or so.
    ------------------------------------------------------------------------------------------------
    */

    #define mix(a,b,c) \
    { \
        a -= b; a -= c; a ^= (c>>13); \
        b -= c; b -= a; b ^= (a<<8); \
        c -= a; c -= b; c ^= (b>>13); \
        a -= b; a -= c; a ^= (c>>12);  \
        b -= c; b -= a; b ^= (a<<16); \
        c -= a; c -= b; c ^= (b>>5); \
        a -= b; a -= c; a ^= (c>>3);  \
        b -= c; b -= a; b ^= (a<<10); \
        c -= a; c -= b; c ^= (b>>15); \
    }

    static inline x10_int hash(const unsigned char *k, x10_int length, x10_int c=0) {

        /* Set up the internal state */
        x10_int a,b;
        a = b = 0x9e3779b9;  /* the golden ratio; an arbitrary value */

        /*---------------------------------------- handle most of the key */
        x10_int len;
        for (len=length ; len>=12 ; k+=12,len-=12) {
            a += (k[0] +((x10_int)k[1]<<8) +((x10_int)k[2]<<16) +((x10_int)k[3]<<24));
            b += (k[4] +((x10_int)k[5]<<8) +((x10_int)k[6]<<16) +((x10_int)k[7]<<24));
            c += (k[8] +((x10_int)k[9]<<8) +((x10_int)k[10]<<16)+((x10_int)k[11]<<24));
            mix(a,b,c);
        }

        /*------------------------------------- handle the last 11 bytes */
        c += length;
        switch(len) {             /* all the case statements fall through */
            case 11: c+=((x10_int)k[10]<<24);
            case 10: c+=((x10_int)k[9]<<16);
            case 9 : c+=((x10_int)k[8]<<8);
            /* the first byte of c is reserved for the length */
            case 8 : b+=((x10_int)k[7]<<24);
            case 7 : b+=((x10_int)k[6]<<16);
            case 6 : b+=((x10_int)k[5]<<8);
            case 5 : b+=k[4];
            case 4 : a+=((x10_int)k[3]<<24);
            case 3 : a+=((x10_int)k[2]<<16);
            case 2 : a+=((x10_int)k[1]<<8);
            case 1 : a+=k[0];
            /* case 0: nothing left to add */
        }
        mix(a,b,c);
        /*-------------------------------------------- report the result */

        return c;
    }

}

#endif
// vim:textwidth=100:tabstop=4:shiftwidth=4:expandtab
