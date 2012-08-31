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

#ifndef X10AUX_DESERIALIZATION_DISPATCHER_H
#define X10AUX_DESERIALIZATION_DISPATCHER_H

#include <x10aux/config.h>

#include <x10aux/ref.h>
#include <x10aux/network.h>

namespace x10 { namespace lang { class Reference; } }

namespace x10aux {

    class deserialization_buffer;

    typedef x10::lang::Reference* (*Deserializer)(deserialization_buffer &buf);
    template<> inline const char *typeName<Deserializer>() { return "Deserializer"; }

    typedef void (*CUDAPre)(deserialization_buffer &buf, place p,
                            size_t &blocks, size_t &threads, size_t &shm, size_t &argc, char *&argv, size_t &cmemc, char *&cmemv);
    template<> inline const char *typeName<CUDAPre>() { return "CUDAPre"; }

    typedef void (*CUDAPost)(deserialization_buffer &buf, place p,
                             size_t blocks, size_t threads, size_t shm, size_t argc, char *argv, size_t cmemc, char *cmemv);
    template<> inline const char *typeName<CUDAPost>() { return "CUDAPost"; }

    typedef void *(*BufferFinder)(deserialization_buffer &buf, x10_int len);
    template<> inline const char *typeName<BufferFinder>() { return "BufferFinder"; }

    typedef void (*Notifier)(deserialization_buffer &buf, x10_int len);
    template<> inline const char *typeName<Notifier>() { return "Notifier"; }

    template<> inline const char *typeName<serialization_id_t>() { return "serialization_id_t"; }

    enum ClosureKind {
        CLOSURE_KIND_NOT_ASYNC,    // is not a closure, or is a closure that is not created in place of an async by the desugarer
        CLOSURE_KIND_SIMPLE_ASYNC, // is an async with just finish state
        CLOSURE_KIND_GENERAL_ASYNC // is an async represented with generial XRX closure
    };

    class DeserializationDispatcher {
        protected:
        static DeserializationDispatcher *it;

        public:
        struct Data {
            BufferFinder put_bfinder;
            Notifier put_notifier;
            BufferFinder get_bfinder;
            Notifier get_notifier;
            BufferFinder cuda_put_bfinder;
            Notifier cuda_put_notifier;
            BufferFinder cuda_get_bfinder;
            Notifier cuda_get_notifier;

            Deserializer deser;
            CUDAPre cuda_pre;
            CUDAPost cuda_post;
            const char *cubin;
            const char *kernel;

            ClosureKind closure_kind;
            x10aux::msg_type mt;
            x10aux::serialization_id_t sid;
        };
        protected:

        Data *data_v;
        size_t data_c;
        size_t next_id;

        public:
        DeserializationDispatcher () : data_v(NULL), data_c(0), next_id(1) { }
        ~DeserializationDispatcher () {
            ::free(data_v); // do not use GC
        }

        
        static x10::lang::Reference* create(deserialization_buffer &buf) {
            return it->create_(buf);
        }
        static x10::lang::Reference* create(deserialization_buffer &buf, serialization_id_t id) {
            return it->create_(buf, id);
        }
        x10::lang::Reference* create_(deserialization_buffer &buf);
        x10::lang::Reference* create_(deserialization_buffer &buf, serialization_id_t id);

        static serialization_id_t addDeserializer (Deserializer deser, ClosureKind kind,
                                                   CUDAPre cuda_pre = NULL,
                                                   CUDAPost cuda_post = NULL,
                                                   const char *cubin = NULL,
                                                   const char *kernel = NULL);
        serialization_id_t addDeserializer_ (Deserializer deser, ClosureKind kind,
                                             CUDAPre cuda_pre = NULL,
                                             CUDAPost cuda_post = NULL,
                                             const char *cubin = NULL, const char *kernel = NULL);

        static CUDAPre getCUDAPre(serialization_id_t id);
        CUDAPre getCUDAPre_(serialization_id_t id);

        static CUDAPost getCUDAPost(serialization_id_t id);
        CUDAPost getCUDAPost_(serialization_id_t id);

        static serialization_id_t addPutFunctions(BufferFinder bfinder, Notifier notifier,
                                                 BufferFinder cuda_bfinder, Notifier cuda_notifier);
        serialization_id_t addPutFunctions_(BufferFinder bfinder, Notifier notifier,
                                            BufferFinder cuda_bfinder, Notifier cuda_notifier);
        static BufferFinder getPutBufferFinder(serialization_id_t id);
        BufferFinder getPutBufferFinder_(serialization_id_t id);
        static Notifier getPutNotifier(serialization_id_t id);
        Notifier getPutNotifier_(serialization_id_t id);
        static BufferFinder getCUDAPutBufferFinder(serialization_id_t id);
        BufferFinder getCUDAPutBufferFinder_(serialization_id_t id);
        static Notifier getCUDAPutNotifier(serialization_id_t id);
        Notifier getCUDAPutNotifier_(serialization_id_t id);

        static serialization_id_t addGetFunctions(BufferFinder bfinder, Notifier notifier,
                                                 BufferFinder cuda_bfinder, Notifier cuda_notifier);
        serialization_id_t addGetFunctions_(BufferFinder bfinder, Notifier notifier,
                                            BufferFinder cuda_bfinder, Notifier cuda_notifier);
        static BufferFinder getGetBufferFinder(serialization_id_t id);
        BufferFinder getGetBufferFinder_(serialization_id_t id);
        static Notifier getGetNotifier(serialization_id_t id);
        Notifier getGetNotifier_(serialization_id_t id);
        static BufferFinder getCUDAGetBufferFinder(serialization_id_t id);
        BufferFinder getCUDAGetBufferFinder_(serialization_id_t id);
        static Notifier getCUDAGetNotifier(serialization_id_t id);
        Notifier getCUDAGetNotifier_(serialization_id_t id);

        static x10aux::msg_type getMsgType(serialization_id_t id);
        x10aux::msg_type getMsgType_(serialization_id_t id);

        static x10aux::ClosureKind getClosureKind(serialization_id_t id);
        x10aux::ClosureKind getClosureKind_(serialization_id_t id);

        static serialization_id_t getSerializationId(x10aux::msg_type id);
        serialization_id_t getSerializationId_(x10aux::msg_type id);

        static void registerHandlers(void);
        void registerHandlers_(void);
    };

    inline CUDAPre DeserializationDispatcher::getCUDAPre (serialization_id_t id)
    { return it->getCUDAPre_(id); }

    inline CUDAPost DeserializationDispatcher::getCUDAPost (serialization_id_t id)
    { return it->getCUDAPost_(id); }


    inline BufferFinder DeserializationDispatcher::getPutBufferFinder (serialization_id_t id)
    { return it->getPutBufferFinder_(id); }

    inline BufferFinder DeserializationDispatcher::getGetBufferFinder (serialization_id_t id)
    { return it->getGetBufferFinder_(id); }

    inline Notifier DeserializationDispatcher::getPutNotifier (serialization_id_t id)
    { return it->getPutNotifier_(id); }

    inline Notifier DeserializationDispatcher::getGetNotifier (serialization_id_t id)
    { return it->getGetNotifier_(id); }

    inline BufferFinder DeserializationDispatcher::getCUDAPutBufferFinder (serialization_id_t id)
    { return it->getCUDAPutBufferFinder_(id); }

    inline BufferFinder DeserializationDispatcher::getCUDAGetBufferFinder (serialization_id_t id)
    { return it->getCUDAGetBufferFinder_(id); }

    inline Notifier DeserializationDispatcher::getCUDAPutNotifier (serialization_id_t id)
    { return it->getCUDAPutNotifier_(id); }

    inline Notifier DeserializationDispatcher::getCUDAGetNotifier (serialization_id_t id)
    { return it->getCUDAGetNotifier_(id); }


    inline x10aux::ClosureKind DeserializationDispatcher::getClosureKind (serialization_id_t id)
    { return it->getClosureKind_(id); }

    inline x10aux::msg_type DeserializationDispatcher::getMsgType (serialization_id_t id)
    { return it->getMsgType_(id); }

    inline serialization_id_t DeserializationDispatcher::getSerializationId (x10aux::msg_type id)
    { return it->getSerializationId_(id); }

    template<> inline const char *typeName<DeserializationDispatcher>()
    { return "DeserializationDispatcher"; }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
