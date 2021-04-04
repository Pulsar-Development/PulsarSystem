package org.Pulsar.PulsarSystem.Listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.Pulsar.PulsarSystem.Main;
import org.Pulsar.PulsarSystem.Utils.Celestials;
import org.Pulsar.PulsarSystem.Utils.ItemStackSerializator;
import org.Pulsar.PulsarSystem.Utils.Moon;
import org.Pulsar.PulsarSystem.Utils.Planet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class PluginMsgListener implements PluginMessageListener {

	private Main main;
	private ItemStackSerializator ser = new ItemStackSerializator();

	public PluginMsgListener(Main main) {
		this.main = main;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
		if (channel.equalsIgnoreCase("pulsar:system")) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String question = in.readUTF();
			switch (question) {
			case "space":
				main.setCelestials(new Celestials(main));
				main.space();
				BukkitRunnable r = new BukkitRunnable() {
					@Override
					public void run() {
						if (main.getServer().getWorld("space") != null) {
							Location loc = new Location(Bukkit.getWorld("space"), 640.0, 128.0, 0.0);
							main.getServer().getOnlinePlayers().forEach(p -> p.teleport(loc));
							String star = in.readUTF();
							String planetline = in.readUTF();
							List<String> planets = new ArrayList<String>();
							if (!planetline.equalsIgnoreCase("")) {
								if (planetline.contains(";")) {
									planets = Arrays.asList(planetline.split(";"));
								} else {
									planets.add(planetline);
								}
							}
							String moonline = in.readUTF();
							List<String> moons = new ArrayList<String>();
							if (!moonline.equalsIgnoreCase("")) {
								moons = Arrays.asList(moonline.split(";"));
							}
							if (!new File(main.getDataFolder(), "schemes").exists()) {
								new File(main.getDataFolder(), "schemes").mkdir();
							}
							main.getCelestials().placeStar(star);
							for (String planet : planets) {
								main.getCelestials().placePlanet(planet);
							}
							for (String moon : moons) {
								for (String p : planets) {
									if (p.startsWith(moon.split("_")[0].split(",")[0])) {
										main.getCelestials().placeMoon(Byte.valueOf(p.split("_")[1]), moon);
										break;
									}
								}
							}
							this.cancel();
						}
					}
				};
				r.runTaskTimer(main, 20L, 200L);
				break;
			case "non-space":
				main.nonSpace(in.readUTF().split(";"));
				break;
			case "playerinfo":
				String inv = in.readUTF();
				String shipid = in.readUTF();
				String world = in.readUTF();
				if (main.getCelestials() == null) {
					receiver.setMetadata("pulsar-world", new FixedMetadataValue(main, world));
				}
				receiver.setMetadata("pulsar-ship", new FixedMetadataValue(main, shipid));
				ItemStack[] items = ser.contentsfromBase64(inv);
				receiver.getInventory().setContents(items);
				break;
			case "online":
				if (main.getCelestials() != null) {
					String server = in.readUTF();
					for (Moon m : main.getCelestials().getMoons()) {
						if (m.getServer().equalsIgnoreCase(server)) {
							m.setOnline(true);
						}
					}
					for (Planet p : main.getCelestials().getPlanets()) {
						if (p.getServer().equalsIgnoreCase(server)) {
							p.setOnline(true);
						}
					}
				}
				break;
			}
		}
	}

}
