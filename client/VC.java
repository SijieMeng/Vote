public class VC {
    public static void main(String[] args) {
        int portnum = 0;
        String domain = args[0];

        try {
            portnum = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            ClientImple.handleException(ex, "Invalid portnumber");
        }

        Client voterClient = new Client(domain, portnum);
        voterClient.Cli_Execution();
        voterClient.close();
    }
}
