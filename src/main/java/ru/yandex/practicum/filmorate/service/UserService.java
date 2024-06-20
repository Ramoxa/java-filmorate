package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден."));
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        getUser(user.getId());
        return userStorage.update(user);
    }

    public void addFriend(int userId, int friendId) {
        getUser(userId);
        getUser(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        getUser(userId);
        getUser(friendId);
        userStorage.removeFriend(userId, friendId);

    }

    public List<User> findUserFriends(int userId) {
        User user = getUser(userId);
        return user.getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public Set<User> getCommonFriends(int userId, int otherUserId) {
        getUser(userId);
        getUser(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}