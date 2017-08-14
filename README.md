# Boggle Solver in Clojure

Produces all the possible words from the official scrabble word list
that can be found given a Boggle board. A Boggle board is an NxN matrix
of tiles. Each tile is a letter (or, in the case of "QU", two letters)
with a grid position. A word is made up of adjacent tiles, but in a
single word, no tile can be used twice (but two distinct words can use
each other's tiles).

## Usage

The Boggle solver reads a board as contained in a text file. The board format is
space-separated cells with line-separated rows, and *nothing else in the file*.
For example, a board file might have the following contents:

```
H F E R
E A O Y
O Y V I
A R E I
```

This would be a 4x4 Boggle board. The solver can cope with non-square boards, as
well as uneven boards (where the rows are different lengths), but these would
not really be Boggle boards.

It can be run as follows:

    $ java -jar boggle-1.0.0.jar "/path/to/board.txt"

It will then produce a list of words (with the "path", i.e. the way it is made
up), along with a count of how many words it found. For example, the output may
be:

```
VIE :  ([[V 2 2] [I 2 3] [E 3 2]] [[V 2 2] [I 3 3] [E 3 2]])
FEH :  ([[F 0 1] [E 1 0] [H 0 0]])
YAR :  ([[Y 2 1] [A 3 0] [R 3 1]])
...
RAY :  ([[R 3 1] [A 3 0] [Y 2 1]])
HAO :  ([[H 0 0] [A 1 1] [O 1 2]] [[H 0 0] [A 1 1] [O 2 0]])
IVY :  ([[I 2 3] [V 2 2] [Y 1 3]] [[I 2 3] [V 2 2] [Y 2 1]] [[I 3 3] [V 2 2] [Y 1 3]] [[I 3 3] [V 2 2] [Y 2 1]])
75 words found!
```

## Building

To build this project, you first need to install its dependencies (i.e.
leiningen). You can do this with:

    $ brew install leiningen

You can then run the following to compile it:

    $ lein uberjar

This puts a jar file in `target/uberjar/boggle-1.0.0.jar`.
