package link.wizapp.booru4diffusion.model;

public class Role {
    private Integer id;

    private ERole name;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }
    public Role(ERole name, Integer id){
        this.name = name;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}