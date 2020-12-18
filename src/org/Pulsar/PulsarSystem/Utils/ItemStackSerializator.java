package org.Pulsar.PulsarSystem.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.Pulsar.PulsarSystem.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemStackSerializator {

	public String contentsToBase64(ItemStack[] it) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BukkitObjectOutputStream dataOutput;
		try {
			dataOutput = new BukkitObjectOutputStream(outputStream);
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (ItemStack i : it) {
				if (i != null && !i.getType().equals(Material.AIR)) {
					items.add(i);
				}
			}
			dataOutput.writeInt(items.size());
			for (ItemStack i : items) {
				dataOutput.writeObject(i);
			}
			dataOutput.close();
		} catch (IOException e) {}
		return Base64Coder.encodeLines(outputStream.toByteArray());
	}

	public ItemStack[] contentsfromBase64(String data) {
		Main.getInstance().send(data);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
		try {
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			int size = dataInput.readInt();
			ItemStack[] contents = new ItemStack[size];
			for (int i = 0; i < size; i++) {
				contents[i] = (ItemStack) dataInput.readObject();
			}
			dataInput.close();
			return contents;
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

}
