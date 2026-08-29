package emu.grasscutter.game.shop;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.config.ConfigContainer.GameOptions.ArtifactShopOptions;
import emu.grasscutter.data.*;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.reliquary.ReliquaryMainPropData;
import emu.grasscutter.game.inventory.*;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.utils.objects.WeightedList;
import it.unimi.dsi.fastutil.ints.*;
import java.util.*;
import lombok.Getter;

/**
 * Lists every official 5-star artifact piece as shop goods. Buying one hands out a freshly rolled
 * artifact rather than a fixed one: the main stat comes from the slot's real pool and the substats
 * from the excel affix table, so every number on the piece is one the game itself would print. The
 * odds are what is bent - towards crit, damage, and the higher end of each roll.
 */
public class ArtifactShop {
    /** Well clear of the ~101,070,304 the excel goods ids reach. */
    private static final int GOODS_ID_BASE = 200_000_000;

    /** The substat pool every 5-star piece draws from. */
    private static final int FIVE_STAR_AFFIX_DEPOT = 501;

    /** Flower, plume, sands, goblet, circlet - the order the bag shows them in. */
    private static final List<EquipType> SLOT_ORDER =
            List.of(
                    EquipType.EQUIP_BRACER,
                    EquipType.EQUIP_NECKLACE,
                    EquipType.EQUIP_SHOES,
                    EquipType.EQUIP_RING,
                    EquipType.EQUIP_DRESS);

    /**
     * The main stat each slot can actually roll at 5 stars, with the game's own odds.
     *
     * <p>The pool has to be spelled out because the shipped ReliquaryMainPropExcelConfigData is
     * flattened - every entry in every depot carries the same weight, and the depots hold stats the
     * slot never rolls - so drawing from one straight gives you a Sands of Eon with flat HP on it.
     */
    private static final Map<EquipType, Map<FightProperty, Double>> MAIN_STATS =
            Map.of(
                    EquipType.EQUIP_BRACER, Map.of(FightProperty.FIGHT_PROP_HP, 100d),
                    EquipType.EQUIP_NECKLACE, Map.of(FightProperty.FIGHT_PROP_ATTACK, 100d),
                    EquipType.EQUIP_SHOES,
                            Map.of(
                                    FightProperty.FIGHT_PROP_HP_PERCENT, 26.68d,
                                    FightProperty.FIGHT_PROP_ATTACK_PERCENT, 26.66d,
                                    FightProperty.FIGHT_PROP_DEFENSE_PERCENT, 26.66d,
                                    FightProperty.FIGHT_PROP_CHARGE_EFFICIENCY, 10d,
                                    FightProperty.FIGHT_PROP_ELEMENT_MASTERY, 10d),
                    EquipType.EQUIP_RING,
                            Map.ofEntries(
                                    Map.entry(FightProperty.FIGHT_PROP_HP_PERCENT, 19.25d),
                                    Map.entry(FightProperty.FIGHT_PROP_ATTACK_PERCENT, 19.25d),
                                    Map.entry(FightProperty.FIGHT_PROP_DEFENSE_PERCENT, 19d),
                                    Map.entry(FightProperty.FIGHT_PROP_FIRE_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_ELEC_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_WATER_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_ICE_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_WIND_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_ROCK_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_GRASS_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_PHYSICAL_ADD_HURT, 5d),
                                    Map.entry(FightProperty.FIGHT_PROP_ELEMENT_MASTERY, 2.5d)),
                    EquipType.EQUIP_DRESS,
                            Map.of(
                                    FightProperty.FIGHT_PROP_HP_PERCENT, 22d,
                                    FightProperty.FIGHT_PROP_ATTACK_PERCENT, 22d,
                                    FightProperty.FIGHT_PROP_DEFENSE_PERCENT, 22d,
                                    FightProperty.FIGHT_PROP_CRITICAL, 10d,
                                    FightProperty.FIGHT_PROP_CRITICAL_HURT, 10d,
                                    FightProperty.FIGHT_PROP_HEAL_ADD, 10d,
                                    FightProperty.FIGHT_PROP_ELEMENT_MASTERY, 4d));

    /** The piece behind each of our goods ids. Empty while the shop is switched off. */
    @Getter private final Int2ObjectMap<ItemData> goods = new Int2ObjectOpenHashMap<>();

    /**
     * Appends the artifact goods to the configured shop, replacing any listed by an earlier call.
     *
     * <p>Safe to call more than once, and it has to be: the shop system is built before the
     * resources are loaded, so the first attempt finds no artifacts to list.
     */
    public void install(Int2ObjectMap<List<ShopInfo>> shopData) {
        var options = GAME_OPTIONS.artifactShop;
        this.goods.clear();
        shopData.values().forEach(list -> list.removeIf(sold -> sold.getGoodsId() >= GOODS_ID_BASE));
        if (!options.enabled) return;

        var pieces = catalog();
        if (pieces.isEmpty()) return;

        var items = shopData.computeIfAbsent(options.shopId, k -> new ArrayList<ShopInfo>());
        int goodsId = GOODS_ID_BASE;
        for (ItemData piece : pieces) {
            items.add(makeGoods(goodsId, piece, options));
            this.goods.put(goodsId, piece);
            goodsId++;
        }

        Grasscutter.getLogger()
                .info("Listed {} 5-star artifacts in shop {}.", pieces.size(), options.shopId);
    }

    /** The piece this goods id sells, or null when the id is not one of ours. */
    public ItemData getPiece(int goodsId) {
        return this.goods.get(goodsId);
    }

    /** Rolls a piece the way a domain drop would, then levels it and applies the configured bias. */
    public GameItem roll(ItemData piece) {
        var options = GAME_OPTIONS.artifactShop;
        var item = new GameItem(piece);

        // The main stat has to be settled first: a substat never repeats it.
        int mainPropId = rollMainProp(piece, options);
        if (mainPropId > 0) {
            item.setMainPropId(mainPropId);
        }

        // A piece starts with its own substat count and gains one at every level in addPropLevels,
        // which is what turns four substats into nine by +20.
        int level = Math.min(Math.max(options.artifactLevel, 0) + 1, piece.getMaxLevel());
        int substats = piece.getAppendPropNum();
        int totalExp = 0;
        for (int lv = 2; lv <= level; lv++) {
            totalExp += GameData.getRelicExpRequired(piece.getRankLevel(), lv - 1);
            if (piece.canAddRelicProp(lv)) substats++;
        }

        item.setLevel(level);
        item.setTotalExp(totalExp);
        item.getAppendPropIdList().clear();
        item.addAppendProps(substats, bias(options));
        return item;
    }

    /** Every official 5-star piece: the four-substat variant of each slot of each real set. */
    private static List<ItemData> catalog() {
        var pieces = new ArrayList<ItemData>();
        for (ItemData data : GameData.getItemDataMap().values()) {
            if (data.getItemType() != ItemType.ITEM_RELIQUARY || data.getRankLevel() != 5) continue;
            // Each piece exists five times over, once per starting substat count. A 5-star out of a
            // domain starts with three or four; four is the one worth selling.
            if (data.getAppendPropNum() != 4) continue;
            if (data.getAppendPropDepotId() != FIVE_STAR_AFFIX_DEPOT) continue;
            // Skips the constrained depots the special drops use, leaving one entry per slot.
            if (data.getMainPropDepotId() != mainPropDepot(data.getEquipType())) continue;
            // Beta and test sets carry no set bonus; every released set does.
            var set = GameData.getReliquarySetDataMap().get(data.getSetId());
            if (set == null || set.getEquipAffixId() <= 0) continue;
            pieces.add(data);
        }

        pieces.sort(
                Comparator.comparingInt(ItemData::getSetId)
                        .thenComparingInt(data -> SLOT_ORDER.indexOf(data.getEquipType())));
        return pieces;
    }

    private static ShopInfo makeGoods(int goodsId, ItemData piece, ArtifactShopOptions options) {
        var goods = new ShopInfo();
        goods.setGoodsId(goodsId);
        goods.setGoodsItem(new ItemParamData(piece.getId(), 1));
        goods.setScoin(options.costMora);
        goods.setHcoin(options.costPrimogems);
        goods.setBuyLimit(options.buyLimit);
        goods.setMinLevel(1);
        goods.setMaxLevel(99);
        // Mutable on purpose: removeVirtualCosts walks this with removeIf.
        var costs = new ArrayList<ItemParamData>(1);
        if (options.costItemId > 0 && options.costItemCount > 0) {
            costs.add(new ItemParamData(options.costItemId, options.costItemCount));
        }
        goods.setCostItemList(costs);
        return goods;
    }

    private static int rollMainProp(ItemData piece, ArtifactShopOptions options) {
        var pool = MAIN_STATS.get(piece.getEquipType());
        var candidates = GameDepot.getRelicMainPropList(piece.getMainPropDepotId());
        if (pool == null || candidates == null) return 0;

        var randomList = new WeightedList<ReliquaryMainPropData>();
        for (ReliquaryMainPropData prop : candidates) {
            double weight = pool.getOrDefault(prop.getFightProp(), 0d);
            if (weight > 0) {
                randomList.add(weight * statWeight(prop.getFightProp(), options), prop);
            }
        }
        return randomList.size() == 0 ? 0 : randomList.next().getId();
    }

    private static ArtifactRollBias bias(ArtifactShopOptions options) {
        return affix -> {
            double weight = statWeight(affix.getFightProp(), options);
            int tiers = GameDepot.getRelicAffixValueTierCount(affix);
            if (tiers > 1 && options.highRollBias > 0) {
                double height = GameDepot.getRelicAffixValueTier(affix) / (double) (tiers - 1);
                weight *= 1 + options.highRollBias * height;
            }
            return weight;
        };
    }

    private static double statWeight(FightProperty prop, ArtifactShopOptions options) {
        return switch (prop) {
            case FIGHT_PROP_CRITICAL, FIGHT_PROP_CRITICAL_HURT -> options.critWeight;
            case FIGHT_PROP_ATTACK_PERCENT,
                    FIGHT_PROP_ELEMENT_MASTERY,
                    FIGHT_PROP_FIRE_ADD_HURT,
                    FIGHT_PROP_ELEC_ADD_HURT,
                    FIGHT_PROP_WATER_ADD_HURT,
                    FIGHT_PROP_GRASS_ADD_HURT,
                    FIGHT_PROP_WIND_ADD_HURT,
                    FIGHT_PROP_ROCK_ADD_HURT,
                    FIGHT_PROP_ICE_ADD_HURT,
                    FIGHT_PROP_PHYSICAL_ADD_HURT -> options.damageWeight;
            default -> 1;
        };
    }

    private static int mainPropDepot(EquipType slot) {
        return switch (slot) {
            case EQUIP_SHOES -> 1000;
            case EQUIP_NECKLACE -> 2000;
            case EQUIP_DRESS -> 3000;
            case EQUIP_BRACER -> 4000;
            case EQUIP_RING -> 5000;
            default -> 0;
        };
    }
}
