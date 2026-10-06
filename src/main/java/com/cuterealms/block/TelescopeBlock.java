package com.cuterealms.block;

import com.cuterealms.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Telescopio: de noche, al mirar por él, localiza la panadería, el santuario o el refugio más cercanos
 * y te dice hacia dónde y a cuántos bloques están.
 */
public class TelescopeBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
    private static final int SEARCH_RADIUS = 64;

    public TelescopeBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }
        if (level.isDay()) {
            player.displayClientMessage(Component.translatable("message.cute_realms.telescope_day"), true);
            return InteractionResult.CONSUME;
        }
        Item cooldownItem = ModRegistry.TELESCOPE_ITEM.get();
        if (player.getCooldowns().isOnCooldown(cooldownItem)) {
            player.displayClientMessage(Component.translatable("message.cute_realms.telescope_wait"), true);
            return InteractionResult.CONSUME;
        }
        player.getCooldowns().addCooldown(cooldownItem, 100);

        BlockPos best = null;
        String bestName = null;
        double bestDistance = Double.MAX_VALUE;
        for (Target target : Target.values()) {
            BlockPos found = serverLevel.findNearestMapStructure(target.tag(), pos, SEARCH_RADIUS, false);
            if (found != null) {
                double distance = Math.sqrt(found.distSqr(pos));
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = found;
                    bestName = target.nameKey;
                }
            }
        }
        if (best == null) {
            player.displayClientMessage(Component.translatable("message.cute_realms.telescope_none"), true);
        } else {
            Direction direction = Direction.getNearest(best.getX() - pos.getX(), 0.0D, best.getZ() - pos.getZ());
            player.displayClientMessage(Component.translatable("message.cute_realms.telescope_found",
                    Component.translatable(bestName),
                    Component.translatable("message.cute_realms.dir." + direction.getName()),
                    (int) Math.round(bestDistance)), false);
        }
        return InteractionResult.CONSUME;
    }

    private enum Target {
        BAKERY("structure.cute_realms.mochi_bakery"),
        SANCTUARY("structure.cute_realms.wishing_sanctuary"),
        SHELTER("structure.cute_realms.pompom_shelter");

        private final String nameKey;

        Target(String nameKey) {
            this.nameKey = nameKey;
        }

        TagKey<Structure> tag() {
            return switch (this) {
                case BAKERY -> ModRegistry.MOCHI_BAKERY;
                case SANCTUARY -> ModRegistry.WISHING_SANCTUARY;
                case SHELTER -> ModRegistry.POMPOM_SHELTER;
            };
        }
    }
}
