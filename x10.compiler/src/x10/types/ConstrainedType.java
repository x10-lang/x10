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

package x10.types;

import polyglot.types.ObjectType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.XConstrainedTerm;

import java.util.Collections;
import java.util.List;

import polyglot.types.Context;
import polyglot.types.JavaArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LazyRef_c;

import polyglot.types.FieldDef;
import polyglot.types.Name;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.JavaPrimitiveType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType_c;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

import x10.types.matcher.Subst;


/**
 * 09/11/09
 * A ConstrainedType_c represents the type T{c}. It has a basetype (of type Ref<? extends Type>)
 * and a constraint (of type Ref<CConstraint>).
 * 
 * @author njnystrom
 * @author vj
 *
 */
public class ConstrainedType extends ReferenceType_c implements ObjectType, X10ThisVar {

		private static final long serialVersionUID = -3797674072640450629L;

		private Ref<CConstraint> constraint; // yoav todo: what about type constraints? We should keep the original expression as well so we will have accurate position info. 
		private Ref<? extends Type> baseType;

		protected CConstraint realXClause;
		protected SemanticException realClauseInvalid;

		public ConstrainedType(TypeSystem ts, Position pos, Position errorPos,
				Ref<? extends Type> baseType, Ref<CConstraint> constraint) {
			super(ts, pos, errorPos);
			assert ts != null;
			this.baseType = baseType;
			// todo: we currently use UnknownType as an InvalidType
			//if ((baseType.known() && baseType.getCached() instanceof UnknownType))
			//	throw new InternalCompilerError("Base type must be known.");
				
			
			//assert  :
			//	" baseType is " + baseType;
			this.constraint = constraint;
		}

		public boolean isGloballyAccessible() {
		    return false;
		}
		
		@Override 
		public ConstrainedType copy() {
			ConstrainedType result = (ConstrainedType) super.copy();
			result.constraint = Types.ref(constraint.get().copy());
			result.baseType = (Ref<? extends Type>) Types.ref((Type) baseType.get().copy());
            result.realXClause = realXClause==null ? null : realXClause.copy();
            // no need to copy realClauseInvalid because it is used to cache if a realXClause is inconsistent.
			return result;
		}
		/**
		 * Check that the basetype and constraint agree on thisVar.
		 */
		public XVar thisVar() {
			if (realXClause == null)
				realXClause = realX();
			if (! realXClause.consistent())
				return null;
			return realXClause.thisVar();
		}
		
		public Ref<? extends Type> baseType() {
			return baseType;
		}
		
		/*public X10Type clearFlags(Flags f) {
			ConstrainedType_c c = (ConstrainedType_c) this.copy();
			X10Type t = (X10Type) Types.get(c.baseType);
			if (t==null)
				throw new InternalCompilerError("Cannot remove flags " + f + " from null type.");
			t = t.clearFlags(f);
			c.baseType = Types.ref(t);
		//	((Ref<Type>)c.baseType).update(t);
			return c;
		}
		*/

		
		public Type makeX10Struct() {
			Type t = Types.get(baseType);
			assert t!=null;
			if (Types.isX10Struct(t))
				return this;
			ConstrainedType c = (ConstrainedType) this.copy();
			c.baseType = Types.ref(Types.makeX10Struct(t));
			return c;

		}

		/*
		public Flags flags() {
			X10Type t = (X10Type) Types.get(this.baseType);
			assert t != null : "Cannot get flags on null type.";
			if (t==null)
				throw new InternalCompilerError("Cannot get flags on null type.");
			return t.flags();
		}
		*/
		public ConstrainedType baseType(Ref<? extends Type> baseType) {
			if (baseType == this.baseType) return this;
			ConstrainedType n = (ConstrainedType) copy();
			n.baseType = baseType;
			return n;
		}
		
		public Ref<CConstraint> constraint() {
			return constraint;
		}
		
		public ConstrainedType constraint(Ref<CConstraint> constraint) {
			if (constraint == this.constraint) return this;
			ConstrainedType n = (ConstrainedType) copy();
			n.constraint = constraint;
			return n;
		}

		/**
		Returns the real clause for this constrained type. The self variable for the returned constraint 
		is the same as the self variable for the depclause.
		 */
		public CConstraint getRealXClause() { 
			if (realXClause == null) {
				realXClause = realX();
			}
			return realXClause; 
		}
		/*public void setRealXClause(CConstraint c, SemanticException error) {
			this.realXClause = c;
			this.realClauseInvalid = error;
		}*/
		
		protected CConstraint realX() {
			// Now get the root clause and join it with the dep clause.
			CConstraint rootClause = Types.realX(Types.get(this.baseType()));
			if (!rootClause.consistent())
			    return rootClause;
			CConstraint depClause = Types.xclause(this);
			if (depClause==null) {
			    return rootClause;
			}
			
			try {
			XVar thisVar = Types.getThisVar(rootClause, depClause);
			} catch (XFailure z) {
			    rootClause.setInconsistent();
			    return rootClause;
			}
			// Need to ensure that returned clause has same self var as Types.xclause(this).
			//if (depClause.valid())
			//    return rootClause;
			depClause.addIn(rootClause);
			return depClause;
		}
		
		public void checkRealClause() throws SemanticException {
			// Force real clause to be computed.
			if (realXClause == null)
				realXClause = realX();
			if (! realXClause.consistent()) {
				if (realClauseInvalid != null) {
					realClauseInvalid = new SemanticException(this 
							+ " has an inconsistent real clause " + realXClause);

				}
				throw realClauseInvalid;
			}
		}
		
		@Override
		public String translate(Resolver c) {
			return baseType().get().translate(c);
		}
		

		/*public boolean isSafe() {
			return ((X10Type) baseType.get()).isSafe();
		}*/

		@Override
		public String typeToString() {
	        Type type = baseType.getCached();
	        String typeName = type.toString();
	        String cString = constraintString();
	        if (type instanceof FunctionType_c && cString.length() > 0)
	            typeName = "("+typeName+")";
	        return typeName + cString;
		}
		
		private String constraintString() {
			StringBuilder sb = new StringBuilder();
			Type base = baseType.getCached();
			CConstraint c = constraint.getCached();
			if (c != null && ! c.valid() && (!c.consistent() || 
					!(c.extConstraintsHideFake().size() == 0))) {
				sb.append(c);
			}
			return sb.toString();
		}

		// vj 08/11/09
		// todo: For each FieldInstance fi of baseType, need to return a new FieldInstance fi' obtained
		// by adding this: this.constraint.
		@Override
		public List<FieldInstance> fields() {
			Type base = baseType.get();
			if (base instanceof ContainerType) {
				final List<FieldInstance> fis = ((ContainerType) base).fields();
				return fis;
			}
			return Collections.emptyList();
		}
		
	/*	public void ensureSelfBound() {
			assert constraint != null;
			XVar self = X10TypeMixin.selfVarBinding(this);
			if (self == null) {
				self = XTerms.makeUQV();
				CConstraint c = constraint.get();
				try {
				c.addSelfBinding(self);
				} catch (XFailure z) {
					System.out.println("failure " + z);
				}
				constraint.update(c);
			}
		}
	*/
		// vj: Revised substantially 08/11/09
		
		@Override
		public List<Type> interfaces() {
			final Type base = baseType.get();
			if (! (base instanceof ObjectType))
				return Collections.emptyList();

			List<Type> l = ((ObjectType) base).interfaces();
			CConstraint c = constraint.get();
			// Get or make a name tt for self.
			XTerm t = c.bindingForVar(c.self());
			if (t == null) {
				t = ConstraintManager.getConstraintSystem().makeEQV();

			}
			final XTerm tt = t;

			return new TransformingList<Type, Type>(l, new Transformation<Type, Type>() {
				public Type transform(Type o) {
					CConstraint c2 = Types.xclause(o);
					c2 = c2 != null ? c2.copy() : ConstraintManager.getConstraintSystem().makeCConstraint();
					if (c2.thisVar() != null)
					    c2.addBinding(c2.thisVar(), tt);
					//c2.addSelfBinding(tt);
					//  c2.substitute(tt, XTerms.)
					// vj: 1/27/11 -- change from past behavior
					// if c2 is inconsistent, we dont return o.
					return Types.xclause(o, c2);

				}
			});
		}


		@Override
		public List<MethodInstance> methods() {
			Type base = baseType.get();
			if (base instanceof ContainerType) {
				return ((ContainerType) base).methods();
			}
			return Collections.emptyList();
		}

		@Override
		public Type superClass() {
			Type base = baseType.get();
			if (base instanceof ObjectType) {
			    Type o = ((ObjectType) base).superClass();
			    if (o != null) {
			    CConstraint c = constraint.get();
			    final XTerm t = c.bindingForVar(c.self());
			    if (t != null) {
			        CConstraint c2 = Types.xclause(o);
			        c2 = c2 != null ? c2.copy() : ConstraintManager.getConstraintSystem().makeCConstraint();
			        TypeSystem xts = (TypeSystem) o.typeSystem();
			        c2.addSelfBinding(t);
			        return Types.xclause(o, c2);
			    }
			    }
			    return o;
			}
			return null;
		}

		public QName fullName() {
			Type base = baseType.get();
			return base.fullName();
		}

		public Name name() {
			Type base = baseType.get();
			return base.name();
		}
		
		@Override
		public boolean isJavaPrimitive() {
			Type base = baseType.get();
			return base.isJavaPrimitive();
		}
		
		
		public boolean isX10Struct() {
			return Types.isX10Struct(baseType.get());
		}
		

		@Override
		public boolean isClass() {
			Type base = baseType.get();
			return base.isClass();
		}
		@Override
		public boolean isNull() {
			Type base = baseType.get();
			return base.isNull();
		}
		@Override
		public boolean isArray() {
			Type base = baseType.get();
			return base.isArray();
		}
		@Override
		public boolean isReference() {
			Type base = baseType.get();
			return base.isReference();
		}
		
		@Override
		public JavaPrimitiveType toPrimitive() {
			Type base = baseType.get();
			return base.toPrimitive();
		}
		
		@Override
		public X10ClassType toClass() {
			Type base = baseType.get();
			return base.toClass();
		}
		
		@Override
		public NullType toNull() {
			Type base = baseType.get();
			return base.toNull();
		}

		@Override
		public JavaArrayType toArray() {
			Type base = baseType.get();
			return base.toArray();
		}
		
		public void print(CodeWriter w) {
			Type base = baseType.get();
			base.print(w);
		}
		
	    public void printConstraint(CodeWriter w) {
	        Type base = baseType.getCached();
	        CConstraint c = constraint.getCached();
	    }
		
		public boolean equalsNoFlag(Type t2) {
			return false;
		}
		// Methods below this have been moved from polyglot.type.Types
		/**
		 * Add the constraint self.rank==x to this unless that causes an inconsistency.
		 * @param x
		 * @return
		 */
		public ConstrainedType addRank(long x) {
		    return addRank(ConstraintManager.getConstraintSystem().makeLit(x, ts.Long()));
		}
		public ConstrainedType addSize(int x) {
		    return addIntProperty(x, Name.make("size"));
		}
		public ConstrainedType addIntProperty(int x, Name name) {
		    return addProperty(ConstraintManager.getConstraintSystem().makeLit(x, ts.Int()), name);
		}

		/**
		 * Add the constraint self.rank==x to this unless that causes an inconsistency.
		 * @param x
		 * @return
		 */
		public ConstrainedType addRank(XTerm x) {
            return addProperty(x, Name.make("rank"));
		}
		public ConstrainedType addProperty(XTerm x, Name name) {
		    XTerm xt = findOrSynthesize(name);
		    if (xt == null) {
		    	throw new InternalCompilerError("*******(" + this + ") Could not find property " + name);
		    }
		    return addBinding(xt, x);
		}
		
		public XTerm findOrSynthesize(Name n) { // todo: why do we have both findOrSynthesize and find if they do the same thing?
		    return find(n);
		}
		
		/**
		 * Find the term t, if any, such that t entails {self.propName==t}.
		 */
		public XTerm findProperty(Name name) {
			CConstraint c = Types.realX(this);
			if (c == null || ! c.consistent()) 
			    return null;
			
			FieldInstance fi = Types.getProperty(this, name);
			// TODO: check dist.region.p and region.p
			if (fi != null)
				return c.bindingForSelfField(fi.def());
		
			MethodInstance mi = Types.getPropertyMethod(this, name);
			if (mi != null) {
			    return c.bindingForSelfField(mi.def());
			}
			
			return null;
		}
		
		/**
		 * Find a property in self with a given name. The term returned may contain self as receiver.
		 * @param name
		 * @return
		 */
		public XTerm find(Name name) {
		    XTerm val = findProperty(name);
		
		    if (val == null) {
		        TypeSystem xts = typeSystem();
		        CConstraint c = Types.realX(this);
		     
		        if (c != null) {
		            // build the synthetic term.
		            FieldInstance fi = Types.getProperty(this, name);
		            XTerm var = selfVar();
		            if (var !=null) {
                        XTypeTranslator translator = xts.xtypeTranslator();
                        if (fi != null) {
		                    val = translator.translate(var, fi);
		                } else {
		                    MethodInstance mi = Types.getPropertyMethod(this, name);
		                    if (mi != null) {
		                        val = translator.translate(var, mi);
		                    }
		                }
		            }
		        }
		    }
            if (val!=null) {                   
                // expand it in order to handle Dist.rank()
                val = XTypeTranslator.expandPropertyMethod(val,false,null,null);
            }
		    return val; // todo: val can be null! if we build a synthetic term, then why not always build it???
		}
		
		public XVar selfVar() {
			return Types.get(constraint()).self();
		}
		
		public ConstrainedType addBinding(XTerm t1, XTerm t2) {
		    CConstraint c = Types.xclause(this);
		    c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
		    c.addBinding(t1, t2);
		    return (ConstrainedType) Types.xclause(Types.baseType(this), c);
		}

		public ConstrainedType addBinding(XTerm t1, XConstrainedTerm t2) {
		    CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
		    c.addBinding(t1, t2);
		    return (ConstrainedType) Types.xclause(this, c);
		}
		public ConstrainedType addSelfBinding(XTerm t1) {
			CConstraint c = Types.xclause(this);
			c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
			c.addSelfBinding(t1);
			return (ConstrainedType) Types.xclause(Types.baseType(this), c); 
		}
		public ConstrainedType addSelfDisBinding(XTerm t1) {
			CConstraint c = Types.xclause(this);
			c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
			c.addSelfDisBinding(t1);
			return (ConstrainedType) Types.xclause(Types.baseType(this), c); 
		}

		/**
		 * Add t1 != t2 to t. Note: The type returned may have an inconsistent
		 * constraint.
		 * @param t
		 * @param t1
		 * @param t2
		 * @return

		public ConstrainedType addDisBinding(Type t, XTerm t1, XTerm t2) {
		    assert (! (t instanceof UnknownType));
		    CConstraint c = Types.xclause(t);
		    c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
		    c.addDisBinding(t1, t2);
		    return (ConstrainedType) Types.xclause(Types.baseType(t), c);
		}
*/
		// vj: 08/11/09 -- have to recursively walk the 
		// type parameters and add the constraint to them.
		public static ConstrainedType xclause(final Ref<? extends Type> t, final Ref<CConstraint> c) {
		    if (t == null) {
		        return null;
		    }
		
		    if (t.known() && c != null && c.known()) {
		        Type tx = Types.get(t);
		        assert tx != null;
		        TypeSystem ts = (TypeSystem) tx.typeSystem();
		        tx = ts.expandMacros(tx);
		
		        CConstraint oldc = Types.xclause(tx);
		        CConstraint newc = Types.get(c);
		
		        if (newc == null)
		            return new ConstrainedType(ts, tx.position(), tx.position(), Types.ref(tx), Types.ref(ConstraintManager.getConstraintSystem().makeCConstraint()));
		        
		        if (oldc == null) {
		            return new ConstrainedType(ts, tx.position(), tx.position(), t, c);
		        }
		        else {
		            newc = newc.copy();
		            newc.addIn(oldc); //  may become inconsistent
		            return new ConstrainedType(ts, tx.position(), tx.position(),
		                                       Types.ref(Types.baseType(tx)), 
		                                       Types.ref(newc));
		        }
		    }
		    
		    final LazyRef_c<Type> tref = new LazyRef_c<Type>(null);
		    tref.setResolver(new Runnable() {
		        public void run() {
		            Type oldt = Types.baseType(Types.get(t));
		            tref.update(oldt);
		        }
		    });
		    
		    final LazyRef_c<CConstraint> cref = new LazyRef_c<CConstraint>(null);
		    cref.setResolver(new Runnable() { 
		        public void run() {
		            CConstraint oldc = Types.xclause(Types.get(t));
		            if (oldc != null) {
		                CConstraint newc = Types.get(c);
		                if (newc != null) {
		                    newc = newc.copy();
		                    newc.addIn(oldc); // newc may have become inconsistent
		                    cref.update(newc);
		                }
		                else {
		                    cref.update(oldc);
		                }
		            }
		            else {
		                cref.update(oldc);
		            }		                
		        }
		    });
		
		    Type tx = t.getCached();
		    assert tx != null;
		    return new ConstrainedType((TypeSystem) tx.typeSystem(), tx.position(), tx.position(), t.known()? t: tref, cref);
		}
		
		/**
		 * Returns a copy of this's constraint, if it has one, null otherwise.
		 * @return
		 */
		public  CConstraint xclause() {
			return Types.get(constraint()).copy();
		}
		public static ConstrainedType xclause(Type t, CConstraint c) {
			if (t == null)
				return null;
			if (c == null /*|| c.valid()*/) {
				c = ConstraintManager.getConstraintSystem().makeCConstraint();
			}
			return xclause(Types.ref(t), Types.ref(c));
		}
		
		public  ConstrainedType addRect() {
		    ConstrainedType result=this;
		    XTerm xt = findOrSynthesize(Name.make("rect"));
		    if (xt != null)
		        result = addBinding(xt, ConstraintManager.getConstraintSystem().xtrue());
		    return result;
		}
		
		/**
		 * Add the constraint self.zeroBased==true;
		 * @return
		 */
		public ConstrainedType addZeroBased() {
		    ConstrainedType result = this;
		    XTerm xt = findOrSynthesize(Name.make("zeroBased"));
		    if (xt != null)
		        result =  addBinding(xt, ConstraintManager.getConstraintSystem().xtrue());
		    return result;
		}
		public ConstrainedType addNonNull() {
		    return addSelfDisBinding(ConstraintManager.getConstraintSystem().xnull());
		}
		
		/**
		 * Return the term self.rank, where self is the selfvar for this.
		 */
		public XTerm rank(Context context) {
            return findOrSynthesize(Name.make("rank"));
        }
        /**
         * Return the term self.size, where self is the selfvar for this.
         */
		public XTerm size(Context context) {
            return findOrSynthesize(Name.make("size"));
        }
		

		/**
		 * Does this type entail self.rect? Use sigma
		 * from the context if necessary.
		 * @param context
		 * @return
		 */
		public boolean isRect(Context context) {
		    return amIProperty(Name.make("rect"), context);
		}
		
		/**
         * Does this type entail self.zeroBased? Use sigma
         * from the context if necessary.
         * @param context
         * @return
         */
		public boolean isZeroBased(Context context) {
			return amIProperty(Name.make("zeroBased"), context);
		}
		
		/**
		 * Does this type entail self.rank==1? Use sigma
         * from the context if necessary.
		 */
		public boolean isRankOne(Context context) {
		    return isRank(typeSystem().ONE(), context);
		}

		/**
         * Does this type entail self.rank==2? Use sigma
         * from the context if necessary.
         */
		public boolean isRegionRankTwo(Context context) {
		    return isRank(typeSystem().TWO(), context);
		}

		/**
         * Does this type entail self.rank==3? Use sigma
         * from the context if necessary.
         */
		public boolean isRegionRankThee(Context context) {
		    return isRank(typeSystem().THREE(), context);
        }
		/**
         * Does this type entail self.rank==lit? Use sigma
         * from the context if necessary.
         */
        public boolean isRank(XLit lit, Context context) {
            return hasPropertyValue(Name.make("rank"), lit, context);
        }
        
        /**
         * Does this type entail self.propName? Use sigma
         * from the context if necessary.
         */
		public boolean amIProperty(Name propName, Context context) {
		    return hasPropertyValue(propName, XTypeTranslator.translate(true, ts), context);
		}
		
		/**
		 * Does this type entail self.propName == lit? Use sigma
		 * from the context if necessary.
		 */

		public  boolean hasPropertyValue(Name propName, XLit lit, final Context context) {
		    TypeSystem xts = typeSystem();
		    final CConstraint r = realX();
		    if (! r.consistent())
		        return false;

		    // first try self.p
		    X10FieldInstance fi = Types.getProperty(this, propName);
		    if (fi != null) {

		        final CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
		        XTerm term = xts.xtypeTranslator().translate(c.self(), fi);
		        c.addBinding(term, lit);
		        if (! c.consistent())
		            return false;
		        return r.entails(c, new ConstraintMaker() { 
		            public CConstraint make() throws XFailure {
		                return context.constraintProjection(r, c);
		            }
		        });
		    }
		    else {
		        // try self.p()
		        try {
		            MethodInstance mi = 
		                xts.findMethod(this, 
		                               xts.MethodMatcher(this, propName, 
		                                                 Collections.<Type>emptyList(), 
		                                                 xts.emptyContext()));
		            XTerm body = mi.body();
		            final CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
		            body = body.subst(c.self(), mi.x10Def().thisVar());
		            c.addTerm(body);
		            return r.entails(c, new ConstraintMaker() { 
		                public CConstraint make() throws XFailure {
		                    return context.constraintProjection(r, c);
		                }
		            });
		        }
		        catch (XFailure f) {
		            return false;
		        }
		        catch (SemanticException f) {
		            return false;
		        }
		    }
		}

		 public XTerm makeProperty( String propStr) {
			 Name propName = Name.make(propStr);
			 CConstraint c = realX();
			 if (c != null) {
				 // build the synthetic term.
				 XTerm var = selfVar();
				 if (var !=null) {
					 X10FieldInstance fi = Types.getProperty(this, propName);
					 if (fi != null) {

						 TypeSystem xts = typeSystem();
						 XTerm val = xts.xtypeTranslator().translate(var, fi);
						 return val;
					 }
				 }
			 }
			 return null;

		 }
		 public XTerm makeZeroBased() {
			 return makeProperty("zeroBased");
		 }

		 public XTerm makeRail() {
			 return makeProperty("rail");
		 }

		 // Ensure type equality works correctly when a type has vacuous constraints.
		 @Override
		 public boolean equalsImpl(TypeObject o) {
			 if (this == o) return true;
			 if (baseType().get().equalsImpl(o)) {
				 CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
				 if (c.entails(constraint().get()))
					 return true;
			 }
			 if (o instanceof ConstrainedType) {
				 ConstrainedType other = (ConstrainedType) o;
				 Type bt = baseType().get();
				 Type obt = other.baseType().get();
				 if ((bt != obt))
					 return false;
				 CConstraint c = constraint().get();
				 CConstraint oc = other.constraint().get();
				 if (! c.entails(oc) || ! oc.entails(c))
					 return false;
				 return true;
			 }
			 return false;
					 
		 }
	}
