package com.sensecode.navigo.data.remote.neo4j;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class Neo4jClient_Factory implements Factory<Neo4jClient> {
  @Override
  public Neo4jClient get() {
    return newInstance();
  }

  public static Neo4jClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Neo4jClient newInstance() {
    return new Neo4jClient();
  }

  private static final class InstanceHolder {
    private static final Neo4jClient_Factory INSTANCE = new Neo4jClient_Factory();
  }
}
