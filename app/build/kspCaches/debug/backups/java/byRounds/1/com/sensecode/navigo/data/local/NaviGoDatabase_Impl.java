package com.sensecode.navigo.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.sensecode.navigo.data.local.dao.EdgeDao;
import com.sensecode.navigo.data.local.dao.EdgeDao_Impl;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import com.sensecode.navigo.data.local.dao.LocationNodeDao_Impl;
import com.sensecode.navigo.data.local.dao.RouteLogDao;
import com.sensecode.navigo.data.local.dao.RouteLogDao_Impl;
import com.sensecode.navigo.data.local.dao.VenueDao;
import com.sensecode.navigo.data.local.dao.VenueDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NaviGoDatabase_Impl extends NaviGoDatabase {
  private volatile LocationNodeDao _locationNodeDao;

  private volatile EdgeDao _edgeDao;

  private volatile RouteLogDao _routeLogDao;

  private volatile VenueDao _venueDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `location_nodes` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `floor` INTEGER NOT NULL, `venueId` TEXT NOT NULL, `accessible` INTEGER NOT NULL, `type` TEXT NOT NULL, `relativeX` REAL NOT NULL, `relativeY` REAL NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `edges` (`fromNodeId` TEXT NOT NULL, `toNodeId` TEXT NOT NULL, `venueId` TEXT NOT NULL, `distanceM` REAL NOT NULL, `directionDegrees` REAL NOT NULL, `directionLabel` TEXT NOT NULL, `instruction` TEXT NOT NULL, `hasStairs` INTEGER NOT NULL, `estimatedSeconds` INTEGER NOT NULL, PRIMARY KEY(`fromNodeId`, `toNodeId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `route_logs` (`sessionId` TEXT NOT NULL, `venueId` TEXT NOT NULL, `startNodeId` TEXT NOT NULL, `destinationNodeId` TEXT NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `deviationCount` INTEGER NOT NULL, `completedSuccessfully` INTEGER NOT NULL, `routeNodeIds` TEXT NOT NULL, PRIMARY KEY(`sessionId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `venues` (`venueId` TEXT NOT NULL, `name` TEXT NOT NULL, `address` TEXT NOT NULL, `orgName` TEXT NOT NULL, `floors` INTEGER NOT NULL, `nodeCount` INTEGER NOT NULL, `publisherId` TEXT NOT NULL, PRIMARY KEY(`venueId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd041a1f9a313b81dc8e814009d5033ea')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `location_nodes`");
        db.execSQL("DROP TABLE IF EXISTS `edges`");
        db.execSQL("DROP TABLE IF EXISTS `route_logs`");
        db.execSQL("DROP TABLE IF EXISTS `venues`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsLocationNodes = new HashMap<String, TableInfo.Column>(8);
        _columnsLocationNodes.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("floor", new TableInfo.Column("floor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("venueId", new TableInfo.Column("venueId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("accessible", new TableInfo.Column("accessible", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("relativeX", new TableInfo.Column("relativeX", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationNodes.put("relativeY", new TableInfo.Column("relativeY", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocationNodes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocationNodes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocationNodes = new TableInfo("location_nodes", _columnsLocationNodes, _foreignKeysLocationNodes, _indicesLocationNodes);
        final TableInfo _existingLocationNodes = TableInfo.read(db, "location_nodes");
        if (!_infoLocationNodes.equals(_existingLocationNodes)) {
          return new RoomOpenHelper.ValidationResult(false, "location_nodes(com.sensecode.navigo.data.local.entity.LocationNodeEntity).\n"
                  + " Expected:\n" + _infoLocationNodes + "\n"
                  + " Found:\n" + _existingLocationNodes);
        }
        final HashMap<String, TableInfo.Column> _columnsEdges = new HashMap<String, TableInfo.Column>(9);
        _columnsEdges.put("fromNodeId", new TableInfo.Column("fromNodeId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("toNodeId", new TableInfo.Column("toNodeId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("venueId", new TableInfo.Column("venueId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("distanceM", new TableInfo.Column("distanceM", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("directionDegrees", new TableInfo.Column("directionDegrees", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("directionLabel", new TableInfo.Column("directionLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("instruction", new TableInfo.Column("instruction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("hasStairs", new TableInfo.Column("hasStairs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEdges.put("estimatedSeconds", new TableInfo.Column("estimatedSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEdges = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEdges = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEdges = new TableInfo("edges", _columnsEdges, _foreignKeysEdges, _indicesEdges);
        final TableInfo _existingEdges = TableInfo.read(db, "edges");
        if (!_infoEdges.equals(_existingEdges)) {
          return new RoomOpenHelper.ValidationResult(false, "edges(com.sensecode.navigo.data.local.entity.EdgeEntity).\n"
                  + " Expected:\n" + _infoEdges + "\n"
                  + " Found:\n" + _existingEdges);
        }
        final HashMap<String, TableInfo.Column> _columnsRouteLogs = new HashMap<String, TableInfo.Column>(9);
        _columnsRouteLogs.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("venueId", new TableInfo.Column("venueId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("startNodeId", new TableInfo.Column("startNodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("destinationNodeId", new TableInfo.Column("destinationNodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("endTime", new TableInfo.Column("endTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("deviationCount", new TableInfo.Column("deviationCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("completedSuccessfully", new TableInfo.Column("completedSuccessfully", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRouteLogs.put("routeNodeIds", new TableInfo.Column("routeNodeIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRouteLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRouteLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRouteLogs = new TableInfo("route_logs", _columnsRouteLogs, _foreignKeysRouteLogs, _indicesRouteLogs);
        final TableInfo _existingRouteLogs = TableInfo.read(db, "route_logs");
        if (!_infoRouteLogs.equals(_existingRouteLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "route_logs(com.sensecode.navigo.data.local.entity.RouteLogEntity).\n"
                  + " Expected:\n" + _infoRouteLogs + "\n"
                  + " Found:\n" + _existingRouteLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsVenues = new HashMap<String, TableInfo.Column>(7);
        _columnsVenues.put("venueId", new TableInfo.Column("venueId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenues.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenues.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenues.put("orgName", new TableInfo.Column("orgName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenues.put("floors", new TableInfo.Column("floors", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenues.put("nodeCount", new TableInfo.Column("nodeCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenues.put("publisherId", new TableInfo.Column("publisherId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVenues = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVenues = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVenues = new TableInfo("venues", _columnsVenues, _foreignKeysVenues, _indicesVenues);
        final TableInfo _existingVenues = TableInfo.read(db, "venues");
        if (!_infoVenues.equals(_existingVenues)) {
          return new RoomOpenHelper.ValidationResult(false, "venues(com.sensecode.navigo.data.local.entity.VenueEntity).\n"
                  + " Expected:\n" + _infoVenues + "\n"
                  + " Found:\n" + _existingVenues);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d041a1f9a313b81dc8e814009d5033ea", "7a2ffd8a8f90c366437acad363381d81");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "location_nodes","edges","route_logs","venues");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `location_nodes`");
      _db.execSQL("DELETE FROM `edges`");
      _db.execSQL("DELETE FROM `route_logs`");
      _db.execSQL("DELETE FROM `venues`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LocationNodeDao.class, LocationNodeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EdgeDao.class, EdgeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RouteLogDao.class, RouteLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VenueDao.class, VenueDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LocationNodeDao locationNodeDao() {
    if (_locationNodeDao != null) {
      return _locationNodeDao;
    } else {
      synchronized(this) {
        if(_locationNodeDao == null) {
          _locationNodeDao = new LocationNodeDao_Impl(this);
        }
        return _locationNodeDao;
      }
    }
  }

  @Override
  public EdgeDao edgeDao() {
    if (_edgeDao != null) {
      return _edgeDao;
    } else {
      synchronized(this) {
        if(_edgeDao == null) {
          _edgeDao = new EdgeDao_Impl(this);
        }
        return _edgeDao;
      }
    }
  }

  @Override
  public RouteLogDao routeLogDao() {
    if (_routeLogDao != null) {
      return _routeLogDao;
    } else {
      synchronized(this) {
        if(_routeLogDao == null) {
          _routeLogDao = new RouteLogDao_Impl(this);
        }
        return _routeLogDao;
      }
    }
  }

  @Override
  public VenueDao venueDao() {
    if (_venueDao != null) {
      return _venueDao;
    } else {
      synchronized(this) {
        if(_venueDao == null) {
          _venueDao = new VenueDao_Impl(this);
        }
        return _venueDao;
      }
    }
  }
}
