package com.mason.mapgen.core;

import com.mason.libgui.components.misc.LoadingMessage;
import com.mason.libgui.components.panes.ScrollablePane;
import com.mason.libgui.core.GUIManager;
import com.mason.libgui.utils.SpeedLogger;
import com.mason.libgui.utils.StyleInfo;
import com.mason.libgui.utils.UIAligner;
import com.mason.mapgen.components.Graph;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.awt.*;

public class WorldManager extends GUIManager{


    private final ScrollablePane viewPane;
    private final World world;
    private final LoadingMessage loadingMessage;
    private final SpeedLogger speedLogger;


    public WorldManager(int width, int height, int worldWidth, int worldHeight){
        super(width, height, "Map Generator");
        viewPane = new ScrollablePane(StyleInfo.DEFAULT_STYLE_INFO, 0, 0, worldWidth, worldHeight,
                width, height, false);
        loadingMessage = new LoadingMessage(Color.WHITE, 0, 0, 80);
        speedLogger = new SpeedLogger(loadingMessage);
        speedLogger.start();
        super.addComponent(viewPane);
        super.addComponent(loadingMessage, UIAligner.Position.MIDDLE, UIAligner.Position.MIDDLE);
        world = new World(worldWidth, worldHeight);
    }


    public World getWorld(){
        return world;
    }

    public Point[][] getMap(){
        return world.getMap();
    }

    public Graph getGraph(){
        return world.getGraph();
    }

    public void recalcWorldImage(){
        world.generateImage();
    }

    public void showWorld(){
        removeComponent(loadingMessage);
        viewPane.addComponent(world);
    }

    public void logSpeed(String message){
        speedLogger.log(message);
    }

}
