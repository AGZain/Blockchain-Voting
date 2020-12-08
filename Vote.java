//this class actually holds the vote itself. 

public class Vote {
    String vote;
    String applicationId;
    String voteId;
    //holds the vote itself, the application id of the application that submited the vote, as well as the unique voter id
    public Vote(String vote, String applicationId, String voteId) {
        this.vote = vote;
        this.applicationId = applicationId;
        this.voteId = voteId;
    }

    //get the vote itself
    public String getVote() {
        return vote;
    }
    //get the application id
    public String getApplicationId() {
        return applicationId;
    }
    //get the voter id
    public String getVoteId() {
        return voteId;
    }
}