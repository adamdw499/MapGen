package com.mason.mapgen.algorithms.riverplacers;

import com.mason.mapgen.components.World;

public interface AbstractRiverPlacer{


    void placeRivers(World world, double[][] heights);

}
