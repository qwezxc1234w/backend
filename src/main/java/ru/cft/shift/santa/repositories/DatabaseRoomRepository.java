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
import java.util.List;

@Repository
public class DatabaseRoomRepository implements RoomRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private RoomExtractor roomExtractor;
    private UserExtractor userExtractor;

    @Autowired
    public DatabaseRoomRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                  RoomExtractor roomExtractor,
                                  UserExtractor userExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.roomExtractor = roomExtractor;
        this.userExtractor = userExtractor;
    }

//    @PostConstruct
//    public void initialize() {
//        String createGenerateRoomsIdSequenceSql = "create sequence ROOMS_ID_GENERATOR";
//        String createRoomsTableSql = "create table ROOMS (" +
//                "ROOM_ID  VARCHAR(64) default ROOMS_ID_GENERATOR.nextval," +
//                "NAME VARCHAR(64)," +
//                "CAPACITY INTEGER" +
//                ");";
//        jdbcTemplate.update(createGenerateRoomsIdSequenceSql, new MapSqlParameterSource());
//        jdbcTemplate.update(createRoomsTableSql, new MapSqlParameterSource());
//    }


    @Override
    public boolean isRoomFull(String roomId) {
        String sql = "select ROOMS.CAPACITY=count(ROOMS.ROOM_ID) AS CONDITION from ROOMS INNER JOIN USERS ON ROOMS.ROOM_ID = USERS.ROOM_ID";
        return jdbcTemplate.query(sql, (rs -> {
            rs.next();
            return rs.getBoolean("CONDITION");
        }));
    }

    @Override
    public List<Room> getAllRooms() {
        String sql = "select ROOM_ID, NAME, CAPACITY from ROOMS";
        return jdbcTemplate.query(sql, roomExtractor);
    }

    @Override
    public Room fetchRoom(String roomId) {
        String sql = "select ROOM_ID, NAME, CAPACITY " +
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
        String insertUserSql = "insert into ROOMS (NAME, CAPACITY) values (:name, :capacity)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", room.getName())
                .addValue("capacity", room.getCapacity());
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertUserSql, params, generatedKeyHolder);
        return fetchRoom(generatedKeyHolder.getKeys().get("ROOM_ID").toString());
    }

    @Override
    public void addUserInRoom(String roomId, User user) {
        String sql = "update USERS " +
                "set ROOM_ID=:roomId " +
                "where USER_ID=:userId";
        MapSqlParameterSource bookParams = new MapSqlParameterSource()
                .addValue("roomId", roomId)
                .addValue("userId", user.getId());
        jdbcTemplate.update(sql, bookParams);
    }

    @Override
    public List<User> getUsersInRoom(String roomId) {
        String sql = "SELECT USER_ID, NAME, WISHES, RECIPIENT_NAME, RECIPIENT_WISHES FROM USERS LEFT JOIN " +
                "(SELECT USER_ID AS RECIPIENT_ID, NAME AS RECIPIENT_NAME, WISHES AS RECIPIENT_WISHES FROM USERS) as RECIPIENTS ON USERS.RECIPIENT_ID = RECIPIENTS.RECIPIENT_ID " +
                "WHERE USERS.ROOM_ID=:roomId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId);
        List<User> users = jdbcTemplate.query(sql, params, userExtractor);
        if (users == null || users.isEmpty())
            throw new NotFoundException();
        else
            return users;
    }
}
