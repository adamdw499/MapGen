package com.mason.mapgen.core;

import com.mason.libgui.components.misc.LoadingMessage;
import com.mason.libgui.components.panes.PannablePane;
import com.mason.libgui.core.GUIManager;
import com.mason.libgui.utils.SpeedLogger;
import com.mason.libgui.utils.StyleInfo;
import com.mason.libgui.utils.UIAligner;
import com.mason.mapgen.components.Graph;
import com.mason.mapgen.components.Point;
import com.mason.mapgen.components.World;

import java.awt.*;

public class WorldManager extends GUIManager{


    private final PannablePane viewPane;
    private final World world;
    private final LoadingMessage loadingMessage;
    private final SpeedLogger speedLogger;


    public WorldManager(int width, int height, int worldWidth, int worldHeight){
        super(width, height, "Map Generator");
        viewPane = new PannablePane(StyleInfo.DEFAULT_STYLE_INFO, 0, 0, worldWidth, worldHeight,
                width, height, true);
        loadingMessage = new LoadingMessage(Color.WHITE, 0, 0, 80);
        speedLogger = new SpeedLogger(loadingMessage);
        addComponent(viewPane);
        addComponent(loadingMessage, UIAligner.Position.MIDDLE, UIAligner.Position.MIDDLE);

        speedLogger.start();
        world = new World(worldWidth, worldHeight);
        speedLogger.log("Initialised world.");
    }


    public World getWorld(){
        return world;
    }

    public Graph getGraph(){
        return world.getGraph();
    }

    public void recalcWorldImage(){
        world.generateImage();
    }

    public void showWorld(){
        removeComponent(loadingMessage);
        viewPane.addToBackground(world);
    }

    public void logSpeed(String message){
        speedLogger.log(message);
    }

    public SpeedLogger getSpeedLogger(){
        return speedLogger;
    }

}
