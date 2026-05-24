package top.fmutren.crh.interaction;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static top.fmutren.crh.interaction.util.PredicatesCreator.isPipeOpen;

public final class PlayerLookOnFace {

    private PlayerLookOnFace() {
    }

    public static Direction.Axis fluidPipeFace(Player player, BlockState state) {
        int x = 0;
        int y = 0;
        int z = 0;

        for (Direction direction : Direction.values()) {
            switch (direction) {
                case UP, DOWN -> {
                    if (isPipeOpen(state, direction)) {
                        y++;
                    }
                }
                case NORTH, SOUTH -> {
                    if (isPipeOpen(state, direction)) {
                        z++;
                    }
                }
                case WEST, EAST -> {
                    if (isPipeOpen(state, direction)) {
                        x++;
                    }
                }
            }
        }

        if (x > y && x > z) {
            return Direction.Axis.X;
        }

        if (y > x && y > z) {
            return Direction.Axis.Y;
        }

        if (z > x && z > y) {
            return Direction.Axis.Z;
        }

        return faceToAxis(getPlayerLookingFace(player));
    }

    public static Direction.Axis faceToAxis(Direction face) {
        return face == null ? Direction.Axis.Y : face.getAxis();
    }

    public static Direction getPlayerLookingFace(Player player) {
        if (player == null) {
            return Direction.UP;
        }

        Vec3 look = player.getLookAngle();
        double x = look.x;
        double y = look.y;
        double z = look.z;

        double ax = Math.abs(x);
        double ay = Math.abs(y);
        double az = Math.abs(z);

        if (ax >= ay && ax >= az) {
            return x > 0 ? Direction.EAST : Direction.WEST;
        }

        if (ay >= ax && ay >= az) {
            return y > 0 ? Direction.UP : Direction.DOWN;
        }

        return z > 0 ? Direction.SOUTH : Direction.NORTH;
    }

}
