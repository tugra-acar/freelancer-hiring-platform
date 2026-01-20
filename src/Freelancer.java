public class Freelancer {
    //ID
    private String freelancerID;
    //next_month's service and skills and price

    //serviceType
    private Service serviceType;
    //price
    private int price;
    //skills
    private int[] skills;
    //rating
    private double rating;
    //composite score
    private int compositeScore;
    //completed jobs, cancelled jobs
    private int completedJobs;
    private int cancelledJobs;
    //this month's cancellations, completed
    private int thisMonthCancellations;
    private int thisMonthCompletedJobs;
    //availability
    private Employment currentEmployment;
    private boolean available;
    //burnout status
    private boolean burnout;
    // pending changes (applied after simulate_month)
    private Service nextServiceType;
    private Integer nextPrice;


    Freelancer(String freelancerID, Service serviceType, int price, int T, int C, int R, int E, int A) {
        this.freelancerID = freelancerID;
        this.serviceType = serviceType;
        this.price = price;
        this.skills = new int[5];
        skills[0] = T;
        skills[1] = C;
        skills[2] = R;
        skills[3] = E;
        skills[4] = A;
        this.rating = 5.0;
        this.available = true;
        this.burnout = false;

        this.completedJobs = 0;
        this.cancelledJobs = 0;

        updateCompositeScore();// compute initial composite score


    }

    //change service <freelancerID> <new service type> <new price>
    //apply change for the next simulate month



    public String getFreelancerID() {return freelancerID;}
    public int getCompositeScore() {return compositeScore;}
    public Service getServiceType() {return serviceType;}
    public int getPrice() {return price;}
    public int[] getSkills() {return skills;}
    public double getRating() {return rating;}
    public boolean isAvailable() {return available;}
    public boolean isBurnout() {return burnout;}
    public int getCompletedJobs() {return completedJobs;}
    public int getCancelledJobs() {return cancelledJobs;}
    public int getThisMonthCancellations() {return thisMonthCancellations;}
    public int getThisMonthCompletedJobs() {return thisMonthCompletedJobs;}
    public Employment getCurrentEmployment() {return currentEmployment;}

    //setters
    public void setCurrentEmployment(Employment currentEmployment) {
        this.currentEmployment = currentEmployment;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }


    public void updateCompositeScore() {

        int[] serviceRequirements = serviceType.getSkillProfile(); // service reqs
        int[] freelancerSkills = this.skills;

        double dotProduct = 0;
        double sumOfS = 0;

        for (int i = 0; i < 5; i++) {
            dotProduct += (double) freelancerSkills[i] * serviceRequirements[i];
            sumOfS += serviceRequirements[i];
        }

        double skillScore = dotProduct / (100.0 * sumOfS);

        // rating score
        double ratingScore = this.rating / 5.0;

        // reliability
        int totalJobs = completedJobs + cancelledJobs;
        double reliabilityScore = (totalJobs == 0) ? 1.0 : 1.0 - ((double) cancelledJobs / totalJobs);

        // burnout penalty
        double penalty = burnout ? 0.45 : 0.0;

        // weighted sum
        double weighted = ((0.55 * skillScore) + (0.25 * ratingScore) + (0.20 * reliabilityScore) - (penalty));

        this.compositeScore = (int) Math.floor(10000.0 * weighted);

        if (this.compositeScore < 0)
            this.compositeScore = 0;
    }

    public void updateRating(int customerRating) {
        int n = completedJobs + cancelledJobs;

        this.rating = (this.rating * (n) + customerRating) / (n+1) ;
        if (customerRating>=4){
            increaseSkillsAfterGoodReview();
        }
    }

    public void increaseSkillsAfterGoodReview() {

        int[] req = serviceType.getSkillProfile();  // Ts, Cs, Rs, Es, As

        // skill index list
        Integer[] importance = {0, 1, 2, 3, 4};

        java.util.Arrays.sort(importance, (a, b) -> req[b] - req[a]);

        int primary      = importance[0];
        int secondary1   = importance[1];
        int secondary2   = importance[2];

        // skill gain apply
        skills[primary]    = Math.min(100, skills[primary] + 2);
        skills[secondary1] = Math.min(100, skills[secondary1] + 1);
        skills[secondary2] = Math.min(100, skills[secondary2] + 1);
    }



    public void decreaseAllSkills(){
        for (int i = 0; i < 5; i++) {
            skills[i] -= 3;
        }
    }


    public void updateSkill(int[] newSkills) {
        for (int i = 0; i < 5; i++) {
            this.skills[i] = newSkills[i];
        }
    }


    public void addCompletedJob() {
        completedJobs++;
        thisMonthCompletedJobs++;
    }

    public void addCancelledJob() {
        cancelledJobs++;
        thisMonthCancellations++;
    }


    public void requestServiceChange(Service newService, int newPrice) {
        this.nextServiceType = newService;
        this.nextPrice = newPrice;
    }

    public void applyPendingChanges() {
        if (nextServiceType != null) {
            this.serviceType = nextServiceType;
            nextServiceType = null;
        }
        if (nextPrice != null) {
            this.price = nextPrice;
            nextPrice = null;
        }
        updateCompositeScore();
    }
    public void resetMonthlyCounters() {
        thisMonthCompletedJobs = 0;
        thisMonthCancellations = 0;
    }
    public void checkBurnout() {
        if (thisMonthCancellations >= 3) {
            burnout = true;
        }
    }

    public void recoverBurnout() {
        if(thisMonthCompletedJobs<=2) {
            burnout = false;
        }
    }
    public String query(){
        StringBuilder sb = new StringBuilder();
        sb.append(freelancerID)
                .append(": ")
                .append(serviceType.name().toLowerCase())
                .append(", price: ").append(price)
                .append(", rating: ").append(String.format("%.1f", rating))
                .append(", completed: ").append(completedJobs)
                .append(", cancelled: ").append(cancelledJobs)
                .append(", skills: (")
                .append(skills[0]).append(",")
                .append(skills[1]).append(",")
                .append(skills[2]).append(",")
                .append(skills[3]).append(",")
                .append(skills[4]).append(")")
                .append(", available: ").append(available ? "yes" : "no")
                .append(", burnout: ").append(burnout ? "yes" : "no").append("\n");

        return sb.toString();
    }

    public void updateBurnoutStatus() {

        // Trigger: If not previously burned out and completed >= 5
        if (!burnout && thisMonthCompletedJobs >= 5) {
            burnout = true;
            return;
        }

        // Recovery: If burned out and completed <= 2
        if (burnout && thisMonthCompletedJobs <= 2) {
            burnout = false;
        }
    }



}
