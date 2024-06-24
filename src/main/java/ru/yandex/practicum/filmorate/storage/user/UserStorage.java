package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Set<User> getCommonFriends(int userId, int anotherUserId);

    List<User> findAll();

    Optional<User> findById(int userId);

    boolean findId(int userId);

    boolean containsId(int userId);

    List<User> findFriends(int userId);

}