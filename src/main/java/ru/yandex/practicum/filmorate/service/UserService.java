package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(int id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        if (getUser(user.getId()) == null) {
            throw new IllegalArgumentException("Пользователь с ID=" + user.getId() + " не найден.");
        }
        return userStorage.update(user);
    }

    public void addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            userStorage.update(user);
            userStorage.update(friend);
        } else {
            throw new IllegalArgumentException("Пользователи не найдены.");
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            userStorage.update(user);
            userStorage.update(friend);
        } else {
            throw new IllegalArgumentException("Пользователи не найдены.");
        }
    }

    public List<User> findUserFriends(int userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с ID=" + userId + " не найден.");
        }
        return user.getFriends().stream().map(this::getUser).collect(Collectors.toList());
    }

    public Set<User> getCommonFriends(int userId, int otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);
        if (user == null || otherUser == null) {
            throw new IllegalArgumentException("Один из пользователей не найден.");
        }
        Set<Integer> userFriends = new HashSet<>(user.getFriends());
        userFriends.retainAll(otherUser.getFriends());
        return userFriends.stream().map(this::getUser).collect(Collectors.toSet());
    }
}