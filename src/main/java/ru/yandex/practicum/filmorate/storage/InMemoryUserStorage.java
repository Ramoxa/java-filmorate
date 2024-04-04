package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
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
    public Set<Integer> getCommonFriends(int userId, int anotherUserId) {
        validateUserExists(userId);
        validateUserExists(anotherUserId);
        Set<Integer> commonFriends = new HashSet<>(users.get(userId).getFriends());
        commonFriends.retainAll(users.get(anotherUserId).getFriends());
        return commonFriends;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(int userId) {
        validateUserExists(userId);
        return users.get(userId);
    }

    @Override
    public boolean findId(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public List<User> findFriends(int userId) {
        validateUserExists(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : users.get(userId).getFriends()) {
            friends.add(users.get(friendId));
        }
        return friends;
    }

    private void validateUserExists(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Юзер не найден ID: " + userId);
        }
    }
}