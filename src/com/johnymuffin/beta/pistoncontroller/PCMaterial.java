package com.johnymuffin.beta.pistoncontroller;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class PCMaterial {
    private final int itemID;
    private final int damageValue;
    private final boolean compareDamage;

    private final boolean removeHigher;

    public int getItemID() {
        return itemID;
    }

    public int getDamageValue() {
        return damageValue;
    }

    public boolean isCompareDamage() {
        return compareDamage;
    }

    public PCMaterial(int itemID, int damageValue, boolean compareDamage, boolean removeHigher) {
        this.itemID = itemID;
        this.damageValue = damageValue;
        this.compareDamage = compareDamage;
        this.removeHigher = removeHigher;
    }

    public PCMaterial(ItemStack itemStack) {
        this.itemID = itemStack.getTypeId();
        this.removeHigher = false; //This can't be determined by Item Stack alone.
        if (itemStack.getData() != null) {
            this.damageValue = itemStack.getData().getData();
            compareDamage = true;
        } else {
            this.damageValue = 0;
            this.compareDamage = false;
        }

    }


    public boolean matches(ItemStack itemStack) {
        if (itemStack.getTypeId() == itemID) {
            if (!compareDamage) {
                return true;
            }
            if (itemStack.getData() != null) {
                if (itemStack.getData().getData() == damageValue) {
                    return true;
                }
                if (removeHigher && itemStack.getData().getData() >= damageValue) {
                    return true;
                }


            }
        }
        return false;
    }

    public boolean matches(Block block) {
        if (block.getTypeId() == itemID) {
            if (!compareDamage) {
                return true;
            }
            if (block.getData() == damageValue) {
                return true;
            }
            if (removeHigher && block.getData() >= damageValue) {
                return true;
            }
        }
        return false;
    }

}
