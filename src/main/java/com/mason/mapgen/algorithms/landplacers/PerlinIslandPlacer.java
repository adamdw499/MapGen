package com.mason.mapgen.algorithms.landplacers;

import com.mason.libgui.utils.noise.LookupNoise;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.WorldMap;


public class PerlinIslandPlacer extends AbstractLandPlacer{


    private final double seaLevel;
    private final double landTapering;
    private final double[][] landMap;


    public PerlinIslandPlacer(WorldMap map, int octaves, double lacunarity, double persistence, double seaLevel, double landTapering, double lakeMoistureCutoff, double moistureDecay){
        super(map, lakeMoistureCutoff, moistureDecay);
        this.seaLevel = seaLevel;
        this.landTapering = landTapering;

        landMap = new double[map.getHeight()][map.getWidth()];
        new LookupNoise(map.getWidth(), map.getHeight(), 2.3, octaves, lacunarity, persistence).apply(landMap);
        makeContinentalShelf(0.3, landMap, map);
    }

    public PerlinIslandPlacer(WorldMap map, double lakeMoistureCutoff, double moistureDecay){
        this(map, 1, 0.7, 0.65, 0.24, 2.9, lakeMoistureCutoff, moistureDecay);
    }


    @Override
    public boolean centroidIsLand(Point centroid) {
        return landMap[centroid.y][centroid.x] > 0.0;
    }


    private static double dist(Point p, double w, double h){
        double dx = 2D*(p.x/w)-1;
        double dy = 2D*(p.y/h)-1;
        return dx*dx + dy*dy;
    }

    private void makeContinentalShelf(double amplitude, double[][] heights, WorldMap map){
        /*for(int y=0; y<heights.length; y++){
            for(int x=0; x<heights[y].length; x++){
                heights[y][x] -= amplitude*(seaLevel + landTapering*dist(map[y][x], map[y].length, map.length));
            }
        }*/

        //REDO
    }

    public double[][] getPreliminaryHeightMap(){
        return landMap;
    }


}
