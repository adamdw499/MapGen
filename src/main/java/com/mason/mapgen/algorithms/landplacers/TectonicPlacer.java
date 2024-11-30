package com.mason.mapgen.algorithms.landplacers;

import com.mason.mapgen.components.Graph.Edge;
import com.mason.mapgen.components.Graph.Vertex;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.WorldMap;
import com.mason.mapgen.core.WorldManager;

import java.util.HashMap;
import java.util.LinkedList;

import static com.mason.libgui.utils.Utils.R;

public class TectonicPlacer extends AbstractLandPlacer{


    private final HashMap<Vertex, Vertex> plateMap = new HashMap<>();
    private final AbstractLandPlacer subPlacer;
    private final int numPlates;


    public TectonicPlacer(WorldMap map, AbstractLandPlacer subPlacer, int numPlates, double lakeMoistureCutoff, double moistureDecay){
        super(map, lakeMoistureCutoff, moistureDecay);
        this.subPlacer = subPlacer;
        this.numPlates = numPlates;

        chunkPolys();
    }

    @Override
    public boolean centroidIsLand(Point centroid){
        return subPlacer.centroidIsLand(plateMap.get(map.getChunkGraph().toVertex(centroid)).point);
    }

    @Override
    public double[][] getPreliminaryHeightMap(){
        return subPlacer.getPreliminaryHeightMap();
    }


    private void chunkPolys(){
        LinkedList<Vertex> plates = getPlates();
        LinkedList<Vertex> frontier = new LinkedList<>();
        Vertex cur;

        for(Vertex v : plates){
            frontier.add(v);
            while(!frontier.isEmpty()){
                cur = frontier.remove(0);
                for(Edge e : cur.edges()){
                    if(canTakeOver(e.b(), getPlate(cur))){
                        frontier.add(e.b());
                        plateMap.put(e.b(), getPlate(cur));
                    }
                }
            }
        }
    }

    private LinkedList<Vertex> getPlates(){
        LinkedList<Vertex> plates = new LinkedList<>();
        LinkedList<Vertex> takeFrom = new LinkedList<>(manager.getGraph().vertices());
        Vertex v;

        for(int n=0; n<numPlates; n++){
            v = takeFrom.remove(R.nextInt(takeFrom.size()));
            plateMap.put(v, v);
            plates.add(v);
        }
        return plates;
    }

    private boolean isPlated(Vertex v){
        return plateMap.containsKey(v);
    }

    private boolean isPlate(Vertex v){
        return plateMap.get(v).equals(v);
    }

    private boolean canTakeOver(Vertex v, Vertex plate){
        if(!isPlated(v)) return true;
        else if(isPlate(v) || getPlate(v).equals(plate)) return false;
        else{
            int dist = v.point.squareDist(getPlate(v).point);
            return R.nextDouble()*(v.point.squareDist(plate.point)+dist) < dist;
        }
    }

    private Vertex getPlate(Vertex v){
        return plateMap.get(v);
    }

}
