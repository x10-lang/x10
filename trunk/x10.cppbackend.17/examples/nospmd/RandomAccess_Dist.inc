array_init_closure_and_args_struct(RandomAccess_Dist, 0, x10::ref<RandomAccess_Dist::localTable>,
array_init_unpacked_body((x10::ref<point> p), {
    
//#line 316 "RandomAccess_Dist.x10"
    return (x10::ref<RandomAccess_Dist::localTable>)(new (x10::alloc<RandomAccess_Dist::localTable>())
                                                     RandomAccess_Dist::localTable(
                                                       RandomAccess_Dist::GLOBAL_STATE.tableSize));
    
}, ), (void* args, x10::ref<point> p),
struct __init__0_args : public x10::closure_args {
    __init__0_args() {
        
    }
    
};
);

struct async__0_args;
async_closure(RandomAccess_Dist, 0, {
    
//#line 369 "RandomAccess_Dist.x10"
    (RandomAccess_Dist::GLOBAL_STATE.Table->x10__array->rawRegion()[(x10_int)
                                                                      (temp &
                                                                         RandomAccess_Dist::GLOBAL_STATE.Table->
                                                                           x10__mask)])++;
}, (x10_long temp));
struct async__0_args : public x10::closure_args {
    async__0_args(x10_long temp) {
        this->temp = temp;
    }
    async__0_args() { }
    x10_long temp;
    
};

struct async__1_args;
async_closure(RandomAccess_Dist, 1, {
    
//#line 389 "RandomAccess_Dist.x10"
    RandomAccess_Dist::GLOBAL_STATE.Table->x10__array->rawRegion()[(x10_int)
                                                                     (temp &
                                                                        RandomAccess_Dist::GLOBAL_STATE.Table->
                                                                          x10__mask)] ^=
      temp;
}, (x10_long temp));
struct async__1_args : public x10::closure_args {
    async__1_args(x10_long temp) {
        this->temp = temp;
    }
    async__1_args() { }
    x10_long temp;
    
};

struct async__2_args;
async_closure(RandomAccess_Dist, 2, {
    
//#line 415 "RandomAccess_Dist.x10"
    RandomAccess_Dist::GLOBAL_STATE.Table->x10__array->rawRegion()[(x10_int)
                                                                     (temp &
                                                                        RandomAccess_Dist::GLOBAL_STATE.Table->
                                                                          x10__mask)] ^=
      temp;
}, (x10_long temp));
struct async__2_args : public x10::closure_args {
    async__2_args(x10_long temp) {
        this->temp = temp;
    }
    async__2_args() { }
    x10_long temp;
    
};

struct async__3_args;
async_closure(RandomAccess_Dist, 3, {
    
//#line 444 "RandomAccess_Dist.x10"
    RandomAccess_Dist::GLOBAL_STATE.SUM->_data[p] = temp;
}, (x10_int p, x10_long temp));
struct async__3_args : public x10::closure_args {
    async__3_args(x10_int p, x10_long temp) {
        this->p = p;
        this->temp = temp;
    }
    async__3_args() { }
    x10_int p;
    x10_long temp;
    
};


void RandomAccess_Dist::localTable::AsyncSwitch(x10_async_handler_t h, void* arg, int niter) {
    switch (h) {
    }
    
}

void* RandomAccess_Dist::localTable::ArrayCopySwitch(x10_async_handler_t h, void* __arg) {
    switch (h) {
    }
    return NULL;
}

void RandomAccess_Dist::AsyncSwitch(x10_async_handler_t h, void* arg, int niter) {
    switch (h) {
    case 0:
        {
        async__0_args* args = (async__0_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__0_args* _arg = args++;
            RandomAccess_Dist::async__0(_arg->temp);
        }
        }
        break;
    case 1:
        {
        async__1_args* args = (async__1_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__1_args* _arg = args++;
            RandomAccess_Dist::async__1(_arg->temp);
        }
        }
        break;
    case 2:
        {
        async__2_args* args = (async__2_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__2_args* _arg = args++;
            RandomAccess_Dist::async__2(_arg->temp);
        }
        }
        break;
    case 3:
        {
        async__3_args* args = (async__3_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__3_args* _arg = args++;
            RandomAccess_Dist::async__3(_arg->p, _arg->temp);
        }
        }
        break;
    }
    
}

void* RandomAccess_Dist::ArrayCopySwitch(x10_async_handler_t h, void* __arg) {
    switch (h) {
    }
    return NULL;
}

