package com.sensecode.navigo.home;

import android.content.Context;
import com.sensecode.navigo.audio.SpeechInputManager;
import com.sensecode.navigo.audio.TtsManager;
import com.sensecode.navigo.data.local.dao.EdgeDao;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import com.sensecode.navigo.data.repository.VenueRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<VenueRepository> venueRepositoryProvider;

  private final Provider<LocationNodeDao> nodeDaoProvider;

  private final Provider<EdgeDao> edgeDaoProvider;

  private final Provider<SpeechInputManager> speechInputManagerProvider;

  private final Provider<TtsManager> ttsManagerProvider;

  private final Provider<Context> contextProvider;

  public HomeViewModel_Factory(Provider<VenueRepository> venueRepositoryProvider,
      Provider<LocationNodeDao> nodeDaoProvider, Provider<EdgeDao> edgeDaoProvider,
      Provider<SpeechInputManager> speechInputManagerProvider,
      Provider<TtsManager> ttsManagerProvider, Provider<Context> contextProvider) {
    this.venueRepositoryProvider = venueRepositoryProvider;
    this.nodeDaoProvider = nodeDaoProvider;
    this.edgeDaoProvider = edgeDaoProvider;
    this.speechInputManagerProvider = speechInputManagerProvider;
    this.ttsManagerProvider = ttsManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(venueRepositoryProvider.get(), nodeDaoProvider.get(), edgeDaoProvider.get(), speechInputManagerProvider.get(), ttsManagerProvider.get(), contextProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<VenueRepository> venueRepositoryProvider,
      Provider<LocationNodeDao> nodeDaoProvider, Provider<EdgeDao> edgeDaoProvider,
      Provider<SpeechInputManager> speechInputManagerProvider,
      Provider<TtsManager> ttsManagerProvider, Provider<Context> contextProvider) {
    return new HomeViewModel_Factory(venueRepositoryProvider, nodeDaoProvider, edgeDaoProvider, speechInputManagerProvider, ttsManagerProvider, contextProvider);
  }

  public static HomeViewModel newInstance(VenueRepository venueRepository, LocationNodeDao nodeDao,
      EdgeDao edgeDao, SpeechInputManager speechInputManager, TtsManager ttsManager,
      Context context) {
    return new HomeViewModel(venueRepository, nodeDao, edgeDao, speechInputManager, ttsManager, context);
  }
}
