/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.parser.antlr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.EmptyPredictionContext;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import x10.parser.antlr.generated.X10Parser;

/**
 * This class allows to save and restore internal data structures of the ANTLR
 * parser.
 *
 * @author Louis Mandel
 *
 */
public class ParserSerializer {
	private static class State {
		public ATN atn;
		public DFA[] decisionToDFA;
		public PredictionContextCache sharedContextCache;
		public DFAState ATNSimulator_ERROR;
		public ATNConfigSet.ConfigEqualityComparator ConfigEqualityComparator_INSTANCE;
		public EmptyPredictionContext PredictionContext_EMPTY;
		public int PredictionContext_globalNodeCount;
		public State() {
		}
		public State(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache,
				DFAState ATNSimulator_ERROR,
				ATNConfigSet.ConfigEqualityComparator ConfigEqualityComparator_INSTANCE,
				EmptyPredictionContext PredictionContext_EMPTY, int PredictionContext_globalNodeCount) {
			this.atn = atn;
			this.decisionToDFA = decisionToDFA;
			this.sharedContextCache = sharedContextCache;
			this.ATNSimulator_ERROR = ATNSimulator_ERROR;
			this.ConfigEqualityComparator_INSTANCE = ConfigEqualityComparator_INSTANCE;
			this.PredictionContext_EMPTY = PredictionContext_EMPTY;
			this.PredictionContext_globalNodeCount = PredictionContext_globalNodeCount;
		}
	}

	private static Field getModifiableField(Class<?> class_, String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = class_.getDeclaredField(fieldName);
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		return field;
	}

	private static Object getStatic(Class<?> class_, String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = class_.getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(null);
	}

	public static void write(OutputStream outputStream)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Kryo kryo = new Kryo();
		Output output = new Output(outputStream);
		State state = new State((ATN) getStatic(X10Parser.class, "_ATN"), // optional
				(DFA[]) getStatic(X10Parser.class, "_decisionToDFA"),
				(PredictionContextCache) getStatic(X10Parser.class, "_sharedContextCache"), // optional
				(DFAState) getStatic(ATNSimulator.class, "ERROR"), // optional
				(ATNConfigSet.ConfigEqualityComparator) getStatic(ATNConfigSet.ConfigEqualityComparator.class, "INSTANCE"), // optional
				(EmptyPredictionContext) getStatic(PredictionContext.class, "EMPTY"),
				(Integer) getStatic(PredictionContext.class, "globalNodeCount"));
		kryo.writeClassAndObject(output, state);
		output.close();
	}
	
	public static void read(InputStream inputStream)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Kryo kryo = new Kryo();
		((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
		Input input = new Input(inputStream);
		State state = (State) kryo.readClassAndObject(input);
		getModifiableField(X10Parser.class, "_ATN").set(null, state.atn); // optional
		getModifiableField(X10Parser.class, "_decisionToDFA").set(null, state.decisionToDFA);
		getModifiableField(X10Parser.class, "_sharedContextCache").set(null, state.sharedContextCache); // optional
		getModifiableField(ATNSimulator.class, "ERROR").set(null, state.ATNSimulator_ERROR);
		getModifiableField(ATNConfigSet.ConfigEqualityComparator.class, "INSTANCE").set(null, state.ConfigEqualityComparator_INSTANCE); // optional
		getModifiableField(PredictionContext.class, "EMPTY").set(null, state.PredictionContext_EMPTY);
		getModifiableField(PredictionContext.class, "globalNodeCount").setInt(null, state.PredictionContext_globalNodeCount);
		input.close();
	}

}
