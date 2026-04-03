package com.sensecode.navigo.navigation_ui;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.sensecode.navigo.audio.SpeechInputManager;
import com.sensecode.navigo.audio.TtsManager;
import com.sensecode.navigo.data.repository.GraphRAGRepository;
import com.sensecode.navigo.data.repository.NavigationRepository;
import com.sensecode.navigo.engine.NavigationEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NavigationViewModel_Factory implements Factory<NavigationViewModel> {
  private final Provider<NavigationEngine> navigationEngineProvider;

  private final Provider<NavigationRepository> navigationRepositoryProvider;

  private final Provider<GraphRAGRepository> graphRAGRepositoryProvider;

  private final Provider<TtsManager> ttsManagerProvider;

  private final Provider<SpeechInputManager> speechInputManagerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<Context> appContextProvider;

  public NavigationViewModel_Factory(Provider<NavigationEngine> navigationEngineProvider,
      Provider<NavigationRepository> navigationRepositoryProvider,
      Provider<GraphRAGRepository> graphRAGRepositoryProvider,
      Provider<TtsManager> ttsManagerProvider,
      Provider<SpeechInputManager> speechInputManagerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider, Provider<Context> appContextProvider) {
    this.navigationEngineProvider = navigationEngineProvider;
    this.navigationRepositoryProvider = navigationRepositoryProvider;
    this.graphRAGRepositoryProvider = graphRAGRepositoryProvider;
    this.ttsManagerProvider = ttsManagerProvider;
    this.speechInputManagerProvider = speechInputManagerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public NavigationViewModel get() {
    return newInstance(navigationEngineProvider.get(), navigationRepositoryProvider.get(), graphRAGRepositoryProvider.get(), ttsManagerProvider.get(), speechInputManagerProvider.get(), savedStateHandleProvider.get(), appContextProvider.get());
  }

  public static NavigationViewModel_Factory create(
      Provider<NavigationEngine> navigationEngineProvider,
      Provider<NavigationRepository> navigationRepositoryProvider,
      Provider<GraphRAGRepository> graphRAGRepositoryProvider,
      Provider<TtsManager> ttsManagerProvider,
      Provider<SpeechInputManager> speechInputManagerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider, Provider<Context> appContextProvider) {
    return new NavigationViewModel_Factory(navigationEngineProvider, navigationRepositoryProvider, graphRAGRepositoryProvider, ttsManagerProvider, speechInputManagerProvider, savedStateHandleProvider, appContextProvider);
  }

  public static NavigationViewModel newInstance(NavigationEngine navigationEngine,
      NavigationRepository navigationRepository, GraphRAGRepository graphRAGRepository,
      TtsManager ttsManager, SpeechInputManager speechInputManager,
      SavedStateHandle savedStateHandle, Context appContext) {
    return new NavigationViewModel(navigationEngine, navigationRepository, graphRAGRepository, ttsManager, speechInputManager, savedStateHandle, appContext);
  }
}
