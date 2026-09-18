package de.steak.macecap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class MaceCap extends JavaPlugin implements Listener {

    private static final double MAX_DAMAGE = 6.5;

    private final Set<UUID> bounties = new HashSet<>();
    private final Random random = new Random();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("MaceCap aktiviert - Maximum: 6.5 Schaden");

        registerShulkerRecipe();

        getLogger().info("Bounty-System aktiviert!");
        getLogger().info("Custom Shulker-Rezept aktiviert!");
    }

    // Mace-Schaden auf maximal 6.5 begrenzen
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        String type = event.getDamageSource().getDamageType().key().value();

        if (type.equals("mace_smash") && event.getDamage() > MAX_DAMAGE) {
            event.setDamage(MAX_DAMAGE);
        }
    }

    // Bounty-System
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        if (killer == null || killer.equals(killed)) {
            return;
        }

        // Wenn der getötete Spieler eine Bounty hatte
        if (bounties.contains(killed.getUniqueId())) {
            bounties.remove(killed.getUniqueId());

            ItemStack reward = createRandomReward();
            killer.getInventory().addItem(reward);

            killer.sendMessage("§6§lBOUNTY! §r§aDu hast die Bounty von §e"
                    + killed.getName() + " §ageholt!");
            killer.sendMessage("§7Belohnung: §f" + reward.getType().name());
        }

        // Der Killer bekommt selbst eine Bounty
        bounties.add(killer.getUniqueId());

        killer.sendMessage("§c§l⚔ BOUNTY! §r§eDu hast jetzt eine Bounty!");
    }

    // Zufällige Netherite-Belohnung
    private ItemStack createRandomReward() {
        Material[] rewards = {
                Material.NETHERITE_SWORD,
                Material.NETHERITE_AXE,
                Material.NETHERITE_PICKAXE,
                Material.NETHERITE_SHOVEL,
                Material.NETHERITE_HOE,
                Material.NETHERITE_HELMET,
                Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS,
                Material.NETHERITE_BOOTS
        };

        Material material = rewards[random.nextInt(rewards.length)];
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Rüstung bekommt Protection III
            if (material.name().contains("HELMET")
                    || material.name().contains("CHESTPLATE")
                    || material.name().contains("LEGGINGS")
                    || material.name().contains("BOOTS")) {

                meta.addEnchant(Enchantment.PROTECTION, 3, true);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    // Eigenes Shulker-Box-Rezept
    private void registerShulkerRecipe() {
        NamespacedKey key = new NamespacedKey(this, "custom_shulker_box");

        // Altes Rezept entfernen, falls vorhanden
        Bukkit.removeRecipe(key);

        ItemStack result = new ItemStack(Material.SHULKER_BOX);

        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape(
                " C ",
                " D ",
                " C "
        );

        recipe.setIngredient('C', Material.CHEST);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);

        Bukkit.addRecipe(recipe);
    }
}
