package ru.cft.shift.santa.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.cft.shift.santa.exception.NotFoundException;
import ru.cft.shift.santa.models.Room;
import ru.cft.shift.santa.models.User;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class DatabaseRoomRepository implements RoomRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private RoomExtractor roomExtractor;

    @Autowired
    public DatabaseRoomRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                  RoomExtractor roomExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.roomExtractor = roomExtractor;
    }

    @PostConstruct
    public void initialize() {
        String createGenerateRoomsIdSequenceSql = "create sequence IF NOT EXISTS ROOMS_ID_GENERATOR";
        String createRoomsTableSql = "create table IF NOT EXISTS  ROOMS (" +
                "ROOM_ID  VARCHAR(64) default ROOMS_ID_GENERATOR.nextval," +
                "NAME VARCHAR(64)," +
                "CAPACITY INTEGER," +
                "SIZE INTEGER" +
                ");";
        jdbcTemplate.update(createGenerateRoomsIdSequenceSql, new MapSqlParameterSource());
        jdbcTemplate.update(createRoomsTableSql, new MapSqlParameterSource());
    }

    @Override
    public boolean isRoomFull(String roomId) {
        String sql = "select CAPACITY<=SIZE from ROOMS WHERE ROOMS.ROOM_ID=:roomId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId);
        Boolean result = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        if (result == null)
            throw new NotFoundException();
        return result;
    }

    @Override
    public void increaseRoomSize(String roomId) {
        String sql = "UPDATE ROOMS SET SIZE = SIZE + 1 WHERE ROOM_ID=:roomId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<Room> getAllRooms() {
        String sql = "select ROOM_ID, NAME, CAPACITY, SIZE from ROOMS";
        return jdbcTemplate.query(sql, roomExtractor);
    }

    @Override
    public Room fetchRoom(String roomId) {
        String sql = "select ROOM_ID, NAME, CAPACITY, SIZE " +
                "from ROOMS where ROOM_ID=:roomId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId);
        List<Room> rooms = jdbcTemplate.query(sql, params, roomExtractor);
        if (rooms == null || rooms.isEmpty())
            throw new NotFoundException();
        else
            return rooms.get(0);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Room createRoom(Room room) {
        String insertUserSql = "insert into ROOMS (NAME, CAPACITY, SIZE) values (:name, :capacity, 0)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", room.getName())
                .addValue("capacity", room.getCapacity());
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertUserSql, params, generatedKeyHolder);
        return fetchRoom(generatedKeyHolder.getKeys().get("ROOM_ID").toString());
    }
}
