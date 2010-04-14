array_init_closure_and_args_struct(1, x10::ref<RandomAccess_Dist::localTable>,
array_init_unpacked_body((x10::ref<point> p), {
    
//#line 316 "x10.backend/examples/RandomAccess_Dist.x10"
    return (x10::ref<RandomAccess_Dist::localTable>)(new (x10::alloc<RandomAccess_Dist::localTable>())
                                                     RandomAccess_Dist::localTable(
                                                       tableSize));
    
}, x10_long tableSize = ((__init__1_args*) args)->tableSize;
), (void* args, x10::ref<point> p),
struct __init__1_args : public x10::closure_args {
    __init__1_args(x10_long tableSize) : tableSize(tableSize) { }
    x10_long tableSize;
    
};
);

struct async__0_args;
async_closure(0, {
    
//#line 369 "x10.backend/examples/RandomAccess_Dist.x10"
    (GLOBAL_STATE.Table->array->_data[(x10_int) (temp & GLOBAL_STATE.Table->
                                                          mask)])++;
}, (x10_long temp));
struct async__0_args : public x10::closure_args {
    async__0_args(x10_long temp) : temp(temp) { }
    x10_long temp;
    
};

struct async__1_args;
async_closure(1, {
    
//#line 389 "x10.backend/examples/RandomAccess_Dist.x10"
    GLOBAL_STATE.Table->array->_data[(x10_int) (temp & GLOBAL_STATE.Table->
                                                         mask)] ^=
      temp
    ;
}, (x10_long temp));
struct async__1_args : public x10::closure_args {
    async__1_args(x10_long temp) : temp(temp) { }
    x10_long temp;
    
};

struct async__2_args;
async_closure(2, {
    
//#line 415 "x10.backend/examples/RandomAccess_Dist.x10"
    GLOBAL_STATE.Table->array->_data[(x10_int) (temp & GLOBAL_STATE.Table->
                                                         mask)] ^=
      temp
    ;
}, (x10_long temp));
struct async__2_args : public x10::closure_args {
    async__2_args(x10_long temp) : temp(temp) { }
    x10_long temp;
    
};

struct async__3_args;
async_closure(3, {
    
//#line 444 "x10.backend/examples/RandomAccess_Dist.x10"
    GLOBAL_STATE.SUM->_data[p] = temp;
}, (x10_int p, x10_long temp));
struct async__3_args : public x10::closure_args {
    async__3_args(x10_int p, x10_long temp) : p(p), temp(temp) { }
    x10_int p;
    x10_long temp;
    
};


void asyncSwitch(x10_async_handler_t h, void* arg, int niter) {
    switch (h) {
    case 0:
        {
        async__0_args* args = (async__0_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__0_args* _arg = args++;
            async__0(_arg->temp);
        }
        }
        break;
    case 1:
        {
        async__1_args* args = (async__1_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__1_args* _arg = args++;
            async__1(_arg->temp);
        }
        }
        break;
    case 2:
        {
        async__2_args* args = (async__2_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__2_args* _arg = args++;
            async__2(_arg->temp);
        }
        }
        break;
    case 3:
        {
        async__3_args* args = (async__3_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__3_args* _arg = args++;
            async__3(_arg->p, _arg->temp);
        }
        }
        break;
    }
    
}

