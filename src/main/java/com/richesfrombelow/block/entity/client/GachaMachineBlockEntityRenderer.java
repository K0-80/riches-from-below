package com.richesfrombelow.block.entity.client;

import com.richesfrombelow.block.GachaMachineBlock;
import com.richesfrombelow.block.entity.GachaMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class GachaMachineBlockEntityRenderer implements BlockEntityRenderer<GachaMachineBlockEntity> {

    private final BlockRenderManager blockRenderManager;

    public GachaMachineBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(GachaMachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        int activationTicks = entity.getActivationTicks();
        if (activationTicks > 0) {
            Random random = Random.create(entity.getPos().asLong());
            float shake = 0.025f;
            matrices.translate(random.nextFloat() * shake - shake / 2f, random.nextFloat() * shake - shake / 2f, random.nextFloat() * shake - shake / 2f);
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        BlockState bottomState = entity.getCachedState();
        BakedModel bottomModel = this.blockRenderManager.getModel(bottomState);
        this.blockRenderManager.getModelRenderer().render(matrices.peek(), vertexConsumer, bottomState, bottomModel, 1.0f, 1.0f, 1.0f, light, overlay);

        if (entity.getWorld() != null) {
            BlockPos topPos = entity.getPos().up();
            BlockState topState = entity.getWorld().getBlockState(topPos);

            if (topState.isOf(bottomState.getBlock()) && topState.get(GachaMachineBlock.HALF) == DoubleBlockHalf.UPPER) {
                matrices.push();
                matrices.translate(0.0, 1.0, 0.0);
                BakedModel topModel = this.blockRenderManager.getModel(topState);
                this.blockRenderManager.getModelRenderer().render(matrices.peek(), vertexConsumer, topState, topModel, 1.0f, 1.0f, 1.0f, light, overlay);
                matrices.pop();
            }
        }

        matrices.pop();
    }
}