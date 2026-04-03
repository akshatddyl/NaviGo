package com.sensecode.navigo.data.repository;

import com.sensecode.navigo.data.local.dao.EdgeDao;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import com.sensecode.navigo.data.local.dao.VenueDao;
import com.sensecode.navigo.data.remote.firebase.FirestoreVenueService;
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
public final class VenueRepository_Factory implements Factory<VenueRepository> {
  private final Provider<LocationNodeDao> nodeDaoProvider;

  private final Provider<EdgeDao> edgeDaoProvider;

  private final Provider<VenueDao> venueDaoProvider;

  private final Provider<FirestoreVenueService> firestoreVenueServiceProvider;

  public VenueRepository_Factory(Provider<LocationNodeDao> nodeDaoProvider,
      Provider<EdgeDao> edgeDaoProvider, Provider<VenueDao> venueDaoProvider,
      Provider<FirestoreVenueService> firestoreVenueServiceProvider) {
    this.nodeDaoProvider = nodeDaoProvider;
    this.edgeDaoProvider = edgeDaoProvider;
    this.venueDaoProvider = venueDaoProvider;
    this.firestoreVenueServiceProvider = firestoreVenueServiceProvider;
  }

  @Override
  public VenueRepository get() {
    return newInstance(nodeDaoProvider.get(), edgeDaoProvider.get(), venueDaoProvider.get(), firestoreVenueServiceProvider.get());
  }

  public static VenueRepository_Factory create(Provider<LocationNodeDao> nodeDaoProvider,
      Provider<EdgeDao> edgeDaoProvider, Provider<VenueDao> venueDaoProvider,
      Provider<FirestoreVenueService> firestoreVenueServiceProvider) {
    return new VenueRepository_Factory(nodeDaoProvider, edgeDaoProvider, venueDaoProvider, firestoreVenueServiceProvider);
  }

  public static VenueRepository newInstance(LocationNodeDao nodeDao, EdgeDao edgeDao,
      VenueDao venueDao, FirestoreVenueService firestoreVenueService) {
    return new VenueRepository(nodeDao, edgeDao, venueDao, firestoreVenueService);
  }
}
