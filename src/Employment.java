public class Employment {

    public enum Status {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    private Customer customer;
    private Freelancer freelancer;
    private Service serviceType;

    private int basePrice;      // the amount freelancer gets
    private int customerPaid;   // the amount customer pays after subsidy

    private Status status;

    private boolean cancelledByCustomer;
    private boolean cancelledByFreelancer;

    private int rating;

    public Employment(Customer customer, Freelancer freelancer, Service serviceType, int basePrice, int customerPaid) {

        this.customer = customer;
        this.freelancer = freelancer;
        this.serviceType = serviceType;

        this.basePrice = basePrice;
        this.customerPaid = customerPaid;

        this.status = Status.ACTIVE;

        this.cancelledByCustomer = false;
        this.cancelledByFreelancer = false;

        this.rating = 0;
    }

    // GETTERS
    public Customer getCustomer() { return customer; }
    public Freelancer getFreelancer() { return freelancer; }
    public Service getServiceType() { return serviceType; }
    public int getBasePrice() { return basePrice; }
    public int getCustomerPaid() { return customerPaid; }
    public Status getStatus() { return status; }
    public int getRating() { return rating; }


    public void completeEmployment(int rating) {
        this.status = Status.COMPLETED;
        this.rating = rating;

        // free the freelancer
        freelancer.setAvailable(true);
        freelancer.setCurrentEmployment(null);
    }

    public void cancelByCustomer() {
        this.status = Status.CANCELLED;
        this.cancelledByCustomer = true;

        // free freelancer
        freelancer.setAvailable(true);
        freelancer.setCurrentEmployment(null);
    }

    public void cancelByFreelancer() {
        this.status = Status.CANCELLED;
        this.cancelledByFreelancer = true;

        // free freelancer
        freelancer.setAvailable(true);
        freelancer.setCurrentEmployment(null);
    }


    public boolean isActive() { return status == Status.ACTIVE; }
    public boolean isCompleted() { return status == Status.COMPLETED; }
    public boolean isCancelled() { return status == Status.CANCELLED; }

    public boolean cancelledByCustomer() { return cancelledByCustomer; }
    public boolean cancelledByFreelancer() { return cancelledByFreelancer; }
}
