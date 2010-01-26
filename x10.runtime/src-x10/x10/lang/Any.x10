package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * The top of the type hierarchy.
 * Implemented by all classes and structs.
 *
 * @author vj 12/14/09
 */
@NativeRep("java", "java.lang.Object", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Any>", "x10::lang::Any", null)
public interface Any {

    /**
     * Return the home location of this entity.
     * This will be 'here' for non-object entities.
     * @return the home location of this entity.
     */
    @Native("java", "x10.lang.Place.place(x10.core.Ref.home(#0))")
    @Native("c++", "x10::lang::Place_methods::place(x10aux::get_location(#0))")
    property def home():Place;

    /**
     * Return true if this entity is in the same home location as the given object.
     *
     * @param p The given object
     * @return true if the home location of this entity is the same as the home location of p.
     */
    @Native("java", "x10.core.Ref.at(#0, #1)")
    @Native("c++", "(x10aux::get_location(#0) == (#1)->location)")
    property def at(p:Object):Boolean;

    /**
     * Return true if the home location of this entity is the given place.
     *
     * @param p The given place
     * @return true if the home location of this entity is p.
     */
    @Native("java", "x10.core.Ref.at(#0, #1.id)")
    @Native("c++", "(x10aux::get_location(#0) == (#1)->FMGL(id))")
    property def at(p:Place):Boolean;

    /**
     * Return the string representation of this entity.
     *
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the string representation
     * has to include only global information, or the implementation has to
     * ensure that the home location of the entity is 'here', and possibly
     * throw an exception if not.
     *
     * @return a string representation of this entity.
     */
    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    global safe def toString():String;

    /**
     * Return a string representation of the run-time type of this entity.
     * Should not be overridden.
     *
     * @return a string representation of the run-time type of this entity.
     */
    @Native("java", "x10.core.Ref.typeName(#0)")
    @Native("c++", "x10aux::type_name(#0)")
    global safe def typeName():String;

    /**
     * Return true if this entity is equal to the given entity in an
     * implementation-dependent way.
     * The usual properties of symmetry, commutativity, and purity should
     * apply, i.e., x.equals(x) should always be true; if x.equals(y) is true,
     * then so should y.equals(x) be; and x.equals(y) should return the same
     * value on subsequent invocations.
     *
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only global information, or the implementation has
     * to ensure that the home location of the entities is 'here', and
     * possibly throw an exception if not.
     *
     * @param that the given entity
     * @return true if this entity is equal to the given entity.
     */
    @Native("java", "#0.equals(#1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    global safe def equals(that:Any):Boolean;

    /**
     * Return the implementation-defined hash code of this entity.
     * The implementation should be pure, i.e., x.hashCode() should return the
     * same value on subsequent invocations, with an additional invariant that
     * if x.equals(y) is true, then x.hashCode() should equal y.hashCode().
     *
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only global information, or the implementation has
     * to ensure that the home location of the entities is 'here', and
     * possibly throw an exception if not.
     *
     * @return the hash code of this entity.
     */
    @Native("java", "#0.hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    global safe def hashCode():Int;
}

// vim:shiftwidth=4:tabstop=4:expandtab
