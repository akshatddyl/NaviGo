package com.sensecode.navigo.domain.usecase

import com.sensecode.navigo.data.repository.VenueRepository
import javax.inject.Inject

class DownloadVenueUseCase @Inject constructor(
    private val venueRepository: VenueRepository
) {
    suspend operator fun invoke(venueId: String): Result<Unit> {
        return venueRepository.downloadVenueFromMapShare(venueId)
    }
}
