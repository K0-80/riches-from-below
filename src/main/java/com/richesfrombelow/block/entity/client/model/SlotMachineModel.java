package com.richesfrombelow.block.entity.client.model;
import com.richesfrombelow.block.entity.SlotMachineBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SlotMachineModel extends Model {
    private final ModelPart lever;
    private final ModelPart main;
    private final ModelPart wheel1;
    private final ModelPart wheel2;
    private final ModelPart wheel3;

    public SlotMachineModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.lever = root.getChild("lever");
        this.main = root.getChild("main");
        this.wheel1 = root.getChild("wheel1");
        this.wheel2 = root.getChild("wheel2");
        this.wheel3 = root.getChild("wheel3");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(102, 73).cuboid(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F))
                .uv(65, 0).cuboid(-6.0F, 0.0F, -5.0F, 12.0F, 3.0F, 13.0F, new Dilation(0.0F))
                .uv(65, 34).cuboid(6.0F, -8.0F, -8.0F, 2.0F, 11.0F, 16.0F, new Dilation(0.0F))
                .uv(102, 34).cuboid(-6.0F, -3.0F, -5.0F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F))
                .uv(0, 73).cuboid(-6.0F, -22.0F, -3.0F, 12.0F, 3.0F, 11.0F, new Dilation(0.0F))
                .uv(54, 90).cuboid(-6.0F, -19.0F, 5.0F, 12.0F, 13.0F, 3.0F, new Dilation(0.0F))
                .uv(27, 88).cuboid(6.0F, -22.0F, -3.0F, 2.0F, 14.0F, 11.0F, new Dilation(0.0F))
                .uv(0, 49).cuboid(-8.0F, -29.0F, -8.0F, 16.0F, 7.0F, 16.0F, new Dilation(0.0F))
                .uv(65, 17).cuboid(-6.0F, -6.0F, -5.0F, 12.0F, 3.0F, 13.0F, new Dilation(0.0F))
                .uv(85, 90).cuboid(4.0F, -3.0F, -5.0F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F))
                .uv(27, 88).mirrored().cuboid(-8.0F, -22.0F, -3.0F, 2.0F, 14.0F, 11.0F, new Dilation(0.0F)).mirrored(false)
                .uv(65, 34).mirrored().cuboid(-8.0F, -8.0F, -8.0F, 2.0F, 11.0F, 16.0F, new Dilation(0.0F)).mirrored(false)
                .uv(149, -1).cuboid(-6.0F, -23.4645F, -4.0503F, 12.0F, 5.0F, 8.0F, new Dilation(0.0F))
                .uv(149, -1).cuboid(-6.0F, -11.5355F, -3.0503F, 12.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 21.0F, 0.0F));

        ModelPartData cube_r1 = main.addChild("cube_r1", ModelPartBuilder.create().uv(140, 37).cuboid(3.0F, -6.0F, -6.5F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(140, 32).cuboid(6.0F, -6.0F, -6.5F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(111, 107).mirrored().cuboid(-3.99F, -8.0F, -6.5F, 2.0F, 7.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(111, 107).cuboid(9.99F, -8.0F, -6.5F, 2.0F, 7.0F, 7.0F, new Dilation(0.0F))
                .uv(102, 51).cuboid(-2.0F, -7.0F, -5.5F, 12.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -2.6967F, -4.1109F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r2 = main.addChild("cube_r2", ModelPartBuilder.create().uv(140, 32).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, -10.1213F, -5.1716F, -2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r3 = main.addChild("cube_r3", ModelPartBuilder.create().uv(92, 107).mirrored().cuboid(6.02F, -8.0F, -6.5F, 2.0F, 7.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(92, 107).cuboid(20.0F, -8.0F, -6.5F, 2.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-14.01F, -16.6967F, -4.1109F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r4 = main.addChild("cube_r4", ModelPartBuilder.create().uv(102, 62).cuboid(-6.0F, -7.0F, -5.5F, 12.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -16.6967F, -5.1109F, -0.7854F, 0.0F, 0.0F));

        ModelPartData lever = modelPartData.addChild("lever", ModelPartBuilder.create(), ModelTransform.pivot(10.0F, 10.0F, 2.0F));

        ModelPartData cube_r5 = lever.addChild("cube_r5", ModelPartBuilder.create().uv(38, 114).cuboid(-1.0F, -13.0F, -2.0F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-2.25F, 0.75F, 0.25F, -0.1309F, 0.0F, 0.1309F));

        ModelPartData cube_r6 = lever.addChild("cube_r6", ModelPartBuilder.create().uv(47, 73).cuboid(-1.0F, -10.0F, -1.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.75F, 0.75F, -0.25F, -0.1309F, 0.0F, 0.1309F));

        ModelPartData wheel1 = modelPartData.addChild("wheel1", ModelPartBuilder.create().uv(0, 114).cuboid(-2.0F, -2.5F, -2.5F, 4.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 6.0F, -1.0503F, -2.3562F, 0.0F, 0.0F));

        ModelPartData wheel2 = modelPartData.addChild("wheel2", ModelPartBuilder.create().uv(19, 114).cuboid(-2.0F, -2.5F, -2.5F, 4.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 6.0F, -1.0503F, -2.3562F, 0.0F, 0.0F));

        ModelPartData wheel3 = modelPartData.addChild("wheel3", ModelPartBuilder.create().uv(0, 114).cuboid(-2.0F, -2.5F, -2.5F, 4.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 6.0F, -1.0503F, -2.3562F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 256, 256);
    }

    public void setAngles(SlotMachineBlockEntity entity, float tickDelta) {
        this.lever.pitch = entity.getLeverPitch(tickDelta);

        this.wheel1.pitch = -1.789F + entity.getWheelRotation(0, tickDelta);
        this.wheel2.pitch = -1.789F + entity.getWheelRotation(1, tickDelta);
        this.wheel3.pitch = -1.789F + entity.getWheelRotation(2, tickDelta);
    }
    

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        lever.render(matrices, vertexConsumer, light, overlay, color);
        main.render(matrices, vertexConsumer, light, overlay, color);
        wheel1.render(matrices, vertexConsumer, light, overlay, color);
        wheel2.render(matrices, vertexConsumer, light, overlay, color);
        wheel3.render(matrices, vertexConsumer, light, overlay, color);
        
    }
}