package com.richesfrombelow;

import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.block.entity.client.GachaMachineBlockEntityRenderer;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.entities.client.GatchaBallModel;
import com.richesfrombelow.entities.client.GatchaBallRenderer;
import com.richesfrombelow.entities.client.ModModelLayers;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class RichesfromBelowClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GATCHA_BALL, GatchaBallModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.GATCHA_BALL, GatchaBallRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.GACHA_MACHINE_BLOCK_ENTITY_TYPE, GachaMachineBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GACHA_MACHINE_BLOCK, RenderLayer.getCutout());

    }
}
