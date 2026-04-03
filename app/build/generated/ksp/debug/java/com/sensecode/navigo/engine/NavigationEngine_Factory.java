package com.sensecode.navigo.engine;

import android.content.Context;
import com.sensecode.navigo.audio.TtsManager;
import com.sensecode.navigo.data.local.dao.RouteLogDao;
import com.sensecode.navigo.data.repository.NavigationRepository;
import com.sensecode.navigo.haptics.HapticManager;
import com.sensecode.navigo.sensors.CompassManager;
import com.sensecode.navigo.sensors.StepCounterManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NavigationEngine_Factory implements Factory<NavigationEngine> {
  private final Provider<StepCounterManager> stepCounterManagerProvider;

  private final Provider<CompassManager> compassManagerProvider;

  private final Provider<TtsManager> ttsManagerProvider;

  private final Provider<HapticManager> hapticManagerProvider;

  private final Provider<NavigationRepository> navigationRepositoryProvider;

  private final Provider<RouteLogDao> routeLogDaoProvider;

  private final Provider<Context> appContextProvider;

  public NavigationEngine_Factory(Provider<StepCounterManager> stepCounterManagerProvider,
      Provider<CompassManager> compassManagerProvider, Provider<TtsManager> ttsManagerProvider,
      Provider<HapticManager> hapticManagerProvider,
      Provider<NavigationRepository> navigationRepositoryProvider,
      Provider<RouteLogDao> routeLogDaoProvider, Provider<Context> appContextProvider) {
    this.stepCounterManagerProvider = stepCounterManagerProvider;
    this.compassManagerProvider = compassManagerProvider;
    this.ttsManagerProvider = ttsManagerProvider;
    this.hapticManagerProvider = hapticManagerProvider;
    this.navigationRepositoryProvider = navigationRepositoryProvider;
    this.routeLogDaoProvider = routeLogDaoProvider;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public NavigationEngine get() {
    return newInstance(stepCounterManagerProvider.get(), compassManagerProvider.get(), ttsManagerProvider.get(), hapticManagerProvider.get(), navigationRepositoryProvider.get(), routeLogDaoProvider.get(), appContextProvider.get());
  }

  public static NavigationEngine_Factory create(
      Provider<StepCounterManager> stepCounterManagerProvider,
      Provider<CompassManager> compassManagerProvider, Provider<TtsManager> ttsManagerProvider,
      Provider<HapticManager> hapticManagerProvider,
      Provider<NavigationRepository> navigationRepositoryProvider,
      Provider<RouteLogDao> routeLogDaoProvider, Provider<Context> appContextProvider) {
    return new NavigationEngine_Factory(stepCounterManagerProvider, compassManagerProvider, ttsManagerProvider, hapticManagerProvider, navigationRepositoryProvider, routeLogDaoProvider, appContextProvider);
  }

  public static NavigationEngine newInstance(StepCounterManager stepCounterManager,
      CompassManager compassManager, TtsManager ttsManager, HapticManager hapticManager,
      NavigationRepository navigationRepository, RouteLogDao routeLogDao, Context appContext) {
    return new NavigationEngine(stepCounterManager, compassManager, ttsManager, hapticManager, navigationRepository, routeLogDao, appContext);
  }
}
