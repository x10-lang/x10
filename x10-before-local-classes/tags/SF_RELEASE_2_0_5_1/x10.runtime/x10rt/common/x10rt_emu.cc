#include <x10rt_net.h>
#include <x10rt_internal.h>
#include <x10rt_types.h>

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
        char *buf = (char*) p->msg;
        x10rt_remote_ptr victim;
        // FIXME: SERIALIZATION FRAMEWORK
        ::memcpy(&victim, buf+0, 8);
        ::memcpy(&value, buf+8, 8);
        victim_ = (unsigned long long*)(size_t)victim;
    }

    void *pack_remote_op_data (x10rt_remote_ptr victim, unsigned long long value)
    {
        // FIXME: SERIALIZATION FRAMEWORK
        char *buf = (char*)malloc(16);
        ::memcpy(buf+0, &victim, 8);
        ::memcpy(buf+8, &value, 8);
        return buf;
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
    x10rt_msg_type id;
    switch (type) {
        case X10RT_OP_ADD: id=REMOTE_ADD_ID; break;
        case X10RT_OP_AND: id=REMOTE_AND_ID; break;
        case X10RT_OP_OR:  id=REMOTE_OR_ID; break;
        case X10RT_OP_XOR: id=REMOTE_XOR_ID; break;
        default:
            fprintf(stderr,"Garbage op type given to x10rt_remote_op.\n");
            abort();
    }
    x10rt_msg_params params = {place, id, pack_remote_op_data(victim,value), 16};
    x10rt_net_send_msg(&params);
    x10rt_net_probe();
    free(params.msg);
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
