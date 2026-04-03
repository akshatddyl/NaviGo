package com.sensecode.navigo;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sensecode.navigo.audio.SpeechInputManager;
import com.sensecode.navigo.audio.TtsManager;
import com.sensecode.navigo.auth.AuthViewModel;
import com.sensecode.navigo.auth.AuthViewModel_HiltModules;
import com.sensecode.navigo.data.local.NaviGoDatabase;
import com.sensecode.navigo.data.local.dao.EdgeDao;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import com.sensecode.navigo.data.local.dao.RouteLogDao;
import com.sensecode.navigo.data.local.dao.VenueDao;
import com.sensecode.navigo.data.remote.firebase.FirebaseAuthService;
import com.sensecode.navigo.data.remote.firebase.FirestoreVenueService;
import com.sensecode.navigo.data.remote.gemini.GeminiClient;
import com.sensecode.navigo.data.remote.neo4j.Neo4jClient;
import com.sensecode.navigo.data.repository.GraphRAGRepository;
import com.sensecode.navigo.data.repository.NavigationRepository;
import com.sensecode.navigo.data.repository.SetupRepository;
import com.sensecode.navigo.data.repository.VenueRepository;
import com.sensecode.navigo.di.DatabaseModule_ProvideDatabaseFactory;
import com.sensecode.navigo.di.DatabaseModule_ProvideEdgeDaoFactory;
import com.sensecode.navigo.di.DatabaseModule_ProvideNodeDaoFactory;
import com.sensecode.navigo.di.DatabaseModule_ProvideRouteLogDaoFactory;
import com.sensecode.navigo.di.DatabaseModule_ProvideVenueDaoFactory;
import com.sensecode.navigo.di.FirebaseModule_ProvideFirebaseAuthFactory;
import com.sensecode.navigo.di.FirebaseModule_ProvideFirebaseFirestoreFactory;
import com.sensecode.navigo.di.NetworkModule_ProvideGeminiClientFactory;
import com.sensecode.navigo.di.NetworkModule_ProvideNeo4jClientFactory;
import com.sensecode.navigo.domain.usecase.DownloadVenueUseCase;
import com.sensecode.navigo.engine.NavigationEngine;
import com.sensecode.navigo.haptics.HapticManager;
import com.sensecode.navigo.home.HomeViewModel;
import com.sensecode.navigo.home.HomeViewModel_HiltModules;
import com.sensecode.navigo.mapshare.MapShareViewModel;
import com.sensecode.navigo.mapshare.MapShareViewModel_HiltModules;
import com.sensecode.navigo.navigation_ui.NavigationViewModel;
import com.sensecode.navigo.navigation_ui.NavigationViewModel_HiltModules;
import com.sensecode.navigo.onboarding.OnboardingViewModel;
import com.sensecode.navigo.onboarding.OnboardingViewModel_HiltModules;
import com.sensecode.navigo.sensors.CompassManager;
import com.sensecode.navigo.sensors.StepCounterManager;
import com.sensecode.navigo.setup.SetupViewModel;
import com.sensecode.navigo.setup.SetupViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerNaviGoApplication_HiltComponents_SingletonC {
  private DaggerNaviGoApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public NaviGoApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements NaviGoApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements NaviGoApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements NaviGoApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements NaviGoApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements NaviGoApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements NaviGoApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements NaviGoApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public NaviGoApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends NaviGoApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends NaviGoApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends NaviGoApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends NaviGoApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_sensecode_navigo_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sensecode_navigo_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sensecode_navigo_mapshare_MapShareViewModel, MapShareViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sensecode_navigo_navigation_ui_NavigationViewModel, NavigationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sensecode_navigo_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sensecode_navigo_setup_SetupViewModel, SetupViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sensecode_navigo_mapshare_MapShareViewModel = "com.sensecode.navigo.mapshare.MapShareViewModel";

      static String com_sensecode_navigo_setup_SetupViewModel = "com.sensecode.navigo.setup.SetupViewModel";

      static String com_sensecode_navigo_home_HomeViewModel = "com.sensecode.navigo.home.HomeViewModel";

      static String com_sensecode_navigo_auth_AuthViewModel = "com.sensecode.navigo.auth.AuthViewModel";

      static String com_sensecode_navigo_onboarding_OnboardingViewModel = "com.sensecode.navigo.onboarding.OnboardingViewModel";

      static String com_sensecode_navigo_navigation_ui_NavigationViewModel = "com.sensecode.navigo.navigation_ui.NavigationViewModel";

      @KeepFieldType
      MapShareViewModel com_sensecode_navigo_mapshare_MapShareViewModel2;

      @KeepFieldType
      SetupViewModel com_sensecode_navigo_setup_SetupViewModel2;

      @KeepFieldType
      HomeViewModel com_sensecode_navigo_home_HomeViewModel2;

      @KeepFieldType
      AuthViewModel com_sensecode_navigo_auth_AuthViewModel2;

      @KeepFieldType
      OnboardingViewModel com_sensecode_navigo_onboarding_OnboardingViewModel2;

      @KeepFieldType
      NavigationViewModel com_sensecode_navigo_navigation_ui_NavigationViewModel2;
    }
  }

  private static final class ViewModelCImpl extends NaviGoApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<MapShareViewModel> mapShareViewModelProvider;

    private Provider<NavigationViewModel> navigationViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<SetupViewModel> setupViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private DownloadVenueUseCase downloadVenueUseCase() {
      return new DownloadVenueUseCase(singletonCImpl.venueRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.mapShareViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.navigationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.setupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_sensecode_navigo_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_sensecode_navigo_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_sensecode_navigo_mapshare_MapShareViewModel, ((Provider) mapShareViewModelProvider)).put(LazyClassKeyProvider.com_sensecode_navigo_navigation_ui_NavigationViewModel, ((Provider) navigationViewModelProvider)).put(LazyClassKeyProvider.com_sensecode_navigo_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_sensecode_navigo_setup_SetupViewModel, ((Provider) setupViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sensecode_navigo_home_HomeViewModel = "com.sensecode.navigo.home.HomeViewModel";

      static String com_sensecode_navigo_auth_AuthViewModel = "com.sensecode.navigo.auth.AuthViewModel";

      static String com_sensecode_navigo_onboarding_OnboardingViewModel = "com.sensecode.navigo.onboarding.OnboardingViewModel";

      static String com_sensecode_navigo_mapshare_MapShareViewModel = "com.sensecode.navigo.mapshare.MapShareViewModel";

      static String com_sensecode_navigo_navigation_ui_NavigationViewModel = "com.sensecode.navigo.navigation_ui.NavigationViewModel";

      static String com_sensecode_navigo_setup_SetupViewModel = "com.sensecode.navigo.setup.SetupViewModel";

      @KeepFieldType
      HomeViewModel com_sensecode_navigo_home_HomeViewModel2;

      @KeepFieldType
      AuthViewModel com_sensecode_navigo_auth_AuthViewModel2;

      @KeepFieldType
      OnboardingViewModel com_sensecode_navigo_onboarding_OnboardingViewModel2;

      @KeepFieldType
      MapShareViewModel com_sensecode_navigo_mapshare_MapShareViewModel2;

      @KeepFieldType
      NavigationViewModel com_sensecode_navigo_navigation_ui_NavigationViewModel2;

      @KeepFieldType
      SetupViewModel com_sensecode_navigo_setup_SetupViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sensecode.navigo.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.firebaseAuthServiceProvider.get());

          case 1: // com.sensecode.navigo.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.venueRepositoryProvider.get(), singletonCImpl.locationNodeDao(), singletonCImpl.edgeDao(), singletonCImpl.speechInputManagerProvider.get(), singletonCImpl.ttsManagerProvider.get(), singletonCImpl.firebaseAuthServiceProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.sensecode.navigo.mapshare.MapShareViewModel 
          return (T) new MapShareViewModel(singletonCImpl.venueRepositoryProvider.get(), viewModelCImpl.downloadVenueUseCase(), singletonCImpl.navigationRepositoryProvider.get(), singletonCImpl.firestoreVenueServiceProvider.get());

          case 3: // com.sensecode.navigo.navigation_ui.NavigationViewModel 
          return (T) new NavigationViewModel(singletonCImpl.navigationEngineProvider.get(), singletonCImpl.navigationRepositoryProvider.get(), singletonCImpl.graphRAGRepositoryProvider.get(), singletonCImpl.ttsManagerProvider.get(), singletonCImpl.speechInputManagerProvider.get(), viewModelCImpl.savedStateHandle, ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.sensecode.navigo.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.sensecode.navigo.setup.SetupViewModel 
          return (T) new SetupViewModel(singletonCImpl.setupRepositoryProvider.get(), singletonCImpl.venueRepositoryProvider.get(), singletonCImpl.stepCounterManagerProvider.get(), singletonCImpl.compassManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends NaviGoApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends NaviGoApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends NaviGoApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<FirebaseAuth> provideFirebaseAuthProvider;

    private Provider<FirebaseAuthService> firebaseAuthServiceProvider;

    private Provider<NaviGoDatabase> provideDatabaseProvider;

    private Provider<FirebaseFirestore> provideFirebaseFirestoreProvider;

    private Provider<FirestoreVenueService> firestoreVenueServiceProvider;

    private Provider<VenueRepository> venueRepositoryProvider;

    private Provider<SpeechInputManager> speechInputManagerProvider;

    private Provider<TtsManager> ttsManagerProvider;

    private Provider<NavigationRepository> navigationRepositoryProvider;

    private Provider<StepCounterManager> stepCounterManagerProvider;

    private Provider<CompassManager> compassManagerProvider;

    private Provider<HapticManager> hapticManagerProvider;

    private Provider<NavigationEngine> navigationEngineProvider;

    private Provider<GeminiClient> provideGeminiClientProvider;

    private Provider<Neo4jClient> provideNeo4jClientProvider;

    private Provider<GraphRAGRepository> graphRAGRepositoryProvider;

    private Provider<SetupRepository> setupRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private LocationNodeDao locationNodeDao() {
      return DatabaseModule_ProvideNodeDaoFactory.provideNodeDao(provideDatabaseProvider.get());
    }

    private EdgeDao edgeDao() {
      return DatabaseModule_ProvideEdgeDaoFactory.provideEdgeDao(provideDatabaseProvider.get());
    }

    private VenueDao venueDao() {
      return DatabaseModule_ProvideVenueDaoFactory.provideVenueDao(provideDatabaseProvider.get());
    }

    private RouteLogDao routeLogDao() {
      return DatabaseModule_ProvideRouteLogDaoFactory.provideRouteLogDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 1));
      this.firebaseAuthServiceProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuthService>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<NaviGoDatabase>(singletonCImpl, 3));
      this.provideFirebaseFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 5));
      this.firestoreVenueServiceProvider = DoubleCheck.provider(new SwitchingProvider<FirestoreVenueService>(singletonCImpl, 4));
      this.venueRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<VenueRepository>(singletonCImpl, 2));
      this.speechInputManagerProvider = DoubleCheck.provider(new SwitchingProvider<SpeechInputManager>(singletonCImpl, 6));
      this.ttsManagerProvider = DoubleCheck.provider(new SwitchingProvider<TtsManager>(singletonCImpl, 7));
      this.navigationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<NavigationRepository>(singletonCImpl, 8));
      this.stepCounterManagerProvider = DoubleCheck.provider(new SwitchingProvider<StepCounterManager>(singletonCImpl, 10));
      this.compassManagerProvider = DoubleCheck.provider(new SwitchingProvider<CompassManager>(singletonCImpl, 11));
      this.hapticManagerProvider = DoubleCheck.provider(new SwitchingProvider<HapticManager>(singletonCImpl, 12));
      this.navigationEngineProvider = DoubleCheck.provider(new SwitchingProvider<NavigationEngine>(singletonCImpl, 9));
      this.provideGeminiClientProvider = DoubleCheck.provider(new SwitchingProvider<GeminiClient>(singletonCImpl, 14));
      this.provideNeo4jClientProvider = DoubleCheck.provider(new SwitchingProvider<Neo4jClient>(singletonCImpl, 15));
      this.graphRAGRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<GraphRAGRepository>(singletonCImpl, 13));
      this.setupRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SetupRepository>(singletonCImpl, 16));
    }

    @Override
    public void injectNaviGoApplication(NaviGoApplication naviGoApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sensecode.navigo.data.remote.firebase.FirebaseAuthService 
          return (T) new FirebaseAuthService(singletonCImpl.provideFirebaseAuthProvider.get());

          case 1: // com.google.firebase.auth.FirebaseAuth 
          return (T) FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 2: // com.sensecode.navigo.data.repository.VenueRepository 
          return (T) new VenueRepository(singletonCImpl.locationNodeDao(), singletonCImpl.edgeDao(), singletonCImpl.venueDao(), singletonCImpl.firestoreVenueServiceProvider.get());

          case 3: // com.sensecode.navigo.data.local.NaviGoDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.sensecode.navigo.data.remote.firebase.FirestoreVenueService 
          return (T) new FirestoreVenueService(singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 5: // com.google.firebase.firestore.FirebaseFirestore 
          return (T) FirebaseModule_ProvideFirebaseFirestoreFactory.provideFirebaseFirestore();

          case 6: // com.sensecode.navigo.audio.SpeechInputManager 
          return (T) new SpeechInputManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.sensecode.navigo.audio.TtsManager 
          return (T) new TtsManager();

          case 8: // com.sensecode.navigo.data.repository.NavigationRepository 
          return (T) new NavigationRepository(singletonCImpl.locationNodeDao(), singletonCImpl.edgeDao());

          case 9: // com.sensecode.navigo.engine.NavigationEngine 
          return (T) new NavigationEngine(singletonCImpl.stepCounterManagerProvider.get(), singletonCImpl.compassManagerProvider.get(), singletonCImpl.ttsManagerProvider.get(), singletonCImpl.hapticManagerProvider.get(), singletonCImpl.navigationRepositoryProvider.get(), singletonCImpl.routeLogDao(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.sensecode.navigo.sensors.StepCounterManager 
          return (T) new StepCounterManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 11: // com.sensecode.navigo.sensors.CompassManager 
          return (T) new CompassManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // com.sensecode.navigo.haptics.HapticManager 
          return (T) new HapticManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 13: // com.sensecode.navigo.data.repository.GraphRAGRepository 
          return (T) new GraphRAGRepository(singletonCImpl.provideGeminiClientProvider.get(), singletonCImpl.provideNeo4jClientProvider.get(), singletonCImpl.navigationRepositoryProvider.get());

          case 14: // com.sensecode.navigo.data.remote.gemini.GeminiClient 
          return (T) NetworkModule_ProvideGeminiClientFactory.provideGeminiClient();

          case 15: // com.sensecode.navigo.data.remote.neo4j.Neo4jClient 
          return (T) NetworkModule_ProvideNeo4jClientFactory.provideNeo4jClient();

          case 16: // com.sensecode.navigo.data.repository.SetupRepository 
          return (T) new SetupRepository(singletonCImpl.locationNodeDao(), singletonCImpl.edgeDao(), singletonCImpl.venueDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
