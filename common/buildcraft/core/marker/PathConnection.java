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
package buildcraft.core.marker;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.core.client.BuildCraftLaserManager;
import buildcraft.lib.client.render.LaserData_BC8;
import buildcraft.lib.client.render.LaserRenderer_BC8;
import buildcraft.lib.marker.MarkerConnection;
import buildcraft.lib.marker.MarkerSubCache;
import buildcraft.lib.misc.VecUtil;

public class PathConnection extends MarkerConnection<PathConnection> {
    private static final double RENDER_SCALE = 1 / 16.05;
    private static final Vec3d VEC_HALF = new Vec3d(0.5, 0.5, 0.5);
    private final Deque<BlockPos> positions = new LinkedList<>();
    private boolean loop = false;

    public static boolean tryCreateConnection(PathSubCache subCache, BlockPos from, BlockPos to) {
        PathConnection connection = new PathConnection(subCache);
        connection.positions.add(from);
        connection.positions.add(to);
        subCache.addConnection(connection);
        return true;
    }

    public PathConnection(MarkerSubCache<PathConnection> subCache) {
        super(subCache);
    }

    public PathConnection(PathSubCache subCache, List<BlockPos> positions) {
        super(subCache);
        for (BlockPos p : positions) {
            if (p.equals(this.positions.peekFirst())) {
                loop = true;
                break;
            } else {
                this.positions.addLast(p);
            }
        }
    }

    @Override
    public void removeMarker(BlockPos pos) {
        if (positions.getFirst().equals(pos)) {
            positions.removeFirst();
            loop = false;
            if (positions.size() < 2) {
                positions.clear();
            }
            subCache.refreshConnection(this);
        } else if (positions.getLast().equals(pos)) {
            positions.removeLast();
            loop = false;
            if (positions.size() < 2) {
                positions.clear();
            }
            subCache.refreshConnection(this);
        } else if (positions.contains(pos)) {
            List<BlockPos> a = new ArrayList<>();
            List<BlockPos> b = new ArrayList<>();
            boolean hasReached = false;
            for (BlockPos p : positions) {
                if (p.equals(pos)) {
                    hasReached = true;
                } else if (hasReached) {
                    b.add(p);
                } else {
                    a.add(p);
                }
            }
            PathConnection conA = new PathConnection(subCache);
            PathConnection conB = new PathConnection(subCache);
            conA.positions.addAll(a);
            conB.positions.addAll(b);
            positions.clear();
            subCache.refreshConnection(this);
            subCache.addConnection(conA);
            subCache.addConnection(conB);
        }
    }

    public boolean addMarker(BlockPos from, BlockPos toAdd) {
        if (loop) {
            return false;
        }
        boolean contains = positions.contains(toAdd);
        if (positions.getFirst().equals(from)) {
            if (positions.getLast().equals(toAdd)) {
                loop = true;
            } else if (!contains) {
                positions.addFirst(toAdd);
            } else {
                return false;
            }
            subCache.refreshConnection(this);
            return true;
        } else if (positions.getLast().equals(from)) {
            if (positions.getFirst().equals(toAdd)) {
                loop = true;
                return true;
            } else if (!contains) {
                positions.addLast(toAdd);
            } else {
                return false;
            }
            subCache.refreshConnection(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean canAddMarker(BlockPos from, BlockPos toAdd) {
        if (loop) {
            return false;
        }
        boolean contains = positions.contains(toAdd);
        if (positions.getFirst().equals(from)) {
            if (contains) {
                return positions.getLast().equals(toAdd);
            } else {
                return true;
            }
        } else if (positions.getLast().equals(from)) {
            if (contains) {
                return positions.getLast().equals(toAdd);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean mergeWith(PathConnection conTo, BlockPos from, BlockPos to) {
        if (loop || conTo.loop) {
            return false;
        } else if (conTo == this) {
            if (positions.size() <= 2) {
                return false;
            }
            if (positions.getFirst().equals(to) && positions.getLast().equals(from)) {
                loop = true;
                subCache.refreshConnection(this);
                return true;
            } else {
                return false;
            }
        } else if (positions.getLast().equals(from) && conTo.positions.getFirst().equals(to)) {
            positions.addAll(conTo.positions);
            conTo.positions.clear();
            subCache.refreshConnection(conTo);
            subCache.refreshConnection(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean canMergeWith(PathConnection conTo, BlockPos from, BlockPos to) {
        if (loop || conTo.loop) {
            return false;
        } else if (conTo == this) {
            if (positions.size() <= 2) {
                return false;
            }
            if (positions.getFirst().equals(to) && positions.getLast().equals(from)) {
                return true;
            } else {
                return false;
            }
        } else if (positions.getLast().equals(from) && conTo.positions.getFirst().equals(to)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ImmutableList<BlockPos> getMarkerPositions() {
        if (loop && positions.size() > 0) {
            ImmutableList.Builder<BlockPos> list = ImmutableList.builder();
            list.addAll(positions);
            list.add(positions.getFirst());
            return list.build();
        }
        return ImmutableList.copyOf(positions);
    }

    public void reverseDirection() {
        Deque<BlockPos> list = new LinkedList<>();
        while (!positions.isEmpty()) {
            list.addFirst(positions.removeFirst());
        }
        positions.clear();
        for (BlockPos pos : list) {
            positions.add(pos);
        }
        subCache.refreshConnection(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInWorld() {
        BlockPos last = null;
        for (BlockPos p : positions) {
            if (last == null) {
                last = p;
            } else {
                renderLaser(VecUtil.add(VEC_HALF, last), VecUtil.add(VEC_HALF, p));
                last = p;
            }
        }
        if (loop) {
            BlockPos from = positions.getLast();
            BlockPos to = positions.getFirst();
            renderLaser(VecUtil.add(VEC_HALF, from), VecUtil.add(VEC_HALF, to));
        }
    }

    @SideOnly(Side.CLIENT)
    private static void renderLaser(Vec3d from, Vec3d to) {
        Vec3d one = offset(from, to);
        Vec3d two = offset(to, from);
        LaserData_BC8 data = new LaserData_BC8(BuildCraftLaserManager.MARKER_PATH_CONNECTED, one, two, RENDER_SCALE);
        LaserRenderer_BC8.renderLaser(data);
    }

    @SideOnly(Side.CLIENT)
    private static Vec3d offset(Vec3d from, Vec3d to) {
        Vec3d dir = to.subtract(from).normalize();
        return from.add(VecUtil.scale(dir, 0.125));
    }
}
