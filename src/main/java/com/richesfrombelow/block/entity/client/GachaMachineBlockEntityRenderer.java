package com.richesfrombelow.block.entity.client;

import com.richesfrombelow.block.GachaMachineBlock;
import com.richesfrombelow.block.entity.GachaMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.RotationAxis;


public class GachaMachineBlockEntityRenderer implements BlockEntityRenderer<GachaMachineBlockEntity> {

    private final BlockRenderManager blockRenderManager;
    private final Random random = Random.create();

    public GachaMachineBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(GachaMachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        int activationTicks = entity.getActivationTicks();
        if (activationTicks > 0) {
            float totalAnimationTime = (float) entity.getTotalAnimationTicks();
            float progress = totalAnimationTime - (float) activationTicks + tickDelta;

            float shakeStartTime = 10.0f;
            float shakeEndTime = totalAnimationTime - 10.0f;

            if (progress >= shakeStartTime && progress < shakeEndTime) { //shake animation
                float shakeDuration = shakeEndTime - shakeStartTime;
                float shakeProgress = progress - shakeStartTime;
                float shakeProgressRatio = shakeProgress / shakeDuration;
                //chjat am i cooked i had to make gpt generate this :sob:
                // --- Angle Scaling ---
                // The maximum tilt angle increases as the animation progresses.
                float startAngle = 2.0f;
                float endAngle = 10.0f;
                float currentMaxAngle = startAngle + (endAngle - startAngle) * shakeProgressRatio;

                // --- Frequency Scaling ---
                // The number of shakes per second also increases.
                float startShakesPerTick = 2.0f / 20.0f; // Start at 2 shakes per second
                float endShakesPerTick = 8.0f / 20.0f;   // End at 8 shakes per second

                // The phase is the integral of the frequency over time.
                // This results in a quadratic function of time, making the oscillation speed up smoothly.
                float phase = 2.0f * (float)Math.PI * (
                        startShakesPerTick * shakeProgress +
                                (endShakesPerTick - startShakesPerTick) / (2.0f * shakeDuration) * shakeProgress * shakeProgress
                );

                float angle = currentMaxAngle * (float) Math.sin(phase);

                matrices.translate(0.5, 0.0, 0.5);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
                matrices.translate(-0.5, 0.0, -0.5);

            } else if (progress >= shakeEndTime) { //expand animation
                float scale = getScale(shakeEndTime, progress);

                matrices.translate(0.5, 0.0, 0.5);
                matrices.scale(scale, scale, scale);
                matrices.translate(-0.5, 0.0, -0.5);
            }
        }

        BlockState bottomState = entity.getCachedState();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(bottomState));
        this.blockRenderManager.renderBlock(bottomState, entity.getPos(), entity.getWorld(), matrices, vertexConsumer, false, this.random);

        if (entity.getWorld() != null) {
            BlockPos topPos = entity.getPos().up();
            BlockState topState = entity.getWorld().getBlockState(topPos);

            if (topState.isOf(bottomState.getBlock()) && topState.get(GachaMachineBlock.HALF) == DoubleBlockHalf.UPPER) {
                matrices.push();
                matrices.translate(0.0, 1.0, 0.0);
                this.blockRenderManager.renderBlock(topState, topPos, entity.getWorld(), matrices, vertexConsumer, false, this.random);
                matrices.pop();
            }
        }

        matrices.pop();
    }

    private static float getScale(float shakeEndTime, float progress) {
        float scale = 1.0f;
        float shrinkPhaseEnd = shakeEndTime + 2.0f;
        float expandPhaseEnd = shrinkPhaseEnd + 3.0f;
        float returnPhaseEnd = expandPhaseEnd + 5.0f;

        float minScale = 0.7f;
        float maxScale = 1.3f;

        if (progress < shrinkPhaseEnd) { // Shrink
            float phaseProgress = (progress - shakeEndTime) / (shrinkPhaseEnd - shakeEndTime);
            scale = 1.0f + phaseProgress * (minScale - 1.0f);
        } else if (progress < expandPhaseEnd) { // Expand
            float phaseProgress = (progress - shrinkPhaseEnd) / (expandPhaseEnd - shrinkPhaseEnd);
            scale = minScale + phaseProgress * (maxScale - minScale);
        } else if (progress < returnPhaseEnd) { // Return to normal
            float phaseProgress = (progress - expandPhaseEnd) / (returnPhaseEnd - expandPhaseEnd);
            scale = maxScale + phaseProgress * (1.0f - maxScale);
        }
        return scale;
    }
}