package com.mason.mapgen.algorithms.chunkers;

import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;

public class VoronoiChunker extends AbstractChunker {


    public VoronoiChunker(int numCentroids, int lloydRelaxCount, WorldManager manager){
        super(numCentroids, lloydRelaxCount, manager);
    }


    @Override
    protected boolean canTakeOver(Point centroid, Point point){
        return canLloydTakeOver(centroid, point);
    }

}
