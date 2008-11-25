#ifndef X10AUX_HERE_H
#define X10AUX_HERE_H

#include <x10aux/serialization.h>

namespace x10 {
    namespace lang {
        template<class T> class Rail;
    }
}

namespace x10aux {

    class place {
    public:
        const x10_int x10__id;

        place(x10_int id) : x10__id(id) { }

        place(const ref<place>& ref) : x10__id(ref->x10__id) { }

        operator x10_int() const { return x10__id; }

        static place places(x10_int id) { return id; }

        place next() const { return abs((x10__id + 1) % x10__MAX_PLACES); }

        place prev() const { return abs((x10__id - 1) % x10__MAX_PLACES); }

        const place* operator->() const { return this; }

        place(const place& p) : x10__id(p.x10__id) { }

        const place& operator=(const place& p) {
            const_cast<x10_int&>(x10__id) = p.x10__id;
            return *this;
        }

        static const x10_int x10__MAX_PLACES;

        static const place x10__FIRST_PLACE;

        static const ref<x10::lang::Rail<place> > x10__placeArray;


    private:
        static void __init__MAX_PLACES();

        friend class __init__;


    public: // Serialization
        void _serialize(serialization_buffer& buf, addr_map& m) { _serialize_ref(this, buf, m); }

        void _serialize_fields(serialization_buffer& buf, addr_map& m) {
            (void) m;
            buf.write(x10__id);
            _S_("Written " << x10__id);
        }

        void _deserialize_fields(serialization_buffer& buf) {
            (void)buf;
            assert(false);
        }
    };


    inline place here() { return (place) x10_here(); }

    template<class T> inline place location(T* p) {
        return (place) x10_ref_get_loc((x10_addr_t)p);
    }

    template<class T> inline place location(ref<T> r) {
        return (place) x10_ref_get_loc((x10_addr_t)r.operator->());
    }

    // FIXME: the operator below clashes with built-in stuff
    //inline bool operator==(x10_int i, const place& p) {
    //    return i == (x10_int)p; }
    inline bool operator!=(x10_int i, const place& p) {
        return i != (x10_int)p;
    }

    // Specialized serialization of x10aux::place
    template<> struct _reference_serializer<place> {
        static void _(const ref<place>& v, serialization_buffer& buf, addr_map& m) {
            _Sd_(size_t len = buf.length());
            _S_("Serializing place");
            v->_serialize_fields(buf, m);
            _S_(_dump_chars(((const char*)buf) + len, buf.length() - len));
        }
    };
    template<> struct _reference_deserializer<place> {
        static ref<place> _(serialization_buffer& buf) {
            // TODO: null
            x10_int id = buf.read<x10_int>();
            _S_("Deserializing place " << id);
            return new place(id);
        }
    };

    extern place __here__;


}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
