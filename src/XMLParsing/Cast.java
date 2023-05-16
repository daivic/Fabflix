package XMLParsing;


import java.util.ArrayList;

public class Cast {

    ArrayList<String> cast = new ArrayList<String>();

    private String movie;



    public Cast(){

    }

    public Cast(ArrayList<String> cast, String movie) {
        this.cast = cast;
        this.movie = movie;

    }
    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }




    public ArrayList<String> getCast() {
        return cast;
    }

    public void setCast(String name) {

        this.cast.add(name);
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Cast Details - ");
        sb.append("Cast:" + getCast());
        sb.append(", ");
        sb.append("Movie:" + getMovie());
        sb.append(".");

        return sb.toString();
    }
}