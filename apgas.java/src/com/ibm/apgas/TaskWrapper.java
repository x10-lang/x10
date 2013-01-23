package com.ibm.apgas;

/**
 * Wrapper class to provide X10 serialization behavior. This class was created
 * by compiling the X10 class below: <code>
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
 * </code> Also possible that we could do this in Task instead, and avoid a
 * wrapper object. But doing it this way is is marginally easy to maintain
 * because we can just regenerate this class when serialization logic changes.
 */
@x10.runtime.impl.java.X10Generated
public class TaskWrapper extends x10.core.Ref implements
		x10.core.fun.VoidFun_0_0, x10.serialization.X10JavaSerializable {
	private static final long serialVersionUID = 1L;

	public static final x10.rtt.RuntimeType<TaskWrapper> $RTT = x10.rtt.NamedType
			.<TaskWrapper> make("TaskWrapper", /* base class */
					TaskWrapper.class, /* parents */
					new x10.rtt.Type[] { x10.core.fun.VoidFun_0_0.$RTT });

	public x10.rtt.RuntimeType<?> $getRTT() {
		return $RTT;
	}

	public x10.rtt.Type<?> $getParam(int i) {
		return null;
	}

	private void writeObject(java.io.ObjectOutputStream oos)
			throws java.io.IOException {
		if (x10.runtime.impl.java.Runtime.TRACE_SER) {
			java.lang.System.out
					.println("Serializer: writeObject(ObjectOutputStream) of "
							+ this + " calling");
		}
		oos.defaultWriteObject();
	}

	public static x10.serialization.X10JavaSerializable $_deserialize_body(
			TaskWrapper $_obj, x10.serialization.X10JavaDeserializer $deserializer)
			throws java.io.IOException {

		if (x10.runtime.impl.java.Runtime.TRACE_SER) {
			x10.runtime.impl.java.Runtime
					.printTraceMessage("X10JavaSerializable: $_deserialize_body() of "
							+ TaskWrapper.class + " calling");
		}
		com.ibm.apgas.Task task = (com.ibm.apgas.Task) $deserializer
				.readRefUsingReflection();
		$_obj.task = task;
		return $_obj;

	}

	public static x10.serialization.X10JavaSerializable $_deserializer(
			x10.serialization.X10JavaDeserializer $deserializer)
			throws java.io.IOException {
		TaskWrapper $_obj = new TaskWrapper((java.lang.System[]) null);
		$deserializer.record_reference($_obj);
		return $_deserialize_body($_obj, $deserializer);

	}

	public void $_serialize(x10.serialization.X10JavaSerializer $serializer)
			throws java.io.IOException {
		$serializer.writeObjectUsingReflection(this.task);
	}

	// constructor just for allocation
	public TaskWrapper(final java.lang.System[] $dummy) {
	}

	public com.ibm.apgas.Task task;

	// creation method for java code (1-phase java constructor)
	public TaskWrapper(final com.ibm.apgas.Task t) {
		this((java.lang.System[]) null);
		TaskWrapper$$init$S(t);
	}

	// constructor for non-virtual call
	final public TaskWrapper TaskWrapper$$init$S(final com.ibm.apgas.Task t) {

		this.task = ((com.ibm.apgas.Task) (t));

		return this;
	}

	public void $apply() {
		final com.ibm.apgas.Task t1 = ((com.ibm.apgas.Task) (task));
		t1.body();
	}

	final public TaskWrapper TaskWrapper$$TaskWrapper$this() {
		return TaskWrapper.this;
	}

}
