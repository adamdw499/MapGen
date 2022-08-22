package com.mason.mapgen.core;

import com.mason.mapgen.algorithms.chunkers.AbstractChunker;
import com.mason.mapgen.algorithms.chunkers.RandomChunker;
import com.mason.mapgen.algorithms.colorers.AbstractColorer;
import com.mason.mapgen.algorithms.colorers.BiomeColorer;
import com.mason.mapgen.algorithms.colorers.HeightNoiseColorer;
import com.mason.mapgen.algorithms.heightMappers.AbstractHeightMapper;
import com.mason.mapgen.algorithms.heightMappers.MidpointHeightMapper;
import com.mason.mapgen.algorithms.heightMappers.Weatherer;
import com.mason.mapgen.algorithms.landplacers.AbstractLandPlacer;
import com.mason.mapgen.algorithms.landplacers.PerlinIslandPlacer;
import com.mason.mapgen.algorithms.riverplacers.AbstractRiverPlacer;
import com.mason.mapgen.algorithms.riverplacers.LakeRiverPlacer;

public class Launcher{


    public static void main(String[] args){

        WorldManager manager = new WorldManager(800, 600, 600, 600);
        manager.start();

        AbstractChunker voronoi = new RandomChunker(450, 3, manager);
        AbstractLandPlacer landPlacer = new PerlinIslandPlacer(manager);
        AbstractHeightMapper heightMapper = new MidpointHeightMapper();
        AbstractColorer colorer = new BiomeColorer(0, 0.99);
        AbstractRiverPlacer riverPlacer = new LakeRiverPlacer(1);

        voronoi.generatePolygons();
        landPlacer.placeLand();
        double[][] heights = heightMapper.mapHeight(manager.getWorld());
        manager.logSpeed("Placed land");
        new Weatherer(manager.getWorld(), heights, 7000).weather();
        new HeightNoiseColorer(heights).color(manager.getWorld());

        /*landPlacer.classifyBiomes();
        manager.logSpeed("Classified lakes");
        colorer.color(manager.getWorld());
        manager.logSpeed("Colored world");
        riverPlacer.placeRivers(manager.getWorld(), heights);
        manager.logSpeed("Generated rivers");*/

        manager.recalcWorldImage();
        manager.showWorld();

        /*double[][] map = new double[600][600];
        PerlinNoise noise = new LookupNoise(600, 600, 1, 3, 0.7, 0.6);
        noise.apply(map);
        noise.normalise(map);
        HeightNoiseColorer colorer = new HeightNoiseColorer(map);
        colorer.color(manager.getWorld());
        manager.recalcWorldImage();
        manager.showWorld();*/
    }

}
