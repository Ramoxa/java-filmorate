package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User create(User user);
    User update(User user);
    void addFriend(int userId, int friendId);
    void removeFriend(int userId, int friendId);
    Set<Integer> getCommonFriends(int userId, int anotherUserId);
    List<User> findAll();
    User findById(int userId);
    List<User> findFriends(int userId);

}