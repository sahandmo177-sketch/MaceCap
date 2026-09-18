package de.steak.macecap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MaceCap extends JavaPlugin implements Listener {

    private static final double MAX_DAMAGE = 6.5;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        Bounty bounty = new Bounty();
        getServer().getPluginManager().registerEvents(bounty, this);

        getLogger().info("MaceCap aktiviert - Maximum: 6.5 Schaden");
        getLogger().info("Bounty-System aktiviert!");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        String type = event.getDamageSource().getDamageType().key().value();

        if (type.equals("mace_smash") && event.getDamage() > MAX_DAMAGE) {
            event.setDamage(MAX_DAMAGE);
        }
    }
}
