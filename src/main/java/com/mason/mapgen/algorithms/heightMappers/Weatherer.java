package com.mason.mapgen.algorithms.heightMappers;

import com.mason.mapgen.components.World;

import static com.mason.libgui.utils.Utils.R;

public class Weatherer{


    private final double[][] heights;
    private final int iterations;
    private final World world;


    public Weatherer(World world, double[][] heights, int iterations){
        this.heights = heights;
        this.world = world;
        this.iterations = iterations;
    }


    public void weather(){
        RainDrop rd;
        for(int n=0; n<iterations; n++){
            rd = new RainDrop(50);
            while(rd.alive()) rd.tick();
        }
    }


    private class RainDrop{


        private int x, y;
        private double sediment;
        private double momentum;
        private int life;


        public RainDrop(int life){
            this.x = R.nextInt(heights[0].length);
            this.y = R.nextInt(heights.length);
            this.life = life;
        }


        public void tick(){
            life--;
            double old = heights[y][x];
            Direction d = move();
            if(d == null){
                life = 0;
            }else{
                double newMom = (old - heights[y+d.y][x+d.x]) * (sediment);
                if(momentum <= newMom){
                    heights[y][x] -= (old - heights[y+d.y][x+d.x]) * (0.2D);
                    sediment += (old - heights[y+d.y][x+d.x]) * (0.2D);
                }else{
                    heights[y][x] += (old - heights[y+d.y][x+d.x]) * (0.1D);
                    sediment -= (old - heights[y+d.y][x+d.x]) * (0.1D);
                }
                x += d.x;
                y += d.y;
                momentum = newMom;
            }
            if(life == 0){
                end();
            }
        }

        private Direction move(){
            double min = heights[y][x];
            Direction dir = null;
            for(Direction d : Direction.values()){
                if(world.pointWithinBounds(x+d.x, y+d.y) && heights[y+d.y][x+d.x] < min){
                    dir = d;
                    min = heights[y+d.y][x+d.x];
                }
            }
            return dir;
        }

        private boolean alive(){
            return life > 0;
        }

        private void end(){
            for(Direction d : Direction.values()) if(world.pointWithinBounds(x+d.x, y+d.y)){
                heights[y+d.y][x+d.x] += sediment/5D;
            }
            heights[y][x] += sediment/5D;
            //world.getMap()[y][x].setColor(Color.RED);
            //world.getMap()[y][x].traverse();
        }

    }

}
