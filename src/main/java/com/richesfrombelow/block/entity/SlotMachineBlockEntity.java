package com.richesfrombelow.block.entity;

import com.richesfrombelow.block.SlotMachineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
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

    private static final int LEVER_PULL_DURATION = 8;
    private static final int LEVER_RETURN_DURATION = 50;
    private static final float MAX_LEVER_PITCH = MathHelper.PI / 3.0f;
    private static final float LEVER_BOUNCE_DECAY = 0.15f;
    private static final float LEVER_BOUNCE_FREQUENCY = 0.8f;
    private static final int[] WHEEL_STOP_TICKS = {60, 90, 140};
    private static final int DECELERATION_TICKS = 40;
    private static final float INITIAL_WHEEL_SPIN_SPEED = 0.8f;

    private int animationTicks = 0;
    private float leverPitch = 0.0f;
    private float prevLeverPitch = 0.0f;
    private final float[] wheelRotations = new float[3];
    private final float[] prevWheelRotations = new float[3];
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
            case LEVER_PULLING:
                float pullProgress = MathHelper.clamp((float) be.animationTicks / LEVER_PULL_DURATION, 0.0f, 1.0f);
                be.leverPitch = MAX_LEVER_PITCH * pullProgress * pullProgress;

                if (be.animationTicks >= LEVER_PULL_DURATION) {
                    be.currentState = State.SPINNING;
                    be.animationTicks = 0;
                }
                break;

            case SPINNING:
                if (be.animationTicks < LEVER_RETURN_DURATION) {
                    float t = be.animationTicks;
                    float decay = (float) Math.exp(-LEVER_BOUNCE_DECAY * t);
                    float oscillation = (float) Math.cos(LEVER_BOUNCE_FREQUENCY * t);
                    be.leverPitch = MAX_LEVER_PITCH * decay * oscillation;
                } else {
                    be.leverPitch = 0;
                }


                for (int i = 0; i < 3; i++) {
                    long stopTick = WHEEL_STOP_TICKS[i];
                    long decelerationStartTick = stopTick - DECELERATION_TICKS;

                    if (be.animationTicks >= stopTick) {
                        if (be.isDecelerating[i]) {
                            be.isDecelerating[i] = false;
                            be.wheelRotations[i] = be.finalWheelRotations[i];
                        }
                    } else if (be.animationTicks >= decelerationStartTick) {
                        if (!be.isDecelerating[i]) {
                            be.isDecelerating[i] = true;
                            be.decelerationStartRotations[i] = be.wheelRotations[i];
                            float estimatedTravelDistance = (INITIAL_WHEEL_SPIN_SPEED / 2.0f) * DECELERATION_TICKS;
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
                        float progress = (float) (be.animationTicks - decelerationStartTick) / DECELERATION_TICKS;
                        float easeOutProgress = 1.0f - (1.0f - progress) * (1.0f - progress);
                        float totalRotationDistance = be.finalWheelRotations[i] - be.decelerationStartRotations[i];
                        be.wheelRotations[i] = be.decelerationStartRotations[i] + totalRotationDistance * easeOutProgress;
                    } else {
                        be.wheelRotations[i] += INITIAL_WHEEL_SPIN_SPEED;
                    }
                }

                if (be.animationTicks >= WHEEL_STOP_TICKS[2] + 40) {
                    be.currentState = State.IDLE;
                }
                break;

            case IDLE:
                be.leverPitch = 0;
                be.animationTicks = 0;
                break;
        }
    }

    public void startSpin(SlotMachineBlock.SlotResult[] results) {
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
            case BLACK -> MathHelper.PI / 2;
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