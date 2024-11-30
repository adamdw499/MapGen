package com.mason.mapgen.components;

import com.mason.libgui.utils.noise.MidpointDisplacementNoise;
import com.mason.libgui.utils.noise.PerlinNoise;

public class WorldTexture{


    private final double[][] stoneTextureMap;
    private final double[][] moistureTextureMap;


    public WorldTexture(int width, int height){
        stoneTextureMap = new double[height][width];
        moistureTextureMap = new double[height][width];
        new MidpointDisplacementNoise(0.5, 0.9, false).apply(stoneTextureMap);
        PerlinNoise pn = new PerlinNoise(width, height, 2, 7, 0.65, 0.85);
        pn.apply(moistureTextureMap);
        pn.normalise(moistureTextureMap);
    }


    public double[][] getMoistureTextureMap(){
        return moistureTextureMap;
    }

    public double[][] getStoneTextureMap(){
        return stoneTextureMap;
    }

}
