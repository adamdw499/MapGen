package com.mason.mapgen.algorithms.landplacers;

import com.mason.mapgen.components.Biome;
import com.mason.mapgen.components.Graph;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.core.WorldManager;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLandPlacer{


    protected final WorldManager manager;


    public AbstractLandPlacer(WorldManager manager){
        this.manager = manager;
    }


    public abstract void placeLand();


    public void classifyBiomes(){
        //Object declaration
        Graph graph = manager.getGraph();
        Point[][] map = manager.getMap();
        LinkedList<Graph.Vertex> frontier = getOceanCorners(graph, map);

        //Separating oceans and lakes
        manager.getWorld().getCentroids().stream().filter(p -> !p.isLand())
                .forEach(p -> p.getSeedInfo().setBiome(Biome.LAKE));
        graph.traverse(frontier, (p) -> p.hasBiome(Biome.LAKE), p -> {
            p.getSeedInfo().setBiome(Biome.OCEAN);
        });

        //Classifying beaches
        classifyBeaches(graph, map);

        //Randomising moisture
        double[][] moisture = manager.getWorld().getMoistureTextureMap();
        manager.getWorld().getCentroids().stream().filter(p -> p.isLand()).forEach(p -> {
            p.getSeedInfo().setMoisture(moisture[p.y][p.x]);
            if(!p.getSeedInfo().isBeach() && moisture[p.y][p.x] > 0.7) p.getSeedInfo().setBiome(Biome.LAKE);
        });

        //Redistributing moisture
        calculateMoisture(graph, 0.86);
        manager.getWorld().getCentroids().stream().map(Point::getSeedInfo).filter(p -> p.getBiome().isLand()).forEach(p -> {
            p.setBiome(Biome.classify(p.getElevation(), p.getMoisture()));
        });
    }

    private void calculateMoisture(Graph graph, double moistureDecay){
        List<Graph.Vertex> frontier = graph.vertices().stream().filter(v -> v.point.hasBiome(Biome.LAKE)).collect(Collectors.toList());
        for(Graph.Vertex v : frontier){
            v.point.getSeedInfo().setMoisture(1);
        }
        Graph.Vertex current;
        while(!frontier.isEmpty()){
            current = frontier.remove(0);
            for(Graph.Edge edge : current.edges()) if(edge.b().point.isLand()
                    && (edge.b().point.getSeedInfo().getMoisture()
                    < current.point.getSeedInfo().getMoisture()*moistureDecay)){
                frontier.add(edge.b());
                edge.b().point.getSeedInfo().setMoisture(current.point.getSeedInfo().getMoisture()*moistureDecay);
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
                    edge.b().point.getSeedInfo().setBeach(true);
                }else{
                    frontier.add(edge.b());
                }
            }
        }
        graph.reset();
    }

    private LinkedList<Graph.Vertex> getOceanCorners(Graph graph, Point[][] map){
        LinkedList<Graph.Vertex> frontier = new LinkedList<>();
        frontier.add(graph.toVertex(map[0][0].getSeed()));
        frontier.add(graph.toVertex(map[0][map[0].length-1].getSeed()));
        frontier.add(graph.toVertex(map[map.length-1][map[0].length-1].getSeed()));
        frontier.add(graph.toVertex(map[map.length-1][0].getSeed()));
        return frontier;
    }

}
