package org.Pulsar.PulsarSystem;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.Pulsar.PulsarSystem.Commands.Manager;
import org.Pulsar.PulsarSystem.Listeners.InteractListener;
import org.Pulsar.PulsarSystem.Listeners.JoinListener;
import org.Pulsar.PulsarSystem.Listeners.MoveListener;
import org.Pulsar.PulsarSystem.Listeners.PluginMsgListener;
import org.Pulsar.PulsarSystem.Listeners.SpaceMoveListener;
import org.Pulsar.PulsarSystem.Utils.Celestials;
import org.Pulsar.PulsarSystem.Utils.IChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin {

	private boolean initialised = false;
	private Celestials celestials;
	private static Main instance;
	private File f = new File(getDataFolder(), "locale.yml");
	private FileConfiguration locale;
	IChunkGenerator cg = new IChunkGenerator();
	MoveListener m = null;
	int size = 0;

	public void onEnable() {
		instance = this;
		saveDefaultLocale();
		getServer().getMessenger().registerOutgoingPluginChannel(this, "pulsar:system");
		getServer().getMessenger().registerIncomingPluginChannel(this, "pulsar:system", new PluginMsgListener(this));
		new JoinListener(this);
		send("&aPlugin enabled!");
	}

	public void onDisable() {
		if (initialised) {
			if (getCelestials() != null) {
				getCelestials().clear();
			}
		}
		reloadLocale();
		send("&cPlugin disabled!");
	}

	public void send(String s) {
		getServer().getConsoleSender().sendMessage("[PulsarSystem] " + ChatColor.translateAlternateColorCodes('&', s));
	}

	public boolean isInitialised() {
		return initialised;
	}

	public void connect() {
		initialised = true;
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("connect");
		Iterables.get(Bukkit.getOnlinePlayers(), 0).sendPluginMessage(this, "pulsar:system", out.toByteArray());
	}

	public Celestials getCelestials() {
		return celestials;
	}

	public void setCelestials(Celestials celestials) {
		this.celestials = celestials;
	}

	public void space() {
		createWorld();
		new SpaceMoveListener(this);
		new InteractListener(this);
		new Manager(this);
	}

	private void createWorld() {
		if (Bukkit.getWorld("space") == null) {
			WorldCreator wc = new WorldCreator("space");
			wc.environment(Environment.THE_END);
			wc.generator(cg);
			wc.generateStructures(false);
			World w = wc.createWorld();
			w.getEntities().forEach(e -> e.remove());
			w.setDifficulty(Difficulty.PEACEFUL);
			w.setPVP(false);
		}
	}

	public void nonSpace(String[] worlds) {
		m = new MoveListener(this);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (size < worlds.length) {
					if (Bukkit.getWorld(worlds[size]) == null) {
						new WorldCreator(worlds[size]).createWorld();
					}
					size++;
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(this, 0L, 100L);
	}

	public static Main getInstance() {
		return instance;
	}

	private void saveDefaultLocale() {
		if (!f.exists()) {
			saveResource("locale.yml", false);
		}
		locale = YamlConfiguration.loadConfiguration(f);
	}

	private void reloadLocale() {
		locale = YamlConfiguration.loadConfiguration(f);
		InputStream defConfigStream = getResource("locale.yml");
		if (defConfigStream == null) {
			return;
		}
		locale.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	public FileConfiguration getLocale() {
		return locale;
	}

	public void sendNotLoaded(String name, String type) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("not-loaded");
		out.writeUTF(type);
		out.writeUTF(name);
		Iterables.get(Bukkit.getOnlinePlayers(), 0).sendPluginMessage(this, "pulsar:system", out.toByteArray());
	}

	public double getDay(byte number, String type, double period) {
		long time = System.currentTimeMillis();
		int days = (int) (time / 3600000);
		while (days >= 720) {
			days -= 720;
		}
		if (type.equalsIgnoreCase("planet")) {
			while (days >= period) {
				days -= period;
			}
		} else {
			while (days >= period) {
				days -= period;
			}
		}
		return days;
	}

	public MoveListener getMoveListener() {
		return m;
	}

}
