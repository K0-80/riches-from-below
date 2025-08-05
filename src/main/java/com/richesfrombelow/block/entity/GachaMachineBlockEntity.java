package com.richesfrombelow.block.entity;

import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.entities.custom.GatchaBallEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GachaMachineBlockEntity extends BlockEntity {

    private int activationTicks = 0;
    private static final int ANIMATION_TIME = 40;

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
                    gatchaBall.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);

                    Vec3d velocity = new Vec3d(state.get(com.richesfrombelow.block.GachaMachineBlock.FACING).getUnitVector())
                            .multiply(0.3);
                    gatchaBall.setVelocity(velocity);
                    serverWorld.spawnEntity(gatchaBall);
                }

                serverWorld.playSound(null, pos, SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
                serverWorld.spawnParticles(ParticleTypes.POOF,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        10, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }

    public void activate() {
        if (this.activationTicks == 0 && this.world != null) {
            this.activationTicks = ANIMATION_TIME;
            markDirty();
        }
    }

    public int getActivationTicks() {
        return activationTicks;
    }

}