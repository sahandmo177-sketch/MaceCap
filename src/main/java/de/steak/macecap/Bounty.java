package de.steak.macecap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Bounty implements Listener {

    private final Set<UUID> bounties = new HashSet<>();
    private final Random random = new Random();

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        if (killer == null || killer.equals(killed)) {
            return;
        }

        // Hat der getötete Spieler eine Bounty?
        if (bounties.contains(killed.getUniqueId())) {
            bounties.remove(killed.getUniqueId());

            ItemStack reward = createRandomReward();
            killer.getInventory().addItem(reward);

            killer.sendMessage("§6§lBOUNTY! §r§aDu hast die Bounty von §e"
                    + killed.getName() + " §ageholt!");
            killer.sendMessage("§7Belohnung: §f" + reward.getType().name());
        }

        // Der Killer bekommt jetzt selbst eine Bounty
        bounties.add(killer.getUniqueId());

        killer.sendMessage("§c§l⚔ BOUNTY! §r§eDu hast jetzt eine Bounty!");
    }

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
            // Rüstung bekommt maximal Protection III
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
}
