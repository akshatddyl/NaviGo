package com.sensecode.navigo.mapshare;

import com.sensecode.navigo.data.remote.firebase.FirestoreVenueService;
import com.sensecode.navigo.data.repository.NavigationRepository;
import com.sensecode.navigo.data.repository.VenueRepository;
import com.sensecode.navigo.domain.usecase.DownloadVenueUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class MapShareViewModel_Factory implements Factory<MapShareViewModel> {
  private final Provider<VenueRepository> venueRepositoryProvider;

  private final Provider<DownloadVenueUseCase> downloadVenueUseCaseProvider;

  private final Provider<NavigationRepository> navigationRepositoryProvider;

  private final Provider<FirestoreVenueService> firestoreVenueServiceProvider;

  public MapShareViewModel_Factory(Provider<VenueRepository> venueRepositoryProvider,
      Provider<DownloadVenueUseCase> downloadVenueUseCaseProvider,
      Provider<NavigationRepository> navigationRepositoryProvider,
      Provider<FirestoreVenueService> firestoreVenueServiceProvider) {
    this.venueRepositoryProvider = venueRepositoryProvider;
    this.downloadVenueUseCaseProvider = downloadVenueUseCaseProvider;
    this.navigationRepositoryProvider = navigationRepositoryProvider;
    this.firestoreVenueServiceProvider = firestoreVenueServiceProvider;
  }

  @Override
  public MapShareViewModel get() {
    return newInstance(venueRepositoryProvider.get(), downloadVenueUseCaseProvider.get(), navigationRepositoryProvider.get(), firestoreVenueServiceProvider.get());
  }

  public static MapShareViewModel_Factory create(Provider<VenueRepository> venueRepositoryProvider,
      Provider<DownloadVenueUseCase> downloadVenueUseCaseProvider,
      Provider<NavigationRepository> navigationRepositoryProvider,
      Provider<FirestoreVenueService> firestoreVenueServiceProvider) {
    return new MapShareViewModel_Factory(venueRepositoryProvider, downloadVenueUseCaseProvider, navigationRepositoryProvider, firestoreVenueServiceProvider);
  }

  public static MapShareViewModel newInstance(VenueRepository venueRepository,
      DownloadVenueUseCase downloadVenueUseCase, NavigationRepository navigationRepository,
      FirestoreVenueService firestoreVenueService) {
    return new MapShareViewModel(venueRepository, downloadVenueUseCase, navigationRepository, firestoreVenueService);
  }
}
