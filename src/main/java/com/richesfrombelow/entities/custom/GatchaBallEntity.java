package com.richesfrombelow.entities.custom;

import com.richesfrombelow.util.GatchaBallLootTableUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.BlockStateParticleEffect;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GatchaBallEntity extends LivingEntity {

    private static final TrackedData<Integer> GATCHA_TIER = DataTracker.registerData(GatchaBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private List<ItemStack> lootToDrop = new ArrayList<>();

    public GatchaBallEntity(EntityType<? extends GatchaBallEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false);
    }

    public void initialize(GatchaBallLootTableUtil.GatchaResult result) {
        this.dataTracker.set(GATCHA_TIER, result.tier().ordinal());
        this.lootToDrop.clear();
        this.lootToDrop.addAll(result.items());
    }

    public GatchaBallLootTableUtil.GatchaTier getGatchaTier() {
        return GatchaBallLootTableUtil.GatchaTier.values()[this.dataTracker.get(GATCHA_TIER)];
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GATCHA_TIER, GatchaBallLootTableUtil.GatchaTier.COMMON.ordinal());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("GatchaTier", getGatchaTier().ordinal());

        NbtList nbtList = new NbtList();
        for (ItemStack itemStack : this.lootToDrop) {
            if (!itemStack.isEmpty()) {
                nbtList.add(itemStack.encode(this.getRegistryManager()));
            }
        }
        nbt.put("LootToDrop", nbtList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("GatchaTier")) {
            this.dataTracker.set(GATCHA_TIER, nbt.getInt("GatchaTier"));
        }

        this.lootToDrop.clear();
        NbtList nbtList = nbt.getList("LootToDrop", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); ++i) {
            ItemStack.fromNbt(getRegistryManager(), nbtList.getCompound(i)).ifPresent(this.lootToDrop::add);
        }
    }




    public static DefaultAttributeContainer.Builder createGatchaBallAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

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

                    this.clientRotation.rotateLocalX((float) Math.toRadians(this.pitchSpeed));
                    this.clientRotation.rotateLocalY((float) Math.toRadians(this.yawSpeed));
                    this.clientRotation.rotateLocalZ((float) Math.toRadians(this.rollSpeed));
                }
            } else {

                Vec3d velocity = this.getVelocity();
                double horizontalSpeed = velocity.horizontalLength();

                if (horizontalSpeed > 0.01) {

                    Vec3d rollAxis = new Vec3d(-velocity.z, 0.0, velocity.x).normalize();

                    float rollAmount = (float)horizontalSpeed * 65.0F;

                    Quaternionf deltaRotation = new Quaternionf().fromAxisAngleDeg((float)rollAxis.x, (float)rollAxis.y, (float)rollAxis.z, rollAmount);
                    this.clientRotation.mul(deltaRotation);
                }
            }
            this.clientRotation.normalize(); // Prevent floating-point drift over time
        }
    }



    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient || this.isRemoved()) {
            return false;
        }

        if (source.getAttacker() instanceof PlayerEntity) {
            this.playBreakSound();
            this.spawnTieredParticles();
            if (!this.lootToDrop.isEmpty()) {
                for (ItemStack stack : this.lootToDrop) {
                    this.dropStack(stack);
                }
            }
            this.discard();
            return true;
        }
        return false;
    }

    private void spawnTieredParticles() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        GatchaBallLootTableUtil.GatchaTier tier = this.getGatchaTier();
        Vec3d center = this.getPos().add(0, this.getHeight() / 2.0, 0);
        double spread = this.getWidth() / 2.5;

        switch (tier) {
            case COMMON -> serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GRAY_CONCRETE.getDefaultState()),
                    center.x, center.y, center.z, 10, spread, spread, spread, 0.1);
            case UNCOMMON -> {
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LIME_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 10, spread, spread, spread, 0.1);
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GREEN_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 5, spread, spread, spread, 0.1);
            }
            case RARE -> {
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LIGHT_BLUE_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 10, spread, spread, spread, 0.1);
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.BLUE_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 5, spread, spread, spread, 0.1);
            }
            case EPIC -> {
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.MAGENTA_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 10, spread, spread, spread, 0.1);
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.PURPLE_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 5, spread, spread, spread, 0.1);
            }
            case LEGENDARY -> {
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.YELLOW_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 10, spread, spread, spread, 0.15);
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ORANGE_CONCRETE.getDefaultState()),
                        center.x, center.y, center.z, 5, spread, spread, spread, 0.15);
                serverWorld.spawnParticles(ParticleTypes.END_ROD,
                        center.x, center.y, center.z, 10, spread, spread, spread, 0.05);
            }
            case EXOTIC -> {
                List<Block> glasses = List.of(
                        Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS,
                        Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS,
                        Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS,
                        Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS,
                        Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS,
                        Blocks.BLACK_STAINED_GLASS
                );
                for (Block glass : glasses) {
                    serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, glass.getDefaultState()),
                            center.x, center.y, center.z, 2, spread, spread, spread, 0.1);
                }
            }
        }
    }

    private void playBreakSound() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_VAULT_REJECT_REWARDED_PLAYER, this.getSoundCategory(), 1.0F, 1.8F);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_VAULT_EJECT_ITEM, this.getSoundCategory(), 1.0F, 1.8F);
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

        this.onLanding();
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {return false;}
}