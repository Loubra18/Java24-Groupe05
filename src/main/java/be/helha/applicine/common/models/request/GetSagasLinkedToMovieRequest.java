package be.helha.applicine.common.models.request;

public class GetSagasLinkedToMovieRequest extends ClientEvent {
    private final int movieId;
    private int amountOfSagas;

    public GetSagasLinkedToMovieRequest(int movieId){
        this.movieId = movieId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getAmountOfSagas() {
        return amountOfSagas;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) {
        requestVisitor.visit(this);
    }

    public void setAmountSagas(int amountSagas) {
        this.amountOfSagas = amountSagas;
    }
}
