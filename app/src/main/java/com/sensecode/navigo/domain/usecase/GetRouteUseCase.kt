package com.sensecode.navigo.domain.usecase

import com.sensecode.navigo.data.repository.NavigationRepository
import com.sensecode.navigo.domain.model.Route
import javax.inject.Inject

class GetRouteUseCase @Inject constructor(
    private val navigationRepository: NavigationRepository
) {
    suspend operator fun invoke(
        startNodeId: String,
        destinationNodeId: String,
        venueId: String,
        accessibleOnly: Boolean = false
    ): Result<Route> {
        return navigationRepository.getRoute(startNodeId, destinationNodeId, venueId, accessibleOnly)
    }
}
