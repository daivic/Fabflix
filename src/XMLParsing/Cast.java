package XMLParsing;


public class Cast {

    private String name;

    private String movie;



    public Cast(){

    }

    public Cast(String name, String movie) {
        this.name = name;
        this.movie = movie;

    }
    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Cast Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Movie:" + getMovie());
        sb.append(".");

        return sb.toString();
    }
}