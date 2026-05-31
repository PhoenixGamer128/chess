# Phase 0

There was a lot of things that I learned.
Thinking through the process of the game,
I tried to implement everything one at a time
until I realized that there had to be ways
to simplify the process through automation
and for loops. One of the biggest issues in my
code is that I have several ways to verify that
a piece can actually move to a specific location.
The Queen, Rook, and Bishop all use the same
process, the King and Knight use a different one,
and the Pawn uses a different setup as well. I
think that if I were to do it again (and I will
for the practice exam and the actual programming
exam), I would more strongly implement the
one functionality rule.

# Phase 1

I learned quite a bit about general code optimization.
There are several things in my code that is probably
unnecessary, so I think the next time I start working
on a big project like this is to design what I want
my project to do and ask myself: is this really the
best way to do what I want it to do, or is there a
better way or is it unnecessary?

# Phase 3

Wow, this was a very long phase. I didn't know what
to do at first, so I just started at the very end of
registration (low level, working with primitives).
I think one of the most important things I learned
was to look at the specs and figure out what exactly
I should be returning. I spent a long time returning
a GameID object/record when it should have been a
simple integer. I also didn't realize that my game
list was supposed to be an actual list instead of --
what I thought was a good idea at the time -- a 
hashmap. I thought that it would make everything
run faster, but there really is no need if I need
to iterate through my games checking for gameIDs.

# Phase 4

I unfortunately lost several hours of work because
I wasn't committing enough. I thought I was, but
I now know that committing EVERY single time you
accomplish something is a reason to commit. This
was a pretty long phase for me, not because it was
inherently difficult, but because I was trying to
remember all the syntax of SQL and how to convert
it for JDBC.

# Phase 5

Printing out the chess board was a lot harder
than I thought. It was easy to print the white
side, but then I had to think about my logic
for turning the board around. I made my code
even messier when it came to adding in the 
coordinates.