/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#include <x10rt_net.h>
#include <x10rt_internal.h>
#include <x10rt_types.h>
#include <x10rt_ser.h>
#include <x10rt_front.h>

#include <cstring>
#include <cstdio>

namespace {

    x10rt_msg_type REMOTE_ADD_ID;
    x10rt_msg_type REMOTE_AND_ID;
    x10rt_msg_type REMOTE_OR_ID;
    x10rt_msg_type REMOTE_XOR_ID;

    void extract_remote_op_data (const x10rt_msg_params *p,
                                 unsigned long long *&victim_, unsigned long long &value)
    {
        x10rt_remote_ptr victim;
        x10rt_deserbuf b; x10rt_deserbuf_init(&b, p);
        x10rt_deserbuf_read(&b, &victim);
        x10rt_deserbuf_read(&b, &value);
        victim_ = (unsigned long long*)(size_t)victim;
    }

    void recv_remote_add (const x10rt_msg_params *p) {
        unsigned long long *victim;
        unsigned long long value;
        extract_remote_op_data(p, victim, value);
        *victim += value;
    }

    void recv_remote_and (const x10rt_msg_params *p) {
        unsigned long long *victim;
        unsigned long long value;
        extract_remote_op_data(p, victim, value);
        *victim &= value;
    }

    void recv_remote_or (const x10rt_msg_params *p) {
        unsigned long long *victim;
        unsigned long long value;
        extract_remote_op_data(p, victim, value);
        *victim |= value;
    }

    void recv_remote_xor (const x10rt_msg_params *p) {
        unsigned long long *victim;
        unsigned long long value;
        extract_remote_op_data(p, victim, value);
        *victim ^= value;
    }

}

void x10rt_emu_remote_op (x10rt_place place, x10rt_remote_ptr victim,
                          x10rt_op_type type, unsigned long long value)
{
    x10rt_msg_type id = REMOTE_ADD_ID;
    switch (type) {
        case X10RT_OP_ADD: id=REMOTE_ADD_ID; break;
        case X10RT_OP_AND: id=REMOTE_AND_ID; break;
        case X10RT_OP_OR:  id=REMOTE_OR_ID; break;
        case X10RT_OP_XOR: id=REMOTE_XOR_ID; break;
        default:
            // caller should ensure this never happens
            fprintf(stderr,"Garbage op type given to x10rt_emu_remote_op.\n");
    }
    x10rt_serbuf b; x10rt_serbuf_init(&b, place, id);
    x10rt_serbuf_write(&b, &victim);
    x10rt_serbuf_write(&b, &value);
    x10rt_net_send_msg(&b.p);
    x10rt_net_probe();
    x10rt_serbuf_free(&b);
}

void x10rt_emu_remote_ops (x10rt_remote_op_params *opv, size_t opc)
{
    for (size_t i=0 ; i<opc ; ++i) {
        x10rt_op_type type = (x10rt_op_type)opv[i].op;
        x10rt_emu_remote_op(opv[i].dest, opv[i].dest_buf, type, opv[i].value);
    }
}

void x10rt_emu_init (x10rt_msg_type *counter)
{
    REMOTE_ADD_ID = (*counter)++;
    x10rt_net_register_msg_receiver(REMOTE_ADD_ID, &recv_remote_add);
    REMOTE_AND_ID = (*counter)++;
    x10rt_net_register_msg_receiver(REMOTE_AND_ID, &recv_remote_and);
    REMOTE_OR_ID  = (*counter)++;
    x10rt_net_register_msg_receiver(REMOTE_OR_ID, &recv_remote_or);
    REMOTE_XOR_ID = (*counter)++;
    x10rt_net_register_msg_receiver(REMOTE_XOR_ID ,&recv_remote_xor);
}


// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
