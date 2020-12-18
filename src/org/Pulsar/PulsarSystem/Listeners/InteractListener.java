package org.Pulsar.PulsarSystem.Listeners;

import org.Pulsar.PulsarSystem.Main;
import org.Pulsar.PulsarSystem.Utils.Colors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;

public class InteractListener implements Listener {

	private Main main;

	public InteractListener(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE)) {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (e.getHand().equals(EquipmentSlot.HAND)) {
					Location block = e.getClickedBlock().getLocation();
					String meta = block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";"
							+ block.getZ();
					e.getPlayer().setMetadata("pulsar-right", new FixedMetadataValue(main, meta));
					e.setCancelled(true);
					e.getPlayer().sendMessage(Colors.clr(main.getLocale().getString("second-set")));
				}
			} else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if (e.getHand().equals(EquipmentSlot.HAND)) {
					Location block = e.getClickedBlock().getLocation();
					String meta = block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";"
							+ block.getZ();
					e.getPlayer().setMetadata("pulsar-left", new FixedMetadataValue(main, meta));
					e.setCancelled(true);
					e.getPlayer().sendMessage(Colors.clr(main.getLocale().getString("first-set")));
				}
			}
		}
	}

}
