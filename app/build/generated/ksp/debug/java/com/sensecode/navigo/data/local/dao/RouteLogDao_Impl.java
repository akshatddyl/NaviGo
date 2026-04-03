package com.sensecode.navigo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sensecode.navigo.data.local.entity.RouteLogEntity;
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
public final class RouteLogDao_Impl implements RouteLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RouteLogEntity> __insertionAdapterOfRouteLogEntity;

  public RouteLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRouteLogEntity = new EntityInsertionAdapter<RouteLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `route_logs` (`sessionId`,`venueId`,`startNodeId`,`destinationNodeId`,`startTime`,`endTime`,`deviationCount`,`completedSuccessfully`,`routeNodeIds`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RouteLogEntity entity) {
        statement.bindString(1, entity.getSessionId());
        statement.bindString(2, entity.getVenueId());
        statement.bindString(3, entity.getStartNodeId());
        statement.bindString(4, entity.getDestinationNodeId());
        statement.bindLong(5, entity.getStartTime());
        statement.bindLong(6, entity.getEndTime());
        statement.bindLong(7, entity.getDeviationCount());
        final int _tmp = entity.getCompletedSuccessfully() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getRouteNodeIds());
      }
    };
  }

  @Override
  public Object insertLog(final RouteLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRouteLogEntity.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLogsForVenue(final String venueId,
      final Continuation<? super List<RouteLogEntity>> $completion) {
    final String _sql = "SELECT * FROM route_logs WHERE venueId = ? ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, venueId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RouteLogEntity>>() {
      @Override
      @NonNull
      public List<RouteLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfStartNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "startNodeId");
          final int _cursorIndexOfDestinationNodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationNodeId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDeviationCount = CursorUtil.getColumnIndexOrThrow(_cursor, "deviationCount");
          final int _cursorIndexOfCompletedSuccessfully = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSuccessfully");
          final int _cursorIndexOfRouteNodeIds = CursorUtil.getColumnIndexOrThrow(_cursor, "routeNodeIds");
          final List<RouteLogEntity> _result = new ArrayList<RouteLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RouteLogEntity _item;
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final String _tmpStartNodeId;
            _tmpStartNodeId = _cursor.getString(_cursorIndexOfStartNodeId);
            final String _tmpDestinationNodeId;
            _tmpDestinationNodeId = _cursor.getString(_cursorIndexOfDestinationNodeId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final int _tmpDeviationCount;
            _tmpDeviationCount = _cursor.getInt(_cursorIndexOfDeviationCount);
            final boolean _tmpCompletedSuccessfully;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompletedSuccessfully);
            _tmpCompletedSuccessfully = _tmp != 0;
            final String _tmpRouteNodeIds;
            _tmpRouteNodeIds = _cursor.getString(_cursorIndexOfRouteNodeIds);
            _item = new RouteLogEntity(_tmpSessionId,_tmpVenueId,_tmpStartNodeId,_tmpDestinationNodeId,_tmpStartTime,_tmpEndTime,_tmpDeviationCount,_tmpCompletedSuccessfully,_tmpRouteNodeIds);
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
