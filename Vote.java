

public class Vote {
    String vote;
    String applicationId;
    String voteId;

    public Vote(String vote, String applicationId, String voteId) {
        this.vote = vote;
        this.applicationId = applicationId;
        this.voteId = voteId;
    }

    public String getVote() {
        return vote;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getVoteId() {
        return voteId;
    }
}