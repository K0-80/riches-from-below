package com.richesfrombelow.block.entity;

import com.mojang.authlib.GameProfile;
import com.richesfrombelow.block.PersonalVaultBlock;
import com.richesfrombelow.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PersonalVaultBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private static final int LOCK_TIME_PER_FRAGMENT_TICKS = 72000; // 1 hour

    private UUID ownerUuid;
    private long nextCheckTime;

    public PersonalVaultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PERSONAL_VAULT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.richesfrombelow.personal_vault");
    }

    public long getRemainingLockTime(World world) {
        if (world == null) return 0;
        long remaining = this.nextCheckTime - world.getTime();
        return Math.max(0, remaining);
    }

    public String getOwnerName(World world) {
        if (this.ownerUuid == null || world.getServer() == null) {
            return "unknown";
        }
        Optional<GameProfile> profile = Objects.requireNonNull(world.getServer().getUserCache()).getByUuid(this.ownerUuid);
        return profile.map(GameProfile::getName).orElse("an unknown player");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    public void setOwner(PlayerEntity player) {
        this.ownerUuid = player.getUuid();
        if (this.world != null) {
            this.nextCheckTime = this.world.getTime();
        } else {
            this.nextCheckTime = 0L;
        }        markDirty();
    }

    public void addLockTime(World world) {
        if (world == null) return;
        long currentTime = world.getTime();
        if (this.nextCheckTime < currentTime) {
            this.nextCheckTime = currentTime + LOCK_TIME_PER_FRAGMENT_TICKS;
        } else {
            this.nextCheckTime += LOCK_TIME_PER_FRAGMENT_TICKS;
        }
        markDirty();
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        nbt.putLong("NextCheckTime", this.nextCheckTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        this.nextCheckTime = nbt.getLong("NextCheckTime");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    public static void tick(World world, BlockPos pos, BlockState state, PersonalVaultBlockEntity be) {
        if (world.isClient() || !state.get(PersonalVaultBlock.LOCKED)) {
            return;
        }

        if (world.getTime() >= be.nextCheckTime) {
            world.setBlockState(pos, state.with(PersonalVaultBlock.LOCKED, false), 3);
            be.markDirty();
        }
    }
}