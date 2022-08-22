package com.mason.mapgen.components;

import java.awt.*;

import static com.mason.libgui.utils.Utils.R;
import static java.lang.Math.abs;

public class Point{


    public final int x, y;
    private Color color;
    private boolean centroid = false;
    private Point seed;
    private CentroidInfo centroidInfo;
    private int distFromCentroid = -1;
    private boolean traversed = false;
    private boolean river = false;


    public Point(int x, int y){
        this.x = x;
        this.y = y;
        color = new Color(x%250, y%250, 125*((x+y)%2));
    }


    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public void setCentroid(boolean centroid){
        this.centroid = centroid;
        if(centroid){
            setSeed(this);
            centroidInfo = new CentroidInfo();
        }else{
            centroidInfo = null;
        }
    }

    public boolean isCentroid(){
        return centroid;
    }


    public int squareDist(Point p){
        return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y);
    }

    public int manhattanDist(Point p){
        return abs(p.x - x) + abs(p.y - y);
    }

    public int minRandomDist(Point a, Point b){
        return minRandomDist(a, b, 0.3);
    }

    public int minRandomDist(Point a, Point b, double prop){
        return Double.compare(squareDist(a)*(1-prop + prop*R.nextDouble()),
                squareDist(b)*(1-prop + prop*R.nextDouble()));
    }

    public int minSquareDist(Point a, Point b){
        return Double.compare(squareDist(a), squareDist(b));
    }

    public int minManhattanDist(Point a, Point b){
        return Double.compare(manhattanDist(a), manhattanDist(b));
    }


    public void setSeed(Point seed){
        this.seed = seed;
    }

    public Point getSeed(){
        return seed;
    }

    public int getSeedIndex(){
        return centroidInfo.getIndex();
    }

    public void setSeedIndex(int index){
        centroidInfo.setIndex(index);
    }

    public boolean hasSameCentroid(Point p){
        return seed.equals(p.seed);
    }

    public void setDistFromCentroid(int distFromCentroid){
        this.distFromCentroid = distFromCentroid;
    }

    public int getDistFromCentroid(){
        return distFromCentroid;
    }

    public CentroidInfo getSeedInfo(){
        return centroid ? centroidInfo : seed.centroidInfo;
    }

    public boolean hasSameBiome(Point p){
        return p.hasBiome(seed.getSeedInfo().getBiome());
    }

    public boolean isTraversed(){
        return traversed;
    }

    public void traverse(){
        traversed = true;
    }

    public void resetTraversal(){
        traversed = false;
    }

    public boolean hasBiome(Biome biome){
        return getSeedInfo().getBiome().equals(biome);
    }
    
    public boolean isLand(){
        return getSeedInfo().getBiome().isLand();
    }

    public boolean isRiver(){
        return river;
    }

    public void setRiver(boolean river, World world){
        this.river = river;
        if(river) setColor(Biome.RIVER.getColor(world, this));
    }

}
