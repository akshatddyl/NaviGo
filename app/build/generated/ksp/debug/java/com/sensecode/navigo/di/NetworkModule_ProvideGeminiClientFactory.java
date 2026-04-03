package com.sensecode.navigo.di;

import com.sensecode.navigo.data.remote.gemini.GeminiClient;
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
public final class NetworkModule_ProvideGeminiClientFactory implements Factory<GeminiClient> {
  @Override
  public GeminiClient get() {
    return provideGeminiClient();
  }

  public static NetworkModule_ProvideGeminiClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GeminiClient provideGeminiClient() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGeminiClient());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideGeminiClientFactory INSTANCE = new NetworkModule_ProvideGeminiClientFactory();
  }
}
