package com.richesfrombelow.entities.custom;

import com.richesfrombelow.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Collections;

public class GatchaBallEntity extends LivingEntity {

    public GatchaBallEntity(EntityType<? extends GatchaBallEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false);
    }

    public static DefaultAttributeContainer.Builder createGatchaBallAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

    //amazing ai generated slop start
    public final Quaternionf clientRotation = new Quaternionf();
    public final Quaternionf prevClientRotation = new Quaternionf();

    private boolean hasLanded = false;
    private boolean rotationInitialized = false;
    private float pitchSpeed;
    private float yawSpeed;
    private float rollSpeed;

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.prevClientRotation.set(this.clientRotation);

            if (!this.rotationInitialized) {
                this.pitchSpeed = (this.random.nextFloat() - 0.5F) * 25.0F;
                this.yawSpeed = (this.random.nextFloat() - 0.5F) * 25.0F;
                this.rollSpeed = (this.random.nextFloat() - 0.5F) * 25.0F;
                this.rotationInitialized = true;
            }

            if (!this.hasLanded) {
                if (this.isOnGround()) {
                    this.hasLanded = true;
                } else {
                    // Apply random tumbling rotation in the air
                    this.clientRotation.rotateLocalX((float) Math.toRadians(this.pitchSpeed));
                    this.clientRotation.rotateLocalY((float) Math.toRadians(this.yawSpeed));
                    this.clientRotation.rotateLocalZ((float) Math.toRadians(this.rollSpeed));
                }
            } else {
                // Apply rolling rotation on the ground based on velocity
                Vec3d velocity = this.getVelocity();
                double horizontalSpeed = velocity.horizontalLength();

                if (horizontalSpeed > 0.01) {
                    // Axis of rotation is perpendicular to the velocity vector
                    Vec3d rollAxis = new Vec3d(-velocity.z, 0.0, velocity.x).normalize();
                    // Amount of rotation is proportional to the distance traveled
                    float rollAmount = (float)horizontalSpeed * 65.0F;

                    // Create an incremental rotation and multiply it with the current orientation
                    Quaternionf deltaRotation = new Quaternionf().fromAxisAngleDeg((float)rollAxis.x, (float)rollAxis.y, (float)rollAxis.z, rollAmount);
                    this.clientRotation.mul(deltaRotation);
                }
            }
            this.clientRotation.normalize(); // Prevent floating-point drift over time
        }
    }
    //amazing ai generated slop end


    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient || this.isRemoved()) {
            return false;
        }

        if (source.getAttacker() instanceof PlayerEntity player) {
            this.playBreakSound();
            this.dropItem(ModItems.GATCHA_BALL_ITEM);
            this.discard();
            return true;
        }

        return false;
    }

    private void playBreakSound() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_FRAME_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(ModItems.GATCHA_BALL_ITEM)),
                    this.getX(),
                    (this.getY() + this.getHeight() / 2.0) + 0.2,
                    this.getZ(),
                    10,
                    this.getWidth() / 4.0F,
                    this.getHeight() / 4.0F,
                    this.getWidth() / 4.0F,
                    0.05
            );
        }
    }

    @Override
    public boolean isPushable() {return true;}

    @Override public boolean isImmobile() {return false;}

    @Override
    public Iterable<ItemStack> getArmorItems() {return Collections.emptyList();}

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {return ItemStack.EMPTY;}

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public Arm getMainArm() {return Arm.RIGHT;}

    @Override
    protected @Nullable SoundEvent getDeathSound() {return null;}

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {return null;}

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos pos) {
        if (!onGround) {
            super.fall(heightDifference, false, state, pos);
            return;
        }
        if (this.fallDistance > 3.0F && !state.isAir()) {
            int particleCount = this.random.nextInt(3) + 2; // Spawns 2 to 4 particles.
            for (int i = 0; i < particleCount; ++i) {
                this.getWorld().addParticle(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                        this.getX(), this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.05,
                        0.2,
                        this.random.nextGaussian() * 0.05
                );
            }
        }

        // Reset fall distance. This prevents this code from running again until the next fall.
        this.onLanding();
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {return false;}
}