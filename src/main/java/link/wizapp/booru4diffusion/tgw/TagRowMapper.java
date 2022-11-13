package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TagRowMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));
        Long[] imageIds = (Long[]) rs.getArray("image_ids").getArray();
        for(Long imgId: imageIds){
            tag.addImageId(imgId);
        }

        return tag;
    }
}
