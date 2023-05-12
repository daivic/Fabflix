package XMLParsing;


public class Star {

    private String name;

    private String birth;

    private String id = "asdf";


    public Star(){

    }

    public Star(String name, String id, String birth) {
        this.name = name;
        this.birth = birth;
        this.id  = id;

    }
    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Birth:" + getBirth());
        sb.append(", ");
        sb.append("Id:" + getId());
        sb.append(".");

        return sb.toString();
    }
}