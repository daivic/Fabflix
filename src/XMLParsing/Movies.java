package XMLParsing;


public class Movies {

    private String title;

    private String year;

    private String director;

    private String id = "asdf";

    private String genres;

    public Movies(){

    }

    public Movies(String title, String id, String year,String director, String genres) {
        this.title = title;
        this.year = year;
        this.id  = id;
        this.director = director;
        this.genres = genres;

    }
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movies Details - ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Genres:" + getGenres());
        sb.append(".");

        return sb.toString();
    }
}