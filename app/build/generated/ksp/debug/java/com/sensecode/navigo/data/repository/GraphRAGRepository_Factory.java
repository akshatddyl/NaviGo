package com.sensecode.navigo.data.repository;

import com.sensecode.navigo.data.remote.gemini.GeminiClient;
import com.sensecode.navigo.data.remote.neo4j.Neo4jClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class GraphRAGRepository_Factory implements Factory<GraphRAGRepository> {
  private final Provider<GeminiClient> geminiClientProvider;

  private final Provider<Neo4jClient> neo4jClientProvider;

  private final Provider<NavigationRepository> navigationRepositoryProvider;

  public GraphRAGRepository_Factory(Provider<GeminiClient> geminiClientProvider,
      Provider<Neo4jClient> neo4jClientProvider,
      Provider<NavigationRepository> navigationRepositoryProvider) {
    this.geminiClientProvider = geminiClientProvider;
    this.neo4jClientProvider = neo4jClientProvider;
    this.navigationRepositoryProvider = navigationRepositoryProvider;
  }

  @Override
  public GraphRAGRepository get() {
    return newInstance(geminiClientProvider.get(), neo4jClientProvider.get(), navigationRepositoryProvider.get());
  }

  public static GraphRAGRepository_Factory create(Provider<GeminiClient> geminiClientProvider,
      Provider<Neo4jClient> neo4jClientProvider,
      Provider<NavigationRepository> navigationRepositoryProvider) {
    return new GraphRAGRepository_Factory(geminiClientProvider, neo4jClientProvider, navigationRepositoryProvider);
  }

  public static GraphRAGRepository newInstance(GeminiClient geminiClient, Neo4jClient neo4jClient,
      NavigationRepository navigationRepository) {
    return new GraphRAGRepository(geminiClient, neo4jClient, navigationRepository);
  }
}
