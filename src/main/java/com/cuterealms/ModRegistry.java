package com.cuterealms;

import com.cuterealms.block.TelescopeBlock;
import com.cuterealms.block.WishingFountainBlock;
import com.cuterealms.entity.CuteMerchantEntity;
import com.cuterealms.entity.LumiEntity;
import com.cuterealms.entity.PompomEntity;
import com.cuterealms.item.BubbleTeaItem;
import com.cuterealms.item.FairyDustItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRegistry {
    private ModRegistry() {}

    private static final String ID = CuteRealmsMod.MOD_ID;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);

    // ---- Tags de estructuras (data/cute_realms/tags/worldgen/structure) ----
    public static final TagKey<Structure> CUTE_STRUCTURES = structureTag("cute_structures");
    public static final TagKey<Structure> TELESCOPE_TARGETS = structureTag("telescope_targets");
    public static final TagKey<Structure> MOCHI_BAKERY = structureTag("mochi_bakery");
    public static final TagKey<Structure> STAR_OBSERVATORY = structureTag("star_observatory");
    public static final TagKey<Structure> WISHING_SANCTUARY = structureTag("wishing_sanctuary");
    public static final TagKey<Structure> POMPOM_SHELTER = structureTag("pompom_shelter");

    private static TagKey<Structure> structureTag(String name) {
        return TagKey.create(Registries.STRUCTURE, new ResourceLocation(ID, name));
    }

    // ---- Tablas de botín de los deseos ----
    public static final ResourceLocation WISH_COIN_LOOT = new ResourceLocation(ID, "gameplay/wish_coin");
    public static final ResourceLocation WISH_NUGGET_LOOT = new ResourceLocation(ID, "gameplay/wish_nugget");
    public static final ResourceLocation GREAT_WISH_LOOT = new ResourceLocation(ID, "gameplay/great_wish");

    // ---- Bloques ----
    public static final RegistryObject<Block> TELESCOPE = BLOCKS.register("telescope",
            () -> new TelescopeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD).strength(2.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> WISHING_FOUNTAIN = BLOCKS.register("wishing_fountain",
            () -> new WishingFountainBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ).strength(3.0F).sound(SoundType.STONE).noOcclusion()
                    .lightLevel(state -> 8)));

    // ---- Entidades ----
    public static final RegistryObject<EntityType<CuteMerchantEntity>> MOCHI = ENTITIES.register("mochi",
            () -> merchant("mochi", CuteMerchantEntity.Kind.MOCHI));
    public static final RegistryObject<EntityType<CuteMerchantEntity>> ESTRELLIN = ENTITIES.register("estrellin",
            () -> merchant("estrellin", CuteMerchantEntity.Kind.STAR));
    public static final RegistryObject<EntityType<CuteMerchantEntity>> TULI = ENTITIES.register("tuli",
            () -> merchant("tuli", CuteMerchantEntity.Kind.TULI));
    public static final RegistryObject<EntityType<LumiEntity>> LUMI = ENTITIES.register("lumi",
            () -> EntityType.Builder.<LumiEntity>of(LumiEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.8F).clientTrackingRange(10).build(ID + ":lumi"));
    public static final RegistryObject<EntityType<PompomEntity>> POMPOM = ENTITIES.register("pompom",
            () -> EntityType.Builder.<PompomEntity>of(PompomEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.9F).clientTrackingRange(10).build(ID + ":pompom"));

    private static EntityType<CuteMerchantEntity> merchant(String name, CuteMerchantEntity.Kind kind) {
        return EntityType.Builder.<CuteMerchantEntity>of((type, level) -> new CuteMerchantEntity(type, level, kind),
                MobCategory.CREATURE).sized(0.6F, 0.95F).clientTrackingRange(10).build(ID + ":" + name);
    }

    // ---- Objetos ----
    public static final RegistryObject<Item> MOCHI_FOOD = ITEMS.register("mochi",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4).saturationMod(0.6F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F).build())));
    public static final RegistryObject<Item> STRAWBERRY_MOCHI = ITEMS.register("strawberry_mochi",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).food(new FoodProperties.Builder()
                    .nutrition(6).saturationMod(0.8F)
                    .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1), 1.0F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F).build())));
    public static final RegistryObject<Item> BUBBLE_TEA = ITEMS.register("bubble_tea",
            () -> new BubbleTeaItem(new Item.Properties().stacksTo(16).food(new FoodProperties.Builder()
                    .nutrition(3).saturationMod(0.4F).alwaysEat()
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1), 1.0F)
                    .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0), 1.0F).build())));
    public static final RegistryObject<Item> WISH_COIN = ITEMS.register("wish_coin",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> FAIRY_DUST = ITEMS.register("fairy_dust",
            () -> new FairyDustItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> POMPOM_TREAT = ITEMS.register("pompom_treat",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STARDUST = ITEMS.register("stardust",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TELESCOPE_ITEM = ITEMS.register("telescope",
            () -> new BlockItem(TELESCOPE.get(), new Item.Properties()));
    public static final RegistryObject<Item> WISHING_FOUNTAIN_ITEM = ITEMS.register("wishing_fountain",
            () -> new BlockItem(WISHING_FOUNTAIN.get(), new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> MOCHI_EGG = ITEMS.register("mochi_spawn_egg",
            () -> new ForgeSpawnEggItem(MOCHI, 0xFFF1F4, 0xFF9CBB, new Item.Properties()));
    public static final RegistryObject<Item> ESTRELLIN_EGG = ITEMS.register("estrellin_spawn_egg",
            () -> new ForgeSpawnEggItem(ESTRELLIN, 0x2B2F6B, 0xFFE27A, new Item.Properties()));
    public static final RegistryObject<Item> TULI_EGG = ITEMS.register("tuli_spawn_egg",
            () -> new ForgeSpawnEggItem(TULI, 0xE9C58E, 0x7DBE7B, new Item.Properties()));
    public static final RegistryObject<Item> LUMI_EGG = ITEMS.register("lumi_spawn_egg",
            () -> new ForgeSpawnEggItem(LUMI, 0xD8F5E6, 0xFFB6E1, new Item.Properties()));
    public static final RegistryObject<Item> POMPOM_EGG = ITEMS.register("pompom_spawn_egg",
            () -> new ForgeSpawnEggItem(POMPOM, 0xFFF6E8, 0xFFB38A, new Item.Properties()));

    // ---- Pestaña creativa ----
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("cute_realms_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cute_realms"))
                    .icon(() -> new ItemStack(WISH_COIN.get()))
                    .displayItems((params, output) -> {
                        output.accept(MOCHI_EGG.get());
                        output.accept(ESTRELLIN_EGG.get());
                        output.accept(TULI_EGG.get());
                        output.accept(LUMI_EGG.get());
                        output.accept(POMPOM_EGG.get());
                        output.accept(MOCHI_FOOD.get());
                        output.accept(STRAWBERRY_MOCHI.get());
                        output.accept(BUBBLE_TEA.get());
                        output.accept(WISH_COIN.get());
                        output.accept(FAIRY_DUST.get());
                        output.accept(STARDUST.get());
                        output.accept(POMPOM_TREAT.get());
                        output.accept(TELESCOPE_ITEM.get());
                        output.accept(WISHING_FOUNTAIN_ITEM.get());
                    })
                    .build());

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        TABS.register(bus);
    }
}
