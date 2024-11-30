package com.mason.mapgen.components;

import com.mason.libgui.core.UIComponent;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Graph extends UIComponent{


    private final LinkedList<Vertex> vertices = new LinkedList<>();
    private final HashMap<Point, Vertex> vertexMap = new HashMap<>();


    public Graph(int w, int h){
        super(0, 0, w, h);
    }


    public record Edge(Vertex a, Vertex b){


        public boolean isTraversed(){
            return b.traversed;
        }

    }


    public static class Vertex{


        public final Point point;
        private final LinkedList<Edge> edges = new LinkedList<>();
        private boolean traversed = false;


        public Vertex(Point point){
            this.point = point;
        }


        public void traverse(){
            traversed = true;
        }

        public void reset(){
            traversed = false;
        }

        public boolean isTraversed(){
            return traversed;
        }

        public void connectTo(Vertex b){
            edges.add(new Edge(this, b));
        }

        public boolean isConnectedTo(Vertex b){
            return edges.stream().anyMatch(edge -> b.equals(edge.b));
        }

        public LinkedList<Edge> edges(){
            return edges;
        }

    }


    public void traverse(Vertex start, Predicate<Point> search, Consumer<Point> action){
        LinkedList<Vertex> frontier = new LinkedList<>();
        frontier.add(start);
        traverse(frontier, search, action);
    }

    public void traverse(LinkedList<Vertex> frontier, Predicate<Point> search, Consumer<Point> action){
        for(Vertex v : frontier) v.traverse();
        Vertex current;
        while(!frontier.isEmpty()){
            current = frontier.remove(0);
            action.accept(current.point);
            for(Edge edge : current.edges) if(search.test(edge.b.point) && !edge.isTraversed()){
                frontier.add(edge.b);
                edge.b.traverse();
            }
        }
        reset();
    }

    public void traverseLand(Vertex start, Consumer<Point> action){
        traverse(start, Point::isLand, action);
    }

    public void reset(){
        for(Vertex v : vertices) v.reset();
    }

    public void add(Point p){
        Vertex v = new Vertex(p);
        vertices.add(v);
        vertexMap.put(p, v);
    }

    public boolean contains(Point p){
        return vertexMap.containsKey(p);
    }

    public boolean isConnected(Point a, Point b){
        return vertexMap.get(a).isConnectedTo(vertexMap.get(b));
    }

    public void connect(Point a, Point b){
        if(!contains(a)) add(a);
        if(!contains(b)) add(b);
        if(!isConnected(a, b)) vertexMap.get(a).connectTo(vertexMap.get(b));
        if(!isConnected(b, a)) vertexMap.get(b).connectTo(vertexMap.get(a));
    }

    public Vertex toVertex(Point p){
        return vertexMap.get(p);
    }

    public LinkedList<Vertex> vertices(){
        return vertices;
    }


    @Override
    public void render(Graphics2D g){
        g.setColor(Color.PINK);
        for(Vertex v : vertices){
            g.fillOval(v.point.x-3, v.point.y-3, 6, 6);
            for(Edge edge : v.edges){
                g.drawLine(edge.a.point.x, edge.a.point.y,
                        edge.b.point.x, edge.b.point.y);
            }
        }
    }

    @Override
    public void tick(int mx, int my){
        //empty
    }

}
