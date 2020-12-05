

public class Vote {
    String vote;
    String applicationId;

    public Vote(String vote, String applicationId) {
        this.vote = vote;
        this.applicationId = applicationId;
    }

    public String getVote() {
        return vote;
    }

    public String getApplicationId() {
        return applicationId;
    }
}