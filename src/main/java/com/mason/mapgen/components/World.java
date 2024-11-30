package com.mason.mapgen.components;

import com.mason.libgui.core.UIComponent;
import com.mason.libgui.utils.noise.MidpointDisplacementNoise;
import com.mason.libgui.utils.noise.PerlinNoise;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;


public class World extends UIComponent{


    private final int worldWidth, worldHeight;

    private final WorldMap worldMap;
    private final WorldImage worldImage;
    private final WorldTexture worldTexture;


    public World(int width, int height){
        super(0, 0, width, height);
        worldWidth = width;
        worldHeight = height;
        worldMap = new WorldMap(width, height);
        worldImage = new WorldImage(width, height);
        worldTexture = new WorldTexture(width, height);
    }


    @Override
    public void render(Graphics2D g){
        worldImage.drawImage(g, x, y);
    }

    @Override
    public void tick(int mx, int my){
        //empty
    }


    /*public double[][] getHeightMap(){
        return heightMap;
    }*/

    public double[][] getMoistureTextureMap(){
        return moistureTextureMap;
    }

    /*public void setHeightMap(double[][] map){
        heightMap = map;
    }*/

    /*public List<Point> getCentroids(){
        return centroids;
    }*/

    /*public void setCentroids(List<Point> centroids){
        this.centroids = centroids;
    }*/

    /*public Graph getGraph(){
        return graph;
    }*/

    /*public boolean pointWithinBounds(int x, int y){
        return x>=0 && y>=0 && y<map.length && x<map[y].length;
    }*/

    /*public void resetTraversals(){
        for(Point[] line : map){
            for(Point point : line){
                point.resetTraversal();
            }
        }
    }*/

    public WorldMap getMap(){
        return worldMap;
    }

}
