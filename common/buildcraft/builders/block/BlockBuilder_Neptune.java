/* Copyright (c) 2016 AlexIIL and the BuildCraft team
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package buildcraft.builders.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import buildcraft.api.enums.EnumBlueprintType;
import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.lib.block.BlockBCTile_Neptune;

public class BlockBuilder_Neptune extends BlockBCTile_Neptune {
    public static final IProperty<EnumFacing> PROP_FACING = BuildCraftProperties.BLOCK_FACING;
    public static final IProperty<EnumBlueprintType> PROP_BPT = BuildCraftProperties.BLUEPRINT_TYPE;

    public BlockBuilder_Neptune(Material material, String id) {
        super(material, id);
        IBlockState state = getDefaultState();
        state = state.withProperty(PROP_FACING, EnumFacing.NORTH);
        state = state.withProperty(PROP_BPT, EnumBlueprintType.NONE);
        setDefaultState(state);
    }

    // IBlockState

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROP_FACING, PROP_BPT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        meta |= state.getValue(PROP_FACING).getHorizontalIndex() & 3;
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        state = state.withProperty(PROP_FACING, EnumFacing.getHorizontal(meta & 3));
        return state;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        EnumFacing facing = state.getValue(PROP_FACING);
        state = state.withProperty(PROP_FACING, rot.rotate(facing));
        return state;
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        EnumFacing facing = state.getValue(PROP_FACING);
        state = state.withProperty(PROP_FACING, mirror.mirror(facing));
        return state;
    }

    // Others

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing orientation = placer.getHorizontalFacing();
        world.setBlockState(pos, state.withProperty(PROP_FACING, orientation.getOpposite()));
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

}
