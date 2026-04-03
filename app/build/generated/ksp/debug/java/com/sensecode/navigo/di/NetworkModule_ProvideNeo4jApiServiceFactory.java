package com.sensecode.navigo.di;

import com.sensecode.navigo.data.remote.neo4j.Neo4jApiService;
import com.sensecode.navigo.data.remote.neo4j.Neo4jClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideNeo4jApiServiceFactory implements Factory<Neo4jApiService> {
  private final Provider<Neo4jClient> clientProvider;

  public NetworkModule_ProvideNeo4jApiServiceFactory(Provider<Neo4jClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public Neo4jApiService get() {
    return provideNeo4jApiService(clientProvider.get());
  }

  public static NetworkModule_ProvideNeo4jApiServiceFactory create(
      Provider<Neo4jClient> clientProvider) {
    return new NetworkModule_ProvideNeo4jApiServiceFactory(clientProvider);
  }

  public static Neo4jApiService provideNeo4jApiService(Neo4jClient client) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideNeo4jApiService(client));
  }
}
