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
public final class PublishVenueUseCase_Factory implements Factory<PublishVenueUseCase> {
  private final Provider<VenueRepository> venueRepositoryProvider;

  public PublishVenueUseCase_Factory(Provider<VenueRepository> venueRepositoryProvider) {
    this.venueRepositoryProvider = venueRepositoryProvider;
  }

  @Override
  public PublishVenueUseCase get() {
    return newInstance(venueRepositoryProvider.get());
  }

  public static PublishVenueUseCase_Factory create(
      Provider<VenueRepository> venueRepositoryProvider) {
    return new PublishVenueUseCase_Factory(venueRepositoryProvider);
  }

  public static PublishVenueUseCase newInstance(VenueRepository venueRepository) {
    return new PublishVenueUseCase(venueRepository);
  }
}
