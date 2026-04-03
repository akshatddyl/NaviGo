package com.sensecode.navigo.di;

import com.sensecode.navigo.data.remote.neo4j.Neo4jClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class NetworkModule_ProvideNeo4jClientFactory implements Factory<Neo4jClient> {
  @Override
  public Neo4jClient get() {
    return provideNeo4jClient();
  }

  public static NetworkModule_ProvideNeo4jClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Neo4jClient provideNeo4jClient() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideNeo4jClient());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideNeo4jClientFactory INSTANCE = new NetworkModule_ProvideNeo4jClientFactory();
  }
}
