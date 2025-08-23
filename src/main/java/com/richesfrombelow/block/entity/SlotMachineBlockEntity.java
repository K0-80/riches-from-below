package com.richesfrombelow.block.entity;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.SlotMachineBlock;
import com.richesfrombelow.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

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

    private SlotMachineBlock.SlotResult[] lastResults = new SlotMachineBlock.SlotResult[3];
    private UUID lastPlayerUUID;
    private int anvilDropDelay = -1;
    private BlockPos anvilTargetPos = null;
    private int hazardPayDelay = -1;

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

        if (!world.isClient) {
            be.handleDelayedTasks((ServerWorld) world);
        }

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
                                        SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.BLOCKS, 0.2f, pitch, false);
                                be.lastHatSoundRotations[i] += (MathHelper.PI / 3.0f);
                            }
                        } else {
                            be.lastHatSoundRotations[i] = be.wheelRotations[i];
                        }
                    }
                }

                if (be.animationTicks == wheelStopTicks[2] && !world.isClient) {
                    be.dispenseRewards();
                }

                if (be.animationTicks >= wheelStopTicks[2]) {
                    be.currentState = State.IDLE;
                    if (!world.isClient) {
                        be.markDirty();
                        world.updateListeners(pos, state, state, 3);
                    }
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
            this.lastResults = results;
            if (player != null) {
                this.lastPlayerUUID = player.getUuid();
            }
            markDirty();
            if (world != null) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
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

        if (this.lastResults != null && this.lastResults[0] != null) {
            nbt.putString("result1", this.lastResults[0].name());
            nbt.putString("result2", this.lastResults[1].name());
            nbt.putString("result3", this.lastResults[2].name());
        }
        if (this.lastPlayerUUID != null) {
            nbt.putUuid("lastPlayer", this.lastPlayerUUID);
        }
        nbt.putInt("anvilDropDelay", this.anvilDropDelay);
        if (this.anvilTargetPos != null) {
            nbt.putLong("anvilTargetPos", this.anvilTargetPos.asLong());
        }
        nbt.putInt("hazardPayDelay", this.hazardPayDelay);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.finalWheelRotations[0] = nbt.getFloat("finalWheel1");
        this.finalWheelRotations[1] = nbt.getFloat("finalWheel2");
        this.finalWheelRotations[2] = nbt.getFloat("finalWheel3");

        if (nbt.contains("result1")) {
            try {
                this.lastResults[0] = SlotMachineBlock.SlotResult.valueOf(nbt.getString("result1"));
                this.lastResults[1] = SlotMachineBlock.SlotResult.valueOf(nbt.getString("result2"));
                this.lastResults[2] = SlotMachineBlock.SlotResult.valueOf(nbt.getString("result3"));
            } catch (IllegalArgumentException e) {
                Arrays.fill(this.lastResults, SlotMachineBlock.SlotResult.YELLOW);
            }
        }
        if (nbt.containsUuid("lastPlayer")) {
            this.lastPlayerUUID = nbt.getUuid("lastPlayer");
        }
        this.anvilDropDelay = nbt.getInt("anvilDropDelay");
        if (nbt.contains("anvilTargetPos")) {
            this.anvilTargetPos = BlockPos.fromLong(nbt.getLong("anvilTargetPos"));
        }
        this.hazardPayDelay = nbt.getInt("hazardPayDelay");


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

    private void handleDelayedTasks(ServerWorld world) {
        if (this.anvilDropDelay > 0) {
            this.anvilDropDelay--;
            if (this.anvilDropDelay == 0 && this.anvilTargetPos != null) {
                world.spawnParticles(ParticleTypes.EXPLOSION, this.anvilTargetPos.getX() + 0.5, this.anvilTargetPos.getY() + 1.0, this.anvilTargetPos.getZ() + 0.5, 5, 1.0, 1.0, 1.0, 0.0);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos dropPos = this.anvilTargetPos.add(dx, 30, dz);
                        FallingBlockEntity.spawnFromBlock(world, dropPos, Blocks.DAMAGED_ANVIL.getDefaultState());
                    }
                }
                world.playSound(null, this.anvilTargetPos, SoundEvents.BLOCK_ANVIL_BREAK, SoundCategory.BLOCKS, 1.0f, 0.5f);
                this.hazardPayDelay = 100;
                this.anvilTargetPos = null;
                markDirty();
            }
        }
    }

    private void dispenseRewards() {
        if (this.world == null || this.world.isClient || this.lastResults == null || this.lastResults[0] == null) return;

        PlayerEntity player = this.lastPlayerUUID != null ? world.getPlayerByUuid(this.lastPlayerUUID) : null;

        Map<SlotMachineBlock.SlotResult, Integer> counts = new HashMap<>();
        for (SlotMachineBlock.SlotResult result : this.lastResults) {
            counts.put(result, counts.getOrDefault(result, 0) + 1);
        }

        Optional<Map.Entry<SlotMachineBlock.SlotResult, Integer>> tripleEntry = counts.entrySet().stream()
                .filter(e -> e.getValue() == 3)
                .findFirst();

        if (tripleEntry.isPresent()) {
            handleTier3(tripleEntry.get().getKey(), player);
        } else {
            Optional<Map.Entry<SlotMachineBlock.SlotResult, Integer>> pairEntry = counts.entrySet().stream()
                    .filter(e -> e.getValue() == 2)
                    .findFirst();
            if (pairEntry.isPresent()) {
                handleTier2(pairEntry.get().getKey(), player);
            } else {
                handleTier1();
            }
        }
    }

    private void handleTier3(SlotMachineBlock.SlotResult result, @Nullable PlayerEntity player) {
        ServerWorld world = (ServerWorld) this.world;
        if (world == null) return;

        BlockState particleBlockState = switch (result) {
            case YELLOW -> Blocks.GOLD_BLOCK.getDefaultState();
            case GREEN -> Blocks.EMERALD_BLOCK.getDefaultState();
            case RED -> Blocks.REDSTONE_BLOCK.getDefaultState();
            case PURPLE -> Blocks.AMETHYST_BLOCK.getDefaultState();
        };
        spawnExplosionParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, particleBlockState), 200, 0.5);

        switch (result) {
            case YELLOW: // 3 x Gold
                world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 0.6f, 1.3f);
                spawnExplosionParticles(ParticleTypes.TOTEM_OF_UNDYING, 1, 0.0);
                generateLoot(world, RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(RichesfromBelow.MOD_ID, "chests/fortune_cookie_treasure")));
                break;
            case GREEN: // 3 x Emerald
                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS, 1.0f, 0.8f);
                for (int i = 0; i < 3; i++) {
                    CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                    launchEntity(creeper);
                    world.spawnParticles(ParticleTypes.LAVA, creeper.getX(), creeper.getY(), creeper.getZ(), 10, 0.2, 0.2, 0.2, 0);
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                    lightning.setPosition(creeper.getPos());
                    world.spawnEntity(lightning);
                }
                break;
            case RED: // 3 x Redstone
                if (player != null) {
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0f, 0.5f);
                    world.spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 50, 1, 1, 1, 0.05);
                    this.anvilDropDelay = 60; // 3 seconds
                    this.anvilTargetPos = player.getBlockPos();
                    markDirty();
                }
                break;
            case PURPLE: // 3 x Amethyst - Pick one of three random events
                int choice = world.random.nextInt(3);
                switch (choice) {
                    case 0: // XP
                        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 0.7f);
                        spawnExplosionParticles(ParticleTypes.ENCHANT, 100, 0.3);
                        int totalXp = 350 + world.random.nextInt(200);
                        ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), totalXp);
                        break;
                    case 1: // Allay with crown
                        if (player != null) {
                            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                            AllayEntity allay = new AllayEntity(EntityType.ALLAY, world);
                            allay.setPosition(player.getPos());
                            allay.setStackInHand(Hand.MAIN_HAND, new ItemStack(ModItems.PACIFIST_CROWN));
                            world.spawnEntity(allay);
                            world.spawnParticles(ParticleTypes.HEART, allay.getX(), allay.getY() + 1, allay.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                        }
                        break;
                    case 2: // Buried treasure loot
                        world.playSound(null, pos, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 1.0f, 1.2f);
                        spawnExplosionParticles(ParticleTypes.NAUTILUS, 100, 0.2);
                        generateLoot(world, RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("minecraft", "chests/buried_treasure")));
                        break;
                }
                break;
        }
    }

    private void handleTier2(SlotMachineBlock.SlotResult result, @Nullable PlayerEntity player) {
        ServerWorld world = (ServerWorld) this.world;
        if (world == null) return;
        Random random = world.getRandom();

        BlockState particleBlockState = switch (result) {
            case YELLOW -> Blocks.GOLD_BLOCK.getDefaultState();
            case GREEN -> Blocks.EMERALD_BLOCK.getDefaultState();
            case RED -> Blocks.REDSTONE_BLOCK.getDefaultState();
            case PURPLE -> Blocks.AMETHYST_BLOCK.getDefaultState();
        };
        spawnLaunchParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, particleBlockState), 30);

        switch (result) {
            case YELLOW: // 2 x Gold
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 1.0f, 1.0f);
                spawnLaunchParticles(ParticleTypes.END_ROD, 20);
                if (random.nextFloat() < 0.25f) {
                    launchItemStack(new ItemStack(ModItems.KOBO_COIN));
                } else {
                    launchItemStack(new ItemStack(ModItems.COIN_FRAGMENT, 2 + random.nextInt(3)));
                }
                break;
            case GREEN: // 2 x Emerald
                world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_CELEBRATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (random.nextBoolean()) {
                    if (player != null) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 5 * 60 * 20, 0));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 10 * 60 * 20, 0));
                        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 50, 0.5, 0.5, 0.5, 0.1);
                    }
                } else {
                    spawnLaunchParticles(ParticleTypes.HAPPY_VILLAGER, 20);
                    launchItemStack(new ItemStack(Items.EMERALD_BLOCK, 5 + random.nextInt(3)));
                }
                break;
            case RED: // 2 x Redstone
                if (random.nextBoolean()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_SILVERFISH_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    spawnLaunchParticles(ParticleTypes.CRIT, 40);
                    int count = 2 + random.nextInt(4);
                    for (int i = 0; i < count; i++) {
                        launchEntity(new SilverfishEntity(EntityType.SILVERFISH, world));
                    }
                } else {
                    if (player != null) {
                        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10* 60 * 20, 10));
                        world.spawnParticles(ParticleTypes.SQUID_INK, player.getX(), player.getY() + 1, player.getZ(), 50, 0.5, 0.5, 0.5, 0.1);
                    }
                }
                break;
            case PURPLE: // 2 x Amethyst
                world.playSound(null, pos, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.BLOCKS, 1.0f, 1.2f);
                if (random.nextBoolean()) {
                    spawnLaunchParticles(ParticleTypes.ENCHANT, 30);
                    ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), 30 + random.nextInt(25)); // ~3-5 levels
                } else {
                    if (player != null) {
                        @SuppressWarnings("unchecked")
                        RegistryEntry<StatusEffect>[] effects = new RegistryEntry[]{StatusEffects.STRENGTH, StatusEffects.REGENERATION, StatusEffects.SPEED, StatusEffects.RESISTANCE, StatusEffects.HASTE, StatusEffects.JUMP_BOOST};
                        RegistryEntry<StatusEffect> chosenEffect = effects[random.nextInt(effects.length)];
                        player.addStatusEffect(new StatusEffectInstance(chosenEffect, StatusEffectInstance.INFINITE, 1));
                        int color = chosenEffect.value().getColor();
                        world.spawnParticles(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, color), player.getX(), player.getY() + 1, player.getZ(), 50, 0.5, 0.5, 0.5, 0.1);
                    }
                }
                break;
        }
    }

    private void handleTier1() {
        ServerWorld world = (ServerWorld) this.world;
        if (world == null) return;
        Random random = world.getRandom();
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0f, 0.8f);
        spawnLaunchParticles(ParticleTypes.SMOKE, 15);

        List<Supplier<ItemStack>> commonPool = List.of(
                () -> new ItemStack(Items.DIRT, 16 + random.nextInt(17)),
                () -> new ItemStack(Items.ROTTEN_FLESH, 8 + random.nextInt(9)),
                () -> new ItemStack(Items.POPPY, 4 + random.nextInt(5)),
                () -> new ItemStack(Items.COBBLESTONE, 8 + random.nextInt(9)),
                () -> new ItemStack(Items.LEATHER, 1 + random.nextInt(3)),
                () -> new ItemStack(Items.STRING, 1 + random.nextInt(2)),
                () -> new ItemStack(Items.STICK, 1 + random.nextInt(4))
        );
        List<Supplier<ItemStack>> uncommonPool = List.of(
                () -> new ItemStack(Items.IRON_NUGGET, 3 + random.nextInt(5)),
                () -> new ItemStack(Items.GOLD_NUGGET, 3 + random.nextInt(5)),
                () -> new ItemStack(Items.COAL, 1 + random.nextInt(3)),
                () -> new ItemStack(Items.ARROW, 1 + random.nextInt(2)),
                () -> new ItemStack(Items.EXPERIENCE_BOTTLE),
                () -> new ItemStack(Items.GLASS_BOTTLE)
        );
        List<Supplier<ItemStack>> rarePool = List.of(
                () -> new ItemStack(Items.COPPER_INGOT, 1 + random.nextInt(2)),
                () -> new ItemStack(Items.IRON_INGOT),
                () -> new ItemStack(Items.GOLD_INGOT),
                () -> new ItemStack(Items.LAPIS_LAZULI),
                () -> new ItemStack(Items.REDSTONE)
        );

        float roll = random.nextFloat();
        if (roll < 0.80f) { // Common
            int count = 2 + random.nextInt(2);
            for (int i = 0; i < count; i++) launchItemStack(commonPool.get(random.nextInt(commonPool.size())).get());
        } else if (roll < 0.95f) { // Uncommon
            int count = 1 + random.nextInt(2);
            for (int i = 0; i < count; i++) launchItemStack(uncommonPool.get(random.nextInt(uncommonPool.size())).get());
        } else { // Rare
            launchItemStack(rarePool.get(random.nextInt(rarePool.size())).get());
        }
    }

    private void spawnLaunchParticles(ParticleEffect particleEffect, int count) {
        if (world instanceof ServerWorld serverWorld) {
            BlockState state = getCachedState();
            if (!(state.getBlock() instanceof SlotMachineBlock)) return;
            Direction facing = state.get(SlotMachineBlock.FACING);

            double spawnX = pos.getX() + 0.5 + facing.getOffsetX() * 0.7;
            double spawnY = pos.getY() + 0.25;
            double spawnZ = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.7;

            serverWorld.spawnParticles(particleEffect, spawnX, spawnY, spawnZ, count, 0.1, 0.1, 0.1, 0.05);
        }
    }

    private void spawnExplosionParticles(ParticleEffect particleEffect, int count, double speed) {
        if (world instanceof ServerWorld serverWorld) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            serverWorld.spawnParticles(particleEffect, x, y, z, count, 1.5, 1.5, 1.5, speed);
        }
    }

    private void generateLoot(ServerWorld world, RegistryKey<LootTable> lootTableKey) {
        LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(lootTableKey);
        LootContextParameterSet params = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .build(LootContextTypes.CHEST);
        lootTable.generateLoot(params, this::launchItemStack);
    }

    private void launchEntity(Entity entity, float forwardVelocityMultiplier) {
        if (world == null || world.isClient) return;

        BlockState state = getCachedState();
        if (!(state.getBlock() instanceof SlotMachineBlock)) return;
        Direction facing = state.get(SlotMachineBlock.FACING);
        Random random = world.getRandom();

        double spawnX = pos.getX() + 0.5 + facing.getOffsetX() * 0.65;
        double spawnY = pos.getY() + 0.1;
        double spawnZ = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.65;
        entity.setPosition(spawnX, spawnY, spawnZ);

        float forwardVelocity = 0.5f * forwardVelocityMultiplier;
        float horizontalVelocity = (random.nextFloat() - 0.5f) * 0.2f;

        Vec3d velocity = new Vec3d(facing.getUnitVector()).multiply(forwardVelocity);
        velocity = velocity.add(new Vec3d(facing.rotateYClockwise().getUnitVector()).multiply(horizontalVelocity));
        velocity = velocity.add(0, 0.2, 0);

        entity.setVelocity(velocity);
        world.spawnEntity(entity);
    }

    private void launchEntity(Entity entity) {
        launchEntity(entity, 1.0f);
    }

    private void launchItemStack(ItemStack stack)   {
        if (stack.isEmpty()) return;
        assert world != null;
        ItemEntity itemEntity = new ItemEntity(world, 0, 0, 0, stack);
        itemEntity.setToDefaultPickupDelay();
        launchEntity(itemEntity, 0.3f);
    }
}