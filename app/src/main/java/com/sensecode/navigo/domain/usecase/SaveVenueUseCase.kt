package com.sensecode.navigo.domain.usecase

import com.sensecode.navigo.data.repository.SetupRepository
import com.sensecode.navigo.setup.SetupRecordingSession
import javax.inject.Inject

class SaveVenueUseCase @Inject constructor(
    private val setupRepository: SetupRepository
) {
    suspend operator fun invoke(
        session: SetupRecordingSession,
        venueName: String,
        venueAddress: String,
        orgName: String,
        floor: Int
    ): Result<String> {
        if (venueName.isBlank()) {
            return Result.failure(Exception("Venue name cannot be empty"))
        }
        if (session.getNodeCount() < 2) {
            return Result.failure(Exception("At least 2 nodes are required to create a venue"))
        }
        return setupRepository.saveRecordingSession(session, venueName, venueAddress, orgName, floor)
    }
}
