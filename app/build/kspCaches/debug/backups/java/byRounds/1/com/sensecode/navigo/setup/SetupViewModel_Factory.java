package com.sensecode.navigo.setup;

import com.sensecode.navigo.data.repository.SetupRepository;
import com.sensecode.navigo.data.repository.VenueRepository;
import com.sensecode.navigo.sensors.CompassManager;
import com.sensecode.navigo.sensors.StepCounterManager;
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
public final class SetupViewModel_Factory implements Factory<SetupViewModel> {
  private final Provider<SetupRepository> setupRepositoryProvider;

  private final Provider<VenueRepository> venueRepositoryProvider;

  private final Provider<StepCounterManager> stepCounterManagerProvider;

  private final Provider<CompassManager> compassManagerProvider;

  public SetupViewModel_Factory(Provider<SetupRepository> setupRepositoryProvider,
      Provider<VenueRepository> venueRepositoryProvider,
      Provider<StepCounterManager> stepCounterManagerProvider,
      Provider<CompassManager> compassManagerProvider) {
    this.setupRepositoryProvider = setupRepositoryProvider;
    this.venueRepositoryProvider = venueRepositoryProvider;
    this.stepCounterManagerProvider = stepCounterManagerProvider;
    this.compassManagerProvider = compassManagerProvider;
  }

  @Override
  public SetupViewModel get() {
    return newInstance(setupRepositoryProvider.get(), venueRepositoryProvider.get(), stepCounterManagerProvider.get(), compassManagerProvider.get());
  }

  public static SetupViewModel_Factory create(Provider<SetupRepository> setupRepositoryProvider,
      Provider<VenueRepository> venueRepositoryProvider,
      Provider<StepCounterManager> stepCounterManagerProvider,
      Provider<CompassManager> compassManagerProvider) {
    return new SetupViewModel_Factory(setupRepositoryProvider, venueRepositoryProvider, stepCounterManagerProvider, compassManagerProvider);
  }

  public static SetupViewModel newInstance(SetupRepository setupRepository,
      VenueRepository venueRepository, StepCounterManager stepCounterManager,
      CompassManager compassManager) {
    return new SetupViewModel(setupRepository, venueRepository, stepCounterManager, compassManager);
  }
}
