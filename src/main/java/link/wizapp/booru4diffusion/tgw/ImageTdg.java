package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class ImageTdg implements IImageTdg{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(Image image) {
        try {
            int res = 0;
            String insertQuery = "INSERT INTO images (title, description, published) VALUES(?,?,?) RETURNING id";
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            res += jdbcTemplate.update(conn -> {
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, image.getTitle());
                preparedStatement.setString(2, image.getDescription());
                preparedStatement.setBoolean(3, image.isPublished());
                return preparedStatement;
            }, generatedKeyHolder);
            long newId = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
            image.setId(newId);

            return res;
        }  catch (DataAccessException e) {
            return -1;
        }
    }

    @Override
    public int update(Image Image) {
        return jdbcTemplate.update("UPDATE images SET title=?, description=?, published=? WHERE id=?",
                new Object[] { Image.getTitle(), Image.getDescription(), Image.isPublished(), Image.getId() });
    }

    @Override
    public Image findById(Long id) {
        try {
            Image Image = jdbcTemplate.queryForObject("SELECT * FROM images WHERE id=?",
                    BeanPropertyRowMapper.newInstance(Image.class), id);

            return Image;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM images WHERE id=?", id);
    }

    @Override
    public List<Image> findAll() {
        return jdbcTemplate.query("SELECT * from images", BeanPropertyRowMapper.newInstance(Image.class));
    }

    @Override
    public List<Image> findByPublished(boolean published) {
        return jdbcTemplate.query("SELECT * from images WHERE published=?",
                BeanPropertyRowMapper.newInstance(Image.class), published);
    }

    @Override
    public List<Image> findByTitleContaining(String title) {
        String q = "SELECT * from images WHERE title ILIKE '%" + title + "%'";

        return jdbcTemplate.query(q, BeanPropertyRowMapper.newInstance(Image.class));
    }

    @Override
    public int deleteAll() {
        return jdbcTemplate.update("DELETE from images");
    }
}