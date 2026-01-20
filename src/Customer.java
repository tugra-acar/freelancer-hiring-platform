import java.util.ArrayList;

public class Customer {
    //ID
    private String customerID;
    //total spent
    private int totalSpent;
    //loyalty tier
    private LoyaltyTier tier;
    //current employments
    private ArrayList<Employment> employments;
    //blacklisted freelancers
    private ArrayList<Freelancer> blacklist;
    //total employment count
    private int totalEmployment;
    //canceling penalty
    private int cancellingPenalty;


    //register customer <customerID>
    //create a customer
    Customer(String customerID) {
        this.customerID = customerID;
        this.totalSpent = 0;
        this.tier = LoyaltyTier.BRONZE;
        this.employments = new ArrayList<>();
        this.blacklist = new ArrayList<>();
        this.totalEmployment = 0;
        this.cancellingPenalty = 0;
    }


    //getters
    public String getCustomerID() {return customerID;}
    public int getTotalSpent() {return totalSpent;    }
    public LoyaltyTier getTier() {return tier;}
    public ArrayList<Employment> getEmployments() {return employments;}
    public ArrayList<Freelancer> getBlacklist() {return blacklist;}
    public int getTotalEmployment() {return totalEmployment;}
    public int getCancellingPenalty() {return cancellingPenalty;}

    public void updateTier() {
        this.tier = LoyaltyTier.getTierOfCustomer(this.totalSpent-this.cancellingPenalty);
    }

    public void addSpending(int amount) {
        this.totalSpent += amount;
    }

    public void addEmployment(Employment e) {
        this.employments.add(e);
        this.totalEmployment++;
    }

    public void endEmployment(Employment e) {
        employments.remove(e);
    }

    public void addToBlacklist(Freelancer f) {
        if (!blacklist.contains(f)) blacklist.add(f);
    }
    public void removeFromBlacklist(Freelancer f) {
        blacklist.remove(f);
    }

    public void incrementCancelPenalty() {
        this.cancellingPenalty+=250;
    }
    public void resetCancelPenalty() {
        this.cancellingPenalty = 0;
    }
    public String query(){
        StringBuilder sb = new StringBuilder();

        sb.append(customerID)
                .append(": total spent: $").append(totalSpent)
                .append(", loyalty tier: ").append(tier.name())
                .append(", blacklisted freelancer count: ").append(blacklist.size())
                .append(", total employment count: ").append(totalEmployment).append("\n");

        return sb.toString();
    }

}
