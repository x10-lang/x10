/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.util.InternalCompilerError;
import polyglot.util.UniqueID;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

/**
 * The <code>AlphaRenamer</code> runs over the AST and alpha-renames any local
 * variable declarations that it encounters.
 **/
public class AlphaRenamer extends NodeVisitor {

  // Each set in this stack tracks the set of local decls in a block that
  // we're traversing.
  protected Stack<Set<Name>> setStack;

  protected Map<Name,Name> renamingMap;

  protected Map<LocalDef, Name> oldNamesMap;
  
  // Tracks the set of variables known to be fresh.
  protected Set<Name> freshVars;

  protected Map<Name,Name> labelMap;


  /**
   * Creates a visitor for alpha-renaming locals.
   *
   * @param nf  The node factory to be used when generating new nodes.
   **/
  public AlphaRenamer() {
    this.setStack = new Stack<Set<Name>>();
    this.setStack.push( CollectionFactory.<Name>newHashSet() );

    this.oldNamesMap = CollectionFactory.newHashMap();
    this.renamingMap = CollectionFactory.newHashMap();
    this.labelMap = CollectionFactory.newHashMap();
    this.freshVars = CollectionFactory.newHashSet();
  }

  /** Map from local def to old names. */
  public Map<LocalDef, Name> getMap() {
      return oldNamesMap;
  }

  public static final String LABEL_PREFIX = "label ";

  public NodeVisitor enter( Node n ) {
    if ( n instanceof Block ) {
      // Push a new, empty set onto the stack.
      setStack.push( CollectionFactory.<Name>newHashSet() );
    }

    if ( n instanceof LocalDecl ) {
      LocalDecl l = (LocalDecl)n;
      Name name = l.name().id();

      if ( !freshVars.contains(name) ) {
	// Add a new entry to the current renaming map.
	Name name_ = Name.makeFresh(name);

	freshVars.add(name_);
	
	setStack.peek().add( name );
	renamingMap.put( name, name_ );
      }
    }

    if ( n instanceof Formal ) {
      Formal f = (Formal)n;
      Name name = f.name().id();

      if ( !freshVars.contains(name) ) {
	// Add a new entry to the current renaming map.
	Name name_ = Name.makeFresh(name);

	freshVars.add(name_);
	
	setStack.peek().add( name );
	renamingMap.put( name, name_ );
      }
    }

    if ( n instanceof Labeled ) {
      Labeled l = (Labeled) n;
      Name name = l.labelNode().id();
      Name key = Name.make(LABEL_PREFIX+name.toString());
      if ( !freshVars.contains(key) ) {
        Name name_ = Name.makeFresh(name);
        Name key_ = Name.make(LABEL_PREFIX+name_.toString());

        freshVars.add(key_);

        setStack.peek().add(key);
        labelMap.put(key, name_);
      }
    }
    return this;
  }

  public Node leave( Node old, Node n, NodeVisitor v ) {
    if ( n instanceof Block ) {
      // Pop the current name set off the stack and remove the corresponding
      // entries from the renaming map.
      Set<Name> s = setStack.pop();
      renamingMap.keySet().removeAll(s);
      labelMap.keySet().removeAll(s);
      return n;
    }

    if ( n instanceof Local ) {
      // Rename the local if its name is in the renaming map.
      Local l = (Local)n;
      Name name = l.name().id();

      if ( !renamingMap.containsKey(name) ) {
	return n;
      }
      
      // Update the local instance as necessary.
      Name newName = renamingMap.get(name);
//      LocalType li = l.localInstance();
//      if (li != null) li.setName(newName);

      return l.name(l.name().id(newName));
    }

    if ( n instanceof LocalDecl ) {
      // Rename the local decl.
      LocalDecl l = (LocalDecl)n;
      Name name = l.name().id();

      if ( freshVars.contains(name) ) {
	return n;
      }

      if ( !renamingMap.containsKey(name) ) {
	throw new InternalCompilerError( "Unexpected error encountered while alpha-renaming." );
      }

      // Update the local instance as necessary.
      Name newName = renamingMap.get(name);
      LocalDef li = l.localDef();
      if (li != null) {
	  oldNamesMap.put(li, li.name());
	  li.setName(newName);
      }
      return l.name(l.name().id(newName));
    }

    if ( n instanceof Formal ) {
      // Rename the local decl.
      Formal f = (Formal)n;
      Name name = f.name().id();

      if ( freshVars.contains(name) ) {
	return n;
      }

      if ( !renamingMap.containsKey(name) ) {
	throw new InternalCompilerError( "Unexpected error encountered while alpha-renaming." );
      }

      // Update the local instance as necessary.
      Name newName = renamingMap.get(name);
      LocalDef li = f.localDef();
      if (li != null) {
	  oldNamesMap.put(li, li.name());
	  li.setName(newName);
      }
      return f.name(f.name().id(newName));
    }

    if ( n instanceof Branch ) {
      // Rename the label if its name is in the renaming map.
      Branch b = (Branch)n;

      if (b.labelNode() == null) {
        return n;
      }

      Name name = b.labelNode().id();
      Name key = Name.make(LABEL_PREFIX+name.toString());

      if ( !labelMap.containsKey(key) ) {
        return n;
      }
        
      Name newName = labelMap.get(key);

      return b.labelNode(b.labelNode().id(newName));
    }

    if ( n instanceof Labeled ) {
      Labeled l = (Labeled) n;
      Name name = l.labelNode().id();
      Name key = Name.make(LABEL_PREFIX+name.toString());

      if ( freshVars.contains(key) ) {
        return n;
      }

      if ( !labelMap.containsKey(key) ) {
        throw new InternalCompilerError( "Unexpected error encountered while alpha-renaming." );
      }

      Name newName = labelMap.get(key);
      return l.labelNode(l.labelNode().id(newName));
    }

    return n;
  }
}
