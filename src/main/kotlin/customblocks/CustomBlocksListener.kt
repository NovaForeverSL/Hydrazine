package io.github.petercrawley.minecraftstarshipplugin.customblocks

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent

class CustomBlocksListener: Listener {
	// For our purposes BlockPistonExtendEvent and BlockPistonRetractEvent can be handled the same way.
	private fun mushroomBlockMovedByPiston(blocks: List<Block>, direction: BlockFace) {
		val blocksToChange = mutableMapOf<Block, BlockData>()

		// Add each mushroom block to the list.
		blocks.forEach {
			if (it.type == Material.MUSHROOM_STEM) blocksToChange[it.getRelative(direction)] = it.blockData
		}

		if (blocksToChange.isNotEmpty()) {
			// Create a task to correct the blocks after the piston is done doing its thing.
			Bukkit.getScheduler().runTask(MinecraftStarshipPlugin.getPlugin(), Runnable {
				blocksToChange.forEach { it.key.setBlockData(it.value, false) }
			})
		}
	}

	@EventHandler fun mushroomBlockPushedByPiston(event: BlockPistonExtendEvent) {
		mushroomBlockMovedByPiston(event.blocks, event.direction)
	}

	@EventHandler fun mushroomBlockPulledByPiston(event: BlockPistonRetractEvent) {
		mushroomBlockMovedByPiston(event.blocks, event.direction)
	}

	// If a mushroom block is placed force its faces to all be true.
	// This allows us to keep allowing the use of the blocks in builds.
	@EventHandler fun mushroomBlockPlaced(event: BlockPlaceEvent) {
		val block = event.blockPlaced

		if (block.type != Material.MUSHROOM_STEM) return

		block.setBlockData(Bukkit.getServer().createBlockData(Material.MUSHROOM_STEM), false)
	}

	// Prevent the block faces from changing.
	// TODO: On the client the mushroom blocks flash with the incorrect faces very briefly, see if this can be avoided.
	@EventHandler fun mushroomBlockPhysicsEvent(event: BlockPhysicsEvent) {
		if (event.changedType == Material.MUSHROOM_STEM) {
			event.isCancelled = true

			Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
				val blocksToUpdate = mutableSetOf<Block>()

				val queuedBlocks = mutableSetOf<Block>()
				val checkedBlocks = mutableSetOf<Block>()

				blocksToUpdate.add(event.block)

				while (queuedBlocks.isNotEmpty()) {
					val block = queuedBlocks.first()
					queuedBlocks.remove(block)

					if (checkedBlocks.contains(block)) continue

					checkedBlocks.add(block)

					if (block.type != Material.MUSHROOM_STEM) continue

					blocksToUpdate.add(block)

					queuedBlocks.add(block.getRelative(1, 0, 0))
					queuedBlocks.add(block.getRelative(-1, 0, 0))
					queuedBlocks.add(block.getRelative(0, 1, 0))
					queuedBlocks.add(block.getRelative(0, -1, 0))
					queuedBlocks.add(block.getRelative(0, 0, 1))
					queuedBlocks.add(block.getRelative(0, 0, -1))
				}

				Bukkit.getScheduler().runTask(MinecraftStarshipPlugin.getPlugin(), Runnable {
					blocksToUpdate.forEach {
						it.state.update(true, false)
					}
				})
			})
		}
	}
}