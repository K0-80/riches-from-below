package com.richesfrombelow.entities.client;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.entities.custom.GatchaBallEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import com.richesfrombelow.util.GatchaBallLootTableUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class GatchaBallRenderer extends LivingEntityRenderer<GatchaBallEntity, GatchaBallModel<GatchaBallEntity>> {

    private static final Identifier COMMON_TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/entity/gatcha_ball/gatcha_ball_common.png");
    private static final Identifier UNCOMMON_TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/entity/gatcha_ball/gatcha_ball_uncommon.png");
    private static final Identifier RARE_TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/entity/gatcha_ball/gatcha_ball_rare.png");
    private static final Identifier EPIC_TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/entity/gatcha_ball/gatcha_ball_epic.png");
    private static final Identifier LEGENDARY_TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/entity/gatcha_ball/gatcha_ball_legendary.png");
    private static final Identifier EXOTIC_TEXTURE = Identifier.of(RichesfromBelow.MOD_ID, "textures/entity/gatcha_ball/gatcha_ball_exotic.png");

    public GatchaBallRenderer(EntityRendererFactory.Context context) {
        super(context, new GatchaBallModel<>(context.getPart(ModModelLayers.GATCHA_BALL)), 0.2f);
    }

    @Override
    protected void setupTransforms(GatchaBallEntity entity, MatrixStack matrices, float ageInTicks, float bodyYaw, float partialTick, float scale) {
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        float yOffset = 2.5f / 16.0f;

        matrices.translate(0.0D, yOffset, 0.0D);

        Quaternionf interpolatedRotation = new Quaternionf(entity.prevClientRotation).slerp(entity.clientRotation, partialTick);
        matrices.multiply(interpolatedRotation);

        matrices.translate(0.0D, -yOffset, 0.0D);  //both translate are needed forwhatever reason???
    }

    @Override
    protected boolean hasLabel(GatchaBallEntity livingEntity) {return false;}

    @Override
    public Identifier getTexture(GatchaBallEntity entity) {
        return switch (entity.getGatchaTier()) {
            case COMMON -> COMMON_TEXTURE;
            case UNCOMMON -> UNCOMMON_TEXTURE;
            case RARE -> RARE_TEXTURE;
            case EPIC -> EPIC_TEXTURE;
            case LEGENDARY -> LEGENDARY_TEXTURE;
            case EXOTIC -> EXOTIC_TEXTURE;
        };
    }
}
