package com.sensecode.navigo.di;

import com.sensecode.navigo.data.local.NaviGoDatabase;
import com.sensecode.navigo.data.local.dao.RouteLogDao;
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
public final class DatabaseModule_ProvideRouteLogDaoFactory implements Factory<RouteLogDao> {
  private final Provider<NaviGoDatabase> dbProvider;

  public DatabaseModule_ProvideRouteLogDaoFactory(Provider<NaviGoDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RouteLogDao get() {
    return provideRouteLogDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRouteLogDaoFactory create(
      Provider<NaviGoDatabase> dbProvider) {
    return new DatabaseModule_ProvideRouteLogDaoFactory(dbProvider);
  }

  public static RouteLogDao provideRouteLogDao(NaviGoDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRouteLogDao(db));
  }
}
