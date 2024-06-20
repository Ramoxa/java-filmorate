package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger userIdCounter = new AtomicInteger();

    @Override
    public User create(User user) {
        user.setId(userIdCounter.incrementAndGet());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        validateUserExists(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        users.get(userId).getFriends().remove(Integer.valueOf(friendId));
        users.get(friendId).getFriends().remove(Integer.valueOf(userId));
    }

    @Override
    public Set<User> getCommonFriends(int userId, int anotherUserId) {
        return null;
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(int userId) {
        validateUserExists(userId);
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public boolean findId(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean containsId(int userId) {
        return false;
    }

    @Override
    public List<User> findFriends(int userId) {
        validateUserExists(userId);
        List<User> friends = new ArrayList<>();
        return friends;
    }

    private void validateUserExists(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Юзер не найден ID: " + userId);
        }
    }
}