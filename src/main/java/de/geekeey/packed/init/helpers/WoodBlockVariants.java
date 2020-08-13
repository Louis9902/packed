package de.geekeey.packed.init.helpers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiFunction;

public class WoodBlockVariants<T, B extends Block> implements Iterable<B> {

    public final ImmutableMap<WoodVariant, B> variants;

    public WoodBlockVariants(T tier, BiFunction<T, WoodVariant, Identifier> id, BiFunction<T, WoodVariant, B> factory) {
        ImmutableMap.Builder<WoodVariant, B> builder = ImmutableMap.builder();
        for (var variant : WoodVariants.values()) {
            var identifier = id.apply(tier, variant);
            var block = factory.apply(tier, variant);
            Registry.register(Registry.BLOCK, identifier, block);
            builder.put(variant, block);
        }
        variants = builder.build();
    }

    public B oak() {
        return variants.get(WoodVariants.OAK);
    }

    public B spruce() {
        return variants.get(WoodVariants.SPRUCE);
    }

    public B birch() {
        return variants.get(WoodVariants.BIRCH);
    }

    public B acacia() {
        return variants.get(WoodVariants.ACACIA);
    }

    public B jungle() {
        return variants.get(WoodVariants.JUNGLE);
    }

    public B darkOak() {
        return variants.get(WoodVariants.DARK_OAK);
    }

    public B crimson() {
        return variants.get(WoodVariants.CRIMSON);
    }

    public B warped() {
        return variants.get(WoodVariants.WARPED);
    }

    @NotNull
    @Override
    public Iterator<B> iterator() {
        return variants.values().iterator();
    }
}

