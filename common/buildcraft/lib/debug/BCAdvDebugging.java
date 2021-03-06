package buildcraft.lib.debug;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.lib.client.model.ModelUtil;
import buildcraft.lib.client.model.MutableQuad;
import buildcraft.lib.client.render.DetatchedRenderer.IDetachedRenderer;

public enum BCAdvDebugging implements IDetachedRenderer {
    INSTANCE;

    private static final MutableQuad[] smallCuboid;

    private IAdvDebugTarget target = null;
    private IAdvDebugTarget targetClient = null;

    static {
        smallCuboid = new MutableQuad[6];
        Tuple3f center = new Point3f(0.5f, 0.5f, 0.5f);
        Tuple3f radius = new Point3f(0.25f, 0.25f, 0.25f);

        for (EnumFacing face : EnumFacing.VALUES) {
            MutableQuad quad = ModelUtil.createFace(face, center, radius, null);
            quad.lightf(1, 1);
            smallCuboid[face.ordinal()] = quad;
        }
    }

    public static boolean isBeingDebugged(IAdvDebugTarget target) {
        return INSTANCE.target == target;
    }

    public static void setCurrentDebugTarget(IAdvDebugTarget target) {
        if (INSTANCE.target != null) {
            INSTANCE.target.disableDebugging();
        }
        INSTANCE.target = target;
    }

    public static void setClientDebugTarget(IAdvDebugTarget target) {
        INSTANCE.targetClient = target;
    }

    public void onServerPostTick() {
        if (target != null) {
            if (!target.doesExistInWorld()) {
                target.disableDebugging();
                target = null;
            } else {
                target.sendDebugState();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityPlayer player, float partialTicks) {
        if (targetClient == null) {
            return;
        } else if (!targetClient.doesExistInWorld()) {
            // targetClient = null;
            // return;
        }
        IDetachedRenderer renderer = targetClient.getDebugRenderer();
        if (renderer != null) {
            renderer.render(player, partialTicks);
        }
    }

    public static void renderSmallCuboid(VertexBuffer vb, BlockPos pos, int colour) {
        vb.setTranslation(pos.getX(), pos.getY(), pos.getZ());
        for (MutableQuad q : smallCuboid) {
            q.texFromSprite(ModelLoader.White.INSTANCE);
            q.colouri(colour);
            q.render(vb);
        }
        vb.setTranslation(0, 0, 0);
    }
}
