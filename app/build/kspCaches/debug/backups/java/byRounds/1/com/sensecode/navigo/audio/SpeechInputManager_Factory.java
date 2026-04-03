package com.sensecode.navigo.audio;

import android.content.Context;
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
public final class SpeechInputManager_Factory implements Factory<SpeechInputManager> {
  private final Provider<Context> contextProvider;

  public SpeechInputManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SpeechInputManager get() {
    return newInstance(contextProvider.get());
  }

  public static SpeechInputManager_Factory create(Provider<Context> contextProvider) {
    return new SpeechInputManager_Factory(contextProvider);
  }

  public static SpeechInputManager newInstance(Context context) {
    return new SpeechInputManager(context);
  }
}
