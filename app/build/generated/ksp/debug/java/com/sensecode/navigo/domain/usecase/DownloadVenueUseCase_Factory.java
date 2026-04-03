package com.sensecode.navigo.domain.usecase;

import com.sensecode.navigo.data.repository.VenueRepository;
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
public final class DownloadVenueUseCase_Factory implements Factory<DownloadVenueUseCase> {
  private final Provider<VenueRepository> venueRepositoryProvider;

  public DownloadVenueUseCase_Factory(Provider<VenueRepository> venueRepositoryProvider) {
    this.venueRepositoryProvider = venueRepositoryProvider;
  }

  @Override
  public DownloadVenueUseCase get() {
    return newInstance(venueRepositoryProvider.get());
  }

  public static DownloadVenueUseCase_Factory create(
      Provider<VenueRepository> venueRepositoryProvider) {
    return new DownloadVenueUseCase_Factory(venueRepositoryProvider);
  }

  public static DownloadVenueUseCase newInstance(VenueRepository venueRepository) {
    return new DownloadVenueUseCase(venueRepository);
  }
}
