package com.mason.mapgen.core;

import com.mason.mapgen.algorithms.chunkers.AbstractChunker;
import com.mason.mapgen.algorithms.chunkers.RandomChunker;
import com.mason.mapgen.algorithms.chunkers.VoronoiChunker;
import com.mason.mapgen.algorithms.colorers.AbstractColorer;
import com.mason.mapgen.algorithms.colorers.BiomeColorer;
import com.mason.mapgen.algorithms.colorers.HeightNoiseColorer;
import com.mason.mapgen.algorithms.heightMappers.AbstractHeightMapper;
import com.mason.mapgen.algorithms.heightMappers.MidpointHeightMapper;
import com.mason.mapgen.algorithms.heightMappers.Weatherer;
import com.mason.mapgen.algorithms.landplacers.AbstractLandPlacer;
import com.mason.mapgen.algorithms.landplacers.PerlinIslandPlacer;
import com.mason.mapgen.algorithms.landplacers.TectonicPlacer;
import com.mason.mapgen.algorithms.riverplacers.AbstractRiverPlacer;
import com.mason.mapgen.algorithms.riverplacers.LakeRiverPlacer;

public class Launcher{


    public static void main(String[] args){

        WorldManager manager = new WorldManager(800, 600, 600, 600);
        manager.start();

        AbstractChunker chunker = new RandomChunker(14350, 1, manager);
        chunker.generatePolygons();

        //AbstractLandPlacer landPlacer = new PerlinIslandPlacer(manager);
        AbstractLandPlacer landPlacer = new TectonicPlacer(manager, new PerlinIslandPlacer(manager, -1, -1), 150, 0.76, 0.83);
        landPlacer.placeLand();
        manager.logSpeed("Placed land");

        AbstractHeightMapper heightMapper = new MidpointHeightMapper();
        double[][] heights = heightMapper.mapHeight(manager.getWorld());
        manager.getWorld().setHeightMap(heights);
        manager.logSpeed("Mapped elevation");
        //new Weatherer(manager.getWorld(), heights, 7000).weather();
        //new HeightNoiseColorer(heights).color(manager.getWorld());
        landPlacer.classifyBiomes();
        manager.logSpeed("Classified lakes");
        

        AbstractColorer colorer = new BiomeColorer(0, 0.99);
        colorer.color(manager.getWorld());
        manager.logSpeed("Colored world");

        AbstractRiverPlacer riverPlacer = new LakeRiverPlacer(1);
        riverPlacer.placeRivers(manager.getWorld(), heights);
        manager.logSpeed("Generated rivers");

        manager.recalcWorldImage();
        manager.showWorld();

    }

}
