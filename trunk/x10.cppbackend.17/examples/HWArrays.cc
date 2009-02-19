#include "HWArrays.h"
using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;

void HWArrays::_run_initializers() {
    x10__z = x10::alloc_array<x10::ref<x10::lang::String> >(2, String("Hi,"), String("mom!"));
    x10__v = 5;
    x10__y = x10::alloc_array<x10::ref<x10::lang::String> >(2, this->x10__z->operator[](0), this->x10__z->operator[](1));
}

static x10::array<x10::ref<String> >* __init__0() {
    // TODO: fold this into x10::alloc_array() as varargs
    x10::array<x10::ref<String> >* val = x10::alloc_array<x10::ref<x10::lang::String> >(2);
//    x10::array<x10::ref<String> >* val = x10::alloc_array<x10::ref<x10::lang::String> >(2, String("Hello,"), String("world!"));
//    cerr << "__init__0: array allocated: " << (void*)val << endl;
    (*val)[0] = String("Hello,");
//    cerr << "__init__0: element[0] assigned " << (const std::string&)(*(*val)[0]) << endl;
    (*val)[1] = String("world!");
//    cerr << "__init__0: element[1] assigned " << (const std::string&)(*(*val)[1]) << endl;
    return val;
}
//const x10::ref<x10::array<x10::ref<String> > > HWArrays::x10__data = x10::alloc_array<ref<x10::lang::String> >(2, String("Hello,"), String("world!"));
//const x10::ref<x10::array<x10::ref<String> > > HWArrays::x10__data = __init__0();
x10::ref<x10::array<x10::ref<String> > > HWArrays::x10__data;

struct __init__1_args : public x10::closure_args {
    __init__1_args() { }
};
static x10::ref<HWArrays> __init__1(void *arg, x10::ref<point> p) {
    __init__1_args* args = (__init__1_args*)arg; // boilerplate
//    cerr << "__init__1: applying to " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
    x10_int i = p->operator[](0); // from exploded vars
//    cerr << "__init__1: got " << i << endl;
    return i==0 ?
        (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(0)) :
        (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(1));
}
//struct __init__1_closure : public x10::__init_closure<x10::ref<HWArrays> > {
//    __init__1_closure(x10::ref<HWArrays>(* const _func)(void*,const point&), __init__1_args* const _args)
//        : x10::__init_closure<x10::ref<HWArrays> >(_func, (void*)_args) { }
//};

//static x10::x10array<x10::ref<String> >* __init__1() {
////    return x10::x10newArray<String>(*new (x10::alloc<_region<1> >()) _region<1>(0,1),x10::array_init(String, { x10_int i = p[0]; return i==0 ? "Hello," : "world!"; }, (const point& p)));
//    return x10::x10newArray<String>(
//                _region<1>(0,1),
//                ({
//                    class __array_init__3 : public x10::_array_init<String> {
//                    public:
//                        String operator() (const point& p) const {
//                            cerr << "array_init->(): applying to " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
//                            x10_int i = p[0];
//                            cerr << "array_init->(): got " << i << endl;
//                            return i==0 ? "Hello," : "world!";
//                        };
//                    } val;
//                    val;
//                }));
//}
//x10::ref<x10::x10array<String> > HWArrays::x10__a = x10::x10newArray<String>(*new (x10::alloc<_region<1> >()) _region<1>(0,1),x10::array_init(String, { x10_int i = p[0]; return i==0 ? "Hello," : "world!"; }, (const point& p)));
//x10::ref<x10::x10array<String> > HWArrays::x10__a =
//    x10::x10newArray<String>(
//            _region<1>(0,1),
//            x10::__id__(({
//                class __array_init__3 : public x10::_array_init<String> {
//                public:
//                    String operator() (const point& p) const {
//                        x10_int i = p[0];
//                        return i==0 ? "Hello," : "world!";
//                    };
//                } val;
//                val;
//            })));

//x10::ref<x10::x10array<String> > HWArrays::x10__a = __init__1();

//x10::ref<x10::x10array<String> > HWArrays::x10__a = x10::x10newArray<String>(*new (x10::alloc<_region<1> >()) _region<1>(0,1),x10::array_init(String, { x10_int i = p[0]; return i==0 ? "Hello," : "world!"; }, (const point& p)));
//struct __init__0_args {
////__init__0_args(x10_int _captVar1, ...) : captVar1(_captVar1) ... { }
////  x10_int captVar1...
//};
//String __init__0(void *arg, const point& p) {
//  __init__0_args* args = (__init__0_args*)arg;
////  x10_int captVar1 = args->captVar1;
////  static x10_int captVar2 = GLOBAL_STATE.captVar2;
//  x10_int i = p[0];
//  return i==0 ? "Hello," : "world!";
//}
//struct __init__0_args args = { };
//x10::ref<x10::x10array<x10::ref<String> > > HWArrays::x10__a = x10::x10newArray<x10::ref<String> >(*new (x10::alloc<_region<1> >()) _region<1>(0,1), __init__0, &__init__0_args(...));

//const x10::ref<x10::x10array<x10::ref<HWArrays> > > HWArrays::x10__a = x10::x10newArray<x10::ref<HWArrays> >((new (x10::alloc<_region<1> >()) _region<1>(0,1))->toDistribution(), &__init__1_closure(__init__1, &__init__1_args()));

x10::ref<x10::x10array<x10::ref<HWArrays> > > HWArrays::x10__a;

struct __init__2_args : public x10::closure_args {
    __init__2_args() { }
};
static x10::ref<HWArrays> __init__2(void *arg, x10::ref<point> p) {
    __init__2_args* args = (__init__2_args*)arg; // boilerplate
//    cerr << "__init__2: applying to " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
    x10_int i = p->operator[](0); // from exploded vars
//    cerr << "__init__2: got " << i << endl;
    return i==1 ?
        (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(1)) :
        (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(0));
}

HWArrays::HWArrays(x10_int value) : x10::lang::Object() {
    _run_initializers();
    this->x10__value = x10__value;
}

extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWArrays::main(args);
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
void HWArrays::main(x10::ref<x10::array<x10::ref<String> > > s) {
//    cerr << "data[0] = " << (const std::string&)(*data[0]) << endl;
//    cerr << "data[1] = " << (const std::string&)(*data[1]) << endl;
//    x10::ref<x10::x10array<x10::ref<HWArrays> > > b =
//        x10::x10newArray<x10::ref<HWArrays> >(((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(0,1)))->toDistribution(),
//                x10::__init_closure<x10::ref<HWArrays> >(__init__2, __init__2_args().ptr()).ptr());
    x10::ref<x10::x10array<x10::ref<HWArrays> > > b =
        array_init_closure_invocation(2,
            ((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(0, 1)))->toDistribution(),
            x10::ref<HWArrays>, ());
//    x10::ref<x10::x10array<x10::ref<HWArrays> > > b =
//        x10::x10newArray<x10::ref<HWArrays> >(((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(0,1)))->toDistribution(),
//                x10::array_init(x10::ref<HWArrays>, { x10_int i = p[0]; return i==1 ? (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(1)) : (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(0)); }, (const point& p)));
//    x10::ref<x10::x10array<x10::ref<HWArrays> > > b =
//        x10::x10newArray<x10::ref<HWArrays> >(((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(0,1)))->toDistribution(),
//                ({
//                    cerr << "({}): defining the class" << endl;
//                    class __array_init__3 : public x10::_array_init<x10::ref<HWArrays> > {
//                    public:
//                        x10::ref<HWArrays> operator() (const point& p) const {
//                            cerr << "array_init->(): applying to " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
//                            x10_int i = p[0];
//                            cerr << "array_init->(): got " << i << endl;
//                            return i==1 ?
//                                (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(1)) :
//                                (x10::ref<HWArrays>)(new (x10::alloc<HWArrays>()) HWArrays(0));
//                        };
//                    } val;
//                    cerr << "({}): defined the class" << endl;
//                    &val;
//                }));
//    cerr << "array initialized" << endl;
//    cerr << "a[0] = " << (void*)(x10__a[0].operator->()) << " .value = " << x10__a[0]->x10__value << endl;
//    cerr << "a[1] = " << (void*)(x10__a[1].operator->()) << " .value = " << x10__a[1]->x10__value << endl;
//    System::x10__out->println(*(x10__data[x10__a[0]->x10__value])+*(x10__data[x10__a[1]->x10__value]));
//    cerr << "a[0] = " << (void*)(x10__a->operator[](0).operator->()) << " .value = " << x10__a->operator[](0)->x10__value << endl;
//    cerr << "a[1] = " << (void*)(x10__a->operator[](1).operator->()) << " .value = " << x10__a->operator[](1)->x10__value << endl;
//    cerr << "data[0] = " << (void*)(data->operator[](0).operator->()) << " '" << *(data->operator[](0)) << "'" << endl;
//    cerr << "data[1] = " << (void*)(data->operator[](1).operator->()) << " '" << *(data->operator[](1)) << "'" << endl;
    System::x10__out->println(x10__data->operator[](x10__a->operator[](0)->x10__value) + String(" ") + x10__data->operator[](x10__a->operator[](1)->x10__value));
    const x10::ref<x10::array<x10::ref<x10::lang::String> > > x = x10::alloc_array<x10::ref<x10::lang::String> >(2, x10__a->operator[](0)->x10__y->operator[](0), x10__a->operator[](0)->x10__y->operator[](1));
    const x10::ref<x10::array<x10::ref<x10::lang::String> > > w = x10::alloc_array<x10::ref<x10::lang::String> >(2, x->operator[](0), x->operator[](1));
    System::x10__out->println(w->operator[](0) + String(" ") + w->operator[](1));
    x10__data->operator[](1) = String("dad?");
    System::x10__out->println(x10__data->operator[](0) + String(" ") + x10__data->operator[](1));
}

void* HWArrays::__static_init() {
    x10__data = x10::alloc_array<x10::ref<x10::lang::String> >(2, String("Hello,"), String("world!"));
    x10__a = x10::x10newArray<x10::ref<HWArrays> >(((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >())
	    _region<1>(0,1)))->toDistribution(),
	x10::__init_closure<x10::ref<HWArrays> >(__init__1, __init__1_args().ptr()).ptr());
    return NULL;
}
static void* __init__ = HWArrays::__static_init();

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
