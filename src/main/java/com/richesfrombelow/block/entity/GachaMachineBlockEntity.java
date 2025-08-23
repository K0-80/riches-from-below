package com.richesfrombelow.block.entity;

import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.entities.custom.GatchaBallEntity;
import com.richesfrombelow.util.GatchaBallLootTableUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GachaMachineBlockEntity extends BlockEntity {

    private int totalAnimationTicks = 0;
    private int activationTicks = 0;
    private GatchaBallLootTableUtil.GatchaResult pendingLoot = null;

    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GACHA_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, GachaMachineBlockEntity be) {
        if (be.activationTicks > 0) {
            if (!world.isClient()) {
                int remainingTicks = be.activationTicks;
                int totalTicks = be.totalAnimationTicks;
                int ticksPassed = totalTicks - remainingTicks;
                final int SOUND_DELAY_TICKS = 10;
                if (totalTicks > 0 && remainingTicks > 5 && ticksPassed >= SOUND_DELAY_TICKS) {
                    if (remainingTicks % 3 == 0) {
                        int soundPhaseDuration = totalTicks - SOUND_DELAY_TICKS;
                        int ticksIntoSoundPhase = ticksPassed - SOUND_DELAY_TICKS;
                        if (soundPhaseDuration > 0) {
                            float progress = (float) ticksIntoSoundPhase / (float) soundPhaseDuration;
                            float grindstonePitch = 0.8f + (1.0f * progress);
                            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 0.2f, grindstonePitch);
                            float bitPitch = 0.4f + (1.0f * progress);
                            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_BIT, SoundCategory.BLOCKS, 0.4f, bitPitch);
                        }
                    }
                }
            }

            be.activationTicks--;
            if (!world.isClient() && be.activationTicks == 5) {
                ServerWorld serverWorld = (ServerWorld) world;
                GatchaBallEntity gatchaBall = ModEntities.GATCHA_BALL.create(serverWorld);
                if (gatchaBall != null) {
                    Direction facing = state.get(com.richesfrombelow.block.GachaMachineBlock.FACING);

                    Vector3f forwardJoml = facing.getUnitVector();
                    Vec3d forwardVec = new Vec3d(forwardJoml.x(), forwardJoml.y(), forwardJoml.z());

                    Vector3f leftJoml = facing.rotateYCounterclockwise().getUnitVector();
                    Vec3d leftVec = new Vec3d(leftJoml.x(), leftJoml.y(), leftJoml.z());

                    Vec3d spawnPos = Vec3d.ofCenter(pos, 0.2)
                            .add(forwardVec.multiply(0.35))
                            .add(leftVec.multiply(-0.2));

                    gatchaBall.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);

                    gatchaBall.initialize(be.pendingLoot);

                    double sidewaysVelocity = (world.random.nextDouble() - 0.5) * 0.1;
                    Vec3d velocity = forwardVec.multiply(0.3).add(leftVec.multiply(sidewaysVelocity));
                    gatchaBall.setVelocity(velocity);
                    serverWorld.spawnEntity(gatchaBall);

                    serverWorld.playSound(null, pos, SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    serverWorld.spawnParticles(ParticleTypes.POOF,
                            spawnPos.x, spawnPos.y, spawnPos.z,
                            10, 0.1, 0.1, 0.1, 0.05);
                }
                be.pendingLoot = null;
                be.markDirty();
            }
        }
    }

    public void activate() {
        if (this.activationTicks == 0 && this.world != null && !this.world.isClient) {
            this.pendingLoot = GatchaBallLootTableUtil.generateLoot(this.world.getRandom());
            this.totalAnimationTicks = GatchaBallLootTableUtil.getAnimationTicks(this.pendingLoot.tier());
            this.activationTicks = this.totalAnimationTicks;
            markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public int getActivationTicks() {
        return activationTicks;
    }

    public int getTotalAnimationTicks() {
        return totalAnimationTicks;
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("activationTicks", activationTicks);
        nbt.putInt("totalAnimationTicks", totalAnimationTicks);
        if (pendingLoot != null) {
            nbt.putInt("PendingTier", pendingLoot.tier().ordinal());
            NbtList nbtList = new NbtList();
            for (ItemStack itemStack : pendingLoot.items()) {
                if (!itemStack.isEmpty()) {
                    nbtList.add(itemStack.encode(registryLookup));
                }
            }
            nbt.put("PendingLoot", nbtList);
        }
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.activationTicks = nbt.getInt("activationTicks");
        this.totalAnimationTicks = nbt.getInt("totalAnimationTicks");
        if (nbt.contains("PendingTier") && nbt.contains("PendingLoot")) {

            GatchaBallLootTableUtil.GatchaTier tier = GatchaBallLootTableUtil.GatchaTier.values()[nbt.getInt("PendingTier")];
            List<ItemStack> items = new ArrayList<>();

            NbtList nbtList = nbt.getList("PendingLoot", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < nbtList.size(); ++i) {
                ItemStack.fromNbt(registryLookup, nbtList.getCompound(i)).ifPresent(items::add);
            }
            this.pendingLoot = new GatchaBallLootTableUtil.GatchaResult(tier, items);

            if (this.totalAnimationTicks == 0 && this.pendingLoot != null) {
                this.totalAnimationTicks = GatchaBallLootTableUtil.getAnimationTicks(this.pendingLoot.tier());
            }
        } else {
            this.pendingLoot = null;
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}