package com.mason.mapgen.components;

import com.mason.libgui.utils.Utils;

import java.awt.*;
import java.util.Objects;

import static com.mason.libgui.utils.Utils.R;
import static java.lang.Math.abs;

public class Point{


    public final int x, y;
    private Color color;
    private Point centroid;
    private CentroidInfo centroidInfo;
    private int distFromCentroid = -1;
    private boolean traversed = false;
    private boolean river = false;
    private double elevation = -1;


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


    public void grantCentroidStatus(){
        setCentroid(this);
        centroidInfo = new CentroidInfo();
    }

    public void revokeCentroidStatus(){
        centroidInfo = null;
    }

    public boolean isCentroid(){
        return centroidInfo != null;
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


    public void setCentroid(Point centroid){
        this.centroid = centroid;
    }

    public boolean hasCentroid(){
        return centroid != null;
    }

    public Point getCentroid(){
        return centroid;
    }

    public boolean sharesCentroid(Point p){
        return centroid.equals(p.centroid);
    }

    public void setDistFromCentroid(int distFromCentroid){
        this.distFromCentroid = distFromCentroid;
    }

    public int getDistFromCentroid(){
        return distFromCentroid;
    }

    public void setElevation(double e){
        elevation = e;
    }

    public double getElevation(){
        return elevation;
    }

    public CentroidInfo centroidInfo(){
        return isCentroid() ? centroidInfo : centroid.centroidInfo;
    }

    public boolean hasSameBiome(Point p){
        return p.hasBiome(centroid.centroidInfo().getBiome());
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
        return centroidInfo().getBiome().equals(biome);
    }
    
    public boolean isLand(){
        return centroidInfo().getBiome().isLand();
    }

    public boolean isRiver(){
        return river;
    }

    @Utils.Unfinished("Review")
    public void setRiver(boolean river, World world){
        this.river = river;
        if(river) setColor(Biome.RIVER.getColor(world, this));
    }


    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

}
