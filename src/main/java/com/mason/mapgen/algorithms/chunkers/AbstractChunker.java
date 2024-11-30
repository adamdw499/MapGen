package com.mason.mapgen.algorithms.chunkers;

import com.mason.libgui.utils.SpeedLogger;
import com.mason.libgui.utils.Utils;
import com.mason.mapgen.components.*;
import com.mason.mapgen.components.Point;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

import static java.lang.Math.*;
import static java.lang.String.format;

public abstract class AbstractChunker{


    private final int numCentroids;
    private final int lloydRelaxCount;
    protected final WorldMap map;


    public AbstractChunker(int numCentroids, int lloydRelaxCount, WorldMap map){
        this.numCentroids = numCentroids;
        this.lloydRelaxCount = lloydRelaxCount;
        this.map = map;
    }


    public void generatePolygons(SpeedLogger logger){
        placeCentroids();
        logger.log("Placed centroids");

        calculatePolygons(lloydRelaxCount>0 ? this::canLloydTakeOver : this::canTakeOver);
        logger.log("Calculated polygons");

        for(int n=1;n<=lloydRelaxCount;n++){
            reseedPolys();
            logger.log(format("Reseeded polygons (%d out of %d iterations)", n, lloydRelaxCount));
            calculatePolygons(lloydRelaxCount==n ? this::canTakeOver : this::canLloydTakeOver);
            logger.log(format("Recalculated polygons (%d out of %d iterations)", n, lloydRelaxCount));
        }

        connectChunks();
        logger.log("Connected chunks");
    }


    private void placeCentroids(){
        int n = 0;
        Point p;
        while(n < numCentroids){
            p = map.getRandomPoint();
            if(!p.isCentroid()){
                p.grantCentroidStatus();
                map.getCentroids().add(p);
                n++;
            }
        }
    }

    private void calculatePolygons(BiPredicate<Point, Point> takeOverPred){
        int searchDepth = (int)(2*(map.getHeight()+map.getWidth())/sqrt(numCentroids));
        for(Point centroid : map.getCentroids()){
            floodFillCentroid(centroid, searchDepth, takeOverPred);
        }
    }

    private void floodFillCentroid(Point centroid, int searchDepth, BiPredicate<Point, Point> takeOverPred){
        if(!centroid.isCentroid()){
            throw new IllegalStateException("Trying to flood fill starting from a non-centroid.");
        }
        LinkedList<Point> frontier = new LinkedList<>();
        frontier.add(centroid);
        centroid.setCentroid(centroid);
        centroid.setDistFromCentroid(0);
        Point current;

        while(!frontier.isEmpty()){
            current = frontier.remove(0);

            for(Point p : map.cardinals(current)){
                if(!p.isCentroid() &&
                        (!p.hasCentroid() ||
                                (!p.getCentroid().equals(centroid) &&
                                        takeOverPred.test(centroid, p)))){
                    p.setCentroid(centroid);
                    p.setDistFromCentroid(current.getDistFromCentroid()+1);
                    if(p.getDistFromCentroid() < searchDepth) frontier.add(p);
                }
            }
        }
    }

    protected abstract boolean canTakeOver(Point centroid, Point point);

    protected boolean canLloydTakeOver(Point centroid, Point point){
        return point.squareDist(centroid)<point.squareDist(point.getCentroid());
    }

    private void reseedPolys(){
        //Initialises a blank table with centroid keys and empty double array values.
        //0: total x in poly
        //1: total y in poly
        //2: total points in poly
        HashMap<Point, double[]> infoTable = initialisePolyInfoTable();
        double[] info;

        //fills in the table
        for(Point p : map){
            info = infoTable.get(p.getCentroid());
            info[0] += p.x;
            info[1] += p.y;
            info[2]++;
        }

        //Finds the averages and swaps the centroid.
        for(double[] i : infoTable.values()){
            i[0] /= i[2];
            i[1] /= i[2];
            swapCentroid(map.getPoint((int)round(i[0]), (int)round(i[1])));
        }

        //Clears the polys for the next lloyd iteration.
        for(Point p : map){
            if(!p.isCentroid()) p.setCentroid(null);
        }
    }

    private HashMap<Point, double[]> initialisePolyInfoTable(){
        HashMap<Point, double[]> ret = new HashMap<>();

        for(Point centroid : map.getCentroids()){
            ret.put(centroid, new double[3]);
        }

        return ret;
    }

    private void swapCentroid(Point newCentroid){
        newCentroid.getCentroid().revokeCentroidStatus();
        map.getCentroids().remove(newCentroid.getCentroid());
        newCentroid.grantCentroidStatus();
        map.getCentroids().add(newCentroid);
    }

    @Utils.Unfinished("Better to do a flood fill")
    private void connectChunks(){
        /*for(int n=0;n<numCentroids;n++){
            centroids.get(n).setColor(new Color((int)(250D*n/numCentroids),
                    (int)(250D*n/numCentroids), (int)(250D*n/numCentroids)));
        }*/
        Graph graph = map.getChunkGraph();
        for(Point p : map){
            connectChunkBorder(graph, p);
        }
    }

    private void connectChunkBorder(Graph graph, Point p){
        for(Point p2 : map.cardinals(p)){
            if(!p.sharesCentroid(p2)){
                graph.connect(p.getCentroid(), p2.getCentroid());
                break;
            }
        }
    }

}
