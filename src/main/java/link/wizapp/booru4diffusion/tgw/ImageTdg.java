package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Image;
import link.wizapp.booru4diffusion.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class ImageTdg implements IImageTdg{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TagTdg tagTdg;

    @Override
    public int save(Image image) {
        try {
            image.setTimestampCreated(new Timestamp(System.currentTimeMillis()));
            image.setTimestampUpdated(new Timestamp(System.currentTimeMillis()));
            int res = 0;
            String insertQuery = """
INSERT INTO images
(user_id, title, description, url, timestamp_created, timestamp_updated, published)
VALUES
(?,?,?,?,?,?,?)
RETURNING id;
""";
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            res += jdbcTemplate.update(conn -> {
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, image.getUserId());
                preparedStatement.setString(2, image.getTitle());
                preparedStatement.setString(3, image.getDescription());
                preparedStatement.setString(4, image.getUrl());
                preparedStatement.setTimestamp(5, image.getTimestampCreated());
                preparedStatement.setTimestamp(6, image.getTimestampUpdated());
                preparedStatement.setBoolean(7, image.isPublished());
                return preparedStatement;
            }, generatedKeyHolder);
            long newId = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
            image.setId(newId);

            return res + tagTdg.addTagsToImage(image.getTags(), image.getId());
        }  catch (DataAccessException e) {
            return -1;
        }
    }



    @Override
    public int update(Image image) {
        int res = 0;
        String updateQueryStr = """
                UPDATE images SET user_id = ?, title=?, description=?, url=?, timestamp_updated=?, published=?
                WHERE id=?
                """;
        image.setTimestampUpdated(new Timestamp(System.currentTimeMillis()));
        res += jdbcTemplate.update(updateQueryStr,
                new Object[] { image.getUserId(), image.getTitle(), image.getDescription(), image.getUrl(),
                        image.getTimestampUpdated(), image.isPublished(), image.getId() });
        return res + tagTdg.addTagsToImage(image.getTags(), image.getId());
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