package com.mason.mapgen.algorithms.landplacers;

import com.mason.mapgen.components.*;
import com.mason.mapgen.core.WorldManager;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLandPlacer{


    protected final WorldMap map;
    private final double moistureDecay;
    private final double lakeMoistureCutoff;


    public AbstractLandPlacer(WorldMap map, double lakeMoistureCutoff, double moistureDecay){
        this.map = map;
        this.moistureDecay = moistureDecay;
        this.lakeMoistureCutoff = lakeMoistureCutoff;
    }


    public final void placeLand(){
        map.getCentroids().stream().filter(this::centroidIsLand)
                .forEach(p -> p.getSeedInfo().setBiome(Biome.LAND));
        map.setHeightMap(getPreliminaryHeightMap());
    }

    protected abstract boolean centroidIsLand(Point centroid);


    public void classifyBiomes(){
        //Object declaration
        Graph graph = manager.getGraph();
        Point[][] map = manager.getMap();
        LinkedList<Graph.Vertex> frontier = getOceanCorners(graph, map);

        //Separating oceans and lakes
        manager.getWorld().getCentroids().stream().filter(p -> !p.isLand())
                .forEach(p -> p.getSeedInfo().setBiome(Biome.LAKE));
        graph.traverse(frontier, (p) -> p.hasBiome(Biome.LAKE), p -> p.centroidInfo().setBiome(Biome.OCEAN));

        //Classifying beaches
        classifyBeaches(graph, map);

        //Randomising moisture
        double[][] moisture = manager.getWorld().getMoistureTextureMap();
        manager.getWorld().getCentroids().stream().filter(Point::isLand).forEach(p -> {
            p.getSeedInfo().setMoisture(moisture[p.y][p.x]);
            if(!p.getSeedInfo().isBeach() && moisture[p.y][p.x] > lakeMoistureCutoff) p.getSeedInfo().setBiome(Biome.LAKE);
        });

        //Redistributing moisture
        calculateMoisture(graph);
        manager.getWorld().getCentroids().stream().map(Point::centroidInfo).filter(p -> p.getBiome().isLand()).forEach(p ->
                p.setBiome(Biome.classify(p.getElevation(), p.getMoisture())));
    }

    private void calculateMoisture(Graph graph){
        List<Graph.Vertex> frontier = graph.vertices().stream().filter(v -> v.point.hasBiome(Biome.LAKE)).collect(Collectors.toList());
        for(Graph.Vertex v : frontier){
            v.point.centroidInfo().setMoisture(1);
        }
        Graph.Vertex current;
        while(!frontier.isEmpty()){
            current = frontier.remove(0);
            for(Graph.Edge edge : current.edges()) if(edge.b().point.isLand()
                    && (edge.b().point.centroidInfo().getMoisture()
                    < current.point.centroidInfo().getMoisture()*moistureDecay)){
                frontier.add(edge.b());
                edge.b().point.centroidInfo().setMoisture(current.point.centroidInfo().getMoisture()*moistureDecay);
            }
        }
        graph.reset();
    }

    private void classifyBeaches(Graph graph, Point[][] map){
        List<Graph.Vertex> frontier = getOceanCorners(graph, map);
        for(Graph.Vertex v : frontier){
            v.traverse();
        }
        Graph.Vertex current;
        while(!frontier.isEmpty()){
            current = frontier.remove(0);
            for(Graph.Edge edge : current.edges()) if(!edge.b().isTraversed()){
                edge.b().traverse();
                if(edge.b().point.isLand()){
                    edge.b().point.centroidInfo().setBeach(true);
                }else{
                    frontier.add(edge.b());
                }
            }
        }
        graph.reset();
    }

    private LinkedList<Graph.Vertex> getOceanCorners(Graph graph, Point[][] map){
        LinkedList<Graph.Vertex> frontier = new LinkedList<>();
        frontier.add(graph.toVertex(map[0][0].getCentroid()));
        frontier.add(graph.toVertex(map[0][map[0].length-1].getCentroid()));
        frontier.add(graph.toVertex(map[map.length-1][map[0].length-1].getCentroid()));
        frontier.add(graph.toVertex(map[map.length-1][0].getCentroid()));
        return frontier;
    }

    protected abstract double[][] getPreliminaryHeightMap();

}
