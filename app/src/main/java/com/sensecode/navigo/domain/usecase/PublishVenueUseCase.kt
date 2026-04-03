package com.sensecode.navigo.domain.usecase

import com.sensecode.navigo.data.repository.VenueRepository
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Venue
import javax.inject.Inject

class PublishVenueUseCase @Inject constructor(
    private val venueRepository: VenueRepository
) {
    suspend operator fun invoke(
        venue: Venue,
        nodes: List<LocationNode>,
        edges: List<Edge>
    ): Result<Unit> {
        if (venue.name.isBlank()) {
            return Result.failure(Exception("Venue name is required"))
        }
        return venueRepository.uploadVenueToMapShare(venue, nodes, edges)
    }
}
