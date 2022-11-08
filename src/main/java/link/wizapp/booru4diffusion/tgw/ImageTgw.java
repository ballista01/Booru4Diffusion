package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImageTgw implements IImageTgw{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(Image Image) {
        return jdbcTemplate.update("INSERT INTO images (title, description, published) VALUES(?,?,?)",
                new Object[] { Image.getTitle(), Image.getDescription(), Image.isPublished() });
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