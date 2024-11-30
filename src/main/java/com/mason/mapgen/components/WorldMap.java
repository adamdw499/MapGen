package com.mason.mapgen.components;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.mason.libgui.utils.Utils.R;

public class WorldMap implements Iterable<Point>{


    private final Point[][] map;
    private final List<Point> centroids = new LinkedList<>();
    private final Graph chunkGraph;


    public WorldMap(int width, int height){
        if(width <= 1 || height <= 1){
            throw new IllegalArgumentException("World map too small.");
        }

        map = new Point[height][width];
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                map[y][x] = new Point(x, y);
            }
        }
        chunkGraph = new Graph(width, height);
    }


    @Override
    public Iterator<Point> iterator(){
        return new Iterator<Point>(){

            int x = -1, y = 0;

            @Override
            public boolean hasNext(){
                return y < map.length || x < map[0].length-1;
            }

            @Override
            public Point next(){
                x++;
                if(x == map[0].length){
                    x = 0;
                    y++;
                }
                return map[y][x];
            }

        };
    }

    public Iterable<Point> cardinals(Point p){
        return () -> new Iterator<>(){

            int i = (p.x == 0) ? (p.y == 0 ? 1 : 0) : (p.y == 0 ? 0 : -1); //skips the left if necessary
            private final static int[][] dir = {{0, -1}, {-1, 0}, {1, 0}, {0, 1}};

            @Override
            public boolean hasNext(){
                return i < 3;
            }

            @Override
            public Point next(){
                i++;
                while(!withinMap(p.y + dir[i][1], p.x + dir[i][0])) i++;
                return map[p.y + dir[i][1]][p.x + dir[i][0]];
            }

        };
    }

    public int getWidth(){
        return map[0].length;
    }

    public int getHeight(){
        return map.length;
    }

    public Graph getChunkGraph(){
        return chunkGraph;
    }

    public List<Point> getCentroids(){
        return centroids;
    }

    public Point getRandomPoint(){
        return map[R.nextInt(map.length)][R.nextInt(map[0].length)];
    }

    public Point getPoint(int x, int y){
        return map[y][x];
    }

    public boolean withinMap(int x, int y){
        return x>=0 && y>=0 && x<map[0].length && y<map.length;
    }

    public void setElevation(double[][] heightMap){
        for(Point p : this) p.setElevation(heightMap[p.y][p.x]);
    }

}
