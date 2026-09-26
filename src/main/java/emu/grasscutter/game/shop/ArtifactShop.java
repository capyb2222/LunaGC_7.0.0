package emu.grasscutter.game.shop;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.config.ConfigContainer.GameOptions.ArtifactShopOptions;
import emu.grasscutter.data.*;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.game.inventory.*;
import it.unimi.dsi.fastutil.ints.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.Getter;

public class ArtifactShop {
    private static final int GOODS_ID_BASE = 200_000_000;

    private static final List<EquipType> SLOT_ORDER =
            List.of(
                    EquipType.EQUIP_BRACER,
                    EquipType.EQUIP_NECKLACE,
                    EquipType.EQUIP_SHOES,
                    EquipType.EQUIP_RING,
                    EquipType.EQUIP_DRESS);

    @Getter private final Int2ObjectMap<ItemData> goods = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<ItemData> threeSubstatVariants = new Int2ObjectOpenHashMap<>();

    public void install(Int2ObjectMap<List<ShopInfo>> shopData) {
        var options = GAME_OPTIONS.artifactShop;
        this.goods.clear();
        shopData.values().forEach(list -> list.removeIf(sold -> sold.getGoodsId() >= GOODS_ID_BASE));
        if (!options.enabled) return;

        this.threeSubstatVariants.clear();
        var pieces = new ArrayList<ItemData>();
        this.catalog(pieces);
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

    public ItemData getPiece(int goodsId) {
        return this.goods.get(goodsId);
    }

    public GameItem roll(ItemData piece) {
        var variant = this.startingVariant(piece);
        var item = new GameItem(variant);

        int level = Math.min(Math.max(GAME_OPTIONS.artifactShop.artifactLevel, 0) + 1, variant.getMaxLevel());
        int upgrades = 0;
        int totalExp = 0;
        for (int lv = 2; lv <= level; lv++) {
            totalExp += GameData.getRelicExpRequired(variant.getRankLevel(), lv - 1);
            if (variant.canAddRelicProp(lv)) upgrades++;
        }

        item.setLevel(level);
        item.setTotalExp(totalExp);
        item.addAppendProps(upgrades);
        return item;
    }

    private ItemData startingVariant(ItemData piece) {
        if (ThreadLocalRandom.current().nextDouble() < GameDepot.getFourSubstatStartChance()) return piece;
        var threeSubstats = this.threeSubstatVariants.get(piece.getId());
        return threeSubstats != null ? threeSubstats : piece;
    }

    private void catalog(List<ItemData> pieces) {
        var fiveStars =
                GameData.getItemDataMap().values().stream()
                        .filter(data -> data.getItemType() == ItemType.ITEM_RELIQUARY)
                        .filter(data -> data.getRankLevel() == 5)
                        .toList();
        int affixDepot = mostCommon(fiveStars.stream().map(ItemData::getAppendPropDepotId).toList());
        Map<EquipType, Integer> mainDepot = new EnumMap<>(EquipType.class);
        fiveStars.stream()
                .collect(Collectors.groupingBy(ItemData::getEquipType))
                .forEach((slot, list) -> mainDepot.put(slot, mostCommon(list.stream().map(ItemData::getMainPropDepotId).toList())));

        Map<Long, ItemData> threeSubstats = new HashMap<>();
        for (ItemData data : fiveStars) {
            if (data.getAppendPropDepotId() != affixDepot) continue;
            if (!Objects.equals(data.getMainPropDepotId(), mainDepot.get(data.getEquipType()))) continue;
            var set = GameData.getReliquarySetDataMap().get(data.getSetId());
            if (set == null || set.getEquipAffixId() <= 0) continue;
            long key = ((long) data.getSetId() << 8) | data.getEquipType().getValue();
            if (data.getAppendPropNum() == 4) pieces.add(data);
            else if (data.getAppendPropNum() == 3) threeSubstats.putIfAbsent(key, data);
        }

        pieces.sort(
                Comparator.comparingInt(ItemData::getSetId)
                        .thenComparingInt(data -> SLOT_ORDER.indexOf(data.getEquipType())));
        for (ItemData piece : pieces) {
            long key = ((long) piece.getSetId() << 8) | piece.getEquipType().getValue();
            var three = threeSubstats.get(key);
            if (three != null) this.threeSubstatVariants.put(piece.getId(), three);
        }
    }

    private static int mostCommon(List<Integer> values) {
        return values.stream()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
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
        var costs = new ArrayList<ItemParamData>(1);
        if (options.costItemId > 0 && options.costItemCount > 0) {
            costs.add(new ItemParamData(options.costItemId, options.costItemCount));
        }
        goods.setCostItemList(costs);
        return goods;
    }
}
