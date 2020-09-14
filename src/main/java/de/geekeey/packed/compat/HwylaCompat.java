package de.geekeey.packed.compat;

import de.geekeey.packed.block.VariantCrateBlock;
import de.geekeey.packed.block.entity.VariantCrateBlockEntity;
import mcp.mobius.waila.api.*;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class HwylaCompat implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerBlockDataProvider(this::appendData, VariantCrateBlockEntity.class);
        registrar.registerComponentProvider(new ComponentProvider(), TooltipPosition.TAIL, VariantCrateBlock.class);
    }

    void appendData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity t) {
        if (t instanceof VariantCrateBlockEntity) {
            VariantCrateBlockEntity blockEntity = (VariantCrateBlockEntity) t;

            int maxStacks = blockEntity.getTier().getStackAmount();
            int itemCount = blockEntity.getItemAmount();
            String item = Registry.ITEM.getId(blockEntity.getItem()).toString();

            CompoundTag subTag = new CompoundTag();
            subTag.putInt("MaxStacks", maxStacks);
            subTag.putInt("Count", itemCount);
            subTag.putString("Item", item);

            data.put("Packed", subTag);
        }
    }

    private static class ComponentProvider implements IComponentProvider {
        @Override
        public void appendTail(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
            CompoundTag serverData = accessor.getServerData();
            if (serverData.contains("Packed", NbtType.COMPOUND)) {
                //We assume all these tags are present
                CompoundTag packed = serverData.getCompound("Packed");
                int maxStacks = packed.getInt("MaxStacks");
                int count = packed.getInt("Count");
                //The Item is transferred as an Identifier String
                Item item = Registry.ITEM.get(Identifier.tryParse(packed.getString("Item")));

                tooltip.add(Text.of("Size: " + maxStacks + " Stacks"));

                if (count > 0) {
                    int stackSize = item.getMaxCount();
                    int fullStacks = count / stackSize;
                    int rest = count % stackSize;

                    if (fullStacks > 0)
                        if (rest > 0)
                            tooltip.add(Text.of("Count: " + fullStacks + "x" + stackSize + " + " + rest));
                        else
                            tooltip.add(Text.of("Count: " + fullStacks + "x" + stackSize));
                    else
                        tooltip.add(Text.of("Count: " + rest));

                    tooltip.add(new LiteralText("Item: ").append(item.getName()));
                }
            }
        }
    }
}
