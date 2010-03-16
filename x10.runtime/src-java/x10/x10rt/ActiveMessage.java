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

package x10.x10rt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * <p>A class whose instances represent active messages that have been registered
 * with the X10RT runtime.  The primary operation that is available, is to invoke
 * send on the message object, which will send an active message to the target place
 * composed of a message id and the data payload.  The target place will respond to this
 * message by invoking the java method associated with the active message with the data
 * payload of the message as its arguments.</p>
 * <p>It is important to note that this implements a mostly asynchronous messaging system.
 * If a place sends a message to itself, then send will be synchronous (the send is implemented
 * by directly invoking the target method with the given arguments).  If the place sends a message
 * to a different place, then send will return as soon as the data has been transfered to the network
 * layer and the send is locally complete.  Local completion does not imply that the target method
 * has been invoked/completed on the remote place.</p>
 */
public class ActiveMessage {

    private static final int MAX_MESSAGE_ID = 100;

    /**
     * A simple lookup from message ids to messages.
     * Used to implement {@link #receiveGeneral(int, byte[])}.
     */
    private static final ActiveMessage[] messages = new ActiveMessage[MAX_MESSAGE_ID+1];

    /**
     * The method that will be invoked when the active message is sent.
     */
    private final Method method;

    /**
     * The numeric id assigned to this message by the {@link MessageRegistry} and
     * used to communicate message identity between places.
     */
    private final int messageId;

    private ActiveMessage(Method method, int id) throws UnsupportedOperationException {
        this.method = method;
        this.messageId = id;
    }

    static ActiveMessage registerMessage(Method method, int id) throws IllegalArgumentException {
        assert X10RT.isBooted() : "must boot X10RT before registering active messages";

        if (method.getReturnType() != Void.TYPE) {
            throw new IllegalArgumentException("Cannot register a method with a non-void return type as an active message");
        }

        assert id < MAX_MESSAGE_ID : "Implement resizing of native registeredMethods array";

        ActiveMessage msg = new ActiveMessage(method, id);
        messages[id] = msg;
        registerMethodImpl(msg.method, msg.method.getDeclaringClass(), msg.messageId);

        return msg;
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rtrt
            sendRemote(place.getId(), messageId);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg first argument to the target method
     */
    public void send(Place place, int arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Reflection is picky about narrowing conversions, while jni is not.
                // So for the local case, we need to undo any widening conversion
                // that was done before doing the invoke.
                Class<?> argType = method.getParameterTypes()[0];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, (byte)arg);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, (short)arg);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, (char)arg);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, int arg1, int arg2) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Reflection is picky about narrowing conversions, while jni is not.
                // So for the local case, we need to undo any widening conversion
                // that was done before doing the invoke.
                Class<?>[] argTypes = method.getParameterTypes();
                if (argTypes[0].equals(Integer.TYPE) && argTypes[1].equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else {
                    int[] args = new int[] { arg1, arg2 };
                    Object[] boxedArgs = new Object[argTypes.length];
                    for (int i=0; i<argTypes.length; i++) {
                        Class<?> argType = argTypes[i];
                        int arg = args[i];
                        if (argType.equals(Byte.TYPE)) {
                            boxedArgs[i] = Byte.valueOf((byte)arg);
                        } else if (argType.equals(Short.TYPE)) {
                            boxedArgs[i] = Short.valueOf((short)arg);
                        } else if (argType.equals(Character.TYPE)){
                            boxedArgs[i] = Character.valueOf((char)arg);
                        } else {
                            assert argType.equals(Integer.TYPE);
                            boxedArgs[i] = Integer.valueOf(arg);
                        }
                    }
                    method.invoke(null, boxedArgs);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     * @param arg3 third argument to the target method
     */
    public void send(Place place, int arg1, int arg2, int arg3) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Reflection is picky about narrowing conversions, while jni is not.
                // So for the local case, we need to undo any widening conversion
                // that was done before doing the invoke.
                Class<?>[] argTypes = method.getParameterTypes();
                if (argTypes[0].equals(Integer.TYPE) && argTypes[1].equals(Integer.TYPE) && argTypes[2].equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2, arg3);
                } else {
                    int[] args = new int[] { arg1, arg2, arg3 };
                    Object[] boxedArgs = new Object[argTypes.length];
                    for (int i=0; i<argTypes.length; i++) {
                        Class<?> argType = argTypes[i];
                        int arg = args[i];
                        if (argType.equals(Byte.TYPE)) {
                            boxedArgs[i] = Byte.valueOf((byte)arg);
                        } else if (argType.equals(Short.TYPE)) {
                            boxedArgs[i] = Short.valueOf((short)arg);
                        } else if (argType.equals(Character.TYPE)){
                            boxedArgs[i] = Character.valueOf((char)arg);
                        } else {
                            assert argType.equals(Integer.TYPE);
                            boxedArgs[i] = Integer.valueOf(arg);
                        }
                    }
                    method.invoke(null, boxedArgs);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2, arg3);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     * @param arg3 third argument to the target method
     * @param arg4 fourth argument to the target method
     */
    public void send(Place place, int arg1, int arg2, int arg3, int arg4) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Reflection is picky about narrowing conversions, while jni is not.
                // So for the local case, we need to undo any widening conversion
                // that was done before doing the invoke.
                Class<?>[] argTypes = method.getParameterTypes();
                if (argTypes[0].equals(Integer.TYPE) && argTypes[1].equals(Integer.TYPE) &&
                        argTypes[2].equals(Integer.TYPE) && argTypes[4].equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2, arg3, arg4);
                } else {
                    int[] args = new int[] { arg1, arg2, arg3, arg4};
                    Object[] boxedArgs = new Object[argTypes.length];
                    for (int i=0; i<argTypes.length; i++) {
                        Class<?> argType = argTypes[i];
                        int arg = args[i];
                        if (argType.equals(Byte.TYPE)) {
                            boxedArgs[i] = Byte.valueOf((byte)arg);
                        } else if (argType.equals(Short.TYPE)) {
                            boxedArgs[i] = Short.valueOf((short)arg);
                        } else if (argType.equals(Character.TYPE)){
                            boxedArgs[i] = Character.valueOf((char)arg);
                        } else {
                            assert argType.equals(Integer.TYPE);
                            boxedArgs[i] = Integer.valueOf(arg);
                        }
                    }
                    method.invoke(null, boxedArgs);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2, arg3, arg4);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg first argument to the target method
     */
    public void send(Place place, long arg) {
        // Because there is a send(I)V, we can't get here via a widening conversion.
        assert method.getParameterTypes()[0].equals(Long.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, long arg1, long arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Long.TYPE) && method.getParameterTypes()[1].equals(Long.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, int arg1, long arg2) {
        // We've covered all the possibilities for arg2 explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Long.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Arg1 may have had a widening conversion applied which we need to undo before calling invoke.
                Class<?> argType = method.getParameterTypes()[0];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, (byte)arg1, arg2);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, (short)arg1, arg2);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, (char)arg1, arg2);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, long arg1, int arg2) {
        // We've covered all the possibilities for arg1 explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Long.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Arg2 may have had a widening conversion applied which we need to undo before calling invoke.
                Class<?> argType = method.getParameterTypes()[1];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, arg1, (byte)arg2);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, arg1, (short)arg2);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, arg1, (char)arg2);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg first argument to the target method
     */
    public void send(Place place, float arg) {
        // Because there is a 1 argument send defined for both int and long,
        // it should be impossible to get here via a widening conversion.
        assert method.getParameterTypes()[0].equals(Float.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, float arg1, float arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Float.TYPE) && method.getParameterTypes()[1].equals(Float.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, int arg1, float arg2) {
        // We've covered all the possibilities for arg2 explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[1].equals(Float.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Arg1 may have had a widening conversion applied which we need to undo before calling invoke.
                Class<?> argType = method.getParameterTypes()[0];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, (byte)arg1, arg2);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, (short)arg1, arg2);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, (char)arg1, arg2);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, long arg1, float arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Long.TYPE) && method.getParameterTypes()[1].equals(Float.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, float arg1, int arg2) {
        // We've covered all the possibilities for arg1 explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Float.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Arg2 may have had a widening conversion applied which we need to undo before calling invoke.
                Class<?> argType = method.getParameterTypes()[1];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, arg1, (byte)arg2);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, arg1, (short)arg2);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, arg1, (char)arg2);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, float arg1, long arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Float.TYPE) && method.getParameterTypes()[1].equals(Long.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg first argument to the target method
     */
    public void send(Place place, double arg) {
        // Because there is a 1 argument send defined for int, float, and long
        // it should be impossible to get here via a widening conversion.
        assert method.getParameterTypes()[0].equals(Double.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, double arg1, double arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Double.TYPE) && method.getParameterTypes()[1].equals(Double.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, int arg1, double arg2) {
        // We've covered all the possibilities for arg2 explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[1].equals(Double.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Arg1 may have had a widening conversion applied which we need to undo before calling invoke.
                Class<?> argType = method.getParameterTypes()[0];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, (byte)arg1, arg2);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, (short)arg1, arg2);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, (char)arg1, arg2);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, float arg1, double arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Float.TYPE) && method.getParameterTypes()[1].equals(Double.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, long arg1, double arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Long.TYPE) && method.getParameterTypes()[1].equals(Double.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, double arg1, int arg2) {
        // We've covered all the possibilities for arg1 explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Double.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                // Arg2 may have had a widening conversion applied which we need to undo before calling invoke.
                Class<?> argType = method.getParameterTypes()[1];
                if (argType.equals(Integer.TYPE)) {
                    method.invoke(null, arg1, arg2);
                } else if (argType.equals(Byte.TYPE)) {
                    method.invoke(null, arg1, (byte)arg2);
                } else if (argType.equals(Short.TYPE)) {
                    method.invoke(null, arg1, (short)arg2);
                } else {
                    assert argType.equals(Character.TYPE);
                    method.invoke(null, arg1, (char)arg2);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, double arg1, float arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Double.TYPE) && method.getParameterTypes()[1].equals(Float.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param arg1 first argument to the target method
     * @param arg2 second argument to the target method
     */
    public void send(Place place, double arg1, long arg2) {
        // We've covered all the combinations explicitly, so we can't get here via widening conversion.
        assert method.getParameterTypes()[0].equals(Double.TYPE) && method.getParameterTypes()[1].equals(Long.TYPE);

        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg1, arg2);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendRemote(place.getId(), messageId, arg1, arg2);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, boolean[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, byte[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, short[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, char[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }
    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */

    public void send(Place place, int[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, float[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, long[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, double[] arg) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, arg.length, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param arg first argument to the target method
     * @param place the Place to which the message should be sent.
     */
    public void send(Place place, int start, int end, int[] arg) {
        if (start > end) throw new IllegalArgumentException("start must greater than end"); 
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                method.invoke(null, arg);
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send: pass down to x10rt
            sendArrayRemote(place.getId(), messageId, start, end, arg);
        }
    }

    /**
     * Send the message represented by <code>this</code> to <code>Place</code>.
     * If <code>place</code> is equal to {@link X10RT#here()}, then the
     * send is implemented as a direct invocation of the target method and will
     * not return until the method invocation completes.
     * If <code>place</code> is not equal to {@link X10RT#here()}, then the
     * send is implemented by sending an active message to the remote place,
     * transmitting the {@link #messageId} and any arguments as data payload. In the remote
     * case, the invocation of send will return as soon as the local network send has
     * completed.  IE, in the remote case, the completion of send does not imply that the
     * method has completed (or even been invoked) on the remote place.
     *
     * @param place the Place to which the message should be sent.
     * @param args the arguments to the target method
     */
    public void send(Place place, Object...args) {
        if (X10RT.here() == place) {
            // local send; simply invoked via reflection.
            try {
                if ((method.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                    method.invoke(null, args);
                } else {
                    Object[] params = new Object[args.length-1];
                    System.arraycopy(args, 1, params, 0, args.length-1);
                    method.invoke(args[0], params);
                }
            } catch (Exception e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        } else {
            // remote send that doesn't have a specialized handler.
            // serialize parameters into a byte array, and then pass the
            // raw bytes to x10rt to send to remote general handler.
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
                for (Object arg : args) {
                    Class<?> argType = arg.getClass();
                    if (argType.isArray()) {
                        Class<?> elemType = argType.getComponentType();
                        if (elemType.equals(Boolean.TYPE)) {
                            boolean[] array = (boolean[])arg;
                            objStream.writeInt(array.length);
                            for (boolean elem : array) {
                                objStream.writeBoolean(elem);
                            }
                        } else if (elemType.equals(Byte.TYPE)) {
                            byte[] array = (byte[])arg;
                            objStream.writeInt(array.length);
                            for (byte elem : array) {
                                objStream.writeByte(elem);
                            }
                        } else if (elemType.equals(Short.TYPE)) {
                            short[] array = (short[])arg;
                            objStream.writeInt(array.length);
                            for (short elem : array) {
                                objStream.writeShort(elem);
                            }
                        } else if (elemType.equals(Character.TYPE)) {
                            char[] array = (char[])arg;
                            objStream.writeInt(array.length);
                            for (char elem : array) {
                                objStream.writeChar(elem);
                            }
                        } else if (elemType.equals(Integer.TYPE)) {
                            int[] array = (int[])arg;
                            objStream.writeInt(array.length);
                            for (int elem : array) {
                                objStream.writeInt(elem);
                            }
                        } else if (elemType.equals(Float.TYPE)) {
                            float [] array = (float[])arg;
                            objStream.writeInt(array.length);
                            for (float elem : array) {
                                objStream.writeFloat(elem);
                            }
                        } else if (elemType.equals(Long.TYPE)) {
                            long [] array = (long [])arg;
                            objStream.writeInt(array.length);
                            for (long elem : array) {
                                objStream.writeLong(elem);
                            }
                        } else if (elemType.equals(Double.TYPE)) {
                            double [] array = (double[])arg;
                            objStream.writeInt(array.length);
                            for (double elem : array) {
                                objStream.writeDouble(elem);
                            }
                        } else {
                            objStream.writeObject(arg);
                        }
                    } else {
                        if (argType.equals(Boolean.class)) {
                            objStream.writeBoolean(((Boolean)arg).booleanValue());
                        } else if (argType.equals(Byte.class)) {
                            objStream.writeByte(((Byte)arg).byteValue());
                        } else if (argType.equals(Short.class)) {
                            objStream.writeShort(((Short)arg).shortValue());
                        } else if (argType.equals(Character.class)) {
                            objStream.writeChar(((Character)arg).charValue());
                        } else if (argType.equals(Integer.class)) {
                            objStream.writeInt(((Integer)arg).intValue());
                        } else if (argType.equals(Float.class)) {
                            objStream.writeFloat(((Float)arg).floatValue());
                        } else if (argType.equals(Long.class)) {
                            objStream.writeLong(((Long)arg).longValue());
                        } else if (argType.equals(Double.class)) {
                            objStream.writeDouble(((Double)arg).doubleValue());
                        } else {
                            objStream.writeObject(arg);
                        }
                    }
                }

                objStream.close();
                byte[] rawBytes = byteStream.toByteArray();
                sendGeneralRemote(place.getId(), messageId, rawBytes.length, rawBytes);
            } catch (IOException e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unused") /* invoked from native code */
    private static void receiveGeneral(int messageId, byte[] args) 
    throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(args);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        ActiveMessage message = messages[messageId];
        Class<?>[] parameterTypes;
        Object[] wrappedArgs;
        if (Modifier.isStatic(message.method.getModifiers())) {
            parameterTypes = message.method.getParameterTypes();
            wrappedArgs = new Object[parameterTypes.length];
        } else {
            parameterTypes = new Class<?>[message.method.getParameterTypes().length+1];
            parameterTypes[0] = message.method.getDeclaringClass();
            int idx = 1;
            for (Class<?> p : message.method.getParameterTypes()) {
                parameterTypes[idx++] = p;
            }
            wrappedArgs = new Object[parameterTypes.length+1];
        }
        
        for (int i=0; i<parameterTypes.length; i++) {
            try {
                Class<?> type = parameterTypes[i];
                if (type.isArray()) {
                    Class<?> elemType = type.getComponentType();
                    if (elemType.equals(Boolean.TYPE)) {
                    	int size = objStream.readInt();
                        boolean [] tmp = new boolean [size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readBoolean();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Byte.TYPE)) {
                    	int size = objStream.readInt();
                        byte [] tmp = new byte[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readByte();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Short.TYPE)) {
                    	int size = objStream.readInt();
                        short[] tmp = new short[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readShort();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Character.TYPE)) {
                    	int size = objStream.readInt();
                        char[] tmp = new char[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readChar();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Integer.TYPE)) {
                    	int size = objStream.readInt();
                        int[] tmp = new int[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readInt();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Float.TYPE)) {
                    	int size = objStream.readInt();
                        float[] tmp = new float[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readFloat();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Long.TYPE)) {
                    	int size = objStream.readInt();
                        long [] tmp = new long[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readLong();
                        }
                        wrappedArgs[i] = tmp;
                    } else if (elemType.equals(Double.TYPE)) {
                    	int size = objStream.readInt();
                        double[] tmp = new double[size];
                        for (int j=0; j<size; j++) {
                            tmp[j] = objStream.readDouble();
                        }
                        wrappedArgs[i] = tmp;
                    } else {
                        try {
                            wrappedArgs[i] = objStream.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //assert false : "implement me " + type;
                    }
                } else {
                    if (type.equals(Boolean.TYPE)) {
                        wrappedArgs[i] = Boolean.valueOf(objStream.readBoolean());
                    } else if (type.equals(Byte.TYPE)) {
                        wrappedArgs[i] = Byte.valueOf(objStream.readByte());
                    } else if (type.equals(Short.TYPE)) {
                        wrappedArgs[i] = Short.valueOf(objStream.readShort());
                    } else if (type.equals(Character.TYPE)) {
                        wrappedArgs[i] = Character.valueOf(objStream.readChar());
                    } else if (type.equals(Integer.TYPE)) {
                        wrappedArgs[i] = Integer.valueOf(objStream.readInt());
                    } else if (type.equals(Float.TYPE)) {
                        wrappedArgs[i] = Float.valueOf(objStream.readFloat());
                    } else if (type.equals(Long.TYPE)) {
                        wrappedArgs[i] = Long.valueOf(objStream.readLong());
                    } else if (type.equals(Double.TYPE)) {
                        wrappedArgs[i] = Double.valueOf(objStream.readDouble());
                    } else {
                        //assert false : "implement me " + type;
                        try {
                            wrappedArgs[i] = objStream.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                    e.printStackTrace();
                }
            }
        }
        objStream.close();

        try {
            if ((message.method.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                message.method.invoke(null, wrappedArgs);
            } else {
                Object[] params = new Object[wrappedArgs.length-1];
                System.arraycopy(wrappedArgs, 1, params, 0, wrappedArgs.length-1);
                message.method.invoke(wrappedArgs[0], params);                  
            }
        } catch (Exception e) {
            if (X10RT.REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                e.printStackTrace();
            }
        }
    }

    static native void initializeMessageHandlers();

    private static synchronized native void registerMethodImpl(Method method, Class<?> targetClass, int messageId);

    private static native void sendRemote(int place, int messageId);

    private static native void sendRemote(int place, int messageId, int arg);
    private static native void sendRemote(int place, int messageId, int arg1, int arg2);
    private static native void sendRemote(int place, int messageId, int arg1, int arg2, int arg3);
    private static native void sendRemote(int place, int messageId, int arg1, int arg2, int arg3, int arg4);

    /*
     * Have to consider combinations to avoid widening operations being applied in ways that
     * are hard to handle efficiently in native code.
     */
    private static native void sendRemote(int place, int messageId, long arg);
    private static native void sendRemote(int place, int messageId, long arg1, long arg2);
    private static native void sendRemote(int place, int messageId, int arg1, long arg2);
    private static native void sendRemote(int place, int messageId, long arg1, int arg2);

    /*
     * Have to consider combinations to avoid widening operations being applied in ways that
     * are hard to handle efficiently in native code.
     */
    private static native void sendRemote(int place, int messageId, float arg);
    private static native void sendRemote(int place, int messageId, float arg1, float arg2);
    private static native void sendRemote(int place, int messageId, int arg1, float arg2);
    private static native void sendRemote(int place, int messageId, long arg1, float arg2);
    private static native void sendRemote(int place, int messageId, float arg1, int arg2);
    private static native void sendRemote(int place, int messageId, float arg1, long arg2);

    /*
     * Have to consider combinations to avoid widening operations being applied in ways that
     * are hard to handle efficiently in native code.
     */
    private static native void sendRemote(int place, int messageId, double arg);
    private static native void sendRemote(int place, int messageId, double arg1, double arg2);
    private static native void sendRemote(int place, int messageId, int arg1, double arg2);
    private static native void sendRemote(int place, int messageId, float arg1, double arg2);
    private static native void sendRemote(int place, int messageId, long arg1, double arg2);
    private static native void sendRemote(int place, int messageId, double arg1, int arg2);
    private static native void sendRemote(int place, int messageId, double arg1, long arg2);
    private static native void sendRemote(int place, int messageId, double arg1, float arg2);

    private static native void sendArrayRemote(int place, int messageId, int arraylen, boolean[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, byte[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, short[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, char[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, int[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, float[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, long[] array);
    private static native void sendArrayRemote(int place, int messageId, int arraylen, double[] array);

    private static native void sendArrayRemote(int place, int messageId, int start, int end, int[] array);

    private static native void sendGeneralRemote(int place, int messageId, int arraylen, byte[] rawBytes);
}
