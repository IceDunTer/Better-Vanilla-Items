package dev.icedunter.bvi.screen;

import dev.icedunter.bvi.BVIMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class FletcherTableScreenHandler extends ScreenHandler {
    private final World world;
    private final Inventory inventory = new SimpleInventory(5);

    public FletcherTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player.getWorld());
    }

    public FletcherTableScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld());
    }

    private FletcherTableScreenHandler(int syncId, PlayerInventory playerInventory, World world) {
        super(BVIMod.FLETCHER_SCREEN, syncId);
        this.world = world;
        inventory.onOpen(playerInventory.player);

        // Слот 0: Кремень (26, 18)
        addSlot(new Slot(inventory, 0, 26, 18) {
            @Override public boolean canInsert(ItemStack stack) { return stack.isOf(Items.FLINT); }
            @Override public void markDirty() { super.markDirty(); FletcherTableScreenHandler.this.onContentChanged(inventory); }
        });

        // Слот 1: Палка (26, 37)
        addSlot(new Slot(inventory, 1, 26, 37) {
            @Override public boolean canInsert(ItemStack stack) { return stack.isOf(Items.STICK); }
            @Override public void markDirty() { super.markDirty(); FletcherTableScreenHandler.this.onContentChanged(inventory); }
        });

        // Слот 2: Перо (26, 56)
        addSlot(new Slot(inventory, 2, 26, 56) {
            @Override public boolean canInsert(ItemStack stack) { return stack.isOf(Items.FEATHER); }
            @Override public void markDirty() { super.markDirty(); FletcherTableScreenHandler.this.onContentChanged(inventory); }
        });

        // Слот 3: Зелье (61, 37)
        addSlot(new Slot(inventory, 3, 61, 37) {
            @Override public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.POTION) && !PotionUtil.getPotionEffects(stack).isEmpty();
            }
            @Override public void markDirty() { super.markDirty(); FletcherTableScreenHandler.this.onContentChanged(inventory); }
        });

        // Слот 4: Результат (133, 37)
        addSlot(new Slot(inventory, 4, 133, 37) {
            @Override public boolean canInsert(ItemStack stack) { return false; }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack result = super.takeStack(amount);
                if (!result.isEmpty()) {
                    removeIngredients();
                }
                return result;
            }
        });

        // Инвентарь игрока
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (int i = 0; i < 9; ++i)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));

        updateResult();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        updateResult();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        for (int i = 0; i < 5; ++i) {
            ItemStack stack = inventory.getStack(i);

            if (!stack.isEmpty()) {
                if (!player.giveItemStack(stack)) {
                    player.dropItem(stack, false);
                }

                inventory.setStack(i, ItemStack.EMPTY);
            }
        }
    }

    private void removeIngredients() {
        inventory.removeStack(0, 1); // Кремень
        inventory.removeStack(1, 1); // Палка
        inventory.removeStack(2, 1); // Перо
        inventory.removeStack(3, 1); // Зелье
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            itemStack = originalStack.copy();

            if (index == 4) {
                if (!this.insertItem(originalStack, 5, 41, true)) return ItemStack.EMPTY;
                removeIngredients();
                onContentChanged(this.inventory);
            } else if (index >= 5 && index < 41) {
                if (!this.insertItem(originalStack, 0, 4, false)) return ItemStack.EMPTY;
            } else if (index >= 0 && index < 4) {
                if (!this.insertItem(originalStack, 5, 41, true)) return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (originalStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, originalStack);
        }

        return itemStack;
    }

    private void updateResult() {
        boolean hasFlint = inventory.getStack(0).isOf(Items.FLINT) && inventory.getStack(0).getCount() >= 1;
        boolean hasStick = inventory.getStack(1).isOf(Items.STICK) && inventory.getStack(1).getCount() >= 1;
        boolean hasFeather = inventory.getStack(2).isOf(Items.FEATHER) && inventory.getStack(2).getCount() >= 1;
        ItemStack potion = inventory.getStack(3);

        if (hasFlint && hasStick && hasFeather) {
            ItemStack result;

            if (!potion.isEmpty() && potion.isOf(Items.POTION)) {
                result = new ItemStack(Items.TIPPED_ARROW, 8);
                result = PotionUtil.setPotion(result, PotionUtil.getPotion(potion));

                if (potion.hasNbt()) {
                    if (potion.getNbt().contains("CustomPotionEffects")) {
                        result.getOrCreateNbt().put("CustomPotionEffects", potion.getNbt().get("CustomPotionEffects"));
                    }
                    if (potion.getNbt().contains("CustomPotionColor")) {
                        result.getOrCreateNbt().putInt("CustomPotionColor", potion.getNbt().getInt("CustomPotionColor"));
                    }
                }
            } else {
                result = new ItemStack(Items.ARROW, 8);
            }

            inventory.setStack(4, result);
            this.getSlot(4).markDirty();
        } else {
            inventory.setStack(4, ItemStack.EMPTY);
            this.getSlot(4).markDirty();
        }

        this.sendContentUpdates();
    }
}