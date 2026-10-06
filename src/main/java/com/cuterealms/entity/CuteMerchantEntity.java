package com.cuterealms.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import com.cuterealms.ModRegistry;

/**
 * Los tres tenderos de los Reinos Cute: Mochi (panadería), Estrellín (observatorio) y Tuli (refugio).
 * Comercian como un aldeano, no se pueden matar y renuevan sus ofertas cada día.
 */
public class CuteMerchantEntity extends AbstractVillager {
    public enum Kind { MOCHI, STAR, TULI }

    private final Kind kind;

    public CuteMerchantEntity(EntityType<? extends AbstractVillager> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.45D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        this.goalSelector.addGoal(6, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.VILLAGER_SPAWN_EGG) || !isAlive() || isTrading() || isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (getOffers().isEmpty()) {
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!level().isClientSide) {
            setTradingPlayer(player);
            openTradingScreen(player, getDisplayName(), 1);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    // ----------------------------------------------------------- Ofertas

    @Override
    protected void updateTrades() {
        if (level().isClientSide) {
            return;
        }
        MerchantOffers offers = getOffers();
        switch (kind) {
            case MOCHI -> {
                buy(offers, Items.WHEAT, 20, 1, 16);
                buy(offers, Items.SUGAR, 24, 1, 16);
                buy(offers, Items.EGG, 12, 1, 16);
                buy(offers, Items.SWEET_BERRIES, 16, 1, 16);
                sell(offers, new ItemStack(ModRegistry.MOCHI_FOOD.get(), 4), 1, 16);
                sell(offers, new ItemStack(ModRegistry.STRAWBERRY_MOCHI.get(), 3), 2, 12);
                sell(offers, new ItemStack(ModRegistry.BUBBLE_TEA.get(), 2), 1, 12);
                sell(offers, new ItemStack(Items.COOKIE, 8), 1, 16);
                sell(offers, new ItemStack(Items.BREAD, 6), 1, 16);
                sell(offers, new ItemStack(Items.PUMPKIN_PIE, 2), 1, 12);
                sell(offers, new ItemStack(Items.CAKE, 1), 2, 8);
                sell(offers, new ItemStack(ModRegistry.WISH_COIN.get(), 3), 3, 8);
            }
            case STAR -> {
                buy(offers, Items.AMETHYST_SHARD, 12, 1, 12);
                buy(offers, Items.GLOWSTONE_DUST, 8, 1, 12);
                buy(offers, Items.LAPIS_LAZULI, 12, 1, 12);
                buy(offers, Items.ENDER_PEARL, 1, 3, 6);
                sell(offers, new ItemStack(ModRegistry.STARDUST.get(), 2), 1, 16);
                sell(offers, new ItemStack(Items.SPYGLASS), 2, 4);
                sell(offers, new ItemStack(Items.CLOCK), 3, 4);
                sell(offers, new ItemStack(Items.COMPASS), 2, 4);
                sell(offers, new ItemStack(Items.FIREWORK_ROCKET, 4), 1, 12);
                sell(offers, new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 3, 8);
                map(offers, ModRegistry.MOCHI_BAKERY, "filled_map.cute_realms.mochi_bakery", 7);
                map(offers, ModRegistry.WISHING_SANCTUARY, "filled_map.cute_realms.wishing_sanctuary", 9);
                map(offers, ModRegistry.POMPOM_SHELTER, "filled_map.cute_realms.pompom_shelter", 7);
            }
            case TULI -> {
                buy(offers, Items.CARROT, 24, 1, 16);
                buy(offers, Items.SWEET_BERRIES, 16, 1, 16);
                buy(offers, Items.WHEAT, 20, 1, 16);
                buy(offers, Items.BEETROOT, 24, 1, 16);
                sell(offers, new ItemStack(ModRegistry.POMPOM_TREAT.get(), 4), 1, 16);
                sell(offers, new ItemStack(Items.LEAD, 2), 2, 8);
                sell(offers, new ItemStack(Items.NAME_TAG), 3, 6);
                sell(offers, new ItemStack(Items.GOLDEN_CARROT, 4), 2, 8);
                sell(offers, new ItemStack(Items.HAY_BLOCK, 3), 1, 12);
                sell(offers, new ItemStack(ModRegistry.POMPOM_EGG.get()), 20, 3);
            }
        }
    }

    private static void buy(MerchantOffers offers, Item item, int amount, int emeralds, int uses) {
        offers.add(new MerchantOffer(new ItemStack(item, amount), new ItemStack(Items.EMERALD, emeralds), uses, 4, 0.05F));
    }

    private static void sell(MerchantOffers offers, ItemStack result, int emeralds, int uses) {
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), result, uses, 6, 0.05F));
    }

    /** Mapa (como el de un cartógrafo) que lleva a la estructura más cercana de ese tipo. */
    private void map(MerchantOffers offers, TagKey<Structure> tag, String nameKey, int emeralds) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos target = serverLevel.findNearestMapStructure(tag, blockPosition(), 100, true);
        if (target == null) {
            return;
        }
        ItemStack map = MapItem.create(serverLevel, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(serverLevel, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", MapDecoration.Type.TARGET_X);
        map.setHoverName(Component.translatable(nameKey));
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), new ItemStack(Items.COMPASS), map, 6, 10, 0.2F));
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5D, getZ(), 3 + this.random.nextInt(4)));
        }
    }

    /** Cada día (24000 ticks) se rellenan todas las ofertas. */
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (level().getGameTime() % 24000L == 0L) {
            for (MerchantOffer offer : getOffers()) {
                offer.resetUses();
            }
        }
    }

    // ------------------------------------------------- Son intocables y fijos

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return kind == Kind.STAR ? SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM : SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return kind == Kind.STAR ? SoundEvents.ALLAY_HURT : SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return kind == Kind.STAR ? SoundEvents.ALLAY_DEATH : SoundEvents.VILLAGER_DEATH;
    }
}
