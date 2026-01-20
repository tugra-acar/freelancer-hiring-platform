public enum LoyaltyTier {
    BRONZE(0, 499, 0.00),
    SILVER(500, 1999, 0.05),
    GOLD(2000, 4999, 0.10),
    PLATINUM(5000, Integer.MAX_VALUE, 0.15);

    public final int min;
    public final int max;
    public final double subsidy;

    LoyaltyTier(int min, int max, double subsidy) {
        this.min = min;
        this.max = max;
        this.subsidy = subsidy;
    }

    public static LoyaltyTier getTierOfCustomer(int spending) {
        for (LoyaltyTier tier : values()) {
            if (spending >= tier.min && spending <= tier.max) {
                return tier;
            }
        }
        return BRONZE;
    }
    
}
