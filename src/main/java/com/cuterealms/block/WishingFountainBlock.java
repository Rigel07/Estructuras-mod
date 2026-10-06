package com.cuterealms.block;

import com.cuterealms.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * Fuente de los deseos: lanza una moneda de deseo (o una pepita de oro) y pide un deseo.
 * Cuanta más Suerte tengas (Pompom, gema, poción), mejores deseos recibirás. Hay un 2 % de "gran deseo".
 */
public class WishingFountainBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    public WishingFountainBlock(Properties properties) {
        super(properties);
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
        ItemStack stack = player.getItemInHand(hand);
        boolean coin = stack.is(ModRegistry.WISH_COIN.get());
        boolean nugget = stack.is(Items.GOLD_NUGGET);
        if (!coin && !nugget) {
            player.displayClientMessage(Component.translatable("message.cute_realms.fountain_hint"), true);
            return InteractionResult.CONSUME;
        }
        Item item = stack.getItem();
        if (player.getCooldowns().isOnCooldown(item)) {
            return InteractionResult.CONSUME;
        }
        player.getCooldowns().addCooldown(item, 30);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        boolean great = this.rollGreatWish(serverLevel);
        ResourceLocation table = great ? ModRegistry.GREAT_WISH_LOOT
                : coin ? ModRegistry.WISH_COIN_LOOT : ModRegistry.WISH_NUGGET_LOOT;
        for (ItemStack reward : generate(serverLevel, pos, player, table)) {
            Block.popResource(level, pos.above(), reward);
        }

        Vec3 c = Vec3.atCenterOf(pos).add(0.0D, 0.9D, 0.0D);
        level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.8F, 1.4F);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, great ? 1.8F : 1.2F);
        serverLevel.sendParticles(ParticleTypes.SPLASH, c.x, c.y, c.z, 20, 0.3D, 0.2D, 0.3D, 0.1D);
        serverLevel.sendParticles(ParticleTypes.NOTE, c.x, c.y + 0.3D, c.z, great ? 8 : 3, 0.3D, 0.3D, 0.3D, 0.0D);
        if (great) {
            level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, c.x, c.y, c.z, 50, 0.4D, 0.5D, 0.4D, 0.3D);
            player.displayClientMessage(Component.translatable("message.cute_realms.great_wish"), true);
        }
        return InteractionResult.CONSUME;
    }

    private boolean rollGreatWish(ServerLevel level) {
        return level.random.nextInt(50) == 0;
    }

    private static List<ItemStack> generate(ServerLevel level, BlockPos pos, Player player, ResourceLocation id) {
        LootTable table = level.getServer().getLootData().getLootTable(id);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);
        return table.getRandomItems(params);
    }
}
