package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems

import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.Starship
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
}

// todo: clean up weak refrences for subsystems multiblocks and starships