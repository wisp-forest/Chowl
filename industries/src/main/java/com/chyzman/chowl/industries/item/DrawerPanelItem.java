package com.chyzman.chowl.industries.item;

import com.chyzman.chowl.core.panel.StoragePanel;
import com.chyzman.chowl.core.panel.FilteringPanel;
import com.chyzman.chowl.core.panel.PanelItem;
import com.chyzman.chowl.core.panel.UpgradeablePanel;
import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.core.util.BigIntUtils;
import com.chyzman.chowl.industries.transfer.BigSingleSlotStorage;
import com.chyzman.chowl.industries.transfer.PanelStorage;
import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import com.chyzman.chowl.industries.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

import static com.chyzman.chowl.industries.Chowl.*;

public class DrawerPanelItem extends BasePanelItem implements PanelItem<ItemVariant>, FilteringPanel<ItemVariant>, StoragePanel<ItemVariant>, UpgradeablePanel {
    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    // region STORAGE

    @Override
    public BigInteger count(ItemStack panel, ItemVariant type) {
        if (isPartOfFilter(panel,type)) return panel.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO);
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger capacity(ItemStack panel, ItemVariant type) {
        if (isPartOfFilter(panel,type)) return BigIntUtils.powOf2(baseCapacity(), StoragePanel.capacityTier(panel));
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_capacity.drawer());
    }
    // endregion

    // region FILTER

    @Override
    public boolean isPartOfFilter(ItemStack panel, ItemVariant target) {
        var variant = panel.getOrDefault(ChowlComponents.VARIANT_FILTER, ItemVariant.blank());
        return variant.isBlank() || variant.equals(target);
    }

    @Override
    public boolean canAddToFilter(ItemStack panel, ItemVariant target) {
        return panel.getOrDefault(ChowlComponents.VARIANT_FILTER, ItemVariant.blank()).isBlank();
    }

    @Override
    public void setFilter(ItemStack panel, ItemVariant newFilter) {
        panel.set(ChowlComponents.VARIANT_FILTER, newFilter);
    }

    // endregion
}
