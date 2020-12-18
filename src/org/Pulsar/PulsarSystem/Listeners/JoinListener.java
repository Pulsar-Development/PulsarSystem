package org.Pulsar.PulsarSystem.Listeners;

import java.io.IOException;
import org.Pulsar.PulsarSystem.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import net.countercraft.movecraft.constructor.PrintHandler;

public class JoinListener implements Listener {
	
	PrintHandler ph = new PrintHandler();

	private Main main;

	public JoinListener(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (ItemStack i : e.getPlayer().getInventory().getContents()) {
			if (i != null) {
				main.send(i.getType().toString());
			}
		}
		if (!main.isInitialised()) {
			BukkitRunnable r = new BukkitRunnable() {
				@Override
				public void run() {
					if (main.getServer().getOnlinePlayers().size() > 0) {
						main.connect();
					}
				}

			};
			r.runTaskLater(main, 100L);
		} else {
			if (main.getCelestials() != null) {
				e.getPlayer().teleport(new Location(Bukkit.getWorld("space"), 640.0, 128.0, 0.0));
				new BukkitRunnable() {
					public void run() {
						if (e.getPlayer().hasMetadata("pulsar-ship")) {
							String shipid = e.getPlayer().getMetadata("pulsar-ship").get(0).asString();
							try {
								ph.pastePrint(e.getPlayer(), shipid);
							} catch (IOException ex) {}
							e.getPlayer().removeMetadata("pulsar-ship", main);
							this.cancel();
						}
					}
				}.runTaskTimer(main, 20L, 20L);
			} else {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (e.getPlayer().hasMetadata("pulsar-world")) {
							if (Bukkit.getWorld(e.getPlayer().getMetadata("pulsar-world").get(0).asString()) != null) {
								e.getPlayer()
										.teleport(new Location(
												Bukkit.getWorld(
														e.getPlayer().getMetadata("pulsar-world").get(0).asString()),
												0.0, 200.0, 0.0));
								e.getPlayer().removeMetadata("pulsar-world", main);
								String shipid = e.getPlayer().getMetadata("pulsar-ship").get(0).asString();
								try {
									ph.pastePrint(e.getPlayer(), shipid);
								} catch (IOException ex) {}
								e.getPlayer().removeMetadata("pulsar-ship", main);
								this.cancel();
							}
						}
					}
				}.runTaskTimer(main, 20L, 20L);
			}
		}
	}

}
