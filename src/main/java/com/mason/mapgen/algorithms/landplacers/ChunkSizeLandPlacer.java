package com.mason.mapgen.algorithms.landplacers;

import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;

public class ChunkSizeLandPlacer extends AbstractLandPlacer{


    private final double landProportion;


    public ChunkSizeLandPlacer(WorldManager manager, double landProportion, double lakeMoistureCutoff, double moistureDecay){
        super(manager, lakeMoistureCutoff, moistureDecay);
        this.landProportion = landProportion;
    }


    /*@Override
    public void placeLand(){
        List<Point> centroids = manager.getWorld().getCentroids();
        centroids.sort(ChunkSizeLandPlacer::chunkSizeComparator);
        Point[][] map = manager.getMap();
        double currentLand = 0, targetPixels = map[0].length * map.length * landProportion;
        for(Point centroid : centroids){
            centroid.getSeedInfo().setBiome(Biome.LAND);
            currentLand += centroid.getSeedInfo().getChunkSize();
            if(currentLand > targetPixels) break;
        }
    }*/

    @Override
    public boolean centroidIsLand(Point centroid){
        return false;
    }

    private static int chunkSizeComparator(Point a, Point b){
        return Integer.compare(a.getSeedInfo().getChunkSize(), b.getSeedInfo().getChunkSize());
    }

}
