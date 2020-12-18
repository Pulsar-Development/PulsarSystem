package org.Pulsar.PulsarSystem.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.Pulsar.PulsarSystem.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Celestials {

	private Main main;
	private List<Planet> planets = new ArrayList<Planet>();
	private List<Moon> moons = new ArrayList<Moon>();
	private Star star = null;

	public Celestials(Main main) {
		this.main = main;
	}

	public boolean schemeExists(String celestial) {
		if (new File(main.getDataFolder(), "schemes").exists()) {
			if (new File(main.getDataFolder() + "/schemes", celestial + ".yml").exists()) {
				return true;
			}
		}
		return false;
	}

	public void placeStar(String star) {
		this.star = new Star(star);
	}

	public void placePlanet(String planet) {
		getPlanets().add(new Planet(planet.split("_")[0], Byte.valueOf(planet.split("_")[1]), Boolean.valueOf(planet.split("_")[2]), planet.split("_")[3]));
	}

	public void placeMoon(byte planetNumber, String moon) {
		moons.add(new Moon(moon.split(",")[1].split("_")[0], Byte.valueOf(moon.split(",")[1].split("_")[1]),
				moon.split(",")[0], planetNumber, Boolean.valueOf(moon.split(",")[1].split("_")[2]), moon.split(",")[1].split("_")[3]));
	}

	public Star getStar() {
		return star;
	}

	public void clear() {
		if (getStar() != null) {
			getStar().remove();
		}
		getPlanets().forEach(p -> p.remove());
		moons.forEach(m -> m.remove());
	}

	public short calculateSchemeSize(String name) {
		List<Double> xes = new ArrayList<Double>();
		for (String s : YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/schemes", name + ".yml"))
				.getStringList("locations")) {
			xes.add(Double.valueOf(s.split(";")[0]));
		}
		double maxx = 0;
		double minx = Double.MAX_VALUE;
		for (double x : xes) {
			if (minx > x) {
				minx = x;
			}
			if (maxx < x) {
				maxx = x;
			}
		}
		return (short) (maxx - minx + 1);
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public List<Moon> getMoons() {
		return moons;
	}

	public void nullStar() {
		star = null;
	}

	public void sendUnload(String name, String type) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("unload");
		out.writeUTF(name);
		out.writeUTF(type);
		Iterables.get(main.getServer().getOnlinePlayers(), 0).sendPluginMessage(main, "pulsar:system",
				out.toByteArray());
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(Colors.clr(main.getLocale().getString("celestial-unloaded").replaceAll("%name%", name)));
		}
	}

}
