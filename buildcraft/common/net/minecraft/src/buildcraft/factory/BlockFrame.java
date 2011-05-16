package net.minecraft.src.buildcraft.factory;

import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BuildCraftTransport;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.core.IPipeConnection;
import net.minecraft.src.buildcraft.core.Utils;

public class BlockFrame extends Block implements IPipeConnection {	
	
	public BlockFrame(int i) {
		super(i, Material.glass);
		
		blockIndexInTexture = ModLoader.addOverride("/terrain.png",
		"/net/minecraft/src/buildcraft/factory/gui/frame.png");
		
		setHardness(0.5F);
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
    
    public int getRenderType()
    {
        return BuildCraftTransport.pipeModel;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
   		float xMin = Utils.pipeMinSize, xMax = Utils.pipeMaxSize, 
   		yMin = Utils.pipeMinSize, yMax = Utils.pipeMaxSize, 
   		zMin = Utils.pipeMinSize, zMax = Utils.pipeMaxSize;

   		if (isPipeConnected (world, i - 1, j, k)) {
   			xMin = 0.0F;
   		}

   		if (isPipeConnected (world, i + 1, j, k)) {
   			xMax = 1.0F;
   		}

   		if (isPipeConnected (world, i, j - 1, k)) {
   			yMin = 0.0F;
   		}

   		if (isPipeConnected (world, i, j + 1, k)) {
   			yMax = 1.0F;
   		}

   		if (isPipeConnected (world, i, j, k - 1)) {
   			zMin = 0.0F;
   		}

   		if (isPipeConnected (world, i, j, k + 1)) {
   			zMax = 1.0F;
   		}
       	
       	    
   		return AxisAlignedBB.getBoundingBoxFromPool((double) i + xMin,
   				(double) j + yMin, (double) k + zMin, (double) i + xMax,
   				(double) j + yMax, (double) k + zMax);
       }
   	
       public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k)
       {
           return getCollisionBoundingBoxFromPool (world, i, j, k);
       }

	@Override
	public boolean isPipeConnected(IBlockAccess blockAccess, int x, int y, int z) {
		return blockAccess.getBlockId(x, y, z) == blockID;
	}
    
}
