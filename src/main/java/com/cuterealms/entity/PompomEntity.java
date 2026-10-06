package com.cuterealms.entity;

import com.cuterealms.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Pompom: una bolita de pelusa que se adopta en el refugio. Se doma con zanahorias, bayas o chuches de pompom.
 * Una vez adoptado te sigue (o se queda quieto) y su pelusa de la suerte te da Suerte mientras esté cerca.
 */
public class PompomEntity extends TamableAnimal {
    public static final int VARIANTS = 6;
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(PompomEntity.class, EntityDataSerializers.INT);

    public PompomEntity(EntityType<? extends PompomEntity> type, Level level) {
        super(type, level);
        this.entityData.set(DATA_VARIANT, this.random.nextInt(VARIANTS));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.4D) {
            @Override
            public boolean canUse() {
                return !PompomEntity.this.isTame() && super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.15D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, Ingredient.of(Items.CARROT, Items.SWEET_BERRIES,
                Items.GOLDEN_CARROT, ModRegistry.POMPOM_TREAT.get()), false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    // ------------------------------------------------------------ Datos

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, Math.floorMod(variant, VARIANTS));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant")) {
            setVariant(tag.getInt("Variant"));
        }
    }

    // ------------------------------------------------------------ Comida

    private static boolean isTreat(ItemStack stack) {
        return stack.is(Items.CARROT) || stack.is(Items.SWEET_BERRIES) || stack.is(Items.GOLDEN_CARROT)
                || stack.is(ModRegistry.POMPOM_TREAT.get());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return isTreat(stack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        PompomEntity baby = ModRegistry.POMPOM.get().create(level);
        if (baby != null) {
            baby.setVariant(other instanceof PompomEntity partner && this.random.nextBoolean()
                    ? partner.getVariant() : getVariant());
            UUID owner = getOwnerUUID();
            if (isTame() && owner != null) {
                baby.setOwnerUUID(owner);
                baby.setTame(true);
            }
        }
        return baby;
    }

    // -------------------------------------------------------- Interacción

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isTame()) {
            if (isTreat(stack)) {
                if (!level().isClientSide) {
                    boolean sure = stack.is(ModRegistry.POMPOM_TREAT.get());
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (sure || this.random.nextInt(3) == 0) {
                        tame(player);
                        this.navigation.stop();
                        setTarget(null);
                        setOrderedToSit(false);
                        setInSittingPose(false);
                        getAttribute(Attributes.MAX_HEALTH).setBaseValue(16.0D);
                        setHealth(16.0F);
                        level().broadcastEntityEvent(this, (byte) 7);
                        playSound(SoundEvents.RABBIT_AMBIENT, 1.0F, 1.8F);
                    } else {
                        level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            return InteractionResult.PASS;
        }

        if (!isOwnedBy(player)) {
            return InteractionResult.PASS;
        }

        if (isTreat(stack) && getHealth() < getMaxHealth()) {
            if (!level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                heal(4.0F);
                level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction()) {
            return result;
        }

        if (stack.isEmpty()) {
            if (!level().isClientSide) {
                boolean sit = !isOrderedToSit();
                setOrderedToSit(sit);
                this.jumping = false;
                this.navigation.stop();
                setTarget(null);
                player.displayClientMessage(Component.translatable(
                        sit ? "message.cute_realms.pompom_stay" : "message.cute_realms.pompom_follow"), true);
                playSound(SoundEvents.RABBIT_AMBIENT, 1.0F, 1.8F);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    // ------------------------------------------------------------- Tick

    @Override
    public void aiStep() {
        super.aiStep();
        // Pelusa de la suerte: Suerte para su dueño mientras esté cerca.
        if (!level().isClientSide && isTame() && tickCount % 80 == 0 && getOwner() instanceof Player owner
                && distanceToSqr(owner) < 12.0D * 12.0D) {
            owner.addEffect(new MobEffectInstance(MobEffects.LUCK, 400, 0, true, false));
            if (level() instanceof ServerLevel serverLevel && this.random.nextInt(4) == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 0.9D, getZ(), 3, 0.3D, 0.3D, 0.3D, 0.0D);
            }
        }
    }

    // ---------------------------------------------------------- Sonidos

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }
}
