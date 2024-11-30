package com.mason.mapgen.components;

import com.mason.libgui.utils.Utils;

public class CentroidInfo{


    private Biome biome = Biome.OCEAN;
    private double elevation = -1;
    private double moisture = -1;
    private boolean beach = false;


    public CentroidInfo(){}


    public Biome getBiome(){
        return biome;
    }

    public void setBiome(Biome biome){
        this.biome = biome;
    }

    public double getElevation(){
        return elevation;
    }

    @Utils.Unfinished("What is the oof?")
    public void setElevation(double elevation){
        this.elevation = elevation;
        if(elevation>1) System.out.println("OOF");
    }

    public double getMoisture(){
        return moisture;
    }

    public void setMoisture(double moisture){
        this.moisture = moisture;
    }

    public void setBeach(boolean beach){
        this.beach = beach;
    }

    public boolean isBeach(){
        return beach;
    }

}
