package org.Pulsar.PulsarSystem.Commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.Pulsar.PulsarSystem.Main;
import org.Pulsar.PulsarSystem.Utils.Colors;
import org.Pulsar.PulsarSystem.Utils.Geometry;
import org.Pulsar.PulsarSystem.Utils.Moon;
import org.Pulsar.PulsarSystem.Utils.Planet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Manager implements CommandExecutor {

	private Main main;

	public Manager(Main main) {
		this.main = main;
		main.getCommand("pulsarsystem").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("pulsarsystem.admin")) {
				sendInfo(sender);
			}
		} else if (args.length == 1) {
			if (sender.hasPermission("pulsarsystem.admin")) {
				switch (args[0]) {
				case "help":
					sendHelp(sender);
					break;
				case "info":
					sendInfo(sender);
					break;
				case "celestials":
					sendCelestials(sender);
					break;
				case "schemes":
					sendSchemes(sender);
					break;
				default:
					sender.sendMessage(Colors.clr(main.getLocale().getString("args")));
					break;
				}
			} else {
				sender.sendMessage(Colors.clr(main.getLocale().getString("no-perm")));
			}
		} else if (args.length == 3) {
			if (sender.hasPermission("pulsarsystem.admin")) {
				if (args[0].equalsIgnoreCase("celestial")) {
					if (args[1].equalsIgnoreCase("enable")) {
						if (main.getCelestials().getStar().getName().equalsIgnoreCase(args[2])) {
							if (!main.getCelestials().getStar().isActivated()) {
								main.getCelestials().getStar().activate();
								sender.sendMessage(Colors.clr(main.getLocale().getString("enabled")));
							} else {
								sender.sendMessage(Colors.clr(main.getLocale().getString("already-enabled")));
							}
							return true;
						} else {
							for (Planet p : main.getCelestials().getPlanets()) {
								if (p.getName().equalsIgnoreCase(args[2])) {
									if (!p.isActivated()) {
										p.activate();
										sender.sendMessage(Colors.clr(main.getLocale().getString("enabled")));
									} else {
										sender.sendMessage(Colors.clr(main.getLocale().getString("already-enabled")));
									}
									return true;
								}
							}
							for (Moon m : main.getCelestials().getMoons()) {
								if (m.getName().equalsIgnoreCase(args[2])) {
									if (!m.isActivated()) {
										m.activate();
										sender.sendMessage(Colors.clr(main.getLocale().getString("enabled")));
									} else {
										sender.sendMessage(Colors.clr(main.getLocale().getString("already-enabled")));
									}
									return true;
								}
							}
						}
						sender.sendMessage(Colors.clr(main.getLocale().getString("celestial-not-exist")));
					} else if (args[1].equalsIgnoreCase("disable")) {
						if (main.getCelestials().getStar().getName().equalsIgnoreCase(args[2])) {
							if (main.getCelestials().getStar().isActivated()) {
								main.getCelestials().getStar().deactivate();
								sender.sendMessage(Colors.clr(main.getLocale().getString("disabled")));
							} else {
								sender.sendMessage(Colors.clr(main.getLocale().getString("already-disabled")));
							}
							return true;
						} else {
							for (Planet p : main.getCelestials().getPlanets()) {
								if (p.getName().equalsIgnoreCase(args[2])) {
									if (p.isActivated()) {
										p.deactivate();
										sender.sendMessage(Colors.clr(main.getLocale().getString("disabled")));
									} else {
										sender.sendMessage(Colors.clr(main.getLocale().getString("already-disabled")));
									}
									return true;
								}
							}
							for (Moon m : main.getCelestials().getMoons()) {
								if (m.getName().equalsIgnoreCase(args[2])) {
									if (m.isActivated()) {
										m.deactivate();
										sender.sendMessage(Colors.clr(main.getLocale().getString("disabled")));
									} else {
										sender.sendMessage(Colors.clr(main.getLocale().getString("already-disabled")));
									}
									return true;
								}
							}
						}
						sender.sendMessage(Colors.clr(main.getLocale().getString("celestial-not-exist")));
					} else {
						sender.sendMessage(Colors.clr(main.getLocale().getString("args")));
					}
				} else if (args[0].equalsIgnoreCase("scheme")) {
					if (args[1].equalsIgnoreCase("create")) {
						if (sender instanceof Player) {
							Player p = (Player) sender;
							if (!main.getCelestials().schemeExists(args[2])) {
								if (p.hasMetadata("pulsar-left") && p.hasMetadata("pulsar-right")) {
									String first = p.getMetadata("pulsar-left").get(0).asString();
									String second = p.getMetadata("pulsar-right").get(0).asString();
									Location f = new Location(Bukkit.getWorld(first.split(";")[0]),
											Double.valueOf(first.split(";")[1]), Double.valueOf(first.split(";")[2]),
											Double.valueOf(first.split(";")[3]));
									Location s = new Location(Bukkit.getWorld(second.split(";")[0]),
											Double.valueOf(second.split(";")[1]), Double.valueOf(second.split(";")[2]),
											Double.valueOf(second.split(";")[3]));
									if (s.getWorld().getName().equalsIgnoreCase(f.getWorld().getName())) {
										if (Geometry.sidesEqual(f, s)) {
											if (Geometry.isOdd(f.getX(), s.getX())) {
												createScheme(args[2], Geometry.getScheme(f, s));
												sender.sendMessage(Colors.clr(main.getLocale()
														.getString("scheme-created").replaceAll("%name%", args[2])));
											} else {
												p.sendMessage(Colors.clr(main.getLocale().getString("scheme-not-odd")));
											}
										} else {
											p.sendMessage(Colors.clr(main.getLocale().getString("sides-not-equal")));
										}
									} else {
										p.sendMessage(Colors.clr(main.getLocale().getString("different-worlds")));
									}
								} else {
									p.sendMessage(Colors.clr(main.getLocale().getString("selection")));
								}
							} else {
								p.sendMessage(Colors.clr(main.getLocale().getString("scheme-exists")));
							}
						} else {
							sender.sendMessage(Colors.clr(main.getLocale().getString("no-console")));
						}
					} else if (args[1].equalsIgnoreCase("delete")) {
						if (main.getCelestials().schemeExists(args[2])) {
							for (Planet p : main.getCelestials().getPlanets()) {
								if (p.getName().equalsIgnoreCase(args[2])) {
									p.remove();
									new BukkitRunnable() {
										@Override
										public void run() {
											main.getCelestials().getPlanets().remove(p);
											main.getCelestials().sendUnload(args[2], "planet");
										}
									}.runTaskLater(main, 100L);
									break;
								}
							}
							for (Moon m : main.getCelestials().getMoons()) {
								if (m.getName().equalsIgnoreCase(args[2])) {
									m.remove();
									new BukkitRunnable() {
										@Override
										public void run() {
											main.getCelestials().getMoons().remove(m);
											main.getCelestials().sendUnload(args[2], "moon");
										}
									}.runTaskLater(main, 100L);
									break;
								}
							}
							if (main.getCelestials().getStar().getName().equalsIgnoreCase(args[2])) {
								main.getCelestials().getStar().remove();
								new BukkitRunnable() {
									@Override
									public void run() {
										main.getCelestials().nullStar();
										main.getCelestials().sendUnload(args[2], "star");
									}
								}.runTaskLater(main, 100L);
							}
							new File(main.getDataFolder() + "/schemes", args[2] + ".yml").delete();
							sender.sendMessage(Colors
									.clr(main.getLocale().getString("scheme-deleted").replaceAll("%name%", args[2])));
						} else {
							sender.sendMessage(Colors.clr(main.getLocale().getString("scheme-not-exist")));
						}
					} else {
						sender.sendMessage(Colors.clr(main.getLocale().getString("args")));
					}
				} else {
					sender.sendMessage(Colors.clr(main.getLocale().getString("args")));
				}
			} else {
				sender.sendMessage(Colors.clr(main.getLocale().getString("no-perm")));
			}
		} else if (args.length != 2) {
			sender.sendMessage(Colors.clr(main.getLocale().getString("args")));
		} else {
			if (args[0].equalsIgnoreCase("coordinates") || args[0].equalsIgnoreCase("coords")) {
				if (sender.hasPermission("pulsarsystem.admin")) {
					for (Planet p : main.getCelestials().getPlanets()) {
						if (p.getName().equalsIgnoreCase(args[1])) {
							sender.sendMessage(Colors.clr(main.getLocale().getString("coords-format")
									.replaceAll("%y%", "128.0").replaceAll("%name%", args[1])
									.replaceAll("%z%", String.valueOf(p.getCurrentRotation()[1]))
									.replaceAll("%x%", String.valueOf(p.getCurrentRotation()[0]))));
							return true;
						}
					}
					for (Moon m : main.getCelestials().getMoons()) {
						if (m.getName().equalsIgnoreCase(args[1])) {
							sender.sendMessage(Colors.clr(main.getLocale().getString("coords-format")
									.replaceAll("%y%", "128.0").replaceAll("%name%", args[1])
									.replaceAll("%z%", String.valueOf(m.getCurrentRotation()[1]))
									.replaceAll("%x%", String.valueOf(m.getCurrentRotation()[0]))));
							return true;
						}
					}
					if (main.getCelestials().getStar() != null) {
						if (main.getCelestials().getStar().getName().equalsIgnoreCase(args[1])) {
							sender.sendMessage(Colors.clr(main.getLocale().getString("coords-format")
									.replaceAll("%y%", "128.0").replaceAll("%name%", args[1]).replaceAll("%z%", "0.0")
									.replaceAll("%x%", "0.0")));
							return true;
						}
					}
					sender.sendMessage(Colors.clr(main.getLocale().getString("celestial-not-exist")));
				}
			} else {
				sender.sendMessage(Colors.clr(main.getLocale().getString("args")));
			}
		}
		return true;
	}

	private void createScheme(String name, List<String> scheme) {
		try {
			File f = new File(main.getDataFolder() + "/schemes", name + ".yml");
			f.createNewFile();
			FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
			conf.set("locations", scheme);
			conf.save(f);
		} catch (IOException e) {}
	}

	private void sendSchemes(CommandSender sender) {
		sender.sendMessage(Colors.clr(main.getLocale().getString("schemes-title")));
		for (File f : new File(main.getDataFolder(), "schemes").listFiles()) {
			sender.sendMessage(Colors.clr(main.getLocale().getString("schemes-format").replaceAll("%name%",
					f.getName().replaceAll(".yml", ""))));
		}
	}

	private void sendCelestials(CommandSender sender) {
		sender.sendMessage(Colors.clr(main.getLocale().getString("celestials-title")));
		if (main.getCelestials().getStar() != null) {
			sender.sendMessage(Colors.clr(main.getLocale().getString("star-format").replaceAll("%name%",
					main.getCelestials().getStar().getName())));
		}
		for (Planet p : main.getCelestials().getPlanets()) {
			sender.sendMessage(Colors.clr(
					main.getLocale().getString("planet-format").replaceAll("%online%", String.valueOf(p.isOnline()))
							.replaceAll("%online%", String.valueOf(p.isOnline()))
							.replaceAll("%number%", String.valueOf(p.getNumber()))
							.replaceAll("%name%", p.getName())));
		}
		for (Moon m : main.getCelestials().getMoons()) {
			sender.sendMessage(Colors
					.clr(main.getLocale().getString("moon-format").replaceAll("%online%", String.valueOf(m.isOnline()))
							.replaceAll("%online%", String.valueOf(m.isOnline()))
							.replaceAll("%number%", String.valueOf(m.getNumber())).replaceAll("%owner%", m.getOwner())
							.replaceAll("%name%", m.getName())));
		}
	}

	private void sendHelp(CommandSender sender) {
		if (sender.hasPermission("pulsarsystem.admin")) {
			sender.sendMessage(Colors.clr("&dPulsarSystem &ehelp page:"));
			sender.sendMessage(Colors.clr("&e/ps help &d- opens this page"));
			sender.sendMessage(Colors.clr("&e/ps info &d- plugin info"));
			sender.sendMessage(Colors.clr("&e/ps celestials &d- planets list"));
			sender.sendMessage(Colors.clr("&e/ps coords/coorinates <name> &d- coordinates of celestial"));
			sender.sendMessage(Colors.clr("&e/ps schemes &d- schemes list"));
			sender.sendMessage(Colors.clr("&e/ps celestial enable <name> &d- enable planet"));
			sender.sendMessage(Colors.clr("&e/ps celestial disable <name> &d- disable planet"));
			sender.sendMessage(Colors.clr("&e/ps scheme create <name> &d- create scheme"));
			sender.sendMessage(Colors.clr("&e/ps scheme delete <name> &d- delete scheme"));
		} else {
			sender.sendMessage(Colors.clr(main.getLocale().getString("no-perm")));
		}
	}

	private void sendInfo(CommandSender sender) {
		if (sender.hasPermission("pulsarsystem.admin")) {
			sender.sendMessage(Colors.clr("&dPulsarSystem"));
			sender.sendMessage(Colors.clr("&eVersion: &b" + main.getDescription().getVersion()));
			sender.sendMessage(Colors.clr("&eAuthor: &b" + main.getDescription().getAuthors().get(0)));
			sender.sendMessage(Colors.clr("&eType &d/ps help &e for help"));
			sender.sendMessage(Colors.clr("&eAliases: [&dps&e, &dpsystem&e]"));
		} else {
			sender.sendMessage(Colors.clr(main.getLocale().getString("no-perm")));
		}
	}

}
