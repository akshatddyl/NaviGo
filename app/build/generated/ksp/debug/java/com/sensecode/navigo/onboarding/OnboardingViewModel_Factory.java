package com.sensecode.navigo.onboarding;

import android.content.Context;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<Context> contextProvider;

  public OnboardingViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<Context> contextProvider) {
    return new OnboardingViewModel_Factory(contextProvider);
  }

  public static OnboardingViewModel newInstance(Context context) {
    return new OnboardingViewModel(context);
  }
}
