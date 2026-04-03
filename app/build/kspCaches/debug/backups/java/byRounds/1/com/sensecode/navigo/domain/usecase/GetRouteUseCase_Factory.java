package com.sensecode.navigo.domain.usecase;

import com.sensecode.navigo.data.repository.NavigationRepository;
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
public final class GetRouteUseCase_Factory implements Factory<GetRouteUseCase> {
  private final Provider<NavigationRepository> navigationRepositoryProvider;

  public GetRouteUseCase_Factory(Provider<NavigationRepository> navigationRepositoryProvider) {
    this.navigationRepositoryProvider = navigationRepositoryProvider;
  }

  @Override
  public GetRouteUseCase get() {
    return newInstance(navigationRepositoryProvider.get());
  }

  public static GetRouteUseCase_Factory create(
      Provider<NavigationRepository> navigationRepositoryProvider) {
    return new GetRouteUseCase_Factory(navigationRepositoryProvider);
  }

  public static GetRouteUseCase newInstance(NavigationRepository navigationRepository) {
    return new GetRouteUseCase(navigationRepository);
  }
}
