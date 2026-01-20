import java.util.ArrayList;

public class GigMatchPro {

    //freelancer Hashmap for O(1) reaching degree
    public hashmap<String, Freelancer> FreelancerHashMap;
    public hashmap<String, Customer> CustomerHashMap;
    //freelancer Priority Queue for O(logN) reaching degree
    private PriorityQueue[] servicePools;

    public GigMatchPro() {
        FreelancerHashMap = new hashmap<>();
        CustomerHashMap = new hashmap<>();

        servicePools = new PriorityQueue[Service.values().length];
        for (int i = 0; i < servicePools.length; i++) {
            servicePools[i] = new PriorityQueue();
        }
    }

    //register customer <customerID>
    //create a customer
    public String register_customer(String customerID) {

        // ID already exists as either customer OR freelancer
        if (CustomerHashMap.containsKey(customerID) || FreelancerHashMap.containsKey(customerID)) {
            return "Some error occurred in register customer.\n";
        }

        // Create and insert new customer
        Customer c = new Customer(customerID);
        CustomerHashMap.put(customerID, c);

        return "registered customer " + customerID + "\n";
    }



    //register_freelancer <freelancerID> <service type> <service price> <T> <C> <R> <E> <A>
    public String register_freelancer(String freelancerID, String service, int price, int T, int C, int R, int E, int A) {

        if (CustomerHashMap.containsKey(freelancerID) || FreelancerHashMap.containsKey(freelancerID)) {
            return "Some error occurred in register_freelancer.\n";
        }
        Service serviceType = Service.fromString(service);
        if (serviceType == null){
            return "Some error occurred in register_freelancer.\n";
        }
        Freelancer f = new Freelancer(freelancerID, serviceType, price, T, C, R, E, A);
        FreelancerHashMap.put(freelancerID, f);

        servicePools[serviceType.ordinal()].push(f);

        return "registered freelancer " + freelancerID + "\n";
    }


    //employ <customerID> <freelancerID>
    public String employ(String customerID, String freelancerID) {

        Customer customer = CustomerHashMap.get(customerID);
        Freelancer freelancer = FreelancerHashMap.get(freelancerID);

        //customer or freelancer does not exist
        if (customer == null || freelancer == null) {
            return "Some error occurred in employ.\n";
        }

        //freelancer is blacklisted by this customer
        if (customer.getBlacklist().contains(freelancer)) {
            return "Some error occurred in employ.\n";
        }

        // freelancer not available
        if (!freelancer.isAvailable()) {
            return "Some error occurred in employ.\n";
        }

        Service service = freelancer.getServiceType();
        int basePrice = freelancer.getPrice();

        double discount = customer.getTier().subsidy;
        int pricePaid = (int) Math.floor(basePrice * (1 - discount));

        // create employment object
        Employment e = new Employment(customer, freelancer, service, basePrice, pricePaid);

        // link job
        customer.addEmployment(e);
        freelancer.setCurrentEmployment(e);
        freelancer.setAvailable(false);

        // remove freelancer from PQ (they are now busy)
        servicePools[service.ordinal()].remove(freelancer);

        return customerID + " employed " + freelancerID +
                " for " + service.name().toLowerCase() + "\n";
    }




    //request_job <customerID> <service type> <num candidates>
    //tries to find a top n candidate list with the priority_queue
    //automatically employs the best candidate
    public String request_job(String customerID, String service, int k) {

        Customer customer = CustomerHashMap.get(customerID);
        Service serviceType = Service.fromString(service);
        if (customer == null || serviceType == null) {
            return "Some error occurred in request_job.\n";
        }

        PriorityQueue pool = servicePools[serviceType.ordinal()];

        ArrayList<Freelancer> popped = new ArrayList<>();
        ArrayList<Freelancer> candidates = new ArrayList<>();

        while (candidates.size() < k && !pool.isEmpty()) {
            Freelancer f = pool.pop();
            popped.add(f);

            if (!customer.getBlacklist().contains(f)) {
                candidates.add(f);
            }
        }

        if (candidates.isEmpty()) {

            for (Freelancer f : popped) pool.push(f);
            return "no freelancers available\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("available freelancers for ").append(serviceType.name().toLowerCase()).append(" (top ").append(k).append("):\n");

        for (Freelancer f : candidates) {
            sb.append(f.getFreelancerID())
                    .append(" - composite: ").append(f.getCompositeScore())
                    .append(", price: ").append(f.getPrice())
                    .append(", rating: ").append(String.format("%.1f", f.getRating()))
                    .append("\n");
        }

        Freelancer chosen = candidates.get(0);
        if (chosen.getFreelancerID().equals("free1365") && customerID.equals("cust1145")) {
            int c;
            c = (int) Math.floor(chosen.getCompositeScore());
        }

        double disc = customer.getTier().subsidy;
        int price = chosen.getPrice();
        int paidPrice = (int) Math.floor(price * (1 - disc));

        Employment e = new Employment(customer, chosen, serviceType, price, paidPrice);

        customer.addEmployment(e);
        chosen.setCurrentEmployment(e);
        chosen.setAvailable(false);

        for (Freelancer f : popped) {
            if (f != chosen) pool.push(f);
        }

        sb.append("auto-employed best freelancer: ")
                .append(chosen.getFreelancerID())
                .append(" for customer ").append(customerID)
                .append("\n");

        return sb.toString();
    }



    //cancel by customer <customerID> <freelancerID>
    //make freelancer available
    //customer loses loyalty points
    //increase canceling penalty of customer

    public String cancel_by_customer(String customerID, String freelancerID) {

        Customer c = CustomerHashMap.get(customerID);
        Freelancer f = FreelancerHashMap.get(freelancerID);

        if (c == null || f == null || f.getCurrentEmployment() == null || f.getCurrentEmployment().getCustomer() != c) {
            return "Some error occurred in cancel_by_customer.\n";
        }

        Employment e = f.getCurrentEmployment();

        e.cancelByCustomer();

        c.incrementCancelPenalty();
        c.endEmployment(e);

        // restore freelancer
        servicePools[f.getServiceType().ordinal()].push(f);



        return "cancelled by customer: " + customerID + " cancelled " + freelancerID + "\n";
    }


    //cancel by freelancer <freelancerID>
    //make freelancer available
    //apply zero-star review
    //decrease all skills 3 points
    //check if he or she canceled 5 or more job this month

    public String cancel_by_freelancer(String freelancerID) {

        Freelancer f = FreelancerHashMap.get(freelancerID);
        if (f == null || f.getCurrentEmployment() == null) {
            return "Some error occurred in cancel_by_freelancer.\n";
        }

        StringBuilder sb = new StringBuilder();
        Employment e = f.getCurrentEmployment();
        Customer c = e.getCustomer();

        e.cancelByFreelancer();

        f.addCancelledJob();

        // update rating as if customer gave 0
        f.updateRating(0);

        // decrease skills
        f.decreaseAllSkills();

        sb.append("cancelled by freelancer: ").append(freelancerID).append(" cancelled ").append(c.getCustomerID()).append("\n");

        // blacklist rule
        if (f.getThisMonthCancellations() >= 5) {
            FreelancerHashMap.remove(freelancerID);
            sb.append("platform banned freelancer: ").append(freelancerID).append("\n");
            return sb.toString();
        }
        // restore freelancer
        f.updateCompositeScore();
        servicePools[f.getServiceType().ordinal()].push(f);

        return sb.toString();
    }


    //complete and rate <freelancerID> <rating>
    //update rating
    //apply skill gains if necessary rating>=4
    //make freelancer available
    public String complete_and_rate(String freelancerID, int rating) {

        Freelancer f = FreelancerHashMap.get(freelancerID);
        if (f == null || f.getCurrentEmployment() == null) {
            return "Some error occurred in complete_and_rate.\n";
        }

        Employment e = f.getCurrentEmployment();
        Customer c = e.getCustomer();

        // Employment finalize
        e.completeEmployment(rating);

        // Apply effects
        f.addCompletedJob();
        f.updateRating(rating);

        c.addSpending(e.getCustomerPaid());

        // put freelancer back to PQ
        f.updateCompositeScore();
        servicePools[f.getServiceType().ordinal()].push(f);

        return freelancerID + " completed job for " + e.getCustomer().getCustomerID() + " with rating " + rating + "\n";
    }


    //change service <freelancerID> <new service type> <new price>
    //apply change for the next simulate month

    public String change_service(String freelancerID, String newServiceType, int newPrice) {

        Freelancer f = FreelancerHashMap.get(freelancerID);
        Service newService = Service.fromString(newServiceType);
        if (f == null || newService == null) {
            return "Some error occurred in change_service.\n";
        }
        Service oldService = f.getServiceType();
        f.requestServiceChange(newService, newPrice);
        return "service change for " + freelancerID + " queued from "+ oldService.name().toLowerCase() +" to " + newServiceType.toLowerCase() + "\n";
    }

    public String query_freelancer(String freelancerID) {
        Freelancer f = FreelancerHashMap.get(freelancerID);
        if (f == null) {
            return "Some error occurred in query_freelancer.\n";
        }
        return f.query();
    }

    public String query_customer(String customerID) {
        Customer c = CustomerHashMap.get(customerID);
        if (c == null) {
            return "Some error occurred in query_customer.\n";
        }
        return c.query();
    }

    public String blacklist(String customerID, String freelancerID) {
        Freelancer f = FreelancerHashMap.get(freelancerID);
        Customer c = CustomerHashMap.get(customerID);
        if (f==null || c == null) {
            return "Some error occurred in blacklist.\n";
        }
        if (c.getBlacklist().contains(f)) {
            return "Some error occurred in blacklist.\n";
        }
        c.addToBlacklist(f);
        return customerID+" blacklisted "+freelancerID + "\n";
    }

    public String unblacklist(String customerID, String freelancerID) {
        Freelancer f = FreelancerHashMap.get(freelancerID);
        Customer c = CustomerHashMap.get(customerID);
        if (f==null || c == null) {
            return "Some error occurred in unblacklist.\n";
        }
        if (!c.getBlacklist().contains(f)) {
            return "Some error occurred in unblacklist.\n";
        }
        c.removeFromBlacklist(f);
        return customerID+" unblacklisted "+freelancerID + "\n";
    }

    public String update_skill (String freelancerID, int T, int C , int R, int E, int A){
        Freelancer f = FreelancerHashMap.get(freelancerID);
        if (f == null) {
            return "Some error occurred in update_skill.\n";
        }
        if (T < 0 || T > 100 ||
                C < 0 || C > 100 ||
                R < 0 || R > 100 ||
                E < 0 || E > 100 ||
                A < 0 || A > 100)
        {
            return "Some error occurred in update_skill.\n";
        }
        int[] newSkills = new int[5];
        newSkills[0] = T;
        newSkills[1] = C;
        newSkills[2] = R;
        newSkills[3] = E;
        newSkills[4] = A;
        Service s = f.getServiceType();
        servicePools[s.ordinal()].remove(f);
        f.updateSkill(newSkills);
        f.updateCompositeScore();

        // Reinsert after score update
        servicePools[s.ordinal()].push(f);

        return "updated skills of " + freelancerID + " for "+ s.name().toLowerCase() +"\n";

    }

    public String simulate_month() {

        for (String id : FreelancerHashMap.keys()) {
            Freelancer f = FreelancerHashMap.get(id);

            servicePools[f.getServiceType().ordinal()].remove(f);

            f.applyPendingChanges();
            f.updateBurnoutStatus();
            f.updateCompositeScore();
            f.resetMonthlyCounters();

            servicePools[f.getServiceType().ordinal()].push(f);
        }

        for (String id : CustomerHashMap.keys()) {
            Customer c = CustomerHashMap.get(id);
            c.updateTier();
        }

        return "month complete\n";
    }




    public boolean doesExist(String freelancerID) {
        return FreelancerHashMap.containsKey(freelancerID);
    }
}
