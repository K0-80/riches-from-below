package com.richesfrombelow.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class DeckOfFatesAnimationRenderer {

    private enum AnimationState { INACTIVE, FAN_OUT, RISE_AND_ASCEND }
    private static AnimationState currentState = AnimationState.INACTIVE;
    private static int animationTicks = 0;
    private static final int CARD_COUNT = 7;
    private static final Random random = new Random();

    private static final List<CardRenderData> cards = new ArrayList<>();
    private static int selectedCardIndex;
    private static ItemStack revealedCardFront;
    private static final ItemStack cardBack;

    //  config
    private static final int FAN_OUT_DURATION = 20;
    private static final int RISE_AND_ASCEND_DURATION = 50;

    static {
        cardBack = new ItemStack(ModItems.DECK_OF_FATES_ANIMATION);
        cardBack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(100));
    }

    public static void showAnimation(ItemStack drawnCard) {
        if (currentState != AnimationState.INACTIVE) return;

        revealedCardFront = drawnCard;
        currentState = AnimationState.FAN_OUT;
        animationTicks = 0;
        selectedCardIndex = 2 + random.nextInt(3);

        cards.clear();
        for (int i = 0; i < CARD_COUNT; i++) {
            cards.add(new CardRenderData());
        }
    }

    public static void tick() {
        if (currentState == AnimationState.INACTIVE) return;

        animationTicks++;

        if (currentState == AnimationState.FAN_OUT && animationTicks > FAN_OUT_DURATION) {
            animationTicks = 0;
            currentState = AnimationState.RISE_AND_ASCEND;
        } else if (currentState == AnimationState.RISE_AND_ASCEND && animationTicks > RISE_AND_ASCEND_DURATION) {
            currentState = AnimationState.INACTIVE;
        }
    }

    public static void render(DrawContext context, float tickDelta) {
        if (currentState == AnimationState.INACTIVE) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        updateCardTransforms(screenWidth, screenHeight, tickDelta);

        for (int i = 0; i < CARD_COUNT; i++) {
            if (i == selectedCardIndex && currentState == AnimationState.RISE_AND_ASCEND) continue;
            renderCard(context, cards.get(i), cardBack);
        }

        if (currentState == AnimationState.RISE_AND_ASCEND) {
            CardRenderData selectedData = cards.get(selectedCardIndex);
            float totalFlipRotation = selectedData.flipRotation;
            boolean showFront = (Math.floor((totalFlipRotation + 90) / 180) % 2) != 0;
            renderCard(context, selectedData, showFront ? revealedCardFront : cardBack);
        }
    }

    private static void updateCardTransforms(float screenWidth, float screenHeight, float tickDelta) {
        float fanCenterX = screenWidth / 2.0f;
        float fanBottomY = screenHeight - 40.0f;
        float fanWidth = screenWidth * 0.3f;
        float fanAngleSpread = 90.0f;
        float fanHeightArc = 30.0f;

        if (currentState == AnimationState.FAN_OUT) {
            float time = animationTicks + tickDelta;
            float fanOutProgress = easeOutCubic(Math.min(time / FAN_OUT_DURATION, 1.0f));
            Vector2f startPos = new Vector2f(fanCenterX - fanWidth / 2.0f - 50, fanBottomY);
            float startRotation = -90.0f;

            for (int i = 0; i < CARD_COUNT; i++) {
                CardRenderData card = cards.get(i);
                float cardFanProgress = (float) i / (CARD_COUNT - 1);

                float targetAngle = -fanAngleSpread / 2.0f + cardFanProgress * fanAngleSpread;
                float targetX = fanCenterX + (cardFanProgress - 0.5f) * fanWidth;
                float targetY = fanBottomY - (float) Math.sin(cardFanProgress * Math.PI) * fanHeightArc;

                if (i == 0 || i == CARD_COUNT - 1) {
                    targetY += 6f;
                    if (i == 0) {
                        targetX += 6f;
                    } else {
                        targetX -= 6f;
                    }
                }

                card.position.x = MathHelper.lerp(fanOutProgress, startPos.x, targetX);
                card.position.y = MathHelper.lerp(fanOutProgress, startPos.y, targetY);
                card.rotation = MathHelper.lerp(fanOutProgress, startRotation, targetAngle);
                card.scale = 64.0f;
            }
        } else if (currentState == AnimationState.RISE_AND_ASCEND) {
            float time = animationTicks + tickDelta;
            final float RISE_DURATION = RISE_AND_ASCEND_DURATION / 2f;
            final float ASCEND_DURATION = RISE_AND_ASCEND_DURATION / 2f;

            for (int i = 0; i < CARD_COUNT; i++) {
                if (i == selectedCardIndex) continue;
                CardRenderData card = cards.get(i);
                float cardFanProgress = (float) i / (CARD_COUNT - 1);

                float restX = fanCenterX + (cardFanProgress - 0.5f) * fanWidth;
                float restY = fanBottomY - (float) Math.sin(cardFanProgress * Math.PI) * fanHeightArc;
                float restRot = -fanAngleSpread / 2.0f + cardFanProgress * fanAngleSpread;

                if (i == 0 || i == CARD_COUNT - 1) {
                    restY += 6f;
                    if (i == 0) restX += 6f;
                    else restX -= 6f;
                }

                if (time >= RISE_DURATION) {
                    // Animate the retreat
                    float fanRetractProgress = (time - RISE_DURATION) / ASCEND_DURATION;
                    float easedFanRetractProgress = easeInCubic(fanRetractProgress);
                    Vector2f endPos = new Vector2f(fanCenterX + fanWidth / 2.0f + 50, fanBottomY);
                    float endRotation = 90.0f;

                    card.position.x = MathHelper.lerp(easedFanRetractProgress, restX, endPos.x);
                    card.position.y = MathHelper.lerp(easedFanRetractProgress, restY, endPos.y);
                    card.rotation = MathHelper.lerp(easedFanRetractProgress, restRot, endRotation);
                    card.alpha = 1.0f - easedFanRetractProgress;
                } else {
                    card.position.x = restX;
                    card.position.y = restY;
                    card.rotation = restRot;
                    card.alpha = 1.0f;
                }
            }

            float p = (float) selectedCardIndex / (CARD_COUNT - 1);
            float startX = fanCenterX + (p - 0.5f) * fanWidth;
            float startY = fanBottomY - (float) Math.sin(p * Math.PI) * fanHeightArc;
            float startFanRot = -fanAngleSpread / 2.0f + p * fanAngleSpread;
            float midX = screenWidth / 2.0f, midY = screenHeight / 2.0f;
            float endY = -100f;

            CardRenderData selected = cards.get(selectedCardIndex);

            if (time < RISE_DURATION) {
                float localProgress = time / RISE_DURATION;
                float easedProgress = easeOutQuartic(localProgress);

                selected.position.x = MathHelper.lerp(easedProgress, startX, midX);
                selected.position.y = MathHelper.lerp(easedProgress, startY, midY);
                selected.rotation = MathHelper.lerp(easedProgress, startFanRot, 0);
                selected.flipRotation = MathHelper.lerp(easedProgress, 0, 540f);
                selected.scale = MathHelper.lerp(easedProgress, 64f, 96f);
                selected.alpha = 1.0f;
            } else {
                float localProgress = (time - RISE_DURATION) / ASCEND_DURATION;
                float easedProgress = easeInQuartic(localProgress);

                selected.position.x = midX;
                selected.position.y = MathHelper.lerp(easedProgress, midY, endY);
                selected.rotation = 0;
                selected.flipRotation = MathHelper.lerp(easedProgress, 540f, 1080f);
                selected.scale = 96f;
                selected.alpha = 1.0f - easedProgress;
            }
        }
    }


    private static void renderCard(DrawContext context, CardRenderData card, ItemStack stack) {
        if (card.alpha <= 0.01f) return;

        context.getMatrices().push();
        context.getMatrices().translate(card.position.x, card.position.y, 100.0f);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(card.rotation));
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(card.flipRotation));
        context.getMatrices().scale(card.scale, -card.scale, card.scale);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.setShaderColor(1.0f, 1.0f, 1.0f, card.alpha);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GUI,
                15728880,
                OverlayTexture.DEFAULT_UV,
                context.getMatrices(),
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(),
                MinecraftClient.getInstance().world,
                0
        );
        MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().draw();

        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        context.getMatrices().pop();
    }

    private static float easeOutCubic(float x) {
        return 1 - (float) Math.pow(1 - x, 3);
    }

    private static float easeInCubic(float x) {
        return x * x * x;
    }

    private static float easeInQuartic(float x) {
        return x * x * x;
    }

    private static float easeOutQuartic(float x) {
        return 1 - (float) Math.pow(1 - x, 3);
    }

    private static class CardRenderData {
        Vector2f position = new Vector2f();
        float rotation = 0.0f;
        float flipRotation = 0.0f;
        float scale = 1.0f;
        float alpha = 1.0f;
    }
}