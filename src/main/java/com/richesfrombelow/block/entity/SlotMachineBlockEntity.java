package com.richesfrombelow.block.entity;

import com.richesfrombelow.block.SlotMachineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlotMachineBlockEntity extends BlockEntity {

    private enum State {
        IDLE,
        LEVER_PULLING,
        SPINNING;

        private static final State[] VALUES = values();
    }

    private State currentState = State.IDLE;

    private int animationTicks = 0;
    private float leverPitch = 0.0f;
    private float prevLeverPitch = 0.0f;

    private final float[] wheelRotations = new float[3];
    private final float[] prevWheelRotations = new float[3];
    private final float[] wheelSpeeds = new float[3];
    private final float[] lastHatSoundRotations = new float[3];
    private final float[] finalWheelRotations = new float[3];
    private final float[] decelerationStartRotations = new float[3];
    private final boolean[] isDecelerating = {false, false, false};

    public SlotMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLOT_MACHINE, pos, state);
    }

    public boolean isIdle() {
        return this.currentState == State.IDLE;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SlotMachineBlockEntity be) {
        be.prevLeverPitch = be.leverPitch;
        be.prevWheelRotations[0] = be.wheelRotations[0];
        be.prevWheelRotations[1] = be.wheelRotations[1];
        be.prevWheelRotations[2] = be.wheelRotations[2];

        be.animationTicks++;

        switch (be.currentState) {
            case LEVER_PULLING -> {
                float pullProgress = MathHelper.clamp((float) be.animationTicks / 10.0f, 0.0f, 1.0f);
                be.leverPitch = (MathHelper.PI / 3.0f) * pullProgress * pullProgress;

                if (be.animationTicks >= 10) {
                    be.currentState = State.SPINNING;
                    be.animationTicks = 0;
                }
            }
            case SPINNING -> {
                if (be.animationTicks == 1) {
                    for (int i = 0; i < 3; i++) {
                        float soundInterval = MathHelper.PI / 3.0f;
                        be.lastHatSoundRotations[i] = be.wheelRotations[i] - (i * soundInterval / 3.0f);
                    }
                }

                if (be.animationTicks < 50) {
                    float t = be.animationTicks;
                    float decay = (float) Math.exp(-0.2f * t);
                    float oscillation = (float) Math.cos(0.8f * t);
                    be.leverPitch = (MathHelper.PI / 3.0f) * decay * oscillation;
                } else {
                    be.leverPitch = 0;
                }

                int[] wheelStopTicks = {40, 60, 100};
                for (int i = 0; i < 3; i++) {
                    long stopTick = wheelStopTicks[i];
                    long decelerationStartTick = stopTick - 30;

                    if (be.animationTicks >= stopTick) {
                        if (be.isDecelerating[i]) {
                            be.isDecelerating[i] = false;
                            be.wheelRotations[i] = be.finalWheelRotations[i];
                        }
                        be.wheelSpeeds[i] = 0.0f;
                    } else if (be.animationTicks >= decelerationStartTick) {
                        if (!be.isDecelerating[i]) {
                            be.isDecelerating[i] = true;
                            be.decelerationStartRotations[i] = be.wheelRotations[i];
                            float estimatedTravelDistance = (0.8f / 2.0f) * 30;
                            float projectedFinalRotation = be.wheelRotations[i] + estimatedTravelDistance;
                            float targetAngle = be.finalWheelRotations[i] % (2 * MathHelper.PI);
                            float currentRevolutions = (float) Math.floor(projectedFinalRotation / (2 * MathHelper.PI));
                            float snappedRotation = currentRevolutions * (2 * MathHelper.PI) + targetAngle;
                            float nextSnappedRotation = (currentRevolutions + 1) * (2 * MathHelper.PI) + targetAngle;
                            if (Math.abs(projectedFinalRotation - nextSnappedRotation) < Math.abs(projectedFinalRotation - snappedRotation)) {
                                snappedRotation = nextSnappedRotation;
                            }
                            be.finalWheelRotations[i] = snappedRotation;
                        }
                        float progress = (float) (be.animationTicks - decelerationStartTick) / 30.0f;
                        be.wheelSpeeds[i] = 0.8f * (1.0f - progress);
                        float easeOutProgress = 1.0f - (1.0f - progress) * (1.0f - progress);
                        float totalRotationDistance = be.finalWheelRotations[i] - be.decelerationStartRotations[i];
                        be.wheelRotations[i] = be.decelerationStartRotations[i] + totalRotationDistance * easeOutProgress;
                    } else {
                        be.wheelRotations[i] += 0.8f;
                        be.wheelSpeeds[i] = 0.8f;
                    }
                }

                if (world.isClient) {
                    for (int i = 0; i < 3; i++) {
                        if (be.wheelSpeeds[i] > 0.01f) {
                            while (be.wheelRotations[i] - be.lastHatSoundRotations[i] >= (MathHelper.PI / 3.0f)) {
                                float speedRatio = be.wheelSpeeds[i] / 0.8f;
                                float pitch = 0.7f + speedRatio * 0.8f;
                                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                        SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.BLOCKS, 0.6f, pitch, false);
                                be.lastHatSoundRotations[i] += (MathHelper.PI / 3.0f);
                            }
                        } else {
                            be.lastHatSoundRotations[i] = be.wheelRotations[i];
                        }
                    }
                }

                if (be.animationTicks >= wheelStopTicks[2]) {
                    be.currentState = State.IDLE;
                }
            }
            case IDLE -> {
                be.leverPitch = 0;
                be.animationTicks = 0;
            }
        }
    }

    public void startSpin(SlotMachineBlock.SlotResult[] results, PlayerEntity player) {
        if (this.currentState == State.IDLE) {
            this.currentState = State.LEVER_PULLING;
            this.animationTicks = 0;
            for (int i = 0; i < 3; i++) {
                this.finalWheelRotations[i] = getRotationFor(results[i]) + ((15 + i * 2) * 2 * MathHelper.PI);
            }
            markDirty();
        }
    }

    private float getRotationFor(SlotMachineBlock.SlotResult result) {
        return switch (result) {
            case YELLOW -> 0;
            case PURPLE -> MathHelper.PI / 2;
            case GREEN -> MathHelper.PI;
            case RED -> 3 * MathHelper.PI / 2;
        };
    }

    public float getLeverPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevLeverPitch, this.leverPitch);
    }

    public float getWheelRotation(int wheel, float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevWheelRotations[wheel], this.wheelRotations[wheel]);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("currentState", this.currentState.ordinal());
        nbt.putFloat("finalWheel1", this.finalWheelRotations[0]);
        nbt.putFloat("finalWheel2", this.finalWheelRotations[1]);
        nbt.putFloat("finalWheel3", this.finalWheelRotations[2]);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.finalWheelRotations[0] = nbt.getFloat("finalWheel1");
        this.finalWheelRotations[1] = nbt.getFloat("finalWheel2");
        this.finalWheelRotations[2] = nbt.getFloat("finalWheel3");

        State oldState = this.currentState;
        this.currentState = State.VALUES[nbt.getInt("currentState")];

        if (this.currentState == State.IDLE) {
            System.arraycopy(this.finalWheelRotations, 0, this.wheelRotations, 0, 3);
            System.arraycopy(this.finalWheelRotations, 0, this.prevWheelRotations, 0, 3);
        } else if (this.currentState != oldState) {
            this.animationTicks = 0;
        }
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}