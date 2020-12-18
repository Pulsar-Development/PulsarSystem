package org.Pulsar.PulsarSystem.Utils;

import java.util.ArrayList;
import java.util.List;

import org.Pulsar.PulsarSystem.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Geometry {

	public static List<String> getScheme(Location f, Location s) {
		List<String> locs = new ArrayList<String>();
		double xmax = Math.max(f.getX(), s.getX());
		double ymax = Math.max(f.getY(), s.getY());
		double zmax = Math.max(f.getZ(), s.getZ());
		double xmin = Math.min(f.getX(), s.getX());
		double ymin = Math.min(f.getY(), s.getY());
		double zmin = Math.min(f.getZ(), s.getZ());
		double xcenter = (xmax + xmin) / 2;
		double ycenter = (ymax + ymin) / 2;
		double zcenter = (zmax + zmin) / 2;
		World w = f.getWorld();
		for (double a = xmin; a <= xmax; a++) {
			for (double b = ymin; b <= ymax; b++) {
				for (double c = zmin; c <= zmax; c++) {
					Location l = new Location(w, a, b, c);
					if (!l.getBlock().getType().equals(Material.AIR)
							&& !l.getBlock().getType().equals(Material.CAVE_AIR)
							&& !l.getBlock().getType().equals(Material.WATER)
							&& !l.getBlock().getType().equals(Material.LAVA)) {
						locs.add((a - xcenter) + ";" + (b - ycenter) + ";" + (c - zcenter) + ";"
								+ l.getBlock().getType().toString());
					}
				}
			}
		}
		return locs;
	}

	public static boolean sidesEqual(Location f, Location s) {
		double xmax = Math.max(f.getX(), s.getX());
		double ymax = Math.max(f.getY(), s.getY());
		double zmax = Math.max(f.getZ(), s.getZ());
		double xmin = Math.min(f.getX(), s.getX());
		double ymin = Math.min(f.getY(), s.getY());
		double zmin = Math.min(f.getZ(), s.getZ());
		return (xmax - xmin == ymax - ymin && xmax - xmin == zmax - zmin);
	}

	public static boolean isOdd(double x, double x2) {
		return ((Math.max(x, x2) - Math.min(x, x2) + 1) % 2 == 1);
	}

	public static double[] getCurrentRotation(byte number) {
		double[] currentRotation = new double[2];
		double dday = 0;
		switch (number) {
		case 1:
			dday = 10;
			break;
		case 2:
			dday = 12;
			break;
		case 3:
			dday = 15;
			break;
		case 4:
			dday = 16;
			break;
		case 5:
			dday = 18;
			break;
		case 6:
			dday = 20;
			break;
		case 7:
			dday = 24;
			break;
		case 8:
			dday = 30;
			break;
		case 9:
			dday = 36;
			break;
		}
		double currentDay = Main.getInstance().getDay(number, "planet", 360 / dday);
		currentRotation[0] = 2160 * number * Math.cos(dday * currentDay);
		currentRotation[1] = 2160 * number * Math.sin(dday * currentDay);
		return currentRotation;
	}

	public static double[] getCurrentMoonRotation(byte number, byte ownerNumber) {
		double[] currentOwnerRotation = getCurrentRotation(ownerNumber);
		double[] currentRotation = new double[2];
		double dday = 18;
		if (number == 1) {
			dday = 36;
		}
		double currentDay = Main.getInstance().getDay(number, "moon", 360 / dday);
		currentRotation[0] = 360 * number * Math.cos(dday * currentDay);
		currentRotation[1] = 360 * number * Math.sin(dday * currentDay);
		currentRotation[0] += currentOwnerRotation[0];
		currentRotation[1] += currentOwnerRotation[1];
		return currentRotation;
	}

}
