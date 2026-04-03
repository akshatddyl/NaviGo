package com.sensecode.navigo.domain.usecase

import com.sensecode.navigo.data.repository.GraphRAGRepository
import com.sensecode.navigo.data.repository.NavigationRepository
import com.sensecode.navigo.domain.model.Route
import javax.inject.Inject

class ResolveVoiceQueryUseCase @Inject constructor(
    private val graphRAGRepository: GraphRAGRepository,
    private val navigationRepository: NavigationRepository
) {
    suspend operator fun invoke(
        userQuery: String,
        venueId: String,
        currentFloor: Int,
        startNodeId: String,
        accessibleOnly: Boolean = false
    ): Result<Route> {
        val nodeResult = graphRAGRepository.resolveNaturalLanguageQuery(
            userQuery = userQuery,
            venueId = venueId,
            currentFloor = currentFloor
        )

        return nodeResult.fold(
            onSuccess = { destinationNode ->
                navigationRepository.getRoute(
                    startNodeId = startNodeId,
                    destinationNodeId = destinationNode.id,
                    venueId = venueId,
                    accessibleOnly = accessibleOnly
                )
            },
            onFailure = { Result.failure(it) }
        )
    }
}
