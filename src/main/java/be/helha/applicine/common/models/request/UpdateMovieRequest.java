package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Movie;

public class UpdateMovieRequest extends ClientEvent{
    private Movie movie;
    private boolean status;

    public UpdateMovieRequest(Movie movie){
        this.movie = movie;
    }

    public Movie getMovie(){
        return movie;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean getStatus(){
        return status;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor){
        requestVisitor.visit(this);
    }
}
