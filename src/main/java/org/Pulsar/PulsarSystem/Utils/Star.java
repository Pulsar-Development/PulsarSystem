package org.Pulsar.PulsarSystem.Utils;

import java.io.File;
import org.Pulsar.PulsarSystem.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

public class Star {

	private String name;
	private boolean activated = false;
	private short size = 0;

	public Star(String name) {
		this.name = name;
		if (Main.getInstance().getCelestials().schemeExists(name)) {
			this.activated = true;
			place();
		} else {
			Main.getInstance().send("&cStar &f" + name + " &cis not loaded!");
			Main.getInstance().sendNotLoaded(name, "star");
		}
	}

	public boolean isActivated() {
		return activated;
	}

	public String getName() {
		return name;
	}

	public short getSize() {
		return size;
	}

	public void remove() {
		for (double a = 0 - (size - 1) / 2; a <= 0 + (size - 1) / 2; a++) {
			for (double b = 128 - (size - 1) / 2; b <= 128 + (size - 1) / 2; b++) {
				for (double c = 0 - (size - 1) / 2; c <= 0 + (size - 1) / 2; c++) {
					Location loc = new Location(Bukkit.getWorld("space"), a, b, c);
					if (!loc.getBlock().getType().equals(Material.AIR)) {
						loc.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}

	private void place() {
		size = Main.getInstance().getCelestials().calculateSchemeSize(name);
		for (String s : YamlConfiguration
				.loadConfiguration(new File(Main.getInstance().getDataFolder() + "/schemes", name + ".yml"))
				.getStringList("locations")) {
			new Location(Bukkit.getWorld("space"), Double.valueOf(s.split(";")[0]),
					Double.valueOf(s.split(";")[1]) + 128, Double.valueOf(s.split(";")[2])).getBlock()
							.setType(Material.valueOf(s.split(";")[3]));
		}
	}

	public void deactivate() {
		activated = false;
	}

	public void activate() {
		activated = true;
	}

}
