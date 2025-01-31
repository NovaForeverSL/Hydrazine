package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.rotation.rotateCoordinates
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.roundToInt

/**
 * A container for three Doubles, [x], [y], and [z]
 *
 * @see BlockLocation
 * @see Vector
 */
class Vector3(x: Double, y: Double, z: Double) : Vector(x, y, z) {
	companion object {
		/**
		 * Null vector
		 */
		val zero: Vector3
			get() = Vector3(0.0, 0.0, 0.0)
	}

	constructor(loc: BlockLocation) : this(loc.x.toDouble(), loc.y.toDouble(), loc.z.toDouble())
	constructor(loc: Location) : this(loc.x, loc.y, loc.z)
	constructor(vec: Vector) : this(vec.x, vec.y, vec.z)

	/**
	 * Add this to [other]
	 */
	operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

	/**
	 * Subtract this from [other]
	 */
	operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)

	/**
	 * Multiply this by [other]
	 */
	operator fun times(other: Float) = Vector3(x * other, y * other, z * other)

	/**
	 * Multiply this by [other]
	 */
	operator fun times(other: Double) = Vector3(x * other, y * other, z * other)

	/**
	 * Divide this by [other]
	 */
	operator fun div(other: Float) = Vector3(x / other, y / other, z / other)

	/**
	 * Divide this by [other]
	 */
	operator fun div(other: Double) = Vector3(x / other, y / other, z / other)

	/**
	 * This Vector3 as a loction.
	 * The world component of the Location will be null
	 * @see BlockLocation.asLocation
	 * @see asBlockLocation
	 * @see Location
	 */
	val asLocation: Location = Location(null, x, y, z)

	/**
	 * This Vector3 as a BlockLocation
	 * The world component of the BlockLocation will be null
	 *
	 * Note, some precision will be lost, as BlockLocations can only store integers.
	 *
	 * @see BlockLocation
	 * @see asLocation
	 * @see Location
	 */
	val asBlockLocation: BlockLocation = BlockLocation(x.roundToInt(), y.roundToInt(), z.roundToInt(), null)

	/**
	 * This Vector3 as a string with MiniMessage formatting
	 */
	val miniMessage: String
		get() = "(<b>$x</b>, <b>$y</b>, <b>$z</b>)"

	/**
	 * Get this position rotated around [origin] by [theta] radians
	 * @see rotateCoordinates
	 */
	fun rotateAround(origin: Vector3, theta: Double) =
		rotateCoordinates(this, origin, theta)

	/**
	 * Get this position rotated around [origin] by [rotation]
	 * @see rotateCoordinates
	 */
	fun rotateAround(origin: Vector3, rotation: RotationAmount) = rotateCoordinates(this, origin, rotation)

	/**
	 * Clamp this vector's components by [min] and [max]
	 */
	fun clamp(min: Vector3, max: Vector3) = Vector3(
		x.coerceIn(min.x, max.x),
		y.coerceIn(min.y, max.y),
		z.coerceIn(min.z, max.z)
	)

	/**
	 * This vector, inverted
	 */
	operator fun unaryMinus(): Vector3 = Vector3(-x, -y, -z)
}
