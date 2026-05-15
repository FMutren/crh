package top.fmutren.crh.interaction.util;


import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.fmutren.crh.interaction.ChainSelection;

import java.util.*;
import java.util.function.Predicate;

public final class ChainCollector {

    private static final Direction[] DIRECTIONS = Direction.values();

    private ChainCollector() {
    }

    public static ChainSelection collectBelt(
            Level level,
            BlockPos origin,
            int limit,
            Predicate<BeltBlockEntity> beltPredicate
    ) {
        if (limit <= 0 || !level.isLoaded(origin)) {
            return ChainSelection.empty();
        }

        if (!(level.getBlockEntity(origin) instanceof BeltBlockEntity originBelt)) {
            return ChainSelection.empty();
        }

        List<BlockPos> chain = BeltBlock.getBeltChain(level, originBelt.getController());
        List<BlockPos> filtered = new ArrayList<>(Math.min(limit, chain.size()));
        boolean truncated = false;

        for (BlockPos beltPos : chain) {
            if (!level.isLoaded(beltPos)) {
                continue;
            }

            if (!(level.getBlockEntity(beltPos) instanceof BeltBlockEntity belt) || !beltPredicate.test(belt)) {
                continue;
            }

            if (filtered.size() >= limit) {
                truncated = true;
                break;
            }

            filtered.add(beltPos.immutable());
        }

        return new ChainSelection(filtered, truncated);
    }

    public static ChainSelection collectShaft(
            Level level,
            BlockPos origin,
            Direction.Axis axis,
            Predicate<BlockState> allowedState,
            int limit
    ) {
        if (limit <= 0 || !isAllowed(level, origin, allowedState)) {
            return ChainSelection.empty();
        }

        List<BlockPos> ordered = new ArrayList<>(limit);
        ordered.add(origin.immutable());

        boolean negativeOpen = true;
        boolean positiveOpen = true;
        boolean truncated = false;
        int distance = 1;

        while (negativeOpen || positiveOpen) {
            if (negativeOpen) {
                BlockPos next = relative(origin, axis, -distance);
                if (isAllowed(level, next, allowedState)) {
                    if (ordered.size() < limit) {
                        ordered.add(next.immutable());
                    } else {
                        truncated = true;
                    }
                } else {
                    negativeOpen = false;
                }
            }

            if (positiveOpen) {
                BlockPos next = relative(origin, axis, distance);
                if (isAllowed(level, next, allowedState)) {
                    if (ordered.size() < limit) {
                        ordered.add(next.immutable());
                    } else {
                        truncated = true;
                    }
                } else {
                    positiveOpen = false;
                }
            }

            if (truncated) {
                break;
            }

            if (ordered.size() >= limit) {
                truncated = hasNextAllowed(level, origin, axis, allowedState, distance + 1, negativeOpen, positiveOpen);
                break;
            }

            distance++;
        }

        return new ChainSelection(ordered, truncated);
    }

    private static boolean isAllowed(Level level, BlockPos pos, Predicate<BlockState> allowedState) {
        return level.isLoaded(pos) && allowedState.test(level.getBlockState(pos));
    }

    private static BlockPos relative(BlockPos pos, Direction.Axis axis, int amount) {
        return switch (axis) {
            case X -> pos.offset(amount, 0, 0);
            case Y -> pos.offset(0, amount, 0);
            case Z -> pos.offset(0, 0, amount);
        };
    }

    private static boolean hasNextAllowed(
            Level level,
            BlockPos origin,
            Direction.Axis axis,
            Predicate<BlockState> allowedState,
            int distance,
            boolean negativeOpen,
            boolean positiveOpen
    ) {
        return (negativeOpen && isAllowed(level, relative(origin, axis, -distance), allowedState))
                || (positiveOpen && isAllowed(level, relative(origin, axis, distance), allowedState));
    }

    public static ChainSelection collectPipe(
            Level level,
            BlockPos origin,
            Predicate<BlockState> allowedState,
            int limit
    ) {
        if (limit <= 0 || !isAllowed(level, origin, allowedState)) {
            return ChainSelection.empty();
        }

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>(limit * 2);
        List<BlockPos> ordered = new ArrayList<>(limit);
        boolean truncated = false;

        BlockPos immutableOrigin = origin.immutable();
        queue.add(immutableOrigin);
        visited.add(immutableOrigin);

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.removeFirst();
            if (!level.isLoaded(currentPos)) {
                continue;
            }

            BlockState currentState = level.getBlockState(currentPos);
            if (!allowedState.test(currentState)) {
                continue;
            }

            ordered.add(currentPos.immutable());

            for (Direction direction : DIRECTIONS) {
                if (!PredicatesCreator.isPipeOpen(currentState, direction)) {
                    continue;
                }

                BlockPos nextPos = currentPos.relative(direction).immutable();
                if (visited.contains(nextPos) || !level.isLoaded(nextPos)) {
                    continue;
                }

                BlockState nextState = level.getBlockState(nextPos);
                if (!allowedState.test(nextState)
                        || !PredicatesCreator.isPipeOpen(nextState, direction.getOpposite())) {
                    continue;
                }

                visited.add(nextPos);
                if (ordered.size() + queue.size() < limit) {
                    queue.addLast(nextPos);
                } else {
                    truncated = true;
                }
            }
        }

        return new ChainSelection(ordered, truncated);
    }

}
