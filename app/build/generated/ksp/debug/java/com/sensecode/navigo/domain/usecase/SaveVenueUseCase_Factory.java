package com.sensecode.navigo.domain.usecase;

import com.sensecode.navigo.data.repository.SetupRepository;
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
public final class SaveVenueUseCase_Factory implements Factory<SaveVenueUseCase> {
  private final Provider<SetupRepository> setupRepositoryProvider;

  public SaveVenueUseCase_Factory(Provider<SetupRepository> setupRepositoryProvider) {
    this.setupRepositoryProvider = setupRepositoryProvider;
  }

  @Override
  public SaveVenueUseCase get() {
    return newInstance(setupRepositoryProvider.get());
  }

  public static SaveVenueUseCase_Factory create(Provider<SetupRepository> setupRepositoryProvider) {
    return new SaveVenueUseCase_Factory(setupRepositoryProvider);
  }

  public static SaveVenueUseCase newInstance(SetupRepository setupRepository) {
    return new SaveVenueUseCase(setupRepository);
  }
}
