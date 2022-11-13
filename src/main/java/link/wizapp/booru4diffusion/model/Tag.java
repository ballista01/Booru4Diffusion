package link.wizapp.booru4diffusion.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tag {
    private Integer id;
    private String name;
    private Set<Long> imageIds = new HashSet<>();

    public Tag(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(Integer id, String name, Set<Long> imageIds){
        this.id = id;
        this.name = name;
        this.imageIds = imageIds;
    }

    public Tag(Integer id, String name, List<Long> imageIds){
        this.id = id;
        this.name = name;
        addImageIds(imageIds);
    }

    public Tag(){
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(Set<Long> imageIds) {
        this.imageIds = imageIds;
    }

    public boolean addImage(Image image){
        return imageIds.add(image.getId());
    }

    public boolean addImageId(Long imageId){
        return imageIds.add(imageId);
    }

    public boolean addImageIds(List<Long> imageIds){
        return this.imageIds.addAll(imageIds);
    }

    @Override
    public int hashCode() {
        return (name + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Tag anotherTag = (Tag) obj;
        return anotherTag.getId().equals(id) && anotherTag.getName().equals(name);
    }

    @Override
    public String toString() {
        return String.format("Tag: id = %d, name = \"%s\"", id, name);
    }
}
