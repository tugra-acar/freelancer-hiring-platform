import java.io.*;
import java.util.Locale;

public class Main {


    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
        System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];
        GigMatchPro gigMatchPro = new GigMatchPro();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line, writer, gigMatchPro);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer, GigMatchPro gigMatchPro)
            throws IOException {

        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "register_customer":
                    // Format: register_customer customerID
                    result = gigMatchPro.register_customer(parts[1]);
                    break;

                case "register_freelancer":
                    // Format: register_freelancer freelancerID serviceName basePrice T C R E A
                    String freelancerID = parts[1];
                    String service = parts[2];
                    int price = Integer.parseInt(parts[3]);
                    int T = Integer.parseInt(parts[4]);
                    int C = Integer.parseInt(parts[5]);
                    int R = Integer.parseInt(parts[6]);
                    int E = Integer.parseInt(parts[7]);
                    int A = Integer.parseInt(parts[8]);
                    result = gigMatchPro.register_freelancer(freelancerID, service, price, T, C, R, E, A);
                    break;

                case "request_job":
                    // Format: request_job customerID serviceName topK
                    String customerID = parts[1];
                    String s = parts[2].toUpperCase();
                    int k = Integer.parseInt(parts[3]);

                    result = gigMatchPro.request_job(customerID, s, k);
                    break;

                case "employ_freelancer":
                    // Format: employ_freelancer customerID freelancerID
                    result = gigMatchPro.employ(parts[1], parts[2]);
                    break;

                case "complete_and_rate":
                    // Format: complete_and_rate freelancerID rating
                    String fID = parts[1];
                    int rating = Integer.parseInt(parts[2]);

                    result = gigMatchPro.complete_and_rate(fID, rating);
                    break;

                case "cancel_by_freelancer":
                    // Format: cancel_by_freelancer freelancerID
                    result = gigMatchPro.cancel_by_freelancer(parts[1]);
                    break;

                case "cancel_by_customer":
                    // Format: cancel_by_customer customerID freelancerID
                    result = gigMatchPro.cancel_by_customer(parts[1], parts[2]);
                    break;

                case "blacklist":
                    // Format: blacklist customerID freelancerID
                    result = gigMatchPro.blacklist(parts[1], parts[2]);
                    break;

                case "unblacklist":
                    // Format: unblacklist customerID freelancerID
                    result = gigMatchPro.unblacklist(parts[1], parts[2]);

                    break;

                case "change_service":
                    // Format: change_service freelancerID newService newPrice
                    String newService = parts[2].toUpperCase();
                    int newPrice = Integer.parseInt(parts[3]);

                    result = gigMatchPro.change_service(parts[1], newService, newPrice);
                    break;

                case "simulate_month":
                    // Format: simulate_month
                    result = gigMatchPro.simulate_month();
                    break;

                case "query_freelancer":
                    // Format: query_freelancer freelancerID
                    result = gigMatchPro.query_freelancer(parts[1]);
                    break;

                case "query_customer":
                    // Format: query_customer customerID
                    result = gigMatchPro.query_customer(parts[1]);
                    break;

                case "update_skill":
                    // Format: update_skill freelancerID T C R E A
                    String f2 = parts[1];
                    int t2 = Integer.parseInt(parts[2]);
                    int c2 = Integer.parseInt(parts[3]);
                    int r2 = Integer.parseInt(parts[4]);
                    int e2 = Integer.parseInt(parts[5]);
                    int a2 = Integer.parseInt(parts[6]);

                    result = gigMatchPro.update_skill(f2, t2, c2, r2, e2, a2);
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);

        } catch (Exception e) {
            writer.write("Error processing command: " + command);
            writer.newLine();
        }
    }
}