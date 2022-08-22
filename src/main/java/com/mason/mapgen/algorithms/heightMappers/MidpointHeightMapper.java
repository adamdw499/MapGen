package com.mason.mapgen.algorithms.heightMappers;

import com.mason.mapgen.algorithms.Direction;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.util.LinkedList;

import static com.mason.libgui.utils.Utils.R;

public class MidpointHeightMapper implements AbstractHeightMapper{


    @Override
    public double[][] mapHeight(World world){
        Point[][] map = world.getMap();
        double[][] heights = new double[map.length][map[0].length];
        double[][] stoneMap = world.getStoneTextureMap();
        LinkedList<Point> frontier = getLandBorders(world, map);
        for(Point p : frontier) p.traverse();
        double increment = 0.015;
        Point c;

        while(!frontier.isEmpty()){
            c = frontier.remove(R.nextInt(frontier.size()));
            for(Direction d : Direction.values()){
                if(world.pointWithinBounds(c.x+d.x, c.y+d.y) && canTakeOver(c.x, c.y, d, heights, map)){
                    if(map[c.y+d.y][c.x+d.x].isLand()){
                        heights[c.y+d.y][c.x+d.x] = heights[c.y][c.x] + increment;
                    }else{
                        heights[c.y+d.y][c.x+d.x] = heights[c.y][c.x] - increment;
                    }
                    map[c.y+d.y][c.x+d.x].traverse();
                    frontier.add(map[c.y+d.y][c.x+d.x]);
                }
            }
        }

        for(int y=0; y<heights.length; y++){
            for(int x=0; x<heights[y].length; x++){
                heights[y][x] = 0.7*heights[y][x] + 0.3*stoneMap[y][x];
            }
        }

        for(Point s : world.getCentroids()) s.getSeedInfo().setElevation(heights[s.y][s.x]);

        world.resetTraversals();

        return heights;
    }


    private LinkedList<Point> getLandBorders(World world, Point[][] map){
        LinkedList<Point> borders = new LinkedList<>();
        for(Point[] line : map){
            for(Point point : line){
                if(point.isLand() && bordersWater(point, world, map)) borders.add(point);
            }
        }
        return borders;
    }

    private boolean bordersWater(Point c, World world, Point[][] map){
        for(Direction d : Direction.values()) if(world.pointWithinBounds(c.x+d.x, c.y+d.y)
                && !map[c.y+d.y][c.x+d.x].isLand()) return true;
        return false;
    }

    private boolean canTakeOver(int x, int y, Direction d, double[][] heights, Point[][] map){
        return !map[y+d.y][x+d.x].isTraversed();
    }

}
