# Game Of Life on Spark

This is an implementation of the game of life (https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) on spark.
This is a distributed version of the game that can run on an arbitrary large universe. 

![Sample from wikipedia](https://upload.wikimedia.org/wikipedia/commons/e/e5/Gospers_glider_gun.gif)

## The Game

As per the wikipedia page:
> The game Game of Life, also known simply as Life, is a cellular automaton devised by the 
British mathematician John Horton Conway in 1970. The game is a zero-player game, meaning that its evolution is 
determined by its initial state, requiring no further input. One interacts with the Game of Life by creating an 
initial configuration and observing how it evolves, or, for advanced players, by creating patterns with particular 
properties.

>### Rules
>The universe of the Game of Life is an infinite, two-dimensional orthogonal grid of square cells, each of which is 
in one of two possible states, alive or dead, (or populated and unpopulated, respectively). Every cell interacts with 
its eight neighbours, which are the cells that are horizontally, vertically, or diagonally adjacent. At each step in 
time, the following transitions occur:

    Any live cell with fewer than two live neighbors dies, as if by under population.
    Any live cell with two or three live neighbors lives on to the next generation.
    Any live cell with more than three live neighbors dies, as if by overpopulation.
    Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.

>The initial pattern constitutes the seed of the system. The first generation is created by applying the above rules 
simultaneously to every cell in the seed; births and deaths occur simultaneously, and the discrete moment at which 
this happens is sometimes called a tick. Each generation is a pure function of the preceding one. The rules continue 
to be applied repeatedly to create further generations.

## How the game is played in a distributed way

The universe is is split into sectors.
Each sector (here represented by the domain model class Sector) can be calculated independently provided that the
boundaries (live cells on nearby sectors, class Boundaries) are communicated between neighbour sectors.

Lets see what that means. We'll show the game played with 10x5 sectors on a 4x4 (16 sectors) universe. This
is the starting state of the universe:

    ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░
    ░█         ░ | ░   ██   █ ▒ | ░█         ░ | ░          ░
    ░     █   █░ | ▒     █    ▒ | ░█ █     █ ░ | ░  █    █  ░
    ░    █    █▒ | ▒█         ░ | ░  ██      ░ | ░  ███     ░
    ░       █ █▒ | ▒█      ██ ░ | ░   █      ░ | ░   █    █ ░
    ░  █   █   ░ | ░  █       ░ | ░  █   █   ░ | ░ █        ░
    ░▒░▒░░░░░░▒░ | ▒░░░▒░░░░░░░ | ░░░░░░▒░▒░░░ | ░░░░▒░░▒░░░░
    -----------------------------------------------------
    ░░░▒░░░▒░░░░ | ░░░▒░░░░░░░░ | ░░░▒░░░▒░░░░ | ░░▒░░░░░░░░░
    ░█ █      █░ | ▒   █      ░ | ░     █ █  ░ | ░   █  █   ░
    ░        ██▒ | ▒██   ██  █░ | ▒          ▒ | ░█    █ █  ░
    ░ █ █      ░ | ░      █   ░ | ░    █ █   ░ | ░ ██ █ █   ░
    ░       █  ░ | ░       █  ░ | ░  █       ░ | ░    █     ░
    ░     ██   ░ | ░         █▒ | ▒█ ██     █░ | ▒          ░
    ░░░░░░░▒░░▒░ | ▒░░▒▒░░░░░░░ | ░░▒░▒░▒▒▒░░▒ | ░▒░░░▒░░░░░░
    -----------------------------------------------------
    ░░░░░░▒▒░░░░ | ░░░░░░░░░░▒▒ | ▒▒░▒▒░░░░░▒░ | ▒░░░░░░░░░░░
    ░      █  █░ | ▒  ██      ░ | ░ █ █ ███  ▒ | ░█   █     ░
    ░    █     ░ | ░          ▒ | ░█         ░ | ░       █ █░
    ░    ███   ░ | ░  █   █   ░ | ░       █  ░ | ░    █   █ ░
    ░ █ █      ░ | ░  ██      ░ | ░ █        ▒ | ░█   █     ░
    ░ █       █▒ | ▒█  █      ░ | ░         █░ | ▒  █ █     ░
    ░░▒░░░░░░░░░ | ░░░░▒░░░░░░░ | ░░░▒░▒▒▒░░▒░ | ▒░░░░▒░░▒▒░░
    -----------------------------------------------------
    ░░▒░░░░░░░▒▒ | ▒▒░░▒░░░░░░░ | ░░░░░░░░░░▒░ | ▒░░▒░▒░░░░░░
    ░ █        ░ | ░   █      ░ | ░  █ ███  █░ | ▒    █  ██ ░
    ░ █ █     █░ | ▒   █  █   ▒ | ░█ █       ░ | ░          ░
    ░          ░ | ░ ██       ░ | ░ █        ▒ | ░██     █  ░
    ░          ░ | ░        █ ░ | ░          ░ | ░   █ █    ░
    ░█  █ ███  ░ | ░      █ ██▒ | ▒█         ░ | ░          ░
    ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░

█ = Live cell  ▒ = Live cell on the boundaries of the sector  ░ = Dead cell on the boundaries

The boundaries we see here are just neighbour sectors communicating their live cells. This is needed because the game
checks all 9 neigbours of a cell to decide if the cell stays alive or dies. For example lets see sector (0,0), it has
2 boundary live cells on the right because sector (1,0) has 2 live cells just next to sector (0,0).

Now next turn and the universe becomes:

    ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░
    ░          ░ | ░    █    █░ | ▒ █        ░ | ░          ░
    ░         █▒ | ▒█   █    █░ | ▒  ██      ░ | ░  █       ░
    ░          ░ | ░          ░ | ░ ███      ░ | ░  █ █     ░
    ░        ██▒ | ▒██        ░ | ░   █      ░ | ░   ██     ░
    ░ █      ██▒ | ▒█         ░ | ░      █   ░ | ░  █       ░
    ░░▒░░░░░░▒▒░ | ▒░▒▒░░░░░░░░ | ░░░░░░░▒░░░░ | ░░░░░░░▒░░░░
    -----------------------------------------------------
    ░░▒░░░░░░▒▒▒ | ▒▒░░░░░░░░░░ | ░░░░░░░▒░░░░ | ░░░▒░░░░░░░░
    ░ █      ██░ | ▒ ██       ░ | ░      █   ░ | ░      █   ░
    ░ ██     ██▒ | ▒█    ██   ░ | ░     ██   ░ | ░ █████ █  ░
    ░        ██▒ | ▒█    ███  ░ | ░          ░ | ░ █ ██ █   ░
    ░      █   ░ | ░          ░ | ░ ██       ░ | ░   █ █    ░
    ░     ███  ░ | ░          ▒ | ░█  ██ █   ░ | ░          ░
    ░░░░░░░▒░░░░ | ░░░░░░░░░░▒░ | ▒░▒░▒▒░▒░░░░ | ░░░░░░░░░░░░
    -----------------------------------------------------
    ░░░░░░▒▒▒░░░ | ░░░░░░░░░░░▒ | ░▒░░▒▒░▒░░░░ | ░░░░░░░░░░░░
    ░      █   ░ | ░         █░ | ▒ █ ██ █   ░ | ░          ░
    ░    █ █   ░ | ░  ██      ░ | ░       █  ░ | ░        █ ░
    ░   ███    ░ | ░  ██      ░ | ░          ░ | ░        █ ░
    ░  █ ██    ░ | ░ ███      ░ | ░          ░ | ░    ██    ░
    ░██        ░ | ░   ██     ░ | ░     █   █▒ | ▒█   ██    ░
    ░▒▒░░░░░░░▒▒ | ▒▒░▒▒▒░░░░░░ | ░░▒░▒░▒░░░░░ | ░░░░▒░░░░░░░
    -----------------------------------------------------
    ░▒▒░░░░░░░░░ | ░░░░▒▒░░░░░░ | ░░░░░░▒░░░▒▒ | ▒▒░░░▒▒░░░░░
    ░██       █▒ | ▒█ ███     ░ | ░ █ █ █    ░ | ░   █      ░
    ░  █       ░ | ░   █      ░ | ░  ██ █    ▒ | ░█      ██ ░
    ░          ░ | ░  █       ░ | ░ █        ░ | ░          ░
    ░      █   ░ | ░       ██ ▒ | ░█         ░ | ░          ░
    ░      █   ░ | ░       ███░ | ▒          ░ | ░          ░
    ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░ | ░░░░░░░░░░░░

Ascii art like this can be created by running class QuickPlay locally (hadoop not needed).

## Implementation

The domain model has all the logic for sectors (Sector class), boundaries (Boundaries class), edges (these are
the messages send from each sector so that boundaries can be created on neighbors, Edges class). The domain model
uses interfaces so that we can impl different (more efficient?) classes, i.e. we can do an empty sector implementation
that uses no memory.