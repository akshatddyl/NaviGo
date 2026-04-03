package com.sensecode.navigo.di;

import com.sensecode.navigo.data.remote.gemini.GeminiApiService;
import com.sensecode.navigo.data.remote.gemini.GeminiClient;
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
public final class NetworkModule_ProvideGeminiApiServiceFactory implements Factory<GeminiApiService> {
  private final Provider<GeminiClient> clientProvider;

  public NetworkModule_ProvideGeminiApiServiceFactory(Provider<GeminiClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public GeminiApiService get() {
    return provideGeminiApiService(clientProvider.get());
  }

  public static NetworkModule_ProvideGeminiApiServiceFactory create(
      Provider<GeminiClient> clientProvider) {
    return new NetworkModule_ProvideGeminiApiServiceFactory(clientProvider);
  }

  public static GeminiApiService provideGeminiApiService(GeminiClient client) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGeminiApiService(client));
  }
}
