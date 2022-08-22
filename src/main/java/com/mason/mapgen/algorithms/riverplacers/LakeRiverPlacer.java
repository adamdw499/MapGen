package com.mason.mapgen.algorithms.riverplacers;

import com.mason.mapgen.algorithms.Direction;
import com.mason.mapgen.components.Biome;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.util.LinkedList;
import java.util.List;

import static com.mason.libgui.utils.Utils.R;

public class LakeRiverPlacer implements AbstractRiverPlacer{


    private final double lakeFraction;


    public LakeRiverPlacer(double lakeFraction){
        this.lakeFraction = lakeFraction;
    }


    @Override
    public void placeRivers(World world, double[][] heights){
        LinkedList<Point> sources = getRiverSources(world, world.getMap(), heights);
        for(Point source : sources) generateRiver(source, world, world.getMap(), heights);
    }

    private LinkedList<Point> getRiverSources(World world, Point[][] map, double[][] heights){
        List<Point> centroids = world.getCentroids().stream().filter(p -> p.hasBiome(Biome.LAKE)).toList();
        LinkedList<Point> frontier = new LinkedList<>(), border = new LinkedList<>(), sources = new LinkedList<>();
        Point c;

        for(Point centroid : centroids) if(!centroid.isTraversed()){
            frontier.add(centroid);
            centroid.traverse();
            while(!frontier.isEmpty()){
                c = frontier.remove(0);
                if(bordersLand(c, world, map)){
                    border.add(c);
                }
                for(Direction d : Direction.values()) if(world.pointWithinBounds(c.x+d.x, c.y+d.y)
                        && !map[c.y+d.y][c.x+d.x].isTraversed()
                        && map[c.y+d.y][c.x+d.x].hasBiome(Biome.LAKE)){
                    frontier.add(map[c.y+d.y][c.x+d.x]);
                    map[c.y+d.y][c.x+d.x].traverse();
                }
            }
            if(R.nextDouble() < lakeFraction){
                sources.add(border.stream().min((a, b) -> compareHeights(a, b, heights)).orElseThrow());
            }
            border.clear();
        }
        world.resetTraversals();
        return sources;
    }

    private boolean bordersLand(Point c, World world, Point[][] map){
        for(Direction d : Direction.values()) if(world.pointWithinBounds(c.x+d.x, c.y+d.y)
            && map[c.y+d.y][c.x+d.x].isLand()) return true;
        return false;
    }

    private static int compareHeights(Point a, Point b, double[][] heights){
        return Double.compare(heights[a.y][a.x], heights[b.y][b.x]);
    }


    private void generateRiver(Point p, World world, Point[][] map, double[][] heights){
        while(p != null){
            p = getNextRiverPoint(p, world, map, heights);
            if(p != null){
                p.setRiver(true, world);
            }
        }
    }

    private Point getNextRiverPoint(Point c, World world, Point[][] map, double[][] heights){
        double minHeight = heights[c.y][c.x];
        Point next = null;
        for(Direction d : Direction.values()) if(world.pointWithinBounds(c.x+d.x, c.y+d.y)
                && heights[c.y+d.y][c.x+d.x] < minHeight){
            minHeight = heights[c.y+d.y][c.x+d.x];
            next = map[c.y+d.y][c.x+d.x];
        }
        return next != null && next.isLand() && !next.isRiver() ? next : null;
    }

}
