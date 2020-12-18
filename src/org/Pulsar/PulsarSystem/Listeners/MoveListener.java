package org.Pulsar.PulsarSystem.Listeners;

import java.util.ArrayList;
import java.util.List;
import org.Pulsar.PulsarSystem.Main;
import org.Pulsar.PulsarSystem.Utils.ItemStackSerializator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.events.CraftTranslateEvent;

public class MoveListener implements Listener {

	private Main main;
	private ItemStackSerializator ser = new ItemStackSerializator();
	private List<String> onlines = new ArrayList<String>();

	public MoveListener(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onMove(CraftTranslateEvent e) {
		Player p = e.getCraft().getNotificationPlayer();
		if (p.getLocation().getY() >= 255) {
			if (onlines.contains(p.getWorld().getName())) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("sendfrom");
				out.writeUTF(p.getLocation().getWorld().getName());
				out.writeUTF(ser.contentsToBase64(p.getInventory().getContents()));
				out.writeUTF(e.getCraft().getID());
				p.getInventory().clear();
				e.setCancelled(true);
				CraftManager.getInstance().removeCraft(e.getCraft());
				p.sendPluginMessage(main, "pulsar:system", out.toByteArray());
			}
		}
	}

	public List<String> getOnlines() {
		return onlines;
	}

}
