package com.mason.mapgen.algorithms.colorers;

import com.mason.libgui.utils.Distribution;
import com.mason.mapgen.components.Graph;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.awt.*;

import static com.mason.libgui.utils.ImageUtils.getColorAverage;
import static java.lang.Math.*;

public class BiomeColorer implements AbstractColorer{


    private final int blurNum;
    private final double sharpness;


    public BiomeColorer(int blurNum, double sharpness){
        this.blurNum = blurNum;
        this.sharpness = sharpness;
    }


    @Override
    public void color(World world){
        for(Point[] points : world.getMap()){
            for(Point point : points){
                point.setColor(point.getSeedInfo().getBiome().getColor(world, point));
            }
        }
        //graphicBlur(world);
        for(int n = 1; n<= blurNum; n++) linearBlur(world);
    }


    private void graphicBlur(World world){
        Graph graph = world.getGraph();
        Point adjacentSeed;
        for(Point[] points : world.getMap()){
            for(Point point : points) if(point.isLand()){
                adjacentSeed = getAdjacentSeed(graph, point);
                if(adjacentSeed.isLand() && !adjacentSeed.hasSameBiome(point)){
                    point.setColor(getColorAverage(point.getColor(), adjacentSeed.getSeedInfo().getBiome().getColor(world, point),
                            getWeight(point, adjacentSeed)));
                }
            }
        }
    }

    private Point getAdjacentSeed(Graph graph, Point point){
        /*Point adjCentroid = graph.toVertex(point.getSeed()).edges().stream().map(e -> e.b().point)
                .min((p1, p2) -> compareBiomeDist(point, p1, p2)).orElseThrow();*/
        return new Distribution<>(graph.toVertex(point.getSeed()).edges(),
                edge -> exp(-edge.b().point.squareDist(point)/100D)+0.0005).get().b().point;
    }

    private static int compareBiomeDist(Point point, Point adj1, Point adj2){
        int dist = point.squareDist(adj2);
        //return Double.compare((0.5*R.nextDouble())*(point.squareDist(adj1)+dist), dist);
        return Integer.compare(point.squareDist(adj1), dist);
    }

    private double getWeight(Point p, Point adj){
        return 1.2D*min( ((double)p.squareDist(p.getSeed()))/((double)p.squareDist(adj)), 1) - 1D;
    }


    private void linearBlur(World world){
        Point[][] map = world.getMap();
        Color[][] colors = new Color[map.length][map[0].length];
        for(Point[] points : map){
            for(Point p : points){
                colors[p.y][p.x] = getGaussianColor(p.x, p.y, map, world);
            }
        }
        for(Point[] points : map){
            for(Point p : points) if(p.isLand()){
                p.setColor(colors[p.y][p.x]);
            }
        }
    }

    private Color getGaussianColor(int xp, int yp, Point[][] map, World world){
        double r=0, g=0, b=0;
        double calc;
        for(int x=-2;x<=2;x++){
            for(int y=-2;y<=2;y++) if(world.pointWithinBounds(xp+x, yp+y) /*&& map[yp+y][xp+x].isLand()*/){
                calc = getGaussianWeight(abs(x), abs(y), sharpness);
                r += map[yp+y][xp+x].getColor().getRed()*calc;
                g += map[yp+y][xp+x].getColor().getGreen()*calc;
                b += map[yp+y][xp+x].getColor().getBlue()*calc;
            }
        }
        return new Color((int)r, (int)g, (int)b);
    }

    private double getGaussianWeight(int x, int y, double coef){
        return (2D-coef*max(x, y))/(50D-40D*coef);
    }

}
