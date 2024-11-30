package com.mason.mapgen.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class WorldImage{


    private final BufferedImage image;


    public WorldImage(int width, int height){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }


    public void generateImage(WorldMap worldMap){
        WritableRaster raster = image.getRaster();
        int[] pixel = new int[]{0,0,0,255};

        for(Point p : worldMap){
            colorToPixel(pixel, p.getColor());
            raster.setPixel(p.x, p.y, pixel);
        }
    }

    private static void colorToPixel(int[] pixel, Color color){
        pixel[0] = color.getRed();
        pixel[1] = color.getGreen();
        pixel[2] = color.getBlue();
    }

    public void drawImage(Graphics2D g, int x, int y){
        g.drawImage(image, null, x, y);
    }

}
