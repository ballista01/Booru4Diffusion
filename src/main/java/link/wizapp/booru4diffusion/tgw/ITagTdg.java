package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Tag;

import java.util.Set;

public interface ITagTdg {
    Tag findByName(String name);
    Tag findById(Integer id);
    int save(Tag tag);
    int addTagToImage(Integer tagId, Long imageId);
    int addTagsToImage(Set<Tag> tags, Long imageId);
}
