package x10.x10rt;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A registry of all active messages known to the system.
 */
public class MessageRegistry {
  private static final ConcurrentHashMap<Method, ActiveMessage> registry = new ConcurrentHashMap<Method, ActiveMessage>();
  private static final ReentrantLock registryLock = new ReentrantLock();
  private static int nextMessageId = 1;

  /**
   * Attempt to lookup a previously registered ActiveMessage
   * object for the argument method.
   *
   * @param method the method to use as a key for the lookup
   * @return The ActiveMessage object that should be used to invoke method.
   * @throws UnknownMessageException if a registered ActiveMessage object cannot be found.
   */
  public static ActiveMessage lookup(Method method) throws UnknownMessageException {
    ActiveMessage message = registry.get(method);
    if (message == null) throw new UnknownMessageException(method.toGenericString());
    return message;
  }

  /**
   * Attempt to lookup a previously registered ActiveMessage
   * object for the method described by the arguments.
   *
   * @param declaringClass the declaring class of the desired method
   * @param name the method name of the desired method
   * @param parameterTypes the list of Class objects corresponding to the parameter types for the desired method.
   * @return The ActiveMessage object that should be used to invoke method.
   * @throws UnknownMessageException if a registered ActiveMessage object cannot be found.
   * @throws IllegalArgumentException if the reflective lookup via {@link Class#getDeclaredMethod(String, Class...)}
   *                                  fails for any reason.
   */
  public static ActiveMessage lookup(Class<?> declaringClass, String name, Class<?>...parameterTypes) throws UnknownMessageException,
                                                                                                             IllegalArgumentException {
    try {
      Method m = declaringClass.getDeclaredMethod(name, parameterTypes);
      return lookup(m);
    } catch (SecurityException e) {
      throw new IllegalArgumentException(e);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Register the argument method as an ActiveMessage.  If the method has already been registered,
   * the existing ActiveMessage object is returned.  If this is the first time the method has
   * been registered, a new ActiveMessage object is created, registered with X10RT, and returned.
   *
   * @param method the method to use as a key for the lookup
   * @return The ActiveMessage object that should be used to invoke method.
   * @throws IllegalArgumentException if the method is not a valid candidate to invoke via an ActiveMessage.
   */
  public static ActiveMessage register(Method method) throws IllegalArgumentException {
    registryLock.lock();
    try {
      ActiveMessage message = registry.get(method);
      if (message == null) {
        message = ActiveMessage.registerMessage(method, nextMessageId++);
        registry.put(method, message);
      }
      return message;
    } finally {
      registryLock.unlock();
    }
  }

  /**
   * Register the argument method as an ActiveMessage.  If the method has already been registered,
   * the existing ActiveMessage object is returned.  If this is the first time the method has
   * been registered, a new ActiveMessage object is created, registered with X10RT, and returned.
   *
   * @param declaringClass the declaring class of the desired method
   * @param name the method name of the desired method
   * @param parameterTypes the list of Class objects corresponding to the parameter types for the desired method.
   * @return The ActiveMessage object that should be used to invoke method.
   * @throws IllegalArgumentException if the method is not a valid candidate to invoke via an ActiveMessage or
   *                                  if the reflective lookup via {@link Class#getDeclaredMethod(String, Class...)}
   *                                  fails for any reason.
   */
  public static ActiveMessage register(Class<?> declaringClass, String name, Class<?>...parameterTypes) throws IllegalArgumentException {
    try {
      Method m = declaringClass.getDeclaredMethod(name, parameterTypes);
      return register(m);
    } catch (SecurityException e) {
      throw new IllegalArgumentException(e);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
