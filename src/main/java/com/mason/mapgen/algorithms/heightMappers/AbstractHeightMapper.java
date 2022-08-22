package com.mason.mapgen.algorithms.heightMappers;

import com.mason.mapgen.components.World;

public interface AbstractHeightMapper{


    double[][] mapHeight(World world);

}
