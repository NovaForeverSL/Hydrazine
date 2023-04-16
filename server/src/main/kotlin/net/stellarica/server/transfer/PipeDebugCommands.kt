package net.stellarica.server.transfer

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("pipedebug")
class PipeDebugCommands : BaseCommand() {
	@Subcommand("node")
	fun onNode(sender: Player) {
		PipeHandler.nodes[sender.world]?.get(sender.getTargetBlockExact(10)?.toBlockPos())?.also {
			sender.sendRichMessage("(${it.pos.x}, ${it.pos.y}, ${it.pos.z}) - F: ${it.content} - C: ${it.connections}")
		}
	}

	@Subcommand("addFuel")
	fun onAddFuel(sender: Player, fuel: Int) {
		PipeHandler.nodes[sender.world]!![sender.getTargetBlockExact(10)?.toBlockPos()]!!.content += fuel
	}

	@Subcommand("dump")
	fun onDump() {
		println(PipeHandler.nodes)
	}

	@Subcommand("sum")
	fun onSum(sender: Player) {
		var sum = 0
		var count = 0
		PipeHandler.nodes.values.forEach { it.values.forEach { sum += it.content; count++ } }
		sender.sendRichMessage("$sum across $count nodes")
	}
}