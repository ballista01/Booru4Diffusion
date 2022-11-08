package link.wizapp.booru4diffusion.controller;

import link.wizapp.booru4diffusion.model.Image;
import link.wizapp.booru4diffusion.tgw.IImageTgw;
import link.wizapp.booru4diffusion.tgw.ImageTgw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    IImageTgw imageTgw;
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("")
    public ResponseEntity<List<Image>> getAllImages(@RequestParam(required = false) String title) {
        try {
            List<Image> images;

            if (title == null)
                images = imageTgw.findAll();
            else
                images = imageTgw.findByTitleContaining(title);

            if (images.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable("id") long id) {
        try {
            Image image = imageTgw.findById(id);
            if(image != null){
                return new ResponseEntity<>(image, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/published")
    public ResponseEntity<List<Image>> getPublishedImages(){
        try {
            List<Image> images = imageTgw.findByPublished(true);
            if(images.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(images, HttpStatus.OK);
            }
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("")
    public ResponseEntity<String> createImage(@RequestBody Image image){
        try {
            imageTgw.save(new Image(image.getTitle(), image.getDescription(), false));
            return new ResponseEntity<>("Image created successfully", HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateImage(@PathVariable("id") long id, @RequestBody Image image){
        try {
            Image imageToUpdate = imageTgw.findById(image.getId());

            if(imageToUpdate != null){
                imageToUpdate.setTitle(image.getTitle());
                imageToUpdate.setDescription(image.getDescription());
                imageToUpdate.setPublished(image.isPublished());
                imageTgw.update(imageToUpdate);
                return new ResponseEntity<>("Image updated successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cannot find image with id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteImage(@PathVariable("id") long id){
        try {
            int res = imageTgw.deleteById(id);
            if(res == 0){
                return new ResponseEntity<>("Cannot find image with id: " + id, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>("Successfully deleted image with id: "+id, HttpStatus.OK);
            }
        } catch(Exception e){
            return new ResponseEntity<>(String.format("Image id :%d deletion failed!", id), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteAllImages(){
        try{
            int res = imageTgw.deleteAll();
            if(res == 0){
                return new ResponseEntity<>("All images successfully deleted!", HttpStatus.OK);
            }else {
                return new ResponseEntity<>("A database error occurred when attempting deleting all images!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e){
            return new ResponseEntity<>("Server error when attempting deleting all images!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}