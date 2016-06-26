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
package buildcraft.core.item;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.lib.item.ItemBC_Neptune;

import gnu.trove.map.hash.TIntObjectHashMap;

public class ItemPaintbrush_BC8 extends ItemBC_Neptune {
    private static final String DAMAGE = "damage";
    private static final int MAX_USES = 64;

    public ItemPaintbrush_BC8(String id) {
        super(id);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 17; i++) {
            subItems.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addModelVariants(TIntObjectHashMap<ModelResourceLocation> variants) {
        addVariant(variants, 0, "clean");
        for (EnumDyeColor colour : EnumDyeColor.values()) {
            addVariant(variants, colour.getMetadata() + 1, colour.getName());
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Brush brush = new Brush(stack);
        if (brush.useOnBlock(world, pos, world.getBlockState(pos), facing)) {
            ItemStack newStack = brush.save(stack);
            if (newStack != null) {
                player.setHeldItem(hand, newStack);
            }
            // We just changed the damage NBT value
            player.inventoryContainer.detectAndSendChanges();
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Brush brush = new Brush(stack);
        String colourComponent = "";
        if (brush.colour != null) {
            String colourLocale = I18n.translateToLocal(brush.colour.getUnlocalizedName());
            colourComponent = " - " + StringUtils.capitalize(colourLocale);
        }
        return super.getItemStackDisplayName(stack) + colourComponent;
    }

    @Override
    public int getDamage(ItemStack stack) {
        Brush brush = new Brush(stack);
        return MAX_USES - brush.usesLeft;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // Explicitly disallow this- some core use cases mistake this for metadata and fail
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        Brush brush = new Brush(stack);
        return brush.colour != null && brush.usesLeft < MAX_USES;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return isDamaged(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        Brush brush = new Brush(stack);
        return 1 - (brush.usesLeft / (double) MAX_USES);
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return super.getDamage(stack);
    }

    /** Delegate class for handling */
    public class Brush {
        public EnumDyeColor colour;
        public int usesLeft;

        public Brush(EnumDyeColor colour) {
            this.colour = colour;
            usesLeft = MAX_USES;
        }

        public Brush(ItemStack stack) {
            int meta = stack.getMetadata();
            if (meta > 0 && meta <= 16) {
                colour = EnumDyeColor.byMetadata(meta - 1);
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt == null) {
                    usesLeft = MAX_USES;
                } else {
                    usesLeft = MAX_USES - nbt.getByte(DAMAGE);
                }
            } else {
                usesLeft = 0;
            }
        }

        public ItemStack save() {
            return save(null);
        }

        public ItemStack save(ItemStack existing) {
            ItemStack stack = existing;
            if (existing == null || existing.getMetadata() != getMeta()) {
                stack = new ItemStack(ItemPaintbrush_BC8.this, 1, getMeta());
            }
            if (usesLeft != MAX_USES && colour != null) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt == null) {
                    nbt = new NBTTagCompound();
                    stack.setTagCompound(nbt);
                }
                nbt.setByte(DAMAGE, (byte) (MAX_USES - usesLeft));
            }
            return stack == existing ? null : stack;
        }

        public int getMeta() {
            return (usesLeft <= 0 || colour == null) ? 0 : colour.getMetadata() + 1;
        }

        public boolean useOnBlock(World world, BlockPos pos, IBlockState state, EnumFacing side) {
            if (colour == null || usesLeft <= 0) return false;
            Block block = state.getBlock();
            if (block.recolorBlock(world, pos, side, colour)) {
                usesLeft--;
                if (usesLeft <= 0) colour = null;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "[" + usesLeft + " of " + (colour == null ? "nothing" : colour.getName()) + "]";
        }
    }
}
