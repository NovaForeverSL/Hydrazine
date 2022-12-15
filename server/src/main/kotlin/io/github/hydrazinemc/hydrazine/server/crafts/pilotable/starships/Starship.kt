package io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.server.HydrazineServer.Companion.plugin
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.control.ShipControlHotbar
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.armor.ArmorSubsystem
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.shields.ShieldSubsystem
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.weapons.WeaponSubsystem
import io.github.hydrazinemc.hydrazine.server.multiblocks.events.MultiblockUndetectEvent
import io.github.hydrazinemc.hydrazine.server.utils.Vector3
import io.github.hydrazinemc.hydrazine.server.utils.locations.BlockLocation
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import kotlin.math.roundToInt

/**
 * Base Starship class
 */
class Starship(origin: BlockLocation) : Pilotable(origin), Listener {

	/**
	 * The number of ticks since this ship moved.
	 * (the number of times [move] was called since moving)
	 */
	var ticksSinceMove = 0
		private set


	val subsystems: Set<Subsystem>
		get() = setOf(weapons, shields, armor)


	val weapons = WeaponSubsystem(this)
	val shields = ShieldSubsystem(this)
	val armor = ArmorSubsystem(this)


	override fun deactivateCraft(): Boolean {
		val p = pilot // it becomes null after this
		val pass = passengers.toMutableSet()
		subsystems.forEach { it.onShipUnpiloted() }
		return super.deactivateCraft().also {
			if (it) {

				// close the ship HUD. this is really dumb
				// todo: fix
				this.passengers = pass
				StarshipHUD.close(this)
				this.passengers.clear()
				// close the hotbar menu
				p?.let { p -> ShipControlHotbar.closeMenu(p) }

				// todo: maybe find a better way than spamming this for everything we register to
				// reflection?
				BlockExplodeEvent.getHandlerList().unregister(this)
				MultiblockUndetectEvent.handlerList.unregister(this)
			}
		}
	}

	override fun activateCraft(pilot: Player): Boolean {
		return super.activateCraft(pilot).also { result ->
			if (result) {
				plugin.server.pluginManager.registerEvents(this, plugin)
				ShipControlHotbar.openMenu(pilot)
				StarshipHUD.open(this)
				subsystems.forEach { it.onShipPiloted() }
			}
		}
	}


	@EventHandler
	fun onBlockExplode(event: BlockExplodeEvent) {
		// todo: fix bad range check
		if (event.block.location.distanceSquared(origin.asLocation) < 500 && contains(event.block.location)) {
			if (shields.shieldHealth > 0) {
				event.isCancelled = true
				shields.damage(event.block.location, event.yield.roundToInt())
			} else {
				detectedBlocks.remove(BlockLocation(event.block.location))
			}
		}
	}

	@EventHandler
	fun onMultiblockUndetect(event: MultiblockUndetectEvent) {
		if (multiblocks.contains(event.multiblock)) {
			multiblocks.remove(event.multiblock)
			subsystems.forEach {
				it.onMultiblockUndetected(event.multiblock)
			}
		}
	}
}
