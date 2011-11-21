package com.ibm.apgas;

import java.io.IOException;
import java.io.ObjectOutputStream;

import x10.core.Ref;
import x10.core.X10Generated;
import x10.core.fun.VoidFun_0_0;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

/**
 * Wrapper class to provide X10 serialization behavior.
 * This class was created by compiling the X10 class below:
 * <code>
 * import com.ibm.apgas.Task;
 * class TaskWrapper implements ()=>void {
 *   val task:Task;
 *
 *   def this(t:Task) {
 *     this.task = t;
 *    }
 *
 *   public operator this() { task.body(); }
 * }
 * </code>
 * Also possible that we could do this in Task instead,
 * and avoid a wrapper object. But doing it this way is
 * is marginally easy to maintain because we can just regenerate
 * this class when serialization logic changes.
 */
@X10Generated class TaskWrapper extends Ref implements VoidFun_0_0, X10JavaSerializable {
	private static final long serialVersionUID = 1L;
	private static final short $_serialization_id = DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, TaskWrapper.class);

	@SuppressWarnings("unchecked")
	public static final RuntimeType<TaskWrapper> $RTT = NamedType.<TaskWrapper> make("TaskWrapper", TaskWrapper.class,new Type[] {VoidFun_0_0.$RTT, Types.OBJECT});
	public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}

	private Task task;

	private void writeObject(ObjectOutputStream oos) throws java.io.IOException { 
		if (x10.runtime.impl.java.Runtime.TRACE_SER) { 
			System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); 
		} 
		oos.defaultWriteObject(); 
	}

	public static X10JavaSerializable $_deserialize_body(TaskWrapper obj, X10JavaDeserializer deserializer) throws IOException { 
		if (x10.runtime.impl.java.Runtime.TRACE_SER) { 
			x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + TaskWrapper.class + " calling"); 
		} 
		Task task = (Task) deserializer.readRefUsingReflection();
		obj.task = task;
		return obj;
	}

	public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException { 
		TaskWrapper obj = new TaskWrapper((java.lang.System[]) null);
		deserializer.record_reference(obj);
		return $_deserialize_body(obj, deserializer);
	}

	public short $_get_serialization_id() {
		return $_serialization_id;
	}

	public void $_serialize(X10JavaSerializer serializer) throws java.io.IOException {
		serializer.writeObjectUsingReflection(this.task);
	}

	// constructor just for allocation
	public TaskWrapper(final java.lang.System[] $dummy) { 
		super($dummy);
	}

	TaskWrapper(Task t) {
		this.task = t;
	}

	@Override
	public void $apply() {
		task.body();
	}
}
