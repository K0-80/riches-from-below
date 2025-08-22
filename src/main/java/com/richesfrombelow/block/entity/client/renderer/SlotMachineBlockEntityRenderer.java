package com.richesfrombelow.block.entity.client.renderer;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.SlotMachineBlock;
import com.richesfrombelow.block.entity.SlotMachineBlockEntity;
import net.minecraft.client.render.WorldRenderer;

import com.richesfrombelow.block.entity.client.ModModelLayers;
import com.richesfrombelow.block.entity.client.model.SlotMachineModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class SlotMachineBlockEntityRenderer implements BlockEntityRenderer<SlotMachineBlockEntity> {
    private static final Identifier TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/block/slot_machine.png");
    private final SlotMachineModel model;

    public SlotMachineBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new SlotMachineModel(ctx.getLayerModelPart(ModModelLayers.SLOT_MACHINE));
    }

    @Override
    public void render(SlotMachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        float rotation = entity.getCachedState().get(SlotMachineBlock.FACING).asRotation() ;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        assert entity.getWorld() != null;
        int worldLight = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

        model.setAngles(entity, tickDelta);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), worldLight, overlay);

        matrices.pop();
    }

}
