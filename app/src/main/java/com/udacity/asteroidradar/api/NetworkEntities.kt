package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.udacity.asteroidradar.databse.RoomAsteroid
import com.udacity.asteroidradar.domain.Asteroid

data class NetworkEntitiesContainer(
    @Json(name = "element_count") val elementCount: Double,
    @Json(name = "near_earth_objects") val entities: List<NetworkAsteroid>
)

data class NetworkAsteroid(
    val id: Long,
    @Json(name = "name") val codename: String,
    @Json(name = "close_approach_data") val closeApproachDate: String,
    @Json(name = "absolute_magnitude_h") val absoluteMagnitude: Double,
    @Json(name = "estimated_diameter") val estimatedDiameter: Double,
    // relativeVelocity & distanceFromEarth
    @Json(name = "close_approach_data") val closeApproachData: CloseApproachData,
    @Json(name = "is_potentially_hazardous_asteroid") val isPotentiallyHazardous: Boolean
)

// For relativeVelocity & distanceFromEarth
data class CloseApproachData(
    @Json(name = "relative_velocity") val relativeVelocity: RelativeVelocity,
    @Json(name = "miss_distance") val missDistance: MissDistance
)

// For relativeVelocity
data class RelativeVelocity(
    @Json(name = "kilometers_per_second") val kilometersPerSecond: Double
)

// For distanceFromEarth
data class MissDistance(
    @Json(name = "kilometers") val kilometers: Double
)

/**
 * Convert network results to domain object
 */
fun NetworkEntitiesContainer.asDomainModel(): List<Asteroid> {
    return entities.map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.closeApproachData.relativeVelocity.kilometersPerSecond,
            distanceFromEarth = it.closeApproachData.missDistance.kilometers,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

/**
 * Convert network results to database object
 */
fun NetworkEntitiesContainer.asDatabaseModel(): Array<RoomAsteroid> {
    return entities.map {
        RoomAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.closeApproachData.relativeVelocity.kilometersPerSecond,
            distanceFromEarth = it.closeApproachData.missDistance.kilometers,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}