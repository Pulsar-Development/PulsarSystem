package org.Pulsar.PulsarSystem.Utils;

import java.io.File;
import org.Pulsar.PulsarSystem.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

public class Planet {

	private String name;
	private byte number;
	private boolean activated = false;
	private short size = 0;
	double[] currentRotation = new double[2];
	private boolean online = false;
	private String server;

	public Planet(String name, byte number, boolean online, String server) {
		this.name = name;
		this.number = number;
		this.server = server;
		if (Main.getInstance().getCelestials().schemeExists(name)) {
			this.activated = true;
			place();
		} else {
			Main.getInstance().send("&cPlanet &f" + name + " &cis not loaded!");
			Main.getInstance().sendNotLoaded(name, "planet");
		}
	}

	public byte getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public boolean isActivated() {
		return activated;
	}

	public short getSize() {
		return size;
	}

	public void remove() {
		for (double a = currentRotation[0] - (size - 1) / 2; a <= currentRotation[0] + (size - 1) / 2; a++) {
			for (double b = 128 - (size - 1) / 2; b <= 128 + (size - 1) / 2; b++) {
				for (double c = currentRotation[1] - (size - 1) / 2; c <= currentRotation[1] + (size - 1) / 2; c++) {
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
		currentRotation = Geometry.getCurrentRotation(number);
		for (String s : YamlConfiguration
				.loadConfiguration(new File(Main.getInstance().getDataFolder() + "/schemes", name + ".yml"))
				.getStringList("locations")) {
			new Location(Bukkit.getWorld("space"), Double.valueOf(s.split(";")[0]) + getCurrentRotation()[0],
					Double.valueOf(s.split(";")[1]) + 128, Double.valueOf(s.split(";")[2]) + getCurrentRotation()[1])
							.getBlock().setType(Material.valueOf(s.split(";")[3]));
		}
	}

	public double[] getCurrentRotation() {
		return currentRotation;
	}

	public void deactivate() {
		activated = false;
	}

	public void activate() {
		activated = true;
	}

	public boolean isOnline() {
		return online ;
	}
	
	public void setOnline(boolean b) {
		online = b;
	}

	public String getServer() {
		return server;
	}

}
