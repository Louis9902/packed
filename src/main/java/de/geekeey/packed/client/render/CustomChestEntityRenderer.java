package de.geekeey.packed.client.render;

import com.google.common.collect.ImmutableMap;
import de.geekeey.packed.Packed;
import de.geekeey.packed.block.CustomChest;
import de.geekeey.packed.block.entity.CustomChestEntity;
import de.geekeey.packed.init.helpers.ChestTier;
import de.geekeey.packed.init.helpers.ChestTiers;
import de.geekeey.packed.init.helpers.WoodVariant;
import de.geekeey.packed.init.helpers.WoodVariants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CustomChestEntityRenderer extends BlockEntityRenderer<CustomChestEntity> {
    private static final Identifier CHEST_ATLAS_TEXTURE = TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

    private static final SpriteIdentifier CLINCH_DEFAULT = createChestTextureId(ChestTiers.DEFAULT.identifier());
    private static final SpriteIdentifier CLINCH_TIER_1 = createChestTextureId(ChestTiers.TIER_1.identifier());
    private static final SpriteIdentifier CLINCH_TIER_2 = createChestTextureId(ChestTiers.TIER_2.identifier());
    private static final SpriteIdentifier CLINCH_TIER_3 = createChestTextureId(ChestTiers.TIER_3.identifier());

    private static final ImmutableMap<WoodVariant, ChestTextureSprites> WOOD_VARIANT_SPRITES;

    static {
        ImmutableMap.Builder<WoodVariant, ChestTextureSprites> builder = ImmutableMap.builder();
        for (WoodVariant variant : WoodVariants.values()) {
            builder.put(variant, new ChestTextureSprites(variant));
        }
        WOOD_VARIANT_SPRITES = builder.build();
    }

    private final ModelPart headerSingleModel;
    private final ModelPart footerSingleModel;
    private final ModelPart clinchSingleModel;

    private final ModelPart headerRightModel;
    private final ModelPart footerRightModel;
    private final ModelPart clinchRightModel;

    private final ModelPart headerLeftModel;
    private final ModelPart footerLeftModel;
    private final ModelPart clinchLeftModel;

    private final Block fallback;

    public CustomChestEntityRenderer(BlockEntityRenderDispatcher dispatcher, Block fallback) {
        super(dispatcher);
        this.fallback = fallback;

        this.footerSingleModel = new ModelPart(64, 64, 0, 19);
        this.footerSingleModel.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);

        this.headerSingleModel = new ModelPart(64, 64, 0, 0);
        this.headerSingleModel.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.headerSingleModel.pivotY = 9.0F;
        this.headerSingleModel.pivotZ = 1.0F;

        this.clinchSingleModel = new ModelPart(16, 16, 0, 0);
        this.clinchSingleModel.addCuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.clinchSingleModel.pivotY = 8.0F;

        this.footerRightModel = new ModelPart(64, 64, 0, 19);
        this.footerRightModel.addCuboid(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);

        this.headerRightModel = new ModelPart(64, 64, 0, 0);
        this.headerRightModel.addCuboid(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.headerRightModel.pivotY = 9.0F;
        this.headerRightModel.pivotZ = 1.0F;

        this.clinchRightModel = new ModelPart(16, 16, 8, 0);
        this.clinchRightModel.addCuboid(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.clinchRightModel.pivotY = 8.0F;

        this.footerLeftModel = new ModelPart(64, 64, 0, 19);
        this.footerLeftModel.addCuboid(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);

        this.headerLeftModel = new ModelPart(64, 64, 0, 0);
        this.headerLeftModel.addCuboid(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.headerLeftModel.pivotY = 9.0F;
        this.headerLeftModel.pivotZ = 1.0F;

        this.clinchLeftModel = new ModelPart(16, 16, 0, 8);
        this.clinchLeftModel.addCuboid(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.clinchLeftModel.pivotY = 8.0F;
    }

    private static SpriteIdentifier createChestTextureId(Identifier path) {
        return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Packed.id("entity/chest/" + path.getPath()));
    }

    private static SpriteIdentifier forChestTier(ChestTier tier) {
        if (tier instanceof ChestTiers) {
            ChestTiers tiers = (ChestTiers) tier;
            switch (tiers) {
                case DEFAULT:
                    return CLINCH_DEFAULT;
                case TIER_1:
                    return CLINCH_TIER_1;
                case TIER_2:
                    return CLINCH_TIER_2;
                case TIER_3:
                    return CLINCH_TIER_3;
            }
        }
        return CLINCH_DEFAULT;
    }

    private static class ChestTextureSprites {
        public final SpriteIdentifier normal;
        public final SpriteIdentifier left;
        public final SpriteIdentifier right;

        public ChestTextureSprites(WoodVariant variant) {
            left = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Packed.id("entity/chest/" + variant.identifier() + "/normal_left"));
            right = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Packed.id("entity/chest/" + variant.identifier() + "/normal_right"));
            normal = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Packed.id("entity/chest/" + variant.identifier() + "/normal"));
        }
    }

    public void render(CustomChestEntity entity, float delta, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay) {
        World world = entity.getWorld();
        boolean inWorld = world != null;

        BlockState state = inWorld ? entity.getCachedState() : fallback.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType type = state.contains(ChestBlock.CHEST_TYPE) ? state.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;

        Block block = state.getBlock();

        if (block instanceof CustomChest) {
            CustomChest chest = (CustomChest) block;
            matrices.push();

            float facing = state.get(ChestBlock.FACING).asRotation();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-facing));
            matrices.translate(-0.5D, -0.5D, -0.5D);

            DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> source;
            if (inWorld) {
                source = chest.getBlockEntitySource(state, world, entity.getPos(), true);
            } else {
                source = DoubleBlockProperties.PropertyRetriever::getFallback;
            }

            float g = source.apply(ChestBlock.getAnimationProgressRetriever(entity)).get(delta);
            g = 1.0F - g;
            g = 1.0F - g * g * g;
            int i = source.apply(new LightmapCoordinatesRetriever<>()).applyAsInt(light);

            SpriteIdentifier foundation = fromWoodVariant(chest.getVariant(), type);
            VertexConsumer verticesFoundation = foundation.getVertexConsumer(vertices, RenderLayer::getEntityCutout);

            SpriteIdentifier clinch = forChestTier(entity.getTier());
            VertexConsumer verticesClinch = clinch.getVertexConsumer(vertices, RenderLayer::getEntityCutout);

            if (type == ChestType.SINGLE) {
                this.renderParts(matrices, verticesFoundation, verticesClinch, this.headerSingleModel, this.clinchSingleModel, this.footerSingleModel, g, i, overlay);
            } else {
                if (type == ChestType.LEFT) {
                    this.renderParts(matrices, verticesFoundation, verticesClinch, this.headerLeftModel, this.clinchLeftModel, this.footerLeftModel, g, i, overlay);
                } else {
                    this.renderParts(matrices, verticesFoundation, verticesClinch, this.headerRightModel, this.clinchRightModel, this.footerRightModel, g, i, overlay);
                }
            }

            matrices.pop();
        }
    }

    private void renderParts(MatrixStack matrices, VertexConsumer chestVertices, VertexConsumer lockVertices, ModelPart header, ModelPart clinch, ModelPart footer, float f, int light, int overlay) {
        header.pitch = -(f * 1.5707964F);
        clinch.pitch = header.pitch;
        header.render(matrices, chestVertices, light, overlay);
        clinch.render(matrices, lockVertices, light, overlay);
        footer.render(matrices, chestVertices, light, overlay);
    }

    private static SpriteIdentifier fromWoodVariant(WoodVariant chest, ChestType type) {
        ChestTextureSprites sprites = WOOD_VARIANT_SPRITES.get(chest);
        switch (type) {
            case LEFT:
                return sprites.left;
            case RIGHT:
                return sprites.right;
            case SINGLE:
            default:
                return sprites.normal;
        }
    }

}
