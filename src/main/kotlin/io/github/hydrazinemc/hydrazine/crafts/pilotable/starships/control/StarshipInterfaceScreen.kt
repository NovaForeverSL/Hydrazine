package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control

import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.utils.extensions.isPilotingCraft
import io.github.hydrazinemc.hydrazine.utils.gui.Screen
import io.github.hydrazinemc.hydrazine.utils.gui.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

/**
 * The GUI opened when a player clicks the starship interface block
 */
class StarshipInterfaceScreen(player: Player, private val craft: Pilotable) : Screen() {
	init {
		createScreen(player, InventoryType.HOPPER, "Starship Interface")

		inventory.setItem(0, namedItem(Material.MINECART, "Detect Starship", null))
		if (player.isPilotingCraft) inventory.setItem(1, namedItem(Material.COMPASS, "Unpilot Starship", null))
		else inventory.setItem(1, namedItem(Material.COMPASS, "Pilot Starship", null))
	}

	override fun onScreenButtonClicked(slot: Int) {
		if (craft.owner == null) craft.owner = player
		when (slot) {
			0 -> craft.detect()
			1 -> {
				if (player.isPilotingCraft) craft.deactivateCraft()
				else craft.activateCraft(player)
				closeScreen()
			}
		}
	}
}
