package io.github.hydrazinemc.hydrazine.utils.extensions

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.activeStarships
import io.github.hydrazinemc.hydrazine.starships.Starship
import org.bukkit.entity.Player

/**
 * The starship the player is riding
 * @see Player.isPilotingShip
 */
val Player.starship: Starship?
	get() {
		activeStarships.forEach{ship ->
			if (ship.passengers.contains(this)) return ship
		}
		return null
	}

/**
 * Whether the player is currently piloting a starship.
 *
 * If you couldn't tell by the name, you have an issue.
 * Go get help.
 *
 * @see Player.starship
 */
val Player.isPilotingShip: Boolean
	get() = this.starship?.pilot == this