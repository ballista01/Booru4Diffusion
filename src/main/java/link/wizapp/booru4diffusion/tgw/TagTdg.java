package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class TagTdg implements ITagTdg {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Tag findByName(String name) {
        try {
            Tag tag = jdbcTemplate.queryForObject("""
                    SELECT tags.id AS id, tags.name AS name, ARRAY_AGG(images_tags.image_id) as image_ids FROM tags
                    LEFT JOIN images_tags ON tags.id = images_tags.tag_id
                    WHERE tags.name = ?
                    GROUP BY tags.id;
                    """, new TagRowMapper(), name);
//            Tag tag = jdbcTemplate.queryForObject("SELECT * FROM tags WHERE name = ?",
//                    BeanPropertyRowMapper.newInstance(Tag.class), name);
//            if(tag == null){
//                return null;
//            }
//            tag.addImageIds(getImageIds(tag.getId()));
            return tag;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public Tag findById(Integer id) {
        try {
            Tag tag = jdbcTemplate.queryForObject("""
                    SELECT tags.id AS id, tags.name AS name, ARRAY_AGG(images_tags.image_id) as image_ids FROM tags
                    LEFT JOIN images_tags ON tags.id = images_tags.tag_id
                    WHERE tags.id = ?
                    GROUP BY tags.id;
                    """, new TagRowMapper(), id);
//            Tag tag = jdbcTemplate.queryForObject("SELECT * FROM tags WHERE id = ?",
//                    BeanPropertyRowMapper.newInstance(Tag.class), id);
//            if(tag == null){
//                return null;
//            }
//            tag.addImageIds(getImageIds(id));
            return tag;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public int save(Tag tag) {
        try {
            int res = 0;
            String insertQuery = """
                    INSERT INTO tags (name) VALUES (?) RETURNING id;
                    """;
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            res += jdbcTemplate.update(conn -> {
                PreparedStatement preparedStatement
                        = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, tag.getName());
                return preparedStatement;
            }, generatedKeyHolder);
            Integer newId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
            tag.setId(newId);
            // TODO: handle insertion error for duplicated tag names

            return res;
        } catch (DuplicateKeyException e) {
            return 0;
        } catch (DataAccessException e){
            return -1;
        }
    }

    @Override
    public int addTagToImage(Integer tagId, Long imageId) {
        try {
            int res = jdbcTemplate.update("INSERT INTO images_tags (tag_id, image_id) VALUES (?,?)",
                    new Object[]{tagId, imageId});
            return res;
        } catch (DuplicateKeyException e){
            return 0;
        } catch (DataAccessException e ){
            return -1;
        }
    }

    @Override
    public int addTagsToImage(Set<Tag> tags, Long imageId) {
        int res = 0;
        if(tags!=null && !tags.isEmpty()){
            for(Tag tag: tags){
                int tagSaveRes = save(tag);
                res += tagSaveRes;
                // if inserted a new tag, tagSaveRes > 0, tag.id is updated, add the newly created tag to image
                if(tagSaveRes > 0) res += addTagToImage(tag.getId(), imageId);
                // otherwise the tag already exists, query for its id and add the tag to image.
                else res += addTagToImage(findByName(tag.getName()).getId(), imageId);
            }
        }
        return res;
    }

    public List<Long> getImageIds(Integer tagId) {
        try {
            List<Long> imageIds = jdbcTemplate.query("SELECT image_id FROM images_tags WHERE tag_id = ?",
                    BeanPropertyRowMapper.newInstance(Long.class), tagId);
            return imageIds;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
}
