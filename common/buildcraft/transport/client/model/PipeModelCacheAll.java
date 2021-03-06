package buildcraft.transport.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;

import buildcraft.lib.client.model.IModelCache;
import buildcraft.lib.client.model.ModelCacheJoiner;
import buildcraft.lib.client.model.ModelCacheJoiner.ModelKeyWrapper;
import buildcraft.transport.client.model.PipeModelCacheBase.PipeBaseCutoutKey;
import buildcraft.transport.client.model.PipeModelCacheBase.PipeBaseTransclucentKey;
import buildcraft.transport.client.model.PipeModelCachePluggable.PluggableKey;
import buildcraft.transport.tile.TilePipeHolder;

public class PipeModelCacheAll {
    static final IModelCache<PipeAllCutoutKey> cacheCutout;
    static final IModelCache<PipeAllTranslucentKey> cacheTranslucent;

    static {
        List<ModelKeyWrapper<PipeAllCutoutKey, ?>> cutout = new ArrayList<>();
        cutout.add(new ModelKeyWrapper<>(PipeAllCutoutKey::getBaseCutout, PipeModelCacheBase.cacheCutout));
        cutout.add(new ModelKeyWrapper<>(PipeAllCutoutKey::getPluggable, PipeModelCachePluggable.cacheCutoutAll));
        cacheCutout = new ModelCacheJoiner<>(cutout);

        List<ModelKeyWrapper<PipeAllTranslucentKey, ?>> translucent = new ArrayList<>();
        translucent.add(new ModelKeyWrapper<>(PipeAllTranslucentKey::getBaseTranslucent, PipeModelCacheBase.cacheTranslucent));
        translucent.add(new ModelKeyWrapper<>(PipeAllTranslucentKey::getPluggable, PipeModelCachePluggable.cacheTranslucentAll));
        cacheTranslucent = new ModelCacheJoiner<>(translucent);
    }

    public static List<BakedQuad> getCutoutModel(TilePipeHolder tile) {
        return cacheCutout.bake(new PipeAllCutoutKey(tile));
    }

    public static List<BakedQuad> getTranslucentModel(TilePipeHolder tile) {
        PipeAllTranslucentKey key = new PipeAllTranslucentKey(tile);
        return cacheTranslucent.bake(key);
    }

    public static void clearModels() {
        cacheCutout.clear();
        cacheTranslucent.clear();
    }

    public static class PipeAllCutoutKey {
        private final PipeBaseCutoutKey cutout;
        private final PluggableKey pluggable;
        private final int hash;

        public PipeAllCutoutKey(TilePipeHolder tile) {
            cutout = new PipeBaseCutoutKey(tile.getPipe().getModel());
            this.pluggable = new PluggableKey(BlockRenderLayer.CUTOUT, tile);
            hash = Objects.hash(cutout, pluggable);
        }

        public PipeBaseCutoutKey getBaseCutout() {
            return cutout;
        }

        public PluggableKey getPluggable() {
            return pluggable;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PipeAllCutoutKey other = (PipeAllCutoutKey) obj;
            if (!cutout.equals(other.cutout)) return false;
            if (!pluggable.equals(other.pluggable)) return false;
            return true;
        }

        @Override
        public String toString() {
            return "PipeAllCutoutKey [base=" + cutout + ", pluggable = " + pluggable + "]";
        }
    }

    public static class PipeAllTranslucentKey {
        private final PipeBaseTransclucentKey translucent;
        private final PluggableKey pluggable;
        private final int hash;

        public PipeAllTranslucentKey(TilePipeHolder tile) {
            translucent = new PipeBaseTransclucentKey(tile.getPipe().getModel());
            this.pluggable = new PluggableKey(BlockRenderLayer.TRANSLUCENT, tile);
            hash = Objects.hash(translucent, pluggable);
        }

        public PipeBaseTransclucentKey getBaseTranslucent() {
            return translucent;
        }

        public PluggableKey getPluggable() {
            return pluggable;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PipeAllTranslucentKey other = (PipeAllTranslucentKey) obj;
            if (!translucent.equals(other.translucent)) return false;
            if (!pluggable.equals(other.pluggable)) return false;
            return true;
        }

        @Override
        public String toString() {
            return "PipeAllTranslucentKey [base=" + translucent + ", pluggable = " + pluggable + "]";
        }
    }
}
