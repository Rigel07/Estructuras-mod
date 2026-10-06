package com.cuterealms.entity;

import com.cuterealms.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Lumi, el hada de la fuente. Revolotea por el santuario y cura (regeneración) a quien se acerque.
 * Si le das bayas dulces o luminosas te regala polvo de hada de vez en cuando.
 */
public class LumiEntity extends PathfinderMob {
    private static final int HOME_RADIUS = 14;
    private int giftCooldown;

    public LumiEntity(EntityType<? extends LumiEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            if (this.random.nextInt(3) == 0) {
                level().addParticle(ParticleTypes.END_ROD, getRandomX(0.4D), getRandomY(), getRandomZ(0.4D),
                        0.0D, -0.02D, 0.0D);
            }
            return;
        }
        if (giftCooldown > 0) {
            giftCooldown--;
        }
        // Su hogar es el sitio donde apareció: no se aleja demasiado.
        if (tickCount == 40 && !hasRestriction()) {
            restrictTo(blockPosition(), HOME_RADIUS);
        }
        if (tickCount % 100 == 0 && hasRestriction() && !isWithinRestriction()) {
            BlockPos center = getRestrictCenter();
            getNavigation().moveTo(center.getX() + 0.5D, center.getY() + 2.0D, center.getZ() + 0.5D, 1.0D);
        }
        // Aura curativa
        if (tickCount % 60 == 0) {
            List<Player> players = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(7.0D, 4.0D, 7.0D));
            for (Player player : players) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 140, 0, true, true));
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SWEET_BERRIES) || stack.is(Items.GLOW_BERRIES)) {
            if (!level().isClientSide && giftCooldown <= 0) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                giftCooldown = 3000;
                spawnAtLocation(new ItemStack(ModRegistry.FAIRY_DUST.get(), 1 + this.random.nextInt(2)));
                playSound(SoundEvents.ALLAY_ITEM_GIVEN, 1.0F, 1.4F);
                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART, getX(), getY() + 0.8D, getZ(), 5, 0.3D, 0.3D, 0.3D, 0.02D);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }
}
