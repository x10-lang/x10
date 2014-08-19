package apgas.impl;

import java.io.Serializable;

/**
 * A serializable runnable interface.
 */
@FunctionalInterface
interface SerializableRunnable extends Serializable, Runnable {
}
