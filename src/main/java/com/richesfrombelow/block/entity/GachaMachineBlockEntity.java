package com.richesfrombelow.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class GachaMachineBlockEntity extends BlockEntity {

    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GACHA_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }


}