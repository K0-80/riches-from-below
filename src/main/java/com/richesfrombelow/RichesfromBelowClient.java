package com.richesfrombelow;

import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.block.entity.SlotMachineBlockEntity;
import com.richesfrombelow.block.entity.client.model.SlotMachineModel;
import com.richesfrombelow.block.entity.client.renderer.GachaMachineBlockEntityRenderer;
import com.richesfrombelow.block.entity.client.renderer.SlotMachineBlockEntityRenderer;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.client.DeckOfFatesAnimationRenderer;
import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.entities.client.GatchaBallModel;
import com.richesfrombelow.entities.client.GatchaBallRenderer;
import com.richesfrombelow.entities.client.ModModelLayers;
import com.richesfrombelow.items.ModItems;
import com.richesfrombelow.networking.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;

public class RichesfromBelowClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ModPackets.registerS2CPackets();
        ClientTickEvents.END_CLIENT_TICK.register(client -> DeckOfFatesAnimationRenderer.tick());
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> DeckOfFatesAnimationRenderer.render(drawContext, renderTickCounter.getTickDelta(true)));

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GATCHA_BALL, GatchaBallModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.GATCHA_BALL, GatchaBallRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.GACHA_MACHINE_BLOCK_ENTITY_TYPE, GachaMachineBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GACHA_MACHINE_BLOCK, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.SLOT_MACHINE, SlotMachineBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SLOT_MACHINE, SlotMachineModel::getTexturedModelData);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLOT_MACHINE, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PERSONAL_VAULT, RenderLayer.getCutout());

    }

    public static void drawCardAnimation(int cardId) {
        ItemStack cardStack = new ItemStack(ModItems.DECK_OF_FATES_ANIMATION);
        cardStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(cardId));
        DeckOfFatesAnimationRenderer.showAnimation(cardStack);


    }
}
