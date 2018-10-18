import java.util.Calendar;

public class Voter {
    private String voterName;
    private boolean voteStatus;
    private String voteTime;

    public Voter(String getVoter) {
    	Calendar calen = Calendar.getInstance();
    	int years = calen.get(Calendar.YEAR);
    	int months = calen.get(Calendar.MONTH);
    	int dates = calen.get(Calendar.DATE);
    	int hours = calen.get(Calendar.HOUR_OF_DAY);
    	int minutes = calen.get(Calendar.MINUTE);
    	int seconds = calen.get(Calendar.SECOND);
    	
        this.voterName = getVoter;
        this.voteStatus = false;
        this.voteTime = "date< " + months + "/" + dates + "/" + years + " > time< " +hours + " : " +minutes + " : " + seconds + " >";
    }

    public String readVoterName() {
        return this.voterName;
    }



    public String readVoterNum() {
        String[] voterNum ;
        voterNum= this.voterName.split(" ");
        return voterNum[1];
    }

    

    public String readTime() {
        return this.voteTime;
    }
    public boolean readStatus() {
        return this.voteStatus;
    }
   
    public void VotedStatus() {
        this.voteStatus = true;
    }

    public void writeTime() {

        this.voteTime = "1";
    }

    public void writeTime(String votTime) {
        this.voteTime = votTime;
    }
}
