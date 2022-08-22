package com.mason.mapgen.components;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.mason.libgui.utils.ImageUtils.*;
import static com.mason.libgui.utils.Utils.R;
import static com.mason.libgui.utils.Utils.sigmoid;

public enum Biome{


    OCEAN((world, p) -> waterTexture(world,
            new Color(165, 182, 218), new Color(67, 73, 96),
            new Color(53, 95, 196), new Color(29, 38, 66)).apply(p), false),
    LAKE((world, p) -> texture(world.getMoistureTextureMap(), new Color(207, 235, 245), new Color(35, 109, 180)).apply(p), false),
    LAND((world, p) -> Color.GREEN, true),
    SCORCHED((world, p) -> texture(world.getStoneTextureMap(), bitmapNoise(153,153,153, 6),
            new Color(76, 82, 80)).apply(p), true),
    BARE((world, p) -> bitmapNoise(147,167,147,7), true),
    TUNDRA((world, p) -> sprinkle(texture(world.getStoneTextureMap(), bitmapNoise(106,73,46, 10),
            new Color(111,122,120)).apply(p), new Color(21, 68, 0), 0.03), true),
    SNOW((world, p) -> sprinkle(bitmapNoise(250,250,250, 8), new Color(10, 60, 10), 0.01), true),
    TEMPERATE_DESERT((world, p) -> sprinkle(texture(world.getMoistureTextureMap(), bitmapNoise(231, 209, 174,5), bitmapNoise(252, 173, 60,4)).apply(p),
            bitmapNoise(93, 155, 93,5), 0.01), true),
    SHRUBLAND((world, p) -> texture(world.getStoneTextureMap(), bitmapNoise(105, 50, 17,7), bitmapNoise(84,85,33,7)).apply(p), true),
    TAIGA((world, p) -> sprinkle(texture(world.getStoneTextureMap(), bitmapNoise(233,167,27,6), bitmapNoise(46,92,90,4)).apply(p),
            bitmapNoise(21,60,74,6), 0.1), true),
    GRASSLAND((world, p) -> bitmapNoise(texture(world.getStoneTextureMap(), Color.decode("#193200"), Color.decode("#3e7e00")).apply(p), 4), true),
    TEMPERATE_DECIDUOUS_FOREST((world, p) -> texture(world.getStoneTextureMap(), bitmapNoise(107,112,66,12), bitmapNoise(117,113,46,10)).apply(p), true),
    TEMPERATE_RAINFOREST((world, p) -> sprinkle(texture(world.getStoneTextureMap(), bitmapNoise(50,83,36,3), bitmapNoise(98,108,11,4)).apply(p),
            bitmapNoise(69,57,44,6), 0.06), true),
    SUBTROPICAL_DESERT((world, p) -> sprinkle(texture(world.getStoneTextureMap(), new Color(233, 221, 199),
            new Color(211,122,54)).apply(p), bitmapNoise(31, 68, 0,5), 0.02), true),
    TROPICAL_SEASONAL_FOREST((world, p) -> sprinkle(texture(world.getStoneTextureMap(), bitmapNoise(47,73,20,4), bitmapNoise(121,127,103,4)).apply(p),
            bitmapNoise(80,69,56,5), 0.05), true),
    TROPICAL_RAIN_FOREST((world, p) -> sprinkle(texture(world.getStoneTextureMap(), new Color(1, 86, 55), new Color(31, 70, 46)).apply(p),
            bitmapNoise(64,50,33,5),0.05), true),
    RIVER((world, p) -> texture(world.getMoistureTextureMap(), new Color(207, 235, 245), new Color(35, 109, 180)).apply(p), false);




    private final BiFunction<World, Point, Color> colorer;
    private final boolean land;

    private final static double[] MOISTURE_LEVELS = new double[]{0.16, 0.33, 0.5, 0.66, 0.84};
    private final static double[] ELEVATION_LEVELS = new double[]{0.25, 0.5, 0.75};


    Biome(BiFunction<World, Point, Color> colorer, boolean land){
        this.colorer = colorer;
        this.land = land;
    }


    public Color getColor(World world, Point point){
        return colorer.apply(world, point);
    }

    public boolean isLand(){
        return land;
    }

    public static Function<Point, Color> texture(double[][] map, Color col1, Color col2){
        return p -> getDichromeColor(p.x, p.y, col1, col2, map);
    }

    public static Function<Point, Color> waterTexture(World world,
                                                      Color landCol1, Color landCol2,
                                                      Color waterCol1, Color waterCol2){
        return p -> getQuadchromeColor(p.x, p.y, world.getStoneTextureMap(), world.getMoistureTextureMap(),
                sigmoid(world.getHeightMap()[p.y][p.x]*3-0.5),
                landCol1, landCol2, waterCol1, waterCol2);
    }

    private static double step(double x){
        return (x < -0.2) ? 0 : (x < 0) ? 0.5 : 1;
    }


    public static Biome classify(double height, double moisture){
        if(height> ELEVATION_LEVELS[2]){
            if(moisture < MOISTURE_LEVELS[0]) return SCORCHED;
            else if(moisture < MOISTURE_LEVELS[1]) return BARE;
            else if(moisture < MOISTURE_LEVELS[2]) return TUNDRA;
            else return SNOW;
        }else if(height> ELEVATION_LEVELS[1]){
            if(moisture < MOISTURE_LEVELS[1]) return TEMPERATE_DESERT;
            else if(moisture < MOISTURE_LEVELS[3]) return SHRUBLAND;
            else return TAIGA;
        }else if(height> ELEVATION_LEVELS[0]){
            if(moisture < MOISTURE_LEVELS[0]) return TEMPERATE_DESERT;
            else if(moisture < MOISTURE_LEVELS[2]) return GRASSLAND;
            else if(moisture < MOISTURE_LEVELS[4]) return TEMPERATE_DECIDUOUS_FOREST;
            else return TEMPERATE_RAINFOREST;
        }else{
            if(moisture < MOISTURE_LEVELS[0]) return SUBTROPICAL_DESERT;
            else if(moisture < MOISTURE_LEVELS[1]) return GRASSLAND;
            else if(moisture < MOISTURE_LEVELS[3]) return TROPICAL_SEASONAL_FOREST;
            else return TROPICAL_RAIN_FOREST;
        }
    }

    public static Color sprinkle(Color standard, Color odd, double chance){
        if(R.nextDouble()<chance) return odd;
        else return standard;
    }

}
