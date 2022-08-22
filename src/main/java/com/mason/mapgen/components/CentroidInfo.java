package com.mason.mapgen.components;

public class CentroidInfo{


    private int index = -1;
    private int chunkSize = -1;
    private int chunkRadius = -1;
    private Biome biome = Biome.OCEAN;
    private double elevation = -1;
    private double moisture = -1;
    private boolean beach = false;


    public CentroidInfo(){}


    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getChunkSize(){
        return chunkSize;
    }

    public void setChunkSize(int chunkSize){
        this.chunkSize = chunkSize;
    }

    public Biome getBiome(){
        return biome;
    }

    public void setBiome(Biome biome){
        this.biome = biome;
    }

    public double getElevation(){
        return elevation;
    }

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

    public int getChunkRadius(){
        return chunkRadius;
    }

    public void setChunkRadius(int chunkRadius){
        this.chunkRadius = chunkRadius;
    }
}
