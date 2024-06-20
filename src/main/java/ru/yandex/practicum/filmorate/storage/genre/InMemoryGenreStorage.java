package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Qualifier("genreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "SELECT * FROM genre WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, new GenreMapper(), id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genre ORDER BY id";
        return jdbcTemplate.query(sqlQuery, new GenreMapper());
    }

    @Override
    public Set<Genre> getFilmGenres(int filmId) {
        String sqlQuery = "SELECT g.id, g.name FROM genre g INNER JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, new GenreMapper(), filmId);
        return new LinkedHashSet<>(genres);
    }

    @Override
    public boolean existsGenreById(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM genre WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Genre.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
        }
    }
}