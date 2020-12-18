package org.Pulsar.PulsarSystem.Listeners;

import org.Pulsar.PulsarSystem.Main;
import org.Pulsar.PulsarSystem.Utils.ItemStackSerializator;
import org.Pulsar.PulsarSystem.Utils.Moon;
import org.Pulsar.PulsarSystem.Utils.Planet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.events.CraftTranslateEvent;

public class SpaceMoveListener implements Listener {

	private Main main;
	private ItemStackSerializator ser = new ItemStackSerializator();

	public SpaceMoveListener(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onMove(CraftTranslateEvent e) {
		Player pl = e.getCraft().getNotificationPlayer();
		Location loc = pl.getLocation();
		for (Planet p : main.getCelestials().getPlanets()) {
			double[] locs = p.getCurrentRotation();
			int size = p.getSize();
			if (loc.getX() < (locs[0] + size + 15) && loc.getX() > (locs[0] - size - 15)) {
				if (loc.getZ() < (locs[1] + size + 15) && loc.getZ() > (locs[1] - size - 15)) {
					if (loc.getY() < (128 + size + 15) && pl.getLocation().getY() > (128 - size - 15)) {
						if (p.isActivated()) {
							if (p.isOnline()) {
								ByteArrayDataOutput out = ByteStreams.newDataOutput();
								out.writeUTF("sendto");
								out.writeUTF(p.getName());
								out.writeUTF(ser.contentsToBase64(pl.getInventory().getContents()));
								out.writeUTF(e.getCraft().getID());
								CraftManager.getInstance().removeCraft(e.getCraft());
								pl.getInventory().clear();
								main.getServer().getScheduler().runTaskLater(main, new Runnable() {
									public void run() {
										pl.teleport(new Location(pl.getWorld(), 640.0, 128.0, 0.0));
									}
								}, 1L);
								pl.sendPluginMessage(main, "pulsar:system", out.toByteArray());
							}
						}
						break;
					}
				}
			}
		}
		for (Moon m : main.getCelestials().getMoons()) {
			double[] locs = m.getCurrentRotation();
			int size = m.getSize();
			if (loc.getX() < (locs[0] + size + 15) && loc.getX() > (locs[0] - size - 15)) {
				if (loc.getZ() < (locs[1] + size + 15) && loc.getZ() > (locs[1] - size - 15)) {
					if (loc.getY() < (128 + size + 15) && pl.getLocation().getY() > (128 - size - 15)) {
						if (m.isActivated()) {
							if (m.isOnline()) {
								ByteArrayDataOutput out = ByteStreams.newDataOutput();
								out.writeUTF("sendto");
								out.writeUTF(m.getName());
								out.writeUTF(ser.contentsToBase64(pl.getInventory().getContents()));
								pl.sendPluginMessage(main, "pulsar:system", out.toByteArray());
								pl.getInventory().clear();
								e.setCancelled(true);
								CraftManager.getInstance().removeCraft(e.getCraft());
								pl.teleport(new Location(pl.getWorld(), 640.0, 128.0, 0.0));
							}
						}
						break;
					}
				}
			}
		}
	}

}
