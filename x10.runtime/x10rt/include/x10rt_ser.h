/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10RT_SER_H
#define X10RT_SER_H

#ifdef __cplusplus
#include <cstdlib>
#include <cstring>
#else
#include <stdlib.h>
#include <string.h>
#endif

/** \file
 * Serialization framework
 *
 * As a user, when preparing messages to send, you can use the definitions in
 * this file to help you pack data into a buffer for sending, without having to
 * worry about endianness, alignment, or padding.
 *
 * An example of serialization (note that the types of data and other_data must
 * be equally sized on both sides of the message, so it is best to use the
 * fixed-size types in stdint.h, such as uint64_t): \code

x10rt_serbuf b; x10rt_serbuf_init(&b, place, msg_id);
x10rt_serbuf_write(&b, &data);
x10rt_serbuf_write(&b, &other_data);
x10rt_send_msg(&b.p);
x10rt_serbuf_free(&b);

 * \endcode
 *
 * An example of deserialization:
 * \code

    x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
    uint64_t data1; x10rt_deserbuf_read(&b, &data1);
    uint64_t data2; x10rt_deserbuf_read(&b, &data2);

 * \endcode
 */

/** \name Serialization */
/** \{ */

/** This object represents a buffer that would be used for a message send.  It
 * is mostly opaque and should be initialised / manipulated using the calls in
 * \ref x10rt_ser.h
 */
struct x10rt_serbuf {
    /** The parameters of the message to send.  */ x10rt_msg_params p;
    /** The current size of the buffer.  */ size_t cap;
};

/** Initialize a buffer.  Note that tihs should be paired with an
 * #x10rt_serbuf_free call when the buffer is no longer needed.
 * \param b The buffer that has not been initialized yet
 * \param dest The buffer will be sent here
 * \param id The message id where this message will be used
 */
static inline void x10rt_serbuf_init (x10rt_serbuf *b, x10rt_place dest, x10rt_msg_type id)
{
    b->p.msg = NULL;
    b->p.dest_place = dest;
    b->p.dest_endpoint = 0;
    b->p.type = id;
    b->p.len = 0;
    b->cap = 0;
}

/** Clean up a buffer.
 * \param b The buffer that is nolonger needed
 */
static inline void x10rt_serbuf_free (x10rt_serbuf *b)
{
    free(b->p.msg);
}

/** Increase the size of the buffer by 30% if it is not big enough to hold sz additional bytes.
 * \param b The buffer being manipulated
 * \param sz The amount of data that needs to be packed 
 */
static inline void x10rt_serbuf_ensure (x10rt_serbuf *b, size_t sz)
{
    if (b->p.len+sz > b->cap) {
        size_t nu = ((b->p.len+sz) * 13) / 10;
        b->p.msg = realloc(b->p.msg, nu);
        b->cap = nu;
    }
}

/** The current location of the 'cursor' in the buffer.  This can be used to
 * overwrite a part of the buffer to reuse it many times for different
 * messages.  \param b The buffer being manipulated
 */
static inline size_t x10rt_serbuf_record (x10rt_serbuf *b)
{
    return b->p.len;
}

/** Puts the given word in network byte order.
 * \param addr The location of the data to manipulate
 * \param sz The size of the word in bytes
 */
static inline void x10rt_swap_if_le (unsigned char *addr, size_t sz)
{
    (void) addr; (void) sz;
    #if !defined(HOMOGENEOUS) && (defined(__i386__) || defined(__x86_64__))
    for (size_t i=0,j=sz-1 ; i<j ; ++i,--j) {
        unsigned char tmp = addr[i];
        addr[i] = addr[j];
        addr[j] = tmp;
    }
    #endif
}

/** Write the given data elements into the buffer at a given location.  The data at this
 * location will be overwritten.  It is preferable to use #x10rt_serbuf_write_at
 * if possible.
 *
 * \param b The buffer into which to pack the data
 *
 * \param at The location within the buffer at which to write.  Must have been
 * returned by #x10rt_serbuf_record.
 *
 * \param d_ The address of the data to write.
 *
 * \param sz The size of each element in bytes
 *
 * \param cnt The number of elements to write
 */
static inline void x10rt_serbuf_write_at_ex (x10rt_serbuf *b,
                                             size_t at, const void *d_, size_t sz, size_t cnt)
{
    unsigned char *d = (unsigned char*)d_;
    unsigned char *bbuf = (unsigned char*)b->p.msg;
    for (size_t i=0 ; i<cnt ; ++i) {
        memcpy(&bbuf[at], d, sz);
        x10rt_swap_if_le(&bbuf[at], sz);
        at += sz;
        d += sz;
    }
}

/** A macro to write a single element into the buffer at a given location.  The
 * type of d is used to decide how much data to write.  The data at this
 * location will be overwritten.
 *
 * \param b The buffer into which to pack the data
 *
 * \param a The location within the buffer at which to write.  Must have been
 * returned by #x10rt_serbuf_record.
 *
 * \param d The address of the variable to write.
 */
#define x10rt_serbuf_write_at(b,a,d) x10rt_serbuf_write_at_ex(b, a, d, sizeof(*d), 1)

/** Write the given data elements into the buffer.  It is preferable to use
 * #x10rt_serbuf_write if possible.
 * \param b The buffer into which to pack the data
 * \param d_ The address of the data to write.
 * \param sz The size of each element in bytes
 * \param cnt The number of elements to write
 */
static inline void x10rt_serbuf_write_ex (x10rt_serbuf *b, const void *d_, size_t sz, size_t cnt)
{
    x10rt_serbuf_ensure(b, sz*cnt);
    x10rt_serbuf_write_at_ex(b, b->p.len, d_, sz, cnt);
    b->p.len += cnt*sz;
}

/** A macro to write a single element into the buffer.  The type of d is used to decide how much data
 * to write.
 * \param b The buffer into which to pack the data
 * \param d The address of the variable to write.
 */
#define x10rt_serbuf_write(b,d) x10rt_serbuf_write_ex(b, d, sizeof(*d), 1)


/** \} */

/** \name Deserialization */
/** \{ */

/** This object represents a buffer that would be used for a message receive.  It
 * is mostly opaque and should be initialised / manipulated using the calls in
 * \ref x10rt_ser.h
 */
struct x10rt_deserbuf {
    /** The buffer that was received. */
    void *buf;
    /** The positoin in the buffer */
    size_t cursor;
};

/** Initialize a buffer.  Note that this does not need to be paired with any 'free' call.
 * \param b The buffer that has not been initialized yet
 * \param p The parameters of the message being received
 */
static inline void x10rt_deserbuf_init (x10rt_deserbuf *b, const x10rt_msg_params *p)
{
    b->buf = p->msg;
    b->cursor = 0;
}

/** Read many elements from the buffer.  It is preferable to use #x10rt_deserbuf_read if possible.
 * \param b The buffer to be read from 
 * \param d_ The memory in which to receive the data
 * \param sz The size of each element of data
 * \param cnt The number of elements of data to read
 */
static inline void x10rt_deserbuf_read_ex (x10rt_deserbuf *b, void *d_, size_t sz, size_t cnt)
{
    unsigned char *d = (unsigned char*)d_;
    unsigned char *bbuf = (unsigned char*)b->buf;
    for (size_t i=0 ; i<cnt ; ++i) {
        memcpy(d, &bbuf[b->cursor], sz);
        x10rt_swap_if_le(d, sz);
        b->cursor += sz;
        d += sz;
    }
}

/** Macro to read a single element from the buffer.  The type of d is used to decide how many bytes
 * to read.
 * \param b The buffer to be read from 
 * \param d The address of a variable in which to receive the data.
 */
#define x10rt_deserbuf_read(b,d) x10rt_deserbuf_read_ex(b, d, sizeof(*d), 1)

/** \} */

#endif
