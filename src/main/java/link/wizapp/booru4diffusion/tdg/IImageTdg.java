package link.wizapp.booru4diffusion.tdg;

import link.wizapp.booru4diffusion.model.Image;

import java.util.List;
import java.util.Set;

public interface IImageTdg {
    int save(Image image);

    int update(Image image);

    Image findById(Long id);

    List<Image> findByTagsName(Set<String> tags);

    int deleteById(Long id);

    List<Image> findAll();

    List<Image> findByPublished(boolean published);

    List<Image> findByTitleContaining(String title);

    List<Image> findByUserId(Long userId, boolean showNotPublished);

    int deleteAll();
}