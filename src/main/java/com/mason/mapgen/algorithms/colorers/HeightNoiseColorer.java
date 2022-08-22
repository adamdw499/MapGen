package com.mason.mapgen.algorithms.colorers;

import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.awt.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HeightNoiseColorer implements AbstractColorer{


    private final double[][] heights;


    public HeightNoiseColorer(double[][] map){
        heights = map;
    }


    @Override
    public void color(World world){
        Point[][] map = world.getMap();
        for(int y=0; y<heights.length; y++){
            for(int x=0; x<heights[y].length; x++){
                if(!map[y][x].isTraversed()) map[y][x].setColor(getColor(heights[y][x]));
            }
        }
    }

    private Color getColor(double h){
        int v = max(min((int)(150D*(0.5D+h)), 255), 0);
        return new Color(v, v, v);
    }

}
