package com.mason.mapgen.algorithms.colorers;

import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.awt.*;
import java.util.List;


public class VoronoiColorer implements AbstractColorer{


    @Override
    public void color(World world){
        List<Point> centroids = world.getCentroids();
        int numCentroids = centroids.size();
        for(int n=0;n<numCentroids;n++){
            centroids.get(n).setColor(new Color((int)(250D*n/numCentroids),
                    (int)(250D*n/numCentroids), (int)(250D*n/numCentroids)));
        }
        for(Point[] points : world.getMap()){
            for(Point point : points){
                point.setColor(point.getCentroid().getColor());
            }
        }
        for(Point centroid : centroids){
            centroid.setColor(Color.RED);
        }
    }

}
