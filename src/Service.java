public enum Service {
    PAINT(70, 60, 50, 85, 90),
    WEB_DEV(95, 75, 85, 80, 90),
    GRAPHIC_DESIGN(75, 85, 95, 70, 85),
    DATA_ENTRY(50, 50, 30, 95, 95),
    TUTORING(80, 95, 70, 90, 75),
    CLEANING(40, 60, 40, 90, 85),
    WRITING(70, 85, 90, 80, 95),
    PHOTOGRAPHY(85, 80, 90, 75, 90),
    PLUMBING(85, 65, 60, 90, 85),
    ELECTRICAL(90, 65, 70, 95, 95);

    public final int T, C, R, E, A;

    Service(int T, int C, int R, int E, int A) {
        this.T = T;
        this.C = C;
        this.R = R;
        this.E = E;
        this.A = A;
    }

    public int[] getSkillProfile() {
        return new int[]{T, C, R, E, A};
    }

    public static Service fromString(String s) {
        try {
            return Service.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

}
