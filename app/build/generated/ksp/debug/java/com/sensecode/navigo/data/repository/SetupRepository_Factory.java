package com.sensecode.navigo.data.repository;

import com.sensecode.navigo.data.local.dao.EdgeDao;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import com.sensecode.navigo.data.local.dao.VenueDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SetupRepository_Factory implements Factory<SetupRepository> {
  private final Provider<LocationNodeDao> nodeDaoProvider;

  private final Provider<EdgeDao> edgeDaoProvider;

  private final Provider<VenueDao> venueDaoProvider;

  public SetupRepository_Factory(Provider<LocationNodeDao> nodeDaoProvider,
      Provider<EdgeDao> edgeDaoProvider, Provider<VenueDao> venueDaoProvider) {
    this.nodeDaoProvider = nodeDaoProvider;
    this.edgeDaoProvider = edgeDaoProvider;
    this.venueDaoProvider = venueDaoProvider;
  }

  @Override
  public SetupRepository get() {
    return newInstance(nodeDaoProvider.get(), edgeDaoProvider.get(), venueDaoProvider.get());
  }

  public static SetupRepository_Factory create(Provider<LocationNodeDao> nodeDaoProvider,
      Provider<EdgeDao> edgeDaoProvider, Provider<VenueDao> venueDaoProvider) {
    return new SetupRepository_Factory(nodeDaoProvider, edgeDaoProvider, venueDaoProvider);
  }

  public static SetupRepository newInstance(LocationNodeDao nodeDao, EdgeDao edgeDao,
      VenueDao venueDao) {
    return new SetupRepository(nodeDao, edgeDao, venueDao);
  }
}
