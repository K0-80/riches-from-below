package com.richesfrombelow.block.entity;

import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.entities.custom.GatchaBallEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
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

public class GachaMachineBlockEntity extends BlockEntity {

    public static final int ANIMATION_TIME = 2 * 20 ;
    private int activationTicks = 0;


    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GACHA_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, GachaMachineBlockEntity be) {
        if (be.activationTicks > 0) {
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

                    Vec3d velocity = forwardVec.multiply(0.3);
                    gatchaBall.setVelocity(velocity);
                    serverWorld.spawnEntity(gatchaBall);

                    serverWorld.playSound(null, pos, SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    serverWorld.spawnParticles(ParticleTypes.POOF,
                            spawnPos.x, spawnPos.y, spawnPos.z,
                            10, 0.1, 0.1, 0.1, 0.05);
                }
            }
        }
    }

    public void  activate() {
        if (this.activationTicks == 0 && this.world != null) {
            this.activationTicks = ANIMATION_TIME;
            markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public int getActivationTicks() {
        return activationTicks;
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("activationTicks", activationTicks);
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.activationTicks = nbt.getInt("activationTicks");
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