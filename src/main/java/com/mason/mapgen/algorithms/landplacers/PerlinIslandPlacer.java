package com.mason.mapgen.algorithms.landplacers;

import com.mason.libgui.utils.noise.LookupNoise;
import com.mason.mapgen.components.Biome;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;

import java.util.List;


public class PerlinIslandPlacer extends AbstractLandPlacer{


    private final int octaves;
    private final double lacunarity;
    private final double persistence;
    private final double seaLevel;
    private final double landTapering;


    public PerlinIslandPlacer(WorldManager manager, int octaves, double lacunarity, double persistence, double seaLevel, double landTapering){
        super(manager);
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.persistence = persistence;
        this.seaLevel = seaLevel;
        this.landTapering = landTapering;
    }

    public PerlinIslandPlacer(WorldManager manager){
        this(manager, 1, 0.7, 0.65, 0.24, 2.9);
    }


    @Override
    public void placeLand(){
        Point[][] map = manager.getMap();
        //PerlinNoise noise = new PerlinNoise(map[0].length, map.length, 2.3, octaves, lacunarity, persistence);
        LookupNoise noise = new LookupNoise(map[0].length, map.length, 2.3, octaves, lacunarity, persistence);
        double[][] heights = new double[map.length][map[0].length];
        noise.apply(heights);
        List<Point> centroids = manager.getWorld().getCentroids();
        makeContinentalShelf(0.3, heights, map);
        for(Point centroid : centroids){
            //centroid.getSeedInfo().setElevation(heights[centroid.y][centroid.x]);
            if(heights[centroid.y][centroid.x]>0.0)
                centroid.getSeedInfo().setBiome(Biome.LAND);
        }
        //manager.getWorld().setWaterTextureMap(heights);
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
