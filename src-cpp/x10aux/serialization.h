#ifndef X10AUX_SERIALIZATION_H
#define X10AUX_SERIALIZATION_H


#include <x10aux/config.h>

#include <x10/x10.h> // pgas

#include <x10aux/pgas.h> // pgas
#include <x10aux/ref.h>
#include <x10aux/alloc.h>
#include <x10aux/deserialization_dispatcher.h>


/*
 * Serialization support.
 *
 * [DC] I will soon add documentation that is based on the old documentation but reflects the
 * changes I have made.  The only reason I haven't done it already is I'm too tired.
 */


namespace x10 {
    namespace lang {
        class Object;
    }
}


namespace x10aux {

    class AnyClosure;

    class SERIALIZATION_MARKER { };

    // Allows runtime detection of Value cycles
    class addr_map {
        int _size;
        const void** _ptrs;
        int _top;
        void _grow() {
            _ptrs = (const void**) ::memcpy(new (x10aux::alloc<const void*>((_size<<1)*sizeof(const void*))) const void*[_size<<1], _ptrs, _size*sizeof(const void*));
            _size <<= 1;
        }
        void _add(const void* ptr) {
            if (_top == _size) _grow();
            _ptrs[_top++] = ptr;
        }
        bool _find(const void* ptr) {
            for (int i = 0; i < _top; i++)
                if (_ptrs[i] == ptr) return true;
            return false;
        }
    public:
        addr_map(int init_size = 4) : _size(init_size), _ptrs(new (x10aux::alloc<const void*>((init_size)*sizeof(const void*)))const void*[init_size]), _top(0) {
            assert (_ptrs != NULL);
        }
        template<class T> bool ensure_unique(const ref<T>& r) {
            return ensure_unique((void*) r.get());
        }
        bool ensure_unique(const void* p) {
            if (_find(p)) return false;
            _add(p);
            return true;
        }
        void reset() { _top = 0; assert (false); }
        ~addr_map() { x10aux::dealloc(_ptrs); }
    };



    // A growable buffer for serialising into and out of
    class serialization_buffer {
    private:
        static const int GROW_PERCENT = 200; // can't use float but don't want extra .cc file
        static const size_t INITIAL_SIZE = 16;
        char* buffer;
        char* limit;
        char* cursor;
        static char* alloc(size_t size) { return x10aux::alloc<char>(size); }
        static void dealloc(char* buf) { x10aux::dealloc<char>(buf); }
        char* grow() {
            assert (limit != NULL);
            char* saved_buf = buffer;
            size_t length = cursor - buffer;
            size_t allocated = limit - buffer;
            size_t new_size = (size_t) (allocated * GROW_PERCENT/100.0);
            buffer = alloc(new_size);
            ::memcpy(buffer, saved_buf, length);
            limit = buffer + new_size;
            cursor = buffer + length;
            dealloc(saved_buf);
            return buffer;
        }
    public:

        serialization_buffer()
            : buffer(alloc(INITIAL_SIZE)), limit(buffer + INITIAL_SIZE), cursor(buffer)
        { }

        ~serialization_buffer() {
            if (limit!=NULL)
                dealloc(buffer);
        }

        const char *get() const { return buffer; }

        // we use the same buffers for serializing and deserializing so the
        // const cast is necessary
        void set(const char* buf) { set(const_cast<char*>(buf)); }
        void set(char* buf) {
            buffer = cursor = buf;
            limit = NULL;
        }

        size_t length() { return cursor - buffer; }


        // default case for primitives and other things that never contain pointers
        template<class T> struct Write {
            static void _(serialization_buffer &buf, const T &val, addr_map &) {
                // FIXME: assumes all places are same endian
                _S_("Serializing a "ANSI_SER<<TYPENAME(T)<<ANSI_RESET": "<<val<<" into buf: "<<&buf);
                *(T*) buf.cursor = val;
                buf.cursor += sizeof(T);
            }
        };

        // case for references e.g. ref<Object>, 
        template<class T> struct Write<ref<T> > {
            static void _(serialization_buffer &buf, ref<T> val, addr_map &m) {
                _S_("Serializing a "ANSI_SER ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET
                    " into buf: "<<&buf);
                //depends what T is (interface/Ref/Value/FinalValue/Closure)
                T::_serialize(val,buf,m);
            }
        };

        template<typename T> void write(const T &val, addr_map &m) {
            if (cursor + sizeof(T) >= limit) grow();
            Write<T>::_(*this,val,m);
        }


        // default case for primitives and other things that never contain pointers
        template<class T> struct Read {
            static T &_(serialization_buffer &buf) {
                // FIXME: assumes all places are same endian
                T &val = *(T*) buf.cursor;
                buf.cursor += sizeof(T);
                _S_("Deserializing a "ANSI_SER<<TYPENAME(T)<<ANSI_RESET": "<<val<<" into buf: "<<&buf);
                return val;
            }
        };

        // case for references e.g. ref<Object>, 
        template<class T> struct Read<ref<T> > {
            static ref<T> _(serialization_buffer &buf) {
                //dispatch because we don't know what it is
                _S_("Deserializing a "ANSI_SER ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET" from buf: "<<&buf);
                return T::template _deserialize<T>(buf);
            }
        };

        template<typename T> T read() {
            return Read<T>::_(*this);
        }

/*
        void clean() {
            dealloc(buffer);
            buffer = alloc(INITIAL_SIZE);
            limit = buffer + INITIAL_SIZE;
            cursor = buffer;
        }
*/

    };


#if 0

#ifndef NO_IOSTREAM
    // Debug helper; usage: o << _dump_chars(b, l)
    class _dump_chars {
        const char* _buf;
        int _len;
        const char* _delim;
    public:
        _dump_chars(const char* buf, int len, const char* delim=" ") :
            _buf(buf), _len(len), _delim(delim) { }
        friend std::ostream& operator<<(std::ostream& os, const _dump_chars d) {
            for (int i = 0; i < d._len; i++)
                os << (i==0?"":d._delim) << (int)(unsigned char)d._buf[i];
            return os;
        }
    };
#endif




    ////////////////////////////////////////////////////////////////////////////////////////////
    //SERIALIZATION/////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    // Specialize for classes without SERIALIZATION_ID (e.g., final classes)
    template<class T> struct _reference_serializer {
        static void _(ref<T> v, serialization_buffer& buf, addr_map& m) {
            _Sd_(size_t len = buf.length());
            _S_("Serializing " << TYPENAME(T) << "...");
            buf.write(T::SERIALIZATION_ID);
            _S_("Wrote " << T::SERIALIZATION_ID);
            v->_serialize_fields(buf, m);
            _S_(x10aux::_dump_chars(((const char*)buf) + len, buf.length() - len));
        }
    };



    template<class T> inline void _serialize_ref(ref<T> v, serialization_buffer& buf, addr_map& m) {
        _reference_serializer<T>::_(v, buf, m);
    }

    template<class T> inline void _serialize_ref(T* p, serialization_buffer& buf, addr_map& m) {
        _serialize_ref(ref<T>(p), buf, m);
    }


    // assume that T implements a (not necessarily virtual) member function _serialize
    template<class T> inline void _serialize_value_ref(serialization_buffer& buf,
                                                       addr_map& m,
                                                       const ref<T>& v)
    {
        v->_serialize(buf, m);
    }

    template<class T> inline void _serialize_value_ref(serialization_buffer& buf,
                                                       addr_map& m,
                                                       T* p)
    {
        _serialize_value_ref(buf, m, ref<T>(p));
    }





    ////////////////////////////////////////////////////////////////////////////////////////////
    //DESERIALIZATION///////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    // Specialize for classes without SERIALIZATION_ID (e.g., final classes)
    template<class T> struct _reference_deserializer {
        static ref<T> _(serialization_buffer& buf) {
            /*
            int id = buf.read<int>();
            // TODO
            //if (id == NULL_SERIALIZATION_ID)
            //    return null;
            assert (id == T::SERIALIZATION_ID);
            _S_("Deserializing " << TYPENAME(T));
            ref<T> rv = ref<T>(new (alloc<T>()) T(SERIALIZATION_MARKER()));
            rv->_deserialize_fields(buf);
            return rv;
            */
            // [DC] this doesn't work when T is an abstract class
            // not sure whether it should be called in these cases at all
            // but commenting it out seems like the best option right now
            (void) buf;
            return 0;
        }
    };

    template<class T> inline ref<T> _deserialize_ref(serialization_buffer& buf) {
        return _reference_deserializer<T>::_(buf);
    }

    template<class T> inline ref<T> _deserialize_value_ref(serialization_buffer& buf) {
        return _deserialize_ref<T>(buf);
    }

    template<class T> inline void serialize_value_type(serialization_buffer& buf, const ref<T>& v) {
        addr_map m;
        _serialize_value_ref(buf, m, v);
    }

    template<class T> inline x10aux::ref<T> deserialize_value_type(serialization_buffer& buf) {
        return _deserialize_value_ref<T>(buf);
    }

    // TODO: Specialize x10::lang::Object serialization/deserialization
    template<> void serialize_value_type<x10::lang::Object>(serialization_buffer& buf, const x10aux::ref<x10::lang::Object>& v);

    template<> x10aux::ref<x10::lang::Object> _deserialize_value_ref<x10::lang::Object>(serialization_buffer& buf);

    struct subclass_vector {
        size_t _length;
        void* (**_subclasses)(serialization_buffer&);
    };

    void _register_subclass_impl(int id, subclass_vector& registered_subclasses, void* (*deserialize_func)(serialization_buffer&));

    template<class S, class T> inline void _register_subclass() {
        //_S_("Registering " << TYPENAME(T) << " as a subclass of " << TYPENAME(S) << " (id=" << id << ")");
        ref<T>(*deserialize_func)(serialization_buffer&) = _deserialize_ref<T>;
        _register_subclass_impl(T::SERIALIZATION_ID, S::_registered_subclasses, reinterpret_cast<void*(*)(serialization_buffer&)>(deserialize_func));
//        int id = T::SERIALIZATION_ID;
//        assert (id >= 0);
//        if (S::_registered_subclasses._length <= (size_t)id) {
//            S::_registered_subclasses._subclasses = (ref<S>(**)(serialization_buffer&)) realloc(S::_registered_subclasses._subclasses, (id+1) * sizeof(ref<S>(*)(serialization_buffer&)));
//            S::_registered_subclasses._length = id+1;
//        }
//        //extern ref<T> _deserialize_ref<T>(serialization_buffer& buf);
//        ref<T>(*deserialize_func)(serialization_buffer&) = _deserialize_ref<T>;
//        S::_registered_subclasses._subclasses[id] = reinterpret_cast<ref<S>(*)(serialization_buffer&)>(deserialize_func);
    }

    void* _deserialize_subclass_impl(int id, serialization_buffer& buf, const subclass_vector& registered_subclasses);

    template<class S> inline ref<S> _deserialize_subclass(int id, serialization_buffer& buf) {
        return ref<S>(reinterpret_cast<S*>(_deserialize_subclass_impl(id, buf, S::_registered_subclasses)));
//        assert (id >= 0);
//        assert ((size_t)id < S::_registered_subclasses._length);
//        ref<S>(*deserialize_func)(serialization_buffer&) = S::_registered_subclasses._subclasses[id];
//        if (deserialize_func == NULL) {
//            assert(false); return null;
//        }
//        return deserialize_func(buf);
    }

/*
    template<class S> inline ref<S> _deserialize_superclass(serialization_buffer& buf) {
        _S_("Deserializing " << TYPENAME(S) << " from ' " << _dump_chars(buf, 40) << " '");
        int id = buf.peek<int>();
        _S_("Id = " << id);
        return _deserialize_subclass<S>(id, buf);
    }
*/

/*
    extern "C" x10aux::AnyClosure *__x10_callback_closureswitch(int id, serialization_buffer& s);
    template<> struct _reference_deserializer<x10aux::AnyClosure> { 
        // can't have a ref of things that don't have RTTs
        static x10aux::AnyClosure *_(serialization_buffer& s) {
            int id = s.read<int>();
            return __x10_callback_closureswitch(id,s);
        }
    };
*/

#endif

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100

