package net.minecraft.src;

import net.minecraft.src.buildcraft.core.Core;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.devel.BlockCheat;


public class mod_BuildCraftDevel extends BaseMod {	

	public static BlockCheat cheatBlock;
	
	public mod_BuildCraftDevel () {		
		mod_BuildCraftCore.initialize();
		
		CraftingManager craftingmanager = CraftingManager.getInstance();
		
		cheatBlock = new BlockCheat (Utils.getFirstFreeBlock());
		ModLoader.RegisterBlock(cheatBlock);
		craftingmanager.addRecipe(new ItemStack(cheatBlock, 1), new Object[] {
			"# ", "  ", Character.valueOf('#'), Block.dirt });
	}
	
	@Override
	public String Version() {
		return Core.version;
	}
}
