package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Image;

import java.util.List;

public interface IImageTdg {
    int save(Image image);

    int update(Image image);

    Image findById(Long id);

    int deleteById(Long id);

    List<Image> findAll();

    List<Image> findByPublished(boolean published);

    List<Image> findByTitleContaining(String title);

    int deleteAll();
}