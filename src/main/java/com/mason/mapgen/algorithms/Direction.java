package com.mason.mapgen.algorithms;

public enum Direction{


    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1,0);


    public final int x, y;


    private Direction(int x, int y){
        this.x = x;
        this.y = y;
    }

}
