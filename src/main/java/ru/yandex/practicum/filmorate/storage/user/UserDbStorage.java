package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Primary
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(name, email, login, birthday) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        if (rowsAffected != 1 || keyHolder.getKey() == null) {
            throw new IllegalStateException("Не удалось создать пользователя");
        }
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        int rowsAffected = jdbcTemplate.update("UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?",
                user.getName(), user.getEmail(), user.getLogin(), Date.valueOf(user.getBirthday()), user.getId());
        if (rowsAffected != 1) {
            throw new NotFoundException("Пользователь не найден ID: " + user.getId());
        }
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ?", user.getId());
        for (Integer friendId : user.getFriends()) {
            jdbcTemplate.update("INSERT INTO friends(user_id, friend_id) VALUES (?, ?)", user.getId(), friendId);
        }
        return user;
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update("INSERT INTO friends(user_id, friend_id) VALUES (?, ?)", userId, friendId);
    }

    @Override
    public Set<User> getCommonFriends(int userId, int otherUserId) {
        List<User> commonFriends = jdbcTemplate.query(
                "SELECT u.* FROM users u JOIN friends f1 ON u.id = f1.friend_id JOIN friends f2 ON u.id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ?",
                new Object[]{userId, otherUserId},
                new UserMapper());
        return new HashSet<>(commonFriends);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    @Override
    public Optional<User> findById(int userId) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", new Object[]{userId}, new UserMapper());
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.get(0);
        List<Integer> friendIds = jdbcTemplate.query("SELECT friend_id FROM friends WHERE user_id = ?", new Object[]{userId}, (rs, rowNum) -> rs.getInt("friend_id"));
        user.setFriends(new HashSet<>(friendIds));
        return Optional.of(user);
    }

    @Override
    public boolean findId(int userId) {
        return false;
    }

    @Override
    public boolean containsId(int userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public List<User> findFriends(int userId) {
        return null;
    }

    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }
    }
}