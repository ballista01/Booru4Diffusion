package link.wizapp.booru4diffusion.controller;

import link.wizapp.booru4diffusion.model.Image;
import link.wizapp.booru4diffusion.security.services.UserDetailsImpl;
import link.wizapp.booru4diffusion.tdg.IImageTdg;
import link.wizapp.booru4diffusion.tdg.ITagTdg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    IImageTdg imageTdg;
    @Autowired
    ITagTdg tagTdg;
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("")
    public ResponseEntity<List<Image>> getAllImages(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String tags
//            @RequestParam(required = false) Set<String> tags
            ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = null;
        if(authentication.getPrincipal() instanceof UserDetailsImpl){
            userDetails = (UserDetailsImpl) authentication.getPrincipal();
        }
        try {
            List<Image> images;

            if (title != null) images = imageTdg.findByTitleContaining(title);
            else if(userId != null) {
                // TODO: Implement findByUserId
                if(userDetails != null && userId.equals(userDetails.getId())){
                    images = imageTdg.findByUserId(userId, true);
                } else {
                    images = imageTdg.findByUserId(userId, false);
                }
            } else if(tags != null) {
//                Variable tags is parsed from REST API query, it may contain duplicate tag names.
//                We can create a set to contain, remove duplicates, and pass as a variable.
//                By replacing array with object, there will be more methods we can use,
//                and the object type gives us hint on what to expect from the data within.
//                For example, when I see a Set being a function parameter, I know elements within
//                are distinct.
                String[] tagsArr = tags.split(" ");
                Set<String> tagSet = new HashSet<>();
                Collections.addAll(tagSet, tagsArr);
                images = imageTdg.findByTagsName(tagSet);
            }
            else images = imageTdg.findByPublished(true);


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
            // TODO: Don't display unpublished images if the userId of the image is not the current user
            Image image = imageTdg.findById(id);
            image.setTags(tagTdg.findByImageId(id));
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
            List<Image> images = imageTdg.findByPublished(true);
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<String> createImage(@RequestBody Image image){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        try {
            Image imageToSave = new Image();
            imageToSave.setUserId(userDetails.getId());
            imageToSave.setTitle(image.getTitle());
            imageToSave.setDescription(image.getDescription());
            imageToSave.setUrl(image.getUrl());
            imageToSave.setTags(image.getTags());
            imageToSave.setPublished(image.isPublished());
            imageTdg.save(imageToSave);
            return new ResponseEntity<>("Image created successfully", HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<String> updateImage(@PathVariable("id") long id, @RequestBody Image image){
        try {
            Image imageToUpdate = imageTdg.findById(id);
            log.info("Before getting userDetails");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            if(imageToUpdate != null && image != null){
                log.info(String.format("imageToUpdate.getUserId() = %d, userDetails.getId() = %d",
                        imageToUpdate.getUserId(), userDetails.getId()));
                if(imageToUpdate.getUserId() != userDetails.getId()){
                    return new ResponseEntity<>("User is unauthorized to update the image.", HttpStatus.UNAUTHORIZED);
                }
                log.info("Passed User Id check");
                imageToUpdate.setId(id);
                imageToUpdate.setTitle(image.getTitle());
                imageToUpdate.setDescription(image.getDescription());
                imageToUpdate.setUrl(image.getUrl());
                imageToUpdate.setTags(image.getTags());
                imageToUpdate.setPublished(image.isPublished());
                imageTdg.update(imageToUpdate);
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
            // TODO: Non admin/mod can only delete images his own image.
            int res = imageTdg.deleteById(id);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllImages(){
        try{
            int res = imageTdg.deleteAll();
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