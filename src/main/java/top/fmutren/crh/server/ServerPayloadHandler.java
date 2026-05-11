package top.fmutren.crh.server;

import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.fmutren.crh.network.ModMessages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerPayloadHandler {

    private static final Set<String> casings = new HashSet<>();
    private static final Map<String, String> shaftCasingType = new HashMap<>();
    private static final Map<String, String> beltCasingType = new HashMap<>();

    static {
        casings.add("create:brass_casing");
        casings.add("create:andesite_casing");
        casings.add("create:copper_casing");
        casings.add("create:wrench");
    }

    static {
        shaftCasingType.put("create:brass_casing", "create:brass_encased_shaft");
        shaftCasingType.put("create:andesite_casing", "create:andesite_encased_shaft");
    }

    static {
        beltCasingType.put("create:brass_casing", "BRASS");
        beltCasingType.put("create:andesite_casing", "ANDESITE");
        beltCasingType.put("create:wrench", "NONE");
    }

    public static void ServerEncase(final ModMessages.EncasingNetWork packet, final IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();
        BlockPos pos = packet.pos();
        Direction face = packet.face();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (packet.hand() == 1) hand = InteractionHand.OFF_HAND;
        boolean isAlt = packet.Alt() == 1;
        BlockState state = level.getBlockState(pos);
        if (player.getItemInHand(hand).isEmpty()) {//空手触发管道
            if (!(state.getBlock() instanceof FluidPipeBlock) && !(state.getBlock() instanceof EncasedPipeBlock))
                return;//不是管道或者套壳的
            player.swing(hand, true);
            ClickFluidPip(player, state, face, level, pos, player.isShiftKeyDown());
        } else if (casings.contains(player.getItemInHand(hand).getItem().toString()) && isAlt) {
            //流体管道套壳
            if (state.getBlock() instanceof FluidPipeBlock || state.getBlock() instanceof EncasedPipeBlock) {
                player.swing(hand, true);
                EncasingFluidPip(player.getItemInHand(hand).getItem(), state, level, pos, 0, "null");
            }
            //传送带套壳
            else if (state.getBlock() instanceof BeltBlock) {
                if (player.getMainHandItem().getItem().toString().equals("create:copper_casing")) return;
                player.swing(hand, true);
                EncasingBelt(player.getItemInHand(hand).getItem(), state, level, pos);
            }
            //传动杆套壳
            else if (state.getBlock() instanceof ShaftBlock) {
                if (player.getMainHandItem().getItem().toString().equals("create:copper_casing")) return;
                player.swing(hand, true);
                EncasingShaft(player.getItemInHand(hand).getItem(), state, level, pos, player.isShiftKeyDown());
            }
            //传动杆拆壳
            else if (state.getBlock().asItem().toString().equals("create:andesite_encased_shaft") || state.getBlock()
                    .asItem()
                    .toString()
                    .equals("create:brass_encased_shaft")) {
                if (player.getMainHandItem().getItem().toString().equals("create:copper_casing")) return;
                player.swing(hand, true);
                EncasingShaft(player.getItemInHand(hand).getItem(), state, level, pos, player.isShiftKeyDown());
            }
        }
    }

    public static void EncasingShaft(Item hand, BlockState state, Level level, BlockPos pos, boolean shift) {
        int limit = 64;
        String axis = state.getValue(ShaftBlock.AXIS).toString();
        int[] end = {0, 0, 0};
        int[] start = {0, 0, 0};
        int[] toEnd = {0, 0, 0};
        int[] toStart = {0, 0, 0};
        switch (axis) {
            case "x":
                toEnd[0] = 1;
                toStart[0] = -1;
                break;
            case "y":
                toEnd[1] = 1;
                toStart[1] = -1;
                break;
            case "z":
                toEnd[2] = 1;
                toStart[2] = -1;
                break;
        }
        if (!hand.toString().equals("create:wrench")) {
            for (int i = 0; i <= limit; i++) {
                end[0] += toEnd[0];
                end[1] += toEnd[1];
                end[2] += toEnd[2];
                BlockPos endPos = new BlockPos(pos.getX() + end[0], pos.getY() + end[1], pos.getZ() + end[2]);
                if (!(level.getBlockState(endPos).getBlock() instanceof ShaftBlock)) break;
                BlockState endState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(shaftCasingType.get(hand.toString())))
                        .defaultBlockState()
                        .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
                level.setBlockAndUpdate(endPos, endState);
            }
            for (int i = 0; i <= limit; i++) {
                start[0] += toStart[0];
                start[1] += toStart[1];
                start[2] += toStart[2];
                BlockPos startPos = new BlockPos(pos.getX() + start[0], pos.getY() + start[1], pos.getZ() + start[2]);
                if (!(level.getBlockState(startPos).getBlock() instanceof ShaftBlock)) break;
                BlockState endState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(shaftCasingType.get(hand.toString())))
                        .defaultBlockState()
                        .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
                level.setBlockAndUpdate(startPos, endState);
            }
        } else if (shift) {
            for (int i = 0; i <= limit; i++) {
                end[0] += toEnd[0];
                end[1] += toEnd[1];
                end[2] += toEnd[2];
                BlockPos endPos = new BlockPos(pos.getX() + end[0], pos.getY() + end[1], pos.getZ() + end[2]);
                if (!level.getBlockState(endPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals("create:andesite_encased_shaft") && !level.getBlockState(endPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals("create:brass_encased_shaft")) break;
                BlockState endState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create:shaft"))
                        .defaultBlockState()
                        .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
                level.setBlockAndUpdate(endPos, endState);
            }
            for (int i = 0; i <= limit; i++) {
                start[0] += toStart[0];
                start[1] += toStart[1];
                start[2] += toStart[2];
                BlockPos startPos = new BlockPos(pos.getX() + start[0], pos.getY() + start[1], pos.getZ() + start[2]);
                if (!level.getBlockState(startPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals("create:andesite_encased_shaft") && !level.getBlockState(startPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals("create:brass_encased_shaft")) break;
                BlockState endState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create:shaft"))
                        .defaultBlockState()
                        .setValue(ShaftBlock.AXIS, state.getValue(ShaftBlock.AXIS));
                level.setBlockAndUpdate(startPos, endState);
            }
        }
    }

    public static void EncasingFluidPip(
            Item hand, BlockState state, Level level, BlockPos pos, int count, String from) {
        if (!(state.getBlock() instanceof FluidPipeBlock || state.getBlock() instanceof EncasedPipeBlock)) return;
        int limit = 16;
        boolean up = false;
        boolean down = false;
        boolean north = false;
        boolean south = false;
        boolean west = false;
        boolean east = false;
        if (count < limit) {
            for (Direction dir : Direction.values()) {
                BooleanProperty prop = FluidPipeBlock.PROPERTY_BY_DIRECTION.get(dir);
                if (prop != null && state.getValue(prop)) {
                    switch (dir) {
                        case UP -> up = true;
                        case DOWN -> down = true;
                        case NORTH -> north = true;
                        case SOUTH -> south = true;
                        case WEST -> west = true;
                        case EAST -> east = true;
                    }
                }
            }
            if (up && !from.equals("up")) {
                BlockPos newPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
                if (level.getBlockState(newPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals(level.getBlockState(pos).getBlock().asItem().toString()) || count == 0)
                    EncasingFluidPip(hand, level.getBlockState(newPos), level, newPos, count + 1, "down");
            }
            if (down && !from.equals("down")) {
                BlockPos newPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
                if (level.getBlockState(newPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals(level.getBlockState(pos).getBlock().asItem().toString()) || count == 0)
                    EncasingFluidPip(hand, level.getBlockState(newPos), level, newPos, count + 1, "up");
            }
            if (north && !from.equals("north")) {
                BlockPos newPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
                if (level.getBlockState(newPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals(level.getBlockState(pos).getBlock().asItem().toString()) || count == 0)
                    EncasingFluidPip(hand, level.getBlockState(newPos), level, newPos, count + 1, "south");
            }
            if (south && !from.equals("south")) {
                BlockPos newPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                if (level.getBlockState(newPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals(level.getBlockState(pos).getBlock().asItem().toString()) || count == 0)
                    EncasingFluidPip(hand, level.getBlockState(newPos), level, newPos, count + 1, "north");
            }
            if (west && !from.equals("west")) {
                BlockPos newPos = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
                if (level.getBlockState(newPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals(level.getBlockState(pos).getBlock().asItem().toString()) || count == 0)
                    EncasingFluidPip(hand, level.getBlockState(newPos), level, newPos, count + 1, "east");
            }
            if (east && !from.equals("east")) {
                BlockPos newPos = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
                if (level.getBlockState(newPos)
                        .getBlock()
                        .asItem()
                        .toString()
                        .equals(level.getBlockState(pos).getBlock().asItem().toString()) || count == 0)
                    EncasingFluidPip(hand, level.getBlockState(newPos), level, newPos, count + 1, "west");
            }
            if (hand.toString().equals("create:copper_casing")) {
                BlockState endState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create:encased_fluid_pipe"))
                        .defaultBlockState()
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), up)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), down)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH), north)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH), south)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST), west)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST), east);
                level.setBlockAndUpdate(pos, endState);
            } else if (hand.toString().equals("create:wrench") && count != 0) {
                BlockState endState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create:fluid_pipe"))
                        .defaultBlockState()
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), up)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), down)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH), north)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH), south)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST), west)
                        .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST), east);
                level.setBlockAndUpdate(pos, endState);
            }
        }
    }

    public static void EncasingBelt(Item hand, BlockState state, Level level, BlockPos pos) {
        int limit = 32;
        String slope = state.getValue(BeltBlock.SLOPE).toString();
        String facing = state.getValue(BeltBlock.HORIZONTAL_FACING).toString();
        String part = state.getValue(BeltBlock.PART).toString();
        int[] end = {0, 0, 0};
        int[] start = {0, 0, 0};
        int[] toEnd = {0, 0, 0};
        int[] toStart = {0, 0, 0};
        if (slope.equals("VERTICAL")) {
            toEnd[1] = 1;
            toStart[1] = 1;
        } else {
            switch (facing) {
                case "east":
                    toEnd[0] = 1;
                    toStart[0] = -1;
                    break;
                case "west":
                    toEnd[0] = -1;
                    toStart[0] = 1;
                    break;
                case "south":
                    toEnd[2] = 1;
                    toStart[2] = -1;
                    break;
                case "north":
                    toEnd[2] = -1;
                    toStart[2] = 1;
            }
        }
        if (!slope.equals("HORIZONTAL")) {
            switch (slope) {
                case "UPWARD":
                    toEnd[1] = 1;
                    toStart[1] = -1;
                    break;
                case "DOWNWARD":
                    toEnd[1] = -1;
                    toStart[1] = 1;
                    break;
            }
        }
        if (!part.equals("END")) {
            for (int i = 0; i < limit; i++) {
                end[0] += toEnd[0];
                end[1] += toEnd[1];
                end[2] += toEnd[2];
                BlockPos endPos = new BlockPos(pos.getX() + end[0], pos.getY() + end[1], pos.getZ() + end[2]);
                Block endBlock = level.getBlockState(endPos).getBlock();
                if (!(endBlock instanceof BeltBlock)) break;
                if (level.getBlockEntity(endPos) instanceof BeltBlockEntity beltEntity) {
                    beltEntity.setCasingType(BeltBlockEntity.CasingType.valueOf(beltCasingType.get(hand.toString())));
                }
                if (level.getBlockState(endPos).getValue(BeltBlock.PART).toString().equals("END")) break;
            }
        }
        if (!part.equals("START")) {
            for (int i = 0; i < limit; i++) {
                start[0] += toStart[0];
                start[1] += toStart[1];
                start[2] += toStart[2];
                BlockPos startPos = new BlockPos(pos.getX() + start[0], pos.getY() + start[1], pos.getZ() + start[2]);
                Block startBlock = level.getBlockState(startPos).getBlock();
                if (!(startBlock instanceof BeltBlock)) break;
                if (level.getBlockEntity(startPos) instanceof BeltBlockEntity beltEntity) {
                    beltEntity.setCasingType(BeltBlockEntity.CasingType.valueOf(beltCasingType.get(hand.toString())));
                }
                if (level.getBlockState(startPos).getValue(BeltBlock.PART).toString().equals("START")) break;
            }
        }
    }

    public static void ClickFluidPip(
            Player player, BlockState state, Direction face, Level level, BlockPos pos, boolean shift) {
        int openCount = 0;
        for (Direction dir : Direction.values()) {//获取开口数
            BooleanProperty prop = FluidPipeBlock.PROPERTY_BY_DIRECTION.get(dir);
            if (prop != null && state.getValue(prop)) {
                openCount++;
            }
        }

        if (shift) {//蹲下切换对面
            switch (face) {
                case DOWN:
                    face = Direction.UP;
                    break;
                case UP:
                    face = Direction.DOWN;
                    break;
                case NORTH:
                    face = Direction.SOUTH;
                    break;
                case SOUTH:
                    face = Direction.NORTH;
                    break;
                case WEST:
                    face = Direction.EAST;
                    break;
                case EAST:
                    face = Direction.WEST;
                    break;
            }
        }

        BooleanProperty prop = FluidPipeBlock.PROPERTY_BY_DIRECTION.get(face);
        boolean current = state.getValue(prop);//扣的那个面是否开口

        if (openCount <= 2 && current && state.getBlock() instanceof FluidPipeBlock) {//不是套壳且正在扣最后两个口
            player.displayClientMessage(Component.translatable("fus.message.pipelowertwo"), true);
            return;
        }

        BlockState newState = state.setValue(prop, !current);

        level.setBlock(pos, newState, 3);
        level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 0.6f, 1.2f);
    }

}
