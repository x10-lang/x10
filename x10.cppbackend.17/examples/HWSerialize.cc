#include "HWSerialize.h"
using namespace x10;
static x10::ref<Exception> EXCEPTION = NULL;

array_init_closure_and_args_struct(HWSerialize, 0, x10_int, array_init_unpacked_body((x10::ref<point> __var0__),
{
    x10_int p = __var0__->operator[](0);
    return p;
}, ), (void* args, x10::ref<point> __var0__),
struct __init__0_args : public x10::closure_args { __init__0_args() { } };
);

array_init_closure_and_args_struct(HWSerialize, 1, x10::ref<place>, array_init_unpacked_body((x10::ref<point> __var1__),
{
    x10_int p = __var1__->operator[](0);
    return ((x10::lang::place)p);
//    return HWSerialize::GLOBAL_STATE.h;
}, ), (void* args, x10::ref<point> __var1__),
struct __init__1_args : public x10::closure_args { __init__1_args() { } };
);

extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWSerialize::main(args);
        } catch(int exitCode) {
            x10::exitCode = exitCode;
        } catch(x10::__ref& e) {
            fprintf(stderr, "%d: ", (int)__here__);
            //fprintf(stderr, "Caught %p\n", e._val);
            ((const x10::ref<Exception>&)e)->printStackTrace(System::x10__out);
            x10::exitCode = 1;
        } catch(...) {
            fprintf(stderr, "%d: Caught exception\n", (int)__here__);
            x10::exitCode = 1;
        }
        x10::free_args(args);
        return x10::exitCode;
    }
}
// the original app-main method
void HWSerialize::main(x10::ref<x10::array<x10::ref<String> > > args) {
    x10::ref<place> h;
    x10::ref<point> q;
    x10::ref<region> r1;
    x10::ref<region> r2;
    x10::ref<dist> dU;
    x10::ref<dist> dU1;
    x10::ref<dist> dl;
    x10::ref<dist> de;
    x10::ref<x10::x10array<x10_int> > ia;
    x10::ref<x10::x10array<x10::ref<place> > > ip;
    if (__here__ != 0) goto SKIP_s2;
    h = __here__;
    q = (x10::ref<x10::lang::_point<2> >)(new (x10::alloc<x10::lang::_point<2> >()) x10::lang::_point<2>(0, 1));
    r1 = (x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(2, 3));
    r2 = (x10::ref<_region<2> >)(new (x10::alloc<_region<2> >()) _region<2>(
                                        (x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(4, 5)),
                                        (x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(6, 7))));
    dU = dist::x10__UNIQUE;
    dU1 = dist::x10__UNIQUE | (__here__);
    dl = (x10::ref<_dist_local>)(new (x10::alloc<_dist_local >()) _dist_local(r2, __here__));
    de = dl | (((x10::lang::place)1));
    ia = array_init_closure_invocation(0, r1->toDistribution(), x10_int, ());
    ip = array_init_closure_invocation(1, r1->toDistribution(), x10::ref<place>, ());
SKIP_s2: ;
    x10::serialization_buffer h__buf;
    if (__here__ == 0)
        x10::serialize_value_type(h__buf, h);
    /* Broadcast value h */;
    buffer_broadcast(h__buf);
    if (__here__ != 0)
        h = x10::deserialize_value_type<place>(h__buf);
    HWSerialize::GLOBAL_STATE.h = h;
    x10::serialization_buffer q__buf;
    if (__here__ == 0)
        x10::serialize_value_type(q__buf, q);
    /* Broadcast value q */;
    buffer_broadcast(q__buf);
    if (__here__ != 0)
        q = x10::deserialize_value_type<point>(q__buf);
    HWSerialize::GLOBAL_STATE.q = q;
    x10::serialization_buffer r1__buf;
    if (__here__ == 0)
        x10::serialize_value_type(r1__buf, r1);
    /* Broadcast value r1 */;
    buffer_broadcast(r1__buf);
    if (__here__ != 0)
        r1 = x10::deserialize_value_type<region>(r1__buf);
    HWSerialize::GLOBAL_STATE.r1 = r1;
    x10::serialization_buffer r2__buf;
    if (__here__ == 0)
        x10::serialize_value_type(r2__buf, r2);
    /* Broadcast value r2 */;
    buffer_broadcast(r2__buf);
    if (__here__ != 0)
        r2 = x10::deserialize_value_type<region>(r2__buf);
    HWSerialize::GLOBAL_STATE.r2 = r2;
    x10::serialization_buffer dU__buf;
    if (__here__ == 0)
        x10::serialize_value_type(dU__buf, dU);
    /* Broadcast value dU */;
    buffer_broadcast(dU__buf);
    if (__here__ != 0)
        dU = x10::deserialize_value_type<dist>(dU__buf);
    HWSerialize::GLOBAL_STATE.dU = dU;
    x10::serialization_buffer dU1__buf;
    if (__here__ == 0)
        x10::serialize_value_type(dU1__buf, dU1);
    /* Broadcast value dU1 */;
    buffer_broadcast(dU1__buf);
    if (__here__ != 0)
        dU1 = x10::deserialize_value_type<dist>(dU1__buf);
    HWSerialize::GLOBAL_STATE.dU1 = dU1;
    x10::serialization_buffer dl__buf;
    if (__here__ == 0)
        x10::serialize_value_type(dl__buf, dl);
    /* Broadcast value dl */;
    buffer_broadcast(dl__buf);
    if (__here__ != 0)
        dl = x10::deserialize_value_type<dist>(dl__buf);
    HWSerialize::GLOBAL_STATE.dl = dl;
    x10::serialization_buffer de__buf;
    if (__here__ == 0)
        x10::serialize_value_type(de__buf, de);
    /* Broadcast value de */;
    buffer_broadcast(de__buf);
    if (__here__ != 0)
        de = x10::deserialize_value_type<dist>(de__buf);
    HWSerialize::GLOBAL_STATE.de = de;
    x10::serialization_buffer ia__buf;
    if (__here__ == 0)
        x10::serialize_value_type(ia__buf, ia);
    /* Broadcast value ia */;
    buffer_broadcast(ia__buf);
    if (__here__ != 0)
        ia = x10::deserialize_value_type<x10array<x10_int> >(ia__buf);
    HWSerialize::GLOBAL_STATE.ia = ia;
    x10::serialization_buffer ip__buf;
    if (__here__ == 0)
        x10::serialize_value_type(ip__buf, ip);
    /* Broadcast value ip */;
    buffer_broadcast(ip__buf);
    if (__here__ != 0)
        ip = x10::deserialize_value_type<x10array<x10::ref<place> > >(ip__buf);
    HWSerialize::GLOBAL_STATE.ip = ip;
    if (__here__ == 0) CS = 1;
    CS = x10::finish_start(CS); // finish#1
    if (1 != CS) goto SKIP_1;
    try {
        x10::ref<point> p = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
        if ((__here__)->x10__id == 0) {
                System::x10__out->println(String("q=[") + q->operator[](0) +
                                          String(",") + q->operator[](1) +
                                          String("]"));
        }
    } catch (x10::__ref& z) {
        EXCEPTION = (const x10::ref<Exception>&)z;
    }
    x10::finish_end(EXCEPTION); // finish#1
    CS = 0;
SKIP_1: ;
}

struct HWSerialize::_GLOBAL_STATE HWSerialize::GLOBAL_STATE;

void AsyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* ArrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }

