package com.itatiers;

import com.itatiers.misc.ConfigManager;
import com.itatiers.misc.Modes;
import com.itatiers.textures.ColorControl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Set;

public class InventoryChecker {
    public static void checkInventory(MinecraftClient client) {
        if (client.player == null) return;

        Modes detected = null;

        PlayerInventory inventory = client.player.getInventory();

        if (checkVanilla(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.VANILLA;
            detected = Modes.VANILLA;
        }

        if (checkSword(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.SWORD;
            detected = Modes.SWORD;
        }

        if (checkUhc(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.UHC;
            detected = Modes.UHC;
        }

        if (checkPot(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.POT;
            detected = Modes.POT;
        }

        if (checkNethPot(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.NETH_POT;
            detected = Modes.NETH_POT;
        }

        if (checkSmp(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.SMP;
            detected = Modes.SMP;
        }

        if (checkAxe(inventory)) {
            ItaTiersClient.activeItaTiersMode = Modes.AXE;
            detected = Modes.AXE;
        }

        if (detected != null)
            client.player.sendMessage(Text.literal("").append(detected.label).append(Text.literal(" was detected").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text")))), true);
        else
            client.player.sendMessage(Text.literal("No gamemode detected").setStyle(Style.EMPTY.withColor(ColorControl.getColor("red"))), true);

        ConfigManager.saveConfig();
    }

    private static boolean checkVanilla(PlayerInventory inventory) {
        boolean hasObsidian = false;
        boolean hasCrystal = false;
        boolean hasAnchor = false;
        boolean hasGlowstone = false;
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasObsidian |= hasItem(stack, Items.OBSIDIAN);
            hasCrystal |= hasItem(stack, Items.END_CRYSTAL);
            hasAnchor |= hasItem(stack, Items.RESPAWN_ANCHOR);
            hasGlowstone |= hasItem(stack, Items.GLOWSTONE);
            hasSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);
        }

        return hasObsidian && hasCrystal && hasAnchor && hasGlowstone && hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkSword(PlayerInventory inventory) {
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasSword |= hasItem(stack, Items.DIAMOND_SWORD);
            hasHelmet |= hasItem(stack, Items.DIAMOND_HELMET);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE);
            hasLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS);
            hasBoots |= hasItem(stack, Items.DIAMOND_BOOTS);

            if (SWORD_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkUhc(PlayerInventory inventory) {
        boolean hasShield = false;
        boolean hasGaps = false;
        boolean hasLava = false;
        boolean hasWater = false;
        boolean hasCobwebs = false;
        boolean hasEnchantedBow = false;
        boolean hasEnchantedCrossbow = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasShield |= hasItem(stack, Items.SHIELD);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasLava |= hasItem(stack, Items.LAVA_BUCKET);
            hasWater |= hasItem(stack, Items.WATER_BUCKET);
            hasCobwebs |= hasItem(stack, Items.COBWEB);
            hasEnchantedBow |= hasItem(stack, Items.BOW, true);
            hasEnchantedCrossbow |= hasItem(stack, Items.CROSSBOW, true);
            hasEnchantedSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasEnchantedAxe |= hasItem(stack, Items.DIAMOND_AXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true) || hasItem(stack, Items.IRON_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (UHC_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasShield && hasGaps && hasLava && hasWater && hasCobwebs && hasEnchantedBow && hasEnchantedCrossbow && hasEnchantedSword &&
                hasEnchantedAxe && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkPot(PlayerInventory inventory) {
        boolean hasSteak = false;
        boolean hasPotions = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasSteak |= hasItem(stack, Items.COOKED_BEEF);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasEnchantedSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasEnchantedHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (POT_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasSteak && hasPotions && hasEnchantedSword && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkNethPot(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasPotions = false;
        boolean hasTotem = false;
        boolean hasXp = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasXp |= hasItem(stack, Items.EXPERIENCE_BOTTLE);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (NETHPOT_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasPotions && hasTotem && hasXp && hasEnchantedSword && hasEnchantedHelmet &&
                hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkSmp(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasPotions = false;
        boolean hasTotem = false;
        boolean hasXp = false;
        boolean hasPearls = false;
        boolean hasShield = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasXp |= hasItem(stack, Items.EXPERIENCE_BOTTLE);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasShield |= hasItem(stack, Items.SHIELD);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedAxe |= hasItem(stack, Items.NETHERITE_AXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (SMP_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasPotions && hasTotem && hasXp && hasPearls && hasShield && hasEnchantedAxe &&
                hasEnchantedSword && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkAxe(PlayerInventory inventory) {
        boolean hasBow = false;
        boolean hasCrossbow = false;
        boolean hasShield = false;
        boolean hasSword = false;
        boolean hasAxe = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasBow |= hasItem(stack, Items.BOW, false);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW, false);
            hasShield |= hasItem(stack, Items.SHIELD, false);
            hasSword |= hasItem(stack, Items.DIAMOND_SWORD, false);
            hasAxe |= hasItem(stack, Items.DIAMOND_AXE, false);
            hasHelmet |= hasItem(stack, Items.DIAMOND_HELMET, false);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, false);
            hasLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, false);
            hasBoots |= hasItem(stack, Items.DIAMOND_BOOTS, false);

            if (AXE_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasBow && hasCrossbow && hasShield && hasSword && hasAxe && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean hasItem(ItemStack itemStack, Item item, boolean needEnchant) {
        return itemStack.getItem() == item && (needEnchant == itemStack.hasEnchantments());
    }

    private static boolean hasItem(ItemStack itemStack, Item item) {
        return itemStack.getItem() == item;
    }

    private static final Set<Item> SWORD_NON_ALLOWED = Set.of(
            Items.DIAMOND_AXE,
            Items.COBWEB,
            Items.SHIELD,
            Items.ENDER_PEARL,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> UHC_NON_ALLOWED = Set.of(
            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> POT_NON_ALLOWED = Set.of(
            Items.DIAMOND_AXE,
            Items.GOLDEN_APPLE,
            Items.SHIELD,
            Items.COBWEB,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> NETHPOT_NON_ALLOWED = Set.of(
            Items.ENDER_PEARL,
            Items.SHIELD,

            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> SMP_NON_ALLOWED = Set.of(
            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_PICKAXE,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> AXE_NON_ALLOWED = Set.of(
            Items.ENDER_PEARL,
            Items.COBWEB,
            Items.GOLDEN_APPLE,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );
}