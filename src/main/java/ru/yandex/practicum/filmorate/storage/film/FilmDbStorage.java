package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Primary
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private static GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        FilmDbStorage.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", new FilmMapper());
        for (Film film : films) {
            Set<Genre> filmGenres = (Set<Genre>) genreStorage.getFilmGenres(film.getId()).stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(filmGenres);
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        }

        return films;
    }

    @Override
    public Film findById(int id) {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films WHERE id = ?", new FilmMapper(), id);
        if (films.isEmpty()) {
            return null;
        }
        Film film = films.get(0);
        film.setGenres((Set<Genre>) genreStorage.getFilmGenres(id).stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new)));
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        return film;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        if (rowsAffected == 0) {
            throw new ValidationException("Не удалось создать фильм.");
        }

        // Получаем сгенерированный идентификатор
        int id = keyHolder.getKey().intValue();
        film.setId(id); // Присваиваем идентификатор объекту фильма
        addFilmGenres(film);

        return findById(id); // Возвращаем фильм по его идентификатору
    }

    @Override
    public Film update(Film film) {
        int rowsAffected = jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм не найден ID: " + film.getId());
        }

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        addFilmGenres(film);

        return findById(film.getId());
    }

    public void addFilmGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                batchArgs.add(new Object[]{film.getId(), genre.getId()});
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Override
    public void deleteById(int id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм не найден ID: " + id);
        }
    }

    @Override
    public boolean userExists(int userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Integer.class, userId) > 0;
    }

    @Override
    public boolean filmExists(int filmId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM films WHERE id = ?", Integer.class, filmId) > 0;
    }

    @Override
    public boolean genreExists(int genreId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM genre WHERE id = ?", Integer.class, genreId) > 0;
    }

    @Override
    public boolean mpaExists(int mpaId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mpa WHERE id = ?", Integer.class, mpaId) > 0;
    }

    @Override
    public void addLike(int userId, int filmId) {
        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден ID: " + userId);
        }
        if (!filmExists(filmId)) {
            throw new NotFoundException("Фильм не найден ID: " + filmId);
        }

        int rowsAffected = jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (?, ?)", userId, filmId);
        if (rowsAffected == 0) {
            throw new ValidationException("Не удалось поставить лайк для пользователя ID: " + userId + " к фильму ID: " + filmId);
        }
    }

    @Override
    public void removeLike(int userId, int filmId) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?", userId, filmId);
        if (rowsAffected == 0) {
            throw new NotFoundException("Лайк не найден для пользователя ID: " + userId + " и фильма ID: " + filmId);
        }
    }

    @Override
    public Collection<Film> findTopFilmsByLikes(int count) {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id " + "FROM films f " + "LEFT JOIN (SELECT film_id, COUNT(*) as like_count FROM likes GROUP BY film_id ORDER BY like_count DESC LIMIT ?) l " + "ON f.id = l.film_id " + "ORDER BY l.like_count DESC, f.id ASC";
        List<Film> films = jdbcTemplate.query(query, new FilmMapper(), count);
        for (Film film : films) {
            film.setGenres((Set<Genre>) genreStorage.getFilmGenres(film.getId()).stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new)));
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        }
        return films;
    }

    private static class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), ""));
            return film;
        }
    }
}