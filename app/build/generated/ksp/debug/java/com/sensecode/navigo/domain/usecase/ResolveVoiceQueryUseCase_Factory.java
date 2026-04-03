package com.sensecode.navigo.domain.usecase;

import com.sensecode.navigo.data.repository.GraphRAGRepository;
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
public final class ResolveVoiceQueryUseCase_Factory implements Factory<ResolveVoiceQueryUseCase> {
  private final Provider<GraphRAGRepository> graphRAGRepositoryProvider;

  private final Provider<NavigationRepository> navigationRepositoryProvider;

  public ResolveVoiceQueryUseCase_Factory(Provider<GraphRAGRepository> graphRAGRepositoryProvider,
      Provider<NavigationRepository> navigationRepositoryProvider) {
    this.graphRAGRepositoryProvider = graphRAGRepositoryProvider;
    this.navigationRepositoryProvider = navigationRepositoryProvider;
  }

  @Override
  public ResolveVoiceQueryUseCase get() {
    return newInstance(graphRAGRepositoryProvider.get(), navigationRepositoryProvider.get());
  }

  public static ResolveVoiceQueryUseCase_Factory create(
      Provider<GraphRAGRepository> graphRAGRepositoryProvider,
      Provider<NavigationRepository> navigationRepositoryProvider) {
    return new ResolveVoiceQueryUseCase_Factory(graphRAGRepositoryProvider, navigationRepositoryProvider);
  }

  public static ResolveVoiceQueryUseCase newInstance(GraphRAGRepository graphRAGRepository,
      NavigationRepository navigationRepository) {
    return new ResolveVoiceQueryUseCase(graphRAGRepository, navigationRepository);
  }
}
