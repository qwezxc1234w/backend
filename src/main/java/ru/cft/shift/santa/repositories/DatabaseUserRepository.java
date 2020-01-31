package ru.cft.shift.santa.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.cft.shift.santa.exception.NotFoundException;
import ru.cft.shift.santa.models.User;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * Реализиция, хранящая все данные в БД
 */
@Repository
public class DatabaseUserRepository implements UserRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private UserExtractor userExtractor;

    @Autowired
    public DatabaseUserRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                  UserExtractor userExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userExtractor = userExtractor;
    }

//    @PostConstruct
//    public void initialize() {
//        String createGenerateUsersIdSequenceSql = "create sequence USERS_ID_GENERATOR";
//        String createUsersTableSql = "create table USERS (" +
//                "USER_ID  VARCHAR(64) default USERS_ID_GENERATOR.nextval," +
//                "RECIPIENT_ID VARCHAR(64)," +
//                "ROOM_ID VARCHAR(64)," +
//                "NAME VARCHAR(64)," +
//                "WISHES VARCHAR(64)" +
//                ");";
//        jdbcTemplate.update(createGenerateUsersIdSequenceSql, new MapSqlParameterSource());
//        jdbcTemplate.update(createUsersTableSql, new MapSqlParameterSource());
//    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT USER_ID, NAME, WISHES, RECIPIENT_NAME, RECIPIENT_WISHES FROM USERS LEFT JOIN " +
                "(SELECT USER_ID AS RECIPIENT_ID, NAME AS RECIPIENT_NAME, WISHES AS RECIPIENT_WISHES FROM USERS) as RECIPIENTS ON USERS.RECIPIENT_ID = RECIPIENTS.RECIPIENT_ID";
        return jdbcTemplate.query(sql, userExtractor);
    }

    @Override
    public User fetchUser(String userId) {
        String sql = "SELECT USER_ID, NAME, WISHES, RECIPIENT_NAME, RECIPIENT_WISHES FROM USERS LEFT JOIN " +
                "(SELECT USER_ID AS RECIPIENT_ID, NAME AS RECIPIENT_NAME, WISHES AS RECIPIENT_WISHES FROM USERS) as RECIPIENTS ON USERS.RECIPIENT_ID = RECIPIENTS.RECIPIENT_ID " +
                "WHERE USERS.USER_ID=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<User> users = jdbcTemplate.query(sql, params, userExtractor);
        if (users == null || users.isEmpty())
            throw new NotFoundException();
        else
            return users.get(0);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public User createUser(User user) {
        String insertUserSql = "insert into USERS (RECIPIENT_ID, ROOM_ID, NAME, WISHES) values (" +
                ":recipientId, :roomId, :name, :wishes" +
                ")";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("recipientId", null)
                .addValue("roomId", null)
                .addValue("name", user.getName())
                .addValue("wishes", user.getWishes());
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertUserSql, params, generatedKeyHolder);
        return fetchUser(generatedKeyHolder.getKeys().get("USER_ID").toString());
    }

    @Override
    public void appointRecipient(String userId, String recipientId) {
        String sql = "update USERS " +
                "set RECIPIENT_ID=:recipientId " +
                "where USER_ID=:userId";
        MapSqlParameterSource bookParams = new MapSqlParameterSource()
                .addValue("recipientId", recipientId)
                .addValue("userId", userId);
        jdbcTemplate.update(sql, bookParams);
    }
}