import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String BALANCE = "BALANCE";
    private static final String LOAN = "LOAN";
    private static final String PAYMENT = "PAYMENT";

    private static String functionality(String input) {
        return input.split(" ")[0];
    }

    private static String bankName(String input) {
        return input.split(" ")[1];
    }

    private static String personName(String input) {
        return input.split(" ")[2];
    }

    public static void main(String[] args) throws FileNotFoundException {
        String filePath = args[0];
//        String filePath = "/Users/pranay.mahendra/Desktop/Personal/temp.txt";
        Scanner inFile1 = new Scanner(new File(filePath)).useDelimiter("\n\\s*");
        List<String> inputList = new ArrayList<String>();
        while (inFile1.hasNext()) {
            inputList.add(inFile1.next());
        }
        inFile1.close();
        for (String s : inputList) {
            if (functionality(s).equals(BALANCE)) {
                String bankName = bankName(s);
                String personName = personName(s);
                Integer emiNo = getEmiNo(s);
                List<String> personList = getAllInputList(inputList, bankName, personName, emiNo);
                String balanceDetails = findBalance(personList, bankName, personName, emiNo);
                System.out.println(balanceDetails);
            }
        }
    }

    private static String findBalance(List<String> personList, String bankName, String personName, Integer emiNo) {
        String balanceDetails = bankName + " " + personName;
        String loanInput = getLoanInput(personList);
        double totalPayable = calculateTotalPayable(loanInput);
        Integer emi = calculateEmi(totalPayable, getYears(loanInput));
        int totalAmountPaid = emiNo * emi + paymentsDone(personList);
        int remainingMonths = (int) Math.ceil((totalPayable - totalAmountPaid) / emi);
        balanceDetails = balanceDetails + " " + totalAmountPaid + " " + remainingMonths;
        return balanceDetails;
    }

    private static String getLoanInput(List<String> personList) {
        String loanInput = "";
        for (String input : personList) {
            if (functionality(input).equals(LOAN)) {
                loanInput = input;
                break;
            }
        }
        return loanInput;
    }

    private static int paymentsDone(List<String> personList) {
        int totalPaymentsDone = 0;
        for (String input : personList) {
            if (functionality(input).equals(PAYMENT)) {
                totalPaymentsDone += getAmountPaid(input);
            }
        }
        return totalPaymentsDone;
    }

    private static int getAmountPaid(String input) {
        return Integer.parseInt(input.split(" ")[3]);
    }

    private static double calculateTotalPayable(String loanInput) {
        double principle = getPrinciple(loanInput);
        double years = getYears(loanInput);
        double roi = getROI(loanInput);
        double totalInterest = principle * years * roi;
        return principle + totalInterest;
    }

    private static Integer calculateEmi(double totalPayable, double years) {
        return (int) Math.ceil(totalPayable / (years * 12));
    }

    private static double getROI(String input) {
        return Double.parseDouble(input.split(" ")[5])/100;
    }

    private static double getYears(String input) {
        return Double.parseDouble(input.split(" ")[4]);
    }

    private static double getPrinciple(String input) {
        return Double.parseDouble(input.split(" ")[3]);
    }

    private static Integer getEmiNo(String input) {
        int offset = 0;
        if (functionality(input).equals(BALANCE)) {
            offset = 3;
        } else if (functionality(input).equals(PAYMENT)) {
            offset = 4;
        } else {
            return -1;
        }
        return Integer.parseInt(input.split(" ")[offset]);
    }

    private static List<String> getAllInputList(List<String> inputList, String bankName, String personName, Integer emiNo) {
        List<String> personList = new ArrayList<>();
        for (String input : inputList) {
            if (!bankName(input).equals(bankName)) {
                continue;
            }
            if (!personName(input).equals(personName)) {
                continue;
            }
            if (functionality(input).equals(PAYMENT) && getEmiNo(input) <= emiNo) {
                personList.add(input);
            }
            if (functionality(input).equals(LOAN)) {
                personList.add(input);
            }
        }
        return personList;
    }
}
