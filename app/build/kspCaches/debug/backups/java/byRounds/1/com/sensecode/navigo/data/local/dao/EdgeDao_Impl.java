package com.sensecode.navigo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sensecode.navigo.data.local.entity.EdgeEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EdgeDao_Impl implements EdgeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EdgeEntity> __insertionAdapterOfEdgeEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteEdgesByVenue;

  public EdgeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEdgeEntity = new EntityInsertionAdapter<EdgeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `edges` (`fromNodeId`,`toNodeId`,`venueId`,`distanceM`,`directionDegrees`,`directionLabel`,`instruction`,`hasStairs`,`estimatedSeconds`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EdgeEntity entity) {
        statement.bindString(1, entity.getFromNodeId());
        statement.bindString(2, entity.getToNodeId());
        statement.bindString(3, entity.getVenueId());
        statement.bindDouble(4, entity.getDistanceM());
        statement.bindDouble(5, entity.getDirectionDegrees());
        statement.bindString(6, entity.getDirectionLabel());
        statement.bindString(7, entity.getInstruction());
        final int _tmp = entity.getHasStairs() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getEstimatedSeconds());
      }
    };
    this.__preparedStmtOfDeleteEdgesByVenue = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM edges WHERE venueId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertEdge(final EdgeEntity edge, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEdgeEntity.insert(edge);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllEdges(final List<EdgeEntity> edges,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEdgeEntity.insert(edges);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEdgesByVenue(final String venueId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteEdgesByVenue.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, venueId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteEdgesByVenue.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getEdgesForVenue(final String venueId,
      final Continuation<? super List<EdgeEntity>> $completion) {
    final String _sql = "SELECT * FROM edges WHERE venueId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, venueId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EdgeEntity>>() {
      @Override
      @NonNull
      public List<EdgeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFromNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromNodeId");
          final int _cursorIndexOfToNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "toNodeId");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfDistanceM = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceM");
          final int _cursorIndexOfDirectionDegrees = CursorUtil.getColumnIndexOrThrow(_cursor, "directionDegrees");
          final int _cursorIndexOfDirectionLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "directionLabel");
          final int _cursorIndexOfInstruction = CursorUtil.getColumnIndexOrThrow(_cursor, "instruction");
          final int _cursorIndexOfHasStairs = CursorUtil.getColumnIndexOrThrow(_cursor, "hasStairs");
          final int _cursorIndexOfEstimatedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedSeconds");
          final List<EdgeEntity> _result = new ArrayList<EdgeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EdgeEntity _item;
            final String _tmpFromNodeId;
            _tmpFromNodeId = _cursor.getString(_cursorIndexOfFromNodeId);
            final String _tmpToNodeId;
            _tmpToNodeId = _cursor.getString(_cursorIndexOfToNodeId);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final float _tmpDistanceM;
            _tmpDistanceM = _cursor.getFloat(_cursorIndexOfDistanceM);
            final float _tmpDirectionDegrees;
            _tmpDirectionDegrees = _cursor.getFloat(_cursorIndexOfDirectionDegrees);
            final String _tmpDirectionLabel;
            _tmpDirectionLabel = _cursor.getString(_cursorIndexOfDirectionLabel);
            final String _tmpInstruction;
            _tmpInstruction = _cursor.getString(_cursorIndexOfInstruction);
            final boolean _tmpHasStairs;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasStairs);
            _tmpHasStairs = _tmp != 0;
            final int _tmpEstimatedSeconds;
            _tmpEstimatedSeconds = _cursor.getInt(_cursorIndexOfEstimatedSeconds);
            _item = new EdgeEntity(_tmpFromNodeId,_tmpToNodeId,_tmpVenueId,_tmpDistanceM,_tmpDirectionDegrees,_tmpDirectionLabel,_tmpInstruction,_tmpHasStairs,_tmpEstimatedSeconds);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getEdgesFromNode(final String fromNodeId,
      final Continuation<? super List<EdgeEntity>> $completion) {
    final String _sql = "SELECT * FROM edges WHERE fromNodeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fromNodeId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EdgeEntity>>() {
      @Override
      @NonNull
      public List<EdgeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFromNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromNodeId");
          final int _cursorIndexOfToNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "toNodeId");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfDistanceM = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceM");
          final int _cursorIndexOfDirectionDegrees = CursorUtil.getColumnIndexOrThrow(_cursor, "directionDegrees");
          final int _cursorIndexOfDirectionLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "directionLabel");
          final int _cursorIndexOfInstruction = CursorUtil.getColumnIndexOrThrow(_cursor, "instruction");
          final int _cursorIndexOfHasStairs = CursorUtil.getColumnIndexOrThrow(_cursor, "hasStairs");
          final int _cursorIndexOfEstimatedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedSeconds");
          final List<EdgeEntity> _result = new ArrayList<EdgeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EdgeEntity _item;
            final String _tmpFromNodeId;
            _tmpFromNodeId = _cursor.getString(_cursorIndexOfFromNodeId);
            final String _tmpToNodeId;
            _tmpToNodeId = _cursor.getString(_cursorIndexOfToNodeId);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final float _tmpDistanceM;
            _tmpDistanceM = _cursor.getFloat(_cursorIndexOfDistanceM);
            final float _tmpDirectionDegrees;
            _tmpDirectionDegrees = _cursor.getFloat(_cursorIndexOfDirectionDegrees);
            final String _tmpDirectionLabel;
            _tmpDirectionLabel = _cursor.getString(_cursorIndexOfDirectionLabel);
            final String _tmpInstruction;
            _tmpInstruction = _cursor.getString(_cursorIndexOfInstruction);
            final boolean _tmpHasStairs;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasStairs);
            _tmpHasStairs = _tmp != 0;
            final int _tmpEstimatedSeconds;
            _tmpEstimatedSeconds = _cursor.getInt(_cursorIndexOfEstimatedSeconds);
            _item = new EdgeEntity(_tmpFromNodeId,_tmpToNodeId,_tmpVenueId,_tmpDistanceM,_tmpDirectionDegrees,_tmpDirectionLabel,_tmpInstruction,_tmpHasStairs,_tmpEstimatedSeconds);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
