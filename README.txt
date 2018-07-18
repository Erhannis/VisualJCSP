Current status:
Slightly functional!  Many of the UI actions and underlying structure are set up, and it can generate some source code - there are likely edge cases I haven't dealt with, yet, and there is an extremely limited palette of blocks to use, atm.


Internals developer notes:
1. A Drawable X's parent Y is responsible for knowing where X is located, relevant to Y.

Dunno:
The lifecycle of a block is as follows: archetype, wireform, runform.
Archetype is basically "class".  It's a type of block, but not an instance of one.  It knows what parameters it WILL BE ABLE to have, but itself has no parameters.
Wireform is what happens when you drag an archetype onto the board.  It has settings, terminals, and is connected to other wireform blocks, but has no actual understanding of what it DOES.
Runform is the actual, runnable instantiation of the wireform.  It may have connections of its own, implemented in the manner of its kind, and can do things or be run, etc.
