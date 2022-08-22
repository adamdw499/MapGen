package com.mason.mapgen.algorithms.landplacers;

import com.mason.libgui.utils.noise.LookupNoise;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;


public class PerlinIslandPlacer extends AbstractLandPlacer{


    private final double seaLevel;
    private final double landTapering;
    private final double[][] landMap;


    public PerlinIslandPlacer(WorldManager manager, int octaves, double lacunarity, double persistence, double seaLevel, double landTapering){
        super(manager);
        this.seaLevel = seaLevel;
        this.landTapering = landTapering;

        Point[][] map = manager.getMap();
        LookupNoise noise = new LookupNoise(map[0].length, map.length, 2.3, octaves, lacunarity, persistence);
        landMap = new double[map.length][map[0].length];
        noise.apply(landMap);
        makeContinentalShelf(0.3, landMap, map);

    }

    public PerlinIslandPlacer(WorldManager manager){
        this(manager, 1, 0.7, 0.65, 0.24, 2.9);
    }


    @Override
    public boolean centroidIsLand(Point centroid) {
        return landMap[centroid.y][centroid.x] > 0.0;
    }


    private double dist(Point p, double w, double h){
        double dx = 2D*(p.x/w)-1;
        double dy = 2D*(p.y/h)-1;
        return dx*dx + dy*dy;
    }

    private void makeContinentalShelf(double amplitude, double[][] heights, Point[][] map){
        for(int y=0; y<heights.length; y++){
            for(int x=0; x<heights[y].length; x++){
                heights[y][x] -= amplitude*(seaLevel + landTapering*dist(map[y][x], map[y].length, map.length));
            }
        }
    }


}
