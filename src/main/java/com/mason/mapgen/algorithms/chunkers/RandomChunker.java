package com.mason.mapgen.algorithms.chunkers;

import com.mason.mapgen.algorithms.Direction;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;

import static com.mason.libgui.utils.Utils.R;

public class RandomChunker extends AbstractChunker{


    public RandomChunker(int numCentroids, int lloydRelaxCount, WorldManager manager){
        super(numCentroids, lloydRelaxCount, manager);
    }


    @Override
    protected boolean canTakeOver(Point centroid, Point point){
        int dist = point.squareDist(point.getSeed());
        return R.nextDouble()*(point.squareDist(centroid)+dist)<dist || surrounded(manager.getMap(), centroid,
                point.x, point.y);
    }

    private boolean surrounded(Point[][] map, Point centroid, int x, int y){
        int adjacents = 0;
        for(Direction dir : Direction.values()) if(manager.getWorld().pointWithinBounds(x+dir.x, y+dir.y)
                && map[y+dir.y][x+dir.x].getSeed()!=null
                && map[y+dir.y][x+dir.x].getSeed().equals(centroid)){
            adjacents++;
        }
        return adjacents>=3;
    }

}
