/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import java.lang.reflect.Type;

/**
 *
 * @author erhannis
 */
public class Generic {
  /*
   Hmm.  So, thoughts.
   We have to be able to handle a case where A <= B <= C.
   Like, Collection <= T <= U <= V <= Map.
  
   What do we want out of this? ...We want to be able to allow blocks
   to limit the input they accept, but still allow for generality.
  
   Do we want to allow for <T super Map> or whatever?  What was that for,
   again?  ...I think that was only for wildcards, not generics.  I think
   its purpose was so you know you can SEND it Maps, at least.  It doesn't
   make sense as an actual generic because if you're using it, you can
   just specify "Map" - otherwise, just say "Object" and Map is a 
   type of Object, anyway.  I think it's specifically for use in
   *instantiating* generic classes.  Hmm.
  
   A generic, here, though, can have upper and lower bounds - what's
   coming in has a certain type, and what's going out has a certain type,
   and I think they have to meet in the middle somewhere - or rather, there
   must *exist* a meeting point in the middle.  Hmm.  Let's try example.
  
   So, suppose a block takes two inputs of T, and emits two outputs of T.
   Don't ask me how; I don't know what it does.  Maybe it swaps the objects
   randomly, or something.  Good enough.  Suppose you feed a HashMap into
   Left Input, and connect Left Output to an acceptor of Collection.
   Now, if you feed a Collection into the Right Input, the Right Output
   can be at most a Collection.  If you feed a HashMap into the Right Output,
   the Right Output could be anything up to a HashMap.
   If you first pull a HashMap from the Right Output, the Right Input
   must be a HashMap or above.
  
   (Note I guess I'm swapping the directionality of the classes.  Subclasses
   are "above" superclasses, despite the conflict in the names.)
  
   If you pull a Collection from the Right Output, the Right Input can be 
   down to a Collection.
  
   Then, there's the notion of transitive generics....
  
   See, suppose you attach the an end of the swap block to a split block.  The
   outputs of the splitter need to update when something's attached to the swap
   block.  A special case of that is when you want to know whether you can
   MAKE the attachment - combinations of paths could result in an invalid
   connection, maybe.
  
   Hmm.  But it seems like, it's not really the responsibility of the generics
   to know about connections, and it's not really the connections' responsibility
   to know about the generics.  Of the two, it's probably more the connections'
   responsibility.
  
   So, at any given point, the connections affect the "permissible" changes
   to the generics - attaching stuff can restrict the generic, but not expand it.
   Probably similiarly with detaching and expanding, too.  Also, the state should
   be history-independent, and probably order independent.  Where and how should
   a generic's current restrictions be given?  I feel like the restrictions
   should maybe be recalculated each time, rather than kept and expanded and
   contracted.  However, they'll need to be kept until the current check/change
   has finished, and the generic's current "value" should be kept for possible
   display purposes.
  
   So, I think maybe we'll have the basic bounds the generic is born with, the
   connections will recursively...hang on, terminals don't really talk with each
   other across a block.  The block will probably need to coordinate them, maybe.
   Hmmmmmm.  Actually, one generic in a block could be assigned to multiple
   connections.
  
   Thought: it concerns me that we'll get infinite loops.  However, we
   recursively travel up and down connections, but ONLY when an actual change is
   made (i.e., restriction for adding connection, destriction for removing one),
   and since for a given network update we're only going one direction, and
   since the class tree is finite, we must eventually halt.  ...Unless we make
   some kind of recursive change like T -> T<T> or something....  Come to think
   of it, I don't really know how to deal with generics-that-have-generics.  I
   don't think I even have a very good idea of what I'd need to deal with, there.
   Something about List<T>, maybe.  Ugh, I'm not even sure what...anything.
  
   Like, I guess there's a difference between being able to cast T to something,
   vs. being able to cast List<T> to something.
  
   Hmm.  If I make the api generic enough (haha), it shouldn't matter what I'm
   forgetting.  Hopefully.  Like, "boolean canRestrictToX(Generic x)" or 
   something.
  
   Is a "generic" separate from an IntOrEventualClass (which oughtta get named
   better)?  I think it has to be, because say <T> and <List<T>> are different
   classes, but share a generic.  Is it a pure placeholder?  I think something
   probably....
  
   I'm thinking that IntOrEventualClass will use Type, instead, and will not 
   track generics.  Generics in Type params are named, and I THINK that the
   name -> generic mapping applies to the whole class using the generics.
   A given Wireform will have a Map<String, Generic> or something, and Generic
   will be responsible for storing the current bounds of the corresponding
   generic.  The Wireform will be responsible for propagating updates in bounds
   to other Wireforms over Connections.  That allows me to let the built in type
   system handle comparisons and the actual structure of the types.  ...Except.
   The Wireforms are restricting the generics in ways the generics are not
   inherently aware of.  I may have to, say, pass a map into a given "compare"
   call (and beware cross-block name conflicts), and reconstruct the type
   structure with further bounded (or just reified) generics (or specific
   classes).
   */

  public static class Bounds {
    public Type[] upperBounds;
    public Type[] lowerBounds;
  }

  public String name;

  // Base bounds
  public Bounds baseBounds;
  
  // Current bounds - or specific places it's been restricted?
  public transient Bounds currentBounds;
}
