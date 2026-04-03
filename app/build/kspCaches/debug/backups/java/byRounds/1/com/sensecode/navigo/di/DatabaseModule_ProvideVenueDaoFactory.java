package com.sensecode.navigo.di;

import com.sensecode.navigo.data.local.NaviGoDatabase;
import com.sensecode.navigo.data.local.dao.VenueDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DatabaseModule_ProvideVenueDaoFactory implements Factory<VenueDao> {
  private final Provider<NaviGoDatabase> dbProvider;

  public DatabaseModule_ProvideVenueDaoFactory(Provider<NaviGoDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VenueDao get() {
    return provideVenueDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideVenueDaoFactory create(Provider<NaviGoDatabase> dbProvider) {
    return new DatabaseModule_ProvideVenueDaoFactory(dbProvider);
  }

  public static VenueDao provideVenueDao(NaviGoDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVenueDao(db));
  }
}
