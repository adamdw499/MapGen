package com.mason.mapgen.components;

import com.mason.libgui.core.UIComponent;
import com.mason.libgui.utils.noise.MidpointDisplacementNoise;
import com.mason.libgui.utils.noise.PerlinNoise;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;


public class World extends UIComponent{


    private final Point[][] map;
    private BufferedImage image;
    private List<Point> centroids;
    private final Graph graph;
    private double[][] heightMap;
    private double[][] stoneTextureMap;
    private double[][] moistureTextureMap;


    public World(int width, int height){
        super(0, 0, width, height);
        map = new Point[height][width];
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                map[y][x] = new Point(x, y);
            }
        }
        graph = new Graph(width, height);
        heightMap = new double[height][width];
        stoneTextureMap = new double[height][width];
        moistureTextureMap = new double[height][width];
        new MidpointDisplacementNoise(0.5, 0.9, false).apply(stoneTextureMap);
        PerlinNoise pn = new PerlinNoise(width, height, 2, 7, 0.65, 0.85);
        pn.apply(moistureTextureMap);
        pn.normalise(moistureTextureMap);
        generateImage();
    }


    @Override
    public void render(Graphics2D g){
        g.drawImage(image, null, 0, 0);
    }


    public void generateImage(){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        int[] pixel = new int[]{0,0,0,255};
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                colorToPixel(pixel, map[y][x].getColor());
                raster.setPixel(x, y, pixel);
            }
        }
    }

    private void colorToPixel(int[] pixel, Color color){
        pixel[0] = color.getRed();
        pixel[1] = color.getGreen();
        pixel[2] = color.getBlue();
    }

    public Point[][] getMap(){
        return map;
    }

    public double[][] getHeightMap(){
        return heightMap;
    }

    public double[][] getMoistureTextureMap(){
        return moistureTextureMap;
    }

    public void setHeightMap(double[][] map){
        heightMap = map;
    }


    public double[][] getStoneTextureMap(){
        return stoneTextureMap;
    }

    public List<Point> getCentroids(){
        return centroids;
    }

    public void setCentroids(List<Point> centroids){
        this.centroids = centroids;
    }

    public Graph getGraph(){
        return graph;
    }

    public boolean pointWithinBounds(int x, int y){
        return x>=0 && y>=0 && y<map.length && x<map[y].length;
    }

    public void resetTraversals(){
        for(Point[] line : map){
            for(Point point : line){
                point.resetTraversal();
            }
        }
    }

}
