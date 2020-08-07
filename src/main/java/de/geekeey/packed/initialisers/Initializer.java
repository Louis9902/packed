package de.geekeey.packed.initialisers;

import de.geekeey.packed.registry.BlockEntities;
import de.geekeey.packed.registry.Blocks;
import de.geekeey.packed.registry.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Initializer implements ModInitializer {

    public static final String ID = "packed";

    public static final ItemGroup packed = FabricItemGroupBuilder.build(new Identifier(ID,ID),() -> new ItemStack(Items.BARREL_DEFAULT_TIER.oak));

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        BlockEntities.register();
        Blocks.register();
        Items.register();


    }
}
