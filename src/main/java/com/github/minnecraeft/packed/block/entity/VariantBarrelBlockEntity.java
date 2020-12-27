package com.github.minnecraeft.packed.block.entity;

import com.github.minnecraeft.packed.block.misc.Upgradable;
import com.github.minnecraeft.packed.init.PackedEntities;
import com.github.minnecraeft.packed.init.helpers.StorageTier;
import com.github.minnecraeft.packed.init.helpers.WoodVariant;
import com.github.minnecraeft.packed.screen.ExtendedGenericContainerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

public class VariantBarrelBlockEntity extends BarrelBlockEntity implements ExtendedScreenHandlerFactory, Upgradable {

    private StorageTier tier;
    private WoodVariant variant;

    public VariantBarrelBlockEntity() {
        super(PackedEntities.CUSTOM_BARREL);
    }

    public VariantBarrelBlockEntity(@NotNull StorageTier tier, @NotNull WoodVariant variant) {
        super(PackedEntities.CUSTOM_BARREL);
        this.tier = tier;
        this.variant = variant;
        setInvStackList(createInventory(tier));
    }

    private static DefaultedList<ItemStack> createInventory(StorageTier tier) {
        return DefaultedList.ofSize(tier.getInventoryWidth() * tier.getInventoryHeight(), ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return getTier().getInventoryWidth() * getTier().getInventoryHeight();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        if (tag.contains("tier", 8)) {
            StorageTier tier = StorageTier.REGISTRY.get(new Identifier(tag.getString("tier")));
            if (tier != null)
                setTier(tier);
        }
        if (tag.contains("variant", 8)) {
            WoodVariant variant = WoodVariant.REGISTRY.get(new Identifier(tag.getString("variant")));
            if (variant != null)
                setVariant(variant);
        }
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("tier", getTier().getIdentifier().toString());
        tag.putString("variant", getVariant().getIdentifier().toString());
        return tag;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(getTier().getInventoryHeight());
        buf.writeInt(getTier().getInventoryWidth());
    }

    @Override
    protected ScreenHandler createScreenHandler(int id, PlayerInventory inventory) {
        int rows = getTier().getInventoryHeight();
        int columns = getTier().getInventoryWidth();
        return new ExtendedGenericContainerScreenHandler(id, inventory, this, rows, columns);
    }

    protected Text getContainerName() {
        Text name = getCustomName();
        return name != null ? name : new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    public @NotNull StorageTier getTier() {
        return tier;
    }

    public void setTier(@NotNull StorageTier tier) {
        setInvStackList(Upgradable.resize(getInvStackList(), tier.getInventoryHeight() * tier.getInventoryWidth()));
        this.tier = tier;
    }

    public @NotNull WoodVariant getVariant() {
        return variant;
    }

    public void setVariant(WoodVariant variant) {
        this.variant = variant;
    }
}
