package net.minecraft.src.buildcraft;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;

public class BlockPlainPipe extends Block {	
	
	public BlockPlainPipe(int i) {
		super(i, Material.glass);
		
		blockIndexInTexture = ModLoader.addOverride("/terrain.png",
		"/buildcraft_gui/plain_pipe.png");
		
		minX = Utils.pipeMinSize;
		minY = 0.0;
		minZ = Utils.pipeMinSize;
		
		maxX = Utils.pipeMaxSize;
		maxY = 1.0;
		maxZ = Utils.pipeMaxSize;
	}
    
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    public int idDropped(int i, Random random)
    {
        return 0;
    }
    
}
