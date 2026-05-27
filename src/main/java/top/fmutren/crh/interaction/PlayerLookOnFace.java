package top.fmutren.crh.interaction;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static top.fmutren.crh.interaction.util.PredicatesCreator.isPipeOpen;

public class PlayerLookOnFace {

    private PlayerLookOnFace() {}

    public static Direction getPlayerDirection(Player player) {
        if (player == null) return Direction.UP;

        Vec3 look = player.getLookAngle();

        double x = look.x;
        double y = look.y;
        double z = look.z;

        double ax = Math.abs(x);
        double ay = Math.abs(y);
        double az = Math.abs(z);

        if (ax >= ay && ax >= az) {
            return x > 0 ? Direction.EAST : Direction.WEST;
        } else if (ay >= ax && ay >= az) {
            return y > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private static Direction.Axis FaceToAxis(Direction face) {
        if(face == null) return Direction.Axis.Y;
        return face.getAxis();
    }

    public static Direction.Axis fluidPipeFace(Player player, BlockState state) {
        int x = 0, y = 0, z = 0;
        for (Direction dir : Direction.values()){
            switch (dir){
                case UP, DOWN -> {
                    if (isPipeOpen(state, dir)) y++;
                }
                case NORTH, SOUTH -> {
                    if (isPipeOpen(state, dir)) z++;
                }
                case WEST, EAST -> {
                    if (isPipeOpen(state, dir)) x++;
                }
            }
        }
        if(x > y && x > z) return Direction.Axis.X;
        else if(y > x && y > z) return Direction.Axis.Y;
        else if(z > x && z > y) return Direction.Axis.Z;
        return FaceToAxis(getPlayerDirection(player));
    }
}
