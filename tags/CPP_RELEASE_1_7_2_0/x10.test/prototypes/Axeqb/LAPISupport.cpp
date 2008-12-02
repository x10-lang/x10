#include <stdlib.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <strings.h>
#include <unistd.h>
#include <limits.h>
#include <assert.h>
#include <pthread.h>

extern "C" {
#include <lapi.h>
}
#include "VMInfo.h"
#include "JAxeqb.h"

#define CheckLapiProblem(e, lapi_rc) \
    if (lapi_rc != LAPI_SUCCESS) { \
        char  msg_buff[LAPI_MAX_ERR_STRING+128]; \
        sprintf(msg_buff,"Line %d: ", __LINE__); \
        LAPI_Msg_string(lapi_rc, msg_buff + strlen(msg_buff)); \
        fprintf(stderr,"LAPI rc = %s\n", msg_buff); \
        if (e) e->ThrowNew(e->FindClass("java/lang/Error"), msg_buff); \
    }

#define CheckJNIException(e) \
    if (e->ExceptionOccurred()) { \
        fprintf(stderr, "A JNI exception occurred above line %d\n", __LINE__); \
        e->ExceptionDescribe(); \
    }
#define IfCheckJNIException(e) CheckJNIException(e)
 
static unsigned int mp_procs;
static unsigned int mp_child;
static lapi_handle_t lapi_handle;
static unsigned int maxLAPIUHdrSz;
static JavaVM *myJavaVM;
static jint myJNIVersion;
static jclass myVMInfoClass;
static jclass mySolverClass;
static struct {
   jlong        globalNameOfSolver;
   jobject      localNameOfSolver;
} solverNameMapping;

enum handlerTypes {
    newSolverHandler,
    setUpSolverHandler,
    LUFactorizeHandler,
    adjustBelowDiagonalHandler,
    adjustBelowDiagonalRetHandler,
    UxeqyHandler,
    rowSumHandler,
    rowSumRetHandler,
    getAatHandler,
    shutdownHandler,
    numHandlerTypes
};

static void *this_vms_newSolver_hdr_handler(lapi_handle_t *hndl,
                                            void *uhdr,
                                            unsigned int *uhdr_len,
                                            lapi_return_info_t *ret_info,
                                            compl_hndlr_t **comp_h,
                                            void **info);

static void *this_vms_setUpSolver_hdr_handler(lapi_handle_t *hndl,
                                              void *uhdr,
                                              unsigned int *uhdr_len,
                                              lapi_return_info_t *ret_info,
                                              compl_hndlr_t **comp_h,
                                              void **info);

static void *this_vms_LUFactorize_hdr_handler(lapi_handle_t *hndl,
                                              void *uhdr,
                                              unsigned int *uhdr_len,
                                              lapi_return_info_t *ret_info,
                                              compl_hndlr_t **comp_h,
                                              void **info);

static void *this_vms_adjustBelowDiagonal_hdr_handler(lapi_handle_t *hndl,
                                                      void *uhdr,
                                                      unsigned int *uhdr_len,
                                                      lapi_return_info_t *ret_info,
                                                      compl_hndlr_t **comp_h,
                                                      void **info);

static void *this_vms_adjustBelowDiagonalRet_hdr_handler(lapi_handle_t *hndl,
                                                         void *uhdr,
                                                         unsigned int *uhdr_len,
                                                         lapi_return_info_t *ret_info,
                                                         compl_hndlr_t **comp_h,
                                                         void **info);

static void *this_vms_Uxeqy_hdr_handler(lapi_handle_t *hndl,
                                        void *uhdr,
                                        unsigned int *uhdr_len,
                                        lapi_return_info_t *ret_info,
                                        compl_hndlr_t **comp_h,
                                        void **info);

static void *this_vms_rowSum_hdr_handler(lapi_handle_t *hndl,
                                         void *uhdr,
                                         unsigned int *uhdr_len,
                                         lapi_return_info_t *ret_info,
                                         compl_hndlr_t **comp_h,
                                         void **info);

static void *this_vms_rowSumRet_hdr_handler(lapi_handle_t *hndl,
                                            void *uhdr,
                                            unsigned int *uhdr_len,
                                            lapi_return_info_t *ret_info,
                                            compl_hndlr_t **comp_h,
                                            void **info);

static void *this_vms_getAat_hdr_handler(lapi_handle_t *hndl,
                                         void *uhdr,
                                         unsigned int *uhdr_len,
                                         lapi_return_info_t *ret_info,
                                         compl_hndlr_t **comp_h,
                                         void **info);

static void *this_vms_shutdown_hdr_handler(lapi_handle_t *hndl,
                                           void *uhdr,
                                           unsigned int *uhdr_len,
                                           lapi_return_info_t *ret_info,
                                           compl_hndlr_t **comp_h,
                                           void **info);

static struct {
    lapi_long_t *handlers;
    void *(*this_vms_handler)(lapi_handle_t *hndl,
                              void *uhdr,
                              unsigned int *uhdr_len,
                              lapi_return_info_t *ret_info,
                              compl_hndlr_t **comp_h,
                              void **info);
} hdr_handlers[numHandlerTypes] = {
    {NULL, this_vms_newSolver_hdr_handler},
    {NULL, this_vms_setUpSolver_hdr_handler},
    {NULL, this_vms_LUFactorize_hdr_handler},
    {NULL, this_vms_adjustBelowDiagonal_hdr_handler},
    {NULL, this_vms_adjustBelowDiagonalRet_hdr_handler},
    {NULL, this_vms_Uxeqy_hdr_handler},
    {NULL, this_vms_rowSum_hdr_handler},
    {NULL, this_vms_rowSumRet_hdr_handler},
    {NULL, this_vms_getAat_hdr_handler},
    {NULL, this_vms_shutdown_hdr_handler},
};
#define HdrHandler(X) hdr_handlers[(X)].handlers[xfer_struct.Am.tgt];

//////////////////////////////////////////////////////////////////////////////
// messages to notifiy VMs 1, 2, ... that VM 0 has created a new Solver object
// or is going to delete it
//////////////////////////////////////////////////////////////////////////////
typedef struct newSolverMsg_s {
    bool                create;
    unsigned int        N;
    jboolean            debug;
    jlong               key;
} newSolverMsg_t;

static void this_vms_newSolver_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);
    newSolverMsg_t *msg = (newSolverMsg_t *) args;
    if (msg != NULL) {
        if (msg->create) {
            solverNameMapping.localNameOfSolver =
                e->NewGlobalRef(
                    e->NewObject(mySolverClass,
                                 e->GetMethodID(mySolverClass,
                                                "<init>", "(IZ)V"),
                                 msg->N, msg->debug));
            CheckJNIException(e);
            solverNameMapping.globalNameOfSolver = msg->key;
            e->CallVoidMethod(solverNameMapping.localNameOfSolver,
                              e->GetMethodID(mySolverClass,
                                             "setGlobalKey", "(J)V"),
                              solverNameMapping.globalNameOfSolver);
            CheckJNIException(e);
        } else {
            e->DeleteGlobalRef(solverNameMapping.localNameOfSolver);
        }
        free(msg);
    } else {
        e->ThrowNew(e->FindClass("java/lang/Error"), "malloc failed for LAPI buffer");
    }
    e->PopLocalFrame(NULL);
}

static void *this_vms_newSolver_hdr_handler(lapi_handle_t *hndl,
                                            void *uhdr,
                                            unsigned int *uhdr_len,
                                            lapi_return_info_t *ret_info,
                                            compl_hndlr_t **comp_h,
                                            void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_newSolver_compl_handler;
    *info = malloc(sizeof(newSolverMsg_t));
    return *info;
}

JNIEXPORT void JNICALL Java_VMInfo_newSolver
(JNIEnv *e, jobject vmInfo, jint N, jboolean debug, jlong key) {
    int lapi_rc;
    lapi_xfer_t xfer_struct;
    newSolverMsg_t msg;
    msg.create = true;
    msg.N = N;
    msg.debug = debug;
    msg.key = (unsigned long) key;
    
    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(newSolverHandler);
    xfer_struct.Am.uhdr_len = 0;
    xfer_struct.Am.uhdr = NULL;
    xfer_struct.Am.udata = (void *) &msg;
    xfer_struct.Am.udata_len = sizeof msg;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
}

JNIEXPORT void JNICALL Java_VMInfo_deleteSolver
(JNIEnv *e, jobject vmInfo, jlong key) {
    int lapi_rc;
    lapi_xfer_t xfer_struct;
    newSolverMsg_t msg;
    msg.create = false;
    msg.key = (unsigned long) key;
    
    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(newSolverHandler);
    xfer_struct.Am.uhdr_len = 0;
    xfer_struct.Am.uhdr = NULL;
    xfer_struct.Am.udata = (void *) &msg;
    xfer_struct.Am.udata_len = sizeof msg;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
}

//////////////////////////////////////////////////////////////////////////////
// messages to request that VMs 1, 2, ... initialize A
//////////////////////////////////////////////////////////////////////////////
static void this_vms_setUpSolver_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);

    jlong key = *((jlong *)args);
    assert(key == solverNameMapping.globalNameOfSolver);
    e->CallVoidMethod(solverNameMapping.localNameOfSolver,
                      e->GetMethodID(mySolverClass, "setup", "()V"));
    CheckJNIException(e);
    e->PopLocalFrame(NULL);
}

static void *this_vms_setUpSolver_hdr_handler(lapi_handle_t *hndl,
                                              void *uhdr,
                                              unsigned int *uhdr_len,
                                              lapi_return_info_t *ret_info,
                                              compl_hndlr_t **comp_h,
                                              void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_setUpSolver_compl_handler;
    assert(*uhdr_len == sizeof(jlong));
    *info = uhdr;
    return NULL;
}

JNIEXPORT void JNICALL Java_VMInfo_setUpSolver
(JNIEnv *e, jobject vmInfo, jlong key) {
    int lapi_rc;
    lapi_xfer_t xfer_struct;
    
    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(setUpSolverHandler);
    xfer_struct.Am.uhdr_len = sizeof(key);
    xfer_struct.Am.uhdr = &key;
    xfer_struct.Am.udata = NULL;
    xfer_struct.Am.udata_len = 0;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
}

//////////////////////////////////////////////////////////////////////////////
// Methods to cause LUFactorization operation on a particular diagonal index
//////////////////////////////////////////////////////////////////////////////
typedef struct LUFactorizeMsg_s {
    jlong       key;
    jint        diag_index;
    lapi_long_t rc_addr;
    int         lapi_task_awaiting_reply;
} LUFactorizeMsg_t;

static void this_vms_LUFactorize_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);

    LUFactorizeMsg_t *msg = (LUFactorizeMsg_t *) args;
    assert(msg->key == solverNameMapping.globalNameOfSolver);
    e->CallVoidMethod(solverNameMapping.localNameOfSolver,
                      e->GetMethodID(mySolverClass,
                                     "LUFactorizeOnAnotherThread", "(IIJ)V"),
                      msg->diag_index,
                      msg->lapi_task_awaiting_reply, msg->rc_addr);
    CheckJNIException(e);
    e->PopLocalFrame(NULL);
}

static void *this_vms_LUFactorize_hdr_handler(lapi_handle_t *hndl,
                                              void *uhdr,
                                              unsigned int *uhdr_len,
                                              lapi_return_info_t *ret_info,
                                              compl_hndlr_t **comp_h,
                                              void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_LUFactorize_compl_handler;
    assert(*uhdr_len == sizeof(LUFactorizeMsg_t));
    *info = uhdr;
    return NULL;
}

JNIEXPORT jboolean JNICALL Java_VMInfo_LUFactorize
(JNIEnv *e, jobject vmInfo, jlong key, jint diag_index) {
    volatile int rc = ~(int) 0;
    int lapi_rc;
    lapi_xfer_t xfer_struct;
    LUFactorizeMsg_t msg;
    msg.key = key;
    msg.diag_index = diag_index;
    msg.rc_addr = (lapi_long_t) &rc;
    msg.lapi_task_awaiting_reply = mp_child;
    
    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(LUFactorizeHandler);
    xfer_struct.Am.uhdr_len = sizeof(msg);
    xfer_struct.Am.uhdr = &msg;
    xfer_struct.Am.udata = NULL;
    xfer_struct.Am.udata_len = 0;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
    while (rc ==  ~(int) 0) {
        LAPI_Probe(lapi_handle);
    }
    return (jboolean) rc;
}

JNIEXPORT void JNICALL Java_VMInfo_returnLUFactorizeValue
(JNIEnv *e, jobject vmInfo, jboolean b, jlong addr) {
    lapi_xfer_t xfer_struct;
    int lapi_rc;
    xfer_struct.Put.Xfer_type = LAPI_PUT_XFER;
    xfer_struct.Put.flags = 0;
    xfer_struct.Put.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Put.tgt_addr = addr;
    xfer_struct.Put.org_addr = &b;
    xfer_struct.Put.len = sizeof b;
    xfer_struct.Put.shdlr = NULL;
    xfer_struct.Put.sinfo = NULL;
    xfer_struct.Put.tgt_cntr = 0;
    xfer_struct.Put.org_cntr = NULL;
    xfer_struct.Put.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
}

//////////////////////////////////////////////////////////////////////////////
// Methods to handle adjustments below diag_index
//////////////////////////////////////////////////////////////////////////////
typedef struct adjustBelowDiagMsg_s {
    jlong        key;
    int          lapi_task_awaiting_reply;
    jint         diag_index;
    jint         largest_row;
    unsigned int multipliers_length;
    jdoubleArray multipliers;
    jboolean    isCopy;
    jdouble *   m;
} adjustBelowDiagMsg_t;
typedef struct adjustBelowDiagReturnMsg_s {
    jboolean    okay;
    int         lapi_task_returning;
} adjustBelowDiagReturnMsg_t;

static void this_vms_adjustBelowDiagonal_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);

    adjustBelowDiagReturnMsg_t retMsg;
    adjustBelowDiagMsg_t *msg = (adjustBelowDiagMsg_t *) args;
    retMsg.okay = JNI_FALSE;
    retMsg.lapi_task_returning = mp_child;
    if (msg == NULL) {
        e->ThrowNew(e->FindClass("java/lang/Error"), "malloc failed for LAPI buffer3");
    } else {
        assert(msg->key == solverNameMapping.globalNameOfSolver);
        if (msg->isCopy == JNI_TRUE) {
            e->ReleaseDoubleArrayElements(msg->multipliers, msg->m, 0);
        }
        e->CallVoidMethod(solverNameMapping.localNameOfSolver,
                          e->GetMethodID(mySolverClass,
                                         "AdjustBelowDiagonal", "(II[D)V"),
                          msg->diag_index, msg->largest_row, msg->multipliers);
        e->DeleteGlobalRef(msg->multipliers);
        // call adjust below diagonal
        // then tell the original caller that we're done
        IfCheckJNIException(e)
        else {
            retMsg.okay = JNI_TRUE;
        }
        int lapi_rc;
        lapi_xfer_t xfer_struct;
        xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
        xfer_struct.Am.flags = 0;
        xfer_struct.Am.tgt = msg->lapi_task_awaiting_reply;
        xfer_struct.Am.hdr_hdl = HdrHandler(adjustBelowDiagonalRetHandler);
        xfer_struct.Am.uhdr_len = sizeof(retMsg);
        xfer_struct.Am.uhdr = &retMsg;
        xfer_struct.Am.udata = NULL;
        xfer_struct.Am.udata_len = 0;
        xfer_struct.Am.shdlr = NULL;
        xfer_struct.Am.sinfo = NULL;
        xfer_struct.Am.tgt_cntr = 0;
        xfer_struct.Am.org_cntr = NULL;
        xfer_struct.Am.cmpl_cntr = NULL;
        lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
        free(msg);
        CheckLapiProblem(e, lapi_rc);
    }
    e->PopLocalFrame(NULL);
}

static void *this_vms_adjustBelowDiagonal_hdr_handler(lapi_handle_t *hndl,
                                                      void *uhdr,
                                                      unsigned int *uhdr_len,
                                                      lapi_return_info_t *ret_info,
                                                      compl_hndlr_t **comp_h,
                                                      void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_adjustBelowDiagonal_compl_handler;
    assert(*uhdr_len == sizeof(adjustBelowDiagMsg_t));
    adjustBelowDiagMsg_t *myMsg = (adjustBelowDiagMsg_t *) uhdr;
    *info = myMsg;
    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);
    jdoubleArray ma = e->NewDoubleArray(myMsg->multipliers_length);
    CheckJNIException(e);
    myMsg->multipliers = (jdoubleArray) e->NewGlobalRef(ma);
    myMsg->m = e->GetDoubleArrayElements(myMsg->multipliers, &myMsg->isCopy);
    e->PopLocalFrame(NULL);
    return myMsg->m;
}


static void this_vms_adjustBelowDiagonalRet_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);
    adjustBelowDiagReturnMsg_t *msg = (adjustBelowDiagReturnMsg_t *) args;
    jclass vmInfoClass = myVMInfoClass;
    jobjectArray VM_ = (jobjectArray) e->GetStaticObjectField(vmInfoClass, e->GetStaticFieldID(vmInfoClass, "VM_", "[LVMInfo;"));
    jobject vmInfo = e->GetObjectArrayElement(VM_, msg->lapi_task_returning);
    e->SetBooleanField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "adjustDone", "Z"), JNI_TRUE);
    CheckJNIException(e);
    e->PopLocalFrame(NULL);
}

static void *this_vms_adjustBelowDiagonalRet_hdr_handler(lapi_handle_t *hndl,
                                                         void *uhdr,
                                                         unsigned int *uhdr_len,
                                                         lapi_return_info_t *ret_info,
                                                         compl_hndlr_t **comp_h,
                                                         void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_adjustBelowDiagonalRet_compl_handler;
    assert(*uhdr_len == sizeof(adjustBelowDiagReturnMsg_t));
    *info = uhdr;
    return NULL;
}

static adjustBelowDiagMsg_t *outgoingAdjustMsg = NULL;
unsigned long int outgoingAdjustMsgLen;
void mySendCompletionHandler(lapi_handle_t *hndl, void *completion_param,
                         lapi_sh_info_t *info) {
    volatile int *flag = (int *) completion_param;
    *flag = ~(int) 0;
}

JNIEXPORT void JNICALL Java_VMInfo_AdjustBelowDiagonal
(JNIEnv *e, jclass vmInfoClass, jlong key, jint diag_index, jint largest_row, jdoubleArray multipliers) {
    assert(outgoingAdjustMsg == NULL);
    // could probably use an automatic here
    outgoingAdjustMsg = (adjustBelowDiagMsg_t *) malloc(sizeof(adjustBelowDiagMsg_t));
    if (outgoingAdjustMsg == NULL) {
        e->ThrowNew(e->FindClass("java/lang/Error"), "malloc failed for LAPI buffer1");
    }
    outgoingAdjustMsg->key = key;
    outgoingAdjustMsg->lapi_task_awaiting_reply = mp_child;
    outgoingAdjustMsg->diag_index = diag_index;
    outgoingAdjustMsg->largest_row = largest_row;
    outgoingAdjustMsg->multipliers_length = e->GetArrayLength(multipliers);
    jboolean is_copy;
    jdouble *m = e->GetDoubleArrayElements(multipliers, &is_copy);
    for (unsigned int lapi_task = 0; lapi_task < mp_procs; ++lapi_task) {
        if (lapi_task != mp_child) {
            volatile int flag = 0;
            int lapi_rc;
            lapi_xfer_t xfer_struct;
    
            xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
            xfer_struct.Am.flags = 0;
            xfer_struct.Am.tgt = lapi_task;
            xfer_struct.Am.hdr_hdl = HdrHandler(adjustBelowDiagonalHandler);
            xfer_struct.Am.uhdr_len = sizeof(adjustBelowDiagMsg_t);
            xfer_struct.Am.uhdr = outgoingAdjustMsg;
            xfer_struct.Am.udata = m;
            xfer_struct.Am.udata_len = outgoingAdjustMsg->multipliers_length * sizeof(jdouble);
            xfer_struct.Am.shdlr = mySendCompletionHandler;
            xfer_struct.Am.sinfo = (void *) &flag;
            xfer_struct.Am.tgt_cntr = 0;
            xfer_struct.Am.org_cntr = NULL;
            xfer_struct.Am.cmpl_cntr = NULL;
            lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
            CheckLapiProblem(e, lapi_rc);
            while (flag ==  0) {
                LAPI_Probe(lapi_handle);
            }
        }
    }
    if (is_copy == JNI_TRUE) {
        e->ReleaseDoubleArrayElements(multipliers, m, JNI_ABORT);
    }
    free(outgoingAdjustMsg);
    outgoingAdjustMsg = NULL;
}

// This is a suspicious thing.... I want to return a double to another
// processor.  I could do some active messages but I'm a bit lazy.  So
// instead, we're going to try to put a message with a volatile flag on
// each side of the value I want to return.  The hope is that if both
// flags are seen to have an updated value on the remote processor,
// then the field in the middle has also been updated.  I'm not at
// all sure that this is guranteed to work.
typedef struct doubleRetMsg_s {
    volatile int        flag0;
    double              d;
    volatile int        flag1;
} doubleRetMsg_t;
//////////////////////////////////////////////////////////////////////////////
// Methods for Uxeqy
//////////////////////////////////////////////////////////////////////////////
typedef struct UxeqyMsg_s {
    jlong       key;
    jint        diag_index;
    jdouble     yi;
    lapi_long_t rc_addr;
    int         lapi_task_awaiting_reply;
} UxeqyMsg_t;

static void this_vms_Uxeqy_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);

    UxeqyMsg_t *msg = (UxeqyMsg_t *) args;
    assert(msg->key == solverNameMapping.globalNameOfSolver);
    e->CallVoidMethod(solverNameMapping.localNameOfSolver,
                      e->GetMethodID(mySolverClass,
                                     "UxeqyOnAnotherThread", "(IDIJ)V"),
                      msg->diag_index, msg->yi,
                      msg->lapi_task_awaiting_reply, msg->rc_addr);
    CheckJNIException(e);
    e->PopLocalFrame(NULL);
}

static void *this_vms_Uxeqy_hdr_handler(lapi_handle_t *hndl,
                                        void *uhdr,
                                        unsigned int *uhdr_len,
                                        lapi_return_info_t *ret_info,
                                        compl_hndlr_t **comp_h,
                                        void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_Uxeqy_compl_handler;
    assert(*uhdr_len == sizeof(UxeqyMsg_t));
    *info = uhdr;
    return NULL;
}

JNIEXPORT jdouble JNICALL Java_VMInfo_Uxeqy
(JNIEnv *e, jobject vmInfo, jlong key, jint diag_index, jdouble yi) {
    volatile doubleRetMsg_t rc;
    rc.flag0 = rc.flag1 = 0;
    int lapi_rc;
    lapi_xfer_t xfer_struct;
    UxeqyMsg_t msg;
    msg.key = key;
    msg.diag_index = diag_index;
    msg.yi = yi;
    msg.rc_addr = (lapi_long_t) &rc;
    msg.lapi_task_awaiting_reply = mp_child;
    
    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(UxeqyHandler);
    xfer_struct.Am.uhdr_len = sizeof(msg);
    xfer_struct.Am.uhdr = &msg;
    xfer_struct.Am.udata = NULL;
    xfer_struct.Am.udata_len = 0;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
    while (rc.flag0 == 0 || rc.flag1 == 0) {
        LAPI_Probe(lapi_handle);
    }
    return rc.d;
}

JNIEXPORT void JNICALL Java_VMInfo_returnUxeqyValue
(JNIEnv *e, jobject vmInfo, jdouble d, jlong addr) {
    doubleRetMsg_t dr;
    dr.flag0 = ~(int) 0;
    dr.d = d;
    dr.flag1 = ~(int) 0;
    lapi_xfer_t xfer_struct;
    int lapi_rc;
    xfer_struct.Put.Xfer_type = LAPI_PUT_XFER;
    xfer_struct.Put.flags = 0;
    xfer_struct.Put.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Put.tgt_addr = addr;
    xfer_struct.Put.org_addr = &dr;
    xfer_struct.Put.len = sizeof dr;
    xfer_struct.Put.shdlr = NULL;
    xfer_struct.Put.sinfo = NULL;
    xfer_struct.Put.tgt_cntr = 0;
    xfer_struct.Put.org_cntr = NULL;
    xfer_struct.Put.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
}

//////////////////////////////////////////////////////////////////////////////
// Methods for summing all the elements in a row to the right or left
// of the diagonal
//////////////////////////////////////////////////////////////////////////////
typedef struct rowSumMsg_s {
    jlong       key;
    int         lapi_task_awaiting_reply;
    jboolean    to_the_right;
    jint        diag_index;
} rowSumMsg_t;
typedef struct rowSumReturnMsg_s {
    jboolean    okay;
    jdouble     sum;
    int         lapi_task_returning;
} rowSumReturnMsg_t;

static void this_vms_rowSum_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);

    rowSumReturnMsg_t retMsg;
    rowSumMsg_t *msg = (rowSumMsg_t *) args;
    retMsg.okay = JNI_FALSE;
    retMsg.lapi_task_returning = mp_child;
    if (msg == NULL) {
        e->ThrowNew(e->FindClass("java/lang/Error"), "malloc failed for LAPI buffer3");
    } else {
        assert(msg->key == solverNameMapping.globalNameOfSolver);
        retMsg.sum = e->CallDoubleMethod(solverNameMapping.localNameOfSolver,
                                         e->GetMethodID(mySolverClass,
                                                    "computeRowSum", "(ZI)D"),
                                         msg->to_the_right, msg->diag_index);
        // call adjust below diagonal
        // then tell the original caller that we're done
        IfCheckJNIException(e)
        else {
            retMsg.okay = JNI_TRUE;
        }
        int lapi_rc;
        lapi_xfer_t xfer_struct;
        xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
        xfer_struct.Am.flags = 0;
        xfer_struct.Am.tgt = msg->lapi_task_awaiting_reply;
        xfer_struct.Am.hdr_hdl = HdrHandler(rowSumRetHandler);
        xfer_struct.Am.uhdr_len = sizeof(retMsg);
        xfer_struct.Am.uhdr = &retMsg;
        xfer_struct.Am.udata = NULL;
        xfer_struct.Am.udata_len = 0;
        xfer_struct.Am.shdlr = NULL;
        xfer_struct.Am.sinfo = NULL;
        xfer_struct.Am.tgt_cntr = 0;
        xfer_struct.Am.org_cntr = NULL;
        xfer_struct.Am.cmpl_cntr = NULL;
        lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
        CheckLapiProblem(e, lapi_rc);
        free(msg);
    }
    e->PopLocalFrame(NULL);
}

static void *this_vms_rowSum_hdr_handler(lapi_handle_t *hndl,
                                         void *uhdr,
                                         unsigned int *uhdr_len,
                                         lapi_return_info_t *ret_info,
                                         compl_hndlr_t **comp_h,
                                         void **info) {
    
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_rowSum_compl_handler;
    *info = malloc(sizeof(rowSumMsg_t));
    return *info;
}

static void this_vms_rowSumRet_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);
    rowSumReturnMsg_t *msg = (rowSumReturnMsg_t *) args;
    jclass vmInfoClass = myVMInfoClass;
    jobjectArray VM_ = (jobjectArray) e->GetStaticObjectField(vmInfoClass, e->GetStaticFieldID(vmInfoClass, "VM_", "[LVMInfo;"));
    jobject vmInfo = e->GetObjectArrayElement(VM_, msg->lapi_task_returning);
    e->SetDoubleField(vmInfo,
                      e->GetFieldID(e->GetObjectClass(vmInfo),
                                    "thisRowSum", "D"),
                      msg->sum);
    CheckJNIException(e);
    e->SetBooleanField(vmInfo,
                       e->GetFieldID(e->GetObjectClass(vmInfo),
                                     "rowSumIsDone", "Z"),
                       JNI_TRUE);
    CheckJNIException(e);
    e->PopLocalFrame(NULL);
}
static void *this_vms_rowSumRet_hdr_handler(lapi_handle_t *hndl,
                                            void *uhdr,
                                            unsigned int *uhdr_len,
                                            lapi_return_info_t *ret_info,
                                            compl_hndlr_t **comp_h,
                                            void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_rowSumRet_compl_handler;
    assert(*uhdr_len == sizeof(rowSumReturnMsg_t));
    *info = uhdr;
    return NULL;
}

static rowSumMsg_t *outgoingRowSumMsg = NULL;
JNIEXPORT void JNICALL Java_VMInfo_rowSum
(JNIEnv *e, jclass vmInfoClass, jlong key, jboolean to_the_right, jint diag_index) {
    assert(outgoingRowSumMsg == NULL);
    outgoingRowSumMsg = (rowSumMsg_t *) malloc(sizeof(rowSumMsg_t));
    if (outgoingRowSumMsg == NULL) {
        e->ThrowNew(e->FindClass("java/lang/Error"), "malloc failed for LAPI buffer1");
    }
    outgoingRowSumMsg->key = key;
    outgoingRowSumMsg->lapi_task_awaiting_reply = mp_child;
    outgoingRowSumMsg->to_the_right = to_the_right;
    outgoingRowSumMsg->diag_index = diag_index;
    for (unsigned int lapi_task = 0; lapi_task < mp_procs; ++lapi_task) {
        if (lapi_task != mp_child) {
            volatile int flag = 0;
            int lapi_rc;
            lapi_xfer_t xfer_struct;
    
            xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
            xfer_struct.Am.flags = 0;
            xfer_struct.Am.tgt = lapi_task;
            xfer_struct.Am.hdr_hdl = HdrHandler(rowSumHandler);
            xfer_struct.Am.uhdr_len = 0;
            xfer_struct.Am.uhdr = NULL;
            xfer_struct.Am.udata = outgoingRowSumMsg;
            xfer_struct.Am.udata_len = sizeof(*outgoingRowSumMsg);
            xfer_struct.Am.shdlr = mySendCompletionHandler;
            xfer_struct.Am.sinfo = (void *) &flag;
            xfer_struct.Am.tgt_cntr = 0;
            xfer_struct.Am.org_cntr = NULL;
            xfer_struct.Am.cmpl_cntr = NULL;
            lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
            CheckLapiProblem(e, lapi_rc);
            while (flag ==  0) {
                LAPI_Probe(lapi_handle);
            }
        }
    }
    free(outgoingRowSumMsg);
    outgoingRowSumMsg = NULL;
}

//////////////////////////////////////////////////////////////////////////////
// Methods for fetching an individual element from A
//////////////////////////////////////////////////////////////////////////////
typedef struct getAatMsg_s {
    jlong       key;
    jint        col;
    jint        row;
    lapi_long_t rc_addr;
    int         lapi_task_awaiting_reply;
} getAatMsg_t;

static void this_vms_getAat_compl_handler(lapi_handle_t *hndl, void *args) {

    JNIEnv *e;
    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);
    getAatMsg_t *msg = (getAatMsg_t *) args;
    jdouble d = e->CallDoubleMethod(solverNameMapping.localNameOfSolver,
                                    e->GetMethodID(mySolverClass,
                                                   "getAat", "(II)D"),
                                    msg->col, msg->row);
    CheckJNIException(e);
    doubleRetMsg_t dr;
    dr.flag0 = ~(int) 0;
    dr.d = d;
    dr.flag1 = ~(int) 0;
    lapi_xfer_t xfer_struct;
    int lapi_rc;
    
    xfer_struct.Put.Xfer_type = LAPI_PUT_XFER;
    xfer_struct.Put.flags = 0;
    xfer_struct.Put.tgt = msg->lapi_task_awaiting_reply;
    xfer_struct.Put.tgt_addr = msg->rc_addr;
    xfer_struct.Put.org_addr = &dr;
    xfer_struct.Put.len = sizeof dr;
    xfer_struct.Put.shdlr = NULL;
    xfer_struct.Put.sinfo = NULL;
    xfer_struct.Put.tgt_cntr = 0;
    xfer_struct.Put.org_cntr = NULL;
    xfer_struct.Put.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
    e->PopLocalFrame(NULL);
}

static void *this_vms_getAat_hdr_handler(lapi_handle_t *hndl,
                                         void *uhdr,
                                         unsigned int *uhdr_len,
                                         lapi_return_info_t *ret_info,
                                         compl_hndlr_t **comp_h,
                                         void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_getAat_compl_handler;
    assert(*uhdr_len == sizeof(getAatMsg_t));
    *info = uhdr;
    return NULL;
}

JNIEXPORT jdouble JNICALL Java_VMInfo_getAat
(JNIEnv *e, jobject vmInfo, jlong key, jint col, jint row) {
    volatile doubleRetMsg_t rc;
    rc.flag0 = rc.flag1 = 0;

    getAatMsg_t msg;
    msg.key = key;
    msg.col = col;
    msg.row = row;
    msg.lapi_task_awaiting_reply = mp_child;
    msg.rc_addr = (lapi_long_t) &rc;
    int lapi_rc;
    lapi_xfer_t xfer_struct;

    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(getAatHandler);
    xfer_struct.Am.uhdr_len = sizeof(msg);
    xfer_struct.Am.uhdr = &msg;
    xfer_struct.Am.udata = NULL;
    xfer_struct.Am.udata_len = 0;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
    while (rc.flag0 == 0 || rc.flag1 == 0) {
        LAPI_Probe(lapi_handle);
    }
    return rc.d;
}

//////////////////////////////////////////////////////////////////////////////
// Methods to shutdown remote VMs
//////////////////////////////////////////////////////////////////////////////
static void this_vms_shutdown_compl_handler(lapi_handle_t *hndl, void *args) {
    JNIEnv *e;

    myJavaVM->GetEnv((void **) &e, myJNIVersion);
    if (e == NULL) {
        myJavaVM->AttachCurrentThreadAsDaemon((void **) &e, NULL);
    }
    e->PushLocalFrame(32);
    jclass vmInfoClass = myVMInfoClass;
    e->SetStaticBooleanField(vmInfoClass,
                             e->GetStaticFieldID(vmInfoClass, "shallIShutdown", "Z"), JNI_TRUE);
    CheckJNIException(e);
    e->PopLocalFrame(NULL);
}
static void *this_vms_shutdown_hdr_handler(lapi_handle_t *hndl,
                                           void *uhdr,
                                           unsigned int *uhdr_len,
                                           lapi_return_info_t *ret_info,
                                           compl_hndlr_t **comp_h,
                                           void **info) {
    ret_info->ret_flags = LAPI_LOCAL_STATE;
    *comp_h = this_vms_shutdown_compl_handler;
    *info = NULL;
    return NULL;
}

JNIEXPORT void JNICALL Java_VMInfo_shutdown
(JNIEnv *e, jobject vmInfo) {
    int lapi_rc;
    lapi_xfer_t xfer_struct;
    
    xfer_struct.Am.Xfer_type = LAPI_AM_XFER;
    xfer_struct.Am.flags = 0;
    xfer_struct.Am.tgt = e->GetIntField(vmInfo, e->GetFieldID(e->GetObjectClass(vmInfo), "lapiTarget", "I"));
    xfer_struct.Am.hdr_hdl = HdrHandler(shutdownHandler);
    xfer_struct.Am.uhdr_len = 0;
    xfer_struct.Am.uhdr = NULL;
    xfer_struct.Am.udata = NULL;
    xfer_struct.Am.udata_len = 0;
    xfer_struct.Am.shdlr = NULL;
    xfer_struct.Am.sinfo = NULL;
    xfer_struct.Am.tgt_cntr = 0;
    xfer_struct.Am.org_cntr = NULL;
    xfer_struct.Am.cmpl_cntr = NULL;
    lapi_rc = LAPI_Xfer(lapi_handle, &xfer_struct);
    CheckLapiProblem(e, lapi_rc);
}

//////////////////////////////////////////////////////////////////////////////
// Methods to keep the main Solver object in a fixed place
//////////////////////////////////////////////////////////////////////////////
JNIEXPORT jlong JNICALL Java_JAxeqb_getGlobalKey
(JNIEnv *e, jobject solver) {
    solverNameMapping.localNameOfSolver = e->NewGlobalRef(solver);
    solverNameMapping.globalNameOfSolver = (jlong) solverNameMapping.localNameOfSolver;
    return solverNameMapping.globalNameOfSolver;
}

JNIEXPORT void JNICALL Java_JAxeqb_releaseGlobalKey
(JNIEnv *e, jobject solver, jlong key) {
    assert(key == solverNameMapping.globalNameOfSolver);
    e->DeleteGlobalRef(solverNameMapping.localNameOfSolver);
}

//////////////////////////////////////////////////////////////////////////////
// Start LAPI up
//////////////////////////////////////////////////////////////////////////////
JNIEXPORT void JNICALL Java_VMInfo_init
(JNIEnv *e, jclass vmInfoClass, jobjectArray VM_)
{
    e->SetStaticObjectField(vmInfoClass,
                            e->GetStaticFieldID(vmInfoClass,"VM_","[LVMInfo;"),
                            VM_);
    CheckJNIException(e);
    e->GetJavaVM(&myJavaVM);
    myJNIVersion = e->GetVersion();
    myVMInfoClass = (jclass) e->NewGlobalRef(vmInfoClass);
    mySolverClass = (jclass) e->NewGlobalRef(e->FindClass("JAxeqb"));

    if (VM_ != NULL) {
        lapi_info_t     lapi_info;
        lapi_extend_t   extended_info;
        int             lapi_rc;
        char            msg_buff[LAPI_MAX_ERR_STRING+128];

        mp_procs = e->GetArrayLength(VM_);
        CheckJNIException(e);
        mp_child = e->GetStaticIntField(vmInfoClass,
                                        e->GetStaticFieldID(vmInfoClass, "THIS_IS_VM", "I"));
        CheckJNIException(e);
        if (mp_child < mp_procs) {
           
            sprintf(msg_buff, "%d", mp_procs);
            setenv("MP_PROCS", msg_buff, 1);
            sprintf(msg_buff, "%d", mp_child);
            setenv("MP_CHILD", msg_buff, 1);
            setenv("MP_MSG_API", "lapi", 1);
            setenv("MP_EUILIB", "ip", 1);

            bzero((void *) &lapi_info,     sizeof lapi_info);
            bzero((void *) &extended_info, sizeof extended_info);
            lapi_info.add_info          = &extended_info;
            extended_info.num_udp_addr  = mp_procs;
            extended_info.add_udp_addrs = (lapi_udp_t *) malloc(sizeof(lapi_udp_t) * mp_procs);
            bool allAllocOk = true;
            for (int ht = 0; ht < sizeof(hdr_handlers) / sizeof(hdr_handlers[0]); ++ht) {
                if ((hdr_handlers[ht].handlers = (lapi_long_t *) malloc(mp_procs * sizeof(hdr_handlers[ht].handlers[0]))) == NULL) {
                    allAllocOk = false;
                    break;
                }
            }
            
            if (extended_info.add_udp_addrs != NULL && allAllocOk) {
                for (unsigned int p = 0; p < mp_procs; ++p) {
                    jobject vm_i = e->GetObjectArrayElement(VM_, p);
                
                    char *hostName;
                    char nm[NI_MAXHOST];
                    struct hostent *he;

                    if ((p == mp_child) &&
                        (gethostname(nm, sizeof(nm)-1) == 0) &&
                        ((nm[sizeof(nm)-1] = '\0') == 0) &&
                        (strlen(nm) < sizeof(nm)-1)) {
                        hostName = nm;
                    } else {
                        hostName = (char *) e->GetStringUTFChars((jstring) e->GetObjectField(vm_i, e->GetFieldID(e->GetObjectClass(vm_i), "hostName", "Ljava/lang/String;")), 0);
                    }
                    he = gethostbyname(hostName);
                    if (he == NULL || he->h_addr_list == NULL) {
                        sprintf(msg_buff,"Cannot find hostname %s (%d)",hostName, p);
                        fprintf(stderr,"%s\n", msg_buff);
                        e->ThrowNew(e->FindClass("java/lang/Error"), msg_buff);
                    } else {
                        extended_info.add_udp_addrs[p].ip_addr = ((struct in_addr *) he->h_addr_list[0])->s_addr;
                        extended_info.add_udp_addrs[p].port_no = e->GetIntField(vm_i, e->GetFieldID(e->GetObjectClass(vm_i), "portNumber", "I"));
                    }
                    if ((p == mp_child) &&
                        (gethostname(nm, sizeof(nm)-1) == 0) &&
                        ((nm[sizeof(nm)-1] = '\0') == 0) &&
                        (strlen(nm) < sizeof(nm)-1)) {
                    } else {
                        e->ReleaseStringUTFChars((jstring) e->GetObjectField(vm_i, e->GetFieldID(e->GetObjectClass(vm_i), "hostName", "Ljava/lang/String;")), hostName);
                    }
                }
                
                lapi_rc = LAPI_Init(&lapi_handle, &lapi_info);
                free(extended_info.add_udp_addrs);
                if (lapi_rc == LAPI_SUCCESS) {
                    int task_id, n_tasks;
                    int qenvtirc = LAPI_Qenv(lapi_handle, TASK_ID,   &task_id);
                    int qenvntrc = LAPI_Qenv(lapi_handle, NUM_TASKS, &n_tasks);
                    int qenvmuhs = LAPI_Qenv(lapi_handle, MAX_UHDR_SZ, (int *) &maxLAPIUHdrSz);
                    if (qenvtirc == LAPI_SUCCESS &&
                        qenvntrc == LAPI_SUCCESS &&
                        qenvmuhs == LAPI_SUCCESS &&
                        task_id  == mp_child     &&
                        n_tasks  == mp_procs) {
                        int ht;
                        for (ht = 0; ht < sizeof(hdr_handlers) / sizeof(hdr_handlers[0]); ++ht) {
                            lapi_rc = LAPI_Address_init64(lapi_handle,
                                                          (lapi_long_t) hdr_handlers[ht].this_vms_handler,
                                                          hdr_handlers[ht].handlers);
                            if (lapi_rc != LAPI_SUCCESS) {
                                break;
                            }
                        }
                        if (lapi_rc == LAPI_SUCCESS) {
                            // good to go
                        } else {
                            sprintf(msg_buff,"LAPI_Address_init rc = %d: ht = %d", lapi_rc, ht);
                            LAPI_Msg_string(lapi_rc, msg_buff + strlen(msg_buff));
                            fprintf(stderr,"%s\n", msg_buff);
                            e->ThrowNew(e->FindClass("java/lang/Error"), msg_buff);
                        }
                    } else {
                        sprintf(msg_buff, "LAPI did not init correctly, %d(%d) %d(%d) %d(%d) %d(%d)\n",
                                qenvtirc, LAPI_SUCCESS,
                                qenvntrc, LAPI_SUCCESS,
                                task_id, mp_child, n_tasks, mp_procs);
                        fprintf(stderr,"%s\n", msg_buff);
                        e->ThrowNew(e->FindClass("java/lang/Error"), msg_buff);
                    }
                } else {
                    sprintf(msg_buff,"LAPI_Init rc = %d: ", lapi_rc);
                    LAPI_Msg_string(lapi_rc, msg_buff + strlen(msg_buff));
                    fprintf(stderr,"%s\n", msg_buff);
                    e->ThrowNew(e->FindClass("java/lang/Error"), msg_buff);
                }
            } else {
                e->ThrowNew(e->FindClass("java/lang/Error"), "Could not allocate storage for LAPI init");
            }
        } else {
            sprintf(msg_buff, "mp_child (%d) >= mp_procs (%d)", mp_child, mp_procs);
            fprintf(stderr,"%s\n", msg_buff);
            e->ThrowNew(e->FindClass("java/lang/Error"), msg_buff);
        }
    } else {
        e->ThrowNew(e->FindClass("java/lang/Error"), "There is no VM_ info. with which to initialize LAPI.");
    }
}

//////////////////////////////////////////////////////////////////////////////
// Shut LAPI down
//////////////////////////////////////////////////////////////////////////////
JNIEXPORT void JNICALL Java_VMInfo_term
  (JNIEnv *e, jclass vmInfoClass)
{
    LAPI_Term(lapi_handle);
    e->DeleteGlobalRef(myVMInfoClass);
    e->DeleteGlobalRef(mySolverClass);
}
