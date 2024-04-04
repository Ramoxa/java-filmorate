package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        setEmail(email);
        setLogin(login);
        if (name == null || name.trim().isEmpty()) {
            this.name = login;
        } else {
            this.name = name;
        }
        setBirthday(birthday);
        setFriends(friends);
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        this.email = email;
    }

    public void setLogin(String login) {
        if (login == null || login.trim().isEmpty() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        this.login = login;
    }

    public void setBirthday(LocalDate birthday) {
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        this.birthday = birthday;
    }
    public void setFriends (Set<Integer> friends) {
        friends.size();
        this.friends = friends;
    }

}