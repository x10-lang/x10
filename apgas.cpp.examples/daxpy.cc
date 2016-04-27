/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2016.
 */

#include <apgas/Runtime.h>
#include <apgas/Task.h>
#include <apgas/RemoteTask.h>
#include <x10/array/DistArray_Block_1.h>

#include <stdio.h>

using namespace apgas;

/**
 * This program initializes two distributed arrays x and y, then computes
 * y = alpha * x + y using the high-level operation DistArray_Block1.map(...) .
 */

const x10_long N = 20;

/**
 * Given values x:Double, y:Double, and captured environment alpha:Double,
 * returns alpha * x + y.
 */
class AXPY: public ::x10::lang::Closure {
public:

	// captured environment
	x10_double alpha;

	AXPY(x10_double alpha) :
			alpha(alpha) {
	}

	x10_double __apply(x10_double yval, x10_double xval) {
		return ((((alpha) * (xval))) + (yval));
	}

	static ::x10::lang::Fun_0_2<x10_double, x10_double, x10_double>::itable<AXPY> _itable;
	static ::x10aux::itable_entry _itables[2];

	virtual ::x10aux::itable_entry* _getITables() {
		return _itables;
	}

	::x10aux::serialization_id_t _get_serialization_id() {
		return _serialization_id;
	}

	void _serialize_body(::x10aux::serialization_buffer &buf) {
		buf.write(this->alpha);
	}

	static x10::lang::Reference* _deserialize(
			::x10aux::deserialization_buffer &buf) {
		AXPY* storage = ::x10aux::alloc_z<AXPY>();
		buf.record_reference(storage);
		x10_double that_alpha = buf.read<x10_double>();
		AXPY* this_ = new (storage) AXPY(that_alpha);
		return this_;
	}

	static const ::x10aux::serialization_id_t _serialization_id;

	static const ::x10aux::RuntimeType* getRTT() {
		return ::x10aux::getRTT<
				::x10::lang::Fun_0_2<x10_double, x10_double, x10_double> >();
	}
	virtual const ::x10aux::RuntimeType *_type() const {
		return ::x10aux::getRTT<
				::x10::lang::Fun_0_2<x10_double, x10_double, x10_double> >();
	}

};
// AXPY

::x10::lang::Fun_0_2<x10_double, x10_double, x10_double>::itable<AXPY> AXPY::_itable(
		&::x10::lang::Reference::equals, &::x10::lang::Closure::hashCode,
		&AXPY::__apply, &AXPY::toString, &::x10::lang::Closure::typeName);
::x10aux::itable_entry AXPY::_itables[2] = { ::x10aux::itable_entry(
		&::x10aux::getRTT<
				::x10::lang::Fun_0_2<x10_double, x10_double, x10_double> >,
		&AXPY::_itable), ::x10aux::itable_entry(NULL, NULL) };

const ::x10aux::serialization_id_t AXPY::_serialization_id =
		::x10aux::DeserializationDispatcher::addDeserializer(
				AXPY::_deserialize);

class PrintY: public RemoteTask {
private:
	x10::array::DistArray_Block_1<x10_double>* y;
public:
	PrintY(x10::array::DistArray_Block_1<x10_double>* y) :
			y(y) {
	}

	static const ::x10aux::serialization_id_t _serialization_id;
	static const ::x10aux::serialization_id_t _network_id;
	::x10aux::serialization_id_t _get_serialization_id() {
		return _serialization_id;
	}
	::x10aux::serialization_id_t _get_network_id() {
		return _network_id;
	}

	void _serialize_body(::x10aux::serialization_buffer &buf) {
		buf.write(this->y);
	}

	static x10::lang::Reference* _deserialize(
			::x10aux::deserialization_buffer &buf) {
		PrintY* storage = Runtime::alloc<PrintY>();
		buf.record_reference(storage);
		x10::array::DistArray_Block_1 < x10_double > *y = buf.read<
				x10::array::DistArray_Block_1<x10_double>*>();
		PrintY* task = new (storage) PrintY(y);
		return task;
	}

	virtual void execute() {
		x10::array::IterationSpace* iter = y->localIndices();
		x10_long min = iter->min(0);
		x10_long max = iter->max(0);
		for (x10_long i = min; i <= max; ++i) {
			printf("%.3f\n", y->__apply(i));
		}
	}
};
// PrintY

const ::x10aux::serialization_id_t PrintY::_serialization_id =
		::x10aux::DeserializationDispatcher::addDeserializer(
				PrintY::_deserialize);
const ::x10aux::serialization_id_t PrintY::_network_id =
		::x10aux::NetworkDispatcher::addNetworkDeserializer(
				PrintY::_deserialize, ::x10aux::CLOSURE_KIND_ASYNC_CLOSURE);

/*
 * This closure is used to initialize a single array element of type Double.
 * For this example we just set each element to the double value of its index
 * i.e. A(i) = i as Double;
 */
class LongToDouble: public ::x10::lang::Closure {
public:

	// captured environment is empty

	LongToDouble() {
	}

	x10_double __apply(x10_long i) {
		return ((x10_double)(i));
	}

	static ::x10::lang::Fun_0_1<x10_long, x10_double>::itable<LongToDouble> _itable;
	static ::x10aux::itable_entry _itables[2];

	virtual ::x10aux::itable_entry* _getITables() {
		return _itables;
	}

	::x10aux::serialization_id_t _get_serialization_id() {
		return _serialization_id;
	}

	void _serialize_body(::x10aux::serialization_buffer &buf) {

	}

	static x10::lang::Reference* _deserialize(
			::x10aux::deserialization_buffer &buf) {
		LongToDouble* storage = ::x10aux::alloc_z<LongToDouble>();
		buf.record_reference(storage);
		LongToDouble* this_ = new (storage) LongToDouble();
		return this_;
	}

	static const ::x10aux::serialization_id_t _serialization_id;

	static const ::x10aux::RuntimeType* getRTT() {
		return ::x10aux::getRTT< ::x10::lang::Fun_0_1<x10_long, x10_double> >();
	}
	virtual const ::x10aux::RuntimeType *_type() const {
		return ::x10aux::getRTT< ::x10::lang::Fun_0_1<x10_long, x10_double> >();
	}
};
// LongToDouble

::x10::lang::Fun_0_1<x10_long, x10_double>::itable<LongToDouble> LongToDouble::_itable(
		&::x10::lang::Reference::equals, &::x10::lang::Closure::hashCode,
		&LongToDouble::__apply, &LongToDouble::toString,
		&::x10::lang::Closure::typeName);
::x10aux::itable_entry LongToDouble::_itables[2] = { ::x10aux::itable_entry(
		&::x10aux::getRTT< ::x10::lang::Fun_0_1<x10_long, x10_double> >,
		&LongToDouble::_itable), ::x10aux::itable_entry(NULL, NULL) };

const ::x10aux::serialization_id_t LongToDouble::_serialization_id =
		::x10aux::DeserializationDispatcher::addDeserializer(
				LongToDouble::_deserialize);

class MyMain: public Task {
private:
	int argc;
	char **argv;
public:
	MyMain(int ac, char** av) :
			argc(ac), argv(av) {
	}

	virtual void execute() {
        // initialize distributed arrays x and y
		x10::array::DistArray_Block_1 <x10_double> *x =
			x10::array::DistArray_Block_1 <x10_double>::_make(N,
			reinterpret_cast< ::x10::lang::Fun_0_1<x10_long,x10_double>*>(
			(new (Runtime::alloc< ::x10::lang::Fun_0_1<x10_long,x10_double> >
			(sizeof(LongToDouble))) LongToDouble())));

		x10::array::DistArray_Block_1 <x10_double> *y =
			x10::array::DistArray_Block_1 <x10_double>::_make(N,
			reinterpret_cast< ::x10::lang::Fun_0_1<x10_long,x10_double>*>(
			(new (Runtime::alloc< ::x10::lang::Fun_0_1<x10_long,x10_double> >
			(sizeof(LongToDouble))) LongToDouble())));

		x10_double alpha = 2.0;

        // compute y = alpha * x + y
		y->map<x10_double, x10_double>(
			reinterpret_cast< ::x10::array::DistArray<x10_double>*>(x),
			reinterpret_cast< ::x10::array::DistArray<x10_double>*>(y),
			reinterpret_cast< ::x10::lang::Fun_0_2<x10_double, x10_double, x10_double>*>(
			(new (
			Runtime::alloc< ::x10::lang::Fun_0_2<x10_double, x10_double,x10_double> >
				(sizeof(AXPY))) AXPY(alpha))));

        // print elements of y
		int np = getRuntime()->numPlaces();
		for (int i = 0; i < np; i++) {
			RemoteTask* t = new (Runtime::alloc<PrintY>()) PrintY(y);
			getRuntime()->runAsyncAt(i % np, t);
		}
	}
};

int main(int argc, char **argv) {
	Runtime* rt = Runtime::getRuntime();
	rt->start(argc, argv);

	if (rt->here() == 0) {
		MyMain m(argc, argv);
		rt->runSync(&m);
		rt->terminate();
	}
}
