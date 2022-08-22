package com.mason.mapgen.algorithms.chunkers;

import com.mason.mapgen.algorithms.Direction;
import com.mason.mapgen.components.Graph;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;

import java.awt.*;
import java.util.LinkedList;
import java.util.function.BiPredicate;

import static com.mason.libgui.utils.Utils.R;
import static java.lang.Math.*;
import static java.lang.String.format;

public abstract class AbstractChunker{


    private final int numCentroids;
    private final int lloydRelaxCount;
    protected final WorldManager manager;
    private final LinkedList<Point> centroids = new LinkedList<>();


    public AbstractChunker(int numCentroids, int lloydRelaxCount, WorldManager manager){
        this.numCentroids = numCentroids;
        this.lloydRelaxCount = lloydRelaxCount;
        this.manager = manager;
    }


    public void generatePolygons(){
        placeCentroids(manager.getMap());
        manager.logSpeed("Placed centroids");
        calculatePolygons(manager.getMap(), lloydRelaxCount>0 ? this::canLloydTakeOver : this::canTakeOver);
        manager.logSpeed("Calculated polygons");
        for(int n=1;n<=lloydRelaxCount;n++){
            reseedPolys(manager.getMap());
            manager.logSpeed(format("Reseeded polygons (%d out of %d iterations)", n, lloydRelaxCount));
            calculatePolygons(manager.getMap(), lloydRelaxCount==n ? this::canTakeOver : this::canLloydTakeOver);
            manager.logSpeed(format("Recalculated polygons (%d out of %d iterations)", n, lloydRelaxCount));
        }
        calcChunckStats(manager.getMap());
        manager.logSpeed("Calculated chunk information");
    }


    private void placeCentroids(Point[][] map){
        int n = 0, x, y;
        while(n < numCentroids){
            x = R.nextInt(map[0].length);
            y = R.nextInt(map.length);
            if(!map[y][x].isCentroid()){
                map[y][x].setCentroid(true);
                map[y][x].setSeedIndex(n);
                centroids.add(map[y][x]);
                n++;
            }
        }
    }

    private void calculatePolygons(Point[][] map, BiPredicate<Point, Point> takeOverPred){
        int searchDepth = (int)(2*(map.length+map[0].length)/sqrt(numCentroids));
        for(Point centroid : centroids){
            floodFillCentroid(map, centroid, searchDepth, takeOverPred);
        }
    }

    private void floodFillCentroid(Point[][] map, Point centroid, int searchDepth, BiPredicate<Point, Point> takeOverPred){
        if(!centroid.isCentroid()){
            throw new IllegalStateException("flood fill");
        }
        LinkedList<Point> frontier = new LinkedList<>();
        frontier.add(centroid);
        centroid.setSeed(centroid);
        centroid.setDistFromCentroid(0);
        Point current, next;

        while(!frontier.isEmpty()){
            current = frontier.remove(0);
            for(Direction dir : Direction.values()){
                if(manager.getWorld().pointWithinBounds(current.x+dir.x, current.y+dir.y) &&
                        !map[current.y+dir.y][current.x+dir.x].isCentroid() &&
                        (map[current.y+dir.y][current.x+dir.x].getSeed()==null ||
                                (!map[current.y+dir.y][current.x+dir.x].hasSameCentroid(centroid) &&
                                    takeOverPred.test(centroid, map[current.y+dir.y][current.x+dir.x])))
                ){
                    next = map[current.y+dir.y][current.x+dir.x];
                    next.setSeed(centroid);
                    next.setDistFromCentroid(current.getDistFromCentroid()+1);
                    if(next.getDistFromCentroid()<searchDepth) frontier.add(next);
                }
            }
        }
    }

    protected abstract boolean canTakeOver(Point centroid, Point point);

    protected boolean canLloydTakeOver(Point centroid, Point point){
        return point.squareDist(centroid)<point.squareDist(point.getSeed());
    }

    private void reseedPolys(Point[][] map){
        double[] xCoords = new double[numCentroids];
        double[] yCoords = new double[numCentroids];
        double[] pixelNum = new double[numCentroids];
        int n;
        for(int y=0; y<map.length; y++){
            for(int x=0; x<map[y].length; x++){
                n = map[y][x].getSeed().getSeedIndex();
                xCoords[n] += x;
                yCoords[n] += y;
                pixelNum[n]++;
            }
        }
        for(n=0; n<numCentroids; n++){
            xCoords[n] /= pixelNum[n];
            yCoords[n] /= pixelNum[n];
            swapSeed(map[(int)round(yCoords[n])][(int)round(xCoords[n])], n);
        }
        for(Point[] points : map){
            for(Point point : points){
                if(!point.isCentroid()) point.setSeed(null);
            }
        }
    }

    private void swapSeed(Point target, int seedIndex){
        target.getSeed().setCentroid(false);

        centroids.remove(target.getSeed());
        target.setCentroid(true);
        target.setSeedIndex(seedIndex);
        centroids.add(seedIndex, target);
    }

    private void calcChunckStats(Point[][] map){
        for(int n=0;n<numCentroids;n++){
            centroids.get(n).setColor(new Color((int)(250D*n/numCentroids),
                    (int)(250D*n/numCentroids), (int)(250D*n/numCentroids)));
        }
        int[] chunkSizes = new int[numCentroids];
        int[] chunkRadii = new int[numCentroids];
        Point borderSeed;
        Graph graph = manager.getGraph();
        for(Point[] points : map){
            for(Point point : points){
                chunkSizes[point.getSeed().getSeedIndex()]++;
                chunkRadii[point.getSeed().getSeedIndex()] = max(point.squareDist(point.getSeed()), chunkRadii[point.getSeed().getSeedIndex()]);
                borderSeed = checkBorder(map, point);
                if(borderSeed!=null) graph.connect(point.getSeed(), borderSeed);
            }
        }
        for(int n=0;n<numCentroids;n++){
            centroids.get(n).getSeedInfo().setChunkSize(chunkSizes[n]);
            centroids.get(n).getSeedInfo().setChunkRadius(chunkRadii[n]);
        }
        manager.getWorld().setCentroids(centroids);
    }

    private Point checkBorder(Point[][] map, Point p){
        for(Direction dir : Direction.values()){
            if(manager.getWorld().pointWithinBounds(p.x+dir.x, p.y+dir.y) && !p.getSeed().equals(map[p.y+dir.y][p.x+dir.x].getSeed()))
                return map[p.y+dir.y][p.x+dir.x].getSeed();
        }
        return null;
    }

}
