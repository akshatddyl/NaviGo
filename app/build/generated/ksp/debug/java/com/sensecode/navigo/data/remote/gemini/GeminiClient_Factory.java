package com.sensecode.navigo.data.remote.gemini;

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
public final class GeminiClient_Factory implements Factory<GeminiClient> {
  @Override
  public GeminiClient get() {
    return newInstance();
  }

  public static GeminiClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GeminiClient newInstance() {
    return new GeminiClient();
  }

  private static final class InstanceHolder {
    private static final GeminiClient_Factory INSTANCE = new GeminiClient_Factory();
  }
}
