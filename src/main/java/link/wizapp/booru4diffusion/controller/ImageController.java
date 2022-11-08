package link.wizapp.booru4diffusion.controller;

import link.wizapp.booru4diffusion.model.Image;
import link.wizapp.booru4diffusion.tgw.IImageTgw;
import link.wizapp.booru4diffusion.tgw.ImageTgw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    IImageTgw imageTgw;
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("/images")
    public ResponseEntity<List<Image>> getAllImages(@RequestParam(required = false) String title) {
        try {
            List<Image> images = new ArrayList<Image>();

//            log.info(String.format("before findAll, title = %s", title));

            if (title == null)
                imageTgw.findAll().forEach(images::add);
            else
                imageTgw.findByTitleContaining(title).forEach(images::add);

            if (images.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}